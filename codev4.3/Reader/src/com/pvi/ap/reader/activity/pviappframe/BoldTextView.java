package com.pvi.ap.reader.activity.pviappframe;

import android.content.Context;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * ДжЬх
 * @author Fly
 *
 */
public class BoldTextView extends TextView {

	public BoldTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
	}

	public BoldTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
	}

	public BoldTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		TextPaint tp = getPaint();
        tp.setFakeBoldText(true);
	}
	

}
