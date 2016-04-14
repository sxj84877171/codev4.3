package com.pvi.ap.reader.activity.pviappframe;

import com.pvi.ap.reader.data.common.Logger;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View.OnClickListener;

/**
 * 封装一个ui item 类
 * @author rd040 马中庆
 *
 */
public class PviUiItem{
    private static final String TAG = "PviUiItem";
    public String id;  //名称   起个唯一的名字，方便找到我
    public int res;//  可以是 0|静态图片资源|状态列表
    
    //针对程序读取远程bitmap的情形
    public Bitmap pic;//    一幅bitmap
    public int bgNormal;    //normal时的背景
    public int bgFocus;    //forcus时的背景
    
    public int resDefault;  //指定一个默认素材
    
    public float left;            //起始位置
    public float top;              //起始位置
    public float width;            //宽度
    public float height;           //高度
    
    public String text;            //显示文本内容
    public float textSize = 22f;          //单位sp
    public boolean isBlod;          //是否加粗
    public int textAlign = 0;           //文本对齐方式 0左 1中  2 右 
    public int textType;            //预定义的文字大小   暂时未增加该逻辑
    public int textColor;           //字体颜色
    
    public boolean focusable = true;         //是否可以聚焦
    public boolean isFocuse = false;        //保存聚焦状态
    public boolean isVisible = false;          //是否显示
    public boolean clickable = true;            //是否可点击
    
    public OnClickListener l;
    public OnUiItemClickListener l2;
    
    public interface OnUiItemClickListener {
        void onUiItemClick(PviUiItem item);
    }
    
    private OnDrawListener onDrawListener;      //绘制前执行onDraw
    public interface OnDrawListener{
        void onDraw(PviUiItem item);
    }
    
    /**
     * 预定义字体
     */
    public static int SMALL = 1;
    public static int COMMON = 2;
    public static int BIG = 3;
    public static int BIGGER = 4;
    public static int BIGGEST = 5;
    
    public static float[] TEXT_SIZES = new float[]{
        16f,
        19f,
        22f,
        25f,
        31f
    };
    
    
    /**
     * 构造函数（这只是比较典型的一种构造，其它属性请直接使用.xx进行设置，均为public）
     * @param id      名称   起个唯一的名字，方便找到我
     * @param res     可以是 0|静态图片资源|状态列表
     * @param left    起始位置 x坐标
     * @param top             y坐标
     * @param width          区域宽度
     * @param height         区域高度
     * @param text      文本
     * @param isFocus        是否处于聚焦状态
     * @param l              时间监听器 设置一个OnClickListener
     */
    public PviUiItem(String id,int res,float left,float top,float width,float height,String text,boolean isFocus,boolean isVisible,OnClickListener l){
        this.id = id;
        this.res = res;
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        this.text = text;
        this.isFocuse = isFocus;
        this.isVisible = isVisible;
        
        this.l = l;
    }
    
    /**
     * 执行点击事件处理
     */
    public void performClick() {
        if (clickable) {
            if (this.l != null) {
                //Logger.d(TAG, "call this.l.onClick(null);");
                this.l.onClick(null);
            } else if (this.l2 != null) {
                //Logger.d(TAG, "call this.l2.OnUiItemClick(this)");
                this.l2.onUiItemClick(this);
            }
        }
    }
    
    public void setFocusable(boolean focusable){
        this.focusable = focusable;
    }
    
