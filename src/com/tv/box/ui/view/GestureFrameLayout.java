package com.tv.box.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public final class GestureFrameLayout extends FrameLayout {
	
//	private GestureObserver mObserver ;

	public GestureFrameLayout(Context context) {
		super(context);
//		init(context);
	}
	
	public GestureFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
//		init(context);
	}
	
	public GestureFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs,defStyle);
//		init(context);
	}
	
//	private void init(Context context){
//		mObserver	= new GestureObserver(context);
//		mObserver.setCallback(new GestureCallback() {
//			
//			@Override
//			public void callback(int event, int distance) {
//				
//				if(event == GestureObserver.EVENT_DOUBLE_TAP){
//					System.out.println(">>>>>>双击");
//				}
//			}
//		}) ;
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		
//		return mObserver.dispatchTouchEvent(event);
//		
//		// return super.onTouchEvent(event);
//	}
	
}
