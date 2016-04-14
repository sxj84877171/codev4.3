/**
 * 
 */
package com.pvi.ap.reader.activity.pviappframe;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.pvi.ap.reader.activity.GlobalVar;

/**
 * @author rd040
 *
 */
public class PviPopupWindow extends PopupWindow {
    
    private static final String TAG = "PviPopupWindow";
    private int deviceType = 2;  //运行设备类型
    private View parentView;
    private int locX = 0;
    private int locY = 0;

    /**
     * 
     */
    public PviPopupWindow() {
        // TODO Auto-generated constructor stub
    }



    /**
     * @param contentView
     */
    public PviPopupWindow(View contentView) {
        super(contentView);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param width
     * @param height
     */
    public PviPopupWindow(int width, int height) {
        super(width, height);
        // TODO Auto-generated constructor stub
        
    }

    /**
     * @param context
     * @param attrs
     * @param defStyle
     */
    public PviPopupWindow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    /**
     * @param contentView
     * @param width
     * @param height
     */
    public PviPopupWindow(View contentView, int width, int height) {
        super(contentView, width, height);
        // TODO Auto-generated constructor stub   
        final GlobalVar appState = ((GlobalVar) contentView.getContext().getApplicationContext());
        deviceType = appState.deviceType;
    }

    /**
     * @param contentView
     * @param width
     * @param height
     * @param focusable
     */
    public PviPopupWindow(View contentView, int width, int height,
            boolean focusable) {
        super(contentView, width, height, focusable);
        // TODO Auto-generated constructor stub
    }

     @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
         //Logger.d(TAG,"x:"+x+",y:"+y);

        //设置刷新模式
        if(deviceType==1){
            //final View cv = getContentView();
            parentView = parent;
            locX = x;
            locY = y-50;//2011-6-28 底部栏封装到框架里了，没有用框架的底栏的咋办？
            /*parentView.invalidate(locX,parentView.getHeight()-(locY+cv.getHeight())
                    ,locX+cv.getWidth(),parentView.getHeight()-locY
                    ,View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);*/
        }
        
        super.showAtLocation(parent, gravity, x, y);      
    }

    @Override
    public void dismiss() {
        
        if(this.isShowing()){
            final View w =  (View)getContentView().getParent();
            if(w!=null){
                if(GlobalVar.deviceType==1){
//                    w.invalidate(View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
                }
            }
        }
        
        
/*        final View cv = getContentView();        
        final int cvWidth = cv.getWidth();
        final int cvHeight = cv.getHeight();*/
        
        super.dismiss();
        //设置刷新模式
/*        if(deviceType==1){            
            if(parentView!=null){
                parentView.invalidate(locX,parentView.getHeight()-(locY+cvHeight)
                    ,locX+cvWidth,parentView.getHeight()-locY
                    ,View.EINK_WAIT_MODE_WAIT| View.EINK_WAVEFORM_MODE_GC16 | View.EINK_UPDATE_MODE_FULL);
            }
        }*/
    }

}
