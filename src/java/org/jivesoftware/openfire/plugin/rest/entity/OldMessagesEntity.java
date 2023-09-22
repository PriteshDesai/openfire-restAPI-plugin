package org.jivesoftware.openfire.plugin.rest.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * OldMessagesEntity user to get all the messages for the user
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
 
@XmlRootElement(name = "oldMessages")
public class OldMessagesEntity {

	private int totalMessageCount;
	private String jid;
	private String subject;
	private String naturalName;
	private List<OldMessageEntity> messages;

	public OldMessagesEntity() {
		super();
	}

	public OldMessagesEntity(int totalMessageCount, String jid, String subject, String naturalName,
			List<OldMessageEntity> messages) {
		super();
		this.totalMessageCount = totalMessageCount;
		this.jid = jid;
		this.subject = subject;
		this.naturalName = naturalName;
		this.messages = messages;
	}

	public int getTotalMessageCount() {
		return totalMessageCount;
	}

	public void setTotalMessageCount(int totalMessageCount) {
		this.totalMessageCount = totalMessageCount;
	}

	public List<OldMessageEntity> getMessages() {
		return messages;
	}

	public void setMessages(List<OldMessageEntity> messages) {
		this.messages = messages;
	}

	public String getJid() {
		return jid;
	}

	public void setJid(String jid) {
		this.jid = jid;
	}

	public String getNaturalName() {
		return naturalName;
	}

	public void setNaturalName(String naturalName) {
		this.naturalName = naturalName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return "OldMessagesEntity [totalMessageCount=" + totalMessageCount + ", jid=" + jid + ", subject=" + subject
				+ ", naturalName=" + naturalName + ", messages=" + messages + "]";
	}
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/
