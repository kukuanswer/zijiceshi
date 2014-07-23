package com.tv.box.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.box.R;
import com.tv.box.model.Channel;

public class ProgramAdapter extends AbstractAdapter {
	
	private List<Channel> mList = new ArrayList<Channel>() ;
	private int selectColor ;

	protected ProgramAdapter(Context context) {
		super(context);
		
		selectColor	= Color.parseColor("#061e24");
	}
	
	protected void loadPrograms(List<Channel> list){
		
		if(list == null || list.isEmpty()){
			return ;
		}
		
		for(Channel program : list){
			if(!mList.contains(program)){
				mList.add(program);
			}
		}
		
		notifyDataSetChanged() ;
	}
	
	protected int getCurrentSelected(){
		
		for(int index = 0 ,size = mList.size(); index < size ; index++){
			Channel program = mList.get(index);
			if(program.isSelected()){
				return index ;
			}
		}
		
		return mList.isEmpty() ? -1 : 0 ;
	}
	
	public Channel nextChannel(){
		
		if(mList.isEmpty()){
			return null ;
		}
		
		int selected = getCurrentSelected();
		
		if(selected == mList.size() - 1){
			return mList.get(0) ;
		} else {
			if(selected >= 0 && (selected + 1) < mList.size()){
				return mList.get(selected + 1) ;
			}
		}
		
		return null ;
	}
	
	public Channel lastChannel(){
		
		int selected = getCurrentSelected();
		
		if(selected == 0){
			return mList.get(mList.size() - 1) ;
		} else {
			if(selected - 1 >= 0){
				return mList.get(selected - 1) ;
			}
		}
		
		return null ;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Channel getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Channel item = getItem(position);
		
		if(convertView == null){
			convertView	= mInflater.inflate(R.layout.item_program_layout,null) ;
		}
		
		TextView name 	= (TextView)convertView.findViewById(R.id.programe_name) ;
		ImageView lock	= (ImageView)convertView.findViewById(R.id.programe_lock) ;
		
		if(item.isSelected()){
			convertView.setBackgroundColor(selectColor);
		} else {
			convertView.setBackgroundColor(Color.TRANSPARENT);
		}
		
		if(item.isLocked()){
			lock.setVisibility(View.VISIBLE);
		} else {
			lock.setVisibility(View.INVISIBLE);
		}
		
		name.setText(item.getName());
		
		return convertView;
	}
	
	public void reset(){
		for(Channel program : mList){
			program.setSelected(false);
		}
	}
	
	public void clear(){
		mList.clear() ;
		notifyDataSetChanged() ;
	}

}
