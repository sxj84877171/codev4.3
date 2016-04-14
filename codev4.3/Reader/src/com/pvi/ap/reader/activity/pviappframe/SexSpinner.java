package com.pvi.ap.reader.activity.pviappframe;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.pvi.ap.reader.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;


/**
 * 个人中心性别选择器
 * @author Fly
 * @since 2011-03-15
 *
 */
public class SexSpinner extends RelativeLayout {
	
	TextView tv ;  //选取的内容显示框
	TextView allValueView1;
	TextView allValueView2;
	TextView allValueView3;
	
	LinearLayout showAllValueLayout; //下拉框主布局
	
	private String selectKey; //当前选中的选项KEY值
	
	LinkedHashMap key_value = new LinkedHashMap(); //所有选项的key-value映射
	
	
	Context context ;
	
	//boolean isOpen = false; //下拉框是否打开状态
	
	public static int ID_SHOW = 1; //tv的ID
	
	
	public static int ID_SHOWALL = 3;//showAllValueLayout的ID
	
	PopupWindow mPopupWindow; //下拉漂浮窗口，showAllValueLayout下拉框主布局的承载体
	
	public String getSelectValue() {
		if(key_value == null){
			return null;
		}else{
			return (String)key_value.get(selectKey);
		}
	}

	public void setSelectKey(String selectKey) {
		this.selectKey = selectKey;
		initSelectKey();
	}
	

	public void initSelect(){
		if("男".equals(selectKey)){
			allValueView1.requestFocus();
		}else if("女".equals(selectKey)){
			allValueView2.requestFocus();
		}else{
			allValueView3.requestFocus();
		}
	}
	
	/**
	 * 初始化选取内容界面
	 */
	public void initSelectKey(){
		if(selectKey == null){
			tv.setText("");
		}else{
			tv.setText(selectKey);
		}
		tv.invalidate();
	}
	
	public void close(){
		
		//isOpen = false;
		if(mPopupWindow!=null && mPopupWindow.isShowing()){
			mPopupWindow.dismiss();  
		}
	}
	public void open(){
		//isOpen = true;
		if(mPopupWindow == null){
			mPopupWindow = new PopupWindow(showAllValueLayout, LayoutParams.WRAP_CONTENT,  
	                  LayoutParams.WRAP_CONTENT);  
			mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mPopupWindow.setFocusable(true);
		}
		mPopupWindow.setAnimationStyle(0);
		mPopupWindow.showAsDropDown(findViewById(ID_SHOW));
		
		initSelect();
		
	}
	
