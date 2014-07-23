package com.tv.box.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.ui.view.MyDialogEdit;
import com.tv.box.ui.view.OnMyEditClickListener;

public class SettingActivity extends BaseActivity implements OnClickListener {

	LinearLayout wifiLayout, parentLockLayout, hostPwdLayout,
			checkVersionLayout, singleLayout, caLayout, 
			searchLayout,factoryLayout, systemLayout;
	
	int currentRid ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		
		setTopBarIcon(R.drawable.ic_setting_sel);
		setTopBarTitle(getString(R.string.title_setting));
		
		wifiLayout			= (LinearLayout)findViewById(R.id.setting_wifi_layout);
		parentLockLayout	= (LinearLayout)findViewById(R.id.setting_parent_lock_layout);
		hostPwdLayout		= (LinearLayout)findViewById(R.id.setting_host_pwd_layout);
		checkVersionLayout	= (LinearLayout)findViewById(R.id.setting_check_version_layout);
		singleLayout		= (LinearLayout)findViewById(R.id.setting_signal_layout);
		caLayout			= (LinearLayout)findViewById(R.id.setting_ca_layout);
		searchLayout		= (LinearLayout)findViewById(R.id.setting_search_layout);
		factoryLayout		= (LinearLayout)findViewById(R.id.setting_factory_layout);
		systemLayout		= (LinearLayout)findViewById(R.id.setting_system_layout);
		
		wifiLayout.setOnClickListener(this);
		parentLockLayout.setOnClickListener(this);
		hostPwdLayout.setOnClickListener(this);
		checkVersionLayout.setOnClickListener(this);
		singleLayout.setOnClickListener(this);
		caLayout.setOnClickListener(this);
		searchLayout.setOnClickListener(this);
		factoryLayout.setOnClickListener(this);
		systemLayout.setOnClickListener(this);
		
		if(AppConfig.IP.trim().equals("")){
			showMessageDialog(getString(R.string.error_network));
		}
	}

	@Override
	public void onClick(View v) {
		
		Intent intent = new Intent() ;
		MyDialogEdit dialog = null ;
		
		switch (v.getId()) {
		case R.id.bnt_left:
			finish();
			break;
		case R.id.setting_wifi_layout :
			
			dialog = new MyDialogEdit(this, getString(R.string.hint_input_wifi_password), "", 4,
					new OnMyEditClickListener() {
						@Override
						public void onMyEditClick(String content) {
							mApi.getSystemApi().verifyHostPassword(content, mHandler);
						}
			});
			
			dialog.setMaxLength(4);
			dialog.show() ;
			
			currentRid	= R.id.setting_wifi_layout ;
			
			break ;
		case R.id.setting_parent_lock_layout :
			
			dialog = new MyDialogEdit(this, getString(R.string.hint_input_host_password), "", 4,
					new OnMyEditClickListener() {
						@Override
						public void onMyEditClick(String content) {
							mApi.getSystemApi().verifyChannelPassword(content, mHandler);
						}
			});
			
			dialog.setMaxLength(4);
			dialog.show() ;
			
			currentRid	= R.id.setting_parent_lock_layout ;
			break ;
		case R.id.setting_host_pwd_layout :
			
			dialog = new MyDialogEdit(this, getString(R.string.hint_input_host_password), "", 4,
					new OnMyEditClickListener() {
						@Override
						public void onMyEditClick(String content) {
							mApi.getSystemApi().verifyHostPassword(content, mHandler);
						}
			});
			
			dialog.setMaxLength(4);
			dialog.show() ;
			
			currentRid	= R.id.setting_host_pwd_layout ;
			break ;
		case R.id.setting_check_version_layout :
			intent.setClass(SettingActivity.this, VersionCheckActivity.class);
			startActivity(intent);
			break ;
		case R.id.setting_signal_layout :
			intent.setClass(this, SingleActivity.class);
			startActivity(intent);
			break ;
		case R.id.setting_ca_layout :
			intent.setClass(this, CAActivity.class);
			startActivity(intent);
			break ;
		case R.id.setting_search_layout :
			
			dialog = new MyDialogEdit(this, getString(R.string.hint_input_host_password), "", 4,
					
					new OnMyEditClickListener() {
						@Override
						public void onMyEditClick(String content) {
							mApi.getSystemApi().verifyHostPassword(content, mHandler);
						}
			});
			
			dialog.setMaxLength(4);
			dialog.show() ;
			
			currentRid	= R.id.setting_search_layout ;
			break ;
		case R.id.setting_factory_layout :
			intent.setClass(this, FactoryActivity.class);
			startActivity(intent);
			break ;
		case R.id.setting_system_layout :
			intent.setClass(this, SystemActivity.class);
			startActivity(intent);
			break ;
		}
	}

	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				Intent intent = new Intent() ;
				switch(msg.what){
				case MSG_VERIFY_HOST_PASSWORD_SUCCESS :
					
					if(currentRid == R.id.setting_parent_lock_layout){
						intent.setClass(SettingActivity.this, ParentLockActivity.class);
						startActivity(intent);
					} else if(currentRid == R.id.setting_host_pwd_layout){
						intent.setClass(SettingActivity.this, HostPasswordActivity.class);
						startActivity(intent);
					} else if(currentRid == R.id.setting_check_version_layout){
						
					} else if(currentRid == R.id.setting_search_layout){
						intent.setClass(SettingActivity.this, SearchChannelActivity.class);
						startActivity(intent);
					} else if(currentRid == R.id.setting_wifi_layout){
						intent.setClass(SettingActivity.this, WifiActivity.class);
						startActivity(intent);
					}
					break ;
				case MSG_VERIFY_HOST_PASSWORD_FAILED :
					showText(getString(R.string.error_host_pwd)) ;
					break ;
				}
			}
			
		};
	}
	
	
}
