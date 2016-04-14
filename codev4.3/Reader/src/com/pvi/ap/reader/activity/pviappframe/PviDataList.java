/**
 * 
 */
package com.pvi.ap.reader.activity.pviappframe;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.data.common.Logger;
import java.util.ArrayList;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

/**
 * ��View�б�ؼ�
 * 
 * ��xml��Ƕ��˿ؼ������ӵ��Զ��������У�(ע���ڲ����ļ��ĸ�Ԫ�������� xmlns:pvi="http://schemas.android.com/apk/res/com.pvi.ap.reader")
 *  lineHeight="100"   һ�еĸ߶�
 *  lineBgNormal="@drawable/..."       �еײ�����  normal״̬���ز�
 *  lineBgHighlight    �еײ�����  focus״̬���ز�
 * 
 * ע�⣺  �������߼�
 * 1�����¼��ƶ��н��㣻�����н���
 * 2�����Ҽ����÷�ҳ
 * 3����������x��y����ȥ������Ӧλ��PviUiItem��OnClickListner
 * 
 * ���ò���ʾ/������ʾ
 * ����setData
 * @author rd040 ������
 * 
 */
public class PviDataList extends View {
    private static final String TAG = "PviDataList";

    public ArrayList<PviUiItem[]> mUiList; //���ݴ��
    public int mCurFoucsRow = -1;
    

    private Canvas mCanvas;
    private Context mContext;
    private Resources mRes;
    
    private int lastFocusRow = -1;// ���潹���뿪����ͼʱ�����ľ۽���

    private int maxRowNum = 7; // �������
    private boolean isDrawEmptyRow = false; // �Ƿ���Ƴ����� ��δ���Ӵ˿����߼�
    
    // ֧�ֵ��Զ�������
    public float lineHeight = 86f;
    private int lineBgNormal = R.drawable.list_line_normal_ui1;
    private int lineBgHighlight = R.drawable.list_line_focus_ui1;
    private float lineBgLeft = 90f; //���ƻ�����ʼλ��

    public PviDataList(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            if (GlobalVar.deviceType == 1) {
//                setUpdateMode(View.EINK_WAVEFORM_MODE_GC16);
            }

            mRes = context.getResources();
            mContext = context;

            /*
             * �Զ�������
             * 
             * lineHeight �и�
             */
            TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.PviDataList);

            lineHeight = a.getFloat(R.styleable.PviDataList_lineHeight,lineHeight);
            lineBgNormal = a.getResourceId(R.styleable.PviDataList_lineBgNormal,lineBgNormal);
            lineBgHighlight = a.getResourceId(R.styleable.PviDataList_lineBgHighlight,lineBgHighlight);
            lineBgLeft = a.getFloat(R.styleable.PviDataList_lineBgLeft,lineBgLeft);
            
