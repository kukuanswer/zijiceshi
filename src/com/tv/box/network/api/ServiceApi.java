package com.tv.box.network.api;

public class ServiceApi {

	private static ServiceApi _instance;
	
	private WifiApi				mWifiApi ;
	private SystemApi			mSystemApi ;
	
	private ServiceApi() {
		mWifiApi			= new WifiApi() ;
		mSystemApi			= new SystemApi() ;
	}

	public static void createService() {
		
		if (_instance == null) {
			_instance = new ServiceApi();
		}
	}
	
	public static ServiceApi getServiceApi() {
		return _instance ;
	}
	
	public WifiApi getWifiApi(){
		return mWifiApi ;
	}
	
	public SystemApi getSystemApi(){
		return mSystemApi ;
	}
}
