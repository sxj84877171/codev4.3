package com.pvi.ap.reader.common.beans;

import android.graphics.Bitmap;

public class BookSummaryBean {
	
	// ����
	public String title = "�ޱ���" ;
	// ����
	public Bitmap bookSurface = null ;
	// ����
	public String auther = "����" ;
	// ����
	public String codeNum = "0" ;
	// �½�
	public String chapterNum = "δ֪" ;
	// ��С
	public String bookSize = "0" ;
	// �Ƽ�
	public String recommend = "0" ;
	// ������
	public String readerNum = "0" ;
	// ���
	public String clickNum = "0" ;
	// �ղ�
	public String collectionNum = "0" ;
	// �ʻ�
	public String flowersNum = "0" ;
	// �ʷ�
	public String charges = "δ֪" ;
	// ���ݼ��
	public String contentAbstract = "�����鼮���..." ;
	// �Ǽ���
	public int starNum = 0 ;
	
	public ContentAbstractListen contentAbstractListen = null; 
	
	
	
	public boolean isSerial = false ;
	public boolean isFinish = false ;
	public boolean canPresent = false ;
	public String authorID = "" ;
	public String chargeMode = "" ;
	
	public String smallUrl = "" ;
	
	public interface ContentAbstractListen {
		public void onClick();
	}
	
}
