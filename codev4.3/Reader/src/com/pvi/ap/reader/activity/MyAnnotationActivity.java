package com.pvi.ap.reader.activity;

import java.io.File;
import java.util.ArrayList;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.CommentsInfo;

/**
 * 我的批注<br>
 * @author 彭见宝
 * @since 2010-11-15
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
public class MyAnnotationActivity extends PviActivity implements Pageable{
     
	private final static String TAG="MyAnnotationActivity";
	
	private int orderType = 1;             //默认1  默认按照增加批注的时间倒序排序。用户可选按照书名排序。
	
	private TextView norecord = null;
	
    public static final int pageSize = 7;
    
    private int index = 0;
    
    private int curPage = -1;
    
    private int pages = -1;
    
    private Cursor cur = null; //显示游标
    
    private long delId[] = new long[7];
    
	
	private String [][]data = null;
	
	//private int themeNum = -1;
    private String bookName=null;//书名
 
    private int deviceType;
    private boolean refresh=false;
    private int id=0;
    PviDataList listView;               //view实例
    ArrayList<PviUiItem[]> list; 
    
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id; 
            
            
            if(vTag.equals("gotoMain")){    
               Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                Bundle sndBundle = new Bundle();
                sndBundle.putString("act",
                        "com.pvi.ap.reader.activity.MainpageInsideActivity");
                sndBundle.putString("haveStatusBar", "1");
                sndBundle.putString("startType", "reuse");
                intent.putExtras(sndBundle);
                sendBroadcast(intent);
           }
            
            if(vTag.equals("sortByTime")){    //通过tag来判断是前面xml中配置的哪个菜单
               //按添加批注时间排序
               orderType = 1;
               getData(orderType);
               refresh=true;
               id++;
               clearUIData();
               initUIData();
               closePopmenu();
               
           }
           
           if(vTag.equals("sortByBook")){    //通过tag来判断是前面xml中配置的哪个菜单
               //按书名排序
               orderType = 2;
               getData(orderType);
               refresh=true;
               id++;
               clearUIData();
               initUIData();
               closePopmenu();
               
           }

            
            if(vTag.equals("delAnnotationBook")){    //通过tag来判断是前面xml中配置的哪个菜单
               if(listView.mCurFoucsRow == -1){
                   PviAlertDialog dialog = new PviAlertDialog(getParent());
                   dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
                   dialog.setMessage(getResources().getString(R.string.myannotation_noselect));
                   dialog.setCanClose(true);
                   dialog.show();
                   closePopmenu();
                   return;
               }
               //删除本书所有批注
               TextView tv = new TextView(MyAnnotationActivity.this);
               tv.setTextColor(Color.BLACK);
               tv.setText(getResources().getString(R.string.myannotation_delsureBook));
               PviAlertDialog dialog = new PviAlertDialog(getParent());
               //Dialog dialog = new AlertDialog.Builder(MyAnnotationActivity.this).setTitle(
               //      getResources().getString(R.string.systemconfig_pop_message))
               //dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
               dialog.setTitle("删除本书所有");
               dialog.setCanClose(true);
               dialog.setMessage(getResources().getString(R.string.myannotation_delsureBook));
               //dialog.setView(tv);// 设置自定义对话框的样式
               dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
                         new android.content.DialogInterface.OnClickListener() {

                             @Override
                             public void onClick(DialogInterface dialog,
                                     int which) {
                                 // TODO Auto-generated method stub
                               Logger.i("MyAnnotationActivity.delete", "del by book");
                               
                               String where=CommentsInfo.ContentName+"='"+list.get(listView.mCurFoucsRow)[2].text+"'";
                               getContentResolver().delete(CommentsInfo.CONTENT_URI,where,null);
                               dialog.cancel();
                               getData(orderType);
                               refresh=true;
                               id++;
                               clearUIData();
                               initUIData();
                             }

                         });
               dialog.setButton(DialogInterface.BUTTON_NEUTRAL,getResources().getString(R.string.system_soft_cancel),
                         new android.content.DialogInterface.OnClickListener() {

                             @Override
                             public void onClick(DialogInterface dialog,
                                     int which) {
                                dialog.cancel();
                             }

                         });
               dialog.show();
               dialog.getWindow().setLayout(400,300);
               closePopmenu();
           }
            
            if(vTag.equals("delAnnotationAll")){    //通过tag来判断是前面xml中配置的哪个菜单
               if(listView.mCurFoucsRow == -1){
                   PviAlertDialog dialog = new PviAlertDialog(getParent());
                   dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
                   dialog.setMessage(getResources().getString(R.string.myannotation_noselect));
                   dialog.setCanClose(true);
                   dialog.show();
                   closePopmenu();
                   return;
               }
               //删除所有批注
               TextView tv = new TextView(MyAnnotationActivity.this);
               tv.setTextColor(Color.BLACK);
               tv.setText(getResources().getString(R.string.myannotation_delsureAll));
               PviAlertDialog dialog = new PviAlertDialog(getParent());
               dialog.setCanClose(true);
               //Dialog dialog = new AlertDialog.Builder(MyAnnotationActivity.this).setTitle(
               //      getResources().getString(R.string.systemconfig_pop_message))
               //dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
               dialog.setTitle("删除全部");
               //dialog.setView(tv);// 设置自定义对话框的样式
               dialog.setMessage(getResources().getString(R.string.myannotation_delsureAll));
               dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
                         new android.content.DialogInterface.OnClickListener() {

                             @Override
                             public void onClick(DialogInterface dialog,
                                     int which) {
                                 // TODO Auto-generated method stub
                               Logger.i("MyAnnotationActivity.delete", "del all");
                               getContentResolver().delete(CommentsInfo.CONTENT_URI,null,null);
                               dialog.cancel();
                               getData(orderType);
                               refresh=true;
                               id++;
                               clearUIData();
                               initUIData();
                             }

                         });
               dialog.setButton(DialogInterface.BUTTON_NEUTRAL,getResources().getString(R.string.system_soft_cancel),
                         new android.content.DialogInterface.OnClickListener() {

                             @Override
                             public void onClick(DialogInterface dialog,
                                     int which) {
                                dialog.cancel();
                             }

                         });
               dialog.show();
               dialog.getWindow().setLayout(400,300);
               closePopmenu();
           }   
            if(vTag.equals("delAnnotation")){    //通过tag来判断是前面xml中配置的哪个菜单
               if(listView.mCurFoucsRow == -1){
                   PviAlertDialog dialog = new PviAlertDialog(getParent());
                   dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
                   dialog.setMessage(getResources().getString(R.string.myannotation_noselect));
                   dialog.setCanClose(true);
                   dialog.show();
                   closePopmenu();
                   return;
               }
               //如果点击了菜单里的删除按钮
               Logger.v("listView.mCurFoucsRow", "listView.mCurFoucsRow="+listView.mCurFoucsRow);
               if(listView.mCurFoucsRow == -1){
                   //如果INDEX == -1，通知用户没有选择要删除的批注
                   TextView tv = new TextView(MyAnnotationActivity.this);
                   tv.setText(getResources().getString(R.string.systemconfig_pop_message));
                   Dialog dialog = new AlertDialog.Builder(MyAnnotationActivity.this).setTitle(
                   "删  除")
                   .setView(tv)// 设置自定义对话框的样式
                   .setPositiveButton(getResources().getString(R.string.system_soft_unauthorized),// 设置确定按钮
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog,
                                       int whichButton) {
                   
                                   dialog.cancel();
                               }
                           }).setNeutralButton(getResources().getString(R.string.system_soft_cancel),
                           new DialogInterface.OnClickListener() {
                               public void onClick(DialogInterface dialog,
                                       int whichButton) {
                                   dialog.cancel();
                               }
                           }).create();// 创建按钮
                   // 显示对话框
                   dialog.show();
               }else{
                   //弹出删除确认框，确认后删除
               
                   TextView tv = new TextView(MyAnnotationActivity.this);
                   tv.setTextColor(Color.BLACK);
                   tv.setText(getResources().getString(R.string.myannotation_delsure));
                   PviAlertDialog dialog = new PviAlertDialog(getParent());
                   dialog.setCanClose(true);
                   //Dialog dialog = new AlertDialog.Builder(MyAnnotationActivity.this).setTitle(
                   //      getResources().getString(R.string.systemconfig_pop_message))
                   //dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
                   dialog.setTitle("删  除");
                   //dialog.setView(tv);// 设置自定义对话框的样式
                   dialog.setMessage(getResources().getString(R.string.myannotation_delsure));
                   dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
                             new android.content.DialogInterface.OnClickListener() {

                                 @Override
                                 public void onClick(DialogInterface dialog,
                                         int which) {
                                     // TODO Auto-generated method stub
                                   Logger.i("MyAnnotationActivity.delete", "del ining "+delId[listView.mCurFoucsRow]);
                                   String where=CommentsInfo._ID+"='"+delId[listView.mCurFoucsRow]+"'";
                                   getContentResolver().delete(CommentsInfo.CONTENT_URI,where,null);
                                   dialog.cancel();
                                   getData(orderType);
                                   refresh=true;
                                   id++;
                                   clearUIData();
                                   initUIData();
                                 }

                             });
                   dialog.setButton(DialogInterface.BUTTON_NEUTRAL,getResources().getString(R.string.system_soft_cancel),
                             new android.content.DialogInterface.OnClickListener() {

                                 @Override
                                 public void onClick(DialogInterface dialog,
                                         int which) {
                                    dialog.cancel();
                                 }

                             });
                   dialog.show();
                   dialog.getWindow().setLayout(400,300);
                   
               }   
               
               closePopmenu();
            }  
       
        }};
	

	
   
    public void initUI(){
    	listView= (PviDataList)findViewById(R.id.list);
		list = new ArrayList<PviUiItem[]>();
		//翻页处理
		this.showPager=true;
//		final GlobalVar app = ((GlobalVar) getApplicationContext());        
//		app.pbb.setPageable(this);
//		app.pbb.setItemVisible("prevpage", true);
//		app.pbb.setItemVisible("pagerinfo", true);
//		app.pbb.setItemVisible("nextpage", true);
    	norecord = (TextView)findViewById(R.id.norecord);
  
    	data = new String[7][13];
    }
    
    public void bandEvent(){
    	
        this.setOnPmShow(new OnPmShowListener(){

            @Override
            public void OnPmShow(PviPopupWindow popmenu) {               
              //设置排序子菜单的焦点与orderType一致                
                if(orderType==1){
                    final PviMenuItem vSortByTime = getMenuItem("sortByTime");
                    if(vSortByTime!=null){
                        vSortByTime.isFocuse = true;
                    }                    
                }else if(orderType==2){
                    final PviMenuItem vSortByBook = getMenuItem("sortByBook");
                    if(vSortByBook!=null){
                        vSortByBook.isFocuse = true;
                    }
                }     
                
            }});
    }
    
    
    
    
    public void getData(int type){
    	try{
    	String columns[] = new String[]{ CommentsInfo._ID, 
				 CommentsInfo.UserID, 
				 CommentsInfo.CommentId,
				 CommentsInfo.Comment,
				 CommentsInfo.CommentTime,
				 CommentsInfo.CurrentPage,
				 CommentsInfo.ChapterId,
				 CommentsInfo.ChapterName,
				 CommentsInfo.ContentId,
				 CommentsInfo.ContentName,
				 CommentsInfo.StartPosition,
				 CommentsInfo.EndPosition,
				 CommentsInfo.FilePath,
				 CommentsInfo.CertPath,
				 };
    	
    	String order = null;
    	if(type == 1){
    	    order = CommentsInfo.CommentTime + " DESC";
    	}else if(type == 2){
    	    order = CommentsInfo.ContentName + " ASC,"+CommentsInfo.CommentTime + " DESC";
    	}
    	
    	
    	String where=null;
        if(bookName!=null){
    		where=CommentsInfo.ContentName+"='"+bookName+"'";
    	}
		//初始化游标数据
    	Logger.v(TAG, order);
		cur = managedQuery(CommentsInfo.CONTENT_URI,columns,where,null,order);
		int records = cur.getCount();//总记录条数
		//初始化总页数
		if(records % pageSize == 0){
			pages = records / pageSize;
			if(records == 0){
				pages = 1;
			}
		}else{
			pages = records / pageSize + 1;
		}
		
		//初始化本页页码
	    if(curPage == -1){
			curPage = 0;
		}else if(curPage >= pages){
			curPage = pages-1;
		}
    	}catch(Exception e){
    		Logger.v("getData", e.toString());
    	}
    }
    public void clearUIData(){
        if(deviceType==1){
			if(id==5){
			id=0;	
//			this.getParent().getWindow().getDecorView().getRootView().postInvalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		 }
        }
    	
    	for(int i=0;i<6;i++){
    		for(int j=0;j<13;j++){
    			data[i][j] = null;
    		}
    	}
    	
    }
   
    public void initUIData(){
//    	if(deviceType==1){
//			
//			this.getParent().getWindow().getDecorView().getRootView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//		}
        Logger.d(TAG,"initUIData()");
    	

    	for(int i=0;i<pageSize;i++){
    		delId[i] = -1;
    	}
    	list.clear();
    	int i = 0;
		try{
    	if(cur.moveToPosition(curPage * pageSize)){
			do{
				final String bookName = cur.getString(cur.getColumnIndex(CommentsInfo.ContentName));
				final String comment = cur.getString(cur.getColumnIndex(CommentsInfo.Comment));
				final String  chapterId = cur.getString(cur.getColumnIndex(CommentsInfo.ChapterId));
				final String  chapterName = cur.getString(cur.getColumnIndex(CommentsInfo.ChapterName));
				final String  commentId = cur.getString(cur.getColumnIndex(CommentsInfo.CommentId));
				final String  commentTime = cur.getString(cur.getColumnIndex(CommentsInfo.CommentTime));
				final String  contentId = cur.getString(cur.getColumnIndex(CommentsInfo.ContentId));
				final String  contentName = cur.getString(cur.getColumnIndex(CommentsInfo.ContentName));
				final String  currentPage = cur.getString(cur.getColumnIndex(CommentsInfo.CurrentPage));
				final String  endPosition = cur.getString(cur.getColumnIndex(CommentsInfo.EndPosition));
				final String  startPosition = cur.getString(cur.getColumnIndex(CommentsInfo.StartPosition));
				final String  userID = cur.getString(cur.getColumnIndex(CommentsInfo.UserID));
				final String  filePath = cur.getString(cur.getColumnIndex(CommentsInfo.FilePath));
				final String  certPath = cur.getString(cur.getColumnIndex(CommentsInfo.CertPath));
				data[i][0] = chapterId;
				data[i][1] = chapterName;
				data[i][2] = comment;
				data[i][3] = commentId;
				data[i][4] = commentTime;
				data[i][5] = contentId;
				data[i][6] = contentName;
				data[i][7] = currentPage;
				data[i][8] = endPosition;
				data[i][9] = startPosition;
				data[i][10] = userID;
				data[i][11] = filePath;
				data[i][12] = certPath;
				delId[i] = cur.getLong(cur.getColumnIndex(CommentsInfo._ID));
				 PviUiItem[] items = new PviUiItem[]{
			                new PviUiItem("icon"+i, R.drawable.annotation, 10, 10, 44, 54, null, false, true, null),
			                new PviUiItem("text1"+i, 0, 100, 30, 200, 30, "我是一列文本", false, true, null),
			                new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
			                new PviUiItem("text3"+0, 0, 100, 55, 250, 25, "我是又一列文本", false, true, null)
			        };
				 items[1].text=comment;
				 items[1].textSize=22;
				 items[2].text=bookName;
				 items[2].textSize=19;
				 items[2].textAlign=2;
				 items[3].text=commentTime;
				 items[3].textSize=19;
				 final int ii=i;
				OnClickListener l = new OnClickListener(){

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                    	if(!new File(filePath).exists()){
							PviAlertDialog dialog = new PviAlertDialog(getParent());
			        	    dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			        		dialog.setCanClose(true);
			        		dialog.setMessage("文件不存在，可能已经被删除！");// 设置自定义对话框的样式
			        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
			                         new android.content.DialogInterface.OnClickListener() {

			                             @Override
			                             public void onClick(DialogInterface dialog,
			                                     int which) {
			                                 // TODO Auto-generated method stub
			                            	 dialog.dismiss();
			                             }

			                         });
			        		dialog.show();
							return;	
						}
                    	Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
						Bundle b = new Bundle();
		                //bundleToSend.putString("haveTitleBar","1");
		                b.putString("haveMenuBar","1");                
		                String toGoToClass = null;
						//判断是哪种文件，分别跳向不同文件批注显示的Activity
						if(filePath.endsWith(".txt")){
							toGoToClass = "com.pvi.ap.reader.activity.TxtReaderActivity";
							b.putString("startType","allwaysCreate");
						}else if(filePath.endsWith(".meb")){
							toGoToClass = "com.pvi.ap.reader.activity.MebViewFileActivity";
							b.putString("startType","allwaysCreate");
						}else if(filePath.endsWith(".pdf")){
							toGoToClass = "com.pvi.ap.reader.activity.PDFReadActivity";
							b.putString("startType","reuse");
						}
						b.putString("act",toGoToClass);
						b.putString(CommentsInfo.ChapterId,chapterId);
						b.putString(CommentsInfo.ChapterName,chapterName);
						b.putString(CommentsInfo.Comment,comment);
						b.putString(CommentsInfo.CommentId,commentId);
						b.putString(CommentsInfo.CommentTime,commentTime);
						b.putString(CommentsInfo.ContentId,contentId);
						b.putString(CommentsInfo.ContentName,contentName);
						b.putString(CommentsInfo.CurrentPage,currentPage);
						b.putString(CommentsInfo.EndPosition,endPosition);
						b.putString(CommentsInfo.StartPosition,startPosition);
						b.putString("Offset",startPosition);
						b.putString(CommentsInfo.UserID,userID);
						b.putString(CommentsInfo.FilePath,filePath);
						b.putString(CommentsInfo.CertPath,certPath);
						b.putString("FromPath", "2");
					    tmpIntent.putExtras(b);
						sendBroadcast(tmpIntent);
                    }
                    
                };
                items[1].l = l;
                items[2].l = l;
                items[3].l = l;
                list.add(items);
                listView.setData(list);
                i++;
			}while(i<pageSize && cur.moveToNext());
			
		}
    	
    	cur.close();
		}catch(Exception e){
			Logger.v("initUIData", e.toString());
		}
    		if(i == 0||cur.getCount()<=0){
    			norecord.setVisibility(View.VISIBLE);
    			listView.setVisibility(View.GONE);
    		}else{
    			norecord.setVisibility(View.INVISIBLE);
    			listView.setVisibility(View.VISIBLE);
    		}
    		
    		final GlobalVar app = ((GlobalVar) getApplicationContext()); 
            updatePagerinfo(curPage+1+" / "+pages);
	  }
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
    	GlobalVar appState = ((GlobalVar) getApplicationContext());
		deviceType=appState.deviceType;
    	setContentView(R.layout.myannotationstyle2);
    	super.onCreate(savedInstanceState);
	    Bundle bunde = this.getIntent().getExtras();
     	if(bunde.containsKey("bookName")){
   	    bookName=bunde.getString("bookName");
   	        }
   	    Logger.v("bookName","MyAnnotationActivity"+bookName);
       initUI();
       
        }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	bandEvent();
    }
    
    private void prevPage(){
    	if(curPage > 0){
    		
 			curPage --;
 			getData(orderType);
 			refresh=true;
 			id++;
    		clearUIData();
    		initUIData();
    		listView.requestFocus();
 		}
    }
    private void nextPage(){
    	if(curPage < pages - 1){
    		
 			curPage ++;
 			getData(orderType);
 			refresh=true;
 			id++;
    		clearUIData();
    		initUIData();
    		listView.requestFocus();
 		}
    }
    @Override
	
	public boolean onKeyUp(int keyCode, KeyEvent event) {
   
//        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
//            // 向左翻页
//        	if(listView.hasFocus()){
//        	prevPage();
//            return true;
//        	}
//        }
//        
//        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            // 向右翻页
//        	if(listView.hasFocus()){
//        	nextPage();
//        	return true;
//        	}
//        }
//        
        if(keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
        	//按确定键
        	
        	if(listView.mCurFoucsRow != -1 && data[listView.mCurFoucsRow][11]!=null){
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle b = new Bundle();
	            //bundleToSend.putString("haveTitleBar","1");
	            b.putString("haveMenuBar","1");                
	            String toGoToClass = null;
				//判断是哪种文件，分别跳向不同文件批注显示的Activity
				if(data[listView.mCurFoucsRow][11].endsWith(".txt")){
					toGoToClass = "com.pvi.ap.reader.activity.TxtReaderActivity";
					b.putString("startType","allwaysCreate");
				}else if(data[listView.mCurFoucsRow][11].endsWith(".meb")){
					toGoToClass = "com.pvi.ap.reader.activity.MebViewFileActivity";
					b.putString("startType","allwaysCreate");
				}else if(data[listView.mCurFoucsRow][11].endsWith(".pdf")){
					toGoToClass = "com.pvi.ap.reader.activity.PDFReadActivity";
					b.putString("startType","reuse");
				}
				b.putString("act",toGoToClass);
				b.putString(CommentsInfo.ChapterId,data[listView.mCurFoucsRow][0]);
				b.putString(CommentsInfo.ChapterName,data[listView.mCurFoucsRow][1]);
				b.putString(CommentsInfo.Comment,data[listView.mCurFoucsRow][2]);
				b.putString(CommentsInfo.CommentId,data[listView.mCurFoucsRow][3]);
				b.putString(CommentsInfo.CommentTime,data[listView.mCurFoucsRow][4]);
				b.putString(CommentsInfo.ContentId,data[listView.mCurFoucsRow][5]);
				b.putString(CommentsInfo.ContentName,data[listView.mCurFoucsRow][6]);
				b.putString(CommentsInfo.CurrentPage,data[listView.mCurFoucsRow][7]);
				b.putString(CommentsInfo.EndPosition,data[listView.mCurFoucsRow][8]);
				b.putString(CommentsInfo.StartPosition,data[listView.mCurFoucsRow][9]);
				b.putString("Offset",data[listView.mCurFoucsRow][9]);
				b.putString(CommentsInfo.UserID,data[listView.mCurFoucsRow][10]);
				b.putString(CommentsInfo.FilePath,data[listView.mCurFoucsRow][11]);
				b.putString(CommentsInfo.CertPath,data[listView.mCurFoucsRow][12]);
				b.putString("FromPath", "2");
				tmpIntent.putExtras(b);
				 sendBroadcast(tmpIntent);
        	}
        }
        return super.onKeyUp(keyCode, event);
    }
    
    
    @Override
    protected void onPause() {
    	closePopmenu();
        super.onPause();
        refresh=false;
        id=0;
        bookName=null;
       // curPage = -1;
       
    }
    public void showtip(String msg){
    	Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", msg);
		msgIntent.putExtras(sndbundle);
		sendBroadcast(msgIntent);
    }
    @Override
    protected void onResume() {
    	showTip("进入我的批注，请稍候...",2000);
    	super.onResume();
        getData(orderType);
		clearUIData();
		initUIData();
		showme();
		
    }
    public OnUiItemClickListener getMenuclick() {
        return this.menuclick;
    }
	private void showme(){
		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");//TabActivity的类名
        bundleToSend.putString("actTabName", "我的批注");
        bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
	}

	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		this.nextPage();
	}

	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		this.prevPage();
	}
}