/**
 * 订购流程
 * @author rd029 晏子凯
 * 
 */
package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.SubscribeViewBuilder;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.content.MonthlyPayment;
import com.pvi.ap.reader.data.content.SubScribe;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import com.pvi.ap.reader.service.FileDownloadManage;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 订购下载处理
 * 
 * @author kyle
 * @author 马中庆
 * 
 */
public class SubscribeProcess extends Activity {
    private final String wxts = "温馨提示";
    private static final String TAG = "SubscribeProcess";

    private ArrayList<String> downloadChapterURLList = new ArrayList<String>();
    private ArrayList<String> downloadChapterIDList = new ArrayList<String>();

    public final static String START_ACTIVITY = "com.pvi.ap.reader.mainframe.START_ACTIVITY";// 在主要区域启动一个Activity（是否每次都OnCreate？）
    public final static String SET_TITLE = "com.pvi.ap.reader.mainframe.SET_TITLE";// 设置标题栏文字
    // public final static String BACK =
    // "com.pvi.ap.reader.mainframe.BACK";//返回上一个子Activity
    private String contentID = "empty";
    private String bookName = "empty";
    private String canDownload = "empty";
    private String isSerial = "empty";
    private String isFinish = "empty";
    private String chargeMode = "empty";
    private String chargeTip = "empty";
    private String subscribeMode = "empty";// fascicleSummary
                                           // ,fascicleList,catalog,feedback,orderbook
    private String fascicle = "empty"; // 有分册的 1:摘要页中进入 2:分册列表中进入
    private String ProductID = "empty";
    private String catalogID = "empty";
    private String authorName = "empty";
    private String chapterURL = "empty";
    private String downloadURL = "empty";
    private String fromCatalogType;// 书籍属于哪个区,按接口 1普通专区 2分类专区 3三元包月专区 4五元包月专区
                                   // 5促销专区 6主题专区
    private String fromCatalogID;// 专区ID
    private String catalogName = "empty";
    private String cataOrdered = "empty";
    private String fee = "empty";

    private boolean catalogOrdered = true;


    //private PviAlertDialog dialog_subscribeOK;
     //private PviAlertDialog dialog_loading;
    //private PviAlertDialog dialog_subscribed;

    private Context mContext = SubscribeProcess.this;
    private boolean doing = false;

    private static final int MOD1 = 101; // 按本收费图书
    private static final int MOD2 = 102;
    public static final int MOD3 = 103;
    public static final int MOD4 = 104;

    public static final int MOD6 = 106;
    public static final int MOD7 = 107;
    public static final int MOD8 = 108;

    public static final int SCATA_MODE1 = 301; // 订购栏目

    private Handler mHandler = new H();
    private PviAlertDialog pd;

    // mod1
    public static final int PD1 = 201;
    public static final int PD11 = 2011; // 已订购
    public static final int PD12 = 2012; // 未订购
    public static final int P13 = 2013;
    public static final int P14 = 2014;
    

    // Mod2
    public static final int PD2 = 202;
    public static final int P21 = 2021;
    public static final int P22 = 2022;
    public static final int P23 = 2023;
    public static final int PD21 = 20211;

    // mod3
    public static final int PD3 = 203;

    // mod4
    public static final int P4 = 204;

    // mod7
    public static final int PD7 = 207;
    public static final int P71 =2071;
    public static final int P72 =2072;
    
    //mod8
    final static int PD8 = 208;

    // scata_mode1
    public static final int PD_SC_1 = 401;
    private static final int P_SC_11 = 4011;
    public static final int PD_SC_11 = 40111;
    
    

    private class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
            
            //主流程
            case MOD1:
                new Thread() {
                    public void run() {
                        subscribeMode01();
                    }
                }.start();
                break;
            case MOD2:
                new Thread() {
                    public void run() {
                        subscribeMode02();
                    }
                }.start();
                break;
            case MOD3:
                new Thread() {
                    public void run() {
                        subscribeMode03();
                    }
                }.start();
                break;
            case MOD4:
                new Thread() {
                    public void run() {
                        subscribeMode04();
                    }
                }.start();
                break;

            case MOD6:
                new Thread() {
                    public void run() {
                        subscribeMode06();
                    }
                }.start();
                break;
            case MOD7:
                new Thread() {
                    public void run() {
                        subscribeMode07();
                    }
                }.start();
                break;
            case MOD8:
                new Thread() {
                    public void run() {
                        subscribeMode08();
                    }
                }.start();
                break;

            case SCATA_MODE1:
                new Thread() {
                    public void run() {
                        subscribeMode05();
                    }
                }.start();
                break;
                
                
                //子处理过程
                
            case P13:
                new Thread() {
                    public void run() {
                        p13();
                    }
                }.start();
                break;
            case P14:
                final Message msg14 = Message.obtain(msg);
                new Thread() {
                    public void run() {
                        p14(msg14);
                    }
                }.start();
                break; 
                
            case P21:
                final Message msg21 = Message.obtain(msg);
                new Thread() {
                    public void run() {
                        p21(msg21);
                    }
                }.start();
                break;
            case P22:
                    new Thread() {
                        public void run() {
                            p22();
                        }
                    }.start();
                    break;
            case P23:
                new Thread() {
                    public void run() {
                        p23();
                    }
                }.start();
                break;  
                
            case P71:
                new Thread() {
                    public void run() {
                        p71();
                    }
                }.start();
                break;
            case P72:
                final Message msg72 = Message.obtain(msg);
                new Thread() {
                    public void run() {
                        p72(msg72);
                    }
                }.start();
                break; 
                
            case P_SC_11:
                new Thread() {
                    public void run() {
                        p_sc_11();
                    }
                }.start();
                break;

            // 提示框
            case PD1:
                pd1();
                break;
            case PD11:
                pd11();
                break;
            case PD12:                
                pd12(msg);
                break;
            case PD2:
                pd2();
                break;
            case PD3:
                //pd3();
                break;
            case P4:
                new Thread() {
                    public void run() {
                       p4();
                    }
                }.start();
                break;
            case PD7:
                pd7();
                break;
            case PD8:
                //pd8();
                break;
            case PD21:
                pd21();
                break;

            case PD_SC_1:
                pd_sc1();
                break;
            case PD_SC_11:
                pd_sc_11(msg);
                break;
                
            case 9991:
            	String StrAULW = msg.getData().getString("StrAULW");
            	if (StrAULW!=null&&StrAULW.length()>9&&StrAULW.substring(0, 10).contains("Exception")) {
                    retry();
                    return;
                } else if (StrAULW!=null&&StrAULW.length()>18&&StrAULW.substring(0, 19).contains("0000")) {
                    PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setCanClose(true);
                    pd.setTitle("操作提示");
                    pd.setMessage("留言成功");
                    pd.show();
                    return;
                }
                break;    
            case 9992:
            	 StrAULW = msg.getData().getString("StrAULW");
            	 if (StrAULW!=null&&StrAULW.length()>9&&StrAULW.substring(0, 10).contains("Exception")) {
                     retry();
                     return;
                 } else if (StrAULW!=null&&StrAULW.length()>18&&StrAULW.substring(0, 19).contains("0000")) {
                     PviAlertDialog pd = new PviAlertDialog(getParent());
                     pd.setCanClose(true);
                     pd.setTitle("操作提示");
                     pd.setMessage("点书成功");
                     pd.show();
                     return;
                 }
                break; 

            default:
                ;
            }

            super.handleMessage(msg);
        }

    }

    private Handler retryHandler = new Handler() {
        public void handleMessage(Message msg) {

            final PviAlertDialog pd = new PviAlertDialog(getParent());
            pd.setTitle(R.string.kyleHint02);
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //onCreate(null);
                            onResume();
                        }
                    });
            pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ;//sendBroadcast(new Intent(WirelessTabActivity.BACK));
                        }
                    });
            pd.show();
        };
    };

    private Handler retry_subscribed = new Handler() {
        public void handleMessage(Message msg) {

            final PviAlertDialog pd = new PviAlertDialog(getParent());
            pd.setMessage("没有未订购章节");
            pd.setTitle(wxts);
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ;//sendBroadcast(new Intent(WirelessTabActivity.BACK));
                        }
                    });
            pd.show();
        };
    };
    final static String count = "1000";

    String[] chapterID = new String[3];

    TextView info;
    boolean activityFinished = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Logger.i(TAG, "onCreate");


        /*dialog_subscribeOK = new PviAlertDialog(getParent());
        dialog_subscribeOK.setCanClose(true);
        dialog_subscribeOK.setMessage("订购成功,进入我的下载?");
        dialog_subscribeOK.setTitle(wxts);
        dialog_subscribeOK.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityFinished = true;
                        Intent tmpIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle bundleToSend = new Bundle();
                        bundleToSend
                                .putString("act",
                                        "com.pvi.ap.reader.activity.MyBookshelfActivity");
                        bundleToSend.putString("startType", "allwaysCreate");
                        bundleToSend.putString("actTabName", "我的下载");
                        bundleToSend.putString("haveTitleBar", "1");
                        tmpIntent.putExtras(bundleToSend);
                        sendBroadcast(tmpIntent);
                    }
                });
        dialog_subscribeOK.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        back();
                    }
                });*/

