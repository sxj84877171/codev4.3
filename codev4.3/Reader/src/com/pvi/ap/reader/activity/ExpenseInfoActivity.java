package com.pvi.ap.reader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;

/**
 * 查看消费详细信息
 * @author Fly
 * 
 */

public class ExpenseInfoActivity extends PviActivity implements OnClickListener {
    private static final String TAG = "ExpenseInfoActivity";
    private static int skinID = 1;//皮肤ID
    private TextView descriptionView;
    private TextView typeView;
    private TextView dateView;
    private TextView priceView;
    private Button retButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	
    	
    	setContentView(R.layout.expenseinfo_ui1);
    	
    	super.onCreate(savedInstanceState);
    	
    	initControls();
    	bindEvent();
    }
    
    public void initControls(){
    	super.initControls();
    	descriptionView = (TextView)findViewById(R.id.description);
    	typeView = (TextView)findViewById(R.id.type);
    	dateView = (TextView)findViewById(R.id.date);
    	priceView = (TextView)findViewById(R.id.price);
    	retButton = (Button)findViewById(R.id.returnB);
    }
    
    public void bindEvent(){
    	super.bindEvent();
    
    	retButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(R.id.returnB == v.getId()){
			
			sendBroadcast(new Intent(MainpageActivity.BACK));
			
		}
		
	}
	
	public void showMe(){
		
		
		Intent tmpIntent = new Intent(
                MainpageActivity.SHOW_ME);
		Bundle bundleToSend = new Bundle();
        bundleToSend.putString("act", "com.pvi.ap.reader.activity.UserCenterActivity");
        bundleToSend.putString("actTabName", "消费详细信息");
        bundleToSend.putString("sender", ExpenseInfoActivity.this.getClass().getName());
        tmpIntent.putExtras(bundleToSend);
        sendBroadcast(tmpIntent);
        tmpIntent = null;
        bundleToSend = null;
    
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Bundle bd = getIntent().getExtras();
		descriptionView.setText(bd.getString("description"));
		typeView.setText(bd.getString("type"));
		dateView.setText(bd.getString("date"));
		priceView.setText(bd.getString("price"));
		showMe();
	}
  
}
