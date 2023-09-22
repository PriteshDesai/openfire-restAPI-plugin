package org.jivesoftware.openfire.plugin.rest.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.jivesoftware.database.DbConnectionManager;
import org.jivesoftware.openfire.custom.dto.PollOpinion;
import org.jivesoftware.openfire.custom.dto.PollOptions;
import org.jivesoftware.openfire.custom.dto.PollOptionsResult;
import org.jivesoftware.openfire.custom.dto.Users;
import org.jivesoftware.openfire.plugin.rest.exceptions.ExceptionType;
import org.jivesoftware.openfire.plugin.rest.exceptions.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pritesh_desai
 * CustomPollController is created to handle the Poll related operations.
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
public class CustomPollController {

	private static final Logger Log = LoggerFactory.getLogger(CustomPollController.class);

	private static final StringBuffer SQL_GET_ALL_OPINION_POLL = new StringBuffer(
			"select o.pollid,o.createdby,o.createdat,o.expiredat,o.timezone,o.isexpired,o.question,o.ofroomid,array_agg(op.optionname) as polloptions, array_agg(op.polloptionid) as polloptionids ")
			.append(" from ofpollmaster o ").append(" inner join ofpolloptions op on (op.pollid = o.pollid) ")
			.append(" group by o.pollid ");

	private static final StringBuffer SQL_GET_SPECIFIC_OPINION_POLL = new StringBuffer(
			"select o.pollid,o.createdby,o.createdat,o.expiredat,o.timezone,o.isexpired,o.question,o.ofroomid,array_agg(op.optionname) as polloptions, array_agg(op.polloptionid) as polloptionids ")
			.append(" from ofpollmaster o ").append(" inner join ofpolloptions op on (op.pollid = o.pollid) ")
			.append(" WHERE o.pollid = ? ").append(" group by o.pollid ");

	private static final StringBuffer SQL_GET_ALL_POLL_OPTIONS = new StringBuffer(
			"select o2.optionname,count(ops.username) as usercount,array_agg(ops.username) as users from ofpolluserresponse ops ")
			.append(" right join ofpolloptions o2 on (o2.polloptionid = ops.polloptionid) where o2.pollid = ?")
			.append(" group by ops.polloptionid, o2.optionname ");

	private static final StringBuffer SQL_GET_OPINION_POLL_USER_DETAILS = new StringBuffer(
			" select o4.name ,o4.username , o4.email,COALESCE(o5.optionname,null) as seloption  from ofmucmember o ")
			.append(" inner join ofpollmaster o2 on (o2.ofroomid = o.roomid) ")
			.append(" inner join ofuser o4 on (o4.username = left(o.jid, strpos(o.jid, '@') - 1)) ")
			.append(" left join ofpolluserresponse o3 on (o3.username = o4.username and o3.pollid = ? ) ")
			.append(" left join ofpolloptions o5 on (o5.polloptionid = o3.polloptionid) ")
			.append(" where o2.pollid = ? ");

	private static final StringBuffer SQL_DELETE_POLL = new StringBuffer(
			" update ofpollmaster SET isexpired = true where pollid = ?");

	public List<PollOpinion> getAllPollDetail() {
		Log.debug("CustomPollController: getAllPollDetail(): START");
		
		List<PollOpinion> pollOpinions = new ArrayList<PollOpinion>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_GET_ALL_OPINION_POLL.toString());
			rs = pstmt.executeQuery();

