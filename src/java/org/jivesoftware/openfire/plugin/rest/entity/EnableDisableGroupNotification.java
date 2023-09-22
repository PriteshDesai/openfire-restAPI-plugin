package org.jivesoftware.openfire.plugin.rest.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * EnableDisableGroupNotification is use to Enable and Disable Group Notification
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "enableDisableGroupNotification")
public class EnableDisableGroupNotification {

	private List<String> userName;
	private String roomName;
	private boolean isMute;

	public EnableDisableGroupNotification() {
		super();
	}

	public EnableDisableGroupNotification(List<String> userName, String roomName, boolean isMute) {
		super();
		this.userName = userName;
		this.roomName = roomName;
		this.isMute = isMute;
	}

	@XmlElement
	public List<String> getUserName() {
		return userName;
	}

	public void setUserName(List<String> userName) {
		this.userName = userName;
	}

	@XmlElement
	public String getRoomName() {
		return roomName;
	}

	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}

	@XmlElement
	public boolean isMute() {
		return isMute;
	}

	public void setMute(boolean isMute) {
		this.isMute = isMute;
	}

	@Override
	public String toString() {
		return "EnableDisableGroupNotification [userName=" + userName + ", roomName=" + roomName + ", isMute=" + isMute
				+ "]";
	}
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/
