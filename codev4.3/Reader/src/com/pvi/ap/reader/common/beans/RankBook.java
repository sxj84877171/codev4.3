package com.pvi.ap.reader.common.beans;

/**
 * 
 * @author rd045
 * bean for the rank type list
 *
 */
public class RankBook extends BookTypeInfo {

	private String rankValue = "";
	private String current = "";

	public RankBook(String contentID, String contentName, String authroId,
			String authorName, String rankValue, String current) {
		super();
		this.contentID = contentID;
		this.contentName = contentName;
		this.authroId = authroId;
		this.authorName = authorName;
		this.rankValue = rankValue;
		this.current = current;
	}

	public String getRankValue() {
		return rankValue;
	}

	public void setRankValue(String rankValue) {
		this.rankValue = rankValue;
	}

	public String getCurrent() {
		return current;
	}

	public void setCurrent(String current) {
		this.current = current;
	}
}
