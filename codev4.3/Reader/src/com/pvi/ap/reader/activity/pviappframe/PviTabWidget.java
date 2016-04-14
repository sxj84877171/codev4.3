package com.pvi.ap.reader.activity.pviappframe;

import java.util.ArrayList;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.pviappframe.PviTabHost.TabSpec;
import com.pvi.ap.reader.data.common.Logger;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.RelativeLayout;

/**
 *
 * Displays a list of tab labels representing each page in the parent's tab
 * collection. The container object for this widget is
 * {@link android.widget.TabHost TabHost}. When the user selects a tab, this
 * object sends a message to the parent container, TabHost, to tell it to switch
 * the displayed page. You typically won't use many methods directly on this
 * object. The container TabHost is used to add labels, add the callback
 * handler, and manage callbacks. You might call this object to iterate the list
 * of tabs, or to tweak the layout of the tab list, but most methods should be
 * called on the containing TabHost object.
 *
 * <p>See the <a href="{@docRoot}resources/tutorials/views/hello-tabwidget.html">Tab Layout
 * tutorial</a>.</p>
 * 
 * @attr ref android.R.styleable#TabWidget_divider
 * @attr ref android.R.styleable#TabWidget_tabStripEnabled
 * @attr ref android.R.styleable#TabWidget_tabStripLeft
 * @attr ref android.R.styleable#TabWidget_tabStripRight
 */
public class PviTabWidget extends View implements OnFocusChangeListener {
    private static final String TAG = "PviTabWidget";

    private OnTabSelectionChanged mSelectionChangedListener;

    private int mSelectedTab = 0;

	private Context mContext;
	
	private ArrayList<PviTab> mTabs = new ArrayList<PviTab>(); //存放tab

    public PviTabWidget(Context context) {    	
        this(context, null);
        this.mContext = context;
    }

    public PviTabWidget(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.tabWidgetStyle);
        this.mContext = context;
        if(!isInEditMode()){
//            if(GlobalVar.deviceType==1){
//                setUpdateMode(View.EINK_WAVEFORM_MODE_GC16);
//            }
        }
    }

    public PviTabWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        
        this.mContext = context;

    }

    /**
     * Returns the tab indicator view at the given index.
     *
     * @param index the zero-based index of the tab indicator view to return
     * @return the tab indicator view at the given index
     */
/*    public View getChildTabViewAt(int index) {
        // If we are using dividers, then instead of tab views at 0, 1, 2, ...
        // we have tab views at 0, 2, 4, ...
        if (mDividerDrawable != null) {
            index *= 2;
        }
        return getChildAt(index);
    }*/

    /**
     * Returns the number of tab indicator views.
     * @return the number of tab indicator views.
     */
    public int getTabCount() {
        return mTabs.size();
    }


    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        // Do nothing if there are no tabs.
        if (getTabCount() == 0) return;


    }

    /**
     * Sets the current tab.
     * This method is used to bring a tab to the front of the Widget,
     * and is used to post to the rest of the UI that a different tab
     * has been brought to the foreground.
     *
     * Note, this is separate from the traditional "focus" that is
     * employed from the view logic.
     *
     * For instance, if we have a list in a tabbed view, a user may be
     * navigating up and down the list, moving the UI focus (orange
     * highlighting) through the list items.  The cursor movement does
     * not effect the "selected" tab though, because what is being
     * scrolled through is all on the same tab.  The selected tab only
     * changes when we navigate between tabs (moving from the list view
     * to the next tabbed view, in this example).
     *
     * To move both the focus AND the selected tab at once, please use
     * {@link #setCurrentTab}. Normally, the view logic takes care of
     * adjusting the focus, so unless you're circumventing the UI,
     * you'll probably just focus your interest here.
     *
     *  @param index The tab that you want to indicate as the selected
     *  tab (tab brought to the front of the widget)
     *
     *  @see #focusCurrentTab
     */
    public void setCurrentTab(int index) {
    	//Logger.d(TAG,"setCurrentTab");
        if (index < 0 || index >= getTabCount()) {
            return;
        }
        if(index == mSelectedTab){
        	//Logger.d(TAG,"currentTab "+index+" has selected");
        	return;
        }

        //getChildTabViewAt(mSelectedTab).setSelected(false);
        mSelectedTab = index;
        //getChildTabViewAt(mSelectedTab).setSelected(true);

    }

    /**
     * Sets the current tab and focuses the UI on it.
     * This method makes sure that the focused tab matches the selected
     * tab, normally at {@link #setCurrentTab}.  Normally this would not
     * be an issue if we go through the UI, since the UI is responsible
     * for calling TabWidget.onFocusChanged(), but in the case where we
     * are selecting the tab programmatically, we'll need to make sure
     * focus keeps up.
     *
     *  @param index The tab that you want focused (highlighted in orange)
     *  and selected (tab brought to the front of the widget)
     *
     *  @see #setCurrentTab
     */
    public void focusCurrentTab(int index) {
        //Logger.d(TAG,"focusCurrentTab new index:"+index);
        final int oldTab = mSelectedTab;

        // set the tab
        setCurrentTab(index);

/*        getChildTabViewAt(index).requestFocus();
        // change the focus if applicable.
        if (oldTab != index) {
            //Logger.i(TAG,index+" focus changed");
            getChildTabViewAt(index).requestFocus();
            
        }*/
        
        this.requestFocus();
        mSelectionChangedListener.onTabSelectionChanged(index, false);
    }

    
    public void addTab(TabSpec tabSpec){
        PviTab tab = new PviTab(tabSpec.getTag());
        this.mTabs.add(tab);
        tab.setOnClickListener(new TabClickListener(getTabCount() - 1));
        tab.setOnFocusChangeListener(this);
    }

    /**
     * Provides a way for {@link TabHost} to be notified that the user clicked on a tab indicator.
     */
    void setTabSelectionListener(OnTabSelectionChanged listener) {
        mSelectionChangedListener = listener;
    }

