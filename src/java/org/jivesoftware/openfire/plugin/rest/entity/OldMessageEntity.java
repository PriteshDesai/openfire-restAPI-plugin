package org.jivesoftware.openfire.plugin.rest.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * OldMessageEntity is used for the Old Messages
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "oldMessage")
public class OldMessageEntity {

	private String body;
	private String fromJID;
	private String toJID;
	private String stanza;
	private long sentDate;

	public OldMessageEntity() {
		super();
	}

	public OldMessageEntity(String body, String fromJID, String toJID, String stanza, long sentDate) {
		super();
		this.body = body;
		this.fromJID = fromJID;
		this.toJID = toJID;
		this.stanza = stanza;
		this.sentDate = sentDate;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFromJID() {
		return fromJID;
	}

	public void setFromJID(String fromJID) {
		this.fromJID = fromJID;
	}

	public String getToJID() {
		return toJID;
	}

	public void setToJID(String toJID) {
		this.toJID = toJID;
	}

	public String getStanza() {
		return stanza;
	}

	public void setStanza(String stanza) {
		this.stanza = stanza;
	}

	public long getSentDate() {
		return sentDate;
	}

	public void setSentDate(long sentDate) {
		this.sentDate = sentDate;
	}

	@Override
	public String toString() {
		return "OldMessageEntity [body=" + body + ", fromJID=" + fromJID + ", toJID=" + toJID + ", stanza=" + stanza
				+ ", sentDate=" + sentDate + "]";
	}
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/
