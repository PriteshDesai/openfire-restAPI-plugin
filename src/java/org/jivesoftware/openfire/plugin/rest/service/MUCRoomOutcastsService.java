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
 * MUCRoomOutcastsService used for the room out Cast relates service
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@Path("restapi/v1/chatrooms/{roomName}/outcasts")
public class MUCRoomOutcastsService {

	private static Logger LOG = LoggerFactory.getLogger(MUCRoomOutcastsService.class);

    @POST
    @Path("/{jid}")
    public Response addMUCRoomOutcast(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("jid") String jid, @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOutcastsService: addMUCRoomOutcast(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
        MUCRoomController.getInstance().addOutcast(serviceName, roomName, jid);
        LOG.debug("MUCRoomOutcastsService: addMUCRoomOutcast(): END");
        return Response.status(Status.CREATED).build();
    }

    @POST
    @Path("/group/{groupname}")
    public Response addMUCRoomOutcastGroup(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("groupname") String groupname, @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOutcastsService: addMUCRoomOutcastGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
        MUCRoomController.getInstance().addOutcast(serviceName, roomName, groupname);
        LOG.debug("MUCRoomOutcastsService: addMUCRoomOutcastGroup(): END");
        return Response.status(Status.CREATED).build();
    }

    @DELETE
    @Path("/{jid}")
    public Response deleteMUCRoomOutcast(@PathParam("jid") String jid,
            @DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOutcastsService: deleteMUCRoomOutcast(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
        MUCRoomController.getInstance().deleteAffiliation1(serviceName, roomName, jid);
        LOG.debug("MUCRoomOutcastsService: deleteMUCRoomOutcast(): END");
        return Response.status(Status.OK).build();
    }

    @DELETE
    @Path("/group/{groupname}")
    public Response deleteMUCRoomOutcastGroup(@PathParam("groupname") String groupname,
            @DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("roomName") String roomName) throws ServiceException {
    	LOG.debug("MUCRoomOutcastsService: deleteMUCRoomOutcastGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
        MUCRoomController.getInstance().deleteAffiliation1(serviceName, roomName, groupname);
        LOG.debug("MUCRoomOutcastsService: deleteMUCRoomOutcastGroup(): END");
        return Response.status(Status.OK).build();
    }
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/