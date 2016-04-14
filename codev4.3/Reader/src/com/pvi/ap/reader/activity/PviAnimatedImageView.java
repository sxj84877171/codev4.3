/**
 * 
 */
package com.pvi.ap.reader.activity;

import android.widget.ImageView;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**会动的imageview  抄自android源码
 * @author rd040 马中庆
 *
 */

public class PviAnimatedImageView extends ImageView {
    private static final String TAG = "PviAnimatedImageView";
    AnimationDrawable mAnim;
    boolean mAttached;

    public PviAnimatedImageView(Context context) {
        super(context);
    }

    public PviAnimatedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void updateAnim() {
        Drawable drawable = getDrawable();
        if (mAttached && mAnim != null) {
            mAnim.stop();
        }
        
        if (drawable instanceof AnimationDrawable) {
            mAnim = (AnimationDrawable)drawable;
            if (mAttached) {
                mAnim.start();
            }
        } else {
            mAnim = null;
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        updateAnim();
    }

    @Override
    public void setImageResource(int resid) {
        super.setImageResource(resid);
        updateAnim();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mAnim != null) {
            mAnim.start();
        }
        mAttached = true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAnim != null) {
            mAnim.stop();
        }
        mAttached = false;
    }
}
