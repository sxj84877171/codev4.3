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
 * pvi app frame:UI配置类
 * @author rd040 马中庆
 *
 */
public class PviReaderUI {
    private static final String TAG = "PviReaderUI";

    public static HashMap<String, HashMap<String, String>> initActList(Resources mRes) {
        HashMap<String, HashMap<String, String>> actList = new HashMap<String, HashMap<String, String>>();

        HashMap<String, String> tempHM = new HashMap<String, String>();

        // 后面都要改成大写的！

        // 首页
        tempHM.clear();
        tempHM.put("actID", "ACT10000");
        // 1 0 0 0 0
        // 头部 一级编码 二级编码 三级编码 四级编码
        tempHM.put("act", "com.pvi.ap.reader.activity.MainpageInsideActivity");
        tempHM.put("startType", "reuse");
        tempHM.put("actName", mRes.getString(R.string.actTitle10000));
        tempHM.put("actLevel", "0");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "0");
        // tempHM.put("childViewBg", "-1"); //-1或者未设置的话，框架载入它时，不改动背景，即使用默认背景素材
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 一级界面

        // 无线书城首页
        tempHM.clear();
        tempHM.put("actID", "ACT19000");
        tempHM.put("act", "com.pvi.ap.reader.activity.WirelessStoreMainpageActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000"); // 总是返回首页
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 无线书城TAB页
        tempHM.clear();
        tempHM.put("actID", "ACT11000");
        tempHM.put("act", "com.pvi.ap.reader.activity.WirelessStoreActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT19000"); 
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 我的书架
        tempHM.clear();
        tempHM.put("actID", "ACT12000");
        tempHM.put("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle12000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("haveTitleBar", "0");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 资源中心
        tempHM.clear();
        tempHM.put("actID", "ACT13000");
        tempHM.put("act", "com.pvi.ap.reader.activity.ResCenterActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle13000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 个人空间
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
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 系统设置
        tempHM.clear();
        tempHM.put("actID", "ACT15000");
        tempHM.put("startType", "reuse");
        tempHM.put("act", "com.pvi.ap.reader.activity.SystemConfigActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle15000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 所有应用
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
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
//     // 我的批注
//        tempHM.clear();
//        tempHM.put("actID", "ACT17000");
//        tempHM.put("startType", "reuse");
//        tempHM.put("act", "com.pvi.ap.reader.activity.MyAnnotationActivity");
//        tempHM.put("actName", mRes.getString(R.string.actTitle17000));
//        tempHM.put("actLevel", "1");
//        tempHM.put("haveStatusBar", "1");
//        tempHM.put("haveTitleBar", "1");
//        tempHM.put("returnActID", "ACT10000");
//        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
//        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
//                .clone());
        
     // 版权声明
        tempHM.clear();
        tempHM.put("actID", "ACT18000");
        tempHM.put("startType", "reuse");
        tempHM.put("act", "com.pvi.ap.reader.activity.ShowAgreementActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle18000));
        tempHM.put("actLevel", "1");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT10000");
        tempHM.put("parentActID", "ACT10000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 二级页面

        // 无线书城的

        
          //   （栏目顺序以程序中为准）
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
         tempHM.put("parentActID", "ACT11000"); // 栏目树父子关系
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
         tempHM.put("parentActID", "ACT11000"); // 栏目树父子关系
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
         tempHM.put("parentActID", "ACT11000"); // 栏目树父子关系
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
         tempHM.put("parentActID", "ACT11000"); // 栏目树父子关系
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
         tempHM.put("parentActID", "ACT11000"); // 栏目树父子关系
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
         tempHM.put("parentActID", "ACT11000"); // 栏目树父子关系
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
         tempHM.put("parentActID", "ACT11000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         

        // 无线书城图书搜索
        tempHM.clear();
        tempHM.put("actID", "ACT11700");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookSearchActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11700));
        tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        //tempHM.put("returnActID", "ACT19000");
        tempHM.put("parentActID", "ACT19000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        //我的书架里面的////////////////
     // 最近阅读
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
        tempHM.put("parentActID", "ACT12000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 我的下载
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
        tempHM.put("parentActID", "ACT12000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 我的书签
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
        tempHM.put("parentActID", "ACT12000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 我的订购
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
        tempHM.put("parentActID", "ACT12000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 我的收藏
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
        tempHM.put("parentActID", "ACT12000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 我的包月
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
        tempHM.put("parentActID", "ACT12000"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // /资源中心 的////////////////////////////

        // TAB内嵌的activity不再独立配置，必需在跳转时自行指定；对于returnActID的设置，增加规则 act10000_X
        // x表示指定tabindex


         //我的批注 
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
         tempHM.put("parentActID", "ACT13000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 本地书库 栏目页 
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
         tempHM.put("parentActID", "ACT13000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 我的文档 栏目页
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
         tempHM.put("parentActID", "ACT13000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 我的音乐 栏目页
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
         tempHM.put("parentActID", "ACT13000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 我的图片 栏目页 
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
         tempHM.put("parentActID", "ACT13000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // |---查看图片 
         tempHM.clear(); tempHM.put("actID", "ACT13310");
         tempHM.put("act", "com.pvi.ap.reader.activity.ShowPicActivity");
         tempHM.put("actName","查看图片"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("haveBottomBar", "0");
         tempHM.put("returnActID", "ACT13300");
         tempHM.put("parentActID", "ACT13300"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //     |----全屏查看图片 
         tempHM.clear(); tempHM.put("actID", "ACT13311");
         tempHM.put("act", "com.pvi.ap.reader.activity.AllPicActivity");
         tempHM.put("actName","全屏查看图片"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "0"); tempHM.put("haveTitleBar", "0");
         tempHM.put("returnActID", "ACT13310");
         tempHM.put("parentActID", "ACT13310"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 我的便笺 栏目页
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
         tempHM.put("parentActID", "ACT13000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());

      // 我的便笺 内容页
         tempHM.clear(); tempHM.put("actID", "ACT13410");
         tempHM.put("act", "com.pvi.ap.reader.activity.ShowNoteTextActivity");
         tempHM.put("actName",
         "便笺内容"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT13400");
         tempHM.put("parentActID", "ACT13400"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
        // 个人空间的


         // 个人信息 TAB子界面 
         tempHM.clear(); tempHM.put("actID", "ACT14100");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.UserInfoActivity"); //这个属性可以清楚的知道这是一个被嵌入到TAB里面的act
         //tempHM.put("actTabIndex", "0"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14100));
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14100)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT10000");
         tempHM.put("parentActID", "ACT14000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //个人信息修改界面
         tempHM.clear(); tempHM.put("actID", "ACT14110");
         tempHM.put("act", "com.pvi.ap.reader.activity.ModifyUserInfoActivity");
         tempHM.put("actName","个人信息修改"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14100");
         tempHM.put("parentActID", "ACT14100"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 好友列表 TAB子界面 
         tempHM.clear(); tempHM.put("actID", "ACT14200");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "1"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14200));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MyFriendListActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14200)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         
         
         
         // 消费记录 TAB子界面
         tempHM.clear(); tempHM.put("actID", "ACT14300");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "2"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14300));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.ExpenseProActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14300)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         
       //消费记录 子界面
         tempHM.clear(); tempHM.put("actID", "ACT14310");
         tempHM.put("act", "com.pvi.ap.reader.activity.ExpenseInfoActivity");
         tempHM.put("actName","消费详细信息"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14300");
         tempHM.put("parentActID", "ACT14300"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
        //书券列表子界面
         tempHM.clear(); tempHM.put("actID", "ACT14320");
         tempHM.put("act", "com.pvi.ap.reader.activity.TicketListActivity");
         tempHM.put("actName","书券列表"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14300");
         tempHM.put("parentActID", "ACT14300"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
     
         // 包月退订 TAB子界面 
         tempHM.clear(); tempHM.put("actID", "ACT14400");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "3"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14400));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.UnsubscribeActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14400)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 连载预定 TAB子界面
         tempHM.clear(); tempHM.put("actID", "ACT14500");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "4"); 
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14500));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.SerialSubscribeActivity");
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14500)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         // 消息中心 TAB子界面
         tempHM.clear(); tempHM.put("actID", "ACT14600");
         tempHM.put("act", "com.pvi.ap.reader.activity.UserCenterActivity");
         //tempHM.put("actTabIndex", "5");
         tempHM.put("actTabName",
                 mRes.getString(R.string.actTitle14600));
         tempHM.put("insideAct", "com.pvi.ap.reader.activity.MessageCenterActivity"); //这个属性可以清楚的知道这是一个被嵌入到TAB里面的act
         tempHM.put("actName",
         mRes.getString(R.string.actTitle14600)); tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("parentActID", "ACT14000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
       //消费记录 子界面
         tempHM.clear(); tempHM.put("actID", "ACT14610");
         tempHM.put("act", "com.pvi.ap.reader.activity.PresentListActivity");
         tempHM.put("actName","赠送记录"); tempHM.put("actLevel", "3");
         tempHM.put("haveStatusBar", "1"); tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT14600");
         tempHM.put("parentActID", "ACT14600"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
         .clone());
         
         //系统设置的//////////////////////
         //日期时间
         tempHM.clear();
         tempHM.put("actID", "ACT15100");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.TimeSetActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15100));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //开机设置
         tempHM.clear();
         tempHM.put("actID", "ACT15200");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.StartUpActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15200));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //待机画面
         tempHM.clear();
         tempHM.put("actID", "ACT15300");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.StandbyActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15300));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //欢迎页设置
         tempHM.clear();
         tempHM.put("actID", "ACT15400");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.WelcomeSetActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15400));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //提醒预定设置
         tempHM.clear();
         tempHM.put("actID", "ACT15500");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.SubscribeRemindActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15500));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //语言设置
         tempHM.clear();
         tempHM.put("actID", "ACT15600");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.NetSetActivity");
         tempHM.put("actName", "联网设置");
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //设备信息
         tempHM.clear();
         tempHM.put("actID", "ACT15700");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.ConfigHandsetInfoActivity");
         tempHM.put("actName", mRes.getString(R.string.actTitle15700));
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //皮肤设置
         tempHM.clear();
         tempHM.put("actID", "ACT15800");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.StorageStatActivity");
         tempHM.put("actName", "存储信息");
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
         
       //恢复出厂设置
         tempHM.clear();
         tempHM.put("actID", "ACT15900");
         tempHM.put("startType", "reuse");
         tempHM.put("act", "com.pvi.ap.reader.activity.RecoveryFactoryActivity");
         tempHM.put("actName", "恢复出厂设置");
         tempHM.put("actLevel", "2");
         tempHM.put("haveStatusBar", "1");
         tempHM.put("haveTitleBar", "1");
         tempHM.put("returnActID", "ACT15000");
         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                 .clone());
//     1    //联网方式设置
//         tempHM.clear();
//         tempHM.put("actID", "ACT15900");
//         tempHM.put("startType", "reuse");
//         tempHM.put("act", "com.pvi.ap.reader.activity.RecoveryFactoryActivity");
//         tempHM.put("actName", "恢复出厂设置");
//         tempHM.put("actLevel", "2");
//         tempHM.put("haveStatusBar", "1");
//         tempHM.put("haveTitleBar", "1");
//         tempHM.put("returnActID", "ACT15000");
//         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
//         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
//                 .clone());
//         //存储空间设置
//         tempHM.clear();
//         tempHM.put("actID", "ACT15900");
//         tempHM.put("startType", "reuse");
//         tempHM.put("act", "com.pvi.ap.reader.activity.RecoveryFactoryActivity");
//         tempHM.put("actName", "恢复出厂设置");
//         tempHM.put("actLevel", "2");
//         tempHM.put("haveStatusBar", "1");
//         tempHM.put("haveTitleBar", "1");
//         tempHM.put("returnActID", "ACT15000");
//         tempHM.put("parentActID", "ACT15000"); // 栏目树父子关系
//         actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
//                 .clone());
        // 三级页面 (内容页)

        // 书籍阅读的
        // 书籍摘要页
        tempHM.clear();
        tempHM.put("actID", "ACT11110");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookSummaryActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11110));
        //tempHM.put("actLevel", "4");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11301"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 书籍评论
        tempHM.clear();
        tempHM.put("actID", "ACT11111");
        tempHM.put("act", "com.pvi.ap.reader.activity.CommentsListActivity");
        tempHM.put("actName", "书籍评论");
        tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11110");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 书籍目录页
        tempHM.clear();
        tempHM.put("actID", "ACT11120");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookCatalogActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11120));
        //tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
     // 书籍 分册列表界面
        tempHM.clear();
        tempHM.put("actID", "ACT111a0");
        tempHM.put("act", "com.pvi.ap.reader.activity.FascicleListActivity");
        tempHM.put("actName", "分册列表");
        //tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        

        // 作者介绍页
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
        
        // 资讯内容页
        tempHM.clear();
        tempHM.put("actID", "ACT11210");
        tempHM.put("act", "com.pvi.ap.reader.activity.InfoContentActivity");
        tempHM.put("actName", "资讯内容");
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11200");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 栏目首页
        tempHM.clear();
        tempHM.put("actID", "ACT11301");
        tempHM.put("act", "com.pvi.ap.reader.activity.CatalogHomepageActivity");
        tempHM.put("actName", "栏目首页");
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT11600"); 
        tempHM.put("parentActID", "ACT11600"); 
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 书包介绍页
        tempHM.clear();
        tempHM.put("actID", "ACT11302");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookPackageInfoActivity");
        tempHM.put("actName", "书包介绍");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("parentActID", "ACT11000"); 
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        //排行榜
        tempHM.clear();
        tempHM.put("actID", "ACT11201");
        tempHM.put("act", "com.pvi.ap.reader.activity.RankingActivity");
        tempHM.put("actName", "排行榜");
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT11200"); 
        tempHM.put("parentActID", "ACT11200"); 
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        // 背景音乐库
        tempHM.clear(); tempHM.put("actID", "ACT13210");
        tempHM.put("act", "com.pvi.ap.reader.activity.BackgroundMusicActivity");
        tempHM.put("actName","背景音乐库"); 
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1"); 
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT13200");
        tempHM.put("parentActID", "ACT13200"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
        .clone());
        

        // 阅读器的界面

        // meb
        tempHM.clear();
        tempHM.put("actID", "ACT11140");
        tempHM.put("act", "com.pvi.ap.reader.activity.MebViewFileActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle11140));
        //tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("returnActID", "ACT13100");

        // level和returnaid都未设置的，back到上一视图？ TabActvity里面tab切换时如何通知actstack变化呢？

        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // meb封面
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
        
        // meb 目录页
        tempHM.clear();
        tempHM.put("actID", "ACT11180");
        tempHM.put("act", "com.pvi.ap.reader.activity.ListFileActivity");
        tempHM.put("actName", "MEB目录页");
        //tempHM.put("actLevel", "4");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // txt阅读界面
        tempHM.clear();
        tempHM.put("actID", "ACT11160");
        tempHM.put("act", "com.pvi.ap.reader.activity.TxtReaderActivity");
        tempHM.put("actName", "TXT阅读");
       // tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("returnActID", "ACT13100");//固定返回我的文档
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // pdf阅读界面
        tempHM.clear();
        tempHM.put("actID", "ACT11170");
        tempHM.put("act", "com.pvi.ap.reader.activity.PDFReadActivity");
        tempHM.put("actName", "PDF阅读");
       // tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        // tempHM.put("returnActID", "ACT13100");//固定返回我的文档
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
     //在线阅读 界面
        tempHM.clear();
        tempHM.put("actID", "ACT11190");
        tempHM.put("act", "com.pvi.ap.reader.activity.ReadingOnlineActivity");
        tempHM.put("actName", "在线阅读");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 本地书库 列表页
        tempHM.clear();
        tempHM.put("actID", "ACT13510");
        tempHM.put("act", "com.pvi.ap.reader.activity.LocalBookListActivity");
        tempHM.put("actName", mRes.getString(R.string.actTitle13510));
        tempHM.put("actLevel", "3");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        tempHM.put("returnActID", "ACT13500");
        tempHM.put("parentActID", "ACT13500"); // 栏目树父子关系
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());

        // 系统设置的

        // 四级页面

        // 独立页面 actLevel = 5
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
        
        // 共用的 书籍列表
        tempHM.clear();
        tempHM.put("actID", "ACT20011");
        tempHM.put("act", "com.pvi.ap.reader.activity.BookListActivity");
        tempHM.put("actName", "书籍列表");
        tempHM.put("actLevel", "5");
        tempHM.put("haveStatusBar", "1");
        tempHM.put("haveTitleBar", "1");
        actList.put(tempHM.get("actID"), (HashMap<String, String>) tempHM
                .clone());
        
        return actList;
    }
    
