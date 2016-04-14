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

/**��װ�ײ�������
 * @author rd040 ������
 *
 */
public class PviBottomBar extends View{
    private static final String TAG = "PviBottomBar";
    private Context mContext;
    private Resources mRes;
    
    private Bitmap mBmp;            //����BottomBar�Ļ���
    
    private boolean firstDraw = true;//��һ��draw�ı�־
    
    //��������
    private float mWidth;         //���� ���
    private float mHeight;         //���� �߶�

    
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

        //��ʼ������ֵ
        mWidth = 600f;
        mHeight = 49f;
        
      //����layout�Ŀ�ȸ߶ȣ�����λͼ
        mBmp = Bitmap.createBitmap((int)mWidth,(int)mHeight,Config.ARGB_8888);
        
        
        //���ó�ʼ����  
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
        
        
        //��һ��
        OnClickListener lPrevChap = new OnClickListener(){

            @Override
            public void onClick(View v) {
                if(pbb.chapable!=null){
                    //Logger.d(TAG,"chapable.OnPrevchap()");
                pbb.chapable.OnPrevchap();
                }
            }};
        addItem(new PviUiItem("prevchap", R.drawable.button_prechapter_ui1, 190,8, 60, 45, null,false,false, lPrevChap));
        //��һ��
        OnClickListener lNextChap = new OnClickListener(){

            @Override
            public void onClick(View v) {
                if(pbb.chapable!=null){
                    //Logger.d(TAG,"chapable.OnNextchap()");
                pbb.chapable.OnNextchap();
                }
            }};
        addItem(new PviUiItem("nextchap", R.drawable.button_nextchapter_ui1, 380,8, 60, 45, null,false,false, lNextChap));
        
        
        //��ҳ��
        
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

            if (firstDraw) {//ֻ�ڵ�һ��OnDraw��ʱ�򣬻���һ�α�������ʵ��������ΪView�ı������ԣ���δ�����������ַ�ʽ�Ƿ���Ч���ϵĲ�ͬ
                addBg();
                firstDraw = false;
            }

