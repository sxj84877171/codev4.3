package com.pvi.ap.reader.external.pdf;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import com.pvi.ap.reader.activity.PDFReadActivity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.MessageQueue;
import android.util.Log;
//forward all call to PDF render engine to a separated thread to speed up performance , improve user experiences
//serialize calls to PDF lib to avoid multi-threading concurrent accessing led corruption of internal data structure 
public class pdfReaderWrapper {
	private final static String TAG="pdfReaderWrapper";
	private final static int MESSAGE_CODE=6;
	private pdfReader m_reader;
	private Handler m_activity_handler = null;
	boolean m_synchronized_call = false;//true;
	boolean m_synchronized_call_2 = false;//true;
	int m_count_getBitmapStuff_call = 0;//number of pending calls for getBitmapStuff
	int m_consecutive_forward_preload_num = 0;
	int m_consecutive_backward_preload_num = 0;
	boolean m_error_flag = false;
	boolean m_opened_flag = false;
	public synchronized boolean get_error_flag() {
		return m_error_flag;
	}
	public synchronized void set_error_flag() {
		m_error_flag = true;
		notify_error();
	}
	public synchronized void clear_error_flag() {
		m_error_flag = false;
		Log.i(TAG,TAG+"::clear_error_flag()");
	}
	public synchronized void set_is_opened(boolean val) {
		m_opened_flag = val;
	}
	public synchronized boolean get_is_opened() {
		return m_opened_flag;
	}
	public synchronized void increase_getBitmapStuff_call() {
		m_count_getBitmapStuff_call++;
	}
	public synchronized void decrease_getBitmapStuff_call() {
		m_count_getBitmapStuff_call--;
	}
	public synchronized int get_getBitmapStuff_call_num() {
		return m_count_getBitmapStuff_call;
	}
	public synchronized void increase_consecutive_forward_preload_num() {
		m_consecutive_forward_preload_num++;
	}
	public synchronized void decrease_consecutive_forward_preload_num() {
		m_consecutive_forward_preload_num--;
	}
	public synchronized int get_consecutive_forward_preload_num() {
		return m_consecutive_forward_preload_num;
	}
	public synchronized void reset_consecutive_forward_preload_num() {
		Log.i(TAG,"reset_consecutive_forward_preload_num");
		m_consecutive_forward_preload_num = 0;
	}
	public synchronized void increase_consecutive_backward_preload_num() {
		m_consecutive_backward_preload_num ++;
	}
	public synchronized void decrease_consecutive_backward_preload_num() {
		m_consecutive_backward_preload_num--;
	}
	public synchronized int get_consecutive_backward_preload_num() {
		return m_consecutive_backward_preload_num;
	}
	public synchronized void reset_consecutive_backward_preload_num() {
		Log.i(TAG,"reset_consecutive_backward_preload_num");
		m_consecutive_backward_preload_num = 0;
	}
	void set_synchronized_call(boolean val) {
		m_synchronized_call = val;
	}
	public pdfReaderWrapper(Handler activity_handler, Context p) {
		m_reader = new pdfReader(p);
		m_activity_handler = activity_handler;
		
	    m_pipeline.start();
	    while(true) {//wait handler in pipeline thread created 
		    try {
				m_pipeline.m_handler_created.acquire();
				m_pipeline.m_handler_created.release();//not need the semaphore
				break;
			} catch (InterruptedException e) {
			}
	    }
	}
	private static Semaphore m_semaphore = new Semaphore(1);
	public static boolean acquire() {
		try {
			m_semaphore.acquire();
			return true;
		} catch (InterruptedException e) {
			Log.i(TAG,"an exception occur when acquire semaphore");
			e.printStackTrace();
			
			return false;
		}
	}
	public static void acquire_noninterruptable() {
		boolean acquired = false;
    	while(!acquired) {
    		acquired = acquire();
    	}
	}
	public static boolean acquire_noninterruptable_timeout(long timeout/*milliseconds*/) {
		//try to acquire a semaphore within specified timeout value. return true if semaphore acquired, false if not
    	while(true) {
    		try {
    			boolean ret = m_semaphore.tryAcquire(timeout,TimeUnit.MILLISECONDS);
    			return ret;
    		} catch (InterruptedException e) {
    			Log.i(TAG,"an exception occur when acquire semaphore");
    			e.printStackTrace();
    		}
    	}
	}
	public static void release() {
		m_semaphore.release();
	}
	
