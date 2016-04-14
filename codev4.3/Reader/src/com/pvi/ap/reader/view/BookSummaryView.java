package com.pvi.ap.reader.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.common.beans.BookChapterBean;
import com.pvi.ap.reader.common.beans.BookCommentBean;
import com.pvi.ap.reader.common.beans.BookFunctionBean;
import com.pvi.ap.reader.common.beans.BookRelationshipBean;
import com.pvi.ap.reader.common.beans.BookSummaryBean;
import com.pvi.ap.reader.data.common.Logger;
/***
 * 书籍摘要页view显示界面
 * @since 2011-06-20
 * @author Elvis
 */
public class BookSummaryView extends SurfaceView implements View.OnTouchListener, View.OnKeyListener,View.OnFocusChangeListener {
	
	/**
	 * 类帮助变量
	 */
	private Context mContext = null ;
	private Paint mPaint =  null; 
	private Paint tPaint = null ;
	private Resources res = null ;
	private String TAG = "BookSummaryView" ;
	
	
	//***素材**//
	private int style2_newline = R.drawable.style2_newline ;
	private int coverlogodefault = R.drawable.coverlogodefault ;
	private int bookdescription_normal = R.drawable.bookdescription_normal ;
	private int wsma_sel = R.drawable.wsma_sel ;
	private int bookdescription_sel = R.drawable.bookdescription_sel ;
	private int cata_booksummary_bg2 = R.drawable.cata_booksummary_bg2 ;
	private int nobook = R.drawable.nobook ;
	private int catabottomline = R.drawable.catabottomline ;
	private int[] chooseline = {R.drawable.chooseline1,R.drawable.chooseline2};
	private int[] star = {R.drawable.star_bg,R.drawable.star_sel,R.drawable.star_half};
	private int[] btn = {R.drawable.btn_pressed_ui1,R.drawable.btn_normal_ui1};
	private int[] catalog = {R.drawable.catalog_booksummary_normal,R.drawable.catalog_booksummary_sel};
	private int[] more = {R.drawable.icon_total_book_count,R.drawable.style2_more};
	private int[] readassociate = {R.drawable.readassociate_normal,R.drawable.readassociate_sel};
	private int[] recommendbtn = {R.drawable.recommendbtn_normal ,R.drawable.recommendbtn_sel};
	private int[] imageframe = {R.drawable.imageframe_mainpage_normal ,R.drawable.imageframe_mainpage_sel};
	private int[] img = {R.drawable.img_border_normal ,R.drawable.img_border_clicked };
	
	//***数据**//
	private BookSummaryBean bookSunmaryInfo = new BookSummaryBean() ;
	private BookFunctionBean bookFunction = new BookFunctionBean();
	private BookChapterBean bookChapterBean = new BookChapterBean();
	private BookCommentBean bookCommentBean = new BookCommentBean();
	private BookRelationshipBean bookRelationshipBean = new BookRelationshipBean();
	
	
	//***焦点**//
	private int focusIdx = 20 ;
	private boolean viewisFocus = false ;
	
	
	public interface DefaultListen{
		public void onClick();
	}
	/**
	 * 缺省监听器，没有点击到任何按钮上，处理的操作
	 */
	public DefaultListen defaultListen = null;
	

	public BookSummaryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context ;
		res = mContext.getResources();
		if(((GlobalVar)context.getApplicationContext()).deviceType == 1){
//			setUpdateMode(View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		}
		init();
	}

	/**
	 * 初始化view准备
	 */
	private void init(){
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setTextSize(21);
		tPaint = new Paint();
		tPaint.setAntiAlias(true);
		tPaint.setDither(true);
		this.setFocusable(true);
		this.setFocusableInTouchMode(true);
		this.setOnTouchListener(this);
		this.setOnKeyListener(this);
		this.setOnFocusChangeListener(this);
		Logger.i(TAG, "init");
	}
	@Override
	protected void onDraw(Canvas canvas) {
		Logger.i(TAG, "onDraw");
		canvas.drawColor(Color.WHITE);
		canvas.save();
		drawBookSummaryInfo(canvas);
		drawFourButton(canvas);
		drawChapterView(canvas);
		drawCommentView(canvas);
		drawRelashipBookView(canvas);
		super.onDraw(canvas);
	}

