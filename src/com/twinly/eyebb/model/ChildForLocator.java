package com.twinly.eyebb.model;

public class ChildForLocator extends Child {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8549868122420844984L;
	private String locationName;
	private long lastAppearTime;

	public ChildForLocator(Child child) {
		super(child.getChildId(), child.getName(), child.getIcon(), child
				.getPhone(), child.getMacAddress());
		this.locationName = "";
		this.lastAppearTime = 0;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public long getLastAppearTime() {
		return lastAppearTime;
	}

	public void setLastAppearTime(long lastAppearTime) {
		this.lastAppearTime = lastAppearTime;
	}

}