/*    public void onFocusChange(View v, boolean hasFocus) {

        
        //for debug
        try {
            RelativeLayout rl = (RelativeLayout)v;
            TextView tv = (TextView)rl.getChildAt(0);
            //Logger.d(TAG,"tv:"+tv.getText());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        //Logger.d(TAG,this);
        //Logger.d(TAG,v.toString()+":   onFocusChange  "+(v==this)+(hasFocus)+(getTabCount()>0));
        
        
    	
        if (v == this && hasFocus && getTabCount() > 0) {
            //Logger.d(TAG,"getChildTabViewAt(mSelectedTab).requestFocus();");
            getChildTabViewAt(mSelectedTab).requestFocus();
            return;
        }

        if (hasFocus) {
            //Logger.d(TAG,v.toString()+" focused !");
            
            int i = 0;
            int numTabs = getTabCount();
            while (i < numTabs) {
                //Logger.d(TAG,"getChildTabViewAt(i) i:"+i+"   "+getChildTabViewAt(i));
                if (getChildTabViewAt(i) == v) {
                    setCurrentTab(i);
                    //Logger.d(TAG,"mSelectionChangedListener.onTabSelectionChanged(i, false); i:"+i);
                    mSelectionChangedListener.onTabSelectionChanged(i, false);
                    break;
                }
                i++;
            }
        }
    }*/

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
        //Logger.d(TAG,"onFocusChanged");
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    // registered with each tab indicator so we can notify tab host
    private class TabClickListener implements OnClickListener {

        private final int mTabIndex;

        private TabClickListener(int tabIndex) {
            mTabIndex = tabIndex;
        }

        public void onClick(View v) {
            //Logger.d(TAG,v.toString()+" onClick(...");
            mSelectionChangedListener.onTabSelectionChanged(mTabIndex, true);
        }
    }

    /**
     * Let {@link TabHost} know that the user clicked on a tab indicator.
     */
    static interface OnTabSelectionChanged {
        /**
         * Informs the TabHost which tab was selected. It also indicates
         * if the tab was clicked/pressed or just focused into.
         *
         * @param tabIndex index of the tab that was selected
         * @param clicked whether the selection changed due to a touch/click
         * or due to focus entering the tab through navigation. Pass true
         * if it was due to a press/click and false otherwise.
         */
        void onTabSelectionChanged(int tabIndex, boolean clicked);
    }
    
    class PviTab implements OnClickListener,OnFocusChangeListener{
        public PviTab(String tag) {
            // TODO Auto-generated constructor stub
            this.text =tag;
        }
        
        public void setOnFocusChangeListener(PviTabWidget pviTabWidget) {
            // TODO Auto-generated method stub
            //Logger.d(TAG,"setOnFocusChangeListener");
        }
        public void setOnClickListener(TabClickListener tabClickListener) {
            this.l = tabClickListener;
        }
        
        String text;    //tab文本
        float textSize = 22f; //文字大小
        boolean isFocused = false;//是否焦点
        private OnClickListener l;

        @Override
        public void onClick(View v) {
            if(this.l!=null){
                this.l.onClick(v);
            }
        }
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            //Logger.d(TAG,"onFocusChange   hasFocus:"+hasFocus);
            this.isFocused = hasFocus;

        }
        


    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Logger.d(TAG,"onKeyDown keycode:"+keyCode);
        //左、右键处理：按下时，移动焦点

            if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
                focusCurrentTab(mSelectedTab-1);
                return true;
            }else if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                focusCurrentTab(mSelectedTab+1);
                return true;
            }
        
        
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!isInEditMode()){
        //Logger.d(TAG,"onDraw");
        //绘制文本上来
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);        
        paint.setAntiAlias(true);
        paint.setTextAlign(Align.CENTER);
        final int count = mTabs.size();
        for (int i = 0; i < count; i++) {
            PviTab tab = mTabs.get(i);
            final int oneTabWidth = this.getWidth()/count;
            paint.setTextSize(tab.textSize);
            canvas.drawText(tab.text, oneTabWidth*i+oneTabWidth/2, (this.getHeight()+tab.textSize)/2-4, paint);
        }
        }
        super.onDraw(canvas);
    }

    public int getChildCount() {        
        return mTabs.size();
    }

    public RelativeLayout getChildAt(int i) {
        // TODO Auto-generated method stub
        return new RelativeLayout(mContext);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        // TODO Auto-generated method stub
        //Logger.d(TAG,"onFocusChange hasFocus:"+hasFocus);
    }

    private int getTabIndexByX(float x){
        final int oneTabWidth = this.getWidth()/this.getTabCount();
        return ((int)x)/oneTabWidth;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        //Logger.d(TAG,"onTouchEvent(");
        focusCurrentTab(getTabIndexByX(event.getX()));
        return super.onTouchEvent(event);
    }

}
