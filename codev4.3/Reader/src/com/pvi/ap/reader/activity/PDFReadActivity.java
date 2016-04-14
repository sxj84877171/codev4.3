package com.pvi.ap.reader.activity;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat; 
import java.util.ArrayList;
import java.util.HashMap; 
import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.external.pdf.ChapterInfo;
import com.pvi.ap.reader.external.pdf.pdfReaderWrapper;
import com.pvi.ap.reader.external.txt.ReadSetView;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.OnBackListener;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.CommentsInfo;

import android.widget.TableLayout.LayoutParams;
@SuppressWarnings("deprecation")
public class PDFReadActivity extends PviActivity implements Pageable
{
	private int type=0;  //设置自动翻页时间
	
	PviDataList m_listView;
	ArrayList<PviUiItem[]> m_list; 
	PviBottomBar  m_pbb;

	@Override
	public OnUiItemClickListener getMenuclick() {
		// TODO Auto-generated method stub
		return this.mainMenuclick;
	}
    /** Called when the activity is first created. */
    @Override 
   public void onCreate(Bundle savedInstanceState) 
    {
//    	FileWriter fw;
//		try {
//			fw = new FileWriter("/data/data/logfile.txt",true);
//	    	Logger.setFw(fw);
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//    	Logger.setWriteToFile(1);
    	Logger.i(TAG,"++++++++++++++++++++++++++++++++onCreate");
    	
        m_watch_at = System.currentTimeMillis();
        m_watch_for   = "Just after activity onCreate";
        m_handler_autoplay = new Handler();
        m_handler_refresh = new Handler();
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);   
            super.onCreate(savedInstanceState);

            getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,   
                    WindowManager.LayoutParams. FLAG_FULLSCREEN);    
 
            GlobalVar appState = (GlobalVar)getApplicationContext();
//            if(appState.getSkinID() == 2){
//            	setContentView(R.layout.pdfview2);
//            }else{
            	setContentView(R.layout.pdfview);
//            }
    		Configuration newConfig = this.getResources().getConfiguration();
    		skin = newConfig.orientation ;


			m_listView= (PviDataList)findViewById(R.id.list);
			m_listView.setOnKeyListener(onKeyListener);
			m_list = new ArrayList<PviUiItem[]>();
			m_listView.setData(m_list);
	        m_pbb = appState.pbb;
	        m_pbb.setOnBackListener(m_onBackListener);
//			m_pbb.setPageable(this);
//			m_pbb.setItemVisible("prevpage", true);
//			m_pbb.setItemVisible("pagerinfo", true);
//			m_pbb.setItemVisible("nextpage", true);
	        this.showPager = true;
		    PviDataList.OnRowClickListener row_listener = new PviDataList.OnRowClickListener() {
		        public void OnRowClick(View v,int rowIndex) {
		        	setEvent(rowIndex);
		        }
		    };
		    m_listView.setOnRowClick(row_listener);

			createImageView();
            createBottomBar();
            createNoteWindow();

            createScalingmenu();
            bindScalingMenuEvent();
            
           // createFontsizemenu();
           // bindFontsizeMenuEvent();
            
//            createPageAdjustmenu();
//            bindPageAdjustMenuEvent();
            
            createTouchFuncmenu();
            bindTouchFuncMenuEvent();

            createpagingmodemenu();   
           // bindpagingmodeMenuEvent();
            
            createTOCList();
            bindTOCItemsListener();
            
            registerBCReceiver();
            
           //\\ super.bindEvent();
   			popmenuView = appState.getMenu(this);
   			feedMenuItemIndexHashmap();
   			//pdf_setMenuListener(popmenuView);	
            
            reader = new pdfReaderWrapper(m_page_update_handler, this);
            if(m_init_ok) {
	            Logger.i(TAG,"onCreate , adobe lib is initialized already .skip initializing adobe lib");
            }else {
	            Logger.i(TAG,"call reader.init()");
	            if (reader.init()) {
	            	Logger.i(TAG, "adobe lib initialized successfully");
	                m_init_ok = true;
	            }else {
	            	Logger.i(TAG, "adobe lib initialized failed at OnCreate");
	            }// almost never get here, deal it in onResume
            }

            
            activity_state = activity_state_t.state_created;
            m_resumed_times = 0;
            m_imgView.setFocusable(true);
            m_imgView.setFocusableInTouchMode(true);
            m_imgView.requestFocus();
            
            this.setOnPmShow(new OnPmShowListener(){
                @Override
                public void OnPmShow(PviPopupWindow popmenu_v) {
                    if(!m_toc_on) {
                    	show_not_toc_menu(true,popmenu_v);
                    }else {
                        show_not_toc_menu(false,popmenu_v);
                    }
                	if(!m_hasTOC) {
                	    PviMenuItem tv = (PviMenuItem)getMenuItem("toc");
	                    if( tv != null ) {
	                        tv.enable=false;
	                    }
                	}else {
                	    PviMenuItem tv = (PviMenuItem)getMenuItem("toc");
	                    if( tv != null ) {
	                        tv.enable=true;
	                    }
                	}
                    if (m_is_cutted)
                    {
                        PviMenuItem tv = (PviMenuItem)getMenuItem("autocut");
                        if (tv !=null) {
                        	String txt = tv.text;
                        	if(!txt.equals(getString(R.string.str_pdf_cancel_cut))) {
                        		tv.text=getString(R.string.str_pdf_cancel_cut);
                        	}
                        }

                    }
                    else
                    {
                        PviMenuItem tv = (PviMenuItem)getMenuItem("autocut");
                        if (tv !=null) {
                        	String txt = tv.text;
                        	if(!txt.equals(getString(R.string.str_pdf_auto_cut))) {
                        		tv.text=getString(R.string.str_pdf_auto_cut);
                        	}
                        }
                    }
                    if((m_pagingmode==0) || (m_pagingmode==1)) {//single page or reflow
                        PviMenuItem tv = (PviMenuItem)getMenuItem("pagingmodeview0");
                    	if(tv != null) {
                    		tv.isFocuse=true;
                    	}
                        tv = (PviMenuItem)getMenuItem("autocut");
                        if (tv !=null) {
                        	if(Math.abs(m_fontscale-1.0f)>0.2) {
                        		tv.enable = false;//disable auto cutting if scale factor is not 1.0
                        	}else {
                        		tv.enable = true;
                        	}
                        }
                    }else if(m_pagingmode==2) {//scrolling
                        PviMenuItem tv = (PviMenuItem)getMenuItem("pagingmodeview1");
                    	if(tv != null) {
                    		tv.isFocuse=true;
                    	}
                        tv = (PviMenuItem)getMenuItem("autocut");
                        if (tv !=null) {
                        	tv.enable = false;
                        }
                    }
                    boolean marked = currentPageIsMarked();
                    PviMenuItem tvabm = (PviMenuItem)getMenuItem("addbm");
                    PviMenuItem tvdbm = (PviMenuItem)getMenuItem("delbm");
                    if (marked)    // enable deleting bookmark, disable adding bookmark
                    {
                        if (tvabm!=null&&tvdbm!=null)
                        {
                            tvabm.enable=false;
                            tvdbm.enable=true;
                        }
                    }
                    else    // enable adding bookmark, disable deleting bookmark
                    {
                        if (tvabm!=null&&tvdbm!=null)
                        {
                        	tvdbm.enable=false;
                            tvabm.enable=true;
                        }
                    }
                }});
            
        }catch(Exception e) {
        	Logger.i(TAG,"OnCreate excepton occurs");
            Logger.e(TAG, e.toString());
            e.printStackTrace();
            
            activity_state = activity_state_t.state_created;
            m_resumed_times = 0;
        }
    }
    
	@Override
    public void OnNextpage() {
        if (!m_toc_on)
            onMenuItemSelectedFunc(ITEM_NEXT_PAGE);
        else
            showOrHideTOCList(m_currentTOCPageNum+1, true);
    }

    @Override
    public void OnPrevpage() {
        if (!m_toc_on)
            onMenuItemSelectedFunc(ITEM_PREV_PAGE);
        else
            showOrHideTOCList(m_currentTOCPageNum-1, true);
    }
    public PviBottomBar.OnBackListener m_onBackListener = new OnBackListener() {
        public boolean onBack() {
        	if(m_toc_on) {
        		onMenuItemSelectedFunc(ITEM_TOC);//call hiding TOC function
        	}else {
        		go_back_or_home(1);
        	}
        	return true;
        }
    };
    private void createImageView(){
        m_imgView = (ImageView) findViewById(R.id.pdfimage);
        m_imgView.setOnKeyListener(onImageViewKeyListener);
        RelativeLayout ll = (RelativeLayout)findViewById(R.id.pdfview11);
        GlobalVar appState = (GlobalVar)getApplicationContext();
        if(appState.deviceType==1){
//        	m_imgView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
        	//ll.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
        }
        ll.setLongClickable(true);
        ll.setOnTouchListener(new OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return touchImageViewEvent(event);                    
            }});
        
        m_bookmarkImgView = (ImageView) findViewById(R.id.BookmarkImageView);
        m_bookmarkImgView.setImageResource(R.drawable.icon);
    } 
    
    private boolean touchImageViewEvent(MotionEvent event)
    {
        if (m_resize_enable) // resize page window 
        {
            return touchEvent4SelectRect(event);
        }else if (m_move_enable) // resize page window 
        {
            return touchEvent4Moving(event);

        }else
        {// page down/up
            if (m_pagingmode==2)
            {
                return touchEvent4ScrolingMode(event);
            }
            else
            {
                return touchEvent4Paging(event);
            }
        }
    }
    
    private boolean touchEvent4Paging(MotionEvent event ){
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            m_keydown_x = (int) event.getX();
            m_keydown_y = (int) event.getY();
        } 
        
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
        }else if (event.getAction() == MotionEvent.ACTION_UP)
        {
			int endX = (int)event.getX();
			int endY = (int)event.getY();

			int vx = endX-m_keydown_x;
			int vy = endY-m_keydown_y;
			int l = vx*vx+vy*vy;
			if (l>50*50)
			{
				if ((vx<0&&vy<0)||(vx>0&&vy<0&&2*vx*vx<l)||(vx<0&&vy>0&&2*vx*vx>l))
				{
					onMenuItemSelectedFunc(ITEM_NEXT_PAGE);
				}
				else if((vx>0&&vy>0)||(vx>0&&vy<0&&2*vx*vx>l)||(vx<0&&vy>0&&2*vx*vx<l))
				{
					onMenuItemSelectedFunc(ITEM_PREV_PAGE);
				}
			}
        }
        return true;
    }
    
    private boolean touchEvent4ScrolingMode(MotionEvent event ){
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            m_keydown_x = (int) event.getX();
            m_keydown_y = (int) event.getY();
        }
    
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
			int endX = (int)event.getX();
			int endY = (int)event.getY();
            int vx   = (endX - m_keydown_x);
            int vy   = (endY - m_keydown_y);

            m_scroll_offset = -vy;
			if (m_scroll_offset > 80 || m_scroll_offset < -80) {
				onMenuItemSelectedFunc(ITEM_SET_SCROLL_OFFSET);
			}else if (vx>50)
            {
                    onMenuItemSelectedFunc(ITEM_PREV_PAGE);
            }else if (vx<-50)
            {
					onMenuItemSelectedFunc(ITEM_NEXT_PAGE);
            }

        } 
        return true;
    }
    
    private boolean touchEvent4SelectRect(MotionEvent event )
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            m_keydown_x = (int) event.getX();
            m_keydown_y = (int) event.getY();
            m_prevx=m_keydown_x;
            m_prevy=m_keydown_y;
            
            m_time_sel = System.currentTimeMillis();
        }
        else if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            if (System.currentTimeMillis()-m_time_sel > 100)
                m_time_sel = System.currentTimeMillis();
            else
                return true;
            
            m_nextx=(int) event.getX();
            m_nexty=(int) event.getY();
            
            int w = m_nextx - m_prevx;
            int h = m_nexty - m_prevy;

            if (!((w>10||w<-10)&&(h>10||h<-10)))
                return true;
            m_select_moving++;
            w = m_nextx - m_keydown_x;
            h = m_nexty - m_keydown_y;

            int startx = 0;
            int starty = 0;
            int widthp = 0;
            int heightp = 0;
            if (w>0)
            {
                startx = m_keydown_x;
                widthp = w;
            }
            else
            {
                widthp = -w;
                startx = m_keydown_x+w;
            }
            if (h>0)
            {
                heightp = h;
                starty = m_keydown_y;
            }
            else
            {
                heightp = -h;
                starty = m_keydown_y+h;
            }
            if(constructBitmap(0,false,false)) {
	            m_bitmap = pasteBitmapRect(m_bitmap,
	                    startx, starty, startx+widthp, startx+heightp, true);
	            updateImage(m_imgView,m_bitmap);
	            
	            m_handler_refresh.post(m_runnable_refresh);
	            m_prevx = m_nextx;
	            m_prevy = m_nexty;
            }
        }
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
             int w = (int) event.getX() - m_keydown_x;
             int h = (int) event.getY() - m_keydown_y;
             
             int startx = 0;
             int starty = 0;
             int widthp = 0;
             int heightp = 0;
             if (w>0)
             {
                 startx = m_keydown_x;
                 widthp = w;
             }
             else
             {
                 widthp = -w;
                 startx = m_keydown_x+w;
             }
             if (h>0)
             {
                 heightp = h;
                 starty = m_keydown_y;
             }
             else
             {
                 heightp = -h;
                 starty = m_keydown_y+h;
             }
             
             if (widthp<50||heightp<50)
             {
                 m_select_moving = 0;
                 updateCurrentShow();
                 m_resize_enable = false;
                 return false;
             }

             m_startx = startx;
             m_starty = starty;
             m_widthp = widthp;
             m_heightp = heightp;
             
             m_widthp = m_widthp < m_width_min?m_width_min:m_widthp;
             m_heightp = m_heightp < m_height_min?m_height_min:m_heightp;
             
             m_resize_enable = false;
             m_size_changed = true;
             m_move_enable = true;

             m_scale = (double)m_width/m_widthp;
             if (m_scale<(double)m_height/m_heightp)
                 m_scale=(double)m_height/m_heightp;
             
             m_bitmap = pasteBitmapRect(m_bitmap,
                     m_startx, m_starty, m_startx+m_widthp, m_startx+m_heightp, true);
             updateImage(m_imgView,m_bitmap);
             m_handler_refresh.post(m_runnable_refresh);
             
             if (m_select_moving >5)    //
             {
                 if(constructBitmap(0, false,false)) {
                	 showCurrentPage();
                 }
             }
             else// in case just click down then up
                 updateCurrentShow();//remove the Rect's
             
             m_select_moving = 0;
        }
        return true;
    }
    
    private boolean touchEvent4Moving(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            m_keydown_x = (int) event.getX();
            m_keydown_y = (int) event.getY();
        }
        
        else if (event.getAction() == MotionEvent.ACTION_UP)
        {
            Logger.i("PDFReadActivity", "("+m_keydown_x+","+m_keydown_y+")  "
                    +"("+(int)event.getX()+","+(int)event.getY()+")");
             int w = (int) event.getX() - m_keydown_x;
             int h = (int) event.getY() - m_keydown_y;
             if (w>20||w<-20)
             {
                 w = w*m_widthp/m_width;
                 m_startx -= w;
                 m_size_changed = true;
             }
             if (h>20||h<-20)
             {
                 h = h*m_heightp/m_height;
                 m_starty -= h;
                 m_size_changed = true;
             }
             if (m_size_changed)
             {
                 if(constructBitmap(0,false,false)) {
                	 showCurrentPage();
                 }
             }
        }
        
        return true;
    }
         
    private void add_annotation(String str) {
        if (!m_search_enable)
        {//fot note
            if (!str.equals(new String()))
            {
                hideNoteWindow();
                insertNote(m_add_note_x,m_add_note_y, str);
                showNoteLabel(m_add_note_x,m_add_note_y);
            }
        }
        else
        {// for searching
            hideNoteWindow();
            if (str!=null&&str.length()>0)
            {
                m_search_text = str;
                int pg = -1;
                
                //search() method is protected for multi-thread access internally
                pg = reader.search(str, true, m_currentPageNum-1, 1);
                
                if (pg>=0)
                    jumpShow(pg);
            }
            m_search_enable = false;
        }
    	
    }
    /*
     * create note input window, include an EditText for text inputing and three
     *  buttons for controling 
     * called by Activity.onCreate() only
     */
    private void createNoteWindow()
    {
        m_note_button_ok = (Button) findViewById(R.id.NoteOk);
        m_note_button_cancel = (Button) findViewById(R.id.NoteCancel);
        m_note_button_delete = (Button) findViewById(R.id.NoteDelete);
        m_note_edittext = (EditText) findViewById(R.id.NoteInputEditText);
        m_bookmark_icon = BitmapFactory.decodeResource(this.getResources(), R.drawable.notemark);
        hideNoteWindow();
    
        View.OnClickListener ok_listener = new View.OnClickListener() 
        {
            public void onClick(View arg0) 
            {
                final String str = m_note_edittext.getText().toString();
                add_annotation(str);
            }
        };
        m_note_button_ok.setOnClickListener (ok_listener); 
    
        View.OnClickListener cancel_listener = new View.OnClickListener() 
        {
            public void onClick(View arg0) 
            {
                hideNoteWindow();
                if (m_search_enable)
                    m_search_enable = false;
            }
        };
        m_note_button_cancel.setOnClickListener (cancel_listener); 
    
        View.OnClickListener delete_listener = new View.OnClickListener() 
        {
            public void onClick(View arg0) 
            {
                if (!m_search_enable) // for note
                {
                    hideNoteWindow();
                    //if (m_addnote_enable)
                    if (deleteNote(m_add_note_x,m_add_note_y)>0)
                    {
                        if(constructBitmap(0,false,false)) {
                        	showCurrentPage();
                        }
                    }
                }
                else
                {
                 // for searching
                    hideNoteWindow();
                    final String str = m_note_edittext.getText().toString();
                    if (str!=null&&str.length()>0)
                    {
                        m_search_text = str;
                        int pg = reader.search(str, false, m_currentPageNum-1, 1);//TODO:
                        if (pg>=0)
                            jumpShow(pg);
                    }
                    m_search_enable = false;
                }
            }
        }; 
        m_note_button_delete.setOnClickListener (delete_listener); 
    
        View.OnTouchListener touch_listener = new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) 
            {
                m_add_note_x = (int)event.getX();
                m_add_note_y = (int)event.getY();
                m_add_note_x = m_add_note_x/NOTE_LABEL_W*NOTE_LABEL_W;
                m_add_note_y = m_add_note_y/NOTE_LABEL_H*NOTE_LABEL_H;
                String str = getClickedNoteString(m_add_note_x,m_add_note_y);
                if (str!=null)  // note exists here
                {
                    m_note_edittext.setText(str);
                    m_note_button_delete.setClickable(true);
                    m_note_button_delete.setTextColor(Color.BLACK);
                    showViewNoteWindow(str);
                }
                else
                {
                    if (!m_addnote_enable)
                        return false;
                    m_note_edittext.setText(getString(R.string.str_pdf_add_note));
                    m_note_button_delete.setClickable(false);
                    m_note_button_delete.setTextColor(Color.GRAY);
                    showNoteWindow();
                }
                m_addnote_enable = false;
                return true;
            }
        };
        m_imgView.setOnTouchListener(touch_listener);
    }
    
    private void createTOCList()
    {
        m_toclist = (ViewGroup) findViewById(R.id.toclist);
        
//        for(int i=0;i<m_tocitems.length;i++) {
//        	m_tocitems[i].setFocusable(true);
//    		m_tocitems[i].setFocusableInTouchMode(true);
//        	m_tocitems[i].setOnKeyListener(onKeyListener);
//        }
        
        
    }
    
    private void bindTOCItemsListener()
    {
        View.OnClickListener tocitems_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                final String str = new String((String)(((LinearLayout)v).getTag()));
                Integer id = null;
                try{
                    id = new Integer(str);
                    showOrHideTOCList(-1, false);
                    jumpShow(id-1);
                    adjustChapterItem();
                }catch(NumberFormatException e )
                {
                }
            }
        };

