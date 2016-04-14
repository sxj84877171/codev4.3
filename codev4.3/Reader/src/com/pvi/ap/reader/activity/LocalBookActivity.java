package com.pvi.ap.reader.activity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.activity.pviappframe.PviDataList;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem;
import com.pvi.ap.reader.activity.pviappframe.PviBottomBar.Pageable;
import com.pvi.ap.reader.activity.pviappframe.PviUiItem.OnUiItemClickListener;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;


/**
 * 本地书籍分类
 * 
 * @author rd034
 * 
 */
public class LocalBookActivity extends PviActivity implements Pageable{

	private int currentPage = 1;
	private int itemPerPage = 7;
	private int totalPage = 1;
	private int themeNum = 1;// 换肤参数
	PviDataList listView;               //view实例
    ArrayList<PviUiItem[]> list; 
    private int id=0;
	private ArrayList<HashMap<String, String>> localbookclass = new ArrayList<HashMap<String, String>>();

	private boolean getLocalBookClass() {
		localbookclass.clear();
		try {
			InputStream in = getResources().openRawResource(R.raw.localbooks);
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
			.newInstance();
			DocumentBuilder db = dbfactory.newDocumentBuilder();
			Document dom = db.parse(in);
			Element rootele = dom.getDocumentElement();
			HashMap<String, String> map = null;
			NodeList nl = rootele.getElementsByTagName("localBook");
			Element entry = null;
			Element eTitle = null;
			Element echapter = null;
			Element etime = null;
			int nl_len = nl.getLength();
			for (int i = 0; i < nl_len; i++) {
				map = new HashMap<String, String>();
				entry = (Element) nl.item(i);
				eTitle = (Element) entry.getElementsByTagName("typeId").item(0);
				map.put("TypeId", eTitle.getFirstChild().getNodeValue());
				echapter = (Element) entry.getElementsByTagName("typeName")
				.item(0);
				String typeName = echapter.getFirstChild().getNodeValue();
				map.put("TypeName", typeName);
				etime = (Element) entry.getElementsByTagName("count").item(0);
				String count = etime.getFirstChild().getNodeValue();
				map.put("Count", count);
				localbookclass.add(map);
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}


	private void setEvent(int i){
		HashMap<String, String> map = null;
		map = localbookclass.get((currentPage - 1) * itemPerPage + i);
		Intent sndintent = new Intent(MainpageActivity.START_ACTIVITY);
		Bundle sndbundle = new Bundle();
		sndbundle.putString("act",
		"com.pvi.ap.reader.activity.LocalBookListActivity");
		sndbundle.putString("startType", "allwaysCreate");
		sndbundle.putString("TypeId", map.get("TypeId"));
		sndbundle.putString("TypeName", map.get("TypeName"));
		sndbundle.putString("mainTitle", map.get("TypeName"));
		sndintent.putExtras(sndbundle);
		sendBroadcast(sndintent);
	}
    private void setValue(){
    	totalPage = this.localbookclass.size() / this.itemPerPage;
		int n = this.localbookclass.size() % this.itemPerPage;

		if (n != 0) {
			totalPage++;
		}
	     
		list.clear();
		for (int i = 0; i < (this.currentPage < this.totalPage ? this.itemPerPage
				: (localbookclass.size() - (this.currentPage - 1)
						* this.itemPerPage)); i++) {
			 PviUiItem[] items = new PviUiItem[]{
		                new PviUiItem("icon"+i, R.drawable.style2_note, 10, 10, 58, 76, null, false, true, null),
		                new PviUiItem("text1"+i, 0, 100, 30, 200, 50, "我是一列文本", false, true, null),
		                new PviUiItem("text2"+i, 0, 540, 30, 200, 50, "我是又一列文本", false, true, null),
		        };
			 items[1].text=localbookclass.get((this.currentPage - 1) * this.itemPerPage + i).get("TypeName").toString();
			 items[1].textSize=22;
			 items[2].text=localbookclass.get((this.currentPage - 1) * this.itemPerPage + i).get("Count").toString();
			 items[2].textSize=19;
			 items[2].textAlign=2;
			 final int ii=i;
			 listView.setOnRowClick(new PviDataList.OnRowClickListener() {
					
					@Override
					public void OnRowClick(View v, int rowIndex) {
						// TODO Auto-generated method stub
						 setEvent(rowIndex);
					}
				});
//				OnClickListener l = new OnClickListener(){
//
//                 @Override
//                 public void onClick(View arg0) {
//                     // TODO Auto-generated method stub
//                     setEvent(ii);
//                 }
//                 
//             };
//             items[1].l = l;
//             items[2].l = l;
				
             list.add(items);
             listView.setData(list);

		}
	
		final GlobalVar app = ((GlobalVar) getApplicationContext());        
        updatePagerinfo(currentPage+" / "+this.totalPage);
        Intent intent = new Intent(MainpageActivity.HIDE_TIP);
		sendBroadcast(intent);
		if(deviceType==1){
		    if(id==5){
		     id=0;	
		  	
//		  getWindow().getDecorView().getRootView().postInvalidate(View.EINK_AUTO_MODE_REGIONAL|View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
		
		    }
		    }
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		super.onResume();
		EPDRefresh.refreshGCOnceFlash();
		
		getLocalBookClass();
        setValue();
		showme();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
        super.onStart();
	}

	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
	
		 setContentView(R.layout.localbook_ui1);
		 listView= (PviDataList)findViewById(R.id.list);
		 list = new ArrayList<PviUiItem[]>();
		//翻页处理
		 this.showPager=true;
//		 final GlobalVar app = ((GlobalVar) getApplicationContext());        
//		 app.pbb.setPageable(this);
//		 app.pbb.setItemVisible("prevpage", true);
//		 app.pbb.setItemVisible("pagerinfo", true);
//		 app.pbb.setItemVisible("nextpage", true);
		 super.onCreate(savedInstanceState);
		
	}
    private void prevPage(){
    	try {
			
			if (currentPage == 1) {
				return ;
			}
			currentPage--;
			id++;
			getLocalBookClass();
	        setValue();
			//onResume();
		} catch (Exception e) {
			Log.e("Reader", "pre page: " + e.toString());
		}
    }
    private void nextPage(){
    	try {
			if (currentPage == totalPage) {
				return ;
			}
			currentPage++;
			id++;
			getLocalBookClass();
	        setValue();
			//onResume();
		} catch (Exception e) {
			Log.e("Reader", "next page: " + e.toString());
		}
    }
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT && listView.hasFocus()) {
		    	prevPage();
	            return true;
	        }
	        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && listView.hasFocus()) {
	        	nextPage();
	            return true;
	        }

	        return super.onKeyDown(keyCode, event);
	    }
	
	
	private OnUiItemClickListener menuclick = new OnUiItemClickListener(){

        @Override
        public void onUiItemClick(PviUiItem item) {

            String vTag = item.id;
            if (vTag.equals("bookshelf")) {
                // 我的书架
                final Intent tmpintent = new Intent(
                        MainpageActivity.START_ACTIVITY);
                Bundle sndbundle = new Bundle();
                sndbundle.putString("act",
                "com.pvi.ap.reader.activity.MyBookshelfActivity");

                tmpintent.putExtras(sndbundle);
                sendBroadcast(tmpintent);
            } else if (vTag.equals("search")) {
                LayoutInflater factory = LayoutInflater
                .from(LocalBookActivity.this);
                final View DialogView = factory.inflate(
                        R.layout.search, null);
                final EditText bookNameEditText = (EditText) DialogView
                .findViewById(R.id.keyword);
                final PviAlertDialog dialog = new PviAlertDialog(getParent());
                dialog.setTitle(getResources().getString(R.string.bookSearch));
                final TextView tv = (TextView)DialogView.findViewById(R.id.hint);
                dialog.setTitle("名称");
                dialog.setView(DialogView);
                Button search = (Button)DialogView.findViewById(R.id.searchbtn);
                // 设置自定义对话框的样式
                search.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //                      final String s_bookName = bookNameEditText
                        //                      .getText().toString();
                        //                      Intent sndintent = new Intent(
                        //                              MainpageActivity.START_ACTIVITY);
                        //                      Bundle sndbundle = new Bundle();
                        //                      sndbundle
                        //                      .putString("act",
                        //                      "com.pvi.ap.reader.activity.LocalBookListActivity");
                        //                      sndbundle.putString("TypeName", "本地书库");
                        //                      sndbundle.putString("s_bookName",
                        //                              s_bookName);
                        //                      sndintent.putExtras(sndbundle);
                        //                      sendBroadcast(sndintent);
                        dialog.dismiss();
                    }
                });
                bookNameEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        // TODO Auto-generated method stub
                        if (hasFocus) {
                            tv.setText("");
                        }
                    }

                });
                // 显示对话框
                dialog.show();
            }
               
        
        }

		};



	@Override
	public OnUiItemClickListener getMenuclick() {
		// TODO Auto-generated method stub
		return this.menuclick;
	}
	
	private void showme(){
		Intent tmpIntent = new Intent(MainpageActivity.SHOW_ME);
        Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.ResCenterActivity");//TabActivity的类名
        bundleToSend.putString("actTabName", "本地书库");
        bundleToSend.putString("sender", this.getClass().getName()); //TAB内嵌activity类的全名
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
	}

	@Override
	public void OnNextpage() {
		// TODO Auto-generated method stub
		nextPage();
	}

	@Override
	public void OnPrevpage() {
		// TODO Auto-generated method stub
		this.prevPage();
	}
}
