package com.tv.box.ui;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.tv.box.AppHanlder;
import com.tv.box.BaseActivity;
import com.tv.box.R;

public final class HostPasswordActivity extends BaseActivity{

	EditText oldEdit ,newEdit, channelEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_host_pwd_layout);
		
		setTopBarTitle(getString(R.string.title_menu_lock));
		setLeftText(getString(R.string.title_setting));
		
		oldEdit		= (EditText)findViewById(R.id.old_pwd_input);
		newEdit		= (EditText)findViewById(R.id.old_pwd_type_1_input);
		channelEdit	= (EditText)findViewById(R.id.old_pwd_type_2_input);
		
		mLeftText.setOnClickListener(this);
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnt_left:
		case R.id.bnt_left_text :
		case R.id.bnt_left_icon :
			finish();
			break;
		case R.id.bnt_confirm :
			
			String opwd = oldEdit.getEditableText().toString().trim() ;
			String npwd = newEdit.getEditableText().toString().trim() ;
			String cpwd = channelEdit.getEditableText().toString().trim() ;
			
			if(opwd.isEmpty()){
				showText(getString(R.string.error_old_pwd));
				return ;
			}
			
			if(npwd.isEmpty()){
				showText(getString(R.string.error_new_pwd));
				return ;
			}
			
			if(npwd.isEmpty()){
				showText(getString(R.string.error_channel_pwd));
				return ;
			}
			
			mApi.getSystemApi().saveHostPassword(opwd, npwd,cpwd, mHandler);
			break ;
		}
	}

	@Override
	protected AppHanlder getHandler() {
		return new AppHanlder(this){

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_TASK_START :
					
					break ;
				case MSG_TASK_END :
					
					break ;
				case MSG_MODIFY_HOST_PASSWORD_SUCCESS :
					showText(getString(R.string.label_modify_host_pwd_sucess));
					finish() ;
					break ;
				case MSG_MODIFY_HOST_PASSWORD_FAILED :
					showText(getString(R.string.label_modify_host_pwd_failed));
					break ;
				}
			}
			
		};
	}
}
