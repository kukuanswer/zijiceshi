package com.tv.box.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.ui.view.AppProgressDialog;

public final class FactoryActivity extends BaseActivity implements View.OnClickListener{

	EditText input ;
	AppProgressDialog dialog ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_factory_layout);
		
		setTopBarTitle(getString(R.string.title_reset_factory));
		setLeftText(getString(R.string.title_setting));
		mLeftText.setOnClickListener(this);
		
		input	= (EditText)findViewById(R.id.menu_password_input);
	}
	
	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_TASK_START :
					if(dialog == null){
						dialog = AppProgressDialog.showDialog(FactoryActivity.this, getString(R.string.label_connecting));
					}
					break ;
				case MSG_TASK_END :
					if(dialog != null){
						dialog.dismiss() ;
						dialog	= null ;
					}
					break ;
				case MSG_VERIFY_HOST_PASSWORD_SUCCESS :
					AlertDialog.Builder builder = new Builder(FactoryActivity.this);
					builder.setTitle(R.string.title_reset_factory);
					builder.setMessage(getString(R.string.label_power_off));
					builder.setPositiveButton(R.string.bnt_label_yes, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							Intent intent = new Intent(AppConfig.ACTION_STOP_ALL) ;
							sendBroadcast(intent);
							
							mApi.getSystemApi().resetFactory(mHandler);
						}
					});
					builder.setNegativeButton(R.string.bnt_label_no, null);
					builder.create().show();
					break ;
				case MSG_VERIFY_HOST_PASSWORD_FAILED :
					showText(getString(R.string.error_host_pwd));
					break ;
				case MSG_SERVER_RESPONSE_ERROR :
					showText(getString(R.string.error_server_response));
					break ;
				case MSG_RESET_FACTORY_FAILED :
					showText(getString(R.string.error_reset_factory));
					break ;
				case MSG_RESET_FACTORY_SUCCESS :
					
					// 初始socket
					Intent intent = new Intent(AppConfig.ACTION_BIND_CLIENT_SOCKET) ;
					sendBroadcast(intent);
					
					// 刷新列表
					intent = new Intent(AppConfig.ACTION_REFRESH_CHANNEL_LIST) ;
					sendBroadcast(intent);
					
					showText(getString(R.string.sucess_reset_factory));
					finish() ;
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
			
			String password = input.getText().toString().trim() ;
			
			if(password.isEmpty()){
				showText(getString(R.string.label_pwd_empty));
				return ;
			}
			
			mApi.getSystemApi().verifyHostPassword(password, mHandler);
			
			break ;
		}
	}
	
}
