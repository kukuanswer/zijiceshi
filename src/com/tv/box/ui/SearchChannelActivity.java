package com.tv.box.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.AppPreferences;
import com.tv.box.BaseActivity;
import com.tv.box.R;

public final class SearchChannelActivity extends BaseActivity {
	
	public static final String type[] 	= {"V","H"} ;
	public static final int value[] 	= {0,1} ;
	
	static int fre 		= 3676 ;
	static int sybole 	= 2300 ;// 2300
	
	private EditText frequency ,symbolRate;
	private TextView polarInput ;
	private int polar = value[1];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_layout);
		
		setTopBarTitle(getString(R.string.title_search));
		setLeftText(getString(R.string.title_setting));
		mLeftText.setOnClickListener(this);
		
		frequency	= (EditText)findViewById(R.id.frequency_input);
		symbolRate	= (EditText)findViewById(R.id.symbol_rate_input);
		polarInput	= (TextView)findViewById(R.id.polar_input);
		
		AppPreferences pf = AppPreferences.getPreferences() ;
		
		int fe	= pf.getInt(AppPreferences.KEY_SEARCH_FEQ,fre);
		int sy	= pf.getInt(AppPreferences.KEY_SEARCH_SYMB,sybole);
		polar	= pf.getInt(AppPreferences.KEY_SEARCH_POLAR,value[1]);
		
		frequency.setText("" + fe);
		symbolRate.setText("" + sy);
		
		updatePolar();
	}
	
	private void updatePolar(){
		
		if(polar == 0){
			polarInput.setText(type[0]);
		} else {
			polarInput.setText(type[1]);
		}
	}
	
	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_SEARCH_CHANNEL_SUCCESS :
					
					mApi.getSystemApi().notifyChannelLock(mHandler);
					
					showText(getString(R.string.label_search_channel_succes));
					
					AppPreferences.getPreferences().putInt(AppPreferences.KEY_CHANNLE_ID, -1);
					
					Intent intent = new Intent(AppConfig.ACTION_REFRESH_CHANNEL_LIST) ;
					sendBroadcast(intent);
					
					break ;
				case MSG_SEARCH_CHANNEL_FAILED :
					showText(getString(R.string.error_search_channel));
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
		case R.id.bnt_confirm :
			
			String fre 	= frequency.getEditableText().toString().trim() ;
			String sr 	= symbolRate.getEditableText().toString().trim() ;
			
			int freq 	= 0 ;
			int syRate 	= 0 ;
			
			try{
				freq	= Integer.valueOf(fre);
			} catch(Exception e){
				e.printStackTrace() ;
				showText(getString(R.string.error_frequency));
				return ;
			}
			
			try {
				syRate	= Integer.valueOf(sr) ;
			} catch(Exception e){
				e.printStackTrace() ;
				showText(getString(R.string.error_symbol_rate));
				return ;
			}
			
			// 停止播放
			Intent intent = new Intent(AppConfig.ACTION_SEARCH_CHANNEL) ;
			sendBroadcast(intent);
			
			AppPreferences pf = AppPreferences.getPreferences() ;
			pf.putInt(AppPreferences.KEY_SEARCH_FEQ, freq);
			pf.putInt(AppPreferences.KEY_SEARCH_SYMB, syRate);
			pf.putInt(AppPreferences.KEY_SEARCH_POLAR, polar);
			
			// 3676
			// 2300
			mApi.getSystemApi().searchProgram(freq,syRate,polar,mHandler);
			
			break ;
		case R.id.bnt_polar_left :
		case R.id.bnt_polar_right :
			polar	= (++polar) % 2 ;
			updatePolar();
			break ;
		}
	}
}
