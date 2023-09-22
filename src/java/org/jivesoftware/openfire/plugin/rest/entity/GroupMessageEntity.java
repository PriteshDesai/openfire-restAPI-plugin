package org.jivesoftware.openfire.plugin.rest.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * GroupMessageEntity user to send the message in the Group
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "groupMessage")
public class GroupMessageEntity {

	private String fromJID;

	private String groupName;

	private String subject;

	private String senderName;

	private String senderImage;

	private String senderUUID;

	private long messageTime;

	/** The body. */
	private String body;

	/**
	 * Instantiates a new message entity.
	 */
	public GroupMessageEntity() {
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	@XmlElement
	public String getBody() {
		return body;
	}

	/**
	 * Sets the body.
	 *
	 * @param body the new body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	@XmlElement
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@XmlElement
	public String getFromJID() {
		return fromJID;
	}

	public void setFromJID(String fromJID) {
		this.fromJID = fromJID;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderImage() {
		return senderImage;
	}

	public void setSenderImage(String senderImage) {
		this.senderImage = senderImage;
	}

	public String getSenderUUID() {
		return senderUUID;
	}

	public void setSenderUUID(String senderUUID) {
		this.senderUUID = senderUUID;
	}

	public long getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(long messageTime) {
		this.messageTime = messageTime;
	}

	public String getGroupName() {
		return groupName;
	}

	@XmlElement
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return "GroupMessageEntity [fromJID=" + fromJID + ", groupName=" + groupName + ", subject=" + subject
				+ ", senderName=" + senderName + ", senderImage=" + senderImage + ", senderUUID=" + senderUUID
				+ ", messageTime=" + messageTime + ", body=" + body + "]";
	}
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/
