package com.pvi.ap.reader.data.common;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

public class QuitSortComparator implements Comparator<HashMap<String,Object>> {
	
	private Collator cmp = null ;
	private String sortKey1 = null ;
	private String sortKey2 = null ;
	private String sortKey3 = null ;
	private boolean descend1 = false ;
	private boolean descend2 = false ;
	private boolean descend3 = false ;
	
	public boolean isDescend1() {
		return descend1;
	}

	public void setDescend1(boolean descend1) {
		this.descend1 = descend1;
	}

	public boolean isDescend2() {
		return descend2;
	}

	public void setDescend2(boolean descend2) {
		this.descend2 = descend2;
	}

	public boolean isDescend3() {
		return descend3;
	}

	public void setDescend3(boolean descend3) {
		this.descend3 = descend3;
	}

	public QuitSortComparator(){
		this(null);
	}

	public QuitSortComparator(String sortKey1){
		this(sortKey1,null);
	}
	
	public QuitSortComparator(String sort1,String sort2){
		this(sort1,sort2,null);
	}
	
	public QuitSortComparator(String sort1,String sort2,String sort3){
		this.sortKey1 = sort1 ;
		this.sortKey2 = sort2 ;
		this.sortKey3 = sort3 ;
		cmp = Collator.getInstance(Locale.CHINA);
	}
	
	public int compare(HashMap<String,Object> o1, HashMap<String,Object> o2) {
		int ret = 0 ;
		if(sortKey1 == null){
			return ret;
		}
		
		if(o1.get(sortKey1).equals(o2.get(sortKey1))){
			if(sortKey2 == null){
				return ret;
			}
			
			if(o1.get(sortKey2).equals(o2.get(sortKey2))){
				if(sortKey3 == null){
					return ret;
				}
				
				ret = cmp.compare(o1.get(sortKey3), o2.get(sortKey3));
				
				if(!isDescend3()){
					return ret;
				}else{
					return -ret ;
				}
			}
			
			ret = cmp.compare(o1.get(sortKey2), o2.get(sortKey2));
			
			if(!isDescend2()){
				return ret;
			}else{
				return -ret ;
			}
		}
		
		ret = cmp.compare(o2.get(sortKey1), o1.get(sortKey1));
		
		if(!isDescend1()){
			return ret;
		}else{
			return -ret ;
		}
	}

}
