package com.pvi.ap.reader.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviMenuItem;
import com.pvi.ap.reader.activity.pviappframe.PviMenuPan;
import com.pvi.ap.reader.activity.pviappframe.PviPopupWindow;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 应用程序
 * 
 * @author kyle & zhongqingma
 * @author rd041
 * 
 */
public class GlobalVar extends Application {
    
    public PviBottomBar pbb;

    public static int deviceType = 1;         //1 fsl   2 模拟器   3marvell  4 orther real device
    
	private static final String TAG = "GlobalVar";
	public PopupWindow popmenu;
	private Resources mRes;

	private String myState = "w";

	private String id = "empty";
	private String product = "empty";
	private String h_version = "empty";
	private String s_version = "empty";
	private String d_version = "empty";
	private String u_agent = "empty";
	private String API_version = "empty";
	private String ScrSize = "empty";
	private String client_key = "empty";
	private String imei = "empty";
	private String created = "empty";

	private String rankType = "empty";
	private String rankCatalog = "empty";
	private String CatalogId = "empty";

	// TODO : need fix later ?
	// add by kizan 2011.01.21 for intent paramaters missing with some case
	// for writerAcivity
	private String authorID = "";
	// for bookpackageActivity
	private String catalogID = "";
	private String catalogName = "";
	private String canSubscribe = "";

/*	private String[] blockName = new String[6];
	private String[] blockId = new String[6];
	private String[] blockType = new String[6];
	private String[] blockPosition = new String[6];
	private String[] blockExtraType = new String[6];// blockType
*/	// =1时此字段起作用，1：包月书包
	// 2：不可包月的书包 3：分类栏目
	
	private ArrayList<HashMap<String,String>> blockInfo = null;//存放blockInfo（6个TAB的数据）
	public long blockInfoFileTime = 0L;

	private String nickName = ""; // 昵称
	
	//打版本清掉这三行的值
    private String regCode = "";
    private String lineNum = ""; // 手机号
    private String userID = ""; // 平台用户唯一标识

    private String moniRegCode = "1vKdvBRGut/0l/muyVBpHg==";
    private String moniLineNum = "13682495390"; // 手机号
    private String moniUserID = "0489a04e54cd87ad5ca03947ab360230"; // 平台用户唯一标识

	private String production = "";
	private String clientVersion = "";
	private String deviceID = "";

	private String simID = "";

	private static SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
	
	public boolean hasMusic = false;
	public int musicFlag = -1;         //-1 未知   0   未播放   1 正在播放

	public static String getFormatTime(long time)
	{
		Date CurTime = new Date(time);
		return  formatter.format(CurTime);
	}
	public static String getFormatTime(Date time)
	{
		return  formatter.format(time);
	}
	
	public static String TimeFormat(String curfomat,String curtime) throws ParseException
	{
		DateFormat format1 = new SimpleDateFormat(curfomat);      
		Date tempTime = new Date();
		tempTime = format1.parse(curtime);
		return GlobalVar.getFormatTime(tempTime);
	}
	public static String TimeFormat(String destformat,String curfomat,String curtime) throws ParseException
	{
		DateFormat format1 = new SimpleDateFormat(curfomat);      
		Date tempTime = new Date();
		tempTime = format1.parse(curtime);
		SimpleDateFormat formatter = new SimpleDateFormat(destformat);
		return formatter.format(tempTime);
	}
	/**
	 * 是否需要鉴权 true 需要鉴权 false 不需要鉴权
	 */
	private boolean needAuth = true; // 如果启用，则需要赋初值为TRUE 默认需要鉴权

	public HashMap<String,View> submenupans = new HashMap<String,View>();

	public String getSimID() {
		return simID;
	}

	public void setSimID(String simID) {
		this.simID = simID;
	}

	public boolean isNeedAuth() {
	    if(deviceType==2){//模拟器不需要鉴权
            return false;
        }else{        
            return needAuth;    
        }
	}

	public void setNeedAuth(boolean toHand) {
		this.needAuth = toHand;
	}

	/*
	 * //theme public void setThemeNum(int tmp){ ThemeNum = tmp; } public int
	 * getThemeNum(){ return ThemeNum; }
	 */
	// test
	public String getState() {
		return myState;
	}

	public void setState(String s) {
		myState = s;
	}

/*	public void setblockId(int index, String blockid) {
		blockId[index] = blockid;
	}

	public String getblockId(int index) {
		return blockId[index];
	}

	public void setblockType(int index, String blocktype) {
		blockType[index] = blocktype;
	}

	public String getblockType(int index) {
		return blockType[index];
	}*/

	public void setrankType(String pa) {
		rankType = pa;
	}

	public String getrankType() {
		return rankType;
	}

	public void setrankCatalog(String pa) {
		rankCatalog = pa;
	}

	public String getrankCatalog() {
		return rankCatalog;
	}

	public void setCatalogId(String pa) {
		CatalogId = pa;
	}

	public String getCatalogId() {
		return CatalogId;
	}

	public String getAuthorID() {
		return authorID;
	}

	public void setAuthorID(String authorID) {
		this.authorID = authorID;
	}

	public String getCatalogID() {
		return catalogID;
	}

	public void setCatalogID(String catalogID) {
		this.catalogID = catalogID;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public String getCanSubscribe() {
		return canSubscribe;
	}

	public void setCanSubscribe(String canSubscribe) {
		this.canSubscribe = canSubscribe;
	}

/*	public void setblockPosition(int index, String blockposition) {
		blockPosition[index] = blockposition;
	}

	public String getblockPosition(int index) {
		return blockPosition[index];
	}

	public void setblockExtraType(int index, String blockExtratype) {
		blockExtraType[index] = blockExtratype;
	}

	public String getblockExtraType(int index) {
		return blockExtraType[index];
	}

	public void setblockName(int index, String blockname) {
		blockName[index] = blockname;
	}

	public String getblockName(int index) {
		return blockName[index];
	}

	public String[] getBlockNames() {
		return blockName;
	}*/

	/**
	 * @return the nickName
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * @param nickName
	 *            the nickName to set
	 */
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	/**
	 * @return the regCode
	 */
	public String getRegCode() {
	    
	    if(deviceType==2){
	        return moniRegCode;
	    }else{
	    
	        return regCode;
	
	    }
	}

	/**
	 * @param regCode
	 *            the regCode to set
	 */
	public void setRegCode(String regCode) {
		this.regCode = regCode;
	}

	/**
	 * @return the lineNum
	 */
	public String getLineNum() {
	       if(deviceType==2){
	            return moniLineNum;
	        }else{
	        
	            return lineNum;
	    
	        }
	}

	/**
	 * @param lineNum
	 *            the lineNum to set
	 */
	public void setLineNum(String lineNum) {
		this.lineNum = lineNum;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
	    if(deviceType==2){
            return moniUserID;
        }else{
        
            return userID;
    
        }
	}

	/**
	 * @param userID
	 *            the userID to set
	 */
	public void setUserID(String userID) {
		this.userID = userID;
	}

	/**
	 * 内置密码
	 */
	private String innerPassword = "PVI80120100922";

	/**
	 * 内置密码
	 * 
	 * @return
	 */
	public String getInnerPassword() {
		return innerPassword;
	}

	/**
	 * 内置密码
	 * 
	 * @param innerPassword
	 */
	public void setInnerPassword(String innerPassword) {
		this.innerPassword = innerPassword;
	}

	public String getRegInfo(String para1) {
		if (para1.equalsIgnoreCase("id")) {
			return id;
		}
		if (para1.equalsIgnoreCase("product")) {
			return product;
		}
		if (para1.equalsIgnoreCase("h_version")) {
			return h_version;
		}
		if (para1.equalsIgnoreCase("s_version")) {
			return s_version;
		}
		if (para1.equalsIgnoreCase("d_version")) {
			return d_version;
		}
		if (para1.equalsIgnoreCase("u_agent")) {
			return u_agent;
		}
		if (para1.equalsIgnoreCase("API_version")) {
			return API_version;
		}
		if (para1.equalsIgnoreCase("ScrSize")) {
			return ScrSize;
		}
		if (para1.equalsIgnoreCase("client_key")) {
			return client_key;
		}
		if (para1.equalsIgnoreCase("imei")) {
			return imei;
		}
		if (para1.equalsIgnoreCase("created")) {
			return created;
		} else
			return "error param";
	}

	public int setRegInfo(String para1, String para2) {
		if (para1.equalsIgnoreCase("id")) {
			id = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("product")) {
			product = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("h_version")) {
			h_version = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("s_version")) {
			s_version = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("d_version")) {
			d_version = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("u_agent")) {
			u_agent = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("API_version")) {
			API_version = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("ScrSize")) {
			ScrSize = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("client_key")) {
			client_key = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("imei")) {
			imei = para2;
			return 0;
		}
		if (para1.equalsIgnoreCase("created")) {
			created = para2;
			return 0;
		} else
			return -1;
	}

	/**
	 * @return the production
	 */
	public String getProduction() {
		return production;
	}

	/**
	 * @param production
	 *            the production to set
	 */
	public void setProduction(String production) {
		this.production = production;
	}

	/**
	 * @return the clientVersion
	 */
	public String getClientVersion() {
		return clientVersion;
	}

	/**
	 * @param clientVersion
	 *            the clientVersion to set
	 */
	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	/**
	 * @return the deviceID
	 */
	public String getDeviceID() {
		return deviceID;
	}

	/**
	 * @param deviceID
	 *            the deviceID to set
	 */
	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}
	
	
	/**
     * 生成menu的view
     * @param context
     * @return
     */

