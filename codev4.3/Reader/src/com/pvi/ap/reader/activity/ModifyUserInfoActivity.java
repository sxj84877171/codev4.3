package com.pvi.ap.reader.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.SexSpinner;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.data.external.manager.LeafNode;
import com.pvi.ap.reader.data.external.manager.XMLUtil;
import com.pvi.ap.reader.data.external.manager.XmlElement;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 修改用户信息
 * 
 * @author 马中庆 2010-11-20
 * @author 田青红
 * 
 */
public class ModifyUserInfoActivity extends PviActivity {

	protected static final String TAG = "ModifyUserInfoActivity";
	private H mHandler = new H();
	private EditText nickname = null;
	private EditText age = null;
	private EditText signature = null;
	
	private TextView bindphone = null ;
	private TextView devid = null ;
	// private RadioGroup sex_group = null;
	private Button modifybtn = null;
	private HashMap<String, Object> userinfo = new HashMap<String, Object>(); // 存储用户信息
	private PviAlertDialog pd;

	private boolean btnClicked = false;

	private TextView address;

	private int themeNum;
	
	private SexSpinner sp;

	//

	private RadioButton malebtn = null;
	private RadioButton femalebtn = null;
	private RadioButton secrecybtn = null;

	private RadioGroup sex_group = null;
	
	TextView t1,t2,t3,t4,t5,t6;
	
	 public void showAlert(String message,boolean canColse){ 
		 
	    	
		 	pd = new PviAlertDialog(getParent());
	        pd.setTitle(getResources().getString(R.string.my_friend_hint));
	        //pd.setMessage(message);
	        TextView tv = new TextView(ModifyUserInfoActivity.this);
	        tv.setText(message);
	        tv.setTextSize(21);
	        tv.setGravity(Gravity.CENTER);
	        tv.setTextColor(Color.BLACK);
	        
	        pd.setView(tv);
	        pd.setCanClose(canColse);
	        pd.show();
	    }

	//

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
			case 1:// 弹出 修改成功 的对话框
				showAlert("修改个人信息成功！",true);
				/*
				PviAlertDialog pd1 = new PviAlertDialog(getParent());
				pd1.setTitle("修改个人信息结果");
				pd1.setMessage("修改个人信息成功！");
				pd1.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								
							
								
								Intent tmpIntent = new Intent(
										MainpageActivity.START_ACTIVITY);
								Bundle sndBundle = new Bundle();
								sndBundle.putString("actID",
								"ACT14100");
								sndBundle.putString("pviapfStatusTip", getResources()
										.getString(R.string.kyleHint01));
								sndBundle.putString("resultCode", "0");
								sndBundle.putString("NickName", userinfo.get(
										"NickName").toString());
								sndBundle.putString("Age", userinfo.get("Age")
										.toString());
								sndBundle.putString("Sex", userinfo.get("Sex")
										.toString());
								sndBundle.putString("Signature", userinfo.get(
										"Signature").toString());
								sndBundle.putString("startType", "allwaysCreate");
								tmpIntent.putExtras(sndBundle);
								sendBroadcast(tmpIntent);
								//startActivity(tmpIntent);
								return;
							}
						});
				pd1.show();*/
				break;
			case 2:// 弹出 修改失败 的对话框
				//showAlert("修改个人信息失败！",true);
				
