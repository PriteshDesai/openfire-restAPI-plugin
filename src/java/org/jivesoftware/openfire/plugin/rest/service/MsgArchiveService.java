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

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jivesoftware.openfire.plugin.rest.controller.MsgArchiveController;
import org.jivesoftware.openfire.plugin.rest.entity.MsgArchiveEntity;
import org.jivesoftware.openfire.plugin.rest.entity.OldMessagesEntity;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path("restapi/v1/archive/messages")
@Tag(name = "Message Archive", description = "Server-sided storage of chat messages.")
public class MsgArchiveService {

	private MsgArchiveController archive;

	private static Logger Log = LoggerFactory.getLogger(MsgArchiveService.class);

	@PostConstruct
	public void init() {
		archive = MsgArchiveController.getInstance();
	}

	@GET
	@Path("/unread/{jid}")
	@Operation(summary = "Unread message count", description = "Gets a count of messages that haven't been delivered to the user yet.", responses = {
			@ApiResponse(responseCode = "200", description = "A message count", content = @Content(schema = @Schema(implementation = MsgArchiveEntity.class))) })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public MsgArchiveEntity getUnReadMessagesCount(
			@Parameter(description = "The (bare) JID of the user for which the unread message count needs to be fetched.", example = "john@example.org", required = true) @PathParam("jid") String jidStr)
			throws ServiceException {
		JID jid = new JID(jidStr);
		int msgCount = archive.getUnReadMessagesCount(jid);
		return new MsgArchiveEntity(jidStr, msgCount);
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	@GET
	@Path("/history")
	@Operation(summary = "Get Message History", description = "Gets the History of the Message.", responses = {
			@ApiResponse(responseCode = "200", description = "Get the History of Message", content = @Content(schema = @Schema(implementation = OldMessagesEntity.class))) })
	@Produces({ MediaType.APPLICATION_JSON })
	public OldMessagesEntity getHistoryMessages(@QueryParam("fromjid") String fromjid,
			@QueryParam("tojid") String tojid, @QueryParam("page") int page) throws ServiceException {
		Log.debug("MsgArchiveService: getHistoryMessages(): START : fromjid: " + fromjid + " tojid: " + tojid
				+ " page: " + page);

		if (fromjid == null || fromjid.isEmpty() || tojid == null || tojid.isEmpty()) {
			throw new ServiceException("From JID or To JID should not be null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		OldMessagesEntity oldMessagesEntity = archive.getHistoryMessages(fromjid, tojid, page);
		Log.debug("MsgArchiveService: getHistoryMessages(): END: oldMessagesEntity: " + oldMessagesEntity.toString());
		return oldMessagesEntity;
	}

	@GET
	@Path("/chatHistory")
	@Produces({ MediaType.APPLICATION_JSON })
	public OldMessagesEntity getChatHistoryMessagesWithPagination(@QueryParam("fromjid") String fromjid,
			@QueryParam("tojid") String tojid, @QueryParam("lastTimeStamp") long lastTimeStamp,
			@QueryParam("page") int page) throws ServiceException {
		Log.debug("MsgArchiveService: getChatHistoryMessagesWithPagination(): START : fromjid: " + fromjid + " tojid: "
				+ tojid + " lastTimeStamp: " + lastTimeStamp + " page: " + page);
		if (fromjid == null || fromjid.isEmpty()) {
			if (tojid == null || tojid.isEmpty()
					|| !tojid.contains("@conference." + JiveGlobals.getProperty("xmpp.domain"))) {
				throw new ServiceException("Room JID should not be null or empty", "",
						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
			}

			OldMessagesEntity oldMessagesEntity = archive.getChatHistoryMessages(null, tojid, lastTimeStamp, page);
			Log.debug("MsgArchiveService: getChatHistoryMessagesWithPagination(): END : oldMessagesEntity: "
					+ oldMessagesEntity.toString());
			return oldMessagesEntity;
		} else {
			if (fromjid == null || fromjid.isEmpty() || tojid == null || tojid.isEmpty()) {
				throw new ServiceException("From JID or To JID should not be null or empty", "",
						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
			}

			OldMessagesEntity oldMessagesEntity = archive.getChatHistoryMessages(fromjid, tojid, lastTimeStamp, page);
			Log.debug("MsgArchiveService: getChatHistoryMessagesWithPagination(): END : oldMessagesEntity: "
					+ oldMessagesEntity.toString());
			return oldMessagesEntity;
		}
	}

//	@GET
//	@Path("/latest")
//	@Produces({ MediaType.APPLICATION_JSON })
//	public OldMessageEntity getLatestMessage(@QueryParam("fromjid") String fromjid, @QueryParam("tojid") String tojid)
//			throws ServiceException {
//
//		if (fromjid == null || fromjid.isEmpty() || tojid == null || tojid.isEmpty()) {
//			throw new ServiceException("From JID or To JID should not be null or empty", "",
//					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
//		}
//
//		return archive.getLatestMessage(fromjid, tojid);
//	}

	@GET
	@Path("/{jid}")
	@Produces({ MediaType.APPLICATION_JSON })
	@Operation(summary = "Get all Chat History", description = "Gets the all chat History", responses = {
			@ApiResponse(responseCode = "200", description = "Get the All chat History Messages", content = @Content(schema = @Schema(implementation = OldMessagesEntity.class))) })
	public List<OldMessagesEntity> getAllChatList(@PathParam("jid") String jid, @QueryParam("page") int page)
			throws ServiceException {
		Log.debug("MsgArchiveService: getAllChatList(): START : jid: " + jid + " page: " + page);
		if (jid == null || jid.isEmpty()) {
			throw new ServiceException("User JID should not be null or empty", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		List<OldMessagesEntity> oldMessagesEntityList =archive.getAllMessages(jid, page);
		Log.debug("MsgArchiveService: getAllChatList(): END: oldMessagesEntityList: " + oldMessagesEntityList.toString());
		return oldMessagesEntityList;
	}

	/*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */

}
