package com.tv.box.ui;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.tv.box.R;

public final class VoiceDialog extends PopupWindow {
	
	protected HomeActivity 		mOwner ;
	protected LayoutInflater 	mLayoutInflater ;
	protected ImageView			mIcon ;
	protected SeekBar			mSeekBar ;
	protected Handler			mHandler = new Handler();
	
	public VoiceDialog(HomeActivity activity){
		
		mOwner			= activity ;
		mLayoutInflater = LayoutInflater.from(activity);
		
		View view = mLayoutInflater.inflate(R.layout.voice_dialog_layout, null);
		setContentView(view);
		
		mIcon 		= (ImageView)view.findViewById(R.id.voice_icon);
		mSeekBar	= (SeekBar)view.findViewById(R.id.audio_bar);
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
				if(fromUser){
					mOwner.setVolume(progress);
				}
			}
		}) ;
		
		mIcon.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				int volume = mOwner.getVolume() ;
				
				if(volume == 0){
					setVoice(1,true);
					mOwner.setVolume(1);
				} else {
					setVoice(0,true);
					mOwner.setVolume(0);
				}
			}
		}) ;
		
		DisplayMetrics dm = new DisplayMetrics();   
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);   
		setWidth((int)(dm.widthPixels * 0.6));
		setHeight(50);
		setOutsideTouchable(true);
	}
	
	public void setVoice(int voice,boolean hide){
		
		if(voice == 0){
			mIcon.setBackgroundResource(R.drawable.ic_voice_silence);
		} else {
			mIcon.setBackgroundResource(R.drawable.ic_voice_nor);
		}
		
		mSeekBar.setProgress(voice);
	}
	
	public void startHide(){
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				if(isShowing()){
					dismiss() ;
				}
			}
		}, 4000) ;
	}

	public final void show(View parent){
		
		mSeekBar.setMax(mOwner.getMaxVolume());
		mSeekBar.setProgress(mOwner.getVolume());
		
		showAtLocation(parent, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
	}	
}
