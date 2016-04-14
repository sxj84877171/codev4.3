package com.pvi.ap.reader.view;

import java.util.Map;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.activity.NetCache;
import com.pvi.ap.reader.activity.WirelessRankActivity;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.common.data_btn;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
/**
 * 无线书城首页Main VIEW
 * @author FlyBird
 * @since 2011-6-20
 */
public class WSMainView extends View implements OnTouchListener,OnKeyListener,OnFocusChangeListener{
	
	private Context context;
	private Resources resources;
	private boolean hasFoucs;
	private PviTextView [][]pviTextView;
	private int foucsTextViewX;
	private int foucsTextViewY;
	private PviTextView foucsTextView;
	
	private Bitmap lineBitmap1;
	private Bitmap lineBitmap2;
	private Bitmap lineBitmap3;
	private Bitmap lineBitmap4;
	private Bitmap lineBitmap5;
	
	private Bitmap recomeBitmap1;
	private Bitmap recomeBitmap2;
	private Bitmap recomeBitmap3;
	private Bitmap recomeBitmap4;
	private Bitmap recomeBitmap5;
	
	private String[] dataTitle = {"","","","","",""};
	private data_btn GoodsDataBtn = new data_btn();     //"精品专栏"信息
	private data_btn recommendDataBtn = new data_btn(); //编辑推荐信息
	private data_btn authorDataBtn = new data_btn();    //名家名作
	private data_btn rankingDataBtn = new data_btn();   //热门排行
	private data_btn catalogDataBtn = new data_btn();   //分类栏目
	private data_btn newsDataBtn = new data_btn();     //包月书包或最新资讯

	

	public WSMainView(Context context) {
		super(context);
	}
	