//        for (int i=0; i<m_tocitems.length;i++)
//            m_tocitems[i].setOnClickListener(tocitems_listener);

    }
    
    private void registerBCReceiver()
    {
        //在IntentFilter中选择你要监听的行为
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_UMS_CONNECTED);
        intentFilter.addAction(Intent.ACTION_UMS_DISCONNECTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT); 
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        //intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        //intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        //intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addDataScheme("file");
        registerReceiver(broadcastRec, intentFilter);//注册监听函数
    }
    private boolean showOrHideTOCList(int page, boolean show)
    {
        boolean ret =  false;
        
        if (show)
        {
            if (page<0)
                page = 0;
            else if (page >m_toc_table.length/toc_item_num)
                page = m_toc_table.length/toc_item_num; 
            
            int pagetotle = m_toc_table.length/toc_item_num;
            if (m_toc_table.length%toc_item_num != 0 )
                pagetotle += 1;
            
            m_currentTOCPageNum = page;
            int start = page*toc_item_num;
            if (m_toclist.getVisibility()!=View.VISIBLE)
                m_toclist.setVisibility(View.VISIBLE);
            int j=0;
            String blankspace="";
//            for (int i=0;i<toc_item_num;i++) //TODO: range check
//            {
//                if (start+i<m_toc_table.length)
//                {
//                	for(j=0,blankspace="";j<m_toc_table[start+i].level;j++)
//                		blankspace +="    ";
//                    mtv_toctitle[i].setText(blankspace + m_toc_table[start+i].title);
//                    mtv_tocposition[i].setText(""+(m_toc_table[start+i].position+1));
//                    m_tocitems[i].setTag(""+(m_toc_table[start+i].position+1));
//                }
//            }
    		m_list.clear();//aaa
            for (int i=0;i<toc_item_num;i++) //TODO: range check
            {
                if (start+i<m_toc_table.length)
                {
                	for(j=0,blankspace="";j<m_toc_table[start+i].level;j++)
                		blankspace +="    ";
	    			int x = 1; int y = 10; int width = 200; int height = 60;
	    			PviUiItem[] items = new PviUiItem[]{
	    					new PviUiItem("item_"+i, 0, x, y, width, height, blankspace + m_toc_table[start+i].title, true, true, null),
	    					new PviUiItem("item_"+i, 0, 530, y, width, height, ""+(m_toc_table[start+i].position+1), true, true, null)
	    			};
	    			final int ii=i;
	    			OnClickListener l = new OnClickListener(){
	
	                    @Override
	                    public void onClick(View arg0) {
	                        // TODO Auto-generated method stub
	                    //	if(ii< curList.size()){
	    						//Logger.v("string", chapters[ii].getText());
	    						Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
	    						Bundle sndbundle = new Bundle();
	    						sndbundle.putString("pviapfStatusTip", "进入书籍");
	    						msgIntent.putExtras(sndbundle);
	    						sendBroadcast(msgIntent);
	    						setEvent(ii);
	    				//	}
	                    }
	                    
	                };				
	                items[0].l = l;
	    			m_list.add(items);
                }else {
	    			int x = 1; int y = 10; int width = 200; int height = 60;
	    			PviUiItem[] items = new PviUiItem[]{
	    					new PviUiItem("item_"+i, 0, x, y, width, height, "", true, true, null),
	    			};
	    			m_list.add(items);
                }
    		}
            {
//    			int x = 1; int y = 10; int width = 200; int height = 60;
//    			PviUiItem[] items = new PviUiItem[]{
//    					new PviUiItem("item_999", 0, x, y, width, height, "", true, true, null),
//    			};
//    			m_list.add(items);
            }
    		m_listView.setData(m_list);///aaa

            String ls_pg_info = ""+(page+1);
            set_page_nbr_info(ls_pg_info,""+pagetotle);
            //mtv_menuBtn.setText(getString(R.string.str_pdf_hide_toc)); 
            
            m_listView.requestFocus();
            
            refresh_page();
        }else 
        { 
            if (m_toclist.getVisibility()!=View.INVISIBLE)
            {
                m_toclist.setVisibility(View.INVISIBLE);
            }            
            
            String ls_pg_info = new Integer(m_currentPageNum).toString();
            set_page_nbr_info(ls_pg_info,new Integer(m_total_page_count).toString());
            //mtv_menuBtn.setText(getString(R.string.str_pdf_menu));
        }
        return ret;
    }
    public void setEvent(int k){
    	if(k>=toc_item_num) {
    		return;
    	}
    	PviUiItem[] items = m_list.get(k);
    	if(items.length < 2) {
    		return;
    	}
	    final String str = new String((String)(items[1].text));
	    Integer id = null;
	    try{
	        id = new Integer(str);
	        showOrHideTOCList(-1, false);
	        jumpShow(id-1);
	        adjustChapterItem();
	    }catch(NumberFormatException e )
	    {
	    }
    }

    /*
     * create bottom bar, called by Activity.onCreate() only
     */
    private void createBottomBar()
    {
        //mtv_menuBtn = (ImageButton)findViewById(R.id.menubtn);
        GlobalVar appState = (GlobalVar)getApplicationContext();
        if(appState.deviceType==1){
	        //ViewGroup bottom_bar = (ViewGroup)findViewById(R.id.bottombar);
	        //bottom_bar.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
        }
        TextView.OnClickListener menu_listener =new TextView.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
            	if(check_too_many_clicks()) {
            		//not clicked too frequently
            		return;
            	}
                if (!m_toc_on) {
                	//show_not_toc_menu(true);
                    hideOrShowMenu();
                } else
                {
                    //showOrHideTOCList(-1,false);
                    //adjustChapterItem();
                    //show_not_toc_menu(false);
                    hideOrShowMenu();
                }
            }           
        };
        
        //mtv_menuBtn.setOnClickListener(menu_listener);   
        
        //m_current_page_txt = (TextView) findViewById(R.id.curpage);
        //m_total_pages_txt = (TextView) findViewById(R.id.pages);
//        m_page_infoA = (TextView) findViewById(R.id.pages);
        View.OnClickListener prev_listener = new View.OnClickListener() 
        {
            public void onClick(View arg0) 
            {
                if (!m_toc_on)
                    onMenuItemSelectedFunc(ITEM_PREV_PAGE);
                else
                    showOrHideTOCList(m_currentTOCPageNum-1, true);

            }
        };
        //m_button_prev = findViewById(R.id.prev);
        //m_button_prev.setOnClickListener (prev_listener);

        View.OnClickListener next_listener = new View.OnClickListener() 
        {
            public void onClick(View arg0) 
            {
                if (!m_toc_on)
                    onMenuItemSelectedFunc(ITEM_NEXT_PAGE);
                else
                    showOrHideTOCList(m_currentTOCPageNum+1, true);
            }
        };
        //m_button_next = findViewById(R.id.next);
        //m_button_next.setOnClickListener (next_listener);
        
        if(((GlobalVar)getApplication()).deviceType == 1 ){        
        	//m_button_prev.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
        	//m_button_next.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
        }
        
        // add
        final View fp_application = findViewById(R.id.fp_application);
        if(fp_application!=null){
        fp_application.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	//goto_all_app();
            	go_back_or_home(3);
            }
        });
        }

        final View fp_settings = findViewById(R.id.fp_settings);
        if(fp_settings!=null){
        fp_settings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	//goto_setting();
            	go_back_or_home(4);
            }
        });
        }
        
        final View fp_music =  findViewById(R.id.fp_music);
        if(fp_music!=null){
        fp_music.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	//goto_music();
            	go_back_or_home(5);
            }
        });
        }
        
        final View back = findViewById(R.id.back);
        if(back!=null){
        back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	if(m_toc_on) {
            		onMenuItemSelectedFunc(ITEM_TOC);//call hiding TOC function
            	}else {
            		go_back_or_home(1);
            	}
            }
        });
        }

    }
    
    protected void onPrepareDialog (int id, Dialog dialog)
    {
    	//if(id == PAGE_INPUT_DIALOG_ID) {
//			View entryView = dialog.findViewById(R.id.font_scale_dialog_view);
//			FontSelectView internalView = (FontSelectView)entryView;
//			String str = Double.toString(m_fontscale);
//			internalView.selectCurrentFont(str,str);
    		dialog.setTitle("");
    	//}
    	super.onPrepareDialog(id,dialog);
    }

    Context context=PDFReadActivity.this;
    protected Dialog onCreateDialog(int id)
    {
        switch(id)
        {
        //跳转
            case PAGE_INPUT_DIALOG_ID:
            	
            		PviAlertDialog pd = jumpPage();
                return pd;
                //自动翻页
            case AUTO_PLAY_DIALOG_ID:
            	// TODO Auto-generated method stub
        		LayoutInflater inflater=LayoutInflater.from(this);
        		final View entryView1=inflater.inflate(R.layout.files_2, null);
        		PviAlertDialog builder=new PviAlertDialog(context);
        		//builder.setTitle(getResources().getString(R.string.InputAutoFlipTime));
        		builder.setTitle("请选择自动翻页时间(单位:S)");
        		builder.setCanClose(true);
        		builder.setView(entryView1);
        	    final Button m_time3=(Button)entryView1.findViewById(R.id.select_3);
        	    final Button m_time5=(Button)entryView1.findViewById(R.id.select_5);
        	    final Button m_time8=(Button)entryView1.findViewById(R.id.select_8);
        	    final Button m_time10=(Button)entryView1.findViewById(R.id.select_10);
        	//	final TextView time_3=(TextView)entryView1.findViewById(R.id.time_3);
        	//	final TextView time_5=(TextView)entryView1.findViewById(R.id.time_5);
        	//	final TextView time_8=(TextView)entryView1.findViewById(R.id.time_8);
        	//	final TextView time_10=(TextView)entryView1.findViewById(R.id.time_10);
        		/**
        		 * 设置自动翻页时间为5s
        		 */
        		
        		m_time3.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View v) {
        				// TODO Auto-generated method stub
        				if(type != 5){
        					type = 5;
        					    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
        					    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        				}
        			}
        			
        		});
        		m_time3.setOnFocusChangeListener(new OnFocusChangeListener(){

        			@Override
        			public void onFocusChange(View v, boolean hasFocus) {
        				// TODO Auto-generated method stub
        				if(hasFocus==true){
        					if(type != 5){
        						type = 5;
        						    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
        						    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        						    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        						    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					}
        					}
        			}
            		
            	});
        		m_time5.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View v) {
        				// TODO Auto-generated method stub
        				if(type != 30){
        					type = 30;
        					    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
        					    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
        					    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        				}
        			}
        			
        		});	
        		m_time5.setOnFocusChangeListener(new OnFocusChangeListener(){

        			@Override
        			public void onFocusChange(View v, boolean hasFocus) {
        				// TODO Auto-generated method stub
        				if(hasFocus==true){
        					if(type != 30){
        						type = 30;
        						    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
        						    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
        						    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        						    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					}
        					}
        			}
            		
            	});
        		m_time8.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View v) {
        				// TODO Auto-generated method stub
        				if(type != 60){
        					type = 60;
        					    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
        					    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
        					    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        				}
        			}
        			
        		});
        		m_time8.setOnFocusChangeListener(new OnFocusChangeListener(){

        			@Override
        			public void onFocusChange(View v, boolean hasFocus) {
        				// TODO Auto-generated method stub
        				if(hasFocus==true){
        					if(type != 60){
        						type = 60;
        						    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
        						    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        						    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
        						    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					}
        					}
        			}
            		
            	});
        		m_time10.setOnClickListener(new OnClickListener(){

        			@Override
        			public void onClick(View v) {
        				// TODO Auto-generated method stub
        				if(type != 90){
        					type = 90;
        					    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
        					    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        					    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
        				}
        			}
        			
        		});
        		m_time10.setOnFocusChangeListener(new OnFocusChangeListener(){

        			@Override
        			public void onFocusChange(View v, boolean hasFocus) {
        				// TODO Auto-generated method stub
        				if(hasFocus==true){
        					if(type != 90){
        						type =90;
        						    m_time3.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
        						    m_time5.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        						    m_time8.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));
        						    m_time10.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
        					}
        					}
        			}
            		
            	});
        		builder.setButton(getResources().getString(R.string.autoPageSet), new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				if(type>0){
        				Toast.makeText(PDFReadActivity.this, getResources().getString(R.string.AutoFlipSettingsSuccess),Toast.LENGTH_LONG ).show();	
        				startOrStopAutoPlay(true, type); //开启
        				}
        				else{
        					Toast.makeText(PDFReadActivity.this, getResources().getString(R.string.TimeInputErrorNumber),Toast.LENGTH_LONG ).show();
							return ;
        				}
        		}});
        		builder.setButton2(getResources().getString(R.string.autoPageClose), new DialogInterface.OnClickListener() {
        			@Override
        			public void onClick(DialogInterface dialog, int which) {
        				Toast.makeText(PDFReadActivity.this,getResources().getString( R.string.CloseAutoPage),Toast.LENGTH_LONG ).show();
        				startOrStopAutoPlay(false, -1); //关闭
        				dialog.cancel();
        			}
        		});
        		return builder;
              /*  LayoutInflater inflater2 = LayoutInflater.from(this);
                View entryview2 = inflater2.inflate(R.layout.pageinputdialog,null);
            
                PviAlertDialog pd2 = new PviAlertDialog(getParent());
                m_autoplayEditText = (EditText)entryview2.findViewById(R.id.input_edittext);
                text_change_listener txt_watcher2 = new text_change_listener();
                txt_watcher2.init(1,3600,m_autoplayEditText);

                TextView tv_hint2 = (TextView)entryview2.findViewById(R.id.input_hint_textview);
                tv_hint2.setText(getString(R.string.str_pdf_start_auto_play_hint));
                pd2.setTitle(getString(R.string.str_pdf_start_auto_play_title));
                pd2.setView(entryview2);
                pd2.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.str_pdf_ok),
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                final String str = m_autoplayEditText.getText().toString();
                                Integer delay = null;
                                try{
                                    delay = new Integer(str);
                                }catch(NumberFormatException e )
                                {
//                                    delay = 10;
                                    return;
                                }
                                if (delay < 3) 
                                    delay = 3;
                                startOrStopAutoPlay(true, delay);
                                return;
                            }

                        });
                pd2.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.str_pdf_cancel),
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                return;
                            }
            
                        });
                return pd2;*/
                //设置
            case FONT_SCALE_DIALOG_ID:
            	
            	
             	
             	break;
            case OPEN_ERROR_DIALOG_ID:
                LayoutInflater inflater3 = LayoutInflater.from(this);
                View entryview3 = inflater3.inflate(R.layout.pageinputdialog,null);
            
                PviAlertDialog pd3 = new PviAlertDialog(getParent());
                EditText l_open_error_EditText = (EditText)entryview3.findViewById(R.id.input_edittext);
                l_open_error_EditText.setVisibility(View.INVISIBLE);
                TextView tv_hint3 = (TextView)entryview3.findViewById(R.id.input_hint_textview);
                if (m_adobe_fault_code==1)
                {
                    tv_hint3.setText(getString(R.string.str_pdf_init_error_hint));
                    pd3.setTitle(getString(R.string.str_pdf_init_error_title));
                    
                }else if (m_adobe_fault_code==2)
                {
                    tv_hint3.setText(getString(R.string.str_pdf_open_error_hint));
                    pd3.setTitle(getString(R.string.str_pdf_open_error_title));
                } 
                pd3.setView(entryview3);
