package com.tv.box.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.IMessageDefine;
import com.tv.box.R;

public class SocketClient {
	
	private static 	SocketClient _instance ;
	
	private Context 			mContext ;
	private boolean				mStart ;
	private Thread				mThread ;
	private Socket 				mSocket ;
	private BufferedReader 		mReader ;
	private BufferedWriter		mWriter ;
	private AppHanlder			mHandler ;
	
	private SocketClient(Context context){
		mContext	= context ;
		
		System.out.println("zhouwei : 创建Socket客户端");
	}
	
	public static SocketClient create(Context context){
		
		if(_instance == null){
			_instance	= new SocketClient(context) ;
		}
		
		return _instance ;
	}
	
	public static SocketClient get(){
		return _instance ;
	}

	public void init(AppHanlder handler){
		
		mStart		= true ;
		mHandler	= handler ;
	
		initThread();
	}
	
	private void initThread(){
		
		mThread	= new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				while(mStart){
					
					try{
						
						if(mSocket == null){
							mSocket	= new Socket(AppConfig.IP, AppConfig.SocketPort);
							mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
							mWriter	= new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));		
							
							registerEvent();
						}
					}catch(Exception e){
						e.printStackTrace() ;
						continue ;
					}
					
					if(mReader != null){
						try {
							
							String msg = mReader.readLine() ;
							
							if(msg == null){
								continue ;
							}
							
							System.out.println("zhouwei : 原始事件 : " + msg);
							
							JSONObject json = new JSONObject(msg);
							
							String event 	= json.getString("event");
							
							if(event.equals("FRONTEND_LOCK_STATE")){
								
								String state	= json.getString("state");
								
								if(state.equals(AppConfig.STATE_UNLOCK)){
									sendMessage(mContext.getResources().getString(R.string.label_register_no_signal));
								}
							} else if(event.equals("SCAN_SATELLITE_INFO")){
								//sendTipMessage(mContext.getResources().getString(R.string.label_register_search_satellite) + json.getString("name"));
							} else if(event.equals("SCAN_TP_INFO")){
								
							} else if(event.equals("SCAN_CHANNEL_INFO")){
								//sendTipMessage(mContext.getResources().getString(R.string.label_register_search_program) + json.getString("name"));
							} else if(event.equals("SCAN_STATUS")){
								
								// sendTipMessage(mContext.getResources().getString(R.string.label_search_program));
								
								int status 		= json.getInt("status");
								int progress 	= json.getInt("progress");
								
								if(status == 1){
									
								}
								
							} else if(event.equals("BS_UPDATE_CHANNEL_LSIT")){
								
								// 刷新列表
								Intent intent = new Intent(AppConfig.ACTION_REFRESH_CHANNEL_LIST) ;
								mContext.sendBroadcast(intent);
								
							} else {
								System.out.println("zhouwei : 未知事件 : " + event);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					try{
						Thread.sleep(5);
					}catch(Exception e){
						e.printStackTrace() ;
					}
				}
			}
		}) ;
		
		mThread.setDaemon(true);
		mThread.setName("#Listener Thread");
		mThread.start() ;
	}
	
	private void sendMessage(String msg){
		if(mHandler != null){
			mHandler.obtainMessage(IMessageDefine.MSG_REGIST_EVENT, msg).sendToTarget();
		}
	}
	
	private void sendTipMessage(String msg){
		if(mHandler != null){
			mHandler.obtainMessage(IMessageDefine.MSG_REGIST_TIP, msg).sendToTarget();
		}
	}
	
	private void registerEvent(){
		
		if(mWriter == null){
			return ;
		}
		
		try{
			JSONObject json = new JSONObject();
			json.put("command", "register_event");
			json.put("commandId", 1111);
			
			JSONArray array = new JSONArray() ;
			array.put("SCAN_STATUS");
			array.put("SCAN_SATELLITE_INFO");
			array.put("SCAN_TP_INFO");
			array.put("SCAN_CHANNEL_INFO");
			array.put("FRONTEND_LOCK_STATE");
			array.put("BS_UPDATE_LOCK_CONTROL");
			array.put("BS_UPDATE_CHANNEL_LSIT");// 节目搜索事件通知
			
			json.put("event", array);
			
			mWriter.write(json.toString());
			mWriter.flush();
		}catch(Exception e){
			e.printStackTrace() ;
		}
	}
	
	public void release(){
		
		mStart	= false ;
		
		try{
			
			if(mSocket != null){
				mSocket.close() ;
				mSocket	= null ;
			}
			
			if(mReader != null){
				mReader.close() ;
				mReader	= null ;
			}
			
			if(mWriter != null){
				mWriter.close() ;
				mWriter	= null ;
			}
		} catch(Exception e){
			e.printStackTrace() ;
		}
	}
}
