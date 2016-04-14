package com.pvi.ap.reader.activity.pviappframe;

import com.pvi.ap.reader.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 订购流程提示界面生成器
 * @author rd040 马中庆
 *
 */
public class SubscribeViewBuilder {
    private LinearLayout layout;
    public TextView tvTop;
    public TextView tvMiddle;
    public TextView tvBottom;
    public Button bTopLeft;
    public Button bTopRight;
    public Button bMiddleLeft;
    public Button bMiddleRight;
    public Button bBottomLeft;
    public Button bBottomRight;
    
    private Context mContext;
    public SubscribeViewBuilder(Context context){
        this.mContext = context;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = (LinearLayout)inflater.inflate(R.layout.subscribeview, null);
        
        tvTop = (TextView)layout.findViewById(R.id.tvTop);
        tvMiddle = (TextView)layout.findViewById(R.id.tvMiddle);
        tvBottom = (TextView)layout.findViewById(R.id.tvBottom);
        
        bTopLeft = (Button)layout.findViewById(R.id.bTopLeft);
        bTopRight = (Button)layout.findViewById(R.id.bTopRight);
        bMiddleLeft = (Button)layout.findViewById(R.id.bMiddleLeft);
        bMiddleRight = (Button)layout.findViewById(R.id.bMiddleRight);
        bBottomLeft = (Button)layout.findViewById(R.id.bBottomLeft);
        bBottomRight = (Button)layout.findViewById(R.id.bBottomRight);        
        
    }
    
    public View getView(){        
        return layout;
    }
}
