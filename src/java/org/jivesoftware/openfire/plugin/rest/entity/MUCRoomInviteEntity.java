package org.jivesoftware.openfire.plugin.rest.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * MUCRoomInviteEntity is used to Invite the User in Room
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement
public class MUCRoomInviteEntity {

	private String roomJID;
	private List<String> memberList;
	private String roomName;
	private String roomProfileURL;
	private long roomCreationDate;
	private String roomOwnerJID;

	public MUCRoomInviteEntity() {
	}

	public MUCRoomInviteEntity(String roomJID, List<String> memberList, String roomName, String roomProfileURL,
			long roomCreationDate, String roomOwnerJID) {
		super();
		this.roomJID = roomJID;
		this.memberList = memberList;
		this.roomName = roomName;
		this.roomProfileURL = roomProfileURL;
		this.roomCreationDate = roomCreationDate;
		this.roomOwnerJID = roomOwnerJID;
	}

	@XmlElement(required = true)
	public String getRoomJID() {
		return roomJID;
	}

	public void setRoomJID(String roomJID) {
		this.roomJID = roomJID;
	}

	@XmlElement(required = true)
	public List<String> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<String> memberList) {
		this.memberList = memberList;
	}

	@XmlElement
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	@XmlElement
	public String getRoomProfileURL() {
		return roomProfileURL;
	}

	public void setRoomProfileURL(String roomProfileURL) {
		this.roomProfileURL = roomProfileURL;
	}

	@XmlElement
	public long getRoomCreationDate() {
		return roomCreationDate;
	}

	public void setRoomCreationDate(long roomCreationDate) {
		this.roomCreationDate = roomCreationDate;
	}

	@XmlElement
	public String getRoomOwnerJID() {
		return roomOwnerJID;
	}

	public void setRoomOwnerJID(String roomOwnerJID) {
		this.roomOwnerJID = roomOwnerJID;
	}

	@Override
	public String toString() {
		return "MUCRoomInviteEntity [roomJID=" + roomJID + ", memberList=" + memberList + ", roomName=" + roomName
				+ ", roomProfileURL=" + roomProfileURL + ", roomCreationDate=" + roomCreationDate + ", roomOwnerJID="
				+ roomOwnerJID + "]";
	}
}
/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */
