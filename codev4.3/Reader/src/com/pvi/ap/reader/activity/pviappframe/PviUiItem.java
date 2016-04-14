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
 * ��װһ��ui item ��
 * @author rd040 ������
 *
 */
public class PviUiItem{
    private static final String TAG = "PviUiItem";
    public String id;  //����   ���Ψһ�����֣������ҵ���
    public int res;//  ������ 0|��̬ͼƬ��Դ|״̬�б�
    
    //��Գ����ȡԶ��bitmap������
    public Bitmap pic;//    һ��bitmap
    public int bgNormal;    //normalʱ�ı���
    public int bgFocus;    //forcusʱ�ı���
    
    public int resDefault;  //ָ��һ��Ĭ���ز�
    
    public float left;            //��ʼλ��
    public float top;              //��ʼλ��
    public float width;            //���
    public float height;           //�߶�
    
    public String text;            //��ʾ�ı�����
    public float textSize = 22f;          //��λsp
    public boolean isBlod;          //�Ƿ�Ӵ�
    public int textAlign = 0;           //�ı����뷽ʽ 0�� 1��  2 �� 
    public int textType;            //Ԥ��������ִ�С   ��ʱδ���Ӹ��߼�
    public int textColor;           //������ɫ
    
    public boolean focusable = true;         //�Ƿ���Ծ۽�
    public boolean isFocuse = false;        //����۽�״̬
    public boolean isVisible = false;          //�Ƿ���ʾ
    public boolean clickable = true;            //�Ƿ�ɵ��
    
    public OnClickListener l;
    public OnUiItemClickListener l2;
    
    public interface OnUiItemClickListener {
        void onUiItemClick(PviUiItem item);
    }
    
    private OnDrawListener onDrawListener;      //����ǰִ��onDraw
    public interface OnDrawListener{
        void onDraw(PviUiItem item);
    }
    
    /**
     * Ԥ��������
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
     * ���캯������ֻ�ǱȽϵ��͵�һ�ֹ��죬����������ֱ��ʹ��.xx�������ã���Ϊpublic��
     * @param id      ����   ���Ψһ�����֣������ҵ���
     * @param res     ������ 0|��̬ͼƬ��Դ|״̬�б�
     * @param left    ��ʼλ�� x����
     * @param top             y����
     * @param width          ������
     * @param height         ����߶�
     * @param text      �ı�
     * @param isFocus        �Ƿ��ھ۽�״̬
     * @param l              ʱ������� ����һ��OnClickListener
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
     * ִ�е���¼�����
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
     * ���Լ����Ƶ�ָ���Ļ�����
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
            
            // ��ͼ��
            if (this.pic != null) {// ����Ѿ�������bitmap ֱ�ӻ����
                cv.drawBitmap(this.pic, this.left, this.top, paint);
            } else if (this.res > 0) { // ������õ���Drawable
                Drawable d = res.getDrawable(this.res);
                if (d instanceof BitmapDrawable) {
                    Bitmap b = ((BitmapDrawable) d).getBitmap();
                    cv.drawBitmap(b, this.left, this.top, paint);
                } else if (d instanceof StateListDrawable) {
                    StateListDrawable dd = (StateListDrawable) d;
                    if (this.isFocuse) {// �����н���ʱ���ز�
                        dd.setState(PviView.FOCUSED_STATE_SET);
                    } else {
                        dd.setState(PviView.EMPTY_STATE_SET);
                    }
                    Drawable ddd = dd.getCurrent();
                    Bitmap b = ((BitmapDrawable) ddd).getBitmap();
                    cv.drawBitmap(b, this.left, this.top, paint);
                }
            } else if (this.resDefault > 0) {// �����ָ��Ĭ��ͼƬ
                Bitmap bmpPic = ((BitmapDrawable) res
                        .getDrawable(this.resDefault)).getBitmap();
                cv.drawBitmap(bmpPic, this.left, this.top, paint);
            }

            // ��ͼ�εı߿�����е����õĻ� �߼�����ǰ�۽����У�����Ҫ
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

            // �����ı�
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
                if(this.res>0){//�����ָ��res�������д���м�
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