//                pd3.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.str_pdf_ok),
//                        new android.content.DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog,
//                                    int which) {
//                                if (m_adobe_fault_code==1)
//                                {
//                                    finish();
//                                }else if (m_adobe_fault_code==2)
//                                {
//                                    reader.close();
//					                  opened = false;
//                                    finish();
//                                } 
//
//                                 return;
//                            }
//
//                        });
                pd3.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.str_pdf_ok),
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                if (m_adobe_fault_code==1)
                                {
                                    finish();
                                }else if (m_adobe_fault_code==2)
                                {
                                    reader.close();
                                    opened = false;
                                    finish();
                                } 

                                 return;
                            }
            
                        });
                return pd3;
            case ADD_ANNOTATION_DIALOG_ID:
            	return buildDialog(PDFReadActivity.this);
            default:
            	break;

        }
        return null;
    }
    public void showFontSettingDialog() {
    	LayoutInflater inflater1=LayoutInflater.from(this);
		   GlobalVar appState = (GlobalVar)getApplicationContext();
		   int skin = 1;//appState.getSkinID();
		   int layoutID = 0 ;
		   switch(skin){
			case 1: layoutID = R.layout.readsetformer ; break ;
			case 2: layoutID = R.layout.readsetformer2 ; break ;
			default: layoutID = R.layout.readsetformer ;
			}
		   final View entryView=inflater1.inflate(layoutID, null);
		   PviAlertDialog dlg = new PviAlertDialog(PDFReadActivity.this);
		   dlg.setView(entryView);
		   ReadSetView internalView = (ReadSetView) entryView;
		   internalView.setDialog(dlg);
		   internalView.setFontBool(false);
		   internalView.setLineBool(false);
		   HashMap<String,String> chooseMap = new HashMap<String,String>();
			chooseMap.put(ReadSetView.CON_SUFANG, "" + m_fontscale);
			internalView.setChooseMap(chooseMap);
			internalView.setSetListener(new ReadSetView.SetListener(){
				public void chooseDoListener(boolean ok, java.util.Map<String,String> chooseButton) {
							if (ok) {
								m_pagingmode = 1;
								float scale = Float.parseFloat(chooseButton.get(ReadSetView.CON_SUFANG));
								if ( (scale-1.0)<0.05 && (scale-1.0)>-0.05) {
		     	                	m_pagingmode = 0;
		     	                }
		     	                double t_fontscale = scale;
		     	                if (m_fontscale-t_fontscale<0.05&&m_fontscale-t_fontscale>-0.05)
		     	                    ;
		     	                else
		     	                {
		     	                    m_fontscale = t_fontscale;
		     	                    scaleShow(m_fontscale);
		     	                  m_is_cutted = false;
		     	                }
							}
						}
					});

		   dlg.show();
    	
    }
    private Dialog build_view_annotation_dlg(String Comment) {
		PviAlertDialog view_annotation_dlg =new PviAlertDialog(context);
		while(Comment.length()<8){
			Comment="\t"+Comment+"\t";
		}
		view_annotation_dlg.setTitle(getResources().getString(R.string.Comtents));
		view_annotation_dlg.setMessage(Comment.toString());
		view_annotation_dlg.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {


			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		return view_annotation_dlg;
    }
	private Dialog buildDialog(Context context){
		LayoutInflater inflater=LayoutInflater.from(this);
		final View entryView=inflater.inflate(R.layout.files, null);
		PviAlertDialog builder=new PviAlertDialog(context);
		builder.setTitle(getResources().getString(R.string.CommmentContent));
		TextView txt_view_content=(TextView)entryView.findViewById(R.id.txt_view_content);
		txt_view_content.setText("");
		builder.setView(entryView);
		builder.setButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				if(((EditText) entryView.findViewById(R.id.view_content)).getText()==null||((EditText) entryView.findViewById(R.id.view_content)).getText().toString().equals("")){
					Toast.makeText(PDFReadActivity.this, getResources().getString(R.string.CommmentContentNull),Toast.LENGTH_LONG ).show();
					post_invalidate_full_screen();
					return;
				}
				String annotation_txt =((EditText) entryView.findViewById(R.id.view_content)).getText().toString();
				add_annotation(annotation_txt);
				dialog.dismiss();
				((EditText)(entryView.findViewById(R.id.view_content))).setText("");
				post_invalidate_full_screen();
			}

		});
		builder.setButton2(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub

				dialog.cancel();
				((EditText)(entryView.findViewById(R.id.view_content))).setText("");
				post_invalidate_full_screen();

			}
		});
		return builder;
	}
    
    //页面跳转
	private PviAlertDialog jumpPage() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View entryview = inflater.inflate(R.layout.pageinputdialog,null);
         
		PviAlertDialog pd = new PviAlertDialog(getParent());
		m_inputEditText2 = (EditText)entryview.findViewById(R.id.input_edittext);
		//text_change_listener txt_watcher = new text_change_listener();
		//txt_watcher.init(1,m_total_page_count,m_inputEditText2);
		TextView tv_hint = (TextView)entryview.findViewById(R.id.input_hint_textview);
		tv_hint.setText(getString(R.string.str_pdf_jump_hint_info));
		Logger.i(TAG, m_total_page_count);
		pd.setTitle(getString(R.string.str_pdf_jump_page)+" (1~" + m_total_page_count + ")");
		pd.setView(entryview);
		pd.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.str_pdf_ok),
		        new android.content.DialogInterface.OnClickListener() {
         
		            @Override
		            public void onClick(DialogInterface dialog,int which) {

		                final String str = m_inputEditText2.getText().toString();
		                Integer page = null;
		                try{
		                    page = new Integer(str);
		                    if(!(page<=0 || page > m_total_page_count)){
		                    jumpShow(page-1);
		                    }else{
		                    	Toast.makeText(PDFReadActivity.this, getResources().getString(R.string.txtInputErrorNumber),Toast.LENGTH_LONG ).show();
		                    }
		                }catch(NumberFormatException e ){
		                	Toast.makeText(PDFReadActivity.this, getResources().getString(R.string.txtInputErrorNumber),Toast.LENGTH_LONG ).show();
		                }
		                //post_invalidate_full_screen();
		            }
		        });
		pd.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.str_pdf_cancel),
		        new android.content.DialogInterface.OnClickListener() {
         
		            @Override
		            public void onClick(DialogInterface dialog,int which) {
		            	post_invalidate_full_screen();
		                return;
		            }
         
		        });
		return pd;
	}
	//我的书签
	public void myBookMark(){
		Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", "进入我的书签");
		msgIntent.putExtras(sndbundle);
		sendBroadcast(msgIntent);
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		sndbundle.putString("act",  "com.pvi.ap.reader.activity.MyBookshelfActivity");
		sndbundle.putString("haveTitleBar","1");
		sndbundle.putString("startType",  "allwaysCreate");   
		sndbundle.putString("SourceType","3");
		sndbundle.putString("FilePath", pdffilename);
		sndbundle.putString("actTabName",  "我的书签");  //跳转到我的书签 ，如果去掉这语句，就会跳到 最近阅读   
		intent.putExtras(sndbundle);
		sendBroadcast(intent);
	}
	private void showMe() {
		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("sender", PDFReadActivity.this.getClass().getName()); // TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
	}

    @Override
    protected void onNewIntent(Intent intent) {
        m_resume_intent = intent;
        super.onNewIntent(intent);
    }
    Intent get_current_intent() {
        Intent revintent = m_resume_intent;
        if (revintent==null)
            revintent = this.getIntent();
        return revintent;
    }
    String get_file_name() {
        Intent revintent = get_current_intent();
        Bundle revbundle = revintent.getExtras();
        String from = revbundle.getString("FromPath");
       // int openPageNum=-1;
        String file_name = new String("");

        if(from == null) {
        	return file_name;
        }
        if (from.equals(new String("2")))   // from annotation activity 
        {
            file_name = revbundle.getString(CommentsInfo.ChapterName);
           // openPageNum = new Integer(revbundle.getString(CommentsInfo.CurrentPage));
           // Logger.i(TAG, revbundle.getString(CommentsInfo.CurrentPage));
        }
        else if(from.equals(new String("1"))) // form book mark activity, or recent read activity 
        {
           // Logger.i(TAG, "PDFRead: From book mark");
            file_name = revbundle.getString("FilePath");
           // openPageNum = new Integer(revbundle.getString(Bookmark.Position));
           // Logger.i("Menu", revbundle.getString(Bookmark.Position));
        }
        else
        {
        	file_name = revbundle.getString("FilePath");
           // openPageNum = getRecentReadingPage();
        }
        return file_name; 
    }
    private boolean getInfoAndStartReader()
    {
        Intent revintent = get_current_intent();
        Bundle revbundle = revintent.getExtras();
        if (revbundle.getString("FontSize") != null
				&& !"".equals(revbundle.getString("FontSize").toString().trim())) {
        	m_fontscale = Float.parseFloat(revbundle.getString("FontSize"));
		}else{
			//m_fontscale = 1.0f ; //if m_fontscale is changed prior to last onPause, keep it 
		}
        String from = revbundle.getString("FromPath");
        int openPageNum=-1;

        if ( (from != null) && from.equals(new String("2")) )   // from annotation activity 
        {
            pdffilename = revbundle.getString(CommentsInfo.ChapterName);
            openPageNum = new Integer(revbundle.getString(CommentsInfo.CurrentPage));
            Logger.i(TAG, revbundle.getString(CommentsInfo.CurrentPage));
        }
        else if( (from != null) && from.equals(new String("1")) ) // form book mark activity, or recent read activity 
        {
            Logger.i(TAG, "PDFRead: From book mark");
            pdffilename = revbundle.getString("FilePath");
            openPageNum = new Integer(revbundle.getString(Bookmark.Position));
            Logger.i("Menu", revbundle.getString(Bookmark.Position));
        }
        else
        {
        	String new_file_name = revbundle.getString("FilePath");
        	if(new_file_name != null) {
        		pdffilename = new_file_name;
        	}
            openPageNum = getRecentReadingPage();
        }
        if (m_toc_on) {
            showOrHideTOCList(-1,false);
            adjustChapterItem();
        }
        m_toc_table = null; //open a new PDF, clean table of contents table
        
        m_first_view_updating = true;

        return open(pdffilename, openPageNum);
    }
	
    public void onResume()
    {
        Logger.i(TAG,"++++++++++++++++++++++++++++++++onResume");
        
        m_resumed_times++;
        m_get_bitmap_failed_times = 0;
        
        if(open_procedure_in_progress) {
        	Logger.i(TAG,TAG + "::onResume() open_procedure_in_progress, return");
        	super.onResume();
        	
        	activity_state = activity_state_t.state_resumed;
        	return;
        }

        //EPDRefresh.refreshGCOnceFlash();
        
        if(opened) {
	        if(m_resumed_times > 1) {
	        	//resume from stopped or paused
	            Intent revintent = get_current_intent();
	            Bundle revbundle = revintent.getExtras();
	            String file_name = get_file_name();
	            if(file_name.equals("") || file_name.equals(pdffilename)) {
	            	//when come from my annotation page, FromPath not included
	            	//already opened same file
	            	super.onResume();
	            	
	            	activity_state = activity_state_t.state_resumed;
	            	
	            	showMe();
	            	
	            	return;
	            }
	        }
        }
        if (opened)
        {
        	close_pdf_reader();
            Logger.i(TAG,TAG+"::onResume : reader closed");
        }
        
        m_imgView.setVisibility(View.INVISIBLE);
        
        final PviAlertDialog dialog = new PviAlertDialog(getParent());
		dialog.setTitle(getResources().getString(R.string.str_pdf_waiting));
		dialog.setMessage(getResources().getString(R.string.str_pdf_opening));
	
//		dialog.show();
		
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
               m_adobe_fault_code = -1;
               switch(msg.what)
               {
                   case 0:  // open succeded
//                       dialog.dismiss();
                       Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
               		Bundle bundleToSend = new Bundle();
               		bundleToSend.putString("sender", PDFReadActivity.this.getClass()
               				.getName()); // TAB内嵌activity类的全名
               		tmpIntent.putExtras(bundleToSend);
               		sendBroadcast(tmpIntent);
               		tmpIntent = null;
               		bundleToSend = null;
                       open_procedure_in_progress = false;
                       
                       break;
                   case 1:  // open failed
                       m_adobe_fault_code = 1;
                       dialog.setMessage(getString(R.string.str_pdf_openerror));
//                       dialog.dismiss();
                       showDialog(OPEN_ERROR_DIALOG_ID);
                       open_procedure_in_progress = false;
                       break;
                   case 2:
                       m_adobe_fault_code = 2;
                       dialog.setMessage("页太大或者文档已经损坏");
//                       dialog.dismiss();
                      // showDialog(OPEN_ERROR_DIALOG_ID);
                       open_procedure_in_progress = false;
                       break;
                   default:
                       break;
               }
               m_timer_resume_show.cancel();
            }
        };
        super.onResume();
        
        //\\Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 1);
        
        open_procedure_in_progress = true;
        
        m_timer_resume_show = new Timer(true);        
        m_timer_resume_show.schedule(
                new TimerTask() 
                { 
                    public void run() 
                    {  
                        Runnable checkUpdate = new Runnable() {  
                            public void run() {
                                if (m_init_ok){
                                    if (getInfoAndStartReader())
                                    {
                                        handler.sendEmptyMessage(0);
                                        m_imgView.setVisibility(View.VISIBLE);
                                    }
                                    else
                                        handler.sendEmptyMessage(2);
                                }
                                else
                                {
                                	Logger.i(TAG, "adobe lib initialized failed ,send 1");
                                    handler.sendEmptyMessage(1);
                                }
                            }
                        };
                        handler.post(checkUpdate);
                    }
                }, 100 ,   Long.MAX_VALUE);
        Logger.i(TAG,"onResum() finished");
        
        activity_state = activity_state_t.state_resumed;
    }
    
    public void onPause()
    {
        Logger.i(TAG,"++++++++++++++++++++++++++++++++onPause");

        if(open_procedure_in_progress) {
        	Logger.i(TAG,TAG + "::onPause() open_procedure_in_progress, return");
        	super.onPause();
        	
        	activity_state = activity_state_t.state_paused;
        	return;
        }

        //add last reading info into DB
        if(opened) {//if open failed , do not insert bookmark
        	insert_bookmark = true;
        	if(insert_bookmark) {
	        	Logger.i(TAG,TAG + "::onPause call insertBookmark");
	        	
	        	insertBookmark(BOOKMARK_TYPE_SYS);/* not just insert, check if it exists the same time*/
	        	Logger.i(TAG,TAG + "end insertBookmark");
	        	
	        	insert_bookmark = false;
        	}
        }
        close_pdf_reader();
        
//        if(m_bitmap!=null&&!m_bitmap.isRecycled())
//            m_bitmap.recycle();
        
        //close menupan
        if(m_popmenu!=null){
            m_popmenu.dismiss();
        }
        
        super.onPause();
        
        Logger.i(TAG,"++++++++++++++++++++++++++++++++onPause finished");
        
        activity_state = activity_state_t.state_paused;
    }
    
    public void onStop()
    {
        Logger.i(TAG,"++++++++++++++++++++++++++++++++onStop");
        if(broadcastRec != null) {
        	try {
        		unregisterReceiver(broadcastRec);
        	}catch (Throwable e) {
        		
        	}
        	broadcastRec = null;
        }
        super.onStop();
        
        activity_state = activity_state_t.state_stopped;
    }
	protected void onStart(){
		Logger.i(TAG,"++++++++++++++++++++++++++++++++onStart");
		super.onStart();
		activity_state = activity_state_t.state_started;
	};
    
    protected void onRestart(){
    	Logger.i(TAG,"++++++++++++++++++++++++++++++++onRestart");
    	super.onRestart();
    	activity_state = activity_state_t.state_restarted;
    };

    protected void onDestroy(){
    	Logger.i(TAG,"++++++++++++++++++++++++++++++++onDestroy");
    	close_pdf_reader();
    	
    	super.onDestroy();
    	activity_state = activity_state_t.state_destroyed;
    };
    void close_pdf_reader() {
    	Logger.i(TAG,"enter close_pdf_reader");
    	if(opened) {
    		Logger.i(TAG,"do close_pdf_reader");
	    	wait_reader_lib_idle();
	        reader.close();
	        wait_reader_lib_idle();
	        opened = false;
	        Logger.i(TAG,"close_pdf_reader finished");
    	}
    }
    void wait_reader_lib_idle() {
 	   boolean has_pend_operation = reader.has_pending_msg();
	   //wait for all pending operation to be finished
	   while(has_pend_operation ) {
		   try {
			   Thread.sleep(50);
		   } catch (InterruptedException e) {
			}
		   has_pend_operation = reader.has_pending_msg();
	   }
    }
    private void go_back_or_home(int flag_in) {
    	final int flag = flag_in;
        if(m_is_playing)
            startOrStopAutoPlay(false, -1); //stop paging
        
		//dialog.setTitle(getResources().getString(R.string.str_pdf_waiting));
		//dialog.setMessage(getResources().getString(R.string.str_pdf_closing));
	
		//dialog.show();
        showMessage(getResources().getString(R.string.str_pdf_closing));
        final Handler handler = new Handler() {
           public void handleMessage(Message msg) {
               if(insert_bookmark) {//if open failed , do not insert bookmark
               	insertBookmark(BOOKMARK_TYPE_SYS);/* not just insert, check if it exists the same time*/
               	insert_bookmark = false;
               }
               close_pdf_reader();
        	   
              //dialog.dismiss();
       			hideMessage();
              Bundle bundle = msg.getData();
              int msg_flag = bundle.getInt("flag");
              if(msg_flag == 1) {
          		Logger.i(TAG,"++++++++++++++++++++++++++++++++go_back_or_home: go Back");
            	  sendBroadcast(new Intent(MainpageActivity.BACK));
              }else if(msg_flag == 2) {
          		Logger.i(TAG,"++++++++++++++++++++++++++++++++go_back_or_home: go to Main page");
            	  gotoMainActiviyPage();
              }else if(msg_flag == 3) {
            	  //all app
            	  Logger.i(TAG,"++++++++++++++++++++++++++++++++go_back_or_home: go all app");
            	  goto_all_app();
              }else if (msg_flag == 4) {
            	  //setting
            	  Logger.i(TAG,"++++++++++++++++++++++++++++++++go_back_or_home: go setting");
            	  goto_setting();
              }else if(msg_flag == 5) {
            	  //music
            	  Logger.i(TAG,"++++++++++++++++++++++++++++++++go_back_or_home: go to music");
            	  goto_music();
              }else {
            	  //error
              }
              }
           };
        Thread checkLoader = new Thread() {  
        public void run() {
        		
                pdfReaderWrapper.acquire_noninterruptable();
                
                try {
                	
            		Message msg = new Message();
            		Bundle bundle = new Bundle();
                	switch(flag) {
                	case 1://go back
                		bundle.putInt("flag", 1);
                		break;
                	case 2://go home
                		bundle.putInt("flag", 2);
                		break;
                	case 3://go all app
                		bundle.putInt("flag", 3);
                		break;
                	case 4://go setting
                		bundle.putInt("flag", 4);
                		break;
                	case 5://go music
                		bundle.putInt("flag", 5);
                		break;
               		default:
                		bundle.putInt("flag", 0);
               			break;
                	}            		
            		msg.setData(bundle);
            		handler.sendMessage(msg);
            		
                }catch(Exception e) {
                	//safety purpose, to avoid semaphore leaking in case of exception thrown
                }catch(Error e) {
                	//safety purpose, to avoid semaphore leaking
                }
                
                pdfReaderWrapper.release();
            }
        };
        checkLoader.start();
    }
    private void onMenuItemSelectedFunc(int select)
    {
        switch (select)
        {
            case ITEM_OPEN://open
                Intent intent = new Intent();
                intent.setClass(PDFReadActivity.this, MyDocumentActivity.class);
                final int REQUEST_CODE = 1;
                startActivityForResult(intent, REQUEST_CODE);
                break;
                
            case ITEM_BACK:// exit
            	if(m_toc_on) {
            		onMenuItemSelectedFunc(ITEM_TOC);//call hiding TOC function
            	}else {
            		go_back_or_home(1);
            	}
                break;
                
            case ITEM_NEXT_PAGE://next
            	if(check_too_many_clicks()) {
            		//too many clicks. ignores, return immediately to avoid 5 seconds android's alive checking
            		break;
            	}
                if (m_pagingmode!=2)
                {
                    if (m_total_page_count== m_currentPageNum && reader.docReachHeadOrTail(false))
                    {
                        Toast toast = Toast.makeText(this, R.string.str_pdf_reach_end, Toast.LENGTH_SHORT);
                        toast.show();
                        if(m_is_playing) {
                        	startOrStopAutoPlay(false, -1); //stop paging
                        }                      
                        break;
                    }
    
                    m_watch_at = System.currentTimeMillis();
                    m_watch_for   = "Just from 'next'";
                    nextShow();
                }
                else
                {
                    //scrollShow(SCROLL_OFFSET_H);
                    scrollShow(get_scroll_page_height());
                }
                break;
                
            case ITEM_PREV_PAGE://previous
            	if(check_too_many_clicks()) {
            		//too many clicks. ignores, return immediately to avoid 5 seconds android's alive checking
            		break;
            	}
                if (m_pagingmode!=2)
                {
                    if (1 == m_currentPageNum && reader.docReachHeadOrTail(true))
                    {
                        Toast toast = Toast.makeText(this, R.string.str_pdf_reach_head, Toast.LENGTH_SHORT);
                        toast.show();
                        break;
                    }
    
                    m_watch_at = System.currentTimeMillis();
                    m_watch_for   = "Just from 'prev'";  
                    prevShow();
                }
                else
                {
                    scrollShow(-get_scroll_page_height());
                }
                break;
            case ITEM_SET_SCROLL_OFFSET:
            	scrollShow(m_scroll_offset);
            	break;
            case ITEM_TOC: // Chapters
                if (!m_toc_on)
                {
                    feedChapterTable();
                    if (m_toc_table==null) 
                        break;
                    if (m_toc_table.length>0)
                        showOrHideTOCList(0, true); //show 
                }
                else
                {
                        showOrHideTOCList(-1, false); //hide
                }
                adjustChapterItem();
                break;
                
            case ITEM_ADD_BM: //add book mark
                this.insertBookmark(BOOKMARK_TYPE_LOC);
                this.hideOrShowBookmarkLabel();
                break;
            case ITEM_MY_BM:
            	myBookMark();
            	break;
            case ITEM_DEL_BM: //remove book mark
                String where = 
                    Bookmark.FilePath+"="+"'"+pdffilename+"'" 
                    +" and "+ 
                    Bookmark.Position + " = " + "'"+new Integer(m_currentPageNum).toString()+"'";
                this.removeBookmark(where,null );
                this.hideOrShowBookmarkLabel();
                break;
                
            case  ITEM_SELECT:    // SELECT
                resetCurrentShow();
                m_resize_enable = true;
                break;
            case ITEM_AUTO_CUT:
                if (m_is_playing)
                    startOrStopAutoPlay(false, -1); //stop paging incase adobe halt
                m_is_cutted = !m_is_cutted;

                //autoCutMargin() method is protected internally now
                reader.autoCutMargin(m_is_cutted);
                
                jumpShow(m_currentPageNum-1);
                break;
                
            case ITEM_AUTO_PLAY:
                if (m_is_playing)
                    startOrStopAutoPlay(false, -1); //stop paging
                else
                    showDialog(AUTO_PLAY_DIALOG_ID);
                break;
                
            case ITEM_PAGE_INPUT:
            	post_invalidate_full_screen();
            	jumpPage().show();
                break;
                
            case ITEM_SEARCH:
                m_search_enable = true;
                showNoteWindow();
                m_note_edittext.setText(m_search_text);
                m_note_button_ok.setText(getString(R.string.str_pdf_foreward));
                m_note_button_delete.setText(getString(R.string.str_pdf_backward));
                break;
                
            case ITEM_ADD_NOTE:
                m_addnote_enable = true;
                break;
                
            case ITEM_NOTE_LIST:
                gotoNoteList();
                break;
                
            case ITEM_SCALING: 
             //   hideOrShowScalingMenu();
                break;
                
            case ITEM_FONT_SIZE:
            	showFontSettingDialog();
                break;
                
            case ITEM_TOUCH_FUNC:
                hideOrShowTouchFuncMenu();
                break;
                
            case ITEM_PAGING_MODE:
                hideOrShowPagingModeMenu();
                break;
            case ITEM_BACK_TO_HOME:
            	go_back_or_home(2);
            	break;
            case ITEM_PAGE_MODE_SINGLE_PAGE:
                if (m_pagingmode==0)
                    ;
                else
                {
                    m_pagingmode = 0;
                    m_fontscale = 1.0;
                    m_is_cutted = false;
                    checkOrientionAndSetViewPort();
                    if (reader.setPagingMode(m_pagingmode))
                        jumpShow(m_currentPageNum-1);
                }
            	break;
            case ITEM_PAGE_MODE_SCROLLING_PAGE:
                if (m_pagingmode==2)
                    ;
                else{
                    m_pagingmode=2;
                    m_fontscale = 1.0;
                    m_is_cutted = false;
                    checkOrientionAndSetViewPort();
                    if (reader.setPagingMode(m_pagingmode))
                        jumpShow(m_currentPageNum-1);
                }
            	break;

            default:
                break;
        }
    }
    
    // get TOC from adobe host, store is in "table"
    private void feedChapterTable()
    {
        if (m_toc_table==null) 
        {
            int totle_len = reader.getChapterListLen();
            final int l_items_each_time = 100;
            m_toc_table = new ChapterInfo[totle_len];

            int times =0;
            int tmp_len=0;
            ChapterInfo[] tmp_table;
            do
            {
                tmp_table = (ChapterInfo[])reader.getChapterList(times);
                if (tmp_table!=null)
                    tmp_len = tmp_table.length;
                else
                    tmp_len =0;

                for (int i=0;i<tmp_len;i++){
                    if (i+times*l_items_each_time>totle_len-1)
                        break;
                    m_toc_table[i+times*l_items_each_time] =tmp_table[i]; 
                }

                times++;
            }while(tmp_len==100);
        }
    }
    
    private boolean scrollShow(int yoffset)
    {

        Logger.i(TAG,"++++++page num:"+m_currentPageNum+" scroll pos: "+m_scroll_offset_totle + " image height:" + last_page_height + " screen height: "+ m_height);
        m_scroll_offset_totle += yoffset;
        if (reader.getPageCount()== m_currentPageNum) {
        	//current last page
        	int diff = last_page_height - m_height;
        	if(diff>0) {
        		if(m_scroll_offset_totle>diff) {
        			m_scroll_offset_totle = diff;
		            reader.setScrollOffset(m_scroll_offset_totle);
		            jumpShow(m_currentPageNum);
		            
	                Toast toast = Toast.makeText(this, R.string.str_pdf_reach_end, Toast.LENGTH_SHORT);
	                toast.show();
        		}
        	}else {
	        	if(m_scroll_offset_totle > 0) {
		            m_scroll_offset_totle = 0;
		            reader.setScrollOffset(m_scroll_offset_totle);
		            jumpShow(m_currentPageNum);
		            
	                Toast toast = Toast.makeText(this, R.string.str_pdf_reach_end, Toast.LENGTH_SHORT);
	                toast.show();
		            return true;
	        	}
        	}

        }
        
        if (m_scroll_offset_totle>m_height)
        {
            if (reader.getPageCount()== m_currentPageNum)
            {
                Toast toast = Toast.makeText(this, R.string.str_pdf_reach_end, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }
            m_scroll_offset_totle = 0;
            reader.setScrollOffset(m_scroll_offset_totle);
            jumpShow(m_currentPageNum);
        }
        else if (m_scroll_offset_totle<-m_height)
        {
            if (1 == m_currentPageNum)
            {
                Toast toast = Toast.makeText(this, R.string.str_pdf_reach_head, Toast.LENGTH_SHORT);
                toast.show();
                return false;
            }

            m_scroll_offset_totle = 0;
            reader.setScrollOffset(m_scroll_offset_totle);
            jumpShow(m_currentPageNum-2);
        }
        else
        {
            reader.setScrollOffset(m_scroll_offset_totle);
            jumpShow(m_currentPageNum-1);
        }
        
        return true;
    }
    private boolean jumpShow(int pg)
    {
    	//refresh_page();
    	
        boolean ret = false;
        if (m_pagingmode!=2)// cached
        {
            ret = reader.gotoPage(pg, false);
            if (ret) {
                if(constructBitmap(0, true,false)) {
                	m_paging_direction = 0;
                	showCurrentPage();
                }
            }
        }
        else
        {
            ret = reader.gotoPage(pg, false);
            if (ret)
            {
                if(constructBitmap(-1,true,false)) {
                	showCurrentPage();
                }
            }
        }
        return ret;
    }

    private boolean resetCurrentShow()
    {
        m_startx    = 0;
        m_starty    = 0;
        m_widthp    = m_width;
        m_heightp   = m_height;
        if(constructBitmap(0, false,false)) {
	        updateImage(m_imgView,m_bitmap);
	        m_handler_refresh.post(m_runnable_refresh);
        }
        return true;
    }
    
    private boolean updateCurrentShow()
    {
        if(constructBitmap(0, false,false)) {
	        if (m_size_changed)
	        {
	            m_bitmap = scalePartialBitmap(m_bitmap,
	                    m_startx, m_starty, m_widthp, m_heightp, true);
	        }
	        updateImage(m_imgView,m_bitmap);
	        m_handler_refresh.post(m_runnable_refresh);
        }
        return true;
    }
    
    private boolean nextShow()
    {
    	//refresh_page();
    	
        if (m_pagingmode!=2)    // cached
        {
        	Logger.i(TAG," call nextShow");
            if(constructBitmap(1, true,true)) {
	            m_paging_direction = 1;
	            showCurrentPage();
	            Logger.i(TAG, "finished call nextShow");
            }
            return true;
        }
        else
        {
            if (reader.gotoNextScreenPage())
            {
                if(constructBitmap(-1,true,false)) {
                	showCurrentPage();
                }
                return true;
            }
            return false;
        }
    }

    private boolean prevShow()
    {
    	//refresh_page();
    	
        if (m_pagingmode!=2)	// cached
        {
            if(constructBitmap(-1, true,true)) {
            	m_paging_direction = -1;
            	showCurrentPage();
            }
            return true;
        }
        else
        {
            if (reader.gotoPrevScreenPage())
            {
                if(constructBitmap(-1, true,false)) {
                	showCurrentPage();
                }
                return true;
            }
            return false;
        }
    }

    private boolean scaleShow(double scale)
    {
        // waiting for loader thread finished
/*        while(m_loader_running.get()){
            try{
                Thread.sleep(50);
                }catch(InterruptedException e)
                {
                    Logger.i(TAG,e.toString());
                }
            }*/
        checkOrientionAndSetViewPort();
        if (reader.setFontSize(scale))
        {
            jumpShow(m_currentPageNum-1);
            return true;
        }
        return false;
    }
    private void show_not_toc_menu(boolean show,PviPopupWindow popmenu_v) {
    	if(show) {
    		getMenuItem("navigation").isVisible = true;
    		getMenuItem("toc").isVisible = true;
    		getMenuItem("back").isVisible = true;
    		
    		getMenuItem("toc").text = getString(R.string.str_pdf_show_toc);
    		
			getMenuItem("addbm").isVisible = true;
			getMenuItem("mybookmark").isVisible = true;
			getMenuItem("fontsize").isVisible = true;
			getMenuItem("autocut").isVisible = true;
			getMenuItem("autoplay").isVisible = true;
			getMenuItem("addnote").isVisible = true;
			getMenuItem("delbm").isVisible = true;
			getMenuItem("pagingmode").isVisible = true;
			getMenuItem("notelist").isVisible = true;
			getMenuItem("input").isVisible = true;
    		//<pvimenuitem android:text="打开背景音乐" pvi:op="bgmusic" />
    		
    	}else {
			getMenuItem("addbm").isVisible = false;
			getMenuItem("mybookmark").isVisible = false;
			getMenuItem("fontsize").isVisible = false;
			getMenuItem("autocut").isVisible = false;
			getMenuItem("autoplay").isVisible = false;
			getMenuItem("addnote").isVisible = false;
			getMenuItem("delbm").isVisible = false;
			getMenuItem("pagingmode").isVisible = false;
			getMenuItem("notelist").isVisible = false;
			getMenuItem("input").isVisible = false;
    	}
    }
    private void adjustChapterItem()
    {
        PviMenuItem tv = null;
        tv = getMenuItem("toc");
        
        if (m_toclist.getVisibility()==View.INVISIBLE)
            m_toc_on = false;
        else if (m_toclist.getVisibility()==View.VISIBLE)
            m_toc_on = true;

        if (tv!=null)
        {
            if (m_toc_on)
                tv.text=getString(R.string.str_pdf_hide_toc);
            else
                tv.text=getString(R.string.str_pdf_show_toc);
        }
    }

private View.OnKeyListener onKeyListener=new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			Logger.i(TAG,"TOC item onKey");
			if(event.getAction()==KeyEvent.ACTION_DOWN)
			{
		    	if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN) {
					switch (v.getId()) {
					default:
						return false;
				  }
		    		
		    	}
		    	if(keyCode==KeyEvent.KEYCODE_DPAD_UP) {
					switch (v.getId()) {
					default:
						return false;
				  }
		    		
		    	}
			
			}
			return false;
		}
};

	public boolean  onKeyDown(int keyCode, KeyEvent  event) {
		if(m_toc_on) {
	    	boolean jump = true;
	    		if(m_listView.hasFocus()) {
	    			jump = false; //do not move focus if any of TOC item has focus
	    		}
	    	if(jump) {
	    		if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
	    			m_listView.requestFocus();
	    			return true;
	    		}
	    	}
			
		}
		
		return super.onKeyDown(keyCode, event);
	}
	private View.OnKeyListener onImageViewKeyListener = new OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
    //public boolean  onKeyUp (int keyCode, KeyEvent  event)
    //{
	    Logger.i(TAG,"onKeyUp(int keyCode, "+keyCode);
	    if (event.getAction() == KeyEvent.ACTION_UP) {
		    if(!m_toc_on) {
		        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
		        		Logger.i(TAG,"onKeyUp KEYCODE_DPAD_LEFT");
		                onMenuItemSelectedFunc(ITEM_PREV_PAGE);
		                return true;
		        }
		        if(keyCode == KeyEvent.KEYCODE_DPAD_UP) {
		        	Logger.i(TAG,"onKeyUp KEYCODE_DPAD_UP");
		        	//m_imgView.requestFocus();
					return false ;
		        }
		        if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
		        		Logger.i(TAG,"onKeyUp KEYCODE_DPAD_RIGHT");
		                onMenuItemSelectedFunc(ITEM_NEXT_PAGE);
		                return true;
		        }
		        if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
		        	Logger.i(TAG,"onKeyUp KEYCODE_DPAD_DOWN");
		        	//m_imgView.requestFocus();
					return false ;
		        }
		        if(keyCode == KeyEvent.KEYCODE_MENU) {
		        		Logger.i(TAG,"onKeyUp KEYCODE_MENU");
			    		if(check_too_many_clicks()) {
			    			return true;
			    		}
		                hideOrShowMenu();
		                return true;
		        }
		        if (keyCode == KeyEvent.KEYCODE_ENTER) {
		        	Logger.i(TAG,"onKeyUp KEYCODE_ENTER");
		        	return true;
		        }
				if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
					Logger.i(TAG,"onKeyUp KEYCODE_DPAD_CENTER");
					return true;
				}
		         if( keyCode == KeyEvent.KEYCODE_BACK) {
		        	 	Logger.i(TAG,"onKeyUp KEYCODE_BACK");
		                onMenuItemSelectedFunc(ITEM_BACK);
		                return true;
		         }
		    }else {
		    	return false;
		    }
	    }
    	//return super.onKeyUp(keyCode, event);
	    return false;
    }
		};

    private void feedMenuItemIndexHashmap()
    {
        m_menu_item_index_hashmap = new HashMap<String, Integer>(ITEM_MAX);    // hash map (tag, index)
        m_menu_item_index_hashmap.put("open",       ITEM_OPEN );
        m_menu_item_index_hashmap.put("back",       ITEM_BACK );
        m_menu_item_index_hashmap.put("next",       ITEM_NEXT_PAGE );
        m_menu_item_index_hashmap.put("prev",       ITEM_PREV_PAGE );
        m_menu_item_index_hashmap.put("toc",        ITEM_TOC );
        m_menu_item_index_hashmap.put("addbm",      ITEM_ADD_BM );
        m_menu_item_index_hashmap.put("mybookmark", ITEM_MY_BM);
        m_menu_item_index_hashmap.put("delbm",      ITEM_DEL_BM );
        m_menu_item_index_hashmap.put("select",     ITEM_SELECT );
        m_menu_item_index_hashmap.put("autocut",    ITEM_AUTO_CUT );
        m_menu_item_index_hashmap.put("autoplay",   ITEM_AUTO_PLAY );
        m_menu_item_index_hashmap.put("input",      ITEM_PAGE_INPUT );
        m_menu_item_index_hashmap.put("search",     ITEM_SEARCH );
        m_menu_item_index_hashmap.put("addnote",    ITEM_ADD_NOTE );
        m_menu_item_index_hashmap.put("notelist",   ITEM_NOTE_LIST );
        m_menu_item_index_hashmap.put("scaling",    ITEM_SCALING );
        m_menu_item_index_hashmap.put("fontsize",   ITEM_FONT_SIZE );
        m_menu_item_index_hashmap.put("touchfunc",  ITEM_TOUCH_FUNC );
        m_menu_item_index_hashmap.put("pagingmode", ITEM_PAGING_MODE );
        m_menu_item_index_hashmap.put("navigation", ITEM_BACK_TO_HOME );
        m_menu_item_index_hashmap.put("pagingmodeview0",ITEM_PAGE_MODE_SINGLE_PAGE);
        m_menu_item_index_hashmap.put("pagingmodeview1",ITEM_PAGE_MODE_SCROLLING_PAGE);
    }
   
