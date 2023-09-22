/*
 * Copyright (c) 2022.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.openfire.plugin.rest.service;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.dom4j.Element;
import org.jivesoftware.openfire.PacketRouter;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.group.GroupNotFoundException;
import org.jivesoftware.openfire.muc.ConflictException;
import org.jivesoftware.openfire.muc.ForbiddenException;
import org.jivesoftware.openfire.muc.MUCRole;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.muc.MultiUserChatService;
import org.jivesoftware.openfire.muc.spi.LocalMUCUser;
import org.jivesoftware.openfire.plugin.rest.controller.MessageController;
import org.jivesoftware.openfire.plugin.rest.controller.UserServiceController;
import org.jivesoftware.openfire.plugin.rest.entity.GroupMessageEntity;
import org.jivesoftware.openfire.plugin.rest.entity.MessageEntity;
import org.jivesoftware.openfire.plugin.rest.entity.UnicastMessageEntity;
import org.jivesoftware.openfire.plugin.rest.entity.UserEntities;
import org.jivesoftware.openfire.plugin.rest.entity.UserEntity;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.component.ComponentException;
import org.xmpp.packet.JID;
import org.xmpp.packet.Message;

import com.google.gson.Gson;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("restapi/v1/messages")
@Tag(name = "Message", description = "Sending (chat) messages to users.")
public class MessageService {

	private MessageController messageController;
	
	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	private UserServiceController plugin;
	private LocalMUCUser localMUCUser;

	private static Logger LOG = LoggerFactory.getLogger(MessageService.class);
	 /*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	
	@PostConstruct
	public void init() {
		/*
		 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		 */
		plugin = UserServiceController.getInstance();
		 /*
		 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		 */
		messageController = MessageController.getInstance();
	}

	@POST
	@Path("/users")
	@Operation(summary = "Broadcast", description = "Sends a message to all users that are currently online.", responses = {
			@ApiResponse(responseCode = "201", description = "Message is sent."),
			@ApiResponse(responseCode = "400", description = "The message content is empty or missing."), })
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response sendBroadcastMessage(
			@RequestBody(description = "The message that is to be broadcast.", required = true) MessageEntity messageEntity)
			throws ServiceException {
		messageController.sendBroadcastMessage(messageEntity);
		return Response.status(Response.Status.CREATED).build();
	}

	
	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	 
	@POST
	@Path("/user")
	@Operation(summary = "message to particular User", description = "Send a message to particular User", responses = {
			@ApiResponse(responseCode = "201", description = "Message is sent."),
			@ApiResponse(responseCode = "400", description = "The message content is empty or missing."), })
	public Response sendUnicastMessage(UnicastMessageEntity unicastMessageEntity) throws ServiceException {
		LOG.debug("MessageService: sendUnicastMessage(): START : unicastMessageEntity: " + unicastMessageEntity.toString());
		
		if (unicastMessageEntity.getFromJID() == null || unicastMessageEntity.getFromJID().isEmpty()) {
			throw new ServiceException("FromJID is null or empty", "", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
					Response.Status.BAD_REQUEST);
		} else if (unicastMessageEntity.getToJID() == null || unicastMessageEntity.getToJID().isEmpty()) {
			throw new ServiceException("toJID is null or empty", "", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
					Response.Status.BAD_REQUEST);
		} else if (unicastMessageEntity.getBody() == null || unicastMessageEntity.getBody().isEmpty()) {
			throw new ServiceException("Message content/body is null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (Objects.isNull(unicastMessageEntity.getMessageTime())) {
			throw new ServiceException("Message Time is null or empty", "", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
					Response.Status.BAD_REQUEST);
		}

		XMPPServer xmppServer = XMPPServer.getInstance();
		PacketRouter packetRouter = xmppServer.getPacketRouter();

		JID from = new JID(unicastMessageEntity.getFromJID());
		JID to = new JID(unicastMessageEntity.getToJID());

		Message packet = new Message();
		packet.setType(Message.Type.chat);
		packet.setID(RandomStringUtils.randomAlphanumeric(7));
		packet.setSubject(unicastMessageEntity.getSubject());
		packet.setBody(unicastMessageEntity.getBody());

		Element e = packet.addChildElement("messageTime", "urn:xmpp:time");
		e.addElement("time").setText(String.valueOf(unicastMessageEntity.getMessageTime()));

		Element e1 = packet.addChildElement("forwaredMessage", "urn:xmpp:forward");
		e1.addElement("forward").setText("false");

		Element e2 = packet.addChildElement("request", "urn:xmpp:receipts");

		// Send message to receiver.
		sendMessageOneToOneChat(packetRouter, packet, from, to);

		// Send message to sender.
		packet.setSubject("20");
		sendMessageOneToOneChat(packetRouter, packet, to, from);

		LOG.info("Unicast message from ::" + packet.getFrom() + "\\");
		LOG.info("Unicast message to ::" + packet.getTo() + "\\");
		LOG.info("Unicast MEssage subject ::" + packet.getSubject() + "\\");
		LOG.info("Unicast MEssage body ::" + packet.getBody() + "\\");
		LOG.info("Message Sent ::" + packet.toXML());
		
		LOG.debug("MessageService: sendUnicastMessage(): END");
		return Response.status(Response.Status.CREATED).build();
	}

	@POST
	@Path("/userAddInRoom")
	public Response addAllTheUserInRoom(UnicastMessageEntity unicastMessageEntity) throws ServiceException {
		LOG.debug("MessageService: addAllTheUserInRoom(): START : unicastMessageEntity: " + unicastMessageEntity.toString());

		JID from = new JID(unicastMessageEntity.getFromJID());
		JID to = new JID(unicastMessageEntity.getToJID());

		if (unicastMessageEntity.getToJID().equals("all@conference.openfire.gatherhall.com")
				|| unicastMessageEntity.getFromJID().equals("all@conference.openfire.gatherhall.com")) {

			UserEntities users = plugin.getUserEntities(null, null, null);

			String xmppdomain = "@" + JiveGlobals.getProperty("xmpp.domain");

			for (UserEntity u : users.getUsers()) {
				addMember("conference", "all", u.getUsername() + xmppdomain, u);
			}
		}

		LOG.debug("MessageService: addAllTheUserInRoom(): END");
		return Response.status(Response.Status.CREATED).build();
	}

	public void addMember(String serviceName, String roomName, String jid, UserEntity user) {
		LOG.debug("MessageService: addMember(): START : serviceName: " + serviceName + " roomName: " + roomName + " jid: " + jid + " user: " + user.toString());

		MUCRoom room = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(serviceName)
				.getChatRoom(roomName.toLowerCase());
		try {
			ArrayList roles = new ArrayList<MUCRole.Role>();
			roles.add("moderator");
			roles.add("participant");
			roles.add("visitor");

			// set the broadcastRoles
			room.setRolesToBroadcastPresence(roles);

			room.addMember(new JID(jid), null, room.getRole());
			LOG.info("User " + jid + " Added to Room : " + room.toString());

		} catch (ForbiddenException e) {
			LOG.error("Could not add member", e);
		} catch (ConflictException e) {
			LOG.error("Could not add member", e);
		}
		
		LOG.debug("MessageService: addMember(): END");
	}

	private void sendMessageOneToOneChat(PacketRouter packetRouter, Message message, JID from, JID to) {
		LOG.debug("MessageService: sendMessageOneToOneChat(): START : packetRouter: " + packetRouter.toString() + " message: " + message.toString() + " from: " + from.toString() + " to: " + to.toString());

		message.setFrom(from + "/chat");
		message.setTo(to);

		packetRouter.route(message);
		
		LOG.debug("MessageService: sendMessageOneToOneChat(): END");
	}

	@POST
	@Path("/group")
	@Operation(summary = "message to group", description = "Send a message to particular group", responses = {
			@ApiResponse(responseCode = "201", description = "Message is sent."),
			@ApiResponse(responseCode = "400", description = "The message content is empty or missing."), })
	public Response sendMessageToGroup(GroupMessageEntity groupMessageEntity)
			throws ServiceException, GroupNotFoundException, ComponentException {
		LOG.debug("MessageService: sendMessageToGroup(): START : groupMessageEntity: " + groupMessageEntity.toString());
		
		Gson gson = new Gson();
		LOG.info("Group Message Entity :: " + gson.toJson(groupMessageEntity));

		if (groupMessageEntity.getFromJID() == null || groupMessageEntity.getFromJID().isEmpty()) {
			throw new ServiceException("FromJID should not be null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (groupMessageEntity.getGroupName() == null || groupMessageEntity.getGroupName().isEmpty()) {
			throw new ServiceException("groupName should not be null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (groupMessageEntity.getBody() == null || groupMessageEntity.getBody().isEmpty()) {
			throw new ServiceException("Message content/body should not be null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (groupMessageEntity.getSenderName() == null || groupMessageEntity.getSenderName().isEmpty()) {
			throw new ServiceException("Sender Name should not be null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}
//		else if (groupMessageEntity.getSenderImage() == null || groupMessageEntity.getSenderImage().isEmpty()) {
//			throw new ServiceException("Sender Image should not be null or empty", "",
//					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
//		} 
		else if (groupMessageEntity.getSenderUUID() == null || groupMessageEntity.getSenderUUID().isEmpty()) {
			throw new ServiceException("Sender UUID should not be null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (Objects.isNull(groupMessageEntity.getMessageTime())) {
			throw new ServiceException("Message Time is null or empty", "", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
					Response.Status.BAD_REQUEST);
		}

		MultiUserChatService multiUserChatService = XMPPServer.getInstance().getMultiUserChatManager()
				.getMultiUserChatService("conference");
		MUCRoom chatRoom = multiUserChatService.getChatRoom(groupMessageEntity.getGroupName());

		if (chatRoom == null) {
			LOG.error("Invalid chat room schedule message, Chatroom Not found.");
			throw new ServiceException("Invalid roomId, Chat room not found.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		Message message = new Message();
		message.setType(Message.Type.groupchat);
		message.setBody(groupMessageEntity.getBody());
		message.setSubject("1");
		message.setTo(chatRoom.getJID());
		message.setFrom(groupMessageEntity.getFromJID());
		message.setID(RandomStringUtils.randomAlphanumeric(7));

		Element groupElement = message.addChildElement("groupData", "urn:xmpp:groupdata");
		Element data = groupElement.addElement("data");

		String senderName = groupMessageEntity.getSenderName();
		String senderUUID = groupMessageEntity.getSenderUUID();
		String senderImage = groupMessageEntity.getSenderImage();

		if (senderImage == null || senderImage.isEmpty()) {
			senderImage = "";
		}

		data.addElement("senderName").setText(senderName);
		data.addElement("senderImage").setText(senderImage);
		data.addElement("senderUUID").setText(senderUUID);
		Element forwardElement = message.addChildElement("forwaredMessage", "urn:xmpp:forward");
		forwardElement.addElement("forward").setText("false");

		Element messageTimeElement = message.addChildElement("messageTime", "urn:xmpp:time");
		messageTimeElement.addElement("time").setText(String.valueOf(groupMessageEntity.getMessageTime()));

		chatRoom.serverScheduleBroadcast(message, groupMessageEntity.getSenderUUID());

		LOG.info("Group Message Sent ::" + message.toXML());

		LOG.debug("MessageService: sendMessageToGroup(): END");
		return Response.status(Response.Status.CREATED).build();
	}
	 /*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
}
