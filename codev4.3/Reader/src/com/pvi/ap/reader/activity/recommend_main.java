package com.pvi.ap.reader.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class recommend_main extends View implements View.OnTouchListener, View.OnFocusChangeListener {

	public static int RECOMMEND_TITLE = 0;
	public static int RECOMMEND_MORE = 1;
	public static int RECOMMEND_BOOK1 = 2;
	public static int RECOMMEND_BOOK2 = 3;
	public static int RECOMMEND_BOOK3 = 4;
	public static int ERROR = -1;
	private static final String TAG = "recommend_main";
	private float paddingleft = 20;
	private String title = "编辑推荐";
	private int drawtitlenormal = R.drawable.recommendbtn_normal;
	private int drawtitlesel = R.drawable.recommendbtn_sel;
	private int divider = R.drawable.style2_newline;
	private int morenormal = R.drawable.icon_total_book_count;
	private int moresel = R.drawable.style2_more;
	private int defaultdraw = R.drawable.image_book_01;
	private int framenormal = R.drawable.imageframe_mainpage_normal;
	private int framesel = R.drawable.imageframe_mainpage_sel;
	private int recommenddividernormal = R.drawable.recommendline_normal;
	private int recommenddividersel = R.drawable.recommendline_sel;
	private boolean ViewisFocus = false;
	private String[] bookname = {
			"","",""
	};
	private String[] details = {"",
			"",
			""		
	};
	private String[] bookauthor = {
			"",
			"",
			""
	};

	private String[] url = {"","",""};
	Paint mPaint = new Paint();
	int itemNum = 3;
	int width = 0;
	int height = 0;
	int focus_idx = 0;
	int pre_focus_idx = 0;
	boolean isclick = false;
	Context context_local = null;

	Rect recommend = new Rect();
	Rect recommendmore = new Rect();
	Rect[] recommendbook = new Rect[3];
	
//	private static final int UPDATE_MODE    = EINK_AUTO_MODE_REGIONAL| EINK_WAIT_MODE_NOWAIT | EINK_WAVEFORM_MODE_GC16| EINK_UPDATE_MODE_PARTIAL;
	public recommend_main(Context context, AttributeSet attrs) {
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
		res= context_local.getResources();
	}

	public recommend_main(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		context_local = context;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(5);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
//		this.setOnKeyListener(this);
		this.setOnFocusChangeListener(this);
		res= context_local.getResources();
	}
	public int getItemNum()
	{
		return this.itemNum;
	}
	public int getFocusIndex()
	{
		return focus_idx;
	}
	public void setFocusIndex(int focusidx)
	{
		this.focus_idx = focusidx;
	}
	public void setIsClick(boolean click)
	{
		this.isclick = click;
	}
	public void setRecommendBook(ArrayList<HashMap<String, String>> bookinfo)
	{
		HashMap<String, String> map = null;
		for(int i = 0; i < (bookinfo.size()<3?bookinfo.size():3); i ++)
		{
			map = bookinfo.get(i);
			this.bookauthor[i] = map.get("Author");
			this.bookname[i] = map.get("Name");
			this.url[i] = map.get("Url");
			this.details[i] = map.get("Detail");
		}
		refresh();
	}
	private Resources res=null;


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			pre_focus_idx = focus_idx;
			float x = event.getX();
			float y = event.getY();
			if(x>this.recommend.left && x < this.recommend.right &&y>this.recommend.top&&y<this.recommend.bottom)
			{
				focus_idx = recommend_main.RECOMMEND_TITLE;
			}
			else if(x>=this.recommendmore.left && x <= this.recommendmore.right &&y>=this.recommendmore.top&&y<=this.recommendmore.bottom)
			{
				focus_idx = recommend_main.RECOMMEND_MORE;
			}
			else if(x>=this.recommendbook[0].left && x <= this.recommendbook[0].right &&y>=this.recommendbook[0].top&&y<=this.recommendbook[0].bottom)
			{
				focus_idx = recommend_main.RECOMMEND_BOOK1;
			}
			else if(x>=this.recommendbook[1].left && x <= this.recommendbook[1].right &&y>=this.recommendbook[1].top&&y<=this.recommendbook[1].bottom)
			{
				focus_idx = recommend_main.RECOMMEND_BOOK2;
			}
			else if(x>=this.recommendbook[2].left && x <= this.recommendbook[2].right &&y>=this.recommendbook[2].top&&y<=this.recommendbook[2].bottom)
			{
				focus_idx = recommend_main.RECOMMEND_BOOK3;
			}
			else
			{
				focus_idx = recommend_main.ERROR;
			}
			//			Logger.e(TAG, focus_idx+  " is click");
			v.requestFocus();
			v.performClick();
			isclick = true;
			refresh();
			return true;
		}
		return false;
	}

	@SuppressWarnings("null")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//Logger.d(TAG, "onDraw is called");
		Bitmap temp = null;
		Rect rect = new Rect();
		canvas.drawColor(Color.WHITE);
		canvas.save();
		canvas.restore();
		canvas.translate(paddingleft, 0);

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

		recommend.left = 20;
		recommend.right = temp.getWidth() + 20;
		recommend.top = 0;
		recommend.bottom = temp.getHeight();

		if((temp.getWidth() > (rect.right-rect.left + 5)) &&(temp.getHeight() > rect.bottom-rect.top + 2))
		{
			//Logger.e(TAG, "OK");
			canvas.drawBitmap(temp, 0, 0, this.mPaint);
			canvas.drawText(title, 5, 20, mPaint);
		}
		canvas.save();
		canvas.restore();
		canvas.translate(320-75, 0);
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
		canvas.translate(75 - 320, recommend.bottom - recommend.top + 1);
		canvas.drawBitmap(temp, 0,0, mPaint);
		canvas.save();
		this.recommendmore.left = 265;
		this.recommendmore.right = 340;
		this.recommendmore.top = recommend.top;
		this.recommendmore.bottom = recommend.bottom;
		canvas.translate(0, temp.getHeight());
		canvas.save();
		Rect recommendtemp = new Rect();
		recommendtemp.left = 20;
		recommendtemp.right = 340;
		recommendtemp.top = recommend.bottom - recommend.top + 1 + temp.getHeight();
		recommendtemp.bottom = recommendtemp.top + 149;
		this.recommendbook[0]=recommendtemp;

		for(int i = 0; i < itemNum; i++)
		{
			if(i!=0)
			{
				recommendtemp = new Rect();
				recommendtemp.left = 20;
				recommendtemp.right = 340;
				recommendtemp.top = this.recommendbook[i-1].bottom;
				recommendtemp.bottom = recommendtemp.top + 155;
				this.recommendbook[i]=recommendtemp;
			}
			canvas.restore();
			canvas.translate(0, 10);
			if(ViewisFocus&&this.focus_idx == i+2 && isclick)
			{
				temp = BitmapFactory.decodeResource(res, this.framesel);
				isclick = false;
			}
			else
			{
				temp = BitmapFactory.decodeResource(res, this.framenormal);
			}

			canvas.drawBitmap(temp, 0, 0, mPaint);
			if(url[i]==null)
			{
				canvas.drawBitmap(BitmapFactory.decodeResource(res, defaultdraw), 3, 3, mPaint);
			}
			if(url[i].equals(""))
			{
				InputStream myFile = null;
				if(i==0)
				{
					myFile = res.openRawResource(R.raw.book01);
				}
				else if(i==1)
				{
					myFile = res.openRawResource(R.raw.book02);
				}
				else
				{
					myFile = res.openRawResource(R.raw.book03);
				}
				canvas.drawBitmap(BitmapFactory.decodeStream(myFile), 3, 3, mPaint);
			}
			else
			{			
				File file = new File(url[i]);
				if (file == null || !file.exists())
					canvas.drawBitmap(BitmapFactory.decodeResource(res, defaultdraw), 3, 3, mPaint);
				else {
					try {
						Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(file));
						canvas.drawBitmap(bmp, 3, 3, mPaint);
					} catch (Exception e) {
						canvas.drawBitmap(BitmapFactory.decodeResource(res, defaultdraw), 3, 3, mPaint);
					}
				}
			}
			canvas.save();
			canvas.translate(105, 0);
			mPaint.setTextSize(18);
			mPaint.setFakeBoldText(true);

			Rect recttmp = new Rect();
			mPaint.getTextBounds(bookname[i], 0, bookname[i].length(), recttmp);
			String tempstr  = this.getLine(mPaint, this.bookname[i], 215);
			if(tempstr.length()>=this.bookname[i].length())
			{
				canvas.drawText(this.bookname[i], 0, this.bookname[i].length(), 0, 15, mPaint);
			}
			else
			{
				tempstr = tempstr.substring(0, tempstr.length()-2) +"...";
				canvas.drawText(tempstr, 0, tempstr.length(), 0, 15, mPaint);
			}

			canvas.save();
			mPaint.setFakeBoldText(false);
			if(details[i].length()>10)
			{
				canvas.drawText(this.details[i], 0, 10, 0, recttmp.bottom - recttmp.top + 25 + 15, mPaint);
				mPaint.getTextBounds(details[i], 10, details[i].length(), recttmp);
				if(recttmp.right - recttmp.left > 320 - 105)
				{
					String str = this.details[i].substring(10, 19>(details[i].length()-10)?details[i].length()-10:19)+"...";
					canvas.drawText(str, 0, str.length(), 0, recttmp.bottom - recttmp.top + 25 + 40, mPaint);
				}
				else
				{
					canvas.drawText(this.details[i], 10, details[i].length(), 0, recttmp.bottom - recttmp.top + 25 + 40, mPaint);
				}

			}
			else
			{
				canvas.drawText(this.details[i], 0, details[i].length(), 0, recttmp.bottom - recttmp.top + 25 + 15, mPaint);
			}
			canvas.save();
			tempstr  = this.getLine(mPaint, "作者：" + this.bookauthor[i], 215);
			if(tempstr.length()>=this.bookauthor[i].length())
			{
				canvas.drawText(tempstr, 0, tempstr.length(),0, recttmp.bottom - recttmp.top + 25 + 80, mPaint);
			}
			else
			{
				if((tempstr.length()-2)<0){
				    canvas.drawText(tempstr, 0, tempstr.length() + 3,0, recttmp.bottom - recttmp.top + 25 + 80, mPaint);
	            }else{
                    tempstr = tempstr.substring(0, tempstr.length()-2) +"...";
                    canvas.drawText(tempstr, 0, tempstr.length() + 3,0, recttmp.bottom - recttmp.top + 25 + 80, mPaint);
                }
			
			}
			canvas.save();
			canvas.translate(-105, 130);
			if(ViewisFocus&&this.focus_idx == i+2)
			{
				canvas.drawBitmap(BitmapFactory.decodeResource(res, this.recommenddividersel), 0, 8, mPaint);
			}
			else
			{
				canvas.drawBitmap(BitmapFactory.decodeResource(res, this.recommenddividernormal), 0, 8, mPaint);
			}
			canvas.save();
			canvas.translate(0, 15);
			canvas.save();
		}
	}
	@Override
	public void onFocusChange(View v, boolean arg1) {
		// TODO Auto-generated method stub
		ViewisFocus = arg1;
		refresh();
		//Logger.d(TAG, "onFocusChange " + arg1);
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
