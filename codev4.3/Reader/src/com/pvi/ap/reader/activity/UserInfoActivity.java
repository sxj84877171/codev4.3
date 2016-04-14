package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Constants;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.UserInfo;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * �鿴�û���Ϣ
 * 
 * @author �����
 * @author rd040 ������ 2010-11-15
 * 
 * 
 */
public class UserInfoActivity extends PviActivity {
	
	public static String phoneNum;
    // ������ͼ
    private TextView bindphone;
    private TextView devid;
    private Button unbindbtn;
    private TextView nickname;
    private TextView age;
    private TextView sex;
    private TextView address;
    private TextView signature;
    Button btnEdit;
    private int skinID = 1;//Ƥ��ID
    
    public final static String unBindSuccess = "result-code: 3212";

    private static final int VALIDATIONCODE_DIALOG = 1;
    private static final int VALIDATION_SUCCESS = 2;
    private static final int VALIDATION_EMPTYCODE = 3;
    private static final int VALIDATION_MAKESURE = 4;
    private static final String TAG = "UserInfoActivity";


    private Handler mHandler = new H();

    private boolean getUserInfoOK;          //��־�� �����Ƿ��Ѿ���ȡ�û���ϢOK
    //�����û���Ϣ
    private HashMap<String, Object> userinfo = new HashMap<String, Object>();
    
    private static final int GET_DATA = 101;
    private static final int CLOSE_PD = 102;
    public static final int SHOW_PD_LOADING = 103;
    public static final int SET_UI_DATA = 104;
    
