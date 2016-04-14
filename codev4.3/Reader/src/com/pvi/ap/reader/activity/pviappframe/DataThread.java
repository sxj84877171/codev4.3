package com.pvi.ap.reader.activity.pviappframe;

import android.content.Context;
import android.content.DialogInterface;

/**
 * 
 * 取数据线程：
 * 封装的特性：
 * 1、可以设置，“自动重试类型”；如果设置1，表示使用缓存，获取数据异常时，自动重试一次直接网路获取，如果仍然失败，再重试一次，仍然失败，弹出手动确认是否重试对话框
 *                       如果设置2，表示没有使用缓存，当获取数据异常时，自动重试一次
 *                       
 * 或者，提供一个接口，供调用者自己来设置重试动作
 * 
 * @author rd040
 *
 */
public class DataThread extends Thread {
    PviAlertDialog pd = null;
    Context mContext = null;
    
    OnErrorListener mOnErrorListener;
    int errorTime = 0 ; //出错次数
    
    /**
     * 最好传入getApplicationContext
     * @param mContext
     */
    public DataThread(Context mContext) {
        super();
        this.mContext = mContext;
    }
    
    /**
     * 发生数据获取异常
     */
    public void error(){
        //自动重试逻辑
        if(errorTime==0){
            ;
        }
        
        
        
        //弹出提示窗
        if(pd!=null && pd.isShowing()){
            pd.dismiss();
        }
        pd = new PviAlertDialog(mContext);
        pd.setTitle("温馨提示");
        pd.setMessage("获取数据异常，可能是网络连接问题，是否重试");
        pd.setCanClose(true);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        // TODO Auto-generated method stub
                        if(mOnErrorListener!=null){
                            mOnErrorListener.OnRetry();
                        }
                    }

                });
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
                new android.content.DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        // TODO Auto-generated method stub
                        pd.dismiss();
                        if(mOnErrorListener!=null){
                            mOnErrorListener.OnCancel();
                        }
                    }

                });
        pd.show();
        
        
    }
    
    /**
     * 设置错误监听
     * @param l
     */    
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public interface OnErrorListener{
        void OnRetry();
        void OnCancel();
    }
    
}
