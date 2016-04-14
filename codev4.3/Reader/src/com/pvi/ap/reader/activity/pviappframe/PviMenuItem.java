/**
 * 
 */
package com.pvi.ap.reader.activity.pviappframe;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * 菜单项封装
 * 
 * @author rd040
 *
 */
public class PviMenuItem extends PviUiItem {
    
    public String tag;                 //私有操作
    private String op;                  //公共操作
    public boolean enable = true;    //为false时，应该表现为 字体变灰色；不可点击
    public View panView;          //包含该菜单项的pan的view引用

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
     * 重绘menupan
     */
    public void redrawMenupan(){
        if(this.panView!=null){
            this.panView.invalidate();
        }
    }
}
