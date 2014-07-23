package com.tv.box.network.api;

import org.json.JSONObject;

import com.tv.box.AppConfig;
import com.tv.box.AppHanlder;
import com.tv.box.model.Wifi;
import com.tv.box.network.BaseTask;
import com.tv.box.network.ResultObject;
import com.tv.box.network.SyncTaskExecutor;

public class WifiApi extends BaseApi {

	protected WifiApi(){
		
	}
	
	public void queryWifiHostPassword(AppHanlder handler) {
		
		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {
				
				ResultObject result = mResultObject.clone();
				String url = buildPost("command");
				
				try{
					
					JSONObject command = new JSONObject();
					command.put("command", "bs_get_wifi_ap_infor");
					command.put("commandId", 111);
					
					boolean noerror = mHttpExecutor.doPost(url,command.toString(), result);
					
					if(noerror){
						
						JSONObject json = new JSONObject(result.getContent());
						int resultCode 	= json.getInt("result");
						
						if(resultCode == AppConfig.Result_Code_Success){
							
							Wifi  wifi = new Wifi() ;
							wifi.setFree_status(json.getInt("free_status"));
							wifi.setWifi_ap_name(json.getString("wifi_ap_name"));
							wifi.setWifi_ap_passwd(json.getString("wifi_ap_passwd"));
							
							sendMessage(MSG_QUERY_WIFI_PASSWORD_SUCCESS,wifi);
						} else {
							sendMessage(MSG_QUERY_WIFI_PASSWORD_FAILED);
						}
					}
				} catch(Exception e){
					e.printStackTrace() ;
					sendMessage(MSG_QUERY_WIFI_PASSWORD_FAILED);
				}
			}
			
		});
	}
	
	public void modifyWifiHostPassword(final Wifi wifi,final int flag,AppHanlder handler){
		
		SyncTaskExecutor.executeWithNewThread(new BaseTask(handler) {

			@Override
			public void processing() {
				
				ResultObject result = mResultObject.clone();
				String url = buildPost("command");
				
				try{
					
					JSONObject command = new JSONObject();
					command.put("command", "bs_set_wifi_ap");
					command.put("commandId", 3005);
					command.put("free_status", flag);
					command.put("wifi_ap_name", wifi.getWifi_ap_name());
					command.put("wifi_ap_passwd", wifi.getWifi_ap_passwd());
					
					boolean noerror = mHttpExecutor.doPost(url,command.toString(), result);
					
					if(noerror){
						
						JSONObject json = new JSONObject(result.getContent());
						int resultCode 	= json.getInt("result");
						
						if(resultCode == AppConfig.Result_Code_Success){
							sendMessage(MSG_MODIFY_WIFI_PWD_SUCCESS);
						} else {
							sendMessage(MSG_MODIFY_WIFI_PWD_FAILED);
						}
					} else {
						sendMessage(MSG_MODIFY_WIFI_PWD_FAILED);
					}
				} catch(Exception e){
					e.printStackTrace() ;
				}
			}
			
		});
	}
}
