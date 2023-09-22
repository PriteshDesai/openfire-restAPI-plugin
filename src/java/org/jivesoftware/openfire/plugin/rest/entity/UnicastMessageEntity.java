package org.jivesoftware.openfire.plugin.rest.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 *  UnicastMessageEntity is used to create the message and get the message
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "unicastMessage")
public class UnicastMessageEntity {

	private String fromJID;

	private String toJID;

	private String subject;
	
	private long messageTime;

	/** The body. */
	private String body;

	/**
	 * Instantiates a new message entity.
	 */
	public UnicastMessageEntity() {
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

	@XmlElement
	public String getToJID() {
		return toJID;
	}

	public void setToJID(String toJID) {
		this.toJID = toJID;
	}

	public long getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(long messageTime) {
		this.messageTime = messageTime;
	}

	@Override
	public String toString() {
		return "UnicastMessageEntity [fromJID=" + fromJID + ", toJID=" + toJID + ", subject=" + subject
				+ ", messageTime=" + messageTime + ", body=" + body + "]";
	}
}
/*
 * <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 */
