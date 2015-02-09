package com.twinly.eyebb.model;

import android.content.Context;

import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class Area {
	private Long areaId;
	private String name;
	private String nameTc;
	private String nameSc;
	private String icon;

	public Long getAreaId() {
		return areaId;
	}

	public void setAreaId(Long areaId) {
		this.areaId = areaId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameTc() {
		return nameTc;
	}

	public void setNameTc(String nameTc) {
		this.nameTc = nameTc;
	}

	public String getNameSc() {
		return nameSc;
	}

	public void setNameSc(String nameSc) {
		this.nameSc = nameSc;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDisplayName(Context context) {
		switch (SharePrefsUtils.getLanguage(context)) {
		case Constants.LOCALE_TW:
		case Constants.LOCALE_HK:
			return nameTc;
		case Constants.LOCALE_CN:
			return nameSc;
		default:
			return name; 
		}
	}

	@Override
	public String toString() {
		return "Area [areaId=" + areaId + ", name=" + name + ", nameTc="
				+ nameTc + ", nameSc=" + nameSc + ", icon=" + icon + "]";
	}

}
