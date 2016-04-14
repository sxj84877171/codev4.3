package com.pvi.ap.reader.activity.pviappframe;


import com.pvi.ap.reader.activity.PviAnimatedImageView;
import com.pvi.ap.reader.data.common.Logger;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 状态栏图标组  
 * @author 马中庆  rd040 修改自Android源码
 *
 */
class PviStatusBarIcon {
    // TODO: get this from a resource
    private static final int ICON_GAP = 8;
    private static final int ICON_WIDTH = 25;
    private static final int ICON_HEIGHT = 25;
    private static final String TAG = "StatusBarIcon";

    public View view;

    PviIconData mData;
    
    private TextView mTextView;
    private PviAnimatedImageView mImageView;
    private TextView mNumberView;

    public PviStatusBarIcon(Context context, PviIconData data) {
        if(data==null){
            throw new NullPointerException("IconData data is null");
        }
        
        mData = data.clone();

        switch (data.type) {
            case PviIconData.TEXT: {
                TextView t;
                t = new TextView(context);
                mTextView = t;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.FILL_PARENT);
                t.setTextSize(16);
                t.setTextColor(0xff000000);
                t.setTypeface(Typeface.DEFAULT_BOLD);
                t.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                t.setPadding(6, 0, 0, 0);
                t.setLayoutParams(layoutParams);
                t.setText(data.text);
                this.view = t;
                break;
            }

            case PviIconData.ICON: {


                // icon
                /*PviAnimatedImageView im = (PviAnimatedImageView)v.findViewById(R.id.image);
                im.setImageDrawable(getIcon(context, data));
                im.setImageLevel(data.iconLevel);
                mImageView = im;*/


                break;
            }
        }
    }

    public void update(Context context, PviIconData data) {
        if (mData.type != data.type) {
            Logger.e(TAG,"status bar entry type can't change");
            return;
        }
        switch (data.type) {
        case PviIconData.TEXT:
            if (!TextUtils.equals(mData.text, data.text)) {
                TextView tv = mTextView;
                tv.setText(data.text);
            }
            break;
        case PviIconData.ICON:
/*            if (((mData.iconPackage != null && data.iconPackage != null)
                        && !mData.iconPackage.equals(data.iconPackage))
                    || mData.iconId != data.iconId
                    || mData.iconLevel != data.iconLevel) {
                ImageView im = mImageView;
                im.setImageDrawable(getIcon(context, data));
                im.setImageLevel(data.iconLevel);
            }
            if (mData.number != data.number) {
                TextView nv = mNumberView;
                if (data.number > 0) {
                    nv.setText("" + data.number);
                } else {
                    nv.setText("");
                }
            }*/
            break;
        }
        mData.copyFrom(data);
    }

    public void update(int number) {
        if (mData.number != number) {
            TextView nv = mNumberView;
            if (number > 0) {
                nv.setText("" + number);
            } else {
                nv.setText("");
            }
        }
        mData.number = number;
    }


    /**
     * Returns the right icon to use for this item, respecting the iconId and
     * iconPackage (if set)
     * 
     * @param context Context to use to get resources if iconPackage is not set
     * @return Drawable for this item, or null if the package or item could not
     *         be found
     */
    static Drawable getIcon(Context context, PviIconData data) {

        Resources r = null;

        if (data.iconPackage != null) {
            try {
                r = context.getPackageManager().getResourcesForApplication(data.iconPackage);
            } catch (PackageManager.NameNotFoundException ex) {
                Logger.e(TAG, "Icon package not found: " + data.iconPackage, ex);
                return null;
            }
        } else {
            r = context.getResources();
        }

        if (data.iconId == 0) {
            Logger.w(TAG, "No icon ID for slot " + data.slot);
            return null;
        }
        
        try {
            if(data.iconLevel>0){
                Drawable d = r.getDrawable(data.iconId);
                d.setLevel(data.iconLevel);
                return d.getCurrent();
            }else{
                return r.getDrawable(data.iconId);
            }
        } catch (RuntimeException e) {
            Logger.w(TAG, "Icon not found in "
                  + (data.iconPackage != null ? data.iconId : "<system>")
                  + ": " + Integer.toHexString(data.iconId));
        }
        catch(OutOfMemoryError e)
        {
        	 Logger.d("StatusBar:", "Sorry, Out of Memory!");
        }

        return null;
    }

    int getNumber() {
        return mData.number;
    }
}

