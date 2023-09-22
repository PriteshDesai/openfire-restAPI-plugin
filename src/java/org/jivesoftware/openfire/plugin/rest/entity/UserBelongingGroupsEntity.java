package org.jivesoftware.openfire.plugin.rest.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author pritesh_desai
 *	UserBelongingGroupsEntity: get the List of the room the User Belong
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
@XmlRootElement(name = "mucRoomDTOs")
public class UserBelongingGroupsEntity {

	List<MUCRoomEntity> mucRoomDTOs;

	public UserBelongingGroupsEntity() {

	}

	public UserBelongingGroupsEntity(List<MUCRoomEntity> mucRoomDTOs) {
		super();
		this.mucRoomDTOs = mucRoomDTOs;
	}

	@XmlElement(name = "mucRoomDTOs")
	@JsonProperty(value = "mucRoomDTOs")
	public List<MUCRoomEntity> getMucRoomDTOs() {
		return mucRoomDTOs;
	}

	public void setMucRoomDTOs(List<MUCRoomEntity> mucRoomDTOs) {
		this.mucRoomDTOs = mucRoomDTOs;
	}

	@Override
	public String toString() {
		return "UserBelongingGroupsEntity [mucRoomDTOs=" + mucRoomDTOs + "]";
	}
}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/