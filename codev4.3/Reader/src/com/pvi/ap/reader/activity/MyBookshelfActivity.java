package com.pvi.ap.reader.activity;

import java.util.HashMap;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviReaderUI;
import com.pvi.ap.reader.activity.pviappframe.PviTabActivity;
import com.pvi.ap.reader.activity.pviappframe.PviTabHost;
import com.pvi.ap.reader.activity.pviappframe.PviTabWidget;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * �ҵ����
 * 
 * @author ������
 * 
 */
public class MyBookshelfActivity extends PviTabActivity {
	private static final String TAG = "MyBookshelfActivity";
	private static final String THIS_ACT = "com.pvi.ap.reader.activity.MyBookshelfActivity";

//	private Handler mHandler = new Handler();
	private Context mContext = MyBookshelfActivity.this;
	private Resources mRes = null;

	//
	private String[] blockNames;
//	private String actID = null;
	private String SourceType = null;
	private String FilePath = null;

	protected void onResume() {
		// TODO Auto-generated method stub
		Logger.i(TAG, "onResume");
		
		if(saved){
            saved = false;
        }else{

    		final PviTabHost mTabHost = getTabHost();
    
    		// ���ָ����tabIndex (�Ӳ������ң�����Ҳ�����������Ϊ0)���л�����
    		String actTabName = blockNames[0];
    		String isSameAct = null;
    
    		final Intent it = getIntent();
    		if (it != null) {
    			Bundle bd = it.getExtras();
    			if (bd != null) {
    
    				//				
    				//				if(bd.containsKey("actID")&&bd.containsKey("SourceType")&&bd.containsKey("FilePath"))
    				//				{
    				//					actID = bd.getString("actID");
    				//					SourceType = bd.getString("SourceType");
    				//					FilePath = bd.getString("FilePath");
    				//				}
    				//			
    				actTabName = bd.getString("actTabName");
    				if (actTabName == null) {
    					actTabName = blockNames[0];
    					//Logger.i(TAG, "actTabIndexStr is null");
    				}
    				isSameAct = bd.getString("isSameAct");
    
    
    			} else {
    				//Logger.i(TAG, "getExtras() is null");
    			}
    		} else {
    			//Logger.i(TAG, "getIntent() is null");
    		}
    
    		if (actTabName == null) {
    			actTabName = blockNames[0];
    		}
    
    		try {
    			final int defaultTabIndex = getBlockIndex(actTabName);
    
    			//�ڿ�ܸ���tabactivity������ʾʱ����ִ�����������
    
    			mTabHost.setCurrentTab(defaultTabIndex);
    
    			//���֪ͨ����ת������ǲ�ͬ�����act���������ͬ�����act
    			/*            if(isSameAct!=null&&isSameAct.equals("1")){
    	            	Logger.i(TAG,"isSameAct = '1'");
    	            	mTabHost.setCurrentTab(defaultTabIndex);
    	            }else{
    	            	Logger.i(TAG,"no isSameAct");
    	            	mTabHost.setCurrentTabO(defaultTabIndex);
    	            }*/
    
    
    			/*            //֪ͨ��ܽ��淢������ת///////////
    	            Intent tmpIntent = new Intent(
    	                    MainpageActivity.START_ACTIVITY);
    	            Bundle bundleToSend = new Bundle();
    	            bundleToSend.putString("act", THIS_ACT);
    	            //bundleToSend.putString("actTabIndex", ((Integer) mTabHost.getCurrentTab()).toString());
    	            bundleToSend.putString("actTabName", actTabName);
    	            bundleToSend.putString("switchInTab", "1");//��tab��ͨ�������ؼ�����������ת
    	            tmpIntent.putExtras(bundleToSend);
    	            sendBroadcast(tmpIntent);
    	            tmpIntent = null;
    	            bundleToSend = null;
    	            //////////////////////////
    			 */        } catch (Exception e) {
    				 Logger.e(TAG, e.getMessage());
    			 }
        }
			 super.onResume();
	}
	private Runnable loadTab = new Runnable() {
		@Override
		public void run() {
			//Logger.i(TAG, "loading tab ...");

			final PviTabHost mTabHost = getTabHost();
			final LinearLayout ll = (LinearLayout) mTabHost.getChildAt(0);
			final PviTabWidget tw = (PviTabWidget) ll.getChildAt(0);

			int tabLayoutXml = R.layout.tab_indicator_ui1;
			

			/*            final RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater
	                    .from(mContext).inflate(tabLayoutXml, tw, false);
	            final RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater
	                    .from(mContext).inflate(tabLayoutXml, tw, false);
	            final RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater
	                    .from(mContext).inflate(tabLayoutXml, tw, false);
	            final RelativeLayout tabIndicator4 = (RelativeLayout) LayoutInflater
	                    .from(mContext).inflate(tabLayoutXml, tw, false);
	            final RelativeLayout tabIndicator5 = (RelativeLayout) LayoutInflater
	                    .from(mContext).inflate(tabLayoutXml, tw, false);
	            final RelativeLayout tabIndicator6 = (RelativeLayout) LayoutInflater
	                    .from(mContext).inflate(tabLayoutXml, tw, false);*/

			try {
				//�������ж�̬����

				final HashMap<String, Class> classes =  PviReaderUI.getClasses();
				//Logger.i(TAG,"classes size:"+classes.size());

				if (blockNames != null) {
					for (int i = 0; i < blockNames.length; i++) {
						try {
							//Logger.d(TAG, blockNames[i]);
							final Intent tmpIntent = new Intent(mContext,
									classes.get(blockNames[i]));
							if(blockNames[i].equals("�ҵ���ǩ"))
							{
								Bundle bd = new Bundle();
								if(SourceType!=null)
								{
									bd.putString("SourceType", SourceType);
								}
								if(FilePath!=null)
								{
									bd.putString("FilePath", FilePath);
								}
								tmpIntent.putExtras(bd);
							}
							//Logger.i(TAG,"classe:"+classes.get(blockNames[i]));
							
							
							mTabHost.addTab(mTabHost.newTabSpec(blockNames[i])
									.setContent(
											tmpIntent));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				} else {
					Logger.e(TAG, "blockName is null");                
				}


				/*        final Intent tmpintent1 = new Intent(mContext,
	                        UserInfoActivity.class);
	                final TextView tvTab1 = (TextView) tabIndicator1.getChildAt(0);
	                tvTab1.setText(mRes.getString(R.string.actTitle14100));
	                mTabHost.addTab(mTabHost.newTabSpec("TAB_1").setIndicator(
	                        tabIndicator1).setContent(tmpintent1));

	                final Intent tmpintent2 = new Intent(mContext,
	                        MyFriendListActivity.class);
	                final TextView tvTab2 = (TextView) tabIndicator2.getChildAt(0);
	                tvTab2.setText(mRes.getString(R.string.actTitle14200));
	                mTabHost.addTab(mTabHost.newTabSpec("TAB_2").setIndicator(
	                        tabIndicator2).setContent(tmpintent2));

	                final Intent tmpintent3 = new Intent(mContext,
	                        ExpenseProActivity.class);
	                final TextView tvTab3 = (TextView) tabIndicator3.getChildAt(0);
	                tvTab3.setText(mRes.getString(R.string.actTitle14300));
	                mTabHost.addTab(mTabHost.newTabSpec("TAB_3").setIndicator(
	                        tabIndicator3).setContent(tmpintent3));

	                final Intent tmpintent4 = new Intent(mContext,
	                        UnsubscribeActivity.class);
	                final TextView tvTab4 = (TextView) tabIndicator4.getChildAt(0);
	                tvTab4.setText(mRes.getString(R.string.actTitle14400));
	                mTabHost.addTab(mTabHost.newTabSpec("TAB_4").setIndicator(
	                        tabIndicator4).setContent(tmpintent4));

	                final Intent tmpintent5 = new Intent(mContext,
	                        SerialSubscribeActivity.class);
	                final TextView tvTab5 = (TextView) tabIndicator5.getChildAt(0);
	                tvTab5.setText(mRes.getString(R.string.actTitle14500));
	                mTabHost.addTab(mTabHost.newTabSpec("TAB_5").setIndicator(
	                        tabIndicator5).setContent(tmpintent5));

	                final Intent tmpintent6 = new Intent(mContext,
	                        MessageCenterActivity.class);
	                final TextView tvTab6 = (TextView) tabIndicator6.getChildAt(0);
	                tvTab6.setText(mRes.getString(R.string.actTitle14600));
	                mTabHost.addTab(mTabHost.newTabSpec("TAB_6").setIndicator(
	                        tabIndicator6).setContent(tmpintent6));*/

			} catch (Exception e) {
				// ���TAB����ܻ�ʧ�ܣ�
				Logger.e(TAG, e.getMessage());
				e.printStackTrace();
				return;
			}


			//tw.setPadding(0, 0, 7, 0);

			tw.setPadding(0, 0, 0, 0);

			// LinearLayout.LayoutParams lp = new
			// LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,52);
			// lp.gravity=Gravity.BOTTOM;
			// tw.setLayoutParams(lp);
			try {
				final ViewGroup vg = (ViewGroup) mTabHost.getTabContentView();
				int vgCount = vg.getChildCount();
				for (int i = 0; i < vgCount; i++) {
					((FrameLayout) ((ViewGroup) vg.getChildAt(i)).getChildAt(0))
					.setForeground(null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}



/*			mTabHost.setOnTabChangedListener(new PviTabHost.OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
					//�޸�ˢ��ģʽ
//					EPDRefresh.refreshDUDither();

					
	                    final String tabName = tabId;
	                    final Runnable showTip = new Runnable() {
	                        @Override
	                        public void run() {
	                            final Intent tmpIntent = new Intent(
	                                    MainpageActivity.SHOW_TIP);
	                            final Bundle sndBundle = new Bundle();
	                            sndBundle.putString("pviapfStatusTip", mRes
	                                    .getString(R.string.entering)
	                                    + tabName + "...");
	                            //sndBundle.putString("pviapfStatusTipTime",
	                            //        "2000");
	                            tmpIntent.putExtras(sndBundle);
	                            sendBroadcast(tmpIntent);
	                        }
	                    };
	                    new Thread() {
	                        public void run() {
	                            mHandler.post(showTip);
	                        }
	                    }.start();
					 
					//֪ͨ��ܽ��淢������ת///////////
					Intent tmpIntent = new Intent(
							MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("act", THIS_ACT);
					//Logger.i(TAG,"blockName:"+blockNames[mTabHost.getCurrentTab()]);
					bundleToSend.putString("actTabName", blockNames[mTabHost.getCurrentTab()]);
					bundleToSend.putString("switchInTab", "1");//��tab��ͨ�������ؼ�����������ת
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
					tmpIntent = null;
					bundleToSend = null;
					//////////////////////////

					
					 * TextView title =
					 * (TextView)getTabHost().getCurrentTabView(
					 * ).findViewById(R.id.title);
					 * title.setTextColor(Color.WHITE);
					 
					try {
						final ViewGroup vg = (ViewGroup) getTabHost()
						.getTabContentView();
						int vgCount = vg.getChildCount();
						for (int i = 0; i < vgCount; i++) {
							((FrameLayout) ((ViewGroup) vg.getChildAt(i))
									.getChildAt(0)).setForeground(null);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});*/

			// �л�TABʱ����ʼ�л�ʱ������ʾ��Ϣ
			final int tabCount = tw.getChildCount();
			for (int i = 0; i < tabCount; i++) {
				final int curTabIndex = i;
				final TextView tv = (TextView) ((RelativeLayout) tw
						.getChildAt(i)).getChildAt(0);

/*				tv.setOnKeyListener(new OnKeyListener(){

					@Override
					public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
						// TODO Auto-generated method stub
						Logger.e(TAG," keycode:"+arg1);
						return true;
					}});
*/
				/* tv.setOnTouchListener(new OnTouchListener(){

						@Override
						public boolean onTouch(View arg0, MotionEvent arg1) {
							// TODO Auto-generated method stub
							Logger.i(TAG," action:"+arg1.getAction());
							if(arg1.getAction()==MotionEvent.ACTION_UP){
			                    //֪ͨ�����ת
			                    Intent tmpIntent = new Intent(
			                            MainpageActivity.START_ACTIVITY);
			                    Bundle bundleToSend = new Bundle();
			                    bundleToSend.putString("act", THIS_ACT);
			                    //Logger.i(TAG,"blockName:"+blockNames[mTabHost.getCurrentTab()]);
			                    bundleToSend.putString("actTabName", ((TextView)arg0).getText().toString());
			                    Logger.i(TAG,((TextView)arg0).getText());
			                    //bundleToSend.putString("switchInTab", "1");//��tab��ͨ�������ؼ�����������ת
			                    tmpIntent.putExtras(bundleToSend);
			                    sendBroadcast(tmpIntent);
			                    tmpIntent = null;
			                    bundleToSend = null;
			                    //////////////////////////
							}
							Logger.e(TAG,"touch me");
							return true;
						}

	                });*/

/*				tv.setOnFocusChangeListener(new OnFocusChangeListener(){

					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						// TODO Auto-generated method stub
						Logger.d(TAG,"focus changed!@!!");
					}});*/

/*/*2011-6-23��ע��
 * 				tv.setOnClickListener(new RelativeLayout.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub 
						final Intent intent = getIntent();
						if (intent != null) {
							final Bundle b = intent.getExtras();
							if (b != null) {
								b.putString("actTabName",blockNames[curTabIndex]);
								intent.putExtras(b);
							}
						}

						//�л�TABʱ�Զ���ʾ��Ϣ  ��������
						
						final TextView tv = (TextView) v;

	                        final Runnable showTip = new Runnable() {
	                            @Override
	                            public void run() {
	                                final Intent tmpIntent = new Intent(
	                                        MainpageActivity.SHOW_TIP);
	                                final Bundle sndBundle = new Bundle();
	                                sndBundle.putString("pviapfStatusTip", mRes
	                                        .getString(R.string.entering)
	                                        + tv.getText() + "...");
	                                //sndBundle.putString("pviapfStatusTipTime",
	                                //        "2000");
	                                tmpIntent.putExtras(sndBundle);
	                                sendBroadcast(tmpIntent);
	                            }
	                        };
	                        new Thread() {
	                            public void run() {
	                                mHandler.post(showTip);
	                            }
	                        }.start();

						                      //֪ͨ�����ת
		                    Intent tmpIntent = new Intent(
		                            MainpageActivity.START_ACTIVITY);
		                    Bundle bundleToSend = new Bundle();
		                    bundleToSend.putString("act", THIS_ACT);
		                    //Logger.i(TAG,"blockName:"+blockNames[mTabHost.getCurrentTab()]);
		                    bundleToSend.putString("actTabName", ((TextView)v).getText().toString());
		                    //bundleToSend.putString("switchInTab", "1");//��tab��ͨ�������ؼ�����������ת
		                    tmpIntent.putExtras(bundleToSend);
		                    sendBroadcast(tmpIntent);
		                    tmpIntent = null;
		                    bundleToSend = null;
		                    //////////////////////////
						 
						final RelativeLayout rl = ((RelativeLayout) v
								.getParent());
						final Runnable rlClick = new Runnable() {
							@Override
							public void run() {
								rl.performClick();
							}
						};
						new Thread() {
							public void run() {
								mHandler.post(rlClick);
							}
						}.start();

					}
				});*/

			}

		}
	};


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);// ���ȵ��ø��෽��
		//Logger.i(TAG,"onCreate");
		/*
		 * ��δʹ�� Intent sndIntent = new Intent(MainpageActivity.SHOW_ALERT);
		 * Bundle sndBundle = new Bundle(); sndBundle.putString("alID",
		 * "loading"); sndIntent.putExtras(sndBundle); sendBroadcast(sndIntent);
		 */
		mRes = getResources();
		// mHandler.sendEmptyMessage(LOAD_TAB);//���ﲻ���Բ���
		//mHandler.post(loadTab);

		final Intent it = getIntent();
		if (it != null) {
			Bundle bd = it.getExtras();
			if (bd != null) {

				if(bd.containsKey("SourceType")&&bd.containsKey("FilePath"))
				{
					SourceType = bd.getString("SourceType");
					FilePath = bd.getString("FilePath");
				}

			} else {
			}
		} else {
		}

		final HashMap<String, String[]> BlockNameList =PviReaderUI.buildBlockNameList(mContext);
		blockNames = BlockNameList.get(getPackageName()+"."+getLocalClassName());
		//Logger.i(TAG,"BlockName length :"+blockNames.length);
		setBlockNames(blockNames);
		loadTab.run();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		setIntent(intent);
		super.onNewIntent(intent);
	}  
}
