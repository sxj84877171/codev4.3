/**
 * 
 */
package com.pvi.ap.reader.activity.pviappframe;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.pvi.ap.reader.data.common.Logger;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Xml;
import android.view.View;

/**改写的动画资源处理类
 * @author rd040
 *
 */
public class PviAnimationDrawable extends DrawableContainer implements Runnable, Animatable {

    private static final String TAG = "PviAnimationDrawable";
    private int mCurFrame = -1;  
    private Canvas mCanvas;
    private View mView;
    
    private float left;
    private float top;
    private float width;
    private float height;
    
private Handler mHandler = new H();
    
    private class H extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {            
                
            default:
                ;
            }

            super.handleMessage(msg);
        }

    }

    /**
     * 
     * @param v
     * @param resid        资源id
     * @param cv
     * @param left         起始x         
     * @param top          起始y
     * @param width        宽度
     * @param res
     */
    public PviAnimationDrawable(View v,int resid,Canvas cv,float left,float top,float width,float height,Resources res) {
        // TODO Auto-generated constructor stub
        this.mCanvas = cv;
        this.mView = v;
        
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        
        try {
            XmlResourceParser parser = res.getXml(resid);
            AttributeSet attrs = Xml.asAttributeSet(parser);
            inflate(res, parser, attrs);
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }    
    
    /**
     * 增加一个构造方法，从AnimationDrawable转过来
     */
    public PviAnimationDrawable(View v,Canvas cv,AnimationDrawable ad,float left,float top,float width,float height){
        this.mCanvas = cv;
        this.mView = v;
        
        this.left = left;
        this.top = top;
        this.width = width;
        this.height = height;
        
        int frameCount = ad.getNumberOfFrames();
        //Logger.d(TAG,"frameCount:"+frameCount);
        for(int i=0;i<frameCount;i++){
            //Logger.d(TAG,"i:"+i);
            Drawable d = ad.getFrame(i);
            addFrame(d, ad.getDuration(i));
            if (d != null) {
                d.setCallback(this);
            }
        }
        setFrame(0, true, false);
    }
    
    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        boolean changed = super.setVisible(visible, restart);
        if (visible) {
            if (changed || restart) {
                setFrame(0, true, true);
            }
        } else {
            unscheduleSelf(this);
        }
        return changed;
    }

    /**
     * <p>Starts the animation, looping if necessary. This method has no effect
     * if the animation is running.</p>
     *
     * @see #isRunning()
     * @see #stop()
     */
    public void start() {
        if (!isRunning()) {
            run();
        }
    }

    /**
     * <p>Stops the animation. This method has no effect if the animation is
     * not running.</p>
     *
     * @see #isRunning()
     * @see #start()
     */
    public void stop() {
        //Logger.d(TAG,"stop me!");
        if (isRunning()) {
            unscheduleSelf(this);
        }
        
        //刷白自己区域

        final Paint paint = new Paint(); 
        paint.setColor(Color.WHITE);
        mCanvas.drawRect( left, top,left+width, top+height, paint);

        mView.postInvalidate();
    }

    /**
     * <p>Indicates whether the animation is currently running or not.</p>
     *
     * @return true if the animation is running, false otherwise
     */
    public boolean isRunning() {
        //Logger.d(TAG,"check isRunning,mCurFrame:"+mCurFrame);
        if(mCurFrame > -1){
            //Logger.d(TAG,"is running!");
        }else{
            //Logger.d(TAG,"is not running");
        }
        return mCurFrame > -1;
    }

    /**
     * <p>This method exists for implementation purpose only and should not be
     * called directly. Invoke {@link #start()} instead.</p>
     *
     * @see #start()
     */
    public void run() {
        //Logger.d(TAG,"run(...");
        nextFrame(false);
    }

    @Override
    public void unscheduleSelf(Runnable what) {
        mCurFrame = -1;

        //super.unscheduleSelf(what);
        mHandler.removeCallbacks(this);
    
        //Logger.d(TAG,"unscheduleSelf");
    }

    @Override
    public void scheduleSelf(Runnable what, long when) {
        //Logger.d(TAG,"scheduleSelf(...");
        // TODO Auto-generated method stub
        //super.scheduleSelf(what, when);
        mHandler.postAtTime(this, when);
    }

    /**
     * @return The number of frames in the animation
     */
    public int getNumberOfFrames() {
        return getFrameCount();
    }
    
    /**
     * @return The Drawable at the specified frame index
     */
    public Drawable getFrame(int index) {
        return getFrames()[index];
    }
    
    /**
     * @return The duration in milliseconds of the frame at the 
     * specified index
     */
    public int getDuration(int i) {
        return mDurations[i];
    }
    
    
    /**
     * Add a frame to the animation
     * 
     * @param frame The frame to add
     * @param duration How long in milliseconds the frame should appear
     */
    public void addFrame(Drawable frame, int duration) {
        //Logger.d(TAG,"addFrame(...  frame:"+frame+",duration:"+duration);
        
        final int pos = mNumFrames;

        if (pos >= mDrawables.length) {
            growArray(pos, pos+10);
        }
        mNumFrames ++;
        mDrawables[pos] = frame;
        mDurations[pos] = duration;
    }
    
    /**
     * 动态增长数组
     * @param oldSize
     * @param newSize
     */
    public void growArray(int oldSize, int newSize) {
        Drawable[] newDrawables = new Drawable[newSize];
        System.arraycopy(mDrawables, 0, newDrawables, 0, oldSize);
        mDrawables = newDrawables;
        
        int[] newDurations = new int[newSize];
        System.arraycopy(mDurations, 0, newDurations, 0, oldSize);
        mDurations = newDurations;
    }
    
    private void nextFrame(boolean unschedule) {
        int next = mCurFrame+1;
        final int N = getFrameCount();

        
        if (next >= N) {
            next = 0;
        }
        //Logger.d(TAG,mOneShot);
        //setFrame(next, unschedule, !mOneShot || next < (N - 1));
        
        setFrame(next, unschedule, true || next < (N - 1)); //永远循环？
    }

    private void setFrame(int frame, boolean unschedule, boolean animate) {

        if (frame >= getFrameCount()) {
            return;
        }
        mCurFrame = frame;
        
        //原代码 selectDrawable(frame);
        
        //绘制本帧
        Bitmap bmp = ((BitmapDrawable)mDrawables[frame]).getBitmap();
        Paint paint = new Paint();
        mCanvas.drawBitmap(bmp, left, top, paint);
        
        mView.invalidate();
        
        if (unschedule) {
            unscheduleSelf(this);
        }
        if (animate) {
            scheduleSelf(this, SystemClock.uptimeMillis() + mDurations[frame]);
        }

    }

    
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        
        //Logger.d(TAG,"load ani from xml ");
        
/*        TypedArray a = r.obtainAttributes(attrs,
                R.styleable.PviAnimationDrawable);

        mOneShot = a.getBoolean(
                R.styleable.PviAnimationDrawable_oneshot, false);
        
        a.recycle();*/
        
        int type;

        final int innerDepth = parser.getDepth()+2;
        int depth;
        while ((type=parser.next()) != XmlPullParser.END_DOCUMENT &&
                ((depth = parser.getDepth()) >= innerDepth || type != XmlPullParser.END_TAG)) {
            
            //Logger.d(TAG,"type:"+type+",depth:"+depth+",innerDepth:"+innerDepth);
            if (type != XmlPullParser.START_TAG) {
                //Logger.d(TAG,"ignore 1");
                continue;
            }

            if (depth > innerDepth || !parser.getName().equals("item")) {
                //Logger.d(TAG,"ignore 2");
                continue;
            }

            final String nsAndroid = "http://schemas.android.com/apk/res/android";

            
            int duration = Integer
            .parseInt(parser.getAttributeValue(
                    nsAndroid, "duration")     );
            
            //Logger.d(TAG,"duration:"+duration);
            
            if (duration < 0) {
                throw new XmlPullParserException(
                        parser.getPositionDescription()
                        + ": <item> tag requires a 'duration' attribute");
            }
            int drawableRes = Integer
            .parseInt(parser.getAttributeValue(
                    nsAndroid, "drawable")
                    .substring(1));
            
            //Logger.d(TAG,"drawableRes:"+drawableRes);
            

            
            Drawable dr;
            if (drawableRes != 0) {
                dr = r.getDrawable(drawableRes);
            } else {
                while ((type=parser.next()) == XmlPullParser.TEXT) {
                    // Empty
                }
                if (type != XmlPullParser.START_TAG) {
                    throw new XmlPullParserException(parser.getPositionDescription() +
                            ": <item> tag requires a 'drawable' attribute or child tag" +
                            " defining a drawable");
                }
                dr = Drawable.createFromXmlInner(r, parser, attrs);
            }
            //Logger.d(TAG,"addFrame(...");
            
            addFrame(dr, duration);
            if (dr != null) {
                dr.setCallback(this);
            }
        }

        setFrame(0, true, false);
    }
    
    private int[] mDurations = new int[10];
    Drawable[]  mDrawables = new Drawable[10];
    int         mNumFrames = 0;

    
    public final int getFrameCount() {
        return mNumFrames;
    }

    public final Drawable[] getFrames() {
        return mDrawables;
    }
    
 
    

}