    private PviAlertDialog pd;
    
    
    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            case GET_DATA:// ִ�� ��ȡ�û�����
                startGetUserInfoThread();
                break;
            case CLOSE_PD:// �ر���ʾ��
                if(pd!=null){pd.dismiss();}
                hideTip();
                break;
            case SHOW_PD_LOADING:// ��ʾ��������Ϣ��
            	showMessage("���ڼ�������...");
                //PviUiUtil.Alert(pd, getParent(), getResources().getString(R.string.kyleHint04), getResources().getString(R.string.kyleHint05), 0, false);
                //showAlert(getResources().getString(R.string.kyleHint04),getResources().getString(R.string.kyleHint05),false);
                break;
            case SET_UI_DATA:// �ѻ�ȡ�����������UI
                setUIData(userinfo);
                showMe();
                break;
            case 201:// �����쳣
            	//mHandler.sendEmptyMessage(CLOSE_PD);    
                showAlertReConnect("��ܰ��ʾ","�����쳣�����ȷ���������ӣ����ȡ��ȡ������");
                break;            
            case 202:// ���ӳ�ʱ
            	//mHandler.sendEmptyMessage(CLOSE_PD);
            	showAlertReConnect("��ܰ��ʾ","�������ӳ�ʱ�����ȷ���������ӣ����ȡ������");
                break;
            case 203:// �ӿڷ��ش���
            	//mHandler.sendEmptyMessage(CLOSE_PD);
            	Bundle b = msg.getData();
            	showGetUserError("��ܰ��ʾ",Error.getErrorDescriptionForContent(b.getString("error.code")));
                break;

            
            default:
                ;
            }

            super.handleMessage(msg);
        }

    }
    
    private void showAlert(String title,String message,boolean isHaveCloseBtn){
    	if(pd!=null && pd.isShowing()){
    		pd.dismiss();
    	}
        pd = new PviAlertDialog(getParent());
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setCanClose(isHaveCloseBtn);
        pd.show();
    }
    
    private void showAlertReConnect(String title,String message){
    	if(pd!=null && pd.isShowing()){
    		pd.dismiss();
    	}
        pd = new PviAlertDialog(getParent());
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        // TODO Auto-generated method stub
                    	mHandler.sendEmptyMessage(GET_DATA); 
                        return;
                    }

                });
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "ȡ��",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        // TODO Auto-generated method stub
                    	pd.dismiss();
                    	 sendBroadcast(new Intent(MainpageActivity.BACK));
                    }

                });
        pd.show();
    }
    
    private void showGetUserError(String title,String message){
    	if(pd!=null && pd.isShowing()){
    		pd.dismiss();
    	}
        pd = new PviAlertDialog(getParent());
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        // TODO Auto-generated method stub
                    	pd.dismiss();
                    	sendBroadcast(new Intent(MainpageActivity.BACK));
                    }

                });
      
        pd.show();
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.userinfo_ui1);
        super.onCreate(savedInstanceState);    
    }
    
    @Override
    public void initControls() {
        super.initControls();
        bindphone = (TextView) findViewById(R.id.bindphone);
        devid = (TextView) findViewById(R.id.devid);
        unbindbtn = (Button) findViewById(R.id.unbindbtn);
        GlobalVar appState = (GlobalVar)getApplicationContext();
        if(!appState.getSimType().equals("USIM")){
        	unbindbtn.setVisibility(View.GONE);
        }
        nickname = (TextView) findViewById(R.id.nickname);
        age = (TextView) findViewById(R.id.age);
        sex = (TextView) findViewById(R.id.sex);
        address = (TextView) findViewById(R.id.address);
        signature = (TextView) findViewById(R.id.signature);

        
        if(deviceType==1){
        	
//        	getWindow().
//            getDecorView()
//            .getRootView()
//            .invalidate(UPDATEMODE_4);
//        	//findViewById(R.id.bindphone_label).invalidate(0, 0, 600,800,UPDATEMODE_4);
//        	if(btnEdit!=null)
//        		btnEdit.setUpdateMode(UPDATEMODE_5);
//        	if(unbindbtn!=null)
//        		unbindbtn.setUpdateMode(UPDATEMODE_5);
//			//     
//        	if(devid!=null)
//        		devid.setUpdateMode(UPDATEMODE_5);
//        	if(bindphone!=null)
//        		bindphone.setUpdateMode(UPDATEMODE_5);
//        	if(age!=null)
//        		age.setUpdateMode(UPDATEMODE_5);
//        	if(sex!=null)
//        		sex.setUpdateMode(UPDATEMODE_5);
//        	if(address!=null)
//        		address.setUpdateMode(UPDATEMODE_5);
//        	if(signature!=null)
//        		signature.setUpdateMode(UPDATEMODE_5);
        }
        

        devid.setText(Constants.ms_DeviceID); 
    }
    

    public void showMe(){
    	 	Intent tmpIntent = new Intent(
	                MainpageActivity.SHOW_ME);
	        Bundle bundleToSend = new Bundle();
	        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
	        bundleToSend.putString("actTabName", "�û���Ϣ");
	        bundleToSend.putString("sender", UserInfoActivity.this.getClass().getName());
	        tmpIntent.putExtras(bundleToSend);
	        sendBroadcast(tmpIntent);
	        tmpIntent = null;
	        bundleToSend = null;
	        
	        hideTip();
    }
    @Override
    protected void onResume() {
        super.onResume();
        Logger.i(TAG,"onResume");
        long TimeStart = System.currentTimeMillis();
        Logger.i("Time", "UserInfoActivity enter: " + Long.toString(TimeStart));

        mHandler.sendEmptyMessage(GET_DATA);
        // �޸ĳɹ��󣬸���UI�ؼ��е�����
        try {
            final Bundle bd = getParent().getIntent().getExtras();
            if (bd != null) {
                final String resultCode = bd.getString("resultCode");
                if (resultCode.equals("0")) {
                    userinfo.put("NickName", bd.getString("NickName"));
                    userinfo.put("Sex", bd.getString("Sex"));
                    userinfo.put("Age", bd.getString("Age"));
                    userinfo.put("Signature", bd.getString("Signature"));
                    setUIData(userinfo);
                }
            }
        } catch (Exception e) {
            ;
        }
        
        //MainpageActivity.sendFrameBroadcast(UserInfoActivity.this,MainpageActivity.SHOW_ME);
        
	    
      
        
    }

    private void setUIData(HashMap<String, Object> userinfo) {
        if (userinfo.containsKey("NickName")) {
            this.nickname.setText(userinfo.get("NickName").toString());
        }
        ;
        if (userinfo.containsKey("Mobile")) {
        	phoneNum = userinfo.get("Mobile").toString();
            this.bindphone.setText(userinfo.get("Mobile").toString());
            // Log.d("Menu", "bindphone:" +
            // userinfo.get("Mobile").toString());
        }
        ;
        if (userinfo.containsKey("Sex")) {
            String temp = userinfo.get("Sex").toString();
            if (temp.equals("1")) {
                this.sex.setText("��");
            } else if (temp.equals("2")) {
                this.sex.setText("Ů");
            } else if (temp.equals("-1")) {
                this.sex.setText("����");
            }
            ;
        }
        ;
        if (userinfo.containsKey("Age")) {
            String temp = userinfo.get("Age").toString();
            this.age.setText(temp);

        }
        ;
        if (userinfo.containsKey("Province")) {
            this.address.setText(userinfo.get("Province").toString());

        }
        ;
        if (userinfo.containsKey("City")) {
            this.address.setText(address.getText().toString()
                    + userinfo.get("City").toString());

        }
        ;
        if (userinfo.containsKey("Signature")) {
            this.signature.setText(userinfo.get("Signature").toString());

        }
        
        //��δʹ��
        //sendBroadcast(new Intent(MainpageActivity.HIDE_ALERT));
    }

    private Runnable getUserInfo = new Runnable() {
        @Override
        public void run() {
            // Call ContentManager to getUserInfo then set to display

            final HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            final HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

            HashMap responseMap = null;
            try {

                responseMap = CPManager.getUserInfo(ahmHeaderMap, ahmNamePair);
                mHandler.sendEmptyMessage(CLOSE_PD);
                // Log.d("Menu", responseMap.get("result-code").toString());
                if (!responseMap.get("result-code").toString().contains(
                        "result-code: 0")) {
                         //��ʾ��ȡ�û���Ϣʧ����ʾ��
                	Message msg = new Message();
                	msg.what = 203;
                	Bundle b = new Bundle();
                	b.putString("error.code",responseMap.get("result-code").toString());
                	msg.setData(b);
					mHandler.sendMessage(msg); 
                    return;
                }
            } catch (HttpException e) {
                mHandler.sendEmptyMessage(CLOSE_PD);
                mHandler.sendEmptyMessage(201);
				return;
            	
            }catch (SocketTimeoutException e) {
                mHandler.sendEmptyMessage(CLOSE_PD);
                mHandler.sendEmptyMessage(202);

                //��ʾ��ȡ�û���Ϣʧ����ʾ��4964
                return;
            } catch (IOException e) {
                mHandler.sendEmptyMessage(CLOSE_PD);
                   mHandler.sendEmptyMessage(201);
					return;
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            //System.err.print(new String(responseBody));
            responseMap = null;//-- 

            Document dom = null;
            try {

                dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

            }catch (Exception e) {
                
                 return ;
            }
            Element root = dom.getDocumentElement();

            NodeList nl = root.getElementsByTagName("Parameter");
            String name = "";
            String value = "";
            Element temp = null;
            for (int i = 0; i < nl.getLength(); i++) {
            	name = "";
                value = "";
                temp = (Element) nl.item(i);
                name = temp.getElementsByTagName("name").item(0)
                        .getFirstChild().getNodeValue();
                try {
                    value = temp.getElementsByTagName("value").item(0)
                            .getFirstChild().getNodeValue();
                } catch (Exception e) {

                }
                userinfo.put(name, value);
            }
            getUserInfoOK = true;
            
            //�ͷŶ���
            responseBody=null;
            dom = null;
            nl = null;            
            
            mHandler.sendEmptyMessage(SET_UI_DATA);
            
            
        }

    };
    
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){
        @Override
        public void onUiItemClick(PviUiItem item) {

            final String vTag = item.id;
            if (vTag.equals("edit")) {

                /*
                 * ���ǰд�� Intent intent = new Intent(); // send UserID or not
                 * intent.setClass(UserInfoActivity.this,
                 * ModifyUserInfoActivity.class); startActivityForResult(intent,
                 * 0);
                 */
                if(!userinfo.isEmpty()){                    
                    gotoEditUserInfo();
                }else{
                    //������ʾ���û���Ϣ��δ����·��ȡ�ɹ�
                }
            }
           closePopmenu(); 
       
        }};
    

	
    private void startGetUserInfoThread() {
        mHandler.sendEmptyMessage(SHOW_PD_LOADING);        
        new Thread() {
            public void run() {
                getUserInfo.run();                
            }
        }.start();
    }



    @Override
    public OnUiItemClickListener getMenuclick() {
        return  this.menuclick;
    }
    
    @Override
    public void bindEvent() {
        // ���
        unbindbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	unbindbtn.setClickable(false);
                showDialog(VALIDATION_MAKESURE);
            }
        });
        
        // �޸�
        btnEdit = (Button)findViewById(R.id.ButtonEdit);
        if(btnEdit!=null){
        btnEdit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //�����û���Ϣ�޸Ľ���
                gotoEditUserInfo();
            }
        });
        }
        super.bindEvent();
        
    }
    
    public void gotoEditUserInfo(){
        if(userinfo.isEmpty()){
        pd = new PviAlertDialog(getParent());
        pd.setTitle("��ܰ��ʾ");
        pd.setMessage("��ȡ�û���Ϣ�쳣");
        pd.setCanClose(true);
        pd.show();
        return;
        }else{
        final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
        final Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act",
                "com.pvi.ap.reader.activity.ModifyUserInfoActivity");
        //bundleToSend.putString("startType", "allwaysCreate");
        bundleToSend.putString("haveTitleBar", "1");
        // ���ݵ�ǰ���û���Ϣ
        bundleToSend.putString("NickName", userinfo.get("NickName")
                .toString());
        bundleToSend.putString("Province", userinfo.get("Province").toString());
        bundleToSend.putString("Age", userinfo.get("Age").toString());
        bundleToSend.putString("Sex", userinfo.get("Sex").toString());
        bundleToSend.putString("Mobile", userinfo.get("Mobile").toString());
        bundleToSend.putString("DeviceID", Constants.ms_DeviceID);
        bundleToSend.putString("Signature", userinfo.get("Signature")
                .toString());
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        }
    }
    

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case VALIDATIONCODE_DIALOG:
            try {
                final LayoutInflater inflater = LayoutInflater.from(this);
                View entryview = null;
                if (skinID == 1) {
                    entryview = inflater.inflate(R.layout.validation_ui1, null);
                } else if (skinID == 2) {
                    entryview = inflater.inflate(R.layout.validation_ui2, null);
                }

                final PviAlertDialog pd = new PviAlertDialog(getParent());
                pd.setTitle("�����֤");
                pd.setView(entryview);
                pd.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method stub
                                showDialog(VALIDATION_SUCCESS);
                                return;
                            }

                        });
                pd.setButton(DialogInterface.BUTTON_NEUTRAL, "ȡ��",
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                // TODO Auto-generated method stub
                                return;
                            }

                        });
                return pd;

                /*
                 * ԭд��
                 * 
                 * AlertDialog.Builder builder = new AlertDialog.Builder(
                 * UserInfoActivity.this); builder.setTitle("Validate");
                 * builder.setView(entryview); builder.setPositiveButton("ȷ��",
                 * new android.content.DialogInterface.OnClickListener() {
                 * 
                 * @Override public void onClick(DialogInterface dialog, int
                 * which) { // TODO Auto-generated method stub
                 * showDialog(VALIDATION_SUCCESS); return; }
                 * 
                 * }); builder.setNegativeButton("ȡ��", new
                 * android.content.DialogInterface.OnClickListener() {
                 * 
                 * @Override public void onClick(DialogInterface dialog, int
                 * which) { // TODO Auto-generated method stub return; }
                 * 
                 * }); return builder.create();
                 */
            } catch (Exception e) {
                Logger.e(TAG, e.toString());
            }
        case VALIDATION_SUCCESS:
            final PviAlertDialog pd = new PviAlertDialog(getParent());
            pd.setTitle("������");
            pd.setMessage("�˺������ڽ���У�");
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendBroadcast(new Intent(MainpageActivity.BACK));
                            return;
                        }

                    });
            return pd;
            /*
             * AlertDialog.Builder builder = new AlertDialog.Builder(
             * UserInfoActivity.this); builder.setTitle("Validate Pass");
             * builder.setMessage("���ɹ���"); builder.setPositiveButton("ȷ��",
             * null); return builder.create();
             */
        case VALIDATION_EMPTYCODE:
            final PviAlertDialog pd3 = new PviAlertDialog(getParent());
            pd3.setTitle("��ܰ��ʾ");
            pd3.setMessage("���������ֻ����յ���֤�롣");
            pd3.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            return;
                        }

                    });
            return pd3;
            
        case VALIDATION_MAKESURE:
            final PviAlertDialog pd4 = new PviAlertDialog(getParent());
            pd4.setTitle("��ܰ��ʾ");
            pd4.setMessage("ȷ��Ҫ����󶨣����������ֻ��Ķ������е�Ȩ�潫�ᱣ����������û�����Э�顷");
            pd4.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        	GlobalVar appState = (GlobalVar)getApplicationContext();
                        	HashMap ahmHeaderMap = new HashMap();
                    		ahmHeaderMap.put("Accept", Config.getString("Accept"));
                    		ahmHeaderMap.put("Host", Config.getString("DRMHost"));
                    		ahmHeaderMap.put("User-Agent", Config.getString("User-Agent"));
                    		String Version = Config.getString("SoftwareVersion");
                    		String password = Config.getString("ClientPWD");
                    		ahmHeaderMap.put("Version", Version);
                    		String deviceID = Config.getString("DeviceId");
                    		ahmHeaderMap.put("deviceID", deviceID);
                    		ahmHeaderMap.put("Content-type", Config.getString("Content-type"));
                    		ahmHeaderMap.put("APIVersion", Config.getString("APIVersion"));
                    		String cardType = appState.getSimType();
                    		if (cardType.equals("USIM")) {
                    			ahmHeaderMap.put("x-up-calling-line-id", appState.getLineNum());
                    		}
                    		ahmHeaderMap.put("user-id", appState.getUserID());
                    		ahmHeaderMap.put("Client-Agent", Config.getString("Client-Agent"));
                    		if(cardType.equals("SIM")){
                    			ahmHeaderMap.put("proxyIP", Config.getString("proxyIP"));
                    	    	ahmHeaderMap.put("port", Config.getString("port"));
                    		}
                    		HashMap responseMap = null ;
                            try {
                                //��POST����ʽ��������
                                responseMap = CPManager.unbindHandset(ahmHeaderMap, responseMap);
                            } catch (HttpException e) {
                                //�����쳣 ,һ��ԭ��Ϊ URL����
                                //��ȡ�ֻ���֤��ʧ��
                                Logger.i(TAG, e.toString());
                                PviAlertDialog errorDialog =  new PviAlertDialog(getParent());
                                errorDialog.setCanClose(true);
                                errorDialog.setTitle("������");
                                errorDialog.setMessage("�������쳣");
                                errorDialog.show();
                                return;
                            } catch (SocketTimeoutException e) {
                                //�����쳣 ,��ʱ
                                //��ȡ�ֻ���֤��ʧ��
                                Logger.i(TAG, e.toString());
                                PviAlertDialog errorDialog =  new PviAlertDialog(getParent());
                                errorDialog.setCanClose(true);
                                errorDialog.setTitle("������");
                                errorDialog.setMessage("���������糬ʱ");
                                errorDialog.show();
                                return;
                            } catch (IOException e) {
                                //IO�쳣 ,һ��ԭ��Ϊ��������
                                //��ȡ�ֻ���֤��ʧ��
                                Logger.i(TAG, e.toString());
                                PviAlertDialog errorDialog =  new PviAlertDialog(getParent());
                                errorDialog.setCanClose(true);
                                errorDialog.setTitle("������");
                                errorDialog.setMessage(getString(R.string.net_failuely));
                                errorDialog.show();
                                return;
                            }
                            
                            String result = responseMap.get("result-code").toString();
                            Logger.i(TAG, result);
                            if (result.contains(unBindSuccess)){
                                //��ȡ�ֻ���֤��ɹ�
                            	appState.setLineNum("");
                    			appState.setRegCode("");
                    			appState.setUserID("");
                    			appState.setNeedAuth(true);

                    			Logger.i(TAG, "updateUserInfo");
                    			String columns[] = {
                    					UserInfo.UserID,UserInfo.RegCode,UserInfo.LineNum
                    			};
                    			String where = UserInfo.SimID + " = '" + appState.getSimID() + "'" ;//+ UserInfo.RegCode + " = '"+ regCode + "'"
                    			//Cursor cur = managedQuery(UserInfo.CONTENT_URI, columns, where, null, null);
                    			ContentValues values = new ContentValues();
                    			if(appState.getSimID() != null){
                    				values.put(UserInfo.SimID, appState.getSimID());
                    			}
                				values.put(UserInfo.UserID, "");
                				values.put(UserInfo.LineNum, "");
                				values.put(UserInfo.RegCode, "");
                				getContentResolver().update(UserInfo.CONTENT_URI, values, where, null);
                    		
                                showDialog(VALIDATION_SUCCESS);
                            }else{
                                //��ȡ�ֻ���֤��ʧ��
                            	if(result == null || "".equals(result.trim())){
                            		result = "3177" ;
                            	}else{
                            		result = result.substring(result.indexOf(' ') + 1);
                            	}
                                PviAlertDialog errorDialog =  new PviAlertDialog(getParent());
                                errorDialog.setCanClose(true);
                                errorDialog.setTitle("��ܰ��ʾ");
                                errorDialog.setMessage("���ʧ��!" + Error.getErrorDescription(result));
                                errorDialog.show();
                                
                            }
                            
                        }

                    });
            pd4.setButton(DialogInterface.BUTTON_NEUTRAL, "ȡ��",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            return;
                        }

                    });
            unbindbtn.setClickable(true);
            return pd4;

        }
        return null;
    }

}