/*    private void pdf_setMenuListener(View v) {
        LinearLayout ll = (LinearLayout) v;
        LinearLayout ll2 = (LinearLayout) ll.getChildAt(0);
        int itemCount = ll2.getChildCount();
        m_menu_item_hashmap = new HashMap<Object, TextView>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            TextView menuItem = (TextView) ll2.getChildAt(i);
            if (menuItem.getTag() != null) {
                //\\menuItem.setOnClickListener(mainMenuclick);
                m_menu_item_hashmap.put(menuItem.getTag(), menuItem);
            }
        }
    }*/
    
    private OnUiItemClickListener mainMenuclick = new OnUiItemClickListener() {
        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
            Logger.v(TAG,"menu item clicked: "+vTag);
            Integer int_var = m_menu_item_index_hashmap.get(vTag);
            //\\m_popmenu.dismiss();
            if(int_var != null) {
                closePopmenu();
                int idx = int_var; 
                onMenuItemSelectedFunc(idx);
            }
        
        }
    };
    private boolean gotoNoteList()
    {
        if(m_is_playing) {
        	startOrStopAutoPlay(false, -1);
        }
        
       	insertBookmark(BOOKMARK_TYPE_SYS);/* not just insert, check if it exists the same time*/
/*		close_pdf_reader();
*/              
		 Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", "进入我的批注");
		msgIntent.putExtras(sndbundle);
		
			sendBroadcast(msgIntent);
		  Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		  Bundle bunde = new Bundle();
		  bunde.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
		  bunde.putString("haveTitleBar","1");
		  bunde.putString("startType",  "allwaysCreate"); 
		  int idx = pdffilename.lastIndexOf("/");
		  if(idx == -1) {
			  bunde.putString("bookName", pdffilename);
		  }else {
			  bunde.putString("bookName", pdffilename.substring(idx+1));
		  }
		  bunde.putString("actTabName",  "我的批注");  //跳转到我的书签 ，如果去掉这语句，就会跳到 最近阅读   
		  intent.putExtras(bunde);
		  sendBroadcast(intent);
				  
//          dialog.dismiss();
        
        return true;
    }
    
    
