package com.pvi.ap.reader.activity.pviappframe;

import java.util.ArrayList;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.data.common.Logger;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

/**
 * �˵����
 * @author rd040 ������
 *
 */
public class PviMenuPan extends View {
    private static final String TAG = "PviMenuPan";

    
    private Context mContext;
    private Resources mRes;
    
    private ArrayList<PviMenuItem> mUiList= new  ArrayList<PviMenuItem>();

    public PviMenuPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public PviMenuPan(Context context) {
        super(context);
        if (!isInEditMode()) {
            mContext = context;
            mRes = context.getResources();
            this.setFocusable(true);
            this.setFocusableInTouchMode(true);

            if (GlobalVar.deviceType == 1) {
//                setUpdateMode(View.EINK_WAVEFORM_MODE_GC16);
            }
        }
    }
    
    /**
     * ����˵���
     * @param menuitem
     */
    public void addMenuItem(PviMenuItem menuitem){
        menuitem.panView = this;
        mUiList.add(menuitem);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //д��������ƻ���
        final int c = mUiList.size();
        int visCount =0;
        for (int i = 0; i < c; i++) {
            PviMenuItem item = mUiList.get(i);
            
            if(item.isVisible){
                item.top = 40*visCount;
                
                if(!item.enable){
                    item.textColor = Color.GRAY;
                    item.clickable = false;
                }else{
                    item.textColor = Color.BLACK;
                    item.clickable = true;
                }
                
                item.draw(canvas, mRes);
                
                visCount++;   
            }
            
        }
        super.onDraw(canvas);
    }
    
    public int getChildCount(){
        return mUiList.size();
    }

    public PviMenuItem getChildAt(int index){
        return mUiList.get(index);
    }

    /**
     * ����view�ߴ磨���ӣ�
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.setMeasuredDimension(190, 40*getVisibleItemCount());
    }
    
    private int getVisibleItemCount(){
        int count=0;
        final int c = mUiList.size();
        for (int i = 0; i < c; i++) {
            PviMenuItem item = mUiList.get(i);
            if(item.isVisible){
                count++;
            }
        }
        return count;
    }

    /**
     * ͨ��id����menuitem
     * @param id
     * @return
     */
    public PviMenuItem getItemById(String id){
        final int c = mUiList.size();
        for (int i = 0; i < c; i++) {
            PviMenuItem item = mUiList.get(i);
            //Logger.d(TAG,"item.id:"+item.id);
            if(id.equals(item.id)){
                return item;
            }
        }
        return null;
    }
    
    private PviMenuItem getItemByY(float y){
        
        int visCount = 0;
        final int c = mUiList.size();
        for (int i = 0; i < c; i++) {
            PviMenuItem item = mUiList.get(i);
            if(item.isVisible){
                float startPosY = item.top;
                float endPosY = item.top+40;
                //Logger.d(TAG,"i:"+i+",startPosY:"+startPosY+",endPosY:"+endPosY+",cur Y :"+y);
                if(y>startPosY
                        && y<endPosY){
                    //Logger.d(TAG,"get menuitem id:"+item.id);
                    return item;
                }
                visCount++;
            }
        }
        return null;
    }
    
    private int getItemIndexByItem(PviMenuItem item){
        final int c = mUiList.size();
        for (int i = 0; i < c; i++) {
            PviMenuItem theItem = mUiList.get(i);
            try {
                if(theItem.id.equals(item.id)){
                   //Logger.d(TAG,"get i:"+i+",id:"+item.id);
                   return i;
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return -1;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {        
        PviMenuItem item = getItemByY(event.getY());
        if(item!=null){
        focusItem(getItemIndexByItem(item));
        if(item.l!=null){
            //Logger.d(TAG,"l1, item clicked: id is "+item.id);
            item.performClick();
        }else if(item.l2!=null){
            //Logger.d(TAG,"l2 ,item clicked: id is "+item.id);
            item.performClick();
        }
        }
        return super.onTouchEvent(event);
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
                if (i == index) {
                    item.isFocuse = true;
                } else {
                    item.isFocuse = false;
                }
            }

            invalidate();
        }

    }
    
    private int mCurFoucsItemIndex = -1;// Ĭ�Ͼ۽���һ���ؼ�

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
            //Logger.d(TAG,"onKeyDown");

            // �����ƶ����㴦��
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                if (mCurFoucsItemIndex <1 )
                    return false;
                focusItem(mCurFoucsItemIndex - 1);
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (mCurFoucsItemIndex > mUiList.size()-2)
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
            
            else if(keyCode == KeyEvent.KEYCODE_MENU){
                //����menuӲ��������֪ͨactiivyȥclose
                ((PviActivity) mContext)
                .closePopmenu();
            }
            

        return super.onKeyDown(keyCode, event);
    }
    
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        if(!isInEditMode()){
            setUpdates(true);
        }

    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!isInEditMode()){
            setUpdates(false);
        }
    }
    
    private boolean mUpdating = false;
    void setUpdates(boolean update) {
        if (update != mUpdating) {
            mUpdating = update;
            if (update) {
                // Register for Intent broadcasts for the clock and battery
                IntentFilter filter = new IntentFilter();
                filter.addAction(PviStatusBar.UPDATE_BGMUSIC_STATE);                
                mContext.registerReceiver(mIntentReceiver, filter, null, null);

            } else {
                mContext.unregisterReceiver(mIntentReceiver);
            }
        }
    }
    
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(PviStatusBar.UPDATE_BGMUSIC_STATE.equals(action)){
                String newStateStr = "";
                final Bundle bd = intent.getExtras();
                if(bd!=null){
                    newStateStr = bd.getString("state");
                    final PviMenuItem item = getItemById("bgmusic");
                    if(item!=null){
                    if("play".equals(newStateStr)){
                        //ȡ��ָ���Ĳ˵��bgmusic
                        //�ı�������ֵ
                        //���»���                        
                        item.text = mRes.getString(R.string.fp_menu1_1);
                        invalidate();
                    }else{
                        item.text = mRes.getString(R.string.fp_menu1);
                        invalidate();
                    }
                    }else{
                        Logger.e(TAG,"menuitem:bgmusic is null");
                    }
                }
            }
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Logger.d(TAG,"dispatchKeyEvent(event:"+event);
        if (
                (event.getAction() == KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.ACTION_MULTIPLE)
                        &&
                        (event.getKeyCode() == KeyEvent.KEYCODE_CAMERA 
                                ||event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP
                                ||event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN        
                        ) ) {

            final Activity thisAct =  (PviActivity) mContext;
            if(thisAct!=null){
                Activity mf =thisAct.getParent();                
                if(mf!=null){
                    if(mf instanceof MainpageActivity){
                        mf.dispatchKeyEvent(event);
                    }else{
                        mf = mf.getParent();
                        if(mf!=null){
                            mf.dispatchKeyEvent(event);
                        }
                    }
                    
                    return true;
                }
            }
            return false;

        }
        return super.dispatchKeyEvent(event);
    }

}
