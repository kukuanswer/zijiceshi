package com.tv.box.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ListView;

import com.tv.box.AppHanlder;
import com.tv.box.network.api.ServiceApi;

public abstract class BaseListView extends ListView {
	
	protected Context 		mContext ;
	protected ServiceApi 	mApi ;
	protected AppHanlder	mHandler ;
	
	public BaseListView(Context context) {
		super(context);
		load(context);
	}
	
	public BaseListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		load(context);
	}

	public BaseListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		load(context);
	}
	
	private void load(Context context){
		mContext	= context ;
		mApi		= ServiceApi.getServiceApi() ;
		mHandler	= getHanlder() ;
	}

	protected final void setCacheColor(){
		setCacheColorHint(Color.TRANSPARENT) ;
	}
	
	public AppHanlder getHanlder(){
		return new AppHanlder(mContext);
	}
	
	public void onResume(){
		
	}
	
	public void onRelaod(){
		
	}
	
	public void onDestory(){
		
	}
}
