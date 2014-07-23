package com.tv.box.ui;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.tv.box.AppHanlder;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.model.CAInfo;

public final class CAActivity extends BaseActivity implements View.OnClickListener{

	private TextView caView ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ca_layout);
		
		setTopBarTitle(getString(R.string.title_ca));
		setLeftText(getString(R.string.title_setting));
		mLeftText.setOnClickListener(this);
		
		caView	= (TextView)findViewById(R.id.ca_text);
		
		mApi.getSystemApi().queryCA(mHandler);
	}
	
	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){
			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_QUERY_CA_SUCCESS :
					CAInfo bean = (CAInfo)msg.obj ;
					caView.setText(getString(R.string.label_ca_expire) + " : "+ bean.getValidDate());
					break ;
				case MSG_QUERY_CA_FAILED:
					caView.setText(getString(R.string.label_ca_expire));
					break ;
				}
			}
		};
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnt_left:
		case R.id.bnt_left_text :
		case R.id.bnt_left_icon :
			finish();
			break;
		}
	}
	
}
