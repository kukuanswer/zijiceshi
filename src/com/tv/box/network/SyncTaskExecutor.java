package com.tv.box.network;

import org.zw.android.framework.impl.ExecuteAsyncTaskImpl;
import org.zw.android.framework.impl.Worker;

import android.util.Log;

public final class SyncTaskExecutor {
	
	private SyncTaskExecutor(){
		
	}

	public static void execute(BaseTask task){
		
		if(task == null){
			return ;
		}
		
		// 异步任务
		Worker woker = ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTask(task);
		
		Log.d("SyncTaskExecutor", "" + woker) ;
	}
	
	public static void executeWithNewThread(BaseTask task){
		
		if(task == null){
			return ;
		}
		
		// 异步任务
		Worker woker =  ExecuteAsyncTaskImpl.defaultSyncExecutor().executeTaskInNewThread(task);
		
		Log.d("SyncTaskExecutor", "" + woker) ;
	}
	
	/** 关闭线程池 */
	public static void shutdown(){
		ExecuteAsyncTaskImpl.defaultSyncExecutor().shutdown() ;
	}
}
