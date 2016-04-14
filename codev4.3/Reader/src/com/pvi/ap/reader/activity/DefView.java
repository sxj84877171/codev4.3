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
import android.view.MotionEvent;
import android.view.View;

public class DefView extends View implements View.OnTouchListener,View.OnFocusChangeListener {
	public DefView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		context_local = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(5);
		mPaint.setTextSize(21);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
//		this.setOnKeyListener(this);
		this.setOnFocusChangeListener(this);
	}
	public static int WIRLESSSTORE = 1;
	public static int CONTINUEREAD = 0;
	public static int BOOKSHELF = 2;
	public static int LOCALBOOK = 3;
	public static int RESCENTER = 4;
	public static int USERCENTER = 5;
	public static int ERROR = -1;
	private boolean ViewisFocus = false;
	private static final String TAG = "defview";
	private float paddingleft = 18;
	private int [] drawnormal = {R.drawable.continueread_normal,R.drawable.wirelessstore_normal,R.drawable.bookshelf_normal,R.drawable.localbook_normal,R.drawable.rescenter_normal,R.drawable.usercenter_normal};
	private int [] drawsel = {R.drawable.continueread_fs,R.drawable.wirelessstore_sel, R.drawable.bookshelf_sel, R.drawable.localbook_sel,R.drawable.rescenter_sel,R.drawable.usercenter_sel};
	String[] title = {"继续阅读","无线书城","我的书架","本地书库","资源中心","个人空间"};
	private float txtpadtop = 125;
	private float btnpadtop = 23;
	Paint mPaint = new Paint();
	int itemNum = 6;
	int width = 0;
	int height = 0;
	int focus_idx = 0;
	int pre_focus_idx = 0;
	boolean isclick = false;

	Context context_local = null;
	public DefView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		context_local = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(5);
		mPaint.setTextSize(21);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		this.setOnFocusChangeListener(this);
	}
	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//Logger.e(TAG, "onDraw is called");
		Bitmap temp = null;
		canvas.translate(0, 0);
		canvas.drawColor(Color.WHITE);
		canvas.save();
		canvas.translate(paddingleft, btnpadtop);
		canvas.save();
		for(int i = 0;i < itemNum;i ++)
		{
			canvas.restore();
			if(ViewisFocus && i == focus_idx)
			{
				temp = BitmapFactory.decodeResource(context_local.getResources(), drawsel[i]);
			}
			else
			{
				temp = BitmapFactory.decodeResource(context_local.getResources(), drawnormal[i]);
			}
			//			Logger.e(TAG, "Width: " + temp.getWidth() + " scaledWidth:" + temp.getScaledWidth(canvas));
			canvas.drawBitmap(temp, 0, 0, mPaint);
			width = temp.getWidth();
			height = temp.getHeight();
			mPaint.setTextAlign(Align.CENTER);
			mPaint.setFakeBoldText(true);
			//			mPaint.setColor(txtcolor);
			canvas.drawText(title[i], 0, title[i].length(), width/2, txtpadtop, mPaint);
			canvas.translate(width, 0);
			canvas.save();
		}
	}

	public int getFocusIndex()
	{
		return focus_idx;
	}
	
	public void setViewFocus(boolean hasfocus)
	{
		this.ViewisFocus = hasfocus;
		this.refresh();
	}
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
			pre_focus_idx = focus_idx;
			float x = event.getX();
			if(x -18 < width)
			{
				focus_idx = DefView.CONTINUEREAD;
			}
			else if(x -18 < 2*width)
			{
				focus_idx = DefView.WIRLESSSTORE;
			}
			else if(x -18 < 3*width)
			{
				focus_idx = DefView.BOOKSHELF;
			}
			else if(x -18 < 4*width)
			{
				focus_idx = DefView.LOCALBOOK;
			}
			else if(x -18 < 5*width)
			{
				focus_idx = DefView.RESCENTER;
			}
			else
			{
				focus_idx = DefView.USERCENTER;
			}
			v.performClick();
			return true;
		}
		return false;
	}
	public int getItemNum()
	{
		return this.itemNum;
	}

	public void setFocusIndex(int focusidx)
	{
		this.focus_idx = focusidx;
	}

	@Override
	public void onFocusChange(View v, boolean arg1) {
		// TODO Auto-generated method stub
		ViewisFocus = arg1;
		refresh();
		//Logger.e(TAG, "onFocusChange " + arg1);
	}
//	private static final int UPDATE_MODE    = EINK_AUTO_MODE_REGIONAL| EINK_WAIT_MODE_NOWAIT | EINK_WAVEFORM_MODE_GC16| EINK_UPDATE_MODE_PARTIAL;
	public void refresh()
	{
		//Logger.e(TAG, "refresh");
		if(GlobalVar.deviceType == 1)
		{
//			invalidate(UPDATE_MODE);
		}
		else
		{
			invalidate();
		}
	}
}