//    @Override
//     can't act normally, if this activity plug into PVI-frame
//    public boolean onTouchEvent(MotionEvent event) 
//    {
//        touchImageViewEvent(event);
//        return super.onTouchEvent(event);
//    }



    private boolean constructBitmap(int cache, boolean first, boolean try_sync_get_first)
    /*
     * the parameter: try_sync_get_first: if true, get bitmap from cache directly,do not dispatch call to the separated thread
     */
    {
        byte[] bytes;
        boolean asyn_call = false;
        try{
        	if(try_sync_get_first) {
        		//firstly try to get bitmap synchronously within 1 seconds. 
        		//if the operation can not be finished within 1 seconds,convert to asynchronous call
        		//in case of asynchronous call, the resulting bitmap will be post to main GUI thread
        		bytes = reader.getBitmapStuff_sync(cache, 500/*timeout in milliseconds*/);
        		if(bytes == null) {
        			bytes = reader.getBitmapStuff(cache); //convert back to asynchronous call
        			asyn_call = true;
        			
        			Logger.i(TAG,"getBitmapStuff, can not get result from cache directly, convert to asynchronous call");
        		}else {
        			Logger.i(TAG,"getBitmapStuff, get result from cache directly");
        		}
        	}else {
        		bytes = reader.getBitmapStuff(cache);
        	}
        }catch(OutOfMemoryError e) {
            bytes = null;
            Logger.e(TAG, e.toString());
        }
        /*
        if(bytes == null) {
        	boolean show_toast = false;
        	if( last_toast_time == -1) {
        		last_toast_time = System.currentTimeMillis();
        		show_toast = true;
        	}else {
        		long current_time = System.currentTimeMillis();
        		if(current_time - last_toast_time > 5000) {
        			show_toast = true;
        			last_toast_time = current_time;
        		}else {
        			//do not show too many toasts
        		}
        	}
        	if(show_toast) {
        		Toast toast = Toast.makeText(this, R.string.str_pdf_processing_in_progress, Toast.LENGTH_SHORT);
        		toast.show();
        	}
        }*/
        if (bytes==null || bytes.length==0) {
        //important: always return true to let outside flow to continue 
            return true;//false;
        }else {
        	//if get bitmap immediately now, decode it and save it to m_bitmap as below
        }

        if(m_bitmap!=null&&!m_bitmap.isRecycled())
            m_bitmap.recycle();
        try
        {
            m_bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
        }catch(OutOfMemoryError e) {
        	Logger.i(TAG,"constructBitmap: decodeByteArray failed due to OutOfMemory");
            Logger.e(TAG, e.toString());
        }
        if(m_bitmap == null) {
            Toast toast = Toast.makeText(this, R.string.str_pdf_decode_bitmap_failed, Toast.LENGTH_SHORT);
            toast.show();
        }

        if (first)
        {
            if (m_pagingmode!=2) //cache enabled
            {
                m_currentPageNum = reader.getCurrentPage(cache)+1;
                Logger.i(TAG,"constructBitmap: set  page info. current page num is" + m_currentPageNum);
            }
            else //no cache
            {
                m_currentPageNum = reader.getCurrentPage(-1)+1;
                Logger.i(TAG,"constructBitmap: no cache ,set  page info. current page num is" + m_currentPageNum);
            }
        }
            
        return true;
    }

    private boolean showCurrentPage() 
    {

        addAllNoteLabel();//TODO: scale may change the note label display
        hideOrShowBookmarkLabel();

        if (m_size_changed)
        {
            m_bitmap = scalePartialBitmap(m_bitmap, 
                    m_startx, m_starty, m_widthp, m_heightp, true);
        }

        String ls_pg_info = new Integer(m_currentPageNum).toString();
        set_page_nbr_info("" + ls_pg_info,new Integer(m_total_page_count).toString());
        //        m_page_infoA.setText("" + m_total_page_count);
        Logger.i(m_watch_for+" to ending 'showCurrentPage'", new Long(System.currentTimeMillis()-m_watch_at).toString());
        
        updateImage(m_imgView,m_bitmap);

        if (m_pagingmode!=2)
        {
            reader.preload(m_paging_direction ,m_currentPageNum-1);
            
            m_paging_direction = -2;

            Logger.i(m_watch_for+" to Thread start ", new Long(System.currentTimeMillis()-m_watch_at).toString());
        }
        return true;
    }
  
    private boolean hideOrShowBookmarkLabel()
    {
        if (currentPageIsMarked()) //current page is marked
        {
            if (m_bookmarkImgView.getVisibility()!=View.VISIBLE)
            {
                m_bookmarkImgView.setVisibility(View.VISIBLE);
                return true;
            }
        }
        else
        {
            if (m_bookmarkImgView.getVisibility()!=View.INVISIBLE)
            {
                m_bookmarkImgView.setVisibility(View.INVISIBLE);
                return true;
            }
        }
        return false;
    }

    private boolean currentPageIsMarked()
    {
        /*TODO: query*/
        boolean ret = false;
        String where = 
            Bookmark.FilePath + "=" + "'"+ pdffilename +"'" 
            +" and " 
            + Bookmark.Position + " = " + "'"+ new Integer(m_currentPageNum).toString()+"'"
            +" and "
            + Bookmark.BookmarkType + " = " + "'1'";
        Logger.i("PDFReadActivity", where);
        Cursor cur=null;
        cur=managedQuery(Bookmark.CONTENT_URI, null,
                where, null, Bookmark.DEFAULT_SORT_ORDER);

        if (cur!=null && cur.moveToFirst())
            ret = true;
        
        if (cur!=null)
            stopManagingCursor(cur);
        if (cur != null && !cur.isClosed()) {
			cur.close();
		}
 
        return ret;
    }

    //add book mark into DB
    private boolean insertBookmark(int type)
    {
        if (type==0)
        {
            String where = Bookmark.FilePath+"="+"'"+pdffilename+"'"
                +" and "
                +Bookmark.BookmarkType+"="+"'0'";
            
            Logger.i(TAG,TAG + "::insertBookmark position: 1");            
            
            getContentResolver().delete(Bookmark.CONTENT_URI,where,null);
            
            Logger.i(TAG,TAG + "::insertBookmark position: 2");
        }else {
            String where = 
                Bookmark.FilePath + "=" + "'"+ pdffilename +"'" 
                +" and "
                + Bookmark.BookmarkType + " = " + "'1'";
            Cursor cur=null;
            cur=managedQuery(Bookmark.CONTENT_URI, null,
                    where, null, Bookmark.CreatedDate);

            if(cur == null) {
            	return false;
            }
            if (cur.getCount()>=5) {
                cur.moveToFirst();
                String min_date = cur.getString(cur.getColumnIndex(Bookmark.CreatedDate) ); 
                where = Bookmark.FilePath+"='"+pdffilename+"'"+" and " +Bookmark.BookmarkType+"='1'" + " and " + Bookmark.CreatedDate + "='" + min_date + "'";
                getContentResolver().delete(Bookmark.CONTENT_URI, where,null);
            }
            if (!cur.isClosed()) {
    			cur.close();
    		}
        }
 
        ContentValues values = new ContentValues();

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");   
        Date CurTime = new Date(System.currentTimeMillis());
        String now = formatter.format(CurTime);

        /*TODO: contentId should be re-defined*/
        String contentId = pdffilename + new Integer(m_currentPageNum).toString();

        values.put(Bookmark.ContentId, contentId);
        values.put(Bookmark.BookmarkType, new Integer(type).toString());
        values.put(Bookmark.SourceType, 3);
        values.put(Bookmark.CreatedDate, now);
        values.put(Bookmark.ContentName, pdffilename.substring(1+pdffilename.lastIndexOf("/")));
        values.put(Bookmark.FilePath, pdffilename);
        values.put(Bookmark.Position, new Integer(m_currentPageNum).toString());
        values.put(Bookmark.FontSize, m_fontscale);
        
        Logger.i(TAG,TAG + "::insertBookmark position: 3");
        
        getContentResolver().insert(Bookmark.CONTENT_URI, values);
        
        Logger.i(TAG,TAG + "::insertBookmark position: 5");
        
        return true;

    }

    // delete book mark from DB
    private int removeBookmark(String where,String[] selectionArgs)
    {
        return getContentResolver().delete(Bookmark.CONTENT_URI,where,selectionArgs);  	
    }

    private int getRecentReadingPage()
    {
        int ret = 0;
        String where = Bookmark.FilePath+"="+"'"+pdffilename+"'"
            +" and "
            +Bookmark.BookmarkType+"="+"'0'"; 

        Cursor cur=null;
        cur=managedQuery(Bookmark.CONTENT_URI, null,where, null, Bookmark.DEFAULT_SORT_ORDER);

        if (cur!=null && cur.moveToFirst())
        {
            String pos =null;
            pos = cur.getString(cur.getColumnIndex(Bookmark.Position));
            ret = new Integer(pos);
        }
        
        if (cur!=null)
            stopManagingCursor(cur);
        if (cur != null && !cur.isClosed()) {
			cur.close();
		}
 
        return ret;
    }

