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
 * MUCRoomAdminsService used for the room related operations for the Admin
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@Path("restapi/v1/chatrooms/{roomName}/admins")
public class MUCRoomAdminsService {

	private static Logger Log = LoggerFactory.getLogger(MUCRoomAdminsService.class);

    @POST
    @Path("/{jid}")
    public Response addMUCRoomAdmin(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("jid") String jid, @PathParam("roomName") String roomName) throws ServiceException {
    	Log.debug("MUCRoomAdminsService: addMUCRoomAdmin(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
        MUCRoomController.getInstance().addAdmin(serviceName, roomName, jid);
        Log.debug("MUCRoomAdminsService: addMUCRoomAdmin(): END");
        return Response.status(Status.CREATED).build();
    }

    @POST
    @Path("/group/{groupname}")
    public Response addMUCRoomAdminGroup(@DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("groupname") String groupname, @PathParam("roomName") String roomName) throws ServiceException {
    	Log.debug("MUCRoomAdminsService: addMUCRoomAdminGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
        MUCRoomController.getInstance().addAdmin(serviceName, roomName, groupname);
        Log.debug("MUCRoomAdminsService: addMUCRoomAdminGroup(): END");
        return Response.status(Status.CREATED).build();
    }

    @DELETE
    @Path("/{jid}")
    public Response deleteMUCRoomAdmin(@PathParam("jid") String jid,
            @DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("roomName") String roomName) throws ServiceException {
    	Log.debug("MUCRoomAdminsService: deleteMUCRoomAdmin(): START : serviceName: " + serviceName + " jid: " + jid + " roomName: " + roomName);
        MUCRoomController.getInstance().deleteAffiliation(serviceName, roomName, jid);
        Log.debug("MUCRoomAdminsService: deleteMUCRoomAdmin(): END");
        return Response.status(Status.OK).build();
    }

    @DELETE
    @Path("/group/{groupname}")
    public Response deleteMUCRoomAdminGroup(@PathParam("groupname") String groupname,
            @DefaultValue("conference") @QueryParam("servicename") String serviceName,
            @PathParam("roomName") String roomName) throws ServiceException {
    	Log.debug("MUCRoomAdminsService: deleteMUCRoomAdminGroup(): START : serviceName: " + serviceName + " groupname: " + groupname + " roomName: " + roomName);
        MUCRoomController.getInstance().deleteAffiliation(serviceName, roomName, groupname);
        Log.debug("MUCRoomAdminsService: deleteMUCRoomAdminGroup(): END");
        return Response.status(Status.OK).build();
    }
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/
