package com.twinly.eyebb.model;

import java.io.Serializable;

public class ActivityInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3L;
	private long childId;
	private String title;
	private String titleTc;
	private String titleSc;
	private String url;
	private String urlTc;
	private String urlSc;
	private String date;
	private String icon;

	public long getChildId() {
		return childId;
	}

	public void setChildId(long childId) {
		this.childId = childId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitleTc() {
		return titleTc;
	}

	public void setTitleTc(String titleTc) {
		this.titleTc = titleTc;
	}

	public String getTitleSc() {
		return titleSc;
	}

	public void setTitleSc(String titleSc) {
		this.titleSc = titleSc;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlTc() {
		return urlTc;
	}

	public void setUrlTc(String urlTc) {
		this.urlTc = urlTc;
	}

	public String getUrlSc() {
		return urlSc;
	}

	public void setUrlSc(String urlSc) {
		this.urlSc = urlSc;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