	public void check_busy() {
		//dummy,placeholder
	}
    
	public boolean init_internal() {
    	check_busy();
    	return m_reader.init();
    }
    public boolean setViewPortSize_internal(int width, int height) {
    	check_busy();
    	return m_reader.setViewPortSize(width, height);
    }
    public boolean setCache_internal(boolean enable) {
    	check_busy();
    	return m_reader.setCache(enable);
    }
    public boolean open_internal(String filepath) {
    	check_busy();
    	return m_reader.open(filepath);
    }
    public boolean close_internal() {
    	check_busy();
    	boolean ret = m_reader.close();
    	clear_error_flag();
    	return ret;
    }
    public byte[] getBitmapStuff_internal(int cacheNum) {
    	check_busy();
    	return m_reader.getBitmapStuff(cacheNum);
    }
    public int getCurrentPage_internal(int cacheNum) {
    	check_busy();
    	return m_reader.getCurrentPage(cacheNum);
    }
    public int getPageCount_internal() {
    	check_busy();
    	return m_reader.getPageCount();
    }
    public int getNaturalSize_internal( int pageNum ) {
    	check_busy();
    	return m_reader.getNaturalSize(pageNum);
    }
    public boolean gotoPage_internal( int pageNum, boolean after ) {
    	check_busy();
    	return m_reader.gotoPage(pageNum, after);
    }
    public boolean gotoPrevScreenPage_internal() {
    	check_busy();
    	return m_reader.gotoPrevScreenPage();
    }
    public boolean gotoNextScreenPage_internal() {
    	check_busy();
    	return m_reader.gotoNextScreenPage();
    }
    public boolean setFontSize_internal(double scale) {
    	check_busy();
    	return m_reader.setFontSize(scale);
    }
    
    public ChapterInfo[] getChapterList_internal(int times) {
    	check_busy();
    	return m_reader.getChapterList(times);
    }
    public int getChapterListLen_internal() {
    	check_busy();
    	return m_reader.getChapterListLen();
    }
    public boolean hasTableOfContent_internal() {
    	check_busy();
    	return m_reader.hasTableOfContent();
    }
    public boolean autoCutMargin_internal(boolean cut) {
    	check_busy();
    	return m_reader.autoCutMargin(cut);
    }
    public boolean setFitMode_internal(int fit) {
    	check_busy();
    	return m_reader.setFitMode(fit);
    }
    public boolean setViewPort_internal(int minx, int miny, int maxx,int maxy) {
    	check_busy();
    	return m_reader.setViewPort(minx, miny, maxx, maxy);
    }
    public boolean setPagingMode_internal(int fit) {
    	check_busy();
    	return m_reader.setPagingMode(fit);
    }
    public boolean setScrollOffset_internal(int offset) {
    	check_busy();
    	return m_reader.setScrollOffset(offset);
    }
    public int search_internal(String text, boolean isBackward, int start, int range) {
    	check_busy();
    	return m_reader.search(text, isBackward, start, range); 
    }
    
    //functions below called from within loader thread,no protection
    public boolean gotoPage_loader( int pageNum, boolean after ) {
    	return m_reader.gotoPage(pageNum, after);
    }
    public boolean gotoPrevScreenPage_loader() {
    	return m_reader.gotoPrevScreenPage();
    }
    public boolean gotoNextScreenPage_loader() {
    	return m_reader.gotoNextScreenPage();
    }
    
