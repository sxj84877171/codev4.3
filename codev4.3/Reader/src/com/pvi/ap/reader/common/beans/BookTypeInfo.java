package com.pvi.ap.reader.common.beans;

import android.graphics.Bitmap;

public class BookTypeInfo {

	protected String contentID;

	protected String contentName;

	protected String authorName;

	protected String smallLogo;

	protected Bitmap image;
	
	protected String authroId ;
	
	public BookTypeInfo() {
		super();
	}

	public BookTypeInfo(String contentID, String contentName,
			String authorName, String smallLogo) {
		super();
		this.contentID = contentID;
		this.contentName = contentName;
		this.authorName = authorName;
		this.smallLogo = smallLogo;
	}

	public String getContentID() {
		return contentID;
	}

	public void setContentID(String contentID) {
		this.contentID = contentID;
	}

	public String getContentName() {
		return contentName;
	}

	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getSmallLogo() {
		return smallLogo;
	}

	public void setSmallLogo(String smallLogo) {
		this.smallLogo = smallLogo;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

}