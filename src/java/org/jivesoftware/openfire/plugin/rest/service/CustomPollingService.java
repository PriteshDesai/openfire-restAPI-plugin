package org.jivesoftware.openfire.plugin.rest.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jivesoftware.openfire.custom.dto.PollOpinion;
import org.jivesoftware.openfire.custom.dto.PollOpinionEntity;
import org.jivesoftware.openfire.plugin.rest.controller.CustomPollController;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author pritesh_desai CustomPollingService is user to perform the operation
 *         of the poll
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@Path("restapi/v1/poll")
@Tag(name = "User Poll", description = "Managing user Polls.")
public class CustomPollingService {

	Logger Log = LoggerFactory.getLogger(CustomPollingService.class);

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public PollOpinionEntity getAllPollRecord() throws ServiceException {
		Log.debug("CustomPollingService: getAllPollRecord(): START");
		CustomPollController pollController = new CustomPollController();
		PollOpinionEntity pollOpinionEntity = new PollOpinionEntity(pollController.getAllPollDetail());
		Log.debug("CustomPollingService: getAllPollRecord(): END: pollOpinions: " + pollOpinionEntity.toString());
		return pollOpinionEntity;
	}

	@GET
	@Path("/{opinionpollid}")
	@Produces({ MediaType.APPLICATION_JSON })
	public PollOpinion getSpecificPollRecord(@PathParam("opinionpollid") String opinionpollid) throws ServiceException {
		Log.debug("CustomPollingService: getSpecificPollRecord(): START : opinionpollid: " + opinionpollid);

		if (opinionpollid == null || opinionpollid.isEmpty()) {
			throw new ServiceException("Opinion Pollid should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}
		CustomPollController pollController = new CustomPollController();
		PollOpinion opinion = pollController.getSpecificPollDetail(opinionpollid);

		if (opinion == null) {
			Log.debug("CustomPollingService: getSpecificPollRecord(): END: Throw Error");
			throw new ServiceException("Invalid Poll Id. Poll not found.", "", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
					Response.Status.BAD_REQUEST, null);
		} else {
			Log.debug("CustomPollingService: getSpecificPollRecord(): END: opinion: " + opinion.toString());
			return opinion;
		}
	}

	@DELETE
	@Path("/{opinionpollid}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteOpinionPoll(@PathParam("opinionpollid") String opinionpollid) throws ServiceException {
		Log.debug("CustomPollingService: deleteOpinionPoll(): START : opinionpollid: " + opinionpollid);

		if (opinionpollid == null || opinionpollid.isEmpty()) {
			throw new ServiceException("Opinion Poll Id should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}
//			if (deleteOpinionPollDto.getPollId() == null || deleteOpinionPollDto.getPollId().isEmpty()) {
//				throw new ServiceException("Opinion Pollid should not be null or empty.", "",
//						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
//			} else if (deleteOpinionPollDto.getFromJId() == null || deleteOpinionPollDto.getPollId().isEmpty()) {
//				throw new ServiceException("fromJID should not be null or empty.", "",
//						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
//			} else if (deleteOpinionPollDto.getPollMessageId() == null
//					|| deleteOpinionPollDto.getPollMessageId().isEmpty()) {
//				throw new ServiceException("messageID should not be null or empty.", "",
//						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
//			} else if (deleteOpinionPollDto.getRoomId() == null || deleteOpinionPollDto.getRoomId().isEmpty()) {
//				throw new ServiceException("roomID should not be null or empty.", "",
//						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
//			}

		CustomPollController pollController = new CustomPollController();
		PollOpinion opinion = pollController.getSpecificPollDetail(opinionpollid);

		if (opinion == null)
			throw new ServiceException("Invalid Poll Id. Poll not found.", "", ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION,
					Response.Status.BAD_REQUEST, null);

//				MultiUserChatService multiUserChatService = XMPPServer.getInstance().getMultiUserChatManager()
//						.getMultiUserChatService("conference");
//				MUCRoom chatRoom = multiUserChatService.getChatRoom(deleteOpinionPollDto.getRoomId());

		pollController.disablePoll(opinionpollid);

//				Message message = new Message();
//				message.setType(Message.Type.groupchat);
//				message.setSubject("18");
//				message.setTo(chatRoom.getJID());
//				message.setFrom(deleteOpinionPollDto.getFromJId());
//
//				Element e = message.addChildElement("pollDelete", "urn:xmpp:deletepoll");
//
//				e.addElement("roomId").setText(deleteOpinionPollDto.getRoomId());
//				e.addElement("pollId").setText(deleteOpinionPollDto.getPollId());
//				e.addElement("pollMessageId").setText(deleteOpinionPollDto.getPollMessageId());
//
//				chatRoom.serverBroadcast(message);
		
		Log.debug("CustomPollingService: deleteOpinionPoll(): END");

		return Response.status(Response.Status.OK).build();

	}
}
/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */
