package com.pvi.ap.reader.external.meb;

import java.io.Serializable;

public class Chapters implements Serializable {
		private int startFile;//某一章文件开始位置
		private int fileLength;//某一章文件长度
		private String fileUrl;//文件章节名或卷名
		private int billingflag;//文件计费标识
		private String mebName;//meb书名
		private String flag;//判断章节或卷   0代表卷，1代表章节
		
	
		public String getFlag() {
			return flag;
		}
		public void setFlag(String flag) {
			this.flag = flag;
		}
		public String getMebName() {
			return mebName;
		}
		public void setMebName(String mebName) {
			this.mebName = mebName;
		}
		public int getStartFile() {
			return startFile;
		}
		public void setStartFile(int startFile) {
			this.startFile = startFile;
		}
		public String getFileUrl() {
			return fileUrl;
		}
		public void setFileUrl(String fileUrl) {
			this.fileUrl = fileUrl;
		}
		public int getBillingflag() {
			return billingflag;
		}
		public void setBillingflag(int billingflag) {
			this.billingflag = billingflag;
		}
		public int getFileLength() {
			return fileLength;
		}

		public void setFileLength(int fileLength) {
			this.fileLength = fileLength;
		}

		public Chapters(int startFile, int fileLength, String fileUrl,
				int billingflag, String mebName,String flag) {
			super();
			this.startFile = startFile;
			this.fileLength = fileLength;
			this.fileUrl = fileUrl;
			this.billingflag = billingflag;
			this.mebName = mebName;
			this.flag=flag;
		}
		public Chapters() {
			super();
		}
		
}
