package org.jivesoftware.openfire.plugin.rest.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jivesoftware.openfire.plugin.rest.utils.DeviceType;

/**
 * @author pritesh_desai
 * UserDeviceEntity is used to change the user device token
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "userDeviceEntity")
public class UserDeviceEntity {

	private String userName;
	private String jid;
	private DeviceType deviceType;
	private String deviceToken;
	private boolean isHuaweiPush = false;
	private String voipToken;
	private String channelName;

	public UserDeviceEntity() {
	}

	public UserDeviceEntity(String userName, String jid, DeviceType deviceType, String deviceToken,
			boolean isHuaweiPush, String voipToken, String channelName) {
		super();
		this.userName = userName;
		this.jid = jid;
		this.deviceType = deviceType;
		this.deviceToken = deviceToken;
		this.isHuaweiPush = isHuaweiPush;
		this.voipToken = voipToken;
		this.channelName = channelName;
	}

	@XmlElement
	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	@XmlElement
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@XmlElement
	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	@XmlElement
	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@XmlElement
	public String getDeviceToken() {
		return deviceToken;
	}

	public void setDeviceToken(String deviceToken) {
		this.deviceToken = deviceToken;
	}

	@XmlElement
	public boolean isHuaweiPush() {
		return isHuaweiPush;
	}

	public void setHuaweiPush(boolean isHuaweiPush) {
		this.isHuaweiPush = isHuaweiPush;
	}

	public String getVoipToken() {
		return voipToken;
	}

	public void setVoipToken(String voipToken) {
		this.voipToken = voipToken;
	}

	@Override
	public String toString() {
		return "UserDeviceEntity [userName=" + userName + ", jid=" + jid + ", deviceType=" + deviceType
				+ ", deviceToken=" + deviceToken + ", isHuaweiPush=" + isHuaweiPush + ", voipToken=" + voipToken
				+ ", channelName=" + channelName + "]";
	}

}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/