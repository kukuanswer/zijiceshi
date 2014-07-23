package com.tv.box;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 
 * @author zhouwei
 *
 */
public final class AppPreferences {
	
	public static final String KEY_CHANNLE_ID 				= "_selected_id" ;
	public static final String KEY_ANDROID_BOX_IP 			= "_android_box_ip" ;
	public static final String KEY_STB_VERSION				= "_stb_soft_version" ;
	
	public static final String KEY_NOT_UPDATE				= "_key_not_update" ;
	public static final String KEY_NEED_WIFI_PWD			= "_key_need_wifi_pwd" ;
	
	public static final String KEY_SEARCH_FEQ				= "_key_search_feq" ;
	public static final String KEY_SEARCH_SYMB				= "_key_search_symb" ;
	public static final String KEY_SEARCH_POLAR				= "_key_search_polar" ;
	
	private static AppPreferences _instance ;
	
	private SharedPreferences 	mSharedPreferences ;
	private Editor 				mEditor ;
	
	private AppPreferences(Context context){
		
		mSharedPreferences 	= PreferenceManager.getDefaultSharedPreferences(context);
		mEditor 			= mSharedPreferences.edit() ;
	}
	
	public static void initPreferences(Context context){
		
		if(context == null){
			return ;
		}
		
		if(_instance == null){
			_instance  = new AppPreferences(context) ;
		}
	}
	
	public static AppPreferences getPreferences(){ 
		return _instance ;
	}
	
	private boolean checkGetInput(String key){
		
		if(key == null || key.equals("")){
			return false ;
		}
		
		if(mSharedPreferences == null){
			return false ;
		}
		
		return true ;
	}
	
	private boolean checkPutInput(String key){
		
		if(key == null || key.equals("")){
			return false ;
		}
		
		if(mEditor == null){
			return false ;
		}
		
		return true ;
	}
	
	public String getString(String key){
		
		if(!checkGetInput(key)) return null ;
		
		return mSharedPreferences.getString(key, "");
	}
	
	public int getInt(String key){
		
		if(!checkGetInput(key)) return -1 ;
		
		return mSharedPreferences.getInt(key, -1);
	}
	
	public int getInt(String key,int defaultValue){
		
		if(!checkGetInput(key)) return defaultValue ;
		
		return mSharedPreferences.getInt(key, defaultValue);
	}
	
	public long getLong(String key){
		
		if(!checkGetInput(key)) return -1 ;
		
		return mSharedPreferences.getLong(key, -1) ;
	}
	
	public boolean getBoolean(String key){
		
		if(!checkGetInput(key)) return false ;
		
		return mSharedPreferences.getBoolean(key, false) ;
	}
	
	public boolean getBoolean(String key,boolean defaultValue){
		
		if(!checkGetInput(key)) return false ;
		
		return mSharedPreferences.getBoolean(key, defaultValue) ;
	}
	
	public float getFloat(String key,float defaultValue){
		
		if(!checkGetInput(key)) return -0.0f ;
		
		return mSharedPreferences.getFloat(key, defaultValue) ;
	}
	
	public void putString(String key,String value){
		
		if(!checkPutInput(key) || value == null) return ;
		
		mEditor.putString(key, value).commit() ;
	}
	
	public void putInt(String key,int value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putInt(key, value).commit() ;
	}
	
	public void putLong(String key,long value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putLong(key, value).commit() ;
	}
	
	public void putBoolean(String key,boolean value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putBoolean(key, value).commit() ;
	}
	
	public void putFloat(String key,float value){
		
		if(!checkPutInput(key)) return ;
		
		mEditor.putFloat(key, value).commit() ;
	}
	
	public void clear(){
		mSharedPreferences 	= null ;
		mEditor				= null ;
		_instance			= null ;
	}
}
