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

public class SystemView extends View implements View.OnTouchListener, View.OnKeyListener,View.OnFocusChangeListener {
	private boolean ViewisFocus = false;
	private static final String TAG = "SystemView";
	private Context context_local=null;
	private Paint paint=null;
	private float paddingLeft=45;
	private float paddingTop = 40;
	private int horiz=4;
	private int verta=3;
	private int focus_id=0;
	int width = 0;
	int height = 0;
	int itemNum = 9;
	public static int TIME_ID=0;
	public static int STARTUP_ID=1;
	public static int STANTBY_ID=2;
	public static int WECOME_ID=3;
	public static int NET_ID=4;
	public static int CONFIG_ID=5;
	public static int REMIND_ID=6;
	public static int RECOVERY_ID=7;
	public static int STORAGESTAT_ID=8;
	private int []normal_id=new int[]{R.drawable.timeset,R.drawable.startup,R.drawable.standby,R.drawable.welcomset,
			R.drawable.netset,R.drawable.confighandsetinfo,R.drawable.subscriberemind,R.drawable.recoveryfactory,R.drawable.strorage
	};
	private int []bg_id=new int[]{R.drawable.timesetbg,R.drawable.startupbg,R.drawable.standbybg,R.drawable.welcomsetbg,
			R.drawable.netsetbg,R.drawable.confighandsetinfobg,R.drawable.subscriberemindbg,R.drawable.recoveryfactorybg,R.drawable.stroragebg
	};
	private String []name=new String[]{"日期和时间","开机画面","待机设置","无线欢迎页","联网方式","设备信息","预定提醒","恢复出厂","存储信息"};
	private String []stat=new String[]{"","","","","","","","",""};
	public SystemView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		context_local = context;
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeWidth(5);
		paint.setTextSize(21);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		this.setOnFocusChangeListener(this);
	}
	public SystemView(Context context,AttributeSet attrs){
		super(context,attrs);
		context_local = context;
		paint=new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeWidth(5);
		paint.setTextSize(21);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		this.setOnFocusChangeListener(this);
	}

	public String[] getName() {
		return name;
	}

	public void setName(String[] name) {
		this.name = name;
		refresh();
	}
	public String[] getStat() {
		return stat;
	}
	public void setStat(String[] stat) {
		this.stat = stat;
		refresh();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Bitmap bm=null;
		canvas.translate(0, 0);
		canvas.drawColor(Color.WHITE);
		canvas.save();
		canvas.translate(paddingLeft, paddingTop);
		canvas.save();
		int id=0;
		for(int i=0;i<verta;i++){

			for (int j=0;j<horiz;j++){
				if(id<bg_id.length){
					canvas.restore();
					if(ViewisFocus&&id==focus_id){
						bm=BitmapFactory.decodeResource(context_local.getResources(),bg_id[id]);
					}else{
						bm=BitmapFactory.decodeResource(context_local.getResources(),normal_id[id]);
					}
					canvas.drawBitmap(bm, 0, 0, paint);
					width=bm.getWidth();
					height=bm.getHeight();
					paint.setTextSize(21);
					paint.setTextAlign(Align.CENTER);
					canvas.drawText(name[id], 0, name[id].length(), width/2, height+30, paint);
					paint.setTextSize(18);
					if(stat[id]!=null){
						canvas.drawText(stat[id], 0, stat[id].length(), width/2, height+50, paint);
					}
					canvas.translate(width+90, 0);
					canvas.save();

					id++;
				}
			}
			canvas.translate(-600, height+100);
			canvas.save();
		}

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
			if(x -paddingLeft < width+20 && y-paddingTop<height+50)
			{
				focus_id =SystemView.TIME_ID;
				v.performClick();
			}
			else if(x>150&&x -paddingLeft < (2*width+90+20)&& y-paddingTop<height+50)
				
			{
				focus_id = SystemView.STARTUP_ID;
				v.performClick();
			}
			else if(x>300&&x -paddingLeft <(20+width+2*(width+90))&& y-paddingTop<height+50)
			{
				focus_id = SystemView.STANTBY_ID;
				v.performClick();
			}
			else if(x>450&&x -paddingLeft < (20+width+3*(width+90))&& y-paddingTop<height+50)
			{
				focus_id = SystemView.WECOME_ID;
				v.performClick();
			}
			else if(x -paddingLeft < width+20 && y>200&&y-paddingTop<2*height+100+50)
			{
				focus_id = SystemView.NET_ID;
				v.performClick();
			}
			else if(x>150&&x -paddingLeft <(2*width+90+20) &&y>190&&y-paddingTop<(2*height+100+50))
			{
				focus_id = SystemView.CONFIG_ID;
				v.performClick();
			}
			else if(x>300&&x -paddingLeft <(20+width+2*(width+90)) && y>190&&y-paddingTop<(2*height+100+50))
			{
				focus_id = SystemView.REMIND_ID;
				v.performClick();
			}
			else if(x>450&&x -paddingLeft < (20+width+3*(width+90)) &&y>190&& y-paddingTop<(2*height+100+50))
			{
				focus_id = SystemView.RECOVERY_ID;
				v.performClick();
			}else if(x -paddingLeft < width+20 &&y>380&& y-paddingTop<(3*height+200+50))
			{
				focus_id = SystemView.STORAGESTAT_ID;
				v.performClick();
			}
			Logger.v("TAG", "focus_id="+focus_id);

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

}
