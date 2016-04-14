package com.pvi.ap.reader.activity;

import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ResultReceiver;
import android.text.Html;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.external.meb.Chapters;
import com.pvi.ap.reader.external.meb.MebInterfaceImpl;
import com.pvi.ap.reader.external.txt.AddNote;
import com.pvi.ap.reader.external.txt.CommentInfo;
import com.pvi.ap.reader.external.txt.ReadSetView;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;
import com.pvi.ap.reader.data.content.CommentsInfo;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;

/*
 * 打开文件的方法
 * @author rd037
 */
public class MebViewFileActivity extends PviActivity  implements PviBottomBar.Chapable , Pageable  {
    private final static String TAG="MebViewFileActivity";
    private String filenameS;//某章所有内容
    private String certPath;//证书路径
    private  float m_size=20 ;//字体大小
    private int chapterIdLength;//当前一章的长度
    private int page=1;//页码 默认是1
    private int staPostion = 0;//偏移量 默认为0
    private String ChapterId;//章节ID
    private String filePath;//文件路径
    private String author;//作者
    private String downloadType = "";// 请参照downloadType的说明文档
    private Handler mHandler = new H();
    public static final int PD_SUMMARY = 101;//提示框：打开摘要页
    public static final int PD_DOWNLOADALL = 102;//提示框：是否下载全部章节
    private HashMap<String,String> bookinfo = null;//保存书籍信息
    private List<CommentInfo> commentList = null;
    private String StartPosition;//批注开始位置
//    private String EndPosition;//批注结束位置
    private String Comment=null;//评论内容
    private int sta = 0;//当前页所点击获得的初始偏移量
    private int end = 0;//当前页所拖动获得的最后偏移量
    private int jumpPages;//在用户跳章节接收的参数
    private float lineSpac=5;//保存行间距
    private List<Integer> total_pages;
    private String conID=null;//内容ID
    private int num=0;
    private List<Chapters>  fileList;
    private CharSequence charSequence;
    private Chapters chapters;
    private MebInterfaceImpl mifImpl;
    private Dialog dialogs;
   // private PopupWindow popmenu = null;
    //private View popmenuView;
    private TextView mebView;
    private TextView freeView;
    private TextView chapterView;
    private String lookNext;
    private LinearLayout    serach;
    private RelativeLayout  mebmenu,serach2;
    private Handler handle = null;
  //  private boolean addannotation = false ;//开启批注则关闭自动翻页
    private int addnote=3;//2 开启便笺，1 关闭便笺
    private boolean autopage=false;//触屏翻页开关变量
    private static boolean isSerach=false;//是否处于查找状态
    private Thread thread=null;
    Spanned spanned;
    private PageTextView contentPageView ;
    Bundle bunde;
    //  SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");   
    private int skin = 0 ;
    private int skinID=0;
    private int type=0;//设置自动翻页时间
  //  private boolean local_file_reading = false;
    PviBottomBar  m_pbb;
    
    static int total_activity_num = 0;
    static int next_activity_id = 1;
    int m_activity_id = 0;
    //MebViewFileActivity old_one = null;
    boolean open_failed = false;
    int m_deviceType = 2;
    private  long m_last_toast_time = -1;
    void show_toast(String str) {
        if(m_last_toast_time == -1) {
        	m_last_toast_time = System.currentTimeMillis();
        	Toast.makeText(MebViewFileActivity.this,str,Toast.LENGTH_LONG ).show();
            return ;
        }
        long new_click_time = System.currentTimeMillis();
        if( new_click_time - m_last_toast_time < 3000) {
            //do not assign new time to last time
            return;
        }else {
        	m_last_toast_time = new_click_time;
            Toast.makeText(MebViewFileActivity.this,str,Toast.LENGTH_LONG ).show();
            return;
        }
    }
    private  long m_last_click_time = -1;
    public  boolean check_too_many_clicks() {
        //check whether user clicks too frequently
        if(m_last_click_time == -1) {
            m_last_click_time = System.currentTimeMillis();
            Logger.v("m_last_click_time1", "m_last_click_time="+m_last_click_time);
            return false;
        }
        long new_click_time = System.currentTimeMillis();
        if( new_click_time - m_last_click_time < 1000) {
            //do not assign new_click_time to last_click_time
            Logger.v("m_last_click_time2", "m_last_click_time="+m_last_click_time);
            return true;
        }else {
            m_last_click_time = new_click_time;
            Logger.v("m_last_click_time3", "m_last_click_time="+m_last_click_time);
            return false;
        }
    }
    @Override
    public OnUiItemClickListener getMenuclick() {
        // TODO Auto-generated method stub
        return this.menuclick;
    }

    @Override
	public
    void OnPrevchap() {
        if (popmenu != null && popmenu.isShowing()) {
            popmenu.dismiss();
        }
        upChapter(Integer.parseInt(ChapterId)-1);
    }
    @Override
	public
    void OnNextchap() {
        if (popmenu != null && popmenu.isShowing()) {
            popmenu.dismiss();
        }
        nextChapter(Integer.parseInt(ChapterId)+1);
    }
	@Override
    public void OnNextpage() {
        if (popmenu != null && popmenu.isShowing()) {
            popmenu.dismiss();
        }
        page=page+1;
        goToNextPage(page);
    }
    @Override
    public void OnPrevpage() {
        if (popmenu != null && popmenu.isShowing()) {
            popmenu.dismiss();
        }
        goToUpPage(page);
    }    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            m_activity_id = next_activity_id;
            next_activity_id++;
            total_activity_num++;
            Logger.i(TAG,"++++++++++++++++++++++++++++++++onCreate ,activity id: " + m_activity_id + ". total MebViewFileActivity num(include this one): " +  total_activity_num );
            
        //  if(old_one != null) {
            //  old_one.finishActivity(0);
        //  }
        //  old_one = this;
            
            if(check_too_many_clicks()){
                return ;
            }   
            FullDisplay();
            GlobalVar appState = (GlobalVar)getApplicationContext();
            m_deviceType = appState.deviceType; 
            //skin = appState.getSkinID();
    		Configuration newConfig = this.getResources().getConfiguration();
    		skin = newConfig.orientation ;

            switch(skin){
            //case 2:skinID = R.layout.filebrowser ;break ;
            case 1:skinID = R.layout.filebrowser2  ;break ; 
            default:skinID = R.layout.filebrowser2 ;break ;
            }
            setContentView(skinID);
            super.onCreate(savedInstanceState);
            bunde = this.getIntent().getExtras();
            filePath=bunde.getString("FilePath");
            certPath=bunde.getString("CertPath");
            ChapterId=bunde.getString("ChapterId");
            conID=bunde.getString("ContentId");
            author=bunde.getString("authorName");
            downloadType=bunde.getString("downloadType");
            
            Logger.d(TAG,"downloadType:"+downloadType);

            if(filePath.contains(Constants.ms_MyDocPath + "MyDoc/") ||
                    filePath.contains(Constants.ms_MyDocPath + "localbook/")    ) {
                Logger.v(TAG,"read a local book");
              //  local_file_reading = true;
                this.setOnPmShow(new OnPmShowListener(){

                    @Override
                    public void OnPmShow(PviPopupWindow popmenu_v) {
                        // TODO Auto-generated method stub
                        if( getMenuItem("inactive")!= null ) {
                            getMenuItem("inactive").isVisible=false;
                        }
                        if( getMenuItem("bookSummary") != null ) {
                            getMenuItem("bookSummary").isVisible=false;
                        }
                        Logger.i(TAG,"pop menu show");
                    }});
            }

            Logger.v("filePath", filePath);
            Logger.v("certPath", certPath);
            Logger.v("ChapterId", ChapterId);
            Logger.v("conID", conID);
            // Logger.v("author",author);
            if(skinID == R.layout.filebrowser2){
                serach2=(RelativeLayout) findViewById(R.id.serachs);
            }else {
                mebmenu=(RelativeLayout) findViewById(R.id.mebmenus);
                serach= (LinearLayout) findViewById(R.id.serachs);
            }   
            mebView=(TextView) findViewById(R.id.mebnames);
            contentPageView=(PageTextView) findViewById(R.id.view_contents);
            if(appState.deviceType==1){
//                contentPageView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
            }
            contentPageView.requestFocus();
            freeView=(TextView) findViewById(R.id.free);
            chapterView=(TextView) findViewById(R.id.chapterName);
 
            if(ChapterId==null) {
            	ChapterId="1";
            }
            if(ChapterId.equals("")){
                ChapterId="1";
            }
            if(bunde.getString("FontSize")!=null){
                m_size=Float.parseFloat(bunde.getString("FontSize"));
                contentPageView.setTextSize(m_size);
            }
            if(bunde.getString("LineSpace")!=null){
                lineSpac=Float.parseFloat(bunde.getString("LineSpace"));
                contentPageView.setLineSpacing(lineSpac, 1);
            }
            mifImpl=new MebInterfaceImpl();
            fileList=mifImpl.openMebFile(filePath);//取得目录集合

            chapters=fileList.get(Integer.parseInt(ChapterId)-1);

            String flag=chapters.getFlag();
            
            if(flag.equals("0")){
                ChapterId=String.valueOf(Integer.parseInt(ChapterId)+1);
                chapters=fileList.get(Integer.parseInt(ChapterId)-1);
            }
            Logger.d(TAG,"billingflag:"+chapters.getBillingflag());
            if(chapters.getBillingflag()!=1){

                filenameS=mifImpl.getFileContent(filePath, chapters.getStartFile(),chapters.getFileLength());

            }else{

                filenameS=mifImpl.getFileFlagContent(filePath, chapters.getStartFile(), chapters.getFileLength(), certPath,appState.getRegCode(),appState.getUserID(), appState.getInnerPassword());

            }

            //Logger.v("filenameS", filenameS);
            spanned=Html.fromHtml(filenameS);
            chapterIdLength=spanned.length();
	        m_pbb = appState.pbb;
//			m_pbb.setPageable(this);
//			m_pbb.setChapable(this);
//			m_pbb.setItemVisible("prevpage", true);
//			m_pbb.setItemVisible("pagerinfo", true);
//			m_pbb.setItemVisible("nextpage", true);
//			m_pbb.setItemVisible("prevchap",true);
//			m_pbb.setItemVisible("nextchap",true);
	        this.showPager = true;
	        this.showChaper = true;
	        
