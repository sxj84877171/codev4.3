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
 * Ӧ�������
 * 
 * @author ������RD040
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
    private LinearLayout mainBlock; // ������������
    private PviStatusBar mStatusBar;
    private PviBottomBar mBottomBar;
    private PviTitleBar mPviTitleBar;
    // private RelativeLayout mTitleBar; // ������ �ؼ�

    private LinearLayout mNav;// ������������������
    private ImageView mStatusBarBottom;

    /*
     * // ʹ�ÿ��Ƕ����Activityʱ�����ò��� private String actID; // �����ܶ����act��ID private
     * String actName; // ����act���� ��ʾ�ڱ����� private String actLevel; // actd�Ĳ㼶
     * private String returnActID; // ���桰ָ�����ؽ��� ��actID�� private String act; //
     * ���� actAction //private String actTabIndex; // ���� tab index private String
     * activityID; // activity��ʶ private String haveStatusBar; // �Ƿ���ʾ״̬��
     * private String haveTitleBar; // �Ƿ���ʾ������ private String haveMenuBar; //
     * ��ʱȥ���� private String fullScreen; // ȫ����ʶ private String startType; //
     * ������������ private String pviapfStatusTip; // ���� acitivtyʱ ��״̬����ʾ��ʾ��Ϣ
     * protected String pviapfStatusTipTime; // ״̬����ʾ��Ϣ��ʾʱ�� ms private String
     * mainTitle; // ��������� private String childViewBg; // ���� Ƕ������� ����
     */
    // �㲥Action��������
    public final static String START_ACTIVITY = "com.pvi.ap.reader.mainframe.START_ACTIVITY";// ����Ҫ��������һ��Activity���Ƿ�ÿ�ζ�OnCreate����
    public final static String SET_TITLE = "com.pvi.ap.reader.mainframe.SET_TITLE";// ���ñ���������
    public final static String BACK = "com.pvi.ap.reader.mainframe.BACK";// ������һ����Activity
    public final static String SHOW_TIP = "com.pvi.ap.reader.mainframe.SHOW_TIP";// ��״̬������ʾ��ʾ��Ϣ
    public final static String HIDE_TIP = "com.pvi.ap.reader.mainframe.HIDE_TIP";// ��״̬������ʾ��ʾ��Ϣ
    public final static String SERVICE_RESP = "com.pvi.ap.reader.mainframe.SERVICE_RESP";// �ӷ����й㲥������ĳЩ�����������Ϣ
    public final static String SHOW_ALERT = "com.pvi.ap.reader.mainframe.SHOW_ALERT";// ֪ͨ��ܣ������ض�����ʾ��
    public final static String HIDE_ALERT = "com.pvi.ap.reader.mainframe.HIDE_ALERT";// ֪ͨ��ܣ�������ʾ��
    public final static String FULLSCREEN_ON = "com.pvi.ap.reader.mainframe.FULLSCREEN_ON";// ֪ͨ��ܣ���ȫ����
    public final static String FULLSCREEN_OFF = "com.pvi.ap.reader.mainframe.FULLSCREEN_OFF";// ȡ����ȫ����
    public final static String VOLUME_CHANGED = "com.pvi.ap.reader.mainframe.VOLUME_CHANGED";// ϵͳ���������˱仯
    public final static String SHOW_ME = "com.pvi.ap.reader.mainframe.SHOW_ME";// �ӽ�����߿�ܣ����Ѿ�������ʾ��

    // activtys ����
    private String lastAct;
    private String lastStatusBar; // �����ϴ�״̬��״̬��־
    private String lastHaveMenuBar;
    private boolean lastHaveStatusBar; // ��־���� ��һ����ͼ�Ƿ���״̬��
    private boolean lastHaveTitleBar; // ��־���� ��һ����ͼ�Ƿ��б����� ����ı����ƺ���Щ���һ���˵�ظ��ˣ�������
    private String thisActStartType; // ���桰��һ���������ġ�������ʽ��
    private String lastChildViewBg; // ���� ��һ��Ƕ����ͼ�� ����
    private Bundle mActivityGroupState;

    private Stack<String[]> msActStack = new Stack<String[]>();// ���汻Ƕ���activity��Ϣ
    private static int actStackSize = 11;
    private String[] lastFrameState = new String[actStackSize];// ����ǰ/ǰ������ܵ�״̬��Ŀǰ
    // ��0����Ƕacvitiy��act����1��״̬��״̬��־1��ʾ/0
    // ��2��acitivityID ��3���Ƿ���ʾ������ 1��ʾ
    // ��4���������ı������Ϊ�������ر���������5��Ƕ�����ı��� 0͸�� ����resid
    // ��6��tabIndex��7��ActID��8��actLevel
    // ��9��startType
    // [10]�Ƿ���ʾ�ײ������� ��2011-6-25���ӣ�
    
    //2011-7-18 start����act ��Ϣ ���Ӵ���ʱ�ޣ�����ʱ����С��500ms
    private Stack<Long> mPtime = new Stack<Long>();

    private ArrayList<String> mAcAct = new ArrayList<String>();// ��������allwaysCreate��ʽ������activityʵ��id

    protected PviAlertDialog pd;

    // ����������Activity
    protected View childView1;
    protected Intent newIntent;

    // act ����
    private HashMap<String, HashMap<String, String>> actList = new HashMap<String, HashMap<String, String>>();
    private ArrayList<String> ignorActList = PviReaderUI.getIgnorActList();

    private LocalActivityManager mLam;
    
    private View curFocusView; // ���浱ǰ����View�ؼ�

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
         * ���Զ�ȡ���app�е��ļ� try { File systemSetFile = new
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

        // ȥ��FrameLayout��ǰ��
        FrameLayout topFrame = (FrameLayout) findViewById(android.R.id.content);
        topFrame.setForeground(null);
        topFrame = null;

        // �����ı��浽��Ա����
        // mContext = getApplicationContext();//getBaseContext();        
        mRes = mContext.getResources();
        mLam = getLocalActivityManager();
        mInflater = LayoutInflater.from(mContext);

        // ��ȡϵͳ��Ϣ
        ReadSystemMessage readSystemMessage = new ReadSystemMessage(
                MainpageActivity.this);
        readSystemMessage.run();
        initNeedAutoList();

        // �����������
        // initActList();
        actList = PviReaderUI.initActList(mRes);

        // �ر���־����ָ��������־ ��ʱע�͵�
        // Logger.closeLogger();
        // Logger.openInfoLogger(false);

        // ��ʼ��UI
        initControls();
        initUpdateMode();
        bindEvent();

        IntentFilter filter = new IntentFilter();
        // ע����ͼ�㲥������

        // ����Զ���Ŀ����Ϣ
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
        
        // ���� ��ҳ
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

        // ���ɡ���ʾ��
        /*
         * pd = new PviAlertDialog(this); pd.setMessage("����ת���С�����");
         * pd.setHaveProgressBar(true);
         */

        mNav = (LinearLayout) mInflater.inflate(R.layout.mainnav, null);
        initNavList();
        NavListAdapter navListAdapter = new NavListAdapter(mContext, navList);
        GridView lvNavListview = (GridView) mNav.findViewById(R.id.navList);
        lvNavListview.setAdapter(navListAdapter);

    }

    private void initUpdateMode() {
        if (((GlobalVar) getApplication()).deviceType == 1) {// fsl���
            // 2011-6-28ע�͵�
            // getWindow().getDecorView().getRootView().setUpdateMode(View.EINK_WAIT_MODE_WAIT|View.EINK_WAVEFORM_MODE_GC16);//ȫ��Ļ
            // ����WAIT
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
     * ֪ͨ��� ���� �׽���
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
     * ֪ͨ������� �׽���
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
        // ������ ����仯�¼�����

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

            // ʹ�ÿ��Ƕ����Activityʱ�����ò���
            String actID = null; // �����ܶ����act��ID
            String actName = null; // ����act���� ��ʾ�ڱ�����
            String actLevel = null; // actd�Ĳ㼶
            String returnActID = null; // ���桰ָ�����ؽ��� ��actID��
            String act = null; // ���� actAction
            // String actTabIndex = null; // ���� tab index
            String actTabName = null;
            String insideAct = null; // ���� ��TABǶ��activity
            String activityID = null; // activity��ʶ
            String haveStatusBar = null; // �Ƿ���ʾ״̬��
            String haveTitleBar = null; // �Ƿ���ʾ������
            String haveMenuBar = null; // ��ʱȥ����
            String fullScreen = null; // ȫ����ʶ
            String startType = null; // ������������
            String pviapfStatusTip = null; // ���� acitivtyʱ ��״̬����ʾ��ʾ��Ϣ
            String pviapfStatusTipTime = null; // ״̬����ʾ��Ϣ��ʾʱ�� ms
            String mainTitle = null; // ���������
            String childViewBg = null; // ���� Ƕ������� ����
            String haveBottomBar = null; // �Ƿ�ʹ�õ���

            // ����Ϊ�Զ���Ŀ����Ϣ����
            if (action.equals(MainpageActivity.START_ACTIVITY)) {
                
               
                Logger.i(TAG, "recv frame broadcast!" + action);

                // ���ɡ���ʾ�򡱣�Ч���в�̫�ã���ʱȥ����
                // mHandler.sendEmptyMessage(100);

                // ��Щ��������գ�����ȡ����Ա�������ı�Ϊ�ֲ�������������������Щֵ
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
                    
                    //2011-7-17�����߼����ڼ���ʱ����ڵ�������Ϣ�����账��
                    Long thisPtime = SystemClock.uptimeMillis();
                    mPtime.push(thisPtime);
                    
                    if(bd.getString("actTabName")==null/*tab��Ƕ������*/
                            &&mPtime.lastElement()-mPtime.firstElement()<500){
                        Logger.d(TAG,"deny start_act !");
                        return;
                    }else{
                        mPtime.clear();
                        mPtime.add(thisPtime);
                    }
                    
                    
                    // ����act���ַ���������ǲ������ڡ�����Ҫ�����activity��
                    if (bd.getString("act") != null
                            && ignorActList.contains(bd.getString("act"))) {
                        newIntent = new Intent(bd.getString("act"))
                                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                        | Intent.FLAG_ACTIVITY_NO_ANIMATION);// reuse
                                                                             // �������Ҫallways
                                                                             // create�ģ��Ժ���������֧��
                        newIntent.putExtras(bd);
                        mLam.startActivity(bd.getString("act"), newIntent); // ֱ�Ӻ�ִ̨�и�activiity
                        return;
                    }

                    // ���������actID������ͨ��actIDȥȡ����ЩԤ��ֵ
                    actID = bd.getString("actID");

                    if (actID == null) {// ������ActID������������ʱ��û�и�actID��ֵ�ģ�Ҫ��actList�м���һ��
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
                            // ��������ȡ����ֵ���bd����û�����õģ��ǵü���bd
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
                         * add by Elvis 2011-5-17 start �˶δ��������Ժ���
                         * ����ǿ��������ҳ�潫ͣ������ҳ ����κβ�������ʾ�û���Ҫ��������ʹ��
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
                    // ���û�����ã�����Ĭ������һ�仰������actName �����ڽ���actName...��
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
                     * //��ֵΪ Ĭ�ϵı����ز� if(childViewBg==null){ childViewBg =
                     * ""+R.drawable.childactivity_bg; }
                     */

                } else {
                    // Logger.i(TAG, "bundle is null !");
                    return;
                }

                /**
                 * startType = "back" //���� = "allwaysCreate" //ǿ��create ���ǿ��
                 * create new��1������ģʽΪ�� 2������activity
                 * ID����ջ��act��+Ψһid(ȡ��ǰʱ�䣺����ʱ����)��
                 * ����������back����1������ģʽΪ��Intent.FLAG_ACTIVITY_SINGLE_TOP��2
                 * ��ջact����activity ID��=action
                 */
                if (startType == null) {
                    startType = "";
                }
                if (activityID == null) {
                    activityID = "";
                }
                if (haveTitleBar == null) {
                    haveTitleBar = "0"; // Ĭ�ϲ���ʾ
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

                // debug ����Щֵ����ӡ����
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

                // frame״̬��ջ

                // 1�����ڡ���һ��״̬��������ε�startTypeδ���ã�Ϊback�������ѡ���һ��״̬��������������ջ
                // ���ӹ���2�������activity���ٴΡ����������ã�����activityʵ���Ļ���Ӧ�ò���ջ��
                // ���������Ӧ�ñȽ��ٴ��ڣ���

                if ((!startType.equals("back") && lastFrameState[0] != null && !lastFrameState[0]
                        .equals(""))
                        && !(startType.equals("") && lastFrameState[0] != null && lastFrameState[0]
                                .equals(act))) {
                    
                    //2011-7-17 Ӧ�������߼��������ջ�����µ�һ����ͬ��������ջ
                    
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

                // �Ӵ˴���ʼ�����԰��´򿪵�act״̬�������� ��Щû�е�״̬�ǵ�Ҫ�ÿգ�����ᱣ����֮ǰ����ֵ
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
                    lastFrameState[10] = "1";// Ĭ����ʾ����
                }

                if (haveStatusBar != null && haveStatusBar.equals("0")) {

                    // 2010-11-22 �����߼������1����������Ƿ��� 2
                    // ǰһ����ͼ��statusbar�������ͼ��statusbar������activity������ɺ�������statusbar
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[1] != null
                                && lastFrameState[1].equals("1")) {//
                            lastHaveStatusBar = true;
                        } else {
                            // ���һ�£�
                            lastHaveStatusBar = false;
                        }
                    }

                    lastFrameState[1] = "0";
                } else {

                    // 2010-11-22�����߼��������������������ǡ����ء�:���ǰһ����ͼ��statusBar�������ͼ��statusbar�����Σ���Ҫ����activity������ɺ�����ʾ������
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[1] != null
                                && lastFrameState[1].equals("1")) {// ǰһ������statusBar�ģ�ֱ��setVisibilitey��ò��û�����Ҫ�ۣ���
                            mStatusBar.setVisibility(View.VISIBLE);
                            lastHaveStatusBar = true;
                        } else {
                            // ���һ�£�
                            lastHaveStatusBar = false;
                        }
                    }
                    lastFrameState[1] = "1";
                }

                // �������Ĵ���

                // ���ڰ�UI1 ʹ�ô��߼�

                if (haveTitleBar.equals("0")) {

                    // 2010-11-22 �����߼������1����������Ƿ��� 2
                    // ǰһ����ͼ��titlebar�������ͼ��titlebar������activity������ɺ�������titlebar
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[3] != null
                                && lastFrameState[3].equals("1")) {//
                            lastHaveTitleBar = true;
                        } else {
                            // ���һ�£�
                            lastHaveTitleBar = false;
                        }
                    }

                    lastFrameState[3] = "0";
                } else if (haveTitleBar.equals("1")) {

                    // 2010-11-22�����߼��������������������ǡ����ء�:���ǰһ����ͼ��titlebar�������ͼ��titlebar�����Σ���Ҫ����activity������ɺ�����ʾ������
                    if ((thisActStartType != null && !thisActStartType
                            .equals("back"))) {
                        if (lastFrameState[3] != null
                                && lastFrameState[3].equals("1")) {// ǰһ������titlebar�ģ�ֱ��setVisibilitey��ò��û�����Ҫ�ۣ���
                            // Logger.i(TAG,"last act have titlebar!");
                            // 2011-5-17ȥ��������mTitleBar.setVisibility(View.VISIBLE);
                            lastHaveTitleBar = true;
                        } else {
                            // ���һ�£�
                            lastHaveTitleBar = false;
                        }
                    }
                    lastFrameState[3] = "1";
                }

                // Logger.d(TAG,"lastFrameState[3]:"+lastFrameState[3]);

                // �������ı��Ĵ��� ����addchildview֮ǰshow����
                // Logger.i(TAG,"set title:"+mainTitle);
                // mtvTitleBarTitle.setText(mainTitle);

                if (mainTitle != null && !mainTitle.equals("")) {
                    lastFrameState[4] = mainTitle;
                } else {
                    lastFrameState[4] = null;
                }

                // Ƕ�����򱳾��Ĵ���
                /*
                 * if(childViewBg!=null&&!childViewBg.equals("")){
                 * Logger.i(TAG,"childViewBg is updated!:"+childViewBg);
                 * if(childViewBg.equals("-1")){//ʹ��ϵͳ����
                 * mainBlock.setBackgroundResource(R.drawable.childactivity_bg);
                 * 
                 * }else{
                 * mainBlock.setBackgroundResource(Integer.parseInt(childViewBg
                 * ));
                 * 
                 * } lastFrameState[5]=childViewBg; }else{//δ���õı����������仯���ɣ�
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

                if (act == null || act.equals("")) { // ���ûָ��act����ص������棡
                    act = "com.pvi.ap.reader.activity.MainpageInsideActivity";
                }

                if (startType.equals("allwaysCreate")) {
                    activityID = act
                            + ((new SimpleDateFormat("yyyyMMddHHmmss"))
                                    .format(new Date()));
                } else if (startType.equals("back")) {// Ŀǰ����startType=backʱ���ᴫ��activityIDֵ
                    ;
                    // 2011-5-2 back��ʱ�򲻻�Я�������������ʱӦ�ø�����ֵΪact
                    if (activityID == null
                            || (activityID != null && activityID.equals(""))) {
                        activityID = act;
                    }
                } else {
                    activityID = act;
                }
                // Logger.i(TAG,"activityID:"+activityID);

                // �ж��Ƿ�����TAB�ڲ���ת
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

                // �����ڼ䣬Ĭ�ϲ�����
                if (startType.equals("allwaysCreate")) {
                    Logger.i(TAG, "set : allwaysCreate");
                    newIntent = new Intent(act)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                } else if (startType.equals("reuse")
                        || startType.equals("back")) {// ���� �� ������ reuse��
                    // ���½�activity
                    Logger.i(TAG, "set reuse or back : reuse");
                    newIntent = new Intent(act)
                            .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                                    | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                } else if (actTabName != null && !actTabName.equals("")
                        && !startType.equals("back")) {
                    // tabǶ���,reuse
                    // ���½�activity
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

                // ��ʾ��ʾ��Ϣ
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

                newIntent.putExtras(bd);// �Ѳ��������µ�intent

                /*
                 * 2011-5-2 ���԰���������Ƶ�show����view֮ǰ˲�� //Ѱ������·�� Ma final
                 * ArrayList<HashMap<String, String>> pathInfo =
                 * getActPathInfo(actID); //��ʾ������ showUiPath(pathInfo);
                 */

                // Ƕ����Activity ���¿��̣߳�
                // if (insideAct != null &&
                // !insideAct.equals("")&&!startType.equals("back")){//back�����β���Ҫ���߼�

                // final HashMap<String, Class> classes =
                // PviReaderUI.getClasses();
                if (actTabName != null && !actTabName.equals("")
                        && !startType.equals("back")) {// back�����β���Ҫ���߼�
                    // Logger.i(TAG,"have actTabName:"+actTabName);
                    // �����Ƕ��TAB����ģ����жϵ�ǰ��Ƕ��act�ǲ��Ǻ�Ҫ���򿪵ı�Ƕ���childview��act��ͬ�������ͬ����Ҫִ��Ƕ��
                    if (!isSameAct) {
                        // Logger.i(TAG,"not same Act");
                        new Thread() {
                            public void run() {
                                mHandler.post(addChildView);
                            }
                        }.start();
                    } else {
                        // Logger.i(TAG,"is same Act");

                        // 2011-5-2���� ���±�����·�� 2011-7-15 ȥ��
                        /*
                         * //Ѱ������·�� Ma final ArrayList<HashMap<String, String>>
                         * pathInfo = getActPathInfo(lastFrameState[7]); //��ʾ��
                         * �������� showUiPath(pathInfo);
                         */

                        String switchInTab = bd.getString("switchInTab");
                        if (switchInTab == null) {
                            // Ӧ�ÿ���TABȥ�л�
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
                     * if(switchInTab==null){ //Ӧ�ÿ���TABȥ�л� new Thread() { public
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
                 * �������ã���ʱȥ�� Logger.i(TAG,"close loading..."); pd.dismiss();
                 */

                // container.requestFocus();
                // childView1.requestFocus();

                /*
                 * lastAct = act;
                 * 
                 * //����acitivity group��״̬�� mActivityGroupState =
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

                // ����uipath����ĩ���ı�
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
                             * //2011-5-2 Ϊ����showuipath��ʱ����Ȼ�����������õı��⣡
                             */
                            lastFrameState[4] = title;
                            final ArrayList<HashMap<String, String>> pathInfo = getActPathInfo(lastFrameState[7]);
                            // Logger.d(TAG,lastFrameState[7]);
                            // ��ʾ�� ��������
                            showUiPath(pathInfo);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (action.equals(MainpageActivity.BACK)) {// ������һ��activity
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
                 * Log.d(TAG,"back to ��"+lastAct); View childView1 =
                 * getLocalActivityManager().startActivity( lastAct, new
                 * Intent(lastAct) .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP))
                 * .getDecorView(); FrameLayout frame = (FrameLayout) childView1
                 * .findViewById(android.R.id.content);
                 * frame.setForeground(null); container.removeAllViews();
                 * container.addView(childView1); }
                 */

                // back �߼� 20101126�޸�
                // ����ȷ������ǰ�����ĸ�����
                // �����returnActID�ģ�ֱ�Ӷ�ȡ���ã������������򷵻�actջǰһ��"��һ��"act��δ����level��actĬ����ͣ�ȡ10��

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

                        // �����߼������������ҳ����BACK��������ʾ�˳��Ķ���
                        // ��ʾ �˳�����Ӧ��
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
                        // Logger.i(TAG, "back to fixed <returnActID>��" +
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
                            // �����ջ�����еģ�ȡ�����״̬ȡ����
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
                            // ������� ��վ��û�е��½��棡 �������ﲻ����back��
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

                        // ���ء��ϼ���
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
                        // �����ء���һ����ʱ��Ҳ���Դ���һЩ������
                        Bundle bd = intent.getExtras();
                        Bundle sndBundle = new Bundle();
                        if (bd != null) {
                            // Logger.i(TAG, "BACK, have data!");
                            sndBundle = (Bundle) bd.clone();
                        } else {
                            // Logger.i(TAG, "BACK, not have data!");
                        }

                        sndBundle.putString("startType", "back");// ���� ����Ƿ��ض���
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
                    // ��ʾ �˳�����Ӧ��
                    // mHandler.sendEmptyMessage(201);
                }

            } /*
               * ����ʹ��else if (action.equals(MainpageActivity.SHOW_TIP)) {
               * Logger.i(TAG, "recv frame broadcast!" + action); Bundle bd =
               * intent.getExtras(); if (bd != null) { pviapfStatusTip =
               * bd.getString("pviapfStatusTip"); pviapfStatusTipTime =
               * bd.getString("pviapfStatusTipTime");
               * //Logger.i(TAG,"pviapfStatusTip:"
               * +pviapfStatusTip+",pviapfStatusTipTime:"+pviapfStatusTipTime);
               * 
               * // ��ʾ��ʾ��Ϣ showTip(pviapfStatusTip, pviapfStatusTipTime); }
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
                // ��ȫ����
                mStatusBar.setVisibility(View.GONE);
                mPviTitleBar.setVisibility(View.GONE);
                mBottomBar.setVisibility(View.GONE);
            } else if (action.equals(MainpageActivity.FULLSCREEN_OFF)) {
                // ȡ����ȫ����
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

                // ����actStack�е�״̬������������activityId�ҵ���Ӧframestate�����浽��9����
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

                    // ����tab��Ƕ�Ľ���
                    if (lastFrameState[6] != null
                            && !lastFrameState[6].equals("")) {
                        Logger.d(TAG, "inside TAB");

                        // ��ͬ��tabactivity����ͬһ��tabactivity�ڵ���ת�������߼���ͬ
                        /*
                         * ͬһ���ڣ����Ϳ����Ϣstart_activity����Я����Ҫ��show���ӽ��桱�Ĳ���������ͨ��view�Ĳ�ι�ϵ
                         * ��ȡ��tabhost��������setcurrenttab���� ����ͬһ���ڣ�ִ��Ƕ��
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
                         * 2011-7-12��ע�� if
                         * (lastFrameState[6].equals(actTabName1)) {
                         * //start�ĵȴ�showme��acttabname���showme�㲥��acttabname��ͬ
                         * ��addview��tabactivity Logger.d(TAG, "actTabName1:" +
                         * actTabName1
                         * +", equals lastFrameState[6], so, to run showChildView()"
                         * ); showChildView(); }else{
                         */
                        // �ж�����TabActivity�Ƿ��ȷ�Ѿ���ʾ�����ˣ���������Ҫshow���ģ���������ҵ����֣�Ȼ��û��ʾ�������Ϳ��ٵ��������⣩

                        try {
                            if (!container.getChildAt(0).getContext()
                                    .toString().contains(lastFrameState[0])) {
                                Logger.d(TAG, "to run showChildView()");
                                showChildView();
                            } else {
                                Logger.d(TAG, "noneed to run showChildView()");

                                // 2011-7-15 ��Ҫ����һ�±�������
                                // Ѱ������·�� Ma
                                final ArrayList<HashMap<String, String>> pathInfo = getActPathInfo(lastFrameState[7]);
                                // ��ʾ�� ��������
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

                        // ���sender�Ǳ�Ƕ��tab��activity�����ж�������ĳ��tabactivity����ҲҪִ��showChildView
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
                                            "sender�Ǳ�Ƕ��tab��activity�����ж�������ĳ��tabactivity����ҲҪִ��showChildView");
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
     * һ����̬������ʹ�ã����Ϳ����Ϣ
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

            // �ͷ�ǰһ����Ƕ��activity�е�view�еı�����
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

                // ����������allwaysCreate�ģ������µ�֮ǰ����ɱ�����е��ظ���
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

                // ����������allwaysCreate�ģ��������µ��Ժ�Ҫ��������id
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
                    msActStack.pop();// �Ƴ�����actջ�����ݣ����ֵ�ǰ��Ƕ����ͼ���仯
                }
                /*
                 * System.gc(); backtoHomeAcvitity();
                 */

                // ����
                return;
            }

            // �����治��Ҫ�첽��ʾ
            if (lastFrameState[2] != null
                    && lastFrameState[2]
                            .equals("com.pvi.ap.reader.activity.MainpageInsideActivity")) {
                // Logger.d(TAG,"�������ֱ����ʾ����  Ŀǰ�� ��ҳ");
                showChildView();
            } else {
                // Logger.d(TAG,"�����acitivyt�Ѿ����У��ȴ�SHOW_ME�㲥");
            }

            // childView1 = null;

            // ���������е���ʾ��
            // mHandler.sendEmptyMessage(101);
            // pd.dismiss();

            // displayAvailMemory();
        }
    };

    // private int flashNum = 0 ;

    /*
     * ������show���ӽ���
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

        // gc16 ˢȫ��һ��
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

        // ������ܵı���
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

        // ����������ͼ
        // Logger.i(TAG,"childView added!");
        if (lastFrameState[1] != null && lastFrameState[1].equals("0")
                && lastFrameState[3] != null && lastFrameState[3].equals("0")) {
            // ȫ��
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

            // ���������·����Ӧ����������ʾ��

            if (lastFrameState[3] != null && lastFrameState[3].equals("1")) {// �����������ʾ������
                // Logger.d(TAG,"��������ʾ������:"+lastFrameState[3]+",mailTitle:"+lastFrameState[4]);
                // Ѱ������·�� Ma
                final ArrayList<HashMap<String, String>> pathInfo = getActPathInfo(lastFrameState[7]);
                // ��ʾ�� ��������
                showUiPath(pathInfo);
            } else {
                // Logger.d(TAG,"����ʾ��������");
            }

            // HERE�� show���ӽ���
            container.addView(childView1);

            // ����allwayCreate�ģ�ɱ��֮ǰ�ظ���ʵ�� 2011-5-23 �߼��Ƶ�activity����ʱ
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

            // ������������
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

        // Ҫ��ȫ�����������
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
         * // �����������������ǡ����ء�������һ����ͼ��״̬����������show����״̬���� if ((thisActStartType !=
         * null && !thisActStartType.equals("back")) && !lastHaveStatusBar) { if
         * (lastFrameState[1] != null && lastFrameState[1].equals("1")) {
         * mStatusBar.setVisibility(View.VISIBLE); } } if ((thisActStartType !=
         * null && !thisActStartType.equals("back")) && lastHaveStatusBar) { if
         * (lastFrameState[1] != null && lastFrameState[1].equals("0")) {
         * mStatusBar.setVisibility(View.GONE); }
         * 
         * } if ((thisActStartType != null && thisActStartType.equals("back"))){
         * //���ص�ʱ�� }
         */

        // ״̬��
        if (lastFrameState[1] != null && lastFrameState[1].equals("0")) {
            // Logger.i(TAG,"hide PviAppFrame status bar");
            mStatusBar.setVisibility(View.GONE);
        } else {
            // Logger.i(TAG,"show PviAppFrame status bar");
            mStatusBar.setVisibility(View.VISIBLE);
        }

        // ����
        int newVisible = View.VISIBLE;
        if (lastFrameState[10] != null && lastFrameState[10].equals("0")) {
            newVisible = View.GONE;
        }
        if (newVisible != mBottomBar.getVisibility()) { // ��������״̬��������״̬��ͬʱ������Ҫ�ı�
            mBottomBar.setVisibility(newVisible);
        }
        /*---�����и�uiitem����/��״̬����
         * �߼���������activity���õ�������ԣ���showPager,showChaper�ȣ�������ؿؼ�����ʾ���         *      
         *      ֻ�ڱ�Ƕactivity��pviactivityʱ�Ŵ�������PviTabActivity����������ǶSHOW����ME��Ϣ�����߼��������ƴ���
         */
        final Activity childActivity = (Activity) childView1.getContext();
        if (childActivity instanceof PviActivity) {
            final PviActivity pa = (PviActivity) childActivity;
            // ���õײ�������

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

        // ������ //20100105�����߼����������ڰ�UI״̬���ײ��زĵı仯����:��/�ޱ�����ʱ��״̬���ĵײ������ǲ�ͬ��
        if (lastFrameState[3] != null && lastFrameState[3].equals("0")) {
            // Logger.i(TAG,"hide PviAppFrame title bar");

            mPviTitleBar.setVisibility(View.GONE);
            /*
             * 2011-3-18ȥ�����߼�
             * statusBarBottomBg=R.drawable.statusbar_bottom_2_ui2;
             * mHandler.sendEmptyMessage(UI_UPDATE_CHANGE_STATUSBAR_BOTTOM);
             */

        } else {
            // Logger.i(TAG,"show PviAppFrame title bar");

            statusBarBottomBg = R.drawable.statusbar_bottom_ui1;
            /*
             * 2011-3-18ȥ�����߼�
             * mHandler.sendEmptyMessage(UI_UPDATE_CHANGE_STATUSBAR_BOTTOM);
             */
            mPviTitleBar.setVisibility(View.VISIBLE);

        }

        // �������ı��Ĵ��� ��addchildview֮show���� ����Ѿ�����
        /*
         * if (lastFrameState[4] != null && !lastFrameState[4].equals("")) { //
         * Logger.i(TAG,"set title:"+lastFrameState[4]);
         * mtvTitleBarTitle.setText(lastFrameState[4]); }
         */

        if (((GlobalVar) getApplication()).deviceType == 1) {
            // ����������ˢһ��ȫ��Ļ

            /*
             * mHandler.post(new Runnable() { public void run() {
             * Logger.d(TAG,"getWindow().getDecorView().invalidate( ...");
             * getWindow().getDecorView().invalidate();
             * getWindow().getDecorView(
             * ).invalidate(View.EINK_DITHER_MODE_DITHER
             * |View.EINK_AUTO_MODE_REGIONAL| View.EINK_WAIT_MODE_NOWAIT |
             * View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL); } });
             */

            // ����tabactivity ʧ��
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

            // ��tab��ˢȫ����������
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

        // ȡȫ���ڴ������Ϣ
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

        // ȡ��ǰ���̵��ڴ������Ϣ

        // ȡ�ĵ�ǰ����pid
        final int curPid = android.os.Process.myPid();

        // ȡ��ǰ���̵��ڴ�ռ����Ϣ
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
            case 100:// ���� ������ ��ʾ��
                new Thread() {
                    public void run() {
                        mHandler.post(alertLoading);
                    }
                }.start();
                ;
                break;
            case 101:// ���� ������ ��ʾ��
                new Thread() {
                    public void run() {
                        mHandler.post(hideAlertDialog);
                    }
                }.start();
                break;
            case 201:// �˳��Ķ�����ȷ�Ͽ�
                // ȡ���˳�APP
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

            // �����͸���Activity�������¼�����Ϊ�е�activity��Ҫ�ڷ���֮ǰ����һЩ����

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
            // �ܾ�����down
            Logger.d(TAG, "refuse continue key down");
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN
                || event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {// ��������+
                // �����������ı�
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

            } else if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {// ��������-
                // �����������ı�
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
                    && event.getAction() == KeyEvent.ACTION_DOWN) {// �������ּ�
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

        // ���ӽ��潹���ƶ���������ʱ�����������£��򽹵��Ƶ� ���� ��
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
        // ��ʱע�͵����У���������ʲô����¼�������߼���
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

    // �����ʾ�� δ���ñ�������
    private Runnable alertNoBgMusic = new Runnable() {
        @Override
        public void run() {
            pd = new PviAlertDialog(mContext);
            pd.setTitle("��ܰ��ʾ");
            pd.setMessage("δ���ñ������֣��뵽���ҵ����֡������á�");
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // ��ת�� �������ֿ�����
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
            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "����",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // ��ת�� �������ֿ�����
                            return;
                        }
                    });
            pd.show();
        }
    };

    // �����ʾ��
    private Runnable alertLoading = new Runnable() {
        @Override
        public void run() {
            pd = new PviAlertDialog(MainpageActivity.this);
            pd.setTitle("��ܰ��ʾ");
            pd.setMessage("���ݼ����У����Ժ�...");
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

    // ȷ��Ҫ�˳�G3�Ķ�����
    private Runnable confirmExit = new Runnable() {
        @Override
        public void run() {
            pd = new PviAlertDialog(MainpageActivity.this);
            pd.setTitle("��ܰ��ʾ");
            pd.setMessage("��ȷ��Ҫ�˳�PVI-G3�Ķ�����");
            // CharSequence sureText;// = new CharSequence();
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ ��",
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

            pd.setButton(DialogInterface.BUTTON_NEGATIVE, "ȡ ��",
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
     * ȡActID����Щactivity��Ƕ��TAB����ģ�������Ҫ����������ȷ�����ĸ�����
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

    // ��actջ�и��ݸ���ActId����act������ActID
    private HashMap<String, Object> findLastActFromStackByActID(String actID) {
        /*
         * Logger.i(TAG, "start find actID:" + actID +
         * "...findLastActFromStackByActID");
         */
        HashMap<String, Object> result = new HashMap<String, Object>();
        Iterator iter = msActStack.iterator();
        int position = -1; // �������һ���ҵ���λ��
        String[] lastFrameState = new String[actStackSize];// �����ҵ���һ������
        int i = 0;
        while (iter.hasNext()) {

            String[] tempState = (String[]) iter.next();

            if (tempState[0] != null && tempState[7] != null) {
                /*
                 * Logger.i(TAG,"act:" + tempState[0]+"actID:" + tempState[7]);
                 */
            }

            if (tempState[7] != null && !tempState[7].equals("")) {// �б���actid�ģ�ֱ���ж�
                if (actID.equals(tempState[7])) {
                    position = i;
                    lastFrameState = tempState;
                    // Logger.i(TAG,
                    // "[actID.equals(tempState[7])] find at postion:"
                    // + position);
                }
            } else {// û����actID�ģ����Բ���һ��
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

        if (position > -1) {// �ҵ���
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
     * ��actջ�в��ҡ�����ġ����ȵ�ǰact��level�ߵ�һ��act�������ǰactδ����level���������һ��act
     * //������һ:��ǰact��ActID
     * 
     *���������û�У�����������actID��
     */

    // ��actջ��ȡ���ȸ���level�ߵġ����һ��act����Ϣ
    private String[] findLastHighlevelAct(String level) {
        if (level != null && !level.equals("")) {

            /*
             * Logger.i(TAG, "this Act have level:" + level +
             * ",find from msActStack, last,hige level:" + level +
             * ",msActStack size:" + msActStack.size());
             */

            String[] frameState = new String[actStackSize];// ����ֵ �����
            Iterator iter = msActStack.iterator();

            int lastP = -1; // postion �������һ���ҵ��� ��ʱ�ĳ���
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

                i++;// λ��+1
            }

            // ���position֮���ջ���ݣ�
            if (lastP != -1) {
                // msActStack.setSize(lastP);
                destroyNouseAct(lastP);
                // Logger.i(TAG, "msActStack modify: setSize lastLen:" +
                // lastLen);
            } else {
                // Logger.i(TAG, "noneed msActStack modify");
            }

            // û��ջ���ҵ� ����ġ�high level act
            // Logger.i(TAG,"lastP:"+lastP);
            if (lastP > -1) { // �ҵ��ˣ�������
                return frameState;
            } else {
                return msActStack.pop();
            }
        } else {
            //�����߼�������͵�ǰ������ͬһ���棬����Ҫ����pop
            
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
                 * i++;// λ��+1 }
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
                 * i++;// λ��+1 }
                 */

                msActStack.setSize(p);

            }
        }.start();

    }

    private void destroySameAct(String act) {
        Logger.d(TAG, "do destroy activity instances, find act:" + act);
        final String id = act;
        synchronized (msActStack) {// ������ͬʱ�޸�actջ ���߳�������������ȫ��Ŀǰ���޷������ȶ�
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
                        i++;// λ��+1
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
     * ��actջ��ɾ��
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
                i++;// λ��+1
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

        // ������ʾ������ȷ�� ���������棬���ٳ������������act

        // ��ע:�ڷ����ض�actStack�ĵط�������Ӧ��act���٣�

        super.onLowMemory();
    }

    // ��ʾ��������
    private void showNav() {
        //mPviTitleBar.updateNavSwitch(false);
        
        /*
         * //�㲥����Ϣ�������� Bundle bundleToSend = new Bundle();
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
         * //������ ����仯�¼����� mNav.setOnFocusChangeListener(new
         * OnFocusChangeListener(){
         * 
         * @Override public void onFocusChange(View arg0, boolean arg1) {
         * Logger.i(TAG,"onFocusChange"); // TODO Auto-generated method stub
         * if(!arg1){ hideNav(); } }});
         */

        curFocusView = mNav;
        mHandler.postDelayed(fouceView, 1000);
    }

    // ������������
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

    // �������� ͨ��listViewʵ��
    // �����������ݷ���һ��ArrayList<Object[]>��
    // ��string[]���δ�ţ�navTitle(R.string.xx),navImage(R.drawable.xx),actID("ACT11000")��ת���Ľ���

    private ArrayList<Object[]> navList = new ArrayList<Object[]>();

    private void initNavList() {
        Object[] navItem = new Object[4];

        // �����Ķ�
        navItem[0] = R.string.fp_cat0;
        navItem[1] = R.drawable.mainnav_image_0;
        navItem[2] = "ACT00000";
        navItem[3] = null;
        navList.add(navItem.clone());    
        
        // �������
        navItem[0] = R.string.fp_cat1;
        navItem[1] = R.drawable.mainnav_image_1;
        navItem[2] = "ACT19000";
        navItem[3] = null;
        navList.add(navItem.clone());

        // �ҵ����
        navItem[0] = R.string.fp_cat2;
        navItem[1] = R.drawable.mainnav_image_2;
        navItem[2] = "ACT12100";
        navItem[3] = null;
        navList.add(navItem.clone());
        // �������
        navItem[0] = R.string.fp_cat3;
        navItem[1] = R.drawable.mainnav_image_3;
        navItem[2] = "ACT13500";
        navItem[3] = null;
        navList.add(navItem.clone());
/*        // �ҵ���ע
        navItem[0] = R.string.fp_cat4;
        navItem[1] = R.drawable.mainnav_image_4;
        navItem[2] = "ACT13600";
        navItem[3] = null;
        navList.add(navItem.clone());*/
        // ��Դ����
        navItem[0] = R.string.fp_cat6;
        navItem[1] = R.drawable.mainnav_image_5;
        navItem[2] = "ACT13100";
        navItem[3] = null;
        navList.add(navItem.clone());
        // ���˿ռ�
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
                    // ��������ص�����
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

    // ��ȡ����·������
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
                    // ��ǰact�ı��⣬������ֵΪ׼��1��startActivityʱ������mainTitle
                    // 2��ͨ����SET_TITLE�㲥�����ñ��⣩
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
     * ��Ҫͨ����Ȩ��Activity��
     */
    private List<String> needAutoList = new ArrayList<String>();

    /**
     * ��ʼ��List ���ֻ��ʼ��һ�Ρ�
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
     * �Ƿ���Ҫ��Ȩ
     * 
     * @param act
     *            ��ת��Activity
     * @return �Ƿ���Ҫ��Ȩ
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
     * ����ʹ��
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
     * if(pviapfStatusTipTime==null){ pviapfStatusTipTime = "60000";//Ĭ�����ʾ30s
     * }
     * 
     * if (pviapfStatusTipTime != null) { long delay =
     * Long.parseLong(pviapfStatusTipTime); if (delay > 0) { Runnable hideTip =
     * new Runnable() {
     * 
     * @Override public void run() {
     * if(SystemClock.uptimeMillis()>=hidetipTime){//��ǰʱ�����������õ�����ʱ���ʱ
     * //mtvTip.setText(""); //����Ϣ���� sendBroadcast(new
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
            pad.setMessage("��������������...");
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

        // ������ˢ��һ��ȫ�� ��������
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
        // ������뷨Ŀǰ���ˣ���ǰ�ؼ�֮�������touch�����������뷨�����ز���
        if (ev.getAction() == MotionEvent.ACTION_DOWN
        /*
         * && ((InputMethodManager)
         * getSystemService(INPUT_METHOD_SERVICE)).isActive()
         */
        ) {
            // Logger.d(TAG,"����Ա�");
            final View curFocusedView = getWindow().getDecorView().findFocus();

            if (curFocusedView != null) {

                // ����������������

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
                        // Logger.d(TAG, "����֮");
                        PviUiUtil.hideInput(curFocusedView);
                    } else {
                        // Logger.d(TAG, "����������ڲ������������");
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

            // ����Ķ�Ϊ�����Ķ�
            if ("4".equals(sourceType)) {

                final PviAlertDialog dialog = new PviAlertDialog(
                        MainpageActivity.this);
                dialog.setTitle(getResources().getString(
                        R.string.systemconfig_pop_message));
                dialog.setMessage("����Ķ�Ϊ�����Ķ��������ȷ�����������Ķ��������ȡ������������ҳ��");
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
                                                "allwaysCreate"); // ÿ�ζ�createһ����ʵ���������ô˲���ʱ��Ĭ��Ϊ�����á����е�
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
                            .setMessage("�Բ�������Ķ��ļ��ѱ�ɾ���򲻴��ڣ����ȷ��ϵͳ��������ҳ��");
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
                    // "�Բ�������Ķ��ļ���ɾ����", Toast.LENGTH_LONG).show();
                }
            }
        } else {

            final PviAlertDialog alertDialog = new PviAlertDialog(
                    MainpageActivity.this);
            alertDialog.setTitle(getResources().getString(
                    R.string.systemconfig_pop_message));
            alertDialog.setMessage("�Բ��𣬻�û������Ķ������ȷ��ϵͳ��������ҳ��");
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