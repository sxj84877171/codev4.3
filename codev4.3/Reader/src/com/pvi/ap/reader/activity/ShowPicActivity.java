package com.pvi.ap.reader.activity;
/**
 * 打开图片
 * @author 刘剑雄
 *
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.QuitSortComparator;
import dalvik.system.VMRuntime;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowPicActivity extends PviActivity {

	private ImageView imageView=null;
	
	private String path=null;
	private String size=null;
	private String dirPath = "";
	private String searchkey = "";
	private Bitmap pngBM=null;
	private float scaleWidth=1,scaleHeight=1;
	private int id=0;
	private int picNum=0;//当前第几张图片
	LinearLayout showpic=null;
	RelativeLayout absolut=null;
	RelativeLayout statusbar=null;
	ArrayList<HashMap<String, Object>> picList = new ArrayList<HashMap<String, Object>>();
	private final static float TARGET_HEAP_UTILIZATION = 0.75f; 
	TextView fp_big = null;
	TextView fp_smoll = null;
	TextView fp_all = null;
    private int priWidth,priHeight;//刚进去的原始宽和高
	 private TextView []CatButton = new TextView[6];//tab
	// private int themeNum=1;//换肤参数
	 private ImageButton preImage = null;
	 private ImageButton nextImage = null;
	
	 private TextView curpage;// 当前页码
		private TextView pages;// 总页
		private TextView textView02;//
	 private int count=0;
	 private QuitSortComparator sortUtil = new QuitSortComparator("isdir","name","time");
	 private int deviceType;
	 private Handler listHandler;
	 private boolean flag=false;//控制全屏变量
//	 private final static int UPDATEMODE_4 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL;
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

	}
	protected void onResume() {
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		
		super.onResume();
		Logger.v("onResume", "onResume");
		Logger.v("flag", flag);

		onCreate(null);
		File extF =  new File(path);
		if(!extF.exists()){
			Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
			Bundle bunde=new Bundle();
			bunde.putString("act",  "com.pvi.ap.reader.activity.ResCenterActivity");
			bunde.putString("haveTitleBar","1");
			//bunde.putString("startType",  "allwaysCreate");   
			bunde.putString("actTabName",  "我的图片");  //跳转到我的书签 ，如果去掉这语句，就会跳到 最近阅读   
			intent.putExtras(bunde);
			sendBroadcast(intent);
			return ;
		}
		extF.setLastModified(System.currentTimeMillis());
		Logger.v("time", "time="+extF.lastModified());
		if(deviceType==1){
		// this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		}
		getPicInfo(dirPath);
		picNum=getIntPic(path);
		//getBytesFrom();
		listHandler = new Handler();
		listHandler.post(new MyThread());

		//showme();
//		showpic.setFocusable(true);
//		showpic.setFocusableInTouchMode(true);
//		showpic.requestFocus();
	}
	class MyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			getBytesFrom();
			showme();
		}
		
	};
	public void forwardAct(String c_path,String c_size){
        Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
		
		Bundle sndBundle = new Bundle();
		sndBundle.putString("act",
				"com.pvi.ap.reader.activity.ShowPicActivity");

		sndBundle.putString("path", c_path);
		sndBundle.putString("size", c_size);
		sndBundle.putString("dirPath", dirPath);
		sndBundle.putString("startType", "allwaysCreate");
		sndBundle.putString("searchkey",searchkey );
		intent.putExtras(sndBundle);
		sendBroadcast(intent);
	}
	
//	private static int defaultUpdateMode = View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAVEFORM_MODE_AUTO|View.EINK_WAIT_MODE_NOWAIT|View.EINK_UPDATE_MODE_PARTIAL;
	
	/**
	 * 非全屏  翻页
	 * @param num
	 */
	public void setPage(int num){
		path=(String) picList.get(num).get("url");
		size=(String) picList.get(num).get("size");
		if(deviceType==1){
		    //imageView.invalidate();
		 //getWindow().getDecorView().invalidate(0,0,600, 680, UPDATEMODE_4);
		}
		if(deviceType==1){
//		    showpic.setUpdateMode(defaultUpdateMode);
//	           imageView.setUpdateMode(UPDATEMODE_4);
	           //imageView.invalidate();
	        }
		getBytesFrom();
		//forwardAct(path,size);
	}
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		 GlobalVar appState = ((GlobalVar) getApplicationContext());
		
			deviceType=appState.deviceType;
		   
	          setContentView(R.layout.showpic);
	       
		
		super.onCreate(savedInstanceState);
		Logger.v("onCreate", "onCreate");
		Intent intent=this.getIntent();
		path=intent.getExtras().getString("path");
		size=intent.getExtras().getString("size");
		searchkey=intent.getExtras().getString("searchkey");
		dirPath=intent.getExtras().getString("dirPath");
		Logger.v("dirPath", "dirPath="+dirPath);
		
		VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);   
        VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION);  
		//System.out.println("path="+path);
		imageView=(ImageView)findViewById(R.id.fp_showpic_img);
		if(deviceType==1){
		   //imageView.setUpdateMode(UPDATEMODE_4);
        }
		//imageView.setOnKeyListener(onKeyListener);
		
		showpic = (LinearLayout) findViewById(R.id.showpic);
		if(deviceType==1){
		    //showpic.setUpdateMode(UPDATEMODE_4);
        }
		absolut=(RelativeLayout)findViewById(R.id.absolut);
		 
		statusbar=(RelativeLayout)findViewById(R.id.RelativeLayout01);
		preImage=(ImageButton)findViewById(R.id.prev);
		nextImage=(ImageButton)findViewById(R.id.next);
		this.curpage = (TextView) this.findViewById(R.id.curpage);
		this.pages = (TextView) this.findViewById(R.id.pages);
		this.textView02 = (TextView) this.findViewById(R.id.TextView02);
		this.fp_big = (TextView) findViewById(R.id.pic_big);
		this.fp_smoll = (TextView) this.findViewById(R.id.pic_smoll);
		this.fp_all = (TextView) this.findViewById(R.id.pic_all);
		showpic.setFocusable(true);
		showpic.setFocusableInTouchMode(true);
		showpic.requestFocus();
		if(deviceType==1){
			//absolut.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			//showpic.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			fp_big.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			fp_smoll.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			fp_all.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
		}
        this.preImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
					picNum=picNum-1;
					if(picNum<0){
						picNum=picList.size()-1;
					}
				
					setPage(picNum);
					//preImage.requestFocus();
			}
        	
        });
        
        this.nextImage.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				picNum=picNum+1;
				if(picNum>=picList.size()){
					picNum=0;
				}
				setPage(picNum);
				//nextImage.requestFocus();
			}
        	
        });
		this.fp_big.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pngBM==null){
   					return ;
   				}
   				int bmpWidth=pngBM.getWidth();   //放大后的宽度
				int bmpHeight=pngBM.getHeight();
				Logger.v("priWidth", "priWidth="+priWidth); //原始宽度

				double scale=1.25;
				
				//计算出这次要放大的比例
				scaleWidth=(float)(scaleWidth*scale);       //放大后的比例
				scaleHeight=(float)(scaleHeight*scale);
				
				if(priWidth*scaleWidth<600&&priHeight*scaleHeight<680){
				    Logger.e("A","执行了放大");
				 big(bmpWidth,bmpHeight);
				}else{
				    Logger.e("B","没执行放大");
					scaleWidth=(float)(scaleWidth/scale);
		 			scaleHeight=(float)(scaleHeight/scale);
		 			Toast.makeText(ShowPicActivity.this,"已是最大",Toast.LENGTH_LONG).show();
				}
			 fp_big.requestFocus();
			}
		});
		this.fp_smoll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(pngBM==null){
   	   				return ;
   	   				}	
   				int bmpWidth=pngBM.getWidth();
   				int bmpHeight=pngBM.getHeight();
 
   				double scale=0.8;
   				scaleWidth=(float)(scaleWidth*scale);
   				scaleHeight=(float)(scaleHeight*scale);
   				
   				if(scaleWidth<0.32768||scaleHeight<0.32768){
   					
   					scaleWidth=(float)(scaleWidth/scale);
		 			scaleHeight=(float)(scaleHeight/scale);
   					 Toast.makeText(ShowPicActivity.this,"已是最小",Toast.LENGTH_LONG).show();  
   				 }else {
   					 small(bmpWidth,bmpHeight); 
   				 }
			fp_smoll.requestFocus();
			}
		});

		this.fp_all.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 sendBroadcast(new Intent(MainpageActivity.FULLSCREEN_ON));
				 statusbar.setVisibility(View.INVISIBLE);
				 setContentView(R.layout.allpic_2);
				
				 imageView=(ImageView)findViewById(R.id.fp_showpic_img);				 
				 allPic(path,size);				  
				 flag=true; 
				 movePic();
				 if(deviceType==1){
//                     getWindow().getDecorView().getRootView().invalidate(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
                 } 
			}
		});
      	
		
		 if (getRetView() != null) {
				getRetView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						retBackTo();
					}
				});
			} 
		 movePic();
	}
	/*
	 * 触屏翻页
	 */
	public void movePic(){
		
		LinearLayout layout=null;
		if(flag){
			layout=(LinearLayout)findViewById(R.id.mainblock);
			}else{
				layout=(LinearLayout)findViewById(R.id.showpic);	
			}
		if(layout==null){
			return ;
		}
		layout.setOnTouchListener(new OnTouchListener(){
			int startX = 0 , startY = 0 ,endX = 0 ,endY = 0 ;
			@Override
			public boolean onTouch(View view, MotionEvent event) {
				// TODO Auto-generated method stub
				
				switch(event.getAction()){
				case MotionEvent.ACTION_DOWN: {
					startX = (int)event.getX();
					startY = (int)event.getY();
					
					return true;
				}
				case MotionEvent.ACTION_MOVE:{
					return true;
				}
				case MotionEvent.ACTION_UP:{
					endX = (int)event.getX();
					endY = (int)event.getY();
					Logger.v("startX", "startX="+startX);
					Logger.v("startY", "startY="+startY);
					Logger.v("endX", "endX="+endX);
					Logger.v("endY", "endY="+endY);
					
					if(startX - endX  > 50 && startX - endX >= Math.abs(startY - endY)){
						Logger.v("picNum", "picNum="+picNum);
						picNum=picNum+1;
						
						if(picNum>=picList.size()){
							picNum=0;
						}
						
						path=picList.get(picNum).get("url").toString();
						size=picList.get(picNum).get("size").toString();
						 if(flag){
						  allPic(path, size);
						}else{
							getBytesFrom();
						}
						
					}else if (startY - endY > 50 && startY - endY >= Math.abs(startX - endX)) {
						Logger.v("picNum", "picNum="+picNum);
						picNum=picNum+1;
						
						if(picNum>=picList.size()){
							picNum=0;
						}
						path=picList.get(picNum).get("url").toString();
						size=picList.get(picNum).get("size").toString();
						 if(flag){
							  allPic(path, size);
							}else{
								getBytesFrom();
							}
					}else if(endX - startX  > 50 && endX - startX >= Math.abs(startY - endY)) {
						Logger.v("picNum", "picNum="+picNum);
						picNum=picNum-1;
						
						if(picNum<0){
							picNum=picList.size()-1;
						}
						
						path=picList.get(picNum).get("url").toString();
						size=picList.get(picNum).get("size").toString();
						 if(flag){
							  allPic(path, size);
							}else{
								getBytesFrom();
							}
					}else if(endY - startY > 50 && endY - startY>= Math.abs(startX - endX)) {
						Logger.v("picNum", "picNum="+picNum);
						picNum=picNum-1;
						
						if(picNum<0){
							picNum=picList.size()-1;
						}
						
						path=picList.get(picNum).get("url").toString();
						size=picList.get(picNum).get("size").toString();
						 if(flag){
							  allPic(path, size);
							}else{
								getBytesFrom();
							}
					}
					return true;
				}
				}
				
				return true;
			}
			
		});
		}
		
	public boolean retBackTo(){
		if(flag){
			 sendBroadcast(new Intent(MainpageActivity.FULLSCREEN_OFF));
			 statusbar.setVisibility(View.VISIBLE);
			 setContentView(R.layout.showpic);
			 
			 showpic = (LinearLayout) findViewById(R.id.showpic);
			 absolut=(RelativeLayout)findViewById(R.id.absolut);
			 //statusbar=(RelativeLayout)findViewById(R.id.RelativeLayout01);
			 preImage=(ImageButton)findViewById(R.id.prev);
			 nextImage=(ImageButton)findViewById(R.id.next);
			 curpage = (TextView) this.findViewById(R.id.curpage);
			 pages = (TextView) this.findViewById(R.id.pages);
			 textView02 = (TextView) this.findViewById(R.id.TextView02);
			 fp_big = (TextView) findViewById(R.id.pic_big);
             fp_smoll = (TextView) this.findViewById(R.id.pic_smoll);
			 fp_all = (TextView) this.findViewById(R.id.pic_all);
			 id=0;
			 imageView=(ImageView)findViewById(R.id.fp_showpic_img);
			 
			 preImage.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						
							picNum=picNum-1;
							if(picNum<0){
								picNum=picList.size()-1;
							}
						
							setPage(picNum);
							//preImage.requestFocus();
					}
		        	
		        });
		        
		        nextImage.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						picNum=picNum+1;
						if(picNum>=picList.size()){
							picNum=0;
						}
						setPage(picNum);
						//nextImage.requestFocus();
					}
		        	
		        });
				fp_big.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(pngBM==null){
		   					return ;
		   				}
		   				int bmpWidth=pngBM.getWidth();
						int bmpHeight=pngBM.getHeight();
						//Logger.v("bmpWidth", "bmpWidth="+bmpWidth+" "+"bmpHeight="+bmpHeight);
                        if(bmpWidth>600){
                        	bmpWidth=600;
                        }
                        if(bmpHeight>680){
                        	bmpHeight=680;
                        }
						double scale=1.25;
						
						//计算出这次要放大的比例
						scaleWidth=(float)(scaleWidth*scale);
						scaleHeight=(float)(scaleHeight*scale);
						if(priWidth*scaleWidth<600&&priHeight*scaleHeight<800){
							
							//Logger.v("id", "id="+id);
							big(bmpWidth,bmpHeight);
						}else{
							scaleWidth=(float)(scaleWidth/scale);
				 			scaleHeight=(float)(scaleHeight/scale);
						Toast.makeText(ShowPicActivity.this,"已是最大",Toast.LENGTH_LONG).show();
						}
					 fp_big.requestFocus();
					}
				});
				fp_smoll.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(pngBM==null){
		   	   				return ;
		   	   				}	
		   				int bmpWidth=pngBM.getWidth();
		   				int bmpHeight=pngBM.getHeight();
		   				//Logger.v("bmpWidth", "bmpWidth="+bmpWidth+" "+"bmpHeight="+bmpHeight);
		   			 if(bmpWidth>600){
                     	bmpWidth=600;
                     }
                     if(bmpHeight>680){
                     	bmpHeight=680;
                     }

		   				double scale=0.8;
		   				scaleWidth=(float)(scaleWidth*scale);
		   				scaleHeight=(float)(scaleHeight*scale);
		   				
		   				if(scaleWidth<0.32768||scaleHeight<0.32768){
		   					
		   					scaleWidth=(float)(scaleWidth/scale);
				 			scaleHeight=(float)(scaleHeight/scale);
		   					 Toast.makeText(ShowPicActivity.this,"已是最小",Toast.LENGTH_LONG).show();  
		   				 }else {
		   					
		   					 small(bmpWidth,bmpHeight); 
		   				 }
					fp_smoll.requestFocus();
					}
				});

				fp_all.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						 sendBroadcast(new Intent(MainpageActivity.FULLSCREEN_ON));
						 statusbar.setVisibility(View.INVISIBLE);
						 setContentView(R.layout.allpic_2);
						
						 imageView=(ImageView)findViewById(R.id.fp_showpic_img);

						 allPic(path,size);
						 flag=true; 
						 movePic(); 
						 
			             if(deviceType==1){
//			                     getWindow().getDecorView().getRootView().invalidate(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			             } 

					}
				});
				final View fp_settings =  findViewById(R.id.fp_settings);
				if(fp_settings!=null){
					fp_settings.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend.putString("startType", "allwaysCreate");
							bundleToSend.putString("actID", "ACT15000");
							tmpIntent.putExtras(bundleToSend);
							sendBroadcast(tmpIntent);
							tmpIntent = null;
							bundleToSend = null;
						}
					});
				}
				final View fp_music =  findViewById(R.id.fp_music);
				if(fp_music!=null){
					fp_music.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend.putString("actID", "ACT13200");
							tmpIntent.putExtras(bundleToSend);
							sendBroadcast(tmpIntent);
							tmpIntent = null;
							bundleToSend = null;
						}
					});
					}
				final View	retView =  findViewById(R.id.back);
				if(retView !=null){
					retView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							sendBroadcast(new Intent(MainpageActivity.BACK));
						}
					});
					}
				final View tv_menuBtn =  findViewById(R.id.menubtn);
				if(tv_menuBtn!=null){
					tv_menuBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							menupan();
						}
					});
					}
			// Logger.v("count", "count="+count);
			if(deviceType==1){
				//getWindow().getDecorView().getRootView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
			  }	
			 getBytesFrom();
			 showpic.setFocusable(true);
			 showpic.setFocusableInTouchMode(true);
			 showpic.requestFocus();
			 flag=false; 
			 movePic();
			 return  true ;
		}
		sendBroadcast(new Intent(MainpageActivity.BACK));
		return true ;	
		
	}
	private void small(int bw,int bh){
		try{
/*		    if(deviceType==1){
                showpic.setUpdateMode(UPDATEMODE_4);
                imageView.setUpdateMode(defaultUpdateMode);
            }*/
		    
			 System.gc();
		
		//产生resize后的Bitmap对象
		Matrix matrix=new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizeBmp=Bitmap.createBitmap(pngBM,0, 0, bw, bh, matrix, true);
        /*showpic = (LinearLayout) findViewById(R.id.showpic);
		 absolut=(RelativeLayout)findViewById(R.id.absolut);
        if(id==0){
			showpic.removeView(imageView);
			absolut.removeView(showpic);
			
		}
		else{
			showpic.removeView((ImageView)findViewById(id));
			absolut.removeView(showpic);
			
		}
	
		id++;
	
		ImageView image=new ImageView(ShowPicActivity.this);
		
		image.setId(id);*/
//		if(deviceType==1){
//			//absolut.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			image.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//		  }
		priWidth=resizeBmp.getWidth();priHeight=resizeBmp.getHeight();
		imageView.setImageBitmap(resizeBmp);
		
/*		image.setImageBitmap(resizeBmp);
		showpic.addView(image);
		absolut.addView(showpic);
		setContentView(absolut);*/
		resizeBmp=null;
		showpic.setFocusable(true);
		showpic.setFocusableInTouchMode(true);
		showpic.requestFocus();
		}catch(OutOfMemoryError o){
        	o.printStackTrace();
        	System.gc();
        	Toast.makeText(ShowPicActivity.this,"对不起，此操作有误", Toast.LENGTH_SHORT).show();
        	return;
	}catch(Exception  e){
		
		return;
	}
		
		}
	private void big(int bw,int bh){
		try{
		    
/*		    if(deviceType==1){
	            showpic.setUpdateMode(UPDATEMODE_4);
                imageView.setUpdateMode(defaultUpdateMode);
	        }*/
		    
			 System.gc();

		
		Matrix matrix=new Matrix();
		Bitmap resizeBmp=null;
		
			matrix.postScale(scaleWidth, scaleHeight);
		    resizeBmp=Bitmap.createBitmap(pngBM, 0, 0, bw, bh, matrix, true);
	
/*		    showpic = (LinearLayout) findViewById(R.id.showpic);
			 absolut=(RelativeLayout)findViewById(R.id.absolut);

		if(id==0){
			showpic.removeView(imageView);
			absolut.removeView(showpic);
			
		}
		else{
			showpic.removeView((ImageView)findViewById(id));
			absolut.removeView(showpic);
			
		}
	
		id++;

		ImageView image=new ImageView(ShowPicActivity.this);
		
		image.setId(id);*/
//		if(deviceType==1){
//			//absolut.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			image.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			
//		}
		 priWidth=resizeBmp.getWidth();priHeight=resizeBmp.getHeight();
		 
/*		image.setImageBitmap(resizeBmp);*/

		imageView.setImageBitmap(resizeBmp);
		
/*		showpic.addView(image);
		absolut.addView(showpic);
		setContentView(absolut);*/
		
		resizeBmp=null;
		showpic.setFocusable(true);
		showpic.setFocusableInTouchMode(true);
		showpic.requestFocus();
		
		}catch(OutOfMemoryError o){
        	o.printStackTrace();
        	System.gc();
        	Toast.makeText(ShowPicActivity.this,"对不起，此操作有误", Toast.LENGTH_SHORT).show();
        	return;
	}catch(Exception e){
		
		return;
	}
		}
	
  /**
   * 设置图片实际太小
   */
  public void shiji(){
	  try{
		  System.gc();
		  if(Double.parseDouble(size)>2.5*1024*1024){
				 BitmapFactory.Options opts = new BitmapFactory.Options(); 
				    opts.inSampleSize = 4;
				    opts.inJustDecodeBounds = false;   
				    opts.inDither = true;   
				    opts.inPreferredConfig = Bitmap.Config.ARGB_8888; 
				   // pngBM=BitmapFactory.decodeFile(path, opts); 
				    int width=pngBM.getWidth();
					  int height=pngBM.getHeight();
					  if(width>=600&&height>680){
						  width=600;
						  height=680;
					  	}else if(width>=600&&height<=680) {
						  width=600;
					  	}else if(width<=600&&height>=680){
						 height=680;
					  	}
					  
						  pngBM=Bitmap.createScaledBitmap(pngBM, width, height, true); 
	
			 }else
			 if(Double.parseDouble(size)>1024*1024&&Double.parseDouble(size)<2.5*1024*1024){
				 BitmapFactory.Options opts = new BitmapFactory.Options();  
				   opts.inSampleSize = 2;
				    opts.inJustDecodeBounds = false;   
				    opts.inDither = true;   
				    opts.inPreferredConfig = Bitmap.Config.ARGB_8888; 
				  //  pngBM=BitmapFactory.decodeFile(path, opts);
				    int width=pngBM.getWidth();
					  int height=pngBM.getHeight();
					  if(width>=600&&height>680){
						  width=600;
						  height=680;
					  	}else if(width>=600&&height<=680) {
						  width=600;
					  	}else if(width<=600&&height>=680){
						 height=680;
					  	}
					  
						  pngBM=Bitmap.createScaledBitmap(pngBM, width, height, true); 
	
			 }else{
			// pngBM=BitmapFactory.decodeFile(path);
			  int width=pngBM.getWidth();
			  int height=pngBM.getHeight();
			  if(width>=600&&height>680){
				  width=600;
				  height=680;
			  	}else if(width>=600&&height<=680) {
				  width=600;
			  	}else if(width<=600&&height>=680){
				 height=680;
			  	}
			  
				  pngBM=Bitmap.createScaledBitmap(pngBM, width, height, true); 

			 }	
		
	
			if(id==0){
				showpic.removeView(imageView);
				absolut.removeView(showpic);
				
			}
			else{
				showpic.removeView((ImageView)findViewById(id));
				absolut.removeView(showpic);
				
			}
		
			id++;
		
			ImageView image=new ImageView(ShowPicActivity.this);
			
			image.setId(id);

			image.setImageBitmap(pngBM);

			showpic.addView(image);
			absolut.addView(showpic);
			setContentView(absolut);
			scaleWidth=1;scaleHeight=1;
			showpic.setFocusable(true);
			showpic.setFocusableInTouchMode(true);
			showpic.requestFocus();
			}catch(OutOfMemoryError o){
	        	o.printStackTrace();
	        	System.gc();
	        	Toast.makeText(ShowPicActivity.this,"对不起，此操作有误", Toast.LENGTH_SHORT).show();
	        	return;
		}catch(Exception  e){
			
			
			return;
		}
  }
  /**
   * 图片合适太小
   */
  public void fitPic(){
	  
  }
  /**
	 * 全屏显示   （全屏时翻页，也执行此函数）
	 */
	public void allPic(String filePath,String l_size){
		try{
			
			  System.gc();
			  
			  if(deviceType==1){
//		               imageView.setUpdateMode(UPDATEMODE_4);
		      }
			  
			  if(!new File(filePath).exists()){
					final PviAlertDialog dialog = new PviAlertDialog(this);
					dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
					dialog.setCanClose(true);
					imageView.setImageBitmap(null);
					dialog.setMessage("文件不存在");
					dialog.show();
					return ;
				}
			  new File(filePath).setLastModified(System.currentTimeMillis());
			  Bitmap allBM=null;
			  int width,height;
			 // statusbar.setVisibility(View.INVISIBLE);
			  if(Double.parseDouble(l_size)>2.5*1024*1024){
					 BitmapFactory.Options opts = new BitmapFactory.Options(); 
					    opts.inSampleSize = 4;
					    opts.inJustDecodeBounds = false;   
					    opts.inDither = true;   
					    opts.inPreferredConfig = Bitmap.Config.ARGB_8888; 
					    allBM=BitmapFactory.decodeFile(path, opts); 
					     width=allBM.getWidth();
						 height=allBM.getHeight();
						if(width>600||height>800){
						    width=600;
						    height=800;
						    	}
						allBM=Bitmap.createScaledBitmap(allBM, width, height, true);
				 }else
				 if(Double.parseDouble(size)>1024*1024&&Double.parseDouble(size)<2.5*1024*1024){
					 BitmapFactory.Options opts = new BitmapFactory.Options();  
					   opts.inSampleSize = 2;
					    opts.inJustDecodeBounds = false;   
					    opts.inDither = true;   
					    opts.inPreferredConfig = Bitmap.Config.ARGB_8888; 
					 
					    allBM=BitmapFactory.decodeFile(path, opts); 
					     width=allBM.getWidth();
						 height=allBM.getHeight();
						if(width>600||height>800){
						    width=600;
						    height=800;
						    	}
						allBM=Bitmap.createScaledBitmap(allBM, width, height, true);
				 }else{
				
					 allBM=BitmapFactory.decodeFile(path); 
					     width=allBM.getWidth();
						 height=allBM.getHeight();
					 if(width>600||height>800){
						 width=600;
						 height=800;
					  }
					 allBM=Bitmap.createScaledBitmap(allBM, width, height, true);
				 }	
			
		      Logger.v("width", "width="+width);
		      imageView.setImageBitmap(null);
			  imageView.setImageBitmap(allBM);
				System.gc();
				scaleWidth=1;scaleHeight=1;
				}catch(OutOfMemoryError o){
		        	o.printStackTrace();
		        	System.gc();
		        	 imageView.setImageBitmap(null);
		        	Toast.makeText(ShowPicActivity.this,"对不起，此图片不能处理", Toast.LENGTH_SHORT).show();
		        	return;
			}catch(Exception  e){
				
				
				return;
			}
		
	}	
	public void getBytesFrom(){
		try{
			if(!new File(path).exists()){
				final PviAlertDialog dialog = new PviAlertDialog(this);
				dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
				dialog.setCanClose(true);
				imageView.setImageBitmap(null);
				dialog.setMessage("文件不存在");
				dialog.show();
				return ;
			}
			new File(path).setLastModified(System.currentTimeMillis());
			 System.gc();
			 pngBM=null;
			 int width,height;
			 if(Double.parseDouble(size)>2.5*1024*1024){
				 BitmapFactory.Options opts = new BitmapFactory.Options(); 
				    opts.inSampleSize = 4;
				    opts.inJustDecodeBounds = false;   
				    opts.inDither = true;   
				    opts.inPreferredConfig = Bitmap.Config.ARGB_8888; 
				    pngBM=BitmapFactory.decodeFile(path, opts); 
				    width=pngBM.getWidth();
					   height=pngBM.getHeight();
					  if(width>=600&&height>680){
						  width=600;
						  height=680;
					  	}else if(width>=600&&height<=680) {
						  width=600;
					  	}else if(width<=600&&height>=680){
						 height=680;
					  	}
					 
						  pngBM=Bitmap.createScaledBitmap(pngBM, width, height, true); 
				    	

			 }else
			 if(Double.parseDouble(size)>1024*1024&&Double.parseDouble(size)<2.5*1024*1024){
				 BitmapFactory.Options opts = new BitmapFactory.Options();  
				   opts.inSampleSize = 2;
				    opts.inJustDecodeBounds = false;   
				    opts.inDither = true;   
				    opts.inPreferredConfig = Bitmap.Config.ARGB_8888; 
				    pngBM=BitmapFactory.decodeFile(path, opts);
				     width=pngBM.getWidth();
					   height=pngBM.getHeight();
					  if(width>=600&&height>680){
						  width=600;
						  height=680;
					  	}else if(width>=600&&height<=680) {
						  width=600;
					  	}else if(width<=600&&height>=680){
						 height=680;
					  	}
					 
						  pngBM=Bitmap.createScaledBitmap(pngBM, width, height, true); 
				    	

			 }else{
			 pngBM=BitmapFactory.decodeFile(path);
			   width=pngBM.getWidth();
			   height=pngBM.getHeight();
			  if(width>=600&&height>680){
				  width=600;
				  height=680;
			  	}else if(width>=600&&height<=680) {
				  width=600;
			  	}else if(width<=600&&height>=680){
				 height=680;
			  	}
			 
				  pngBM=Bitmap.createScaledBitmap(pngBM, width, height, true); 
		    	

			 }
			 priWidth=width;priHeight=height;
			 imageView.setImageBitmap(null);
			imageView.setImageBitmap(pngBM);
			scaleWidth=1;scaleHeight=1;
			System.gc();
			count=picList.size();
			if(count>0){
				
				curpage.setText((picNum+1)+"");
				pages.setText(count + "");
				textView02.setText("/");
				//page.setText((picNum+1)+"/"+count);
			}else{
				//page.setText(0+"/"+0);
				curpage.setText("1");
				pages.setText("1");
				textView02.setText("/");
			}
			
		}catch(OutOfMemoryError o){
	        	o.printStackTrace();
	        	System.gc();
	        	imageView.setImageBitmap(null);
	        	Toast.makeText(ShowPicActivity.this,"对不起，此图片不能处理", Toast.LENGTH_SHORT).show();
	        	
				picNum=getIntPic(path);
				
				count=picList.size();
				if(count>0){
					
					curpage.setText((picNum+1)+"");
					pages.setText(count + "");
					textView02.setText("/");
					
				}else{
					
					curpage.setText("1");
					pages.setText("1");
					textView02.setText("/");
				}
	        	return;
		}catch(Exception  e){
			imageView.setImageBitmap(null);
			Toast.makeText(ShowPicActivity.this,"对不起，此图片不能处理", Toast.LENGTH_SHORT).show();
			
			Logger.v("path e", path);
			picNum=getIntPic(path);
			Logger.v("picNum e", picNum);
			count=picList.size();
			if(count>0){
				
				curpage.setText((picNum+1)+"");
				pages.setText(count + "");
				textView02.setText("/");
				
			}else{
				
				curpage.setText("1");
				pages.setText("1");
				textView02.setText("/");
			}
			//Logger.v("MyImage1", e.getMessage());
			return;
		}

			
	
		
		
	}	
	public  byte[] getBytesFromInputStream(InputStream is, int bufsiz) 
	throws IOException { 
		int total = 0; 
		byte[] bytes = new byte[4096]; 
		ByteBuffer bb = ByteBuffer.allocate(bufsiz); 

		while (true) { 
			int read = is.read(bytes); 
			if (read == -1) 
				break; 
			bb.put(bytes, 0, read); 
			total += read; 
		} 

		byte[] content = new byte[total]; 
		bb.flip(); 
		bb.get(content, 0, total); 
        bb.clear();
		return content; 
	} 
	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
               if(vTag.equals("all")){
                 
               }
               else if (vTag.equals("size")) {
               //  shiji();
                   Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
                   
                   Bundle sndBundle = new Bundle();
                   sndBundle.putString("act",
                           "com.pvi.ap.reader.activity.ShowPicActivity");
                   sndBundle.putString("startType",  "allwaysCreate");
                   sndBundle.putString("path", path);
                   sndBundle.putString("size", size);
                   sndBundle.putString("dirPath", dirPath);
                   
                   sndBundle.putString("searchkey",searchkey );
                   intent.putExtras(sndBundle);
                   sendBroadcast(intent);
               } else if (vTag.equals("big")) {
                   if(pngBM==null){
                       return ;
                   }
                   int bmpWidth=pngBM.getWidth();
                   int bmpHeight=pngBM.getHeight();
                   Logger.v("bmpWidth", "bmpWidth="+bmpWidth);

                   double scale=1.25;
                   
                   //计算出这次要放大的比例
                   scaleWidth=(float)(scaleWidth*scale);
                   scaleHeight=(float)(scaleHeight*scale);
                   if(bmpWidth*scaleWidth<600&&bmpHeight*scaleHeight<680){
                    big(bmpWidth,bmpHeight);
                   }else{
                       scaleWidth=(float)(scaleWidth/scale);
                       scaleHeight=(float)(scaleHeight/scale);
                   Toast.makeText(ShowPicActivity.this,"已是最大",Toast.LENGTH_SHORT).show();
                   }

               } else if (vTag.equals("small")) {
                   //small();
                   if(pngBM==null){
                       return ;
                       }   
                   int bmpWidth=pngBM.getWidth();
                   int bmpHeight=pngBM.getHeight();
                   
                   if(bmpWidth<=100||bmpHeight<=100){
                       Toast.makeText(ShowPicActivity.this,"已是最小",Toast.LENGTH_SHORT).show(); 
                       return ;
                    }
                   double scale=0.8;
                   scaleWidth=(float)(scaleWidth*scale);
                   scaleHeight=(float)(scaleHeight*scale);
                   
                   if(scaleWidth<0.32768||scaleHeight<0.32768){
                       // scaleWidth=0.32768f;
                        //scaleHeight=0.32768f;
                       scaleWidth=(float)(scaleWidth/scale);
                       scaleHeight=(float)(scaleHeight/scale);
                        Toast.makeText(ShowPicActivity.this,"已是最小",Toast.LENGTH_SHORT).show();  
                    }else {
                        small(bmpWidth,bmpHeight); 
                    }
               }
               closePopmenu(); 
       
        }};


	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Logger.v("onPause", "onPause");