    /**
     * 每次连接无线书城都需要重新建立一下这个TAB
     * @param blockName
     * @return
     */
    public static HashMap<String, String[]> buildBlockNameList(Context context) {
        HashMap<String, String[]> blockNameList = new HashMap<String, String[]>(); 
        
        String[] blockNameTemp = new String[6];
        
        //我的书架
        blockNameTemp[0]="最近阅读";
        blockNameTemp[1]="我的下载";
        blockNameTemp[2]="我的书签";
        blockNameTemp[3]="我的订购";
        blockNameTemp[4]="我的收藏";
        blockNameTemp[5]="我的包月";
        blockNameList.put("com.pvi.ap.reader.activity.MyBookshelfActivity",blockNameTemp.clone());
        
        //资源中心
        blockNameTemp[0]="我的文档";
        blockNameTemp[1]="我的音乐";
        blockNameTemp[2]="我的图片";
        blockNameTemp[3]="我的便笺";
        blockNameTemp[4]="本地书库";
        blockNameTemp[5]="我的批注";
        blockNameList.put("com.pvi.ap.reader.activity.ResCenterActivity",blockNameTemp.clone());
        
        //个人空间
        blockNameTemp[0]="用户信息";
        blockNameTemp[1]="好友管理";
        blockNameTemp[2]="消费记录";
        blockNameTemp[3]="包月退订";
        blockNameTemp[4]="连载预订";
        blockNameTemp[5]="消息中心";
        blockNameList.put("com.pvi.ap.reader.activity.UserCenterActivity",blockNameTemp.clone());
        
        //无线书城 尝试从传入参数获得，默认参数
        blockNameTemp[0]="分类栏目";
        blockNameTemp[1]="编辑推荐";
        blockNameTemp[2]="热门排行";
        blockNameTemp[3]="名家名作";
        blockNameTemp[4]="精品专栏";
        blockNameTemp[5]="包月书包";
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
        classes.put("最近阅读", RecentBookActivity.class);
        classes.put("我的下载", MyDownloadsActivity.class);
        classes.put("我的书签", MyBookMarkActivity.class);
        classes.put("我的订购", MySubscribesActivity.class);
        classes.put("我的收藏", MyFavoriteActivity.class);
        classes.put("我的包月", MyMonthlyPaymentActivity.class);
        
        classes.put("我的文档", MyDocumentActivity.class);
        classes.put("我的音乐", MyMusicActivity.class);
        classes.put("我的图片", MyImageActivity.class);
        classes.put("我的便笺", MyNoteActivity.class);
        classes.put("本地书库", LocalBookActivity.class);
        classes.put("我的批注", MyAnnotationActivity.class);
        
        classes.put("用户信息", UserInfoActivity.class);
        classes.put("好友管理", MyFriendListActivity.class);
        classes.put("消费记录", ExpenseProActivity.class);
        classes.put("包月退订", UnsubscribeActivity.class);
        classes.put("连载预订", SerialSubscribeActivity.class);
        classes.put("消息中心", MessageCenterActivity.class);
        
        classes.put("分类栏目", WirelessTabActivity.class);
        classes.put("编辑推荐", WirelessTabActivity.class);
        classes.put("热门排行", WirelessRankActivity.class);
        classes.put("名家名作", WirelessTabActivity.class);
        classes.put("精品专栏", WirelessTabActivity.class);
        classes.put("包月书包", WirelessTabActivity.class);
        classes.put("最新资讯", WirelessTabActivity.class);

        
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
