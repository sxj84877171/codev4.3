package com.pvi.ap.reader.activity.pviappframe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.content.Context;
import android.text.TextPaint;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.GlobalVar;
import com.pvi.ap.reader.data.common.EPDRefresh;
import com.pvi.ap.reader.data.common.Logger;


/**
 * @author rd040 马中庆
 * 
 *         setTimeout(n) n毫秒后自行关闭 setCanClose(true) 设置右上角有关闭X ，默认无
 *         setHaveProgressBar(true) 设置显示进度条 setGif(R.XX) 设置弹出一个过渡效果的gif动画
 */
public class PviAlertDialog extends AlertDialog {
	private static final String TAG = "PviAlertDialog";
	private Context mContext;
	private long timeout;
	private boolean canClose;
	private boolean haveProgressBar;
	private int progress; // 进度值
	private boolean indeterminate = true; // 进度条不确定
	private int max = 100;
	private boolean backgroundTransparent; // 提示框是否透明背景
	private int gif;
	private int width = 402;   //固定死了宽度

	public PviAlertDialog(Context context) {		
		super(context, R.style.Theme_Dialog_PviAlert);
		this.setWinWidth(width);

		mContext = context;
		// 隐藏左上角提示信息
		//context.sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	}
	
/*	   public PviAlertDialog(Context context,int theme,int skinID) {
	        super(context, R.style.Theme_Dialog_PviAlert,skinID);
	        // TODO Auto-generated constructor stub
	        mContext = context;
	        // 隐藏左上角提示信息
	        context.sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	    }*/
	
	 
	/**@param width the width of the dialog
	 *  add by kizan to modify the dialog width
	 */
	public void setDialogWidth(int width){
		// TODO Auto-generated method stub
		this.width = width;
	}
	
	
	
/*	//@Override
	public void on1WindowFocusChanged(boolean hasFocus) {
			
		// TODO Auto-generated method stub
		FrameLayout fl = (FrameLayout) ((ViewGroup) getWindow().getDecorView())
				.getChildAt(0);
		
		// modified by kizan for bug 205 set the width
 
		FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(width,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		fl.setLayoutParams(lp1);

		// 增加关闭按钮
		if (isCanClose()) {
			// 如果已经存在此按钮，则不重复添加
			if (fl.findViewWithTag("PviAlertDialogCloseBtn") == null) {

				ImageView iv = new ImageView(mContext);
				iv.setTag("PviAlertDialogCloseBtn");
				iv.setBackgroundResource(R.drawable.bg_button_close_dialog);
				iv.setScaleType(ScaleType.CENTER);
				iv.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});

				RelativeLayout ll = new RelativeLayout(mContext);
				RelativeLayout.LayoutParams rll = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				rll.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				rll.setMargins(10, 8, 10, 0);
				ll.addView(iv, rll);
				// add by kizanliu for bug 205
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.FILL_PARENT,
						FrameLayout.LayoutParams.WRAP_CONTENT);
				fl.addView(ll, lp);
			}
		}

		LinearLayout parentPanel = (LinearLayout) fl.getChildAt(0);
		// 修改panel背景
		int childPanelCount = parentPanel.getChildCount();
		for (int i = 0; i < childPanelCount; i++) {
			parentPanel.getChildAt(i).setBackgroundResource(0);
		}
		if (isBackgroundTransparent()) {
			parentPanel.setBackgroundResource(0);
		} else {
			parentPanel.setBackgroundResource(R.drawable.bg_popwin_ui1);
		}
		parentPanel.setPadding(0, 0, 30, 0);
		
		// 去掉icon
		ImageView ic = (ImageView) findViewById(android.R.id.icon);
		ic.setBackgroundResource(0);
		ic.setVisibility(View.GONE);
		
	    LinearLayout topPanel = (LinearLayout) parentPanel.getChildAt(0);
		//设置提示框标题
	    LinearLayout titleTemplate = (LinearLayout) topPanel.getChildAt(0);
	    TextView alertTitle = (TextView) titleTemplate.getChildAt(1);
		alertTitle.setTextAppearance(mContext, R.style.normal_white);
		alertTitle.setGravity(Gravity.CENTER);
	    
		// 去掉titleDivider
		ImageView titleDivider = (ImageView) topPanel.getChildAt(1);
		titleDivider.setBackgroundResource(0);
		titleDivider.setVisibility(View.INVISIBLE);

		// 设置 提示信息 的 文字颜色及宽度
		TextView message = (TextView) findViewById(android.R.id.message);
		if (message != null) {
			message.setTextColor(Color.BLACK);

			// message.setWidth(235);
		}
		// 设置按钮

		Button button1 = (Button) findViewById(android.R.id.button1);
		Button button2 = (Button) findViewById(android.R.id.button2);
		Button button3 = (Button) findViewById(android.R.id.button3);

		button1.setBackgroundResource(R.drawable.booksummarybtn);
		button2.setBackgroundResource(R.drawable.booksummarybtn);
		button3.setBackgroundResource(R.drawable.booksummarybtn);

		button1.setTextAppearance(mContext, R.style.normal_black_common);
		button2.setTextAppearance(mContext, R.style.normal_black_common);
		button3.setTextAppearance(mContext, R.style.normal_black_common);
		
		button1.setGravity(Gravity.CENTER);
		button2.setGravity(Gravity.CENTER);
		button3.setGravity(Gravity.CENTER);
		
		button1.setPadding(20,0,20,0);
		button2.setPadding(20,0,20,0);
		button3.setPadding(20,0,20,0);
		
		button1.setVisibility(View.VISIBLE);
		
		LinearLayout buttonPanel = (LinearLayout) parentPanel.getChildAt(3);
		LinearLayout.LayoutParams bpLp = (LinearLayout.LayoutParams) buttonPanel
				.getLayoutParams();
		bpLp.width = 350;// LinearLayout.LayoutParams.WRAP_CONTENT;
		buttonPanel.setLayoutParams(bpLp);

		// 如果设置需要进度条
		if (isHaveProgressBar()) {
			// Logger.i(TAG,"isHaveProgressBar: true");
			RelativeLayout progressView = (RelativeLayout) getLayoutInflater()
					.inflate(R.layout.pvi_progress_dialog, null);
			FrameLayout customPanel = (FrameLayout) parentPanel.getChildAt(2);
			FrameLayout customFrame = (FrameLayout) customPanel.getChildAt(0);
			customFrame.addView(progressView);
			customPanel.setVisibility(View.VISIBLE);

			// 设置进度条
			ProgressBar pb = (ProgressBar) progressView
					.findViewById(R.id.progress);
			if (isIndeterminate()) {
				pb.setIndeterminate(indeterminate);
			} else {
				pb.setProgress(progress);
				pb.setMax(max);
				TextView tvProgress = (TextView) progressView
						.findViewById(R.id.progress_number);
				tvProgress.setText("" + progress);
				TextView tvProgressPercent = (TextView) progressView
						.findViewById(R.id.progress_percent);
				tvProgressPercent.setText("" + 100 * progress / pb.getMax()
						+ "%");
			}
		} else {

			// Logger.i(TAG,"isHaveProgressBar: false");
		}

		// 如果设置了gif动画
		if (gif > 0) {
			FrameLayout customPanel = (FrameLayout) parentPanel.getChildAt(2);
			GifView gv = new GifView(mContext);
			gv.setImageResource(gif);
			LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(
					270, 200);
			param1.gravity = Gravity.CENTER;
			gv.setLayoutParams(param1);
			customPanel.addView(gv, param1);
			customPanel.setVisibility(View.VISIBLE);
		}

		// 延时消失
		if(timeout<1){
		    timeout = 20; //未设置的话，最多等待20秒
		}
		
		if (timeout > 0) {
			Thread thread = new Thread() {
				public void run() {
					Timer timer = new Timer();// 计时器
					TimerTask task = new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							dismiss();
						}
					};
					timer.schedule(task, timeout);// 延时DELAY
				}
			};
			thread.start();
		}

		super.onWindowFocusChanged(hasFocus);
	}*/

	/**
	 * @param timeout
	 *            the timeout to set
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the canClose
	 */
	public boolean isCanClose() {
		return canClose;
	}

	/**
	 * @param canClose
	 *            the canClose to set
	 */
	public void setCanClose(boolean canClose) {	    
		this.canClose = canClose;
	}

	public void setHaveProgressBar(boolean haveProcessingAni) {
		this.haveProgressBar = haveProcessingAni;
	}

	public boolean isHaveProgressBar() {
		return haveProgressBar;
	}

	public void setProgress(int progress) {
		try {
			ProgressBar pb = (ProgressBar) findViewById(R.id.progress);
			pb.setProgress(progress);
			pb.setMax(max);
			TextView tvProgress = (TextView) findViewById(R.id.progress_number);
			tvProgress.setText("" + progress);
			TextView tvProgressPercent = (TextView) findViewById(R.id.progress_percent);
			tvProgressPercent.setText("" + 100 * progress / pb.getMax() + "%");
		} catch (Exception e) {
			;
		}
		this.progress = progress;
	}

	public int getProgress() {
		return progress;
	}

	/**
	 * @return the indeterminate
	 */
	public boolean isIndeterminate() {
		return indeterminate;
	}

	/**
	 * @param indeterminate
	 *            the indeterminate to set
	 */
	public void setIndeterminate(boolean indeterminate) {
		this.indeterminate = indeterminate;
	}

	public void setBackgroundTransparent(boolean backgroundTransparent) {
		this.backgroundTransparent = backgroundTransparent;
	}

	public boolean isBackgroundTransparent() {
		return backgroundTransparent;
	}

	public void setGif(int gif) {
		this.gif = gif;
	}

	public int getGif() {
		return gif;
	}


    //@Override
    public void on2WindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        
        View v = this.getWindow().getDecorView();
        //刷屏设置
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();  //为获取屏幕宽、高        
        Logger.i(TAG,"x:"+(d.getWidth()-v.getWidth())/2+",y:"+(d.getHeight()-v.getHeight())/2+",width:"+v.getWidth()+",height:"+v.getHeight());
        EPDRefresh.refreshA2Dither();
        
       //open /proc/whiteRect and write it
        Logger.i(TAG,"+++++ open /proc/whiteRect and write it");
        File file = new File("/proc/whiteRect");
        
        try {
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write("lurd="+Integer.toHexString((d.getWidth()-v.getWidth())/2)+","+Integer.toHexString((d.getHeight()-v.getHeight())/2)+","+Integer.toHexString(v.getWidth())+","+Integer.toHexString(v.getHeight())+",");
            osw.flush();
            fos.flush();
            osw.close();
            fos.close();
            Logger.i(TAG,"---- open /proc/whiteRect and write it");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    

	@Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		TextView ct = new TextView(mContext);
		ct.setGravity(Gravity.CENTER);
		ct.setText(title);
		ct.setTextAppearance(mContext, R.style.bold_black_big);
		TextPaint tp = ct.getPaint();
        tp.setFakeBoldText(true);//加粗
		super.setCustomTitle(ct);
	}

	/**
	 * 设置消息，对齐方式
	 */
    @Override
    public void setMessage(CharSequence message, int gravity) {
        // TODO Auto-generated method stub
        super.setMessage(message, gravity);
    }
    
    @Override
    public void onAttachedToWindow() {      
        
        //关闭按钮处理
        if(this.canClose){
            ImageButton ibClose = (ImageButton) findViewById(R.id.ibClose);
            if(ibClose!=null){
            ibClose.setVisibility(View.VISIBLE);
            ibClose.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    dismiss();
                }});
            }
        }        
        
        setScreenUpdate();
