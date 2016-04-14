package com.pvi.ap.reader.activity.pviappframe;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.data.common.Logger;
import android.app.Activity;
import android.app.ActivityGroup;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabWidget;

/**
 * 改写的PviTabActivity
 * 
 * @author rd040 马中庆
 */
public class PviTabActivity extends ActivityGroup {
    private static final String TAG = "PviTabActivity";
    private PviTabHost mTabHost;
    private String mDefaultTab = null;
    private int mDefaultTabIndex = -1;
    
    private String[] blockNames;
    public boolean saved = false;
    public int countF = 0;//countFullFlashAfterAttachToWindow

    public String[] getBlockNames() {
        return blockNames;
    }

    public void setBlockNames(String[] blockNames) {
        this.blockNames = blockNames;
    }

    public PviTabActivity() {
    }

    /**
     * Sets the default tab that is the first tab highlighted.
     * 
     * @param tag the name of the default tab
     */
    public void setDefaultTab(String tag) {
        mDefaultTab = tag;
        mDefaultTabIndex = -1;
    }

    /**
     * Sets the default tab that is the first tab highlighted.
     * 
     * @param index the index of the default tab
     */
    public void setDefaultTab(int index) {
        mDefaultTab = null;
        mDefaultTabIndex = index;
    }
    
    //给外部提供控制TAB切换的能力
    public void setCurrentTab(String tabName){
        if(tabName==null||"".equals(tabName)){
            throw new NullPointerException("param 'tabName' is null or empty");
        }
        //Logger.i(TAG,"setCurrentTab "+tabName);
        final int curTabIndex = getBlockIndex(tabName);
        final PviTabHost mTabHost = getTabHost();
        if(curTabIndex>-1){
            mTabHost.setCurrentTab(curTabIndex);
        }
    }
    
    public int getBlockIndex(String name){
        if(name==null||"".equals(name)){
            throw new NullPointerException("param 'name' is null or empty");
        }
        
        int index = -1;
        if(blockNames!=null){
            final int len = blockNames.length;
            for(int i=0;i<len;i++){
                if(name.equals(blockNames[i])){
                    index = i;
                    return index;
                }
            }
        }else{
            onResume();
        }
        return index;
    }

    @Override
    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        ensureTabHost();
        String cur = state.getString("currentTab");
        if (cur != null) {
            mTabHost.setCurrentTabByTag(cur);
        }
        if (mTabHost.getCurrentTab() < 0) {
            if (mDefaultTab != null) {
                mTabHost.setCurrentTabByTag(mDefaultTab);
            } else if (mDefaultTabIndex >= 0) {
                mTabHost.setCurrentTab(mDefaultTabIndex);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle icicle) {        
        super.onPostCreate(icicle);

        ensureTabHost();

/*2011-7-13注释掉        if (mTabHost.getCurrentTab() == -1) {
            mTabHost.setCurrentTab(0);
        }*/
        
        
        //仅当-1   且  没有指定 子界面 时，才需要载入0号子界面  .....  把这个逻辑移到其它地方去
        
/*        if (mTabHost.getCurrentTab() == -1
                && (mTabHost.actTabNameWaitToShow==null||"".equals(mTabHost.actTabNameWaitToShow) )
                ) {
            mTabHost.setCurrentTab(0);
        }*/       
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String currentTabTag = mTabHost.getCurrentTabTag();
        if (currentTabTag != null) {
            outState.putString("currentTab", currentTabTag);
        }
    }

    /**
     * Updates the screen state (current list and other views) when the
     * content changes.
     * 
     *@see Activity#onContentChanged()
     */
    @Override
    public void onContentChanged() {
        //Logger.d(TAG,"onContentChanged");
        
        super.onContentChanged();
        mTabHost = (PviTabHost) findViewById(android.R.id.tabhost);
        
/*        try{
            System.err.println("pvitabactivity updatemode is set .");
          //设置刷新模式
            //((LinearLayout)mTabHost.getChildAt(0)).setUpdateMode(View.EINK_AUTO_MODE_AUTOMATIC| View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
            //((FrameLayout)mTabHost.getParent()).setUpdateMode(View.EINK_AUTO_MODE_MASK| View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
            //((FrameLayout)mTabHost.getParent()).invalidate(View.EINK_AUTO_MODE_MASK| View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
        }catch(Exception e){
            System.err.println("pvitabactivity updatemode  set  fail!!!.");
            e.printStackTrace();}*/

        if (mTabHost == null) {
            throw new RuntimeException(
                    "Your content must have a TabHost whose id attribute is " +
                    "'android.R.id.tabhost'");
        }
        mTabHost.setup(getLocalActivityManager());
    }

    private void ensureTabHost() {
        if (mTabHost == null) {
            this.setContentView(R.layout.tab_content_pvi);
        }
    }



    /**
     * Returns the {@link TabHost} the activity is using to host its tabs.
     *
     * @return the {@link TabHost} the activity is using to host its tabs.
     */
    public PviTabHost getTabHost() {
        ensureTabHost();
        return mTabHost;
    }

    /**
     * Returns the {@link TabWidget} the activity is using to draw the actual tabs.
     *
     * @return the {@link TabWidget} the activity is using to draw the actual tabs.
     */
    public PviTabWidget getTabWidget() {
        return mTabHost.getTabWidget();
    }

    @Override
    public void onDetachedFromWindow() {
        // TODO Auto-generated method stub
        countF = 0;
        super.onDetachedFromWindow();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK||keyCode == KeyEvent.KEYCODE_MENU) {//传递给内嵌activity
            //if(mTabHost!=null
                    //&& mTabHost.getCurrentView()!=null){
                //mTabHost.getCurrentView().dispatchKeyEvent(event);   //这个也可以 哪种写法好？
            if(mTabHost!=null){               
                final View v = mTabHost.getCurrentView();
                if(v!=null){
                    v.dispatchKeyEvent(event);
                }
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        //tabhost注册showme监听
        mTabHost.setUpdates(false);
        super.onPause();
    }

    @Override
    protected void onResume() {
        //tabhost注册showme监听
        Logger.d(TAG,"onResume()");
        mTabHost.setUpdates(true);
        super.onResume();
    }
    
}