//    protected void onActivityResult(int requestcode, int resultcode, Intent data)
//    {
//        if (resultcode==RESULT_OK)
//        {
//            Bundle extras = data.getExtras();
//            if (extras!=null)
//            {
//                pdffilename = extras.getString("FilePath"); 
//                open(pdffilename, m_openPageNum);
//            }
//        }
//    }

    private boolean addAllNoteLabel()
    {
        //find all the note in current page
        //add all the note label onto current PDF page image
        boolean ret =false;
        String where = 
            CommentsInfo.FilePath + " = " + "'"+ pdffilename +"'" 
            +" and " 
            + CommentsInfo.CurrentPage + " = " + "'"+ new Integer(m_currentPageNum).toString()+"'";
        Logger.i("PDFReadActivity", where);
        Cursor cur=null;
        cur=managedQuery(CommentsInfo.CONTENT_URI, null,where, null, CommentsInfo.DEFAULT_SORT_ORDER);

        if (cur!=null && cur.moveToFirst())
        {
            String pos =null;
            int pos_x = 0;
            int pos_y = 0;
            do
            {
                pos = cur.getString(cur.getColumnIndex(CommentsInfo.StartPosition));
                Logger.i("PDFReadActivity", pos);
                String pos_strs[]=pos.split(" ",2);
                if (pos_strs!=null&&pos_strs.length==2)
                {
                    pos_x = new Integer(pos_strs[0]);
                    pos_y = new Integer(pos_strs[1]);
                    m_bitmap = pasteBitmap(m_bitmap,m_bookmark_icon,pos_x,pos_y,true);
                    Logger.i("PDFReadActivity", new Integer(pos_x).toString()+"   "+new Integer(pos_y).toString());
                }
            } while (cur.moveToNext());
            ret = true;
        }
        else
        {
            Logger.i("PDFReadActivity", "No notes at this page");
            ret = false;
        }
        
        if (cur!=null)
            stopManagingCursor(cur);
        if (cur != null && !cur.isClosed()) {
			cur.close();
		}
        
        return ret;
    }
    
    private void showNoteLabel(int x, int y) 
    {
        m_bitmap = pasteBitmap(m_bitmap,m_bookmark_icon,x,y, true);
        updateImage(m_imgView,m_bitmap);
    }

    private Bitmap pasteBitmap( Bitmap inbm, Bitmap addbm , int x, int y, boolean recycle)  
    {  
        if( inbm == null )  
            return null;  
        int w = inbm.getWidth();
        int h = inbm.getHeight();
        try{
            Bitmap newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );
            Canvas cv = new Canvas( newb );  
            cv.drawBitmap( inbm, 0, 0, null );
            if (recycle)
                if(inbm!=null&&!inbm.isRecycled())
                    inbm.recycle();
            
            Paint paint = new Paint();
            paint.setAlpha(128);  
            cv.drawBitmap( addbm, x+NOTE_LABEL_W/2, y+NOTE_LABEL_H/2, paint ); 
            cv.save( Canvas.ALL_SAVE_FLAG );  
            cv.restore();  
            return newb;  
        }catch(OutOfMemoryError e) {
        	Logger.i(TAG,"pasteBitmap: OutOfMemoryError");
            Logger.e(TAG, e.toString());
            return inbm;
        }
    }

//    private Bitmap cloneBitmap( Bitmap inbm )  
//    {  
//        if( inbm == null )  
//            return null;  
//        int w = inbm.getWidth();
//        int h = inbm.getHeight();
//        Bitmap newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );
//        Canvas cv = new Canvas( newb );  
//        cv.drawBitmap( inbm, 0, 0, null );
//        
//        Paint paint = new Paint();
//        paint.setAlpha(128);  
//        cv.drawBitmap( inbm, 0, 0, paint ); 
//        cv.save( Canvas.ALL_SAVE_FLAG );  
//        cv.restore();  
//        return newb;  
//    }
    
    private Bitmap pasteBitmapRect( Bitmap inbm, 
                                        int left, int top, 
                                        int right, int bottom, boolean recycle)  
    {  
        if( inbm == null )  
            return null;  
        int w = inbm.getWidth();
        int h = inbm.getHeight();
        try{
            Bitmap newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );
            Canvas cv = new Canvas( newb );  
            cv.drawBitmap( inbm, 0, 0, null );
            if (recycle)
                if(inbm!=null&&!inbm.isRecycled())
                    inbm.recycle();
            
            Paint paint = new Paint();
            paint.setAlpha(128);  
            cv.drawRect((float)left,(float)top, (float)right, (float)bottom, paint);
            cv.save( Canvas.ALL_SAVE_FLAG );
            cv.restore();  
            return newb;
        }catch(OutOfMemoryError e) {
            Logger.e(TAG, e.toString());
            return inbm;
        }
    }
    
    private Bitmap scalePartialBitmap( Bitmap inbm,
            int startx, int starty, int width, int height, boolean recycle)
    {  
        if( inbm == null )  
            return null;

        if (startx==0&&starty==0&&width==m_width&&height==m_height)//TODO: 
            return inbm;
        
        Logger.i("PDFReadActivity", "startx="+startx+":"+ width+"starty="+starty+":"+height);

        int w = inbm.getWidth();
        int h = inbm.getHeight();
        
        if (startx>w-width)
            startx = w-width;
        else if (startx<0)
            startx = 0;
        if (starty>h-height)
            starty = h-height;
        else if (starty<0)
            starty = 0;
        m_startx = startx;
        m_starty = starty;

        try{
            Bitmap newb = Bitmap.createBitmap( w, h, Config.ARGB_8888 );
            Canvas cv = new Canvas( newb );  
            Paint paint = new Paint();
    
            Rect src = new Rect(startx, starty, startx+width, starty+height);
            Rect dst = new Rect(0, 0, w, h);
    
            cv.drawBitmap(inbm, src, dst, paint);
            if (recycle)
                if(inbm!=null&&!inbm.isRecycled())
                    inbm.recycle();
    
            cv.save( Canvas.ALL_SAVE_FLAG );  
            cv.restore();  
            return newb;
        }catch(OutOfMemoryError e) {
            Logger.e(TAG, e.toString());
            return inbm;
        }
    }

    private boolean insertNote(int x, int y, String comment)
    {
        String comment_old =  getClickedNoteString(x,y);
        if (comment_old==null) // no note at (x,y)
        {
            Logger.i("PDFReadActivity", "insertNote start");
            ContentValues values = new ContentValues();

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            Date CurTime = new Date(System.currentTimeMillis());
            String now = formatter.format(CurTime);

            /*TODO: contentId should be re-defined*/
            String contentId = pdffilename + 
                new Integer(m_currentPageNum).toString()
                + " "+ new Integer(x).toString()
                + " "+ new Integer(y).toString();
            String position = new Integer(x).toString()+" "+ new Integer(y).toString();
            values.put(CommentsInfo.ContentId, contentId);
            values.put(CommentsInfo.Comment, comment);
            values.put(CommentsInfo.ChapterName,pdffilename);//TODO: error if use this item
            values.put(CommentsInfo.ContentName,  pdffilename.substring(1+pdffilename.lastIndexOf("/")));
            values.put(CommentsInfo.FilePath,pdffilename);
            values.put(CommentsInfo.CurrentPage, new Integer(m_currentPageNum).toString());
            values.put(CommentsInfo.CommentTime, now);
            values.put(CommentsInfo.StartPosition,position);
            values.put(CommentsInfo.EndPosition,position);

            getContentResolver().insert(CommentsInfo.CONTENT_URI, values);
            Logger.i("PDFReadActivity", "insertNote end");
        }
        else if(!comment_old.equals(comment))//changed
        {
            Logger.i("PDFReadActivity", "comment="+comment+ "old="+comment_old);
            /*TODO: contentId should be re-defined*/
            String contentId = pdffilename + 
                new Integer(m_currentPageNum).toString()
                + " "+ new Integer(x).toString()
                + " "+ new Integer(y).toString();
            ContentValues values = new ContentValues();
            values.put(CommentsInfo.Comment, comment);
            Logger.i("PDFReadActivity", "comment="+comment+ "old="+comment_old);
            getContentResolver().update(CommentsInfo.CONTENT_URI,values,CommentsInfo.ContentId + "=" + "'"+contentId+"'",null);
            Logger.i("PDFReadActivity", "comment="+comment+ "old="+comment_old);
        }
        return true;
    }

    private int deleteNote(int x, int y)
    {
        String contentId = pdffilename + 
            new Integer(m_currentPageNum).toString()
            + " "+ new Integer(x).toString()
            + " "+ new Integer(y).toString();
        return getContentResolver().delete(CommentsInfo.CONTENT_URI,CommentsInfo.ContentId + "=" + "'"+contentId+"'",null);
    }

    private String getClickedNoteString(int x, int y)
    {
        //x=x*NOTE_LABEL_W/NOTE_LABEL_W;
        //y=y*NOTE_LABEL_H/NOTE_LABEL_H;
        String ret = null;
        String where = 
            CommentsInfo.FilePath + " = " + "'"+ pdffilename +"'" 
            +" and " 
            + CommentsInfo.CurrentPage + " = " + "'"+ new Integer(m_currentPageNum).toString()+"'"
            +" and "
            + CommentsInfo.StartPosition +"="+ "'"+new Integer(x).toString()+" "+new Integer(y).toString()+"'";
        Logger.i("PDFReadActivity","getClickedNoteString"+ where);
        Cursor cur=null;
        cur=managedQuery(CommentsInfo.CONTENT_URI, null,where, null, CommentsInfo.DEFAULT_SORT_ORDER);

        if (cur!=null && cur.moveToFirst())
        {
            ret =cur.getString(cur.getColumnIndex(CommentsInfo.Comment)); 
        }
        
        if (cur!=null)
            stopManagingCursor(cur);
        if (cur != null && !cur.isClosed()) {
			cur.close();
		}
        
        return ret;
    }
    private void showNoteWindow()
    {
   		showDialog(ADD_ANNOTATION_DIALOG_ID);
   		post_invalidate_full_screen();
    	if(false) {
        if (m_note_button_ok.getVisibility()!=View.VISIBLE)
            m_note_button_ok.setVisibility(View.VISIBLE);
        if (m_note_button_cancel.getVisibility()!=View.VISIBLE)
            m_note_button_cancel.setVisibility(View.VISIBLE);
        if (m_note_button_delete.getVisibility()!=View.VISIBLE)
            m_note_button_delete.setVisibility(View.VISIBLE);
        if (m_note_edittext.getVisibility()!=View.VISIBLE)
            m_note_edittext.setVisibility(View.VISIBLE);
    	}
    }
    private void showViewNoteWindow(String comment) {
    	if( view_annotation_dlg == null ) {
    		view_annotation_dlg = build_view_annotation_dlg(comment);
    	}
    	if(view_annotation_dlg != null) {
    		view_annotation_dlg.show();
    	}
    }

    private void hideNoteWindow()
    {
        if (m_note_button_ok.getVisibility()!=View.INVISIBLE)
            m_note_button_ok.setVisibility(View.INVISIBLE);
        if (m_note_button_cancel.getVisibility()!=View.INVISIBLE)
            m_note_button_cancel.setVisibility(View.INVISIBLE);
        if (m_note_button_delete.getVisibility()!=View.INVISIBLE)
            m_note_button_delete.setVisibility(View.INVISIBLE);
        if (m_note_edittext.getVisibility()!=View.INVISIBLE)
            m_note_edittext.setVisibility(View.INVISIBLE);
        hideInputMethodWindow();
    }

    private void setOpenParaToDefault()
    {
        m_is_cutted = false;
//        m_fontscale = 1.0;
        m_pagingmode = 0;
    }
    private boolean open(String filename, int pg)
    {
        File file = new File(filename);
        if (!file.exists())
        {
            Toast toast = Toast.makeText(this, R.string.str_pdf_open_error_hint, Toast.LENGTH_SHORT);
            toast.show();
        	gotoMainActiviyPage();
            //finish();
            return false;
        }
    	
        setOpenParaToDefault();
        m_bitmap = null;
        updateImage(m_imgView,m_bitmap);
        insert_bookmark = false;
        Logger.i(TAG,"open file. clear old content");
        if (pdffilename.length()!=0) // not empty
        {
            if (opened)
            {
                reader.close();
                m_toc_table = null; //free old TOC table
                opened = false;
            }
            
            Intent tmpIntent = new Intent(
                    MainpageActivity.SET_TITLE);
            Bundle sndbundle = new Bundle();
            sndbundle.putString("title", pdffilename.substring(1+pdffilename.lastIndexOf("/")));
            tmpIntent.putExtras(sndbundle);
            sendBroadcast(tmpIntent);
            
            long now = System.currentTimeMillis();
            if (reader.open(pdffilename))
            {
                m_hasTOC =  reader.hasTableOfContent();
                m_total_page_count = reader.getPageCount(); //call getPageCount before jumpShow to avoid blocking
                reader.setFontSize(m_fontscale);
                m_currentPageNum = pg;
                insert_bookmark = true;
                if (jumpShow(pg-1))
                {
                    Logger.i("(Adobe open only) open Elapse", new Long(System.currentTimeMillis()-now).toString());
                    opened = true;

                    return true;
                }
            }else {
              //  Toast toast = Toast.makeText(this, R.string.str_pdf_open_error_hint, Toast.LENGTH_SHORT);
               // toast.show();
            	gotoMainActiviyPage();
            }
        }
        return false;
    }
   
    private void hideOrShowMenu()
    {
    	if(true) {
    	super.menupan();
    	}else {
    	Logger.i(TAG,TAG+"::hideOrShowMenu()");
        GlobalVar appState = ((GlobalVar)getApplicationContext());
        if(m_popmenu==null)
        {
            popmenuView = appState.getMenu(this);
            feedMenuItemIndexHashmap();
            //pdf_setMenuListener(popmenuView);
            m_popmenu = new PviPopupWindow(popmenuView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//            translateMenuItemsString();
        }     
        
        if(m_popmenu!=null)
        {
            if(m_popmenu.isShowing())
            {
                m_popmenu.dismiss();
            }else
            {
                hideAllSubMenus();
                adjustMenuItems();
                m_popmenu.showAtLocation(findViewById(R.id.bottombar), Gravity.BOTTOM|Gravity.LEFT, 0, 45);
            }
        }
    	}

    }
     
    /*
     * get menu item of "strtag" specified 
     * in: tag
     * out: TextView of the menu item
     */
    //
/*    private PviMenuItem getMenuItemByTag(String strtag)
    {
        return (PviMenuItem) m_menu_item_hashmap.get(strtag);
    }*/
  
    /*
     * enable or disable items on condition  
     */
    private boolean adjustMenuItems()
    {

        PviMenuItem tv = null;
        tv = getMenuItem("toc");
        
        if (tv!=null)
        {
            if (m_hasTOC)
            {
                tv.enable=true;
            }
            else
            {
                tv.enable=false;
            }
        }
        else
        {
            return false;
        }
        
        PviMenuItem tvac = null;
        tvac = getMenuItem("autocut");

        if (m_pagingmode!=0)    //scroll mode
        {
            if (tvac!=null)
            {
                tvac.enable=false;
            }
        }
        else
        {
            if (tvac!=null)
            {
                tvac.enable=true;
            }
        }
        
        if (m_is_cutted)
        {
            if (tvac!=null)
                tvac.text=getString(R.string.str_pdf_cancel_cut);

        }
        else
        {
            if (tvac!=null)
                tvac.text=getString(R.string.str_pdf_auto_cut);
        }

        PviMenuItem tvpm = null;
        tvpm = getMenuItem("pageadjust");
        if (m_pagingmode==2)    //scroll mode
        {
            if (tvpm!=null)
            {
                tvpm.enable=true;
            }
        }
        else
        {
            if (tvpm!=null)
            {
                tvpm.enable=false;
            }
        }
        
        boolean marked = currentPageIsMarked();
        PviMenuItem tvabm = getMenuItem("addbm");
        PviMenuItem tvdbm = getMenuItem("delbm");
        if (marked)    // enable deleting bookmark, disable adding bookmark
        {
            if (tvabm!=null&&tvdbm!=null)
            {
                tvabm.enable=false;
                
                tvdbm.enable=true;

            }
        }
        else    // enable adding bookmark, disable deleting bookmark
        {
            if (tvabm!=null&&tvdbm!=null)
            {
                tvabm.enable=true;
                
                tvdbm.enable=false;
            }
        }
        
        return true;
    }
    
    

//  private TextView[] pageadjustmenuText = new TextView[4];
//  private ImageView[] pageadjustmenuLine = new ImageView[4];
//  private AbsoluteLayout pageadjustmenu = null;
//  private int m_pagefit=0;
//    private void createPageAdjustmenu()
//    {
//        pageadjustmenu = (AbsoluteLayout) findViewById(R.id.pageadjustmenu);
//        this.pageadjustmenuText[0] = (TextView) findViewById(R.id.pageadjustview0);
//        this.pageadjustmenuText[1] = (TextView) findViewById(R.id.pageadjustview1);
//        this.pageadjustmenuText[2] = (TextView) findViewById(R.id.pageadjustview2);
//        this.pageadjustmenuText[3] = (TextView) findViewById(R.id.pageadjustview3);
//        
//        this.pageadjustmenuLine[0] = (ImageView) findViewById(R.id.pageadjustline0);
//        this.pageadjustmenuLine[1] = (ImageView) findViewById(R.id.pageadjustline1);
//        this.pageadjustmenuLine[2] = (ImageView) findViewById(R.id.pageadjustline2);
//        this.pageadjustmenuLine[3] = (ImageView) findViewById(R.id.pageadjustline3);
//    }
    
//    private void bindPageAdjustMenuEvent(){
//        //setup menu text onfocuslistener
//        int count_items = pageadjustmenuText.length;
//        for(int i=0;i<count_items;i++){
//            final int j = i;
//            OnFocusChangeListener menuItem_listener =  new OnFocusChangeListener(){
//                public void onFocusChange(View V, boolean isFocused){
//                    if (isFocused==true)
//                        pageadjustmenuLine[j].setVisibility(View.VISIBLE);
//                    else
//                        pageadjustmenuLine[j].setVisibility(View.INVISIBLE);
//                }
//            };
//            pageadjustmenuText[i].setOnFocusChangeListener(menuItem_listener);
//        }
//
//        TextView.OnClickListener item0_listener =   new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                int fit = 0;
//                if (m_pagefit==fit)
//                    return;
//                m_pagefit = fit;
//                if (reader.setFitMode(fit))
//                    jumpShow(m_currentPageNum-1);
//                hideOrShowPageAdjustMenu();
//            }
//        }; 
//        pageadjustmenuText[0].setOnClickListener(item0_listener);
//
//
//        TextView.OnClickListener item1_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                int fit = 1;
//                if (m_pagefit==fit)
//                    return;
//                m_pagefit = fit;
//                if (reader.setFitMode(fit))
//                    jumpShow(m_currentPageNum-1);
//                hideOrShowPageAdjustMenu();
//            }
//        };
//        pageadjustmenuText[1].setOnClickListener(item1_listener);
//
//
//        TextView.OnClickListener item2_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                int fit = 2;
//                if (m_pagefit==fit)
//                    return;
//                m_pagefit = fit;
//                if (reader.setFitMode(fit))
//                    jumpShow(m_currentPageNum-1);
//                hideOrShowPageAdjustMenu();
//            }
//        }      ;
//        pageadjustmenuText[2].setOnClickListener(item2_listener);
//
//
//        TextView.OnClickListener item3_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                hideOrShowPageAdjustMenu();
//            }
//        }      ;
//        pageadjustmenuText[3].setOnClickListener(item3_listener);
//
//    }
//
//
//    private void hideOrShowPageAdjustMenu()
//    {
//        if (pageadjustmenu.getVisibility() != View.VISIBLE) 
//        {
//            pageadjustmenu.setVisibility(View.VISIBLE);
//            pageadjustmenu.bringToFront();
//            pageadjustmenuText[0].requestFocus();
//        } 
//        else
//        {
//            pageadjustmenu.setVisibility(View.INVISIBLE);
//        }
//
//    }
    
    private void createScalingmenu()
    {
    }
    
    private void bindScalingMenuEvent(){
//        //setup menu text onfocuslistener
//        int count_items = scalingmenuText.length;
//        for(int i=0;i<count_items;i++){
//            final int j = i;
//            OnFocusChangeListener menuItem_listener =  new OnFocusChangeListener(){
//                public void onFocusChange(View V, boolean isFocused){
//                    if (isFocused==true)
//                        scalingmenuLine[j].setVisibility(View.VISIBLE);
//                    else
//                        scalingmenuLine[j].setVisibility(View.INVISIBLE);
//                }
//            };
//            scalingmenuText[i].setOnFocusChangeListener(menuItem_listener);
//        }
//
//        TextView.OnClickListener item0_listener =   new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                double t_scale = 0.75;
//                if (m_scale-t_scale<0.05&&m_scale-t_scale>-0.05)
//                    ;
//                else
//                {
//                    m_scale = t_scale;
//                    m_size_changed = true;
//                }
//                //hideOrShowScalingMenu();
//            }
//        }; 
//        scalingmenuItem[0].setOnClickListener(item0_listener);
//
//
//        TextView.OnClickListener item1_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                double t_scale =1.0;
//                if (m_scale-t_scale<0.05&&m_scale-t_scale>-0.05)
//                    ;
//                else
//                {
//                    m_scale = t_scale;
//                    m_size_changed = true;
//                }
//               // hideOrShowScalingMenu();
//            }
//        };
//        scalingmenuItem[1].setOnClickListener(item1_listener);
//
//
//        TextView.OnClickListener item2_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                double t_scale =1.5;
//                if (m_scale-t_scale<0.05&&m_scale-t_scale>-0.05)
//                    ;
//                else
//                {
//                    m_scale = t_scale;
//                    m_size_changed = true;
//                }
//                //hideOrShowScalingMenu();
//            }
//        }      ;
//        scalingmenuItem[2].setOnClickListener(item2_listener);
//
//
//        TextView.OnClickListener item3_listener = new TextView.OnClickListener() {
//            @Override
//                public void onClick(View v) {
//                double t_scale = 2.0;
//                if (m_scale-t_scale<0.05&&m_scale-t_scale>-0.05)
//                    ;
//                else{
//                    m_scale = t_scale;
//                    m_size_changed = true;
//                }
//               // hideOrShowScalingMenu();
//                }
//        }      ;
//        scalingmenuItem[3].setOnClickListener(item3_listener);
//
//
//        TextView.OnClickListener item4_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                double t_scale = 4.0;
//                if (m_scale-t_scale<0.05&&m_scale-t_scale>-0.05)
//                    ;
//                {
//                    m_scale = t_scale;
//                    m_size_changed = true;
//                }
//             //   hideOrShowScalingMenu();
//            }
//        }      ;
//        scalingmenuItem[4].setOnClickListener(item4_listener);
//        
//        TextView.OnClickListener item5_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//              //  scalingmenu.setVisibility(View.INVISIBLE);
//                //hideOrShowScalingMenu();
//            }
//        }      ;
//        scalingmenuItem[5].setOnClickListener(item5_listener);

    }
    
    private void createTouchFuncmenu()
    {
     //   touchfuncmenu = (AbsoluteLayout) findViewById(R.id.touchfuncmenu);
        
        this.touchfuncmenuItem[0] = (LinearLayout) findViewById(R.id.touchfuncitem0);
        this.touchfuncmenuItem[1] = (LinearLayout) findViewById(R.id.touchfuncitem1);
        this.touchfuncmenuItem[2] = (LinearLayout) findViewById(R.id.touchfuncitem2);
        
        this.touchfuncmenuText[0] = (TextView) findViewById(R.id.touchfuncview0);
        this.touchfuncmenuText[1] = (TextView) findViewById(R.id.touchfuncview1);
        this.touchfuncmenuText[2] = (TextView) findViewById(R.id.touchfuncview2);

        this.touchfuncmenuLine[0] = (ImageView) findViewById(R.id.touchfuncline0);
        this.touchfuncmenuLine[1] = (ImageView) findViewById(R.id.touchfuncline1);
        this.touchfuncmenuLine[2] = (ImageView) findViewById(R.id.touchfuncline2);
        
    }
    
    private void bindTouchFuncMenuEvent(){
        //setup menu text onfocuslistener
        int count_items = touchfuncmenuText.length;
        for(int i=0;i<count_items;i++){
            final int j = i;
            OnFocusChangeListener menuItem_listener =  new OnFocusChangeListener(){
                public void onFocusChange(View V, boolean isFocused){
                    if (isFocused==true)
                        touchfuncmenuLine[j].setVisibility(View.VISIBLE);
                    else
                        touchfuncmenuLine[j].setVisibility(View.INVISIBLE);
                }
            };
            touchfuncmenuText[i].setOnFocusChangeListener(menuItem_listener);
        }

        TextView.OnClickListener item0_listener =   new TextView.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                m_move_enable = false;
                hideOrShowTouchFuncMenu();
            }
        }; 
        touchfuncmenuItem[0].setOnClickListener(item0_listener);


        TextView.OnClickListener item1_listener = new TextView.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                m_move_enable = true;
                hideOrShowTouchFuncMenu();
            }
        };
        touchfuncmenuItem[1].setOnClickListener(item1_listener);


        TextView.OnClickListener item2_listener = new TextView.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                hideOrShowTouchFuncMenu();
            }
        }      ;
        touchfuncmenuItem[2].setOnClickListener(item2_listener);

    }


    private void hideOrShowTouchFuncMenu()
    {
//        if (touchfuncmenu.getVisibility() != View.VISIBLE) 
//        {
//            int t_selected = m_move_enable?1:0;
//            for (int i=0;i<touchfuncmenuItem.length-1;i++)
//            {
//                if (i==t_selected)
//                    touchfuncmenuText[i].setTextColor(Color.GRAY);
//                else
//                    touchfuncmenuText[i].setTextColor(Color.BLACK);
//            }
//            
//           // touchfuncmenu.setVisibility(View.VISIBLE);
//            //touchfuncmenu.bringToFront();
//            touchfuncmenuText[0].requestFocus();
//        } 
//        else
//        {
//           // touchfuncmenu.setVisibility(View.INVISIBLE);
//        }

    }
    private void createpagingmodemenu()
    {
//        pagingmodemenu = (ViewGroup) findViewById(R.id.pagingmodemenu);
//
//        this.pagingmodemenuItem[0] = (LinearLayout) findViewById(R.id.pagingmodeitem0);
//        this.pagingmodemenuItem[1] = (LinearLayout) findViewById(R.id.pagingmodeitem1);
//        this.pagingmodemenuItem[2] = (LinearLayout) findViewById(R.id.pagingmodeitem2);
//
//        this.pagingmodemenuText[0] = (TextView) findViewById(R.id.pagingmodeview0);
//        this.pagingmodemenuText[1] = (TextView) findViewById(R.id.pagingmodeview1);
//        this.pagingmodemenuText[2] = (TextView) findViewById(R.id.pagingmodeview2);
//        
//        this.pagingmodemenuLine[0] = (ImageView) findViewById(R.id.pagingmodeline0);
//        this.pagingmodemenuLine[1] = (ImageView) findViewById(R.id.pagingmodeline1);
//        this.pagingmodemenuLine[2] = (ImageView) findViewById(R.id.pagingmodeline2);
    }
    
