package org.jivesoftware.openfire.plugin.rest.service;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.openfire.plugin.rest.controller.MUCRoomController;

/**
 * @author pritesh_desai
 * MUCRoomOwnersService used for Get the room Owner Service
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@Path("restapi/v1/chatrooms/{roomName}/owners")
public class MUCRoomOwnersService {
	
	private static Logger LOG = LoggerFactory.getLogger(MUCRoomOwnersService.class);

    @POST
    @Path("/{jid}")
    public Response addMUCRoomOwner(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("jid") String jid, @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOwnersService: addMUCRoomOwner(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
        MUCRoomController.getInstance().addOwner(serviceName, roomName, jid);
        LOG.debug("MUCRoomOwnersService: addMUCRoomOwner(): END");
        return Response.status(Status.CREATED).build();
    }

    @POST
    @Path("/group/{groupname}")
    public Response addMUCRoomOwnerGroup(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("groupname") String groupname, @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOwnersService: addMUCRoomOwnerGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
        MUCRoomController.getInstance().addOwner(serviceName, roomName, groupname);
        LOG.debug("MUCRoomOwnersService: addMUCRoomOwnerGroup(): END");
        return Response.status(Status.CREATED).build();
    }

    @DELETE
    @Path("/{jid}")
    public Response deleteMUCRoomOwner(@PathParam("jid") String jid,
            @DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOwnersService: deleteMUCRoomOwner(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
        MUCRoomController.getInstance().deleteAffiliation(serviceName, roomName, jid);
        LOG.debug("MUCRoomOwnersService: deleteMUCRoomOwner(): END");
        return Response.status(Status.OK).build();
    }

    @DELETE
    @Path("/group/{groupname}")
    public Response deleteMUCRoomOwnerGroup(@PathParam("groupname") String groupname,
            @DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOwnersService: deleteMUCRoomOwnerGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
        MUCRoomController.getInstance().deleteAffiliation(serviceName, roomName, groupname);
        LOG.debug("MUCRoomOwnersService: deleteMUCRoomOwnerGroup(): END");
        return Response.status(Status.OK).build();
    }
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/
