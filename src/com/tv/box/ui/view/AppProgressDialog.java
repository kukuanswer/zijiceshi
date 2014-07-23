package com.tv.box.ui.view;

import org.zw.android.framework.impl.Worker;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tv.box.R;

public final class AppProgressDialog extends ProgressDialog implements OnCancelListener {
	
	private ImageView loadIcon ;
	private TextView  loadTextView ;
	private String 	  loadText ;
	private Worker	  worker ;

	private AppProgressDialog(Context context) {
		super(context);
		
		setOnCancelListener(this);
	}
	
	private void setText(String text){
		
		if(text != null && !text.equals("")){
			loadText = text ;
		}
	}
	
	public void attachWorker(Worker worker){
		this.worker = worker ;
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		
		Log.d("TotooleProgressDialog", "on cancel");
		
		if(worker != null && !worker.isCancelled()){
			worker.onCancelled() ;
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog_layout) ;
		
		loadIcon		= (ImageView)findViewById(R.id.rorate_image);
		loadTextView	= (TextView)findViewById(R.id.rorate_text);
		
		if(loadText != null){
			loadTextView.setText(loadText) ;
		}
		
		initRorateAnim();
	}
	
	private void initRorateAnim(){
		Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.loading_anim) ;
		animation.setInterpolator(new LinearInterpolator());
		loadIcon.startAnimation(animation);
	}
	
	public static AppProgressDialog showDialog(Context context,String text){
		
		AppProgressDialog dialog = new AppProgressDialog(context) ;
		dialog.setText(text) ;
		dialog.setCanceledOnTouchOutside(false);
		dialog.setCancelable(true);
		dialog.show() ;
		
		return dialog ;
	}
	
	public static AppProgressDialog showDialog(Context context,String text,boolean cancel){
		
		AppProgressDialog dialog = new AppProgressDialog(context) ;
		dialog.setText(text) ;
		dialog.setCancelable(cancel);
		dialog.show() ;
		
		return dialog ;
	}
}
