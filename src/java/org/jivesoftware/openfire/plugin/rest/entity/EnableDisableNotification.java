package org.jivesoftware.openfire.plugin.rest.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * EnableDisableNotification is use to Enable and Disable User Notification
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "enableDisableNotification")
public class EnableDisableNotification {

	private String userName;
	private boolean isActive;

	public EnableDisableNotification() {
		super();
	}

	public EnableDisableNotification(String userName, boolean isActive) {
		super();
		this.userName = userName;
		this.isActive = isActive;
	}

	@XmlElement
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	@Override
	public String toString() {
		return "EnableDisableNotification [userName=" + userName + ", isActive=" + isActive + "]";
	}
}


/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */