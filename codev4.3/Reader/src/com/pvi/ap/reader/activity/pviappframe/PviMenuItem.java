/**
 * 
 */
package com.pvi.ap.reader.activity.pviappframe;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * �˵����װ
 * 
 * @author rd040
 *
 */
public class PviMenuItem extends PviUiItem {
    
    public String tag;                 //˽�в���
    private String op;                  //��������
    public boolean enable = true;    //Ϊfalseʱ��Ӧ�ñ���Ϊ ������ɫ�����ɵ��
    public View panView;          //�����ò˵����pan��view����

    /**
     * @param id
     * @param res
     * @param left
     * @param top
     * @param width
     * @param height
     * @param text
     * @param isFocus
     * @param isVisible
     * @param l
     */
    
    
    
    public PviMenuItem(String id, int res, float left, float top, float width,
            float height, String text, boolean isFocus, boolean isVisible,
            OnClickListener l) {
        super(id, res, left, top, width, height, text, isFocus, isVisible, l);
        // TODO Auto-generated constructor stub
    }

    
    /**
     * @return the op
     */
    public String getOp() {
        return op;
    }

    /**
     * @param op the op to set
     */
    public void setOp(String op) {
        this.op = op;
    }
    
    /**
     * �ػ�menupan
     */
    public void redrawMenupan(){
        if(this.panView!=null){
            this.panView.invalidate();
        }
    }
}
