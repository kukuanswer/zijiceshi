package com.tv.box.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class AbstractAdapter extends BaseAdapter {

	protected LayoutInflater 	mInflater ;
	protected Context			mContext ;
	
	protected AbstractAdapter(Context context){
		mContext	= context ;
		mInflater	= LayoutInflater.from(context) ;
	}
	
	public void clear(){
		
	}
}
