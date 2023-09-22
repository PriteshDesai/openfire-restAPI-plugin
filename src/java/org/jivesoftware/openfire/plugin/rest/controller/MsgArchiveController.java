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

package org.jivesoftware.openfire.plugin.rest.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.plugin.rest.entity.MUCRoomEntity;
import org.jivesoftware.openfire.plugin.rest.entity.OldMessageEntity;
import org.jivesoftware.openfire.plugin.rest.entity.OldMessagesEntity;
import org.jivesoftware.util.JiveGlobals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmpp.packet.JID;

/**
 * The Class MsgArchiveController.
 */
public class MsgArchiveController {

	/** The Constant LOG. */
	private static final Logger LOG = LoggerFactory.getLogger(MsgArchiveController.class);

	
	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	
	/** The Constant INSTANCE. */
	public static final MsgArchiveController INSTANCE = new MsgArchiveController();

	public static final int pastMonth = JiveGlobals.getIntProperty("message.archive.data.history.month", 1);

	public static final int limit = JiveGlobals.getIntProperty("message.archive.data.limit", 100);

	public static final int roomLimit = JiveGlobals.getIntProperty("room.message.archive.data.limit", 25);

	/** The Constant USER_MESSAGE_COUNT. */
	private static final String USER_MESSAGE_COUNT = "select COUNT(1) from ofMessageArchive a "
			+ "join ofPresence p on (a.sentDate > p.offlineDate) " + "WHERE a.toJID = ? AND p.username = ?";

//	private static final String USER_ROOM_MESSAGE_COUNT = "select COUNT(1) from ofMucConversationLog a "
//			+ "join ofPresence p on (CAST (a.logTime as BIGINT) > CAST (p.offlineDate as BIGINT)) "
//			+ "join ofmucroom r on a.roomid = r.roomid WHERE r.name = ? and p.username = ?;";
//
//	private static final String USER_LATEST_MESSAGE = "select * from ofmessagearchive"
//			+ " where (fromjid = ? and tojid = ?) or (tojid = ? and fromjid = ?) order by sentdate desc limit 1;";

	private static final String OLD_USER_MESSAGE = "select coalesce(body, '') as body, "
			+ "coalesce(fromjid, '') as fromjid, coalesce(tojid, '') as tojid, coalesce(stanza, '') as stanza, "
			+ "coalesce(sentdate, 0) as sentdate from ofmessagearchive"
			+ " where ((fromjid = ? and tojid = ?) or (tojid = ? and fromjid = ?)) and sentdate > ? "
			+ "order by sentdate desc offset ? limit ?;";

	private static final String OLD_USER_MESSAGE_COUNT = "select count(*) from ofmessagearchive"
			+ " where (fromjid = ? and tojid = ?) or (tojid = ? and fromjid = ?) and sentdate > ?;";

	private static final String OLD_ROOM_MESSAGE = "select coalesce(body, '') as body, "
			+ "coalesce(fromjid, '') as fromjid, coalesce(tojid, '') as tojid, coalesce(stanza, '') as stanza, "
			+ "coalesce(sentdate, 0) as sentdate from ofmessagearchive"
			+ " where tojid = ? and sentdate > ? order by sentdate desc offset ? limit ?;";

	private static final String OLD_ROOM_MESSAGE_COUNT = "select count(*) from ofmessagearchive"
			+ " where tojid = ? and sentdate > ?;";

//	private static final String GET_USERS_LIST = "select distinct(fromjid, tojid) from ofmessagearchive "
//			+ "where sentdate > ? and (fromjid = ? or tojid = ?)";

	private static final String GET_USERS_LIST = "select distinct(fromjid, tojid) from ofmessagearchive "
			+ "where sentdate > ? and tojid like ? and (fromjid = ? or tojid = ?)";

	private static final String GET_ROOM_LIST = "select distinct(mr.name), mr.naturalname, mr.subject from ofmucroom mr "
			+ "inner join ofmucmember mm on mr.roomid = mm.roomid "
			+ "inner join ofmucaffiliation ma on mr.roomid = ma.roomid "
			+ "where CAST(mr.creationdate as BIGINT) > ? and (mm.jid = ? or ma.jid = ?);";
	
