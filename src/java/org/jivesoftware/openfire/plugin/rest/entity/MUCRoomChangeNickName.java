package org.jivesoftware.openfire.plugin.rest.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * MUCRoomChangeNickName is used to change the Room NickName
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "MUCRoomChangeNickName")
public class MUCRoomChangeNickName {

	private String roomName;
	private String naturalName;

	public MUCRoomChangeNickName() {
	}

	public MUCRoomChangeNickName(String roomName, String naturalName) {
		super();
		this.roomName = roomName;
		this.naturalName = naturalName;
	}

	@XmlElement
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	@XmlElement
	public String getNaturalName() {
		return naturalName;
	}

	public void setNaturalName(String naturalName) {
		this.naturalName = naturalName;
	}

	@Override
	public String toString() {
		return "MUCRoomChangeNickName [roomName=" + roomName + ", naturalName=" + naturalName + "]";
	}
	
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/