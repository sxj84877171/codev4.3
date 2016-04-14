/**
 * 
 */
package com.pvi.ap.reader.activity.pviappframe;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * @author rd040
 *
 */
public class BoldButton extends Button {

    /**
     * @param context
     */
    public BoldButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
    }

    /**
     * @param context
     * @param attrs
     */
    public BoldButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public BoldButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
        TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
    }

}
