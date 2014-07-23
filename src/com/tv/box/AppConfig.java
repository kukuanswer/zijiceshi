package com.tv.box;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class AppConfig {

	public static boolean	DEBUG					= true ;
	public static String 	IP						= "" ;
	public static String	API_PORT				= "8085" ;
	public static int 		SocketPort				= 8100 ;
	public static int 		Result_Code_Success		= 0 ;
	public static String	SatelliteId				= "Mwatch_sat" ;
	public static String 	STB_HOME				= "http://122.227.52.54:8088/" ;
	public static String 	STB_BIN_URL				= "http://122.227.52.54:8088/flashrom.bin" ;
	public static String 	STB_CONFIG_URL			= "http://122.227.52.54:8088/CONFIG.INI" ;
	public static String 	FLOADER					= "update" ;
	public static int		TIME_OUT				= 6000 ;
	public static String 	STB_UPDATE_CONFIG		= "stb_config.config" ;
	public static String 	STB_UPDATE_TEMP			= "stb_temp.config" ;
	public static String 	SUPER_PASSWORD			= "" ;
	
	public static int 		UNLOCK_STATE			= 0 ; // 解锁
	public static int 		LOCK_STATE				= 1 ; // 锁定
	
	public static final int SWITCH_ON				= 0 ; // 开
	public static final int SWITCH_OFF				= 1 ; // 关
	
	public static final String STATE_UNLOCK			= "unlock" ;
	
	public static String 	VERSION_DATE			= "2013-12-31 00:00:00" ;
	
	public static String 	UPNP_NAME				= "gbox" ;
	public static String 	ACTION_SEARCH_BOX_IP	= "action_search_box_ip" ;
	
	public static String 	COMMAND_SCAN_STATUS				= "SCAN_STATUS";
	public static String 	COMMAND_SCAN_SATELLITE_INFO		= "SCAN_SATELLITE_INFO";
	public static String 	COMMAND_SCAN_TP_INFO			= "SCAN_TP_INFO";
	public static String 	COMMAND_SCAN_CHANNEL_INFO		= "SCAN_CHANNEL_INFO";
	public static String 	COMMAND_FRONTEND_LOCK_STATE		= "FRONTEND_LOCK_STATE";
	public static String 	COMMAND_BS_UPDATE_LOCK_CONTROL	= "BS_UPDATE_LOCK_CONTROL";
	
	public static String 	ACTION_BIND_CLIENT_SOCKET		= "action.bind.client.socket" ;
	public static String 	ACTION_LOCK_CHANNEL				= "action.lock.channel" ;
	public static String 	ACTION_REFRESH_CHANNEL_LIST		= "action.refresh.channel.list" ;
	public static String 	ACTION_SEARCH_CHANNEL			= "action.search.channel" ;
	public static String 	ACTION_STOP_ALL					= "action.stop.all" ;
	
	public final static String createLocalDevicePath(Context context) {
		
		final String dir = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		String out = dir != null ? (dir + File.separator + FLOADER) : null;

		if (out != null) {
			File f = new File(out);

			if (!f.exists()) {
				f.mkdirs();
			}
		}

		return out;
	}
	
	private final static File getExternalCacheDir(Context context) {
		
		if(context == null){
			return null ;
		}
		
		final String cacheDir = "/Android/data/" + context.getPackageName() + "/";
		
		return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
	}
	
	public final static int intToByteArray(byte[] buffer, int offset,int value) {
		
		buffer[offset + 3] = (byte) ((value >> 24) & 0xFF);
		buffer[offset + 2] = (byte) ((value >> 16) & 0xFF);
		buffer[offset + 1] = (byte) ((value >> 8) & 0xFF);
		buffer[offset + 0] = (byte) (value & 0xFF);
		
		return 4 ;
	}
	
	public static int byteArraytoInt(byte[] buffer, int offset) {
		return ((buffer[offset + 3] & 0xFF) << 24) 
				| ((buffer[offset + 2] & 0xFF) << 16) 
				| ((buffer[offset + 1] & 0xFF) << 8)
				| ((buffer[offset + 0] & 0xFF)) ;
	}
}
