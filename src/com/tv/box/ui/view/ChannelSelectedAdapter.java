package com.tv.box.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.R;
import com.tv.box.model.Channel;
import com.tv.box.network.api.ServiceApi;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class ChannelSelectedAdapter extends AbstractAdapter implements View.OnClickListener {
	
	private List<Channel> mList = new ArrayList<Channel>();
	private AppHanlder mHandler ;

	protected ChannelSelectedAdapter(Context context) {
		super(context);
		
		mHandler	= new AppHanlder(context) {

			@Override
			public void disposeMessage(Message msg) {
				switch(msg.what){
				case MSG_LOCK_CHANNEL_SUCCESS :
					mContext.sendBroadcast(new Intent(AppConfig.ACTION_LOCK_CHANNEL)) ;
					ServiceApi.getServiceApi().getSystemApi().notifyChannelLock(mHandler);
					break ;
				case MSG_LOCK_CHANNEL_FAILED :
					showText(mContext.getResources().getString(R.string.label_lock_failed)) ;
					break ;
				}
			}
		} ;
	}
	
	public void loadChannel(List<Channel> list){
		
		if(list == null || list.isEmpty()){
			return ;
		}
		
		for(Channel channel : list){
			mList.add(channel) ;
		}
		
		notifyDataSetChanged() ;
	}
	
	public List<Integer> getLockChannelList(){
		
		List<Integer> list = new ArrayList<Integer>();
		
		for(Channel ch : mList){
			if(ch.isLocked()){
				list.add(ch.getChannelId());
			}
		}
		
		return list ;
	}

	@Override
	public int getCount() {
		return mList.size() ;
	}

	@Override
	public Channel getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position ;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Channel item = getItem(position) ;
		
		if(convertView == null){
			convertView	= mInflater.inflate(R.layout.item_channel_lock_layout, null);
		}
		
		TextView name = (TextView)convertView.findViewById(R.id.channel_name);
		TextView lock = (TextView)convertView.findViewById(R.id.channel_lock);
		
		name.setText(item.getName());
		lock.setTag(item);
		lock.setOnClickListener(this);
		
		if(item.isLocked()){
			lock.setBackgroundResource(R.drawable.ic_lock_sel);
		} else {
			lock.setBackgroundResource(R.drawable.ic_lock_nor);
		}
		
		return convertView ;
	}

	@Override
	public void onClick(View v) {
		onChannelSelected((Channel)v.getTag()) ;
	}
	
	public void onChannelSelected(final Channel item){
		
		item.setLocked(!item.isLocked());
		
		if(item.isLocked()){
			item.setLock(AppConfig.LOCK_STATE);
		} else {
			item.setLock(AppConfig.UNLOCK_STATE);
		}
		
		notifyDataSetChanged() ;
		
		// 解锁或者锁定
		ServiceApi.getServiceApi().getSystemApi()
				.lockChannel(item,item.isLocked() ? AppConfig.LOCK_STATE : AppConfig.UNLOCK_STATE, mHandler) ;
	}
}
