package com.pvi.ap.reader.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.ActivityManager;
import android.app.LocalActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviReaderUI;
import com.pvi.ap.reader.activity.pviappframe.PviServiceUtil;
import com.pvi.ap.reader.activity.pviappframe.PviStatusBar;
import com.pvi.ap.reader.activity.pviappframe.PviTabActivity;
import com.pvi.ap.reader.activity.pviappframe.PviTitleBar;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviTitleBar.OnSwtichListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.Bookmark;

/**
 * 应用主框架
 * 
 * @author 马中庆RD040
 * 
 */

public class MainpageActivity extends ActivityGroup {
    private static final String TAG = "MainpageActivity";
    private Context mContext = MainpageActivity.this;;
    private LayoutInflater mInflater;
    private Resources mRes;
    private Handler mHandler = new H();
    private static final float TARGET_HEAP_UTILIZATION = 0.75f;
    private final static long PVI_HEAP_SIZE = 6 * 1024 * 1024;

    private FrameLayout container;
    private LinearLayout mainBlock; // 包括标题栏的
    private PviStatusBar mStatusBar;
    private PviBottomBar mBottomBar;
    private PviTitleBar mPviTitleBar;
    // private RelativeLayout mTitleBar; // 标题栏 控件

    private LinearLayout mNav;// 主导航栏（可显隐）
    private ImageView mStatusBarBottom;

    /*
     * // 使用框架嵌入子Activity时，可用参数 private String actID; // 保存框架定义的act的ID private
     * String actName; // 保存act名称 显示在标题栏 private String actLevel; // actd的层级
     * private String returnActID; // 保存“指定返回界面 的actID” private String act; //
     * 保存 actAction //private String actTabIndex; // 保存 tab index private String
     * activityID; // activity标识 private String haveStatusBar; // 是否显示状态栏
     * private String haveTitleBar; // 是否显示标题栏 private String haveMenuBar; //
     * 暂时去掉！ private String fullScreen; // 全屏标识 private String startType; //
     * “启动”类型 private String pviapfStatusTip; // 启动 acitivty时 在状态栏显示提示信息
     * protected String pviapfStatusTipTime; // 状态栏提示信息显示时间 ms private String
     * mainTitle; // 保存标题栏 private String childViewBg; // 保存 嵌入区域的 背景
     */
    // 广播Action常量定义
    public final static String START_ACTIVITY = "com.pvi.ap.reader.mainframe.START_ACTIVITY";// 在主要区域启动一个Activity（是否每次都OnCreate？）
    public final static String SET_TITLE = "com.pvi.ap.reader.mainframe.SET_TITLE";// 设置标题栏文字
    public final static String BACK = "com.pvi.ap.reader.mainframe.BACK";// 返回上一个子Activity
    public final static String SHOW_TIP = "com.pvi.ap.reader.mainframe.SHOW_TIP";// 在状态栏上显示提示信息
    public final static String HIDE_TIP = "com.pvi.ap.reader.mainframe.HIDE_TIP";// 在状态栏上显示提示信息
    public final static String SERVICE_RESP = "com.pvi.ap.reader.mainframe.SERVICE_RESP";// 从服务中广播过来的某些操作结果等消息
    public final static String SHOW_ALERT = "com.pvi.ap.reader.mainframe.SHOW_ALERT";// 通知框架，弹出特定的提示框
    public final static String HIDE_ALERT = "com.pvi.ap.reader.mainframe.HIDE_ALERT";// 通知框架，隐藏提示框
    public final static String FULLSCREEN_ON = "com.pvi.ap.reader.mainframe.FULLSCREEN_ON";// 通知框架，“全屏”
    public final static String FULLSCREEN_OFF = "com.pvi.ap.reader.mainframe.FULLSCREEN_OFF";// 取消“全屏”
    public final static String VOLUME_CHANGED = "com.pvi.ap.reader.mainframe.VOLUME_CHANGED";// 系统音量发生了变化
    public final static String SHOW_ME = "com.pvi.ap.reader.mainframe.SHOW_ME";// 子界面告诉框架，我已经可以显示了

    // activtys 管理
    private String lastAct;
    private String lastStatusBar; // 保存上次状态栏状态标志
    private String lastHaveMenuBar;
    private boolean lastHaveStatusBar; // 标志量： 上一个视图是否有状态栏
    private boolean lastHaveTitleBar; // 标志量： 上一个视图是否有标题栏 这里的变量似乎有些混乱或者说重复了，待整理
    private String thisActStartType; // 保存“这一个动作”的“启动方式”
    private String lastChildViewBg; // 保存 上一个嵌入视图的 背景
    private Bundle mActivityGroupState;

    private Stack<String[]> msActStack = new Stack<String[]>();// 保存被嵌入的activity信息
    private static int actStackSize = 11;
    private String[] lastFrameState = new String[actStackSize];// （当前/前）主框架的状态，目前
    // 【0】被嵌acvitiy的act，【1】状态栏状态标志1显示/0
    // 【2】acitivityID 【3】是否显示标题栏 1显示
    // 【4】标题栏文本（如果为空则隐藏标题栏）【5】嵌入区的背景 0透明 其它resid
    // 【6】tabIndex【7】ActID【8】actLevel
    // 【9】startType
    // [10]是否显示底部工具栏 （2011-6-25增加）
    
    //2011-7-18 start——act 消息 增加处理时限，允许时间间隔小于500ms
    private Stack<Long> mPtime = new Stack<Long>();

    private ArrayList<String> mAcAct = new ArrayList<String>();// 保存所有allwaysCreate方式启动的activity实例id

    protected PviAlertDialog pd;

    // 用来保存子Activity
    protected View childView1;
    protected Intent newIntent;

    // act 数据
    private HashMap<String, HashMap<String, String>> actList = new HashMap<String, HashMap<String, String>>();
    private ArrayList<String> ignorActList = PviReaderUI.getIgnorActList();

    private LocalActivityManager mLam;
    
    private View curFocusView; // 保存当前焦点View控件

    private int statusBarBottomBg = R.drawable.statusbar_bottom_ui1;
    public final static int UI_UPDATE_CHANGE_STATUSBAR_BOTTOM = 501;

    // public final static int UPDATEMODE_1 = View.EINK_WAIT_MODE_NOWAIT |
    // View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL;
    // public final static int UPDATEMODE_2 = View.EINK_WAIT_MODE_NOWAIT |
    // View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // Logger.i(TAG,"onCreate");

        // Logger.i(TAG,""+VMRuntime.getRuntime().setTargetHeapUtilization(TARGET_HEAP_UTILIZATION));
        // VMRuntime.getRuntime().setMinimumHeapSize(PVI_HEAP_SIZE);

        /*
         * 测试读取别的app中的文件 try { File systemSetFile = new
         * File(Environment.getDataDirectory() +
         * "/data/com.pvi.ap.reader.service/main.xml"); BufferedReader reader =
         * new BufferedReader(new FileReader(systemSetFile)); String text = "";
         * while ( (text = reader.readLine()) != null ){ Log.i("xx",text); }
         * 
         * } catch (Exception e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         */

        // // set the default handler to caught the exceptions
        // Thread.setDefaultUncaughtExceptionHandler(new
        // UncaughtExceptionHandler() {
        //
        // @Override
        // public void uncaughtException(Thread thread, Throwable ex) {
        // // TODO Auto-generated method stub
        // // we can't show any visible view as the main view
        // // thread has crashed
        // // if(ex instanceof OutOfMemoryError){
        // // if is oom we restart it
        // Logger.d(thread.getName(),ex.getLocalizedMessage());
        // PendingIntent intent = PendingIntent.getActivity(
        // getBaseContext(), 0, new Intent(getIntent()),
        // getIntent().getFlags());
        //
        // AlarmManager mgr = (AlarmManager)
        // getSystemService(Context.ALARM_SERVICE);
        // mgr.set(AlarmManager.RTC,
        // System.currentTimeMillis() + 2000, intent);
        // System.exit(2);
        // // }
        // }
        //
        // });
        // Config.setString("STORE_WELCOME_TIME", "0");

        SharedPreferences settings = getSharedPreferences(
                Constants.configFileName, MODE_WORLD_READABLE
                        | MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor ed = settings.edit();
        ed.putString(WirelessStoreMainpageActivity.STORE_WELCOME_TIME, "0");
        ed.commit();

/*        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  */     

        setContentView(R.layout.mainframe_ui1);
        super.onCreate(savedInstanceState);

        // 去掉FrameLayout的前景
        FrameLayout topFrame = (FrameLayout) findViewById(android.R.id.content);
        topFrame.setForeground(null);
        topFrame = null;

        // 上下文保存到成员变量
        // mContext = getApplicationContext();//getBaseContext();        
        mRes = mContext.getResources();
        mLam = getLocalActivityManager();
        mInflater = LayoutInflater.from(mContext);

        // 读取系统信息
        ReadSystemMessage readSystemMessage = new ReadSystemMessage(
                MainpageActivity.this);
        readSystemMessage.run();
        initNeedAutoList();

        // 载入界面配置
        // initActList();
        actList = PviReaderUI.initActList(mRes);

        // 关闭日志、打开指定级别日志 暂时注释掉
        // Logger.closeLogger();
        // Logger.openInfoLogger(false);

        // 初始化UI
        initControls();
        initUpdateMode();
        bindEvent();

        IntentFilter filter = new IntentFilter();
        // 注册意图广播接收者

        // 添加自定义的框架消息
        filter.addAction(MainpageActivity.START_ACTIVITY);
        filter.addAction(MainpageActivity.SET_TITLE);
        filter.addAction(MainpageActivity.BACK);
        filter.addAction(MainpageActivity.SHOW_ME);
        filter.addAction(MainpageActivity.SERVICE_RESP);
        filter.addAction(MainpageActivity.FULLSCREEN_ON);
        filter.addAction(MainpageActivity.FULLSCREEN_OFF);

        registerReceiver(mIntentReceiver, filter);

        IntentFilter upfi = new IntentFilter();
        upfi.addAction("com.pvi.ap.reader.service.update");
        registerReceiver(updateReceiver, upfi);

        mPtime.add(0L);
        
        // 载入 首页
        homeAcvitity();

    }

    /*
     * public void onResume() { //to turn off boot animation try { File file=new
     * File("/proc/bootAnimation"); FileOutputStream fos; fos = new
     * FileOutputStream(file); OutputStreamWriter osw=new
     * OutputStreamWriter(fos); osw.write("0"); osw.flush(); fos.close();
     * osw.close(); } catch (FileNotFoundException e) { e.printStackTrace(); }
     * catch (IOException e) { e.printStackTrace(); }
     * 
     * super.onResume(); }
     */

    private void initControls() {
        // bReturn = (TextView) findViewById(R.id.returnbtn);
        container = (FrameLayout) findViewById(R.id.ChildActivityView);
        mainBlock = (LinearLayout) findViewById(R.id.MainBlock);

        // mStatusIcons = (LinearLayout) findViewById(R.id.statusIcons);

        mStatusBar = (PviStatusBar) findViewById(R.id.StatusBar);
        mBottomBar = (PviBottomBar) findViewById(R.id.BottomBar);
        mPviTitleBar = (PviTitleBar) findViewById(R.id.PviTitleBar);
        final GlobalVar app = ((GlobalVar) getApplicationContext());
        app.pbb = mBottomBar;

        // mtvTip.setTextColor(Color.BLACK);
        // boBackBtn = (ImageView) findViewById(R.id.GobackBtn);

        // mStatusBarBottom = (ImageView)
        // findViewById(R.id.ImageViewStatusBarBottom);

        // 过渡“提示框”
        /*
         * pd = new PviAlertDialog(this); pd.setMessage("界面转换中。。。");
         * pd.setHaveProgressBar(true);
         */

        mNav = (LinearLayout) mInflater.inflate(R.layout.mainnav, null);
        initNavList();
        NavListAdapter navListAdapter = new NavListAdapter(mContext, navList);
        GridView lvNavListview = (GridView) mNav.findViewById(R.id.navList);
        lvNavListview.setAdapter(navListAdapter);

    }

    private void initUpdateMode() {
        if (((GlobalVar) getApplication()).deviceType == 1) {// fsl真机
            // 2011-6-28注释掉
            // getWindow().getDecorView().getRootView().setUpdateMode(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);//全屏幕
            // 设置WAIT
            // container.setUpdateMode(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);
            // mNav.setUpdateMode(View.EINK_WAIT_MODE_WAIT);

            // mUiPath.setUpdateMode(View.EINK_WAIT_MODE_WAIT);
            // btnSwichNav.setUpdateMode(UPDATEMODE_1);

            // mTitleBar.setUpdateMode(UPDATEMODE_1);

            // mStatusIcons.setUpdateMode(UPDATEMODE_1);
            // mStatusBar.setUpdateMode(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);
        }
    }

    /**
     * 通知框架 返回 首界面
     */
    private void backtoHomeAcvitity() {

        Intent intent = new Intent(MainpageActivity.BACK);
        Bundle sndBundle = new Bundle();
        sndBundle.putString("actID", "ACT10000");
        intent.putExtras(sndBundle);
        sendBroadcast(intent);
        sndBundle = null;
        intent = null;
        Logger.i(TAG, "backtoHomeAcvitity()");
    }

    /**
     * 通知框架载入 首界面
     */
    private void homeAcvitity() {

        SharedPreferences settings = getSharedPreferences(Config
                .getString("configFileName"), MODE_WORLD_READABLE
                | MODE_WORLD_WRITEABLE);
        String state = settings.getString(StartUpActivity.STATE, "");

        if (StartUpActivity.MAINPAGE.equals(state)) {
            Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
            Bundle sndBundle = new Bundle();
            sndBundle.putString("act",
                    "com.pvi.ap.reader.activity.MainpageInsideActivity");
            sndBundle.putString("haveStatusBar", "1");
            sndBundle.putString("startType", "reuse");
            intent.putExtras(sndBundle);
            sendBroadcast(intent);
            sndBundle = null;
            intent = null;

        } else if (StartUpActivity.LASTREAD.equals(state)) {

            final String[] frameState = new String[actStackSize];
            frameState[7] = "ACT10000";
            msActStack.push(frameState.clone());

            goonRead();
        } else {
            final Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
            final Bundle sndBundle = new Bundle();
            sndBundle.putString("act",
                    "com.pvi.ap.reader.activity.MainpageInsideActivity");
            sndBundle.putString("haveStatusBar", "1");
            sndBundle.putString("startType", "reuse");
            intent.putExtras(sndBundle);
            sendBroadcast(intent);

        }

    }