    public View getMenu(final Context context) {

        //置空“二级菜单框”数据
        submenupans.clear();

        // 菜单颜色
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


                        //二级菜单项
                        final String p = parser.getAttributeValue(pviNsHome, "p");
                        if(p!=null){
                            //是二级菜单项，加入二级菜单的view
                            PviMenuItem submenuitem = getMenuItem( parser, context,androidNsHome,pviNsHome,csl);

                            try {
                                PviMenuPan submenupan = (PviMenuPan)submenupans.get(p);
                                submenupan.addMenuItem(submenuitem);
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } 
                        }else{//普通菜单项、含有二级子菜单的菜单项 都要执行这个片段
                            PviMenuItem menuitem = getMenuItem( parser, context,androidNsHome,pviNsHome,csl);


                            //处理二级菜单
                            final String sub = parser.getAttributeValue(pviNsHome, "sub");
                            if(sub!=null){
                                //Logger.d(TAG,"I have submenu ! sub:"+sub);
                                //menuitem.setSub(sub);
                                menuitem.id="sub_"+sub;//把sub设置为view的Tag
                                //有子菜单的，1 增加一个隐藏的“菜单框”的view，放入成员变量HashMap submenupans 中  2增加  显/隐 菜单框  的事件处理
                                PviMenuPan submenupan = new PviMenuPan(context);
                                submenupan.setId(1);
                                submenupan.setBackgroundResource(R.drawable.bg_menupan_ui1);
                                submenupan.setTag(sub);
                                submenupans.put(sub, submenupan);

                                final int firstLevelMenuitemIndex1 = firstLevelMenuitemIndex;

                                //Logger.d(TAG,"setOnClickListener");
                                menuitem.l2=new OnUiItemClickListener() {
                                    @Override
                                    public void onUiItemClick(PviUiItem item) {
                                        submenupan(sub,((PviActivity) menupan.getContext()),menupan,firstLevelMenuitemIndex1);
                                    }
                                };

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

        if (i == 0) {// 如果没有配置菜单
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
        
        final int visibility = parser.getAttributeIntValue(
                androidNsHome, "visibility", 0);
        if (visibility == 1) {
            menuitem.isVisible=false;
        } else if (visibility == 2) {
            menuitem.isVisible=false;
        }
        // 国际化
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

        // 给item设置监听器
        String op = parser.getAttributeValue(pviNsHome, "op");

        if (op != null) {
            menuitem.setOp(op);
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
                       
                    }};         
                


            } else if (op.equals("wirelessstore")) {
                
                menuitem.l2 = new OnUiItemClickListener(){

                    @Override
                    public void onUiItemClick(PviUiItem item) {
                        final Intent tmpIntent = new Intent(
                                MainpageActivity.SHOW_TIP);
                        final Bundle Bundle = new Bundle();
                        Bundle.putString("pviapfStatusTip","进入无线书城，请稍候...");

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
                        "无线书城欢迎页加载中");
                        msgIntent
                        .putExtras(sndbundle);
                        context
                        .sendBroadcast(msgIntent);

                        final Intent intent = new Intent(
                                context,
                                WirelessStoreWelcomeActivity.class);
                        context
                        .startActivity(intent);
                
                    }
                };



            } else if (op.equals("bgmusic")) {
                
                menuitem.l2 = new OnUiItemClickListener(){

                    @Override
                    public void onUiItemClick(PviUiItem item) {

                        if (item.text.equals(
                                        mRes
                                        .getString(R.string.fp_menu1))) {
                            final Intent myintent = new Intent(
                            "com.pvi.ap.reader.service.BackGroundMusicService");
                            startService(myintent);
                        } else {          

                            final Intent myintent = new Intent("com.pvi.ap.reader.service.BackGroundMusicService");
                            stopService(myintent);

                        }
                    }};
                    

            } else if (op.equals("back")) {

                menuitem.l2 = new OnUiItemClickListener() {
                    @Override
                    public void onUiItemClick(PviUiItem item) {

                        sendBroadcast(new Intent(
                                MainpageActivity.BACK));
                    
                    }
                };             


            } else if (op.equals("mybookshelf")) {// 我的书架

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
                    
                    }
                };

            } else if (op.equals("userspace")) {// 个人空间
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
                    }
                };             

            } else if (op.equals("g3booksearch")) {// 在线图书搜索

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
                    
                    }
                }; 

            }
        }       

        return menuitem;
    }


	public void buildPopupMenu(Context context) {
		this.popmenu = new PopupWindow(getMenu(context),
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	/*
	 * 显示或隐藏 子菜单框
	 * 
	 * menuindex 
	 * */
	private void submenupan(String sub,PviActivity a,View v,int menuitemIndex){
		
		final LinearLayout submenupan = (LinearLayout)submenupans.get(sub);
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

					final View mainBlock = a.findViewById(R.id.mainblock);//必须有id为mainBlock的局部元素
					if(mainBlock!=null){
						int bottom = 40;//菜单底部 至 屏幕底 的高度
						int bottom_sub = 0;  //待计算子菜单底部距 屏幕底 高度
						int height_menuitem = 38;	//单个菜单项的高度

						int menuitem_count = ((LinearLayout)((LinearLayout)a.popmenu.getContentView()).getChildAt(0)).getChildCount();//一级菜单项个数
						int sub_menuitem_count = ((LinearLayout)submenupan.getChildAt(0)).getChildCount();//二级菜单项个数
						bottom_sub = bottom+height_menuitem*(menuitem_count-menuitemIndex-1)-height_menuitem*sub_menuitem_count/2;

						if(bottom_sub<0){
							bottom_sub=0;
						}

						//从父级菜单的位置弹出
						try {
						    if(mainBlock!=null){
                            a.subpopmenu.showAtLocation(mainBlock,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		// long mhs = VMRuntime.getRuntime().setMinimumHeapSize(6*1024*1024);
		// Logger.i(TAG,"VMRuntime.getRuntime().setMinimumHeapSize() = "+Long.toString(mhs));

//		 控制方向
//		Surface.setOrientation(0, 3);
		System.out.close();
		System.err.close();

		// 关闭旋转
		Settings.System.putInt(getContentResolver(),Settings.System.ACCELEROMETER_ROTATION, 1);
		
		initConfig();
		mRes = getResources();
		//        Logger.setWriteToFile(1);

		// start net cache service

		NetCache.start(this);
		
		
		refleshDocutment();

		super.onCreate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onLowMemory()
	 */
	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		Logger.i(TAG, "application onLowMemory()");
		super.onLowMemory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		Logger.i(TAG, "application onTerminate()");
		if (popmenu != null) {
			popmenu.dismiss();
			popmenu = null;
		}
		mRes = null;
		super.onTerminate();
	}

	/**
	 * @author 孙向锦 添加对网络连接的快速判断<br>
	 *         网络是否连接
	 * @return 是否连接
	 */
	public boolean networkConnectAvailable() {
		// String strGBL = SubscribeProcess.network("getBlockList", "1",
		// null, null, null);
		// if (strGBL.substring(0, 10).contains("Exception")) {
		// return false ;
		// }
		return true;
		// Context context = getApplicationContext();
		// ConnectivityManager connectivity = (ConnectivityManager)
		// context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// if (connectivity != null) {
		// NetworkInfo[] info = connectivity.getAllNetworkInfo();
		// if (info != null) {
		// for (int i = 0; i < info.length; i++) {
		// if (info[i].getState() == NetworkInfo.State.CONNECTED) {
		// return true;
		// }
		// }
		// }
		// }
		// return false;
	}

	public void initConfig() {
		InputStream configInput = null;
		File configFile = null;
		OutputStream configOutPut = null;
		configFile = new File(Config.configFileName);
		try {
			if (!configFile.exists()) {
				File pathFile = new File(Environment.getDataDirectory()
						+ "/data/com.pvi.ap.reader/files/");
				if (!pathFile.exists()) {
					pathFile.mkdir();
				}
				configFile.createNewFile();
				configInput = this.getResources().openRawResource(R.raw.config);
				configOutPut = new FileOutputStream(configFile);
				int b = -1;
				while ((b = configInput.read()) != -1) {
					configOutPut.write(b);
				}
				configOutPut.close();
				configInput.close();
			}

		} catch (IOException e) {
			Logger.i(TAG, "加载配置文件异常");
			Logger.i(TAG, e.toString());
		}
		Config.init();

		InputStream errorInput = null;
		File errorFile = null;
		OutputStream errorOutPut = null;
		errorFile = new File(Error.configFileName);
		try {
			if (!errorFile.exists()) {
				File pathFile = new File(Environment.getDataDirectory()
						+ "/data/com.pvi.ap.reader/files/");
				if (!pathFile.exists()) {
					pathFile.mkdir();
				}
				errorFile.createNewFile();
				errorInput = this.getResources().openRawResource(
						R.raw.error_zh_cn);
				errorOutPut = new FileOutputStream(errorFile);
				Writer out = new OutputStreamWriter(errorOutPut, "UTF-8");
				int b = -1;
				while ((b = errorInput.read()) != -1) {
					out.write(b);
				}
				out.close();
				errorOutPut.close();
				errorInput.close();
			}

		} catch (IOException e) {
			Logger.i(TAG, "加载配置文件异常");
			Logger.i(TAG, e.toString());
		}
		Error.init();

		InputStream systemSetInput = null;
		File systemSetFile = null;
		OutputStream systemSetOutPut = null;
		systemSetFile = new File(Config.systemSetFileName);
		try {
			if (!systemSetFile.exists()) {
				File pathFile = new File(Environment.getDataDirectory()
						+ "/data/com.pvi.ap.reader/shared_prefs/");
				if (!pathFile.exists()) {
					pathFile.mkdir();
				}
				systemSetFile.createNewFile();
				systemSetInput = this.getResources().openRawResource(
						R.raw.systemset);
				systemSetOutPut = new FileOutputStream(systemSetFile);
				int b = -1;
				while ((b = systemSetInput.read()) != -1) {
					systemSetOutPut.write(b);
				}                
				systemSetOutPut.close();
				systemSetInput.close();
			}

		} catch (IOException e) {
			Logger.i(TAG, "加载系统设置文件异常");
			Logger.i(TAG, e.toString());
		}
		
		//无线书城TAB初始数据
        InputStream wsInput = null;
        File wsFile = null;
        OutputStream wsOutPut = null;
        try {
            wsFile = new File(WirelessStoreActivity.blockListLocalFile);
            if (!wsFile.exists()) {
                File pathFile = new File(Environment.getDataDirectory()
                        + "/data/com.pvi.ap.reader/files/");
                if (!pathFile.exists()) {
                    pathFile.mkdir();
                }
                wsFile.createNewFile();
                wsInput = this.getResources().openRawResource(R.raw.blocklist);
                wsOutPut = new FileOutputStream(wsFile);
                int b = -1;
                while ((b = wsInput.read()) != -1) {
                    wsOutPut.write(b);
                }
                wsOutPut.close();
                wsInput.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /*
        //初始化DB
        InputStream dbInput = null;
		File dbFile = null;
		OutputStream dbOutPut = null;
		dbFile = new File(Environment.getDataDirectory()
				+ "/data/com.pvi.ap.reader.data/databases/G3Read.db");
		try {
			if (!dbFile.exists()) {
				File pathFile = new File(Environment.getDataDirectory()
						+ "/data/com.pvi.ap.reader.data/databases/");
				if (!pathFile.exists()) {
					pathFile.mkdir();
				}
				dbFile.createNewFile();
				dbInput = this.getResources().openRawResource(
						R.raw.db);
				dbOutPut = new FileOutputStream(dbFile);
				//Writer out = new OutputStreamWriter(dbOutPut, "UTF-8");
				
				int b = -1;
				while ((b = dbInput.read()) != -1) {
					//out.write(b);
					dbOutPut.write(b);
				}
				//out.
				//out.close();
				dbOutPut.close();
				dbInput.close();
			}

		} catch (IOException e) {
			Logger.i(TAG, "加载配置文件异常");
			Logger.i(TAG, e.toString());
		}
		Error.init();*/

	}
	
	
	public void refleshDocutment(){
		long when = 0 ;
		try {
			when = Long.parseLong(Config.getString("reflesherrordocutemttime"));
		} catch (NumberFormatException e) {
//			Logger.i(TAG, e);
		}
		if(when <= 0){
			when = 30l * 24l * 3600l * 1000l ;
		}
		getErrorDocutemt(when);
	}
	
	
	
	public static void  getErrorDocutemt(long when){
		Logger.i("getErrorDocutemt", "getErrorDocutemt:"  + when);
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
				HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
				ahmNamePair.put("type", "1");
				HashMap retMap = null;
				try {
					System.out.println(ahmHeaderMap);
					System.out.println(ahmNamePair);
					retMap = CPManager.getHandsetProperties(ahmHeaderMap,
							ahmNamePair);
					if (retMap == null) {
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
					return ;
				}
				System.out.println(retMap);
				if(retMap.get("result-code") == null){
					return ;
				}
				String resultCode = (String) retMap.get("result-code");
				if (resultCode == null
						|| !resultCode.contains("result-code: 0")) {
					Logger.i(TAG, "getErrorDocutemt: fail");
					return;
				}
				byte[] responseBody = (byte[]) retMap.get("ResponseBody");
				try {
					OutputStream os = new FileOutputStream(new File(Environment
							.getDataDirectory()
							+ "/data/com.pvi.ap.reader/files/"
							+ "error_zh_cn_net.properties"));
//					FileWriter fw = new FileWriter(new File(Environment
//							.getDataDirectory()
//							+ "/data/com.pvi.ap.reader/files/"
//							+ "error_zh_cn_net.properties"), false);
					os.write(responseBody);
					os.flush();
					os.close();
//					fw.write(retStr);
//					fw.flush();
//					fw.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}, 10 * 1000,when);
		
	}
	
	public String getSimType(){
	    if(this.deviceType==1){//非思卡尔
	        return android.os.SystemProperties.get("gsm.sim.card.type");
	    }else if(this.deviceType==2){//模拟器
            return "USIM";
        }else if(this.deviceType==3){//马威尔
            return "";
        }else if(this.deviceType==4){//普通手机
            return "SIM";//默认有卡且是SIM卡
        }else{
            return "WRONGSIM";
        }
	}
	
	
	/**
	 * 取blockinfo  所有取这个值的地方都统一调此方法
	 * @return
	 */
	public ArrayList<HashMap<String,String>> getBlockInfo(){
	    //比较一下“计算”的文件的最后修改时间是否相同，如果不相同，重新“计算”
	    final long nowFileTime = PviUiUtil.getLastModify(PviUiUtil.BlockListLocalFile);
	    if( nowFileTime>blockInfoFileTime ){
	        //Logger.e(TAG,"变量已过期，重新‘计算’。nowFileTime="+nowFileTime+"，curTime="+blockInfoFileTime);
	        blockInfo = PviUiUtil.getBlockInfo();
	        blockInfoFileTime = PviUiUtil.getLastModify(PviUiUtil.BlockListLocalFile);
	    }
	    return blockInfo;
	}
}
