package com.pvi.ap.reader.activity.pviappframe;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 下拉框组件
 * @author Fly
 * @since 2011-03-15
 *
 */
@SuppressWarnings("unused")
public class SelectSpinner extends RelativeLayout {
	
//	public final static int UPDATEMODE_5 = View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL;
	
	TextView tv ;  //选取的内容显示框
	
	int deviceType = 2;
	
	TextView downImageView; //下拉按钮
	
	LinearLayout showAllValueLayout; //下拉框主布局
	
	TextView[] allValueView; //下拉框所有选项
	
	private String selectKey; //当前选中的选项KEY值
	
	LinkedHashMap key_value; //所有选项的key-value映射
	
	int width; //要显示的宽度
	
	int textSize; //显示字体
	
	String gravity; //对齐方式
	
	Context context ;
	
	//boolean isOpen = false; //下拉框是否打开状态
	
	public static int ID = 99; //tv的ID
	
	public static int ID_SHOW = 100; //tv的ID
	
	public static int ID_CLICK = 200;//downImageView的ID
	
	public static int ID_SHOWALL = 300;//showAllValueLayout的ID
	
	PopupWindow mPopupWindow; //下拉漂浮窗口，showAllValueLayout下拉框主布局的承载体
	
	public String getSelectValue() {
		if(key_value == null){
			return null;
		}else{
			return key_value.get(selectKey).toString();
		}
	}

	public void setSelectKey(String selectKey) {
		this.selectKey = selectKey;
		initSelectKey();
	}
	
	public void setKey_value(LinkedHashMap key_value) {
		this.key_value = key_value;
		initDropPopWindow();
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
	
	/**
	 * 初始化下拉界面
	 */
	public void initDropPopWindow(){
		//初始化选取显示内容
		showAllValueLayout.removeAllViews(); //先清除所有子布局
		if(key_value == null){
			return;
		}else{
			int size = key_value.size();
			Set key = key_value.keySet();
			allValueView = new TextView[size];
			//String[] keyArray = (String[])key.toArray();
			Iterator it = key.iterator();
			int i=0;
			while(it.hasNext()){
				String s = (String)it.next();
			//for(int i=0;i<keyArray.length;i++){
				allValueView[i] = new TextView(context);
				if("center".equals(gravity)){
					allValueView[i].setGravity(Gravity.CENTER);
				}
				android.widget.LinearLayout.LayoutParams params1 = new android.widget.LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				allValueView[i].setLayoutParams(params1);
				//allValueView[i].setWidth(width-2);
				//allValueView[i].setBackgroundColor(Color.WHITE);
				allValueView[i].setTextColor(Color.BLACK);
				allValueView[i].setText(s);
				allValueView[i].setTextSize(20);
				//allValueView[i].setPadding(3, 0, 0, 0);
				allValueView[i].setBackgroundResource(R.drawable.selecttextview);
				
				if(deviceType == 1){
//					allValueView[i].setUpdateMode(View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_PARTIAL);
				}
				
				final CharSequence ss = s;
				final int t = i;
				
				allValueView[i].setOnKeyListener(new SpinnerOnKeyListener());
				 
				allValueView[i].setOnTouchListener(new OnTouchListener() {
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
				allValueView[i].setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						selectKey =ss.toString();
						tv.setText(ss);
						tv.invalidate();
						close();
					}
				});
				
				allValueView[i].setFocusable(true);
				allValueView[i].setFocusableInTouchMode(true);
				
				/*
				allValueView[i].setOnFocusChangeListener(new OnFocusChangeListener() {
					
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
				});*/
				android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
				showAllValueLayout.addView(allValueView[i],params);
				i++;
			}
			
		}
		showAllValueLayout.invalidate();
	}
	
	public void close(){
		//if(isOpen == true){
		//	isOpen = false;
			if(mPopupWindow!=null && mPopupWindow.isShowing()){
				mPopupWindow.dismiss(); 
			}
		//}
	}
	