	/*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
	
	
	/**
	 * Gets the single instance of MsgArchiveController.
	 *
	 * @return single instance of MsgArchiveController
	 */
	public static MsgArchiveController getInstance() {
		return INSTANCE;
	}

	/**
	 * The Constructor.
	 */
	private MsgArchiveController() {
	}

	/**
	 * Returns the total number of messages that haven't been delivered to the user.
	 *
	 * @param jid the jid
	 * @return the total number of user unread messages.
	 */
	public int getUnReadMessagesCount(JID jid) {
		int messageCount = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			con = DbConnectionManager.getConnection();
			pstmt = con.prepareStatement(USER_MESSAGE_COUNT);
			pstmt.setString(1, jid.toBareJID());
			pstmt.setString(2, jid.getNode());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				messageCount = rs.getInt(1);
			}
		} catch (SQLException sqle) {
			LOG.error(sqle.getMessage(), sqle);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}
		return messageCount;
	}

	
	/*
	 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	 */
	
//	private int getUnReadMessagesCountMUCRoom(JID jid, JID roomJID) {
//		LOG.debug("MsgArchiveController: getUnReadMessagesCountMUCRoom(): START : jid: " + jid.toString() + " roomJID: " + roomJID.toString());	
//		int messageCount = 0;
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//			con = DbConnectionManager.getConnection();
//			pstmt = con.prepareStatement(USER_ROOM_MESSAGE_COUNT);
//			pstmt.setString(1, roomJID.getNode());
//			pstmt.setString(2, jid.getNode());
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				messageCount = rs.getInt(1);
//			}
//		} catch (SQLException sqle) {
//			LOG.error(sqle.getMessage(), sqle);
//		} finally {
//			DbConnectionManager.closeConnection(rs, pstmt, con);
//		}
//		LOG.debug("MsgArchiveController: getUnReadMessagesCountMUCRoom(): END : messageCount: " + messageCount);	
//		return messageCount;
//	}
//	
//	public OldMessageEntity getLatestMessage(String fromjid, String tojid) {
//		LOG.debug("MsgArchiveController: getLatestMessage(): START : fromjid: " + fromjid + " tojid: " + tojid);	
//		OldMessageEntity latestMessage = new OldMessageEntity();
//
//		Connection con = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		try {
//			con = DbConnectionManager.getConnection();
//			pstmt = con.prepareStatement(USER_LATEST_MESSAGE);
//			pstmt.setString(1, fromjid);
//			pstmt.setString(2, tojid);
//			pstmt.setString(3, fromjid);
//			pstmt.setString(4, tojid);
//
//			LOG.info("Latest Message Query :: " + pstmt.toString());
//
//			rs = pstmt.executeQuery();
//			if (rs.next()) {
//				latestMessage.setBody(rs.getString("body"));
//				latestMessage.setFromJID(rs.getString("fromjid"));
//				latestMessage.setToJID(rs.getString("tojid"));
//				latestMessage.setStanza(rs.getString("stanza"));
//				latestMessage.setSentDate(rs.getLong("sentdate"));
//			}
//		} catch (SQLException sqle) {
//			LOG.error(sqle.getMessage(), sqle);
//		} finally {
//			DbConnectionManager.closeConnection(rs, pstmt, con);
//		}
//
//		LOG.debug("MsgArchiveController: getLatestMessage(): END : latestMessage: " + latestMessage.toString());	
//		return latestMessage;
//	}

	public OldMessagesEntity getHistoryMessages(String fromjid, String tojid, int page) {
		LOG.debug("MsgArchiveController: getHistoryMessages(): START : fromjid: " + fromjid + " tojid: " + tojid + " page: " + page);	

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<OldMessageEntity> oldMessages = new ArrayList<OldMessageEntity>();
		OldMessagesEntity oldMessagesEntity = new OldMessagesEntity();

		try {

			con = DbConnectionManager.getConnection();

			// Check if jid is for 1-1 chat or muc room.
			boolean isRoom = false;
			if (tojid.contains("@conference." + JiveGlobals.getProperty("xmpp.domain"))) {
				isRoom = true;
			}

			Calendar calNow = Calendar.getInstance();

			// adding -1 month
			calNow.add(Calendar.MONTH, -pastMonth);

			// fetching updated time
			Date dateBeforeAMonth = calNow.getTime();

			int offset = 0;

			if (page > 1) {
				offset = (page - 1) * limit;
			}

			oldMessagesEntity.setJid(tojid);
			oldMessagesEntity
					.setTotalMessageCount(getTotalMessageCount(fromjid, tojid, dateBeforeAMonth.getTime(), isRoom));

			if (isRoom) {
				pstmt = con.prepareStatement(OLD_ROOM_MESSAGE);

				pstmt.setString(1, tojid);
				pstmt.setLong(2, dateBeforeAMonth.getTime());
				pstmt.setInt(3, offset);
				pstmt.setInt(4, limit);
			} else {
				pstmt = con.prepareStatement(OLD_USER_MESSAGE);

				pstmt.setString(1, fromjid);
				pstmt.setString(2, tojid);
				pstmt.setString(3, fromjid);
				pstmt.setString(4, tojid);
				pstmt.setLong(5, dateBeforeAMonth.getTime());
				pstmt.setInt(6, offset);
				pstmt.setInt(7, limit);
			}
			LOG.info("Old Message Query :: " + pstmt.toString());

			OldMessageEntity oldMessage = null;
			rs = pstmt.executeQuery();

			while (rs.next()) {

				oldMessage = new OldMessageEntity();

				oldMessage.setBody(rs.getString("body"));
				oldMessage.setFromJID(rs.getString("fromjid"));
				oldMessage.setToJID(rs.getString("tojid"));
				oldMessage.setStanza(rs.getString("stanza"));
				oldMessage.setSentDate(rs.getLong("sentdate"));

				oldMessages.add(oldMessage);
			}

			oldMessagesEntity.setMessages(oldMessages);

		} catch (SQLException sqle) {
			LOG.error(sqle.getMessage(), sqle);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}

		LOG.debug("MsgArchiveController: getHistoryMessages(): END : oldMessagesEntity: " + oldMessagesEntity.toString());	

		return oldMessagesEntity;
	}

	/**
	 * Get Chat History Messages with the Pagination
	 * 
	 * @param fromjid
	 * @param tojid
	 * @param page
	 * @return
	 */
	public OldMessagesEntity getChatHistoryMessages(String fromjid, String tojid, long lastTimeStamp, int page) {

		LOG.debug("MsgArchiveController: getChatHistoryMessages(): START : fromjid: " + fromjid + " tojid: " + tojid + " lastTimeStamp: " + lastTimeStamp + " page: "+ page);;	

		
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<OldMessageEntity> oldMessages = new ArrayList<OldMessageEntity>();
		OldMessagesEntity oldMessagesEntity = new OldMessagesEntity();

		try {

			con = DbConnectionManager.getConnection();

			int offset = 0;

			if (page > 1) {
				offset = (roomLimit * page) - roomLimit;
			}

			oldMessagesEntity.setJid(tojid);

			if (fromjid == null) {
				oldMessagesEntity.setTotalMessageCount(getTotalMessageCount(null, tojid, lastTimeStamp, true));

				pstmt = con.prepareStatement(OLD_ROOM_MESSAGE);

				pstmt.setString(1, tojid);
				pstmt.setLong(2, lastTimeStamp);
				pstmt.setInt(3, offset);
				pstmt.setInt(4, roomLimit);
			} else {
				oldMessagesEntity.setTotalMessageCount(getTotalMessageCount(fromjid, tojid, lastTimeStamp, false));

				pstmt = con.prepareStatement(OLD_USER_MESSAGE);

				pstmt.setString(1, fromjid);
				pstmt.setString(2, tojid);
				pstmt.setString(3, fromjid);
				pstmt.setString(4, tojid);
				pstmt.setLong(5, lastTimeStamp);
				pstmt.setInt(6, offset);
				pstmt.setInt(7, roomLimit);
			}
			LOG.info("Old Message Query :: " + pstmt.toString());

			OldMessageEntity oldMessage = null;
			rs = pstmt.executeQuery();

			while (rs.next()) {

				oldMessage = new OldMessageEntity();

				oldMessage.setBody(rs.getString("body"));
				oldMessage.setFromJID(rs.getString("fromjid"));
				oldMessage.setToJID(rs.getString("tojid"));
				oldMessage.setStanza(rs.getString("stanza"));
				oldMessage.setSentDate(rs.getLong("sentdate"));

				oldMessages.add(oldMessage);
			}

			oldMessagesEntity.setMessages(oldMessages);
			oldMessagesEntity.setSubject("");
			oldMessagesEntity.setNaturalName("");
		} catch (SQLException sqle) {
			LOG.error(sqle.getMessage(), sqle);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
		}

		LOG.debug("MsgArchiveController: getChatHistoryMessages(): END: oldMessagesEntity: " + oldMessagesEntity.toString());
		return oldMessagesEntity;
	}

	public List<OldMessagesEntity> getAllMessages(String jid, int page) {

		LOG.debug("MsgArchiveController: getAllMessages(): START : jid: " + jid + " page: "+ page);;	

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		List<OldMessagesEntity> oldMessagesEntityList = new ArrayList<OldMessagesEntity>();
		OldMessageEntity oldMessage = null;

		try {

			con = DbConnectionManager.getConnection();

			Calendar calNow = Calendar.getInstance();

			// adding -1 month
			calNow.add(Calendar.MONTH, -pastMonth);

			// fetching updated time
			Date dateBeforeAMonth = calNow.getTime();

			// Get Users list for 1-1 chat only.
			Set<String> usersList = getUsersList(jid, dateBeforeAMonth);

			// Get Users list for group chat only.
			pstmt = con.prepareStatement(GET_ROOM_LIST);
			pstmt.setLong(1, dateBeforeAMonth.getTime());
			pstmt.setString(2, jid);
			pstmt.setString(3, jid);

			LOG.info("Get Users List Query :: " + pstmt.toString());

			rs = pstmt.executeQuery();

			HashMap<String, MUCRoomEntity> roomsData = new HashMap<String, MUCRoomEntity>();
			MUCRoomEntity room = null;
			String roomId = "";

			while (rs.next()) {

				roomId = rs.getString("name") + "@conference." + JiveGlobals.getProperty("xmpp.domain");
				usersList.add(roomId);

				room = new MUCRoomEntity();
				room.setNaturalName(rs.getString("naturalname"));
				room.setSubject(rs.getString("subject"));

				roomsData.put(roomId, room);
			}

			for (String fromjid : usersList) {

				if (fromjid.equals(jid)) {
					continue;
				}

				OldMessagesEntity oldMessagesEntity = new OldMessagesEntity();

				// Check if jid is for chat or muc room.
				boolean isRoom = false;
				if (fromjid.contains("@conference." + JiveGlobals.getProperty("xmpp.domain"))) {
					isRoom = true;
				}

				// Set total message count for chat or muc room conversation.
				oldMessagesEntity
						.setTotalMessageCount(getTotalMessageCount(jid, fromjid, dateBeforeAMonth.getTime(), isRoom));

				List<OldMessageEntity> oldMessages = new ArrayList<OldMessageEntity>();

				PreparedStatement pstmt2 = null;
				ResultSet oldMessagers = null;

				try {
					if (isRoom) {
						pstmt2 = con.prepareStatement(OLD_ROOM_MESSAGE);

						pstmt2.setString(1, fromjid);
						pstmt2.setLong(2, dateBeforeAMonth.getTime());
						pstmt2.setInt(3, 0);
						pstmt2.setInt(4, 1);

						// Set Subject and NaturalName for room.
						MUCRoomEntity mucRoomEntity = roomsData.get(fromjid);

						if (mucRoomEntity != null) {
							oldMessagesEntity.setSubject(mucRoomEntity.getSubject());
							oldMessagesEntity.setNaturalName(mucRoomEntity.getNaturalName());
						}
					} else {
						pstmt2 = con.prepareStatement(OLD_USER_MESSAGE);

						pstmt2.setString(1, jid);
						pstmt2.setString(2, fromjid);
						pstmt2.setString(3, jid);
						pstmt2.setString(4, fromjid);
						pstmt2.setLong(5, dateBeforeAMonth.getTime());
						pstmt2.setInt(6, 0);
						pstmt2.setInt(7, 1);
					}

					LOG.info("Old Message Query :: " + pstmt2.toString());

					oldMessagers = pstmt2.executeQuery();

					while (oldMessagers.next()) {

						oldMessage = new OldMessageEntity();

						oldMessage.setBody(oldMessagers.getString("body"));
						oldMessage.setFromJID(oldMessagers.getString("fromjid"));
						oldMessage.setToJID(oldMessagers.getString("tojid"));
						oldMessage.setStanza(oldMessagers.getString("stanza"));
						oldMessage.setSentDate(oldMessagers.getLong("sentdate"));

						oldMessages.add(oldMessage);
					}

				} catch (SQLException sqle) {
					LOG.error(sqle.getMessage(), sqle);
				} finally {
					DbConnectionManager.closeStatement(oldMessagers, pstmt2);
					LOG.info("MsgArciveController : Prepare statmenta and result set closed");
				}

				oldMessagesEntity.setMessages(oldMessages);
				oldMessagesEntity.setJid(fromjid);

				oldMessagesEntityList.add(oldMessagesEntity);
			}
		} catch (SQLException sqle) {
			LOG.error(sqle.getMessage(), sqle);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
			LOG.info("MsgArciveController : Prepare statmenta and result set closed");
		}

		LOG.debug("MsgArchiveController: getAllMessages(): END : oldMessagesEntityList: " + oldMessagesEntityList.toString());
		
		return oldMessagesEntityList;
	}

	/**
	 * Get users list for 1-1 chat only.
	 * 
	 * @param jid
	 * @param dateBeforeAMonth
	 * @return
	 */
	private Set<String> getUsersList(String jid, Date dateBeforeAMonth) {

		LOG.debug("MsgArchiveController: getUsersList(): START : jid: " + jid + " dateBeforeAMonth: " + dateBeforeAMonth);
		
		Set<String> usersList = new HashSet<String>();

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			con = DbConnectionManager.getConnection();

			// Get 1-1 chat users list from past month.
			pstmt = con.prepareStatement(GET_USERS_LIST);
			pstmt.setLong(1, dateBeforeAMonth.getTime());
			pstmt.setString(2, "%@openfire%");
			pstmt.setString(3, jid);
			pstmt.setString(4, jid);

			rs = pstmt.executeQuery();

			while (rs.next()) {

				String[] row = rs.getString("row").substring(1, rs.getString("row").length() - 1).split(",");

				usersList.add(row[0]);
				usersList.add(row[1]);
			}
		} catch (SQLException sqle) {
			LOG.error(sqle.getMessage(), sqle);
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, con);
			LOG.info("MsgArciveController : getUsersList : Prepare statmenta and result set closed");
		}

		LOG.debug("MsgArchiveController: getUsersList(): END : usersList: " + usersList.toString());

		return usersList;
	}

	private int getTotalMessageCount(String jid, String fromjid, long historyDays, boolean isRoom) {

		LOG.debug("MsgArchiveController: getTotalMessageCount(): START : jid: " + jid + " fromjid: " + fromjid + " historyDays: " + historyDays + " isRoom: " + isRoom);

		int messageCount = 0;

		Connection con = null;
		PreparedStatement pstmt1 = null;
		ResultSet countrs = null;

		try {
			con = DbConnectionManager.getConnection();

			if (isRoom) {
				pstmt1 = con.prepareStatement(OLD_ROOM_MESSAGE_COUNT);

				pstmt1.setString(1, fromjid);
				pstmt1.setLong(2, historyDays);
			} else {
				pstmt1 = con.prepareStatement(OLD_USER_MESSAGE_COUNT);

				pstmt1.setString(1, jid);
				pstmt1.setString(2, fromjid);
				pstmt1.setString(3, jid);
				pstmt1.setString(4, fromjid);
				pstmt1.setLong(5, historyDays);
			}

			countrs = pstmt1.executeQuery();

			if (countrs.next()) {
				messageCount = countrs.getInt(1);
			}

		} catch (SQLException sqle) {
			LOG.error(sqle.getMessage(), sqle);
		} finally {
			DbConnectionManager.closeConnection(countrs, pstmt1, con);
			LOG.info("MsgArciveController : getTotalMessageCount : Prepare statmenta and result set closed");
		}

		LOG.debug("MsgArchiveController: getTotalMessageCount(): START : messageCount: " + messageCount);
		
		return messageCount;
	}
	
	 /*
	 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
	 */
}
