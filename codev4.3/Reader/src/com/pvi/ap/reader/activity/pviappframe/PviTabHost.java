package com.pvi.ap.reader.activity.pviappframe;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.List;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.data.common.Logger;

/**
 * Container for a tabbed window view. This object holds two children: a set of tab labels that the
 * user clicks to select a specific tab, and a FrameLayout object that displays the contents of that
 * page. The individual elements are typically controlled using this container object, rather than
 * setting values on the child elements themselves.
 *
 * <p>See the <a href="{@docRoot}resources/tutorials/views/hello-tabwidget.html">Tab Layout
 * tutorial</a>.</p>
 */
public class PviTabHost extends FrameLayout implements ViewTreeObserver.OnTouchModeChangeListener {

    private static final String TAG = "PviTabHost";
    private PviTabWidget mTabWidget;
    private FrameLayout mTabContent;
    private List<TabSpec> mTabSpecs = new ArrayList<TabSpec>(2);
    
    //private boolean switchingChildView = false;//接受到showme消息时，切换child view过程中，不处理其它切换动作

    
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected int mCurrentTab = -1;
    private View mCurrentView = null;
    
    private View[] childViews = new View[6];
    
    /**
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected LocalActivityManager mLocalActivityManager = null;
    private OnTabChangeListener mOnTabChangeListener;
    private OnKeyListener mTabKeyListener;
    
    protected int tabIndexWaitToShow = -1;
    public String actTabNameWaitToShow;

    public PviTabHost(Context context) {
        super(context);
        initTabHost();

        mContext = context;
    }

    public PviTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTabHost();
        
        mContext = context;
    }

    private void initTabHost() {
        setFocusableInTouchMode(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        mCurrentTab = -1;
        mCurrentView = null;
    }

    /**
     * Get a new {@link TabSpec} associated with this tab host.
     * @param tag required tag of tab.
     */
    public TabSpec newTabSpec(String tag) {
        return new TabSpec(tag);
    }



    /**
      * <p>Call setup() before adding tabs if loading TabHost using findViewById().
      * <i><b>However</i></b>: You do not need to call setup() after getTabHost()
      * in {@link android.app.TabActivity TabActivity}.
      * Example:</p>
<pre>mTabHost = (TabHost)findViewById(R.id.tabhost);
mTabHost.setup();
mTabHost.addTab(TAB_TAG_1, "Hello, world!", "Tab 1");
      */
    public void setup() {
        //Logger.d(TAG,"setup");
        mTabWidget = (PviTabWidget) findViewById(android.R.id.tabs);
        if (mTabWidget == null) {
            throw new RuntimeException(
                    "Your TabHost must have a TabWidget whose id attribute is 'android.R.id.tabs'");
        }
        
      //设置tab默认背景
        final GlobalVar appState = ((GlobalVar) getContext().getApplicationContext());
        mTabWidget.setBackgroundResource(R.drawable.tab_bgimg_0_ui1);          

        //结束 设置tab默认背景
        
        //设置eink屏刷模式  这里设置没有作用
        if(appState.deviceType==1){
            //mTabWidget.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL);
            /*2011-6-25注释调
            mTabWidget.setUpdateMode( View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);
            ((View)mTabWidget.getParent()).setUpdateMode(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);
             */
            
            //2011-6-25 增加
            //((View)mTabWidget.getParent()).setUpdateMode(View.EINK_WAVEFORM_MODE_GC16);
        }

        // KeyListener to attach to all tabs. Detects non-navigation keys
        // and relays them to the tab content.
        mTabKeyListener = new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                    case KeyEvent.KEYCODE_ENTER:
                        return false;

                }
                
                mTabContent.requestFocus(View.FOCUS_FORWARD);
                
