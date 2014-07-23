package com.tv.box.network;

import org.zw.android.framework.IExecuteAsyncTask.IAsyncTask;

import android.util.Log;

import com.tv.box.AppHanlder;
import com.tv.box.IMessageDefine;

/**
 * 异步任务
 * @author zhouwei
 *
 */
public abstract class BaseTask extends IAsyncTask implements IMessageDefine {
	
	static final String TAG = "BaseTask" ;
	
	private AppHanlder mHandler ;
	
	public BaseTask(){
		this(null);
	}
	
	public BaseTask(AppHanlder handler){
		mHandler	= handler;
	}
	
	public void sendMessage(int what){
		if(mHandler != null){
			mHandler.sendMessage(what);
		}
	}
	
	public void sendMessage(int what,Object obj){
		if(mHandler != null){
			mHandler.sendMessage(what,obj);
		}
	}
	
	@Override
	public Object onProcessing() {
		
		// start task
		sendMessage(MSG_TASK_START);
		
		Log.d(TAG, "<<<<<<<<<<<<<sync task start>>>>>>>>>>>>>");
		
		try{
			
			// processing
			processing() ;
			
		}catch(Exception e){
			e.printStackTrace() ;
		}
		
		// end
		sendMessage(MSG_TASK_END);
		
		Log.d(TAG, "------------sync task end---------------");
		
		return null;
	}
	
	public abstract void processing() ;
}
