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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.plugin.rest.controller.UserServiceController;
import org.jivesoftware.openfire.plugin.rest.entity.EnableDisableGroupNotification;
import org.jivesoftware.openfire.plugin.rest.entity.EnableDisableNotification;
import org.jivesoftware.openfire.plugin.rest.entity.UserBelongingGroupsEntity;
import org.jivesoftware.openfire.plugin.rest.entity.UserEntities;
import org.jivesoftware.openfire.plugin.rest.entity.UserEntity;
import org.jivesoftware.openfire.plugin.rest.entity.UserPresence;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.jivesoftware.openfire.plugin.rest.utils.DeviceType;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.Presence;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.jivesoftware.openfire.custom.dto.UserDeviceEntity;
import org.jivesoftware.openfire.plugin.rest.controller.MUCRoomController;
import org.jivesoftware.openfire.plugin.rest.entity.MUCRoomEntity;

@Path("restapi/v1/users")
@Tag(name = "Users", description = "Managing Openfire users.")
public class UserService {

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	private static final Logger Log = LoggerFactory.getLogger(UserService.class);

	private static final StringBuffer SQL_ADD_UPDATE_USER_DEVICE_DETAIL = new StringBuffer(
			"INSERT INTO ofuserdevicedeatil(username,jid,devicetoken,devicetype,ishuaweipush,voiptoken, channelname)")
					.append(" values(?,?,?,?,?,?,?)").append(" ON CONFLICT (username) DO UPDATE")
					.append(" set jid = excluded.jid,devicetoken=excluded.devicetoken,devicetype=excluded.devicetype,ishuaweipush=excluded.ishuaweipush,voiptoken=excluded.voiptoken");

	private static final StringBuffer SQL_GET_USER_GROUP_LIST = new StringBuffer("select * from ofmucroom o ")
			.append("inner join ofmucmember o2 on o2.roomid = o.roomid ").append("where o2.jid  = ?");

	/* This query will give you userJID from ofmucmember table */
	private static final StringBuffer SQL_GET_USERJID_FROM_ROOMID = new StringBuffer(
			"select jid from ofmucmember where roomid = ? ");

	/* This is the query to get the room id from the ofmucaffiliation table by passing user JID 
	 * and using roomId get the room name 
	 * */
	private static final StringBuffer SQL_GET_ROOMID_FROM_AFFILIATION_BY_USER_JID_SUB_QUERY_OF_GET_NAME = new 
			StringBuffer("select name from ofmucroom ")
			.append("where roomid in (SELECT roomid FROM ofmucaffiliation where jid = ?)");
	
	/*
	 * This is the query that will remove whole userDeviceDetailsEntry which voip token maches 
	 * */
	private static final StringBuffer SQL_REMOVE_DEVICE_DETAIL_FROMV_VOIP_TOKEN = 
			new StringBuffer("DELETE FROM ofuserdevicedeatil WHERE voiptoken = ?");
	
	private static final StringBuffer SQL_DEL_USER_DEVICE_DETAIL = new StringBuffer(
			"DELETE FROM ofuserdevicedeatil where username=?");

	private static final StringBuffer SQL_ENABLE_DISABLE_PUSH_NOTIFICATION = new StringBuffer(
			"UPDATE ofuserdevicedeatil set isactive = ? where username=?");

	private static final StringBuffer SQL_ENABLE_GROUP_NOTIFICATION = new StringBuffer(
			"INSERT INTO ofmucpushnotificationdisable(ofroomid, username, roomname) values((SELECT roomid from ofmucroom where name = ?),?,?) ")
			.append(" ON CONFLICT (ofroomid, username, roomname) DO UPDATE ")
			.append(" set ofroomid=excluded.ofroomid,username=excluded.username,roomname=excluded.roomname");

	private static final StringBuffer SQL_DISABLE_GROUP_NOTIFICATION = new StringBuffer(
			"DELETE FROM ofmucpushnotificationdisable WHERE username = ? and roomname = ?");

	private static final StringBuffer SQL_GET_GROUP_MUTE_LIST = new StringBuffer(
			"SELECT roomname from ofmucpushnotificationdisable where username = ?");

	private static final StringBuffer SQL_GET_PUSHNOTIFICATION_STATUS = new StringBuffer(
			"SELECT isactive from ofuserdevicedeatil where username = ?");