            // �ػ�mBmp��View�Ļ�����
            Paint paint = new Paint();
            canvas.drawBitmap(mBmp, 0, 0, paint);
        }

        super.onDraw(canvas);
    }
    
    /**
     * ���Ʊ���
     */
    private void addBg(){
        Canvas tmpCv = new Canvas(mBmp);
        
        Bitmap bmp = BitmapFactory.decodeResource(mRes, R.drawable.statusbar_bottom_ui1);
        Paint paint = new Paint();
        tmpCv.drawBitmap(bmp, 0, 0, paint);
    }
    
    /**
     * ���� ĳ����ˢ��
     * ��������ʼx,���
     */
    private void drawWhite(float x,float y ,float width){
        final Canvas tmpCv = new Canvas(mBmp);
        final Paint paint = new Paint(); 
        paint.setColor(Color.WHITE);
        tmpCv.drawRect( x, y,x+width,mHeight, paint);   
    }
    
    /**
     * ���۽�����ĳUIԪ��
     * ʵ����������һ����־�����Լ�¼�ĸ��ؼ�Ŀǰ�����ڽ���λ�á���
     * ��ʱ�����OK����Ӧ�ô����ÿؼ����¼���
     * �ÿؼ��ľ۽�Ч��Ӧ�ñ������ͬʱ�����Ǿ۽��ؼ���Ӧ�ñ�����Ϊnormal״̬���زģ�
     * ������Viewʧȥ����ʱ�������ӿؼ�Ӧ��ȫ��������Ϊnormal״̬
     *
     *������ArrayList���index
     */
    private void focusItem(int index) {
        //Logger.d(TAG,"index:"+index+"  �� I want get focus ; mCurFoucsItemIndex is "+mCurFoucsItemIndex);

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
     * ����һ���µ�ui�ؼ����ڷŵ� ���� ��ָ��λ��
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
     * ͨ��idȡ��һ��uiitem
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
     * ͨ��indexȡ��һ��item
     */
    private PviUiItem getItem(int index){
        if(mUiList!=null
                &&index>=0&&index<mUiList.size()){
            return mUiList.get(index);
        }
        return null;
    } 
    
    /**
     * ����һ��item
     * @param item
     */
    private void drawItem(PviUiItem item){
        if(item==null){
            Logger.e(TAG,"item is null");
            return;
        }
        
        if(item.isVisible){//���ƿɼ�item
        
        //�װ�
        Bitmap bmp = Bitmap.createBitmap((int)item.width,(int)item.height,Config.ARGB_8888);
        
        //���Ʊ���
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
                if(item.isFocuse){//�����н���ʱ���ز�                    
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
        
        //�����ı�
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
            //���Ƶ� ���� ��
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
        
        //����ý���ʱ �۽���һ��ui�ؼ�
        if(gainFocus){
            focusItem(0);
        }else{//ʧȥ����ʱ�����пؼ���Ӧ��ʧȥ���㣬���ػ�
            focusItem(-1);
        }
        
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    
    private int mCurFoucsItemIndex = -1;// Ĭ�Ͼ۽���һ���ؼ�

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            //Logger.d(TAG,"onKeyDown");

            // �����ƶ����㴦��
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
            // ���������OK��������õ�ǰ����item��click
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
                // Logger.d(TAG,"��Ҫ�۽��� "+clickIndex);                
                if(item!=null
                        && item.isVisible && !item.isFocuse){
                    focusItem(clickIndex); 
                    
                    //��ʱȡ�����н���Ч��
                    mHandler.postDelayed(new Runnable(){

                        @Override
                        public void run() {
                            focusItem(-1);
                            
                        }} ,100);
                }
            }
            
            if(clickIndex > -1){
                //�����¼�
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
     * ��ҳ�ӿ�
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
     * ���½ӿ�
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
     * onBack�ӿ�
     */
    public interface OnBackListener{
        boolean onBack();
    }
    private OnBackListener mOnBackListener;
    public void setOnBackListener(OnBackListener l){
        mOnBackListener = l;
    }
    
/*    *//**
     * ���·�ҳ����Ϣ   
     * @param text  ���� ��12 / 200��
     *//*
    public void updatePagerinfo(String text){
        //Logger.d(TAG,"updatePagerinfo:"+text);
        PviUiItem item = getItem("pagerinfo");
        item.text = text;
        //drawItem(item);
        //invalidate();  �����Ƿ�ᵼ�£���û��show����me��������ʾ����ҳ�룬Ӧ�ò���ɣ���Ϊ��û��������ؿؼ���isvisibleΪtrue
    }*/
    
/*    *//**
     * ���ط�ҳ�������һ����ܱ����
     *//*
    public void hidePager(Context context){
        this.getItem("prevpage").isVisible = false;
        this.getItem("pagerinfo").isVisible = false;
        this.getItem("nextpage").isVisible = false;
        ((PviActivity)context).showPager = false;
        updateDraw();
    }
    
    *//**
     * ��ʾ��ҳ��
     *//*
    public void showPager(Context context){
        this.getItem("prevpage").isVisible = true;
        this.getItem("pagerinfo").isVisible = true;
        this.getItem("nextpage").isVisible = true;
        ((PviActivity)context).showPager = true;
        updateDraw();
    }*/
    
    /**
     * ����item����/��
     */
    public void setItemVisible(String id,boolean isVisible){
        PviUiItem item = getItem(id);
        if(item!=null){
            item.isVisible = isVisible;
        }
    }

    /**
     * ��������UI����
     */
    private void updateDraw(){
        //Logger.d(TAG,"updateDraw()");
        if(this.mUiList==null){
            Logger.e(TAG,"mUiList is null");
            return;
        }
        
        //�����߼�������ҳ��Ŀ�ȣ���̬ȷ�� ��һ�� ��һҳ ҳ����ʾ ��һҳ ��һ�� �Ļ���λ��
        
        
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
        
        drawWhite(100,5,600-160);//ˢ��
        
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
        //��ȡ��ǰacitivty�ķ�ҳ��Ϣ���ػ�
        if(pageable!=null){
            PviUiItem item = getItem("pagerinfo");
            item.text = ((PviActivity)pageable).getPagerinfo();
            updateDraw();
        }
    }
    
    /**
     * ���ط�ҳ�������һ����ܱ����
     */
    public void actionHidePager(){
        this.getItem("prevpage").isVisible = false;
        this.getItem("pagerinfo").isVisible = false;
        this.getItem("nextpage").isVisible = false;
        updateDraw();
    }
    
    /**
     * ��ʾ��ҳ��
     */
    public void actionShowPager(){
        this.getItem("prevpage").isVisible = true;
        this.getItem("pagerinfo").isVisible = true;
        this.getItem("nextpage").isVisible = true;
        updateDraw();
    }
    
    /**
     * ���ط�ҳ�������һ����ܱ����
     */
    public void actionHideChaper(){
        this.getItem("prevchap").isVisible = false;
        this.getItem("nextchap").isVisible = false;
        updateDraw();
    }
    
    /**
     * ��ʾ��ҳ��
     */
    public void actionShowChaper(){
        this.getItem("prevchap").isVisible = true;
        this.getItem("nextchap").isVisible = true;
        updateDraw();
    }
}
