package com.tv.box;

import android.app.Application;

import com.tv.box.network.api.ServiceApi;

public final class AppContext extends Application {
	
	static final String TAG 		= "AppContext" ;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		AppDebug.d(TAG, "onCreate()");
		
		// init preferences
		AppPreferences.initPreferences(this);
		
		// init service api
		ServiceApi.createService() ;
		
	}
	
}