/*        if(firstAttached){
            getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
            firstAttached = false;
        }*/
        super.onAttachedToWindow();    
   
    }

    @Override
    public void onDetachedFromWindow() {        
        setScreenUpdate();

        super.onDetachedFromWindow();
    }
    
    
    /**
     * 设置刷新！
     */
    private void setScreenUpdate(){
        final GlobalVar appState = ((GlobalVar) mContext.getApplicationContext());
        if(appState.deviceType==1){
            
            final View alertDialogWindowView = getWindow().getDecorView();
            
            if(alertDialogWindowView.getWidth()==0){
                alertDialogWindowView.measure(0, 0);
            }          
            
            final int alertDialogWindowViewWidth = alertDialogWindowView.getMeasuredWidth();
            final int alertDialogWindowViewHeight = alertDialogWindowView.getMeasuredHeight();
            
            final View v = ((Activity) mContext).getWindow().getDecorView().getRootView();
            
            //final int screenWidth = 600;
            final int screenHeight = 800;
            
            final int parentWidth=v.getWidth();
            final int parentHeight = v.getHeight();
            final GlobalVar app = (GlobalVar) mContext.getApplicationContext();
            if (app.deviceType == 1) {
                
                final int top = (screenHeight - alertDialogWindowViewHeight) / 2
                - (screenHeight - parentHeight);
                
//                v.invalidate((parentWidth - alertDialogWindowViewWidth) / 2,
//                        top,
//                        (parentWidth - alertDialogWindowViewWidth) / 2
//                                + alertDialogWindowViewWidth,
//                        top+ alertDialogWindowViewHeight,
//                        View.EINK_WAIT_MODE_WAIT | View.EINK_WAVEFORM_MODE_GC16
//                                | View.EINK_UPDATE_MODE_FULL);
            } /*什么作用？？
            else {
                v.invalidate((parentWidth - alertDialogWindowViewWidth) / 2,
                        (screenHeight - alertDialogWindowViewHeight) / 2
                                - (screenHeight - parentHeight),
                        (parentWidth - alertDialogWindowViewWidth) / 2
                                + alertDialogWindowViewWidth,
                        (screenHeight - alertDialogWindowViewHeight) / 2
                                - (screenHeight - parentHeight)
                                + alertDialogWindowViewHeight);
            }*/
            
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //如果输入法目前打开了，则当前控件之外区域的touch，将触发输入法的隐藏操作
        if(ev.getAction()==MotionEvent.ACTION_DOWN
        /*&& ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).isActive()  */      
        ){
            //Logger.d(TAG,"坐标对比");
            final View curFocusedView = getWindow().getDecorView().findFocus();
            
            if (curFocusedView != null) {
                
                //如果是在输入框上面
                
                if(curFocusedView instanceof EditText){
                    int[] location = { 0, 0 };
                    curFocusedView.getLocationOnScreen(location);
                    //Logger.d(TAG,"curFocusedView:"+curFocusedView.toString()+",id:"+curFocusedView.getId()+"; loc x:"+location[0]+" ,y:"+location[1]);
                    final int touchX = (int) ev.getRawX();
                    final int touchY = (int) ev.getRawY();
                    //Logger.d(TAG,"touch x:"+touchX+" ,y:"+touchY);
                    if (!(touchX > location[0] && touchX < location[0]
                            + curFocusedView.getWidth()
                            && touchY > location[1] && touchY < location[1]
                                    + curFocusedView.getHeight())) {
                        //Logger.d(TAG, "隐藏之");
                        PviUiUtil.hideInput(curFocusedView);
                    } else {
                        //Logger.d(TAG, "仍在输入框内部点击，不隐藏");
                    }
                } 
                
                
            }else{
                //Logger.d(TAG, "no focused view");
            }
            
        }
        

        return super.dispatchTouchEvent(ev);


    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_HOME){
            dismiss();
            PviUiUtil.toHomePage(mContext);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