				PviAlertDialog pd2 = new PviAlertDialog(getParent());
				pd2.setTitle("温馨提示");
				pd2.setMessage("操作出现网络错误！",Gravity.CENTER);pd2.setCanClose(false);
				pd2.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								// stub
								sendBroadcast(new Intent(MainpageActivity.BACK));
								return;
							}
						});
				pd2.show();
				break;
			case 3:// 弹出 昵称重复、修改失败 的对话框
				showAlert("修改个人信息失败， 原因：用户昵称重复！",true);
				/*
				PviAlertDialog pd3 = new PviAlertDialog(getParent());
				pd3.setTitle("修改个人信息结果");
				pd3.setMessage("修改个人信息失败， 原因：用户昵称重复！");
				pd3.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method
								// stub
								return;
							}
						});
				pd3.show();*/
				break;
			case 4:// 弹出 年龄不符合要求、修改失败 的对话框
				break;
			case 5:// 弹出 年龄不符合要求、请检查输入的对话框

				age.requestFocus();
				showAlert("年龄不符合要求！",true);
				/*
				pd = new PviAlertDialog(getParent());
				pd.setTitle("输入错误");
				pd.setMessage("年龄不符合要求！");

				pd.show();*/
				// close the dialog after 2000ms	
				mHandler.sendEmptyMessageDelayed(80, 2000);
				break;
			case 6:// 弹出 昵称不符合要求、请检查输入 的对话框
				break;
			case 7:// 弹出 输入不能为空 的对话框
				break;
			case 8:// 弹出 正在操作网络请等待 的对话框
				showAlert("正在保存数据到网络，请等待...",false);
				/*
				pd = new PviAlertDialog(getParent());
				pd.setTitle("温馨提示");
				pd.setMessage("正在保存数据到网络，请等待。。。");
				pd.setHaveProgressBar(true);
				pd.show();*/
				break;
			case 999:// 弹出 正在操作网络请等待 的对话框
				Bundle bundle = msg.getData();
				showAlert(bundle.getString("error"),true);
				break;
			case 80:// 关闭 正在操作网络请等待 的对话框
				if (pd != null) {
					pd.dismiss();
				}
				break;
			default:
				;
			}

			super.handleMessage(msg);
		}

	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		Intent intent = new Intent(MainpageActivity.SET_TITLE);
		Bundle sndBundle = new Bundle();
		sndBundle.putString("title", "修改个人信息");
		intent.putExtras(sndBundle);
		sendBroadcast(intent);

		setContentView(R.layout.modifyuserinfo_ui1);

		t1 = (TextView) findViewById(R.id.catalogBtn01);
		t2 = (TextView) findViewById(R.id.catalogBtn02);
		t3 = (TextView) findViewById(R.id.catalogBtn03);
		t4 = (TextView) findViewById(R.id.catalogBtn04);
		t5 = (TextView) findViewById(R.id.catalogBtn05);
		t6 = (TextView) findViewById(R.id.catalogBtn06);
		
		t1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14100");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14200");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t3.setOnClickListener(new OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14300");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t4.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14400");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t5.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14500");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
		t6.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14600");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
				tmpIntent = null;
				bundleToSend = null;
			}
		});
	

		malebtn = (RadioButton) findViewById(R.id.malebtn);
		femalebtn = (RadioButton) findViewById(R.id.femalebtn);
		secrecybtn = (RadioButton) findViewById(R.id.secrecybtn);
		sex_group = (RadioGroup) this.findViewById(R.id.sex_group);
		
		bindphone = (TextView)findViewById(R.id.bindphone);
		devid = (TextView)findViewById(R.id.devid);

		

		this.nickname = (EditText) this.findViewById(R.id.nickname);
		this.age = (EditText) this.findViewById(R.id.age);
		this.signature = (EditText) this.findViewById(R.id.signature);
		this.sp = (SexSpinner) this.findViewById(R.id.sex);
		//LinkedHashMap sexMap = new LinkedHashMap();
		//sexMap.put("男","1");
		//sexMap.put("女","2");
		//sexMap.put("保密","-1");
		//sp.setKey_value(sexMap);

		
		// sex_group = (RadioGroup) this.findViewById(R.id.sex_group);

		this.address = (TextView) findViewById(R.id.address);

		modifybtn = (Button) this.findViewById(R.id.commitbtn);

		
		
		if(deviceType==1){
//			findViewById(R.id.bindphone_label).invalidate(0, 0, 600,800,UPDATEMODE_4);
//			modifybtn.setUpdateMode(UPDATEMODE_5);
//        	sp.setUpdateMode(UPDATEMODE_5);
//        
//        	t1.setUpdateMode(UPDATEMODE_5);
//        	t2.setUpdateMode(UPDATEMODE_5);
//        	t3.setUpdateMode(UPDATEMODE_5);
//        	t4.setUpdateMode(UPDATEMODE_5);
//        	t5.setUpdateMode(UPDATEMODE_5);
//        	t6.setUpdateMode(UPDATEMODE_5);
//        	address.setUpdateMode(UPDATEMODE_5);
//        	signature.setUpdateMode(UPDATEMODE_5);
//        	age.setUpdateMode(UPDATEMODE_5);
//        	devid.setUpdateMode(UPDATEMODE_5);
//        	nickname.setUpdateMode(UPDATEMODE_5);
//        	bindphone.setUpdateMode(UPDATEMODE_5);
        }
		
		//bindEvent();
		super.onCreate(savedInstanceState);

	}

	OnClickListener modifyClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!btnClicked) {
				Logger.i(TAG, "valid click!");
				btnClicked = true;
				String agestr = age.getText().toString().trim();
				int age = -1;
				try{
					age = Integer.parseInt(agestr);
				}catch (Exception e) {
					/*pd = new PviAlertDialog(getParent());
					pd.setTitle("输入错误");
					pd.setMessage("输入年龄格式不正确！");
					pd.setCanClose(true);
					pd.show();*/
					showAlert("输入年龄格式不正确！",true);
					btnClicked = false;
					return;
				}
				if (age <= 0 || age > 110) {
					/*pd = new PviAlertDialog(getParent());
					pd.setTitle("输入错误");
					pd.setMessage("年龄不符合要求！");
					pd.setCanClose(true);
					pd.show();*/
					showAlert("年龄不符合要求！",true);
					btnClicked = false;
					return;
				}
				String sex = sp.getSelectValue();
				if(!"1".equals(sex) && !"2".equals(sex) && !"-1".equals(sex)){
					showAlert("请选择性别！",true);
					btnClicked = false;
					return;
				}

				// 新开线程取请求网络接口
				new Thread() {
					public void run() {
						doModify.run();
					}
				}.start();
			} else {
				Logger.i(TAG, "invalid click!");
			}
		}
	};

	OnClickListener cancleClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	public void bindEvent() {
		super.bindEvent();
		// 提交处理
		modifybtn.setOnClickListener(modifyClick);
		// 返回
		TextView returnBtn = (TextView) findViewById(R.id.returnbtn);
		returnBtn.setOnClickListener(new TextView.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID", "ACT14100");
				bundleToSend.putString("startType", "allwaysCreate");
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
			
			}
		});

	}

	private Runnable doModify = new Runnable() {
		@Override
		public void run() {

			String nicknamestr = nickname.getText().toString().trim();
			String agestr = age.getText().toString().trim();
			String sexstr = "";

			sexstr = ((RadioButton) findViewById(sex_group
					.getCheckedRadioButtonId())).getText().toString();

			if (sexstr.trim().equals("男")) {
				sexstr = "1";
			} else if (sexstr.trim().equals("女")) {
				sexstr = "2";
			} else {
				sexstr = "-1";
			}
			sexstr = sp.getSelectValue();
			String signatureStr = signature.getText().toString().trim();
			userinfo.put("NickName", nicknamestr);
			userinfo.put("Age", agestr);
			userinfo.put("Sex", sexstr);
			userinfo.put("Signature", signatureStr);

			LeafNode nametemp = new LeafNode("name", "NickName");
			LeafNode valtemp = new LeafNode("value", nicknamestr);

			List param = new ArrayList();
			param.add(nametemp);
			param.add(valtemp);
			XmlElement temp = new XmlElement("Parameter", param);
			List paramlist = new ArrayList();
			paramlist.add(temp);
			nametemp = new LeafNode("name", "Age");
			valtemp = new LeafNode("value", agestr);
			param = new ArrayList();
			param.add(nametemp);
			param.add(valtemp);
			temp = new XmlElement("Parameter", param);
			paramlist.add(temp);
			nametemp = new LeafNode("name", "Sex");
			valtemp = new LeafNode("value", sexstr);
			param = new ArrayList();
			param.add(nametemp);
			param.add(valtemp);
			temp = new XmlElement("Parameter", param);
			paramlist.add(temp);
			nametemp = new LeafNode("name", "Signature");
			valtemp = new LeafNode("value", signatureStr);
			param = new ArrayList();
			param.add(nametemp);
			param.add(valtemp);
			temp = new XmlElement("Parameter", param);
			paramlist.add(temp);

			XmlElement element = new XmlElement("ParameterList", paramlist);

			LeafNode totalcount = new LeafNode("totalRecordCount", "4");
			List totaltemp = new ArrayList();

			totaltemp.add(totalcount);
			totaltemp.add(element);

			XmlElement root = new XmlElement("UserInfo", totaltemp);
			root = XMLUtil.getParentXmlElement("ModifyUserInfoReq", root);
			root = XMLUtil.getParentXmlElement("Request", root);

			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();

			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
			HashMap responseMap = null;

			try {
				mHandler.sendEmptyMessage(8);

				ahmNamePair.put("XMLBody", XMLUtil
						.getXmlStringFromXmlElement(root));
				responseMap = CPManager.modifyUserInfo(ahmHeaderMap,
						ahmNamePair);

				mHandler.sendEmptyMessage(80);

				Logger.i(TAG, responseMap.get("result-code").toString());

				/*if(responseMap.get("result-code").toString().contains(
						"result-code: 0")) {
					mHandler.sendEmptyMessage(1);
					btnClicked = false;
					return;
				} else if (responseMap.get("result-code").toString().contains(
						"result-code: 3157")) {
					mHandler.sendEmptyMessage(3);
					btnClicked = false;
					return;
				} else {
					mHandler.sendEmptyMessage(2);
					btnClicked = false;
					return;
				}*/
				if(responseMap.get("result-code").toString().contains(
				"result-code: 0")) {
					mHandler.sendEmptyMessage(1);
					btnClicked = false;
					return;
				}else{
						//mHandler.sendEmptyMessage(3);
						String errorStr = Error.getErrorDescriptionForContent(responseMap.get("result-code").toString());
						Message msg = new Message();
						msg.what = 999;
						Bundle b = new Bundle();
						b.putString("error", errorStr);
						msg.setData(b);
						mHandler.sendMessage(msg);
						btnClicked = false;
						return;
				} 
			} catch (Exception e) {
				Logger.e(TAG, e.getMessage());
				mHandler.sendEmptyMessage(80);
				mHandler.sendEmptyMessage(2);
				btnClicked = false;
				return;
			}

		}
	};

	
	public void showMe(){
		
		
			Intent tmpIntent = new Intent(
	                MainpageActivity.SHOW_ME);
			Bundle bundleToSend = new Bundle();
	        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
	        bundleToSend.putString("actTabName", "个人信息修改");
	        bundleToSend.putString("sender", ModifyUserInfoActivity.this.getClass().getName());
	        tmpIntent.putExtras(bundleToSend);
	        sendBroadcast(tmpIntent);
	        tmpIntent = null;
	        bundleToSend = null;
	    
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 根据传入的值设置初始值

		Bundle bd = getIntent().getExtras();
		nickname.setText(bd.getString("NickName"));
		bindphone.setText(bd.getString("Mobile"));
		devid.setText(bd.getString("DeviceID"));
		age.setText(bd.getString("Age"));
		String temp = bd.getString("Sex");
		if (temp.equals("1")) {
			sp.setSelectKey("男");
			malebtn.setChecked(true);

		} else if (temp.equals("2")) {
			sp.setSelectKey("女");
			femalebtn.setChecked(true);

		} else if (temp.equals("-1")) {
			sp.setSelectKey("保密");
			secrecybtn.setChecked(true);

		}
		address.setText(bd.getString("Province"));
		signature.setText(bd.getString("Signature"));
		signature.setSelection(bd.getString("Signature").length());
		
		//EPDRefresh.refreshGCOnceFlash();
		showMe();
		
	}

}