                return mTabContent.dispatchKeyEvent(event);
            }

        };

        mTabWidget.setTabSelectionListener(new PviTabWidget.OnTabSelectionChanged() {
            public void onTabSelectionChanged(int tabIndex, boolean clicked) {
                //Logger.e(TAG,"onTabSelectionChanged(int tabIndex, boolean clicked) tabIndex:"+tabIndex+",clicked:"+clicked);
                setCurrentTab(tabIndex);
                if (clicked) {
                    mTabContent.requestFocus(View.FOCUS_FORWARD);
                }
            }
        });

        mTabContent = (FrameLayout) findViewById(android.R.id.tabcontent);
        if (mTabContent == null) {
            throw new RuntimeException(
                    "Your TabHost must have a FrameLayout whose id attribute is "
                            + "'android.R.id.tabcontent'");
        }
        
        if(appState.deviceType==1){
           
            //2011-6-25注释掉mTabContent.setUpdateMode( View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);
            //2011-6-25增加
            //mTabContent.setUpdateMode(View.EINK_WAVEFORM_MODE_GC16);
        }
    }

    /**
     * If you are using {@link TabSpec#setContent(android.content.Intent)}, this
     * must be called since the activityGroup is needed to launch the local activity.
     *
     * This is done for you if you extend {@link android.app.TabActivity}.
     * @param activityGroup Used to launch activities for tab content.
     */
    public void setup(LocalActivityManager activityGroup) {
        setup();
        mLocalActivityManager = activityGroup;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.addOnTouchModeChangeListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        final ViewTreeObserver treeObserver = getViewTreeObserver();
        if (treeObserver != null) {
            treeObserver.removeOnTouchModeChangeListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void onTouchModeChanged(boolean isInTouchMode) {
        if (!isInTouchMode) {
            // leaving touch mode.. if nothing has focus, let's give it to
            // the indicator of the current tab
            if (mCurrentView != null && (!mCurrentView.hasFocus() || mCurrentView.isFocused())) {
                mTabWidget.focusCurrentTab(mCurrentTab);
            }
        }
    }

    /**
     * Add a tab.
     * @param tabSpec Specifies how to create the indicator and content.
     */
    public void addTab(TabSpec tabSpec) {

/*        if (tabSpec.mIndicatorStrategy == null) {
            throw new IllegalArgumentException("you must specify a way to create the tab indicator.");
        }*/

        if (tabSpec.mContentStrategy == null) {
            throw new IllegalArgumentException("you must specify a way to create the tab content");
        }
/*        View tabIndicator = tabSpec.mIndicatorStrategy.createIndicatorView();
        tabIndicator.setOnKeyListener(mTabKeyListener);*/
        mTabWidget.setOnKeyListener(mTabKeyListener);

        // If this is a custom view, then do not draw the bottom strips for
        // the tab indicators.
/*        if (tabSpec.mIndicatorStrategy instanceof ViewIndicatorStrategy) {
            mTabWidget.setStripEnabled(false);
        }*/
        //mTabWidget.addView(tabIndicator);
        mTabWidget.addTab(tabSpec);
        mTabSpecs.add(tabSpec);

        /*
        if (mCurrentTab == -1) {
            setCurrentTab(0);
        }*/
    }


    /**
     * Removes all tabs from the tab widget associated with this tab host.
     */
    public void clearAllTabs() {
        initTabHost();
        mTabContent.removeAllViews();
        mTabSpecs.clear();
        requestLayout();
        invalidate();
    }

    public PviTabWidget getTabWidget() {
        return mTabWidget;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public String getCurrentTabTag() {
        if (mCurrentTab >= 0 && mCurrentTab < mTabSpecs.size()) {
            return mTabSpecs.get(mCurrentTab).getTag();
        }
        return null;
    }



    public View getCurrentView() {
        return mCurrentView;
    }

    public void setCurrentTabByTag(String tag) {
        //Logger.d(TAG,"setCurrentTabByTag(String tag)  tag:"+tag);
        int i;
        for (i = 0; i < mTabSpecs.size(); i++) {
            if (mTabSpecs.get(i).getTag().equals(tag)) {
                setCurrentTab(i);
                break;
            }
        }
    }

    /**
     * Get the FrameLayout which holds tab content
     */
    public FrameLayout getTabContentView() {
        return mTabContent;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        final boolean handled = super.dispatchKeyEvent(event);

        // unhandled key ups change focus to tab indicator for embedded activities
        // when there is nothing that will take focus from default focus searching
        if (!handled
                && (event.getAction() == KeyEvent.ACTION_DOWN)
                && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP)
                && (mCurrentView != null)
                && (mCurrentView.hasFocus())
                && (mCurrentView.findFocus().focusSearch(View.FOCUS_UP) == null)) {

            mTabWidget.focusCurrentTab(mCurrentTab);

            playSoundEffect(SoundEffectConstants.NAVIGATION_UP);
            return true;
        }
        return handled;
    }

    @Override
    public void dispatchWindowFocusChanged(boolean hasFocus) {
        if (mCurrentView != null){
            mCurrentView.dispatchWindowFocusChanged(hasFocus);
        }
    }
    
    
    /**
     * 2011-7-13增加逻辑：对于不是自己内嵌的界面发来的广播，应该不予处理
     * 可以在监听器的注册时机上加以控制
     */
    
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {   
		public void onReceive(Context context, Intent intent) {
        	String action = "";
        	Bundle bd = null;
        	String actTabName = "";
            if (intent != null) {
            	action = intent.getAction();

                bd = intent.getExtras();
            }
            if (bd != null) {
            	actTabName = bd.getString("actTabName");            	
            } 
            
            //add by kizan 0323 ...null pointerException occur for non-tab activities
            if(actTabName == null){
            	return;
            }
            
            Logger.i(TAG,"recv: actTabName:"+actTabName+",action:"+action+",actTabNameWaitToShow:"+actTabNameWaitToShow);
            
            if (action.equals(MainpageActivity.SHOW_ME)) {

                //先更新下
                updateBottomBar();
                
                if (actTabName != null) {
                    if (actTabName.equals(actTabNameWaitToShow)) {
                        //Logger.i("PviTabHost", "setCurrentTabDo");
                        int index = -1;
                        int i = 0;
                        for (TabSpec ts : mTabSpecs) {
                            if (ts.getTag().equals(actTabName)) {
                                index = i;
                            }
                            i++;
                        }

                        // add by kizan for a bug back to the tab activity
                        // if the tab was the current showed
                        // need no action of close!
                        // 2011 03.3
                        //Logger.e(TAG,"the tab index :"+index+" will be shown, mCurrentTab is "+mCurrentTab);
                        if (mCurrentTab != index) {
                            //Logger.e(TAG,"to DO change view!");
                            // 这时全刷一下整屏！
                            final GlobalVar app = (GlobalVar) mTabContent
                                    .getContext().getApplicationContext();
                            final PviTabActivity tab = (PviTabActivity) context;
                            if (app != null && app.deviceType == 1
                                    && tab != null && tab.countF > 0) {

                                // 增加一个逻辑 每次attachWindow后的、第一次刷新 不执行刷全屏，因为框架会刷！
                                // （每次dettachwindow时，将这个刷新次数置0，然后只需要以该计数值为判断依据）

                                 Logger.e(TAG,"in TAB switching, invalidate window:GC16 FULL");
                                 //getRootView().invalidate(View.EINK_WAIT_MODE_WAIT
//                                 mCurrentView.invalidate(View.EINK_WAIT_MODE_WAIT
//                                 | View.EINK_WAVEFORM_MODE_GC16 |
//                                 View.EINK_UPDATE_MODE_FULL);
                            } else {
                                // Logger.e(TAG,"in TAB or not in FSL  ,not invalidate window");
                            }
                            if (tab != null) {
                                tab.countF++;
                            }
                            setCurrentTabDo(index);
                        }else{
                            //Logger.e(TAG,"the view is shown! noneed to changeview");
                        }

                        // 修改TabActivity的intent的参数
                        final PviTabActivity pta = (PviTabActivity) getContext();
                        final Intent intent2 = pta.getIntent();
                        final Bundle bd2 = intent.getExtras();
                        bd2.putString("actTabName", actTabName);
                        intent2.putExtras(bd2);
                        pta.setIntent(intent2);
                        
                        
                        //2011-7-15 移走，到setcurrenttabD里面

                    }
                }
            }
            
            //setCurrentTabDo();
        }};
        
/*        public void setCurrentTabO(int index) { 
        	 
        	
            if (index < 0 || index >= mTabSpecs.size()) {
                return;
            }

            if (index == mCurrentTab) {
                return;
            }      

            // notify old tab content
            if (mCurrentTab != -1) {
                mTabSpecs.get(mCurrentTab).mContentStrategy.tabClosed();
            }

            mCurrentTab = index;
            final PviTabHost.TabSpec spec = mTabSpecs.get(index);

            // Call the tab widget's focusCurrentTab(), instead of just
            // selecting the tab.
            mTabWidget.focusCurrentTab(mCurrentTab);

            // tab content
            mCurrentView = spec.mContentStrategy.getContentView();
            
            if (mCurrentView.getParent() == null) {
                mTabContent
                        .addView(
                                mCurrentView,
                                new ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT));
            }

            if (!mTabWidget.hasFocus()) {
                // if the tab widget didn't take focus (likely because we're in touch mode)
                // give the current tab content view a shot
                
              //触摸也不让让contentview获取焦点  注释调这行
                //mCurrentView.requestFocus();
            }

            //mTabContent.requestFocus(View.FOCUS_FORWARD);
            invokeOnTabChangeListener();
        }     */ 

    public void setCurrentTab(int index) { 
    	
    	//执行被嵌入的activity 但是不addview
    	
        Logger.d(TAG,"setCurrentTab(int index) index:"+index+",mCurrentTab:"+mCurrentTab);
    	
        if (index < 0 || index >= mTabSpecs.size()) {
            Logger.e(TAG,"index is out of range");
            return;
        }

/*        if (index == mCurrentTab) {
            Logger.d(TAG,"noneed to change");
            return;
        }*/
        
        //Logger.i("PviTabHost","in setCurrentTab , tab changed to "+index);
        
    	//保存最后的切换操作对象
    	this.tabIndexWaitToShow = index;
    	this.actTabNameWaitToShow = mTabSpecs.get(index).getTag(); 
    	
    	Logger.d(TAG,"I am waiting to be shown :actTabNameWaitToShow "+actTabNameWaitToShow+", tabIndexWaitToShow:"+tabIndexWaitToShow);
        
        
        //给框架发个消息“我要进行界面切换”
        
        //获取当前外层tabactivity的类名
    	String act = PviReaderUI.findActByTabname(actTabNameWaitToShow, PviReaderUI.buildBlockNameList(this.getContext()));
        if(act!=null){
	       // Logger.i("PviTabHost","act:"+act);
	        
	              //通知框架跳转
	        Intent tmpIntent = new Intent(
	                MainpageActivity.START_ACTIVITY);
	        Bundle bundleToSend = new Bundle();
	        bundleToSend.putString("act", act);
	        //Logger.i(TAG,"blockName:"+blockNames[mTabHost.getCurrentTab()]);
	        bundleToSend.putString("actTabName", actTabNameWaitToShow);
	        bundleToSend.putString("switchInTab", "1");//在tab中通过操作控件发生界面跳转
	        tmpIntent.putExtras(bundleToSend);
	        this.getContext().sendBroadcast(tmpIntent);
	        tmpIntent = null;
	        bundleToSend = null;
	        //////////////////////////     


        }else{
            //Logger.e(TAG,"String act = PviReaderUI.findActByTabname(...   act is null!");
        }
        

        final PviTabHost.TabSpec spec = mTabSpecs.get(index);

        // tab content
        //Logger.d(TAG,"get child activity's dector view");
        //mCurrentView = spec.mContentStrategy.getContentView();
        
        childViews[index]=spec.mContentStrategy.getContentView();


    }
    
  
    
    public void setCurrentTabDo(int index) { 
        Logger.d(TAG,"setCurrentTabDo( index:"+index);
        // notify old tab content
        if (mCurrentTab != -1) {
            mTabSpecs.get(mCurrentTab).mContentStrategy.tabClosed();
        }

        mCurrentTab = index;

        // Call the tab widget's focusCurrentTab(), instead of just
        // selecting the tab.
        //mTabWidget.focusCurrentTab(mCurrentTab);
        
        
        //切换tab背景
        //GlobalVar appState = ((GlobalVar) getContext().getApplicationContext());

        //Logger.i("TabHost","setCurrentTabDo , mCurrentTab:"+mCurrentTab);
        /*final LinearLayout ll = (LinearLayout) mTabHost.getChildAt(0);
        final PviTabWidget tw = (PviTabWidget) ll.getChildAt(0);*/

            final int[] bgs = {R.drawable.tab_bgimg_0_ui1,
                    R.drawable.tab_bgimg_1_ui1,
                    R.drawable.tab_bgimg_2_ui1,
                    R.drawable.tab_bgimg_3_ui1,
                    R.drawable.tab_bgimg_4_ui1,
                    R.drawable.tab_bgimg_5_ui1};
            if(-1<mCurrentTab && mCurrentTab<6){
                mTabWidget.setBackgroundResource(bgs[mCurrentTab]);
            }

        //结束——切换tab背景

        //Logger.e(TAG,"mCurrentTab:"+mCurrentTab);
        mCurrentView = childViews[mCurrentTab];
        //2011-7-15设置标题为tabname  放到框架的showme消息处理里面去做了，到底哪种做法更合适？
/*        final Intent intent = new Intent(MainpageActivity.SET_TITLE);
        final Bundle sndBundle = new Bundle();
        sndBundle.putString("title", this.getCurrentTabTag());
        intent.putExtras(sndBundle);
        mContext.sendBroadcast(intent);*/
        
        if(mCurrentView==null){
            //Logger.e(TAG,"mCurrentView is null");
            return;
        }

        if (mCurrentView.getParent() == null) {
            
            
            
            //设置eink屏刷模式    这里不起作用        
            /*final GlobalVar app = (GlobalVar)mTabContent.getContext().getApplicationContext();
            if(app.deviceType==1){
                mTabContent.setUpdateMode(View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
            }*/            
            mTabContent
                    .addView(
                            mCurrentView,
                            new ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT));
            
            //Logger.e(TAG,mCurrentTab+" is added");
        }else{
            //Logger.e(TAG,mCurrentTab+" re show");
            mCurrentView.setVisibility(View.VISIBLE);
        } 
        
        if (!mTabWidget.hasFocus()) {
            // if the tab widget didn't take focus (likely because we're in touch mode)
            // give the current tab content view a shot
            
           //触摸也不让让contentview获取焦点  注释调这行   
            //2011-5-4恢复这行
            mCurrentView.requestFocus();
        }

        mTabContent.requestFocus(View.FOCUS_FORWARD);

        invokeOnTabChangeListener();
        
/*        //全刷一下屏幕  2011-5-29 移到接受show——me 消息时 全刷
        final GlobalVar app = (GlobalVar)mTabContent.getContext().getApplicationContext();
        if(app.deviceType==1){
            Logger.d(TAG,"invalidate window:gc16 full");
            //刷新全屏 会刷死 ?           
            getRootView().invalidate(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL); 
        } */
        
        
        //2011-7-15 由 showme 消息处理 移到这里！
        updateBottomBar();

    }    

    private void updateBottomBar(){
        if(mCurrentView!=null){
        // 框架底栏控件更新
        final Activity childActivity = (Activity) mCurrentView
                .getContext();
        if (childActivity instanceof PviActivity) {
            final PviActivity pa = (PviActivity) childActivity;
            // 设置底部工具栏
            final PviBottomBar bottomBar = ((GlobalVar) pa
                    .getApplication()).pbb;
/*                            // Logger.d(TAG, "set bottom bar：");
            // Logger.d(TAG, "set pager visiblity:" +
            // pa.showPager);
            bottomBar.setItemVisible("prevpage", pa.showPager);
            bottomBar.setItemVisible("pagerinfo", pa.showPager);
            bottomBar.setItemVisible("nextpage", pa.showPager);

            // Logger.d(TAG, "set chaper visiblity:" +
            // pa.showChaper);
            bottomBar.setItemVisible("prevchap", pa.showChaper);
            bottomBar.setItemVisible("nextchap", pa.showChaper);

            bottomBar.updateDraw();*/

            if (pa.showChaper) {
                Logger.d(TAG,"分章控件：  底栏 关联 tab内嵌activity");
                bottomBar.setChapable(pa); 
                bottomBar.actionShowChaper();
            }else{
                Logger.d(TAG,"不显示分章控件");
                bottomBar.actionHideChaper();
            }
            
            if (pa.showPager) {
                Logger.d(TAG,"分页控件：底栏 关联 tab内嵌activity:"+pa);
                bottomBar.setPageable(pa);
                bottomBar.actionShowPager();
                bottomBar.actionUpdatePagerinfo();
            }else{
                Logger.d(TAG,"不显示分页控件");
                bottomBar.actionHidePager();
            }

        }
        }
    }
    
    /**
     * Register a callback to be invoked when the selected state of any of the items
     * in this list changes
     * @param l
     * The callback that will run
     */
    public void setOnTabChangedListener(OnTabChangeListener l) {
        mOnTabChangeListener = l;
    }

    private void invokeOnTabChangeListener() {
        if (mOnTabChangeListener != null) {
            mOnTabChangeListener.onTabChanged(getCurrentTabTag());
        }
    }

    /**
     * Interface definition for a callback to be invoked when tab changed
     */
    public interface OnTabChangeListener {
        void onTabChanged(String tabId);
    }


    /**
     * Makes the content of a tab when it is selected. Use this if your tab
     * content needs to be created on demand, i.e. you are not showing an
     * existing view or starting an activity.
     */
    public interface TabContentFactory {
        /**
         * Callback to make the tab contents
         *
         * @param tag
         *            Which tab was selected.
         * @return The view to display the contents of the selected tab.
         */
        View createTabContent(String tag);
    }


    /**
     * A tab has a tab indicator, content, and a tag that is used to keep
     * track of it.  This builder helps choose among these options.
     *
     * For the tab indicator, your choices are:
     * 1) set a label
     * 2) set a label and an icon
     *
     * For the tab content, your choices are:
     * 1) the id of a {@link View}
     * 2) a {@link TabContentFactory} that creates the {@link View} content.
     * 3) an {@link Intent} that launches an {@link android.app.Activity}.
     */
    public class TabSpec {

        private String mTag;

        private IndicatorStrategy mIndicatorStrategy;
        private ContentStrategy mContentStrategy;

        private TabSpec(String tag) {
            mTag = tag;
        }

/*        *//**
         * Specify a label as the tab indicator.
         *//*
        public TabSpec setIndicator(CharSequence label) {
            mIndicatorStrategy = new LabelIndicatorStrategy(label);
            return this;
        }

        *//**
          Specify a label and icon as the tab indicator.
         *//*
        public TabSpec setIndicator(CharSequence label, Drawable icon) {
            mIndicatorStrategy = new LabelAndIconIndicatorStrategy(label, icon);
            return this;
        }*/

        /**
         * Specify a view as the tab indicator.
         */
        public TabSpec setIndicator(View view) {
            mIndicatorStrategy = new ViewIndicatorStrategy(view);
            return this;
        }

        /**
         * Specify the id of the view that should be used as the content
         * of the tab.
         */
        public TabSpec setContent(int viewId) {
            mContentStrategy = new ViewIdContentStrategy(viewId);
            return this;
        }

        /**
         * Specify a {@link android.widget.TabHost.TabContentFactory} to use to
         * create the content of the tab.
         */
        public TabSpec setContent(TabContentFactory contentFactory) {
            mContentStrategy = new FactoryContentStrategy(mTag, contentFactory);
            return this;
        }

        /**
         * Specify an intent to use to launch an activity as the tab content.
         */
        public TabSpec setContent(Intent intent) {
            mContentStrategy = new IntentContentStrategy(mTag, intent);
            return this;
        }


        public String getTag() {
            return mTag;
        }
    }

    /**
     * Specifies what you do to create a tab indicator.
     */
    private static interface IndicatorStrategy {

        /**
         * Return the view for the indicator.
         */
        View createIndicatorView();
    }

    /**
     * Specifies what you do to manage the tab content.
     */
    private static interface ContentStrategy {

        /**
         * Return the content view.  The view should may be cached locally.
         */
        View getContentView();

        /**
         * Perhaps do something when the tab associated with this content has
         * been closed (i.e make it invisible, or remove it).
         */
        void tabClosed();
    }

/*    *//**
     * How to create a tab indicator that just has a label.
     *//*
    private class LabelIndicatorStrategy implements IndicatorStrategy {

        private final CharSequence mLabel;

        private LabelIndicatorStrategy(CharSequence label) {
            mLabel = label;
        }

        public View createIndicatorView() {
            final Context context = getContext();
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tabIndicator = inflater.inflate(R.layout.tab_indicator,
                    mTabWidget, // tab widget is the parent
                    false); // no inflate params

            final TextView tv = (TextView) tabIndicator.findViewById(R.id.title);
            tv.setText(mLabel);

            if (context.getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.DONUT) {
                // Donut apps get old color scheme
                tabIndicator.setBackgroundResource(R.drawable.tab_indicator_v4);
                tv.setTextColor(context.getResources().getColorStateList(R.color.tab_indicator_text_v4));
            }
            
            return tabIndicator;
        }
    }

    *//**
     * How we create a tab indicator that has a label and an icon
     *//*
    private class LabelAndIconIndicatorStrategy implements IndicatorStrategy {

        private final CharSequence mLabel;
        private final Drawable mIcon;

        private LabelAndIconIndicatorStrategy(CharSequence label, Drawable icon) {
            mLabel = label;
            mIcon = icon;
        }

        public View createIndicatorView() {
            final Context context = getContext();
            LayoutInflater inflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View tabIndicator = inflater.inflate(R.layout.tab_indicator,
                    mTabWidget, // tab widget is the parent
                    false); // no inflate params

            final TextView tv = (TextView) tabIndicator.findViewById(android.R.id.title);
            tv.setText(mLabel);

            final ImageView iconView = (ImageView) tabIndicator.findViewById(android.R.id.icon);
            iconView.setImageDrawable(mIcon);

            if (context.getApplicationInfo().targetSdkVersion <= Build.VERSION_CODES.DONUT) {
                // Donut apps get old color scheme
                tabIndicator.setBackgroundResource(R.drawable.tab_indicator_v4);
                tv.setTextColor(context.getResources().getColorStateList(R.color.tab_indicator_text_v4));
            }
            
            return tabIndicator;
        }
    }*/

    /**
     * How to create a tab indicator by specifying a view.
     */
    private class ViewIndicatorStrategy implements IndicatorStrategy {

        private final View mView;

        private ViewIndicatorStrategy(View view) {
            mView = view;
        }

        public View createIndicatorView() {
            return mView;
        }
    }

    /**
     * How to create the tab content via a view id.
     */
    private class ViewIdContentStrategy implements ContentStrategy {

        private final View mView;

        private ViewIdContentStrategy(int viewId) {
            mView = mTabContent.findViewById(viewId);
            if (mView != null) {
                mView.setVisibility(View.GONE);
            } else {
                throw new RuntimeException("Could not create tab content because " +
                        "could not find view with id " + viewId);
            }
        }

        public View getContentView() {
            mView.setVisibility(View.VISIBLE);
            return mView;
        }

        public void tabClosed() {
            mView.setVisibility(View.GONE);
        }
    }

    /**
     * How tab content is managed using {@link TabContentFactory}.
     */
    private class FactoryContentStrategy implements ContentStrategy {
        private View mTabContent;
        private final CharSequence mTag;
        private TabContentFactory mFactory;

        public FactoryContentStrategy(CharSequence tag, TabContentFactory factory) {
            mTag = tag;
            mFactory = factory;
        }

        public View getContentView() {
            if (mTabContent == null) {
                mTabContent = mFactory.createTabContent(mTag.toString());
            }
            mTabContent.setVisibility(View.VISIBLE);
            return mTabContent;
        }

        public void tabClosed() {
            mTabContent.setVisibility(View.GONE);
        }
    }

    /**
     * How tab content is managed via an {@link Intent}: the content view is the
     * decorview of the launched activity.
     */
    private class IntentContentStrategy implements ContentStrategy {

        private final String mTag;
        private final Intent mIntent;

        private View mLaunchedView;

        private IntentContentStrategy(String tag, Intent intent) {
            mTag = tag;
            mIntent = intent;
        }

        public View getContentView() {
            //Logger.d(TAG,"getContentView()");
            if (mLocalActivityManager == null) {
                throw new IllegalStateException("Did you forget to call 'public void setup(LocalActivityManager activityGroup)'?");
            }
            //Logger.d(TAG,"mLocalActivityManager.startActivity(...");
            //mIntent.setFlags(Intent.fl)
            final Window w = mLocalActivityManager.startActivity(
                    mTag, mIntent);
            final View wd = w != null ? w.getDecorView() : null;
            if (mLaunchedView != wd && mLaunchedView != null) {
                if (mLaunchedView.getParent() != null) {
                    //Logger.e(TAG,"mTabContent.removeView(mLaunchedView);");
                    mTabContent.removeView(mLaunchedView);
                }
            }
            mLaunchedView = wd;

            // XXX Set FOCUS_AFTER_DESCENDANTS on embedded activities for now so they can get
            // focus if none of their children have it. They need focus to be able to
            // display menu items.
            //
            // Replace this with something better when Bug 628886 is fixed...
            //
           if (mLaunchedView != null) {
                //2011-7-15删除 mLaunchedView.setVisibility(View.VISIBLE);
                mLaunchedView.setFocusableInTouchMode(true);
                ((ViewGroup) mLaunchedView).setDescendantFocusability(
                        FOCUS_AFTER_DESCENDANTS);
                
                FrameLayout ff = (FrameLayout)mLaunchedView.findViewById(android.R.id.content);
                if(ff!=null){
                    ff.setForeground(null);
                }
                
            }
           
         //设置eink屏刷模式
           final GlobalVar appState = ((GlobalVar) getContext().getApplicationContext());
           if(appState.deviceType==1){
               //System.err.println("tabwidget 's updatemode is set!");
               //只刷新tab内嵌的activity区域  起作用马？
               //mLaunchedView.setUpdateMode( View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
               
           }

            return mLaunchedView;
        }

        public void tabClosed() {
            if (mLaunchedView != null) {
                mLaunchedView.setVisibility(View.GONE);
            }
        }
    }
    
    
    private boolean mUpdating = false;
    private Context mContext;
    void setUpdates(boolean update) {
        //Logger.d(TAG,"setUpdates(boolean update) update:"+update);
        if (update != mUpdating) {
            mUpdating = update;
            if (update) {
                // Register for Intent broadcasts for the clock and battery
                final IntentFilter filter = new IntentFilter();
                filter.addAction(MainpageActivity.SHOW_ME);                
                mContext.registerReceiver(mIntentReceiver, filter);
                //Logger.d(TAG,"注册");
            } else {
                mContext.unregisterReceiver(mIntentReceiver);
                //Logger.d(TAG,"反注册");
            }
        }
    }
    
    

}
