package com.pvi.ap.reader.data.common;

import java.text.Collator;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
/**
 * 
 * @author xiong
 * 
 */
public class SortUtil implements Comparator<HashMap>{
   
	
	private String s=null;//对应HashMap的Key值
	private boolean flag = true ;//升序、降序
   
	public SortUtil(String s){
		this.s=s;
	}
	
	public void setDescending(boolean flag){
		this.flag = flag ;
	}
	@Override
	public int compare(HashMap o1, HashMap o2) {
		// TODO Auto-generated method stub
		String arg1=null,arg2=null;
		if(s==null||s.equals("")){  //排除NullPointerException
        	return 0;
        }
		arg1=(String) o1.get(s); arg2=(String) o2.get(s);//s代表按什么排序，如书名，作者名
     Collator cmp = Collator.getInstance(Locale.CHINA);//转换中文环境
     if(flag){
    	 return cmp.compare(arg1, arg2);
     }else{
    	 return cmp.compare(arg2, arg1);
     }
	}
	
}
