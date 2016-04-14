package com.pvi.ap.reader.activity.pviappframe;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.TimeSetActivity;
import com.pvi.ap.reader.data.common.Logger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.media.AudioManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.View;

/**
 * ״̬�� ��װ
 * @author rd040 ������
 *
 */
public class PviStatusBar extends View {
    private static final String TAG = "PviStatusBar";
    public final static String SHOW_TIP = "com.pvi.ap.reader.mainframe.SHOW_TIP";
    public final static String HIDE_TIP = "com.pvi.ap.reader.mainframe.HIDE_TIP";
    public static final String TIMEVIEW_CONFIG_CHANGED = "com.pvi.ap.reader.activity.TimeView.TIMEVIEW_CONFIG_CHANGED";

    private boolean mUpdating = false;
    private Context mContext;
    private Resources mRes;

    
    private Bitmap mBmp;
    
    //private HashMap<Integer,Float[]> aniList = new HashMap<Integer,Float[]>();//����״̬����Ŀǰ��ʾ�е� ani:  key  resid ��value  posX,width 
    private HashMap<Integer,HashMap<String,Object>> aniList = new HashMap<Integer,HashMap<String,Object>>();
    //��������
    private float mWidth;         //״̬������ʾ���� ���
    private float mHeight;         //״̬���߶�
    private float mHeightWithoutBottomLine;         //״̬������ʾ����߶�
    
    
    //ʱ��
    private String mDataFormat;
    private float timePosX;         //ʱ����ʾλ�� X
    private float timePosY;         //ʱ����ʾλ�� Y
    private float timeWidth;        //ʱ����ʾ����Ŀ��
    
    
    //tip
    private long hidetipTime;       //tip�����ص�����ʱ���
    private float tipPosX;         //tip��ʾλ�� X
    private float tipPosY;         //tip��ʾλ�� Y
    private float tipWidth;         //tip�Ŀ��
    private float tipIconPosX;      //tipͼ�����ʼx
    private float tipIconPosY;      //tipͼ�����ʼY
    private float tipIconWidth;     //tipͼ��Ŀ��
    private float tipIconHeight;     //tipͼ��ĸ߶�
    
    //battery
    private float batteryIconLeft;
    private float batteryIconTop;
    private float batteryIconWidth;
    private float batteryIconHeight;
    
    //download
    private float downloadIconLeft;
    private float downloadIconTop;
    private float downloadIconWidth;
    private float downloadIconHeight;
    
    // phone
    private TelephonyManager mPhone;
    private IBinder mPhoneIcon;
    // ***** Signal strength icons
    private PviIconData mPhoneData;
    // GSM/UMTS
    private static int[] sSignalImages = new int[] {
        R.drawable.stat_sys_signal_0_ui1,
        R.drawable.stat_sys_signal_1_ui1,
        R.drawable.stat_sys_signal_2_ui1,
        R.drawable.stat_sys_signal_3_ui1,
        R.drawable.stat_sys_signal_4_ui1 };
    private static int[] sSignalImages_r = new int[] {
        R.drawable.stat_sys_r_signal_0_ui1,
        R.drawable.stat_sys_r_signal_1_ui1,
        R.drawable.stat_sys_r_signal_2_ui1,
        R.drawable.stat_sys_r_signal_3_ui1,
        R.drawable.stat_sys_r_signal_4_ui1 };
    
    IccCardState mSimState = IccCardState.READY;
    int mPhoneState = TelephonyManager.CALL_STATE_IDLE;
    int mDataState = TelephonyManager.DATA_DISCONNECTED;
    int mDataActivity = TelephonyManager.DATA_ACTIVITY_NONE;
    ServiceState mServiceState;
    SignalStrength mSignalStrength;
    
    // data connection
    private IBinder mDataIcon;
    private PviIconData mDataData;
    private boolean mDataIconVisible;
    private boolean mHspaDataDistinguishable = true;
 // ***** Data connection icons
    private int[] mDataIconList = sDataNetType_g;
    // GSM/UMTS
    private static int[] sDataNetType_g = new int[] {
        R.drawable.stat_sys_data_connected_g_ui1,
        R.drawable.stat_sys_data_in_g_ui1,
        R.drawable.stat_sys_data_out_g_ui1,
        R.drawable.stat_sys_data_inandout_g_ui1 };
    private static int[] sDataNetType_3g = new int[] {
        R.drawable.stat_sys_data_connected_3g_ui1,
        R.drawable.stat_sys_data_in_3g_ui1,
        R.drawable.stat_sys_data_out_3g_ui1,
        R.drawable.stat_sys_data_inandout_3g_ui1 };
    private static int[] sDataNetType_e = new int[] {
        R.drawable.stat_sys_data_connected_e_ui1,
        R.drawable.stat_sys_data_in_e_ui1,
        R.drawable.stat_sys_data_out_e_ui1,
        R.drawable.stat_sys_data_inandout_e_ui1};
    // 3.5G
    private static int[] sDataNetType_h = new int[] {
        R.drawable.stat_sys_data_connected_h_ui1,
        R.drawable.stat_sys_data_in_h_ui1,
        R.drawable.stat_sys_data_out_h_ui1,
        R.drawable.stat_sys_data_inandout_h_ui1 };
    
    // usb_con
    private static int[] sUsbConImages = new int[] { 0, R.drawable.usb_con_connected_ui1 };
    private IBinder mUsbConIcon;
    private PviIconData mUsbConData;
    private boolean mIsUsbConnected = false;
    
    private boolean firstDraw = true;//��һ��draw�ı�־
    
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
    
