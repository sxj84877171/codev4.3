package com.pvi.ap.reader.activity;

import java.util.ArrayList;
import java.util.HashMap;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.data.common.Logger;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class recent_main extends View implements View.OnTouchListener, View.OnFocusChangeListener{

	public static int RECENT_TITLE = 0;
	public static int RECENT_MORE = 1;
	public static int RECENT_RECORD1 = 2;
	public static int RECENT_RECORD2 = 3;
	public static int RECENT_RECORD3 = 4;
	public static int RECENT_RECORD4 = 5;
	public static int RECENT_RECORD5 = 6;
	public static int RECENT_NORECORD =7;
	public static int ERROR = -1;
	private static final String TAG = "recent_main";
	private String norecordhint = null;
	private String title = "最近阅读";
	private int drawtitlenormal = R.drawable.recommendbtn_normal;
	private int drawtitlesel = R.drawable.recommendbtn_sel;
	private int divider = R.drawable.style2_newline;
	private int morenormal = R.drawable.icon_total_book_count;
	private int moresel = R.drawable.style2_more;
	private int recentdividernormal = R.drawable.zjyd_normal;
	private int recentdividersel = R.drawable.zjydline_sel;
	private boolean ViewisFocus = false;
	private String[] bookname = {
			"","","","",""
	};
	private String[] chapter = {
			"",
			"",
			"",
			"",
			""
	};
	private String[] readtime = {
			"",
			"",
			"",
			"",
			""
	};

	public void setFocusIndex(int focusidx)
	{
		this.focus_idx = focusidx;
	}
	Paint mPaint = new Paint();
	//	int itemNum = 5;
	int width = 0;
	int height = 0;
	int focus_idx = 0;
	int pre_focus_idx = 0;
	Context context_local = null;
	private Resources res=null;
	Rect recent = new Rect();
	Rect recentmore = new Rect();
	Rect[] recentbook = new Rect[5];
	Rect norecord = new Rect();
//	private static final int UPDATE_MODE = EINK_AUTO_MODE_REGIONAL| EINK_WAIT_MODE_NOWAIT | EINK_WAVEFORM_MODE_GC16| EINK_UPDATE_MODE_PARTIAL;
	public recent_main(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		context_local = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(5);
		mPaint.setTextSize(19);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		this.setOnFocusChangeListener(this);
		res= context_local.getResources();
		this.norecordhint = res.getString(R.string.no_recent_read_books_tip_mainpage);
	}

	public recent_main(Context context) {
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
		res= context_local.getResources();
		this.norecordhint = res.getString(R.string.no_recent_read_books_tip_mainpage);
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			pre_focus_idx = focus_idx;
			float x = event.getX();
			float y = event.getY();
			if(x>this.recent.left && x < this.recent.right &&y>this.recent.top&&y<this.recent.bottom)
			{
				focus_idx = recent_main.RECENT_TITLE;
			}
			else if(x>=this.recentmore.left && x <= this.recentmore.right &&y>=this.recentmore.top&&y<=this.recentmore.bottom)
			{
				focus_idx = recent_main.RECENT_MORE;
			}
			else if((this.getItemNum() < 1) && x>=this.norecord.left && x <= this.norecord.right &&y>=this.norecord.top&&y<=this.norecord.bottom )
			{
				focus_idx = recent_main.RECENT_NORECORD;
			}
			else if((this.getItemNum()>=1) &&this.recentbook[0]!=null && x>=this.recentbook[0].left && x <= this.recentbook[0].right &&y>=this.recentbook[0].top&&y<=this.recentbook[0].bottom)
			{
				focus_idx = recent_main.RECENT_RECORD1;
			}
			else if((this.getItemNum()>=2) &&this.recentbook[1]!=null&&x>=this.recentbook[1].left && x <= this.recentbook[1].right &&y>=this.recentbook[1].top&&y<=this.recentbook[1].bottom)
			{
				focus_idx = recent_main.RECENT_RECORD2;
			}
			else if((this.getItemNum()>=3) &&this.recentbook[2]!=null&&x>=this.recentbook[2].left && x <= this.recentbook[2].right &&y>=this.recentbook[2].top&&y<=this.recentbook[2].bottom)
			{
				focus_idx = recent_main.RECENT_RECORD3;
			}
			else if((this.getItemNum()>=4) &&this.recentbook[3]!=null&&x>=this.recentbook[3].left && x <= this.recentbook[3].right &&y>=this.recentbook[3].top&&y<=this.recentbook[3].bottom)
			{
				focus_idx = recent_main.RECENT_RECORD4;
			}
			else if((this.getItemNum()>=5) &&this.recentbook[4]!=null&&x>=this.recentbook[4].left && x <= this.recentbook[4].right &&y>=this.recentbook[4].top&&y<=this.recentbook[4].bottom)
			{
				focus_idx = recent_main.RECENT_RECORD5;
			}
			else
			{
				focus_idx = recent_main.ERROR;
			}
			v.requestFocus();
			v.performClick();
			refresh();
			return true;
		}
		return false;
	}
	@Override
	public void onFocusChange(View v, boolean arg1) {
		// TODO Auto-generated method stub
		ViewisFocus = arg1;
		refresh();
//		Logger.e(TAG, "onFocusChange " + arg1);
	}
	int getItemNum()
	{
		int itemNum = 0;
		for(int i = 0; i < bookname.length; i++)
		{
			if(bookname[i]!=null&&!bookname[i].equals(""))
			{
				itemNum ++;
			}
		}
		return itemNum;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		Bitmap temp = null;
		Rect rect = new Rect();
		canvas.drawColor(Color.WHITE);
		canvas.save();
		canvas.restore();
		mPaint.setTextSize(19);
		mPaint.setFakeBoldText(true);

		mPaint.getTextBounds(title, 0, title.length(), rect);
		if(ViewisFocus && focus_idx==0)
		{
			temp = BitmapFactory.decodeResource(res, drawtitlesel);
		}
		else
		{
			temp = BitmapFactory.decodeResource(res, drawtitlenormal);
		}

		if(temp!=null)
		{
			recent.left = 0;
			recent.right = temp.getWidth();
			recent.top = 0;
			recent.bottom = temp.getHeight();
		}

		if((temp.getWidth() > (rect.right-rect.left + 5)) &&(temp.getHeight() > rect.bottom-rect.top + 2))
		{
			canvas.drawBitmap(temp, 0, 0, this.mPaint);
			canvas.drawText(title, 5, 20, mPaint);
		}
		canvas.save();
		canvas.restore();
		canvas.translate(217-55, 0);
		if(ViewisFocus&&focus_idx == 1)
		{
			temp = BitmapFactory.decodeResource(res, moresel);
		}
		else
		{
			temp = BitmapFactory.decodeResource(res, morenormal);
		}

		canvas.drawBitmap(temp, 0, 6, mPaint);
		mPaint.setFakeBoldText(true);
		mPaint.setTextSize(16);
		canvas.drawText("更多", 0, 2, temp.getWidth()+6, 20, mPaint);
		canvas.save();
		temp = BitmapFactory.decodeResource(res, divider);
		canvas.translate(55 - 217, recent.bottom - recent.top + 1);
		canvas.drawBitmap(temp, 0,0, mPaint);
		canvas.save();
		this.recentmore.left = 162;
		this.recentmore.right = 217;
		this.recentmore.top = recent.top;
		this.recentmore.bottom = recent.bottom;
		canvas.translate(0, temp.getHeight());
		canvas.save();
		int itemnum = getItemNum();
		if(itemnum == 0)
		{
			mPaint.setTextAlign(Align.CENTER);
			canvas.translate(0, 180);
			norecord.left = 0;
			norecord.right = 217;
			norecord.top = recent.bottom - recent.top + 1 + temp.getHeight();
			norecord.bottom = 500;
			int length = 0;
			mPaint.setTextSize(19);
			mPaint.setFakeBoldText(false);
			String tempstr = this.getLine(mPaint, this.norecordhint, 217);
			length = tempstr.length();
			while(length <= norecordhint.length())
			{
				canvas.drawText(tempstr, 108, 15, mPaint);
				tempstr = norecordhint.substring(length, norecordhint.length());
				tempstr = this.getLine(mPaint, tempstr, 217);
				if(tempstr.equals(""))
				{
					break;
				}
				length = length + tempstr.length();
//				Logger.e(TAG, "tempstr:" + tempstr.length() +"length:"+ length);
				canvas.translate(0, 25);
				canvas.save();
			}
			canvas.translate(0, 27);
			if(ViewisFocus&&focus_idx == 7)
			{
				canvas.drawBitmap(BitmapFactory.decodeResource(res, this.recentdividersel), 0, 0, mPaint);
			}
			else
			{
				canvas.drawBitmap(BitmapFactory.decodeResource(res, this.recentdividernormal), 0, 0, mPaint);
			}
			mPaint.setTextAlign(Align.LEFT);
		}
		else
		{
			Rect recenttemp = new Rect();
			recenttemp.left = 0;
			recenttemp.right = 217;
			recenttemp.top = recent.bottom - recent.top + 1 + temp.getHeight();
			recenttemp.bottom = recenttemp.top + 91;
			this.recentbook[0]=recenttemp;
			String tempstr = null;
			for(int i=0; i<itemnum; i++)
			{
				if(i!=0)
				{
					recenttemp = new Rect();
					recenttemp.left = 0;
					recenttemp.right = 217;
					recenttemp.top = this.recentbook[i-1].bottom;
					recenttemp.bottom = recenttemp.top + 92;
					this.recentbook[i]=recenttemp;
					//				Logger.e(TAG, i+" left:" + this.recommendbook[i].left + " right:" + this.recommendbook[i].right +" top:" + this.recommendbook[i].top +" bottom:" + this.recommendbook[i].bottom);
				}
				mPaint.setTextSize(19);
				mPaint.setFakeBoldText(true);
				tempstr = this.getLine(mPaint, this.bookname[i], 217);
				if(tempstr.length() >= this.bookname[i].length())
				{
					canvas.drawText(tempstr, 0, 22, mPaint);
				}
				else
				{
					canvas.drawText(tempstr.subSequence(0, tempstr.length()-2) + "...", 0, 22, mPaint);
				}
				
				mPaint.setFakeBoldText(false);
				tempstr = this.getLine(mPaint, this.chapter[i], 217);
				if(tempstr.length() >= this.chapter[i].length())
				{
					canvas.drawText(tempstr, 0, 52, mPaint);
				}
				else
				{
					canvas.drawText(tempstr.subSequence(0, tempstr.length()-2) + "...", 0, 52, mPaint);
				}
				
				tempstr = this.getLine(mPaint, "阅读时间:"+this.readtime[i], 217);
				if(tempstr.length() >= ("阅读时间:"+this.readtime[i]).length())
				{
					canvas.drawText(tempstr, 0, 82, mPaint);
				}
				else
				{
					canvas.drawText(tempstr.subSequence(0, tempstr.length()-2) + "...", 0, 82, mPaint);
				}
				if(ViewisFocus&&focus_idx == (i+2))
				{
					canvas.drawBitmap(BitmapFactory.decodeResource(res, this.recentdividersel), 0, 90, mPaint);
				}
				else
				{
					canvas.drawBitmap(BitmapFactory.decodeResource(res, this.recentdividernormal), 0, 90, mPaint);
				}
				canvas.translate(0, 92);
			}
		}
	}

	public int getFocusIndex()
	{
		return focus_idx;
	}

	public void setRecentBook(ArrayList<HashMap<String, String>> bookinfo)
	{
		
		HashMap<String, String> map = null;

		for(int i = 0; i < 5; i ++)
		{
			if(bookinfo!=null && i<bookinfo.size())
			{
				map = bookinfo.get(i);
				this.bookname[i] = map.get("Name");
				this.chapter[i] = map.get("Chapter");
				this.readtime[i] = map.get("ReadTime");
			}
			else
			{
				this.bookname[i] = "";
				this.chapter[i] = "";
				this.readtime[i] = "";
			}
		}
		refresh();
	}
	void refresh()
	{
		if(GlobalVar.deviceType == 1)
		{
//			invalidate(UPDATE_MODE);
		}
		else
		{
			invalidate();
		}
	}
	private String getLine(Paint mpaint,String source, int width)
	{
		Rect rect = new Rect();
		String dest = null;
		for(int i = 1; i <= source.length(); i ++)
		{
			mpaint.getTextBounds(source, 0, i, rect);
			if(rect.right - rect.left > width)
			{
				dest = source.substring(0, i-1);
				return dest;
			}
		}
		dest = source.substring(0);
		return dest;
	}
}
