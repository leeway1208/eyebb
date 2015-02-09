package com.twinly.eyebb.model;

import java.io.Serializable;

import com.twinly.eyebb.utils.CommonUtils;

public class Child implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private long childId;
	private String name;
	private String icon;
	private String phone;
	private String macAddress;
	private String relationWithUser;

	public Child() {

	}

	public Child(long childId, String name, String icon) {
		super();
		this.childId = childId;
		this.name = name;
		this.icon = icon;
	}

	public Child(long childId, String name, String icon, String phone,
			String macAddress) {
		super();
		this.childId = childId;
		this.name = name;
		this.icon = icon;
		this.phone = phone;
		this.macAddress = macAddress;
	}

	public long getChildId() {
		return childId;
	}

	public void setChildId(long childId) {
		this.childId = childId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public void setMacAddress(String macAddress) {
		if (CommonUtils.isNotNull(macAddress)) {
			this.macAddress = macAddress;
		} else {
			this.macAddress = "";
		}
	}

	public String getRelationWithUser() {
		return relationWithUser;
	}

	public void setRelationWithUser(String relationWithUser) {
		this.relationWithUser = relationWithUser;
	}

	@Override
	public String toString() {
		return "Child [childId=" + childId + ", name=" + name + ", icon="
				+ icon + ", phone=" + phone + ", macAddress=" + macAddress
				+ ", relationWithUser=" + relationWithUser + "]";
	}

}
