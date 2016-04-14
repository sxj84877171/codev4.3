package com.pvi.ap.reader.activity.pviappframe;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParserException;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.MainpageActivity;
import com.pvi.ap.reader.activity.NetCache;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * UI工具类
 * @author rd040 马中庆
 * 
 *
 */
public class PviUiUtil {
    protected static final String TAG = "PviUiUtil";
    public final static String BlockListLocalFile = Environment.getDataDirectory() + "/data/com.pvi.ap.reader/files/"+"blocklist.dat";

    public static ColorStateList getColorStateList(Resources r, int xml) {
        ColorStateList csl = null;
        XmlResourceParser parser = r.getXml(xml);
        try {
            csl = ColorStateList.createFromXml(r, parser);
            parser.close();
        } catch (XmlPullParserException e) {
            parser.close();
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            parser.close();
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return csl;
    }

    public static void Alert(PviAlertDialog pd, Context context, String title,
            String message, long timeout, boolean isHaveCloseBtn) {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        pd = new PviAlertDialog(context);
        pd.setTitle(title);
        pd.setMessage(message);
        pd.setCanClose(isHaveCloseBtn);
        if (timeout > 0) {
            pd.setTimeout(timeout);
        }
        pd.show();
    }

    public static void Redo(PviAlertDialog pd, Context context,Intent ii) {
        {
            if(pd!=null&&pd.isShowing()){
                pd.dismiss();
            }
            final Intent iii = ii;
            final Activity activity = (Activity) context;
            pd = new PviAlertDialog(activity.getParent());
            pd.setTitle(R.string.kyleHint02);
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //activity.onCreate(null);
                            //activity.
/*                            Intent i = activity.getIntent();
                            if(i!=null){
                                Bundle bd = i.getExtras();
                                Log.e(TAG,bd.toString());
                            }else{
                                Log.e(TAG,"i is null");
                            }*/
                            
/*                            if(iii!=null){
                                Bundle bd = iii.getExtras();
                                Log.e(TAG,bd.getString("act"));
                            }else{
                                Log.e(TAG,"iii is null");
                            }*/
                            //activity.sendBroadcast(new Intent(MainpageActivity.START_ACTIVITY));
                            activity.sendBroadcast(iii);
                        }
                    });
            pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //activity.sendBroadcast(new Intent(
                                    //MainpageActivity.BACK));
                        }

                    });
            pd.show();
        }
    }
    
    public static String FormatTime(String curtime,String curfomat,String newformat) 
    {
        DateFormat format1 = new SimpleDateFormat(curfomat);      
        Date tempTime = new Date();
        try {
            tempTime = format1.parse(curtime);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
        DateFormat format2 = new SimpleDateFormat(newformat);
        return format2.format(tempTime);
    }
    

    
    /**
     * 从本地文件中读取无线书城的6个主TAB栏目   并排序
     * @return
     */
    public static ArrayList<HashMap<String,String>> getBlockInfo() {
        final ArrayList<HashMap<String,String>> blockInfo = new ArrayList<HashMap<String,String>>();        
        
        final String strGBL = getBlockListXml();        

        if (strGBL != null ) {
            //Logger.d(TAG, "get right xml from net");

            InputStream is = new ByteArrayInputStream(strGBL.getBytes());
            Element rootele = null;
            try {
                DocumentBuilderFactory dbfactory = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbfactory.newDocumentBuilder();
                Document dom = db.parse(is);
                rootele = dom.getDocumentElement();
                NodeList nl1 = rootele.getElementsByTagName("BlockInfo");


                for (int i = 0; i < nl1.getLength(); i++) {
                    //Logger.e(TAG,"get block from xml,i:"+i);
                    Element entry = (Element) nl1.item(i);
                    
                    final HashMap<String,String> hm = new HashMap<String,String>();


                        Element eTitle = (Element) entry.getElementsByTagName(
                                "blockName").item(0);                       
                        
                        hm.put("blockName", eTitle.getFirstChild().getNodeValue());
                        
                        Element eblockid = (Element) entry
                                .getElementsByTagName("blockID").item(0);
                        hm.put("blockID", eblockid.getFirstChild()
                                .getNodeValue());
                        
                        Element eblocktype = (Element) entry
                                .getElementsByTagName("blockType").item(0);
                        hm.put("blockType", eblocktype.getFirstChild()
                                .getNodeValue());
                        
                        Element eblockposition = (Element) entry
                                .getElementsByTagName("blockPosition").item(0);
                        hm.put("blockPosition", eblockposition
                                .getFirstChild().getNodeValue());

                        blockInfo.add(hm);
                    
                }

            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("error", "2 get wiress store Blocklist error");
                return null;
            }

        } else {
            Logger.e("error", "3 get wiress store Blocklist error");
            return null;
        }
        
        if(blockInfo.size()<6){//至少6个才能保证界面正常
            Logger.e("error", "blockinfo size < 6");
            return null;
        }
        
        
        //排序
        ArrayList<HashMap<String,String>> orderedBlockInfo = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<6;i++){
            //Logger.e(TAG,"order blockinfo,i:"+i);
            final HashMap<String,String> hm = getBlockByPosition(blockInfo,i);
            if(hm!=null){
                orderedBlockInfo.add(hm);
        
            }else{
                Logger.e(TAG,"order blockinfo err, hm is null, i:"+i);
            }
        }
        
        return orderedBlockInfo;
    }
    
    public static HashMap<String,String> getBlockByPosition(ArrayList<HashMap<String,String>> blockInfo,int pos){
        final int sizeBlockInfo = blockInfo.size();
        for(int i=0;i<sizeBlockInfo;i++){
            final HashMap<String,String> hm = blockInfo.get(i);
            if(hm!=null&&(""+(pos+1)).equals(hm.get("blockPosition"))){
                return hm;
            }
        }
        return null;
    }
    
    /*取无线书城书籍栏目数据XML
     * 1、从本地读取
     * 2、读取后，取出文件最后修改时间，判断是否已超过24小时未更新；如果超过，尝试网络操作更新此文件    * 
     */
     public static String getBlockListXml(){
         
         /*
          * 线程：从网络获取新文件
          */
         Thread writeFile = new Thread(){
             public void run() {
                 
                 HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
                 HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
                 HashMap responseMap = null;
                 ahmNamePair.put("catalogId", "1");
                 try {
                     responseMap = CPManager.getBlockList(ahmHeaderMap, ahmNamePair);
                 } catch (HttpException e) {
                     e.printStackTrace();
                     return;
                 } catch (SocketTimeoutException e) {
                     e.printStackTrace();
                     return;
                 } catch (IOException e) {
                     e.printStackTrace();
                     return;
                 }
                 
                 if(responseMap==null){
                     return;
                 }
                 
                 
                 
                 if(responseMap.get("result-code")!=null){
                     final String retcode = responseMap.get("result-code").toString();                 
                     if (retcode != null && retcode.contains("result-code: 0")) {
                         //写入本地
                         try {
                             byte[] responseBody = (byte[]) responseMap.get("ResponseBody");
                             final String xml = new String(responseBody);
                             final File f = new File(BlockListLocalFile);
                             final FileWriter writer = new FileWriter(f);
                             writer.write(xml);
                             writer.close();
                             final String sDt = responseMap.get("TimeStamp").toString().replace("TimeStamp: ", "");
                             SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                             Date dt = sdf.parse(sDt);
                             f.setLastModified(dt.getTime());//以服务器端时间为准
                         } catch (IOException e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                         }catch (Exception e) {
                             // TODO Auto-generated catch block
                             e.printStackTrace();
                         }
                     }
                 }
             }
             
         };
         
         StringBuffer xml = new StringBuffer();
         try {
             final File f = new File(BlockListLocalFile);
             final BufferedReader reader = new BufferedReader(new FileReader(f));
             String line = "";
             while (  (line = reader.readLine()) != null  ){
                 xml.append(line);
             }
             reader.close();
             
             if((System.currentTimeMillis()-f.lastModified())>86400){//信任系统时间，因为做了时间同步
                 writeFile.start();
             }
             
         } catch (FileNotFoundException e) {
             // TODO Auto-generated catch block
             writeFile.start();
             e.printStackTrace();
         } catch (IOException e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }    
         
         return xml.toString();
     }

     public static long getLastModify(String filename){
         final File f = new File(filename);
         if(f!=null){
             return f.lastModified();
         }else{
             return 0L;
         }
     }
     
     /**
      * 从一个相对于平台CPC_BASE_URL的路径，取得一个bitmap
      * @param relaPath
      * @return
      */
     public static Bitmap getImage(String relaPath) {
         if(relaPath==null||"".equals(relaPath)){
             Logger.e(TAG,"ImageUri is null or empty");
             return null;
         }

         Bitmap bitmap = null;
         try {
             //Logger.d(TAG,"getImage:"+Config.getString("CPC_BASE_URL")+ relaPath);
             bitmap = NetCache.GetNetImage(Config.getString("CPC_BASE_URL")+ relaPath);
         } catch (Exception e) {
             e.printStackTrace();
         }
         return bitmap;
     }
     
     /**
      * 截取指定宽度的文本，后面自动加上...
      * @param mpaint   包含了文本显示时的字体大小等信息
      * @param source
      * @param width
      * @return
      */
     public static String getClipedText(Paint paint,String source, int width){
         Rect rect = new Rect();
         String dest = null;
         String lSource ;
         paint.getTextBounds(source, 0, source.length(), rect);
         if(rect.right - rect.left <= width){
             return source ;
         }else{
             lSource = "..." + source ; 
         }
         for(int i = 4; i <= lSource.length(); i ++)
         {
             paint.getTextBounds(lSource, 0, i, rect);
             if(rect.right - rect.left > width)
             {
                 dest = lSource.substring(3, i-1);
                 return dest + "...";
             }
         }
         dest = source + "..." ;
         return dest;
     }
     
     /**
      * 获取文本显示的宽度
      * @param paint
      * @param text
      * @return
      */
     public static int getTextDrawWidth(Paint paint,String text){
         Rect rect = new Rect();
         paint.getTextBounds(text, 0, text.length(), rect);
         return rect.right - rect.left;
     }

     
     /**
      * 获取文本显示的高度
      * @param paint
      * @param text
      * @return
      */
     public static int getTextDrawHeight(Paint paint,String text){
         Rect rect = new Rect();
         paint.getTextBounds(text, 0, text.length(), rect);
         return rect.bottom - rect.top;
     }
     
     public static void toHomePage(Context context) {
         final Intent intent = new Intent(MainpageActivity.START_ACTIVITY);
         final Bundle sndBundle = new Bundle();
         sndBundle.putString("act",
                 "com.pvi.ap.reader.activity.MainpageInsideActivity");
         sndBundle.putString("haveStatusBar", "1");
         sndBundle.putString("startType", "reuse");
         intent.putExtras(sndBundle);
         context.sendBroadcast(intent);
     }
     
     public static String strarrToStr(String[] arr){
         StringBuffer sb = new StringBuffer();
         final int len = arr.length;
         for(int i=0;i<len;i++){
             sb.append(arr[i]);
         }
         return sb.toString();
     }
     
     /**
      * 隐藏输入法  （总是起作用么？）
      * @param v
      */
     public static void hideInput(View v){
             //Logger.d(TAG,"call 隐藏输入法() v:"+v);
             
             final Context context = v.getContext();
             final View rv = v.getRootView();
             //Logger.d(TAG,"context:"+context);
             ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE))
             .hideSoftInputFromWindow(rv.getWindowToken(),
                     InputMethodManager.HIDE_NOT_ALWAYS);
             
             /*if(deviceType==1){
                 //Logger.d(TAG,"pviactivity hideInput() ... setupdatemode(...gc16 full");
                 getWindow().
                 getDecorView()
                 .getRootView()
                 .setUpdateMode(View.EINK_WAIT_MODE_WAIT |
                         View.EINK_WAVEFORM_MODE_GC16 | 
                         View.EINK_UPDATE_MODE_FULL); 
             }*/
     }
     
     public static void showInput(View v){
         //Logger.e(TAG,"强制弹出输入法？？");
         final Context context = v.getContext();
         ((InputMethodManager)context.getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(v, InputMethodManager.SHOW_FORCED);
     }
}
