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
 * @author rd040 ������
 * 
 *         setTimeout(n) n��������йر� setCanClose(true) �������Ͻ��йر�X ��Ĭ����
 *         setHaveProgressBar(true) ������ʾ������ setGif(R.XX) ���õ���һ������Ч����gif����
 */
public class PviAlertDialog extends AlertDialog {
	private static final String TAG = "PviAlertDialog";
	private Context mContext;
	private long timeout;
	private boolean canClose;
	private boolean haveProgressBar;
	private int progress; // ����ֵ
	private boolean indeterminate = true; // ��������ȷ��
	private int max = 100;
	private boolean backgroundTransparent; // ��ʾ���Ƿ�͸������
	private int gif;
	private int width = 402;   //�̶����˿��

	public PviAlertDialog(Context context) {		
		super(context, R.style.Theme_Dialog_PviAlert);
		this.setWinWidth(width);

		mContext = context;
		// �������Ͻ���ʾ��Ϣ
		//context.sendBroadcast(new Intent(MainpageActivity.HIDE_TIP));
	}
	
/*	   public PviAlertDialog(Context context,int theme,int skinID) {
	        super(context, R.style.Theme_Dialog_PviAlert,skinID);
	        // TODO Auto-generated constructor stub
	        mContext = context;
	        // �������Ͻ���ʾ��Ϣ
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

		// ���ӹرհ�ť
		if (isCanClose()) {
			// ����Ѿ����ڴ˰�ť�����ظ����
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
		// �޸�panel����
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
		
		// ȥ��icon
		ImageView ic = (ImageView) findViewById(android.R.id.icon);
		ic.setBackgroundResource(0);
		ic.setVisibility(View.GONE);
		
	    LinearLayout topPanel = (LinearLayout) parentPanel.getChildAt(0);
		//������ʾ�����
	    LinearLayout titleTemplate = (LinearLayout) topPanel.getChildAt(0);
	    TextView alertTitle = (TextView) titleTemplate.getChildAt(1);
		alertTitle.setTextAppearance(mContext, R.style.normal_white);
		alertTitle.setGravity(Gravity.CENTER);
	    
		// ȥ��titleDivider
		ImageView titleDivider = (ImageView) topPanel.getChildAt(1);
		titleDivider.setBackgroundResource(0);
		titleDivider.setVisibility(View.INVISIBLE);

		// ���� ��ʾ��Ϣ �� ������ɫ�����
		TextView message = (TextView) findViewById(android.R.id.message);
		if (message != null) {
			message.setTextColor(Color.BLACK);

			// message.setWidth(235);
		}
		// ���ð�ť

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

		// ���������Ҫ������
		if (isHaveProgressBar()) {
			// Logger.i(TAG,"isHaveProgressBar: true");
			RelativeLayout progressView = (RelativeLayout) getLayoutInflater()
					.inflate(R.layout.pvi_progress_dialog, null);
			FrameLayout customPanel = (FrameLayout) parentPanel.getChildAt(2);
			FrameLayout customFrame = (FrameLayout) customPanel.getChildAt(0);
			customFrame.addView(progressView);
			customPanel.setVisibility(View.VISIBLE);

			// ���ý�����
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

		// ���������gif����
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

		// ��ʱ��ʧ
		if(timeout<1){
		    timeout = 20; //δ���õĻ������ȴ�20��
		}
		
		if (timeout > 0) {
			Thread thread = new Thread() {
				public void run() {
					Timer timer = new Timer();// ��ʱ��
					TimerTask task = new TimerTask() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							dismiss();
						}
					};
					timer.schedule(task, timeout);// ��ʱDELAY
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
        //ˢ������
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        Display d = wm.getDefaultDisplay();  //Ϊ��ȡ��Ļ����        
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
        tp.setFakeBoldText(true);//�Ӵ�
		super.setCustomTitle(ct);
	}

	/**
	 * ������Ϣ�����뷽ʽ
	 */
    @Override
    public void setMessage(CharSequence message, int gravity) {
        // TODO Auto-generated method stub
        super.setMessage(message, gravity);
    }
    
    @Override
    public void onAttachedToWindow() {      
        
        //�رհ�ť����
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
     * ����ˢ�£�
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
            } /*ʲô���ã���
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
        //������뷨Ŀǰ���ˣ���ǰ�ؼ�֮�������touch�����������뷨�����ز���
        if(ev.getAction()==MotionEvent.ACTION_DOWN
        /*&& ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).isActive()  */      
        ){
            //Logger.d(TAG,"����Ա�");
            final View curFocusedView = getWindow().getDecorView().findFocus();
            
            if (curFocusedView != null) {
                
                //����������������
                
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
                        //Logger.d(TAG, "����֮");
                        PviUiUtil.hideInput(curFocusedView);
                    } else {
                        //Logger.d(TAG, "����������ڲ������������");
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