	public void initSubView(){
		pviTextView = new PviTextView[13][5];
		
		Bitmap bg1 = BitmapFactory.decodeResource(resources, R.drawable.recommendbtn_normal);
		Bitmap bgFoucs1 = BitmapFactory.decodeResource(resources, R.drawable.recommendbtn_sel);
		
		Bitmap bg2 = BitmapFactory.decodeResource(resources, R.drawable.icon_total_book_count);
		Bitmap bgFoucs2 = BitmapFactory.decodeResource(resources, R.drawable.style2_more);
		
		Bitmap bgFoucs3 = BitmapFactory.decodeResource(resources, R.drawable.wsma_sel);
		
		int x,y;
		int t = 39;
		//1
		x = 20;
		y = 5;
		pviTextView[0][0] = new PviTextView(this,bg1,bgFoucs1,
				"",x,y,x+5,y+20,18,new Rect(x, y, x+109, y+26),true,true,true,false,109,-1,0);
		x = 528;
		y = 8;
		pviTextView[0][1] = new PviTextView(this,bg2,bgFoucs2,
				"更多",x,y,x+22,y+15,15,new Rect(x, y-3, x+52, y+19),true,true,true,false,52,-1,0);
		
		//2
		x = 20;
		y = 40;
		pviTextView[1][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		
		x = 20+90+27;
		y = 40;
		pviTextView[1][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		x = 20+90+27+90+27;
		y = 40;
		pviTextView[1][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		x = 20+90+27+90+27+90+27;
		y = 40;
		pviTextView[1][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		x = 580-90;
		y = 40;
		pviTextView[1][4] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		
		//3
		x = 20;
		y = 90;
		pviTextView[2][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		
		x = 20+90+27;
		y = 90;
		pviTextView[2][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		x = 20+90+27+90+27;
		y = 90;
		pviTextView[2][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		x = 20+90+27+90+27+90+27;
		y = 90;
		pviTextView[2][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		x = 580-90;
		y = 90;
		pviTextView[2][4] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+90, y+44),false,false,false,false,90,90,2);
		
		//4
		x = 20;
		y = 150;
		pviTextView[3][0] = new PviTextView(this,bg1,bgFoucs1,
				"",x,y,x+5,y+20,18,new Rect(x, y, x+109, y+26),true,true,true,false,109,-1,0);
		x = 528;
		y = 153;
		pviTextView[3][1] = new PviTextView(this,bg2,bgFoucs2,
				"更多",x,y,x+22,y+15,15,new Rect(x, y-3, x+52, y+20),true,true,true,false,52,-1,0);
		
		//5
		x = 20;
		y = 265;
		pviTextView[4][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y-90, x+110, y+44),false,false,false,false,130,110,0);
		
		x = 136;
		y = 265;
		pviTextView[4][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y-90, x+110, y+44),false,false,false,false,135,110,0);
		
		x = 247;
		y = 265;
		pviTextView[4][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y-90, x+110, y+44),false,false,false,false,135,110,0);
		
		x = 358;
		y = 265;
		pviTextView[4][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y-90, x+110, y+44),false,false,false,false,135,110,0);
		
		x = 469;
		y = 265;
		pviTextView[4][4] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y-90, x+110, y+44),false,false,false,false,135,110,0);
		

		
		
		
		//6
		x = 20;
		y = 320;
		pviTextView[5][0] = new PviTextView(this,bg1,bgFoucs1,
				"",x,y,x+5,y+20,18,new Rect(x, y, x+109, y+26),true,true,true,false,109,-1,0);
		x = 219;
		y = 323;
		pviTextView[5][1] = new PviTextView(this,bg2,bgFoucs2,
				"更多",x,y,x+22,y+15,15,new Rect(x, y-3, x+52, y+22),true,true,true,false,52,-1,0);
		x = 329;
		y = 320;
		pviTextView[5][2] = new PviTextView(this,bg1,bgFoucs1,
				"",x,y,x+5,y+20,18,new Rect(x, y, x+109, y+26),true,true,true,false,109,-1,0);
		x = 528;
		y = 323;
		pviTextView[5][3] = new PviTextView(this,bg2,bgFoucs2,
				"更多",x,y,x+22,y+15,15,new Rect(x, y-3, x+52, y+22),true,true,true,false,52,-1,0);
		
		
		//7
		x = 20;
		y = 353;
		pviTextView[6][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 153;
		y = 353;
		pviTextView[6][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 328;
		y = 353;
		pviTextView[6][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 461;
		y = 353;
		pviTextView[6][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		
		//8
		x = 20;
		y = 403;
		pviTextView[7][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 153;
		y = 403;
		pviTextView[7][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 328;
		y = 403;
		pviTextView[7][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 461;
		y = 403;
		pviTextView[7][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		
		
		//9
		x = 20;
		y = 453;
		pviTextView[8][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 153;
		y = 453;
		pviTextView[8][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 328;
		y = 453;
		pviTextView[8][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 461;
		y = 453;
		pviTextView[8][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,22,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		
		
		//10
		x = 20;
		y = 500;
		pviTextView[9][0] = new PviTextView(this,bg1,bgFoucs1,
				"",x,y,x+5,y+20,18,new Rect(x, y, x+109, y+26),true,true,true,false,109,-1,0);
		x = 219;
		y = 503;
		pviTextView[9][1] = new PviTextView(this,bg2,bgFoucs2,
				"更多",x,y,x+22,y+15,15,new Rect(x, y-3, x+52, y+22),true,true,true,false,52,-1,0);
		x = 329;
		y = 500;
		pviTextView[9][2] = new PviTextView(this,bg1,bgFoucs1,
				"",x,y,x+5,y+20,18,new Rect(x, y, x+109, y+26),true,true,true,false,109,-1,0);
		x = 528;
		y = 503;
		pviTextView[9][3] = new PviTextView(this,bg2,bgFoucs2,
				"更多",x,y,x+22,y+15,15,new Rect(x, y-3, x+52, y+22),true,true,true,false,52,-1,0);
		
		//11
		x = 20;
		y = 533;
		pviTextView[10][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 153;
		y = 533;
		pviTextView[10][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 328;
		y = 533;
		pviTextView[10][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 461;
		y = 533;
		pviTextView[10][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		
		//12
		x = 20;
		y = 583;
		pviTextView[11][0] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 153;
		y = 583;
		pviTextView[11][1] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 328;
		y = 583;
		pviTextView[11][2] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		x = 461;
		y = 583;
		pviTextView[11][3] = new PviTextView(this,null,bgFoucs3,
				"",x,y+t,x,y+30,19,new Rect(x, y, x+120, y+44),false,false,false,false,120,120,0);
		
	}
	public void initSubViewListenser(){
	
		
		pviTextView[0][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
			
			@Override
			public void onClick(PviTextView view, Map inputData) {
				// TODO Auto-generated method stub
				System.out.println("dddddddddddddddddddddddddddddd00 clicked");
				gotoWirelessStore("分类栏目");
			}
		}, null);
				
				pviTextView[0][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						gotoWirelessStore("分类栏目");
					}
				}, null);
				
				pviTextView[1][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd10 clicked");
						if(catalogDataBtn.dataId[0]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[0]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
				pviTextView[1][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd11 clicked");
						if(catalogDataBtn.dataId[1]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[1]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
				
				pviTextView[1][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd12 clicked");
						if(catalogDataBtn.dataId[2]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[2]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
			
				pviTextView[1][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd13 clicked");
						if(catalogDataBtn.dataId[3]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[3]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
				
				pviTextView[1][4].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd12 clicked");
						if(catalogDataBtn.dataId[4]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[4]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
			
				pviTextView[2][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd20 clicked");
						if(catalogDataBtn.dataId[5]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[5]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
				
				pviTextView[2][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd21 clicked");
						if(catalogDataBtn.dataId[6]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[6]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
				
				pviTextView[2][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(catalogDataBtn.dataId[7]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[7]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
				
				pviTextView[2][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(catalogDataBtn.dataId[8]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[8]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
				
				pviTextView[2][4].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(catalogDataBtn.dataId[9]!=null){
							Intent tmpIntent = new Intent(
									MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							bundleToSend
							.putString("act",
							"com.pvi.ap.reader.activity.CatalogHomepageActivity");
							bundleToSend.putString("startType", "allwaysCreate");
							//					bundleToSend.putString("pviapfStatusTip",
							//							getResources().getString(R.string.kyleHint01));
							bundleToSend.putString("haveTitleBar", "1");
							bundleToSend.putString("catalogId", catalogDataBtn.dataId[9]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
						}
					}
				}, null);
			

				pviTextView[3][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						gotoWirelessStore("编辑推荐");
					}
				}, null);
				
				pviTextView[3][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						gotoWirelessStore("编辑推荐");
					}
				}, null);
			
				pviTextView[4][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(recommendDataBtn.dataId[0]!=null){
						    handContentListClick(0);
						}					
					}
				}, null);
				
				pviTextView[4][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(recommendDataBtn.dataId[1]!=null){
						    handContentListClick(1);
						}
					}
				}, null);
				
				pviTextView[4][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(recommendDataBtn.dataId[2]!=null){
						    handContentListClick(2);
						}
					}
				}, null);
				
				pviTextView[4][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {						
						if(recommendDataBtn.dataId[3]!=null){
						    handContentListClick(3);
						}
					}
				}, null);
				
				pviTextView[4][4].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(recommendDataBtn.dataId[4]!=null){
						    handContentListClick(4);
						}
					}
				}, null);
				

				pviTextView[5][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

						gotoWirelessStore("热门排行");
					}
				}, null);
				
				pviTextView[5][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						gotoWirelessStore("热门排行");
					}
				}, null);
				
				pviTextView[5][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						gotoWirelessStore("名家名作");
					}
				}, null);
				
				pviTextView[5][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						gotoWirelessStore("名家名作");
					}
				}, null);
				

				pviTextView[6][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(rankingDataBtn.dataId[0]!=null){
							
							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							tmpIntent.putExtras(bundleToSend);
							bundleToSend.putString("actID", "ACT11000");
							bundleToSend.putString("actTabName", "热门排行");
							WirelessRankActivity.setRankTypeId(rankingDataBtn.dataId[0]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
							
						}
						
						
					}
				}, null);
				
				pviTextView[6][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(rankingDataBtn.dataId[3]!=null){
							
							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							tmpIntent.putExtras(bundleToSend);
							bundleToSend.putString("actID", "ACT11000");
							bundleToSend.putString("actTabName", "热门排行");
							WirelessRankActivity.setRankTypeId(rankingDataBtn.dataId[3]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);							
							
						}
					}
				}, null);
				
				pviTextView[6][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(authorDataBtn.dataId[0]!=null){
						    handAuthorsClick(0);
						}
					}
				}, null);
				
				pviTextView[6][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(authorDataBtn.dataPrimaryBtn[0]!=null){
						    handAuthorsClick(0);
						}
					}
				}, null);
				

				pviTextView[7][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

						if(rankingDataBtn.dataId[1]!=null){
							
							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							tmpIntent.putExtras(bundleToSend);
							bundleToSend.putString("actID", "ACT11000");
							bundleToSend.putString("actTabName", "热门排行");
							WirelessRankActivity.setRankTypeId(rankingDataBtn.dataId[1]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
							
							
						}
					}
				}, null);
				
