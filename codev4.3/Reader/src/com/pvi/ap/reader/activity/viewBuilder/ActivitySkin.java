package com.pvi.ap.reader.activity.viewBuilder;

public class ActivitySkin {
	
	private String activityName;
	private String skinVersion;
	private int layoutId;

	public ActivitySkin(String activityName, String skinVersion, int layoutId) {
		super();
		this.activityName = activityName;
		this.skinVersion = skinVersion;
		this.layoutId = layoutId;
	}
	
	public String getActivityName() {
		return activityName;
	}
	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	public String getSkinVersion() {
		return skinVersion;
	}
	public void setSkinVersion(String skinVersion) {
		this.skinVersion = skinVersion;
	}
	public int getLayoutId() {
		return layoutId;
	}
	public void setLayoutId(int layoutId) {
		this.layoutId = layoutId;
	}
	
	
}
