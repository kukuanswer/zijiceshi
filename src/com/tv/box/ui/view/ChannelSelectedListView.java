package com.tv.box.ui.view;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.tv.box.model.Channel;

public class ChannelSelectedListView extends BaseListView implements OnItemClickListener{
	
	ChannelSelectedAdapter mAdapter ;

	public ChannelSelectedListView(Context context) {
		super(context);
		
		init(context) ;
	}
	
	public ChannelSelectedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init(context) ;
	}

	private void init(Context context){
		mAdapter	= new ChannelSelectedAdapter(context);
		setAdapter(mAdapter);
		setOnItemClickListener(this);
	}
	
	public void loadChannel(List<Channel> list){
		mAdapter.loadChannel(list);
	}
	
	public List<Integer> getLockChannelList(){
		return mAdapter.getLockChannelList() ;
	}
	
	public void refreshList(){
		mAdapter.notifyDataSetChanged() ;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		mAdapter.onChannelSelected(mAdapter.getItem(position));
	}
}
