package com.pvi.ap.reader.activity;
/**
 * 屏保显示类
 * @author 彭见宝
 *
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.pvi.ap.reader.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class ScreenSaveActivity extends Activity implements Runnable,ViewSwitcher.ViewFactory {
	
	private ImageSwitcher screenImage;
	
	public static int changeTime = 10000;
	
	private MyHandler changeImageHandler = new MyHandler();
	
	private int index = 0;
	
	private int fileNum = 0;
	
	File[] allpicfile = null;
	
	public static final String imagePath = "MyPic";
	

	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags
                (
                  WindowManager.LayoutParams.FLAG_FULLSCREEN,
                  WindowManager.LayoutParams.FLAG_FULLSCREEN
                );

        setContentView(R.layout.screen);
       
        screenImage = (ImageSwitcher)findViewById(R.id.screenView);
        //提供显示图片的地方 
        screenImage.setFactory(this); 
        //screenImage.setImageResource(imagelist[index]);

        
        //系统的anim中的fade_in.xml 
        screenImage.setInAnimation(AnimationUtils.loadAnimation(this, 
                android.R.anim.fade_in)); 
        //系统的anim中的fade_out.xml 
        screenImage.setOutAnimation(AnimationUtils.loadAnimation(this, 
                android.R.anim.fade_out)); 
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_UNMOUNTED)) {
			return;
		}
		File path = android.os.Environment.getExternalStorageDirectory();
		path = new File(path.getAbsolutePath() + File.separator + imagePath);
		path.mkdirs();
		allpicfile = path.listFiles();
		if(allpicfile == null || allpicfile.length == 0){
			return;
		}
		fileNum = allpicfile.length;
		new Thread(this).start();
	 }
    
  
   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
         super.onKeyDown(keyCode, event);
         finish();
         return true;
    }

   
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	finish();
    	return super.onTouchEvent(event);
    }

	@Override
	public View makeView() {
		// TODO Auto-generated method stub
		//return new ImageView(this);
		ImageView i = new ImageView(this);  
		i.setBackgroundColor(0xFF000000); 
		i.setScaleType(ImageView.ScaleType.FIT_CENTER); 
		i.setLayoutParams(new ImageSwitcher.LayoutParams(600,800));
		return i;  
	}

	@Override
	public void run() {
		
		
		while(true){
			index++;
			if(index >= fileNum){
				index = 0;
			}
			Message msg=new Message();

			Bundle b=new Bundle();

			b.putString("msg", "change");

			msg.setData(b);

			changeImageHandler.sendMessage(msg);//通过sendMessage向Handler发送更新UI的消息
			
			
			
			try {
				Thread.sleep(changeTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	class MyHandler extends Handler{//继承Handler类时，必须重写handleMessage方法

		
		public MyHandler(){
			super();
		}

		public MyHandler(Looper l){

			super(l);

		}

		public void handleMessage(Message msg) {//执行接收到的通知，此时执行的顺序是按照队列进行，即先进先出
			
			super.handleMessage(msg);

			Bundle b=msg.getData();

			String msgStr =b.getString("msg");
			
			if("change".equals(msgStr)){
				
				String filePath = allpicfile[index].getAbsolutePath();
				//screenImage.setImageURI(Uri.fromFile(new File(filePath))); 
				//screenImage.setImageResource(imagelist[index]);
				 Bitmap bm = null;
				 try {
					 //System.out.println("=========");
	                 FileInputStream fis = new FileInputStream(filePath);
	                 BufferedInputStream bis = new BufferedInputStream(fis);
	                 bm = BitmapFactory.decodeStream(bis);
				 } catch (FileNotFoundException e) {

	             }
				 BitmapDrawable bmpDraw=new BitmapDrawable(bm);
				 screenImage.setImageDrawable(bmpDraw);
				 //screenImage.set.setImageBitmap(bm);
			}
		}
	}

}