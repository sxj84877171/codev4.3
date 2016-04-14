package com.pvi.ap.reader.activity.pviappframe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.data.common.Logger;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * 框架 标题栏
 * @author rd040 马中庆
 *
 */
public class PviTitleBar extends View {
    private static final String TAG = "PviTitleBar";

    private Resources mRes;
    private Context mContext;


    private ArrayList<HashMap<String, String>> pathInfo;        //保存pathinfo
    private boolean uipathNeedReDraw = false;
    
    private PviUiItem navSwitch;
    public boolean navIsOpen = false;          //初始未打开导航栏
    //private boolean switchNeedReDraw =true;     //初始需要绘制

    /**
     * @param context
     * @param attrs
     */
    public PviTitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        if (!isInEditMode()) {
            if (GlobalVar.deviceType == 1) {
//                setUpdateMode(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);
            }
        
        mContext = context;
        mRes = context.getResources();

        OnClickListener l1 = new OnClickListener(){

            @Override
            public void onClick(View v) {
                //Logger.d(TAG,"onNavSwitch clicked!"+l);
                if(navIsOpen){
                    navSwitch.res = R.drawable.bg_btn_shownav;
                }else{
                    navSwitch.res = R.drawable.bg_btn_hidenav;
                }
                invalidate();
                
                if(l!=null){
                    l.onSwtich(navIsOpen);
                }
                navIsOpen = !navIsOpen;
            }};
        navSwitch = new PviUiItem("switch", R.drawable.bg_btn_shownav, 550, 0, 40, 32, null, false, true, l1);
        
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (!isInEditMode()) {
            //Logger.d(TAG, "onDraw");

            reDrawUiPath(canvas);
            reDrawNavSwitch(canvas);

            /*
             * final int c = mUiList.size(); for (int i = 0; i < c; i++) { final
             * PviUiItem item = mUiList.get(i); Logger.d(TAG,"redraw item");
             * item.draw(canvas, mRes); }
             */

            // super.onDraw(canvas);

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getX() > navSwitch.left) {
            navSwitch.performClick();
        } else if (event.getX() < 70) {
            final Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
            final Bundle sndBundle = new Bundle();
            sndBundle.putString("actID", "ACT10000");
            intent.putExtras(sndBundle);
            mContext.sendBroadcast(intent);
        } else if (event.getX() > 70 && event.getX() < 180) {
            if (pathInfo != null && pathInfo.size() > 1) {
                final HashMap<String, String> hm = pathInfo.get(1);
                if (hm != null) {
                    final Intent intent = new Intent(
                            MainpageActivity.START_ACTIVITY);
                    final Bundle sndBundle = new Bundle();
                    String actID = hm.get("actID");
                    String act = hm.get("act");
                    if ("ACT11000".equals(actID)||"com.pvi.ap.reader.activity.WirelessStoreActivity".equals(act)) {
                        actID = "ACT19000";
                    }
                    sndBundle.putString("actID", actID);
                    intent.putExtras(sndBundle);
                    mContext.sendBroadcast(intent);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void updateUiPath(ArrayList<HashMap<String, String>> newPathInfo) {
        this.pathInfo = newPathInfo;
        uipathNeedReDraw = true;
        invalidate();
        
/*20110725暂去掉如果重复，不重绘的优化逻辑（判断是否重复这里有问题）        Logger.d(TAG,"updateUiPath(... is called");
        if(newPathInfo!=null){
            Logger.e(TAG,newPathInfo.toString());
        }else{
            Logger.e(TAG,"newPathInfo is null");
            return;
        }
        if(this.pathInfo!=null){
            Logger.e(TAG,this.pathInfo.toString());
            }else{
                Logger.e(TAG,"this.pathinfo is null");
            }
        if(!newPathInfo.equals(this.pathInfo)){
            this.pathInfo = extracted(newPathInfo);
            uipathNeedReDraw = true;
            Logger.d(TAG,"uipathNeedReDraw = true;  need redraw");
            invalidate();
        }else{
            Logger.d(TAG,"no need to redraw");
        }*/
        
    }

    @SuppressWarnings("unchecked")
    private ArrayList<HashMap<String, String>> extracted(
            ArrayList<HashMap<String, String>> newPathInfo) {
        return (ArrayList<HashMap<String, String>>) newPathInfo.clone();
    }
    public void updateNavSwitch(Boolean toOpen) {

        if(!toOpen.equals(this.navIsOpen)){
            this.navIsOpen = toOpen;
            //this.switchNeedReDraw = true;
            invalidate();
        }
    }

    private void reDrawUiPath(Canvas mCanvas){
        
        if(true){       //总是绘制
            //Logger.d(TAG,"redraw text");
            uipathNeedReDraw =false;
        // 根据pathInfo重绘
        
        //刷白
        //mCanvas.drawRect(left, top, right, bottom, paint)
        
        float textSize = 19f;
        float marginLeft = 10f;
        
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
        
        if (pathInfo != null) {

            float curWidth = marginLeft;
            
            int depth = pathInfo.size();

            for (int i = depth; i > 0; i--) {
                final HashMap<String, String> hm = pathInfo.get(i - 1);

                
                if(true){
                    //最末层增加  的替换 [>>] 的处理
                    String title = "";
                    if (hm.get("mainTitle") != null
                            && !hm.get("mainTitle").equals("")) {
                        //Logger.d(TAG,"mainTitle1:"+hm.get("mainTitle"));
                        title = hm.get("mainTitle");
                    } else {
                        //Logger.d(TAG,"hm.get(actName):"+hm.get("actName"));
                        title = hm.get("actName");
                        if(title==null){
                            title="";
                        }
                    }                    
                   
                    
                    final String solidSign = "[>>]";
                    if(title.contains(solidSign)){

                            //Logger.e(TAG,"1 curWidth:"+curWidth);
                        
/*                            TextView pathNodeLeft = new TextView(mContext);
                            pathNodeLeft.setTextAppearance(mContext,
                                    R.style.normal_black_common);
                            pathNodeLeft.setLines(1);
                            // Logger.d(TAG,"-"+title.substring(0, title.indexOf(solidSign))+"-");
                            pathNodeLeft.setText((title.substring(0, title
                                    .indexOf(solidSign))).trim());
                            
                            //mUiPath.addView(pathNodeLeft, lpPathNode);
                            //Logger.e(TAG,"drawText pathNodeLeft.getText() "+pathNodeLeft.getText());                            
                            mCanvas.drawText(pathNodeLeft.getText().toString(), curWidth, 4+textSize, paint);
                            pathNodeLeft.measure(0,0);
                            curWidth = curWidth+pathNodeLeft.getMeasuredWidth();*/
                            
                            
                            curWidth = tryDrawText(curWidth,(title.substring(0, title
                                    .indexOf(solidSign))).trim(),mCanvas);
                            if(curWidth==0){
                                break;
                            }
                            
                            //Logger.e(TAG,"2 curWidth:"+curWidth);
                            
/*                            final Drawable d = mRes.getDrawable(R.drawable.slider);
                            final Bitmap bmp = ((BitmapDrawable)d).getBitmap();
                            mCanvas.drawBitmap(bmp, curWidth, 10, paint);
                            curWidth = curWidth+bmp.getWidth();*/
                            
                            curWidth = tryDrawSlider(curWidth,mCanvas);
                            if(curWidth==0){
                                break;
                            }
                            
                            //Logger.e(TAG,"3 curWidth:"+curWidth);
                            
/*                            TextView pathNodeRight = new TextView(mContext);
                            pathNodeRight.setTextAppearance(mContext,
                                    R.style.normal_black_common);
                            pathNodeRight.setLines(1);
                            // Logger.d(TAG,"-"+title.substring(title.indexOf(solidSign)+solidSign.length())+"-");
                            pathNodeRight.setText((title.substring(title
                                    .indexOf(solidSign)
                                    + solidSign.length())).trim());
                            //mUiPath.addView(pathNodeRight, lpPathNode);
                            //Logger.e(TAG,"drawText pathNodeRight() "+pathNodeRight.getText());
                            Logger.e(TAG,"curWidth:"+curWidth);
                            mCanvas.drawText(pathNodeRight.getText().toString(), curWidth, 4+textSize, paint);
                            pathNodeRight.measure(0,0);
                            curWidth = curWidth+pathNodeRight.getMeasuredWidth();*/
                            
                            curWidth = tryDrawText(curWidth,(title.substring(title
                                    .indexOf(solidSign)
                                    + solidSign.length())).trim(),mCanvas);
                            if(curWidth==0){
                                break;
                            }
                        
                    }else{
                        //Logger.d(TAG,"set just string title");
                        
                        //Logger.e(TAG,"4 curWidth:"+curWidth);
                        
/*                        TextView pathNode = new TextView(mContext);
                        pathNode.setTextAppearance(mContext,
                                R.style.normal_black_common);
                        pathNode.setLines(1);
                        pathNode.setText(title);
                        //mUiPath.addView(pathNode,lpPathNode);
                        mCanvas.drawText(pathNode.getText().toString(), curWidth,4+textSize, paint);
                        pathNode.measure(0,0);
                        curWidth = curWidth+pathNode.getMeasuredWidth();*/
                        
                        curWidth = tryDrawText(curWidth,title,mCanvas);
                        if(curWidth==0){
                            break;
                        }

                    }
                    if (i > 1) {
                        try {
                            //Logger.e(TAG,"5 curWidth:"+curWidth);
                            
/*                            final Drawable d = mRes.getDrawable(R.drawable.slider);
                            final Bitmap bmp = ((BitmapDrawable)d).getBitmap();
                            mCanvas.drawBitmap(bmp, curWidth, 10, paint);
                            curWidth = curWidth+bmp.getWidth();*/
                            
                            curWidth = tryDrawSlider(curWidth,mCanvas);
                            if(curWidth==0){
                                break;
                            }

                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    
                }
                
                
                
            }

        }
        }

    }
    
    private float tryDrawSlider(float curWidth,Canvas canvas){
        final Drawable d = mRes.getDrawable(R.drawable.slider);
        final Bitmap bmp = ((BitmapDrawable)d).getBitmap();
        float newWidth = curWidth+bmp.getWidth();
        if(newWidth<560){
            Paint paint = new Paint();
            canvas.drawBitmap(bmp, curWidth, 10, paint);            
            return newWidth;
        }else{
            return 0;
        }
    }
    private float tryDrawText(float curWidth,String text,Canvas canvas){

        TextView pathNode = new TextView(mContext);
        pathNode.setTextAppearance(mContext,
                R.style.normal_black_common);
        pathNode.setLines(1);
        pathNode.setText(text);
        pathNode.measure(0,0);
        float newWidth = curWidth+pathNode.getMeasuredWidth();
        
        
        float textSize = 19f;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);  
        
        if(newWidth<560){         
            canvas.drawText(text, curWidth, 4+textSize, paint);        
            return newWidth;
        }else{
            text = PviUiUtil.getClipedText(paint, text, (int)(560-curWidth));
            canvas.drawText(text, curWidth, 4+textSize, paint); 
            return 0;
        }
    }

    private void reDrawNavSwitch(Canvas canvas) {
        //Logger.d(TAG,"reDrawNavSwitch");
/*        if (switchNeedReDraw) {   
            Paint paint = new Paint();
            Drawable d = mRes.getDrawable(R.drawable.slider);
            BitmapDrawable bd = (BitmapDrawable)d;
            Bitmap bmp = bd.getBitmap();
            canvas.drawBitmap(bmp, 550, 10, paint);
            //navSwitch.draw(canvas, mRes);
            switchNeedReDraw = false;
        }*/
        
        navSwitch.draw(canvas, mRes);
    }
    
    
    /**
     * 显/隐导航把手
     *
     */
    public interface OnSwtichListener {
        void onSwtich(boolean isOpen);
    }
    private OnSwtichListener l;
    public void setOnSwtichListener(OnSwtichListener l) {
        this.l = l;
    }  
}