    //functions below called directly
    public byte[] getBitmapStuff_direct(int cacheNum) {
    	byte[] ret = null;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        //Log.i(TAG,"getBitmapStuff_direct semaphore acquired");
        try {
        	ret = m_reader.getBitmapStuff(cacheNum);
        }catch(Throwable e) {
        	//safety purpose, to avoid semaphore leaking in case of exception thrown
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    	
    }
    public int getCurrentPage_direct(int cacheNum) {
    	int ret = 0;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.getCurrentPage(cacheNum);
        }catch(Throwable e) {
        	//safety purpose, to avoid semaphore leaking in case of exception thrown
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    public int getPageCount_direct() {
    	int ret = 0;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.getPageCount();
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    public int getNaturalSize_direct( int pageNum ) {
    	int ret = 0;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.getNaturalSize(pageNum);
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    public boolean setFontSize_direct(double scale) {
    	boolean ret = false;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.setFontSize(scale);
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    public ChapterInfo[] getChapterList_direct(int times) {
    	ChapterInfo[] ret = null;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.getChapterList(times);
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    public int getChapterListLen_direct() {
    	int ret = 0;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.getChapterListLen();
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    public boolean hasTableOfContent_direct() {
    	boolean ret = false;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.hasTableOfContent();
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    public int search_direct(String text, boolean isBackward, int start, int range) {
    	int ret = 0;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.search(text, isBackward, start, range);
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;
    }

    public boolean docReachHeadOrTail(boolean head) {
    	boolean ret = false;
    	
        pdfReaderWrapper.acquire_noninterruptable();
        try {
        	ret = m_reader.docReachHeadOrTail(head);
        }catch(Throwable e) {
        }
        pdfReaderWrapper.release();
        
    	return ret;    	
    }
    
    //forwards call to internal thread or directly call PDF lib dependently
	public boolean init() {
		//\\if(!m_synchronized_call_2) {
		if(false) {//init should be synchronous always
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", init);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
		}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.init( );
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
		}
    }
    public boolean setViewPortSize(int width, int height) {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", setViewPortSize);
		bundle.putInt("width",width);
		bundle.putInt("height",height);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;		
		m_handler.sendMessage(msg);

		return true;//default;
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.setViewPortSize(width, height);
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean setCache(boolean enable) {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", setCache);
		bundle.putBoolean("enable",enable);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.setCache(enable);
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean open(String filepath) {
    	//\\if(!m_synchronized_call_2) {
    	if(false) {//open should be a synchronous operation always
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", open);
		bundle.putString("filepath",filepath);
		msg.setData(bundle);
		
    	reset_consecutive_forward_preload_num();
    	reset_consecutive_backward_preload_num();
		
    	msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
        	reset_consecutive_forward_preload_num();
        	reset_consecutive_backward_preload_num();

        	pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.open( filepath );
            }catch(Throwable e) {
            }
            if(ret) {
            	set_is_opened(true);
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean close() {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", close);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		Log.i(TAG,TAG+"::close()");
		
		return true;//default
    }
    public byte[] getBitmapStuff_sync(int cacheNum, long timeout/*millisecond*/) {
    	//get bitmap synchronously, directly from cache ,not dispatched to another thread
    	
		/*can not be blocked here. return within certain duration back to main GUI thread to process user input otherwise application
		 * will be terminated by android's internal audit mechanism : 5 seconds
		 * */
    	byte[] ret = null;
		boolean semaphore_acquired = acquire_noninterruptable_timeout(timeout);
		if(!semaphore_acquired) {
			Log.i(TAG,"getBitmapStuff_sync semaphore not acquired");
			return ret;//failed to get bitmap
		}
		
        Log.i(TAG,"getBitmapStuff_sync semaphore acquired");
        
        try {
			boolean is_opened = get_is_opened();
			boolean in_error_state = get_error_flag();
			if( !in_error_state &&  is_opened ) {
				ret = m_reader.getBitmapStuff(cacheNum);
			}else {
				ret = null;
			}
        }catch(Throwable e) {
        	//safety purpose, to avoid semaphore leaking in case of exception thrown
        }
        pdfReaderWrapper.release();
        
        return ret;
    }
    public byte[] getBitmapStuff(int cacheNum) {
    	/*
    	 note: if getBitmapStuff() is modeled as synchronized, functions: setFontSize() , gotoPage(), getCurrentPage() should be synchronized too
    	 because they depend on each other.
    	 */
    	if(!m_synchronized_call) {
    	Log.i(TAG,TAG+"::getBitmapStuff asynchronously");
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", getBitmapStuff);
		bundle.putInt("cacheNum", cacheNum);
		msg.setData(bundle);
		
		increase_getBitmapStuff_call();
		
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return null;//The bitmap is posted to main thread later
    	}else {
    		if(false) {
    		byte[] ret = null;
    		//Log.i(TAG,"call getBitmapStuff");
    		ret = getBitmapStuff_direct(cacheNum);
    		//Log.i(TAG,"finished call getBitmapStuff");
    		
    		return ret;
    		}else {
        		byte[] ret = null;
        		
        		//Log.i(TAG,"call getBitmapStuff");
        		
        		getBitmapStuff_sync(cacheNum,3000/*milliseconds*/);
    	        
        		//Log.i(TAG,"finished call getBitmapStuff");
        		
        		return ret;
    		}
    	}
    }
    public int getCurrentPage(int cacheNum) {
    	if(false) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", getCurrentPage);
		bundle.putInt("cacheNum", cacheNum);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return 0;//default
    	}else {//modeled as direct call,need result immediately
    		return getCurrentPage_direct(cacheNum);
    	}
    }
    public int getPageCount() {
    	if(false) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", getPageCount);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return 0;//default
    	}else {
    		return getPageCount_direct();//need result immediately
    	}
    }
    public int getNaturalSize( int pageNum ) {
    	if(false) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", getNaturalSize);
		bundle.putInt("pageNum", pageNum);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return 0;//default
    	}else {
    		return getNaturalSize_direct(pageNum);//direct call
    	}
    }
    public boolean gotoPage( int pageNum, boolean after ) {
    	if(!m_synchronized_call) {
    	Log.i(TAG, TAG+"::gotoPage asynchronously");
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", gotoPage);
		bundle.putInt("pageNum", pageNum);
		bundle.putBoolean("after", after);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
    	reset_consecutive_forward_preload_num();//reset consecutive movement record
    	reset_consecutive_backward_preload_num();
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.gotoPage( pageNum, after );
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean gotoPrevScreenPage() {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", gotoPrevScreenPage);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
    	reset_consecutive_forward_preload_num();//called only when non-cache mode, reset it
    	reset_consecutive_backward_preload_num();
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.gotoPrevScreenPage( );
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean gotoNextScreenPage() {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", gotoNextScreenPage);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
    	reset_consecutive_forward_preload_num();//called only when non-cache mode, reset it
    	reset_consecutive_backward_preload_num();
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.gotoNextScreenPage( );
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean setFontSize(double scale) {
    	if(!m_synchronized_call) {
    	Log.i(TAG,TAG+"::send setFontSize call");
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", setFontSize);
		bundle.putDouble("scale", scale);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    	}else {
    		Log.i(TAG,TAG+"::setFontSize synchronously");
    		return setFontSize_direct(scale);
    	}
    }
    
    public ChapterInfo[] getChapterList(int times) {
    	if(false) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", getChapterList);
		bundle.putInt("times", times);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return null;//default
    	}else {
    		return getChapterList_direct(times);//direct call
    	}
    }
    public int getChapterListLen() {
    	if(false) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", getChapterListLen);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return 0;//default
    	}else {
    		return getChapterListLen_direct();//direct call
    	}
    }
    public boolean hasTableOfContent() {
    	if(false) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", hasTableOfContent);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    	}else {
    		return hasTableOfContent_direct();//directly
    	}
    }
    public boolean autoCutMargin(boolean cut) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", autoCutMargin);
		bundle.putBoolean("cut", cut);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    }
    public boolean setFitMode(int fit) {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", setFitMode);
		bundle.putInt("fit", fit);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.setFitMode( fit);
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean setViewPort(int minx, int miny, int maxx,int maxy) {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", setViewPort);
		bundle.putInt("minx", minx);
		bundle.putInt("miny", miny);
		bundle.putInt("maxx", maxx);
		bundle.putInt("maxy", maxy);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.setViewPort(minx, miny, maxx,maxy);
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean setPagingMode(int fit) {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", setPagingMode);
		bundle.putInt("fit", fit);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
    	if(fit == 2) {
        	reset_consecutive_forward_preload_num();//set to non-cache mode, reset it
        	reset_consecutive_backward_preload_num();
    	}
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.setPagingMode(fit);
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public boolean setScrollOffset(int offset) {
    	if(!m_synchronized_call_2) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", setScrollOffset);
		bundle.putInt("offset", offset);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return true;//default
    	}else {
        	boolean ret = false;
        	
            pdfReaderWrapper.acquire_noninterruptable();
            try {
            	ret = m_reader.setScrollOffset(offset);
            }catch(Throwable e) {
            }
            pdfReaderWrapper.release();
            
        	return ret;    	
    	}
    }
    public int search(String text, boolean isBackward, int start, int range) {
    	if(false) {
		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", search);
		bundle.putString("text", text);
		bundle.putBoolean("isBackward", isBackward);
		bundle.putInt("start", start);
		bundle.putInt("range", range);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		return 0;//default
    	}else {
    		return search_direct(text, isBackward, start, range);
    	}
    }

    public void preload(int direction, int page_number) {
    	Log.i(TAG,"send preload call");

		Message msg = new Message();
		Bundle bundle = new Bundle();
		bundle.putInt("fun_id", preload);
		bundle.putInt("direction", direction);
		bundle.putInt("page_number", page_number);
		msg.setData(bundle);
		msg.what = MESSAGE_CODE;
		m_handler.sendMessage(msg);
		
		Log.i(TAG,"finished sending preload");
		
    	if (direction == 0) {
        	reset_consecutive_forward_preload_num();
        	reset_consecutive_backward_preload_num();
    	}else if(direction == 1) {
            increase_consecutive_forward_preload_num();
    	}else if (direction == -1) {
            increase_consecutive_backward_preload_num();
    	}
		
		return;
    }
 
    void process_call(Bundle bundle) {
    	int fun_id = bundle.getInt("fun_id");
    	switch(fun_id) {
        case init:// = 1;//boolean ();
        	init_internal();
        	break;
        case setViewPortSize:// = 2;//boolean (int width, int height);
        	int width,height;
        	width = bundle.getInt("width");
        	height = bundle.getInt("height");
        	setViewPortSize_internal(width,height);
    	break;
        case setCache:// = 3;//boolean (boolean enable);
        	boolean enable;
        	enable = bundle.getBoolean("enable");
        	setCache_internal(enable);
    	break;
        case open:// = 4;//boolean (String filepath);
        	String filepath;
        	filepath = bundle.getString("filepath");
        	open_internal(filepath);
    	break;
        case close:// = 5;//boolean ();
        	set_is_opened(false);
        	close_internal();
    	break;
        case getBitmapStuff:// = 6;//byte[] (int cacheNum);
        	Log.i(TAG,TAG+"::getBitmapStuff internally processed");
        	int num_pending_call_getBitmapStuff = get_getBitmapStuff_call_num();
        	if(num_pending_call_getBitmapStuff > 1) {
        		decrease_getBitmapStuff_call();
        		Log.i(TAG,"getBitmapSutff is skipped because it is not last one");
        		break;//only last page Bitmap will be useful on screen refreshing
        	}
        	int cacheNum;
        	cacheNum = bundle.getInt("cacheNum");
        	byte[] bytes = getBitmapStuff_internal(cacheNum);
        	Log.i(TAG,TAG+"::getBitmapStuff : getBitmapStuff_internal finished");
    		Message msg = new Message();
    		msg.what = 1;
    		Bundle new_bundle = new Bundle();
    		new_bundle.putByteArray("bitmap", bytes);
    		int pageNum = getCurrentPage_internal(cacheNum);
    		new_bundle.putInt("pageNum",pageNum);
    		msg.setData(new_bundle);
    		m_activity_handler.sendMessage(msg);
    		decrease_getBitmapStuff_call();
    		Log.i(TAG,TAG+"::getBitmapStuff internally processing finished");
    	break;
        case getCurrentPage:// = 7;//int (int cacheNum);
        	//int cacheNum;
        	cacheNum = bundle.getInt("cacheNum");
        	getCurrentPage_internal(cacheNum);
    	break;
        case getPageCount:// = 8;//int ();
        	getPageCount_internal();
    	break;
        case getNaturalSize:// = 9;//int ( int pageNum );
        	pageNum = bundle.getInt("pageNum");
        	getNaturalSize_internal(pageNum);
    	break;
        case gotoPage:// = 10;//boolean ( int pageNum, boolean after );
        	Log.i(TAG,TAG+"::gotoPage internally processed");
        	/*int*/ pageNum = bundle.getInt("pageNum");
        	boolean after = bundle.getBoolean("after");
        	boolean ret = gotoPage_internal(pageNum,after);
        	if(!ret) {
        		set_error_flag();
        		Log.i(TAG,TAG+"::gotoPage internally failed");
        	}
        	Log.i(TAG,TAG+"::gotoPage internally processing finished");
    	break;
        case gotoPrevScreenPage:// = 11;//boolean ();
        	gotoPrevScreenPage_internal();
    	break;
        case gotoNextScreenPage:// = 12;//boolean ();
        	gotoNextScreenPage_internal();
    	break;
        case setFontSize:// = 13;//boolean (double scale);
        	double scale = bundle.getDouble("scale");
        	setFontSize_internal(scale);
    	break;
        
        case getChapterList:// = 14;//ChapterInfo[] (int times);
        	int times = bundle.getInt("times");
        	getChapterList_internal(times);
    	break;
        case getChapterListLen:// = 15;//int ();
        	getChapterListLen_internal();
    	break;
        case hasTableOfContent:// = 16;//boolean ();
        	hasTableOfContent_internal();
    	break;
        case autoCutMargin:// = 17;//boolean (boolean cut);
        	boolean cut = bundle.getBoolean("cut");
        	Log.i(TAG,"enter autoCutMargin_internal, param:" + cut);
        	autoCutMargin_internal(cut);
        	Log.i(TAG,"finished autoCutMargin_internal");
    	break;
        case setFitMode:// = 18;//boolean (int fit);
        	int fit = bundle.getInt("fit");
        	setFitMode_internal(fit);
    	break;
        case setViewPort:// = 19;//boolean (int minx, int miny, int maxx,int maxy);
        	int minx = bundle.getInt("minx");
        	int miny = bundle.getInt("miny");
        	int maxx = bundle.getInt("maxx");
        	int maxy = bundle.getInt("maxy");
        	setViewPort_internal(minx,miny,maxx,maxy);
    	break;
        case setPagingMode:// = 20;//boolean (int fit);
        	/*int*/fit = bundle.getInt("fit");
        	setPagingMode_internal(fit);
    	break;
        case setScrollOffset:// = 21;//boolean (int offset);
        	int offset = bundle.getInt("offset");
        	setScrollOffset_internal(offset);
    	break;
        case search:// = 22;//int (String text, boolean isBackward, int start, int range);
        	String text = bundle.getString("text");
        	boolean isBackward = bundle.getBoolean("isBackward");
        	int start = bundle.getInt("start");
        	int range = bundle.getInt("range");
        	search_internal(text,isBackward,start,range);
    	break;
    	
        case preload:// = 100;//void preload(int direction, int page_number);
        	Log.i(TAG,"enter preloading");
        	try {
        	int direction = bundle.getInt("direction");
        	int page_number = bundle.getInt("page_number");
        	//Log.i(TAG,"internal process call preload");
        	Log.i(TAG,"preload direction: " + direction + " ,  page number: " + page_number);
        	if (direction == 0) {
                this.gotoPage_loader(page_number, true);
            	reset_consecutive_forward_preload_num();
            	reset_consecutive_backward_preload_num();
        	}else if(direction == 1) {
        		int old_num = get_consecutive_forward_preload_num();
        		if(false/*old_num > 3*/) {
        			//if has more than 3 consecutive forward preload operations pending, not need to do these operations except last three.
        			//because the result of old loading will be overwritten by last 3 times loads
        			Log.i(TAG, "skip old forward preloading.  consecutive_forward_preload_num is" + old_num);
        		}else {
        			this.gotoNextScreenPage_loader();
        		}
                decrease_consecutive_forward_preload_num();
        	}else if (direction == -1) {
        	//	int old_num = get_consecutive_backward_preload_num();
        	//	if(false/*old_num > 3*/) {
        //			Log.i(TAG, "skip old backward preloading.  consecutive_backward_preload_num is" + old_num);
        	//	}else {
        			this.gotoPrevScreenPage_loader();
        	//	}
                decrease_consecutive_backward_preload_num();
        	}
        	Log.i(TAG,"preload call finished");
        	}catch(Throwable e) {
        		e.printStackTrace();
        		Log.i(TAG,"preload call generates an exception");
        	}
       	break;
    
    	default:
   		break;
    	}
    }
    void notify_error() {
		Message msg = new Message();
		msg.what = 2;
		Bundle new_bundle = new Bundle();
		new_bundle.putBoolean("error_flag",true);
		msg.setData(new_bundle);
		m_activity_handler.sendMessage(msg);
    }
    
    private static final int init = 1;//();
    private static final int setViewPortSize = 2;//(int width, int height);
    private static final int setCache = 3;//(boolean enable);
    private static final int open = 4;//(String filepath);
    private static final int close = 5;//();
    private static final int getBitmapStuff = 6;//(int cacheNum);
    private static final int getCurrentPage = 7;//(int cacheNum);
    private static final int getPageCount = 8;//();
    private static final int getNaturalSize = 9;//( int pageNum );
    private static final int gotoPage = 10;//( int pageNum, boolean after );
    private static final int gotoPrevScreenPage = 11;//();
    private static final int gotoNextScreenPage = 12;//();
    private static final int setFontSize = 13;//(double scale);
    
    private static final int getChapterList = 14;//(int times);
    private static final int getChapterListLen = 15;//();  
    private static final int hasTableOfContent = 16;//();
    private static final int autoCutMargin = 17;//(boolean cut);
    private static final int setFitMode = 18;//(int fit);
    private static final int setViewPort = 19;//(int minx, int miny, int maxx,int maxy);
    private static final int setPagingMode = 20;//(int fit);
    private static final int setScrollOffset = 21;//(int offset);
    private static final int search = 22;//(String text, boolean isBackward, int start, int range);
    
    private static final int preload = 100;//void preload(int direction, int page_number);
	
	public Handler m_handler;
    class renderpipeThread extends Thread {
    	public renderpipeThread(String threadName) {
    		super(threadName);
    	}
    	public void run() {
    		Looper.prepare();
    		m_handler = new Handler() {
    			public void handleMessage(Message msg) {
    				// process incoming messages here
    				Bundle bundle = msg.getData();
    				int fun_id = bundle.getInt("fun_id");
    				boolean is_opened = get_is_opened();
    				
    				boolean in_error_state = get_error_flag();
    				if(    (!in_error_state || (fun_id == close))
    				   &&  (is_opened || ( (fun_id == open) && !is_opened)) 
    				  )
    				{
    					
	    				acquire_noninterruptable();
	    		        try {
	        	        	process_call(bundle);
	    		        }catch(Throwable e) {
	    		        	//safety purpose, to avoid semaphore leaking in case of exception thrown
	    		        }
	   	        	
	    	            release();
	    	            
    				}else {
        				if(fun_id == getBitmapStuff) {
        					decrease_getBitmapStuff_call();
        				}else if(fun_id == preload) {
        					int direction = bundle.getInt("direction");
        		        	if (direction == 0) {
        		            	reset_consecutive_forward_preload_num();
        		            	reset_consecutive_backward_preload_num();
        		        	}else if(direction == 1) {
        		        		decrease_consecutive_forward_preload_num();
        		        	}else if(direction == -1) {
        		        		decrease_consecutive_backward_preload_num();
        		        	}
        				}
    				}
    			}
    		};
    		m_handler_created.release();
    		Looper.loop();
    	}
    	public Semaphore m_handler_created = new Semaphore(0);
    }
    private renderpipeThread m_pipeline = new renderpipeThread("PDF-render");
    public boolean has_pending_msg() {
    	return m_handler.hasMessages(MESSAGE_CODE);
    }
}