    // icons
    private HashMap<IBinder, PviStatusBarIcon> mIconMap = new HashMap<IBinder, PviStatusBarIcon>();
    private String[] mRightIconSlots;
    private int gapWidth = 6;
    private float iconsPosY;
    private int mIconsWidthLast;

    public PviStatusBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (!isInEditMode()) {

            mContext = context;
            mRes = context.getResources();

            mWidth = 600f;
            mHeight = 36f;
            mHeightWithoutBottomLine = 32f;

            // ����layout�Ŀ�ȸ߶ȣ�����λͼ
            mBmp = Bitmap.createBitmap((int) mWidth, (int) mHeight,
                    Config.ARGB_8888);

            // ʱ��
            timePosX = 504f;
            timePosY = 24f;
            timeWidth = 100f;

            // tip
            tipPosX = 35f;
            tipPosY = 22f;
            tipWidth = 250f;
            tipIconPosX = 6f;
            tipIconPosY = 6f;
            tipIconWidth = 19f;
            tipIconHeight = 21f;
            
            //battery
            batteryIconLeft = 470f;
            batteryIconTop = 10f;
            batteryIconWidth = 26f;
            batteryIconHeight = 16f;
            
            //download
            downloadIconWidth = 15f;
            downloadIconHeight = 16f;
            downloadIconLeft = 300f;
            downloadIconTop = 10f;
            
            // icons
            mRightIconSlots = mRes
                    .getStringArray(R.array.status_bar_icon_order);
            iconsPosY = 0f;

            // battery ģ�����޷����Ա仯��
            mBatteryData = PviIconData.makeIcon("battery", null, R.drawable.battery_unknown_ui1, 0, 0,0, 26, true);
            //mBatteryIcon = addIcon(mBatteryData);

            // volume
            mVolumeData = PviIconData.makeIcon("volume", null,
                    R.drawable.volume_sys_ui1, 0, 0, 8,25, true);
            mVolumeIcon = addIcon(mVolumeData);
            updateVolume();

            // msg
            mMsgData = PviIconData.makeIcon("msg", null,
                    R.drawable.have_unread_message, 0, 0,4, 19, false);
            mMsgIcon = addIcon(mMsgData);

            // sdcard
            mSdcardData = PviIconData.makeIcon("sdcard", null,
                    R.drawable.stat_notify_sdcard_not, 0, 0, 6,14, true);   //Ĭ����ʾ
            mSdcardIcon = addIcon(mSdcardData);
            //��ʼ�������һ��  ����п������ظ�ͼ��
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
                updateSdcard(1);
            }

            // music
            mMusicData = PviIconData.makeIcon("music", null,
                    R.drawable.state_bgmusic, 0, 0,6, 15, false);
            mMusicIcon = addIcon(mMusicData);
            
