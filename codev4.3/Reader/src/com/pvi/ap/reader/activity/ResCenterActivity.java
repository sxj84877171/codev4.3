package com.pvi.ap.reader.activity;

import java.util.HashMap;
import com.pvi.ap.reader.activity.pviappframe.PviReaderUI;
import com.pvi.ap.reader.activity.pviappframe.PviTabActivity;
import com.pvi.ap.reader.activity.pviappframe.PviTabHost;
import com.pvi.ap.reader.data.common.Logger;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

/**
 * ��Դ���� Tab Activity
 * @author rd040 ������
 *
 */

public class ResCenterActivity extends PviTabActivity {

	private static final String TAG = "ResCenterActivity";
	private static final String THIS_ACT = "com.pvi.ap.reader.activity.ResCenterActivity";
	PviTabHost mTabHost = null;   
	private Intent[] intent = new Intent[6];
	private Intent revintent  = null;
	private Bundle revbundle = null;
	private String DirPath = "";
	private String bookName=null;
	private Handler mHandler = new Handler();
	private Resources mRes = null;
	private Context mContext = ResCenterActivity.this;
	private String[] blockNames;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);	    

		mRes = getResources();

		//ȫ������
		try
		{	

		     mTabHost = getTabHost(); 
		        

		        final HashMap<String, String[]> BlockNameList =PviReaderUI.buildBlockNameList(mContext);
		        blockNames = BlockNameList.get(getPackageName()+"."+getLocalClassName());
		        setBlockNames(blockNames);
		        final HashMap<String, Class> classes =  PviReaderUI.getClasses();
		        
		        if (blockNames != null) {
		            for (int i = 0; i < blockNames.length; i++) {
		                try {

		                    final Intent tmpIntent = new Intent(mContext,
		                            classes.get(blockNames[i]));
		                    
		                    Bundle tmpBundle = new Bundle();

		                    try
		                    {
		                        this.revintent = this.getIntent();
		                        this.revbundle = this.revintent.getExtras();
                                this.bookName=this.revbundle.getString("bookName");
                                if(this.bookName!=null)
		                        {
		                            tmpBundle.putString("bookName",this.bookName);
		                        }
		                        this.DirPath = this.revbundle.getString("DirPath");
		                        if(this.DirPath!=null)
		                        {
		                            tmpBundle.putString("DirPath",this.DirPath);
		                        }

		                    }
		                    catch(Exception e)
		                    {
		                        this.DirPath = "";
		                    }                    
		                    tmpIntent.putExtras(tmpBundle);
		                    
		                    
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
			
			
			
/*			mTabHost = getTabHost(); 
			LinearLayout ll = (LinearLayout)mTabHost.getChildAt(0);   
			TabWidget tw = (TabWidget)ll.getChildAt(0);  


            int tabLayoutXml = 0;
            if(skinID==1){
                tabLayoutXml = R.layout.tab_indicator_ui1;
            }else if(skinID==2){
                tabLayoutXml = R.layout.tab_indicator_ui2;
            }
			String actTabIndexStr = "0";
			Intent temp = new Intent();
			try
			{
				this.revintent = this.getIntent();
				this.revbundle = this.revintent.getExtras();
				actTabIndexStr = revbundle.getString("actTabIndex");
				if(actTabIndexStr==null){
				    actTabIndexStr = "0";
				}
				this.DirPath = this.revbundle.getString("DirPath");
				if(this.DirPath!=null)
				{
					Bundle bundle = new Bundle();
					bundle.putString("DirPath",this.DirPath);
					Log.i("Reader", "ResCenterActivity DirPath: " + DirPath);
					temp.putExtras(bundle);
				}

			}
			catch(Exception e)
			{
				actTabIndexStr = "0";
				this.DirPath = "";
			}			
			
			if(actTabIndexStr.equals("0"))
			{
				temp.setClass(this,MyDocumentActivity.class);
				intent[0] = temp;
				intent[1] = new Intent(this,MyMusicActivity.class);
			}
			else
			{
				temp.setClass(this,MyMusicActivity.class);
				intent[0] = new Intent(this,MyDocumentActivity.class);
				intent[1] = temp;
			}
			int defaultTabIndex = Integer.parseInt(actTabIndexStr);
			mTabHost.setCurrentTab(defaultTabIndex);
			intent[2]=new Intent(this,MyImageActivity.class);
			intent[3]=new Intent(this,MyNoteActivity.class);
			intent[4]=new Intent(this,LocalBookActivity.class);
			intent[5]=new Intent(this,StorageStatActivity.class);


			//			LinearLayout.LayoutParams llLp = (LinearLayout.LayoutParams)tw.getLayoutParams();
			//			llLp.leftMargin = 12;
			//			llLp.rightMargin = 12;
			//			tw.setLayoutParams(llLp);

			RelativeLayout tabIndicator1 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
			RelativeLayout tabIndicator2 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
			RelativeLayout tabIndicator3 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
			RelativeLayout tabIndicator4 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
			RelativeLayout tabIndicator5 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   
			RelativeLayout tabIndicator6 = (RelativeLayout) LayoutInflater.from(this).inflate(tabLayoutXml, tw, false);   

			TextView tvTab1 = (TextView)tabIndicator1.getChildAt(0);   
			tvTab1.setText("�ҵ��ĵ�"); 
			mTabHost.addTab( mTabHost.newTabSpec("TAB_1")   
					.setIndicator(tabIndicator1)   
					.setContent(intent[0]) );


			TextView tvTab2 = (TextView)tabIndicator2.getChildAt(0);   
			tvTab2.setText("�ҵ�����"); 
			mTabHost.addTab( mTabHost.newTabSpec("TAB_2")   
					.setIndicator(tabIndicator2)   
					.setContent(intent[1]) );


			TextView tvTab3 = (TextView)tabIndicator3.getChildAt(0);   
			tvTab3.setText("�ҵ�ͼƬ"); 
			mTabHost.addTab( mTabHost.newTabSpec("TAB_3")   
					.setIndicator(tabIndicator3)   
					.setContent(intent[2]) );

			TextView tvTab4 = (TextView)tabIndicator4.getChildAt(0);   
			tvTab4.setText("�ҵı��"); 
			mTabHost.addTab( mTabHost.newTabSpec("TAB_4")   
					.setIndicator(tabIndicator4)   
					.setContent(intent[3]) );

			TextView tvTab5 = (TextView)tabIndicator5.getChildAt(0);   
			tvTab5.setText("�������"); 
			mTabHost.addTab( mTabHost.newTabSpec("TAB_5")   
					.setIndicator(tabIndicator5)   
					.setContent(intent[4]) );

			TextView tvTab6 = (TextView)tabIndicator6.getChildAt(0);   
			tvTab6.setText("�洢�ռ�"); 
			mTabHost.addTab( mTabHost.newTabSpec("TAB_6")   
					.setIndicator(tabIndicator6)   
					.setContent(intent[5]) );  
*/



		        
/*2011-6-25ȥ��			try{
				ViewGroup vg = (ViewGroup)mTabHost.getTabContentView();
				int vgCount = vg.getChildCount();
				for(int i=0;i<vgCount;i++){
					((FrameLayout)((ViewGroup)vg.getChildAt(i)).getChildAt(0)).setForeground(null);
				}
			}catch(Exception e){
				e.printStackTrace();
			}*/

/*2011-7-12ע�ͣ���Ϊshowme����ʱ���Ѿ��������������
 * 			mTabHost.setOnTabChangedListener(new PviTabHost.OnTabChangeListener() {

				@Override
				public void onTabChanged(String tabId) {
				    
                    //֪ͨ��ܽ��淢������ת///////////
                    Intent tmpIntent = new Intent(
                            MainpageActivity.START_ACTIVITY);
                    Bundle bundleToSend = new Bundle();
                    bundleToSend.putString("act", THIS_ACT);
                    bundleToSend.putString("actTabName", blockNames[mTabHost.getCurrentTab()]);
                    bundleToSend.putString("switchInTab", "1");//��tab��ͨ�������ؼ�����������ת
                    tmpIntent.putExtras(bundleToSend);
                    sendBroadcast(tmpIntent);
                    tmpIntent = null;
                    bundleToSend = null;
                    //////////////////////////
				    
					try{
						ViewGroup vg = (ViewGroup)getTabHost().getTabContentView();
						int vgCount = vg.getChildCount();
						for(int i=0;i<vgCount;i++){
						    
						  //�л�TABʱ�Զ���ʾ��Ϣ  ��������
							Intent intent = new Intent(MainpageActivity.SHOW_TIP);
							Bundle sndBundle = new Bundle();
							sndBundle.putString("pviapfStatusTip",  "����������...");
							sndBundle.putString("pviapfStatusTipTime", "3000");
							intent.putExtras(sndBundle);
							sendBroadcast(intent);
							
							//2011-6-25 comment this ��((FrameLayout)((ViewGroup)vg.getChildAt(i)).getChildAt(0)).setForeground(null);
						}
					}catch(Exception e){

					}
				}
			});*/

			// �л�TABʱ����ʼ�л�ʱ������ʾ��Ϣ
			/*final int tabCount = tw.getChildCount();
			for (int i = 0; i < tabCount; i++) {
				final int curTabIndex = i;
				final TextView tv = (TextView) ((RelativeLayout) tw
						.getChildAt(i)).getChildAt(0);
				
//                tv.setOnTouchListener(new OnTouchListener(){
//
//					@Override
//					public boolean onTouch(View arg0, MotionEvent arg1) {
//						// TODO Auto-generated method stub
//						Logger.i(TAG," action:"+arg1.getAction());
//						if(arg1.getAction()==MotionEvent.ACTION_UP){
//		                    //֪ͨ�����ת
//		                    Intent tmpIntent = new Intent(
//		                            MainpageActivity.START_ACTIVITY);
//		                    Bundle bundleToSend = new Bundle();
//		                    bundleToSend.putString("act", THIS_ACT);
//		                    //Logger.i(TAG,"blockName:"+blockNames[mTabHost.getCurrentTab()]);
//		                    bundleToSend.putString("actTabName", ((TextView)arg0).getText().toString());
//		                    Logger.i(TAG,((TextView)arg0).getText());
//		                    //bundleToSend.putString("switchInTab", "1");//��tab��ͨ�������ؼ�����������ת
//		                    tmpIntent.putExtras(bundleToSend);
//		                    sendBroadcast(tmpIntent);
//		                    tmpIntent = null;
//		                    bundleToSend = null;
//		                    //////////////////////////
//						}
//						return true;
//					}
//                	
//                });
				
2011-6-23��ע��
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
								sndBundle.putString("pviapfStatusTipTime",
								"1000");
								tmpIntent.putExtras(sndBundle);
								sendBroadcast(tmpIntent);
							}
						};
						new Thread() {
							public void run() {
								mHandler.post(showTip);
							}
						}.start();

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
				});

			}*/


		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	protected void onResume() {  
		   
		   //if(saved){//�ӱ��activty ���ָ��������� ���Σ�tabactivity��ִ���κβ�������
		       //Logger.d(TAG,"�ӱ��activty ���ָ��������� ����");
		       //saved = false;
		  // }else{
		       //Logger.d(TAG,"ͨ��������");

		       final PviTabHost mTabHost = getTabHost();
		       
    	        // ���ָ����tabIndex (�Ӳ������ң�����Ҳ�����������Ϊ0)���л�����
    	        String actTabName = blockNames[0];
    	        
    	        final Intent it = getIntent();
    	        if (it != null) {
    	            Bundle bd = it.getExtras();
    	            if (bd != null) {
    
    	                actTabName = bd.getString("actTabName");
    	                if (actTabName == null) {
    	                    actTabName = blockNames[0];
    	                }
    
    
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
    	            final int curTabIndex = getBlockIndex(actTabName);
    	            mTabHost.setCurrentTab(curTabIndex);
    
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }

		  // }
    	        
    	 
    	 super.onResume();		
	}
	
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        //Logger.e(TAG, "onNewIntent");
        setIntent(intent);
        super.onNewIntent(intent);
    }

}
