package com.twinly.eyebb.model;

import com.twinly.eyebb.utils.CommonUtils;

public class Performance {
	private long childId;
	private String jsonData;
	private String lastUpdateTime;

	public long getChildId() {
		return childId;
	}

	public void setChildId(long childId) {
		this.childId = childId;
	}

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		if (CommonUtils.isNotNull(jsonData))
			this.jsonData = jsonData;
	}

	public String getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
}