	/**
	 * 取得view组建中的数据
	 * @return
	 */
	public BookSummaryBean getBookSunmaryInfo() {
		return bookSunmaryInfo;
	}

	/**
	 * 设置view中的数据
	 * @param bookSunmaryInfo
	 */
	public void setBookSunmaryInfo(BookSummaryBean bookSunmaryInfo) {
		this.bookSunmaryInfo = bookSunmaryInfo;
	}
	
	/***
	 * 画书籍摘要的上面部分 书籍摘要信息
	 * @param canvas
	 */
	private void drawBookSummaryInfo(Canvas canvas){
		canvas.translate(20, 25);
		mPaint.setFakeBoldText(false);
		tPaint.setFakeBoldText(false);
		int x = 0 , y = 0 ;
		x = getWidth(mPaint,bookSunmaryInfo.title,600);
		canvas.drawText(bookSunmaryInfo.title, x, y, mPaint);
		Bitmap bitmap = BitmapFactory.decodeResource(res, style2_newline);
		canvas.drawBitmap(bitmap, 0, 5, mPaint);
		bitmap = BitmapFactory.decodeResource(res, imageframe[0]);
		canvas.drawBitmap(bitmap, 0,19, tPaint);
		if(bookSunmaryInfo.bookSurface == null){
			bitmap = BitmapFactory.decodeResource(res, coverlogodefault);
		}else{
			bitmap = bookSunmaryInfo.bookSurface ;
		}
		canvas.drawBitmap(bitmap, 3,22, tPaint);
		int k = 0 ;
		for(int i = 0 ; i < bookSunmaryInfo.starNum / 2 ; i++){
			bitmap = BitmapFactory.decodeResource(res,star[1]);
			canvas.drawBitmap(bitmap, 18 * k ,150 , mPaint);
			k ++ ;
		}
		if(bookSunmaryInfo.starNum % 2 != 0){
			bitmap = BitmapFactory.decodeResource(res,star[2]);
			canvas.drawBitmap(bitmap, 18 * k ,150 , mPaint);
			k ++ ;
		}
		while(k < 5){
			bitmap = BitmapFactory.decodeResource(res,star[0]);
			canvas.drawBitmap(bitmap, 18 * k ,150 , mPaint);
			k ++ ;
		}
		
		// 116 40
		tPaint.setTextSize(19);
		canvas.drawText("作者：", 116, 40, tPaint);
		canvas.drawText(getLine(tPaint,bookSunmaryInfo.auther,85), 116+57, 40, tPaint);
		canvas.drawText("字数：", 116 + 91 + 57, 40, tPaint);
		canvas.drawText(bookSunmaryInfo.codeNum, 116+91+57+57, 40, tPaint);
		canvas.drawText("章节：", 116+91+91+57+57, 40, tPaint);
		canvas.drawText(bookSunmaryInfo.chapterNum, 116+91+91+57+57+57, 40, tPaint);
		
		canvas.drawText("大小：", 116, 40 +25, tPaint);
		canvas.drawText(bookSunmaryInfo.bookSize, 116+57, 40+25, tPaint);
		canvas.drawText("推荐：", 116+57+91, 40+25, tPaint);
		canvas.drawText(bookSunmaryInfo.recommend, 116+57+91 + 57, 40+25, tPaint);
		canvas.drawText("读者数: ", 116+57+91 + 57 +91, 40+25, tPaint);
		canvas.drawText(bookSunmaryInfo.readerNum, 116+57+91 + 57 + 91 +62, 40+25, tPaint);
		
		canvas.drawText("点击：", 116, 90, tPaint);
		canvas.drawText(bookSunmaryInfo.clickNum, 116+57, 90, tPaint);
		canvas.drawText("收藏：", 116+57+91, 90, tPaint);
		canvas.drawText(bookSunmaryInfo.collectionNum, 116+57+91 + 57, 90, tPaint);
		canvas.drawText("鲜花： ", 116+57+91 + 57 +91, 90, tPaint);
		canvas.drawText(bookSunmaryInfo.flowersNum, 116+57+91 + 57 + 91 +57, 90, tPaint);
		
		tPaint.setFakeBoldText(true);
		canvas.drawText("资       费:", 116, 115, tPaint);
		tPaint.setFakeBoldText(false);
		canvas.drawText(getLine(tPaint,bookSunmaryInfo.charges,600-20-116-82-20 -20), 116 + 82, 115, tPaint);
		bitmap = BitmapFactory.decodeResource(res, wsma_sel);
		if(focusIdx == 1){
			canvas.drawBitmap(bitmap, 116, 144, tPaint);
		}
		tPaint.setFakeBoldText(true);
		canvas.drawText("内容简介:", 116, 140, tPaint);
		tPaint.setFakeBoldText(false);
		int index = getLine(tPaint,bookSunmaryInfo.contentAbstract,600-20-118-82-20 -20,true);
		canvas.drawText(bookSunmaryInfo.contentAbstract.substring(0, index), 200, 140, tPaint);
		canvas.drawText(getLine(tPaint,bookSunmaryInfo.contentAbstract.substring(index),340), 200, 165, tPaint);
		
		bitmap = BitmapFactory.decodeResource(res, bookdescription_normal);
		canvas.drawBitmap(bitmap, 116, 148, tPaint);
		canvas.save();
	}
	
