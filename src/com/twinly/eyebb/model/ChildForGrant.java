package com.twinly.eyebb.model;

public class ChildForGrant extends Child {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5767714546616816018L;
	private boolean withAccess;
	private String totalQuota;
	private String quotaLeft;

	public boolean isWithAccess() {
		return withAccess;
	}

	public void setWithAccess(boolean withAccess) {
		this.withAccess = withAccess;
	}

	public String getQuotaLeft() {
		return quotaLeft;
	}

	public void setQuotaLeft(String quotaLeft) {
		this.quotaLeft = quotaLeft;
	}

	public String getTotalQuota() {
		return totalQuota;
	}

	public void setTotalQuota(String totalQuota) {
		this.totalQuota = totalQuota;
	}
}
