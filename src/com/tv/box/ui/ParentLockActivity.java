package com.tv.box.ui;

import android.os.Bundle;
import android.os.Message;
import android.view.View;

import com.tv.box.AppHanlder;
import com.tv.box.BaseActivity;
import com.tv.box.R;
import com.tv.box.ui.view.AppProgressDialog;
import com.tv.box.ui.view.ChannelSelectedListView;

public final class ParentLockActivity extends BaseActivity {

	ChannelSelectedListView mListView ;
	AppProgressDialog dialog ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parent_lock_layout);
		
		setTopBarTitle(getString(R.string.title_parent_lock));
		setLeftText(getString(R.string.title_setting));
		
		mListView		= (ChannelSelectedListView)findViewById(R.id.list_view) ;
		mLeftText.setOnClickListener(this);
		
		mListView.loadChannel(mApi.getSystemApi().getChannelsList()) ;
		
		mApi.getSystemApi().queryParentLockInfo(mHandler);
		
		showText(getString(R.string.label_parent_lock_hint));
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bnt_left:
		case R.id.bnt_left_text :
		case R.id.bnt_left_icon :
			finish();
			break;
		case R.id.bnt_right :
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
					if(dialog == null){
						dialog = AppProgressDialog.showDialog(ParentLockActivity.this, getString(R.string.label_connecting));
					}
					break ;
				case MSG_TASK_END :
					if(dialog != null){
						dialog.dismiss() ;
						dialog	= null ;
					}
					break ;
				case MSG_LOCK_CHANNEL_SUCCESS :
					showText(getString(R.string.label_lock_sucess)) ;
					break ;
				case MSG_LOCK_CHANNEL_FAILED :
					showText(getString(R.string.label_lock_failed)) ;
					break ;
				}
			}
		};
	}
	
}