	public SexSpinner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	
	}

	public SexSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

	}

	public SexSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		
		if(!isInEditMode()){

		key_value.put("男","1");
		key_value.put("女","2");
		key_value.put("保密","-1");
		
		this.context = context;
		
		//实例化下拉布局
		showAllValueLayout = new LinearLayout(context);
		showAllValueLayout.setId(ID_SHOWALL);
		android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		showAllValueLayout.setLayoutParams(params);
		showAllValueLayout.setOrientation(LinearLayout.VERTICAL);
		showAllValueLayout.setBackgroundColor(Color.BLACK);
		showAllValueLayout.setPadding(1, 1, 1, 1);
	
    
	
		
		
		allValueView1 = new TextView(context);
		android.widget.LinearLayout.LayoutParams params1 = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params1.leftMargin = 1;
		params1.topMargin = 1;
		params1.rightMargin = 1;
		params1.bottomMargin = 1;
		//allValueView1.setLayoutParams(params1);
		allValueView1.setWidth(253);
		allValueView1.setPadding(5, 0, 0, 0);
		//allValueView1.setBackgroundColor(Color.WHITE);
		allValueView1.setTextColor(Color.BLACK);
		allValueView1.setText("男");
		allValueView1.setTextSize(20);
		allValueView1.setBackgroundResource(R.drawable.selecttextview);
		
		allValueView2 = new TextView(context);
		//allValueView2.setLayoutParams(params1);
		allValueView2.setPadding(5, 0, 0, 0);
		allValueView2.setWidth(253);
		//allValueView2.setBackgroundColor(Color.WHITE);
		allValueView2.setTextColor(Color.BLACK);
		allValueView2.setText("女");
		allValueView2.setTextSize(20);
		allValueView2.setBackgroundResource(R.drawable.selecttextview);
		
		allValueView3 = new TextView(context);
		//allValueView3.setLayoutParams(params1);
		allValueView3.setPadding(5, 0, 0, 0);
		allValueView3.setWidth(253);
		//allValueView3.setBackgroundColor(Color.WHITE);
		allValueView3.setTextColor(Color.BLACK);
		allValueView3.setText("保密");
		allValueView3.setTextSize(20);
		allValueView3.setBackgroundResource(R.drawable.selecttextview);
		
		showAllValueLayout.addView(allValueView1);
		showAllValueLayout.addView(allValueView2);
		showAllValueLayout.addView(allValueView3);
		
		tv = new TextView(context);
		tv.setFocusable(true);
		tv.setId(ID_SHOW);
		tv.setPadding(5, 0, 0, 0);
		tv.setTextSize(20);
		tv.setTextColor(Color.BLACK);
		tv.setBackgroundResource(R.drawable.userinfoselect);
		addView(tv);
		tv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
				if(mPopupWindow==null || !mPopupWindow.isShowing()){
					open(); //打开下拉选项框
				}else{
					close(); //关闭下拉选项框
				}
			}
		});
		tv.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus == false){
					close(); //关闭下拉选项框
					
				}else{
					open(); //打开下拉选项框
				}
			}
		});
		
		//tv.setText(s);
		final CharSequence ss = "男";
		allValueView1.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				selectKey =ss.toString();
				tv.setText(ss);
				tv.invalidate();
				close();
				return false;
			}
		});
		allValueView1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectKey =ss.toString();
				tv.setText(ss);
				tv.invalidate();
				close();
			}
		});
		
		allValueView1.setFocusable(true);
		allValueView1.setFocusableInTouchMode(true);
		allValueView1.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
			
				if(hasFocus == true){//如果得到焦点，显示下拉框
					//System.out.println(showAllValueLayout.hasFocus());
					//open();
				}else{//如果失去焦点，影藏下拉框
					System.out.println(showAllValueLayout.hasFocus());
					
					new Thread(){
						public void run() {
							if(showAllValueLayout.hasFocus() == false){
								close();
							}
						};
					}.start();
				}
				
			}
		});
		
		
		
		//tv.setText(s);
		final CharSequence ss2 = "女";
		allValueView2.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				selectKey =ss2.toString();
				tv.setText(ss2);
				tv.invalidate();
				close();
				return false;
			}
		});
		allValueView2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectKey =ss2.toString();
				tv.setText(ss2);
				tv.invalidate();
				close();
			}
		});
		
		allValueView2.setFocusable(true);
		allValueView2.setFocusableInTouchMode(true);
		allValueView2.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
			
				if(hasFocus == true){//如果得到焦点，显示下拉框
					//System.out.println(showAllValueLayout.hasFocus());
					//open();
				}else{//如果失去焦点，影藏下拉框
					System.out.println(showAllValueLayout.hasFocus());
					
					new Thread(){
						public void run() {
							if(showAllValueLayout.hasFocus() == false){
								close();
							}
						};
					}.start();
				}
				
			}
		});
		
		
		
		//tv.setText(s);
		final CharSequence ss3 = "保密";
		allValueView3.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				selectKey =ss3.toString();
				tv.setText(ss3);
				tv.invalidate();
				close();
				return false;
			}
		});
		allValueView3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectKey =ss3.toString();
				tv.setText(ss3);
				tv.invalidate();
				close();
			}
		});
		
		allValueView3.setFocusable(true);
		allValueView3.setFocusableInTouchMode(true);
		allValueView3.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
			
				if(hasFocus == true){//如果得到焦点，显示下拉框
					//System.out.println(showAllValueLayout.hasFocus());
					//open();
				}else{//如果失去焦点，影藏下拉框
					System.out.println(showAllValueLayout.hasFocus());
					
					new Thread(){
						public void run() {
							if(showAllValueLayout.hasFocus() == false){
								close();
							}
						};
					}.start();
				}
				
			}
		});
		
		}

		
	
	}
}