    private void bindEvent() {

        // Logger.d(TAG,"mNav setOnFocusChangeListener");
        // 主导航 焦点变化事件处理

/*          mNav.setOnFocusChangeListener(new OnFocusChangeListener(){
         
         @Override public void onFocusChange(View arg0, boolean arg1) {
         //Logger.i(TAG,"onFocusChange");
         if(!arg1){ hideNav(); } }});*/
         

        mPviTitleBar.setOnSwtichListener(new OnSwtichListener() {

            @Override
            public void onSwtich(boolean isOpen) {
                // Logger.d(TAG,"onSwtich ( isOPen:"+isOpen);
                if (isOpen) {
                    hideNav();
                } else {
                    showNav();
                }
            }
        });
    }

    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // 使用框架嵌入子Activity时，可用参数
            String actID = null; // 保存框架定义的act的ID
            String actName = null; // 保存act名称 显示在标题栏
            String actLevel = null; // actd的层级
            String returnActID = null; // 保存“指定返回界面 的actID”
            String act = null; // 保存 actAction
            // String actTabIndex = null; // 保存 tab index
            String actTabName = null;
            String insideAct = null; // 保存 被TAB嵌的activity
            String activityID = null; // activity标识
            String haveStatusBar = null; // 是否显示状态栏
            String haveTitleBar = null; // 是否显示标题栏
            String haveMenuBar = null; // 暂时去掉！
            String fullScreen = null; // 全屏标识
            String startType = null; // “启动”类型
            String pviapfStatusTip = null; // 启动 acitivty时 在状态栏显示提示信息
            String pviapfStatusTipTime = null; // 状态栏提示信息显示时间 ms
            String mainTitle = null; // 保存标题栏
            String childViewBg = null; // 保存 嵌入区域的 背景
            String haveBottomBar = null; // 是否使用底栏

            // 以下为自定义的框架消息处理
            if (action.equals(MainpageActivity.START_ACTIVITY)) {
                
               
                Logger.i(TAG, "recv frame broadcast!" + action);

                // 过渡“提示框”（效果尚不太好，暂时去掉）
                // mHandler.sendEmptyMessage(100);

                // 这些变量先清空，或者取消成员变量，改变为局部变量，仅用来保存这些值
                actID = null;
                act = null;
                // actTabIndex = null;
                actTabName = null;
                insideAct = null;
                actName = null;
                actLevel = null;
                haveStatusBar = null;
                haveTitleBar = null;
                haveBottomBar = null;
                returnActID = null;
                childViewBg = null;
                activityID = null;
                startType = null;
                pviapfStatusTip = null;
                pviapfStatusTipTime = null;
                mainTitle = null;

                Bundle bd = null;
                if (intent != null) {
                    bd = intent.getExtras();
                }
                if (bd != null) {
                    
                    //2011-7-17增加逻辑，在极短时间段内的连续消息，不予处理
                    Long thisPtime = SystemClock.uptimeMillis();
                    mPtime.push(thisPtime);
                    
                    if(bd.getString("actTabName")==null/*tab内嵌的例外*/
                            &&mPtime.lastElement()-mPtime.firstElement()<500){
                        Logger.d(TAG,"deny start_act !");
                        return;
                    }else{
                        mPtime.clear();
                        mPtime.add(thisPtime);
                    }
                    
                    
                    // 根据act的字符串，检查是不是属于“不需要界面的activity”
                    if (bd.getString("act") != null
                            && ignorActList.contains(bd.getString("act"))) {
                        newIntent = new Intent(bd.getString("act"))
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);// reuse
                                                                             // 如果有需要allways
                                                                             // create的，以后增加配置支持
                        newIntent.putExtras(bd);
                        mLam.startActivity(bd.getString("act"), newIntent); // 直接后台执行该activiity
                        return;
                    }

                    // 如果传入了actID，则先通过actID去取得这些预设值
                    actID = bd.getString("actID");

                    if (actID == null) {// 已设置ActID，但是启动的时候没有给actID赋值的，要从actList中检索一下
                        // Logger.d(TAG,"actID  not set");
                        actID = getActIDByAct(bd.getString("act"), bd
                                .getString("actTabName"));
                        // actID = getActIDByAct(bd.getString("act"));
                        // Logger.d(TAG,"try to get actID, result:"+actID);
                    }

                    if (actID != null && !actID.equals("")) {

                        // Logger.d(TAG,
                        // "embed childView - load actinfo by actID " + actID);
                        HashMap theAct = actList.get(actID);

                        if (theAct != null) {
                            // Logger.d(TAG, "theAct is not null  ,act:" +
                            // theAct.get("act") );
                            // 从配置中取出的值如果bd里面没有设置的，记得加入bd
                            if (theAct.get("startType") != null) {
                                startType = theAct.get("startType").toString();
                                if (bd.getString("startType") == null) {
                                    bd.putString("startType", startType);
                                }
                            }

                            if (theAct.get("insideAct") != null) {
                                insideAct = theAct.get("insideAct").toString();
                                if (bd.getString("insideAct") == null) {
                                    bd.putString("insideAct", insideAct);
                                }
                            }

                            if (theAct.get("act") != null) {
                                act = theAct.get("act").toString();
                                if (bd.getString("act") == null) {
                                    bd.putString("act", act);
                                }
                            }

                            /*
                             * if (theAct.get("actTabIndex") != null) {
                             * actTabIndex = theAct.get("actTabIndex")
                             * .toString(); if (bd.getString("actTabIndex") ==
                             * null) { bd.putString("actTabIndex", actTabIndex);
                             * } }
                             */

                            if (theAct.get("actTabName") != null) {
                                actTabName = theAct.get("actTabName")
                                        .toString();
                                if (bd.getString("actTabName") == null) {
                                    bd.putString("actTabName", actTabName);
                                }
                            }

                            if (theAct.get("actName") != null) {
                                actName = theAct.get("actName").toString();
                                if (bd.getString("actName") == null) {
                                    bd.putString("actName", actName);
                                }
                            }
                            if (theAct.get("actLevel") != null) {
                                actLevel = theAct.get("actLevel").toString();
                                if (bd.getString("actLevel") == null) {
                                    bd.putString("actLevel", actLevel);
                                    // Logger.d(TAG,"bd.getString(actLevel) is null, use the config value, actLevel:"+actLevel);
                                } else {
                                    // Logger.d(TAG,"bd.getString(actLevel) = "+bd.getString("actLevel")
                                    // );
                                }
                            } else {
                                // Logger.d(TAG,"theAct actLevel is empty");
                            }
                            if (theAct.get("haveStatusBar") != null) {
                                haveStatusBar = theAct.get("haveStatusBar")
                                        .toString();
                                if (bd.getString("haveStatusBar") == null) {
                                    bd
                                            .putString("haveStatusBar",
                                                    haveStatusBar);
                                }
                            }
                            if (theAct.get("haveTitleBar") != null) {
                                haveTitleBar = theAct.get("haveTitleBar")
                                        .toString();
                                if (bd.getString("haveTitleBar") == null) {
                                    bd.putString("haveTitleBar", haveTitleBar);
                                }
                                // Logger.d(TAG,bd.getString("haveTitleBar") );
                                // Logger.d(TAG,"theAct.get(haveTitleBar):"+theAct.get("haveTitleBar"));
                            }
                            if (theAct.get("haveBottomBar") != null) {
                                haveBottomBar = theAct.get("haveBottomBar")
                                        .toString();
                                if (bd.getString("haveBottomBar") == null) {
                                    bd
                                            .putString("haveBottomBar",
                                                    haveBottomBar);
                                }
                            }
                            if (theAct.get("returnActID") != null) {
                                returnActID = theAct.get("returnActID")
                                        .toString();
                                if (bd.getString("returnActID") == null) {
                                    bd.putString("returnActID", returnActID);
                                }
                            }

                            if (theAct.get("childViewBg") != null) {
                                childViewBg = theAct.get("childViewBg")
                                        .toString();
                                if (bd.getString("childViewBg") == null) {
                                    bd.putString("childViewBg", childViewBg);
                                }
                            }

                            theAct = null;
                        } else {
                            // Logger.d(TAG, "can not get theAct !");
                        }

                    } else {
                        // Logger.d(TAG, "can not find actID");
                    }

                    act = bd.getString("act");

                    if (isInNeedAutoList(act)) {
                        /**
                         * add by Elvis 2011-5-17 start 此段代码留给以后备用
                         * 遇到强制升级，页面将停留在首页 点击任何操作，提示用户需要升级才能使用
                         */
                        if (mustUpdate) {
                            updateWindown();
                            return;
                        }

                        /**
                         * add by Elvis 2011-5-17 end
                         */
                        GlobalVar app = ((GlobalVar) getApplicationContext());
                        if (app.isNeedAuth()) {
                            Logger.i(TAG, act);
                            NetAuthenticate netAuthenticate = NetAuthenticate
                                    .getNetAuthenticate(MainpageActivity.this);
                            if (netAuthenticate != null) {
                                netAuthenticate.setMyIntent(intent);
                                netAuthenticate.mainRun();
                            } else {
                                PviAlertDialog errorDialog = new PviAlertDialog(
                                        MainpageActivity.this);
                                errorDialog.setCanClose(true);
                                errorDialog
                                        .setTitle(getString(R.string.nosimorusim));
                                errorDialog
                                        .setMessage(getString(R.string.noconnectwork));
                                errorDialog.show();
                            }
                            return;
                        }
                    }

                    // actTabIndex = bd.getString("actTabIndex");
                    actTabName = bd.getString("actTabName");
                    insideAct = bd.getString("insideAct");
                    activityID = bd.getString("activityID");
                    haveStatusBar = bd.getString("haveStatusBar");
                    haveTitleBar = bd.getString("haveTitleBar");
                    haveBottomBar = bd.getString("haveBottomBar");
                    haveMenuBar = bd.getString("haveMenuBar");
                    fullScreen = bd.getString("fullScreen");
                    startType = bd.getString("startType");
                    // 如果没有设置，可以默认生成一句话，根据actName “正在进入actName...”
                    String defaultTip = "";
                    if (actName != null && !actName.equals("")) {
                        defaultTip = mRes.getString(R.string.entering) + " "
                                + actName + "...";
                    }
                    // pviapfStatusTip =
                    // bd.getString("pviapfStatusTip")==null?defaultTip:bd.getString("pviapfStatusTip");
                    pviapfStatusTip = bd.getString("pviapfStatusTip");
                    pviapfStatusTipTime = bd.getString("pviapfStatusTipTime");
                    mainTitle = bd.getString("mainTitle") == null
                            || (bd.getString("mainTitle") != null && bd
                                    .getString("mainTitle").equals("")) ? actName
                            : bd.getString("mainTitle");
                    childViewBg = bd.getString("childViewBg") == null ? childViewBg
                            : bd.getString("childViewBg");
                    actLevel = bd.getString("actLevel");

                    /*
                     * //赋值为 默认的背景素材 if(childViewBg==null){ childViewBg =
                     * ""+R.drawable.childactivity_bg; }
                     */

                } else {
                    // Logger.i(TAG, "bundle is null !");
                    return;
                }

                /**
                 * startType = "back" //返回 = "allwaysCreate" //强制create 如果强制
                 * create new：1、启动模式为： 2、（即activity
                 * ID）入栈的act名+唯一id(取当前时间：日期时分秒)；
                 * 其它（包括back），1、启动模式为：Intent.FLAG_ACTIVITY_SINGLE_TOP，2
                 * 入栈act（即activity ID）=action
                 */
                if (startType == null) {
                    startType = "";
                }
                if (activityID == null) {
                    activityID = "";
                }
                if (haveTitleBar == null) {
                    haveTitleBar = "0"; // 默认不显示
                }
                if (mainTitle == null) {
                    mainTitle = "";
                }
                if (actLevel == null) {
                    actLevel = "";
                }

                /*
                 * if (childViewBg == null) { childViewBg = ""; }
                 */

                // debug 把这些值都打印出来
                /*
                 * Logger.d(TAG, "actID = " + actID + " ;act = " + act +
                 * "; actName = " + actName + ";  actLevel = " + actLevel +
                 * ";haveStatusBar = " + haveStatusBar + ";" +
                 * " haveTitleBar = " + haveTitleBar + "; returnActID = " +
                 * returnActID + ";" + "childViewBg = " + childViewBg +
                 * "; activityID = " + activityID + ";" + "startType = " +
                 * startType + "; pviapfStatusTip = " + pviapfStatusTip + ";" +
                 * " pviapfStatusTipTime = " + pviapfStatusTipTime +
                 * "; mainTitle = " + mainTitle + ";");
                 */

                // frame状态入栈

                // 1、存在“上一个状态”，且这次的startType未设置（为back？），把“上一个状态”复制下来，入栈
                // 增加规则：2、如果本activity，再次“启动（重用）”本activity实例的话，应该不入栈！
                // （这种情况应该比较少存在！）

                if ((!startType.equals("back") && lastFrameState[0] != null && !lastFrameState[0]
                        .equals(""))
                        && !(startType.equals("") && lastFrameState[0] != null && lastFrameState[0]
                                .equals(act))) {
                    
                    //2011-7-17 应该增加逻辑，如果和栈内最新的一个相同，则不需入栈
                    
                    if(msActStack.isEmpty()){
                        //Logger.d(TAG, "acvitity stack push:");
                        msActStack.push(lastFrameState.clone());
                        //Logger.d(TAG, "cur stack size:" + msActStack.size());
                    }else{
                        final String[] lastEle = msActStack.lastElement();
                        if(!(lastFrameState[0].equals(lastEle[0])&&lastFrameState[6]==null)
                                || !(lastFrameState[6]!=null&&lastFrameState[0].equals(lastEle[0])&&lastFrameState[6].equals(lastEle[6]))
                        ){
                            msActStack.push(lastFrameState.clone());
/*                            Logger.d(TAG, "acvitity stack push:");
                            Logger.d(TAG, "lastFrameState[0]:" + lastFrameState[0]
                                    + ",[1]:" + lastFrameState[1] + ",[2]:"
                                    + lastFrameState[2] + ",[3]:" + lastFrameState[3]
                                    + ",[4]:" + lastFrameState[4] + ",[5]:"
                                    + lastFrameState[5] + ",[6]:" + lastFrameState[6]
                                    + ",[7]:" + lastFrameState[7] + ",[8]:"
                                    + lastFrameState[8] + ",[9]:" + lastFrameState[9]);*/
                            //Logger.d(TAG, "cur stack size:" + msActStack.size());
                        }else{
                            //Logger.d(TAG,"no need to push into [ActStack]");
                        }
                    }

                }

                // 从此处开始，可以把新打开的act状态保存起来 有些没有的状态记得要置空！否则会保存有之前的脏值
                lastFrameState[9] = startType;

