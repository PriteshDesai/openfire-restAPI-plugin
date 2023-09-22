package org.jivesoftware.openfire.plugin.rest.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author pritesh_desai
 * DeleteOpinionPollDto is use to delete the Opinion of the Poll
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "deleteOpinionPoll")
public class DeleteOpinionPollDto {

	private String fromJId;
	private String pollId;
	private String pollMessageId;
	private String roomId;

	public DeleteOpinionPollDto() {
		super();
	}

	public DeleteOpinionPollDto(String fromJId, String pollId, String pollMessageId, String roomId) {
		super();
		this.fromJId = fromJId;
		this.pollId = pollId;
		this.pollMessageId = pollMessageId;
		this.roomId = roomId;
	}

	@XmlElement
	public String getFromJId() {
		return fromJId;
	}

	public void setFromJId(String fromJId) {
		this.fromJId = fromJId;
	}

	@XmlElement
	public String getPollId() {
		return pollId;
	}

	public void setPollId(String pollId) {
		this.pollId = pollId;
	}

	@XmlElement
	public String getPollMessageId() {
		return pollMessageId;
	}

	public void setPollMessageId(String pollMessageId) {
		this.pollMessageId = pollMessageId;
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	@Override
	public String toString() {
		return "DeleteOpinionPollDto [fromJId=" + fromJId + ", pollId=" + pollId + ", pollMessageId=" + pollMessageId
				+ ", roomId=" + roomId + "]";
	}
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/