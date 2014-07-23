package com.tv.box.ui;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import com.tv.box.model.Wifi;

public final class WifiActivity extends BaseActivity {

	TextView needBnt;
	EditText wifiName,wifiPassword ;
	Wifi wifiInfo = new Wifi() ;
	
	int needPwd ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_layout);

		setTopBarTitle(getString(R.string.title_wifi));
		setLeftText(getString(R.string.title_setting));
		mLeftText.setOnClickListener(this);
		
		final AppPreferences pf = AppPreferences.getPreferences() ;
		needPwd			= pf.getInt(AppPreferences.KEY_NEED_WIFI_PWD,AppConfig.SWITCH_OFF);
		
		wifiName	= (EditText)findViewById(R.id.wifi_name);
		wifiPassword= (EditText)findViewById(R.id.wifi_password_input);
		needBnt		= (TextView)findViewById(R.id.bnt_need_password);
		
		needBnt.setOnClickListener(this);
		
		WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);  
		WifiInfo info = mWifiManager.getConnectionInfo(); 
		
		List<ScanResult> list = mWifiManager.getScanResults() ;
		
		if(list != null && info != null){
			String name = info.getSSID() ;
			wifiName.setText(name.substring(1, name.length() - 1)) ;
		}
		
		mApi.getWifiApi().queryWifiHostPassword(mHandler);
		
		changeNeedPassword(false);
	}
	
	private void changeNeedPassword(boolean change){
		
		if(change) needPwd	= (++needPwd) % 2 ;
		
		if(needPwd == AppConfig.SWITCH_ON){
			needBnt.setBackgroundResource(R.drawable.ic_lock_sel) ;
		} else {
			needBnt.setBackgroundResource(R.drawable.ic_lock_nor) ;
		}
		
		AppPreferences.getPreferences().putInt(AppPreferences.KEY_NEED_WIFI_PWD, needPwd);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnt_left:
		case R.id.bnt_left_text :
		case R.id.bnt_left_icon :
			finish();
			break;
		case R.id.bnt_confirm :
			
			String name = wifiName.getEditableText().toString().trim() ;
			String pwd = wifiPassword.getEditableText().toString().trim() ;
			
			if(name.length() < 1 ){
				showText(getString(R.string.error_wifi_name_error));
				return ;
			}
			
			if(needPwd == AppConfig.SWITCH_OFF && pwd.length() < 8){
				showText(getString(R.string.error_wifi_lenght_error));
				return ;
			}
			
			if(wifiInfo == null){
				return ;
			}
			
			wifiInfo.setWifi_ap_name(name);
			wifiInfo.setWifi_ap_passwd(pwd);
			
			mApi.getWifiApi().modifyWifiHostPassword(wifiInfo,needPwd, mHandler);
			
			break ;
		case R.id.bnt_need_password :
			changeNeedPassword(true);
			break ;
		}
	}

	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_QUERY_WIFI_PASSWORD_SUCCESS :
					wifiInfo = (Wifi)msg.obj ;
					break ;
				case MSG_QUERY_WIFI_PASSWORD_FAILED :
					showText(getString(R.string.error_wifi_query_error));
					break ;
				case MSG_MODIFY_WIFI_PWD_SUCCESS :
					showText(getString(R.string.error_wifi_modify_sucess));
					break ;
				case MSG_MODIFY_WIFI_PWD_FAILED :
					showText(getString(R.string.error_wifi_modify_error));
					break ;
				}
			}
			
		};
	}
	
}
