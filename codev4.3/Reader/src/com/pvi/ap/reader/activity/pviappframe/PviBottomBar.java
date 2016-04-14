/**
 * 
 */
package com.pvi.ap.reader.activity.pviappframe;

import java.util.ArrayList;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.data.common.Logger;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/**封装底部工具栏
 * @author rd040 马中庆
 *
 */
public class PviBottomBar extends View{
    private static final String TAG = "PviBottomBar";
    private Context mContext;
    private Resources mRes;
    
    private Bitmap mBmp;            //保存BottomBar的画面
    
    private boolean firstDraw = true;//第一次draw的标志
    
    //基本属性
    private float mWidth;         //底栏 宽度
    private float mHeight;         //底栏 高度

    
    private ArrayList<PviUiItem> mUiList= new  ArrayList<PviUiItem>();
    
    private Handler mHandler = new H();
    private class H extends Handler {}

    /**
     * @param context
     * @param attrs
     */
    public PviBottomBar(Context context, AttributeSet attrs) {        
        super(context, attrs);
        
        if(GlobalVar.deviceType==1){
//            setUpdateMode(View.EINK_WAVEFORM_MODE_GC16);
        }
        
        if(!isInEditMode()){
        
        mContext = context;
        mRes = context.getResources();

        //初始化属性值
        mWidth = 600f;
        mHeight = 49f;
        
      //根据layout的宽度高度，创建位图
        mBmp = Bitmap.createBitmap((int)mWidth,(int)mHeight,Config.ARGB_8888);
        
        
        //设置初始界面  
        final PviBottomBar pbb = this;

        OnClickListener l0 = new OnClickListener(){

            @Override
            public void onClick(View v) {
                //Logger.d(TAG,"cause onKeyUp(KEYCODE_MENU)");
                // TODO Auto-generated method stub
                /*final Intent tmpIntent = new Intent(PviStatusBar.DOWNLOAD_STATE_UPDATE);
                final Bundle bundleToSend = new Bundle();
                bundleToSend.putInt("STATE", 1);
                tmpIntent.putExtras(bundleToSend);
                mContext.sendBroadcast(tmpIntent);*/

                ((Activity)mContext).onKeyUp(KeyEvent.KEYCODE_MENU, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MENU));
            }};
        addItem(new PviUiItem("menu", R.drawable.button_menu_ui1, 5,4, 60, 45, null,false, true,l0));
        
        
        //上一章
        OnClickListener lPrevChap = new OnClickListener(){

            @Override
            public void onClick(View v) {
                if(pbb.chapable!=null){
                    //Logger.d(TAG,"chapable.OnPrevchap()");
                pbb.chapable.OnPrevchap();
                }
            }};
        addItem(new PviUiItem("prevchap", R.drawable.button_prechapter_ui1, 190,8, 60, 45, null,false,false, lPrevChap));
        //下一章
        OnClickListener lNextChap = new OnClickListener(){

            @Override
            public void onClick(View v) {
                if(pbb.chapable!=null){
                    //Logger.d(TAG,"chapable.OnNextchap()");
                pbb.chapable.OnNextchap();
                }
            }};
        addItem(new PviUiItem("nextchap", R.drawable.button_nextchapter_ui1, 380,8, 60, 45, null,false,false, lNextChap));
        
        
        //分页条
        
        OnClickListener l4 = new OnClickListener(){

            @Override
            public void onClick(View v) {
                actionPrevPage();
            }};
        addItem(new PviUiItem("prevpage", R.drawable.button_prepage_ui1, 250,6, 40, 45, null,false,false, l4));
        
        PviUiItem pagerinfo =new PviUiItem("pagerinfo", 0, 290,12, 60, 45, "1 / 1",false,false, null);
        pagerinfo.setFocusable(false);
        addItem(pagerinfo);
        
        OnClickListener l5 = new OnClickListener(){

            @Override
            public void onClick(View v) {
                actionNextPage();
            }};
        addItem(new PviUiItem("nextpage", R.drawable.button_nextpage_ui1, 350,6, 40, 45, null,false,false, l5));
        
        
        
        OnClickListener l1 = new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                final Bundle bundleToSend = new Bundle();
                bundleToSend.putString("actID", "ACT15000");
                tmpIntent.putExtras(bundleToSend);
                mContext.sendBroadcast(tmpIntent);
            }};
        addItem(new PviUiItem("setting", R.drawable.button_setting_ui1, 450,4, 45, 45, null,false,true, l1));
        
        OnClickListener l2 = new OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
                final Bundle bundleToSend = new Bundle();
                bundleToSend.putString("actID", "ACT13200");
                tmpIntent.putExtras(bundleToSend);
                mContext.sendBroadcast(tmpIntent);
            }};
        addItem(new PviUiItem("music", R.drawable.button_music_ui1, 500,4, 45, 45, null,false,true, l2));
        
        OnClickListener l3 = new OnClickListener(){

            @Override
            public void onClick(View v) {    
                    if (mOnBackListener != null) {
                        if (!mOnBackListener.onBack()) {
                            ((Activity) mContext).onKeyUp(
                                    KeyEvent.KEYCODE_BACK, new KeyEvent(
                                            KeyEvent.ACTION_UP,
                                            KeyEvent.KEYCODE_BACK));
                        }
                    } else {
                        ((Activity) mContext).onKeyUp(KeyEvent.KEYCODE_BACK,
                                new KeyEvent(KeyEvent.ACTION_UP,
                                        KeyEvent.KEYCODE_BACK));
                    }
            }};
        addItem(new PviUiItem("back", R.drawable.button_back_ui1, 550,4, 45, 45, null,false,true, l3));
        
        

        
        invalidate();
        
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Logger.d(TAG, "onDraw");
        if (!isInEditMode()) {

            if (firstDraw) {//只在第一次OnDraw的时候，绘制一次背景，其实可以设置为View的背景属性，尚未具体区别两种方式是否有效率上的不同
                addBg();
                firstDraw = false;
            }

            // 重绘mBmp到View的画布上
            Paint paint = new Paint();
            canvas.drawBitmap(mBmp, 0, 0, paint);
        }

        super.onDraw(canvas);
    }
    
    /**
     * 绘制背景
     */
    private void addBg(){
        Canvas tmpCv = new Canvas(mBmp);
        
        Bitmap bmp = BitmapFactory.decodeResource(mRes, R.drawable.statusbar_bottom_ui1);
        Paint paint = new Paint();
        tmpCv.drawBitmap(bmp, 0, 0, paint);
    }
    
    /**
     * 底栏 某区域刷白
     * 参数：起始x,宽度
     */
    private void drawWhite(float x,float y ,float width){
        final Canvas tmpCv = new Canvas(mBmp);
        final Paint paint = new Paint(); 
        paint.setColor(Color.WHITE);
        tmpCv.drawRect( x, y,x+width,mHeight, paint);   
    }
    
    /**
     * “聚焦”到某UI元素
     * 实际上是设置一个标志，可以记录哪个控件目前“处于焦点位置”；
     * 此时如果按OK键，应该触发该控件的事件；
     * 该控件的聚焦效果应该被绘出，同时其它非聚焦控件的应该被绘制为normal状态的素材；
     * 当整个View失去焦点时，所有子控件应该全部被绘制为normal状态
     *
     *参数：ArrayList里的index
     */
    private void focusItem(int index) {
        //Logger.d(TAG,"index:"+index+"  ： I want get focus ; mCurFoucsItemIndex is "+mCurFoucsItemIndex);

        if (index != mCurFoucsItemIndex) {
            mCurFoucsItemIndex = index;
            final int uiItemCount = mUiList.size();
            for (int i = 0; i < uiItemCount; i++) {
                PviUiItem item = mUiList.get(i); 
                if(item.isVisible){
                if (i == index) {
                    item.isFocuse = true;
                } else {
                    item.isFocuse = false;
                }
                drawItem(item);
                }
            }

            invalidate();
        }

    }
    
    /**
     * 增加一个新的ui控件，摆放到 底栏 的指定位置
     */
    private void addItem(PviUiItem item){
        if(item==null){
            Logger.e(TAG,"item is null");
            return;
        }
        mUiList.add(item);
        if(item.isVisible){
        drawItem(item);
        }
    }
    
    /**
     * 通过id取出一个uiitem
     */
    private PviUiItem getItem(String id){
        final int count = mUiList.size();
        for(int i=0;i<count;i++){
            PviUiItem item = mUiList.get(i);
            if(item.id.equals(id)){
                //Logger.d(TAG,"getItem id is "+id);
                return item;
            }
        }
        return null;
    }
    
    /**
     * 通过index取出一个item
     */
    private PviUiItem getItem(int index){
        if(mUiList!=null
                &&index>=0&&index<mUiList.size()){
            return mUiList.get(index);
        }
        return null;
    } 
    
    /**
     * 绘制一个item
     * @param item
     */
    private void drawItem(PviUiItem item){
        if(item==null){
            Logger.e(TAG,"item is null");
            return;
        }
        
        if(item.isVisible){//绘制可见item
        
        //底板
        Bitmap bmp = Bitmap.createBitmap((int)item.width,(int)item.height,Config.ARGB_8888);
        
        //绘制背景
        if(item.res>0){
            //.d(TAG,"draw item's bg");
            Canvas cv = new Canvas(bmp);
            Paint paint = new Paint();
            Drawable d = mRes.getDrawable(item.res);
            if(d instanceof BitmapDrawable){
                Bitmap b = ((BitmapDrawable)d).getBitmap();
                cv.drawBitmap(b, 0, 0, paint);
            }else if(d instanceof StateListDrawable){
                StateListDrawable dd = (StateListDrawable)d;
                if(item.isFocuse){//绘制有焦点时的素材                    
                    dd.setState(FOCUSED_STATE_SET);   
                    //Logger.d(TAG,item.id+" :draw FOCUSED_STATE_SET");
                }else{
                    dd.setState(EMPTY_STATE_SET);
                    //Logger.d(TAG,item.id+" :draw EMPTY_STATE_SET");
                }
                Drawable ddd = dd.getCurrent();
                Bitmap b = ((BitmapDrawable)ddd).getBitmap();
                cv.drawBitmap(b, 0, 0, paint);
            }
        }else{
            //Logger.d(TAG,"this item has no bg");
        }
        
        //绘制文本
        if(item.text!=null&&!"".equals(item.text)){
            Canvas cv = new Canvas(bmp);
            Paint paint = new Paint();   
            paint.setColor(Color.WHITE);
            cv.drawRect(0, 0, item.width, item.height, paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(item.textSize);
            paint.setAntiAlias(true);
            //Logger.d(TAG,"draw item's text:"+item.text);
            cv.drawText(item.text, 0, item.textSize, paint);
        }
        
        synchronized (mBmp) {
            //绘制到 底栏 上
            Canvas cv = new Canvas(mBmp);
            Paint paint = new Paint();
            cv.drawBitmap(bmp, item.left, item.top, paint);
        }
        
        }
    }
    

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        
        //Logger.d(TAG,"onFocusChanged(...   am I have focus ?"+gainFocus);
        
        //当获得焦点时 聚焦第一个ui控件
        if(gainFocus){
            focusItem(0);
        }else{//失去焦点时，所有控件都应该失去焦点，并重绘
            focusItem(-1);
        }
        
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    
    private int mCurFoucsItemIndex = -1;// 默认聚焦第一个控件

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            //Logger.d(TAG,"onKeyDown");

            // 左右移动焦点处理
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mCurFoucsItemIndex == 0)
                    return false;
                focusItem(mCurFoucsItemIndex - 1);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mCurFoucsItemIndex == mUiList.size() - 1)
                    return false;
                focusItem(mCurFoucsItemIndex + 1);
                return true;
            }else
            // 如果按下了OK键，则调用当前焦点item的click
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                    && mUiList!=null
                    && mCurFoucsItemIndex>=0 && mCurFoucsItemIndex<mUiList.size()
                    && mUiList.get(mCurFoucsItemIndex)!=null) {
                mUiList.get(mCurFoucsItemIndex).performClick();
            }
            

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        //Logger.d(TAG,"onTouchEvent: action:"+event.getAction()+", x:"+event.getX()+",y:"+event.getY());

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int clickIndex = getUiIndexByXY(event.getX(), event.getY());

            //Logger.d(TAG,"clickIndex:"+clickIndex);
            PviUiItem item = getItem(clickIndex);
            
            if (clickIndex > -1 && clickIndex != mCurFoucsItemIndex) {
                // Logger.d(TAG,"需要聚焦到 "+clickIndex);                
                if(item!=null
                        && item.isVisible && !item.isFocuse){
                    focusItem(clickIndex); 
                    
                    //延时取消所有焦点效果
                    mHandler.postDelayed(new Runnable(){

                        @Override
                        public void run() {
                            focusItem(-1);
                            
                        }} ,100);
                }
            }
            
            if(clickIndex > -1){
                //触发事件
                if(item!=null
                        && item.isVisible ){
                    item.performClick(); 
                }
            }


        }/*else if (event.getAction() == MotionEvent.ACTION_UP) {
            Logger.e(TAG,"touch up! mCurFoucsItemIndex is "+mCurFoucsItemIndex);
            if(mCurFoucsItemIndex!=-1){
                focusItem(-1);
            }
        }*/

        return super.onTouchEvent(event);
    }

    private int getUiIndexByXY(float x, float y){
        final int uiItemCount = mUiList.size();
        for(int i=0;i<uiItemCount;i++){
            PviUiItem item = mUiList.get(i);
            if(x>item.left&&x<(item.left+item.width)
            &&y>item.top && y<(item.top+item.height)        
            ){
                return i;
            }
        }
        return -1;
    }
    
    
    
    /**
     * 翻页接口
     *
     */
    public interface Pageable {
        void OnPrevpage();
        void OnNextpage();
    }
    Pageable pageable;
    public void setPageable(Pageable p) {
        this.pageable = p;
    }
    
   
    
    /**
     * 翻章接口
     *
     */
    public interface Chapable {
        void OnPrevchap();
        void OnNextchap();
    }
    Chapable chapable;
    public void setChapable(Chapable c) {
        this.chapable = c;
    }    
    
    /**
     * onBack接口
     */
    public interface OnBackListener{
        boolean onBack();
    }
    private OnBackListener mOnBackListener;
    public void setOnBackListener(OnBackListener l){
        mOnBackListener = l;
    }
    
