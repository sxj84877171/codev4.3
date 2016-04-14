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
 * 单View列表控件
 * 
 * 在xml中嵌入此控件，增加的自定义属性有：(注意在布局文件的根元素内增加 xmlns:pvi="http://schemas.android.com/apk/res/com.pvi.ap.reader")
 *  lineHeight="100"   一行的高度
 *  lineBgNormal="@drawable/..."       行底部线条  normal状态的素材
 *  lineBgHighlight    行底部线条  focus状态的素材
 * 
 * 注意：  已内置逻辑
 * 1、上下键移动行焦点；触摸行焦点
 * 2、左右键调用翻页
 * 3、触摸根据x、y坐标去触发相应位置PviUiItem的OnClickListner
 * 
 * 设置并显示/更新显示
 * 调用setData
 * @author rd040 马中庆
 * 
 */
public class PviDataList extends View {
    private static final String TAG = "PviDataList";

    public ArrayList<PviUiItem[]> mUiList; //数据存放
    public int mCurFoucsRow = -1;
    

    private Canvas mCanvas;
    private Context mContext;
    private Resources mRes;
    
    private int lastFocusRow = -1;// 保存焦点离开此视图时，最后的聚焦行

    private int maxRowNum = 7; // 最大行数
    private boolean isDrawEmptyRow = false; // 是否绘制出空行 暂未增加此控制逻辑
    
    // 支持的自定义属性
    public float lineHeight = 86f;
    private int lineBgNormal = R.drawable.list_line_normal_ui1;
    private int lineBgHighlight = R.drawable.list_line_focus_ui1;
    private float lineBgLeft = 90f; //底纹绘制起始位置

    public PviDataList(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {
            if (GlobalVar.deviceType == 1) {
//                setUpdateMode(View.EINK_WAVEFORM_MODE_GC16);
            }

            mRes = context.getResources();
            mContext = context;

            /*
             * 自定义属性
             * 
             * lineHeight 行高
             */
            TypedArray a =
                context.obtainStyledAttributes(attrs, R.styleable.PviDataList);

            lineHeight = a.getFloat(R.styleable.PviDataList_lineHeight,lineHeight);
            lineBgNormal = a.getResourceId(R.styleable.PviDataList_lineBgNormal,lineBgNormal);
            lineBgHighlight = a.getResourceId(R.styleable.PviDataList_lineBgHighlight,lineBgHighlight);
            lineBgLeft = a.getFloat(R.styleable.PviDataList_lineBgLeft,lineBgLeft);
            
            // 构造函数 给一些成员变量等 赋初始值
            invalidate();

        }
    }

    /**
     * 设置数据
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

        // 如果有数据，绘制
        if (mUiList != null) {           
            Logger.d(TAG,"列表控件 OnDraw()");
            /*
             * Bitmap totalBmp =
             * Bitmap.createBitmap(layoutWidth,layoutHeight,Config.ARGB_8888);
             * Canvas cv = new Canvas(totalBmp);
             */
            final int rowNum = mUiList.size();
            for (int i = 0; i < rowNum; i++) {

                // 绘制一行：依次绘制每个item到画布上
                PviUiItem[] items = mUiList.get(i);
                final int count = items.length;
                for (int j = 0; j < count; j++) {
                    drawItem(items[j], i);
                }
                // 绘制行的底纹
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
     * 高亮某一行
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
     * 更新左侧所有pic
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
     * 在第N行，绘制出一个uiItem
     * 
     * @param item
     * @param rowNum
     */
    private void drawItem(PviUiItem item, int rowIndex) {

        /*Logger.d(TAG, "draw item id:" + item.id + ", in row:" + rowIndex
                + ",draw pos left:" + item.left + ",top:"
                + (lineHeight * rowIndex + item.top));*/

        Paint paint = new Paint();
        // 绘图形
        if (item.pic != null) {// 如果已经设置了bitmap 直接绘出它
            if(item.pic.getWidth()>item.width||item.pic.getHeight()>item.height){
                mCanvas.drawBitmap(Bitmap.createScaledBitmap(item.pic, (int)item.width, (int)item.height, true), item.left, lineHeight * rowIndex
                    + item.top, paint);
            }else{
                mCanvas.drawBitmap(item.pic, item.left, lineHeight * rowIndex
                        + item.top, paint);
            }
        } else if (item.res > 0) { // 如果设置的是Drawable
            Drawable d = mRes.getDrawable(item.res);
            if (d instanceof BitmapDrawable) {
                Bitmap b = ((BitmapDrawable) d).getBitmap();
                mCanvas.drawBitmap(b, item.left, lineHeight * rowIndex
                        + item.top, paint);
            } else if (d instanceof StateListDrawable) {
                StateListDrawable dd = (StateListDrawable) d;
                if (item.isFocuse) {// 绘制有焦点时的素材
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
        } else if (item.resDefault > 0) {// 如果有指定默认图片
            Bitmap bmpPic = ((BitmapDrawable) mRes.getDrawable(item.resDefault))
                    .getBitmap();
            mCanvas.drawBitmap(bmpPic, item.left, lineHeight * rowIndex
                    + item.top, paint);
        }

        // 绘图形的边框：如果有的设置的话 逻辑，当前聚焦的行，才需要
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

        // 绘制文本
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
            if(item.res>0){         //如果设置了有图片，则文字绘制在图片的中央
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
     * 通过点击位置 取item
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
     * 单击（包括按OK键、触摸）某一行时，监听器 
     *
     */
    public interface OnRowClickListener {
        void OnRowClick(View v,int rowIndex);
    }

    OnRowClickListener onRowClickListener;

    /**
     * 设置 行事件监听  单击（包括按OK键、触摸）某一行时，监听器
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
     * 未实现  立即重绘
     */
    private void redraw(){
       
    }

    @Override
    public void invalidate() {
        //Logger.d(TAG,"invalidate()");
        super.invalidate();
    }
}
