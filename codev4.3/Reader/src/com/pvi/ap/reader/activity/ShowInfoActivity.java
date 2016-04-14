/**
 * 
 */
package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author rd001
 *
 */
public class ShowInfoActivity extends Activity {
	private static final String LOGTAG = "ShowInfoActivity";
	private Intent revIntent = null;
	private Bundle revBundle = null;
	private Handler mHandler;
	private String infotype = "0";
	private String ms_infoTitle = "";
	private StringBuffer msb_infoContent = new StringBuffer();
	private TextView mtv_content = null;
	private TextView mtv_infoTitle = null;
	private WebView wv_content = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,   
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
		setContentView(R.layout.showinfo);
		
		mtv_infoTitle = (TextView)findViewById(R.id.title);
		mtv_content = (TextView)findViewById(R.id.content);
		wv_content = (WebView)findViewById(R.id.wvContent);
		bindEvent();		
		
		//recieve
		revIntent = this.getIntent();
		revBundle = revIntent.getExtras();
		if (revBundle != null) {
			infotype = revBundle.getString("type");
			if(infotype==null){
				infotype="0";
			}
		}

		if(infotype.equals("2")){
			ms_infoTitle = "服务协议";
		}else if(infotype.equals("3")){
			ms_infoTitle = "版权信息";
		}else if(infotype.equals("4")){
			ms_infoTitle = "用户隐私";
		}else if(infotype.equals("0")){
			ms_infoTitle = "使用帮助";
			msb_infoContent.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><div align=\"center\">帮助信息内容/帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/<br>帮助信息内容/帮助信息内容/<br><br><br><br>帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/<br>帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/<br>帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/<br>帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/<br>帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/<br>帮助信息内容/帮助信息内容/<br><br><br><br>帮助信息内容/帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/<br>帮助信息内容/<br>帮助信息内容/帮助信息内容/<br>帮助信息内容/帮助信息内容/帮助信息内容/<br></div>");
		}
		
		mtv_infoTitle.setText(ms_infoTitle);
		
		mHandler = new Handler();
		setUIData();
		
	}
	
	private void setUIData() {
		new Thread() {
			public void run() {
				mHandler.post(getData);
			}
		}.start();
	}
	
	private Runnable getData = new Runnable() {
		@Override
		public void run() {			
			
			if(infotype.equals("0")){
				try{
			        final String mimeType = "text/html";
			        final String encoding = "utf-8";
					wv_content.loadData(msb_infoContent.toString(), mimeType, encoding);
					
				}catch (Exception e) {
					Log.e(LOGTAG, e.getMessage());
				}
			}else{
				// read data from remote
				
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
				
				ahmNamePair.put("type", infotype);
				
				HashMap responseMap = null;
				try {
					responseMap = CPManager.getHandsetProperties(ahmHeaderMap, ahmNamePair);
					if (!responseMap.get("result-code").toString().contains("result-code: 0")) {
						Log.i(LOGTAG, responseMap.get("result-code").toString());
						return ;
					}
				} catch (HttpException e) {
					Log.e(LOGTAG, e.getMessage());
					return ;
				} catch (IOException e) {
					Log.e(LOGTAG, e.getMessage());
					return ;
				}
				byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
				
				try{
			        final String mimeType = "text/html";
			        final String encoding = "utf-8";
			       
			        String myContent = new String(responseBody);
			        myContent = Html.fromHtml(myContent).toString();
					wv_content.loadData(myContent, mimeType, encoding);
				}catch (Exception e) {
					Log.e(LOGTAG, e.getMessage());
				}
			}
		}
	};

	private void bindEvent(){
		Button b_return = (Button)findViewById(R.id.b_return);
		b_return.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		}
}
