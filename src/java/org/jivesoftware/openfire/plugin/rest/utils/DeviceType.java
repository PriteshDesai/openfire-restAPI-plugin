package org.jivesoftware.openfire.plugin.rest.utils;

/**
 * @author pritesh_desai
 * DeviceType is used for the enum for the device type
 */

/*
 * >>>>>>>>>>>>>>>>>>>>>>>>> Custom code Started  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 */
public enum DeviceType {

	ANDROID("ANDROID"), IOS("IOS");

	public String deviceType;

	DeviceType() {
		// TODO Auto-generated constructor stub
	}

	DeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String value() {
		return deviceType;
	}

}
/*
* <<<<<<<<<<<<<<<<<<<<<<<<<<<< Custom code Ended  <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
*/