	public void open(){
		//if(isOpen == false){
		//	isOpen = true;
			if(mPopupWindow == null){
				mPopupWindow = new PopupWindow(showAllValueLayout, LayoutParams.WRAP_CONTENT,  
		                  LayoutParams.WRAP_CONTENT); 
				mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
				mPopupWindow.setFocusable(true);
	
			}
		//}
		mPopupWindow.setAnimationStyle(0);
		mPopupWindow.showAsDropDown(tv);
		
		initSelect();
		
	
		//mPopupWindow.showAtLocation(SelectSpinner.this,
		//		Gravity.TOP| Gravity.LEFT, this.getLeft(), this.getTop()+82);//底部工具栏高度   
		
		
	}
	
	public SelectSpinner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	
	}

	public SelectSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

	}
	
    public SelectSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        this.context = context;
        if (!isInEditMode()) {

            // 实例化下拉布局
            showAllValueLayout = new LinearLayout(context);

            final GlobalVar appState = ((GlobalVar) context
                    .getApplicationContext());
            deviceType = appState.deviceType;
            if (deviceType == 1) {
//                showAllValueLayout.setUpdateMode(UPDATEMODE_5);
            }

            showAllValueLayout.setId(ID_SHOWALL);
            android.widget.LinearLayout.LayoutParams params = new android.widget.LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            // params.width = 97;
            showAllValueLayout.setLayoutParams(params);
            showAllValueLayout.setOrientation(LinearLayout.VERTICAL);
            // showAllValueLayout.setBackgroundResource(R.drawable.btn_bg_2);
            // showAllValueLayout.setPadding(1, 1, 1, 1);

            // 获取属性
            TypedArray ta = context.obtainStyledAttributes(attrs,
                    R.styleable.SelectSpinner);
            width = ta.getInt(R.styleable.SelectSpinner_width, 100);
            textSize = ta.getInt(R.styleable.SelectSpinner_textSize, 20);
            gravity = ta.getString(R.styleable.SelectSpinner_gravity);
            tv = new TextView(context);
            tv.setId(ID_SHOW);
            tv.setGravity(Gravity.CENTER);
            tv.setWidth(width);
            tv.setHeight(32);
            tv.setTextSize(textSize);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundResource(R.drawable.showselectunfs);
            addView(tv);

            downImageView = new TextView(context);
            downImageView.setWidth(40);
            downImageView.setHeight(32);
            downImageView.setTextColor(Color.BLACK);
            downImageView.setBackgroundResource(R.drawable.selectunfs);

            downImageView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (mPopupWindow == null || !mPopupWindow.isShowing()) {
                        open();
                        // allValueView[0].requestFocus();
                        // downImageView.requestFocus();
                        return false;

                    } else {
                        close(); // 关闭下拉选项框
                        return false;
                    }
                }
            });

            downImageView.setFocusable(true);
            // downImageView.setFocusableInTouchMode(true);
            downImageView.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    // TODO Auto-generated method stub

                    if (hasFocus == true) {// 如果得到焦点，显示下拉框
                        open();// 打开下拉框
                        tv.setBackgroundResource(R.drawable.showselect);
                        downImageView.setBackgroundResource(R.drawable.select);
                    } else {// 如果失去焦点，影藏下拉框
                        close();
                        tv.setBackgroundResource(R.drawable.showselectunfs);
                        downImageView
                                .setBackgroundResource(R.drawable.selectunfs);
                    }

                }
            });

            RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            lp1.addRule(RelativeLayout.RIGHT_OF, ID_SHOW);
            addView(downImageView, lp1); // 把点击下拉按钮布局在显示框正右边
        }
    }
	
	public void initSelect(){
		int size = key_value.size();
		Set key = key_value.keySet();
		Iterator it = key.iterator();
		int i=0;
		while(it.hasNext()){
			String s = (String)it.next();
			if(s.equals(selectKey)){
				allValueView[i].requestFocus();
			}
			i++;
		}
	}
	
	class SpinnerOnKeyListener implements OnKeyListener{
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(event.ACTION_DOWN == event.getAction() && deviceType == 1){
//				if(showAllValueLayout != null&&GlobalVar.deviceType==1){
//					showAllValueLayout.invalidate(View.EINK_WAIT_MODE_WAIT |
//	                        View.EINK_WAVEFORM_MODE_GC16 | 
//	                        View.EINK_UPDATE_MODE_PARTIAL);
//				}
				return false;
			}
			return false;
		}
		
	}
	

}

