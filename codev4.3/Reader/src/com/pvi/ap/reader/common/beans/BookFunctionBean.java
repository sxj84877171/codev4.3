package com.pvi.ap.reader.common.beans;

import java.util.Map;

public class BookFunctionBean {
	
	public String readonline = "�����Ķ�";
	public boolean canreadonline = true;
	
	public String downread = "�����Ķ�";
	public boolean candownread = true ;
	
	public String reservationUpdate = "Ԥ������";
	public boolean canbookupdate = true;
	
	public String collectionread = "�ղر���";
	public boolean cancollectionread = true ;
	
	public ReadonlineListen readonlineListen = null;
	
	public DownreadListen downreadListen = null ;
	
	public CollectionreadListen collectionreadListen = null ;
	
	public ReservationUpdateListen reservationUpdateListen = null ;
	
	public Map<String,Object> map = null ;
	
	
	public interface ReadonlineListen{
		void onClick();
	}
	
	public interface DownreadListen{
		void onClick();
	}
	
	public interface CollectionreadListen{
		void onClick();
	}
	
	public interface ReservationUpdateListen{
		void onClick();
	}
}