				pviTextView[7][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(rankingDataBtn.dataId[4]!=null){
							
							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							tmpIntent.putExtras(bundleToSend);
							bundleToSend.putString("actID", "ACT11000");
							bundleToSend.putString("actTabName", "热门排行");
							WirelessRankActivity.setRankTypeId(rankingDataBtn.dataId[4]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
							
						}
					}
				}, null);
				
				pviTextView[7][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(authorDataBtn.dataId[1]!=null){
						    handAuthorsClick(1);
						}
					}
				}, null);
				
				pviTextView[7][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(authorDataBtn.dataPrimaryBtn[1]!=null){
						    handAuthorsClick(1);
						}
					}
				}, null);
				

				pviTextView[8][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(rankingDataBtn.dataId[2]!=null){
							
							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							tmpIntent.putExtras(bundleToSend);
							bundleToSend.putString("actID", "ACT11000");
							bundleToSend.putString("actTabName", "热门排行");
							WirelessRankActivity.setRankTypeId(rankingDataBtn.dataId[2]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
							
							
						}
					}
				}, null);
				
				pviTextView[8][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						// TODO Auto-generated method stub
						System.out.println("dddddddddddddddddddddddddddddd00 clicked");
						if(rankingDataBtn.dataId[5]!=null){
							
							Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
							Bundle bundleToSend = new Bundle();
							tmpIntent.putExtras(bundleToSend);
							bundleToSend.putString("actID", "ACT11000");
							bundleToSend.putString("actTabName", "热门排行");
							WirelessRankActivity.setRankTypeId(rankingDataBtn.dataId[5]);
							tmpIntent.putExtras(bundleToSend);
							context.sendBroadcast(tmpIntent);
							
							
						}
					}
				}, null);
				
				pviTextView[8][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(authorDataBtn.dataId[2]!=null){
							handAuthorsClick(2);
						}
					}
				}, null);
				
				pviTextView[8][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
						if(authorDataBtn.dataPrimaryBtn[2]!=null){
						    handAuthorsClick(2);
						}
					}
				}, null);
				

				pviTextView[9][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
					    gotoWirelessStore("精品专栏");
					}
				}, null);
				
				pviTextView[9][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
					    gotoWirelessStore("精品专栏");
					}
				}, null);
				
				pviTextView[9][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

						if(dataTitle[5].equals("最新资讯"))
						{
							gotoWirelessStore("最新资讯");// 最新资讯
						}
						else
						{
							gotoWirelessStore("包月书包");
						}
					}
				}, null);
				
				pviTextView[9][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

						if(dataTitle[5].equals("最新资讯"))
						{
							gotoWirelessStore("最新资讯");// 最新资讯
						}
						else
						{
							gotoWirelessStore("包月书包");
						}
					}
				}, null);
				

				pviTextView[10][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

					    handGoodsClick(0);
					}
				}, null);
				
				pviTextView[10][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

					    handGoodsClick(2);
					}
				}, null);
				
				pviTextView[10][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

						if(dataTitle[5].equals("最新资讯"))
						{
						    handInfoClick(0);
						}
						else
						{
							handBookPackageInfoListClick(0);
						}
					}
				}, null);
				
				pviTextView[10][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {

						if(dataTitle[5].equals("最新资讯"))
						{
                            handInfoClick(0);
                        }
                        else
                        {
                            handBookPackageInfoListClick(0);
                        }
					}
				}, null);
				

				pviTextView[11][0].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
					    handGoodsClick(1);
					}
				}, null);
				
				pviTextView[11][1].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
					    handGoodsClick(3);
					}
				}, null);
				
				pviTextView[11][2].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
					    if(dataTitle[5].equals("最新资讯"))
                        {
                            handInfoClick(1);
                        }
                        else
                        {
                            handBookPackageInfoListClick(1);
                        }
					}
				}, null);
				
				pviTextView[11][3].setPviTextViewClickListenser(new PviTextViewClickListenser() {
					
					@Override
					public void onClick(PviTextView view, Map inputData) {
					    if(dataTitle[5].equals("最新资讯"))
                        {
                            handInfoClick(1);
                        }
                        else
                        {
                            handBookPackageInfoListClick(1);
                        }
					}
				}, null);

	}
	public WSMainView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public WSMainView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		System.out.println("33333333333");
		this.context = context;
		resources = context.getResources();
		//setBackgroundColor(Color.RED);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setOnTouchListener(this);
		setOnKeyListener(this);
		setOnFocusChangeListener(this);
		
		lineBitmap1 = BitmapFactory.decodeResource(resources, R.drawable.style2_newline);
		lineBitmap2 = BitmapFactory.decodeResource(resources, R.drawable.catabottomline);
		lineBitmap3 = Bitmap.createScaledBitmap(lineBitmap1,253, lineBitmap1.getHeight(), true);
		lineBitmap4 = BitmapFactory.decodeResource(resources, R.drawable.rankbottomline);
		lineBitmap5 = BitmapFactory.decodeResource(resources, R.drawable.wsma_line_vertical);
		
		recomeBitmap1 = BitmapFactory.decodeResource(resources, R.drawable.default_bookcover_6080);
		recomeBitmap2 = recomeBitmap1;
		recomeBitmap3 = recomeBitmap1;
		recomeBitmap4 = recomeBitmap1;
		recomeBitmap5 = recomeBitmap1;
		
		
		initSubView();
		initSubViewListenser();
		requestFocus();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);
		
		drawLine(canvas);
		for(int i=0;i<pviTextView.length;i++){
			for(int j=0;j<pviTextView[i].length;j++){
				if(pviTextView[i][j]!=null){
					pviTextView[i][j].setCanvas(canvas);
				}
			}
		}
		
		for(int i=0;i<pviTextView.length;i++){
			for(int j=0;j<pviTextView[i].length;j++){
				if(pviTextView[i][j]!=null){
					pviTextView[i][j].onDraw();
				}
			}
		}
		
	}
	
	
	public void drawLine(Canvas canvas){
		int x,y;
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(1);
		
		x = 298;
		y = 355;
		canvas.drawBitmap(lineBitmap5, x, y, p);
		
		x = 20;
		y = 29;
		canvas.drawBitmap(lineBitmap1, x, y, p);
		
		x = 20;
		y = 85;
		canvas.drawBitmap(lineBitmap2, x, y, p);
		
		x = 20;
		y = 135;
		canvas.drawBitmap(lineBitmap2, x, y, p);
		
		x = 20;
		y = 174;
		canvas.drawBitmap(lineBitmap1, x, y, p);
		
		x = 20;
		y = 346;
		canvas.drawBitmap(lineBitmap3, x, y, p);
		
		x = 329;
		y = 346;
		canvas.drawBitmap(lineBitmap3, x, y, p);
		
		
		
		////////////////1
		
		x = 20;
		y = 396;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		x = 329;
		y = 396;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		////////////////2
		
		x = 20;
		y = 446;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		x = 329;
		y = 446;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		////////////////3
		
		x = 20;
		y = 496;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		x = 329;
		y = 496;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		
		
		
		x = 20;
		y = 526;
		canvas.drawBitmap(lineBitmap3, x, y, p);
		
		x = 329;
		y = 526;
		canvas.drawBitmap(lineBitmap3, x, y, p);
		
		////////////////1
		
		x = 20;
		y = 576;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		x = 329;
		y = 576;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		////////////////2
		
		x = 20;
		y = 626;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		x = 329;
		y = 626;
		canvas.drawBitmap(lineBitmap4, x, y, p);
		
		////////////////////////////////////////////////////////
		x = 50;
		y = 190;
		canvas.drawRect(x-2, y-3, x+recomeBitmap1.getWidth()+2, y+recomeBitmap1.getHeight()+2, p);
		canvas.drawBitmap(recomeBitmap1, x, y, p);
		
		x = 160;
		y = 190;
		canvas.drawRect(x-2, y-3, x+recomeBitmap1.getWidth()+2, y+recomeBitmap1.getHeight()+2, p);
		canvas.drawBitmap(recomeBitmap2, x, y, p);
		
		x = 270;
		y = 190;
		canvas.drawRect(x-2, y-3, x+recomeBitmap1.getWidth()+2, y+recomeBitmap1.getHeight()+2, p);
		canvas.drawBitmap(recomeBitmap3, x, y, p);
		
		x = 380;
		y = 190;
		canvas.drawRect(x-2, y-3, x+recomeBitmap1.getWidth()+2, y+recomeBitmap1.getHeight()+2, p);
		canvas.drawBitmap(recomeBitmap4, x, y, p);
		
		x = 490;
		y = 190;
		canvas.drawRect(x-2, y-3, x+recomeBitmap1.getWidth()+2, y+recomeBitmap1.getHeight()+2, p);
		canvas.drawBitmap(recomeBitmap5, x, y, p);

		
		
		x = 20;
		y = 310;
		canvas.drawBitmap(lineBitmap2, x, y, p);
		
		
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(MotionEvent.ACTION_DOWN == event.getAction()){
			int x = (int)event.getX();
			int y = (int)event.getY();
			for(int i=0;i<pviTextView.length;i++){
				for(int j=0;j<pviTextView[i].length;j++){
					if(pviTextView[i][j]!=null){
						if(pviTextView[i][j].checkClick(x, y) == true){
							lostFoucs();
							if(foucsTextViewX!=i || foucsTextViewY!=j){
								foucsTextView.lostFoucs();
								foucsTextViewX = i;
								foucsTextViewY = j;
								foucsTextView = pviTextView[i][j];
								foucsTextView.requestFoucs();
								invalidate();
								return true;
							}
							
						
						}
						
					}
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.ACTION_DOWN == event.getAction()){
			if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
				if(foucsTextView != null){
				    //Logger.e("无线书城首页","foucsTextView.text:"+foucsTextView.text);
					foucsTextView.perfomClick();
				}
				return true;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT && findLeftFoucs()){
				invalidate();
				return true;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && findRightFoucs()){
				invalidate();
				return true;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_UP && findUpFoucs()){
				invalidate();
				return true;
			}
			if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN && findDownFoucs()){
				invalidate();
				return true;
			}
			
			return false;
		}
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		if(hasFocus){
			foucsTextViewX = 0;
			foucsTextViewY = 0;
			foucsTextView = pviTextView[foucsTextViewX][foucsTextViewY];
			foucsTextView.requestFoucs();
		}else{
			lostFoucs();
		}
	}
	
	public boolean findLeftFoucs(){
		int i = foucsTextViewY-1;
		while(i>=0){
			PviTextView pv = pviTextView[foucsTextViewX][i];
			if(pv!=null && pv.isFoucsAble()){
				foucsTextView.lostFoucs();
				foucsTextViewY = i;
				foucsTextView = pv;
				foucsTextView.requestFoucs();
				return true;
			}
			i--;
		}
		return false;
	}
	
	public boolean findRightFoucs(){
		int i = foucsTextViewY+1;
		while(i<pviTextView[0].length){
			PviTextView pv = pviTextView[foucsTextViewX][i];
			if(pv!=null && pv.isFoucsAble()){
				foucsTextView.lostFoucs();
				foucsTextViewY = i;
				foucsTextView = pv;
				foucsTextView.requestFoucs();
				return true;
			}
			i++;
		}
		return false;
	}

	public boolean findUpFoucs(){
		int i = foucsTextViewX-1;
		while(i>=0){
			PviTextView pv = pviTextView[i][foucsTextViewY];
			if(pv!=null && pv.isFoucsAble()){
				foucsTextView.lostFoucs();
				foucsTextViewX = i;
				foucsTextView = pv;
				foucsTextView.requestFoucs();
				return true;
			}
			i--;
		}
		return false;
	}
	
	public boolean findDownFoucs(){
		int i = foucsTextViewX+1;
		while(i<pviTextView.length){
			PviTextView pv = pviTextView[i][foucsTextViewY];
			if(pv!=null && pv.isFoucsAble()){
				foucsTextView.lostFoucs();
				foucsTextViewX = i;
				foucsTextView = pv;
				foucsTextView.requestFoucs();
				return true;
			}
			i++;
		}
		return false;
	}
	
	public void lostFoucs(){
		for(int i=0;i<pviTextView.length;i++){
			for(int j=0;j<pviTextView[i].length;j++){
				if(pviTextView[i][j]!=null){
					pviTextView[i][j].lostFoucs();
				}
			}
		}
	}
	
	public void setData(){
		
		//设置标题
		pviTextView[0][0].setText(dataTitle[0]);
		pviTextView[3][0].setText(dataTitle[1]);
		pviTextView[5][0].setText(dataTitle[2]);
		pviTextView[5][2].setText(dataTitle[4]);
		pviTextView[9][0].setText(dataTitle[3]);
		pviTextView[9][2].setText(dataTitle[5]);
		
		//设置分类栏目
		for(int i=0;i<10 && catalogDataBtn.dataPrimary[i]!=null;i++){
			if(i<5){
				pviTextView[1][i].setText(catalogDataBtn.dataPrimary[i]);
			}else{
				pviTextView[2][i-5].setText(catalogDataBtn.dataPrimary[i]);
			}
		}
		
		//设置编辑推荐
		for(int i=0;i<5 && recommendDataBtn.dataPrimary[i]!=null;i++){
			pviTextView[4][i].setText(recommendDataBtn.dataPrimary[i]);
		}
		if(NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[0])!=null){
			Bitmap drawBitmap = NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[0]);
			if(drawBitmap!=null){
				recomeBitmap1 = Bitmap.createScaledBitmap(drawBitmap,60,80, true);
			}
			
		}
		if(NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[1])!=null){
			Bitmap drawBitmap = NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[1]);
			if(drawBitmap!=null){
				recomeBitmap2 = Bitmap.createScaledBitmap(drawBitmap,60,80, true);
			}
			
		}
		if(NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[2])!=null){
			Bitmap drawBitmap = NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[2]);
			if(drawBitmap!=null){
				recomeBitmap3 = Bitmap.createScaledBitmap(drawBitmap,60,80, true);
			}
			
		}
		if(NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[3])!=null){
			Bitmap drawBitmap = NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[3]);
			if(drawBitmap!=null){
				recomeBitmap4 = Bitmap.createScaledBitmap(drawBitmap,60,80, true);
			}
			
		}
		if(NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[4])!=null){
			Bitmap drawBitmap = NetCache.GetNetImage(recommendDataBtn.dataPrimaryBtn[4]);
			if(drawBitmap!=null){
				recomeBitmap5 = Bitmap.createScaledBitmap(drawBitmap,60,80, true);
			}
			
		}
		
		
		//设置热门排行
		for(int i=0;i<6 && rankingDataBtn.dataPrimary[i]!=null;i++){
			if(i==0){
				pviTextView[6][0].setText(rankingDataBtn.dataPrimary[i]);
			}else if(i==1){
				pviTextView[7][0].setText(rankingDataBtn.dataPrimary[i]);
			}else if(i==2){
				pviTextView[8][0].setText(rankingDataBtn.dataPrimary[i]);
			}else if(i==3){
				pviTextView[6][1].setText(rankingDataBtn.dataPrimary[i]);
			}else if(i==4){
				pviTextView[7][1].setText(rankingDataBtn.dataPrimary[i]);
			}else if(i==5){
				pviTextView[8][1].setText(rankingDataBtn.dataPrimary[i]);
			}
		}
		
		
		//设置名家名作
		for(int i=0;i<3 && authorDataBtn.dataPrimary[i]!=null;i++){
			if(i==0){
				pviTextView[6][2].setText(authorDataBtn.dataPrimary[i]);
				pviTextView[6][3].setText(authorDataBtn.dataSlave[i]);
			}else if(i==1){
				pviTextView[7][2].setText(authorDataBtn.dataPrimary[i]);
				pviTextView[7][3].setText(authorDataBtn.dataSlave[i]);
			}else if(i==2){
				pviTextView[8][2].setText(authorDataBtn.dataPrimary[i]);
				pviTextView[8][3].setText(authorDataBtn.dataSlave[i]);
			}
		}
		
		//设置精品专栏
		for(int i=0;i<4 && GoodsDataBtn.dataPrimary[i]!=null;i++){
			if(i==0){
				pviTextView[10][0].setText(GoodsDataBtn.dataPrimary[i]);
			}else if(i==1){
				pviTextView[11][0].setText(GoodsDataBtn.dataPrimary[i]);
			}else if(i==2){
				pviTextView[10][1].setText(GoodsDataBtn.dataPrimary[i]);
			}else if(i==3){
				pviTextView[11][1].setText(GoodsDataBtn.dataPrimary[i]);
			}
			
			
		}
		
		//设置最新资讯 /包月书包
		for(int i=0;i<2 && newsDataBtn.dataPrimary[i]!=null;i++){
			if(i==0){
				pviTextView[10][2].setText(newsDataBtn.dataPrimary[i]);
				pviTextView[10][3].setText(newsDataBtn.dataSlave[i]);
			}else if(i==1){
				pviTextView[11][2].setText(newsDataBtn.dataPrimary[i]);
				pviTextView[11][3].setText(newsDataBtn.dataSlave[i]);
			}
		}
		
	}
	
	public void setDataTitle(String[] dataTitle){
		this.dataTitle = dataTitle;
	}
	public void setGoodsDataBtn(data_btn GoodsDataBtn){
		this.GoodsDataBtn = GoodsDataBtn;
	}
	public void setRecommendDataBtn(data_btn recommendDataBtn){
		this.recommendDataBtn = recommendDataBtn;
	}
	public void setAuthorDataBtn(data_btn authorDataBtn){
		this.authorDataBtn = authorDataBtn;
	}
	public void setRankingDataBtn(data_btn rankingDataBtn){
		this.rankingDataBtn = rankingDataBtn;
	}
	public void setCatalogDataBtn(data_btn catalogDataBtn){
		this.catalogDataBtn = catalogDataBtn;
	}
	public void setNewsDataBtn(data_btn newsDataBtn){
		this.newsDataBtn = newsDataBtn;
	}
	
	private void gotoWirelessStore(String tag) {
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act",
		"com.pvi.ap.reader.activity.WirelessStoreActivity");
		bundleToSend.putString("haveTitleBar", "1");
		bundleToSend.putString("startType", "allwaysCreate");
		bundleToSend.putString("actTabName", tag);
		tmpIntent.putExtras(bundleToSend);
		context.sendBroadcast(tmpIntent);
	}
	
    private void handInfoClick(int i) {
        final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        final Bundle bundleToSend = new Bundle();
        bundleToSend.putString("bookNewsID", newsDataBtn.dataId[i]);
        bundleToSend.putString("act",
                "com.pvi.ap.reader.activity.InfoContentActivity");
        bundleToSend.putString("pviapfStatusTip", getResources().getString(
                R.string.kyleHint01));
        bundleToSend.putString("haveTitleBar", "1");
        tmpIntent.putExtras(bundleToSend);
        context.sendBroadcast(tmpIntent);
    }
    
    //包月书包
    private void handBookPackageInfoListClick(int i) {
        final String tmpStr1 = newsDataBtn.dataId[i];
        final String tmpStr2 = newsDataBtn.dataPrimary[i];

        final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        final Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act",
                "com.pvi.ap.reader.activity.BookPackageInfoActivity");
        bundleToSend.putString("startType", "allwaysCreate");
        bundleToSend.putString("catalogID", tmpStr1);
        bundleToSend.putString("catalogName", tmpStr2);
        bundleToSend.putString("canSubscribe", "true");
        bundleToSend.putString("pviapfStatusTip", getResources().getString(
                R.string.kyleHint01));
        bundleToSend.putString("haveTitleBar", "1");
        bundleToSend.putString("blockid", "3");         //包月书包是3？
        bundleToSend.putString("mainTitle", tmpStr2);

        tmpIntent.putExtras(bundleToSend);

        context.sendBroadcast(tmpIntent);
    }
    
    //精品专栏
    private void handGoodsClick(int i) {
        final String tmpStr1 = GoodsDataBtn.dataId[i];
        final String tmpStr2 = GoodsDataBtn.dataPrimary[i];

        final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        final Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act",
                "com.pvi.ap.reader.activity.BookPackageInfoActivity");
        bundleToSend.putString("startType", "allwaysCreate");
        bundleToSend.putString("catalogID", tmpStr1);
        bundleToSend.putString("catalogName", tmpStr2);
        bundleToSend.putString("canSubscribe", "true");
        bundleToSend.putString("pviapfStatusTip", getResources().getString(
                R.string.kyleHint01));
        bundleToSend.putString("haveTitleBar", "1");
        bundleToSend.putString("blockid", "3");         //精品专栏是？
        bundleToSend.putString("mainTitle", tmpStr2);

        tmpIntent.putExtras(bundleToSend);

        context.sendBroadcast(tmpIntent);
    }
    
    //跳到作家介绍页
    private void handAuthorsClick(int i) {
        Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act",
                "com.pvi.ap.reader.activity.WriterActivity");
        bundleToSend.putString("authorID", authorDataBtn.dataId[i]);
        bundleToSend.putString("pviapfStatusTip", getResources().getString(
                R.string.kyleHint01));
        bundleToSend.putString("haveTitleBar", "1");
        tmpIntent.putExtras(bundleToSend);
        context.sendBroadcast(tmpIntent);
    }
    
    private void handContentListClick(int i) {
        final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        final Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act","com.pvi.ap.reader.activity.BookSummaryActivity");
        bundleToSend.putString("startType", "allwaysCreate");
        bundleToSend.putString("haveTitleBar", "1");
        bundleToSend.putString("contentID", recommendDataBtn.dataId[i]);
        bundleToSend.putString("pviapfStatusTip", getResources().getString(R.string.kyleHint01));
        tmpIntent.putExtras(bundleToSend);
        context.sendBroadcast(tmpIntent);
    }
}
