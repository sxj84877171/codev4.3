package com.pvi.ap.reader.activity.pviappframe;

import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.activity.WirelessStoreWelcomeActivity;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Chapable;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnDrawListener;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Logger;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout.LayoutParams;
/**
 * PviActivity����
 * @author rd040 ������
 *
 */
public class PviActivity extends Activity implements Pageable,Chapable {
	public PviPopupWindow popmenu;
	public PviPopupWindow subpopmenu;
	public HashMap<String,View> submenupans = new HashMap<String,View>();//��������˵���
	public View popmenuView;
	private Resources mRes;
	
	private Handler mHandler = new H();
	
	public boolean showPager = false;//�����Ƿ���Ҫ��ʾ��ҳ��
	public boolean showChaper = false;//�����Ƿ���Ҫ��ʾ���¿ؼ�
	public PviBottomBar mBottomBar;             //�ײ�������

	private static final String TAG = "PviActivity";

	private OnUiItemClickListener menuclick;
	public int deviceType = 2;  //�����豸����  ȥ��GlobalVar��deviceType
//	public final static int UPDATEMODE_1 = View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL;
//	public final static int UPDATEMODE_2 = View.EINK_WAIT_MODE_NOWAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL;
//	public final static int UPDATEMODE_3 = View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL;
//	public final static int UPDATEMODE_4 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL;
//	public final static int UPDATEMODE_5 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL;
//	public final static int UPDATEMODE_6 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_FULL;
//	public final static int UPDATEMODE_7 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_DU | View.EINK_UPDATE_MODE_PARTIAL;
//	public final static int UPDATEMODE_8 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_FULL;
//	public final static int UPDATEMODE_9 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_AUTO | View.EINK_UPDATE_MODE_PARTIAL;

	// add by kizan for wireless store search 

	private EditText searchinput = null;
	private Button searchbtn = null;

	private View retView = null ;
	
	//public boolean isShown = false;   //�����Ƿ���ʾ
	
	private boolean isAttached = false;
	//
	//	private long intervaltime = 1000;
	//
	//	public void setInterval(long time)
	//	{
	//		if(time > 3000)
	//		{
	//			intervaltime = 3000;
	//			return;
	//		}
	//		intervaltime = time;
	//	}
	
	private class H extends Handler {}

