package com.twinly.eyebb.model;

public class PerformanceListItem {
	private String title;
	private int progressBarstyle;
	private int time;
	private int progress;
	private int maxProgress;
	private boolean flag;

	/**
	 * 
	 * @param title location name
	 * @param subTitle today 
	 * @param titleBackground
	 * @param progressBarstyle
	 * @param time
	 * @param progress
	 * @param maxProgress
	 */
	public PerformanceListItem(String title, int progressBarstyle, int time,
			int progress, int maxProgress) {
		super();
		this.title = title;
		this.progressBarstyle = progressBarstyle;
		this.time = time;
		this.progress = progress;
		this.maxProgress = maxProgress;
	}

	public String getTitle() {
		return title;
	}

	public int getProgressBarstyle() {
		return progressBarstyle;
	}

	public int getTime() {
		return time;
	}

	public int getProgress() {
		return progress;
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	@Override
	public String toString() {
		return "PerformanceListItem [title=" + title + ", progressBarstyle="
				+ progressBarstyle + ", time=" + time + ", progress="
				+ progress + ", maxProgress=" + maxProgress + ", flag=" + flag
				+ "]";
	}

}