			while (rs.next()) {
				PollOpinion pollOpinion = new PollOpinion();
				pollOpinion.setPollId(rs.getString("pollid"));
				pollOpinion.setCreatedBy(rs.getString("createdby"));
				pollOpinion.setCreatedOn(rs.getLong("createdat"));
				pollOpinion.setExpiredAt(rs.getLong("expiredat"));
				pollOpinion.setTimeZone(rs.getString("timezone"));
				pollOpinion.setExpired(rs.getBoolean("isexpired"));
				pollOpinion.setQuestion(rs.getString("question"));
				pollOpinion.setRoomId(rs.getInt("ofroomid"));
				pollOpinion.setPollOptionsText((String[]) rs.getArray("polloptions").getArray());
				pollOpinion.setPollOptionIds((Integer[]) rs.getArray("polloptionids").getArray());

				pstmt = conn.prepareStatement(SQL_GET_ALL_POLL_OPTIONS.toString());
				pstmt.setString(1, rs.getString("pollid"));

				ResultSet oprs = pstmt.executeQuery();

				List<PollOptions> pollOptions = new ArrayList<PollOptions>();
				while (oprs.next()) {
					PollOptions pollOption = new PollOptions();
					pollOption.setCount(oprs.getInt("usercount"));
					pollOption.setOptionName(oprs.getString("optionname"));
					pollOption.setUsers((String[]) oprs.getArray("users").getArray());

					pollOptions.add(pollOption);
				}
				pollOpinion.setPollOptions(pollOptions);
				pstmt.close();
				pstmt = conn.prepareStatement(SQL_GET_OPINION_POLL_USER_DETAILS.toString());
				pstmt.setString(1, rs.getString("pollid"));
				pstmt.setString(2, rs.getString("pollid"));

				ResultSet oprss = pstmt.executeQuery();
				List<Users> users = new ArrayList<Users>();
				while (oprss.next()) {
					Users user = new Users();
					user.setName(oprss.getString("name"));
					user.setUserName(oprss.getString("username"));
					user.setEmail(oprss.getString("email"));
					user.setSelectedOption(oprss.getString("seloption"));
					users.add(user);
				}
				pollOpinion.setUsers(users);

				if (pollOpinion.isExpired()) {

					PollOptionsResult winner = null;
					if (pollOptions != null && pollOptions.size() > 0) {
						for (PollOptions optionsRes : pollOptions) {
							if (winner == null) {
								winner = new PollOptionsResult();
								winner.setStatus("win");
								winner.setPolloption(optionsRes);
								continue;
							}

							if (optionsRes != null && winner != null) {
								if (optionsRes.getCount() > winner.getPolloption().getCount()) {
									winner.setStatus("win");
									winner.setPolloption(optionsRes);
								} else if (winner.getPolloption().getCount() == optionsRes.getCount()
										&& optionsRes.getCount() == 0) {
									winner.setStatus("No Response");
									winner.setPolloption(null);
								} else if (winner.getPolloption().getCount() == optionsRes.getCount()) {
									winner.setStatus("Tie");
									winner.setPolloption(null);
								}
							}
						}
					}
					if (winner == null) {
						winner = new PollOptionsResult("No Response", null);
					} else if (winner != null && winner.getPolloption() != null
							&& winner.getPolloption().getCount() == 0) {
						winner.setStatus("No Response");
						winner.setPolloption(null);
					}
					pollOpinion.setResult(winner);
				} else {
					PollOptionsResult result = new PollOptionsResult("running", null);
					pollOpinion.setResult(result);
				}
				pollOpinions.add(pollOpinion);
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, conn);
			Log.info("CustomPollController : getAllPollDetails : PreparedStatement and connection is closed.");
		}
		
		Log.debug("CustomPollController: getAllPollDetail(): END: pollOpinions: " + pollOpinions.toString());

		return pollOpinions;

	}

	public PollOpinion getSpecificPollDetail(String pollId) {
		Log.debug("CustomPollController: getSpecificPollDetail(): START : pollId: " + pollId);

		PollOpinion pollOpinion = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_GET_SPECIFIC_OPINION_POLL.toString());
			pstmt.setString(1, pollId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				pollOpinion = new PollOpinion();
				pollOpinion.setPollId(rs.getString("pollid"));
				pollOpinion.setCreatedBy(rs.getString("createdby"));
				pollOpinion.setCreatedOn(rs.getLong("createdat"));
				pollOpinion.setExpiredAt(rs.getLong("expiredat"));
				pollOpinion.setTimeZone(rs.getString("timezone"));
				pollOpinion.setExpired(rs.getBoolean("isexpired"));
				pollOpinion.setQuestion(rs.getString("question"));
				pollOpinion.setRoomId(rs.getInt("ofroomid"));
				pollOpinion.setPollOptionsText((String[]) rs.getArray("polloptions").getArray());
				pollOpinion.setPollOptionIds((Integer[]) rs.getArray("polloptionids").getArray());

				pstmt = conn.prepareStatement(SQL_GET_ALL_POLL_OPTIONS.toString());
				pstmt.setString(1, rs.getString("pollid"));

				ResultSet oprs = pstmt.executeQuery();

				List<PollOptions> pollOptions = new ArrayList<PollOptions>();
				while (oprs.next()) {
					PollOptions pollOption = new PollOptions();
					pollOption.setCount(oprs.getInt("usercount"));
					pollOption.setOptionName(oprs.getString("optionname"));
					pollOption.setUsers((String[]) oprs.getArray("users").getArray());

					pollOptions.add(pollOption);
				}
				pollOpinion.setPollOptions(pollOptions);
				pstmt.close();
				pstmt = conn.prepareStatement(SQL_GET_OPINION_POLL_USER_DETAILS.toString());
				pstmt.setString(1, rs.getString("pollid"));
				pstmt.setString(2, rs.getString("pollid"));

				ResultSet oprss = pstmt.executeQuery();
				List<Users> users = new ArrayList<Users>();
				while (oprss.next()) {
					Users user = new Users();
					user.setName(oprss.getString("name"));
					user.setUserName(oprss.getString("username"));
					user.setEmail(oprss.getString("email"));
					user.setSelectedOption(oprss.getString("seloption"));
					users.add(user);
				}
				pollOpinion.setUsers(users);

				if (pollOpinion.isExpired()) {
					PollOptionsResult winner = null;
					if (pollOptions != null && pollOptions.size() > 0) {
						for (PollOptions optionsRes : pollOptions) {
							if (winner == null) {
								winner = new PollOptionsResult();
								winner.setStatus("win");
								winner.setPolloption(optionsRes);
								continue;
							}

							if (optionsRes != null && winner != null) {
								if (optionsRes.getCount() > winner.getPolloption().getCount()) {
									winner.setStatus("win");
									winner.setPolloption(optionsRes);
								} else if (winner.getPolloption().getCount() == optionsRes.getCount()
										&& optionsRes.getCount() == 0) {
									winner.setStatus("No Response");
									winner.setPolloption(null);
								} else if (winner.getPolloption().getCount() == optionsRes.getCount()) {
									winner.setStatus("Tie");
									winner.setPolloption(null);
								}
							}
						}
					}
					if (winner == null) {
						winner = new PollOptionsResult("No Response", null);
					} else if (winner != null && winner.getPolloption() != null
							&& winner.getPolloption().getCount() == 0) {
						winner.setStatus("No Response");
						winner.setPolloption(null);
					}
					pollOpinion.setResult(winner);
				} else {
					PollOptionsResult result = new PollOptionsResult("running", null);
					pollOpinion.setResult(result);
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DbConnectionManager.closeConnection(rs, pstmt, conn);
			Log.info("CustomPollController : getSpecificPollDetail : PreparedStatement and connection is closed.");

		}
		
		Log.debug("CustomPollController: getSpecificPollDetail(): END: pollOpinion: " + pollOpinion);
		return pollOpinion;

	}

	public void disablePoll(String pollId) throws ServiceException {
		Log.debug("CustomPollController: disablePoll(): START : pollId: " + pollId);
		
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DbConnectionManager.getConnection();
			pstmt = conn.prepareStatement(SQL_DELETE_POLL.toString());
			pstmt.setString(1, pollId);
			pstmt.executeUpdate();

			Log.info("Poll Deleted Successfully.");
		} catch (Exception ex) {
			throw new ServiceException("Fail to Delete Opinion Poll.", "", ExceptionType.PROPERTY_NOT_FOUND,
					Response.Status.BAD_REQUEST);
		} finally {
			DbConnectionManager.closeConnection(pstmt, conn);
			Log.info("CustomPollController : desablePoll : PreparedStatement and connection is closed.");
		}
		
		Log.debug("CustomPollController: disablePoll(): END");

	}
}

/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */
