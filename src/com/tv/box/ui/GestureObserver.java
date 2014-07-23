package com.tv.box.ui;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * 
 * @author zhouwei
 * 
 */
public final class GestureObserver {

	static final String TAG 					= "GestureObserver";
	static final int ScrollMaxDistance 			= 80 ;
	static final int UnitDistance				= 10 ;
	
	public final static int EVENT_DIRECTION_UP					= 1 ;
	public final static int EVENT_DIRECTION_DOWN				= 2 ;
	public final static int EVENT_DIRECTION_LEFT				= 3 ;
	public final static int EVENT_DIRECTION_RIGHT				= 4 ;
	public final static int EVENT_SINGLE_TAP					= 5 ;
	public final static int EVENT_DOUBLE_TAP					= 6 ;
	
	private GestureDetector mProxy;
	private Context			mContext ;
	private GestureCallback	mCallback ;
	private float 			mEventX;
	private int 			mLastX;
	
	public GestureObserver(Context context) {
		mProxy		= new GestureDetector(context, listener);
		mProxy.setOnDoubleTapListener(doubleListener);
		mContext	= context ;
	}

	/** 事件分发 */
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return mProxy.onTouchEvent(ev);
	}
	
	/** 设置回调 */
	public void setCallback(GestureCallback callback){
		mCallback	= callback ;
	}
	
	/** 回调 */
	private void callback(int event,int distance,MotionEvent e){
		if(mCallback != null){
			mCallback.callback(event, distance,e) ;
		}
	}

	private final OnDoubleTapListener doubleListener = new OnDoubleTapListener() {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			//Log.d(TAG, "zhouwei : onSingleTapConfirmed");

			callback(EVENT_SINGLE_TAP,0,e);
			
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			//Log.d(TAG, "zhouwei : onDoubleTapEvent");

			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			//Log.d(TAG, "zhouwei : onDoubleTap");
			
			callback(EVENT_DOUBLE_TAP,0,e);
			
			return false;
		}
	};

	private final OnGestureListener listener = new OnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// Log.d(TAG, "zhouwei : onSingleTapUp");
			
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			//Log.d(TAG, "zhouwei : onShowPress");
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			
			int dx 	= (int)((e2.getX() - mEventX) / UnitDistance) ;
			
			//Log.d(TAG, "zhouwei : onScroll   distanceX=" + distanceX  + "  distanceY" + distanceY); 
			
			if(distanceX > 0 && Math.abs(distanceY) <= 1){
				
				if(mLastX != dx){
					callback(EVENT_DIRECTION_LEFT,dx,e2);
					mLastX	= dx ;
					mEventX	= e2.getX() ;
				}
			} else if(distanceX < 0 && Math.abs(distanceY) <= 1){
				
				if(mLastX != dx){
					callback(EVENT_DIRECTION_RIGHT,dx,e2);
					mLastX	= dx ;
					mEventX	= e2.getX() ;
				}
			}
			
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			//Log.d(TAG, "zhouwei : onLongPress");

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			
			// Log.d(TAG, "zhouwei : onFling  velocityX=" + velocityX + "  velocityY=" + velocityY);
			
			if (velocityY > 1000) {

				callback(EVENT_DIRECTION_DOWN, 0, e2);

				return true;
			} else if (velocityY < 0 && Math.abs(velocityY) > 1000) {

				callback(EVENT_DIRECTION_UP, 0, e2);
				
				return true;
			}

			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			// Log.d(TAG, "zhouwei : onDown");
			
			mEventX	= e.getX() ;
			mLastX	= 0 ;
			
			return true ;
		}
	};
	
	public static interface GestureCallback {
		
		public void callback(int event, int distance,MotionEvent e) ;
	}
}