            //dowload

            
            //phone
         // phone_signal
            mPhone = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);

            mPhoneData = PviIconData.makeIcon("phone_signal", null,
                        R.drawable.stat_sys_signal_null_ui1, 0, 0,4,25,true);
             
            

            mPhoneIcon = addIcon(mPhoneData);

            // register for phone state notifications.
            ((TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE)).listen(
                    mPhoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE
                            | PhoneStateListener.LISTEN_SIGNAL_STRENGTHS
                            | PhoneStateListener.LISTEN_CALL_STATE
                            | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
                            | PhoneStateListener.LISTEN_DATA_ACTIVITY);
            
            // data_connection
            mDataData = PviIconData.makeIcon("data_connection", null,
                    0, 0, 0,2,10,false);
            mDataIcon = addIcon(mDataData);
            
           // usb_con
            mUsbConData = PviIconData
                    .makeIcon("usb_con", null, sUsbConImages[0], 0, 0,2,18,false);
            mUsbConIcon = addIcon(mUsbConData);
        }
    }

    
    
    private BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            //Logger.d(TAG,"receive action:"+action);
            
            if (action.equals(Intent.ACTION_TIME_TICK)
                    || action.equals(Intent.ACTION_TIMEZONE_CHANGED)||action.equals(Intent.ACTION_TIME_CHANGED)) {
                updateClock();
            }else if(action.equals(TIMEVIEW_CONFIG_CHANGED)){
                try {
                    SharedPreferences settings = context.getSharedPreferences(com.pvi.ap.reader.data.common.Config.getString("configFileName"), Activity.MODE_WORLD_READABLE|Activity.MODE_WORLD_WRITEABLE); 
                    if(settings!=null){
                        mDataFormat = settings.getString(TimeSetActivity.DATE_FORMAT,"");
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                updateClock();
            }
            
            //tip
            else if (SHOW_TIP.equals(action)) {
                //ȡ������
                String tiptype = intent.getStringExtra("tiptype");
                String tipText = intent.getStringExtra("pviapfStatusTip");
                
                //�����ӳ�
                String timeout = intent.getStringExtra("pviapfStatusTipTime");
/*                if(timeout==null){
                    timeout = "60000";
                }*/
                
                //����tip ����ʾ
                if(tipText!=null&&!tipText.equals("")){
                    updateTip(tiptype,tipText);
                    hideTip(timeout);
                }
                
            }else if(HIDE_TIP.equals(action)) {//�յ��˹㲥��tip�������� 2011-7-8��Ϊ�����ӳ�1s
                hideTip("1000");           
            }
            
            //battery
            else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                updateBattery(intent);
            } 
            
            //volume
            else if (VOLUME_CHANGED.equals(action)) {                
                updateVolume();
            }
            
            //msg            
            else if (MESSAGE_UPDATE.equals(action)) {
                int state = intent.getIntExtra("STATE", 0);
                updateMsg(state);
            }           
            //music
            else if(UPDATE_BGMUSIC_STATE.equals(action)){
                String newStateStr = "";
                Bundle bd = intent.getExtras();
                if(bd!=null){
                    newStateStr = bd.getString("state");
                    updateMusic(newStateStr);
                }
                
                
            }
            
            //download            
            else if (DOWNLOAD_STATE_UPDATE.equals(action)) {
                int state = intent.getIntExtra("STATE", 0);
                updateDownloadState(state);
            }
            
            else if (Intent.ACTION_UMS_CONNECTED.equals(action)) {
                mIsUsbConnected = true;
                updateUsbCon(intent);
            } else if (Intent.ACTION_UMS_DISCONNECTED.equals(action)) {
                mIsUsbConnected = false;
                updateUsbCon(intent);
            }
            
            
        }
    };
    
    private BroadcastReceiver mIntentReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if(Intent.ACTION_MEDIA_MOUNTED.equals(action)){
                updateSdcard(1);
            }else if(Intent.ACTION_MEDIA_REMOVED.equals(action)
                    ||Intent.ACTION_MEDIA_UNMOUNTED.equals(action)
                    ||Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)){
                updateSdcard(2);
            }
        }
    };
 
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        
        if(!isInEditMode()){
            setUpdates(true);
        }

    }
    
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(!isInEditMode()){
            setUpdates(false);
        }
    }
    
    void setUpdates(boolean update) {
        if (update != mUpdating) {
            mUpdating = update;
            if (update) {
                // Register for Intent broadcasts for the clock and battery
                IntentFilter filter = new IntentFilter();
                
                //��ʾ��
                filter.addAction(SHOW_TIP);
                filter.addAction(HIDE_TIP);
                
                //ʱ��
                filter.addAction(Intent.ACTION_TIME_TICK);
                filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
                filter.addAction(Intent.ACTION_TIME_CHANGED);
                filter.addAction(TIMEVIEW_CONFIG_CHANGED);
                filter.addAction(Intent.ACTION_SCREEN_ON);
                
                //battery
                filter.addAction(Intent.ACTION_BATTERY_CHANGED);
                
                //volume
                filter.addAction(VOLUME_CHANGED);
                
                //msg
                filter.addAction(MESSAGE_UPDATE);

                //music
                filter.addAction(UPDATE_BGMUSIC_STATE);
                
                //download
                filter.addAction(DOWNLOAD_STATE_UPDATE);
                
                // usb ��Ϣ
                filter.addAction(Intent.ACTION_UMS_CONNECTED);
                filter.addAction(Intent.ACTION_UMS_DISCONNECTED);
                
                mContext.registerReceiver(mIntentReceiver, filter, null, null);
                
                IntentFilter filter2 = new IntentFilter();
               
                // sdcard
                filter2.addAction(Intent.ACTION_MEDIA_MOUNTED);
                filter2.addAction(Intent.ACTION_MEDIA_REMOVED);
                filter2.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
                filter2.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
                filter2.addDataScheme("file");

                mContext.registerReceiver(mIntentReceiver2, filter2, null, null);
            } else {
                mContext.unregisterReceiver(mIntentReceiver);
                mContext.unregisterReceiver(mIntentReceiver2);
            }
        }
    }
  
    

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        
        if(!isInEditMode()){
        //Logger.d(TAG,"onDraw");
        
        if(firstDraw){
            

            addBg();
            
            updateClock();
            
            
            firstDraw = false;
        }
        
        Paint paint = new Paint();
        canvas.drawBitmap(mBmp, 0, 0, paint);
        
        }
        
        //super.onDraw(canvas);

    }
    
    /**
     * ����ʱ��
     */
    private final void updateClock() {
        //Logger.d(TAG,"update clock");
        
        final Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd HH:mm");
        if(TimeSetActivity.DATE_FORMAT_12.equals(mDataFormat)){
            formatter = new SimpleDateFormat("MM/dd hh:mm");
        }
        
        Paint paint = new Paint();       

        Canvas tmpCv = new Canvas(mBmp);
        paint.setColor(Color.WHITE);
        tmpCv.drawRect( timePosX, 0,mWidth,mHeightWithoutBottomLine, paint);       

        paint.setColor(Color.BLACK);
        paint.setTextSize(16f);
        paint.setFakeBoldText(true);
        paint.setAntiAlias(true);
        tmpCv.drawText(formatter.format(now), timePosX, timePosY, paint);

        invalidate();
    }
    
    /**
     * ����״̬���ı���
     */
    private void addBg(){
        Canvas tmpCv = new Canvas(mBmp);
        
        Bitmap bmp = BitmapFactory.decodeResource(mRes, R.drawable.statusbar_bottom_ui1);
        Paint paint = new Paint();
        tmpCv.drawBitmap(bmp, 0, mHeightWithoutBottomLine, paint);
    }
    
    /**
     * ��״̬����ĳ����ˢ��
     * ��������ʼx,���
     */
    private void drawWhite(float x,float width){
        final Canvas tmpCv = new Canvas(mBmp);
        final Paint paint = new Paint(); 
        paint.setColor(Color.WHITE);
        tmpCv.drawRect( x, 0,x+width,mHeightWithoutBottomLine, paint);
    }
    
    /**
     * ��״̬��ĳ����
     * ��������ʼλ�� left��top   ��Դid
     * ��ѡ������level  ����LevelListDrawable��Ҫ�˲���
     * ����Drawable  :��Ҫ����Drawable ��LevelListDrawable��AnimationDrawable,Gif(?)
     */
    private void drawRes(float left ,float top,float width,float height,int resid,int level,boolean isReplaceOld){
        //Logger.d(TAG,"drawRes(...");      
        
        Drawable d = mRes.getDrawable(resid);
        if(d!=null){
            Bitmap bitmap = null;
            if(d instanceof BitmapDrawable){
                bitmap = ((BitmapDrawable)d).getBitmap();
                //����bitmap
                Canvas tmpCv = new Canvas(mBmp);
                Paint paint = new Paint();
                tmpCv.drawBitmap(bitmap, left, top, paint);             
                
            }else if(d instanceof LevelListDrawable){
                LevelListDrawable lld = (LevelListDrawable)d;
                lld.setLevel(level);
                Drawable dd =lld.getCurrent();
                if(dd instanceof BitmapDrawable){
                    bitmap = ((BitmapDrawable)dd).getBitmap();
                    //����bitmap
                    Canvas tmpCv = new Canvas(mBmp);
                    Paint paint = new Paint();
                    tmpCv.drawBitmap(bitmap, left, top, paint);
                }else if(dd instanceof AnimationDrawable){
                    //����ani
                    drawAni(left ,top,width,height,resid,(AnimationDrawable)dd,isReplaceOld);
                }
            }else if(d instanceof AnimationDrawable){
                //����ani  
                drawAni(left ,top,width,height,resid);
            }else{
                //Logger.d(TAG,d);
            }
        }
    }

    
    private void drawAni(float left ,float top,float width,float height,int resid){
      //����ö����Ѿ������У��򲻼���ִ��
        final HashMap<String,Object> tempHm = aniList.get(resid);
        if(tempHm!=null){
            return;
        }
        
        PviAnimationDrawable ani = new PviAnimationDrawable(this,resid,new Canvas(mBmp),left,top,width,height,mRes);
        ani.start();

        HashMap<String,Object> hm = new HashMap<String,Object>();
        hm.put("ani", ani);
        hm.put("left", left);
        hm.put("width", width);
        aniList.put(resid, hm);     //��Ŷ���״̬
    }
    
    /**
     * ����resid������animationDrawable��xml��
     * @param left
     * @param top
     * @param width
     * @param height
     * @param resid
     * @param ad
     * @param isReplaceOld    //�Ƿ��滻���ɵģ�stop�ɵģ�start�µ�
     */
    private void drawAni(float left ,float top,float width,float height,int resid,AnimationDrawable ad,boolean isReplaceOld){
        
        if(!isReplaceOld){
        //����ö����Ѿ������У��򲻼���ִ��
          final HashMap<String,Object> tempHm = aniList.get(resid);
          if(tempHm!=null){
              return;
          }
        }else{
            final HashMap<String,Object> tempHm = aniList.get(resid);
            if(tempHm!=null){
                PviAnimationDrawable ani = (PviAnimationDrawable)tempHm.get("ani");
                if(ani!=null){
                    ani.stop();
                }
            }
        }
          
          PviAnimationDrawable ani = new PviAnimationDrawable(this,new Canvas(mBmp),ad,left,top,width,height);
          ani.start();

          HashMap<String,Object> hm = new HashMap<String,Object>();
          hm.put("ani", ani);
          hm.put("left", left);
          hm.put("width", width);
          aniList.put(resid, hm);     //��Ŷ���״̬
      }

    /**
     * ����icon �� text
     * tiptype    tip����
     * text       ��Ϣ�ı�
     * timeout    ���ó�ʱʱ��
     * 
     */
    private final void updateTip(String tiptype,String tipText) {
        
        //Logger.i(TAG,"updateTip!");
        
        //ˢ��
        drawWhite(tipIconPosX,tipIconWidth+tipWidth);
        
        if(tiptype==null){
            tiptype = "1";
        }

        if(tiptype.equals("1")){

            //��ʾ������ͼ�� 

            //����
            /*Canvas tmpCv = new Canvas(mBmp);
            Paint paint = new Paint(); 
            Bitmap bmpIcons = BitmapFactory.decodeResource(mRes, R.drawable.loading);
            tmpCv.drawBitmap(bmpIcons, tipIconPosX, tipIconPosY, paint);*/

            drawRes(tipIconPosX,tipIconPosY,tipIconWidth,tipIconHeight,R.anim.loadingtest,-1,false);
            
        }else if(tiptype.equals("2")){
            //...
        }

        if(!"".equals(tipText)){
            
            Paint paint = new Paint();       
            Canvas tmpCv = new Canvas(mBmp);       
            paint.setColor(Color.BLACK);
            paint.setTextSize(18f);
            paint.setAntiAlias(true);
            tmpCv.drawText(tipText, tipPosX, tipPosY+2, paint);

            invalidate();

        }

    }
    
    /**
     * �����Լ�
     */
    private final void hideTip(String timeout){
        if (timeout != null) {//��ʱ����
            long delay = 0;
            try {
                delay = Long.parseLong(timeout);
            } catch (NumberFormatException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (delay > 0) {
                Runnable hideTip = new Runnable() {
                    @Override
                    public void run() {
                        if(SystemClock.uptimeMillis()>=hidetipTime){//��ǰʱ�����������õ�����ʱ���ʱ
                            
                            //Logger.d(TAG,"[delay hideTip(...]  stopAni, draw white");
                            
                            //�رն���
                            stopAni(R.anim.loadingtest);
                            
                            //ˢ��
                            drawWhite(tipIconPosX,tipIconWidth+tipWidth);
                            invalidate();
                            
                        }
                    }
                };
                hidetipTime = SystemClock.uptimeMillis()+delay;
                //Logger.d(TAG,"delay "+delay+" ms, will run hideTip...");
                mHandler.postAtTime(hideTip, hidetipTime);
            }
        }else{//2011-7-14 ���û�����ã����������أ���ע��֮
/*          //�رն���
            stopAni(R.anim.loadingtest);
            
          //ˢ��
            drawWhite(tipIconPosX,tipIconWidth+tipWidth);
            
            invalidate();
            Logger.d(TAG,"instance hideTip(...  stopAni, draw white");*/
        }

        
    }
    
  /**�رն���
   * 
   * @param resid
   */
private void stopAni(int resid){
    //Logger.d(TAG,"stop Ani resid:"+resid);
    HashMap<String,Object> hm = aniList.get(resid);
    if(hm!=null){
        PviAnimationDrawable ani = (PviAnimationDrawable)hm.get("ani");
        ani.stop();
        aniList.remove(resid);
    }
}
    
    /**
     * ȡiconλ��
     * @param slot
     * @return
     */
    private int getRightIconIndex(String slot) {
        final int N = mRightIconSlots.length;
        for (int i = 0; i < N; i++) {
            if (mRightIconSlots[i].equals(slot)) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * ����icon
     * @param data
     * @return
     * @throws Exception 
     */
    public IBinder addIcon(PviIconData data)  {
        int slot;
        // assert early-on if they using a slot that doesn't exist.
        if (data != null) {
            slot = getRightIconIndex(data.slot);
            if (slot < 0) {
                throw new SecurityException("invalid status bar icon slot: "
                        + (data.slot != null ? "'" + data.slot + "'" : "null"));
            }
        } else {
            slot = -1;
        }
        IBinder key = new Binder();

        updateIcon(key, data);

        return key;
    }


    /**
     * ����ͼ��
     * @throws Exception 
     */
    private void updateIcon(IBinder key, PviIconData data) {        

        //Logger.d(TAG,"updateIcon(...key:"+key);
        
        
        PviStatusBarIcon icon = mIconMap.get(key);
        if (icon == null) {
            // add
            //Logger.d(TAG,"add a new icon:"+key);
            icon = new PviStatusBarIcon(mContext, data);
            mIconMap.put(key, icon);
            //mIconList.add(icon);



        } else {
            //Logger.d(TAG,"data.isVisble:"+data.isVisble);
            //Logger.d(TAG,"icon ...isVisble:"+icon.mData.isVisble);
            // right hand side icons -- these don't reorder
            icon.update(mContext, data);
            //Logger.d(TAG,"icon ...isVisble:"+icon.mData.isVisble);
            
            //����map��
            //mIconMap.put(key, icon);

        }
        
        //�����߼�
        reDrawIcons();
    };    
    
    /**
     * �ػ�icon��
     */
    private void reDrawIcons(){        
        //Logger.d(TAG,"reDrawIcons");
        
        final int iconsWidth = getVisibleIconWidthTotal();
        //Logger.d(TAG,"iconsWidth:"+iconsWidth+",(int)mHeightWithoutBottomLine:"+(int)mHeightWithoutBottomLine);
        Bitmap bmpIcons = Bitmap.createBitmap(iconsWidth,(int)mHeightWithoutBottomLine,Config.ARGB_8888);

        
        Canvas cv = new Canvas(bmpIcons);
        Paint paint = new Paint();
        int curDrawPosX = 0;
        final int len = mRightIconSlots.length;
        for (int i = 0; i < len; i++) {
            PviStatusBarIcon icon = getIconBySlot(mRightIconSlots[i]);
            if(icon!=null&&icon.mData!=null&&icon.mData.isVisble){
                //Logger.d(TAG,"draw icon :"+mRightIconSlots[i]);
                Drawable d = PviStatusBarIcon.getIcon(mContext, icon.mData);
                Bitmap bitmap = null;
                if(d instanceof BitmapDrawable){
                    bitmap = ((BitmapDrawable)d).getBitmap();
                }else if(d instanceof LevelListDrawable){
                    Drawable dd =((LevelListDrawable)d).getCurrent();
                    if(dd instanceof BitmapDrawable){
                        bitmap = ((BitmapDrawable)dd).getBitmap();
                    }else if(dd instanceof AnimationDrawable){
                        Drawable ddd = ((AnimationDrawable)dd).getCurrent();
                        bitmap = ((BitmapDrawable)ddd).getBitmap();
                    }
                }
                if(bitmap!=null){
                    cv.drawBitmap(bitmap, curDrawPosX, icon.mData.top, paint);
                    curDrawPosX = curDrawPosX+icon.mData.width+gapWidth;
                }
            }else{
                //Logger.d(TAG,"this icon is null or not visble!");
            }
        }
        
        //�ػ�״̬���ϵ�icons����
        //ˢ��   (��Ϊbitmap��ȷ����˱仯)

        Canvas tmpCv = new Canvas(mBmp);
        if(mIconsWidthLast>0){
            paint.setColor(Color.WHITE);
            final float iconsPosX = mWidth-(mIconsWidthLast+batteryIconWidth+timeWidth+gapWidth);//������һ״̬�Ŀ�ȶ���
            
            //Logger.d(TAG,"clear from x:"+iconsPosX+",to right:"+(mWidth-batteryIconWidth-timeWidth));
            
            tmpCv.drawRect(iconsPosX , 0, mWidth-batteryIconWidth-timeWidth-gapWidth, mHeightWithoutBottomLine, paint);   
        }
        
        if(iconsWidth>0){//���>0ʱ����Ҫ����
            final float iconsPosX = mWidth-(iconsWidth+batteryIconWidth+timeWidth+gapWidth);//���ݱ��ο�ȶ���
            tmpCv.drawBitmap(bmpIcons, iconsPosX, iconsPosY, paint);
            
            //��¼�ÿ��
            mIconsWidthLast = iconsWidth;
            //Logger.d(TAG,"mIconsWidthLast:"+mIconsWidthLast);
            
            invalidate();
        }
        
    }
    
    private int getVisibleIconWidthTotal(){
        int widthTotal = 0;
        //final int size = mIconMap.size();
        //Logger.d(TAG,"mIconMap.size():"+size);
        Set<IBinder> set = mIconMap.keySet();
        Iterator<IBinder> iter = set.iterator();
        while (iter.hasNext()) {
            PviStatusBarIcon icon = mIconMap.get(iter.next());
            if(icon!=null&&icon.mData!=null){
                if(icon.mData.isVisble){
                    //Logger.d(TAG,"widthTotal +,now widthTotal is :"+widthTotal);
                    widthTotal = widthTotal+icon.mData.width+gapWidth;                    
                }else{
                    //Logger.d(TAG,"is not visble");
                }
            } 
        }

        return widthTotal;
    }
 
    /**
     * ͨ��slot�ַ�����hashMap��ȡ��һ��icon
     * @param slot
     * @return
     */
    private PviStatusBarIcon getIconBySlot(String slot){
        //final int size = mIconMap.size();
        //Logger.d(TAG,"mIconMap.size():"+size);
        Set<IBinder> set = mIconMap.keySet();
        Iterator<IBinder> iter = set.iterator();
        while (iter.hasNext()) {
            PviStatusBarIcon icon = mIconMap.get(iter.next());
            if(icon!=null&&icon.mData!=null&&icon.mData.slot!=null&&icon.mData.slot.equals(slot)){
                return icon;
            } 
        }
        return null;
    }
    
    // battery
    //private IBinder mBatteryIcon;
    private PviIconData mBatteryData;

    // ���µ��
    private final void updateBattery(Intent intent) {
        //Logger.d(TAG,"updateBattery");
        boolean plugged = intent.getIntExtra("plugged", 0) != 0;
        int level = intent.getIntExtra("level", 0);
        if (plugged) {
            mBatteryData.iconId = R.drawable.battery_charge_ui1;            
            mBatteryData.iconLevel = level;
            drawRes(batteryIconLeft, batteryIconTop, batteryIconWidth, batteryIconHeight, mBatteryData.iconId, mBatteryData.iconLevel,true);
        }else{
            mBatteryData.iconId = R.drawable.battery_ui1;
                stopAni(R.drawable.battery_charge_ui1);   //�ε���ֹͣ����綯��
                mBatteryData.iconLevel = level;
                drawRes(batteryIconLeft, batteryIconTop, batteryIconWidth, batteryIconHeight, mBatteryData.iconId, mBatteryData.iconLevel,true);
                invalidate();            
        }
    }
    
    // ringer volume
    private IBinder mVolumeIcon;
    private PviIconData mVolumeData;
    public final static String VOLUME_CHANGED = "com.pvi.ap.reader.mainframe.VOLUME_CHANGED";//ϵͳ���������˱仯
    private final void updateVolume() {
        final AudioManager audioManager = (AudioManager) mContext .getSystemService(Context.AUDIO_SERVICE);
        /*����
         * final int ringerMode = audioManager.getRingerMode();
        mVolumeData.iconId = sVolumeImages[ringerMode];*/
        
        //ϵͳ����
        final int curSysVolume = audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM );
        final int maxSysVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM );
        //����ͼ����Դ��level
        mVolumeData.iconLevel = curSysVolume*100/maxSysVolume;
        //Logger.i(TAG,"curSysVolume*100/maxSysVolume:"+curSysVolume+","+maxSysVolume+","+curSysVolume*100/maxSysVolume);
        updateIcon(mVolumeIcon, mVolumeData);

    }
    
    //-------------------------------------
    //message
    private IBinder mMsgIcon;
    private PviIconData mMsgData;
    public final static String MESSAGE_UPDATE = "com.pvi.ap.reader.messageview.MESSAGE_UPDATE";// �ж��Ƿ��Ѿ�����δ����Ϣ�����������Ϊ����STATE=��HAVENEW��||��NONEW��
    public final static int HAVENEW = 1;
    public final static int NONEW = 2;
    private final void updateMsg(int state) {
        if(state==HAVENEW){
            mMsgData.iconId = R.drawable.have_unread_message;
            mMsgData.isVisble = true;
        }else if(state==NONEW){
            mMsgData.isVisble = false;
        }
        updateIcon(mMsgIcon, mMsgData);
    }
    
    //sdcard
    private IBinder mSdcardIcon;
    private PviIconData mSdcardData;
    /**
     * ����sdcard��icon״̬
     * @param state     1  �п�    2�޿�
     */
    private final void updateSdcard(int state) {
        if(state==1){      
            mSdcardData.isVisble = false;
        }else if(state==2){
            mSdcardData.iconId = R.drawable.stat_notify_sdcard_not;
            mSdcardData.isVisble = true;
        }
        updateIcon(mSdcardIcon, mSdcardData);
    }
    
    //music
    private IBinder mMusicIcon;
    private PviIconData mMusicData;
    public static final String UPDATE_BGMUSIC_STATE = "com.pvi.ap.reader.service.backgroundmusicservice.state";
    
    private final void updateMusic(String newStateStr) {

        final GlobalVar app = (GlobalVar)mContext.getApplicationContext();
            
            int lastMusicFlag = app.musicFlag;
        
            if("play".equals(newStateStr)){
                //�޸�ȫ�ֱ���                
                app.musicFlag = 1;                
                mMusicData.iconId = R.drawable.state_bgmusic;
                mMusicData.isVisble = true;
                //Logger.d(TAG,"music:play");
            }else{//else if("stop".equals(newStateStr)){             
                //�޸�ȫ�ֱ���
                app.musicFlag = 0;                
                mMusicData.isVisble = false;
                //Logger.d(TAG,"music:stop");
            }
            
            if(lastMusicFlag==-1||app.musicFlag!=lastMusicFlag){            //����ǵ�һ��   �������ϴ�״̬��ͬ
                //Logger.d(TAG,"update music icon");
                updateIcon(mMusicIcon, mMusicData);
            }else{
                //Logger.d(TAG,"noneed to update music icon");
            }
            
     
    }
    
    //dowanload
    public final static String DOWNLOAD_STATE_UPDATE = "com.pvi.ap.reader.DownloadStateView.DOWNLOAD_STATE_UPDATE";
    public final static int DOWNLOADING = 1;//������
    public final static int NOT_DOWNLOADING = 2;//û���������������    
    private final void updateDownloadState(int state) {
        if(state==DOWNLOADING){
            //downloadIconLeft = mWidth-(downloadIconWidth+mIconsWidthLast+batteryIconWidth+timeWidth);
            drawRes(downloadIconLeft,downloadIconTop,downloadIconWidth,downloadIconHeight,R.anim.downloading,-1,true);
        }else if(state==NOT_DOWNLOADING){
          //�رն���
            stopAni(R.anim.downloading);
            
            //ˢ��
            drawWhite(downloadIconLeft,downloadIconWidth);
            invalidate();
        }       
    }
    
    
 // ����绰�ź�
    public enum IccCardState {
        UNKNOWN, ABSENT, PIN_REQUIRED, PUK_REQUIRED, NETWORK_LOCKED, READY, NOT_READY;
    }
    
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            mSignalStrength = signalStrength;            
            updateSignalStrength();
        }

        @Override
        public void onServiceStateChanged(ServiceState state) {
            mServiceState = state;
            updateSignalStrength();
            updateDataIcon();
        }



        @Override
        public void onDataActivity(int direction) {
            mDataActivity = direction;
            updateDataIcon();
        }

        @Override
        public void onDataConnectionStateChanged(int state) {
            mDataState = state;
            updateDataIcon();
        }
        
        @Override
        public void onDataConnectionStateChanged(int state, int networkType) {
            //final GlobalVar app = ((GlobalVar) getApplicationContext());
            //if(app.getSimType().equals("SIM")||app.getSimType().equals("USIM")){//�в忨����Ҫ�����߼�
                mDataState = state;
                updateDataNetType(networkType);
                updateDataIcon();
            //}
        }
        
    };

    // �����ź�ǿ��
    private boolean isCdma() {
        return (mSignalStrength != null) && !mSignalStrength.isGsm();
    }

    private final void updateSignalStrength() {
        int iconLevel = -1;
        int[] iconList;

        // Display signal strength while in "emergency calls only" mode
        if (!hasService()||mSignalStrength==null) {  //���ӵ����������������Ϊnullʱ����Ϊû�з���
            mPhoneData.iconId = R.drawable.stat_sys_signal_null_ui1;
            
            updateIcon(mPhoneIcon, mPhoneData);
            // ����������ʽͼ�� �߼���û�ź��˶����϶�û������������
            mDataData.isVisble = false;
            updateIcon(mDataIcon, mDataData);

        } else {
            // ����������ʽͼ��Ϊ�ɼ� ���źŲ�һ�������������ӣ��������ﲻ��������
            // setIconVisibility(mDataIcon, true);

            if (!isCdma()) {
                int asu = mSignalStrength.getGsmSignalStrength();

                // ASU ranges from 0 to 31 - TS 27.007 Sec 8.5
                // asu = 0 (-113dB or less) is very weak
                // signal, its better to show 0 bars to the user in such cases.
                // asu = 99 is a special case, where the signal strength is
                // unknown.
                if (asu <= 2 || asu == 99)
                    iconLevel = 0;
                else if (asu >= 12)
                    iconLevel = 4;
                else if (asu >= 8)
                    iconLevel = 3;
                else if (asu >= 5)
                    iconLevel = 2;
                else
                    iconLevel = 1;

                // Though mPhone is a Manager, this call is not an IPC
                if (mPhone.isNetworkRoaming()) {
                    iconList = sSignalImages_r;
                } else {
                    iconList = sSignalImages;
                }
            } else {
                iconList = sSignalImages;

                // If 3G(EV) and 1x network are available than 3G should be
                // displayed, displayed RSSI should be from the EV side.
                // If a voice call is made then RSSI should switch to 1x.

                iconLevel = getCdmaLevel();

            }        
            mPhoneData.iconId = iconList[iconLevel];
            updateIcon(mPhoneIcon, mPhoneData);
        }

    }

    private int getCdmaLevel() {
        final int cdmaDbm = mSignalStrength.getCdmaDbm();
        final int cdmaEcio = mSignalStrength.getCdmaEcio();
        int levelDbm = 0;
        int levelEcio = 0;

        if (cdmaDbm >= -75)
            levelDbm = 4;
        else if (cdmaDbm >= -85)
            levelDbm = 3;
        else if (cdmaDbm >= -95)
            levelDbm = 2;
        else if (cdmaDbm >= -100)
            levelDbm = 1;
        else
            levelDbm = 0;

        // Ec/Io are in dB*10
        if (cdmaEcio >= -90)
            levelEcio = 4;
        else if (cdmaEcio >= -110)
            levelEcio = 3;
        else if (cdmaEcio >= -130)
            levelEcio = 2;
        else if (cdmaEcio >= -150)
            levelEcio = 1;
        else
            levelEcio = 0;

        return (levelDbm < levelEcio) ? levelDbm : levelEcio;
    }

    // ���� �������� ״̬��ʾ
    private boolean hasService() {
        final GlobalVar app = ((GlobalVar) mContext.getApplicationContext());
        if(!app.getSimType().equals("SIM")&&!app.getSimType().equals("USIM")){
            Logger.e(TAG,"no sim or usim card !");
            return false;
        }
        if (mServiceState != null) {
            switch (mServiceState.getState()) {
            case ServiceState.STATE_OUT_OF_SERVICE:
            case ServiceState.STATE_POWER_OFF:
                return false;
            default:
                return true;
            }
        } else {
            return false;
        }
    }

    private final void updateDataIcon() {
        //Logger.d(TAG,"update dataicon");
        final GlobalVar app = ((GlobalVar) mContext.getApplicationContext());

        if(!app.getSimType().equals("SIM")&&!app.getSimType().equals("USIM")){
            Logger.e(TAG,"no sim or usim card !");
            return;
        }
        int iconId;


        if (!isCdma()) {
            // GSM case, we have to check also the sim state
            //if (mSimState == IccCardState.READY
                    //|| mSimState == IccCardState.UNKNOWN ) {//����ȥ������߼���ʹ�������Լ���SIM���ж��߼�
                if (hasService()
                        && mDataState == TelephonyManager.DATA_CONNECTED) {
                    switch (mDataActivity) {
                    case TelephonyManager.DATA_ACTIVITY_IN:
                        iconId = mDataIconList[1];
                        break;
                    case TelephonyManager.DATA_ACTIVITY_OUT:
                        iconId = mDataIconList[2];
                        break;
                    case TelephonyManager.DATA_ACTIVITY_INOUT:
                        iconId = mDataIconList[3];
                        break;
                    default:
                        iconId = mDataIconList[0];
                        break;
                    }
                    mDataData.iconId = iconId;
                    mDataData.isVisble = true;
                    updateIcon(mDataIcon, mDataData);
                    
                    //�������������һЩ�������������������ͬ������
                    
                } else {
                    //Logger.e(TAG,"no data connection");
                    mDataData.isVisble = false;
                    updateIcon(mDataIcon, mDataData);
                }
            /*} else {
                mDataData.iconId = R.drawable.stat_sys_no_sim;
                updateIcon(mDataIcon, mDataData);
            }*/
        } else {
            // CDMA case, mDataActivity can be also DATA_ACTIVITY_DORMANT
            if (hasService() && mDataState == TelephonyManager.DATA_CONNECTED) {
                switch (mDataActivity) {
                case TelephonyManager.DATA_ACTIVITY_IN:
                    iconId = mDataIconList[1];
                    break;
                case TelephonyManager.DATA_ACTIVITY_OUT:
                    iconId = mDataIconList[2];
                    break;
                case TelephonyManager.DATA_ACTIVITY_INOUT:
                    iconId = mDataIconList[3];
                    break;
                case TelephonyManager.DATA_ACTIVITY_DORMANT:
                default:
                    iconId = mDataIconList[0];
                    break;
                }
                mDataData.iconId = iconId;
                updateIcon(mDataIcon, mDataData);
            } else {
                //visible = false;
            }
        }

/*        if (mDataIconVisible != visible) {
            mDataData.isVisble = visible;
            updateIcon(mDataIcon, mDataData);
            mDataIconVisible = visible;
        }*/
    }
    
    private final void updateDataNetType(int net) {
        Logger.e(TAG,"new network type:"+net);
        switch (net) {
        case TelephonyManager.NETWORK_TYPE_EDGE:
            mDataIconList = sDataNetType_e;
            mDataData.isVisble = true;
            updateIcon(mDataIcon, mDataData);
            break;
        case TelephonyManager.NETWORK_TYPE_UMTS:
            mDataIconList = sDataNetType_3g;
            mDataData.isVisble = true;
            updateIcon(mDataIcon, mDataData);
            break;
        case TelephonyManager.NETWORK_TYPE_HSDPA:
        case TelephonyManager.NETWORK_TYPE_HSUPA:
        case TelephonyManager.NETWORK_TYPE_HSPA:
        case 12:
        case 6:
            if (mHspaDataDistinguishable) {
                mDataIconList = sDataNetType_h;
            } else {
                mDataIconList = sDataNetType_3g;
            }
            mDataData.isVisble = true;
            updateIcon(mDataIcon, mDataData);
            break;
        default://���δ֪ģʽ�������ص���  
            break;
        }
    }
    
    
    // ����usb_conͼ��
    private final void updateUsbCon(Intent intent) {
        if (mIsUsbConnected) {
            mUsbConData.iconId = sUsbConImages[1];
            mUsbConData.isVisble = true;
            updateIcon(mUsbConIcon, mUsbConData);
        } else {
            mUsbConData.iconId = sUsbConImages[0];
            mUsbConData.isVisble = false;
            updateIcon(mUsbConIcon, mUsbConData);
        }
    }
}
