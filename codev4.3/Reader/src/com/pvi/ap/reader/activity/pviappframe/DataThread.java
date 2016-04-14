package com.pvi.ap.reader.activity.pviappframe;

import android.content.Context;
import android.content.DialogInterface;

/**
 * 
 * ȡ�����̣߳�
 * ��װ�����ԣ�
 * 1���������ã����Զ��������͡����������1����ʾʹ�û��棬��ȡ�����쳣ʱ���Զ�����һ��ֱ����·��ȡ�������Ȼʧ�ܣ�������һ�Σ���Ȼʧ�ܣ������ֶ�ȷ���Ƿ����ԶԻ���
 *                       �������2����ʾû��ʹ�û��棬����ȡ�����쳣ʱ���Զ�����һ��
 *                       
 * ���ߣ��ṩһ���ӿڣ����������Լ����������Զ���
 * 
 * @author rd040
 *
 */
public class DataThread extends Thread {
    PviAlertDialog pd = null;
    Context mContext = null;
    
    OnErrorListener mOnErrorListener;
    int errorTime = 0 ; //�������
    
    /**
     * ��ô���getApplicationContext
     * @param mContext
     */
    public DataThread(Context mContext) {
        super();
        this.mContext = mContext;
    }
    
    /**
     * �������ݻ�ȡ�쳣
     */
    public void error(){
        //�Զ������߼�
        if(errorTime==0){
            ;
        }
        
        
        
        //������ʾ��
        if(pd!=null && pd.isShowing()){
            pd.dismiss();
        }
        pd = new PviAlertDialog(mContext);
        pd.setTitle("��ܰ��ʾ");
        pd.setMessage("��ȡ�����쳣�������������������⣬�Ƿ�����");
        pd.setCanClose(true);
        pd.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
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
        pd.setButton(DialogInterface.BUTTON_NEUTRAL, "ȡ��",
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
     * ���ô������
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