	private static final String UPDATE_NAME = "UPDATE ofuserdevicedeatil SET channelname=? WHERE username=?";
	 /*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	
	
	private UserServiceController plugin;
	
	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	private UserManager userManager;
	/*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	
	@PostConstruct
	public void init() {
		plugin = UserServiceController.getInstance();
		
		/*
		 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
		 */
		userManager = XMPPServer.getInstance().getUserManager().getInstance();
		/*
		 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
		 */
	}

	@GET
	@Operation(summary = "Get users", description = "Retrieve all users defined in Openfire (with optional filtering).", responses = {
			@ApiResponse(responseCode = "200", description = "A list of Openfire users.", content = @Content(schema = @Schema(implementation = UserEntities.class))), })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UserEntities getUsers(
			@Parameter(description = "Search/Filter by username. This act like the wildcard search %String%", required = false) @QueryParam("search") String userSearch,
			@Parameter(description = "Filter by a user property name.", required = false) @QueryParam("propertyKey") String propertyKey,
			@Parameter(description = "Filter by user property value. Note: This can only be used in combination with a property name parameter", required = false) @QueryParam("propertyValue") String propertyValue)
			throws ServiceException {
		return plugin.getUserEntities(userSearch, propertyKey, propertyValue);
	}

	@POST
	@Operation(summary = "Create user", description = "Add a new user to Openfire.", responses = {
			@ApiResponse(responseCode = "201", description = "The user was created."), })
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createUser(
			@RequestBody(description = "The definition of the user to create.", required = true) UserEntity userEntity)
			throws ServiceException {
		plugin.createUser(userEntity);
		return Response.status(Response.Status.CREATED).build();
	}

	@GET
	@Path("/{username}")
	@Operation(summary = "Get user", description = "Retrieve a user that is defined in Openfire.", responses = {
			@ApiResponse(responseCode = "200", description = "A list of Openfire users.", content = @Content(schema = @Schema(implementation = UserEntity.class))),
			@ApiResponse(responseCode = "404", description = "No user with that username was found."), })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UserEntity getUser(
			@Parameter(description = "The username of the user to return.", required = true) @PathParam("username") String username)
			throws ServiceException {
		return plugin.getUserEntity(username);
	}

	@PUT
	@Path("/{username}")
	@Operation(summary = "Update user", description = "Update an existing user in Openfire.", responses = {
			@ApiResponse(responseCode = "200", description = "The user was updated."), })
	public Response updateUser(
			@Parameter(description = "The username of the user to update.", required = true) @PathParam("username") String username,
			@RequestBody(description = "The definition update of the user.", required = true) UserEntity userEntity)
			throws ServiceException {
		plugin.updateUser(username, userEntity);
		return Response.status(Response.Status.OK).build();
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	@GET
	@Operation(summary = "take userJID and return all the groups in which that user is present", description = "take userJID and return all the groups in which that user is present", responses = {
			@ApiResponse(responseCode = "200", description = "A list of Openfire users.", content = @Content(schema = @Schema(implementation = UserBelongingGroupsEntity.class))),
			@ApiResponse(responseCode = "404", description = "No user with that username was found."), })
	@Path("/{userJID}/all-usergroup")
	@Produces({MediaType.APPLICATION_JSON})
	public UserBelongingGroupsEntity getUserGroupByUserJID(@PathParam("userJID") String userJID)
			throws ServiceException, SQLException {
		Log.debug("UserService: getUserGroupByUserJID(): START: userJID: " + userJID);
		Log.info("getUserGroupByUserJID : UserService");

		if (null == userJID) {
			Log.error("UserJID is null ");
			throw new ServiceException("userJID Should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		
		Connection connAffiliation = null;
		PreparedStatement pstmtAffiliation = null;
		
		try {
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_GET_USER_GROUP_LIST.toString());
			
			connAffiliation = DbConnectionManager.getConnection();
			pstmtAffiliation = connAffiliation
					.prepareStatement(SQL_GET_ROOMID_FROM_AFFILIATION_BY_USER_JID_SUB_QUERY_OF_GET_NAME.toString());
			
			pstmt.setString(1, userJID);
			pstmtAffiliation.setString(1, userJID);
			
			ResultSet rs = pstmt.executeQuery();
			ResultSet rsAffiliation = pstmtAffiliation.executeQuery();

			UserBelongingGroupsEntity groupsEntity = new UserBelongingGroupsEntity();
			List<MUCRoomEntity> rooms = new ArrayList<MUCRoomEntity>();

			// this is for regular users
			if (null != rs) {
				while (rs.next()) {
					rooms.add(MUCRoomController.getInstance().getChatRoom(rs.getString("name"), "conference", true));
				}
			}

			// This is for admin
			if (null != rsAffiliation) {
				while (rsAffiliation.next()) {
					rooms.add(MUCRoomController.getInstance().getChatRoom(rsAffiliation.getString("name"), "conference",
							true));
				}
			}

			groupsEntity.setMucRoomDTOs(rooms);
			
			Log.debug("UserService: getUserGroupByUserJID(): END: groupsEntity: " + groupsEntity.toString());
			return groupsEntity;
		} catch (SQLException sqlException) {
			Log.error("Some exception happend at SQL level : " + sqlException);
			throw new SQLException("SQLException while geting the user group : " + sqlException);
		} finally {
			DbConnectionManager.closeConnection(pstmt, conn);
			DbConnectionManager.closeConnection(pstmtAffiliation, connAffiliation);
			Log.info("UserService : getUserGroupByUserJID : prepare statment and connection is closed.");
		}
	}

	@PUT
	@Operation(summary = "Remove the voip token of the User", description = "Remove the voip token of the User", responses = {
			@ApiResponse(responseCode = "200", description = "Removed the Vopi Token"),
			@ApiResponse(responseCode = "404", description = "Not Removed the Vopi token"), })
	@Path("device-details/remove-voip-token/{voiptoken}")
	public Response removeVoipToken(@PathParam("voiptoken") String voipToken ) throws ServiceException, SQLException {
		Log.debug("UserService: removeVoipToken(): START: voipToken: " + voipToken);

		Log.info("removeVoipToken : UserService");

		if (null == voipToken) {
			Log.error("voip token is null ");
			return Response.status(Response.Status.NO_CONTENT).build();
		}
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_REMOVE_DEVICE_DETAIL_FROMV_VOIP_TOKEN.toString());
			pstmt.setString(1, voipToken);
			int effectedRow = pstmt.executeUpdate();

			if (effectedRow >= 0) {
				Log.info("Total " + effectedRow + " rows are deleted. " );
				Log.debug("UserService: removeVoipToken(): END: Voip Token Updated");
				return Response.status(Response.Status.OK).build();
			}else {
				Log.error("UserService: removeVoipToken(): END: ERROR");
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
			}
		}
		catch (SQLException sqlException) {
			Log.error("Some exception happend at SQL level : " + sqlException);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		} finally {
			DbConnectionManager.closeConnection(pstmt, conn);
			Log.info("UserService : removeVoipToken : prepare statment and connection is closed.");
		}
	}
	
	@PUT
	@Path("/profile/{username}/{profilename}")
	@Operation(summary = "Update Profile", description = "User Update Profile.", responses = {
			@ApiResponse(responseCode = "200", description = "User Profile Updated"),
			@ApiResponse(responseCode = "404", description = "User Profile Not Updated"), })
	public Response updateProfileName(@PathParam("username") String username,
			@PathParam("profilename") String profilename) throws ServiceException {
		Log.debug("UserService: updateProfileName(): START: username: " + username + " profilename: " + profilename);

		Connection con = null;
		PreparedStatement pstmt = null;
		User user = null;
		try {
			user = userManager.getUser(username);
		} catch (UserNotFoundException e) {
			throw new ServiceException("Could not get user roster", username, ExceptionType.USER_NOT_FOUND_EXCEPTION,
					Response.Status.BAD_REQUEST, e);
		}
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(UPDATE_NAME);
			if (profilename == null || profilename.matches("\\s*")) {
				pstmt.setNull(1, Types.VARCHAR);
			} else {
				pstmt.setString(1, profilename);
			}
			pstmt.setString(2, username);
			pstmt.executeUpdate();
		} catch (SQLException sqle) {
			throw new ServiceException("Invalid UserName", username, ExceptionType.USER_NOT_FOUND_EXCEPTION,
					Response.Status.BAD_REQUEST, sqle);
		} finally {
			DbConnectionManager.closeConnection(pstmt, con);
			Log.info("UserService : updateProfileName : prepare statment and connection is closed.");
		}
		Log.debug("UserService: updateProfileName(): END");
		return Response.status(Response.Status.OK).build();
	}
	/*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */

	@DELETE
	@Path("/{username}")
	@Operation(summary = "Delete user", description = "Remove an existing user from Openfire.", responses = {
			@ApiResponse(responseCode = "200", description = "The user was removed."),
			@ApiResponse(responseCode = "404", description = "No user with that username was found."), })
	public Response deleteUser(
			@Parameter(description = "The username of the user to remove.", required = true) @PathParam("username") String username)
			throws ServiceException {
		plugin.deleteUser(username);
		return Response.status(Response.Status.OK).build();
	}

	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	@GET
	@Path("/{username}/presence")
	@Operation(summary = "Get Presence of the User", description = "Get the User Presence or not.", responses = {
			@ApiResponse(responseCode = "200", description = "The user is Presence."),
			@ApiResponse(responseCode = "404", description = "The user is not Presence."), })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UserPresence getUserPresence(@PathParam("username") String username) throws ServiceException {
		Log.debug("UserService: getUserPresence(): START: username: " + username);

		User user = null;
		try {
			user = userManager.getUser(username);
		} catch (UserNotFoundException e) {
			throw new ServiceException("Could not get user roster", username, ExceptionType.USER_NOT_FOUND_EXCEPTION,
					Response.Status.BAD_REQUEST, e);
		}
		Presence presence = XMPPServer.getInstance().getPresenceManager().getPresence(user);
		if (presence == null) {
			UserPresence userPresence = new UserPresence(0);
			Log.debug("UserService: getUserPresence(): END: userPresence: " + userPresence.toString());
			return userPresence;
		}else {
			UserPresence userPresence = new UserPresence(1);
			Log.debug("UserService: getUserPresence(): END: userPresence: " + userPresence.toString());
			return userPresence;
		}
	}

	@POST
	@Path("/device")
	@Operation(summary = "Update the Device Token", description = "API is use to Update the User Device Token", responses = {
			@ApiResponse(responseCode = "200", description = "Device Token is Updated"),
			@ApiResponse(responseCode = "404", description = "Device Token is not Updated"), })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response registerUserDeviceDetails(UserDeviceEntity userDeviceEntity) throws ServiceException {
		Log.debug("UserService: registerUserDeviceDetails(): START: userDeviceEntity: " + userDeviceEntity.toString());
		
		if (userDeviceEntity.getDeviceToken() == null || userDeviceEntity.getDeviceToken().isEmpty()) {
			throw new ServiceException("Device Token Should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (userDeviceEntity.getDeviceType() == null || userDeviceEntity.getDeviceType().value().isEmpty()) {
			throw new ServiceException("Device type should not be empty or null.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (userDeviceEntity.getJid() == null || userDeviceEntity.getJid().isEmpty()) {
			throw new ServiceException("User JID Should not be empty or null", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (userDeviceEntity.getUserName() == null || userDeviceEntity.getUserName().isEmpty()) {
			throw new ServiceException("User Name Should not be empty or null", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (userDeviceEntity.getDeviceType().equals(DeviceType.IOS) && userDeviceEntity.getVoipToken() == null) {
			throw new ServiceException("Voip Token Should not be empty or null", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		Connection connection = null;
		PreparedStatement pstmt = null;

		try {
			connection = DbConnectionManager.getConnection();
			pstmt = connection.prepareStatement(SQL_ADD_UPDATE_USER_DEVICE_DETAIL.toString());
			pstmt.setString(1, userDeviceEntity.getUserName());
			pstmt.setString(2, userDeviceEntity.getJid());
			pstmt.setString(3, userDeviceEntity.getDeviceToken());
			pstmt.setString(4, userDeviceEntity.getDeviceType().value());
			pstmt.setBoolean(5, userDeviceEntity.isHuaweiPush());
			pstmt.setString(6, userDeviceEntity.getVoipToken());
			pstmt.setString(7, userDeviceEntity.getChannelName());

			Log.info(pstmt.toString());

			pstmt.execute();
		} catch (SQLException ex) {
			throw new ServiceException("Fail to add or update user device datil. " + ex.getCause(), "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST, ex);
		} finally {
			DbConnectionManager.closeConnection(pstmt, connection);
			Log.info("UserService : registerUserDeviceDetails : Prepare statment and connection is closed.");
		}

		Log.debug("UserService: registerUserDeviceDetails(): END");
		return Response.status(Response.Status.OK).build();
	}

	@DELETE
	@Path("/device/{username}")
	@Operation(summary = "Delete the particular User Device Token", description = "API is use to Delete the particular User User Device Token", responses = {
			@ApiResponse(responseCode = "200", description = "Device Token is Deleted"),
			@ApiResponse(responseCode = "404", description = "Device Token is not Deleted"), })
	public Response deleteUserDeviceDetail(@PathParam("username") String username) throws ServiceException {
		Log.debug("UserService: deleteUserDeviceDetail(): START: username: " + username);

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			if (username == null || username.isEmpty()) {
				throw new ServiceException("UserName Should not be null or empty.", "",
						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
			}
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_DEL_USER_DEVICE_DETAIL.toString());
			pstmt.setString(1, username);

			pstmt.executeUpdate();
		} catch (Exception ex) {
			throw new ServiceException("Fail to delete user device detail.", "", ExceptionType.PROPERTY_NOT_FOUND,
					Response.Status.BAD_REQUEST);
		} finally {
			DbConnectionManager.closeConnection(pstmt, conn);
			Log.info("UserService : deleteUserDeviceDetail : Prepare statment and connection is closed.");
		}

		Log.debug("UserService: deleteUserDeviceDetail(): END");
		return Response.status(Response.Status.OK).build();
	}

	@PUT
	@Path("/device/notification/onoff")
	@Operation(summary = "Enable Disable the Notification", responses = {
			@ApiResponse(responseCode = "200", description = "Notification changes are updated Sucessfully"),
			@ApiResponse(responseCode = "404", description = "Notification changes are not Updated"), })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response enableDisableNotification(EnableDisableNotification enableDisableNotification)
			throws ServiceException {
		Log.debug("UserService: enableDisableNotification(): START: enableDisableNotification: " + enableDisableNotification.toString());

		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			if (enableDisableNotification.getUserName() == null || enableDisableNotification.getUserName().isEmpty()) {
				throw new ServiceException("UserName Should not be null or empty.", "",
						ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
			}

			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_ENABLE_DISABLE_PUSH_NOTIFICATION.toString());
			pstmt.setBoolean(1, enableDisableNotification.isActive());
			pstmt.setString(2, enableDisableNotification.getUserName());
			pstmt.executeUpdate();
		} catch (Exception ex) {
			throw new ServiceException("Fail to Enabale or Disable Push Notification.", "",
					ExceptionType.PROPERTY_NOT_FOUND, Response.Status.BAD_REQUEST);
		} finally {
			DbConnectionManager.closeConnection(pstmt, conn);
			Log.info("UserService : enableDisableNotification : Prepare statment and connection is closed.");
		}
		
		Log.debug("UserService: enableDisableNotification(): END");
		return Response.status(Response.Status.OK).build();
	}

	@POST
	@Path("/device/groupnotification/onoff")
	@Operation(summary = "Notification on off for the Group", description = "API is use to On/Off the Notification for the Group", responses = {
			@ApiResponse(responseCode = "200", description = "Notification changes are updated"),
			@ApiResponse(responseCode = "404", description = "Notification changes are not updated"), })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response enableDisableGroupNotification(EnableDisableGroupNotification enableDisableGroupNotification)
			throws ServiceException {
		Log.debug("UserService: enableDisableGroupNotification(): START: enableDisableGroupNotification: " + enableDisableGroupNotification.toString());

		if (enableDisableGroupNotification.getRoomName() == null
				|| enableDisableGroupNotification.getRoomName().isEmpty()) {
			throw new ServiceException("RoomName Should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		} else if (enableDisableGroupNotification.getUserName() == null
				|| enableDisableGroupNotification.getUserName().isEmpty()) {
			throw new ServiceException("UserName Should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		if (enableDisableGroupNotification.isMute()) {
			try {
				conn = DbConnectionManager.getConnection();
				for (String userName : enableDisableGroupNotification.getUserName()) {

					pstmt = conn.prepareStatement(SQL_ENABLE_GROUP_NOTIFICATION.toString());
					pstmt.setString(1, enableDisableGroupNotification.getRoomName());
					pstmt.setString(2, userName);
					pstmt.setString(3, enableDisableGroupNotification.getRoomName());

					pstmt.execute();
					pstmt.close();
				}
				conn.close();
			} catch (Exception ex) {
				throw new ServiceException("Fail to Enabale or Disable Group Push Notification.", "",
						ExceptionType.PROPERTY_NOT_FOUND, Response.Status.BAD_REQUEST);
			} finally {
				DbConnectionManager.closeConnection(pstmt, conn);
				Log.info("UserService : enableDisableGroupNotification : Prepare statment and connection is closed.");
			}

		} else {
			try {
				conn = DbConnectionManager.getConnection();
				for (String userName : enableDisableGroupNotification.getUserName()) {

					pstmt = conn.prepareStatement(SQL_DISABLE_GROUP_NOTIFICATION.toString());
					pstmt.setString(1, userName);
					pstmt.setString(2, enableDisableGroupNotification.getRoomName());

					pstmt.execute();
					pstmt.close();
				}
				conn.close();
			} catch (Exception ex) {

			} finally {
				DbConnectionManager.closeConnection(pstmt, conn);
				Log.info("UserService : enableDisableGroupNotification : Prepare statment and connection is closed.");
			}
		}
		
		Log.debug("UserService: enableDisableGroupNotification(): END");
		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("/device/notificationstatus/{username}")
	@Operation(summary = "Get Notification on off for the user", description = "API is use to On/Off the Notification for the user", responses = {
			@ApiResponse(responseCode = "200", description = "Notification changes are get"),
			@ApiResponse(responseCode = "404", description = "Notification changes are not get"), })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public boolean getPushNotificationResponse(@PathParam("username") String username) throws ServiceException {
		Log.debug("UserService: getPushNotificationResponse(): START: username: " + username);
		if (username == null || username.isEmpty()) {
			throw new ServiceException("UserName Should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_GET_PUSHNOTIFICATION_STATUS.toString());
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs != null && rs.next()) {
				boolean flag = rs.getBoolean(1);
				Log.debug("UserService: getPushNotificationResponse(): END: flag: " + flag);
				return flag;
			}

		} catch (Exception ex) {

		} finally {
			DbConnectionManager.closeConnection(pstmt, conn);
			Log.info("UserService : getPushNotificationResponse : Prepare statment and connection is closed.");
		}
		
		Log.debug("UserService: getPushNotificationResponse(): END: flag: false");
		return false;
	}

	@GET
	@Path("/device/groupnotification/{username}")
	@Operation(summary = "Get Notification on off for the group", description = "API is use to On/Off the Notification for the group", responses = {
			@ApiResponse(responseCode = "200", description = "Notification changes are get for group"),
			@ApiResponse(responseCode = "404", description = "Notification changes are not get for group"), })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<String> getEnableDisableGroupNotification(@PathParam("username") String username)
			throws ServiceException {
		Log.debug("UserService: getEnableDisableGroupNotification(): START: username: " + username);

		if (username == null || username.isEmpty()) {
			throw new ServiceException("UserName Should not be null or empty.", "",
					ExceptionType.ILLEGAL_ARGUMENT_EXCEPTION, Response.Status.BAD_REQUEST);
		}

		Connection conn = null;
		PreparedStatement pstmt = null;
		List<String> groups = new ArrayList<String>();

		try {
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_GET_GROUP_MUTE_LIST.toString());
			pstmt.setString(1, username);
			ResultSet rs = pstmt.executeQuery();

			if (rs != null) {

				while (rs.next()) {
					groups.add(rs.getString(1));
				}
			}

		} catch (Exception ex) {
			throw new ServiceException("Fail to get Group Push Notification list.", "",
					ExceptionType.PROPERTY_NOT_FOUND, Response.Status.BAD_REQUEST);
		} finally {
			DbConnectionManager.closeConnection(pstmt, conn);
			Log.info("UserService : getEnableDisableGroupNotification : Prepare statment and connection is closed.");
		}
		
		Log.debug("UserService: getEnableDisableGroupNotification(): END: groups: " + groups.toString());
		return groups;

	}
	/*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
}