            // ���캯�� ��һЩ��Ա������ ����ʼֵ
            invalidate();

        }
    }

    /**
     * ��������
     */
    public void setData(Bitmap[] pics, ArrayList<String[]> textArr) {

        invalidate();

    }

    public void setData(ArrayList<PviUiItem[]> list) {
        this.mUiList = list;
        //onMeasure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        mCanvas = canvas;

        // ��������ݣ�����
        if (mUiList != null) {           
            Logger.d(TAG,"�б�ؼ� OnDraw()");
            /*
             * Bitmap totalBmp =
             * Bitmap.createBitmap(layoutWidth,layoutHeight,Config.ARGB_8888);
             * Canvas cv = new Canvas(totalBmp);
             */
            final int rowNum = mUiList.size();
            for (int i = 0; i < rowNum; i++) {

                // ����һ�У����λ���ÿ��item��������
                PviUiItem[] items = mUiList.get(i);
                final int count = items.length;
                for (int j = 0; j < count; j++) {
                    drawItem(items[j], i);
                }
                // �����еĵ���
                int lineBg = lineBgNormal;
                if (mCurFoucsRow == i) {
                    lineBg = lineBgHighlight;
                }
                Paint paint = new Paint();
                mCanvas.drawBitmap(((BitmapDrawable) mRes.getDrawable(lineBg))
                        .getBitmap(), lineBgLeft, lineHeight * (i + 1) - 2,
                        paint);

            }

        }

        super.onDraw(canvas);
    }

    /**
     * ����ĳһ��
     * 
     * @param i
     */
    private void highLightRow(int i) {
        if (i != mCurFoucsRow) {
            mCurFoucsRow = i;
            invalidate();
        }
    }

    /**
     * �����������pic
     */
    private void updatePics() {

    }

     @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int clickIndex = getRowByXY(event.getX(), event.getY());

            if (clickIndex > -1 && clickIndex != mCurFoucsRow) {
                highLightRow(clickIndex);
            }

            if (mUiList != null && mCurFoucsRow >= 0
                    && mCurFoucsRow < mUiList.size()
                    && mUiList.get(mCurFoucsRow) != null) {
                //Logger.d(TAG,"row:"+mCurFoucsRow+" clicked!");
                if(this.onRowClickListener!=null){
                    this.onRowClickListener.OnRowClick(this, mCurFoucsRow);
                }
            }
             
            
            PviUiItem item = getItemByXY(event.getX(), event.getY());
            if(item!=null&&item.l!=null){
                item.performClick();
            }

        }
        return super.onTouchEvent(event);
    }

    private int getRowByXY(float x, float y) {

        return (int) (y / lineHeight);
    }

    

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {

        if (gainFocus) {
            if (lastFocusRow == -1) {
                highLightRow(0);
            } else {
                highLightRow(lastFocusRow);
            }

        } else {
            lastFocusRow = mCurFoucsRow;
            highLightRow(-1);
        }

        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    /**
     * �ڵ�N�У����Ƴ�һ��uiItem
     * 
     * @param item
     * @param rowNum
     */
    private void drawItem(PviUiItem item, int rowIndex) {

        /*Logger.d(TAG, "draw item id:" + item.id + ", in row:" + rowIndex
                + ",draw pos left:" + item.left + ",top:"
                + (lineHeight * rowIndex + item.top));*/

        Paint paint = new Paint();
        // ��ͼ��
        if (item.pic != null) {// ����Ѿ�������bitmap ֱ�ӻ����
            if(item.pic.getWidth()>item.width||item.pic.getHeight()>item.height){
                mCanvas.drawBitmap(Bitmap.createScaledBitmap(item.pic, (int)item.width, (int)item.height, true), item.left, lineHeight * rowIndex
                    + item.top, paint);
            }else{
                mCanvas.drawBitmap(item.pic, item.left, lineHeight * rowIndex
                        + item.top, paint);
            }
        } else if (item.res > 0) { // ������õ���Drawable
            Drawable d = mRes.getDrawable(item.res);
            if (d instanceof BitmapDrawable) {
                Bitmap b = ((BitmapDrawable) d).getBitmap();
                mCanvas.drawBitmap(b, item.left, lineHeight * rowIndex
                        + item.top, paint);
            } else if (d instanceof StateListDrawable) {
                StateListDrawable dd = (StateListDrawable) d;
                if (item.isFocuse) {// �����н���ʱ���ز�
                    dd.setState(FOCUSED_STATE_SET);
                } else {
                    dd.setState(EMPTY_STATE_SET);
                }
                Drawable ddd = dd.getCurrent();
                if(ddd instanceof BitmapDrawable){
                Bitmap b = ((BitmapDrawable) ddd).getBitmap();
                mCanvas.drawBitmap(b, item.left, lineHeight * rowIndex
                        + item.top, paint);
                }else if(ddd instanceof NinePatchDrawable){
                    final NinePatchDrawable npd = (NinePatchDrawable)ddd;
                    Rect rect = new Rect();
                    rect.left=(int)item.left;
                    rect.right=(int)(item.left+item.width);
                    rect.top=(int)(lineHeight * rowIndex
                    + item.top);
                    rect.bottom=(int)(lineHeight * rowIndex
                            + item.top+item.height);
                    //Logger.e(TAG,rect);
                    npd.setBounds(rect);
                    npd.draw(mCanvas);
                }
            }
        } else if (item.resDefault > 0) {// �����ָ��Ĭ��ͼƬ
            Bitmap bmpPic = ((BitmapDrawable) mRes.getDrawable(item.resDefault))
                    .getBitmap();
            mCanvas.drawBitmap(bmpPic, item.left, lineHeight * rowIndex
                    + item.top, paint);
        }

        // ��ͼ�εı߿�����е����õĻ� �߼�����ǰ�۽����У�����Ҫ
        if (item.isVisible) {
            int picBg = item.bgNormal;
            if (mCurFoucsRow == rowIndex) {
                picBg = item.bgFocus;
            }
            if (picBg > 0) {
                mCanvas.drawBitmap(((BitmapDrawable) mRes.getDrawable(picBg))
                        .getBitmap(), item.left - 2, lineHeight * rowIndex
                        + (item.top - 2), paint);
            }
        }

        // �����ı�
        if (item.text != null && !"".equals(item.text)) {
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            if(item.textType>0){
                //Logger.d(TAG,"set textType:"+this.textType);
                item.textSize = PviUiItem.TEXT_SIZES[item.textType-1];                    
            }else{
                //Logger.d(TAG,"not set textType");
            }
            paint.setTextSize(item.textSize);
            paint.setFakeBoldText(item.isBlod);
            //Logger.d(TAG, "draw item's text:" + item.text);
            final String text = PviUiUtil.getClipedText(paint, item.text, (int)item.width);
            if(item.res>0){         //�����������ͼƬ�������ֻ�����ͼƬ������
                paint.setTextAlign(Align.CENTER);
                mCanvas.drawText(text, item.left+item.width/2, lineHeight * rowIndex
                        + item.top+item.height/2+item.textSize/2, paint);
            }
            else{
                if(item.textAlign==0){
                    paint.setTextAlign(Align.LEFT);
                }else if(item.textAlign==1){
                    paint.setTextAlign(Align.CENTER);
                }else if(item.textAlign==2){
                    paint.setTextAlign(Align.RIGHT);
                }
                mCanvas.drawText(text, item.left, lineHeight * rowIndex
                    + item.top+item.textSize, paint);
            }
        }

    }
    /**
     * ͨ�����λ�� ȡitem
     * @return
     */
    private PviUiItem getItemByXY(float x, float y){
        final int rowIndex = getRowByXY(x,y);
        
        if(rowIndex>mUiList.size()-1){
            return null;
        }
        
            PviUiItem[] items = mUiList.get(rowIndex);
            
            if(items==null){
                return null;
            }
            
            final int count = items.length;
            for (int j = 0; j < count; j++) {
                PviUiItem item = items[j];
                if(item!=null 
                        && x>item.left&&x<(item.left+item.width)
                        &&y>this.lineHeight*rowIndex+item.top && y<(this.lineHeight*rowIndex+item.top+item.height)){
                    return item;
                }
            }

        return null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Logger.d(TAG,"onMeasure(int widthMeasureSpec, int heightMeasureSpec)");
        
        if (this.getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = 0;
            if (mUiList != null) {
                //Logger.d(TAG,"height is :"+height);
                height = (int) this.lineHeight * mUiList.size();
            }
            this.setMeasuredDimension(width, height);
            // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
    
    /**
     * ������������OK����������ĳһ��ʱ�������� 
     *
     */
    public interface OnRowClickListener {
        void OnRowClick(View v,int rowIndex);
    }

    OnRowClickListener onRowClickListener;

    /**
     * ���� ���¼�����  ������������OK����������ĳһ��ʱ��������
     * @param l
     */
    public void setOnRowClick(OnRowClickListener l) {
        this.onRowClickListener = l;
    }

    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mUiList == null) {            
            Logger.e(TAG, "mUiList is null");            
        } else {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                    if (mCurFoucsRow == 0)
                        return false;
                    highLightRow(mCurFoucsRow - 1);
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    if (mCurFoucsRow == mUiList.size() - 1)
                        return false;
                    highLightRow(mCurFoucsRow + 1);
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    final GlobalVar app = (GlobalVar) mContext
                            .getApplicationContext();
                    app.pbb.actionPrevPage();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    final GlobalVar app = (GlobalVar) mContext
                            .getApplicationContext();
                    app.pbb.actionNextPage();
                    return true;
                } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (mUiList != null && mCurFoucsRow >= 0
                            && mCurFoucsRow < mUiList.size()
                            && mUiList.get(mCurFoucsRow) != null) {
                        // Logger.d(TAG,"row:"+mCurFoucsRow+" key center down!");
                        if (this.onRowClickListener != null) {
                            this.onRowClickListener.OnRowClick(this,
                                    mCurFoucsRow);
                        }
                    }
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
    
    /**
     * δʵ��  �����ػ�
     */
    private void redraw(){
       
    }

    @Override
    public void invalidate() {
        //Logger.d(TAG,"invalidate()");
        super.invalidate();
    }
}
