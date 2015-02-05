package com.twinly.eyebb.model;

import android.content.Context;

import com.twinly.eyebb.constant.Constants;
import com.twinly.eyebb.utils.SharePrefsUtils;

public class Location {
	private long id;
	private String name;
	private String nameSc;
	private String nameTc;
	private String type;
	private String icon;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameSc() {
		return nameSc;
	}

	public void setNameSc(String nameSc) {
		this.nameSc = nameSc;
	}

	public String getNameTc() {
		return nameTc;
	}

	public void setNameTc(String nameTc) {
		this.nameTc = nameTc;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