//    private void bindpagingmodeMenuEvent(){
//        //setup menu text onfocuslistener
//        int count_items = pagingmodemenuText.length;
//        for(int i=0;i<count_items;i++){
//            final int j = i;
//            OnFocusChangeListener menuItem_listener =  new OnFocusChangeListener(){
//                public void onFocusChange(View V, boolean isFocused){
//                    if (isFocused==true)
//                        pagingmodemenuLine[j].setVisibility(View.VISIBLE);
//                    else
//                        pagingmodemenuLine[j].setVisibility(View.INVISIBLE);
//                }
//            };
//            pagingmodemenuItem[i].setOnFocusChangeListener(menuItem_listener);
//        }
//
//        TextView.OnClickListener item0_listener =   new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                if (m_pagingmode==0)
//                    ;
//                else
//                {
//                    m_pagingmode = 0;
//                    m_fontscale = 1.0;
//                    m_is_cutted = false;
//                    if (reader.setPagingMode(m_pagingmode))
//                        jumpShow(m_currentPageNum-1);
//                }
//                hideOrShowPagingModeMenu();
//            }
//        }; 
//        pagingmodemenuItem[0].setOnClickListener(item0_listener);
//
//
//        TextView.OnClickListener item1_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                if (m_pagingmode==2)
//                    ;
//                else{
//                    m_pagingmode=2;
//                    m_fontscale = 1.0;
//                    if (reader.setPagingMode(m_pagingmode))
//                        jumpShow(m_currentPageNum-1);
//                }
//                hideOrShowPagingModeMenu();
//            }
//        };
//        pagingmodemenuItem[1].setOnClickListener(item1_listener);
//
//
//        TextView.OnClickListener item2_listener = new TextView.OnClickListener() {
//            @Override
//            public void onClick(View v) 
//            {
//                hideOrShowPagingModeMenu();
//            }
//        };
//        pagingmodemenuItem[2].setOnClickListener(item2_listener);
//    }


    private void hideOrShowPagingModeMenu()
    {

    }
    
    private void hideAllSubMenus()
    {
//        if (scalingmenu.getVisibility() != View.INVISIBLE) 
//        {
//            scalingmenu.setVisibility(View.INVISIBLE);
//        }
//        if (fontsizemenu.getVisibility() != View.INVISIBLE) 
//        {
//            fontsizemenu.setVisibility(View.INVISIBLE);
//        }
////        if (pageadjustmenu.getVisibility() != View.INVISIBLE) 
////        {
////            pageadjustmenu.setVisibility(View.INVISIBLE);
////        }
//        if (touchfuncmenu.getVisibility() != View.INVISIBLE) 
//        {
//            touchfuncmenu.setVisibility(View.INVISIBLE);
//        }
        if (pagingmodemenu.getVisibility() != View.INVISIBLE) 
        {
            pagingmodemenu.setVisibility(View.INVISIBLE);
        }
    }
    /*
     * parameter
     * play: 
     *      true:   auto page foreward in every 'delay' second
     *      false:  stop paging
     * delay: by seconds, usless if 'play' is false
     */
    private boolean startOrStopAutoPlay(boolean play, int delay)
    {
        if (play)
        {
            Logger.i(TAG,"+++++++++++++++++++++ you set delay to"+delay);
            m_is_playing = true;
            m_auto_play_timer = new Timer(true);
            m_auto_play_timer.schedule(
                    new TimerTask() 
                    { 
                        public void run() 
                        {  
                            m_handler_autoplay.post(m_runnable_next);
                        } 
                    }, delay*1000 , delay*1000);
        }
        else
        {
        	if(m_is_playing) {
        		m_is_playing = false;
            	m_auto_play_timer.cancel();
            	Logger.i(TAG,"stop page auto turnning");
        	}
        }
        
        PviMenuItem tv = null;
        tv = getMenuItem("autoplay");
        
        if (tv!=null)
        {
            if (m_is_playing)
                tv.text=getString(R.string.str_pdf_stop_auto_play);
            else
                tv.text=getString(R.string.str_pdf_start_auto_play);
        }
        else
        {
            return false;
        }
        
        return true;
    }
    
    private void hideInputMethodWindow(){
      ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
      .hideSoftInputFromWindow(m_note_edittext
      .getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
	private void gotoMainActiviyPage() {
		Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("act",
				"com.pvi.ap.reader.activity.MainpageInsideActivity");
		intent.putExtras(sndBundle);
		sendBroadcast(intent);
	}
	private void goto_all_app() {
        Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("actID", "ACT16000");
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
   	}
	private void goto_setting() {
        Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("actID", "ACT15000");
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
   	}
	private void goto_music() {
        Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("actID", "ACT13200");
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
    }

    private pdfReaderWrapper reader;               //adobe reader
    private ChapterInfo[] m_toc_table;      //toc list
    private String pdffilename = "";        //PDF file name
    private boolean opened = false;
    private static boolean m_init_ok = false;
    
    private HashMap<String, Integer> m_menu_item_index_hashmap;    // hash map (tag, index)
    public static final int  ITEM_OPEN = 0;//open
    public static final int  ITEM_BACK = 1; // exit
    public static final int  ITEM_NEXT_PAGE = 2;//next
    public static final int  ITEM_PREV_PAGE = 3;//previous
    public static final int  ITEM_TOC =  6;// Chapters
    public static final int  ITEM_ADD_BM =  7;//add book mark
    public static final int  ITEM_DEL_BM =  8;//remove book mark
    public static final int  ITEM_SELECT = 9;    // SELECT
    public static final int  ITEM_AUTO_CUT = 10;
    public static final int  ITEM_AUTO_PLAY = 11;
    public static final int  ITEM_PAGE_INPUT = 12;
    public static final int  ITEM_SEARCH = 13;
    public static final int  ITEM_ADD_NOTE = 14;
    public static final int  ITEM_NOTE_LIST = 15;
    public static final int  ITEM_SCALING = 16;
    public static final int  ITEM_FONT_SIZE = 17;
    public static final int  ITEM_TOUCH_FUNC = 18;
    public static final int  ITEM_PAGING_MODE = 19;
    public static final int  ITEM_BACK_TO_HOME = 20;
    public static final int  ITEM_PAGE_MODE_SINGLE_PAGE = 21;
    public static final int  ITEM_PAGE_MODE_SCROLLING_PAGE = 22;
    public static final int  ITEM_MY_BM = 23;
    public static final int  ITEM_SET_SCROLL_OFFSET = 24;
    public static final int  ITEM_MAX = 25; //change it if add item, must
    
    private int m_scroll_offset;
    
    //about note 
    private boolean m_addnote_enable = false;
    private Button m_note_button_ok = null;
    private Button m_note_button_cancel = null;
    private Button m_note_button_delete = null;
    private EditText m_note_edittext = null;
    private int m_add_note_x = 0 ;
    private int m_add_note_y = 0 ;
    
    //PDF image
    public ImageView m_imgView = null;      // image view for PDF page
    private Bitmap m_bitmap =null ;     //PDF page bitmap with note labels

    //bookmark
    private ImageView m_bookmarkImgView = null; // bookmark label
    private Bitmap m_bookmark_icon = null;      // bookmark label bm object
    private static final int BOOKMARK_TYPE_SYS = 0;
    private static final int BOOKMARK_TYPE_LOC = 1;
    
    private static final int NOTE_LABEL_W=100;
    private static final int NOTE_LABEL_H=100;

    private double m_fontscale = 1.0;
    private static final int toc_item_num = 8;
    
//    private LinearLayout[] scalingmenuItem = new LinearLayout[6];
    private TextView[] scalingmenuText = new TextView[6];
    private ImageView[] scalingmenuLine = new ImageView[6];
    private double m_scale = 1.0;
    
    private TextView[] fontsizemenuText = new TextView[6];
    private ImageView[] fontsizemenuLine = new ImageView[6];
        


    private LinearLayout[] touchfuncmenuItem = new LinearLayout[3];
    private TextView[] touchfuncmenuText = new TextView[3];
    private ImageView[] touchfuncmenuLine = new ImageView[3];
    
//    private LinearLayout[] pagingmodemenuItem = new LinearLayout[3];
    private TextView[] pagingmodemenuText = new TextView[3];
    private ImageView[] pagingmodemenuLine = new ImageView[3];
    private ViewGroup pagingmodemenu = null;
    private int m_pagingmode = 0;  //0:whole page, 1: reflow, 2: scrolling
    
    //watch point: time, content
    private long    m_watch_at =0;
    private String  m_watch_for ="";
    
    //private TextView m_current_page_txt = null;   //page infomation   (xxx/xxx)
    //private TextView m_total_pages_txt = null;
    //private View m_button_prev  = null ;   // previous page angle 
    //private View m_button_next  = null ;    //next page angle

    private int m_currentPageNum =1;
    private int m_paging_direction =0;

    private int m_startx=0;
    private int m_starty=0;
    private int m_widthp=600;
    private int m_heightp=676;

    //pointes for selecting view rect
    private int m_prevx=0;
    private int m_prevy=0;
    private int m_nextx=0;
    private int m_nexty=0;

    private long m_time_sel = 0;
    
    private int m_width=600;
    private int m_height=676;
    
    //the min size for scaling 
    private final static int m_width_min = 150;
    private final static int m_height_min = 200;
    
    // flags for scaling the whole page
    private boolean m_size_changed = false; // scaled or moved(for the whole page)
    private boolean m_resize_enable = false; // enable resize
    private boolean m_move_enable = false;  // enable move flag
    private int m_select_moving = 0;

    // key down point 
    private int m_keydown_x = 0;
    private int m_keydown_y = 0;
    
    //popup menu
    private PviPopupWindow m_popmenu = null;     
    //private ImageButton mtv_menuBtn = null;
    private View popmenuView;
    //private HashMap<Object, PviMenuItem> m_menu_item_hashmap;    // hash map (tag, item)

    private boolean m_search_enable = false;    //search state flag
    private String m_search_text = "";          //record text to search

    private Timer m_auto_play_timer = null;     //auto-play timer
    private boolean m_is_playing = false;       //auto-play state flag
    private Handler m_handler_autoplay;     //auto-play handler
    private Runnable m_runnable_next        //auto-play runnable  
        = new Runnable() {  
        public void run() {
            onMenuItemSelectedFunc(ITEM_NEXT_PAGE);//next page
            }  
        };
        
    //toc list
    private ViewGroup m_toclist = null;      // toc list 
    
    private boolean m_toc_on = false;   // if List activity for "table of content" is shown  
    private boolean m_hasTOC = false;   // if current opened book has table of content
    private int m_currentTOCPageNum = 0;
    
    EditText m_inputEditText2 = null;
    
    private final static int PAGE_INPUT_DIALOG_ID=1;
    private final static int AUTO_PLAY_DIALOG_ID=2;
    private final static int OPEN_ERROR_DIALOG_ID=3;
    private final static int FONT_SCALE_DIALOG_ID=4;
    private final static int ADD_ANNOTATION_DIALOG_ID=5;
    private final static String TAG="PDFReadActivity";
    private int m_scroll_offset_totle = 0;
    private Intent m_resume_intent ;
    
    private Handler m_handler_refresh=null;
    private Runnable m_runnable_refresh = new Runnable() {//auto-play runnable              
        public void run() {
            m_imgView.postInvalidate();
        }  
    };
    
    private boolean m_is_cutted = false;    //margin cuted or not
    
    private BroadcastReceiver broadcastRec = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent) 
        {
           if(intent.getAction().equals(Intent.ACTION_UMS_CONNECTED )
        		   ||intent.getAction().equals(Intent.ACTION_UMS_DISCONNECTED)
        		   || intent.getAction().equals(Intent.ACTION_MEDIA_EJECT  )
        		   )//SD卡已经成功挂载
           {
        	//   close_pdf_reader();
        	   Logger.i(TAG,"usb connected/disconnected");
               if(activity_state == activity_state_t.state_resumed) {
                   onMenuItemSelectedFunc(ITEM_BACK);
            	   Logger.i(TAG,"usb connected/disconnected, go back");
               }
           }else if(intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)//各种未挂载状态
                   ||intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)
                   ||intent.getAction().equals(Intent.ACTION_MEDIA_BAD_REMOVAL))
           {
        	  // close_pdf_reader();
        	   Logger.i(TAG,"media connected/disconnected");
               if(activity_state == activity_state_t.state_resumed) {
                   onMenuItemSelectedFunc(ITEM_BACK);
            	   Logger.i(TAG,"media removed, go back");
            	   Toast toast = Toast.makeText(this_Activity, R.string.str_pdf_media_removal, Toast.LENGTH_SHORT);
            	   toast.show();
               }
           }
        }
    };

	/*private class text_change_listener implements TextWatcher {
		private int m_min = 0;
		private int m_max = 1;
		String m_txt = new String("");
		boolean m_initialized = false;
		public void init(int min, int max, EditText obj) {
		//	text_change_listener text_change_listener = new text_change_listener();
			obj.addTextChangedListener(this);
			m_min = min;
			m_max = max;
			m_initialized = true;
		}
		public void afterTextChanged (Editable s) {
			if(m_initialized) {
				String new_txt = s.toString();
				if(new_txt.length() == 0) {
					return;
				}
				if(new_txt.equals(m_txt)) {
					return;
				}
				Integer new_int = null;
				try {
					new_int = Integer.valueOf(new_txt);
				}catch (Throwable e) {
					new_int = null;
				}
				boolean good = false;
				if(new_int != null) {
					if( (new_int >= m_min) && (new_int <= m_max)) {
						good = true;
					}
				}
				if(!good) {
					s.clear();
					s.append(m_txt);//restore old one
				}else {
					m_txt = new_txt;
				}
			}
		}
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		}
	}*/
    private int m_adobe_fault_code = -1;
    private Timer m_timer_resume_show = null;

    
    //added
    public enum activity_state_t {
        state_init, state_created, state_started, state_restarted, state_resumed, state_paused, state_stopped, state_destroyed
        //If is stopped. It still retains all state and member information...
    };    
    activity_state_t activity_state = activity_state_t.state_init;
    
    int m_resumed_times = 0;

    private Dialog view_annotation_dlg = null;
    int m_total_page_count = 0;
    long m_last_click_time = -1;
    long page_navigate_times = 0;
    boolean open_procedure_in_progress = false;
    boolean insert_bookmark = false;
    int last_page_height = -1;
    boolean m_first_view_updating = true;
    
    private int skin = 0 ;
    private int m_get_bitmap_failed_times = 0;
    
    Activity this_Activity = this;
    boolean check_too_many_clicks() {
    	//check whether user clicks too frequently
    	if(m_last_click_time == -1) {
    		m_last_click_time = System.currentTimeMillis();
    		return false;
    	}
    	long new_click_time = System.currentTimeMillis();
    	if( new_click_time - m_last_click_time < 900) {
    		//do not assign new_click_time to last_click_time
    		return true;
    	}else {
    		m_last_click_time = new_click_time;
    		return false;
    	}
    }
    void refresh_page() {
    	page_navigate_times++;
    	if(page_navigate_times >= 1) {
    		page_navigate_times = 0;
    		if(((GlobalVar)getApplication()).deviceType ==1){
    			//this.getWindow().getDecorView().getRootView().setUpdateMode(UPDATEMODE_4);
    			//this.getWindow().getDecorView().getRootView().setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
    		}
/*    		EPDRefresh.refreshGCOnceFlash();*/
    		if(((GlobalVar)getApplication()).deviceType ==1){
    			if(skin == 1){
    				//\\this.getWindow().getDecorView().getRootView().postInvalidate(0,0,600, 800, UPDATEMODE_4);
//    				m_imgView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
    			//\\	m_imgView.postInvalidate();
//    				this.getWindow().getDecorView().getRootView().setVisibility(View.INVISIBLE);
    			//	this.getWindow().getDecorView().getRootView().setVisibility(View.VISIBLE);
    			}else{
//    				this.getWindow().getDecorView().getRootView().postInvalidate(0,0,800, 600, UPDATEMODE_4);
  //  				this.getWindow().getDecorView().getRootView().setVisibility(View.INVISIBLE);
    			//	this.getWindow().getDecorView().getRootView().setVisibility(View.VISIBLE);
    			}
    		}
    	}
    }
    
	public Handler m_page_update_handler = new Handler() {
		public void handleMessage(Message msg) {
        	Bundle bundle = msg.getData();
        	if(bundle == null) {
        		return;
        	}
        	if(msg.what == 2) {
        		//indicates an error occurs in render pipeline
        		boolean error_flag = bundle.getBoolean("error_flag");
        		if(error_flag) {
        			Logger.i(TAG,TAG+"::m_page_update_handler , an error indication received");
                    Toast toast = Toast.makeText(this_Activity, R.string.str_pdf_decode_bitmap_failed, Toast.LENGTH_SHORT);
                    toast.show();
                    insert_bookmark = false;
                    go_back_or_home(1); //go back
        		}
        		return;
        	}
        	if(msg.what != 1) {
        		return;
        	}
        	//msg.what == 2
        	Logger.i(TAG, "receive a posted back bitmap");
        	byte[] bytes = bundle.getByteArray("bitmap");
        	
        	if(bytes == null) {
        		Logger.i(TAG,"m_page_update_handler: bytes of bitmap is null");
                Toast toast = Toast.makeText(this_Activity, R.string.str_pdf_decode_bitmap_failed, Toast.LENGTH_SHORT);
                if(m_get_bitmap_failed_times < 9) {
					m_get_bitmap_failed_times++;
                	try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    reader.getBitmapStuff(0);
                }else {
                	m_get_bitmap_failed_times = 0;
	                toast.show();
	                insert_bookmark = false;
                }
        		return;
        	}
        	m_get_bitmap_failed_times = 0;
        	
        	if(bytes.length == 0) {
        		Logger.i(TAG,"m_page_update_handler: len of bytes of bitmap is 0");
                Toast toast = Toast.makeText(this_Activity, R.string.str_pdf_decode_bitmap_failed, Toast.LENGTH_SHORT);
                toast.show();
                insert_bookmark = false;
        		return;
        	}
        	int page_number = bundle.getInt("pageNum");
        	m_currentPageNum = page_number + 1;
        	
            String page_count_info = new Integer(m_currentPageNum).toString();
            String total_page_count_info = new Integer(m_total_page_count).toString();
            set_page_nbr_info("" + page_count_info,total_page_count_info);
            Logger.i(TAG,"m_page_update_handler: set  page info. current page num is" + m_currentPageNum);
//            m_page_infoA.setText("" + m_total_page_count);//modify
        	
        	Bitmap new_bmp = null;
	        try
	        {
	            new_bmp = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
	        }catch(OutOfMemoryError e) {
	        	Logger.i(TAG,"m_page_update_handler: decodeByteArray failed due to OutOfMemory");
	        	Logger.e(TAG,e.toString());
	        	e.printStackTrace();
	        }catch (Exception e) {
	        	new_bmp = null;
	        	Logger.e(TAG,e.toString()); 
	        	e.printStackTrace();
	        }catch(Error e) {
	        	new_bmp = null;
	        	Logger.e(TAG,e.toString());
	        	e.printStackTrace();
	        }
	        if(new_bmp != null) {
	        	Bitmap old_bmp = m_bitmap;
	        	m_bitmap = new_bmp;
	        	
	            addAllNoteLabel();
	            hideOrShowBookmarkLabel();
	        	
	        	updateImage(m_imgView,m_bitmap);
	        	//m_imgView.postInvalidate();
	        	last_page_height = m_bitmap.getHeight();
     	
	        	if(old_bmp !=null ) {
	        		if(!m_bitmap.isRecycled()) {
	        			old_bmp.recycle();
	        		}
	        	} 
	        }else {
	        	Logger.i(TAG,"m_page_update_handler: decodeByteArray failed");
                Toast toast = Toast.makeText(this_Activity, R.string.str_pdf_decode_bitmap_failed, Toast.LENGTH_SHORT);
                toast.show();
                insert_bookmark = false;
	        }
		}
	};
	public void showMessage(String msg){
		final Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_TIP);
		final Bundle sndBundle = new Bundle();
		sndBundle.putString("pviapfStatusTip",msg);
		//sndBundle.putString("pviapfStatusTipTime",
		//        "2000");
		tmpIntent.putExtras(sndBundle);
		sendBroadcast(tmpIntent);
	}
	public void hideMessage(){
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	}
	private void updateImage(ImageView view, Bitmap bitmap) {
		if(m_first_view_updating) {
			m_first_view_updating = false;
			m_imgView.requestFocus();
		}
		if(bitmap != null) {
			//\\if(m_first_view_updating) {
			//\\	m_first_view_updating = false;
				
				hideMessage();//hide hint of loading data			
			//\\}
		}
	//	view.setVisibility(View.INVISIBLE);
	//	this.getWindow().getDecorView().getRootView().setVisibility(View.INVISIBLE);
		refresh_page();
		view.setImageBitmap(bitmap);
	//	view.setVisibility(View.VISIBLE);
		//EPDRefresh.refreshGCOnceFlash();
//		if(bitmap != null) {
		//	refresh_page(); //if bitmap is null, refresh a blank page
//		}
	}
	//@Override
	public void onConfigurationChanged_not_used(Configuration newConfig) {
/*    		if(fullScreem == 2){ setCloseFullScreem();fullScreem = 2 ;}*/
		skin = newConfig.orientation ;
/*		if(skin == Configuration.ORIENTATION_LANDSCAPE){
			setContentView(R.layout.txtfilebrowser_orientation);
		}else{
			setContentView(R.layout.txtfilebrowser_end);
		}
		bindEvent();
		contentPageView.setTextSize(codeSize);
		contentPageView.setLineSpacing(lineSpacingf, lineMult);
		contentPageView.requestFocus();*/
		if(((GlobalVar)getApplication()).deviceType == 1 ){
			if(skin == Configuration.ORIENTATION_LANDSCAPE){
//				this.getWindow().getDecorView().getRootView().postInvalidate(0,0,800,600,UPDATEMODE_4);
			}else{
//				this.getWindow().getDecorView().getRootView().postInvalidate(0, 0, 600, 800, UPDATEMODE_4);
			}
		}
//    		if(fullScreem == 2){ setOpenFullScreem();}
		super.onConfigurationChanged(newConfig);
	}
	public void post_invalidate_full_screen() {
		if(((GlobalVar)getApplication()).deviceType == 1 ){
			if(skin == Configuration.ORIENTATION_LANDSCAPE){
//				this.getWindow().getDecorView().getRootView().postInvalidate(0,0,800,600,UPDATEMODE_4);
			}else{
//				this.getWindow().getDecorView().getRootView().postInvalidate(0, 0, 600, 800, UPDATEMODE_4);
			}
		}
	}
    public void set_page_nbr_info(String cur_page, String total_page) {
        //m_current_page_txt.setText(cur_page);
        //m_total_pages_txt.setText(total_page);
    	updatePagerinfo(cur_page+" / "+total_page);

    }
	
	
	private int m_bmp_width= 0;
	private int m_bmp_height=0;
	private void checkOrientionAndSetViewPort()
	{
        if(m_pagingmode==1 ||(m_pagingmode==2)) //reflow mode
        {
            //m_imgView.setScaleType(ScaleType.FIT_XY);
            if (skin == Configuration.ORIENTATION_LANDSCAPE)
                setViewPortSize(800, 490);
            else
                setViewPortSize(600, 676);
            jumpShow(m_currentPageNum-1);
        }
        else if (m_pagingmode==2) //scrolling
        {
            //m_imgView.setScaleType(ScaleType.CENTER_CROP);
            setViewPortSize(600, 676);
        }else
        {
        	//m_imgView.setScaleType(ScaleType.CENTER_INSIDE);
        	setViewPortSize(600, 676);
        }
	}
	private void setViewPortSize(int w, int h)
	{
		if (w==m_bmp_width&&h==m_bmp_height)
		{
			;
		}
		else
		{
			m_bmp_width =w;
			m_bmp_height=h;
			reader.setViewPortSize(w,h);
		}
	
	}
	
    private int get_scroll_page_height()
    {
    	if (skin == Configuration.ORIENTATION_LANDSCAPE)
    	{
    		return 450;
    	}
    	else
    	{
    		return 650;
    	}
    }
 
}