	/**
	 * draw 四个按钮
	 * @param canvas
	 */
	private void drawFourButton(Canvas canvas){
		Bitmap bitmap1 = null,bitmap2 = null ;
		bitmap1 = BitmapFactory.decodeResource(res, btn[0]);
		bitmap2 = BitmapFactory.decodeResource(res, btn[1]);
		int width = bitmap1.getWidth();
		canvas.drawBitmap(focusIdx == 2 && viewisFocus ? bitmap1 : bitmap2, 40,184, tPaint);
		canvas.drawBitmap(focusIdx == 3 && viewisFocus ? bitmap1 : bitmap2, 65 + width,184,tPaint);
		canvas.drawBitmap(focusIdx == 4 && viewisFocus ? bitmap1 : bitmap2, 90 + 2 * width,184, tPaint);
		canvas.drawBitmap(focusIdx == 5 && viewisFocus ? bitmap1 : bitmap2, 115 +3 * width,184, tPaint);
		tPaint.setFakeBoldText(true);
		canvas.drawText(bookFunction.readonline, 51, 208, tPaint);
		if(!bookFunction.candownread){tPaint.setColor(Color.GRAY);}
		canvas.drawText(bookFunction.downread, 76 + width, 208, tPaint);
		if(!bookFunction.candownread){tPaint.setColor(Color.BLACK);}
		if(!bookFunction.canbookupdate){tPaint.setColor(Color.GRAY);}
		canvas.drawText(bookFunction.reservationUpdate, 101 + 2 * width, 208, tPaint);
		if(!bookFunction.canbookupdate){tPaint.setColor(Color.BLACK);}
		if(!bookFunction.cancollectionread){tPaint.setColor(Color.GRAY);}
		canvas.drawText(bookFunction.collectionread, 126 +3 * width, 208, tPaint);
		if(!bookFunction.cancollectionread){tPaint.setColor(Color.BLACK);}
		tPaint.setFakeBoldText(false);
		canvas.save();
	}
	
