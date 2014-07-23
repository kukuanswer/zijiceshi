package com.tv.box.ui.view;

import java.util.List;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;

import com.tv.box.AppHanlder;
import com.tv.box.R;
import com.tv.box.model.PageInfo;
import com.tv.box.model.Channel;

public class ProgramListView extends BaseListView {

	private ProgramAdapter		mAdapter;
	private OnChannelListChange mListener ;

	public ProgramListView(Context context) {
		super(context);
		init(context);
	}

	public ProgramListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		mAdapter = new ProgramAdapter(context);
		setAdapter(mAdapter);
		setCacheColor();
	}
	
	public void setChangeListener(OnChannelListChange l){
		mListener	= l ;
	}

	public Channel getItem(int position) {
		return mAdapter.getItem(position);
	}
	
	public void reset(){
		mAdapter.reset();
	}
	
	public void notifyUI(){
		mAdapter.notifyDataSetChanged() ;
	}
	
	public void showCurrentItem(){
		
		int selected = mAdapter.getCurrentSelected() ;
		
		if(selected >= 0){
			setSelection(selected);
		}
	}

	public void onResume() {
		
		if(mAdapter.getCount() == 0){
			mApi.getSystemApi().queryChannelList(mHandler) ;
		}
	}
	
	public boolean isEmpty(){
		return mAdapter.isEmpty() ;
	}
	
	public void onRelaod(){
		mAdapter.clear() ;
		onResume() ;
	}
	
	public void clear(){
		mAdapter.clear() ;
	}

	public void onDestory() {
		
	}
	
	public Channel getNextChannel(){
		return mAdapter.nextChannel() ;
	}
	
	public Channel getLastChannel(){
		return mAdapter.lastChannel() ;
	}
	
	public void playLast(){
		
	}

	@Override
	public AppHanlder getHanlder() {
		return new AppHanlder(mContext){

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_QUERY_PROGRAM_LIST_SUCCESS :
					
					@SuppressWarnings("unchecked")
					PageInfo<Channel> page = (PageInfo<Channel>)msg.obj ;
					
					mAdapter.loadPrograms(page.getList());
					
					if(mListener != null){
						mListener.onChange(page.getList()) ;
					}
					
					break ;
				case MSG_QUERY_PROGRAM_LIST_FAILED :
					showText(getContext().getString(R.string.error_network)) ;
					break ;
				case MSG_QUERY_PROGRAM_EMPTY :
					// showText(getContext().getString(R.string.error_channel_empty)) ;
					break ;
				}
			}
		};
	}
	
	public static interface OnChannelListChange {
		
		public void onChange(List<Channel> page) ;
	}
}
