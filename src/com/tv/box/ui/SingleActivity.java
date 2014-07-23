package com.tv.box.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tv.box.AppHanlder;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.model.SignalInfo;

public final class SingleActivity extends BaseActivity {

	TextView	proText,noiseText ;
	ProgressBar proBar ,noiseBar;
	Timer timer = new Timer() ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_layout);
		
		setTopBarTitle(getString(R.string.title_signal));
		setLeftText(getString(R.string.title_setting));
		
		proText		= (TextView)findViewById(R.id.line_2);
		noiseText	= (TextView)findViewById(R.id.line_3);
		proBar		= (ProgressBar)findViewById(R.id.progress_signal_quality);
		noiseBar 	= (ProgressBar)findViewById(R.id.progress_noise_ratio);
		proBar.setMax(100);
		noiseBar.setMax(100);
		
		mLeftText.setOnClickListener(this);
		
		// 获取信号强度,每隔3s
		timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				mApi.getSystemApi().querySignal(mHandler);
			}
		}, 0, 3000) ;
	}
	
	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this) {

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_QUERY_SIGNAL_SUCCESS :
					
					SignalInfo bean = (SignalInfo)msg.obj ;
					
					int noise 		= bean.getNoiseRatio() ;
					int strength 	= bean.getStrength() ;
					
					if(noise > 100){
						noise	= 0 ;
					}
					
					if(strength > 100){
						noise	= 0 ;
					}
					
					proText.setText(noise + "%");
					proBar.setProgress(noise);
					
					noiseText.setText(strength + "%");
					noiseBar.setProgress(strength);
					break ;
				case MSG_QUERY_SIGNAL_FAILED :
					showText(getString(R.string.error_signal));
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		if(timer != null){
			timer.cancel() ;
			timer	= null ;
		}
	}
	
}
