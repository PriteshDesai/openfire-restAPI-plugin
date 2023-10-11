package org.jivesoftware.openfire.plugin.rest.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.muc.MUCRoom;
import org.jivesoftware.openfire.plugin.rest.controller.MUCRoomController;
import org.jivesoftware.openfire.plugin.rest.entity.MUCRoomEntity;

/**
 * @author pritesh_desai
 * MUCRoomMembersService is used for the Room related operations for the Room Member.
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@Path("restapi/v1/chatrooms/{roomName}/members")
public class MUCRoomMembersService {

	private static Logger LOG = LoggerFactory.getLogger(MUCRoomMembersService.class);

	@POST
	@Path("/{jid}")
	public Response addMUCRoomMember(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
			@PathParam("jid") String jid, @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomMembersService: addMUCRoomMember(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
		MUCRoomController.getInstance().addMember(serviceName, roomName, jid);
		LOG.debug("MUCRoomMembersService: addMUCRoomMember(): END");
		return Response.status(Status.CREATED).build();
	}

	@POST
	@Path("/multiple/{jid}")
	public Response addMUCRoomMembers(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
			@PathParam("jid") String jid, @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomMembersService: addMUCRoomMembers(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
		MUCRoomController.getInstance().addMembers(serviceName, roomName, jid);
		LOG.debug("MUCRoomMembersService: addMUCRoomMembers(): END");
		return Response.status(Status.CREATED).build();
	}

	@POST
	@Path("/group/{groupname}")
	public Response addMUCRoomMemberGroup(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
			@PathParam("groupname") String groupname, @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomMembersService: addMUCRoomMemberGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
		MUCRoomController.getInstance().addMember(serviceName, roomName, groupname);
		LOG.debug("MUCRoomMembersService: addMUCRoomMemberGroup(): END");
		return Response.status(Status.CREATED).build();
	}

	@DELETE
	@Path("/{jid}")
	@Produces({ MediaType.APPLICATION_JSON })
	public MUCRoomEntity deleteMUCRoomMember(@PathParam("jid") String jid,
			@DefaultValue("conference") @QueryParam("servicename") String serviceName,
			@PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomMembersService: deleteMUCRoomMember(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
    	
		MUCRoomController.getInstance().deleteAffiliation1(serviceName, roomName, jid);
		
		MUCRoom chatRoom = XMPPServer.getInstance().getMultiUserChatManager().getMultiUserChatService(serviceName)
				.getChatRoom(roomName);		
		MUCRoomEntity room = MUCRoomController.getInstance().getChatRoom(roomName, serviceName, false);

		int memberCount = 0;
		int ownerCount = 0;

		LOG.debug("Owners: " + chatRoom.getOwners());
		LOG.debug("Members: " + chatRoom.getMembers());
		LOG.debug("Owners Size: " + chatRoom.getOwners().size());
		LOG.debug("Members Size: " + chatRoom.getMembers().size());

		if (chatRoom != null) {
			ownerCount = chatRoom.getOwners().size();
			memberCount = chatRoom.getMembers().size();
		}
		

		if ((ownerCount + memberCount) <= 1) {
			// delete the room if only one user in the room.
			LOG.debug("Delete the Chat room is called");
			room.setPersistent(false);
			MUCRoomController.getInstance().deleteChatRoom(roomName, serviceName);
		}

		LOG.debug("MUCRoomMembersService: deleteMUCRoomMember(): END: room: " + room.toString());
		return room;
	}

	@DELETE
	@Path("/group/{groupname}")
	public Response deleteMUCRoomMemberGroup(@PathParam("groupname") String groupname,
			@DefaultValue("conference") @QueryParam("servicename") String serviceName,
			@PathParam("roomName") String roomName) throws ServiceException {
    	LOG.info("MUCRoomMembersService: deleteMUCRoomMemberGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
		MUCRoomController.getInstance().deleteAffiliation1(serviceName, roomName, groupname);
		LOG.debug("MUCRoomMembersService: deleteMUCRoomMemberGroup(): END");
		return Response.status(Status.OK).build();
	}
}
/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */
