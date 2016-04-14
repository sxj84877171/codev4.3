package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.apache.commons.httpclient.HttpException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar;
import com.pvi.ap.reader.activity.pviappframe.PviUiUtil;
import com.pvi.ap.reader.activity.pviappframe.SelectSpinner;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.data.common.Error;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManager;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 在线图书搜索
 * @author RD040 马中庆
 * 
 */
public class BookSearchActivity extends PviActivity implements Pageable {
	protected static final String TAG = "BookSearchActivity";    
	private EditText met_Keyword = null; // 搜索关键字
	
	PviBottomBar  pbb;     //引用框架底部工具条

	private ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();

	private TextView mtv_ResultCount = null;

	private String catalogId = null;// 搜索栏目
	private Intent revIntent = null;
	private Bundle revBundle = null;
	private int sortVariable;
	private Button b_Submit= null;
	// 分页
	// pager


	private int total = 0;
	private int perpage = 10;
	private int pages = 0;
	private int curpage = 1;
	private int start = 0;

	private PviAlertDialog pd;
	//	private int type = 1 ;
	private LinkedHashMap<String, String> SortListMap = new LinkedHashMap<String, String>();
	private SelectSpinner mark_sort = null;

	private Handler mHandler = new H();
	public static final int GET_DATA = 101;    //获取数据
	public static final int SET_UI_DATA = 102; //设置UI
	private boolean fetchingData = false;

	private RelativeLayout[] serachlayout = null;
	private TextView[] tv_order = null;
	private TextView[] tv_bookname = null;
	private TextView[] tv_author = null;
	private int pageCounter = 0;//翻页计数器
	private void compPageCounter() {
		if (((GlobalVar) getApplication()).deviceType == 1) {
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


	private View.OnClickListener searchclicklistener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int index = 0;
			switch(v.getId())
			{
			case R.id.searchlayout01:
				index= 0;
				break;
			case R.id.searchlayout02:
				index= 1;
				break;
			case R.id.searchlayout03:
				index= 2;
				break;
			case R.id.searchlayout04:
				index= 3;
				break;
			case R.id.searchlayout05:
				index= 4;
				break;
			case R.id.searchlayout06:
				index= 5;
				break;
			case R.id.searchlayout07:
				index= 6;
				break;
			case R.id.searchlayout08:
				index= 7;
				break;
			case R.id.searchlayout09:
				index= 8;
				break;
			case R.id.searchlayout10:
				index= 9;
				break;
			}
			//showMessage("进入图书摘要...","20000");
			//打开图书摘要界面
			final int indexT = (curpage-1)*perpage + index ;
			if(indexT < bookList.size())
			{
				final Intent tmpIntent = new Intent(MainpageActivity.START_ACTIVITY);
				final Bundle bundleToSend = new Bundle();
				bundleToSend.putString("actID","ACT11110");
				bundleToSend.putString("startType", "allwaysCreate");
				bundleToSend.putString("contentID",bookList.get(
				        indexT).get("contentID"));
				tmpIntent.putExtras(bundleToSend);
				sendBroadcast(tmpIntent);
			}else{
			    Logger.e(TAG,"indexT: "+indexT+", out of range bookList.size():"+bookList.size());
			}
		}
	};

	private class H extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case GET_DATA:// 执行 获取用户数据
				getData();
				break;    
			case SET_UI_DATA:
				setUIData();
				break;  
			default:
				;
			}

			super.handleMessage(msg);
		}
	}

	public void setSort(int i) {
		String[] s = { "按书名", "按作者", "综合搜索" };
		mark_sort.setSelectKey(s[i]);
	}

	private void initspinner()
	{
		SortListMap.put("按书名", "1");
		SortListMap.put("按作者", "2");
		SortListMap.put("综合搜索", "6");
		mark_sort.setKey_value(SortListMap);
	}

	public void initControls() {

		met_Keyword = (EditText) findViewById(R.id.Keyword);
		met_Keyword.setOnFocusChangeListener(new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if(arg1==false){
				    PviUiUtil.hideInput(arg0);
				}else{
					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).showSoftInput(met_Keyword, 0);
					met_Keyword.setText("");
					met_Keyword.setTextColor(Color.BLACK);
				}
			}});
		//		mlv_BookList = (ListView) findViewById(R.id.BookList);
		mtv_ResultCount = (TextView) findViewById(R.id.ResultCount);

		b_Submit = (Button) findViewById(R.id.Submit);
		mark_sort = (SelectSpinner)findViewById(R.id.mark_sort);


		if(deviceType==1)
		{
//			met_Keyword.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			mtv_ResultCount.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			b_Submit.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
//			mark_sort.setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);	
		}
		initspinner();
		setSort(0);
		super.initControls();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			revIntent = this.getIntent();
			revBundle = revIntent.getExtras();
			if (revBundle != null) {
				if(revBundle.containsKey("catalogId"))
				{
					catalogId = revBundle.getString("catalogId");
				}
				if(revBundle.containsKey("searchKey"))
				{
					met_Keyword.setText(revBundle.getString("searchKey"));
					sortVariable = 2;
					setSort(sortVariable);
					b_Submit.performClick();
				}
				else
				{				    
					showme();
				}
			}
		} catch (Exception e) {
			Logger.e(TAG, e.toString());
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {


		setContentView(R.layout.booksearch_ui1);

		super.onCreate(savedInstanceState);
		
		pbb = ((GlobalVar)getApplication()).pbb;

		serachlayout = new RelativeLayout[this.perpage];
		this.tv_author = new TextView[this.perpage];
		this.tv_order=new TextView[this.perpage];
		this.tv_bookname = new TextView[this.perpage];

		this.tv_author[0] = (TextView) this.findViewById(R.id.tv_author01);
		this.tv_author[1] = (TextView) this.findViewById(R.id.tv_author02);
		this.tv_author[2] = (TextView) this.findViewById(R.id.tv_author03);
		this.tv_author[3] = (TextView) this.findViewById(R.id.tv_author04);
		this.tv_author[4] = (TextView) this.findViewById(R.id.tv_author05);
		this.tv_author[5] = (TextView) this.findViewById(R.id.tv_author06);
		this.tv_author[6] = (TextView) this.findViewById(R.id.tv_author07);
		this.tv_author[7] = (TextView) this.findViewById(R.id.tv_author08);
		this.tv_author[8] = (TextView) this.findViewById(R.id.tv_author09);
		this.tv_author[9] = (TextView) this.findViewById(R.id.tv_author10);

		this.tv_bookname[0] = (TextView) this.findViewById(R.id.tv_contentname01);
		this.tv_bookname[1] = (TextView) this.findViewById(R.id.tv_contentname02);
		this.tv_bookname[2] = (TextView) this.findViewById(R.id.tv_contentname03);
		this.tv_bookname[3] = (TextView) this.findViewById(R.id.tv_contentname04);
		this.tv_bookname[4] = (TextView) this.findViewById(R.id.tv_contentname05);
		this.tv_bookname[5] = (TextView) this.findViewById(R.id.tv_contentname06);
		this.tv_bookname[6] = (TextView) this.findViewById(R.id.tv_contentname07);
		this.tv_bookname[7] = (TextView) this.findViewById(R.id.tv_contentname08);
		this.tv_bookname[8] = (TextView) this.findViewById(R.id.tv_contentname09);
		this.tv_bookname[9] = (TextView) this.findViewById(R.id.tv_contentname10);

		this.tv_order[0] = (TextView) this.findViewById(R.id.tv_order01);
		this.tv_order[1] = (TextView) this.findViewById(R.id.tv_order02);
		this.tv_order[2] = (TextView) this.findViewById(R.id.tv_order03);
		this.tv_order[3] = (TextView) this.findViewById(R.id.tv_order04);
		this.tv_order[4] = (TextView) this.findViewById(R.id.tv_order05);
		this.tv_order[5] = (TextView) this.findViewById(R.id.tv_order06);
		this.tv_order[6] = (TextView) this.findViewById(R.id.tv_order07);
		this.tv_order[7] = (TextView) this.findViewById(R.id.tv_order08);
		this.tv_order[8] = (TextView) this.findViewById(R.id.tv_order09);
		this.tv_order[9] = (TextView) this.findViewById(R.id.tv_order10);

		this.serachlayout[0] = (RelativeLayout) this.findViewById(R.id.searchlayout01);
		this.serachlayout[1] = (RelativeLayout) this.findViewById(R.id.searchlayout02);
		this.serachlayout[2] = (RelativeLayout) this.findViewById(R.id.searchlayout03);
		this.serachlayout[3] = (RelativeLayout) this.findViewById(R.id.searchlayout04);
		this.serachlayout[4] = (RelativeLayout) this.findViewById(R.id.searchlayout05);
		this.serachlayout[5] = (RelativeLayout) this.findViewById(R.id.searchlayout06);
		this.serachlayout[6] = (RelativeLayout) this.findViewById(R.id.searchlayout07);
		this.serachlayout[7] = (RelativeLayout) this.findViewById(R.id.searchlayout08);
		this.serachlayout[8] = (RelativeLayout) this.findViewById(R.id.searchlayout09);
		this.serachlayout[9] = (RelativeLayout) this.findViewById(R.id.searchlayout10);

		this.serachlayout[0].setOnClickListener(searchclicklistener);
		this.serachlayout[1].setOnClickListener(searchclicklistener);
		this.serachlayout[2].setOnClickListener(searchclicklistener);
		this.serachlayout[3].setOnClickListener(searchclicklistener);
		this.serachlayout[4].setOnClickListener(searchclicklistener);
		this.serachlayout[5].setOnClickListener(searchclicklistener);
		this.serachlayout[6].setOnClickListener(searchclicklistener);
		this.serachlayout[7].setOnClickListener(searchclicklistener);
		this.serachlayout[8].setOnClickListener(searchclicklistener);
		this.serachlayout[9].setOnClickListener(searchclicklistener);

		if(deviceType==1)
		{
			for(int i=0; i<perpage; i++)
			{
				//serachlayout[i].setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
			}
		}
	}

	//	private static class BookListAdapter extends BaseAdapter {
	//		private LayoutInflater mInflater;
	//		private ArrayList<HashMap<String, String>> bkList;
	//
	//		public BookListAdapter(Context context,
	//				ArrayList<HashMap<String, String>> list) {
	//			mInflater = LayoutInflater.from(context);
	//			bkList = list;
	//		}
	//
	//		public int getCount() {
	//			return bkList.size();
	//		}
	//
	//		public Object getItem(int position) {
	//			return position;
	//		}
	//
	//		public long getItemId(int position) {
	//			return position;
	//		}
	//
	//		public View getView(int position, View convertView, ViewGroup parent) {
	//			
	//		    try {
	//                if(bkList!=null&&bkList.size()>0&&bkList.get(position)!=null){
	//                    ViewHolder holder;
	//                	if (convertView == null) {
	//                		if(skinID==1){
	//                			convertView = mInflater.inflate(
	//                					R.layout.booksearchresultlistitem_ui1, null);
	//                		}else if(skinID==2){
	//                			convertView = mInflater.inflate(
	//                					R.layout.booksearchresultlistitem_ui2, null);
	//                		}
	//                		holder = new ViewHolder();
	//                		holder.tv_order = (TextView) convertView
	//                		.findViewById(R.id.tv_order);
	//                		holder.tv_contentname = (TextView) convertView
	//                		.findViewById(R.id.tv_contentname);
	//                		holder.tv_author = (TextView) convertView
	//                		.findViewById(R.id.tv_author);    
	//                		convertView.setTag(holder);
	//                	} else {
	//                		holder = (ViewHolder) convertView.getTag();
	//                	}
	//   
	//                	holder.tv_order.setText("" + (position + 1));
	//                	final String contentName = bkList.get(position).get(
	//                    "contentName");
	//                	if(contentName!=null){
	//                	    holder.tv_contentname.setText(contentName);
	//                	}
	//	                	final String authorName = bkList.get(position).get("authorName");
	//                	if(authorName!=null){
	//                	    holder.tv_author.setText(authorName);
	//                    }
	//                	
	//                }
	//            } catch (Exception e) {
	//                // TODO Auto-generated catch block
	//                e.printStackTrace();
	//                return convertView;
	//            }
	//			return convertView;
	//		}
	//
	//		static class ViewHolder {
	//			TextView tv_order;
	//			TextView tv_contentname;
	//			TextView tv_author;
	//		}
	//	}

	private String getDatafromNet()
	{
		if(fetchingData){
			return "1";    //
		}
		fetchingData = true;
		showMessage("数据获取中...");

		//		bookList.clear(); 	    

		// read data from remote
		if((curpage-1)*perpage >= bookList.size())
		{
			HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
			HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();

			// POST
			String requestXMLBody = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<Request>" + "<SearchContentReq>";
			if (catalogId != null) {
				requestXMLBody = requestXMLBody + "<catalogID>" + catalogId
				+ "</catalogID>";
			}
			if (start > 0) {
				requestXMLBody = requestXMLBody + "<start>" + start
				+ "</start>";
			}
			requestXMLBody = requestXMLBody + "<SearchInfo>" + "<searchType>"
			+ (mark_sort.getSelectValue())
			+ "</searchType>" + "<searchContent>"
			+ met_Keyword.getText() + "</searchContent>"
			+ "</SearchInfo>" + "</SearchContentReq>" + "</Request>";

			ahmNamePair.put("XMLBody", requestXMLBody);
			
			//Logger.d(TAG,requestXMLBody);

			HashMap responseMap = null;
			try {
				responseMap = CPManager
				.searchContent(ahmHeaderMap, ahmNamePair);
				if (!responseMap.get("result-code").toString().contains(
				"result-code: 0")) {
					Logger.e(TAG, responseMap.get("result-code").toString());

					if (responseMap.get("result-code").toString().contains(
					"result-code: 3117")) {
						pd = new PviAlertDialog(getParent());
						pd.setTitle("搜索返回");
						pd.setMessage(Error.getErrorDescription("3117"));
						pd.setCanClose(true);
						pd.show();
					}
					return responseMap.get("result-code").toString();
				}
			} catch (HttpException e) {
			    e.printStackTrace();
				fetchingData = false;
				return "2";
			} 
			catch(SocketTimeoutException e)
			{
			    e.printStackTrace();
				fetchingData = false;
				return "3";
			}
			catch (IOException e) {
			    e.printStackTrace();
				fetchingData = false;
				return "2";
			}catch (Exception e) {
                e.printStackTrace();
                fetchingData = false;
                return "4";
            }
			byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

			Document dom = null;
			try {
				String xml = new String(responseBody);
				xml = xml.replaceAll("\\s", "");
				dom = CPManagerUtil.getDocumentFrombyteArray(xml.getBytes());
				//Logger.d(TAG,xml);
			} catch (Exception e) {
				e.printStackTrace();
				fetchingData = false;
				return "2";
			}

			Element root = dom.getDocumentElement();
			NodeList nl1 = root.getChildNodes();
			nl1 = nl1.item(0).getChildNodes();

			int nl1Count = nl1.getLength();
			for (int i = 0; i < nl1Count; i++) {
				Element el1 = (Element) nl1.item(i);
				if (el1.getNodeName().equals("totalRecordCount")) {

					total = Integer
					.parseInt(el1.getFirstChild().getNodeValue());
					if(total == 0)
					{
						pages = 0;
					}
					else
					{
						pages = total / perpage;
						if (total % perpage > 0) {
							pages = pages + 1;
						}
					}

					if (curpage == 0 && total > 0) {
						curpage = 1;
					}
					if(curpage > pages)
					{
						curpage = pages;
					}

				} else if (el1.getNodeName().equals("ContentInfoList")) {
					NodeList nl2 = el1.getChildNodes();
					int nl2Count = nl2.getLength();
					for (int j = 0; j < nl2Count; j++) {
						Element el2 = (Element) nl2.item(j);
						if (el2.getNodeName().equals("ContentInfo")) {
							HashMap<String, String> tempHM = new HashMap<String, String>();
							NodeList nl3 = el2.getChildNodes();
							int nl3Count = nl3.getLength();
							for (int k = 0; k < nl3Count; k++) {
								Element el3 = (Element) nl3.item(k);
								tempHM.put(el3.getNodeName(), el3
										.getFirstChild().getNodeValue());
							}
							bookList.add(tempHM);
						}
					}

				}
			}
		}


		Logger.i(TAG,"booklist size:"+bookList.size());			

		fetchingData = false;
		return "0";
	}

	private Runnable getData = new Runnable() {
		@Override
		public void run() {
			setUIData();
			Logger.i("Time", "BookSearchActivity Booklist ok:"
					+ Long.toString(System.currentTimeMillis()));
		}
	};

	private void getData(){
		new Thread() {
			public void run() {
				if("0".equals(getDatafromNet())){
				    mHandler.post(getData);
				}else{
				    showTip("数据获取失败！",1000);
				}
			}
		}.start();
	}

	private void setUIData() {
		if(this.isShown())
		{
			compPageCounter();
		}
		mtv_ResultCount.setText(""+total);
		
		if(pages>0){
		    showPager();
		    updatePagerinfo(curpage+" / "+pages);
		}else{
		    hidePager();
		}

		Boolean noData = true;
		if (bookList.size() < 1) {
			noData = true;
		} else {
			noData = false;
		}

		if (!noData) {
			HashMap<String, String> map = null;
			for(int i=0; i < perpage; i++)
			{
				if((curpage-1)*perpage+i < bookList.size())
				{
					this.serachlayout[i].setVisibility(View.VISIBLE);
					map = bookList.get((curpage-1)*perpage+i);
					this.tv_author[i].setText(map.get("authorName"));
					this.tv_bookname[i].setText(map.get("contentName"));
					this.tv_order[i].setText("" + ((curpage-1)*perpage+i + 1));
				}
				else
				{
					this.serachlayout[i].setVisibility(View.INVISIBLE);
				}
			}

		}
		else
		{
			super.hideTip();
			for(int i=0; i < perpage; i++)
			{
				this.serachlayout[i].setVisibility(View.INVISIBLE);
			}
		}
		
        showme();

	}

	//	// 可能没用
	//	private void setBookList(ArrayList<HashMap<String, String>> list) {
	//		bookList.clear();
	//		for (HashMap<String, String> content : list) {
	//			bookList.add(content);
	//		}
	//	}

	public void bindEvent() {
		super.bindEvent();

		//		prechoose.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				// TODO Auto-generated method stub
		//				if (sortVariable == 0) {
		//					sortVariable = 2;
		//				} else {
		//					sortVariable--;
		//				}
		//				setSort(sortVariable);
		//			}
		//
		//		});
		//		nextchoose.setOnClickListener(new OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				// TODO Auto-generated method stub
		//				if (sortVariable == 2) {
		//					sortVariable = 0;
		//				} else {
		//					sortVariable++;
		//				}
		//				setSort(sortVariable);
		//			}
		//
		//		});


		b_Submit.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
			    PviUiUtil.hideInput(v);
				if(fetchingData){
					return;
				}
				curpage = 1;
				String words = null;
				words = met_Keyword.getText().toString();
				if (words != null && !("").equals(words) && words.length()<512) {

					/*Thread checkUpdate2 = new Thread() {
						public void run() {
							setUIData();
						}
					};
					checkUpdate2.start();*/
					bookList.clear();
					mHandler.sendEmptyMessage(GET_DATA);
				} else {
					pd = new PviAlertDialog(getParent());
					pd.setTitle("温馨提示");
					pd.setMessage("搜索关键字输入有误，请检查。",Gravity.CENTER);
					pd.setCanClose(true);
					pd.show();
				}
			}
		});
	}

	private void showme()
	{
	    if(!this.isShown()){
	        super.hideTip();
	        super.showMe(getClass());
	    }
		
	}
    @Override
    public void OnNextpage() {
        if(fetchingData){
            return;
        }
        if (total > 0 && curpage < pages) {
            curpage = curpage + 1;
            start = (curpage - 1) * perpage + 1;

            mHandler.sendEmptyMessage(GET_DATA);
        }
        super.OnNextpage();
    }
    @Override
    public void OnPrevpage() {
        if(fetchingData){
            return ;
        }
        if (total > 0 && curpage > 1) {
            curpage = curpage - 1;
            start = (curpage - 1) * perpage + 1;

            mHandler.sendEmptyMessage(GET_DATA);
        }
        super.OnPrevpage();
    }

}
