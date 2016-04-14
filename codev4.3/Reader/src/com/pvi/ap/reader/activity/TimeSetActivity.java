package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.pvi.ap.reader.data.common.Logger;
import org.apache.commons.httpclient.HttpException;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviStatusBar;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.external.manager.CPManager;
import android.os.Handler;
import android.os.Message;
/**黄辉设计的UI
 * 时间设置界面类<br>
 * @author 彭见宝
 * @since 2010-11-12
 * @version V1.0.0
 * (C)Copyright 2010-2013, by www.pvi.com.tw
 * @since 2011-04-02 @author 刘剑雄
 */
@SuppressWarnings("unused")
public class TimeSetActivity extends PviActivity {
	
	 public static String service = "com.eink.system.set.TimeSetService";
	 
	 private String dateFormat;
	 
	 private EditText yearEditText;
	 
	 private EditText monthEditText;
	 
	 private EditText dateEditText;
	 
	 private EditText hourEditText;
	 
	 private EditText minuterEditText;
	 
	 private EditText secondEditText;
	 
	 private Button format12Button;
	 
	 private Button format24Button;
	 
	 private Button okButton;
	 
	 private Button cancelButton;
	 
	 private Button netButton=null;//获取网络时间
	 
	
	 private TextView dateView;
	 
	 private TextView timeView;
	 private LinearLayout linear_date=null;
	 private LinearLayout linear_time=null;
	
	 private int type = -1;
	 
	 TextView fp_application = null;
		TextView fp_settings = null;
		TextView fp_music = null;
		TextView fp_back = null;
		TextView fp_mean = null;  
		
	 public static String DATE_FORMAT = "DATE_FORMAT";
		
	public static String DATE_FORMAT_12 = "DATE_FORMAT_12";
	
	public static String DATE_FORMAT_24 = "DATE_FORMAT_24";
	Date date=null;
	private LinearLayout all=null;

		   
		   