/*        dialog_loading = new PviAlertDialog(getParent());
        dialog_loading.setCanClose(true);
        dialog_loading.setMessage("下载中,进入我的下载?");
        dialog_loading.setTitle(wxts);
        dialog_loading.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityFinished = true;
                        Intent tmpIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle bundleToSend = new Bundle();
                        bundleToSend
                                .putString("act",
                                        "com.pvi.ap.reader.activity.MyBookshelfActivity");
                        bundleToSend.putString("startType", "allwaysCreate");
                        bundleToSend.putString("actTabName", "我的下载");// 1：我的下载
                        bundleToSend.putString("haveTitleBar", "1");
                        tmpIntent.putExtras(bundleToSend);
                        sendBroadcast(tmpIntent);
                    }
                });
        dialog_loading.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        back();
                    }
                });*/

        /*dialog_subscribed = new PviAlertDialog(getParent());
        dialog_subscribed.setMessage("已订购过，开始下载,进入我的下载?");
        dialog_subscribed.setCanClose(true);
        dialog_subscribed.setTitle(wxts);
        dialog_subscribed.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityFinished = true;
                        Intent tmpIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle bundleToSend = new Bundle();
                        bundleToSend
                                .putString("act",
                                        "com.pvi.ap.reader.activity.MyBookshelfActivity");
                        bundleToSend.putString("startType", "allwaysCreate");
                        bundleToSend.putString("actTabName", "我的下载");
                        bundleToSend.putString("haveTitleBar", "1");
                        tmpIntent.putExtras(bundleToSend);
                        sendBroadcast(tmpIntent);
                    }
                });
        dialog_subscribed.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        back();
                    }
                });*/

    }

    // end of onCreate()

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Logger.i(TAG, "onResume");

        Bundle bundleToRec = new Bundle();
        bundleToRec = this.getIntent().getExtras();
        Logger.i(TAG, bundleToRec);
        this.setContentView(R.layout.subscribeprocess);
        info = (TextView) findViewById(R.id.info);
        info.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                back();
            }
        });
        contentID = bundleToRec.getString("contentID");
        if (null == contentID) {
            contentID = "empty";
        }
        bookName = bundleToRec.getString("bookName");
        if (null == bookName) {
            bookName = "empty";
        }
        canDownload = bundleToRec.getString("canDownload");
        if (null == canDownload) {
            canDownload = "empty";
        }
        isSerial = bundleToRec.getString("isSerial");
        if (null == isSerial) {
            isSerial = "empty";
        }
        isFinish = bundleToRec.getString("isFinish");
        if (null == isFinish) {
            isFinish = "empty";
        }
        chargeMode = bundleToRec.getString("chargeMode");
        if (null == chargeMode) {
            chargeMode = "empty";
        }
        chargeTip = bundleToRec.getString("chargeTip");
        if (null == chargeTip) {
            chargeTip = "empty";
        }
        subscribeMode = bundleToRec.getString("subscribeMode");
        if (null == subscribeMode) {
            subscribeMode = "empty";
        }
        catalogID = bundleToRec.getString("catalogID");
        if (null == catalogID) {
            catalogID = "empty";
        }
        chapterID[0] = bundleToRec.getString("chapterID");
        if (null == chapterID[0]) {
            chapterID[0] = "empty";
        }
        ProductID = bundleToRec.getString("ProductID");
        if (null == ProductID) {
            ProductID = "empty";
        }
        authorName = bundleToRec.getString("authorName");
        if (null == authorName) {
            authorName = "empty";
        }
        fascicle = bundleToRec.getString("fascicle");
        if (null == fascicle) {
            fascicle = "empty";
        }
        catalogName = bundleToRec.getString("catalogName");
        if (null == catalogName) {
            catalogName = "empty";
        }
        fee = bundleToRec.getString("fee");
        if (null == catalogName) {
            fee = "empty";
        }
        cataOrdered = bundleToRec.getString("catalogOrdered");
        if (null == cataOrdered) {
            cataOrdered = "empty";
        } else {
            cataOrdered = "false";
            catalogOrdered = false;
        }

        // subscribeMode = "presentbook";
        // contentID = "60949900";

        // 下载阅读,非连载,按本收费 OK
        if (subscribeMode.equals("download") && isSerial.equals("0")
                && chargeMode.equals("1")) {
            mHandler.sendEmptyMessage(MOD1);
            activityFinished = true;
            return;
        }
        // 下载阅读,连载中,按章收费 下载所有免费章节或收费章节,单章MEB
        if (subscribeMode.equals("download") && isSerial.equals("1")
                && chargeMode.equals("2") && isFinish.equals("0")) {
            mHandler.sendEmptyMessage(MOD7);
            activityFinished = true;
            return;
        }

        // 下载阅读,连载完本,无分册,按章收费，－－－－下载一个MEB
        if (subscribeMode.equals("download") 
                && !"1".equals(fascicle)
                && "1".equals(isSerial) 
                && "1".equals(isFinish) 
                && "2".equals(chargeMode)
                ) {
            mHandler.sendEmptyMessage(MOD2);
            activityFinished = true;
            return;
        }

        // 下载阅读,连载完本,有分册,按章收费，－－－－下载一个MEB
        if (subscribeMode.equals("download") && (fascicle.equals("1"))
                && isSerial.equals("1") && chargeMode.equals("2")) {
            //mHandler.sendEmptyMessage(MOD3);// 盘龙
            mHandler.sendEmptyMessage(MOD2);// 2011-5-29 改为与无分册的一样的下载逻辑
            activityFinished = true;
            return;
        }
        // 免费图书，下载阅读
        if (subscribeMode.equals("download") && chargeMode.equals("0")) {
            mHandler.sendEmptyMessage(MOD4);
            activityFinished = true;
            return;
        }

        // 分册,下载阅读,摘要页进入
        if (subscribeMode.equals("fascicleSummary")) {

            return;
        }

        // 分册，从分册列表点击“确认订购”
        if (subscribeMode.equals("fascicleList")) {
            mHandler.sendEmptyMessage(MOD8);
            activityFinished = true;
            return;
        }

        // 连载,在线阅读
        if (subscribeMode.equals("online") && isSerial.equals("1")) {
            mHandler.sendEmptyMessage(MOD6);
            activityFinished = true;
            return;
        }

        // catalog
        if (subscribeMode.equals("catalog") && catalogID != null) {

            mHandler.sendEmptyMessage(SCATA_MODE1);
            activityFinished = true;
            return;
        }

        if (subscribeMode.equals("feedback")) {
            feedback();
            activityFinished = true;
            return;
        }
        if (subscribeMode.equals("presentbook")) {
            presentbook();
            activityFinished = true;
            return;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // download
    private void download(String contentID, String chapterID, String PID,
            String url, String size, String ticketURL, int downloadType) {
        Logger.d("subscribeMode download url:", url);
        final Bundle extras = new Bundle();
        final GlobalVar appState = (GlobalVar) getApplicationContext();

        extras.putString("fileName", contentID + ".meb");
        extras.putString("name", bookName);
        extras.putString("contentID", contentID);
        extras.putString("downloadType", ""+downloadType);

        // chapter:调用getResources接口下载
        extras.putString("url", url);
        // insert to db
        extras.putString("bookSize", size);
       extras.putString("cateLog", "0");
        extras.putString("pathType", "1");
        extras.putString("processPer", "");
        extras.putString("author", authorName);
        extras.putString("maker", "0");
        extras.putString("saleTime", "0");

        extras.putString("Version", Config.getString("SoftwareVersion"));
        extras.putString("CID", contentID);
        extras.putString("PID", PID);
        extras.putString("Nonce", Config.getString("Nonce"));
        extras.putString("requesttype", "2");
        extras.putString("user-id", appState.getUserID());
        extras.putString("password", Config.getString("ClientPWD"));
        extras.putString("Accept", "*/*");
        extras.putString("Host", Config.getString("DRM_URL_DOWNLOADHOST"));
        extras.putString("User-Agent", "EInkStack");
        
        if("SIM".equalsIgnoreCase(appState.getSimType())){
            extras.putString("x-up-calling-line-id", appState.getLineNum());
        }

        // Intent mIntent = new Intent(this,FileDownloadService.class);
        // mIntent.putExtras(extras);
        // startService(mIntent);

        FileDownloadManage mFileDownloadManage = new FileDownloadManage(this);
        if (ticketURL != null) {
            mFileDownloadManage.downloadCert(extras);
        }
        if (chapterID != null) {
            mFileDownloadManage.downloadChapter(extras);
        } else {
            mFileDownloadManage.downloadMebBook(extras);
        }
        mFileDownloadManage.newDownloadTask();
    }

    private void downloadChapter(String contentID, String PID,
            ArrayList<String> urllist, ArrayList<String> idlist) {
        Logger.d("subscribeMode download chapters:", "");
        final Bundle extras = new Bundle();
        final GlobalVar appState = (GlobalVar) getApplicationContext();

        extras.putString("fileName", contentID + ".meb");
        extras.putString("name", bookName);
        extras.putString("contentID", contentID);

        // insert to db
        extras.putStringArrayList("urlList", urllist);
        extras.putStringArrayList("idList", idlist);

        extras.putString("cateLog", "0");
        extras.putString("pathType", "1");
        extras.putString("processPer", "");
        extras.putString("author", authorName);
        extras.putString("maker", "0");
        extras.putString("saleTime", "0");
        extras.putString("downloadType", "4");//目前只有连载中的使用此方法，故这里固定为4

        extras.putString("Version", Config.getString("SoftwareVersion"));
        extras.putString("CID", contentID);
        extras.putString("PID", PID);
        extras.putString("Nonce", Config.getString("Nonce"));
        extras.putString("requesttype", "2");
        extras.putString("user-id", appState.getUserID());
        extras.putString("password", Config.getString("ClientPWD"));
        extras.putString("Accept", "*/*");
        extras.putString("Host", Config.getString("DRM_URL_DOWNLOADHOST"));
        extras.putString("User-Agent", "EInkStack");
        
        if(!"SIM".equals(appState.getSimType())){
            extras.putString("x-up-calling-line-id", appState.getLineNum());
        }

        // Intent mIntent = new Intent(this,FileDownloadService.class);
        // mIntent.putExtras(extras);
        // startService(mIntent);

        FileDownloadManage mFileDownloadManage = new FileDownloadManage(this);
        mFileDownloadManage.downloadChapter(extras);
        mFileDownloadManage.newDownloadTask();
    }

    public static String network(String itface, String para1, String para2,
            String para3, String para4) {
        Logger.i("BubscribeProcess Network", "itface " + itface + "para1 "
                + para1 + "para2 " + para2);
        if (itface.equalsIgnoreCase("getAllSerialChapters")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("contentId", para1);
            ahmNamePair.put("start", para2);
            ahmNamePair.put("count", para3);
            try {
                responseMap = NetCache.getAllSerialChapters(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

            String rsp = new String(responseBody);
            rsp.equals("");
            return rsp;
        }
        if (itface.equalsIgnoreCase("getChaptersUrl")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("contentId", para1);
            if (para2 != null) {
                ahmNamePair.put("startChapterId", para2);
            }
            if (para3 != null) {
                ahmNamePair.put("start", para3);
            }
            if (para4 != null) {
                ahmNamePair.put("count", para4);
            }
            // ahmNamePair.put("start","0");
            // ahmNamePair.put("count",count);
            try {
                responseMap = NetCache
                        .getChaptersUrl(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("subscribeChapters")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><BatchSubscribeChapterReq><contentId>"
                    + para1 + "</contentId><ChapterIdList>";
            String tail = "</ChapterIdList></BatchSubscribeChapterReq></Request>";
            String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><BatchSubscribeChapterReq><contentId>"
                    + para1
                    + "</contentId><ChapterIdList><chapterId>"
                    + para2
                    + "</chapterId></ChapterIdList></BatchSubscribeChapterReq></Request>";
            String requestXMLBodyBatch = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><BatchSubscribeChapterReq><contentId>"
                    + para1
                    + "</contentId><ChapterIdList></ChapterIdList></BatchSubscribeChapterReq></Request>";
            if (para4 != null && para4.equals("batch")) {
                ahmNamePair.put("XMLBody", requestXMLBodyBatch);
            } else {
                ahmNamePair.put("XMLBody", requestXMLBody);
            }
            HashMap responseMap = null;
            try {
                responseMap = NetCache.subscribeChapters(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("subscribeChaptersSerializing")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><BatchSubscribeChapterReq><contentId>"
                    + para1 + "</contentId><ChapterIdList>";
            String tail = "</ChapterIdList></BatchSubscribeChapterReq></Request>";
            String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><BatchSubscribeChapterReq><contentId>"
                    + para1
                    + "</contentId><ChapterIdList><chapterId>"
                    + para2
                    + "</chapterId></ChapterIdList></BatchSubscribeChapterReq></Request>";
            String requestXMLBodyBatch = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Request><BatchSubscribeChapterReq><contentId>"
                    + para1
                    + "</contentId><ChapterIdList>"
                    + para2
                    + "</ChapterIdList></BatchSubscribeChapterReq></Request>";
            if (para4 != null && para4.equals("batch")) {
                ahmNamePair.put("XMLBody", requestXMLBodyBatch);
            } else {
                ahmNamePair.put("XMLBody", requestXMLBody);
            }
            HashMap responseMap = null;
            try {
                responseMap = NetCache.subscribeChapters(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("downloadContent")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            // if(!para2.equals(null)){
            // ahmNamePair.put("catalogId",para2);
            // }
            ahmNamePair.put("contentId", para1);
            HashMap responseMap = null;
            try {
                responseMap = NetCache.downloadContent(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rsp = new String(responseBody);
            rsp.equals("");
            return rsp;

        }
        if (itface.equalsIgnoreCase("downloadFreeContent")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            // if(!para2.equals(null)){
            // ahmNamePair.put("catalogId",para2);
            // }
            ahmNamePair.put("contentId", para1);
            HashMap responseMap = null;
            try {
                responseMap = NetCache.downloadFreeContent(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getContentProductInfo")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            if (para2 != null) {
                ahmNamePair.put("chapterId", para2);
            }
            ahmNamePair.put("contentId", para1);
            HashMap responseMap = null;
            try {
                responseMap = NetCache.getContentProductInfo(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rsp = new String(responseBody);
            rsp.equals("");
            return rsp;
        }
        if (itface.equalsIgnoreCase("subscribeContent")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            ahmNamePair.put("contentId", para1);
            if (para2 != null) {
                ahmNamePair.put("chapterId", para2);
            }
            if (para3 != null) {
                ahmNamePair.put("productId", para3);
            }
            if (para4 != null) {
                ahmNamePair.put("fascicleId", para4);
            }

            HashMap responseMap = null;
            try {
                responseMap = NetCache.subscribeContent(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("subscribeCatalog")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            // if(!para2.equals(null)){
            // ahmNamePair.put("catalogId",para2);
            // }
            ahmNamePair.put("catalogId", para1);
            HashMap responseMap = null;
            try {
                responseMap = NetCache.subscribeCatalog(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getCatalogSubscriptionList")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            try {
                responseMap = NetCache.getCatalogSubscriptionList(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;

        }
        if (itface.equalsIgnoreCase("getSubscriptionList")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("start", para1);
            ahmNamePair.put("count", para2);
            try {
                responseMap = NetCache.getSubscriptionList(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            try {
                System.out.println("返回的XML为：");
                System.out.println(CPManagerUtil
                        .getStringFrombyteArray(responseBody));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getFavorite")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("start", para1);
            ahmNamePair.put("count", para2);
            try {
                responseMap = NetCache.getFavorite(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            // try {
            // System.out.println("返回的XML为：");
            // System.out.println(CPManagerUtil
            // .getStringFrombyteArray(responseBody));
            // } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
            // }
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getUserBookmark")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("start", para1);
            ahmNamePair.put("count", para2);
            try {
                responseMap = NetCache.getUserBookmark(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

            try {
                System.out.println("返回的XML为：");
                System.out.println(CPManagerUtil
                        .getStringFrombyteArray(responseBody));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("deleteBookmark")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("bookmarkId", para1);
            try {
                responseMap = NetCache
                        .deleteBookmark(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("deleteAllUserBookmark")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            try {
                responseMap = NetCache.deleteAllUserBookmark(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("deleteFavorite")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("contentId", para1);
            try {
                responseMap = NetCache
                        .deleteFavorite(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("deleteAllFavorite")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            try {
                responseMap = NetCache.deleteAllFavorite(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("unsubscribeCatalog")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("catalogId", para1);
            try {
                responseMap = NetCache.unsubscribeCatalog(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }

            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;

        }
        if (itface.equalsIgnoreCase("getBookUpdateList")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("start", para1);
            ahmNamePair.put("count", para2);
            try {
                responseMap = NetCache.getBookUpdateList(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rsp = new String(responseBody);
            Logger.i("Reader", rsp);
            rsp.equals("");
            return rsp;
        }
        if (itface.equalsIgnoreCase("unbookUpdate")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("contentId", para1);
            try {
                responseMap = NetCache.unbookUpdate(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rsp = new String(responseBody);
            rsp.equals("");
            return rsp;
        }
        if (itface.equalsIgnoreCase("bookUpdateSet")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("type", para1);
            try {
                responseMap = NetCache.bookUpdateSet(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rsp = new String(responseBody);
            rsp.equals("");
            return rsp;
        }
        if (itface.equalsIgnoreCase("getBlockContent")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("blockId", para1);
            ahmNamePair.put("start", para2);
            ahmNamePair.put("count", para3);
            try {
                responseMap = NetCache.getBlockContent(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            
            if(responseMap==null){
                return "Exception_UNKNOW1";
            }
            
            if(responseMap.get("result-code")==null){
                return "Exception_UNKNOW2";
            }
            
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getAuthorInfo")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("authorId", para1);
            ahmNamePair.put("start", para2);
            ahmNamePair.put("count", para3);
            try {
                responseMap = NetCache.getAuthorInfo(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getSpecifiedRank")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("catalogId", "0");
            ahmNamePair.put("rankType", para1);
            ahmNamePair.put("rankTime", para2);
            ahmNamePair.put("start", para3);
            ahmNamePair.put("count", para4);
            try {
                responseMap = NetCache.getSpecifiedRank(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getCatalogHomePage")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("catalogId", para1);
            try {
                responseMap = NetCache.getCatalogHomePage(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            
            if(responseMap==null){
                return "Exception_UNKNOWN1";
            }
            if(responseMap.get("result-code")==null){
                return "Exception_UNKNOWN2";
            }
            
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getCatalogContent")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("catalogId", para1);
            if (para2 != null) {
                ahmNamePair.put("start", para2);
            }
            if (para3 != null) {
                ahmNamePair.put("count", para3);
            }
            try {
                responseMap = NetCache.getCatalogContent(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getSystemParameter")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            try {
                responseMap = NetCache.getSystemParameter(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getBlockList")) {             
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("catalogId", para1);
            try {
                responseMap = NetCache.getBlockList(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("getRecommendContentList")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("contentId", para1);
            ahmNamePair.put("type", para2);// 1读过此书的还读过 2订购过此书的还订购过 3同类型书 4同系列的书
            ahmNamePair.put("start", para3);
            ahmNamePair.put("count", para4);
            try {
                responseMap = NetCache.getRecommendContentList(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }

        if (itface.equalsIgnoreCase("addUserLeaveWord")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Request><AddUserLeaveWordRequest><wordsContent>"
                    + para1
                    + "</wordsContent></AddUserLeaveWordRequest></Request>";
            ahmNamePair.put("XMLBody", requestXMLBody);
            try {
                responseMap = CPManager.addUserLeaveWord(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }

        if (itface.equalsIgnoreCase("userOrderBook")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Request><UserOrderBookRequest><orderBookContent>"
                    + para1
                    + "</orderBookContent></UserOrderBookRequest></Request>";
            ahmNamePair.put("XMLBody", requestXMLBody);
            try {
                responseMap = CPManager
                        .userOrderBook(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("recommendedContent")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Request><RecommendedContentReq><contentID>"
                    + para1
                    + "</contentID><chapterID>"
                    + para2
                    + "</chapterID><msisdn>"
                    + para3
                    + "</msisdn><message>"
                    + para4 + "</message></RecommendedContentReq></Request>";

            ahmNamePair.put("XMLBody", requestXMLBody);
            try {
                responseMap = CPManager.recommendedContent(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        if (itface.equalsIgnoreCase("presentBook")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Request><PresentBookReq><contentId>"
                    + para1
                    + "</contentId><msisdn>"
                    + para2
                    + "</msisdn><message>"
                    + para3 + "</message></PresentBookReq></Request>";

            ahmNamePair.put("XMLBody", requestXMLBody);
            try {
                responseMap = CPManager.presentBook(ahmHeaderMap, ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }

            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }

        if (itface.equalsIgnoreCase("getPresentBookInfo")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            ahmNamePair.put("contentId", para1);

            HashMap responseMap = null;
            try {
                responseMap = CPManager.getPresentBookInfo(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }

        // 获取分册列表接口
        if (itface.equalsIgnoreCase("getFascicleList")) {

            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("contentId", para1);
            if (para2 != null) {
                ahmNamePair.put("start", para2);
            }
            if (para3 != null) {
                ahmNamePair.put("count", para3);
            }

            try {
                responseMap = NetCache.getFascicleList(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }

        return "para error";
    }

    public static String getvalue(String source, String pattern) {

        Logger.d(TAG, "getvalue  pattern:" + pattern);
        if (source == null || (source != null && source.equals(""))) {
            return "error";
        }
        source = source.replace("result-code: 0000\r\n", "");
        try {

            DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbfactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(source.getBytes());
            Document dom = db.parse(is);
            Element rootele = dom.getDocumentElement();
            NodeList nl = rootele.getElementsByTagName(pattern);
            String tmp = "error";
            if (nl.item(0) != null) {
                tmp = nl.item(0).getFirstChild().getNodeValue();
            }
            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 从一个xml中取出某tag第一次出现的值
     * @param source
     * @param pattern
     * @return
     */
    public static String getFirstValue(String source, String pattern) {
        Logger.d(TAG, "getFirstValue  pattern:" + pattern);
        if (source == null || (source != null && source.equals(""))) {
            return null;
        }
        source = source.replace("result-code: 0000\r\n", "");
        try {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbfactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(source.getBytes());
            Document dom = db.parse(is);
            Element rootele = dom.getDocumentElement();
            NodeList nl = rootele.getElementsByTagName(pattern);

            if (nl.item(0) != null) {
                String tmp = nl.item(0).getFirstChild().getNodeValue();
                if (tmp == null) {
                    tmp = "";
                }
                return tmp;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 取出某tag的第二次出现时的值
     * @param source
     * @param pattern
     * @return
     */
    public static String getSecondValue(String source, String pattern) {
        Logger.d(TAG, "getSecondValue  pattern:" + pattern);
        if (source == null || (source != null && source.equals(""))) {
            return null;
        }else{
           Logger.d(TAG,source);
        }
        source = source.replace("result-code: 0000\r\n", "");
        try {
            DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder db = dbfactory.newDocumentBuilder();
            InputStream is = new ByteArrayInputStream(source.getBytes());
            Document dom = db.parse(is);
            Element rootele = dom.getDocumentElement();
            NodeList nl = rootele.getElementsByTagName(pattern);

            if (nl.item(1) != null) {
                String tmp = nl.item(1).getFirstChild().getNodeValue();
                if (tmp == null) {
                    tmp = "";
                }
                return tmp;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String network2(String itface, String para1, String para2,
            String para3, String para4, String para5) {
        if (itface.equalsIgnoreCase("getSpecifiedRank")) {
            HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
            HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
            HashMap responseMap = null;
            ahmNamePair.put("catalogId", para1);
            ahmNamePair.put("rankType", para2);
            ahmNamePair.put("rankTime", para3);
            ahmNamePair.put("start", para4);
            ahmNamePair.put("count", para5);
            try {
                responseMap = CPManager.getSpecifiedRank(ahmHeaderMap,
                        ahmNamePair);
            } catch (HttpException e) {
                e.printStackTrace();
                return "Exception_Http";
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                return "Exception_TimeOut";
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception_IO";
            }
            String rspC = responseMap.get("result-code").toString();
            if (rspC.contains("result-code: 0")) {
                rspC = "result-code: 0000\r\n";
            }
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            String rspB = new String(responseBody);
            rspB.equals("");
            return rspC + rspB;
        }
        return "para error";

    }

    /*
     * @Override protected void onResume() { // TODO Auto-generated method stub
     * if(activityFinished){ back(); } super.onResume(); }
     * 
     * @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
     * 
     * if(keyCode == KeyEvent.KEYCODE_BACK){ //通知框架返回上一个子activty back(); return
     * false; } return super.onKeyUp(keyCode, event); }
     */

    public void insertDataBook(String id, String name, String author) {
        ContentValues values = new ContentValues();
        values.put(SubScribe.ContentID, id);
        values.put(SubScribe.Name, name);
        values.put(SubScribe.UserID, "BK-001");
        values.put(SubScribe.Author, author);
        values.put(SubScribe.URL, "");
        values.put(SubScribe.ChapterID, "");
        values.put(SubScribe.ChapterName, "");
        Date CurTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatter.format(CurTime);
        values.put(SubScribe.OrderTime, now);
        this.getContentResolver().insert(SubScribe.CONTENT_URI, values);
    }

    public void insertDataBookpack(String id, String name) {
        ContentValues values = new ContentValues();
        values.put(MonthlyPayment.UserID, "BK-001");
        values.put(MonthlyPayment.ParentCatalogName, "");
        values.put(MonthlyPayment.ParentCatalogID, "");
        values.put(MonthlyPayment.Fee, "");
        values.put(MonthlyPayment.URL, "");
        values.put(MonthlyPayment.CatalogID, id);
        values.put(MonthlyPayment.CatalogName, name);

        Date CurTime = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = formatter.format(CurTime);
        values.put(MonthlyPayment.NextChargeTime, now);
        values.put(MonthlyPayment.StartTime, now);
        this.getContentResolver().insert(MonthlyPayment.CONTENT_URI, values);

    }

    private void subscribeMode01() {
        Logger.d("subscribeMode", "01");
        showTip();

        String strGCPI = network("getContentProductInfo", contentID, null,
                null, null);
        if (strGCPI!=null&&strGCPI.length()>9&&strGCPI.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }
        Logger.d(TAG,strGCPI);
        ProductID = getvalue(strGCPI, "productID");
        String ordered = getvalue(strGCPI, "isOrdered");
        if (ordered.equalsIgnoreCase("false")) {
            
            //fee = getvalue(strGCPI, "feeDescription");
            
            Message msg = Message.obtain(mHandler);
            msg.what = PD12;
            Bundle bd = new Bundle();
            bd.putString("xml", strGCPI);
            msg.setData(bd);            
            mHandler.sendMessage(msg);

        } else if (ordered.equalsIgnoreCase("true")) {
            //下载过程
            mHandler.sendEmptyMessage(P13);
            
            mHandler.sendEmptyMessage(PD11);

        }

    }

    private int subscribeMode02() {
        Logger.d("subscribeMode", "02");
        showTip();

        mHandler.sendEmptyMessage(PD2);

        return 0;
    }

    // 盘龙
    private int subscribeMode03() {
        Logger.d("subscribeMode", "03");

        showTip();

        mHandler.sendEmptyMessage(PD3);

        return 0;
    }

    private int subscribeMode04() {
        Logger.d("subscribeMode", "04");
        Logger.d("SubscribeProcess", "free book");

        showTip();

        mHandler.sendEmptyMessage(P4); // 执行下载

        return 0;
    }

    private int subscribeMode05() {

        Logger.d("subscribe catalog", "05 catalog");

        showTip();

        mHandler.sendEmptyMessage(PD_SC_1);

        return 0;
    }

    private int subscribeMode06() {
        Logger.d("subscribeMode", "06");
        String StrSC = network("subscribeContent", contentID, chapterID[0],
                ProductID, null);
        if (StrSC!=null&&StrSC.length()>9&&StrSC.substring(0, 10).contains("Exception")) {
            retry();
            return 1;
        }
        if (StrSC!=null&&StrSC.length()>18&&StrSC.substring(0, 19).contains("0000")) {
            info.setText("按章订购成功,请进目录查看");
        } else {
            info.setText("订购出错,点击返回");
        }
        info.setVisibility(View.VISIBLE);
        return 0;
    }

    private int subscribeMode07() {
        Logger.d("subscribeMode", "07");
        showTip();
        mHandler.sendEmptyMessage(PD7);
        return 0;

    }

    private int subscribeMode08() {
        Logger.d("subscribeMode", "08");
        final PviAlertDialog pd = new PviAlertDialog(getParent());
        pd.setCanClose(true);
        pd.setTitle(chargeTip);
        pd.setMessage(catalogID);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String StrSC = network("subscribeContent", contentID,
                                null, ProductID, fascicle);
                        if (StrSC!=null&&StrSC.length()>9&&StrSC.substring(0, 10).contains("Exception")) {
                            retry();
                            return;
                        }
                        // if(StrSC.substring(0,
                        // 19).contains("0000")||StrSC.substring(0,
                        // 19).contains("3159")){
                        // subscribeBookOK();
                        // String strDC =
                        // network("downloadContent",contentID,null,null,null);
                        // if (strDC.substring(0, 10).contains("Exception")) {
                        // retry();
                        // return;
                        // }
                        // String url = getvalue(strDC,"url");
                        // String size = getvalue(strDC,"size");
                        // String ticketURL = getvalue(strDC,"ticketURL");
                        // download(contentID,null,contentID,url,size,ticketURL,null);
                        // dialog_subscribeOK.show();
                        // }else{
                        // retry();
                        // }
                    }
                });
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                    }
                });
        recommend(pd);
        pd.show();

        return 0;
    }

    private int feedback() {
        final String content = null;
        Logger.d("subscribeMode", "feedback");
        final PviAlertDialog pd = new PviAlertDialog(getParent());
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.feedback, null);
        final EditText et = (EditText) layout.findViewById(R.id.editText);
        final TextView lw = (TextView) layout.findViewById(R.id.leaveword);
        final TextView ob = (TextView) layout.findViewById(R.id.orderbook);
        TextPaint tp = lw.getPaint();
        tp.setFakeBoldText(true);
        tp = ob.getPaint();
        tp.setFakeBoldText(true);

        lw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String content = et.getText().toString();
                if (content.equals("")) {
                    PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setCanClose(true);
                    pd.setTitle("操作提示");
                    pd.setMessage("没有输入。");
                    pd.show();
                    return;
                } else {
                	new Thread(){
                		public void run() {
                			String StrAULW = network("addUserLeaveWord", et.getText()
                                    .toString(), null, null, null);
                			Message msg = new Message();
                			msg.what = 9991;
                			Bundle b = new Bundle();
                			b.putString("StrAULW", StrAULW);
                			msg.setData(b);
                			mHandler.sendMessage(msg);
                		};
                	}.start();
                    
                    pd.dismiss();
                    
                }
            }
        });
        ob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String content = et.getText().toString();
                if (content.equals("")) {
                    PviAlertDialog pd = new PviAlertDialog(getParent());
                    pd.setCanClose(true);
                    pd.setTitle("操作提示");
                    pd.setMessage("没有输入。");
                    pd.show();
                    return;
                } else {
                	new Thread(){
                		public void run() {
                			String StrAULW = network("userOrderBook", et.getText()
                                    .toString(), null, null, null);
                			Message msg = new Message();
                			msg.what = 9992;
                			Bundle b = new Bundle();
                			b.putString("StrAULW", StrAULW);
                			msg.setData(b);
                			mHandler.sendMessage(msg);
                		};
                	}.start();
                    
                    pd.dismiss();
                   
                }
            }

        });
        pd.setView(layout);
        pd.setCanClose(true);
        pd.setTitle("反馈点书");
        pd.setWinWidth(400);
        pd.show();

        return 0;

    }

    private int presentbook() {
        String StrGPBI = network("getPresentBookInfo", contentID, null, null,
                null);
        if (StrGPBI!=null&&StrGPBI.length()>9&&StrGPBI.substring(0, 10).contains("Exception")) {
            retry();
            return 0;
        }
        
        if(StrGPBI!=null&&StrGPBI.length()>19){
        
        final String fee1 = getvalue(StrGPBI.substring(20), "description");
        final String fee2 = getvalue(StrGPBI.substring(20), "presentDepict");
        final String fee = fee1 + "\n" + fee2;

        final String content = null;
        Logger.d("subscribeMode", "presentbook");
        final PviAlertDialog pd = new PviAlertDialog(getParent());
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout layout = (LinearLayout) inflater.inflate(
                R.layout.presentbook, null);
        final TextView tvFee = (TextView) layout.findViewById(R.id.fee);
        final EditText msg = (EditText) layout.findViewById(R.id.message);
        final EditText cell = (EditText) layout.findViewById(R.id.phonenumber);
        pd.setView(layout);
        pd.setCanClose(true);
        pd.setTitle("赠送书籍");
        tvFee.setText(fee);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String content = cell.getText().toString();
                        if (content.equals("")) {
                            PviAlertDialog pd = new PviAlertDialog(getParent());
                            pd.setCanClose(true);
                            pd.setTitle("操作提示");
                            pd.setMessage("没有输入。");
                            pd.show();
                            return;
                        } else {
                            String StrAULW = network("presentBook", contentID,
                                    cell.getText().toString(), msg.getText()
                                            .toString(), null);
                            if (StrAULW!=null&&StrAULW.length()>9&&StrAULW.substring(0, 10).contains("Exception")) {
                                retry();
                                return;
                            } else if (StrAULW!=null&&StrAULW.length()>18&&StrAULW.substring(0, 19)
                                    .contains("0000")) {
                                PviAlertDialog pd = new PviAlertDialog(
                                        getParent());
                                pd.setCanClose(true);
                                pd.setTitle("操作提示");
                                pd.setMessage("书籍赠送成功");
                                pd.show();
                                return;
                            }

                        }

                    }
                });
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ;
                    }
                });
        pd.show();

        return 0;
        }else{
            return 0;
        }

    }

    private void retry() {
        retryHandler.sendEmptyMessage(0);
    }

    private void retry_subscribed() {
        retry_subscribed.sendEmptyMessage(0);
    }

    private void subscribeBookOK() {

    }

    private void subscribeCatalogOK() {

    }

    private int getCatalogSubscribed() {
        Logger.d(TAG, "check   getCatalogSubscribed");
        int three = 0;
        int five = 0;
        String StrGCSL = SubscribeProcess.network("getCatalogSubscriptionList",
                "0", "100", null, null);
        Logger.d(TAG, StrGCSL);
        if (StrGCSL!=null&&StrGCSL.length()>9&&StrGCSL.substring(0, 10).contains("Exception")) {
            Logger.e(TAG, "getCatalogSubscriptionList err");
            // retry();
            return 1;
        }

        try {
            if (StrGCSL!=null&&StrGCSL.length()>18&&StrGCSL.substring(0, 19).contains("0000")) {
                InputStream is = new ByteArrayInputStream(StrGCSL.substring(20).getBytes());
                Element rootele = null;
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(is);
                rootele = dom.getDocumentElement();
                NodeList nl = rootele.getElementsByTagName("CatalogInfo");
                for (int i = 0; i < nl.getLength(); i++) {
                    Element entry = (Element) nl.item(i);
                    NodeList nl2 = entry.getElementsByTagName("fee");
                    String tmp = nl2.item(0).getFirstChild().getNodeValue();
                    Logger.d("fee", tmp);
                    if (tmp.contains("3")) {
                        three = 3;
                    }
                    if (tmp.contains("5")) {
                        five = 5;
                    }
                }// end of content item loop
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 2;
        }
        return three + five;
    }

    // 关联推荐
    private void recommendCatalog(PviAlertDialog pd, String catalogfee) {
        final String fee = catalogfee;
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "订购" + catalogName + ","
                + fee, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final PviAlertDialog pd = new PviAlertDialog(getParent());
                pd.setCanClose(true);
                pd.setTitle("确认订购书包");
                pd.setMessage(fee);
                pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                String strSC = network("subscribeCatalog",
                                        catalogID, null, null, null);
                                if (strSC!=null&&strSC.length()>9&&strSC.substring(0, 10)
                                        .contains("Exception")) {
                                    retry();
                                    return;
                                }
                                if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19).contains("0000")) {
                                    subscribeCatalogOK();
                                    final PviAlertDialog pd2 = new PviAlertDialog(
                                            getParent());
                                    pd2.setCanClose(true);
                                    pd2.setTitle("书包订购成功");
                                    pd2
                                            .setButton(
                                                    DialogInterface.BUTTON_POSITIVE,
                                                    "确定",
                                                    new android.content.DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            back();
                                                        }
                                                    });
                                    pd2.show();
                                }
                                if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19).contains("3159")) {
                                    final PviAlertDialog pd = new PviAlertDialog(
                                            getParent());
                                    pd.setCanClose(true);
                                    pd.setTitle("书包已订购过");
                                    pd
                                            .setButton(
                                                    DialogInterface.BUTTON_POSITIVE,
                                                    "返回",
                                                    new android.content.DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            back();
                                                        }
                                                    });
                                    pd.show();
                                }
                            }
                        });

                pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                               
                            }
                        });

                pd.show();

            }
        });
    }

    private void recommendCatalog(PviAlertDialog pd, TextView tv,
            String catalogfee) {
        final String fee = catalogfee;
        tv.setText("订购" + catalogName + "," + fee);
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "订购专区",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final PviAlertDialog pd = new PviAlertDialog(
                                getParent());
                        pd.setCanClose(true);
                        pd.setTitle("确认订购书包");
                        pd.setMessage(fee);
                        pd
                                .setButton(
                                        DialogInterface.BUTTON_POSITIVE,
                                        "确定",
                                        new android.content.DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                String strSC = network(
                                                        "subscribeCatalog",
                                                        catalogID, null, null,
                                                        null);
                                                if (strSC!=null&&strSC.length()>9&&strSC.substring(0, 10)
                                                        .contains("Exception")) {
                                                    retry();
                                                    return;
                                                }
                                                if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19)
                                                        .contains("0000")) {
                                                    subscribeCatalogOK();
                                                    final PviAlertDialog pd2 = new PviAlertDialog(
                                                            getParent());
                                                    pd2.setCanClose(true);
                                                    pd2.setTitle("书包订购成功");
                                                    pd2
                                                            .setButton(
                                                                    DialogInterface.BUTTON_POSITIVE,
                                                                    "确定",
                                                                    new android.content.DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(
                                                                                DialogInterface dialog,
                                                                                int which) {
                                                                            back();
                                                                        }
                                                                    });
                                                    pd2.show();
                                                }
                                                if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19)
                                                        .contains("3159")) {
                                                    final PviAlertDialog pd = new PviAlertDialog(
                                                            getParent());
                                                    pd.setCanClose(true);
                                                    pd.setTitle("书包已订购过");
                                                    pd
                                                            .setButton(
                                                                    DialogInterface.BUTTON_POSITIVE,
                                                                    "返回",
                                                                    new android.content.DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(
                                                                                DialogInterface dialog,
                                                                                int which) {
                                                                            back();
                                                                        }
                                                                    });
                                                    pd.show();
                                                }
                                            }
                                        });

                        pd
                                .setButton(
                                        DialogInterface.BUTTON_NEGATIVE,
                                        "取消",
                                        new android.content.DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(
                                                    DialogInterface dialog,
                                                    int which) {
                                                
                                            }
                                        });

                        pd.show();

                    }
                });
    }

    private void recommendCatalog(TextView btn, TextView tv, String catalogfee) {
        final String fee = catalogfee;
        btn.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        btn.setText("确认订购");
        tv.setText("订购" + catalogName + "," + fee);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PviAlertDialog pd = new PviAlertDialog(getParent());
                pd.setCanClose(true);
                pd.setTitle("确认订购书包");
                pd.setMessage(fee);
                pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                String strSC = network("subscribeCatalog",
                                        catalogID, null, null, null);
                                if (strSC!=null&&strSC.length()>9&&strSC.substring(0, 10)
                                        .contains("Exception")) {
                                    retry();
                                    return;
                                }
                                if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19).contains("0000")) {
                                    subscribeCatalogOK();
                                    final PviAlertDialog pd2 = new PviAlertDialog(
                                            getParent());
                                    pd2.setCanClose(true);
                                    pd2.setTitle("书包订购成功");
                                    pd2
                                            .setButton(
                                                    DialogInterface.BUTTON_POSITIVE,
                                                    "确定",
                                                    new android.content.DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            back();
                                                        }
                                                    });
                                    pd2.show();
                                }
                                if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19).contains("3159")) {
                                    final PviAlertDialog pd = new PviAlertDialog(
                                            getParent());
                                    pd.setCanClose(true);
                                    pd.setTitle("书包已订购过");
                                    pd
                                            .setButton(
                                                    DialogInterface.BUTTON_POSITIVE,
                                                    "返回",
                                                    new android.content.DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            back();
                                                        }
                                                    });
                                    pd.show();
                                }
                            }
                        });

                pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                
                            }
                        });

                pd.show();
            }
        });
    }

    /**
     * 重写一个 推荐订购
     * 
     * @param btn
     * @param tv
     * @param catalogfee
     */
    private void recommendCatalog(Button btn, Button btnC, TextView tv, String catalogId,String cataName, String catalogfee) {
        Logger.d(TAG,"recom cata: catalogId:"+catalogId+",catalogName:"+cataName+",catalog feeDescribe:"+catalogfee);
        btnC.setText("取消");
        btnC.setVisibility(View.VISIBLE);
        btnC.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.dismiss();
                    }
                });
        
        final String cataId = catalogId;
        final String cataFee = catalogfee;
        btn.setVisibility(View.VISIBLE);
        tv.setVisibility(View.VISIBLE);
        btn.setText("确认订购");
        //tv.setText("订购" + cataName + "," + cataFee);
        tv.setText(cataFee);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pd!=null){
                    pd.dismiss();
                }
                pd = new PviAlertDialog(getParent());
                pd.setCanClose(true);
                pd.setTitle("确认订购");
                pd.setMessage(cataFee);
                pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                if(pd!=null){
                                    pd.dismiss();
                                }
                                pd = new PviAlertDialog(getParent());

                                new Thread() {
                                    public void run() {

                                        String strSC = network(
                                                "subscribeCatalog", cataId,
                                                null, null, null);
                                        if (strSC != null
                                                && strSC.length() > 9
                                                && strSC.substring(0, 10)
                                                        .contains("Exception")) {
                                            retry();
                                            return;
                                        }
                                        if (strSC != null
                                                && strSC.length() > 18
                                                && strSC.substring(0, 19)
                                                        .contains("0000")) {
                                            if(pd!=null){
                                            pd.setCanClose(true);
                                            pd.setTitle("订购成功");
                                            pd
                                                    .setButton(
                                                            DialogInterface.BUTTON_POSITIVE,
                                                            "确定",
                                                            new android.content.DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    onResume();
                                                                }
                                                            });
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.show();
                                                }
                                            });
                                            }
                                        } else if (strSC != null
                                                && strSC.length() > 18
                                                && strSC.substring(0, 19)
                                                        .contains("3159")) {

                                            if(pd!=null){
                                            pd.setCanClose(true);
                                            pd.setTitle("已订购过");
                                            pd
                                                    .setButton(
                                                            DialogInterface.BUTTON_POSITIVE,
                                                            "返回",
                                                            new android.content.DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(
                                                                        DialogInterface dialog,
                                                                        int which) {
                                                                    onResume();
                                                                }
                                                            });
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    pd.show();
                                                }
                                            });
                                            }
                                        }

                                    }
                                }.start();

                            }
                        });

                pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                    int which) {
                                ;
                            }
                        });

                pd.show();
            }
        });
    }

    // 普通专区中的关联推荐,推荐未包月用户订购3元区

    private int get3yuanid() {        
        
        int rCatalogid = -1;
        String cataname = null;
        String strGCH = SubscribeProcess.network("getCatalogHomepage", "1",
                null, null, null);
        if (strGCH!=null&&strGCH.length()>9&&strGCH.substring(0, 10).contains("Exception")) {
            Logger.d("get3yuanid", "network ERROR");
            return -1;
        }
        try {
            if (strGCH != null && strGCH.length() > 19) {
                InputStream is2 = new ByteArrayInputStream(strGCH.substring(20)
                        .getBytes());
                Element rootele2 = null;
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(is2);
                rootele2 = dom.getDocumentElement();

                NodeList nl = rootele2.getElementsByTagName("BlockContentInfo");
                for (int k = 0; k < nl.getLength(); k++) {
                    Element entry0 = (Element) nl.item(k);
                    NodeList nlcatalogid = entry0
                            .getElementsByTagName("blockContentID");
                    int catalogid = Integer.parseInt(nlcatalogid.item(0)
                            .getFirstChild().getNodeValue());
                    NodeList nn = entry0
                            .getElementsByTagName("BlockContentParam");
                    for (int j = 0; j < nn.getLength(); j++) {
                        Element eparam1 = (Element) nn.item(j);
                        NodeList nl3 = eparam1.getElementsByTagName("name");
                        String name = nl3.item(0).getFirstChild()
                                .getNodeValue();
                        if (name.equals("catalogType")) {
                            NodeList nl4 = eparam1
                                    .getElementsByTagName("value");
                            // primary[i].setText(nl4.item(0).getFirstChild().getNodeValue());
                            if (nl4.item(0).getFirstChild().getNodeValue()
                                    .equals("3")) {
                                Logger.d("default 3 yuan catalog", ""
                                        + catalogid);
                                // NodeList nl5 =
                                // eparam1.getElementsByTagName("name");
                                // name =
                                // nl5.item(0).getFirstChild().getNodeValue();
                                // if (name.equals("catalogName")) {
                                // NodeList nl6 =
                                // eparam1.getElementsByTagName("value");
                                // catalogName =
                                // nl6.item(0).getFirstChild().getNodeValue();
                                // }
                                rCatalogid = catalogid;
                            }
                            continue;
                        }
                        if (name.equals("catalogName")) {
                            NodeList nl4 = eparam1
                                    .getElementsByTagName("value");
                            cataname = nl4.item(0).getFirstChild()
                                    .getNodeValue();
                        }
                        if (cataname != null && rCatalogid != -1) {
                            catalogName = cataname;
                            catalogID = "" + rCatalogid;
                            return rCatalogid;
                        }

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Logger.d("", "get3yuan XML ERROR");
            return -2;
        }
        return 0;
    }
    
    private String get3yuanId(){
        return "32";
    }

    private String get3yuanfee(int catalogid) {
        String strGCH = SubscribeProcess.network("getCatalogContent", ""
                + catalogid, null, null, null);
        if (strGCH!=null&&strGCH.length()>9&&strGCH.substring(0, 10).contains("Exception")) {
            Logger.d("get3yuanfee", "network ERROR");
            return "";
        }
        try {
            if(strGCH!=null&&strGCH.length()>19){
                InputStream is2 = new ByteArrayInputStream(strGCH.substring(20)
                        .getBytes());
                Element rootele2 = null;
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(is2);
                rootele2 = dom.getDocumentElement();
                NodeList nl = rootele2.getElementsByTagName("description");
                return nl.item(0).getFirstChild().getNodeValue();
            }else{
                return "";
            }
        } catch (Exception e) {
            Logger.d("", "get3yuanfee XML ERROR");
            return "";
        }
    }
    
    /**
     * 获取栏目资费信息
     * @param catalogId
     * @return
     */
    private String getCatalogProductInfo(String catalogId){
        String xml = null;
        HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();        
        ahmNamePair.put("catalogId", catalogId);
        try {
            HashMap responseMap = NetCache.getAllSerialChapters(ahmHeaderMap,ahmNamePair);
            byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
            xml = new String(responseBody);
        } catch (HttpException e) {
            e.printStackTrace();
            return null;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }       

        return xml;
    }

    private void recommend(PviAlertDialog pd) {
        if (cataOrdered.equalsIgnoreCase("empty")) {
            if (0 == getCatalogSubscribed()) {
                Logger.d("subscribeProcess", "fromcommon");
                recommendCatalog(pd, get3yuanfee(get3yuanid()));
            }

        } else if (cataOrdered.equalsIgnoreCase("false")) {
            Logger.d("subscribeProcess", "fromcatalog");
            recommendCatalog(pd, fee);
        }
    }

    private void recommend(PviAlertDialog pd, TextView tv) {
        if (cataOrdered.equalsIgnoreCase("empty")) {
            if (0 == getCatalogSubscribed()) {
                Logger.d("subscribeProcess", "fromcommon");
                recommendCatalog(pd, tv, get3yuanfee(get3yuanid()));
            }

        } else if (cataOrdered.equalsIgnoreCase("false")) {
            Logger.d("subscribeProcess", "fromcatalog");
            recommendCatalog(pd, tv, fee);
        }
    }

    private void recommend(TextView btn, TextView tv) {
        if (cataOrdered.equalsIgnoreCase("empty")) {
            if (0 == getCatalogSubscribed()) {
                Logger.d("subscribeProcess", "fromcommon");
                recommendCatalog(btn, tv, get3yuanfee(get3yuanid()));
            }

        } else if (cataOrdered.equalsIgnoreCase("false")) {
            Logger.d("subscribeProcess", "fromcatalog");
            recommendCatalog(btn, tv, fee);
        }
    }

    private void recommend(Button btn, TextView tv) {

        if (cataOrdered.equalsIgnoreCase("empty")) {
            if (0 == getCatalogSubscribed()) {
                Logger.d("subscribeProcess", "fromcommon");
                recommendCatalog(btn, tv, get3yuanfee(get3yuanid()));
            }

        } else if (cataOrdered.equalsIgnoreCase("false")) {
            Logger.d("subscribeProcess", "fromcatalog");
            recommendCatalog(btn, tv, fee);
        }
    }

    private void back() {
        sendBroadcast(new Intent(WirelessTabActivity.BACK));
    }

    @Override
    protected void onDestroy() {
        Logger.d("subscribeProcess", "onDestroy");
        super.onDestroy();
    }

    private void pd1() {
     
    }

    private void pd11() {
        if(pd!=null){
            pd.dismiss();
        }
        pd = new PviAlertDialog(getParent());
        pd.setCanClose(true);
        pd.setTitle(wxts);
        pd.setMessage("已添加至下载列表，请在【我的下载】中查看。");
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "我的下载",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityFinished = true;
                        Intent tmpIntent = new Intent(
                                MainpageActivity.START_ACTIVITY);
                        Bundle bundleToSend = new Bundle();
                        bundleToSend
                                .putString("actID", "ACT12200");//我的下载
                        tmpIntent.putExtras(bundleToSend);
                        sendBroadcast(tmpIntent);
                    }
                });
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "返 回",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ;
                    }
                });
        pd.show();
    }

    private void pd12(Message msg) {
        if(msg==null){
            return;
        }
        String xml = "";
        Bundle bd = msg.getData();
        if(bd!=null){
            xml = bd.getString("xml");
        }
        
        //String feeDescription = getvalue(xml, "feeDescription");
        String feeDescription = getFirstValue(xml,"feeDescription");
        
        if(pd!=null){
            pd.dismiss();
        }
        pd = new PviAlertDialog(getParent());

        SubscribeViewBuilder sv = new SubscribeViewBuilder(mContext);

        sv.tvTop.setText(feeDescription);
        sv.tvTop.setVisibility(View.VISIBLE);
        sv.bTopLeft.setText("确认订购");
        sv.bTopLeft.setVisibility(View.VISIBLE);
        final String xml2 = xml;
        sv.bTopLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pd!=null){
                    pd.dismiss();
                }
                
                //订购 加入下载 过程
                Message msg = Message.obtain(mHandler);
                msg.what = P14;
                Bundle bd = new Bundle();
                bd.putString("contentID", contentID);
                bd.putString("productID", getFirstValue(xml2,"productID"));
                msg.setData(bd);            
                mHandler.sendMessage(msg);
            }
        });
        
        sv.bTopRight.setVisibility(View.INVISIBLE);


        // recommend(sv.bMiddleLeft,sv.tvMiddle);
      
        //推荐
        String catalogIdBelong = getFirstValue(xml,"catalogID");
        if(catalogIdBelong==null){
            Logger.d(TAG,"this content is not belong any catalog");
            if(0==getCatalogSubscribed()){//用户尚未订购任何包月栏目
                //String id3yuan = get3yuanId();
                //recommendCatalog(sv.bMiddleLeft,sv.bMiddleRight,sv.tvMiddle,id3yuan,"3元优惠专区",getFirstValue(getCatalogProductInfo(id3yuan),"feeDescription"));
                recommendCatalog(sv.bMiddleLeft,sv.bMiddleRight,sv.tvMiddle,"32","3元优惠专区","3元优惠专区资费介绍");
            }else{
                sv.bTopRight.setText("取消");
                sv.bTopRight.setVisibility(View.VISIBLE);
                sv.bTopRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.dismiss();
                    }
                });
            }
        }else{
            Logger.d(TAG,"this content belongs catalog:"+catalogIdBelong);
            if(!isOrdered("cata", catalogIdBelong)){
                recommendCatalog(sv.bMiddleLeft,sv.bMiddleRight,sv.tvMiddle,catalogIdBelong, getFirstValue(xml,"catalogName"), getSecondValue(xml,"feeDescription"));
            }else{
                sv.bTopRight.setText("取消");
                sv.bTopRight.setVisibility(View.VISIBLE);
                sv.bTopRight.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pd.dismiss();
                    }
                });
            }
        }

        pd.setView(sv.getView());
        pd.setTitle("订购提示");
        pd.setCanClose(true);
        pd.show();
    }
    
    /**
     * 下载 单本书（按本计费） 过程
     * @param msg
     */
    private void p13(){
        
        final String strDC = network("downloadContent", contentID, null, null,null);
        Logger.d(TAG,strDC);
        if (strDC==null || "".equals(strDC) || (strDC!=null&&strDC.length()>9&&strDC.substring(0, 10).contains("Exception"))) {
            return;
        }
        String url = getvalue(strDC, "url");
        String size = getvalue(strDC, "size");
        String ticketURL = getvalue(strDC, "ticketURL");
        download(contentID, null, contentID, url, size, ticketURL, 2);
    }
    
    /**
     * 订购  下载 单本书 过程
     * @param msg
     */
    private void p14(Message msg){
        if(msg==null){
            return;
        }
        String cID = "";
        String pID = "";
        Bundle bd = msg.getData();
        if(bd!=null){
            Logger.d(TAG,bd.toString());
            cID = bd.getString("contentID");
            pID = bd.getString("productID");
            
        }
        
        //订购
    String StrSC = network("subscribeContent", cID, null,
            pID, null);
    if (StrSC!=null&&StrSC.length()>9&&StrSC.substring(0, 10).contains("Exception")) {
        retry();
        return;
    }
    
    //下载
    if (StrSC!=null&&StrSC.length()>18&&StrSC.substring(0, 19).contains("0000")
            || StrSC!=null&&StrSC.length()>18&&StrSC.substring(0, 19).contains("3159")) {
        subscribeBookOK();
        String strDC = network("downloadContent", cID, null,
                null, null);
        if (strDC!=null&&strDC.length()>9&&strDC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }
        String url = getvalue(strDC, "url");
        String size = getvalue(strDC, "size");
        String ticketURL = getvalue(strDC, "ticketURL");
        download(cID, null, cID, url, size, ticketURL, 2);
        //dialog_subscribeOK.show();
        mHandler.sendEmptyMessage(PD11);
    } else {
        retry();
    }
    }

    /**
     * 提示框与过程混合？？？  
     */
    private void pd2() {

        Logger.i(TAG, contentID);
        showTip();

        pd = new PviAlertDialog(getParent());

        final SubscribeViewBuilder sv = new SubscribeViewBuilder(mContext);

        sv.tvTop.setText("下载免费章节。");
        sv.tvTop.setVisibility(View.VISIBLE);

        sv.bTopLeft.setText("确认下载");
        sv.bTopLeft.setVisibility(View.VISIBLE);
        sv.bTopLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pd!=null){
                    pd.dismiss();
                }
                
                Message msg = Message.obtain(mHandler);
                msg.what = P21;
                Bundle bd = new Bundle();
                bd.putString("isFinish", "1");
                msg.setData(bd);            
                mHandler.sendMessage(msg);
            }
        });
        
        sv.bTopRight.setVisibility(View.INVISIBLE);
        
        
        new Thread() {
            public void run() {
             // 判断是否已订购所有章节
                String strGASC = network("getAllSerialChapters", contentID, "1", "1",
                        null);
                Logger.d(TAG,"getAllSerialChapters");
                Logger.d(TAG,strGASC);
                if (strGASC != null && strGASC.length() > 10
                        && strGASC.substring(0, 10).contains("Exception")) {
                    retry();
                    return;
                }

                String ordered = getvalue(strGASC, "isOrdered");
                if ("true".equalsIgnoreCase(ordered)) {
                    sv.tvMiddle.setText("您已订购该书全部章节，下载全部章节。");
                    sv.tvMiddle.setVisibility(View.VISIBLE);
                    // 按钮为 确认下载
                    sv.bMiddleLeft.setText("确认下载");
                    sv.bMiddleLeft.setVisibility(View.VISIBLE);
                    sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(pd!=null){
                                pd.dismiss();
                            }
                            mHandler.sendEmptyMessage(P22);
                        }
                    });
                    
                    sv.bMiddleRight.setVisibility(View.INVISIBLE);

                } else {
                    // 按钮为 确认订购
                    String feeDescription = getvalue(strGASC, "feeDescription");
                    
                    sv.tvMiddle.setText(feeDescription);
                    sv.tvMiddle.setVisibility(View.VISIBLE);
                    
                    sv.bMiddleLeft.setText("确认订购");
                    sv.bMiddleLeft.setVisibility(View.VISIBLE);
                    sv.bMiddleRight.setVisibility(View.INVISIBLE);
                    sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(pd!=null){
                                pd.dismiss();
                            }
                            mHandler.sendEmptyMessage(P23);
                        }

                    });

                    /*// 推荐
                    String catalogIdRecom = getFirstValue(strGASC, "catalogID");
                    if (catalogIdRecom == null) {
                        if (0 == getCatalogSubscribed()) {// 用户尚未订购任何包月栏目
                            String id3yuan = get3yuanId();
                            recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, id3yuan,"3元优惠专区",getFirstValue(getCatalogProductInfo(id3yuan),"feeDescription"));
                        }else{
                            sv.bMiddleRight.setText("取消");
                            sv.bMiddleRight.setVisibility(View.VISIBLE);
                            sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pd.dismiss();
                                }
                            });
                        }
                        
                    } else {
                        if (!isOrdered("cata", catalogIdRecom)) {
                            recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, catalogIdRecom, getFirstValue(strGASC,
                                    "catalogName"),getSecondValue(strGASC,"feeDescription"));
                        }else{
                            sv.bMiddleRight.setText("取消");
                            sv.bMiddleRight.setVisibility(View.VISIBLE);
                            sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pd.dismiss();
                                }
                            });
                        }
                    }*/
                    
                 // 推荐  原处理在客户端过于麻烦，根据平台提供的推荐信息加以显示即可
                    String catalogIdRecom = getFirstValue(strGASC, "catalogID");
                    if (catalogIdRecom != null && !"".equals(catalogIdRecom)){
                            recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, catalogIdRecom, getFirstValue(strGASC,
                                    "catalogName"),getSecondValue(strGASC,"feeDescription"));
                        
                    }else{
                        sv.bMiddleRight.setText("取消");
                        sv.bMiddleRight.setVisibility(View.VISIBLE);
                        sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pd.dismiss();
                            }
                        });
                    }

                }        

                pd.setView(sv.getView());
                pd.setTitle(wxts);
                pd.setCanClose(true);
                
                mHandler.post(new Runnable(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        pd.show();
                    }});
                
                

            }
        }.start();

        
    }
    
    /**
     * 提示框：没有免费章节
     */
    private void pd21(){
        final PviAlertDialog newPd = new PviAlertDialog(getParent());
        newPd.setCanClose(true);
        newPd.setMessage("该书没有免费章节可以下载");
        newPd.setTitle(wxts);
        newPd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityFinished = true;
                    }
                });
        newPd.show();
    }
    
    /**
     * 下载免费章节过程   需要增加参数，以区分是 完本|连载中
     * 参数contentID
     */
    private void p21(Message msg) {
        if(msg==null){
            return;
        }
        String isFinish = "";
        Bundle bd = msg.getData();
        if(bd!=null){
            isFinish = bd.getString("isFinish");           
        }
        if(isFinish==null||"".equals(isFinish)){
            return;
        }        
        
        final String StrDFC = network("downloadFreeContent", contentID, null,
                null, null);
        if (StrDFC!=null&&StrDFC.length()>9&&StrDFC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }
        if (StrDFC!=null&&StrDFC.length()>18&&StrDFC.substring(0, 19).contains("0000")) {
            String url = getvalue(StrDFC.substring(20), "url");
            String size = getvalue(StrDFC.substring(20), "size");
            if("0".equals(isFinish)){
                download(contentID, null, contentID, url, size, null,
                    3);
            }else if("1".equals(isFinish)){
                download(contentID, null, contentID, url, size, null,
                        5);
            }
            mHandler.sendEmptyMessage(PD11);
        }else if (StrDFC!=null&&StrDFC.length()>18&&StrDFC.substring(0, 19).contains("3221")) {
            mHandler.sendEmptyMessage(PD21);
        }
    }
    
    /**
     * 下载全部章节过程
     */
    private void p22() {
               
        final String StrDC = network("downloadContent", contentID, null,
                null, null);
        if (StrDC!=null&&StrDC.length()>9&&StrDC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }
        String url = getvalue(StrDC, "url");
        String size = getvalue(StrDC, "size");
        String ticketURL = getvalue(StrDC, "ticketURL");
        download(contentID, null, contentID, url, size, ticketURL,
                6);
        mHandler.sendEmptyMessage(PD11);
    }
    
    /**
     * 先订购，再下载全部章节 过程
     */
    private void p23() {
        
       // 执行订购
       final String StrSC = network("subscribeChapters", contentID, " ",
                null, "batch");
        if (StrSC!=null&&StrSC.length()>9&&StrSC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }

        // 执行下载
        if (StrSC!=null&&StrSC.length()>18&&StrSC.substring(0, 19).contains("0000")
                || StrSC!=null&&StrSC.length()>18&&StrSC.substring(0, 19).contains("3159")) {
            String StrDC = network("downloadContent", contentID,
                    null, null, null);
            if (StrDC!=null&&StrDC.length()>9&&StrDC.substring(0, 10).contains("Exception")) {
                retry();
                return;
            }
            String url = getvalue(StrDC, "url");
            String size = getvalue(StrDC, "size");
            String ticketURL = getvalue(StrDC, "ticketURL");
            download(contentID, null, contentID, url, size,
                    ticketURL, 6);
            mHandler.sendEmptyMessage(PD11);
        } else {
            retry();
        }
        
    }

 /*   private void pd3() {
        pd = new PviAlertDialog(getParent());
        SubscribeViewBuilder sv = new SubscribeViewBuilder(mContext);
        sv.tvTop.setText("下载免费章节。");
        sv.tvTop.setVisibility(View.VISIBLE);
        sv.bTopLeft.setText("确认下载");
        sv.bTopLeft.setVisibility(View.VISIBLE);
        sv.bTopRight.setVisibility(View.INVISIBLE);
        sv.bTopLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String StrDFC = network("downloadFreeContent", contentID, null,
                        null, null);
                if (StrDFC!=null&&StrDFC.length()>9&&StrDFC.substring(0, 10).contains("Exception")) {
                    retry();
                    return;
                }
                if (StrDFC!=null&&StrDFC.length()>18&&StrDFC.substring(0, 19).contains("0000")) {
                    String url = getvalue(StrDFC.substring(20), "url");
                    String size = getvalue(StrDFC.substring(20), "size");
                    download(contentID, null, contentID, url, size, null,
                            "免费章节");
                    pd.dismiss();
                    //dialog_subscribeOK.show();
                    mHandler.sendEmptyMessage(PD11);
                }
                if (StrDFC!=null&&StrDFC.length()>18&&StrDFC.substring(0, 19).contains("3221")) {
                    mHandler.sendEmptyMessage(PD21);
                }
            }
        });

        // 判断是否已订购所有章节
        String strGASC = network("getAllSerialChapters", contentID, "1", "1",
                null);
        Logger.d(TAG, strGASC);
        if (strGASC != null && strGASC.length() > 10
                && strGASC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }

        String ordered = getvalue(strGASC, "isOrdered");
        if ("true".equalsIgnoreCase(ordered)) {
            sv.tvMiddle.setText("您已订购该书全部章节，下载全部章节。");
            sv.tvMiddle.setVisibility(View.VISIBLE);
            // 按钮为 确认下载
            sv.bMiddleLeft.setText("确认下载");
            sv.bMiddleLeft.setVisibility(View.VISIBLE);
            sv.bMiddleRight.setVisibility(View.INVISIBLE);
            sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pd!=null){
                        pd.dismiss();
                    }
                    String StrDC = network("downloadContent", contentID, null,
                            null, null);
                    if (StrDC!=null&&StrDC.length()>9&&StrDC.substring(0, 10).contains("Exception")) {
                        retry();
                        return;
                    }
                    String url = getvalue(StrDC, "url");
                    String size = getvalue(StrDC, "size");
                    String ticketURL = getvalue(StrDC, "ticketURL");
                    download(contentID, null, contentID, url, size, ticketURL,
                            "收费章节");
                    //dialog_subscribed.show();
                    mHandler.sendEmptyMessage(PD11);
                }
            });

        } else {
            // 按钮为 确认订购
            String feeDescription = getvalue(strGASC, "feeDescription");
            sv.tvMiddle.setText(feeDescription);
            sv.tvMiddle.setVisibility(View.VISIBLE);
            sv.bMiddleLeft.setText("确认订购");
            sv.bMiddleLeft.setVisibility(View.VISIBLE);
            sv.bMiddleRight.setVisibility(View.INVISIBLE);
            sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 执行订购
                    String StrSC = network("subscribeChapters", contentID, " ",
                            null, "batch");
                    if (StrSC!=null&&StrSC.length()>9&&StrSC.substring(0, 10).contains("Exception")) {
                        retry();
                        return;
                    }

                    // 执行下载
                    if (StrSC!=null&&StrSC.length()>18&&StrSC.substring(0, 19).contains("0000")
                            || StrSC!=null&&StrSC.length()>18&&StrSC.substring(0, 19).contains("3159")) {
                        String StrDC = network("downloadContent", contentID,
                                null, null, null);
                        if (StrDC!=null&&StrDC.length()>9&&StrDC.substring(0, 10).contains("Exception")) {
                            retry();
                            return;
                        }
                        String url = getvalue(StrDC, "url");
                        String size = getvalue(StrDC, "size");
                        String ticketURL = getvalue(StrDC, "ticketURL");
                        download(contentID, null, contentID, url, size,
                                ticketURL, "收费章节");
                        pd.dismiss();
                        //dialog_subscribeOK.show();
                        mHandler.sendEmptyMessage(PD11);
                    } else {
                        retry();
                    }
                }

            });

            // 推荐 (这里应该不同。。。因为这种属于有分册的情形)
            String catalogIdRecom = getFirstValue(strGASC, "catalogID");
            if (catalogIdRecom == null) {
                if (0 == getCatalogSubscribed()) {// 用户尚未订购任何包月栏目
                    String id3yuan = get3yuanId();
                    recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, id3yuan,"3元优惠专区",getFirstValue(getCatalogProductInfo(id3yuan),"feeDescription"));
                    
                }else{
                    sv.bMiddleRight.setText("取消");
                    sv.bMiddleRight.setVisibility(View.VISIBLE);
                    sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pd.dismiss();
                        }
                    });
                }
            } else {
                if (!isOrdered("cata", catalogIdRecom)) {
                    recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, catalogIdRecom,getFirstValue(strGASC,
                    "catalogName"), getSecondValue(strGASC,
                            "feeDescription"));
                    
                }else{
                    sv.bMiddleRight.setText("取消");
                    sv.bMiddleRight.setVisibility(View.VISIBLE);
                    sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pd.dismiss();
                        }
                    });
                }
            }

        }

        pd.setView(sv.getView());
        pd.setTitle("订购提示");
        pd.setCanClose(true);
        pd.show();
    }*/

    //按本  免费图书下载过程
    private void p4() {
        String StrDC = network("downloadFreeContent", contentID, null, null,
                null);
        if (StrDC != null && StrDC.length()>9 && StrDC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }
        String url = getvalue(StrDC, "url");
        String size = getvalue(StrDC, "size");
        download(contentID, null, contentID, url, size, null, 1);
        mHandler.sendEmptyMessage(PD11);// 弹出提示
    }

    private void pd_sc1() {
        pd = new PviAlertDialog(getParent());
        pd.setCanClose(true);
        pd.setTitle("订购提示");
        pd.setMessage(chargeTip);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "确认订购",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mHandler.sendEmptyMessage(P_SC_11);//订购包月过程
                    }
                });
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ;
                    }
                });

        pd.show();
    }
    
    /**
     * 通过msg传入的字符串，弹出提示信息：仅有一个确定按钮，且点击“确定”关闭，无操作（有操作的，待添加）
     */
    private void pd_sc_11(Message msg){
        if(msg==null){
            return;
        }
        String infoText = "";
        Bundle bd = msg.getData();
        if(bd!=null){

            infoText = bd.getString("infoText");
           
        }
        
        
        pd = new PviAlertDialog(getParent());
        pd.setCanClose(true);
        pd.setTitle(wxts);
        pd.setMessage(infoText);
        pd
                .setButton(
                        DialogInterface.BUTTON_POSITIVE,
                        "确定",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                //订购成功
                                ;
                            }
                        });
        pd.show();
    }
    
    /**
     * 过程：订购包月栏目  使用场景：从包月介绍页点“订购”，订购成功后resume介绍页
     */
    private void p_sc_11(){
        String strSC = network("subscribeCatalog", catalogID,
                null, null, null);
        Logger.d(TAG, strSC);
        if (strSC!=null&&strSC.length()>9&&strSC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }
        if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19).contains("0000")) {
            Message msg = Message.obtain(mHandler);
            msg.what = PD_SC_11;
            Bundle bd = new Bundle();
            bd.putString("infoText", getvalue(strSC,"description"));
            msg.setData(bd);            
            mHandler.sendMessage(msg);

        } else if (strSC!=null&&strSC.length()>18&&strSC.substring(0, 19).contains("3159")) {
            Message msg = Message.obtain(mHandler);
            msg.what = PD_SC_11;
            Bundle bd = new Bundle();
            bd.putString("infoText", "您已订购过。");
            msg.setData(bd);            
            mHandler.sendMessage(msg);
        } else {
            Message msg = Message.obtain(mHandler);
            msg.what = PD_SC_11;
            Bundle bd = new Bundle();
            bd.putString("infoText", "订购失败。");
            msg.setData(bd);            
            mHandler.sendMessage(msg);
        }
    }

    private void pd7() {
        pd = new PviAlertDialog(getParent());

        SubscribeViewBuilder sv = new SubscribeViewBuilder(mContext);

        sv.tvTop.setText("下载免费章节。");
        sv.tvTop.setVisibility(View.VISIBLE);
        sv.bTopLeft.setText("确认下载");
        sv.bTopLeft.setVisibility(View.VISIBLE);
        sv.bTopRight.setVisibility(View.INVISIBLE);
        sv.bTopLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pd!=null){
                    pd.dismiss();
                }
                Message msg = Message.obtain(mHandler);
                msg.what = P21;
                Bundle bd = new Bundle();
                bd.putString("isFinish", "0");
                msg.setData(bd);            
                mHandler.sendMessage(msg);
            }
        });

        // 判断是否已订购所有章节
        final String strGASC = network("getAllSerialChapters", contentID, "1",
                "1", null);
        if (strGASC != null && strGASC.length() > 10
                && strGASC.substring(0, 10).contains("Exception")) {
            retry();
            return;
        }

        String ordered = getvalue(strGASC, "isOrdered");
        if ("true".equalsIgnoreCase(ordered)) {
            sv.tvMiddle.setText("您已订购该书全部章节，下载全部章节。");
            sv.tvMiddle.setVisibility(View.VISIBLE);
            sv.bMiddleLeft.setText("确认下载");
            sv.bMiddleLeft.setVisibility(View.VISIBLE);
            sv.bMiddleRight.setVisibility(View.INVISIBLE);
            sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(pd!=null){
                        pd.dismiss();
                    }
                    mHandler.sendEmptyMessage(P71);
                }
            });
        } else {
            final String feeDescription = getvalue(strGASC, "feeDescription");// 取出资费描述
            
               if(!"error".equals(feeDescription)){   //回头把getvalue函数写规范些！
                
                    sv.tvMiddle.setText(feeDescription);
                    sv.tvMiddle.setVisibility(View.VISIBLE);
                    sv.bMiddleLeft.setText("确认订购");
                    sv.bMiddleLeft.setVisibility(View.VISIBLE);
                    sv.bMiddleRight.setVisibility(View.INVISIBLE);
                    sv.bMiddleLeft.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 二次确认   似乎不需要吧？？  暂时保留确认，因为这个下载可能会非常耗时。。。
                            final PviAlertDialog pd2 = new PviAlertDialog(getParent());
                            pd2.setTitle("温馨提示");
                            pd2.setCanClose(true);
                            //pd2.setMessage(feeDescription);
                            pd2.setMessage("按章节计费的连载中书籍，确认下载全部章节？");
                            pd2
                                    .setButton(
                                            DialogInterface.BUTTON_NEGATIVE,
                                            "取消",
                                            new android.content.DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    ;
                                                }
                                            });
                            pd2
                                    .setButton(
                                            DialogInterface.BUTTON_POSITIVE,
                                            "确定",
                                            new android.content.DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    if(pd!=null){
                                                        pd.dismiss();
                                                    }
                                                    
                                                    //批量 订购 加入下载 过程
                                                    Message msg = Message.obtain(mHandler);
                                                    msg.what = P72;
                                                    Bundle bd = new Bundle();
                                                    bd.putString("xml", strGASC);
                                                    msg.setData(bd);            
                                                    mHandler.sendMessage(msg);
                                                }
                                            });
                            pd2.show();
                            
                            
        
                        }
                        
                        
        
                    });                
            }
            
            // 处理推荐
            /*String catalogIdRecom = getFirstValue(strGASC, "catalogID");
            if (catalogIdRecom == null) {
                if (0 == getCatalogSubscribed()) {// 用户尚未订购任何包月栏目
                    String id3yuan = get3yuanId();
                    recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, id3yuan,"3元优惠专区",getFirstValue(getCatalogProductInfo(id3yuan),"feeDescription"));
                }else{
                    sv.bMiddleRight.setText("取消");
                    sv.bMiddleRight.setVisibility(View.VISIBLE);
                    sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pd.dismiss();
                        }
                    });
                }
            } else {
                if (!isOrdered("cata", catalogIdRecom)) {
                    recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, ""
                            + catalogIdRecom, getFirstValue(strGASC,
                            "catalogName"),getSecondValue(strGASC,
                            "feeDescription"));
                }else{
                    sv.bMiddleRight.setText("取消");
                    sv.bMiddleRight.setVisibility(View.VISIBLE);
                    sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pd.dismiss();
                        }
                    });
                }
            }*/
            
         // 推荐  原处理在客户端过于麻烦，根据平台提供的推荐信息加以显示即可
            String catalogIdRecom = getFirstValue(strGASC, "catalogID");
            if (catalogIdRecom != null && !"".equals(catalogIdRecom)){
                    recommendCatalog(sv.bBottomLeft,sv.bBottomRight, sv.tvBottom, catalogIdRecom, getFirstValue(strGASC,
                            "catalogName"),getSecondValue(strGASC,"feeDescription"));
                
            }else{               
                if(sv.bMiddleLeft.getVisibility()==View.VISIBLE){
                    sv.bMiddleRight.setText("取消");
                    sv.bMiddleRight.setVisibility(View.VISIBLE);
                    sv.bMiddleRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pd.dismiss();
                        }
                    });
                }else if(sv.bTopLeft.getVisibility()==View.VISIBLE){
                    sv.bTopRight.setText("取消");
                    sv.bTopRight.setVisibility(View.VISIBLE);
                    sv.bTopRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            pd.dismiss();
                        }
                    });
                }

            }
            
        }

        pd.setView(sv.getView());
        pd.setTitle("订购提示");
        pd.setCanClose(true);
        pd.show();
    }
    
    /**
     * 过程：针对按章收费的  连载中 图书，下载所有章节
     */
    private void p71(){
     // 自己实现的 下载目前所有本书章节的meb列表
        // 1、获得所有章节url放入list
        String firstChapterId = "";
        String strGCU = network("getChaptersUrl", contentID,
                firstChapterId, null, null);
        // 2、调根据url列表下载章节的接口
        if (strGCU!=null&&strGCU.length()>9&&strGCU.substring(0, 10).contains("Exception")) {
            retry();
            return;
        } else {
            try {
                if(strGCU!=null&&strGCU.length()>19){
                ArrayList<String> downloadChapterURLList = new ArrayList<String>();
                ArrayList<String> downloadChapterIDList = new ArrayList<String>();

                InputStream is = new ByteArrayInputStream(strGCU
                        .substring(20).getBytes());
                Element rootele = null;
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(is);
                rootele = dom.getDocumentElement();

                NodeList nl = rootele
                        .getElementsByTagName("ChapterInfo");
                // loop item:content item
                for (int j = 0; j < nl.getLength(); j++) {
                    Element entry = (Element) nl.item(j);
                    NodeList nl2 = entry
                            .getElementsByTagName("chapterID");
                    String chapterid = nl2.item(0).getFirstChild()
                            .getNodeValue();
                    NodeList nl3 = entry
                            .getElementsByTagName("chapterName");
                    String chaptername = nl3.item(0)
                            .getFirstChild().getNodeValue();
                    NodeList nl4 = entry
                            .getElementsByTagName("url");
                    String url = nl4.item(0).getFirstChild()
                            .getNodeValue();
                    downloadChapterURLList.add(url);
                    downloadChapterIDList.add(chapterid);
                    // download(contentID+chapterid,chapterid,contentID,url,null,null," 之 "
                    // + chaptername);
                }// end of content item loop
                downloadChapter(contentID, contentID,
                        downloadChapterURLList,
                        downloadChapterIDList);

                //dialog_subscribed.show();
                mHandler.sendEmptyMessage(PD11);
                }else{
                    return;
                }
                } catch (Exception e) {
                    e.printStackTrace();
                return;
            }
        }
    }
    
    /**
     * 过程：
     * 针对按章计费，连载中图书
     * 批量订购、下载全部章节
     */
    private void p72(Message msg){
        if(msg==null){
            return;
        }
        String xml = "";
        Bundle bd = msg.getData();
        if(bd!=null){
            xml = bd.getString("xml");            
        }
        
        
     // 批量订购所有未订购的章节

        String totalRecordCount = getvalue(
                xml, "totalRecordCount");// 获取未订购章节
                                             // 数量

        NodeList totalNl = null;

        String strGASC1 = network(
                "getAllSerialChapters",
                contentID, "1",
                totalRecordCount, null);
        {
            try {
                InputStream is = new ByteArrayInputStream(
                        strGASC1.getBytes());
                Element rootele = null;
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbfactory
                        .newDocumentBuilder();
                Document dom = db.parse(is);
                rootele = dom
                        .getDocumentElement();
                totalNl = rootele
                        .getElementsByTagName("chapterID");

                // end of content item loop
            } catch (Exception e) {
                e.printStackTrace();
                retry();
                return;
            }

        }

        final NodeList totalNl2 = totalNl;
        
        if(totalNl2==null){
            return;
        }

        String cpNode = null;
        for (int i = 0; i < totalNl2
                .getLength(); i++) {
            Logger.d("node", " " + i);
            String tmpNode = null;

            StringWriter sw = new StringWriter();
            Transformer serializer;
            try {
                serializer = TransformerFactory
                        .newInstance()
                        .newTransformer();
                serializer
                        .transform(
                                new DOMSource(
                                        totalNl2
                                                .item(i)),
                                new StreamResult(
                                        sw));
            } catch (Exception e1) {
                retry();
                e1.printStackTrace();
            }

            tmpNode = sw.toString();
            tmpNode = tmpNode.substring(38);
            tmpNode = tmpNode.replaceAll(
                    "chapterID",
                    "chapterId");
            Logger.d("tmpnode", tmpNode);
            cpNode = tmpNode + cpNode;
            if ((i % 9 == 0)
                    || i == (totalNl2
                            .getLength() - 1)) {
                // 订购
                String strSC = network(
                        "subscribeChaptersSerializing",
                        contentID, cpNode,
                        null, "batch");
                if (strSC!=null&&strSC.length()>9&&strSC
                        .substring(0, 10)
                        .contains(
                                "Exception")) {
                    retry();
                    return;
                } else {
                    Logger.d("cpNode",
                            cpNode);
                    cpNode = null;
                    Logger
                            .d(
                                    "serializing subscribe result",
                                    strSC);
                }
            }
        }

        // 获取章节url列表；调用下载

        String strGCU = network(
                "getChaptersUrl",
                contentID, null, "1", "9");
        if (strGCU!=null&&strGCU.length()>9&&strGCU.substring(0, 10)
                .contains("Exception")) {
            retry();
            return;
        } else {
            String totalRC = getvalue(
                    strGCU,
                    "totalRecordCount");
            strGCU = network(
                    "getChaptersUrl",
                    contentID, null, "1",
                    totalRC);
            if (strGCU!=null&&strGCU.length()>9&&strGCU.substring(0, 10)
                    .contains("Exception")) {
                retry();
                return;
            } else {
                try {
                    if(strGCU!=null&&strGCU.length()>19){
                    InputStream is = new ByteArrayInputStream(
                            strGCU
                                    .substring(
                                            20)
                                    .getBytes());
                    Element rootele = null;
                    DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                            .newInstance();
                    DocumentBuilder db = dbfactory
                            .newDocumentBuilder();
                    Document dom = db
                            .parse(is);
                    rootele = dom
                            .getDocumentElement();

                    NodeList nl = rootele
                            .getElementsByTagName("ChapterInfo");
                    // loop item:content
                    // item
                    for (int j = 0; j < nl
                            .getLength(); j++) {
                        Element entry = (Element) nl
                                .item(j);
                        NodeList nl2 = entry
                                .getElementsByTagName("chapterID");
                        String chapterid = nl2
                                .item(0)
                                .getFirstChild()
                                .getNodeValue();
                        NodeList nl3 = entry
                                .getElementsByTagName("chapterName");
                        String chaptername = nl3
                                .item(0)
                                .getFirstChild()
                                .getNodeValue();
                        NodeList nl4 = entry
                                .getElementsByTagName("url");
                        String url = nl4
                                .item(0)
                                .getFirstChild()
                                .getNodeValue();
                        downloadChapterURLList
                                .add(url);
                        downloadChapterIDList
                                .add(chapterid);
                    }// end of content item
                     // loop
                    downloadChapter(
                            contentID,
                            contentID,
                            downloadChapterURLList,
                            downloadChapterIDList);
                    pd.dismiss();
                    //dialog_subscribeOK.show();
                    mHandler.sendEmptyMessage(PD11);
                    
                    }else{
                        return;
                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    return;
                }
            }
        }
    }

    /**
     * 显示提示信息
     */
    public void showTip() {
        Intent intent = new Intent(MainpageActivity.SHOW_TIP);
        Bundle sndbundle = new Bundle();
        sndbundle.putString("pviapfStatusTip", "处理中请稍候...");
        sndbundle.putString("pviapfStatusTipTime", "3000");
        intent.putExtras(sndbundle);
        sendBroadcast(intent);
    }

    /**
     * 正在处理中。。。
     */
    private void markDoing() {

    }

    /**
     * 获取推荐栏目或分册的信息 用在下载时，提供推荐购买提示 (暂未使用，直接取相应字段去判断)
     */
    public HashMap getRecomInfo(String chargeMode, String xml) {
        HashMap hm = new HashMap();

        return hm;
    }

    /**
     * 获取某包月栏目或分册是否已订购
     */
    public boolean isOrdered(String type, String id) {

        if ("cata".equals(type)) {
            String xml = SubscribeProcess.network("getCatalogContent", id, "1",
                    "1", null);
            if (getFirstValue(xml, "isOrdered") != null) {
                return true;
            }
        } else if ("fas".equals(type)) {
            String xml = SubscribeProcess.network("getFascicleList", id, "1",
                    "1", null);
            String isSubscribe = getFirstValue(xml, "isSubscribe");
            if (isSubscribe != null && "1".equals(isSubscribe)) {
                return true;
            }
        }

        return false;
    }
}