	protected void registerWirelessSearch(){
		this.searchinput=(EditText) findViewById(R.id.searchInput);
		this.searchbtn=(Button) findViewById(R.id.searchButton);


		if(searchinput == null || searchbtn == null )
			return;

		searchinput.setPadding(10,0,0,0);
		searchinput.setText("��������������");
		searchinput.setTextColor(R.color.dark_grey);


		this.searchbtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			    Logger.e(TAG,"����� ���� ��ť���������뷨");
				PviUiUtil.hideInput(v);
			

				if(searchinput.getText().toString().equals("")||searchinput.getText().toString().equals("��������������"))
				{
				    Logger.e(TAG,"��Ч����");
					searchinput.setText("��������������");
					searchinput.setTextColor(R.color.dark_grey);
					return;
				}
				else
				{
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend
					.putString("act",
					"com.pvi.ap.reader.activity.BookSearchActivity");
					bundleToSend.putString("haveTitleBar", "1");
					bundleToSend.putString("searchKey", searchinput.getText().toString());
					tmpIntent.putExtras(bundleToSend);
					//				searchinput.setText("");
					sendBroadcast(tmpIntent);
				}
			}
		});
		searchinput.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
			    //Logger.e(TAG,"�����ý��㣡");
				//searchinput.requestFocus();
			    //searchinput.setText("");
                searchinput.setTextColor(Color.BLACK);
                PviUiUtil.showInput(arg0);
			}
		});
		searchinput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
				{
				    Logger.e(TAG,"onFocusChange hasFocus=true ����Ϊ���ַ���");
					//					if(deviceType==1){
					//						getWindow().
					//						getDecorView()
					//						.getRootView()
					//						.invalidate(View.EINK_WAIT_MODE_WAIT |
					//								View.EINK_WAVEFORM_MODE_GC16 | 
					//								View.EINK_UPDATE_MODE_FULL); 
					//					}
					//					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(v, 0);
					searchinput.setText("");
					searchinput.setTextColor(Color.BLACK);
				}
				else
				{
				    Logger.e(TAG,"onFocusChange hasFocus=false call hideInput(");
					PviUiUtil.hideInput(v);
				}

			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		if(popmenu!=null){
			if(popmenu.isShowing()){
				popmenu.dismiss();
			}
		}
		if(subpopmenu!=null)
		{
			if(subpopmenu.isShowing()){
				subpopmenu.dismiss();
			}
		}
		//hideInput();

		this.searchinput=(EditText) findViewById(R.id.searchInput);
		if(searchinput!=null)
		{
			searchinput.setTextColor(R.color.black);
		}
		super.onPause();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			menupan();
			if(this.subpopmenu!=null&&!this.popmenu.isShowing()){
				this.subpopmenu.dismiss();
			}
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// ֪ͨ��ܷ�����һ����activty
			sendBroadcast(new Intent(MainpageActivity.BACK));
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	public void setMenuListener(View v) {

		final OnUiItemClickListener theMenuClick = getMenuclick();
		final PviMenuPan menupan = (PviMenuPan) v;

		final int itemCount = menupan.getChildCount();
		for (int i = 0; i < itemCount; i++) {
			//        	PviMenuItem temp = (PviMenuItem)v;
			final PviMenuItem menuItem = (PviMenuItem) menupan.getChildAt(i);
			if (menuItem.tag != null&&!"".equals(menuItem.tag)) {
				final String tag = menuItem.tag;		
				//Logger.d(TAG,"menuitem tag:"+tag);
				if(!tag.contains("sub_")){
					//Logger.d(TAG,tag+" setonclicklistener");
					menuItem.l2 = theMenuClick;
				}else{
					//Logger.d(TAG,tag+" not setonclicklistener");
				}
			}
		}
	}

	/**
	 * ��ʾ�˵�ʱ��������
	 * @author rd040
	 *
	 */
	public interface OnPmShowListener {
		void OnPmShow(PviPopupWindow popmenu);
	}

	OnPmShowListener onPmShowListener;

	public void setOnPmShow(OnPmShowListener l) {
		this.onPmShowListener = l;
	}

	   /**
     * ����tag �� op ȡ�˵���
     * 
     * ��Ϊsub����Ҳ�ᱻ����Ϊtag����Ҳ����ͨ���˺����ҵ�
     */
	public PviMenuItem getMenuItem(String tagOrOp){
	    PviMenuItem v = null;
        if(subpopmenu!=null){
            v = ((PviMenuPan)subpopmenu.getContentView()).getItemById(tagOrOp);
        }

        if(v==null){
            if(popmenu!=null){
                v = ((PviMenuPan)popmenu.getContentView()).getItemById(tagOrOp);
                if(v==null){
                    v = ((PviMenuPan)popmenu.getContentView()).getItemById("sub_"+tagOrOp);
                }
            }
        }
        return v;
    }

	/**
	 * ����tag �� op ȡ�˵���
	 */

	/**
	 * �����˵����
	 */
	public void menupan() {

		if (popmenu == null) {
			popmenuView = getMenu(this);
			popmenu = new PviPopupWindow(popmenuView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);  
			setMenuListener(popmenuView);
			popmenu.setBackgroundDrawable(new BitmapDrawable());
			popmenu.setFocusable(true);
			//popmenuView = null;	


		}

		if (popmenu != null) {
			if (popmenu.isShowing()) {
				popmenu.dismiss();
			} else {

				if(this.onPmShowListener!=null){
					//Logger.d(TAG,"menu:process OnPmShow");
					this.onPmShowListener.OnPmShow(popmenu);
				}

				final View mainBlock = findViewById(R.id.mainblock);//������idΪmainBlock�ľֲ�Ԫ��
				if(mainBlock!=null){
					int bottom = 50;

					try {
						if(mainBlock!=null){
							popmenu.showAtLocation(getWindow().getDecorView(),
									Gravity.BOTTOM | Gravity.LEFT, 10, bottom);//�ײ��������߶� 

						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}       

					/*                    mHandler.postDelayed(new Runnable(){

                        @Override
                        public void run() {
                          //�˵���1 ��ȡ����
                            final LinearLayout menupan = (LinearLayout)popmenu.getContentView();
                            if(menupan!=null){
                                Logger.i(TAG,"menupan;"+menupan.toString());
                                final LinearLayout menuframe = (LinearLayout)menupan.getChildAt(0);
                                if(menuframe!=null){
                                    Logger.i(TAG,"menuframe;"+menuframe.toString());
                                    final TextView menuItem1 = (TextView)menuframe.getChildAt(0);
                                    if(menuItem1!=null){
                                        Logger.i(TAG,menuItem1.toString()+",menuItem1.requestFocus();");
                                        menuItem1.requestFocus();
                                    }
                                }
                            }                            
                        }}, 1000);*/

				}
			}
		}
	}

	/**
	 * @return the popmenu
	 */
	public PviPopupWindow getPopmenu() {
		return popmenu;
	}
	/**
	 * @param popmenu the popmenu to set
	 */
	public void setPopmenu(PviPopupWindow popmenu) {
		this.popmenu = popmenu;
	}


	public void bindEvent() {
		// �˵�����¼�����
		final View tv_menuBtn =  findViewById(R.id.menubtn);
		if(tv_menuBtn!=null){
			tv_menuBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					menupan();
				}
			});

			//����eink��ˢģʽ
			/*if(deviceType==1){
			    tv_menuBtn.setUpdateMode(UPDATEMODE_3);
	        }*/
		}else{
			//Logger.i(TAG,"tv_menuBtn is null");
		}
		//�ײ��������¼�����      
		final View fp_application = findViewById(R.id.fp_application);
		if(fp_application!=null){
			fp_application.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// ��ʾ���г���
					// ֪ͨ���ȥ����Activity
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT16000");
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
					tmpIntent = null;
					bundleToSend = null;
				}
			});
			//����eink��ˢģʽ
			if(deviceType==1){
//				fp_application.setUpdateMode(UPDATEMODE_5);
			}
		}

		final View fp_settings =  findViewById(R.id.fp_settings);
		if(fp_settings!=null){
			fp_settings.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("startType", "allwaysCreate");
					bundleToSend.putString("actID", "ACT15000");
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
					tmpIntent = null;
					bundleToSend = null;
				}
			});
			//����eink��ˢģʽ
			if(deviceType==1){
//				fp_settings.setUpdateMode(UPDATEMODE_5);
			}
		}

		final View fp_music =  findViewById(R.id.fp_music);
		if(fp_music!=null){
			fp_music.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("actID", "ACT13200");
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
					tmpIntent = null;
					bundleToSend = null;
				}
			});
			//����eink��ˢģʽ
			if(deviceType==1){
//				fp_music.setUpdateMode(UPDATEMODE_5);
			}
		}

		retView =  findViewById(R.id.back);
		if(retView !=null){
			retView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sendBroadcast(new Intent(MainpageActivity.BACK));
				}
			});
			//����eink��ˢģʽ
			if(deviceType==1){
//				retView.setUpdateMode(UPDATEMODE_5);
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final GlobalVar app = ((GlobalVar) getApplicationContext());
		deviceType = app.deviceType;
		mRes = getResources();
		mBottomBar = app.pbb;
		initControls();
		bindEvent();
		super.onCreate(savedInstanceState);
	}

	/**
	 * ��ʼ������UI�ؼ�
	 */
	public void initControls(){

	}

	/**
	 * �رղ˵�
	 */
	public void closePopmenu(){

        if (subpopmenu != null && subpopmenu.isShowing()) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    subpopmenu.dismiss();
                }
            }, 500);

        }

        if (popmenu != null && popmenu.isShowing()) {
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    popmenu.dismiss();
                }
            }, 500);

        }
	}
	
	/**
	 * �ػ�˵�
	 * ���˵�������Է����仯֮����Ҫ���ô˷������»��������˵�
	 */
	
	public void redrawMenu(){
	    if (subpopmenu != null && subpopmenu.isShowing()) {
	        subpopmenu.getContentView().invalidate();
        }

        if (popmenu != null && popmenu.isShowing()) {            
            popmenu.getContentView().invalidate();
        }
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 0);
		//		EPDRefresh.refreshGCOnceFlash();

		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {

		if(intent!=null){
			final Bundle bd = intent.getExtras();
			if(bd!=null){
				final String startType = bd.getString("startType");
				if("back".equals(startType)){
					//�ϲ�����
					final Intent oldIntent = getIntent();
					if(oldIntent!=null){
						final Bundle oldBd = oldIntent.getExtras();
						oldBd.putAll(bd);
						oldIntent.putExtras(oldBd);
						setIntent(oldIntent);
					}
				}else{
					setIntent(intent);
				}
			}
		}   

		super.onNewIntent(intent);
	}

	 /**
     * show the network processing msg to the status bar
     */
    protected void showNetWorkProcessing2() {
        final Intent tip = new Intent(MainpageActivity.SHOW_TIP);
        final Bundle data = new Bundle();
        data.putString("pviapfStatusTip", "һ��������...");
        //      data.putString("pviapfStatusTipTime", "2000");
        tip.putExtras(data);
        sendBroadcast(tip);
        //      
        //      dialog = new PviAlertDialog(getParent());
        //      dialog.setTitle(getResources().getString(R.string.kyleHint04));
        //      dialog.setMessage(getResources().getString(R.string.kyleHint05));
        //      dialog.show();
    }

	/**
	 * show the network processing msg to the status bar
	 */
	protected void showNetWorkProcessing() {
		Intent tip = new Intent(MainpageActivity.SHOW_TIP);
		Bundle data = new Bundle();
		data.putString("pviapfStatusTip", "���ݻ�ȡ��...");
		//		data.putString("pviapfStatusTipTime", "2000");
		tip.putExtras(data);
		sendBroadcast(tip);
		//		
		//		dialog = new PviAlertDialog(getParent());
		//		dialog.setTitle(getResources().getString(R.string.kyleHint04));
		//		dialog.setMessage(getResources().getString(R.string.kyleHint05));
		//		dialog.show();
	}

	/**
	 * show the network going msg to the status bar
	 */
	protected void showOnGoing() {
		Intent tip = new Intent(MainpageActivity.SHOW_TIP);
		Bundle data = new Bundle();
		data.putString("pviapfStatusTip", "�ϴ��������ڴ���...");
		//		data.putString("pviapfStatusTipTime", "2000");
		tip.putExtras(data);
		sendBroadcast(tip);
		//		
		//		dialog = new PviAlertDialog(getParent());
		//		dialog.setTitle(getResources().getString(R.string.kyleHint04));
		//		dialog.setMessage(getResources().getString(R.string.kyleHint05));
		//		dialog.show();
	}

	/**
	 * show the child activity 
	 * @param cls
	 */
	protected void showMe(Class cls){
	    if(!isShown()){
    		if(deviceType==1){
    			//Logger.d(TAG,"invalidate in showme(... gc16 full");
    			//this.getParent().getWindow().getDecorView().invalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
    		}
    		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
    		
    		Intent tmpIntent = new Intent(
    				MainpageActivity.SHOW_ME);
    		Bundle bundleToSend = new Bundle();
    		bundleToSend.putString("sender", cls.getName()); //TAB��Ƕactivity���ȫ��
    		tmpIntent.putExtras(bundleToSend);
    		sendBroadcast(tmpIntent);
	    }
	}

	public void showMessage(String msg){
		final Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_TIP);
		final Bundle sndBundle = new Bundle();
		sndBundle.putString("pviapfStatusTip",msg);
		//sndBundle.putString("pviapfStatusTipTime",
		//        "2000");
		tmpIntent.putExtras(sndBundle);
		sendBroadcast(tmpIntent);
	}
	public void showMessage(String msg,String time){
		final Intent tmpIntent = new Intent(
				MainpageActivity.SHOW_TIP);
		final Bundle sndBundle = new Bundle();
		sndBundle.putString("pviapfStatusTip",msg);
		sndBundle.putString("pviapfStatusTipTime",
				time);
		tmpIntent.putExtras(sndBundle);
		sendBroadcast(tmpIntent);
	}

	public void hideTip(){
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	}

	public View getRetView() {
		return retView;
	}

	public void setRetView(View retView) {
		this.retView = retView;
	}

	public interface showAlert{
		boolean canClose = false;
		void OnOk();
	}



	public void showTip(String text){
		Intent intent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", text);
		intent.putExtras(sndbundle);
		sendBroadcast(intent);
	}

	public void showTip(String text,int timeout){
		Intent intent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", text);
		if(timeout>0){
			sndbundle.putString("pviapfStatusTipTime", ""+timeout);
		}
		intent.putExtras(sndbundle);
		sendBroadcast(intent);
	}

	public void showTip(){
		Intent intent = new Intent(MainpageActivity.SHOW_TIP);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("pviapfStatusTip", "���ݻ�ȡ��...");
		intent.putExtras(sndbundle);
		sendBroadcast(intent);
	}	

	@Override
    public void onAttachedToWindow() {
	    isAttached = true;
	    PviUiUtil.hideInput(this.getWindow().getDecorView());
        super.onAttachedToWindow();
    }

    @Override
	public void onDetachedFromWindow() {
		//�رղ˵�  ����
		if(subpopmenu!=null)
		{
			subpopmenu.dismiss();
		}
		//һ��
		if(popmenu!=null){
			popmenu.dismiss();
		}
		isAttached = false;
		//PviUiUtil.hideInput(this.getWindow().getDecorView());
		super.onDetachedFromWindow();
	}

	/**
	 * ����menu��view
	 * @param context
	 * @return
	 */

	public View getMenu(final Context context) {

		//�ÿա������˵�������
		submenupans.clear();

		// �˵���ɫ
		ColorStateList csl = null;

		final String act = ((Activity) context).getLocalClassName();
		
		final PviMenuPan menupan = new PviMenuPan(context);
		menupan.setId(1);
		menupan.setBackgroundResource(R.drawable.bg_menupan_ui1);


		XmlResourceParser parser = mRes.getXml(R.xml.menus);

		final int depth = parser.getDepth();
		int i = 0;
		int type;
		final String pviNsHome = "http://schemas.android.com/apk/res/com.pvi.ap.reader";
		final String androidNsHome = "http://schemas.android.com/apk/res/android";
		boolean menuStart = false;
		boolean menuEnd = false;

		int firstLevelMenuitemIndex = 0;

		try {
			while (((type = parser.next()) != XmlPullParser.END_TAG || parser
					.getDepth() > depth)
					&& type != XmlPullParser.END_DOCUMENT) {

				if (type != XmlPullParser.START_TAG) {
					continue;
				}

				boolean added = false;
				final String name = parser.getName();

				if ("pvimenu".equals(name)) {
					if (parser.getAttributeValue(pviNsHome, "act").equals(act)) {
						menuStart = true;
					}

					if (menuStart
							&& !parser.getAttributeValue(pviNsHome, "act")
							.equals(act)) {
						menuEnd = true;
					}

				}
				if (menuStart && !menuEnd) {
					if ("pvimenuitem".equals(name)) {                   


						//�����˵���
						final String p = parser.getAttributeValue(pviNsHome, "p");
						if(p!=null){
							//�Ƕ����˵����������˵���view
							PviMenuItem submenuitem = getMenuItem( parser, context,androidNsHome,pviNsHome,csl);



							try {
								PviMenuPan submenupan = (PviMenuPan)submenupans.get(p);
								submenupan.addMenuItem(submenuitem);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} 
						}else{//��ͨ�˵�����ж����Ӳ˵��Ĳ˵��� ��Ҫִ�����Ƭ��
							PviMenuItem menuitem = getMenuItem( parser, context,androidNsHome,pviNsHome,csl);


							//��������˵�
							final String sub = parser.getAttributeValue(pviNsHome, "sub");
							if(sub!=null){
								//Logger.d(TAG,"I have submenu ! sub:"+sub);
								//menuitem.setSub(sub);
								menuitem.tag = "sub_"+sub;//��sub����Ϊview��Tag
								menuitem.id=menuitem.tag;
								//���Ӳ˵��ģ�1 ����һ�����صġ��˵��򡱵�view�������Ա����HashMap submenupans ��  2����  ��/�� �˵���  ���¼�����
								PviMenuPan submenupan = new PviMenuPan(context);
								submenupan.setId(1);
								submenupan.setBackgroundResource(R.drawable.bg_menupan_ui1);
								submenupan.setTag(sub);
								submenupans.put(sub, submenupan);

								final int firstLevelMenuitemIndex1 = firstLevelMenuitemIndex;

								//Logger.d(TAG,"�����ж����˵����Ĳ˵��������¼����� menuitem.l2 = OnUiItemClickListener");
								menuitem.l2=new OnUiItemClickListener() {
                                    @Override
                                    public void onUiItemClick(PviUiItem item) {
                                        submenupan(sub,((PviActivity) menupan.getContext()),menupan,firstLevelMenuitemIndex1);
                                    }
								};

/*2011-7-5
 * 								menuitem.setOnTouchListener(new OnTouchListener(){
									@Override
									public boolean onTouch(View v,
											MotionEvent arg1) {
										//Logger.d(TAG,"I am touched ! sub:"+sub);
										if(arg1.getAction()==MotionEvent.ACTION_UP){
											if(!v.hasFocus()){
												//Logger.d(TAG,v.getTag()+": performClick");
												//Logger.d(TAG,v.performClick());
												v.performClick();
											}else{
												//Logger.d(TAG,v.getTag()+": do nothing");
											}
										}
										return false;
									}});*/
							}

							menupan.addMenuItem(menuitem);
							firstLevelMenuitemIndex++;
						}






						added = true;
						//op = null;
						// menuitem = null;
					}

				}

				if (added)
					i++;

			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			;
		}

		if (i == 0) {// ���û�����ò˵�
			menupan.setVisibility(View.GONE);
		}

		// release object:

		parser.close();
		parser = null;

		//Logger.d(TAG,"menupan created!");	

		return menupan;
	}
	


	private PviMenuItem getMenuItem(XmlResourceParser parser,Context context1,String androidNsHome,String pviNsHome,ColorStateList csl){
		final Context context = context1;
		final PviMenuItem menuitem = new PviMenuItem("id", R.drawable.bg_menuitem_ui1, 0, 0, 188, 38, "text", false, true, null);
		//menuitem.setId(3 + i);
		// menuitem.setId(parser.getAttributeIntValue(androidNsHome,"id",
		// 0));
		String vTag = "";
		try {
			vTag = parser.getAttributeValue(androidNsHome,
			"tag");
		} catch (Exception e) {
			e.printStackTrace();
		}
		menuitem.id=vTag;
		menuitem.tag=vTag;
		
		final int visibility = parser.getAttributeIntValue(
				androidNsHome, "visibility", 0);
		if (visibility == 1) {
			menuitem.isVisible=false;
		} else if (visibility == 2) {
		    menuitem.isVisible=false;
		}
		// ���ʻ�
		if (parser.getAttributeValue(androidNsHome, "text")
				.charAt(0) == '@') {
			menuitem.text=getResources().getString(
					Integer
					.parseInt(parser.getAttributeValue(
							androidNsHome, "text")
							.substring(1)));
		} else {
			menuitem.text=parser.getAttributeValue(
					androidNsHome, "text");
		}
		// menuitem.setFocusable(true);  
		menuitem.textSize = 20f;

		// ��item���ü�����
		String op = parser.getAttributeValue(pviNsHome, "op");

		if (op != null) {
			menuitem.setOp(op);
			menuitem.id = op;//2011-7-11�����߼���op��tag����Ϊid
			
			if (op.equals("mainpage")) {
			    menuitem.l2 = new OnUiItemClickListener(){

                    @Override
                    public void onUiItemClick(PviUiItem item) {
                        Intent intent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle sndBundle = new Bundle();
                        sndBundle.putString("actID",
                        "ACT10000");
                        intent.putExtras(sndBundle);
                        sendBroadcast(intent);
                        intent = null;
                        sndBundle = null;
                        closePopmenu();                        
                    }};		    
				


			} else if (op.equals("wirelessstore")) {
			    
			    menuitem.l2 = new OnUiItemClickListener(){

                    @Override
                    public void onUiItemClick(PviUiItem item) {
                        final Intent tmpIntent = new Intent(
                                MainpageActivity.SHOW_TIP);
                        final Bundle Bundle = new Bundle();
                        Bundle.putString("pviapfStatusTip","����������ǣ����Ժ�...");

                        tmpIntent.putExtras(Bundle);
                        sendBroadcast(tmpIntent);

                            Intent intent = new Intent(
                                    MainpageActivity.START_ACTIVITY);
                            Bundle sndBundle = new Bundle();
                            sndBundle
                            .putString("actID",
                            "ACT19000");
                            intent.putExtras(sndBundle);
                            sendBroadcast(intent);
                            intent = null;
                            sndBundle = null;
                            closePopmenu();

                    }}; 

				


			} else if (op.equals("welcome")) {

				menuitem.l2 = new OnUiItemClickListener() {

                    @Override
                    public void onUiItemClick(PviUiItem item) {


                        Intent msgIntent = new Intent(
                                MainpageActivity.SHOW_TIP);

                        Bundle sndbundle = new Bundle();

                        sndbundle.putString(
                                "pviapfStatusTip",
                        "������ǻ�ӭҳ������");
                        msgIntent
                        .putExtras(sndbundle);
                        context
                        .sendBroadcast(msgIntent);

                        final Intent intent = new Intent(
                                context,
                                WirelessStoreWelcomeActivity.class);
                        context
                        .startActivity(intent);

                        closePopmenu();

                
                    }
				};



			} else if (op.equals("bgmusic")) {
			    
			    menuitem.setOnDrawListener(new OnDrawListener(){

                    @Override
                    public void onDraw(PviUiItem item) {
                        //��ʱ��ȡ���ֲ���״̬
                        final GlobalVar app = ((GlobalVar) getApplicationContext());
                        if(app.musicFlag!=1){
                            item.text = mRes
                            .getString(R.string.fp_menu1);
                        }else{
                            item.text = mRes
                            .getString(R.string.fp_menu1_1);
                        }
                    }
                    
                });
			    
			    //�����¼�����
			    //Logger.d(TAG,"set l2 for "+op);
			    menuitem.l2 = new OnUiItemClickListener(){

                    @Override
                    public void onUiItemClick(PviUiItem item) {
                        //Logger.d(TAG,"onUiItemClick, item id:"+item.id);
                        if (item.text.equals(
                                        mRes
                                        .getString(R.string.fp_menu1))) {
                            final Intent myintent = new Intent(
                            "com.pvi.ap.reader.service.BackGroundMusicService");
                            //Logger.d(TAG,"startService:com.pvi.ap.reader.service.BackGroundMusicService");
                            startService(myintent);
                        } else {          

                            final Intent myintent = new Intent("com.pvi.ap.reader.service.BackGroundMusicService");
                            //Logger.d(TAG,"stopService:com.pvi.ap.reader.service.BackGroundMusicService");
                            stopService(myintent);

                        }
                    }};

                    

			} else if (op.equals("back")) {

				menuitem.l2 = new OnUiItemClickListener() {

                    @Override
                    public void onUiItemClick(PviUiItem item) {
                        sendBroadcast(new Intent(
                                MainpageActivity.BACK));
                        closePopmenu();                    
                    }
				};             


			} else if (op.equals("mybookshelf")) {// �ҵ����

				menuitem.l2= new OnUiItemClickListener() {

				    @Override
                    public void onUiItemClick(PviUiItem item) {

                        Intent intent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle sndBundle = new Bundle();
                        sndBundle.putString("actID",
                        "ACT12000");
                        intent.putExtras(sndBundle);
                        sendBroadcast(intent);
                        intent = null;
                        sndBundle = null;
                        closePopmenu();
                    
                    }
				};

			} else if (op.equals("userspace")) {// ���˿ռ�
				menuitem.l2 = new OnUiItemClickListener() {
                    @Override
                    public void onUiItemClick(PviUiItem item) {

                        Intent intent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle sndBundle = new Bundle();
                        sndBundle.putString("actID",
                        "ACT14000");
                        intent.putExtras(sndBundle);
                        sendBroadcast(intent);
                        intent = null;
                        sndBundle = null;
                        closePopmenu();
                    
                    }
				};             

			} else if (op.equals("g3booksearch")) {// ����ͼ������

				menuitem.l2= new OnUiItemClickListener() {

                    @Override
                    public void onUiItemClick(PviUiItem item) {

                        Intent intent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle sndBundle = new Bundle();
                        sndBundle.putString("actID",
                        "ACT11700");
                        intent.putExtras(sndBundle);
                        sendBroadcast(intent);
                        intent = null;
                        sndBundle = null;
                        closePopmenu();
                    
                    }
				}; 

			}
		}

		return menuitem;
	}


	/*
	 * ��ʾ������ �Ӳ˵���
	 * 
	 * menuindex 
	 * */
	private void submenupan(String sub,PviActivity a,View v,int menuitemIndex){
		final PviMenuPan submenupan = (PviMenuPan)submenupans.get(sub);		
		if(submenupan!=null){		    
			if (a.subpopmenu == null||a.subpopmenu!=null&&!a.subpopmenu.getContentView().getTag().equals(sub)) {

				a.subpopmenu = new PviPopupWindow(submenupan, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);  							
				a.setMenuListener(submenupan);
				a.subpopmenu.setBackgroundDrawable(new BitmapDrawable());
				a.subpopmenu.setFocusable(true);

				//popmenuView = null;
			}

			if (a.subpopmenu != null) {
				if (a.subpopmenu.isShowing()) {
					a.subpopmenu.dismiss();
					a.popmenu.dismiss();
				} else {

					if(this.onPmShowListener!=null){
						//Logger.d(TAG,"subpopmenu:process OnPmShow");
						this.onPmShowListener.OnPmShow(subpopmenu);
					}

					final View mainBlock = a.findViewById(R.id.mainblock);//������idΪmainBlock�ľֲ�Ԫ��
					if(mainBlock!=null){
						int bottom = 49;//�˵��ײ� �� ��Ļ�� �ĸ߶�
						int bottom_sub = 0;  //�������Ӳ˵��ײ��� ��Ļ�� �߶�
						int height_menuitem = 38;   //�����˵���ĸ߶�

						int menuitem_count = ((PviMenuPan)popmenu.getContentView()).getChildCount();//һ���˵������
						int sub_menuitem_count = submenupan.getChildCount();//�����˵������
						bottom_sub = bottom+height_menuitem*(menuitem_count-menuitemIndex-1)-height_menuitem*sub_menuitem_count/2;

						if(bottom_sub<0){
							bottom_sub=0;
						}

						//�Ӹ����˵���λ�õ���
						try {
							if(mainBlock!=null){
								a.subpopmenu.showAtLocation(getWindow().getDecorView(),
										Gravity.BOTTOM | Gravity.LEFT, v.getRight(), bottom_sub);
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}       



					}
				}
			}
		}
	}

    @Override
    public void OnNextpage() {
        
    }

    @Override
    public void OnPrevpage() {
        
    }

    @Override
    public void OnNextchap() {
        
    }

    @Override
    public void OnPrevchap() {
        
    }

    public void setMenuclick(OnUiItemClickListener menuclick) {
        this.menuclick = menuclick;
    }

    public OnUiItemClickListener getMenuclick() {
        return menuclick;
    }

    /**
     * ��app��ܷ���Ϣ ����
     */
    public void back() {
        sendBroadcast(new Intent(MainpageActivity.BACK));
    }


    /**
     * �жϸ�activity��ǰ�Ƿ���ʾ��
     * @return
     */
    public boolean isShown(){
        final View dv = getWindow().getDecorView();
        if(dv.getVisibility()==View.VISIBLE
                &&isAttached){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * ��ŷ�ҳ��Ϣ��
     */
    private String pagerinfo="";
    public void setPagerinfo(String info){
        this.pagerinfo = info;
    }

    public String getPagerinfo() {
        return pagerinfo;
    }
    
    public void updatePagerinfo(String info){
        setPagerinfo(info);
        if(isShown()){
            mBottomBar.actionUpdatePagerinfo();
        }
    }

    
    public void showPager(){
        this.showPager = true;
        if(isShown()){
            mBottomBar.actionShowPager();
        }
    }
    public void hidePager(){
        this.showPager = false;
        if(isShown()){
            mBottomBar.actionHidePager();
        }
    }
    
    public void showChaper(){
        this.showChaper = true;
        if(isShown()){
            mBottomBar.actionShowChaper();
        }
    }
    public void hideChaper(){
        this.showChaper = false;
        if(isShown()){
            mBottomBar.actionHideChaper();
        }
    }
}
