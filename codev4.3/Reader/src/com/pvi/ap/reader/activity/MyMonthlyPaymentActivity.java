package com.pvi.ap.reader.activity;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.data.common.Logger;

/**
 * 我的包月
 * @author 刘剑雄
 *
 */
public class MyMonthlyPaymentActivity extends PviActivity {

	PviDataList listView;
	ArrayList<PviUiItem[]> datalist;
	PviBottomBar  pbb;  

	private RelativeLayout norecord_layout = null;
	private TextView tishi=null;
	private TextView retbtn = null;

	private int pageNum=1;//页码
	private int count=1;//总页
	private int rows=0;//数据行数
	private int number=7;//每页多少记录

	private boolean isshow = false;
	private Handler listHandler = new Handler();
	private Runnable setdata = new Runnable()
	{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			setValue();
		}

	};
	ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	protected final String TAG = "MyFavoriteActivity";
	private int pageCounter = 0;//翻页计数器
	private void compPageCounter() {
		if (GlobalVar.deviceType == 1) {
			pageCounter++;
			if (pageCounter == 5) {
				pageCounter = 0;
				// gc16 full flash window
				Logger.d(TAG, "gc16 full");
//				getWindow().getDecorView().getRootView().postInvalidate(
//						View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//						| View.EINK_UPDATE_MODE_FULL);
			} else {
				Logger.d(TAG, "DU content");
			}
		}
	}
	//	private String order=MonthlyPayment.StartTime+" DESC ";
	protected void onResume() {
		// TODO Auto-generated method stub
		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MyMonthlyPaymentActivity" + Long.toString(TimeStart));
		super.onResume();
		isshow = false;
		super.showMessage("进入我的包月...","15000");
		Thread checkUpdate = new Thread() {  
			public void run() {
				Logger.d("MyMonthlyPayment","thread entered");
				getDataFromNetwork();
				listHandler.post(setdata);

			}
		};
		checkUpdate.start();
	}

	/**
	 * 从MonthlyPayment表获取数据
	 * 
	 */         
	//	public void getDataFromDB(){
	//		list.clear();
	//		String columns[] = new String[]{ 
	//				MonthlyPayment._ID,
	//				MonthlyPayment.UserID, 
	//				MonthlyPayment.ParentCatalogID,
	//				MonthlyPayment.ParentCatalogName,
	//				MonthlyPayment.CatalogID,
	//				MonthlyPayment.CatalogName,
	//				MonthlyPayment.URL,
	//				MonthlyPayment.Fee,
	//				MonthlyPayment.NextChargeTime,
	//				MonthlyPayment.StartTime
	//		};
	//		Cursor cur = null;
	//
	//		cur = managedQuery(MonthlyPayment.CONTENT_URI, columns, null, null, order );
	//
	//		HashMap<String, Object> map = null;
	//		try{
	//			if (cur.moveToFirst()){
	//
	//				do{
	//					map = new HashMap<String, Object>();
	//					String id=cur.getString(cur.getColumnIndex(MonthlyPayment._ID));
	//					map.put("ID", id);
	//					String userID = cur.getString(cur.getColumnIndex(MonthlyPayment.UserID));
	//					map.put("userID", userID);
	//					String parentCatalogID = cur.getString(cur.getColumnIndex(MonthlyPayment.ParentCatalogID));
	//					map.put("parentCatalogID", parentCatalogID);
	//					String parentCatalogName = cur.getString(cur.getColumnIndex(MonthlyPayment.ParentCatalogName));
	//
	//					map.put("parentCatalogName", parentCatalogName);
	//					String catalogID = cur.getString(cur.getColumnIndex(MonthlyPayment.CatalogID));
	//
	//					map.put("catalogID", catalogID);
	//					String catalogname= cur.getString(cur.getColumnIndex(MonthlyPayment.CatalogName));
	//
	//					map.put("catalogname", catalogname);
	//					String url = cur.getString(cur.getColumnIndex(MonthlyPayment.URL));
	//
	//					map.put("url", url);
	//					String fee= cur.getString(cur.getColumnIndex(MonthlyPayment.Fee));
	//					if(fee==null){
	//						fee="";
	//					}
	//					map.put("fee", fee);
	//					String nextchargetime = cur.getString(cur.getColumnIndex(MonthlyPayment.NextChargeTime));
	//					if(nextchargetime==null){
	//						nextchargetime="";
	//					}
	//					map.put("nextchargetime", nextchargetime);
	//					String starttime= cur.getString(cur.getColumnIndex(MonthlyPayment.StartTime));
	//					map.put("starttime", starttime);
	//
	//					list.add(map); 
	//
	//
	//				}while (cur.moveToNext());
	//
	//			}
	//		}catch(Exception e){
	//			return;
	//		}finally{
	//			if(cur!=null){
	//				cur.close();
	//			}
	//		}
	//	}
	public int getDataFromNetwork(){
		list.clear();

		HashMap<String, Object> map = null;
		String StrGCSL = SubscribeProcess.network("getCatalogSubscriptionList",null,null,null,null);
		if (StrGCSL.substring(0, 10).contains("Exception")) {
			// retry();
			return 1;
		}

		try {
			if(StrGCSL.substring(0, 19).contains("0000")){
				InputStream is = new ByteArrayInputStream(StrGCSL.substring(20).getBytes());	
				Element rootele = null;
				DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance(); 
				DocumentBuilder db = dbfactory.newDocumentBuilder();	
				Document dom = db.parse(is);
				rootele = dom.getDocumentElement();
				NodeList nl = rootele.getElementsByTagName("CatalogInfo");
				for(int i = 0;i < nl.getLength();i++){
					map = new HashMap<String, Object>();
					Element entry = (Element)nl.item(i);
					NodeList nl2 = entry.getElementsByTagName("parentCatalogID");
					String tmp = nl2.item(0).getFirstChild().getNodeValue();
					Logger.d("",tmp);
					map.put("parentCatalogID",tmp);

					NodeList nl3 = entry.getElementsByTagName("catalogID");
					map.put("catalogID", nl3.item(0).getFirstChild().getNodeValue());

					NodeList nl4 = entry.getElementsByTagName("catalogName");
					map.put("catalogname", nl4.item(0).getFirstChild().getNodeValue());

					try {
						NodeList nl5 = entry.getElementsByTagName("image");
						map.put("url", nl5.item(0).getFirstChild().getNodeValue());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						map.put("url","null");
					}

					NodeList nl6 = entry.getElementsByTagName("fee");
					map.put("fee", nl6.item(0).getFirstChild().getNodeValue());

					//							NodeList nl7 = entry.getElementsByTagName("nextChargeTime");
					//							map.put("nextchargetime", nl7.item(0).getFirstChild().getNodeValue());

					NodeList nl8 = entry.getElementsByTagName("startTime");
					map.put("starttime", nl8.item(0).getFirstChild().getNodeValue());


					list.add(map); 
				}//end of content item loop
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 2;
		}
		return 0;
	}
     
	public void setValue(){
		if(isshow){
			compPageCounter();
		}

		rows=list.size();
		double j=(double)rows/number;
		count=(int)Math.ceil(j);
		if(count>0){
			if(pageNum>count){
				pageNum=count;
			}
			showPager();
			updatePagerinfo(pageNum+" / " + count);
		}else{
			pageNum = 1;
			count = 1;
			hidePager();
		}
		if(rows==0){
			listView.setVisibility(View.GONE);
			norecord_layout.setVisibility(View.VISIBLE);
			tishi.setText(getResources().getString(R.string.bookmymonthlynone));
			retbtn.setText("转入无线书城");
			retbtn.requestFocus();
			this.retbtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					//					showTip("进入无线书城...",20000);
					Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
					Bundle bundleToSend = new Bundle();
					bundleToSend.putString("act",
					"com.pvi.ap.reader.activity.WirelessStoreMainpageActivity");
					bundleToSend.putString("haveTitleBar", "1");
					tmpIntent.putExtras(bundleToSend);
					sendBroadcast(tmpIntent);
				}
			});
		}
		else
		{
			listView.setVisibility(View.VISIBLE);
			norecord_layout.setVisibility(View.GONE);
			PviUiItem[] items = null;
			datalist.clear();
			for(int i = 0; i < this.number; i ++)
			{
				items = null;
				if((pageNum-1)*number + i < list.size())
				{
					items = new PviUiItem[]{
							new PviUiItem("pic"+i, R.drawable.mymoth, 38, 23, 60, 80, null, true, true, null),
							new PviUiItem("pkgname"+i, 0, 100, 35, 550, 30, "", false, true, null),
					};
					HashMap<String,Object> map=new HashMap<String,Object>();

					map=list.get((pageNum-1)*number+i);
					items[1].text = map.get("catalogname").toString();
					if(datalist!=null && i < datalist.size())
					{
						this.datalist.set(i, items);
					}
					else
					{
						this.datalist.add(i, items);
					}
				}
			}
			listView.setData(datalist);
		}
		if(!isshow)
		{
			showme();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		long TimeStart = System.currentTimeMillis();
		Logger.i("Time","MyMonthlyPaymentActivity start" + Long.toString(TimeStart));
		setContentView(R.layout.mymonthlypayment);
		super.onCreate(savedInstanceState);
		list.clear();
		listHandler = new Handler();
		listView= (PviDataList)findViewById(R.id.list);
		listView.lineHeight = 90;
		datalist = new ArrayList<PviUiItem[]>();
		final GlobalVar app = ((GlobalVar) getApplicationContext());    
		pbb = app.pbb;
		this.showPager = true;
		norecord_layout = (RelativeLayout) this.findViewById(R.id.norecordlayout);
		tishi = (TextView) this.findViewById(R.id.tishi);
		retbtn = (TextView) this.findViewById(R.id.gotowirlessstore);
		listView.setOnRowClick(new PviDataList.OnRowClickListener()
		{
			@Override
			public void OnRowClick(View v, int rowIndex) {
				// TODO Auto-generated method stub
				setEvent(rowIndex);
			}
		});
		
		
	}
	public void setEvent(int i){
		super.showMessage("进入书包介绍...", "10000");
		HashMap<String, Object> map=list.get((pageNum-1)*number+i);
		final String catalogId=map.get("catalogID").toString();
		final String name=map.get("catalogname").toString();
		Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act","com.pvi.ap.reader.activity.BookPackageInfoActivity"); 
		bundleToSend.putString("catalogID",catalogId);  
		bundleToSend.putString("catalogName",name);
		bundleToSend.putString("startType",  "allwaysCreate");
		bundleToSend.putString("canSubscribe","true");
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}

	private void showme(){
		isshow = true;
		Logger.d("show me",this.getClass().getName());
		sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
		bundleToSend.putString("act", "com.pvi.ap.reader.activity.MyBookshelfActivity");//TabActivity的类名
		bundleToSend.putString("actTabName", "我的包月");
		bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
		tmpIntent.putExtras(bundleToSend);
		sendBroadcast(tmpIntent);
	}


}