            moveFile();
            meb_bindEvent();            
            handle = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    page=page+1;
                    goToNextPage(page); 
                }
            };
            freeChapter();

            Intent tmpIntent = new Intent(
                    MainpageActivity.SET_TITLE);
            Bundle sndbundle = new Bundle();
            sndbundle.putString("title", chapters.getMebName()+"[>>]"+chapters.getFileUrl());
            tmpIntent.putExtras(sndbundle);
            sendBroadcast(tmpIntent);

        } catch (NullPointerException e) {
            // TODO: handle exception
            Logger.e("meb1",e.toString());
            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.FailException), Toast.LENGTH_LONG).show();
            if(thread != null ){
                thread.interrupt();
                thread = null ;
            }
         //   Intent intent1 = new Intent(
         //           MainpageActivity.BACK);
        //  sendBroadcast(intent1);
            open_failed = true;
            sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
            return ;
        }catch (Throwable e) {
            // TODO: handle exception
            Logger.e("meb2",e.toString());
            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.FailException), Toast.LENGTH_LONG).show();
            if(thread != null ){
                thread.interrupt();
                thread = null ;
            }
         //   Intent intent1 = new Intent(
         //           MainpageActivity.BACK);
        //  sendBroadcast(intent1);
            open_failed = true;
            sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
            return ;
        }
    }
    public void page_finished() {
        //
        try {
            if(spanned==null){
                //Logger.v("spanned", "spanned="+spanned);
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
            }
            PageTextView pageView = (PageTextView)contentPageView;
            total_pages = pageView.pages;
            getMyannotatio(filePath,ChapterId);
            if(bunde.getString("FromPath").equals("1")){//书签
                staPostion=Integer.parseInt(bunde.getString("Offset"));
                for(int i=total_pages.size()-1;i>=0;i--){
                    if(staPostion>=total_pages.get(i)){
                        page=i+1;
                        break;
                    }
                }
                if(page==total_pages.size()){
                    charSequence=spanned.subSequence(total_pages.get(page-1), chapterIdLength-1);
                }else {
                    charSequence=spanned.subSequence(total_pages.get(page-1),total_pages.get(page));
                }
            }else if(bunde.getString("FromPath").equals("2")){//批注
                StartPosition=bunde.getString(CommentsInfo.StartPosition);
   //             EndPosition=bunde.getString(CommentsInfo.EndPosition);
                Comment=bunde.getString(CommentsInfo.Comment);
                for(int i=total_pages.size()-1;i>=0;i--){
                    if(Integer.parseInt(StartPosition)>=total_pages.get(i)){
                        page=i+1;
                        break;
                    }
                }
                if(page==total_pages.size()){
                    charSequence=spanned.subSequence(total_pages.get(page-1), chapterIdLength-1);

                }else {
                    charSequence=spanned.subSequence(total_pages.get(page-1),total_pages.get(page));
                }

            }else {
                if(total_pages.size()==1){
                    charSequence=spanned;
                }else {
                    charSequence=spanned.subSequence(0, total_pages.get(1));
                }

            }
            contentPageView.setText(getUnderLine(charSequence));
            updatePagerinfo(page+"/"+total_pages.size());
            sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));

        } catch (NullPointerException e) {
            // TODO: handle exception
            Logger.e("meb3", e.toString());
            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.FailException), Toast.LENGTH_LONG).show();
            if(thread != null ){
                thread.interrupt();
                thread = null ;
            }
            Intent intent1 = new Intent(
                    MainpageActivity.BACK);
            sendBroadcast(intent1);
            return ;
        }catch (Exception e) {
            // TODO: handle exception
            Logger.e("meb4", e.toString());
            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
            if(thread != null ){
                thread.interrupt();
                thread = null ;
            }
            Intent intent1 = new Intent(
                    MainpageActivity.BACK);
            sendBroadcast(intent1);
            return ;
        }

    }

    public void meb_bindEvent(){        

        //TextView txMenu=(TextView) findViewById(R.id.createmenus);
        if(skinID == R.layout.filebrowser2){
        }else {
        }

        final Button b_serach = (Button) findViewById(R.id.find);
        Button b_Close = (Button) findViewById(R.id.close);
        final EditText look=(EditText)findViewById(R.id.serach);
        final ResultReceiver input_result_receiver = new ResultReceiver(new Handler()) {
        	protected void onReceiveResult(int resultCode, Bundle resultData) {
        		switch(resultCode) {
        		case InputMethodManager.RESULT_HIDDEN:
					post_invalidate_full_screen();
        			break;
        		case InputMethodManager.RESULT_SHOWN:
					post_invalidate_full_screen();
        			break;
        		default:
        			break;
        		}
        	}
        };
        look.setOnTouchListener(new OnTouchListener() { 
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
//                IBinder b = ServiceManager.getService(Context.INPUT_METHOD_SERVICE);
//                IInputMethodManager  = IInputMethodManager.Stub.asInterface(b);
//
//            	boolean old_shown = imm.isInputViewShown();

            	v.onTouchEvent(event);
            	
//            	boolean new_shown = imm.isInputViewShown();
//            	if(old_shown != new_shown ) {
//            		post_invalidate_full_screen();
//            		Logger.i(TAG,"search edit text position changed. refresh view");
//            	}
            	return true;
            }
        });
        look.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(arg0, 0,input_result_receiver);
				}else{
					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(look.getWindowToken (),0,input_result_receiver);
				}
			}
			
		});

        //      txMenu.setOnClickListener(new android.view.View.OnClickListener(){
        //          
        //          public void onClick(View v) {
        //              // TODO Auto-generated method stub
        //              menupan();
        //          }
        //      });


        b_serach.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method st    
                if(look.getText()==null||look.getText().toString().equals("")){
                    Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.SerchComtent),Toast.LENGTH_LONG ).show();
                    return;
                }
                String lookup=look.getText().toString();
                int lookNum;
                autopage=true;
                SpannableStringBuilder style = null ;
                List<Integer> finds=getFinds(lookup, 0);
                if(finds.size()==0){
                    Toast.makeText(MebViewFileActivity.this,getResources().getString(R.string.NoRelashipComent),Toast.LENGTH_LONG ).show();
                    return;
                }else if(b_serach.getText().equals(getResources().getString(R.string.Serach))){
                    lookNum=finds.get(num);
                    if(total_pages.size()==1){
                        charSequence=spanned;
                        page=1;
                    }else if(lookNum<total_pages.get(total_pages.size()-1)){
                        for(int i=0;i<total_pages.size()-1;i++){
                            if(lookNum<total_pages.get(i+1)){
                                page=i+1;
                                break;
                            }
                        }
                        charSequence=spanned.subSequence(total_pages.get(page-1), total_pages.get(page));
                    }else {
                        page=total_pages.size();
                        charSequence=spanned.subSequence(total_pages.get(page-1), chapterIdLength-1);
                    }

                    style=new SpannableStringBuilder(charSequence);
                    if(lookNum-total_pages.get(page-1)==0){
                        style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                        style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }else if(page<total_pages.size()&&(lookNum<total_pages.get(page)&&lookNum+lookup.length()>total_pages.get(page))){
                        style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                        style.setSpan(new URLSpan(charSequence.toString()), lookNum, total_pages.get(page), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else {
                        style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-1-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                        style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    num++;
                    lookNext=lookup;
                    b_serach.setText(R.string.Next);
                    updatePagerinfo(page+"/"+total_pages.size());
                    contentPageView.setText(style);
                }else {
                    lookNext=lookup;
                    Logger.v("lookup", lookup);
                    System.out.println("lookNext="+lookNext);
                    Logger.v("lookNext", lookNext);

                    if(lookNext.equals(lookup)){
                        if(num<finds.size()){
                            lookNum=finds.get(num);
                            if(total_pages.size()==1){
                                charSequence=spanned;
                                page=1;
                            }else if(lookNum<total_pages.get(total_pages.size()-1)){
                                for(int i=0;i<total_pages.size()-1;i++){
                                    if(lookNum<total_pages.get(i+1)){
                                        page=i+1;
                                        break;
                                    }
                                }
                                charSequence=spanned.subSequence(total_pages.get(page-1), total_pages.get(page));
                            }else {
                                page=total_pages.size();
                                charSequence=spanned.subSequence(total_pages.get(page-1), chapterIdLength-1);
                            }

                            style=new SpannableStringBuilder(charSequence);
                            if(lookNum-total_pages.get(page-1)==0){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else    if(page<total_pages.size()&&(lookNum<total_pages.get(page)&&lookNum+lookup.length()>total_pages.get(page))){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum, total_pages.get(page), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else {
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-1-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            num++;
                            b_serach.setText(R.string.Next);
                            updatePagerinfo(page+"/"+total_pages.size());
                            contentPageView.setText(style); 
                        }else{
                            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.LastOne),Toast.LENGTH_LONG ).show();
                            style=new SpannableStringBuilder(charSequence);
                            lookNum=finds.get(finds.size()-1);//last one
                            if(lookNum < total_pages.get(page-1)) {
                                //not found in current page
                                return;
                            }
                            if(lookNum-total_pages.get(page-1)==0){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else  if(page<total_pages.size()&&(lookNum<total_pages.get(page)&&lookNum+lookup.length()>total_pages.get(page))){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum, total_pages.get(page), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else{
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-1-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            contentPageView.setText(style);
                        }
                    }else{
                        finds=getFinds(lookup, 0);
                        num=0;
                        if(finds.size()==0){
                            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.NoRelashipComent),Toast.LENGTH_LONG ).show();
                            return;
                        }else if(num<finds.size()){
                            lookNum=finds.get(num);
                            if(total_pages.size()==1){
                                charSequence=spanned;
                                page=1;
                            }else if(lookNum<total_pages.get(total_pages.size()-1)){
                                for(int i=0;i<total_pages.size()-1;i++){
                                    if(lookNum<total_pages.get(i+1)){
                                        page=i+1;
                                        break;
                                    }
                                }
                                charSequence=spanned.subSequence(total_pages.get(page-1), total_pages.get(page));
                            }else {
                                page=total_pages.size();
                                charSequence=spanned.subSequence(total_pages.get(page-1), chapterIdLength-1);
                            }
                            lookNext=lookup;
                            style=new SpannableStringBuilder(charSequence);
                            if(lookNum-total_pages.get(page-1)==0){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else  if(page<total_pages.size()&&(lookNum<total_pages.get(page)&&lookNum+lookup.length()>total_pages.get(page))){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum, total_pages.get(page), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else {
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-1-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            num++;
                            updatePagerinfo(page+"/"+total_pages.size());
                            contentPageView.setText(style); 
                        }else {
                            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.LastOne),Toast.LENGTH_LONG ).show();
                            style=new SpannableStringBuilder(charSequence);
                            lookNum=finds.get(finds.size()-1);//last one
                            if(lookNum-total_pages.get(page-1)==0){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else  if(page<total_pages.size()&&(lookNum<total_pages.get(page)&&lookNum+lookup.length()>total_pages.get(page))){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum, total_pages.get(page), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }else{
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0,lookNum-1-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(charSequence.toString()), lookNum-total_pages.get(page-1), lookNum-total_pages.get(page-1)+lookup.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            contentPageView.setText(style);
                        }
                    }
                }

            }

        });
        b_Close.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PviUiUtil.hideInput(v);
                
                autopage=false;
                isSerach=false;
                if(skinID == 999/*R.layout.filebrowser*/){
                    mebmenu.setVisibility(View.VISIBLE);
                    serach.setVisibility(View.INVISIBLE);
                }else {
                    serach2.setVisibility(View.INVISIBLE);
                }

                b_serach.setText(R.string.Serach);
                num=0;
                look.setText("");
                contentPageView.setText(getUnderLine(charSequence));
                updatePagerinfo(page+"/"+total_pages.size());

            }

        });
        contentPageView.setOnKeyListener(onKeyListener);
       //\\ super.bindEvent();
    }

    private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id; 
            if (vTag.equals("jumpChapter")) {
                //跳转章节
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                //showDialog(2);
                Dialog dlg = buildDialogChapter(MebViewFileActivity.this);
                dlg.show();
            }else if (vTag.equals("addBookMark")) {
                //for test skin switch
                if(false) {
                int id = 1;
                int view_width = contentPageView.getWidth();
                int view_height = contentPageView.getHeight();
                if(view_height > view_width) {
                    id = 2;
                }else {
                    id = 1;
                }
                switch_skin(id);
                }else {
                //end for test
                //添加书签
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                addBookMark(total_pages.get(page-1));
                }
            }else if(vTag.equals("bookSummary")){
                Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("pviapfStatusTip", "进入书籍摘要");
                msgIntent.putExtras(sndbundle);
                sendBroadcast(msgIntent);
                Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle bundleToSend = new Bundle();
                bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity"); 
                bundleToSend.putString("contentID", conID);
                bundleToSend.putString("startType",  "allwaysCreate");
                bundleToSend.putString("pviapfStatusTip",  getResources().getString(R.string.booksubscripLoad));
                bundleToSend.putString("pviapfStatusTipTime",  "5000");
                tmpIntent.putExtras(bundleToSend);
                sendBroadcast(tmpIntent);
            }
            else if (vTag.equals("bookMarkList")) {
                //我的书签MyBookMarkActivity
                Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("pviapfStatusTip", "进入我的书签");
                msgIntent.putExtras(sndbundle);
                sendBroadcast(msgIntent);
                Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                bunde.putString("act",  "com.pvi.ap.reader.activity.MyBookshelfActivity");
                bunde.putString("haveTitleBar","1");
                bunde.putString("startType",  "allwaysCreate");
                if(filePath.contains("/sdcard/localbook/book")){
                    bunde.putString("SourceType","1");
                }else if(filePath.contains("/sdcard/download/book")){
                    bunde.putString("SourceType","2");
                }else {
                    bunde.putString("SourceType","3");
                }
                bunde.putString("FilePath", filePath);
                bunde.putString("actTabName",  "我的书签");  //跳转到我的书签 ，如果去掉这语句，就会跳到 最近阅读   
                intent.putExtras(bunde);
                sendBroadcast(intent);
            }else if (vTag.equals("returncontents")) {
                //目录页
                /*Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("pviapfStatusTip", "进入书籍目录");
                msgIntent.putExtras(sndbundle);
                sendBroadcast(msgIntent);*/
                Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("actID","ACT11180");
                //bunde.putString("act","com.pvi.ap.reader.activity.ListFileActivity");
                //bunde.putString("haveStatusBar","1");
                //bunde.putString("startType",  "allwaysCreate");
                sndbundle.putString("pviapfStatusTip", getResources().getString(R.string.waitting));
                sndbundle.putString("FilePath",filePath);
                sndbundle.putString("CertPath", certPath);
                sndbundle.putString("ContentId", conID);
                Logger.d(TAG,"put downloadType:"+downloadType);
                sndbundle.putString("downloadType", downloadType);
                intent.putExtras(sndbundle);
                sendBroadcast(intent);
            }else if (vTag.equals("returnpic")) {
                //返回封面
                Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("pviapfStatusTip", "进入书籍封面");
                msgIntent.putExtras(sndbundle);
                sendBroadcast(msgIntent);
                Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndbundle1 = new Bundle();
                sndbundle1.putString("act","com.pvi.ap.reader.activity.MebPicActivity");
                sndbundle1.putString("haveStatusBar","0");
                sndbundle1.putString("startType",  "allwaysCreate");
                sndbundle1.putString("haveTitleBar","0");
                sndbundle1.putString("haveBottomBar","0");
                sndbundle1.putString("FilePath",filePath);
                sndbundle1.putString("CertPath", certPath);
                sndbundle1.putString("ContentId", conID);
                intent.putExtras(sndbundle1);
                sendBroadcast(intent);
            }else if (vTag.equals("flowers")) {
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                //送鲜花
                if(VoteFlower())
                {
                    // 提示添加成功
                    showResult(getResources().getString(R.string.Flowers), getResources().getString(R.string.FlowersSucess));
                }
                else
                {
                    // 提示添加失败
                    showResult(getResources().getString(R.string.Flowers), getResources().getString(R.string.FlowersFailure));
                }

            }else if (vTag.equals("sendcomments")) {

                //发表评论

                Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle bundleToSend = new Bundle();
                bundleToSend.putString("contentID", conID);
                bundleToSend.putString("act",
                "com.pvi.ap.reader.activity.NewCommentActivity");
                bunde.putString("haveStatusBar","1");
                tmpIntent.putExtras(bundleToSend);
                sendBroadcast(tmpIntent);
            }else if (vTag.equals("comments")) {
                Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("pviapfStatusTip", "进入书籍评论");
                msgIntent.putExtras(sndbundle);
                sendBroadcast(msgIntent);
                Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle = new Bundle();
                sndBundle.putString("act",
                "com.pvi.ap.reader.activity.CommentsListActivity");

                sndBundle.putString("contentID", conID);
                intent.putExtras(sndBundle);
                sendBroadcast(intent);
            }else if (vTag.equals("friends")) {
                //推荐给好友
                Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("pviapfStatusTip", "进入好友列表");
                msgIntent.putExtras(sndbundle);
                sendBroadcast(msgIntent);
                Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                bunde.putString("act","com.pvi.ap.reader.activity.RecommendToFriend");
                bunde.putString("contentID",conID);
                bunde.putString("chapterID",ChapterId);
                bunde.putString("startType",  "allwaysCreate");
                bunde.putBoolean("RequestMsisdn", true);
                intent.putExtras(bunde);
                sendBroadcast(intent);
            }else if (vTag.equals("serach")) {
                //内容搜索
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                isSerach=true;
                if(skinID == 999/*R.layout.filebrowser*/){
                    //mebmenu.setVisibility(View.INVISIBLE);
                    serach.requestFocus();
                    serach.setVisibility(View.VISIBLE);
                }else {

                    //mebmenu2.setVisibility(View.INVISIBLE);
                    Logger.v("mebmenu2", "success");
                    serach2.requestFocus();
                    serach2.setVisibility(View.VISIBLE);
                }
            }else if (vTag.equals("typefaces")) {
                //showDialog(3);
            	Dialog dlg = buildDialogTypefaces(MebViewFileActivity.this);
            	dlg.show();
            }else if(vTag.equals("autoPager")){
                //showDialog(5);
            	Dialog dlg = buildDialogautoPager(MebViewFileActivity.this);
            	dlg.show();
            }else if(vTag.equals("openNote")){
                addnote = 1;
                
        	    PviMenuItem tv = (PviMenuItem)getMenuItem("autoPager");
                if( tv != null ) {
                    tv.enable=true;
                }
            }else if(vTag.equals("openpage")){
                addnote = 3;
        	    PviMenuItem tv = (PviMenuItem)getMenuItem("autoPager");
                if( tv != null ) {
                    tv.enable=true;
                }
            }else if(vTag.equals("myAnnotation")){
                Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("pviapfStatusTip", "进入我的批注");
                msgIntent.putExtras(sndbundle);
                sendBroadcast(msgIntent);
                Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                bunde.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
                bunde.putString("haveTitleBar","1");
                bunde.putString("startType",  "allwaysCreate"); 
                Logger.v("mebName", chapters.getMebName());
                bunde.putString("bookName", chapters.getMebName());
                bunde.putString("actTabName",  "我的批注");  //跳转到我的书签 ，如果去掉这语句，就会跳到 最近阅读   
                intent.putExtras(bunde);
                sendBroadcast(intent);

            }else
                if (vTag.equals("postil")) {
                    if(thread != null ){
                        thread.interrupt();
                        thread = null ;
                    }
                    addnote  = 2 ;
            	    PviMenuItem tv = (PviMenuItem)getMenuItem("autoPager");
                    if( tv != null ) {
                        tv.enable=false;
                    }
                }  
            closePopmenu();
        
        }

        };




        //    /**
        //   * 关闭菜单
        //   */
        //  public void closePopmenu(){
        //      if(popmenu!=null){
        //          popmenu.dismiss();
        //      }
        //      if(subpopmenu!=null)
        //      {
        //          subpopmenu.dismiss();
        //      }
        //  }
        //    public void menupan() {
        //        GlobalVar appState = ((GlobalVar) getApplicationContext());
        //        if (popmenu == null) {
        ////            popmenuView = appState.getMenu(this);
        ////          
        ////            setMenuListener(popmenuView);
        ////            popmenu = new PopupWindow(popmenuView, LayoutParams.WRAP_CONTENT,
        ////                    LayoutParams.WRAP_CONTENT);
        ////            ;
        //            popmenuView = appState.getMenu(this);
        //
        //          popmenu = new PopupWindow(popmenuView, LayoutParams.WRAP_CONTENT,
        //                  LayoutParams.WRAP_CONTENT);         
        //          setMenuListener(popmenuView);
        //          popmenu.setBackgroundDrawable(new BitmapDrawable());
        //          popmenu.setFocusable(true);
        //
        //          popmenuView = null;
        //        }
        //        if (popmenu != null) {
        //            if (popmenu.isShowing()) {
        //                popmenu.dismiss();
        //            } else {
        //               if(conID==null||conID.equals("")){
        //                      popmenuView.findViewWithTag("flowers").setVisibility(View.GONE);
        //                      popmenuView.findViewWithTag("sendcomments").setVisibility(View.GONE);
        //                      popmenuView.findViewWithTag("friends").setVisibility(View.GONE);
        //                      popmenuView.findViewWithTag("comments").setVisibility(View.GONE);
        //                    }
        //                popmenu.showAtLocation(findViewById(R.id.view_sv),
        //                        Gravity.BOTTOM | Gravity.LEFT, 0, 46); 
        //            }
        //        }
        //    }

        public void FullDisplay(){
            requestWindowFeature(Window.FEATURE_NO_TITLE);   
            getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,   
                    WindowManager.LayoutParams. FLAG_FULLSCREEN);   

        }

        //点击Menu按钮 会弹出一个对话框---------------------------------------------------
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = this.getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }
        public boolean onOptionsItemSelected(MenuItem item)
        {

            return super.onOptionsItemSelected(item);
        }
        //添加书签
        private void addBookMark(int postion){ 
            String columns[] = new String[]{
                    Bookmark.ChapterId,
                    Bookmark.FilePath,
                    Bookmark.Position,
                    Bookmark.BookmarkType,
                    Bookmark.Author,
                    Bookmark.CreatedDate
            };
            Cursor cur = null;
            String where=Bookmark.ChapterId+"='"+ChapterId+"'" + " and " + Bookmark.FilePath+"='"+filePath+"'"+" and "+Bookmark.Position+"='"+String.valueOf(postion)+"'"+ " and " +Bookmark.BookmarkType+"='1'";
            cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null, null);
            if(cur.getCount()>0){
                //already has book-mark in this page
                Toast.makeText(this, getResources().getString(R.string.BookmarsBeen), Toast.LENGTH_LONG).show();
                cur.close();
            }else{
                cur.close();
                
                where=Bookmark.FilePath+"='"+filePath+"'"+" and " +Bookmark.BookmarkType+"='1'";
                cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null, Bookmark.CreatedDate); 
                if(cur.getCount()>=5){
                    //the number of all bookmarks of the book is greater than 5, delete one 
                    cur.moveToFirst();
                     String min_date = cur.getString(cur.getColumnIndex(Bookmark.CreatedDate) ); 
                     where = Bookmark.FilePath+"='"+filePath+"'"+" and " +Bookmark.BookmarkType+"='1'" + " and " + Bookmark.CreatedDate + "='" + min_date + "'";
                     getContentResolver().delete(Bookmark.CONTENT_URI, where,null);
                }
                cur.close();
                
                ContentValues values = new ContentValues();
                if(filePath.contains("/sdcard/localbook/book")){
                    values.put(Bookmark.SourceType, 1);
                }else if(filePath.contains("/sdcard/download/book")){
                    values.put(Bookmark.SourceType, 2);
                }else if(filePath.contains("/sdcard")){
                    values.put(Bookmark.SourceType, 3);
                }else {
                    values.put(Bookmark.SourceType, 5);
                }
                GlobalVar app = (GlobalVar)getApplicationContext() ;

                values.put(Bookmark.ContentName,fileList.get(Integer.parseInt(ChapterId)-1).getMebName());
                values.put(Bookmark.ChapterName, fileList.get(Integer.parseInt(ChapterId)-1).getFileUrl());
                values.put(Bookmark.ContentId, conID);
                values.put(Bookmark.LineSpace, lineSpac);
                values.put(Bookmark.FontSize, contentPageView.getTextSize());
                values.put(Bookmark.UserID, app.getUserID());
                values.put(Bookmark.BookmarkType, 1);
                values.put(Bookmark.ChapterId,ChapterId);
                values.put(Bookmark.OperationType, 0);
                values.put(Bookmark.Position,postion);
                values.put(Bookmark.SynchFlag, 0);
                values.put(Bookmark.FilePath, filePath);
                values.put(Bookmark.CertPath, certPath);
                values.put(Bookmark.Author, author);
                values.put(Bookmark.CreatedDate, GlobalVar.getFormatTime(System.currentTimeMillis()));
                getContentResolver().insert(Bookmark.CONTENT_URI, values);
                Toast.makeText(this, getResources().getString(R.string.AddBookmarkSucess), Toast.LENGTH_LONG).show();
            }

        }
        //下一页
        private void goToNextPage(int pag){
            long TimeStart = System.currentTimeMillis();
            Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));

            if(pag>total_pages.size()){
                nextChapter(Integer.parseInt(ChapterId)+1);
                return ;
            }else if(pag==total_pages.size()){
                charSequence=spanned.subSequence(total_pages.get(pag-1), chapterIdLength-1);

            }else {
                charSequence=spanned.subSequence(total_pages.get(pag-1),total_pages.get(pag));
            }
            contentPageView.setText(getUnderLine(charSequence));
            updatePagerinfo(pag+"/"+total_pages.size());
            page=pag;
            TimeStart = System.currentTimeMillis();
            Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));
        }
        //上一页
        private void goToUpPage(int pag){

            long TimeStart = System.currentTimeMillis();
            Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));
            pag=pag-1;
            if(pag<1){
                GlobalVar appState = ((GlobalVar) getApplicationContext());
                ChapterId=String.valueOf(Integer.parseInt(ChapterId)-1);
                if(Integer.parseInt(ChapterId)<1||(Integer.parseInt(ChapterId)==1&&fileList.get(0).getFlag().equals("0"))){
                	show_toast(getResources().getString(R.string.txtFirstPage));
                	
                    ChapterId="1";
                    return ;    
                }else {
                    chapters=fileList.get(Integer.parseInt(ChapterId)-1);

                    Intent tmpIntent = new Intent(
                            MainpageActivity.SET_TITLE);
                    Bundle sndbundle = new Bundle();
                    sndbundle.putString("title", chapters.getMebName()+"[>>]"+chapters.getFileUrl());
                    tmpIntent.putExtras(sndbundle);
                    sendBroadcast(tmpIntent);

                    if(chapters.getBillingflag()!=1){
                        filenameS=mifImpl.getFileContent(filePath, chapters.getStartFile(),chapters.getFileLength());
                    }else{
                        filenameS=mifImpl.getFileFlagContent(filePath, chapters.getStartFile(), chapters.getFileLength(), certPath, appState.getRegCode(), appState.getUserID(), appState.getInnerPassword());
                    }
                    spanned=Html.fromHtml(filenameS);
                    chapterIdLength=spanned.length();
                    SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(spanned.toString().trim());
                    PageTextView pageView = (PageTextView)contentPageView;
                    total_pages = pageView.all_pages(span_strBuilder,true,true);
                    getMyannotatio(filePath,ChapterId);
                    page=total_pages.size();
                    charSequence=spanned.subSequence(total_pages.get(page-1),chapterIdLength);
                    contentPageView.setText(getUnderLine(charSequence));
                    updatePagerinfo(page+"/"+total_pages.size());
                    freeChapter();
                    return ;
                }
            }else {
                charSequence=spanned.subSequence(total_pages.get(pag-1),total_pages.get(pag));
            }
            page=pag;
            contentPageView.setText(getUnderLine(charSequence));
            updatePagerinfo(pag+"/"+total_pages.size());
            TimeStart = System.currentTimeMillis();
            Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));
        }
        @Override
        protected Dialog onCreateDialog(int id) {
            // TODO Auto-generated method stub
            switch (id){
//            case 1:
//                return buildDialog(MebViewFileActivity.this);
//            case 2:
//                return buildDialogChapter(MebViewFileActivity.this);
//            case 3:
//                return buildDialogTypefaces(MebViewFileActivity.this);
//            case 5:
//                return buildDialogautoPager(MebViewFileActivity.this);
            }
            return null;
        }



        /**
         * 自动翻页设置
         * @param context
         * @return
         */
        private Dialog buildDialogautoPager(Context context) {

            // TODO Auto-generated method stub
            LayoutInflater inflater=LayoutInflater.from(this);
            final View entryView=inflater.inflate(R.layout.files_2, null);
            PviAlertDialog builder=new PviAlertDialog(context);
            //builder.setTitle(getResources().getString(R.string.InputAutoFlipTime));
            builder.setTitle("请选择自动翻页时间(单位:S)");
            builder.setCanClose(true);
            builder.setView(entryView);
            final Button m_time3=(Button)entryView.findViewById(R.id.select_3);
            final Button m_time5=(Button)entryView.findViewById(R.id.select_5);
            final Button m_time8=(Button)entryView.findViewById(R.id.select_8);
            final Button m_time10=(Button)entryView.findViewById(R.id.select_10);
         //   final TextView time_3=(TextView)entryView.findViewById(R.id.time_3);
         //   final TextView time_5=(TextView)entryView.findViewById(R.id.time_5);
         //   final TextView time_8=(TextView)entryView.findViewById(R.id.time_8);
         //   final TextView time_10=(TextView)entryView.findViewById(R.id.time_10);
            /**
             * 设置自动翻页时间为3s
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
            builder.setButton("设  置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    //              if(((EditText) entryView.findViewById(R.id.view_content)).getText()==null||((EditText) entryView.findViewById(R.id.view_content)).getText().toString().equals("")){
                    //                  Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.TimeNull),Toast.LENGTH_LONG ).show();
                    //                  return ;
                    //              }else{
                    if(thread != null ){
                        thread.interrupt();
                        thread = null ;

                    }
                    //final String auto=((EditText) entryView.findViewById(R.id.view_content)).getText().toString();
                    try {
                        //final int sleepTime = Integer.parseInt(auto);
                        Logger.v("type", type);
                        final int sleepTime = type;
                        if(sleepTime>0){
                            if(Integer.parseInt(ChapterId)<=fileList.size()){
                                thread =new Thread(){
                                    @Override
                                    public void run() {
                                        while (true) {
                                            try {
                                                thread.sleep(sleepTime*1000);    
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                                return ;
                                            }
                                            Message msg = new Message();
                                            handle.sendMessage(msg);
                                        }
                                    }
                                };
                                thread.start();
                                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.AutoFlipSettingsSuccess),Toast.LENGTH_LONG ).show(); 
                            }
                        }else {
                            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.TimeInputErrorNumber),Toast.LENGTH_LONG ).show();
                            return ;
                        }   
                    } catch (Exception e) {
                        // TODO: handle exception
                        Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.TimeInputErrorNumber),Toast.LENGTH_LONG ).show();
                        return ;
                    }
                    dialog.dismiss();
                    //((EditText)(entryView.findViewById(R.id.view_content))).setText("");
                }
                //  }
            });
            builder.setButton2("关  闭", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if(thread != null ){
                        thread.interrupt();
                        thread = null ;
                        Toast.makeText(MebViewFileActivity.this,getResources().getString( R.string.CloseAutoPage),Toast.LENGTH_LONG ).show();
                    }
                    dialog.cancel();
                    //((EditText)(entryView.findViewById(R.id.view_content))).setText("");
                }
            });
            return builder;
        }
        /**
         * 字体等设置
         * @param context
         * @return
         */
        private Dialog buildDialogTypefaces(Context context) {
            LayoutInflater inflater=LayoutInflater.from(this);
            GlobalVar appState = (GlobalVar)getApplicationContext();
            int skin = 1;//appState.getSkinID();
            int layoutID = 0 ;
            switch(skin){
            case 1: layoutID = R.layout.readsetformer ; break ;
            case 2: layoutID = R.layout.readsetformer2 ; break ;
            default: layoutID = R.layout.readsetformer ;
            }
            final View entryView=inflater.inflate(layoutID, null);
            PviAlertDialog dlg = new PviAlertDialog(context);
            //         dlg.setTitle(R.string.fontSizeDialogBoxTitle);      
            dlg.setView(entryView);
            ReadSetView internalView = (ReadSetView) entryView;
            internalView.setDialog(dlg);
            internalView.setSufangBool(false);
            HashMap<String,String> chooseMap = new HashMap<String,String>();
            chooseMap.put(ReadSetView.CON_FONESIZE, "" + (int)m_size);
            chooseMap.put(ReadSetView.CON_LINESIZE, "" + (int)lineSpac);
            internalView.setChooseMap(chooseMap);
            internalView.setSetListener(new ReadSetView.SetListener(){
                public void chooseDoListener(boolean ok, java.util.Map<String,String> chooseButton) {
                    if (ok) {
                        String foneSize = chooseButton.get(ReadSetView.CON_FONESIZE);
                        String lineSize = chooseButton.get(ReadSetView.CON_LINESIZE);
                        m_size = (int) Float.parseFloat(foneSize);
                        lineSpac = (int) Float.parseFloat(lineSize);
                        contentPageView.setTextSize(m_size);
                        contentPageView.setLineSpacing(lineSpac, 1);
                        SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(spanned.toString().trim());
                        PageTextView pageView = (PageTextView)contentPageView;
                        total_pages = pageView.all_pages(span_strBuilder,true,false);
                        if(page >= total_pages.size()){
                            page=total_pages.size();
                            charSequence=spanned.subSequence(total_pages.get(page-1),chapterIdLength-1);
                        }else{
                            charSequence=spanned.subSequence(total_pages.get(page-1), total_pages.get(page));
                        }
                        contentPageView.setText(getUnderLine(charSequence));
                        updatePagerinfo(page+"/"+total_pages.size());
                    }
                }
            });


            return dlg;

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
                        Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.CommmentContentNull),Toast.LENGTH_LONG ).show();
                        return;
                    }
                    String postils=((EditText) entryView.findViewById(R.id.view_content)).getText().toString();
                    addCommentsInfo(postils);
                    dialog.dismiss();
                    ((EditText)(entryView.findViewById(R.id.view_content))).setText("");
                }

            });
            builder.setButton2(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                    dialog.cancel();
                    ((EditText)(entryView.findViewById(R.id.view_content))).setText("");

                }
            });
            return builder;
        }
        private Dialog buildDialogChapter(Context context){
            LayoutInflater inflater=LayoutInflater.from(this);
            final View entryView=inflater.inflate(R.layout.files, null);
            PviAlertDialog builder=new PviAlertDialog(context);
            builder.setTitle(getResources().getString(R.string.InputSectionNumber)+"("+1+"~"+fileList.size()+")");
            TextView txt_view_content=(TextView)entryView.findViewById(R.id.txt_view_content);
            txt_view_content.setText(getResources().getString(R.string.desChap));
            builder.setView(entryView);
            builder.setButton(getResources().getString(R.string.OK), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    if(((EditText)entryView.findViewById(R.id.view_content)).getText()==null||((EditText) entryView.findViewById(R.id.view_content)).getText().toString().equals("")){
                        Toast.makeText(MebViewFileActivity.this,getResources().getString(R.string.InputNull),Toast.LENGTH_LONG ).show();
                        return ;
                    }else{
                        try {
                            jumpPages=Integer.parseInt(((EditText) entryView.findViewById(R.id.view_content)).getText().toString());
                            if(jumpPages>=1&&jumpPages<=fileList.size()){
                                jumpChapter(jumpPages);
                            }else{
                                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.ChaptersLength),Toast.LENGTH_LONG ).show();
                                return ;
                            }
                        } 
                        catch (NumberFormatException e) {
                            // TODO: handle exception
                            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.InputErrorNumber),Toast.LENGTH_LONG ).show();
                            return ;
                        }       
                    }
                    dialog.dismiss();
                    ((EditText)(entryView.findViewById(R.id.view_content))).setText("");
                }

            });
            builder.setButton2(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                    ((EditText)(entryView.findViewById(R.id.view_content))).setText("");
                }
            });
            return builder;
        }
        private void addCommentsInfo(String postils){
            if(page>total_pages.size()){
                page=total_pages.size();
            }
            if(sta>end){
                int staend=sta;
                sta=end;
                end=staend;
            }
            ContentValues values = new ContentValues();
            GlobalVar app = (GlobalVar)getApplicationContext() ;
            values.put(CommentsInfo.UserID, app.getUserID());
            values.put(CommentsInfo.ChapterId, ChapterId);
            values.put(CommentsInfo.ChapterName,fileList.get(Integer.parseInt(ChapterId)-1).getFileUrl() );
            values.put(CommentsInfo.ContentName,fileList.get(Integer.parseInt(ChapterId)-1).getMebName() );
            values.put(CommentsInfo.ContentId, conID);
            values.put(CommentsInfo.StartPosition, sta);
            values.put(CommentsInfo.EndPosition, end);
            values.put(CommentsInfo.Comment, postils);
            values.put(CommentsInfo.CertPath, certPath);
            values.put(CommentsInfo.FilePath, filePath);
            getContentResolver().insert(CommentsInfo.CONTENT_URI, values);
            Toast.makeText(this,R.string.CommmentSucess, Toast.LENGTH_LONG).show();
            getMyannotatio(filePath, ChapterId);
            contentPageView.setText(getUnderLine(charSequence));
        }

        public void showMe(){
            Intent tmpIntent = new Intent(
                    MainpageActivity.SHOW_ME);
            Bundle bundleToSend = new Bundle();

            bundleToSend.putString("sender", MebViewFileActivity.this.getClass().getName()); //TAB内嵌activity类的全名
            tmpIntent.putExtras(bundleToSend);
            sendBroadcast(tmpIntent);
            tmpIntent = null;
            bundleToSend = null;
        }

        private Dialog buildDialogfile(Context context){
            PviAlertDialog builder=new PviAlertDialog(context);
            while(Comment.length()<8){
                Comment="\t"+Comment+"\t";
            }
            builder.setTitle(getResources().getString(R.string.Comtents));
            builder.setMessage(Comment.toString());
            builder.setButton(getString(R.string.OK), new DialogInterface.OnClickListener() {


                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            return builder;
        }
        @Override
        protected void onResume() {
            // TODO Auto-generated method stub
            Logger.i(TAG,"++++++++++++++++++++++++++++++++onResume ,activity id: " + m_activity_id);
            long TimeStart = System.currentTimeMillis();
            Logger.i("Time","Mainpage Pressed" + Long.toString(TimeStart));
            super.onResume();
            //EPDRefresh.refreshGCOnceFlash();
            if(!open_failed) {
                showMe();
            }
            sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
           //\\ Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 1);
        }
        protected void  onPause(){
            // TODO Auto-generated method stub
            Logger.i(TAG,"++++++++++++++++++++++++++++++++onPause ,activity id: " + m_activity_id);
            super.onPause();
            if(filenameS!=null){
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                String columns[] = new String[]{
                        Bookmark.UserID, 
                        Bookmark.BookmarkId,
                        Bookmark.ContentId, 
                        Bookmark.ChapterId,
                        Bookmark.ChapterName,
                        Bookmark.ContentName,
                        Bookmark.CertPath,
                        Bookmark.FilePath,
                        Bookmark.CreatedDate,
                        Bookmark.Position,
                        Bookmark.BookmarkType,
                        Bookmark.CreatedDate,
                        Bookmark.Author,
                        Bookmark.DownloadType
                };
                Cursor cur = null;
                String where= new String("");
                if(downloadType!=null) {
	                if(conID == null) {
	                	where = Bookmark.BookmarkType+"='"+0+"'" + " and " + Bookmark.FilePath+"='"+filePath+"' and "+Bookmark.DownloadType+"='"+downloadType+"'"; 
	                }else if(conID.length() == 0) {
	                	where = Bookmark.BookmarkType+"='"+0+"'" + " and " + Bookmark.FilePath+"='"+filePath+"' and "+Bookmark.DownloadType+"='"+downloadType+"'";
	                }else {
	                	where = Bookmark.BookmarkType+"='"+0+"'" + " and " + Bookmark.ContentId+"='"+conID+"' and "+Bookmark.DownloadType+"='"+downloadType+"'";
	                }
                }else {
	                if(conID == null) {
	                	where = Bookmark.BookmarkType+"='"+0+"'" + " and " + Bookmark.FilePath+"='"+filePath+"'"; 
	                }else if(conID.length() == 0) {
	                	where = Bookmark.BookmarkType+"='"+0+"'" + " and " + Bookmark.FilePath+"='"+filePath+"'";
	                }else {
	                	where = Bookmark.BookmarkType+"='"+0+"'" + " and " + Bookmark.ContentId+"='"+conID+"'";
	                }
                }
                cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null, null);
                GlobalVar app = (GlobalVar)getApplicationContext() ;
                getContentResolver().delete(Bookmark.CONTENT_URI, where, null);
                if(false/*cur.getCount()==1*/){ 
                    ContentValues values = new ContentValues(); 
                    Logger.v("size", fileList.size());
                    Logger.v("ChapterId", ChapterId);

                    values.put(Bookmark.ChapterName,fileList.get(Integer.parseInt(ChapterId)-1).getFileUrl());
                    values.put(Bookmark.ContentId,  conID);
                    values.put(Bookmark.UserID,app.getUserID());
                    values.put(Bookmark.BookmarkType, 0);
                    values.put(Bookmark.FontSize, contentPageView.getTextSize());
                    values.put(Bookmark.LineSpace, lineSpac);
                    values.put(Bookmark.ChapterId, ChapterId);
                    values.put(Bookmark.OperationType, 0);
                    values.put(Bookmark.Author, author);
                    try {
                        values.put(Bookmark.Position,total_pages.get(page-1));
                    } catch (Exception e) {
                        Logger.v("meb4", e.toString());
                        Intent intent1 = new Intent(
                                MainpageActivity.BACK);
                        sendBroadcast(intent1);
                        return ;
                        // TODO: handle exception
                    }
                    values.put(Bookmark.SynchFlag, 0);
                    values.put(Bookmark.CertPath, certPath);
                    if(filePath.contains("/sdcard/localbook/book")){
                        values.put(Bookmark.SourceType, 1);
                    }else if(filePath.contains("/sdcard/download/book")){
                        values.put(Bookmark.SourceType, 2);
                    }else if(filePath.contains("/sdcard")) {
                        values.put(Bookmark.SourceType, 3);
                    }else {
                        values.put(Bookmark.SourceType, 5);
                    }
                    values.put(Bookmark.FilePath, filePath);
                    values.put(Bookmark.CreatedDate, GlobalVar.getFormatTime(System.currentTimeMillis()));      
                    where=Bookmark.BookmarkType+"='"+0+"'" + " and " + Bookmark.ContentId+"='"+conID+"'"; 
                    getContentResolver().update(Bookmark.CONTENT_URI,values,where,null);

                }else{
                    mebInsertSystemBookmark();
                }

                if(cur != null) {
                	cur.close();
                }
                if(popmenu!=null){
                    popmenu.dismiss();
                }
            }

            //onDestroy();
        }
        public void onStop()
        {
            Logger.i(TAG,"++++++++++++++++++++++++++++++++onStop ,activity id: " + m_activity_id);
            super.onStop();
            
        }
        protected void onStart(){
            Logger.i(TAG,"++++++++++++++++++++++++++++++++onStart ,activity id: " + m_activity_id);
            super.onStart();
        };
        
        protected void onRestart(){
            Logger.i(TAG,"++++++++++++++++++++++++++++++++onRestart ,activity id: " + m_activity_id);
            super.onRestart();
        };

        protected void onDestroy(){
            total_activity_num--;
            Logger.i(TAG,"++++++++++++++++++++++++++++++++onDestroy,activity id: " + m_activity_id + ". total MebViewFileActivity num(exclude this one): " +  total_activity_num );
            
            super.onDestroy();
        };

        private void mebInsertSystemBookmark(){
            ContentValues values = new ContentValues();
            if(filePath.contains("/sdcard/localbook/book")){
                values.put(Bookmark.SourceType, 1);
            }else if(filePath.contains("/sdcard/download/book")){
                values.put(Bookmark.SourceType, 2);
            }else if(filePath.contains("/sdcard")) {
                values.put(Bookmark.SourceType, 3);
            }else {
                values.put(Bookmark.SourceType, 5);
            }
            GlobalVar app = (GlobalVar)getApplicationContext() ;
            values.put(Bookmark.ContentName,fileList.get(Integer.parseInt(ChapterId)-1).getMebName() );
            values.put(Bookmark.ContentId,  conID);
            values.put(Bookmark.UserID, app.getUserID());
            values.put(Bookmark.FontSize, contentPageView.getTextSize());
            values.put(Bookmark.LineSpace, lineSpac);
            values.put(Bookmark.BookmarkType, 0);
            values.put(Bookmark.ChapterId, ChapterId);
            values.put(Bookmark.ChapterName,fileList.get(Integer.parseInt(ChapterId)-1).getFileUrl());
            values.put(Bookmark.OperationType, 0);
            values.put(Bookmark.DownloadType,downloadType);
            try {
                values.put(Bookmark.Position,total_pages.get(page-1));
            } catch (Exception e) {
                Logger.e("meb5", e.toString());
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
                // TODO: handle exception
            }
            values.put(Bookmark.SynchFlag, 0);
            values.put(Bookmark.CertPath, certPath);
            values.put(Bookmark.FilePath, filePath);
            values.put(Bookmark.Author, author);
            getContentResolver().insert(Bookmark.CONTENT_URI, values);
        }
        
        //查询所有的批注
        private void getMyannotatio(String tmpFilePath,String chapterId){
            String columns[] = new String[]{
                    CommentsInfo.StartPosition, 
                    CommentsInfo.EndPosition, 
                    CommentsInfo.Comment,
                    CommentsInfo.ChapterId,
                    CommentsInfo.FilePath
            };
            Cursor cur = null;  
            //m_Countannotatio = 0;
            String where=CommentsInfo.ChapterId+"='"+chapterId+"'" //currentPage
            + " and " + 
            CommentsInfo.FilePath+"='"+tmpFilePath+"'"; //filenameString        
            cur = managedQuery(CommentsInfo.CONTENT_URI, columns, where, null, null);
            commentList = new ArrayList<CommentInfo>();
            while(cur.moveToNext()){
                CommentInfo comm = new CommentInfo();
                comm.setStartPostion(Integer.parseInt(cur.getString(cur
                        .getColumnIndex(CommentsInfo.StartPosition))));
                comm.setEndPostion(Integer.parseInt(cur.getString(cur
                        .getColumnIndex(CommentsInfo.EndPosition))));
                comm.setComment(cur.getString(cur
                        .getColumnIndex(CommentsInfo.Comment)));
                commentList.add(comm);
            }
            Collections.sort(commentList, new CommentInfo());

        }

        //给批注所在位置加下划线
        private SpannableStringBuilder getUnderLine(CharSequence cSequence){
            SpannableStringBuilder style=new SpannableStringBuilder(cSequence); 

            if(total_pages.size()==1){
                for(int i=0;i<commentList.size();i++){
                    CommentInfo comm = commentList.get(i);
                    if(comm.getStartPostion()==0){
                        style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                        style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion(),comm.getEndPostion(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    
                    }else {
                        style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                        style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion(),comm.getEndPostion(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    
                    }   
                }
            }else{
                for(int i=0;i<commentList.size();i++){
                    if(page<total_pages.size()&&page==1){
                        CommentInfo comm = commentList.get(i);
                        if(comm.getStartPostion()>=total_pages.get(page-1)&&comm.getEndPostion()<=total_pages.get(page)){
                            if(comm.getStartPostion()==total_pages.get(page-1)){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion(), comm.getEndPostion(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            }else {
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion(), comm.getEndPostion(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            }

                        }else if(comm.getStartPostion()<total_pages.get(page)&&comm.getEndPostion()>total_pages.get(page)){
                            style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion() , total_pages.get(page), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
                        }
                    }else if (page<total_pages.size()&&page!=total_pages.size()){
                        CommentInfo comm = commentList.get(i);
                        if(comm.getStartPostion()==total_pages.get(page-1)&&comm.getEndPostion()<=total_pages.get(page)){
                            if(comm.getStartPostion()==total_pages.get(page-1)){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion()-total_pages.get(page-1),comm.getEndPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    
                            }else {
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-total_pages.get(page-1)-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion()-total_pages.get(page-1),comm.getEndPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    
                            }

                        }else if(comm.getStartPostion()<=total_pages.get(page-1)&&comm.getEndPostion()>=total_pages.get(page-1)){
                            style.setSpan(new StyleSpan(Typeface.BOLD), 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            style.setSpan(new URLSpan(cSequence.toString()), 0, comm.getEndPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);    
                        }else if(comm.getStartPostion()<=total_pages.get(page)&&comm.getEndPostion()>=total_pages.get(page)) {
                            style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-total_pages.get(page-1)-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion()-total_pages.get(page-1), total_pages.get(page)-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }else if (page==total_pages.size()){
                        CommentInfo comm = commentList.get(i);
                        if(comm.getStartPostion()>=total_pages.get(page-1)&&comm.getEndPostion()<=chapterIdLength){
                            if(comm.getStartPostion()==total_pages.get(page-1)){
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion()-total_pages.get(page-1), comm.getEndPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            }else {
                                style.setSpan(new StyleSpan(Typeface.BOLD), 0, comm.getStartPostion()-total_pages.get(page-1)-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                                style.setSpan(new URLSpan(cSequence.toString()), comm.getStartPostion()-total_pages.get(page-1), comm.getEndPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            }

                        }else if(comm.getStartPostion()<=total_pages.get(page-1)&&comm.getEndPostion()>=total_pages.get(page-1)){
                            style.setSpan(new StyleSpan(Typeface.BOLD), 0, 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);   
                            style.setSpan(new URLSpan(cSequence.toString()), 0, comm.getEndPostion()-total_pages.get(page-1), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                }
            }
            return style;
        }
        //本章或本页查找
        private List<Integer> getFinds(String findFile,int  chapterOrPage ){
            int startPos = 0;
            int foundPos = -1; // -1 represents not found.
            List<Integer> foundItems =new ArrayList<Integer>();
            String chapterAndPage="";
            if(chapterOrPage==0){
                //表示本章查找
                chapterAndPage=spanned.toString().toLowerCase();
            }else{
                //当前页查找
                chapterAndPage=charSequence.toString().toLowerCase();
            }
            do{
                foundPos=chapterAndPage.indexOf(findFile.toLowerCase());
                if(foundPos==-1){
                    break;
                }
                startPos = startPos+foundPos;
                foundItems.add(startPos);
                chapterAndPage=" "+chapterAndPage.substring(foundPos+1);

            }while(foundPos>-1);
            return foundItems;
        }


        //下一章
        private void nextChapter(int chap){
        //  EPDRefresh.refreshGCOnceFlash();
            try {
                num = 0;
                if(chap>fileList.size()||(chap==fileList.size()&&fileList.get(chap-1).getFlag().equals("0"))){
                    if(thread != null ){
                        thread.interrupt();
                        thread = null ;
                    }
                    ChapterId=String.valueOf(fileList.size());
                    if(page>=total_pages.size()){
                        page=total_pages.size();
                        Toast.makeText(MebViewFileActivity.this,getResources().getString(R.string.txtEndPage),Toast.LENGTH_LONG ).show();
                    }else {
                        Toast.makeText(MebViewFileActivity.this,getResources().getString(R.string.EndChapter),Toast.LENGTH_LONG ).show();
                    }
                    
                    Logger.d(TAG,"downloadType:"+downloadType);
                    //最后章节处理逻辑
                    if("4".equals(downloadType)){
                        new Thread() {
                            public void run() {
                                boolean needAlert = false;
                                final HashMap<String,String> bookinfo = getBookInfo(conID);
                                try {
                                    if(bookinfo!=null
                                            &&"1".equals(bookinfo.get("IsSerial"))
                                            &&"0".equals(bookinfo.get("IsFinish"))                                        
                                            ){
                                        mHandler.sendEmptyMessage(PD_SUMMARY);
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                    }else if("3".equals(downloadType)||"5".equals(downloadType)){
                        mHandler.sendEmptyMessage(PD_DOWNLOADALL);
                    }
                    
                    return ;
                }else{
                    page=1;
                    GlobalVar appState = ((GlobalVar) getApplicationContext());
                    ChapterId=String.valueOf(chap);
                    chapters=fileList.get(chap-1);
                    
                    String flag=chapters.getFlag();
                    if(flag.equals("0")){
                        ChapterId=String.valueOf(chap+1);
                        chapters=fileList.get(chap);
                    }
                    Intent tmpIntent = new Intent(
                            MainpageActivity.SET_TITLE);
                    Bundle sndbundle = new Bundle();
                    sndbundle.putString("title", chapters.getMebName()+"[>>]"+chapters.getFileUrl());
                    Logger.i(TAG,"SET_TITLE" + chapters.getMebName()+"[>>]"+chapters.getFileUrl());
                    tmpIntent.putExtras(sndbundle);
                    sendBroadcast(tmpIntent);
                    if(chapters.getBillingflag()!=1){
                        filenameS=mifImpl.getFileContent(filePath, chapters.getStartFile(),chapters.getFileLength());
                    }else{
                        filenameS=mifImpl.getFileFlagContent(filePath, chapters.getStartFile(), chapters.getFileLength(), certPath, appState.getRegCode(), appState.getUserID(), appState.getInnerPassword());
                    }
                    spanned=Html.fromHtml(filenameS);
                    chapterIdLength=spanned.length();
                    SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(spanned.toString().trim());
                    PageTextView pageView = (PageTextView)contentPageView;
                    total_pages = pageView.all_pages(span_strBuilder,true,true);
                    getMyannotatio(filePath,ChapterId);
                    if(total_pages.size()==1){
                        charSequence=spanned;
                    }else {
                        charSequence=spanned.subSequence(0, total_pages.get(1));
                    }
                    contentPageView.setText(getUnderLine(charSequence));
                    updatePagerinfo(page+"/"+total_pages.size());
                    freeChapter();
                }
            } catch (NullPointerException e) {
                // TODO: handle exception
                Logger.v("meb6", e.toString());
                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.FailException), Toast.LENGTH_LONG).show();
                ChapterId=String.valueOf(chap-1);
                page=total_pages.size();
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
            }catch (Exception e) {
                // TODO: handle exception
                Logger.v("meb7", e.toString());
                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
            }
        }
        //
        private void moveFile(){
            contentPageView.setOnTouchListener(new OnTouchListener() { 

                private boolean in_annotation_state = false;
                private int annotation_start_pos = 0;
                //StyleSpan selection_span;
                URLSpan selection_span;
                private int startX = 0 , startY = 0 ,endX = 0 ,endY = 0 ;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (popmenu != null && popmenu.isShowing()) {
                        popmenu.dismiss();
                    }
                    Layout layout = contentPageView.getLayout();
                    int line = 0; 
                    switch(event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //\\line=layout.getLineForVertical(tv.getScrollY()+(int)event.getY());
                        startX = (int)event.getX();
                        startY = (int)event.getY();
                        line=layout.getLineForVertical((int)event.getY());
                        sta = layout.getOffsetForHorizontal(line, (int)event.getX());
                        end = sta;
                        try {
                            int  Position=total_pages.get(page-1)+sta;
                            for(int i=0;i<commentList.size();i++){
                                CommentInfo comm = commentList.get(i);
                                if(comm.getStartPostion()<=Position&&Position<=comm.getEndPostion()){
                                    Comment=comm.getComment();
                                    dialogs = buildDialogfile(MebViewFileActivity.this);
                                    dialogs.show(); 
                                }   
                            }
                        } catch (Exception e) {
                            // TODO: handle exception
                            return true;
                        }

                        if( addnote <= 2) {
                            in_annotation_state = true;
                            GlobalVar appState = (GlobalVar)getApplicationContext();
                            if(appState.deviceType==1){
//                                contentPageView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
                            }
                            {
                                int offset = 0;
                                int h_offset = 0;
                                //offset =layout.getLineStart(line);
                                h_offset = layout.getOffsetForHorizontal(line, (float)event.getX());
                                offset = offset + h_offset;
                                //\\showDialog(1);
                                // tv.setSelection(1, offset);
                                annotation_start_pos = offset;
                            }
                        }
                        //\\return true;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //\\line=layout.getLineForVertical(tv.getScrollY()+ (int)event.getY());
                        line=layout.getLineForVertical((int)event.getY());
                        end = layout.getOffsetForHorizontal(line, (int)event.getX());
                        if(addnote <= 2){
                            if(in_annotation_state) {
                                int offset = 0;
                                int h_offset = 0;
                                //\\offset =layout.getLineStart(line);
                                h_offset = layout.getOffsetForHorizontal(line, (int)event.getX());
                                offset = offset + h_offset;
                                //\\showDialog(1);
                                CharSequence char_seq = contentPageView.getText();
                                SpannableStringBuilder span_builder =(SpannableStringBuilder)char_seq;
                                if(char_seq != null) {
                                    if(selection_span == null) {
                                        //selection_span = new StyleSpan(Typeface.BOLD_ITALIC);
                                        selection_span = new URLSpan(char_seq.toString());
                                    }else {
                                        span_builder.removeSpan(selection_span);
                                        if(offset >= annotation_start_pos) {
                                            span_builder.setSpan(selection_span, annotation_start_pos, offset, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        }else {
                                            span_builder.setSpan(selection_span, offset, annotation_start_pos,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        }
                                    }
                                }
                                if(offset > annotation_start_pos) {
                                    //\\ tv.setSelection(annotation_start_pos,offset);
                                }else {
                                    //\\ tv.setSelection(offset,annotation_start_pos);
                                }
                            }
                        }

                        return  true;
                    case MotionEvent.ACTION_UP:
                        endX = (int)event.getX();
                        endY = (int)event.getY();
                        if(in_annotation_state) {
	                        GlobalVar appState = (GlobalVar)getApplicationContext();
	                        if(appState.deviceType==1){
//	                            contentPageView.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
	                        }
                        }    
                        if(addnote==1){
                            in_annotation_state = false;
                            CharSequence char_seq = contentPageView.getText();
                            SpannableStringBuilder span_builder = (SpannableStringBuilder) char_seq;
                            if (char_seq != null) {
                                if (selection_span != null) {
                                    span_builder.removeSpan(selection_span);
                                    selection_span = null;
                                }
                            }
                            if(sta > end){
                                int temp = sta ;
                                sta = end ;
                                end = temp ;
                            }else if(sta == end){
                                return true;
                            }
                            String noteInfoStr = char_seq.toString().substring(sta,end);
                            PviAlertDialog pad = new PviAlertDialog(MebViewFileActivity.this);
                            LayoutInflater inflater = LayoutInflater.from(MebViewFileActivity.this);
                            final View entryView = inflater.inflate(R.layout.addnewnote, null);
                            EditText et = (EditText)entryView.findViewById(R.id.notecontent);
                            et.setText(noteInfoStr);
                            pad.setTitle("添加便笺");
                            pad.setView(entryView);
                            pad.setCanClose(true);
                            pad.setButton(getString(R.string.txtPositiveButton),
                                    new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    EditText et1 = (EditText)entryView.findViewById(R.id.notetile);
                                    EditText et2 = (EditText)entryView.findViewById(R.id.notecontent);
                                    String str1 = et1.getText().toString();
                                    String str2 = et2.getText().toString();
                                    String message = "" ;
                                    boolean su = true;
                                    Map map = new HashMap();
                                    AddNote an = new AddNote(MebViewFileActivity.this);
                                    if(str1.equals("")){

                                        //                                              Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
                                        //                                              Bundle sndbundle = new Bundle();
                                        //                                              sndbundle.putString("pviapfStatusTip", "便笺标题不能为空");
                                        //                                              msgIntent.putExtras(sndbundle);
                                        //                                              sendBroadcast(msgIntent);
                                        //                                              
                                        //                                              return ;
                                        message = "标题不能为空" ;
                                        su = false ;
                                    }

                                    PviAlertDialog pad2 = new PviAlertDialog(MebViewFileActivity.this);
                                    pad2.setTitle("便笺添加 提示");
                                    if(su){
                                        map.put("noteName", str1);
                                        map.put("noteText", str2);
                                        an.addNoteInfo(map);
                                        message = "添加成功";
                                        pad2.setMessage(message,Gravity.CENTER);
                                    }else{
                                    	message = "添加失败,失败原因为：" + message;
                                        pad2.setMessage(message,Gravity.CENTER);
                                    }
                                    pad2.setCanClose(true);
                                    pad2.setButton(getString(R.string.txtPositiveButton),
                                            new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    //pad2.show();
                                    Toast.makeText(MebViewFileActivity.this, message, Toast.LENGTH_LONG).show();                                    
                                }

                            });
                            pad.setButton2(getString(R.string.txtCancelButton),
                                    new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {

                                }

                            });
                            pad.show();
                        }
                        else if(addnote == 2) {
                            in_annotation_state = false;
                            CharSequence char_seq = contentPageView.getText();

                            SpannableStringBuilder span_builder = (SpannableStringBuilder)char_seq;
                            if(sta!=end){
                            	boolean show_dlg = false;
                                if(sta>end){
                                    int staend=sta;
                                    sta=end;
                                    end=staend;
                                }
                                if(commentList.size()==0){
                                    //showDialog(1);
                                	show_dlg = true;
                                }else{
                                    int i=0;
                                    boolean falg=false;
                                    sta=sta+total_pages.get(page-1);
                                    end=end+total_pages.get(page-1);
                                    if(end<commentList.get(0).getStartPostion()){
                                        //showDialog(1);
                                    	show_dlg = true;
                                    }else if (sta>commentList.get(commentList.size()-1).getEndPostion()) {
                                        //showDialog(1);
                                    	show_dlg = true;
                                    }else if (commentList.size()==1) {
                                        if(sta>commentList.get(0).getEndPostion()||end<commentList.get(0).getStartPostion()){
                                            //showDialog(1);
                                        	show_dlg = true;
                                        }
                                    }else {
                                        do{
                                            if(end<commentList.get(i+1).getStartPostion()&&sta>commentList.get(i).getEndPostion()){
                                                falg=true;
                                            }
                                            i++;
                                        }while(i<commentList.size()-1);
                                        if(falg){
                                            //showDialog(1);
                                        	show_dlg = true;
                                        }else {
                                            Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.CommentHaveBeen), Toast.LENGTH_LONG).show();
                                        }
                                    }

                                }
                                if(show_dlg) {
                                	Dialog dlg = buildDialog(MebViewFileActivity.this);
                                	dlg.show();
                                }
                            }
                            if(char_seq != null) {
                                if(selection_span != null) {
                                    span_builder.removeSpan(selection_span);
                                    selection_span = null;
                                }
                            }

                        }else {
                            if(!autopage){
                                if(startX - endX  > 100 && startX - endX >= Math.abs(startY - endY)){
                                    page=page+1;
                                    goToNextPage(page);
                                }else if (startY - endY > 100 && startY - endY >= Math.abs(startX - endX)) {
                                    page=page+1;
                                    goToNextPage(page);
                                }else if(endX - startX  > 100 && endX - startX >= Math.abs(startY - endY)) {
                                    goToUpPage(page);
                                }else if(endY - startY > 100 && endY - startY>= Math.abs(startX - endX)) {
                                    goToUpPage(page);
                                }

                            }
                        }
                        return  true;
                    }       

                    return true;
                }
            });

            mebView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if(popmenu!=null){
                        popmenu.dismiss();
                    }
                    return true;
                }
            });
            if(skinID == R.layout.filebrowser2){
            }else {
                mebmenu.setOnTouchListener(new OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // TODO Auto-generated method stub
                        if(popmenu!=null){
                            popmenu.dismiss();
                        }
                        return true;
                    }
                });
            }

        }
        //上一章
        private void upChapter(int chap){
            System.out.println("chap="+chap);
            EPDRefresh.refreshGCOnceFlash();
            try {
                num = 0;

                if(chap<1||(chap==1&&fileList.get(0).getFlag().equals("0"))){
                    ChapterId="1";
                    if(page>0){
                        Toast.makeText(MebViewFileActivity.this,getResources().getString(R.string.FirstChapter),Toast.LENGTH_LONG ).show();
                    }else {
                        page=1;
                        show_toast(getResources().getString(R.string.txtFirstPage));
                    }

                    return ;
                }else {

                    GlobalVar appState = ((GlobalVar) getApplicationContext());
                    page=1;
                    ChapterId=String.valueOf(chap);
                    chapters=fileList.get(chap-1);
                    if(chapters.getFlag().equals("0")){
                        ChapterId=String.valueOf(chap-2);
                        chapters=fileList.get(chap-2);
                    }
                    Intent tmpIntent = new Intent(
                            MainpageActivity.SET_TITLE);
                    Bundle sndbundle = new Bundle();
                    sndbundle.putString("title", chapters.getMebName()+"[>>]"+chapters.getFileUrl());
                    Logger.i(TAG,"SET_TITLE" + chapters.getMebName()+"[>>]"+chapters.getFileUrl());
                    tmpIntent.putExtras(sndbundle);
                    sendBroadcast(tmpIntent);
                    if(chapters.getBillingflag()!=1){
                        filenameS=mifImpl.getFileContent(filePath, chapters.getStartFile(),chapters.getFileLength());
                    }else{
                        filenameS=mifImpl.getFileFlagContent(filePath, chapters.getStartFile(), chapters.getFileLength(), certPath, appState.getRegCode(), appState.getUserID(), appState.getInnerPassword());
                    }
                    spanned=Html.fromHtml(filenameS);
                    chapterIdLength=spanned.length();
                    SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(spanned.toString().trim());
                    PageTextView pageView = (PageTextView)contentPageView;
                    total_pages = pageView.all_pages(span_strBuilder,true,true);
                    getMyannotatio(filePath,ChapterId);
                    if(total_pages.size()==1){
                        charSequence=spanned;
                    }else {
                        charSequence=spanned.subSequence(0, total_pages.get(1));
                    }
                    contentPageView.setText(getUnderLine(charSequence));
                    updatePagerinfo(page+"/"+total_pages.size());
                    freeChapter();
                }

            } catch (NullPointerException e) {
                // TODO: handle exception
                Logger.v("meb8", e.toString());
                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.FailException), Toast.LENGTH_LONG).show();
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
            }catch (Exception e) {
                // TODO: handle exception
                Logger.v("meb9", e.toString());
                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
            }

        }
        //跳章
        private void jumpChapter(int jumpPage){
            EPDRefresh.refreshGCOnceFlash();
            try{
                if(jumpPage<1||jumpPage>fileList.size()){
                    return;
                }
                page=1;
                GlobalVar appState = ((GlobalVar) getApplicationContext());
                ChapterId=String.valueOf(jumpPage);
                chapters=fileList.get(jumpPage-1);
                
                Intent tmpIntent = new Intent(
                        MainpageActivity.SET_TITLE);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("title", chapters.getMebName()+"[>>]"+chapters.getFileUrl());
                tmpIntent.putExtras(sndbundle);
                sendBroadcast(tmpIntent);

                if(chapters.getBillingflag()!=1){
                    filenameS=mifImpl.getFileContent(filePath, chapters.getStartFile(),chapters.getFileLength());
                }else{
                    filenameS=mifImpl.getFileFlagContent(filePath, chapters.getStartFile(), chapters.getFileLength(), certPath, appState.getRegCode(),appState.getUserID(),appState.getInnerPassword());
                }
                spanned=Html.fromHtml(filenameS);
                chapterIdLength=spanned.length();
                SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(spanned.toString().trim());
                PageTextView pageView = (PageTextView)contentPageView;
                total_pages = pageView.all_pages(span_strBuilder,true,true);
                getMyannotatio(filePath,ChapterId);
                if(total_pages.size()==1){
                    charSequence=spanned;
                }else {
                    charSequence=spanned.subSequence(0, total_pages.get(1));
                }
                contentPageView.setText(getUnderLine(charSequence));
                updatePagerinfo(page+"/"+total_pages.size());
                freeChapter();
            }catch (NullPointerException e) {
                // TODO: handle exception
                Logger.v("meb10", e.toString());
                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.FailException), Toast.LENGTH_LONG).show();
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
            }catch (Exception e) {
                // TODO: handle exception
                Logger.v("meb11", e.toString());
                Toast.makeText(MebViewFileActivity.this, getResources().getString(R.string.deadException), Toast.LENGTH_LONG).show();
                if(thread != null ){
                    thread.interrupt();
                    thread = null ;
                }
                Intent intent1 = new Intent(
                        MainpageActivity.BACK);
                sendBroadcast(intent1);
                return ;
            }

        }

        //送鲜花
        private boolean VoteFlower()
        {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

            ahmNamePair.put("contentId", conID);

            ahmNamePair.put("vote", "1");

            HashMap responseMap = null;

            try {
                //以POST的形式连接请求
                responseMap = CPManager.submitVote(ahmHeaderMap, ahmNamePair);
                if (responseMap.get("result-code").toString().contains(
                "result-code: 0")) {

                    return true;
                }
            } catch (HttpException e) {
                //连接异常 ,一般原因为 URL错误
                e.printStackTrace();
                return false;
            } catch (IOException e) {
                //IO异常 ,一般原因为网络问题
                e.printStackTrace();
                return false;
            }

            //      System.out.println("返回zhuangtai："+responseMap.get("result-code"));
          //  byte[] responseBody = (byte[])responseMap.get("ResponseBody");

            //      try {
            //          System.out.println("返回的XML为：");
            //          System.out.println(CPManagerUtil.getStringFrombyteArray(responseBody));
            //      } catch (UnsupportedEncodingException e) {
            //          e.printStackTrace();
            //      }
            return false;
        }
        //送鲜花提示框
        private void showResult(String title, String msg)
        {
            PviAlertDialog builder = new PviAlertDialog(this);
            builder.setMessage(msg);
            builder.setTitle(title);
            builder.setButton(getResources().getString(R.string.OK),new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    dialog.cancel();
                }

            });
            builder.show();
        }
        //显示是否此章是否免 或收费 或不显示
        private void freeChapter(){
            mebView.setText(chapters.getMebName());
            if(chapters.getBillingflag()==1){
                freeView.setText(getResources().getString(R.string.chaptercharge));
                chapterView.setText(chapters.getFileUrl());
            }else {
                freeView.setText(getResources().getString(R.string.chapterfree));
                chapterView.setText(chapters.getFileUrl());
            }
        }

    	private View.OnKeyListener onKeyListener = new OnKeyListener() {
    		public boolean onKey(View v, int keyCode, KeyEvent event) {
       //public boolean onKeyUp(int keyCode, KeyEvent event) {
   			if (event.getAction() == KeyEvent.ACTION_UP) {
	            if(!isSerach){
	                if (keyCode == KeyEvent.KEYCODE_MENU) {   
	                      //  menupan();
	                	return false;
	                    }
	                if(keyCode==event.KEYCODE_DPAD_LEFT){
	            /*        if (popmenu != null && popmenu.isShowing()) {
	                        popmenu.dismiss();
	                    }*/
	                    goToUpPage(page);
	                }
	                if(keyCode==event.KEYCODE_DPAD_RIGHT){
	                    /*if (popmenu != null && popmenu.isShowing()) {
	                        popmenu.dismiss();
	                    }*/
	                    page=page+1;
	                    goToNextPage(page);
	                }
	                if(keyCode==event.KEYCODE_DPAD_UP){
	                 /*   if (popmenu != null && popmenu.isShowing()) {
	                        popmenu.dismiss();
	                    }*/       
	                    //\\upChapter(Integer.parseInt(ChapterId)-1);
	                }
	                if(keyCode==event.KEYCODE_DPAD_DOWN){
	                    /*if (popmenu != null && popmenu.isShowing()) {
	                        popmenu.dismiss();
	                    }*/
	                    //\\nextChapter(Integer.parseInt(ChapterId)+1);
	                    //\\nextPage.requestFocus();
	                }
	            }
	
	            if (keyCode == KeyEvent.KEYCODE_BACK) {
	                // 通知框架返回上一个子activty
	               // sendBroadcast(new Intent(MainpageActivity.BACK));
	            	return false;
	            }
	
	            //return super.onKeyUp(keyCode, event);
   			}
            return true;
        }
    	};
        
        
        //联网获取书籍信息
          private HashMap<String,String> getBookInfo(String contentID) {
              if(bookinfo!=null&&!bookinfo.isEmpty()){
                  return bookinfo;
              }else{
                  
                bookinfo = new HashMap<String,String>();
                HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
                HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
                ahmNamePair.put("contentId", contentID);

                HashMap responseMap = null;
                // Logger.i("Reader", "getBookSummaryInfo");
                try {

                    responseMap = NetCache.getContentInfo(ahmHeaderMap, ahmNamePair);
                    if (responseMap == null) {                      
                        return null;
                    } else if (responseMap.containsKey("result-code")) {
                        if (responseMap.get("result-code") != null) {
                            if (!responseMap.get("result-code").toString().contains(
                            "result-code: 0")) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }

                } catch (HttpException e) {
                    e.printStackTrace();
                    return null;
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
                 try {
                 System.out.println("返回的XML为：");
                 System.out.println(CPManagerUtil
                 .getStringFrombyteArray(responseBody));
                 } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
                 }

                Document dom = null;
                try {
                    dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                    return null;
                } catch (SAXException e) {
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                Element root = dom.getDocumentElement();

                NodeList nl = root.getElementsByTagName("contentName");
                Element contentName = (Element) nl.item(0);

                String bookname = contentName.getFirstChild().getNodeValue();

                nl = root.getElementsByTagName("authorID");
                Element temp = (Element) nl.item(0);
                bookinfo.put("AuthorID", temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("authorName");
                temp = (Element) nl.item(0);
                bookinfo.put("AuthorName", temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("isSerial");
                Element isSerial = (Element) nl.item(0);
                if (isSerial.getFirstChild().getNodeValue().equals("false")) {
                    bookinfo.put("IsSerial", "0");
                    bookinfo.put("IsFinish", "1");
                } else {
                    bookinfo.put("IsSerial", "1");
                    NodeList overlist = root.getElementsByTagName("isFinish");
                    Element isfinish = (Element) overlist.item(0);
                    if (isfinish.getFirstChild().getNodeValue().equals("false")) {
                        bookinfo.put("IsFinish", "0");

                    } else {
                        bookinfo.put("IsFinish", "1");
                    }
                }
                bookinfo.put("BookName", bookname);

                nl = root.getElementsByTagName("fascicleFlag");
                temp = (Element) nl.item(0);
                bookinfo.put("fascicleFlag", temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("chargeTip");
                temp = (Element) nl.item(0);
                if(temp!=null && (temp.getFirstChild()!=null))
                {
                    bookinfo.put("ChargeTip", temp.getFirstChild().getNodeValue());
                }
                else
                {
                    bookinfo.put("ChargeTip", "");
                }

                nl = root.getElementsByTagName("canDownload");
                temp = (Element) nl.item(0);
                bookinfo.put("CanDownload", temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("chargeMode");
                temp = (Element) nl.item(0);
                bookinfo.put("ChargeMode", temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("canPresent");
                temp = (Element) nl.item(0);
                bookinfo.put("canPresent", temp.getFirstChild().getNodeValue());

                nl = root.getElementsByTagName("smallLogo");
                if (nl != null) {
                    temp = (Element) nl.item(0);
                    if ((temp != null)) {
                        if (temp.getFirstChild() != null) {
                            bookinfo.put("SmallLogoUrl", temp.getFirstChild()
                                    .getNodeValue());
                        } else {
                            bookinfo.put("SmallLogoUrl", "No Small logo URL");
                        }
                    } else {
                        bookinfo.put("SmallLogoUrl", "No Small logo URL");
                    }
                } else {
                    bookinfo.put("SmallLogoUrl", "No Small logo URL");
                }

                return bookinfo;
                }
            }

            private class H extends Handler {
                @Override
                public void handleMessage(Message msg) {
                    // TODO Auto-generated method stub

                    super.handleMessage(msg);
                    switch (msg.what) {
                    case PD_SUMMARY:

                            final PviAlertDialog pd = new PviAlertDialog(
                                    MebViewFileActivity.this);
                            pd.setTitle("温馨提示");
                            pd.setMessage("该书尚在更新中，您可以进入摘要页查看或预定最新章节");
                            pd.setButton(DialogInterface.BUTTON_POSITIVE, "确 定",
                                    new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    pd.dismiss();
                                    //打开摘要页
                                    final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                                    final Bundle bundleToSend = new Bundle();
                                    bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity"); 
                                    bundleToSend.putString("contentID", conID);
                                    bundleToSend.putString("startType",  "allwaysCreate");
                                    bundleToSend.putString("pviapfStatusTip",  "正在进入书籍摘要...");
                                    tmpIntent.putExtras(bundleToSend);
                                    sendBroadcast(tmpIntent);
                                }
                            });
                            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取 消",
                                    new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    
                                }
                            });
                            pd.show();

                    case PD_DOWNLOADALL:
                        final PviAlertDialog pd2 = new PviAlertDialog(
                                MebViewFileActivity.this);
                        pd2.setTitle("温馨提示");
                        pd2.setMessage("您尚未下载本书其它章节，是否下载阅读剩余章节？");
                        pd2.setButton(DialogInterface.BUTTON_POSITIVE, "下载阅读",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int id) {
                                pd2.dismiss();
                                //调 下载/订购流程
                                
                                new Thread() {
                                    public void run() {
                                        bookinfo = getBookInfo(conID);

                                        if (bookinfo != null
                                                && !bookinfo.isEmpty()) {

                                            final Intent intent1 = new Intent(
                                                    MainpageActivity.START_ACTIVITY);
                                            final Bundle sndBundle1 = new Bundle();
                                            sndBundle1
                                                    .putString("act",
                                                            "com.pvi.ap.reader.activity.SubscribeProcess");
                                            sndBundle1.putString("contentID",
                                                    conID);

                                            sndBundle1.putString("isSerial",
                                                    bookinfo.get("IsSerial"));
                                            sndBundle1.putString("isFinish",
                                                    bookinfo.get("IsFinish"));
                                            sndBundle1
                                                    .putString("subscribeMode",
                                                            "download");
                                            if (bookinfo
                                                    .containsKey("FascicleFlag")
                                                    && "1".equals(bookinfo.get(
                                                            "FascicleFlag")
                                                            .toString())) {
                                                sndBundle1.putString(
                                                        "fascicle", "1");
                                            }

                                            if (bookinfo
                                                    .containsKey("CanDownload")) {
                                                if ("false".equals(bookinfo
                                                        .get("CanDownload")
                                                        .toString())) {
                                                    sndBundle1.putString(
                                                            "canDownload", "0");
                                                } else {
                                                    sndBundle1.putString(
                                                            "canDownload", "1");
                                                }
                                            }

                                            if (bookinfo
                                                    .containsKey("ChargeMode")) {
                                                sndBundle1.putString(
                                                        "chargeMode",
                                                        bookinfo.get(
                                                                "ChargeMode")
                                                                .toString());
                                            }

                                            if (bookinfo
                                                    .containsKey("ChargeTip")) {
                                                sndBundle1.putString(
                                                        "chargeTip",
                                                        bookinfo.get(
                                                                "ChargeTip")
                                                                .toString());
                                            }

                                            if (bookinfo
                                                    .containsKey("BookName")) {
                                                sndBundle1
                                                        .putString(
                                                                "bookName",
                                                                bookinfo
                                                                        .get(
                                                                                "BookName")
                                                                        .toString());
                                            }

                                            if (bookinfo
                                                    .containsKey("AuthorName")) {
                                                sndBundle1.putString(
                                                        "authorName",
                                                        bookinfo.get(
                                                                "AuthorName")
                                                                .toString());
                                            }
                                            intent1.putExtras(sndBundle1);
                                            Logger.d(TAG, sndBundle1);
                                            sendBroadcast(intent1);
                                        }
                                    }
                                }.start();
                                

                            }
                        });
                        pd2.setButton(DialogInterface.BUTTON_NEGATIVE, "书籍摘要", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                    int id) {
                                pd2.dismiss();
                              //打开摘要页
                                final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                                final Bundle bundleToSend = new Bundle();
                                bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity"); 
                                bundleToSend.putString("contentID", conID);
                                bundleToSend.putString("startType",  "allwaysCreate");
                                bundleToSend.putString("pviapfStatusTip",  "正在进入书籍摘要...");
                                tmpIntent.putExtras(bundleToSend);
                                sendBroadcast(tmpIntent);
                            }
                        });
                        pd2.setCanClose(true);
                        pd2.show();

                    default:
                        ;
                    }
                }

            }         
    	//@Override
    	public void onConfigurationChanged_not_used(Configuration newConfig) {
    		skin = newConfig.orientation ;
    		int id;
    		if(skin == Configuration.ORIENTATION_LANDSCAPE){
    			id = 2;
    		}else{
    			id = 1;
    		}
    		switch_skin(id);
    		super.onConfigurationChanged(newConfig);
    	}
    	public void switch_skin(int id) {
//    		int view_width = contentPageView.getWidth();
//    		int view_height = contentPageView.getHeight();
    		RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams)contentPageView.getLayoutParams();
    		if(id == 1) {
    			param.height = 630;
    			param.width = 550;
    		}else if(id == 2) {
    			param.height = 460;
    			param.width = 730;
    		}
    		contentPageView.setLayoutParams(param);
    		if(((GlobalVar)getApplication()).deviceType == 1 ){
    			if(skin == Configuration.ORIENTATION_LANDSCAPE){
//    				this.getWindow().getDecorView().getRootView().invalidate(0,0,800,600,UPDATEMODE_4);
    			}else{
//    				this.getWindow().getDecorView().getRootView().invalidate(0, 0, 600, 800, UPDATEMODE_4);
    			}
    		}
    	}
    	public void switch_skin_2(int id) {
    		if(id == 1){
    			setContentView(R.layout.txtfilebrowser_orientation);
    		}else if(id == 2){
    			setContentView(R.layout.txtfilebrowser_end);
    		}else {
    			return;
    		}
    		contentPageView=(PageTextView) findViewById(R.id.view_contents);
            serach2=(RelativeLayout) findViewById(R.id.serachs);
            mebView=(TextView) findViewById(R.id.mebnames);
            contentPageView=(PageTextView) findViewById(R.id.view_contents);
            freeView=(TextView) findViewById(R.id.free);
            chapterView=(TextView) findViewById(R.id.chapterName);
    		meb_bindEvent();
//    		contentPageView.setTextSize(codeSize);
//    		contentPageView.setLineSpacing(lineSpacingf, lineMult);
    		if(id == 1){
    			skinID = R.layout.mebbrowserlandscape ;
    		}else if(id == 2){
    			skinID = R.layout.filebrowser2 ;
    		}
    		if(((GlobalVar)getApplication()).deviceType == 1 ){
    			if(skin == Configuration.ORIENTATION_LANDSCAPE){
//    				this.getWindow().getDecorView().getRootView().invalidate(0,0,800,600,UPDATEMODE_4);
    			}else{
//    				this.getWindow().getDecorView().getRootView().invalidate(0, 0, 600, 800, UPDATEMODE_4);
    			}
    		}
    		contentPageView.requestFocus();
    		
    	}
      
    	public void post_invalidate_full_screen() {
    		if(((GlobalVar)getApplication()).deviceType == 1 ){
    			if(skin == Configuration.ORIENTATION_LANDSCAPE){
//    				this.getWindow().getDecorView().getRootView().postInvalidate(0,0,800,600,UPDATEMODE_4);
    			}else{
//    				this.getWindow().getDecorView().getRootView().postInvalidate(0, 0, 600, 800, UPDATEMODE_4);
    			}
    		}
    	}
}
