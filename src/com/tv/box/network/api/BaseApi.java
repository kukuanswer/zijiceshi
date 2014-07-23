package com.tv.box.network.api;

import com.tv.box.AppConfig;
import com.tv.box.model.Channel;
import com.tv.box.network.ResultObject;

public abstract class BaseApi {
	
	protected HttpExecutor mHttpExecutor	= new HttpExecutor() ;
	
	protected ResultObject mResultObject	= new ResultObject() ;
	
	protected BaseApi(){
		
	}
	
	protected String buildPost(String command){
		
		StringBuilder str = new StringBuilder() ;
		str.append("http://");
		str.append(AppConfig.IP);
		str.append(":");
		str.append(AppConfig.API_PORT);
		str.append("/");
		str.append(command);
		
		return str.toString() ;
	}
	
	public static String buildChannelUrl(Channel channel){
		
		if(channel == null){
			return null ;
		}
		
		StringBuilder str = new StringBuilder() ;
		str.append("http://");
		str.append(AppConfig.IP);
		str.append(":");
		str.append(AppConfig.API_PORT);
		str.append("/player.");
		str.append(channel.getChannelId());
		
		return str.toString() ;
	}
}