//		try {
//			if (pngBM != null) {
//				if (!pngBM.isRecycled()) {
//					pngBM.recycle();
//				}
//				pngBM = null;
//			}
//		} catch (Exception e) {
//			Logger.e("recycle", e.toString());
//		}
		
		// pageNum=1;
		flag=false;
	}

	@Override
	public OnUiItemClickListener getMenuclick() {
		// TODO Auto-generated method stub
		return this.menuclick;
	}
	private void showtip(String msg)
	{
		Intent msgIntent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", msg);
		msgIntent.putExtras(sndbundle);
		sendBroadcast(msgIntent);
	}
	private void showme()
	{
		Intent intent1 = new Intent(MainpageActivity.HIDE_TIP);
		sendBroadcast(intent1);

		Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
	    bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");
        bundleToSend.putString("actTabName", "查看图片");
        bundleToSend.putString("sender",  ShowPicActivity.this.getClass().getName());
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
		tmpIntent = null;
		bundleToSend = null;
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.gc();
		if(keyCode==event.KEYCODE_DPAD_LEFT){
			if(flag){
				picNum=picNum-1;
				if(picNum<0){
					picNum=picList.size()-1;
				}
				
				path=picList.get(picNum).get("url").toString();
				size=picList.get(picNum).get("size").toString();
				allPic(path, size);
				return true;
			}
			if(showpic.hasFocus()){
			preImage.performClick();
			return true;
           }
		}
		if(keyCode==event.KEYCODE_DPAD_RIGHT){
			if(flag){
				picNum=picNum+1;
				if(picNum>=picList.size()){
					picNum=0;
				}
				path=picList.get(picNum).get("url").toString();
				size=picList.get(picNum).get("size").toString();
				allPic(path, size);
				return true;
			}
			if(showpic.hasFocus()){
			nextImage.performClick();
			return true;
			}
		}
		if(keyCode==event.KEYCODE_MENU){
			if(flag){
				return false;
			}
			menupan();
			if(this.subpopmenu!=null&&!this.popmenu.isShowing()){
				this.subpopmenu.dismiss();
			}
			return true;
		}
		if(keyCode==event.KEYCODE_DPAD_CENTER||keyCode==event.KEYCODE_ENTER){
			if(flag){
				return false;
			}
			if(getCurrentFocus().getId()==R.id.prev){
				preImage.performClick();
				preImage.requestFocus();
			}else if(getCurrentFocus().getId()==R.id.next){
				nextImage.performClick();
				nextImage.requestFocus();
			}else if(getCurrentFocus().getId()==R.id.pic_big){
				fp_big.performClick();
				fp_big.requestFocus();
			}else if(getCurrentFocus().getId()==R.id.pic_smoll){
				fp_smoll.performClick();
				fp_smoll.requestFocus();
			}else if(getCurrentFocus().getId()==R.id.pic_all){
				fp_all.performClick();
				fp_all.requestFocus();
			}
			return true;
		}
		if(keyCode==event.KEYCODE_BACK){
			
			return retBackTo();
		}
		return super.onKeyUp(keyCode, event);
	}
	public void getPicInfo(String path) {
		picList.clear();
		String dir = "";
//		if (path.equals("")) {
//			this.dirPath = "MyPic";
//		} else {
//			this.dirPath = path;
//		}
  
		dir = android.os.Environment.getExternalStorageDirectory().getPath()
				+ "/" + path;
		File mypic = new File(dir);
		mypic.mkdir();

		File[] allpicfile = null;
		allpicfile = mypic.listFiles();
		int length = allpicfile.length;
		String temp = "";
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			temp = allpicfile[i].getName();
			if ((temp.toLowerCase().endsWith("bmp")
					|| temp.toLowerCase().endsWith("gif")
					|| temp.toLowerCase().endsWith("jpeg")
					|| temp.toLowerCase().endsWith("jpg")
					|| temp.toLowerCase().endsWith("tiff")
					|| temp.toLowerCase().endsWith("tif")
					|| temp.toLowerCase().endsWith("png"))&&allpicfile[i].isFile()) {
				Long time = allpicfile[i].lastModified();//最后修改时间
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				map.put("time", formatter.format(time));
				map.put("isdir", "false");
				if (searchkey.equals("")) {
					String url = dir + "/" + temp;
					long size = allpicfile[i].length();
					
					
					map.put("url", url);
					map.put("size", String.valueOf(size));
					map.put("name", allpicfile[i].getName());
					picList.add(map);
				} else {
					if (temp.toLowerCase().contains(searchkey.toLowerCase())) {
						String url = dir + "/" + temp;
						long size = allpicfile[i].length();
						map.put("url", url);
						map.put("size", String.valueOf(size));
						map.put("name", allpicfile[i].getName());
						picList.add(map);
					}
				}
			}

		}
		sortUtil.setDescend2(false);
		Collections.sort(picList,sortUtil);
	}
	public int getIntPic(String path){
		Logger.v("path","path="+ path);
		for (int i = 0; i <picList.size(); i++) {
			HashMap<String, Object> picMap=picList.get(i);
			if(path.equals(picMap.get("url"))){
				return i ;
			}
		}
		return 0;
	}
}