/*    *//**
     * 更新分页条信息   
     * @param text  形如 “12 / 200”
     *//*
    public void updatePagerinfo(String text){
        //Logger.d(TAG,"updatePagerinfo:"+text);
        PviUiItem item = getItem("pagerinfo");
        item.text = text;
        //drawItem(item);
        //invalidate();  这里是否会导致，还没有show——me，就先显示出了页码，应该不会吧，因为还没有设置相关控件的isvisible为true
    }*/
    
/*    *//**
     * 隐藏分页条，而且还不能被点击
     *//*
    public void hidePager(Context context){
        this.getItem("prevpage").isVisible = false;
        this.getItem("pagerinfo").isVisible = false;
        this.getItem("nextpage").isVisible = false;
        ((PviActivity)context).showPager = false;
        updateDraw();
    }
    
    *//**
     * 显示分页条
     *//*
    public void showPager(Context context){
        this.getItem("prevpage").isVisible = true;
        this.getItem("pagerinfo").isVisible = true;
        this.getItem("nextpage").isVisible = true;
        ((PviActivity)context).showPager = true;
        updateDraw();
    }*/
    
    /**
     * 设置item的显/隐
     */
    public void setItemVisible(String id,boolean isVisible){
        PviUiItem item = getItem(id);
        if(item!=null){
            item.isVisible = isVisible;
        }
    }

    /**
     * 更新整个UI画面
     */
    private void updateDraw(){
        //Logger.d(TAG,"updateDraw()");
        if(this.mUiList==null){
            Logger.e(TAG,"mUiList is null");
            return;
        }
        
        //增加逻辑：根据页码的宽度，动态确定 上一章 上一页 页码显示 下一页 下一章 的绘制位置
        
        
        final PviUiItem prevchap = getItem("prevchap");
        final PviUiItem nextchap = getItem("nextchap");
        final PviUiItem prevpage = getItem("prevpage");
        final PviUiItem nextpage = getItem("nextpage");
        final PviUiItem pagerinfo = getItem("pagerinfo");
        
        final int pagerinfoDrawWidth = pagerinfo.getTextDrawWidth()+10;
        prevchap.left = 600-160-nextchap.width-nextpage.width-pagerinfoDrawWidth-prevpage.width-prevchap.width;
        nextchap.left = 600-160-nextchap.width;
        prevpage.left = 600-160-nextchap.width-nextpage.width-pagerinfoDrawWidth-prevpage.width;
        nextpage.left = 600-160-nextchap.width-nextpage.width;
        pagerinfo.left = 600-160-nextchap.width-nextpage.width-pagerinfoDrawWidth;
        pagerinfo.width = pagerinfoDrawWidth;
        
        drawWhite(100,5,600-160);//刷白
        
        final int itemCount = mUiList.size();
        for (int i = 0; i < itemCount; i++) {
            final PviUiItem item = mUiList.get(i);
            if(item!=null){
                item.draw(new Canvas(mBmp), mRes);
            }
        }
        invalidate();
    }
    
    
    public void actionNextPage(){
        if(pageable!=null){
            pageable.OnNextpage();
            updateDraw();
        }
    }
    
    public void actionPrevPage(){
        if(pageable!=null){
            pageable.OnPrevpage();
            updateDraw();
        }
    }
    
    public void actionUpdatePagerinfo(){
        //读取当前acitivty的分页信息；重绘
        if(pageable!=null){
            PviUiItem item = getItem("pagerinfo");
            item.text = ((PviActivity)pageable).getPagerinfo();
            updateDraw();
        }
    }
    
    /**
     * 隐藏分页条，而且还不能被点击
     */
    public void actionHidePager(){
        this.getItem("prevpage").isVisible = false;
        this.getItem("pagerinfo").isVisible = false;
        this.getItem("nextpage").isVisible = false;
        updateDraw();
    }
    
    /**
     * 显示分页条
     */
    public void actionShowPager(){
        this.getItem("prevpage").isVisible = true;
        this.getItem("pagerinfo").isVisible = true;
        this.getItem("nextpage").isVisible = true;
        updateDraw();
    }
    
    /**
     * 隐藏分页条，而且还不能被点击
     */
    public void actionHideChaper(){
        this.getItem("prevchap").isVisible = false;
        this.getItem("nextchap").isVisible = false;
        updateDraw();
    }
    
    /**
     * 显示分页条
     */
    public void actionShowChaper(){
        this.getItem("prevchap").isVisible = true;
        this.getItem("nextchap").isVisible = true;
        updateDraw();
    }
}
