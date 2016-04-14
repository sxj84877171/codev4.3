package com.pvi.ap.reader.external.meb;

import java.io.Serializable;

public class MetaFile implements Serializable
{
	private String ms_FileID;
	private int ms_FileStartPosition;
	private int mi_FileLength;
	private int mi_FileNameLength;
	private String ms_FileName;
	
	public MetaFile(String as_FileID,
				int as_FileStartPosition,
				int ai_FileLength,
				int ai_FileNameLength,
				String as_FileName)
	{
		this.ms_FileID = as_FileID;
		this.ms_FileStartPosition = as_FileStartPosition;
		this.mi_FileLength = ai_FileLength;
		this.mi_FileNameLength = ai_FileNameLength;
		this.ms_FileName = as_FileName;
	}			
	
	public void setFileID(String as_FileID)
	{
		this.ms_FileID = as_FileID;
	}
	public String getFileID()
	{
		return this.ms_FileID;
	}
	
	public void setFileStartPosition(int as_FileStartPosition)
	{
		this.ms_FileStartPosition = as_FileStartPosition;
	}
	public int getFileStartPosition()
	{
		return this.ms_FileStartPosition;
	}
	
	public void setFileLength(int ai_FileLength)
	{
		this.mi_FileLength = ai_FileLength;
	}
	public int getFileLength()
	{
		return this.mi_FileLength;
	}
	public void setFileNameLength(int ai_FileNameLength)
	{
		this.mi_FileNameLength = ai_FileNameLength;
	}
	public int getFileNameLength()
	{
		return this.mi_FileNameLength;
	}
	
	public void setFileNam(String as_FileName)
	{
		this.ms_FileName = as_FileName;
	}
	public String getFileName()
	{
		return this.ms_FileName;
	}

}