	/**
	 * draw 目录及章节
	 * @param canvas
	 */
	private void drawChapterView(Canvas canvas){
		Bitmap bitmap = null ;
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 6 && viewisFocus ? catalog[1]:catalog[0]);
		canvas.drawBitmap(bitmap, 0, 219, tPaint);
		tPaint.setFakeBoldText(true);
		canvas.drawText("目    录", 5, 240, tPaint);
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 7 && viewisFocus ? more[1]:more[0]);
		canvas.drawBitmap(bitmap, 500,225, tPaint);
		tPaint.setTextSize(17);
		canvas.drawText("更多", 523, 240, tPaint);
		tPaint.setFakeBoldText(false);
		bitmap = BitmapFactory.decodeResource(res, style2_newline);
		canvas.drawBitmap(bitmap, 0, 245, mPaint);
		tPaint.setTextSize(19);
		if (bookChapterBean.chapter[0].chapterName != null) {
			canvas.drawText(getLine(tPaint, bookChapterBean.chapter[0].chapterName, 260),
					20, 275, tPaint);
		}
		if(focusIdx == 8 && viewisFocus){
			bitmap = BitmapFactory.decodeResource(res, chooseline[0]);
			canvas.drawBitmap(bitmap, 20, 280, mPaint);
		}
		if(bookChapterBean.chapter[2].chapterName != null){
			canvas.drawText(getLine(tPaint, bookChapterBean.chapter[2].chapterName, 260),
					280, 275, tPaint);
		}
		if(focusIdx == 9 && viewisFocus){
			bitmap = BitmapFactory.decodeResource(res, chooseline[0]);
			canvas.drawBitmap(bitmap, 280, 280, mPaint);
		}
		bitmap = BitmapFactory.decodeResource(res, cata_booksummary_bg2);
		canvas.drawBitmap(bitmap, 20, 285, tPaint);
		if (bookChapterBean.chapter[1].chapterName != null) {
			canvas.drawText(getLine(tPaint, bookChapterBean.chapter[1].chapterName, 260),
					20,310, tPaint);
		}
		if(focusIdx == 10 && viewisFocus){
			bitmap = BitmapFactory.decodeResource(res, chooseline[0]);
			canvas.drawBitmap(bitmap, 20, 317, mPaint);
		}
		if (bookChapterBean.chapter[3].chapterName != null) {
			canvas.drawText(getLine(tPaint, bookChapterBean.chapter[3].chapterName, 260),
					280, 310, tPaint);
		}
		bitmap = BitmapFactory.decodeResource(res, cata_booksummary_bg2);
		canvas.drawBitmap(bitmap, 20, 322, tPaint);
		if(focusIdx == 11 && viewisFocus){
			bitmap = BitmapFactory.decodeResource(res, chooseline[0]);
			canvas.drawBitmap(bitmap, 280, 317, mPaint);
		}
		canvas.save();
	}
	
	/**
	 * draw 评论及评论内容
	 * @param canvas
	 */
	private void drawCommentView(Canvas canvas){
		Bitmap bitmap = null ;
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 12 && viewisFocus ? catalog[1]:catalog[0]);
		canvas.drawBitmap(bitmap, 0, 333, tPaint);
		tPaint.setFakeBoldText(true);
		canvas.drawText("评    论", 5, 354, tPaint);
		canvas.drawText(bookCommentBean.totleCount, 92, 354, tPaint);
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 13 && viewisFocus ? more[1]:more[0]);
		canvas.drawBitmap(bitmap, 500,339, tPaint);
		tPaint.setTextSize(17);
		canvas.drawText("更多", 523, 354, tPaint);
		tPaint.setFakeBoldText(false);
		tPaint.setTextSize(19);
		bitmap = BitmapFactory.decodeResource(res, style2_newline);
		canvas.drawBitmap(bitmap, 0, 359, mPaint);
		bitmap = BitmapFactory.decodeResource(res, cata_booksummary_bg2);
		if(bookCommentBean.cinfo[0].getComment() != null){
			canvas.drawText(getLine(tPaint, bookCommentBean.cinfo[0].getComment(), 520),
					20, 384, tPaint);
			canvas.drawBitmap(bitmap, 20, 401, tPaint);
		}
		if(focusIdx == 14 && viewisFocus){
			bitmap = BitmapFactory.decodeResource(res, chooseline[1]);
			canvas.drawBitmap(bitmap, 20, 396, mPaint);
		}
		bitmap = BitmapFactory.decodeResource(res, cata_booksummary_bg2);
		if(bookCommentBean.cinfo[1].getComment()!= null){
			canvas.drawText(getLine(tPaint, bookCommentBean.cinfo[1].getComment(), 520),
					20, 424, tPaint);
			canvas.drawBitmap(bitmap, 20, 440, tPaint);
		}
		if(focusIdx == 15 && viewisFocus){
			bitmap = BitmapFactory.decodeResource(res, chooseline[1]);
			canvas.drawBitmap(bitmap, 20, 435, mPaint);
		}
		canvas.save();
	}
	
	/***
	 * draw 相关书籍信息
	 * @param canvas
	 */
	private void drawRelashipBookView(Canvas canvas){
		Bitmap bitmap = null ;
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 16 && viewisFocus ? readassociate[1] : readassociate[0]);
		canvas.drawBitmap(bitmap, 0, 457, tPaint);
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 18 && viewisFocus ? recommendbtn[1] : recommendbtn[0]);
		canvas.drawBitmap(bitmap, 300,457,tPaint);
		tPaint.setFakeBoldText(true);
		canvas.drawText("读过该书的人还喜欢", 5, 478, tPaint);
		canvas.drawText("同类推荐", 305, 478, tPaint);
		tPaint.setTextSize(17);
		canvas.drawText("更多", 222, 478, tPaint);
		canvas.drawText("更多", 523, 478, tPaint);
		tPaint.setTextSize(19);
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 17 && viewisFocus ? more[1]:more[0]);
		canvas.drawBitmap(bitmap, 205,465, tPaint);
		bitmap = BitmapFactory.decodeResource(res, focusIdx == 19 && viewisFocus ? more[1]:more[0]);
		canvas.drawBitmap(bitmap, 500,465, tPaint);
		tPaint.setFakeBoldText(false);
		bitmap = BitmapFactory.decodeResource(res, style2_newline);
		bitmap = Bitmap.createBitmap(bitmap,0,0,260,4);
		canvas.drawBitmap(bitmap, 0, 484, mPaint);
		canvas.drawBitmap(bitmap, 300, 484, mPaint);
		int wd = 0 ;
		for(int i = 0 ; i < 6; i++){
			if(i >= 3){
				wd = 30 ;
			}
			if(bookRelationshipBean.book1Map[i] == null){
				bitmap = BitmapFactory.decodeResource(res, nobook);
				canvas.drawBitmap(bitmap, 10 + 90 * i + wd,493, tPaint);
			}else{
				bitmap = BitmapFactory.decodeResource(res, img[focusIdx == 20 + i && viewisFocus ? 1 : 0]);
				canvas.drawBitmap(bitmap, 10 + 90 * i + wd ,493, tPaint);
				canvas.drawBitmap(bookRelationshipBean.book1Map[i], 10 + 90 * i + wd +2 ,493 + 2, tPaint);
				if(bookRelationshipBean.book1Name[i] == null){
					bookRelationshipBean.book1Name[i] = "" ;
				}
				canvas.drawText(getLine(tPaint,bookRelationshipBean.book1Name[i],75), 5 + 90 * i + wd, 593, tPaint);
			}
			if (focusIdx == 20 + i && viewisFocus) {
				bitmap = BitmapFactory.decodeResource(res,
						bookdescription_sel);
				canvas.drawBitmap(bitmap, 90 * i + wd, 598, tPaint);
			}
		}
		bitmap = BitmapFactory.decodeResource(res, catabottomline);
		canvas.drawBitmap(bitmap, 0,601, tPaint);
		canvas.save();
	}
	
	/**
	 * 取的指定长度的文本
	 * @param mpaint
	 * @param source
	 * @param width
	 * @return
	 */
	private String getLine(Paint mpaint,String source, int width){
		Rect rect = new Rect();
		String dest = null;
		String lSource ;
		mpaint.getTextBounds(source, 0, source.length(), rect);
		if(rect.right - rect.left <= width){
			return source ;
		}else{
			lSource = "..." + source ; 
		}
		for(int i = 4; i <= lSource.length(); i ++)
		{
			mpaint.getTextBounds(lSource, 0, i, rect);
			if(rect.right - rect.left > width)
			{
				dest = lSource.substring(3, i-1);
				return dest + "...";
			}
		}
		dest = source + "..." ;
		return dest;
	}
	
	/**
	 * 取得制定文本长度返回的位置
	 * @param mpaint
	 * @param source
	 * @param width
	 * @param flag
	 * @return
	 */
	private int getLine(Paint mpaint,String source, int width,boolean flag){
		Rect rect = new Rect();
		for(int i = 1; i <= source.length(); i ++)
		{
			mpaint.getTextBounds(source, 0, i, rect);
			if(rect.right - rect.left > width)
			{
				return i;
			}
		}
		return source.length();
	}
	
	/**
	 * 取的文本在view上的宽度
	 * @param paint
	 * @param source
	 * @return
	 */
	private int getWidth(Paint paint,String source){
		Rect rect = new Rect();
		paint.getTextBounds(source, 0, source.length(), rect);
		return rect.right - rect.left;
	}
	
	/**
	 * 在指定宽度中文本的宽度
	 * @param paint
	 * @param source
	 * @param width
	 * @return
	 */
	private int getWidth(Paint paint,String source,int width){
		int w = getWidth(paint,source);
		return width > w ? (width - w) / 2 : 0 ;
	}
	
	/**
	 * 点击事件
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		viewisFocus = true ; 
		float x = event.getX() , y = event.getY();
//		Logger.i(TAG, "x=" + x + "  y = " + y);
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			int ret = pressButton(x, y);
			if(ret != 0){
				focusIdx = ret ;
				invalidate();
			}
			return true ;
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			int ret =  pressButton(x, y) ;
			if (focusIdx == ret) {
				requestFocus();
				click();
			}
			return true;
		}
		return false;
	}

	/**
	 * 判断点的坐标，确定点击的按钮
	 * @param x
	 * @param y
	 * @return
	 */
	private int pressButton(float x, float y) {
		Logger.i(TAG, "pressButton    x = " + x + "  y = " + y);
		if(x >= 135 && x <= 576 && y >= 150 && y <= 200){
			return 1 ;
		}
		
		if (x >= 51 && x <= 165 && y >= 208 && y <= 245) {
			return 2;
		}
		if (x > 165 && x <= 280 && y >= 208 && y <= 245) {
			return 3 ;
		}
		if (x > 280 && x <= 405 && y >= 208 && y <= 245) {
			return 4 ;
		}
		if (x > 405 && x <= 540 && y >= 208 && y <= 245) {
			return 5 ;
		}
		if(x >= 20 && x < 200 && y > 245 && y <= 280 ){
			return 6 ;
		}
		if(x >= 400 && x <= 580 && y > 245 && y <= 280){
			return 7 ;
		}
		if(x >= 30 && x <= 300 && y > 280 && y <= 315){
			return 8 ;
		}
		if(x >= 300 && x <= 570 && y > 280 && y <= 315){
			return 9 ;
		}
		if(x >= 30 && x <= 300 && y > 315 && y <= 355){
			return 10 ;
		}
		if(x >= 300 && x <= 570 && y > 315 && y <= 355){
			return 11 ;
		}
		if(x >= 20 && x < 200 && y > 355 && y <= 390 ){
			return 12 ;
		}
		if(x >= 400 && x <= 580 && y > 355 && y <= 390){
			return 13;
		}
		if(x >= 30 && x <= 570 && y > 390 && y <= 430){
			return 14 ;
		}
		if(x >= 30 && x <= 570 && y > 430 && y <= 470){
			return 15 ;
		}
		if(x >= 15 && x < 216 && y > 470 && y <= 510 ){
			return 16;
		}
		if(x >= 216 && x <= 300 && y > 470 && y <= 510){
			return 17 ;
		}
		if(x > 300 && x < 470 && y > 470 && y <= 510 ){
			return 18 ;
		}
		if(x >= 500 && x <= 600 && y > 470 && y <= 510){
			return 19 ;
		}
		
		if(x >= 18 && x <= 102 && y > 515 && y <= 630){
			return 20 ;
		}
		
		if(x > 102 && x <= 196 && y > 515 && y <= 630){
			return 21 ;
		}
		
		if(x >= 196 && x <= 300 && y > 515 && y <= 630){
			return 22 ;
		}
		
		if(x >= 301 && x <= 405 && y > 515 && y <= 630){
			return 23 ;
		}
		
		if(x >= 405 && x <= 495 && y > 515 && y <= 630){
			return 24 ;
		}
		
		if(x >= 495 && x <= 600 && y > 515 && y <= 630){
			return 25 ;
		}
		return 0 ;
	}

	/***
	 * 键盘事件
	 */
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Logger.i(TAG, "onkey" + keyCode);
		if(event.getAction() == KeyEvent.ACTION_DOWN){
			
			if(keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
				click();
				return true ;
			}else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
				if(focusIdx >= 3 && focusIdx <= 5  || focusIdx > 6 && focusIdx % 2 == 1 && focusIdx < 15 || focusIdx > 16 && focusIdx <= 19 || focusIdx > 20 && focusIdx <= 25){
					focusIdx --  ;
					invalidate();
					return true ;
				}
					
			}else if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
				if(focusIdx >= 2 && focusIdx <= 4  || focusIdx >= 6 && focusIdx % 2 == 0 && focusIdx < 13 || focusIdx >= 16 && focusIdx < 19 || focusIdx >= 20 && focusIdx < 25){
					focusIdx ++ ;
					invalidate();
					return true ;
				}
			}else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
				switch(focusIdx){
				case 1:focusIdx = 2 ;break;
				case 2:
				case 3:focusIdx = 6 ;break;
				case 4:
				case 5:focusIdx = 7; break;
				case 6:focusIdx = 8; break;
				case 7:focusIdx = 9; break;
				case 8:focusIdx = 10; break;
				case 9:focusIdx = 11; break;
				case 10:focusIdx = 12; break;
				case 11:focusIdx = 13; break;
				case 12:focusIdx = 14; break;
				case 13:focusIdx = 14; break;
				case 14:focusIdx = 15; break;
				case 15:focusIdx = 16; break;
				case 16:focusIdx = 20; break;
				case 17:focusIdx = 22; break;
				case 18:focusIdx = 23; break;
				case 19:focusIdx = 25; break;
				default :return false ;
				}
				invalidate();
				return true;
			}else if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
				switch(focusIdx){
				case 2:focusIdx = 1;break;
				case 3:focusIdx = 1 ;break;
				case 4:focusIdx = 1 ;break;
				case 5:focusIdx = 1; break;
				case 6:focusIdx = 2; break;
				case 7:focusIdx = 5; break;
				case 8:focusIdx = 6; break;
				case 9:focusIdx = 7; break;
				case 10:focusIdx = 8; break;
				case 11:focusIdx = 9; break;
				case 12:focusIdx = 10; break;
				case 13:focusIdx = 11; break;
				case 14:focusIdx = 12; break;
				case 15:focusIdx = 14; break;
				case 16:focusIdx = 15; break;
				case 17:focusIdx = 15; break;
				case 18:focusIdx = 15; break;
				case 19:focusIdx = 15; break;
				case 20:focusIdx = 16; break;
				case 21:focusIdx = 16; break;
				case 22:focusIdx = 17; break;
				case 23:focusIdx = 18; break;
				case 24:focusIdx = 18; break;
				case 25:focusIdx = 19; break;
				default:return false;
				}
				invalidate();
				return true;
			}
		}
		return false;
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		viewisFocus = hasFocus ;
		Logger.i(TAG, "onFocusChange " + v.getId() +  ":" + hasFocus);
	}

	/**
	 * 取得view对应的数据
	 * @return
	 */
	public BookFunctionBean getBookFunction() {
		return bookFunction;
	}

	/**
	 * 设置view指定的数据
	 * @param bookFunction
	 */
	public void setBookFunction(BookFunctionBean bookFunction) {
		this.bookFunction = bookFunction;
		invalidate();
	}

	/**
	 * 取得view对应的数据
	 * @return
	 */
	public BookChapterBean getBookChapterBean() {
		return bookChapterBean;
	}

	/**
	 * 设置view指定的数据
	 * @param bookChapterBean
	 */
	public void setBookChapterBean(BookChapterBean bookChapterBean) {
		this.bookChapterBean = bookChapterBean;
		invalidate();
	}

	/**
	 * 取得view对应的数据
	 * @return
	 */
	public BookCommentBean getBookCommentBean() {
		return bookCommentBean;
	}

	/**
	 * 设置view指定的数据
	 * @param bookCommentBean
	 */
	public void setBookCommentBean(BookCommentBean bookCommentBean) {
		this.bookCommentBean = bookCommentBean;
		invalidate();
	}

	/**
	 * 取得view对应的数据
	 * @return
	 */
	public BookRelationshipBean getBookRelationshipBean() {
		return bookRelationshipBean;
	}

	/**
	 * 设置view指定的数据
	 * @param bookRelationshipBean
	 */
	public void setBookRelationshipBean(BookRelationshipBean bookRelationshipBean) {
		this.bookRelationshipBean = bookRelationshipBean;
		invalidate();
	}
	
	/**
	 * 设置view指定的数据
	 * @param bookSunmaryInfo
	 * @param bookFunction
	 * @param bookChapterBean
	 * @param bookCommentBean
	 * @param bookRelationshipBean
	 */
	public void setDataSource(BookSummaryBean bookSunmaryInfo
			,BookFunctionBean bookFunction
			,BookChapterBean bookChapterBean
			,BookCommentBean bookCommentBean
			,BookRelationshipBean bookRelationshipBean){
		this.bookSunmaryInfo = bookSunmaryInfo ;
		this.bookFunction = bookFunction ;
		this.bookChapterBean = bookChapterBean ;
		this.bookCommentBean = bookCommentBean ;
		this.bookRelationshipBean = bookRelationshipBean ;
		invalidate();
	}
	
	/**
	 * 处理相应的操作
	 */
	private void click(){
		Logger.i(TAG, "click:" + focusIdx );
		switch(focusIdx){
		case 1:
			if(bookSunmaryInfo.contentAbstractListen != null){
				bookSunmaryInfo.contentAbstractListen.onClick();
			}
			break ;
		case 2:
			if (bookFunction.readonlineListen != null) {
				bookFunction.readonlineListen.onClick();
			}
			break ;
		case 3:
			if(bookFunction.downreadListen != null){
				bookFunction.downreadListen.onClick();
			}
			break ;
		case 4:
			if(bookFunction.reservationUpdateListen != null){
				bookFunction.reservationUpdateListen.onClick();
			}
			break ;
		case 5:
			if(bookFunction.collectionreadListen != null){
				bookFunction.collectionreadListen.onClick();
			}
			break ;
		case 6:
		case 7:
			if(bookChapterBean.chapterAllListen != null){
				bookChapterBean.chapterAllListen.onClick();
			}
			break ;
		case 8:
			if(bookChapterBean.chapterListen[0] != null){
				bookChapterBean.chapterListen[0].onClick();
			}
			break ;
		case 9:
			if(bookChapterBean.chapterListen[1] != null){
				bookChapterBean.chapterListen[1].onClick();
			}
			break ;
		case 10:
			if(bookChapterBean.chapterListen[2] != null){
				bookChapterBean.chapterListen[2].onClick();
			}
			break ;
		case 11:
			if(bookChapterBean.chapterListen[3] != null){
				bookChapterBean.chapterListen[3].onClick();
			}
			break ;
		case 12:
		case 13:
			if(bookCommentBean.commentAllListen != null){
				bookCommentBean.commentAllListen.onClick();
			}
			break ;
		case 14:
			if(bookCommentBean.commentListen[0] != null){
				bookCommentBean.commentListen[0].onClick();
			}
			break ;
		case 15:
			if(bookCommentBean.commentListen[1] != null){
				bookCommentBean.commentListen[1].onClick();
			}
		break ;
		case 16:
		case 17:
			if(bookRelationshipBean.bookRelationshipListen[0] != null){
				bookRelationshipBean.bookRelationshipListen[0].onClick();
			}
			break ;
		case 18:
		case 19:
			if(bookRelationshipBean.bookRelationshipListen[1] != null){
				bookRelationshipBean.bookRelationshipListen[1].onClick();
			}
			break ;
		case 20:
			if(bookRelationshipBean.bookListen[0] != null){
				bookRelationshipBean.bookListen[0].onClick();
			}
			break ;
		case 21:
			if(bookRelationshipBean.bookListen[1] != null){
				bookRelationshipBean.bookListen[1].onClick();
			}
			break ;
		case 22:
			if (bookRelationshipBean.bookListen[2] != null) {
				bookRelationshipBean.bookListen[2].onClick();
			}
			break ;
		case 23:
			if (bookRelationshipBean.bookListen[3] != null) {
				bookRelationshipBean.bookListen[3].onClick();
			}
			break ;
		case 24:
			if (bookRelationshipBean.bookListen[4] != null) {
				bookRelationshipBean.bookListen[4].onClick();
			}
			break ;
		case 25:
			if (bookRelationshipBean.bookListen[5] != null) {
				bookRelationshipBean.bookListen[5].onClick();
			}
			break ;
		default :
			if(defaultListen != null) defaultListen.onClick();
			break ;
		}
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		return ;
	}
}
