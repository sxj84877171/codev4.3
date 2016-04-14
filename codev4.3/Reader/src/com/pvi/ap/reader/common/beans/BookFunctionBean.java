package com.pvi.ap.reader.common.beans;

import java.util.Map;

public class BookFunctionBean {
	
	public String readonline = "在线阅读";
	public boolean canreadonline = true;
	
	public String downread = "下载阅读";
	public boolean candownread = true ;
	
	public String reservationUpdate = "预订更新";
	public boolean canbookupdate = true;
	
	public String collectionread = "收藏本书";
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