                /*
                 * if (actTabIndex != null && !actTabIndex.equals("")) {
                 * lastFrameState[6] = actTabIndex; } else { lastFrameState[6] =
                 * null; }
                 */

                if (actTabName != null && !actTabName.equals("")) {
                    Logger.i(TAG, "acttabname:" + actTabName);
                    lastFrameState[6] = actTabName;
                } else {
                    Logger.i(TAG, "acttabname is empty");
                    lastFrameState[6] = null;
                }

                if (actID != null && !actID.equals("")) {
                    lastFrameState[7] = actID;
                } else {
                    lastFrameState[7] = null;
                }

                if (actLevel != null && !actLevel.equals("")) {
                    Logger.i(TAG, "actlevel is " + actLevel);
                    lastFrameState[8] = actLevel;
                } else {
                    Logger.i(TAG, "actlevel is empty");
                    lastFrameState[8] = null;
                }

                if (haveBottomBar != null && !haveBottomBar.equals("")) {
                    lastFrameState[10] = haveBottomBar;
                } else {
                    lastFrameState[10] = "1";// 默认显示底栏
                }

                if (haveStatusBar != null && haveStatusBar.equals("0")) {

                    // 2010-11-22 加入逻辑：如果1这个动作不是返回 2
                    // 前一个视图有statusbar，这个视图无statusbar，在子activity载入完成后，再隐藏statusbar
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[1] != null
                                && lastFrameState[1].equals("1")) {//
                            lastHaveStatusBar = true;
                        } else {
                            // 标记一下！
                            lastHaveStatusBar = false;
                        }
                    }

                    lastFrameState[1] = "0";
                } else {

                    // 2010-11-22加入逻辑：如果“这个动作”不是“返回”:如果前一个视图无statusBar，这个视图有statusbar的情形，需要在子activity载入完成后，再显示出来它
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[1] != null
                                && lastFrameState[1].equals("1")) {// 前一个就有statusBar的，直接setVisibilitey（貌似没这个必要哇）！
                            mStatusBar.setVisibility(View.VISIBLE);
                            lastHaveStatusBar = true;
                        } else {
                            // 标记一下！
                            lastHaveStatusBar = false;
                        }
                    }
                    lastFrameState[1] = "1";
                }

                // 标题栏的处理

                // 深圳版UI1 使用此逻辑

                if (haveTitleBar.equals("0")) {

                    // 2010-11-22 加入逻辑：如果1这个动作不是返回 2
                    // 前一个视图有titlebar，这个视图无titlebar，在子activity载入完成后，再隐藏titlebar
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[3] != null
                                && lastFrameState[3].equals("1")) {//
                            lastHaveTitleBar = true;
                        } else {
                            // 标记一下！
                            lastHaveTitleBar = false;
                        }
                    }

                    lastFrameState[3] = "0";
                } else if (haveTitleBar.equals("1")) {

                    // 2010-11-22加入逻辑：如果“这个动作”不是“返回”:如果前一个视图无titlebar，这个视图有titlebar的情形，需要在子activity载入完成后，再显示出来它
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[3] != null
                                && lastFrameState[3].equals("1")) {// 前一个就有titlebar的，直接setVisibilitey（貌似没这个必要哇）！
                            // Logger.i(TAG,"last act have titlebar!");
                            // 2011-5-17去掉。。。mTitleBar.setVisibility(View.VISIBLE);
                            lastHaveTitleBar = true;
                        } else {
                            // 标记一下！
                            lastHaveTitleBar = false;
                        }
                    }
                    lastFrameState[3] = "1";
                }

                // Logger.d(TAG,"lastFrameState[3]:"+lastFrameState[3]);

                // 标题栏文本的处理 可在addchildview之前show出来
                // Logger.i(TAG,"set title:"+mainTitle);
                // mtvTitleBarTitle.setText(mainTitle);

                if (mainTitle != null && !mainTitle.equals("")) {
                    lastFrameState[4] = mainTitle;
                } else {
                    lastFrameState[4] = null;
                }

                // 嵌入区域背景的处理
                /*
                 * if(childViewBg!=null&&!childViewBg.equals("")){
                 * Logger.i(TAG,"childViewBg is updated!:"+childViewBg);
                 * if(childViewBg.equals("-1")){//使用系统背景
                 * mainBlock.setBackgroundResource(R.drawable.childactivity_bg);
                 * 
                 * }else{
                 * mainBlock.setBackgroundResource(Integer.parseInt(childViewBg
                 * ));
                 * 
                 * } lastFrameState[5]=childViewBg; }else{//未设置的背景不发生变化即可？
                 * lastFrameState[5]=null; }
                 */
                lastFrameState[5] = childViewBg;

                /*
                 * if(haveMenuBar!=null&&haveMenuBar.equals("1")){
                 * Log.d(TAG,"haveMenuBar");
                 * mrlMenuBar.setVisibility(View.VISIBLE); }else{
                 * Log.d(TAG,"NOT haveMenuBar");
                 * mrlMenuBar.setVisibility(View.GONE); }
                 */

                if (act == null || act.equals("")) { // 如果没指定act，则回到主界面！
                    act = "com.pvi.ap.reader.activity.MainpageInsideActivity";
                }

                if (startType.equals("allwaysCreate")) {
                    activityID = act
                            + ((new SimpleDateFormat("yyyyMMddHHmmss"))
                                    .format(new Date()));
                } else if (startType.equals("back")) {// 目前仅当startType=back时，会传递activityID值
                    ;
                    // 2011-5-2 back有时候不会携带这个参数，这时应该给它赋值为act
                    if (activityID == null
                            || (activityID != null && activityID.equals(""))) {
                        activityID = act;
                    }
                } else {
                    activityID = act;
                }
                // Logger.i(TAG,"activityID:"+activityID);

                // 判断是否是在TAB内部跳转
                boolean isSameAct = false;
                if (lastFrameState[0] != null && lastFrameState[0].equals(act)) {
                    isSameAct = true;
                }

                if (isSameAct) {
                    bd.putString("isSameAct", "1");
                }

                if (act != null && !act.equals("")) {
                    lastFrameState[0] = act;
                } else {
                    lastFrameState[0] = null;
                }

                if (activityID != null && !activityID.equals("")) {
                    lastFrameState[2] = activityID;
                } else {
                    lastFrameState[2] = null;
                }

                // Intent
                // newIntent;

                // 测试期间，默认不缓存
                if (startType.equals("allwaysCreate")) {
                    Logger.i(TAG, "set : allwaysCreate");
                    newIntent = new Intent(act)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                } else if (startType.equals("reuse")
                        || startType.equals("back")) {// 返回 和 设置了 reuse的
                    // 不新建activity
                    Logger.i(TAG, "set reuse or back : reuse");
                    newIntent = new Intent(act)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                } else if (actTabName != null && !actTabName.equals("")
                        && !startType.equals("back")) {
                    // tab嵌入的,reuse
                    // 不新建activity
                    Logger.i(TAG, "intab : reuse");
                    newIntent = new Intent(act)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                } else {
                    Logger.i(TAG, "default : reuse");
                    newIntent = new Intent(act)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                }
                thisActStartType = startType;

                // 显示提示信息
                if (pviapfStatusTip != null && !pviapfStatusTip.equals("")) {
                    final Intent tempIntent = new Intent(
                            MainpageActivity.SHOW_TIP);
                    final Bundle tempBd = new Bundle();
                    tempBd.putString("pviapfStatusTip", pviapfStatusTip);
                    tempBd
                            .putString("pviapfStatusTipTime",
                                    pviapfStatusTipTime);
                    tempIntent.putExtras(tempBd);
                    sendBroadcast(tempIntent);
                }
                // showTip(pviapfStatusTip, pviapfStatusTipTime);

                newIntent.putExtras(bd);// 把参数放入新的intent

                /*
                 * 2011-5-2 尝试把这个过程移到show出子view之前瞬间 //寻找它的路径 Ma final
                 * ArrayList<HashMap<String, String>> pathInfo =
                 * getActPathInfo(actID); //显示标题栏 showUiPath(pathInfo);
                 */

                // 嵌入子Activity （新开线程）
                // if (insideAct != null &&
                // !insideAct.equals("")&&!startType.equals("back")){//back的情形不需要此逻辑

                // final HashMap<String, Class> classes =
                // PviReaderUI.getClasses();
                if (actTabName != null && !actTabName.equals("")
                        && !startType.equals("back")) {// back的情形不需要此逻辑
                    // Logger.i(TAG,"have actTabName:"+actTabName);
                    // 如果是嵌在TAB里面的，先判断当前被嵌的act是不是和要被打开的被嵌入的childview的act相同，如果相同则不需要执行嵌入
                    if (!isSameAct) {
                        // Logger.i(TAG,"not same Act");
                        new Thread() {
                            public void run() {
                                mHandler.post(addChildView);
                            }
                        }.start();
                    } else {
                        // Logger.i(TAG,"is same Act");

                        // 2011-5-2加入 更新标题栏路径 2011-7-15 去掉
                        /*
                         * //寻找它的路径 Ma final ArrayList<HashMap<String, String>>
                         * pathInfo = getActPathInfo(lastFrameState[7]); //显示在
                         * 标题栏里 showUiPath(pathInfo);
                         */

                        String switchInTab = bd.getString("switchInTab");
                        if (switchInTab == null) {
                            // 应该控制TAB去切换
                            try {
                                final PviTabActivity childActivity = (PviTabActivity) childView1
                                        .getContext();
                                childActivity.setCurrentTab(actTabName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    /*
                     * String switchInTab = bd.getString("switchInTab");
                     * if(switchInTab==null){ //应该控制TAB去切换 new Thread() { public
                     * void run() { mHandler.post(addChildView); } }.start();
                     * }else{//Logger.i(TAG,
                     * "just tell appframe switch in tab, follow history logic"
                     * ); }
                     */

                } else {
                    Logger.i(TAG, "not set actTabName");
                    new Thread() {
                        public void run() {
                            mHandler.post(addChildView);
                        }
                    }.start();
                }
                /*
                 * 不起作用，暂时去掉 Logger.i(TAG,"close loading..."); pd.dismiss();
                 */

                // container.requestFocus();
                // childView1.requestFocus();

                /*
                 * lastAct = act;
                 * 
                 * //保存acitivity group的状态！ mActivityGroupState =
                 * getLocalActivityManager().saveInstanceState();
                 */

            } else if (action.equals(MainpageActivity.SET_TITLE)) {
                Logger.i(TAG, "recv frame broadcast!" + action);
                /*
                 * Bundle bd = intent.getExtras(); try {
                 * mtvTitleBarTitle.setText(bd.getString("title"));
                 * 
                 * } catch (Exception e) {
                 * 
                 * } bd = null;
                 */

                // 设置uipath的最末层文本
                final Bundle bd = intent.getExtras();
                if (bd != null) {
                    final String title = bd.getString("title");
                    if (title != null && !title.equals("")) {

                        // Logger.d(TAG,"dynic title:"+title);

                        // final int nodeCount = mUiPath.getChildCount();

                        try {
                            /*
                             * TextView tv =
                             * (TextView)mUiPath.getChildAt(nodeCount-1);
                             * tv.setText(title); lastFrameState[4] = title;
                             * //2011-5-2 为了让showuipath的时候仍然记着这里设置的标题！
                             */
                            lastFrameState[4] = title;
                            final ArrayList<HashMap<String, String>> pathInfo = getActPathInfo(lastFrameState[7]);
                            // Logger.d(TAG,lastFrameState[7]);
                            // 显示在 标题栏里
                            showUiPath(pathInfo);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (action.equals(MainpageActivity.BACK)) {// 返回上一个activity
                Logger.i(TAG, "recv frame broadcast!" + action);
                /*
                 * try{ Bundle bd = mActivityGroupState.getBundle(lastAct);
                 * 
                 * Iterator<String> it = bd.keySet().iterator();
                 * while(it.hasNext()){ String key = it.next();
                 * 
                 * Bundle bd1 = bd.getBundle(key); Iterator<String> it2 =
                 * bd1.keySet().iterator(); while(it2.hasNext()){ String key2 =
                 * it2.next();
                 * Log.i(TAG,"key:"+key+",value:"+bd1.getString(key2)
                 * );//bd.getString(key) }
                 * 
                 * }
                 * 
                 * }catch(Exception e){}
                 */

                /*
                 * if (!msActStack.isEmpty()) { lastAct = msActStack.pop(); //if
                 * (lastAct == null || lastAct.equals("")) { // lastAct =
                 * "com.pvi.ap.reader.activity.MainpageInsideActivity"; //}
                 * Log.d(TAG,"back to ："+lastAct); View childView1 =
                 * getLocalActivityManager().startActivity( lastAct, new
                 * Intent(lastAct) .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                 * .getDecorView(); FrameLayout frame = (FrameLayout) childView1
                 * .findViewById(android.R.id.content);
                 * frame.setForeground(null); container.removeAllViews();
                 * container.addView(childView1); }
                 */

                // back 逻辑 20101126修改
                // 首先确定，当前处在哪个界面
                // 如果有returnActID的，直接读取配置，载入它；否则返回act栈前一个"上一层"act，未设置level的act默认最低，取10吧

                if (!msActStack.isEmpty()) {

                    String nowActID = "";
                    if (actID != null) {
                        nowActID = actID;
                        // Logger.i(TAG, "get nowActID:" + nowActID);
                    } else {
                        nowActID = getActIDByAct(lastFrameState[0],
                                lastFrameState[6]);
                        // nowActID = getActIDByAct(lastFrameState[0]);
                        /*
                         * if (nowActID != null) { Logger .i( TAG,
                         * "get nowActID:" + nowActID +
                         * ",getActIDByAct(lastFrameState[0],lastFrameState[6])"
                         * ); }
                         */
                    }

                    String backReturnActID = "";
                    if (nowActID != null && !nowActID.equals("")) {

                        // 增加逻辑，如果已在首页，则按BACK键就是提示退出阅读器
                        // 提示 退出整个应用
                        if (nowActID.equals("ACT10000")) {
                            mHandler.sendEmptyMessage(201);
                            return;
                        }

                        HashMap tempHM = actList.get(nowActID);
                        if (tempHM != null) {
                            if (tempHM.get("returnActID") != null) {
                                backReturnActID = tempHM.get("returnActID")
                                        .toString();
                            }
                            if (backReturnActID != null
                                    && !backReturnActID.equals("")) {
                                // Logger.i(TAG, "have backReturnActID !:" +
                                // backReturnActID);
                            }
                        }
                        tempHM = null;
                    }

                    if (backReturnActID != null && !backReturnActID.equals("")) {
                        // Logger.i(TAG, "back to fixed <returnActID>：" +
                        // backReturnActID);

                        HashMap<String, Object> findHM = new HashMap<String, Object>();

                        final int splitPos = backReturnActID.indexOf("_");
                        String backReturnActID_ActID = null;
                        String backReturnActID_ActTabName = null;

                        if (splitPos > 0) {
                            backReturnActID_ActID = backReturnActID.substring(
                                    0, splitPos);
                            backReturnActID_ActTabName = backReturnActID
                                    .substring(splitPos + 1);
                            findHM = findLastActFromStackByActID(backReturnActID_ActID);
                        } else {
                            findHM = findLastActFromStackByActID(backReturnActID);
                        }

                        if (findHM != null) {
                            int p = (Integer) findHM.get("position");
                            // Logger.i(TAG, "find act from actStack at p:" +
                            // p);
                            // msActStack.setSize(p);
                            destroyNouseAct(p);
                        } else {
                            // Logger.i(TAG, "not find act from actStack ");
                        }

                        // Logger.i(TAG, "sendBroadcast start.");
                        Intent backToIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle bd = intent.getExtras();
                        Bundle sndBundle = new Bundle();
                        if (bd != null) {
                            // Logger.i(TAG, "BACK, have data!");
                            sndBundle = (Bundle) bd.clone();
                        } else {
                            // Logger.i(TAG, "BACK, not have data!");
                        }

                        if (findHM != null) {
                            sndBundle.putString("startType", "back");

                            lastFrameState = (String[]) findHM.get("data");
                            // 如果是栈内已有的，取保存的状态取出来
                            sndBundle.putString("act", lastFrameState[0]);
                            sndBundle
                                    .putString("activityID", lastFrameState[2]);
                            sndBundle.putString("haveStatusBar",
                                    lastFrameState[1]);
                            sndBundle.putString("haveTitleBar",
                                    lastFrameState[3]);
                            sndBundle.putString("mainTitle", lastFrameState[4]);
                            sndBundle.putString("childViewBg",
                                    lastFrameState[5]);
                            // sndBundle.putString("actTabIndex",
                            // lastFrameState[6]);
                            sndBundle
                                    .putString("actTabName", lastFrameState[6]);
                            sndBundle.putString("actID", lastFrameState[7]);
                            sndBundle.putString("actLevel", lastFrameState[8]);

                        } else {
                            // 这里可能 是站内没有的新界面！ 所以这里不再是back！
                        }

                        sndBundle.putString("actID", backReturnActID);

                        if (backReturnActID_ActTabName != null) {
                            sndBundle.putString("actTabName",
                                    backReturnActID_ActTabName);
                        }

                        backToIntent.putExtras(sndBundle);
                        sendBroadcast(backToIntent);
                        // Logger.i(TAG, "sendBroadcast over.");

                        /*
                         * Logger.i(TAG,
                         * "BACK(have fixed returnActID) to Act info:" +
                         * lastFrameState[0]);
                         * Logger.i(TAG,",[0]:"+lastFrameState[0]+
                         * ",[1]:"+lastFrameState[1] +",[2]:"+lastFrameState[2]
                         * +",[3]:"+lastFrameState[3] +",[4]:"+lastFrameState[4]
                         * +",[5]:"+lastFrameState[5] +",[6]:"+lastFrameState[6]
                         * +",[7]:"+lastFrameState[7]
                         * +",[8]:"+lastFrameState[8]);
                         */

                        findHM = null;
                        bd = null;
                        sndBundle = null;
                        backToIntent = null;

                    } else {
                        Logger
                                .i(TAG,
                                        "not set <returnActID>, begin to find which act to BACK ...");
                        Logger.i(TAG, ",[0]:" + lastFrameState[0] + ",[1]:"
                                + lastFrameState[1] + ",[2]:"
                                + lastFrameState[2] + ",[3]:"
                                + lastFrameState[3] + ",[4]:"
                                + lastFrameState[4] + ",[5]:"
                                + lastFrameState[5] + ",[6]:"
                                + lastFrameState[6] + ",[7]:"
                                + lastFrameState[7] + ",[8]:"
                                + lastFrameState[8]);
                        String curActLevel = "";
                        if (lastFrameState[8] != null) {
                            curActLevel = lastFrameState[8];
                            Logger.i(TAG, "curActLevel=lastFrameState[8]:"
                                    + curActLevel);
                        } else {
                            String curActID = "";
                            if (lastFrameState[7] != null
                                    && !lastFrameState[7].equals("")) {
                                curActID = lastFrameState[7];
                                Logger.i(TAG, "curActID=lastFrameState[7]:"
                                        + curActID);
                            } else {
                                curActID = getActIDByAct(lastFrameState[0],
                                        lastFrameState[6]);
                                // curActID = getActIDByAct(lastFrameState[0]);
                                Logger.i(TAG,
                                        "curActID=getActIDByAct(lastFrameState[0]):"
                                                + curActID);
                            }

                            if (curActID != null && !curActID.equals("")) {
                                final HashMap hm = actList.get(curActID);
                                if (hm != null) {
                                    final Object obj = hm.get("actLevel");
                                    if (obj != null) {
                                        curActLevel = obj.toString();
                                        Logger.i(TAG,
                                                "read curActLevel from UI pre-set, curActLevel:"
                                                        + curActLevel);
                                    }
                                }
                            }
                        }

                        // 返回“上级”
                        lastFrameState = findLastHighlevelAct(curActLevel);// msActStack.pop();

                        Logger.i(TAG, "BACK to Act info:" + lastFrameState[0]);
                        Logger.i(TAG, ",[0]:" + lastFrameState[0] + ",[1]:"
                                + lastFrameState[1] + ",[2]:"
                                + lastFrameState[2] + ",[3]:"
                                + lastFrameState[3] + ",[4]:"
                                + lastFrameState[4] + ",[5]:"
                                + lastFrameState[5] + ",[6]:"
                                + lastFrameState[6] + ",[7]:"
                                + lastFrameState[7] + ",[8]:"
                                + lastFrameState[8]);

                        Intent backToIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);

                        // Bundle sndBundle = new Bundle();
                        // “返回”上一界面时，也可以传入一些参数？
                        Bundle bd = intent.getExtras();
                        Bundle sndBundle = new Bundle();
                        if (bd != null) {
                            // Logger.i(TAG, "BACK, have data!");
                            sndBundle = (Bundle) bd.clone();
                        } else {
                            // Logger.i(TAG, "BACK, not have data!");
                        }

                        sndBundle.putString("startType", "back");// 标明 这个是返回动作
                        sndBundle.putString("act", lastFrameState[0]);
                        sndBundle.putString("activityID", lastFrameState[2]);
                        sndBundle.putString("haveStatusBar", lastFrameState[1]);
                        sndBundle.putString("haveTitleBar", lastFrameState[3]);
                        sndBundle.putString("mainTitle", lastFrameState[4]);
                        sndBundle.putString("childViewBg", lastFrameState[5]);
                        // sndBundle.putString("actTabIndex",
                        // lastFrameState[6]);
                        sndBundle.putString("actTabName", lastFrameState[6]);
                        sndBundle.putString("actID", lastFrameState[7]);
                        sndBundle.putString("actLevel", lastFrameState[8]);

                        backToIntent.putExtras(sndBundle);
                        sendBroadcast(backToIntent);

                        bd = null;
                        sndBundle = null;
                        backToIntent = null;

                    }

                } else {
                    PviUiUtil.toHomePage(mContext);
                    // 提示 退出整个应用
                    // mHandler.sendEmptyMessage(201);
                }

            } /*
               * 不再使用else if (action.equals(MainpageActivity.SHOW_TIP)) {
               * Logger.i(TAG, "recv frame broadcast!" + action); Bundle bd =
               * intent.getExtras(); if (bd != null) { pviapfStatusTip =
               * bd.getString("pviapfStatusTip"); pviapfStatusTipTime =
               * bd.getString("pviapfStatusTipTime");
               * //Logger.i(TAG,"pviapfStatusTip:"
               * +pviapfStatusTip+",pviapfStatusTipTime:"+pviapfStatusTipTime);
               * 
               * // 显示提示信息 showTip(pviapfStatusTip, pviapfStatusTipTime); }
               * 
               * bd = null; } else if (action.equals(MainpageActivity.HIDE_TIP))
               * { Logger.i(TAG, "recv frame broadcast!" + action); Runnable
               * hideTip = new Runnable() {
               * 
               * @Override public void run() { mtvTip.setText(""); } };
               * mHandler.post(hideTip); }
               */else if (action.equals(MainpageActivity.SERVICE_RESP)) {
                // Logger.i(TAG, "recv frame broadcast!" + action);
                if (intent != null) {
                    Bundle bd = intent.getExtras();
                    String serviceName;
                    String para1 = "";
                    if (bd != null) {
                        serviceName = bd.getString("serviceName");
                        if (serviceName.equals("BackGroundMusicService")) {
                            para1 = bd.getString("para1");
                            if (para1 != null && para1.equals("1")) {
                                mHandler.post(alertNoBgMusic);
                            }
                        }
                    }

                    bd = null;
                }
            } else if (action.equals(MainpageActivity.SHOW_ALERT)) {
                // Logger.i(TAG, "recv frame broadcast!" + action);
                if (intent != null) {
                    Bundle bd = intent.getExtras();
                    String alID = "";
                    if (bd != null) {
                        alID = bd.getString("alID");
                    }
                    bd = null;

                    if (alID != null) {
                        if (alID.equals("loading")) {
                            // Logger.i(TAG, "alID:" + alID);
                            mHandler.sendEmptyMessage(100);

                        } else {
                            // Logger.i(TAG, "no set alID");
                        }
                    }
                }
            } else if (action.equals(MainpageActivity.HIDE_ALERT)) {
                // Logger.i(TAG, "recv frame broadcast!" + action);
                mHandler.sendEmptyMessage(101);
            } else if (action.equals(MainpageActivity.FULLSCREEN_ON)) {
                // “全屏”
                mStatusBar.setVisibility(View.GONE);
                mPviTitleBar.setVisibility(View.GONE);
                mBottomBar.setVisibility(View.GONE);
            } else if (action.equals(MainpageActivity.FULLSCREEN_OFF)) {
                // 取消“全屏”
                if (lastFrameState[1] != null && lastFrameState[1].equals("1")) {
                    mStatusBar.setVisibility(View.VISIBLE);
                }
                if (lastFrameState[3] != null && lastFrameState[3].equals("1")) {
                    mPviTitleBar.setVisibility(View.VISIBLE);
                }
                if (lastFrameState[10] != null
                        && lastFrameState[10].equals("1")) {
                    mBottomBar.setVisibility(View.VISIBLE);
                }

            } else if (action.equals(MainpageActivity.SHOW_ME)) {
                Logger.i(TAG, "recv frame broadcast!" + action);

                // 更新actStack中的状态：遍历，根据activityId找到相应framestate，保存到【9】中
                /*
                 * String activityId = ""; final Intent it = getIntent(); if (it
                 * != null) { Bundle bd = it.getExtras(); if (bd != null) {
                 * activityId = bd.getString("activityID"); Logger.i(TAG,
                 * "this.activityId:"+activityId); } else { Logger.i(TAG,
                 * "getExtras() is null"); } } else { Logger.i(TAG,
                 * "getIntent() is null"); }
                 * 
                 * updateActShown(activityId);
                 */

                if (intent != null) {
                    Bundle bd = intent.getExtras();
                    String sender = "";
                    if (bd != null) {
                        sender = bd.getString("sender");
                        // Logger.d(TAG, "sender:" + sender);
                    }
                    String actTabName1 = "";
                    if (bd != null) {
                        actTabName1 = bd.getString("actTabName");
                        // Logger.d(TAG, "actTabName1:" + actTabName1);
                    }

                    // 对于tab内嵌的界面
                    if (lastFrameState[6] != null
                            && !lastFrameState[6].equals("")) {
                        Logger.d(TAG, "inside TAB");

                        // 不同的tabactivity和在同一个tabactivity内的跳转，处理逻辑不同
                        /*
                         * 同一个内：发送框架消息start_activity，并携带“要求show出子界面”的参数；或者通过view的层次关系
                         * ，取得tabhost，调用其setcurrenttab方法 不在同一个内：执行嵌入
                         */

                        /*
                         * Logger.i(TAG,"acttabname:"+lastFrameState[6]); final
                         * HashMap<String,Class> classes
                         * =PviReaderUI.getClasses();
                         * 
                         * if(classes.get(lastFrameState[6])!=null&&classes.get(
                         * lastFrameState[6]).getName().equals(sender)){
                         * Logger.i
                         * (TAG,"this inside act is :"+classes.get(lastFrameState
                         * [6]).getName()); showChildView(); }else{
                         * Logger.d(TAG,"app error:no this acttab"); }
                         */

                        /*
                         * 2011-7-12暂注释 if
                         * (lastFrameState[6].equals(actTabName1)) {
                         * //start的等待showme的acttabname与此showme广播的acttabname相同
                         * ，addview其tabactivity Logger.d(TAG, "actTabName1:" +
                         * actTabName1
                         * +", equals lastFrameState[6], so, to run showChildView()"
                         * ); showChildView(); }else{
                         */
                        // 判断下其TabActivity是否的确已经显示出来了，否则还是需要show出的（比如点了我的音乐，然后还没显示出来，就快速点击本地书库）

                        try {
                            if (!container.getChildAt(0).getContext()
                                    .toString().contains(lastFrameState[0])) {
                                Logger.d(TAG, "to run showChildView()");
                                showChildView();
                            } else {
                                Logger.d(TAG, "noneed to run showChildView()");

                                // 2011-7-15 需要更新一下标题栏！
                                // 寻找它的路径 Ma
                                final ArrayList<HashMap<String, String>> pathInfo = getActPathInfo(lastFrameState[7]);
                                // 显示在 标题栏里
                                showUiPath(pathInfo);
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        // }

                    } else if (lastFrameState[0].equals(sender)
                            || (sender.indexOf("$") > -1 && lastFrameState[0]
                                    .equals(sender.substring(0, sender
                                            .indexOf("$"))))) {
                        /*Logger
                                .d(
                                        TAG,
                                        "111 act(lastFrameState[0]):"
                                                + lastFrameState[0]
                                                + ", 'act' equals 'sender', to run showChildView()");
                        */
                        showChildView();
                    } else {
                        Logger.d(TAG, "222 act(lastFrameState[0]):"
                                + lastFrameState[0] + "  ,'sender':" + sender
                                + "  , not found start act or acttabname");

                        // 如果sender是被嵌入tab的activity，且判断它属于某个tabactivity，则也要执行showChildView
                        final HashMap<String, String[]> list = PviReaderUI
                                .buildBlockNameList(mContext);
                        final String[] tabnames = list.get(lastFrameState[0]);
                        boolean isInside = false;
                        if (tabnames != null) {
                            for (int i = 0; i < tabnames.length; i++) {
                                // Logger.d(TAG,tabnames[i]);
                                if (tabnames[i] != null
                                        && tabnames[i].equals(actTabName1)) {
                                    isInside = true;
                                    break;
                                }
                            }
                        } else {
                            Logger.d(TAG, "tabnames is empty");
                        }

                        if (isInside) {
                            Logger
                                    .d(TAG,
                                            "sender是被嵌入tab的activity，且判断它属于某个tabactivity，则也要执行showChildView");
                            showChildView();
                        } else {
                            Logger.d(TAG, "orther case");
                        }

                    }

                }

            }

        }
    };

    /**
     * 一个静态方法供使用，发送框架消息
     */
    public static void sendFrameBroadcast(Context context, String action) {
        String name = context.getClass().getName();
        Logger.i(TAG, "sender name:" + name);
        Intent intent = new Intent(action);
        // intent.setComponent(new ComponentName("com.pvi.ap.reader",
        // "com.pvi.ap.reader.activity.MainpageActivity"));
        intent.putExtra("sender", name);
        context.sendBroadcast(intent);
    }

    private Runnable addChildView = new Runnable() {
        @Override
        public void run() {
            Logger.i(TAG, "Runnable addChildView run(...");

            // 释放前一个被嵌入activity中的view中的背景等
            if (childView1 != null) {
                try {
                    if (childView1.getBackground() != null) {
                        childView1.getBackground().setCallback(null);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            try {
                // VMRuntime.getRuntime().gcSoftReferences();
                // childView1 = null;

                // childView1 =
                // getLocalActivityManager().startActivity(lastFrameState[2],
                // newIntent).getDecorView();

                // 对于设置了allwaysCreate的，创建新的之前，先杀掉旧有的重复的
                if ("allwaysCreate".equals(lastFrameState[9])) {
                    destroySameAct2(lastFrameState[0]);
                }
                Logger.i(TAG, "try mLam.startActivity(lastFrameState[2]:"
                        + lastFrameState[2]);
                childView1 = mLam.startActivity(lastFrameState[2], newIntent)
                        .getDecorView();

                if (childView1 != null) {
                    childView1.setId(11100);
                }

                // 对于设置了allwaysCreate的，建立了新的以后，要保存起来id
                if ("allwaysCreate".equals(lastFrameState[9])) {
                    mAcAct.add(lastFrameState[2]);
                }

            } catch (Exception e) {
                Logger
                        .i(TAG, "start child activity fail!  e:"
                                + e.getMessage());
                e.printStackTrace();
                System.gc();
                System.runFinalization();
                System.gc();
                mLam.removeAllActivities();
                if (msActStack != null && !msActStack.isEmpty()) {
                    msActStack.pop();// 移出已入act栈的内容，保持当前被嵌入视图不变化
                }
                /*
                 * System.gc(); backtoHomeAcvitity();
                 */

                // 返回
                return;
            }

            // 主界面不需要异步显示
            if (lastFrameState[2] != null
                    && lastFrameState[2]
                            .equals("com.pvi.ap.reader.activity.MainpageInsideActivity")) {
                // Logger.d(TAG,"这个界面直接显示出来  目前仅 首页");
                showChildView();
            } else {
                // Logger.d(TAG,"界面的acitivyt已经运行，等待SHOW_ME广播");
            }

            // childView1 = null;

            // 隐藏载入中的提示框
            // mHandler.sendEmptyMessage(101);
            // pd.dismiss();

            // displayAvailMemory();
        }
    };

    // private int flashNum = 0 ;

    /*
     * 真正的show出子界面
     */
    private void showChildView() {
        // Logger.d(TAG, "showChildView" );

        if (childView1 == null) {
            Logger.e(TAG, "system err:childView1 is null");
            return;
        }

        FrameLayout frame = null;
        View v = null;
        if (childView1 != null) {
            v = childView1.findViewById(android.R.id.content);
        } else {
            Logger.e(TAG, "system err:childView1 is null");
            return;
        }
        if (v == null) {
            Logger
                    .e(TAG,
                            "system err: childView1's child 'android.R.id.content' is null");
            return;
        }

        // gc16 刷全屏一下
        if (((GlobalVar) getApplication()).deviceType == 1) {
            Logger.i(TAG, "PviAppFrame FULL FLASH Window:GC16 FULL");
//            getWindow().getDecorView().invalidate(
//                    View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//                            | View.EINK_UPDATE_MODE_FULL);
        }

        if (v instanceof FrameLayout) {
            frame = (FrameLayout) v;
            frame.setForeground(null);
            frame = null;
        }

        // 整个框架的背景
        if (lastFrameState[5] != null && !lastFrameState[5].equals("")) {
            try {
                // Logger.i(TAG,"change childView bg:"+lastFrameState[5]);
                childView1.setBackgroundResource(Integer
                        .parseInt(lastFrameState[5]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Logger.i(TAG,"childView not set , set it to transparent");
            try {
                childView1.setBackgroundResource(0);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // 最后加入子视图
        // Logger.i(TAG,"childView added!");
        if (lastFrameState[1] != null && lastFrameState[1].equals("0")
                && lastFrameState[3] != null && lastFrameState[3].equals("0")) {
            // 全屏
            Display display = getWindowManager().getDefaultDisplay();
            int screenHeight = display.getHeight();
            int screenWidth = display.getWidth();
            ViewGroup.LayoutParams cvlp = new ViewGroup.LayoutParams(
                    screenWidth, screenHeight);
            try {
                childView1.setLayoutParams(cvlp);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            cvlp = null;
            display = null;
        }

        try {

            container.removeAllViewsInLayout();
            container.removeAllViews();

            // 标题栏里的路径，应该在这里显示？

            if (lastFrameState[3] != null && lastFrameState[3].equals("1")) {// 如果设置了显示标题栏
                // Logger.d(TAG,"设置了显示标题栏:"+lastFrameState[3]+",mailTitle:"+lastFrameState[4]);
                // 寻找它的路径 Ma
                final ArrayList<HashMap<String, String>> pathInfo = getActPathInfo(lastFrameState[7]);
                // 显示在 标题栏里
                showUiPath(pathInfo);
            } else {
                // Logger.d(TAG,"不显示标题栏！");
            }

            // HERE： show出子界面
            container.addView(childView1);

            // 对于allwayCreate的，杀掉之前重复的实例 2011-5-23 逻辑移到activity创建时
            /*
             * Logger.d(TAG,"act instance:"+lastFrameState[2]+",startType:"+lastFrameState
             * [9]); if(lastFrameState[0]!=null && lastFrameState[2]!=null &&
             * lastFrameState[9]!=null &&
             * "allwaysCreate".equals(lastFrameState[9])){
             * Logger.d(TAG,"kill same activity instances,act:"
             * +lastFrameState[0]); destroySameAct(lastFrameState[0]); }else{
             * Logger.d(TAG,"no need to kill same activity instances"); }
             */

            if (((GlobalVar) getApplication()).deviceType == 1) {
                // getWindow().getDecorView().setUpdateMode(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_DU |
                // View.EINK_UPDATE_MODE_FULL);
                // findViewById(R.id.MainFrame).setUpdateMode(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_DU |
                // View.EINK_UPDATE_MODE_FULL);
                // mStatusBar.setUpdateMode(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 |
                // View.EINK_UPDATE_MODE_FULL);
                // mStatusBar.invalidate();

                /*
                 * flashNum ++ ; if(flashNum >= 5){ flashNum = 0 ;
                 * getWindow().getDecorView
                 * ().setUpdateMode(View.EINK_AUTO_MODE_REGIONAL|
                 * View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 |
                 * View.EINK_UPDATE_MODE_FULL); }else{
                 * container.setUpdateMode(View.EINK_AUTO_MODE_REGIONAL|
                 * View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 |
                 * View.EINK_UPDATE_MODE_FULL); }
                 */

                // container.invalidate(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16|
                // View.EINK_UPDATE_MODE_FULL);

                // findViewById(R.id.MainFrame).setUpdateMode(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 |
                // View.EINK_UPDATE_MODE_FULL);
                // findViewById(R.id.MainFrame).invalidate(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 |
                // View.EINK_UPDATE_MODE_FULL);

                // getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_DU |
                // View.EINK_UPDATE_MODE_FULL);
                // findViewById(R.id.MainFrame).invalidate(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_DU |
                // View.EINK_UPDATE_MODE_FULL);
                // container.invalidate(View.EINK_AUTO_MODE_REGIONAL|
                // View.EINK_WAIT_MODE_WAIT | View.EINK_UPDATE_MODE_FULL);

                // childView1.setUpdateMode(View.EINK_UPDATE_MODE_FULL|View.EINK_WAVEFORM_MODE_GC16|View.EINK_WAIT_MODE_NOWAIT);
                // findViewById(R.id.MainFrame).setUpdateMode(View.EINK_WAIT_MODE_WAIT
                // | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
                // if(findViewById(R.id.MainFrame) instanceof ViewGroup){
                // for(View v:(ViewGroup)findViewById(R.id.MainFrame).)
                // Logger.i(TAG, )
                // }

            }

            // 隐藏主导航栏
            if (mPviTitleBar.navIsOpen) {
                hideNav();                
            }
            
            // childView1.destroyDrawingCache();

            // Logger.i(TAG, "new childView loaded");

        } catch (Exception e) {
            Logger.e(TAG, "remove old childView or add new childView fail!");
            e.printStackTrace();
            return;
        }

        // container.getChildAt(0).getWindowToken();
        // getLocalActivityManager().getActivity("first").finish();

        // 要求全屏载入的情形
        /*
         * if (fullScreen != null && fullScreen.equals("1")) { Logger.i(TAG,
         * "fullScreen:" + fullScreen); RelativeLayout ll = new
         * RelativeLayout(getBaseContext()); RelativeLayout.LayoutParams rll =
         * new RelativeLayout.LayoutParams( 100, 100);
         * rll.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
         * ll.setBackgroundColor(Color.GRAY); ll.addView(childView1, rll);
         * 
         * RelativeLayout.LayoutParams rll2 = new RelativeLayout.LayoutParams(
         * 300, 300); rll2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
         * container.addView(ll, rll2); container.bringChildToFront(ll);
         * 
         * } else { // container.removeAllViews(); //
         * container.addView(childView1);
         * 
         * }
         */

        /*
         * // 如果“这个动作”不是“返回”，且上一个视图无状态栏：在这里show出“状态栏” if ((thisActStartType !=
         * null && !thisActStartType.equals("back")) && !lastHaveStatusBar) { if
         * (lastFrameState[1] != null && lastFrameState[1].equals("1")) {
         * mStatusBar.setVisibility(View.VISIBLE); } } if ((thisActStartType !=
         * null && !thisActStartType.equals("back")) && lastHaveStatusBar) { if
         * (lastFrameState[1] != null && lastFrameState[1].equals("0")) {
         * mStatusBar.setVisibility(View.GONE); }
         * 
         * } if ((thisActStartType != null && thisActStartType.equals("back"))){
         * //返回的时候， }
         */

        // 状态栏
        if (lastFrameState[1] != null && lastFrameState[1].equals("0")) {
            // Logger.i(TAG,"hide PviAppFrame status bar");
            mStatusBar.setVisibility(View.GONE);
        } else {
            // Logger.i(TAG,"show PviAppFrame status bar");
            mStatusBar.setVisibility(View.VISIBLE);
        }

        // 底栏
        int newVisible = View.VISIBLE;
        if (lastFrameState[10] != null && lastFrameState[10].equals("0")) {
            newVisible = View.GONE;
        }
        if (newVisible != mBottomBar.getVisibility()) { // 仅当显隐状态设置与现状态不同时，才需要改变
            mBottomBar.setVisibility(newVisible);
        }
        /*---底栏中各uiitem的显/隐状态处理
         * 逻辑：根据子activity设置的相关属性，如showPager,showChaper等，控制相关控件的显示与否         *      
         *      只在被嵌activity是pviactivity时才处理，对于PviTabActivity，在它的内嵌SHOW——ME消息处理逻辑中做类似处理；
         */
        final Activity childActivity = (Activity) childView1.getContext();
        if (childActivity instanceof PviActivity) {
            final PviActivity pa = (PviActivity) childActivity;
            // 设置底部工具栏

            if (pa.showPager) {
                mBottomBar.setPageable(pa);
                mBottomBar.actionShowPager();
                mBottomBar.actionUpdatePagerinfo();
            }else{
                mBottomBar.actionHidePager();
            }

            if (pa.showChaper) {
                mBottomBar.setChapable(pa);
                mBottomBar.actionShowChaper();
            }else{
                mBottomBar.actionHideChaper();
            }

        }

        // 标题栏 //20100105增加逻辑：对于深圳版UI状态栏底部素材的变化处理:有/无标题栏时，状态栏的底部线条是不同的
        if (lastFrameState[3] != null && lastFrameState[3].equals("0")) {
            // Logger.i(TAG,"hide PviAppFrame title bar");

            mPviTitleBar.setVisibility(View.GONE);
            /*
             * 2011-3-18去掉此逻辑
             * statusBarBottomBg=R.drawable.statusbar_bottom_2_ui2;
             * mHandler.sendEmptyMessage(UI_UPDATE_CHANGE_STATUSBAR_BOTTOM);
             */

        } else {
            // Logger.i(TAG,"show PviAppFrame title bar");

            statusBarBottomBg = R.drawable.statusbar_bottom_ui1;
            /*
             * 2011-3-18去掉此逻辑
             * mHandler.sendEmptyMessage(UI_UPDATE_CHANGE_STATUSBAR_BOTTOM);
             */
            mPviTitleBar.setVisibility(View.VISIBLE);

        }

        // 标题栏文本的处理 在addchildview之show出来 这个已经废弃
        /*
         * if (lastFrameState[4] != null && !lastFrameState[4].equals("")) { //
         * Logger.i(TAG,"set title:"+lastFrameState[4]);
         * mtvTitleBarTitle.setText(lastFrameState[4]); }
         */

        if (((GlobalVar) getApplication()).deviceType == 1) {
            // 尝试在这里刷一下全屏幕

            /*
             * mHandler.post(new Runnable() { public void run() {
             * Logger.d(TAG,"getWindow().getDecorView().invalidate( ...");
             * getWindow().getDecorView().invalidate();
             * getWindow().getDecorView(
             * ).invalidate(View.EINK_DITHER_MODE_DITHER
             * |View.EINK_AUTO_MODE_REGIONAL| View.EINK_WAIT_MODE_NOWAIT |
             * View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL); } });
             */

            // 测试tabactivity 失败
            /*
             * if(!lastFrameState[0].contains("ResCenter")){
             * Logger.d(TAG,"invalidate fullscreen");
             * getWindow().getDecorView().
             * invalidate(View.EINK_COMBINE_MODE_COMBINE |
             * View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 |
             * View.EINK_UPDATE_MODE_FULL);
             * 
             * }else{ Logger.d(TAG,"not rescenter,noneed fullscreen update"); }
             */

            // getWindow().getDecorView().getRootView().invalidate(View.EINK_WAIT_MODE_WAIT
            // | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);

            // getWindow().getDecorView().getRootView().setUpdateMode(View.EINK_WAIT_MODE_WAIT
            // | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);

            // 非tab的刷全屏放在这里
            /*
             * if(!lastFrameState[0].contains("ResCenter")){
             * Logger.d(TAG,"is not tabactivity");
             * getWindow().getDecorView().invalidate
             * (View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16|
             * View.EINK_UPDATE_MODE_FULL); }
             */
        }

    }

    private void displayAvailMemory() {
        StringBuffer sbf = new StringBuffer();
        final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        // 取全局内存分配信息
        sbf.append(",Debug.getGlobalAllocCount():").append(
                Debug.getGlobalAllocCount());
        sbf.append(",getGlobalAllocSize():").append(Debug.getGlobalAllocSize());
        sbf.append(",getGlobalExternalAllocCount:").append(
                Debug.getGlobalExternalAllocCount());
        sbf.append(",Debug.getGlobalExternalAllocSize():").append(
                Debug.getGlobalExternalAllocSize());
        sbf.append(",getGlobalExternalFreedCount():").append(
                Debug.getGlobalExternalFreedCount());
        sbf.append(",Debug.getGlobalExternalFreedSize():").append(
                Debug.getGlobalExternalFreedSize());
        sbf.append(",Debug.getGlobalFreedCount():").append(
                Debug.getGlobalFreedCount());
        sbf.append(",Debug.getGlobalFreedSize():").append(
                Debug.getGlobalFreedSize());
        sbf.append(",getGlobalGcInvocationCount():").append(
                Debug.getGlobalGcInvocationCount());

        sbf.append("\n,Debug.getNativeHeapAllocatedSize():").append(
                Debug.getNativeHeapAllocatedSize());
        sbf.append(",Debug.getNativeHeapFreeSize():").append(
                Debug.getNativeHeapFreeSize());
        sbf.append(",Debug.getNativeHeapSize():").append(
                Debug.getNativeHeapSize());

        sbf.append("\n,Debug.getThreadAllocCount():").append(
                Debug.getThreadAllocCount());
        sbf.append(",Debug.getThreadAllocSize():").append(
                Debug.getThreadAllocSize());
        sbf.append(",Debug.getThreadExternalAllocCount():").append(
                Debug.getThreadExternalAllocCount());
        sbf.append(",Debug.getThreadExternalAllocSize(():").append(
                Debug.getThreadExternalAllocSize());
        sbf.append(",Debug.getThreadGcInvocationCount():").append(
                Debug.getThreadGcInvocationCount());

        // 取当前进程的内存分配信息

        // 取的当前进程pid
        final int curPid = android.os.Process.myPid();

        // 取当前进程的内存占用信息
        int[] pids = { curPid };
        android.os.Debug.MemoryInfo curPMemInfo = activityManager
                .getProcessMemoryInfo(pids)[0];
        sbf.append("\n,dalvikPrivateDirty:").append(
                curPMemInfo.dalvikPrivateDirty);
        sbf.append(",dalvikPss:").append(curPMemInfo.dalvikPss);
        sbf.append(",dalvikSharedDirty:").append(curPMemInfo.dalvikSharedDirty);
        sbf.append(",nativePrivateDirty:").append(
                curPMemInfo.nativePrivateDirty);
        sbf.append(",nativePss:").append(curPMemInfo.nativePss);
        sbf.append(",nativeSharedDirty:").append(curPMemInfo.nativeSharedDirty);
        sbf.append(",otherPrivateDirty:").append(curPMemInfo.otherPrivateDirty);
        sbf.append(",otherPss:").append(curPMemInfo.otherPss);
        sbf.append(",otherSharedDirty:").append(curPMemInfo.otherSharedDirty);
        sbf.append(",curPMemInfo.toString():").append(curPMemInfo.toString());

        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();

        activityManager.getMemoryInfo(outInfo);

        sbf.append("\n1:").append(outInfo.availMem >> 10).append("k");
        sbf.append(",2:").append(activityManager.getMemoryClass());
        sbf.append(",3:").append(outInfo.threshold >> 10).append("k");

        Logger.i(TAG, sbf.toString());

    }

    private class H extends Handler {

        /*
         * (non-Javadoc)
         * 
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case 100:// 弹出 载入中 提示框
                new Thread() {
                    public void run() {
                        mHandler.post(alertLoading);
                    }
                }.start();
                ;
                break;
            case 101:// 隐藏 载入中 提示框
                new Thread() {
                    public void run() {
                        mHandler.post(hideAlertDialog);
                    }
                }.start();
                break;
            case 201:// 退出阅读器的确认框
                // 取消退出APP
                /*
                 * new Thread() { public void run() {
                 * mHandler.post(confirmExit); } }.start();
                 */
                break;
            case UI_UPDATE_CHANGE_STATUSBAR_BOTTOM:
                mStatusBarBottom.setImageResource(statusBarBottomBg);
                break;

            default:
                ;
            }

            super.handleMessage(msg);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.ActivityGroup#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(mIntentReceiver);
        unregisterReceiver(updateReceiver);
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Logger.d(TAG,"frame onKeyUp: keycode:"+keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU) {

            // 都发送给子Activity处理返回事件，因为有的activity需要在返回之前进行一些操作

            final View cv = container.getChildAt(0);
            if (cv != null) {
                // Logger.d(TAG,"transfor keyEvent to childView:"+event);
                cv.dispatchKeyEvent(event);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        //Logger.d(TAG,"dispatchKeyEvent(KeyEvent event): keycode:"+event.getKeyCode()+", action:"+event.getAction());

        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            // 拒绝连续down
            Logger.d(TAG, "refuse continue key down");
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN
                || event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {// 处理音量+
                // 不允许连续改变
                if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                    // Logger.i(TAG, "KeyEvent.ACTION_MULTIPLE");
                    return true;
                }
                // Logger.i(TAG, "KeyEvent.KEYCODE_VOLUME_UP");
                AudioManager audioManager = (AudioManager) getApplicationContext()
                        .getSystemService(Context.AUDIO_SERVICE);
                int curSysVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_SYSTEM);
                final int maxSysVolume = audioManager
                        .getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
                if (curSysVolume < maxSysVolume) {
                    curSysVolume++;
                    audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                            curSysVolume, 0);

                    final int maxMusicVolume = audioManager
                            .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int newMusicVolume = 0;
                    int m = (maxMusicVolume + 1) / (maxSysVolume + 1);
                    if (curSysVolume == maxSysVolume) {
                        newMusicVolume = maxMusicVolume;
                    } else {
                        newMusicVolume = curSysVolume * m;
                    }

                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            newMusicVolume, 0);
                    sendBroadcast(new Intent(MainpageActivity.VOLUME_CHANGED));
                }
                audioManager = null;
                return true;

            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {// 处理音量-
                // 不允许连续改变
                if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                    // Logger.i(TAG, "KeyEvent.ACTION_MULTIPLE");
                    return true;
                }
                // Logger.i(TAG, "KeyEvent.KEYCODE_VOLUME_DOWN");
                AudioManager audioManager = (AudioManager) getApplicationContext()
                        .getSystemService(Context.AUDIO_SERVICE);
                int curSysVolume = audioManager
                        .getStreamVolume(AudioManager.STREAM_SYSTEM);
                if (curSysVolume > 0) {
                    curSysVolume--;
                    // Logger.i(TAG, "curSysVolume:" + curSysVolume);
                    audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
                            curSysVolume, 0);
                    sendBroadcast(new Intent(MainpageActivity.VOLUME_CHANGED));
                }
                audioManager = null;
                return true;
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_CAMERA
                    && event.getAction() == KeyEvent.ACTION_DOWN) {// 背景音乐键
                // Logger.d(TAG,"Key pressed BGMusic ");
                if (PviServiceUtil.IsBackgroundMusicOn(mContext)) {
                    final Intent myintent = new Intent(
                            "com.pvi.ap.reader.service.BackGroundMusicService");
                    // Logger.d(TAG,"to stopService(...");
                    stopService(myintent);
                } else {
                    final Intent myintent = new Intent(
                            "com.pvi.ap.reader.service.BackGroundMusicService");
                    // Logger.d(TAG,"to startService(...");
                    startService(myintent);
                }
                return true;
            }else if (event.getKeyCode() == KeyEvent.KEYCODE_HOME){
                PviUiUtil.toHomePage(mContext);
                return true;
            }
        }

        final boolean handled = super.dispatchKeyEvent(event);

        // 当子界面焦点移动到最下面时，继续按向下，则焦点移到 底栏 上
        if (!handled
                && (event.getAction() == KeyEvent.ACTION_DOWN)
                && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN)
                && (childView1 != null)
                && (childView1.hasFocus())
                && (childView1.findFocus().focusSearch(View.FOCUS_DOWN) == null)) {

            mBottomBar.requestFocus();

            return true;
        }
        return handled;

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#finishActivityFromChild(android.app.Activity,
     * int)
     */
    @Override
    public void finishActivityFromChild(Activity child, int requestCode) {
        // TODO Auto-generated method stub
        // Logger.i(TAG, "finishActivityFromChild");
        // super.finishActivityFromChild(child, requestCode);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#finishFromChild(android.app.Activity)
     */
    @Override
    public void finishFromChild(Activity child) {
        // TODO Auto-generated method stub
        // Logger.i(TAG, "finishFromChild");
        // 暂时注释掉这行，忘了这是什么情况下加了这个逻辑，
        // sendBroadcast(new Intent(MainpageActivity.BACK));

        // super.finishFromChild(child);
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub\
        Logger.i(TAG, "MainFrame:onActivityResult: requestCode" + requestCode
                + ",resultCode:" + resultCode);

        super.onActivityResult(requestCode, resultCode, data);

    }

    /*
     * private Runnable showLoading = new Runnable() {
     * 
     * @Override public void run() { pd.show(); } };
     * 
     * private Runnable hideLoading = new Runnable() {
     * 
     * @Override public void run() { pd.dismiss(); } };
     */

    // 框架提示框 未设置背景音乐
    private Runnable alertNoBgMusic = new Runnable() {
        @Override
        public void run() {
            pd = new PviAlertDialog(mContext);
            pd.setTitle("温馨提示");
            pd.setMessage("未设置背景音乐，请到“我的音乐”中设置。");
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 跳转到 背景音乐库设置
                            Intent tmpIntent = new Intent(
                                    MainpageActivity.START_ACTIVITY);
                            Bundle bundleToSend = new Bundle();
                            bundleToSend.putString("actID", "ACT13200");
                            tmpIntent.putExtras(bundleToSend);
                            sendBroadcast(tmpIntent);
                            tmpIntent = null;
                            bundleToSend = null;
                            return;
                        }
                    });
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "返回",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 跳转到 背景音乐库设置
                            return;
                        }
                    });
            pd.show();
        }
    };

    // 框架提示框
    private Runnable alertLoading = new Runnable() {
        @Override
        public void run() {
            pd = new PviAlertDialog(MainpageActivity.this);
            pd.setTitle("温馨提示");
            pd.setMessage("数据加载中，请稍候...");
            pd.setHaveProgressBar(true);
            pd.show();
        }
    };

    private Runnable hideAlertDialog = new Runnable() {
        @Override
        public void run() {
            if (pd != null) {
                pd.dismiss();
            }
        }
    };

    private Runnable fouceView = new Runnable() {
        @Override
        public void run() {
            // Logger.d(TAG,"tag:"+curFocusView.toString()+" try requestFocus()");
            curFocusView.requestFocus();
        }
    };

    // 确认要退出G3阅读器？
    private Runnable confirmExit = new Runnable() {
        @Override
        public void run() {
            pd = new PviAlertDialog(MainpageActivity.this);
            pd.setTitle("温馨提示");
            pd.setMessage("您确认要退出PVI-G3阅读器吗？");
            // CharSequence sureText;// = new CharSequence();
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "确 定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method
                            // stub
                            // finish();
                            ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                            manager.restartPackage(getPackageName());
                            manager = null;
                        }
                    });

            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取 消",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method
                            // stub
                            return;
                        }
                    });
            pd.show();
        }
    };

    /**
     * 取ActID。有些activity是嵌在TAB里面的，这种需要两个参数来确定是哪个界面
     * 
     * @param act
     * @param actTabName
     * @return
     */
    private String getActIDByAct(String act, String actTabName) {

        if (act == null && actTabName == null) {
            return null;
        }

        /*
         * Logger.i(TAG, "getActIDByAct(String act:" + act +
         * ", String actTabIndex:" + actTabIndex + ")");
         */
        if (actTabName != null && !actTabName.equals("")) {
            String actID = "";
            Iterator iter = actList.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                try {
                    HashMap tempHM = (HashMap) val;
                    if (tempHM != null) {
                        if (tempHM.get("act") != null
                                && tempHM.get("act").equals(act)
                                && tempHM.get("actTabName") != null
                                && tempHM.get("actTabName").equals(actTabName)) {
                            actID = tempHM.get("actID").toString();
                            return actID;
                        }
                    }
                } catch (Exception e) {
                    return actID;
                }
            }
            return actID;

        } else {
            return getActIDByAct(act);
        }
    }

    private String getActIDByAct(String act) {
        // Logger.i(TAG, "getActIDByAct(String act:" + act + ")");

        String actID = null;

        Iterator iter = actList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            try {
                HashMap tempHM = (HashMap) val;
                if (tempHM != null) {
                    if (tempHM.get("act") != null
                            && tempHM.get("act").equals(act)
                            && tempHM.get("actTabName") == null) {
                        actID = tempHM.get("actID").toString();
                        // Logger.i(TAG, "find actID:" + actID);
                        return actID;
                    }
                }
            } catch (Exception e) {
                // Logger.e(TAG, "err: find actID," + e.getMessage());
                return actID;
            }
        }

        // Logger.i(TAG, "not find actID!");
        return actID;
    }

    // 从act栈中根据给定ActId查找act：参数ActID
    private HashMap<String, Object> findLastActFromStackByActID(String actID) {
        /*
         * Logger.i(TAG, "start find actID:" + actID +
         * "...findLastActFromStackByActID");
         */
        HashMap<String, Object> result = new HashMap<String, Object>();
        Iterator iter = msActStack.iterator();
        int position = -1; // 保存最后一个找到的位置
        String[] lastFrameState = new String[actStackSize];// 保存找到的一条数据
        int i = 0;
        while (iter.hasNext()) {

            String[] tempState = (String[]) iter.next();

            if (tempState[0] != null && tempState[7] != null) {
                /*
                 * Logger.i(TAG,"act:" + tempState[0]+"actID:" + tempState[7]);
                 */
            }

            if (tempState[7] != null && !tempState[7].equals("")) {// 有保存actid的，直接判断
                if (actID.equals(tempState[7])) {
                    position = i;
                    lastFrameState = tempState;
                    // Logger.i(TAG,
                    // "[actID.equals(tempState[7])] find at postion:"
                    // + position);
                }
            } else {// 没保存actID的，可以查找一下
                // String curActID = getActIDByAct(tempState[0], tempState[6]);
                final String curActID = getActIDByAct(tempState[0]);
                if (curActID != null) {
                    // Logger.i(TAG,
                    // "no actID , get it from acgList, get result: curActID:"
                    // + curActID);
                }
                if (actID.equals(curActID)) {
                    position = i;
                    lastFrameState = tempState;
                    // Logger.i(TAG, "find at postion:" + position);
                } else {

                }
            }
            i++;
        }

        if (position > -1) {// 找到啦
            result.put("position", position);
            result.put("data", lastFrameState);
            // Logger.i(TAG, "find at postion:" + position);
            return result;
        } else {
            // Logger.i(TAG, "not find ,findLastActFromStackByActID");
            return null;
        }
    }

    /**
     * 从act栈中查找“最近的”、比当前act的level高的一个act；如果当前act未设置level，返回最近一个act
     * //参数组一:当前act的ActID
     * 
     *参数组二：没有（检索不到）actID的
     */

    // 从act栈中取出比给定level高的、最近一个act的信息
    private String[] findLastHighlevelAct(String level) {
        if (level != null && !level.equals("")) {

            /*
             * Logger.i(TAG, "this Act have level:" + level +
             * ",find from msActStack, last,hige level:" + level +
             * ",msActStack size:" + msActStack.size());
             */

            String[] frameState = new String[actStackSize];// 返回值 待获得
            Iterator iter = msActStack.iterator();

            int lastP = -1; // postion 保存最后一个找到的 当时的长度
            int i = 0;
            while (iter.hasNext()) {

                String[] tempState = (String[]) iter.next();

                /*
                 * Logger.i(TAG,",tempState[0]:"+tempState[0]+
                 * ",tempState[1]:"+tempState[1] +",tempState[2]:"+tempState[2]
                 * +",tempState[3]:"+tempState[3] +",tempState[4]:"+tempState[4]
                 * +",tempState[5]:"+tempState[5] +",tempState[6]:"+tempState[6]
                 * +",tempState[7]:"+tempState[7]
                 * +",tempState[8]:"+tempState[8]);
                 */

                try {
                    if (tempState[8] != null
                            && Integer.parseInt(tempState[8]) < Integer
                                    .parseInt(level)) {
                        frameState = tempState;
                        lastP = i;
                        // Logger.i(TAG,
                        // "find last,and high level act! lastLen:"
                        // + lastLen);

                    }
                } catch (Exception e) {
                    // Logger.i(TAG,
                    // "err:find from msActStack, last,hige level:"
                    // + level);
                    return msActStack.pop();
                }

                i++;// 位置+1
            }

            // 清除position之后的栈内容！
            if (lastP != -1) {
                // msActStack.setSize(lastP);
                destroyNouseAct(lastP);
                // Logger.i(TAG, "msActStack modify: setSize lastLen:" +
                // lastLen);
            } else {
                // Logger.i(TAG, "noneed msActStack modify");
            }

            // 没从栈中找到 最近的、high level act
            // Logger.i(TAG,"lastP:"+lastP);
            if (lastP > -1) { // 找到了，返回它
                return frameState;
            } else {
                return msActStack.pop();
            }
        } else {
            //增加逻辑，如果和当前界面是同一界面，则需要继续pop
            
             //Logger.d(TAG, "curActLevel is null ,get last act in stack ");
            String[] last;
            while(true){
                last = msActStack.pop();
                if(lastFrameState[6]!=null
                        && lastFrameState[0].equals(last[0])
                        && lastFrameState[6].equals(last[6])
                ){
                    continue;
                }
                
                if(lastFrameState[6]==null
                        && lastFrameState[0].equals(last[0])
                        ){
                    continue;
                }
                
                break;

            }            
            
            return last;
        }
    }

    private void destroyNouseAct(int startP) {

        final int p = startP;

        new Thread() {
            public void run() {

                /*
                 * Iterator iter = msActStack.iterator(); int i = 0; while
                 * (iter.hasNext()) {
                 * 
                 * 
                 * String[] tempState = (String[]) iter.next(); if(i>p){ try {
                 * if (tempState[2] != null && !tempState[2].equals("")) {
                 * 
                 * Logger.i(TAG, "destroy act:" + tempState[2]);
                 * getLocalActivityManager().destroyActivity(tempState[2],
                 * true); } } catch (Exception e) { ; } } tempState = null;
                 * i++;// 位置+1 }
                 */

                msActStack.setSize(p);

            }
        }.start();

    }

    private void destroyNouseActWithAllwayCreate(int startP) {

        final int p = startP;

        new Thread() {
            public void run() {

                /*
                 * Iterator iter = msActStack.iterator(); int i = 0; while
                 * (iter.hasNext()) {
                 * 
                 * 
                 * String[] tempState = (String[]) iter.next(); if(i>p){ try {
                 * if (tempState[2] != null && !tempState[2].equals("")) {
                 * 
                 * Logger.i(TAG, "destroy act:" + tempState[2]);
                 * getLocalActivityManager().destroyActivity(tempState[2],
                 * true); } } catch (Exception e) { ; } } tempState = null;
                 * i++;// 位置+1 }
                 */

                msActStack.setSize(p);

            }
        }.start();

    }

    private void destroySameAct(String act) {
        Logger.d(TAG, "do destroy activity instances, find act:" + act);
        final String id = act;
        synchronized (msActStack) {// 不允许同时修改act栈 在线程里操作这个不安全，目前仍无法保持稳定
            new Thread() {
                public void run() {

                    Iterator<String[]> iter = msActStack.iterator();

                    int i = 0;

                    while (iter.hasNext()) {

                        final String[] tempState = (String[]) iter.next();
                        Logger.d(TAG, "get instanceId:" + tempState[2]);
                        if (tempState[2] != null && tempState[2].startsWith(id)) {
                            Logger.d(TAG, "destroy act:" + tempState[2]);
                            mLam.destroyActivity(tempState[2], true);
                            msActStack.remove(i);
                        }
                        i++;// 位置+1
                    }

                }
            }.start();
        }
    }

    private void destroySameAct2(String act) {
        // Logger.d(TAG,"do destroy activity instances, find act:"+act);

        for (int i = 0; i < mAcAct.size(); i++) {
            // Logger.d(TAG,"i:"+i);
            final String instanceID = mAcAct.get(i);
            if (instanceID != null && instanceID.startsWith(act)) {
                // Logger.d(TAG, "destroy instanceID:" + instanceID);
                mLam.destroyActivity(instanceID, true);
                mAcAct.remove(i);

                removeFromActStack(instanceID);

            }
        }

    }

    /**
     * 从act栈中删除
     * 
     * @param actInstanceID
     */
    private void removeFromActStack(String actInstanceID) {

        ArrayList<String[]> delIdList = new ArrayList<String[]>();

        final Iterator<String[]> iter = msActStack.iterator();

        int i = 0;
        if (iter != null)
            while (iter.hasNext()) {

                final String[] tempState = (String[]) iter.next();

                if (tempState[2] != null && !tempState[2].equals("")
                        && tempState[2].equals(actInstanceID)) {
                    delIdList.add(tempState);
                    // msActStack.remove(tempState);
                }
                i++;// 位置+1
            }

        final int count = delIdList.size();
        for (int j = 0; j < count; j++) {
            msActStack.remove(delIdList.get(j));
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onLowMemory()
     */
    @Override
    public void onLowMemory() {
        // TODO Auto-generated method stub
        Logger.e(TAG, "onLowMemory");

        // 弹出提示窗，点确认 返回主界面，销毁除了主界面外的act

        // 备注:在发生截断actStack的地方，把相应的act销毁！

        super.onLowMemory();
    }

    // 显示主导航栏
    private void showNav() {
        //mPviTitleBar.updateNavSwitch(false);
        
        /*
         * //广播新消息和下载中 Bundle bundleToSend = new Bundle();
         * bundleToSend.putInt("STATE",1); final Intent tmpintent = new
         * Intent(MessageView.MESSAGE_UPDATE);
         * tmpintent.putExtras(bundleToSend); sendBroadcast(tmpintent);
         * 
         * final Intent tmpintent2 = new
         * Intent(DownloadStateView.DOWNLOAD_STATE_UPDATE);
         * tmpintent2.putExtras(bundleToSend); sendBroadcast(tmpintent2);
         */

        try {
            if(container.equals((FrameLayout)mNav.getParent())){
                container.removeView(mNav);
            }
            container.addView(mNav, LayoutParams.FILL_PARENT, 130);
            PviUiUtil.hideInput(container);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }
        // btnSwichNav.setImageResource(R.drawable.bg_btn_hidenav);
        // (container.findViewById(R.id.mainNav)).requestFocus();
        // mNav.requestFocus();

        /*
         * //主导航 焦点变化事件处理 mNav.setOnFocusChangeListener(new
         * OnFocusChangeListener(){
         * 
         * @Override public void onFocusChange(View arg0, boolean arg1) {
         * Logger.i(TAG,"onFocusChange"); // TODO Auto-generated method stub
         * if(!arg1){ hideNav(); } }});
         */

        curFocusView = mNav;
        mHandler.postDelayed(fouceView, 1000);
    }

    // 隐藏主导航栏
    private void hideNav() {
        //this.mPviTitleBar.updateNavSwitch(true);
        try {
            if (container != null) {
                if(GlobalVar.deviceType==1){
//                    mNav.invalidate(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16|View.EINK_UPDATE_MODE_FULL);
                }
                container.removeView(mNav);
/*                if(((GlobalVar) getApplication()).deviceType ==1){
                    ((View)container.getParent()).invalidate(0, 38, 600, 165, View.EINK_WAVEFORM_MODE_GC16|View.EINK_UPDATE_MODE_FULL);
                }*/
                }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    // 主导航栏 通过listView实现
    // 导航栏的数据放在一个ArrayList<Object[]>中
    // ，string[]依次存放：navTitle(R.string.xx),navImage(R.drawable.xx),actID("ACT11000")跳转到的界面

    private ArrayList<Object[]> navList = new ArrayList<Object[]>();

    private void initNavList() {
        Object[] navItem = new Object[4];

        // 继续阅读
        navItem[0] = R.string.fp_cat0;
        navItem[1] = R.drawable.mainnav_image_0;
        navItem[2] = "ACT00000";
        navItem[3] = null;
        navList.add(navItem.clone());    
        
        // 无线书城
        navItem[0] = R.string.fp_cat1;
        navItem[1] = R.drawable.mainnav_image_1;
        navItem[2] = "ACT19000";
        navItem[3] = null;
        navList.add(navItem.clone());

        // 我的书架
        navItem[0] = R.string.fp_cat2;
        navItem[1] = R.drawable.mainnav_image_2;
        navItem[2] = "ACT12100";
        navItem[3] = null;
        navList.add(navItem.clone());
        // 本地书库
        navItem[0] = R.string.fp_cat3;
        navItem[1] = R.drawable.mainnav_image_3;
        navItem[2] = "ACT13500";
        navItem[3] = null;
        navList.add(navItem.clone());
/*        // 我的批注
        navItem[0] = R.string.fp_cat4;
        navItem[1] = R.drawable.mainnav_image_4;
        navItem[2] = "ACT13600";
        navItem[3] = null;
        navList.add(navItem.clone());*/
        // 资源中心
        navItem[0] = R.string.fp_cat6;
        navItem[1] = R.drawable.mainnav_image_5;
        navItem[2] = "ACT13100";
        navItem[3] = null;
        navList.add(navItem.clone());
        // 个人空间
        navItem[0] = R.string.fp_cat7;
        navItem[1] = R.drawable.mainnav_image_6;
        navItem[2] = "ACT14100";
        navItem[3] = null;
        navList.add(navItem.clone());

    }

    private class NavListAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private ArrayList<Object[]> navList;

        public NavListAdapter(Context context, ArrayList<Object[]> list) {
            mInflater = LayoutInflater.from(context);
            navList = list;
        }

        public int getCount() {
            return navList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.mainnavitem, null);
                holder = new ViewHolder();
                holder.ivNavImage = (ImageView) convertView
                        .findViewById(R.id.ImageViewNavImage);

                holder.tvNavTitle = (TextView) convertView
                        .findViewById(R.id.NavTitle);
                /*
                 * holder.ivSwicher = (ImageView) convertView
                 * .findViewById(R.id.ImageViewSwicher);
                 */
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.ivNavImage
                    .setImageResource((Integer) navList.get(position)[1]);
            holder.tvNavTitle.setText(mRes.getString((Integer) navList
                    .get(position)[0]));

            // add click event:

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show messge
                    if(R.string.fp_cat0==((Integer)navList.get(position)[0])){
                        goonRead();
                    }else{
                    Bundle bundleToSend = new Bundle();
                    bundleToSend.putString("actID", (String) navList
                            .get(position)[2]);
                    if (navList.get(position)[3] != null) {
                        bundleToSend.putString("actName", (String) navList
                                .get(position)[3]);
                    }
                    final Intent tmpintent = new Intent(
                            MainpageActivity.START_ACTIVITY);
                    tmpintent.putExtras(bundleToSend);
                    sendBroadcast(tmpintent);
                    }
                    // 点击后隐藏导航栏
                    hideNav();

                }
            });
            return convertView;
        }

        class ViewHolder {
            ImageView ivNavImage;
            TextView tvNavTitle;
            // ImageView ivSwicher;//no use
        }
    }

    // 获取界面路径数据
    private ArrayList<HashMap<String, String>> getActPathInfo(String actID) {
        final ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        String curActId = actID;
        // Logger.i(TAG,"finding act path ...");
        int i = 0;
        while (true) {
            if (actList != null) {
                i++;
                HashMap<String, String> thisHM = actList.get(curActId);
                // Logger.i(TAG,"curActId is :"+curActId);
                if (thisHM == null) {
                    // Logger.i(TAG,"could not get this act:"+curActId);
                    break;
                } else {
                    // 当前act的标题，以设置值为准（1、startActivity时设置了mainTitle
                    // 2、通过发SET_TITLE广播来设置标题）
                    if (i == 1) {
                        thisHM.put("mainTitle", lastFrameState[4]);
                    }

                    list.add(thisHM);
                }
                if (thisHM.get("parentActID") == null) {
                    // Logger.i(TAG,"not set parent act");
                    break;
                }/*
                  * else
                  * if((thisHM.get("parentActID")!=null&&thisHM.get("parentActID"
                  * ).equals("ACT10000"))){ Logger.i(TAG,"is root"); break; }
                  */else {

                    HashMap<String, String> parent = actList.get(thisHM
                            .get("parentActID"));
                    if (parent != null) {
                        curActId = parent.get("actID");
                        // Logger.i(TAG,"find a parent node: "+curActId);
                    } else {
                        // Logger.e(TAG,"could not find this act:"+curActId);
                        break;
                    }
                }
            }
        }
        return list;
    }

    private void showUiPath(ArrayList<HashMap<String, String>> pathInfo) {
        mPviTitleBar.updateUiPath(pathInfo);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * 需要通过鉴权的Activity表
     */
    private List<String> needAutoList = new ArrayList<String>();

    /**
     * 初始化List 最好只初始化一次。
     */
    private void initNeedAutoList() {
        needAutoList.add("BookSummaryActivity");
        needAutoList.add("UserCenterActivity");
        needAutoList.add("UserInfoActivity");
        needAutoList.add("WirelessStoreActivity");
        needAutoList.add("WirelessStoreMainpageActivity");
        needAutoList.add("ReadingOnlineActivity");
        needAutoList.add("SubscribeProcess");
        needAutoList.add("MyFavoriteActivity");
        needAutoList.add("MySubscribesActivity");
        needAutoList.add("RecentBookActivity");
        // needAutoList.add("ShowAgreementActivity");
        needAutoList.add("SubscribeProcess");
    }

    /**
     * 是否需要鉴权
     * 
     * @param act
     *            跳转的Activity
     * @return 是否需要鉴权
     */
    private boolean isInNeedAutoList(String act) {
        if (act == null) {
            return false;
        }
        for (String acti : needAutoList) {
            String tmp = act.substring(act.lastIndexOf('.'), act.length());
            if (tmp.contains(acti)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 不再使用
     **/
    /*
     * private void showTip(String pviapfStatusTip,String pviapfStatusTipTime){
     * if (pviapfStatusTip != null && !pviapfStatusTip.equals("")) { //
     * mtvTip.setText(pviapfStatusTip); final String pviapfStatusTip2 =
     * pviapfStatusTip; final Runnable setStatusTip = new Runnable() {
     * 
     * @Override public void run() { mtvTip.setText(pviapfStatusTip2); } }; new
     * Thread() { public void run() { mHandler.post(setStatusTip); } }.start();
     * 
     * }
     * 
     * if(pviapfStatusTipTime==null){ pviapfStatusTipTime = "60000";//默认最长显示30s
     * }
     * 
     * if (pviapfStatusTipTime != null) { long delay =
     * Long.parseLong(pviapfStatusTipTime); if (delay > 0) { Runnable hideTip =
     * new Runnable() {
     * 
     * @Override public void run() {
     * if(SystemClock.uptimeMillis()>=hidetipTime){//当前时间点落后于设置的隐藏时间点时
     * //mtvTip.setText(""); //发消息隐藏 sendBroadcast(new
     * Intent(MainpageActivity.HIDE_TIP)); } } }; hidetipTime =
     * SystemClock.uptimeMillis()+delay; mHandler.postAtTime(hideTip,
     * hidetipTime); } } }
     */

    /*
     * private void updateActShown(String activityId){
     * 
     * if(activityId!=null&&!activityId.equals("")){ Iterator iter =
     * msActStack.iterator(); while (iter.hasNext()) { String[] tempState =
     * (String[]) iter.next(); try { if (tempState[2] != null &&
     * tempState[2].equals(activityId)) { tempState[9] = "shown";
     * 
     * } } catch (Exception e) { e.printStackTrace(); } } } }
     */

    private boolean mustUpdate = false;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals("com.pvi.ap.reader.service.update")) {
                Bundle bundle = arg1.getExtras();
                message = bundle.getString("message");
                title = bundle.getString("title");
                path = bundle.getString("path");
                mustUpdate = arg1.getBooleanExtra("mustUpdate", false);
                updateWindown();
            }
        }
    };

    private String message = null;
    private String title = null;
    private String path = null;
    private boolean ok = false;

    private void updateWindown() {
        PviAlertDialog pad = new PviAlertDialog(mContext);
        pad.setTitle(R.string.system_soft_update);
        pad.setMessage(mContext.getString(R.string.system_soft_update_message));
        if (message != null && !"".equals(message.trim())) {
            pad.setMessage(message);
        }
        if (ok) {
            pad.setMessage("程序正在升级中...");
        }
        pad.setCanClose(true);
        pad.setButton(mContext.getString(R.string.OK),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (!ok) {
                            Intent intent = new Intent(
                                    "com.pvi.ap.reader.service.update.ok");
                            Bundle extras = new Bundle();
                            extras.putString("message", message);
                            extras.putString("title", title);
                            extras.putString("path", path);
                            intent.putExtras(extras);
                            mContext.sendBroadcast(intent);
                            ok = true;
                        }

                        arg0.dismiss();
                    }
                });
        pad.setButton2(mContext.getString(R.string.Cancel),
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        arg0.dismiss();
                    }
                });
        pad.show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        // Logger.d(TAG,"onResume()");

        // 在这里刷新一下全屏 起作用吗？
        if (((GlobalVar) getApplication()).deviceType == 1) {
//            Logger.i(TAG, "onResume() : GC16 FULL draw screen");
//            getWindow().getDecorView().invalidate(
//                    View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//                            | View.EINK_UPDATE_MODE_FULL);
        }

        super.onResume();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 如果输入法目前打开了，则当前控件之外区域的touch，将触发输入法的隐藏操作
        if (ev.getAction() == MotionEvent.ACTION_DOWN
        /*
         * && ((InputMethodManager)
         * getSystemService(INPUT_METHOD_SERVICE)).isActive()
         */
        ) {
            // Logger.d(TAG,"坐标对比");
            final View curFocusedView = getWindow().getDecorView().findFocus();

            if (curFocusedView != null) {

                // 如果是在输入框上面

                if (curFocusedView instanceof EditText) {
                    int[] location = { 0, 0 };
                    curFocusedView.getLocationOnScreen(location);
                    // Logger.d(TAG,"curFocusedView:"+curFocusedView.toString()+",id:"+curFocusedView.getId()+"; loc x:"+location[0]+" ,y:"+location[1]);
                    final int touchX = (int) ev.getRawX();
                    final int touchY = (int) ev.getRawY();
                    // Logger.d(TAG,"touch x:"+touchX+" ,y:"+touchY);
                    if (!(touchX > location[0]
                            && touchX < location[0] + curFocusedView.getWidth()
                            && touchY > location[1] && touchY < location[1]
                            + curFocusedView.getHeight())) {
                        // Logger.d(TAG, "隐藏之");
                        PviUiUtil.hideInput(curFocusedView);
                    } else {
                        // Logger.d(TAG, "仍在输入框内部点击，不隐藏");
                    }
                }

            } else {
                // Logger.d(TAG, "no focused view");
            }

        }

        return super.dispatchTouchEvent(ev);

    }



    @Override
    public void onAttachedToWindow() {
        //getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD); 
        super.onAttachedToWindow();
    }
    
    private void goonRead(){

        String columns[] = new String[] { Bookmark.BookmarkType,
                Bookmark.ContentId, Bookmark.ChapterId,
                Bookmark.ChapterName, Bookmark.ContentName,
                Bookmark.CertPath, Bookmark.FilePath, Bookmark.CreatedDate,
                Bookmark.Position, Bookmark._ID, Bookmark.Author,
                Bookmark.LineSpace, Bookmark.FontSize, Bookmark.SourceType,
                Bookmark.ChapterName };
        Cursor cur = null;
        String where = Bookmark.BookmarkType + "='" + 0 + "'";
        String order = Bookmark.CreatedDate + " DESC ";
        cur = managedQuery(Bookmark.CONTENT_URI, columns, where, null,
                order);
        if (cur.moveToFirst()) {

            // String id=cur.getString(cur.getColumnIndex(Bookmark._ID));
            // String type =
            // cur.getString(cur.getColumnIndex(Bookmark.BookmarkType));
            // String author =
            // cur.getString(cur.getColumnIndex(Bookmark.Author));
            final String chapterid = cur.getString(cur
                    .getColumnIndex(Bookmark.ChapterId));
            final String contentid = cur.getString(cur
                    .getColumnIndex(Bookmark.ContentId));
            // String bookname=
            // cur.getString(cur.getColumnIndex(Bookmark.ContentName));
            // String readtime =
            // cur.getString(cur.getColumnIndex(Bookmark.CreatedDate));
            final String readposition = cur.getString(cur
                    .getColumnIndex(Bookmark.Position));
            String filepath = cur.getString(cur
                    .getColumnIndex(Bookmark.FilePath));
            String certpath = cur.getString(cur
                    .getColumnIndex(Bookmark.CertPath));
            String LineSpace = cur.getString(cur
                    .getColumnIndex(Bookmark.LineSpace));
            final String FontSize = cur.getString(cur
                    .getColumnIndex(Bookmark.FontSize));
            final String chaptername = cur.getString(cur
                    .getColumnIndex(Bookmark.ChapterName));
            String sourceType = cur.getString(cur
                    .getColumnIndex(Bookmark.SourceType));

            // 最近阅读为在线阅读
            if ("4".equals(sourceType)) {

                final PviAlertDialog dialog = new PviAlertDialog(
                        MainpageActivity.this);
                dialog.setTitle(getResources().getString(
                        R.string.systemconfig_pop_message));
                dialog.setMessage("最近阅读为在线阅读，点击“确定”将联网阅读，点击“取消”将进入首页！");
                dialog.setCanClose(true);
                dialog
                        .setButton(
                                DialogInterface.BUTTON_POSITIVE,
                                getResources().getString(
                                        R.string.system_soft_unauthorized),
                                new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        // TODO Auto-generated method stub
                                        dialog.cancel();
                                        Intent intent = new Intent(
                                                MainpageActivity.START_ACTIVITY);
                                        Bundle sndBundle = new Bundle();
                                        sndBundle
                                                .putString("act",
                                                        "com.pvi.ap.reader.activity.ReadingOnlineActivity");
                                        sndBundle.putString("haveTitleBar",
                                                "1");
                                        sndBundle.putString("startType",
                                                "allwaysCreate"); // 每次都create一个新实例，不设置此参数时，默认为“复用”已有的
                                        sndBundle.putString("Position",
                                                readposition);
                                        sndBundle.putString("ChapterID",
                                                chapterid);
                                        sndBundle.putString("ChapterName",
                                                chaptername);
                                        sndBundle.putString("ContentID",
                                                contentid);
                                        sndBundle.putString("FontSize",
                                                FontSize);
                                        // sndBundle.putString("pviapfStatusTip",
                                        // getResources().getString(R.string.myimageOpen));
                                        intent.putExtras(sndBundle);
                                        sendBroadcast(intent);
                                        return;
                                    }

                                });
                dialog
                        .setButton(
                                DialogInterface.BUTTON_NEUTRAL,
                                getResources().getString(
                                        R.string.system_soft_cancel),
                                new android.content.DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.cancel();
                                    }

                                });
                dialog.setOnCancelListener(new OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        PviUiUtil.toHomePage(mContext);
                    }
                });
                dialog.show();

            } else {
                HashMap data = new HashMap();
                data.put("FilePath", filepath);
                data.put("ChapterID", chapterid);
                data.put("Offset", readposition);
                data.put("FromPath", 1);
                data.put("CertPath", certpath);
                data.put("ContentID", contentid);
                data.put("LineSpace", LineSpace);
                data.put("FontSize", FontSize);
                if (new File(filepath).exists()) {
                    OpenReader.gotoReader(MainpageActivity.this, data);
                } else {

                    final PviAlertDialog alertDialog = new PviAlertDialog(
                            MainpageActivity.this);
                    alertDialog.setTitle(getResources().getString(
                            R.string.systemconfig_pop_message));
                    alertDialog
                            .setMessage("对不起，最近阅读文件已被删除或不存在，点击确定系统将进入首页！");
                    alertDialog
                            .setButton(
                                    DialogInterface.BUTTON_POSITIVE,
                                    getResources()
                                            .getString(
                                                    R.string.system_soft_unauthorized),
                                    new android.content.DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            // TODO Auto-generated method
                                            // stub
                                            alertDialog.dismiss();
                                            PviUiUtil.toHomePage(mContext);
                                        }

                                    });
                    alertDialog.setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            PviUiUtil.toHomePage(mContext);
                        }
                    });
                    alertDialog.show();
                    // Toast.makeText(MainpageActivity.this,
                    // "对不起，最近阅读文件已删除！", Toast.LENGTH_LONG).show();
                }
            }
        } else {

            final PviAlertDialog alertDialog = new PviAlertDialog(
                    MainpageActivity.this);
            alertDialog.setTitle(getResources().getString(
                    R.string.systemconfig_pop_message));
            alertDialog.setMessage("对不起，还没有最近阅读，点击确定系统将进入首页！");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    getResources().getString(
                            R.string.system_soft_unauthorized),
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            // TODO Auto-generated method stub
                            alertDialog.dismiss();
                            Intent intent = new Intent(
                                    MainpageActivity.START_ACTIVITY);
                            Bundle sndBundle = new Bundle();
                            sndBundle
                                    .putString("act",
                                            "com.pvi.ap.reader.activity.MainpageInsideActivity");
                            sndBundle.putString("haveStatusBar", "1");
                            sndBundle.putString("startType", "reuse");
                            intent.putExtras(sndBundle);
                            sendBroadcast(intent);
                            return;
                        }

                    });
            alertDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                    PviUiUtil.toHomePage(mContext);
                }
            });
            alertDialog.show();
        }

        cur.close();
    
    }
}