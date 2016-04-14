package com.pvi.ap.reader.activity.pviappframe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import android.content.Context;
import android.content.res.Resources;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.ExpenseProActivity;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.activity.LocalBookActivity;
import com.pvi.ap.reader.activity.MessageCenterActivity;
import com.pvi.ap.reader.activity.MyAnnotationActivity;
import com.pvi.ap.reader.activity.MyBookMarkActivity;
import com.pvi.ap.reader.activity.MyDocumentActivity;
import com.pvi.ap.reader.activity.MyDownloadsActivity;
import com.pvi.ap.reader.activity.MyFavoriteActivity;
import com.pvi.ap.reader.activity.MyFriendListActivity;
import com.pvi.ap.reader.activity.MyImageActivity;
import com.pvi.ap.reader.activity.MyMonthlyPaymentActivity;
import com.pvi.ap.reader.activity.MyMusicActivity;
import com.pvi.ap.reader.activity.MyNoteActivity;
import com.pvi.ap.reader.activity.MySubscribesActivity;
import com.pvi.ap.reader.activity.RecentBookActivity;
import com.pvi.ap.reader.activity.SerialSubscribeActivity;
import com.pvi.ap.reader.activity.UnsubscribeActivity;
import com.pvi.ap.reader.activity.UserInfoActivity;
import com.pvi.ap.reader.activity.WirelessRankActivity;
import com.pvi.ap.reader.activity.WirelessTabActivity;
import com.pvi.ap.reader.data.common.Logger;


/**
 * pvi app frame:UI������
 * @author rd040 ������
 *
 */
public class PviReaderUI {
    private static final String TAG = "PviReaderUI";

    public static HashMap<String, HashMap<String, String>> initActList(Resources mRes) {
        HashMap<String, HashMap<String, String>> actList = new HashMap<String, HashMap<String, String>>();

        HashMap<String, String> tempHM = new HashMap<String, String>();

        // ���涼Ҫ�ĳɴ�д�ģ�

        // ��ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT10000");
        // 1 0 0 0 0
        // ͷ�� һ������ �������� �������� �ļ�����
        tempHM.put("act", "com.pvi.ap.reader.activity.MainpageInsideActivity");
        tempHM.put("startType", "reuse");
        tempHM.put("actName", mRes.getString(R.string.actTitle10000));
        tempHM.put("actLevel", "0");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "0");
        // tempHM.put("childViewBg", "-1"); //-1����δ���õĻ������������ʱ�����Ķ���������ʹ��Ĭ�ϱ����ز�
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // һ������

        // ���������ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT19000");
        tempHM.put("act", "com.pvi.ap.reader.activity.WirelessStoreMainpageActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000"); // ���Ƿ�����ҳ
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �������TABҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11000");
        tempHM.put("act", "com.pvi.ap.reader.activity.WirelessStoreActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT19000"); 
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // �ҵ����
        tempHM.clear();
        tempHM.put("actID", "ACT12000");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("haveTitleBar", "0");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // ��Դ����
        tempHM.clear();
        tempHM.put("actID", "ACT13000");
        tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle13000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // ���˿ռ�
        tempHM.clear();
        tempHM.put("actID", "ACT14000");
        tempHM.put("startType", "reuse");
        tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle14000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("childViewBg", "" + R.drawable.childactivity_bg_3);
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // ϵͳ����
        tempHM.clear();
        tempHM.put("actID", "ACT15000");
        tempHM.put("startType", "reuse");
        tempHM.put("act", "com.pvi.ap.reader.activity.SystemConfigActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle15000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // ����Ӧ��
        tempHM.clear();
        tempHM.put("actID", "ACT16000");
        tempHM.put("startType", "reuse");
        tempHM.put("act", "com.pvi.ap.reader.activity.AllAppActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle16000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        //tempHM.put("childViewBg", "" + R.drawable.childactivity_bg_3);
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
//     // �ҵ���ע
//        tempHM.clear();
//        tempHM.put("actID", "ACT17000");
//        tempHM.put("startType", "reuse");
//        tempHM.put("act", "com.pvi.ap.reader.activity.MyAnnotationActivity");
//        tempHM.put("actName", mRes.getString(R.string.actTitle17000));
//        tempHM.put("actLevel", "1");
//        tempHM.put("haveStatusBar", "1");
//        tempHM.put("haveTitleBar", "1");
//        tempHM.put("returnActID", "ACT10000");
//        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
//        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
//                .clone());
        
     // ��Ȩ����
        tempHM.clear();
        tempHM.put("actID", "ACT18000");
        tempHM.put("startType", "reuse");
        tempHM.put("act", "com.pvi.ap.reader.activity.ShowAgreementActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle18000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // ����ҳ��

        // ������ǵ�

        
          //   ����Ŀ˳���Գ�����Ϊ׼��
        tempHM.clear(); tempHM.put("actID", "ACT11100");
          tempHM.put("act",
          "com.pvi.ap.reader.activity.WirelessStoreActivity");
         //tempHM.put("actTabIndex", "0"); 
          tempHM.put("actTabName",
                  mRes.getString(R.string.actTitle11100));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.WirelessTabActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle11100)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT19000");
         tempHM.put("parentActID", "ACT11000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 
         tempHM.clear(); tempHM.put("actID", "ACT11200");
         tempHM.put("act",
         "com.pvi.ap.reader.activity.WirelessStoreActivity");
         //tempHM.put("actTabIndex", "1"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle11200));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.WirelessTabActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle11200)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT19000");
         tempHM.put("parentActID", "ACT11000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 
         tempHM.clear(); tempHM.put("actID", "ACT11300");
         tempHM.put("act",
         "com.pvi.ap.reader.activity.WirelessStoreActivity");
         //tempHM.put("actTabIndex", "2"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle11300));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.WirelessTabActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle11300)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT19000");
         tempHM.put("parentActID", "ACT11000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 
         tempHM.clear(); tempHM.put("actID", "ACT11400");
         tempHM.put("act",
         "com.pvi.ap.reader.activity.WirelessStoreActivity");
         //tempHM.put("actTabIndex", "3"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle11400));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.WirelessTabActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle11400)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT19000");
         tempHM.put("parentActID", "ACT11000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //  
         tempHM.clear(); tempHM.put("actID", "ACT11500");
         tempHM.put("act",
         "com.pvi.ap.reader.activity.WirelessStoreActivity");
         //tempHM.put("actTabIndex", "4"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle11500));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.WirelessTabActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle11500)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT19000");
         tempHM.put("parentActID", "ACT11000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 
         tempHM.clear(); tempHM.put("actID", "ACT11600");
         tempHM.put("act",
         "com.pvi.ap.reader.activity.WirelessStoreActivity");
         //tempHM.put("actTabIndex", "5"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle11600));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.WirelessTabActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle11600)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT19000");
         tempHM.put("parentActID", "ACT11000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //
         tempHM.clear(); tempHM.put("actID", "ACT11800");
         tempHM.put("act",
         "com.pvi.ap.reader.activity.WirelessStoreActivity");
         //tempHM.put("actTabIndex", "5"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle11800));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.WirelessTabActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle11800)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT19000");
         tempHM.put("parentActID", "ACT11000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         

        // �������ͼ������
        tempHM.clear();
        tempHM.put("actID", "ACT11700");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookSearchActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11700));
        tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        //tempHM.put("returnActID", "ACT19000");
        tempHM.put("parentActID", "ACT19000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        //�ҵ���������////////////////
     // ����Ķ�
        tempHM.clear();
        tempHM.put("actID", "ACT12100");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        //tempHM.put("actTabIndex", "0");
        tempHM.put("actTabName", mRes.getString(R.string.actTitle12100));
        tempHM.put("insideAct", "com.pvi.ap.reader.activity.RecentBookActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12100));
        tempHM.put("actLevel", "2");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT12000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �ҵ�����
        tempHM.clear();
        tempHM.put("actID", "ACT12200");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        //tempHM.put("actTabIndex", "1");
        tempHM.put("actTabName", mRes.getString(R.string.actTitle12200));
        tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyDownloadsActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12200));
        tempHM.put("actLevel", "2");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT12000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �ҵ���ǩ
        tempHM.clear();
        tempHM.put("actID", "ACT12300");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        //tempHM.put("actTabIndex", "2");
        tempHM.put("actTabName", mRes.getString(R.string.actTitle12300));
        tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyBookMarkActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12300));
//        tempHM.put("actLevel", "2");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
//        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT12000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �ҵĶ���
        tempHM.clear();
        tempHM.put("actID", "ACT12400");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        //tempHM.put("actTabIndex", "3");
        tempHM.put("actTabName", mRes.getString(R.string.actTitle12400));
        tempHM.put("insideAct", "com.pvi.ap.reader.activity.MySubscribesActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12400));
        tempHM.put("actLevel", "2");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT12000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �ҵ��ղ�
        tempHM.clear();
        tempHM.put("actID", "ACT12500");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        //tempHM.put("actTabIndex", "4");
        tempHM.put("actTabName", mRes.getString(R.string.actTitle12500));
        tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyFavoriteActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12500));
        tempHM.put("actLevel", "2");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT12000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �ҵİ���
        tempHM.clear();
        tempHM.put("actID", "ACT12600");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        //tempHM.put("actTabIndex", "5");
        tempHM.put("actTabName", mRes.getString(R.string.actTitle12600));
        tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyMonthlyPaymentActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12600));
        tempHM.put("actLevel", "2");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT12000"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // /��Դ���� ��////////////////////////////

        // TAB��Ƕ��activity���ٶ������ã���������תʱ����ָ��������returnActID�����ã����ӹ��� act10000_X
        // x��ʾָ��tabindex


         //�ҵ���ע 
        tempHM.clear(); tempHM.put("actID", "ACT13600");
         tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
         //tempHM.put("actTabIndex", "5"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle13600));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyAnnotationActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle13600)); 
        // tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         //tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT13000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // ������� ��Ŀҳ 
         tempHM.clear(); tempHM.put("actID", "ACT13500");
         tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
         //tempHM.put("actTabIndex", "4"); 
         tempHM.put("actTabName",  mRes.getString(R.string.actTitle13500)); 
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.LocalBookActivity");
         tempHM.put("actName",  mRes.getString(R.string.actTitle13500)); 
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); 
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT13000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // �ҵ��ĵ� ��Ŀҳ
         tempHM.clear(); tempHM.put("actID", "ACT13100");
         tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
         //tempHM.put("actTabIndex", "0"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle13100));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyDocumentActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle13100)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT13000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // �ҵ����� ��Ŀҳ
         tempHM.clear(); tempHM.put("actID", "ACT13200");
         tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
         //tempHM.put("actTabIndex", "1"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle13200));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyMusicActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle13200)); 
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT13000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // �ҵ�ͼƬ ��Ŀҳ 
         tempHM.clear(); tempHM.put("actID", "ACT13300");
         tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
         //tempHM.put("actTabIndex", "2"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle13300)); 
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyImageActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle13300)); tempHM.put("actLevel", "2");
         tempHM.put("haveBottomBar", "1");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT13000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // |---�鿴ͼƬ 
         tempHM.clear(); tempHM.put("actID", "ACT13310");
         tempHM.put("act", "com.pvi.ap.reader.activity.ShowPicActivity");
         tempHM.put("actName","�鿴ͼƬ"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("haveBottomBar", "0");
         tempHM.put("returnActID", "ACT13300");
         tempHM.put("parentActID", "ACT13300"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //     |----ȫ���鿴ͼƬ 
         tempHM.clear(); tempHM.put("actID", "ACT13311");
         tempHM.put("act", "com.pvi.ap.reader.activity.AllPicActivity");
         tempHM.put("actName","ȫ���鿴ͼƬ"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "0"); tempHM.put("haveTitleBar", "0");
         tempHM.put("returnActID", "ACT13310");
         tempHM.put("parentActID", "ACT13310"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // �ҵı�� ��Ŀҳ
         tempHM.clear(); tempHM.put("actID", "ACT13400");
         tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
         //tempHM.put("actTabIndex", "3"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle13400));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyNoteActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle13400)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT13000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());

      // �ҵı�� ����ҳ
         tempHM.clear(); tempHM.put("actID", "ACT13410");
         tempHM.put("act", "com.pvi.ap.reader.activity.ShowNoteTextActivity");
         tempHM.put("actName",
         "�������"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT13400");
         tempHM.put("parentActID", "ACT13400"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
        // ���˿ռ��


         // ������Ϣ TAB�ӽ��� 
         tempHM.clear(); tempHM.put("actID", "ACT14100");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.UserInfoActivity"); //������Կ��������֪������һ����Ƕ�뵽TAB�����act
         //tempHM.put("actTabIndex", "0"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14100));
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14100)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT14000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //������Ϣ�޸Ľ���
         tempHM.clear(); tempHM.put("actID", "ACT14110");
         tempHM.put("act", "com.pvi.ap.reader.activity.ModifyUserInfoActivity");
         tempHM.put("actName","������Ϣ�޸�"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14100");
         tempHM.put("parentActID", "ACT14100"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // �����б� TAB�ӽ��� 
         tempHM.clear(); tempHM.put("actID", "ACT14200");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "1"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14200));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyFriendListActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14200)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         
         
         
         // ���Ѽ�¼ TAB�ӽ���
         tempHM.clear(); tempHM.put("actID", "ACT14300");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "2"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14300));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.ExpenseProActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14300)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         
       //���Ѽ�¼ �ӽ���
         tempHM.clear(); tempHM.put("actID", "ACT14310");
         tempHM.put("act", "com.pvi.ap.reader.activity.ExpenseInfoActivity");
         tempHM.put("actName","������ϸ��Ϣ"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14300");
         tempHM.put("parentActID", "ACT14300"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
        //��ȯ�б��ӽ���
         tempHM.clear(); tempHM.put("actID", "ACT14320");
         tempHM.put("act", "com.pvi.ap.reader.activity.TicketListActivity");
         tempHM.put("actName","��ȯ�б�"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14300");
         tempHM.put("parentActID", "ACT14300"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
     
         // �����˶� TAB�ӽ��� 
         tempHM.clear(); tempHM.put("actID", "ACT14400");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "3"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14400));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.UnsubscribeActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14400)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // ����Ԥ�� TAB�ӽ���
         tempHM.clear(); tempHM.put("actID", "ACT14500");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "4"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14500));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.SerialSubscribeActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14500)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // ��Ϣ���� TAB�ӽ���
         tempHM.clear(); tempHM.put("actID", "ACT14600");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "5");
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14600));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MessageCenterActivity"); //������Կ��������֪������һ����Ƕ�뵽TAB�����act
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14600)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
       //���Ѽ�¼ �ӽ���
         tempHM.clear(); tempHM.put("actID", "ACT14610");
         tempHM.put("act", "com.pvi.ap.reader.activity.PresentListActivity");
         tempHM.put("actName","���ͼ�¼"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14600");
         tempHM.put("parentActID", "ACT14600"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //ϵͳ���õ�//////////////////////
         //����ʱ��
         tempHM.clear();
         tempHM.put("actID", "ACT15100");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.TimeSetActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15100));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //��������
         tempHM.clear();
         tempHM.put("actID", "ACT15200");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.StartUpActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15200));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //��������
         tempHM.clear();
         tempHM.put("actID", "ACT15300");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.StandbyActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15300));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //��ӭҳ����
         tempHM.clear();
         tempHM.put("actID", "ACT15400");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.WelcomeSetActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15400));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //����Ԥ������
         tempHM.clear();
         tempHM.put("actID", "ACT15500");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.SubscribeRemindActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15500));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //��������
         tempHM.clear();
         tempHM.put("actID", "ACT15600");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.NetSetActivity");
         tempHM.put("actName", "��������");
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //�豸��Ϣ
         tempHM.clear();
         tempHM.put("actID", "ACT15700");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.ConfigHandsetInfoActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15700));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //Ƥ������
         tempHM.clear();
         tempHM.put("actID", "ACT15800");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.StorageStatActivity");
         tempHM.put("actName", "�洢��Ϣ");
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //�ָ���������
         tempHM.clear();
         tempHM.put("actID", "ACT15900");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.RecoveryFactoryActivity");
         tempHM.put("actName", "�ָ���������");
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
//     1    //������ʽ����
//         tempHM.clear();
//         tempHM.put("actID", "ACT15900");
//         tempHM.put("startType", "reuse");
//         tempHM.put("act", "com.pvi.ap.reader.activity.RecoveryFactoryActivity");
//         tempHM.put("actName", "�ָ���������");
//         tempHM.put("actLevel", "2");
//         tempHM.put("haveStatusBar", "1");
//         tempHM.put("haveTitleBar", "1");
//         tempHM.put("returnActID", "ACT15000");
//         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
//         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
//                 .clone());
//         //�洢�ռ�����
//         tempHM.clear();
//         tempHM.put("actID", "ACT15900");
//         tempHM.put("startType", "reuse");
//         tempHM.put("act", "com.pvi.ap.reader.activity.RecoveryFactoryActivity");
//         tempHM.put("actName", "�ָ���������");
//         tempHM.put("actLevel", "2");
//         tempHM.put("haveStatusBar", "1");
//         tempHM.put("haveTitleBar", "1");
//         tempHM.put("returnActID", "ACT15000");
//         tempHM.put("parentActID", "ACT15000"); // ��Ŀ�����ӹ�ϵ
//         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
//                 .clone());
        // ����ҳ�� (����ҳ)

        // �鼮�Ķ���
        // �鼮ժҪҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11110");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookSummaryActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11110));
        //tempHM.put("actLevel", "4");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11301"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �鼮����
        tempHM.clear();
        tempHM.put("actID", "ACT11111");
        tempHM.put("act", "com.pvi.ap.reader.activity.CommentsListActivity");
        tempHM.put("actName", "�鼮����");
        tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11110");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // �鼮Ŀ¼ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11120");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookCatalogActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11120));
        //tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
     // �鼮 �ֲ��б����
        tempHM.clear();
        tempHM.put("actID", "ACT111a0");
        tempHM.put("act", "com.pvi.ap.reader.activity.FascicleListActivity");
        tempHM.put("actName", "�ֲ��б�");
        //tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        

        // ���߽���ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11130");
        tempHM.put("act", "com.pvi.ap.reader.activity.WriterActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11130));
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT11300");
        tempHM.put("parentActID", "ACT11300");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // ��Ѷ����ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11210");
        tempHM.put("act", "com.pvi.ap.reader.activity.InfoContentActivity");
        tempHM.put("actName", "��Ѷ����");
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11200");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // ��Ŀ��ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11301");
        tempHM.put("act", "com.pvi.ap.reader.activity.CatalogHomepageActivity");
        tempHM.put("actName", "��Ŀ��ҳ");
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT11600"); 
        tempHM.put("parentActID", "ACT11600"); 
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �������ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11302");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookPackageInfoActivity");
        tempHM.put("actName", "�������");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11000"); 
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        //���а�
        tempHM.clear();
        tempHM.put("actID", "ACT11201");
        tempHM.put("act", "com.pvi.ap.reader.activity.RankingActivity");
        tempHM.put("actName", "���а�");
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT11200"); 
        tempHM.put("parentActID", "ACT11200"); 
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // �������ֿ�
        tempHM.clear(); tempHM.put("actID", "ACT13210");
        tempHM.put("act", "com.pvi.ap.reader.activity.BackgroundMusicActivity");
        tempHM.put("actName","�������ֿ�"); 
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1"); 
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT13200");
        tempHM.put("parentActID", "ACT13200"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
        .clone());
        

        // �Ķ����Ľ���

        // meb
        tempHM.clear();
        tempHM.put("actID", "ACT11140");
        tempHM.put("act", "com.pvi.ap.reader.activity.MebViewFileActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11140));
        //tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("returnActID", "ACT13100");

        // level��returnaid��δ���õģ�back����һ��ͼ�� TabActvity����tab�л�ʱ���֪ͨactstack�仯�أ�

        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // meb����
        tempHM.clear();
        tempHM.put("actID", "ACT11150");
        tempHM.put("act", "com.pvi.ap.reader.activity.MebPicActivity");
        // tempHM.put("actName", mRes.getString(R.string.actTitle11140));
        //tempHM.put("actLevel", "4");
        tempHM.put("haveStatusBar", "0");
        tempHM.put("haveTitleBar", "0");
        tempHM.put("childViewBg", "0");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // meb Ŀ¼ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT11180");
        tempHM.put("act", "com.pvi.ap.reader.activity.ListFileActivity");
        tempHM.put("actName", "MEBĿ¼ҳ");
        //tempHM.put("actLevel", "4");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // txt�Ķ�����
        tempHM.clear();
        tempHM.put("actID", "ACT11160");
        tempHM.put("act", "com.pvi.ap.reader.activity.TxtReaderActivity");
        tempHM.put("actName", "TXT�Ķ�");
       // tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("returnActID", "ACT13100");//�̶������ҵ��ĵ�
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // pdf�Ķ�����
        tempHM.clear();
        tempHM.put("actID", "ACT11170");
        tempHM.put("act", "com.pvi.ap.reader.activity.PDFReadActivity");
        tempHM.put("actName", "PDF�Ķ�");
       // tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("returnActID", "ACT13100");//�̶������ҵ��ĵ�
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
     //�����Ķ� ����
        tempHM.clear();
        tempHM.put("actID", "ACT11190");
        tempHM.put("act", "com.pvi.ap.reader.activity.ReadingOnlineActivity");
        tempHM.put("actName", "�����Ķ�");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // ������� �б�ҳ
        tempHM.clear();
        tempHM.put("actID", "ACT13510");
        tempHM.put("act", "com.pvi.ap.reader.activity.LocalBookListActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle13510));
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT13500");
        tempHM.put("parentActID", "ACT13500"); // ��Ŀ�����ӹ�ϵ
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // ϵͳ���õ�

        // �ļ�ҳ��

        // ����ҳ�� actLevel = 5
        tempHM.clear();
        tempHM.put("actID", "ACT20010");
        tempHM.put("act", "com.pvi.ap.reader.activity.ScreenSaveActivity");
        tempHM.put("startType", "reuse");
        tempHM.put("actName", mRes.getString(R.string.actTitle13510));
        tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "0");
        tempHM.put("haveTitleBar", "0");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // ���õ� �鼮�б�
        tempHM.clear();
        tempHM.put("actID", "ACT20011");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookListActivity");
        tempHM.put("actName", "�鼮�б�");
        tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        return actList;
    }
    
    /**
     * ÿ������������Ƕ���Ҫ���½���һ�����TAB
     * @param blockName
     * @return
     */
    public static HashMap<String, String[]> buildBlockNameList(Context context) {
        HashMap<String, String[]> blockNameList = new HashMap<String, String[]>(); 
        
        String[] blockNameTemp = new String[6];
        
        //�ҵ����
        blockNameTemp[0]="����Ķ�";
        blockNameTemp[1]="�ҵ�����";
        blockNameTemp[2]="�ҵ���ǩ";
        blockNameTemp[3]="�ҵĶ���";
        blockNameTemp[4]="�ҵ��ղ�";
        blockNameTemp[5]="�ҵİ���";
        blockNameList.put("com.pvi.ap.reader.activity.MyBookshelfActivity",blockNameTemp.clone());
        
        //��Դ����
        blockNameTemp[0]="�ҵ��ĵ�";
        blockNameTemp[1]="�ҵ�����";
        blockNameTemp[2]="�ҵ�ͼƬ";
        blockNameTemp[3]="�ҵı��";
        blockNameTemp[4]="�������";
        blockNameTemp[5]="�ҵ���ע";
        blockNameList.put("com.pvi.ap.reader.activity.ResCenterActivity",blockNameTemp.clone());
        
        //���˿ռ�
        blockNameTemp[0]="�û���Ϣ";
        blockNameTemp[1]="���ѹ���";
        blockNameTemp[2]="���Ѽ�¼";
        blockNameTemp[3]="�����˶�";
        blockNameTemp[4]="����Ԥ��";
        blockNameTemp[5]="��Ϣ����";
        blockNameList.put("com.pvi.ap.reader.activity.UserCenterActivity",blockNameTemp.clone());
        
        //������� ���ԴӴ��������ã�Ĭ�ϲ���
        blockNameTemp[0]="������Ŀ";
        blockNameTemp[1]="�༭�Ƽ�";
        blockNameTemp[2]="��������";
        blockNameTemp[3]="��������";
        blockNameTemp[4]="��Ʒר��";
        blockNameTemp[5]="�������";
        blockNameList.put("com.pvi.ap.reader.activity.WirelessStoreActivity",blockNameTemp.clone());
        
        final GlobalVar app = ((GlobalVar) context.getApplicationContext());
        final ArrayList<HashMap<String, String>> blockInfo = app.getBlockInfo();
        if (blockInfo != null && !blockInfo.isEmpty()) {
            final int countBlock = blockInfo.size();
            if (countBlock == 6) {
                for (int i = 0; i < countBlock; i++) {
                    final HashMap<String, String> hm = blockInfo.get(i);
                    if (hm != null) {
                        final String blockname = hm.get("blockName");
                        if (blockname != null) {
                            blockNameTemp[i] = blockname;
                            //Logger.e("add a blockname ,name:",blockname);
                        }
                    }
                }
                
                blockNameList.put(
                        "com.pvi.ap.reader.activity.WirelessStoreActivity",
                        blockNameTemp.clone());
            }
        }

        return blockNameList;
    }
    
    public static HashMap<String, Class> getClasses(){
        HashMap<String, Class> classes = new HashMap<String, Class>();
        classes.put("����Ķ�", RecentBookActivity.class);
        classes.put("�ҵ�����", MyDownloadsActivity.class);
        classes.put("�ҵ���ǩ", MyBookMarkActivity.class);
        classes.put("�ҵĶ���", MySubscribesActivity.class);
        classes.put("�ҵ��ղ�", MyFavoriteActivity.class);
        classes.put("�ҵİ���", MyMonthlyPaymentActivity.class);
        
        classes.put("�ҵ��ĵ�", MyDocumentActivity.class);
        classes.put("�ҵ�����", MyMusicActivity.class);
        classes.put("�ҵ�ͼƬ", MyImageActivity.class);
        classes.put("�ҵı��", MyNoteActivity.class);
        classes.put("�������", LocalBookActivity.class);
        classes.put("�ҵ���ע", MyAnnotationActivity.class);
        
        classes.put("�û���Ϣ", UserInfoActivity.class);
        classes.put("���ѹ���", MyFriendListActivity.class);
        classes.put("���Ѽ�¼", ExpenseProActivity.class);
        classes.put("�����˶�", UnsubscribeActivity.class);
        classes.put("����Ԥ��", SerialSubscribeActivity.class);
        classes.put("��Ϣ����", MessageCenterActivity.class);
        
        classes.put("������Ŀ", WirelessTabActivity.class);
        classes.put("�༭�Ƽ�", WirelessTabActivity.class);
        classes.put("��������", WirelessRankActivity.class);
        classes.put("��������", WirelessTabActivity.class);
        classes.put("��Ʒר��", WirelessTabActivity.class);
        classes.put("�������", WirelessTabActivity.class);
        classes.put("������Ѷ", WirelessTabActivity.class);

        
        return classes;
        
    }
    
    public static String findActByTabname(String actTabName,HashMap<String,String[]> blockNameList){
        //Logger.d(TAG,"actTabName:"+actTabName+",blockNameList:"+blockNameList);
        
    	String act = null;
    	Set<String> ks = blockNameList.keySet();
        Iterator iter = blockNameList.entrySet().iterator();
        while (iter.hasNext()) {
        	Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            String[] blockNames= (String[])val;
            for(String blockName:blockNames){
            	if(blockName.equals(actTabName)){
            		act = (String)key;
            		return act;
            	}
            }
        }
        return act;
    }
    
    public static ArrayList<String> getIgnorActList(){
        ArrayList<String> ignorActList = new ArrayList<String>();
        ignorActList.add("com.pvi.ap.reader.activity.SubscribeProcess");
        return ignorActList;
    }
    
    public static int[] tabBgs = {R.drawable.tab_bgimg_0_ui1,
        R.drawable.tab_bgimg_1_ui1,
        R.drawable.tab_bgimg_2_ui1,
        R.drawable.tab_bgimg_3_ui1,
        R.drawable.tab_bgimg_4_ui1,
        R.drawable.tab_bgimg_5_ui1};

}
