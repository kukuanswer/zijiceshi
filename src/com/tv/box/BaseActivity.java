package com.tv.box;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tv.box.network.api.ServiceApi;

public abstract class BaseActivity extends Activity implements  View.OnClickListener{

	protected AppContext 	mApplication ;
	protected AppHanlder 	mHandler ;
	
	private TextView 		mIcon ;
	private TextView 		mTitle ;
	protected TextView		mLeftText ;
	protected Button		mLeftIcon ;
	protected ServiceApi	mApi ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mApplication	= (AppContext)getApplication() ;
		mHandler		= getHandler() ;
		mApi			= ServiceApi.getServiceApi() ;
	}
	
	protected final void setTopBarIcon(int resid){
		
		if(mIcon == null){
			mIcon	= (TextView)findViewById(R.id.top_center_icon);
		}
		
		if(mIcon != null){
			mIcon.setVisibility(View.VISIBLE);
			mIcon.setBackgroundResource(resid);
		}
	}
	
	protected final void setTopBarTitle(String text){
		
		if(mTitle == null){
			mTitle	= (TextView)findViewById(R.id.top_center_title);
		}
		
		if(text != null){
			mTitle.setVisibility(View.VISIBLE);
			mTitle.setText(text);
		}
	}
	
	protected final void setLeftText(String left){
		
		if(mLeftText == null){
			mLeftText = (TextView)findViewById(R.id.bnt_left_text);
		}
		
		if(mLeftIcon == null){
			mLeftIcon = (Button)findViewById(R.id.bnt_left_icon);
		}
		
		if(left != null){
			mLeftText.setVisibility(View.VISIBLE);
			mLeftText.setText(left);
		}
		
		if(mLeftIcon != null){
			mLeftIcon.setOnClickListener(this);
			mLeftIcon.setVisibility(View.VISIBLE);
		}
	}
	
	protected final void showMessageDialog(String msg) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.label_hint);
		builder.setMessage(msg);
		builder.setPositiveButton(R.string.bnt_label_yes, null);
		builder.create().show();
	}
	
	protected void showText(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show() ;
	}
	
	protected AppHanlder getHandler(){
		return new AppHanlder(this);
	}
}
