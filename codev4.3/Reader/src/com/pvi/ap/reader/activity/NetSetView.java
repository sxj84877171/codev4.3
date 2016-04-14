package com.pvi.ap.reader.activity;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.data.common.Logger;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class NetSetView extends View implements View.OnTouchListener, View.OnKeyListener,View.OnFocusChangeListener{

	private boolean ViewisFocus = false;
	private static final String TAG = "NetSetView";
	private Context context_local=null;
	private Paint paint=null;
	private int focus_id=-1;
	int width = 0;
	int height = 0;
	int itemNum = 3;
	private float paddingLeft=40;
	private float paddingTop = 140;
	public static int AUTO_ID=0;
	public static int TD_ID=1;
	public static int EDGE_ID=2;
	private int pic_line=R.drawable.timeset_split;
	private int pic_background=R.drawable.timeset_back;
	private int pic_notcheck=R.drawable.notcheck;
	private int pic_check=R.drawable.check;
	private String title="";
	private String []setTime=new String[]{"","",""};
	private String cur = "获取中...";
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
		refresh();
	}
	public String[] getSetTime() {
		return setTime;
	}
	public void setSetTime(String[] setTime) {
		this.setTime = setTime;
		refresh();
	}
	public NetSetView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		context_local = context;
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeWidth(5);
		paint.setTextSize(25);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		this.setOnFocusChangeListener(this);
	}
	public NetSetView(Context context,AttributeSet attrs){
		  super(context,attrs);
		// TODO Auto-generated constructor stub
		  context_local = context;
		  paint=new Paint();
		  paint.setAntiAlias(true);
		  paint.setDither(true);
		  paint.setStrokeWidth(5);
		  paint.setTextSize(25);
		  this.setFocusable(true);
		  this.setFocusableInTouchMode(true);
		  this.setOnTouchListener(this);
		  this.setOnKeyListener(this);
		  this.setOnFocusChangeListener(this);
	  }
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Bitmap bm=null;
		canvas.translate(0, 0);
		canvas.drawColor(Color.WHITE);
		canvas.save();
		paint.setTextAlign(Align.CENTER);
		 paint.setTextSize(25);
		canvas.drawText(title, 300, 70, paint);
		bm=BitmapFactory.decodeResource(context_local.getResources(), pic_line);
		canvas.drawBitmap(bm, 20, 85, paint);
		
		
		canvas.drawText("当前设置："+cur, 300, 130, paint);
		canvas.drawText("修改设置", 100, 180, paint);
		
		
		canvas.save();
		canvas.translate(40, 200);
		int id=0;
		
		bm=BitmapFactory.decodeResource(context_local.getResources(), pic_background);
		canvas.drawBitmap(bm, 0, 0, paint);
		for(int j=0;j<3;j++){
			if(id==focus_id){
				bm=BitmapFactory.decodeResource(context_local.getResources(),pic_check);
			}else{
				bm=BitmapFactory.decodeResource(context_local.getResources(),pic_notcheck);
			}
			width=bm.getWidth();
			height=bm.getHeight();
			canvas.drawBitmap(bm, 40, 15, paint);
			paint.setTextAlign(Align.LEFT);
			paint.setTextSize(21);
			canvas.drawText(setTime[id], 75, 35, paint);
			id++;
			 canvas.translate(width+145, 0);
			 canvas.save();
		}
		canvas.translate(-3*(width+145), height+70);
		canvas.save();
		
	}
	public int getFocusIndex()
	{
		return focus_id;
	}
	
	public void setFocusIndex(int focusid)
	{
		this.focus_id = focusid;
	}
	public int getItemNum()
	{
		return this.itemNum;
	}
	public void setViewFocus(boolean hasfocus)
	{
		this.ViewisFocus = hasfocus;
		this.refresh();
	}
	
//	private static final int UPDATE_MODE_FULL    = EINK_AUTO_MODE_REGIONAL| EINK_WAIT_MODE_NOWAIT | EINK_WAVEFORM_MODE_GC16| EINK_UPDATE_MODE_PARTIAL;
	public void refresh()
	{
	    Logger.e(TAG,"invalidate!!!");
		if(GlobalVar.deviceType == 1)
		{
//			invalidate(UPDATE_MODE_FULL);
		}
		else
		{
			invalidate();
		}
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			//pre_focus_idx = focus_idx;
			float x = event.getX();
			float y = event.getY();
			Logger.v("TAG", "x="+x+" y="+y);
			Logger.v("TAG","width="+width+" height="+height);
			if(x>40&&x-paddingLeft < 40+100+width&&y> paddingTop-80&& y-paddingTop<50+80)
			{
				focus_id =NetSetView.AUTO_ID;
				v.performClick();
			}
			else if(x>190&&x -paddingLeft <40+100+2*width+150&&y> paddingTop-80&& y-paddingTop<50+80)
			{
				focus_id = NetSetView.TD_ID;
				v.performClick();
			}
			else if(x>340&&x -paddingLeft <40+100+3*width+2*150&&y> paddingTop-80&& y-paddingTop<50+80)
			{
				focus_id = NetSetView.EDGE_ID;
				v.performClick();
			}
			
			Logger.e("TAG", "focus_id="+focus_id);
			
			return true;
		}
		return false;
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		ViewisFocus = hasFocus;
		refresh();
	}
	
	/**
	 * 更新
	 */
	public void setCur(String cur){
	    this.cur = cur;
	    this.invalidate();
	}
	
	
}
