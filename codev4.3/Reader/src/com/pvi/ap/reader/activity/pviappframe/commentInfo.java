package com.pvi.ap.reader.activity.pviappframe;

public class commentInfo {
	private String commentID;
	private String fromUser;
	private String time;
	private String Comment;
	private String UpTotal;
	private String DownTotal;
	private String floorCount;

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getCommentID() {
		return commentID;
	}
	public void setCommentID(String commentID) {
		this.commentID = commentID;
	}
	public String getFromUser() {
		return fromUser;
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	public String getUpTotal() {
		return UpTotal;
	}
	public void setUpTotal(String upTotal) {
		UpTotal = upTotal;
	}
	public String getDownTotal() {
		return DownTotal;
	}
	public void setDownTotal(String downTotal) {
		DownTotal = downTotal;
	}
	public void setFloorCount(String floorCount) {
		this.floorCount = floorCount;
	}
	public String getFloorCount() {
		return floorCount;
	}
}