    /**
     * 将自己绘制到指定的画布上
     * 
     * @param cv
     * @param res
     */
    public void draw(Canvas cv, Resources res) {
        //Logger.d(TAG, "draw me on the Canvas,my id:"+this.id);
        if(this.onDrawListener!=null){
            this.onDrawListener.onDraw(this);
        }

        if (cv == null || res == null) {
            Logger.e(TAG, "null! cv is :" + cv + ",res is :" + res);
            return;
        }
        Paint paint = new Paint();
        if (this.isVisible) {
            //Logger.d(TAG, "I'm visible");
            
            // 绘图形
            if (this.pic != null) {// 如果已经设置了bitmap 直接绘出它
                cv.drawBitmap(this.pic, this.left, this.top, paint);
            } else if (this.res > 0) { // 如果设置的是Drawable
                Drawable d = res.getDrawable(this.res);
                if (d instanceof BitmapDrawable) {
                    Bitmap b = ((BitmapDrawable) d).getBitmap();
                    cv.drawBitmap(b, this.left, this.top, paint);
                } else if (d instanceof StateListDrawable) {
                    StateListDrawable dd = (StateListDrawable) d;
                    if (this.isFocuse) {// 绘制有焦点时的素材
                        dd.setState(PviView.FOCUSED_STATE_SET);
                    } else {
                        dd.setState(PviView.EMPTY_STATE_SET);
                    }
                    Drawable ddd = dd.getCurrent();
                    Bitmap b = ((BitmapDrawable) ddd).getBitmap();
                    cv.drawBitmap(b, this.left, this.top, paint);
                }
            } else if (this.resDefault > 0) {// 如果有指定默认图片
                Bitmap bmpPic = ((BitmapDrawable) res
                        .getDrawable(this.resDefault)).getBitmap();
                cv.drawBitmap(bmpPic, this.left, this.top, paint);
            }

            // 绘图形的边框：如果有的设置的话 逻辑，当前聚焦的行，才需要
            if (this.isVisible) {
                int picBg = this.bgNormal;
                if (this.isFocuse) {
                    picBg = this.bgFocus;
                }
                if (picBg > 0) {
                    cv.drawBitmap(((BitmapDrawable) res.getDrawable(picBg))
                            .getBitmap(), this.left - 2, this.top - 2, paint);
                }
            }

            // 绘制文本
            if (this.text != null && !"".equals(this.text)) {
                paint.setColor(Color.BLACK);
                if(this.textColor<0){
                    paint.setColor(this.textColor);
                }
                paint.setAntiAlias(true);
                if(this.textType>0){
                    //Logger.d(TAG,"set textType:"+this.textType);
                    this.textSize = TEXT_SIZES[this.textType-1];                    
                }else{
                    //Logger.d(TAG,"not set textType");
                }
                paint.setTextSize(this.textSize);
                paint.setFakeBoldText(this.isBlod);
                if(this.res>0){//如果有指定res，则把字写在中间
                    paint.setTextAlign(Align.CENTER);
                    cv.drawText(this.text, this.width/2, this.top + this.textSize/4+this.height/2,
                            paint);
                }else{
                    if(this.textAlign==0){
                    paint.setTextAlign(Align.LEFT);
                  //Logger.d(TAG, "draw this's text:" + this.text+", isBlod:"+paint.isFakeBoldText()+",textSize:"+paint.getTextSize());
                    cv.drawText(this.text, this.left, this.top + this.textSize,
                            paint);
                    }else if(this.textAlign==1){
                    paint.setTextAlign(Align.CENTER);
                  //Logger.d(TAG, "draw this's text:" + this.text+", isBlod:"+paint.isFakeBoldText()+",textSize:"+paint.getTextSize());
                    cv.drawText(this.text, this.width/2, this.top + this.textSize,
                            paint);
                    }else if(this.textAlign==2){
                    paint.setTextAlign(Align.RIGHT);
                  //Logger.d(TAG, "draw this's text:" + this.text+", isBlod:"+paint.isFakeBoldText()+",textSize:"+paint.getTextSize());
                    cv.drawText(this.text, this.left, this.top + this.textSize,
                            paint);
                    }
                }
                
            }

        } else {
            //Logger.d(TAG, "I'm INvisible");
            paint.setColor(Color.WHITE);
            cv.drawRect(left, top, left + width, top + height, paint);
        }

    }
    
    public int getTextDrawWidth(){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(this.textSize);
        paint.setFakeBoldText(this.isBlod);
        return PviUiUtil.getTextDrawWidth(paint, this.text);
    }

    public OnDrawListener getOnDrawListener() {
        return onDrawListener;
    }

    public void setOnDrawListener(OnDrawListener onDrawListener) {
        this.onDrawListener = onDrawListener;
    }
}