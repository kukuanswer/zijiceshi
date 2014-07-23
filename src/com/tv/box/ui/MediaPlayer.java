package com.tv.box.ui;

import org.libsdl.app.SDLActivity;
import org.libsdl.app.SDLSurface;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public final class MediaPlayer {

	static final String TAG = "MediaPlayer";

	public static final int MP_STATUS_UNKOWN = -1;
	public static final int MP_STATUS_OPEN_ERROR = 12;
	public static final int MP_STATUS_PREPARE = 9;
	public static final int MP_STATUS_PREPARED = 10;
	public static final int MP_STATUS_PREPARE_ERROR = 11;
	public static final int MP_STATUS_START = 13;
	public static final int MP_STATUS_SEEK_UPDATE = 30;

	private Context mContext;
	private int mStatus;
	private MediaplayerListener mListener;
	private Handler mHandler;
	private PlayerTask mTask;
	private SDLActivity mSoftPlayer;
	private String mPath;
	private int mBuffersize = 3 * 15;
	private int maxAnalyzeDuration = 100000;

	@SuppressLint("HandlerLeak")
	public MediaPlayer(Context context) {

		mStatus = MP_STATUS_UNKOWN;
		mContext = context;
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				callback(msg.what, "");
			}

		};
	}

	public void setDataSource(final String url) {

		if (mContext == null || url == null) {
			return;
		}

		mPath = url;
	}

	public void setListener(MediaplayerListener l) {
		mListener = l;
	}

	public SDLSurface getSurfaceView() {
		return mSoftPlayer != null ? mSoftPlayer.getSDLSurface() : null;
	}

	public int getBuffersize() {
		return mBuffersize;
	}

	public void setBuffersize(int mBuffersize) {
		this.mBuffersize = mBuffersize;
	}

	public int getMaxAnalyzeDuration() {
		return maxAnalyzeDuration;
	}

	public void setMaxAnalyzeDuration(int maxAnalyzeDuration) {
		this.maxAnalyzeDuration = maxAnalyzeDuration;
	}

	public void notifySurfaceSizeChange(int width, int height) {
		mSoftPlayer.getSDLSurface().getHolder().setFixedSize(width, height);
	}

	public synchronized boolean start() {

		if (mSoftPlayer != null) {
			stop();
		}

		if (mPath == null) {
			return false;
		}

		mTask = new PlayerTask();
		mTask.execute(mPath);

		return true;
	}

	public void pause() {

		if (mSoftPlayer != null) {
			mSoftPlayer.onPause();
		}
	}

	public int[] getAudioData() {

		if (mSoftPlayer != null) {

		}

		String temp = SDLActivity.getyingguiData();

		System.out.println(temp);

		return null;
	}

	public void switchAudio(int index) {

	}

	public void resume() {
		if (mSoftPlayer != null) {
			mSoftPlayer.onResume();
		}
	}

	public void stop() {

		if (mTask != null) {
			mTask.cancel(true);
		}
		mTask = null;
	}

	public void release() {
		stop();
	}

	private void stopPlayer() {

		try {

			if (mSoftPlayer != null) {
				mSoftPlayer.exit();
				mSoftPlayer.onPause();
				mSoftPlayer.onDestroy();
			}

			mSoftPlayer = null;
			
			// 保证完全释放资源
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendMessage(int status, String error) {
		mHandler.obtainMessage(status, error).sendToTarget();
	}

	private void callback(int status, String error) {

		mStatus = status;

		if (mListener != null) {
			mListener.callback(status, error);
		}

		Log.d(TAG, "Status = " + mStatus);
	}

	private final class PlayerTask extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {

			stopPlayer();
			
			return mPath;
		}

		@Override
		protected void onPostExecute(String result) {

//			// 1. 创建播放器
//			mSoftPlayer = new SDLActivity(mContext, mHandler, mPath,
//					mBuffersize, maxAnalyzeDuration);
//
//			// 2. 准备完成
//			sendMessage(MP_STATUS_PREPARED, "");
		}

		@Override
		protected void onPreExecute() {
			sendMessage(MP_STATUS_PREPARE, "");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}

	public static interface MediaplayerListener {

		public void callback(int status, String error);
	}
}