	public void onResume(){
		super.onResume();
//		if(deviceType==1){
//			this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//		}
		Date now = new Date();
		DateFormat df1 = new SimpleDateFormat("yyyy/MM/dd");
		//DateFormat df2 = new SimpleDateFormat("hh:mm:ss");
		dateView.setText(df1.format(now));
		int h = now.getHours();
		int m = now.getMinutes();
		int s = now.getSeconds();
		
		Calendar nowCalendar = Calendar.getInstance();
		yearEditText.setText(String.valueOf(nowCalendar.get(Calendar.YEAR)));
		monthEditText.setText(String.valueOf(nowCalendar.get(Calendar.MONTH) + 1));
		dateEditText.setText(String.valueOf(nowCalendar.get(Calendar.DATE)));
		hourEditText.setText(String.valueOf(nowCalendar.get(Calendar.HOUR_OF_DAY)));
		minuterEditText.setText(String.valueOf(nowCalendar.get(Calendar.MINUTE)));
		secondEditText.setText(String.valueOf(nowCalendar.get(Calendar.SECOND)));
		
		SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
		String dataFormat = settings.getString(DATE_FORMAT,"");
		if(DATE_FORMAT_12.equals(dataFormat)){
			type = 0;
			dateFormat = "12";
			format12Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
			format24Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
			if(h <= 12){
				timeView.setText(h+":"+m+":"+s+"AM");
			}else{
				timeView.setText((h-12)+":"+m+":"+s+"PM");
			}
		
		}else if(DATE_FORMAT_24.equals(dataFormat)){
			type = 1;
			dateFormat = "24";
			format12Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
			format24Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
			timeView.setText(h+":"+m+":"+s);
		}
		
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
      
        bundleToSend.putString("sender", TimeSetActivity.this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
        sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
        
        dateView.setFocusable(true);
        dateView.setFocusableInTouchMode(true);
        dateView.requestFocus();
	}	
	protected void onPause() {
		super.onPause();
		closePopmenu();
		//hideInput();
	}
	/**
	 * 获取网络时间
	 * @return
	 */
	  public Date getNetDate(){
		  Date date=null;
 	    	date=CPManager.getServerTimeAsDate();
 	    	return date;
 	    }
	 public void onCreate(Bundle savedInstanceState) {
		  setContentView(R.layout.timesetstyle2);
	        super.onCreate(savedInstanceState);
	      
	        
	        yearEditText = (EditText)findViewById(R.id.year);
		 
	        monthEditText = (EditText)findViewById(R.id.month);
		 
	        dateEditText = (EditText)findViewById(R.id.date);
		 
	        hourEditText = (EditText)findViewById(R.id.hour);
		 
	        minuterEditText = (EditText)findViewById(R.id.miniuter);
		 
	        secondEditText = (EditText)findViewById(R.id.second);
		 
	        format12Button = (Button)findViewById(R.id.format12);
		 
	        format24Button = (Button)findViewById(R.id.format24);
		 
	        okButton = (Button)findViewById(R.id.ok);
		 
	        cancelButton = (Button)findViewById(R.id.cancel);
	        
	        netButton=(Button)findViewById(R.id.nettime);
	        
	   	 	dateView = (TextView)findViewById(R.id.dateview);
		 
	   	 	timeView = (TextView)findViewById(R.id.timeview);
	   	    linear_date=(LinearLayout)findViewById(R.id.linear_date);
	   	    linear_time=(LinearLayout)findViewById(R.id.linear_time);
	   	   if(deviceType==1){
	   		all=(LinearLayout)findViewById(R.id.mainblock);
			//all.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
//			okButton.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			netButton.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			cancelButton.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			yearEditText.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			monthEditText.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			dateEditText.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			minuterEditText.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			hourEditText.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			secondEditText.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			//linear_date.setUpdateMode(View.EINK_WAIT_MODE_WAIT |View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			//linear_time.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//			format12Button.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			format24Button.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
	   	   }

			
	   	   

	   	    format12Button.setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg1==true){
						if(type != 0){
							type = 0;
							dateFormat = "12";
							format12Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
							format24Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
						}	
					}
				}
	   	    	
	   	    });
	   	 	format12Button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(type != 0){
						type = 0;
						dateFormat = "12";
						format12Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
						format24Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
					}
					
				}
			});
	   	 format24Button.setOnFocusChangeListener(new OnFocusChangeListener(){

				@Override
				public void onFocusChange(View arg0, boolean arg1) {
					// TODO Auto-generated method stub
					if(arg1==true){
						if(type != 1){
							type = 1;
							dateFormat = "24";
							format12Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
							format24Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));	
						}	
					}
				}
	   	    	
	   	    });
	   	    format24Button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(type != 1){
						type = 1;
						dateFormat = "24";
						format12Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.notcheck));	
						format24Button.setBackgroundDrawable(getResources().getDrawable(R.drawable.check));
					}
				}
			});
	   	  
	   	    netButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//netButton.setEnabled(false);
				final	Handler  listHandler = new Handler(){  
				          
					        public void handleMessage(Message msg) {
					        	PviAlertDialog dialog = new PviAlertDialog(getParent());
				        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
				        		dialog.setCanClose(true);
				        		dialog.setMessage("网络获取时间失败");// 设置自定义对话框的样式
				        		dialog.show();
					        }
					        
					 };
					 final	Handler  handler = new Handler(){  
				          
					        public void handleMessage(Message msg) {
					        	int m_y = 0;
								int m_month = 0;
								int m_d = 0;
								int m_h = 0;
								int m_m = 0;
								int m_s = 0;
								m_y=date.getYear()+1900;
								m_month=date.getMonth()+1;
								m_d=date.getDate();
								m_h=date.getHours();
								m_m=date.getMinutes();
								m_s=date.getSeconds();
								Logger.v("m_y","m_y="+m_y);
								yearEditText.setText(String.valueOf(m_y));
								monthEditText.setText(String.valueOf(m_month));
								dateEditText.setText(String.valueOf(m_d));
								hourEditText.setText(String.valueOf(m_h));
								minuterEditText.setText(String.valueOf(m_m));
								secondEditText.setText(String.valueOf(m_s));
								if(deviceType==1){
//									TimeSetActivity.this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
								}
								//netButton.setEnabled(true);
					        }
					        
					 };
					Thread getDate= new Thread(){
						public void run() {
							 
							try {
								
								date=CPManager.getServerTime();
							} catch (HttpException e) {
								// TODO Auto-generated catch block
								listHandler.sendEmptyMessage(0);
								e.printStackTrace();
								return ;
							}catch (SocketTimeoutException e) {
								listHandler.sendEmptyMessage(0);
								e.printStackTrace();
								return;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								listHandler.sendEmptyMessage(0);
								e.printStackTrace();
								return ;
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								listHandler.sendEmptyMessage(0);
								e.printStackTrace();
								return ;
							}
							if(date==null){
								listHandler.sendEmptyMessage(0);
								
								return ;
							}	
							handler.sendEmptyMessage(0);
						}
					};
					getDate.start();

					
				}
	   	    	
	   	    	
	   	    });
	   	    
	   	    okButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					SharedPreferences settings = getSharedPreferences(Config.getString("configFileName"), 0); 
					SharedPreferences.Editor editor = settings.edit();
					
					int m_y = 0;
					int m_month = 0;
					int m_d = 0;
					int m_h = 0;
					int m_m = 0;
					int m_s = 0;
					
					
					try{
						
						m_y = Integer.valueOf(yearEditText.getText().toString());
						
						  if(m_y<1000||m_y>=10000){
							
							PviAlertDialog dialog = new PviAlertDialog(getParent());
			        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			        		dialog.setCanClose(true);
			        		dialog.setMessage("您填入的'年'格式不正确");// 设置自定义对话框的样式
			        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
			                         new android.content.DialogInterface.OnClickListener() {

			                             @Override
			                             public void onClick(DialogInterface dialog,
			                                     int which) {
			                                 // TODO Auto-generated method stub
			                            	 dialog.dismiss();
			                             }

			                         });
			        		// 显示对话框
			        		dialog.show();
			        		return ;
			        		}
					}catch(Exception e){
						Log.i("年输入错误", e.toString());
						PviAlertDialog dialog = new PviAlertDialog(getParent());
		        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		        		dialog.setCanClose(true);
		        		dialog.setMessage("您填入的'年'格式不正确");// 设置自定义对话框的样式
		        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
		                         new android.content.DialogInterface.OnClickListener() {

		                             @Override
		                             public void onClick(DialogInterface dialog,
		                                     int which) {
		                                 // TODO Auto-generated method stub
		                            	 dialog.dismiss();
		                             }

		                         });
		        		// 显示对话框
		        		dialog.show();
		        		return;
					}
					
					try{
						m_month = Integer.valueOf(monthEditText.getText().toString());
						if(m_month<1||m_month>12){
							PviAlertDialog dialog = new PviAlertDialog(getParent());
			        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			        		dialog.setCanClose(true);
			        		dialog.setMessage("您填入的'月'格式不正确");// 设置自定义对话框的样式
			        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
			                         new android.content.DialogInterface.OnClickListener() {

			                             @Override
			                             public void onClick(DialogInterface dialog,
			                                     int which) {
			                                 // TODO Auto-generated method stub
			                            	 dialog.dismiss();
			                             }

			                         });
			        		// 显示对话框
			        		dialog.show();
			        		return;
						}
					}catch(Exception e){
						Log.i("月输入错误", e.toString());
						PviAlertDialog dialog = new PviAlertDialog(getParent());
		        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		        		dialog.setCanClose(true);
		        		dialog.setMessage("您填入的'月'格式不正确");// 设置自定义对话框的样式
		        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
		                         new android.content.DialogInterface.OnClickListener() {

		                             @Override
		                             public void onClick(DialogInterface dialog,
		                                     int which) {
		                                 // TODO Auto-generated method stub
		                            	 dialog.dismiss();
		                             }

		                         });
		        		// 显示对话框
		        		dialog.show();
		        		return;
					}
					
					try{
						m_d =  Integer.valueOf(dateEditText.getText().toString());
						if(m_d<1||m_d>31){
							PviAlertDialog dialog = new PviAlertDialog(getParent());
			        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			        		dialog.setCanClose(true);
			        		dialog.setMessage("您填入的'日'格式不正确");// 设置自定义对话框的样式
			        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
			                         new android.content.DialogInterface.OnClickListener() {

			                             @Override
			                             public void onClick(DialogInterface dialog,
			                                     int which) {
			                                 // TODO Auto-generated method stub
			                            	 dialog.dismiss();
			                             }

			                         });
			        		// 显示对话框
			        		dialog.show();
			        		return;
						}
					}catch(Exception e){
						Log.i("日输入错误", e.toString());
						PviAlertDialog dialog = new PviAlertDialog(getParent());
		        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		        		dialog.setCanClose(true);
		        		dialog.setMessage("您填入的'日'格式不正确");// 设置自定义对话框的样式
		        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
		                         new android.content.DialogInterface.OnClickListener() {

		                             @Override
		                             public void onClick(DialogInterface dialog,
		                                     int which) {
		                                 // TODO Auto-generated method stub
		                            	 dialog.dismiss();
		                             }

		                         });
		        		// 显示对话框
		        		dialog.show();
		        		return;
					}
					
					try{
						m_h = Integer.valueOf(hourEditText.getText().toString());
						if(m_h<0||m_h>24){
							PviAlertDialog dialog = new PviAlertDialog(getParent());
			        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			        		dialog.setCanClose(true);
			        		dialog.setMessage("您填入的'时'格式不正确");// 设置自定义对话框的样式
			        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
			                         new android.content.DialogInterface.OnClickListener() {

			                             @Override
			                             public void onClick(DialogInterface dialog,
			                                     int which) {
			                                 // TODO Auto-generated method stub
			                            	 dialog.dismiss();
			                             }

			                         });
			        		// 显示对话框
			        		dialog.show();
			        		return;
						}
					}catch(Exception e){
						Log.i("时输入错误", e.toString());
						PviAlertDialog dialog = new PviAlertDialog(getParent());
		        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		        		dialog.setCanClose(true);
		        		dialog.setMessage("您填入的'时'格式不正确");// 设置自定义对话框的样式
		        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
		                         new android.content.DialogInterface.OnClickListener() {

		                             @Override
		                             public void onClick(DialogInterface dialog,
		                                     int which) {
		                                 // TODO Auto-generated method stub
		                            	 dialog.dismiss();
		                             }

		                         });
		        		// 显示对话框
		        		dialog.show();
		        		return;
					}
					
					try{
						
						m_m = Integer.valueOf(minuterEditText.getText().toString());
						if(m_m<0||m_m>60){
							PviAlertDialog dialog = new PviAlertDialog(getParent());
			        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			        		dialog.setCanClose(true);
			        		dialog.setMessage("您填入的'分'格式不正确");// 设置自定义对话框的样式
			        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
			                         new android.content.DialogInterface.OnClickListener() {

			                             @Override
			                             public void onClick(DialogInterface dialog,
			                                     int which) {
			                                 // TODO Auto-generated method stub
			                            	 dialog.dismiss();
			                             }

			                         });
			        		// 显示对话框
			        		dialog.show();
			        		return;
						}
					}catch(Exception e){
						Log.i("分输入错误", e.toString());
						PviAlertDialog dialog = new PviAlertDialog(getParent());
		        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		        		dialog.setCanClose(true);
		        		dialog.setMessage("您填入的'分'格式不正确");// 设置自定义对话框的样式
		        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
		                         new android.content.DialogInterface.OnClickListener() {

		                             @Override
		                             public void onClick(DialogInterface dialog,
		                                     int which) {
		                                 // TODO Auto-generated method stub
		                            	 dialog.dismiss();
		                             }

		                         });
		        		// 显示对话框
		        		dialog.show();
		        		return;
					}
					
					try{
						m_s = Integer.valueOf(secondEditText.getText().toString());
						if(m_s<0||m_s>60){
							PviAlertDialog dialog = new PviAlertDialog(getParent());
			        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
			        		dialog.setCanClose(true);
			        		dialog.setMessage("您填入的'秒'格式不正确");// 设置自定义对话框的样式
			        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
			                         new android.content.DialogInterface.OnClickListener() {

			                             @Override
			                             public void onClick(DialogInterface dialog,
			                                     int which) {
			                                 // TODO Auto-generated method stub
			                            	 dialog.dismiss();
			                             }

			                         });
			        		// 显示对话框
			        		dialog.show();
			        		return;
						}
					}catch(Exception e){
						Log.i("秒输入错误", e.toString());
						PviAlertDialog dialog = new PviAlertDialog(getParent());
		        		dialog.setTitle(getResources().getString(R.string.systemconfig_pop_message));
		        		dialog.setCanClose(true);
		        		dialog.setMessage("您填入的'秒'格式不正确");// 设置自定义对话框的样式
		        		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.system_soft_unauthorized),
		                         new android.content.DialogInterface.OnClickListener() {

		                             @Override
		                             public void onClick(DialogInterface dialog,
		                                     int which) {
		                                 // TODO Auto-generated method stub
		                            	 dialog.dismiss();
		                             }

		                         });
		        		// 显示对话框
		        		dialog.show();
		        		return;
					}

					Intent i = new Intent(service);
					Bundle b = new Bundle();
					b.putInt("YEAR", m_y);
					b.putInt("MONTH", m_month-1);
					b.putInt("DATE", m_d);
					b.putInt("HOUR", m_h);
					b.putInt("MINUTER", m_m);
					b.putInt("SECOND", m_s);
					b.putString("DATAFORMAT", dateFormat);
					i.putExtras(b);
					startService(i);
					
					if(type == 0){
						 editor.putString(DATE_FORMAT, DATE_FORMAT_12);  
						 editor.commit();
					}else if(type == 1){
						 editor.putString(DATE_FORMAT, DATE_FORMAT_24);  
						 editor.commit();
					}
					
					//通知TimeView设置更新
					sendBroadcast(new Intent(PviStatusBar.TIMEVIEW_CONFIG_CHANGED));
					 if(deviceType==1){
//						   	yearEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							monthEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							dateEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							minuterEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							hourEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							secondEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
							}
					sendBroadcast(new Intent(MainpageActivity.BACK));

				}
			});
	   	 
	   		cancelButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
						 if(deviceType==1){
//						   	yearEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							monthEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							dateEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							minuterEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							hourEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//							secondEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
							}
						
				    Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				    Bundle bundleToSend = new Bundle();
	                bundleToSend.putString("act","com.pvi.ap.reader.activity.SystemConfigActivity");
	                //bundleToSend.putString("haveTitleBar","1");
	                bundleToSend.putString("haveMenuBar","1"); 
	                bundleToSend.putString("startType",  "allwaysCreate");
	                tmpIntent.putExtras(bundleToSend);
	                sendBroadcast(tmpIntent);
				}
			});
	        
	   		if (getRetView() != null) {
				getRetView().setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						 if(deviceType==1){
//							   	yearEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//								monthEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//								dateEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//								minuterEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//								hourEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//								secondEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
								}
						sendBroadcast(new Intent(MainpageActivity.BACK));
					}
				});
			}
	        //Thread uiLoadDataThread = new Thread(new MyThread());
	        //uiLoadDataThread.start();
	 }
	 
    
	 @Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if(keyCode == event.KEYCODE_BACK){
			 if(deviceType==1){
//			   	yearEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//				monthEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//				dateEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//				minuterEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//				hourEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//				secondEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
				}
			 sendBroadcast(new Intent(MainpageActivity.BACK));
			 return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	public boolean dispatchTouchEvent(MotionEvent ev) {
	        //如果输入法目前打开了，则当前控件之外区域的touch，将触发输入法的隐藏操作
	        if(ev.getAction()==MotionEvent.ACTION_DOWN
	        /*&& ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).isActive()  */      
	        ){
	            //Logger.d(TAG,"坐标对比");
	            final View curFocusedView = getWindow().getDecorView().findFocus();
	            
	            if (curFocusedView != null) {
	                
	                //如果是在输入框上面
	                
	                if(curFocusedView instanceof EditText){
	                    int[] location = { 0, 0 };
	                    curFocusedView.getLocationOnScreen(location);
	                    //Logger.d(TAG,"curFocusedView:"+curFocusedView.toString()+",id:"+curFocusedView.getId()+"; loc x:"+location[0]+" ,y:"+location[1]);
	                    final int touchX = (int) ev.getRawX();
	                    final int touchY = (int) ev.getRawY();
	                    //Logger.d(TAG,"touch x:"+touchX+" ,y:"+touchY);
	                    if (!(touchX > location[0] && touchX < location[0]
	                            + curFocusedView.getWidth()
	                            && touchY > location[1] && touchY < location[1]
	                                    + curFocusedView.getHeight())) {
	                        //Logger.d(TAG, "隐藏之");
	                        PviUiUtil.hideInput(curFocusedView);
	                    	if(deviceType==1){
//	                			getWindow().
//	                			getDecorView()
//	                			.getRootView()
//	                			.invalidate(View.EINK_WAIT_MODE_WAIT |
//	                					View.EINK_WAVEFORM_MODE_GC16 | 
//	                					View.EINK_UPDATE_MODE_FULL); 
	                		}
	                    } else {
	                        //Logger.d(TAG, "仍在输入框内部点击，不隐藏");
	                    }
	                } 
	                
	                
	            }else{
	                //Logger.d(TAG, "no focused view");
	            }
	            
	        }
	        

	        return super.dispatchTouchEvent(ev);


	    }

	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
               if(vTag.equals("back1")){
                if(deviceType==1){
//                   yearEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//                   monthEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//                   dateEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//                   minuterEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//                   hourEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
//                   secondEditText.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL);
                   }
                sendBroadcast(new Intent(MainpageActivity.BACK));   
               }
               
               
               closePopmenu(); 
       
        }};

	@Override
	public OnUiItemClickListener getMenuclick() {
		// TODO Auto-generated method stub
		return this.menuclick;
	}  
	 
}