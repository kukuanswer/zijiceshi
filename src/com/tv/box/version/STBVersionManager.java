package com.tv.box.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.tv.box.AppConfig;

/**
 * 版本管理
 * 
 * @author mac
 *
 */
public final class STBVersionManager {
	
	static final String TAG = "STBVersionManager" ;
	
	public static final int MSG_NOT_NEW_VERSION 		= 100 ;
	public static final int MSG_HAS_NEW_VERSION			= MSG_NOT_NEW_VERSION + 1 ;
	public static final int MSG_DOWNLOAD_PROGRESS 		= MSG_HAS_NEW_VERSION + 1 ;
	public static final int MSG_DOWNLOAD_FINISH			= MSG_DOWNLOAD_PROGRESS + 1 ;
	public static final int MSG_DOWNLOAD_ERROR			= MSG_DOWNLOAD_FINISH + 1; 
	
	/** 配置文件路径 */
	public static String getConfigPath(Context context){
		String root = AppConfig.createLocalDevicePath(context);
		return root + "/" + AppConfig.STB_UPDATE_CONFIG;
	}
	
	/** stb 软件路径 */
	public static String getStbBinPath(Context context){
		String root = AppConfig.createLocalDevicePath(context);
		
		VersionInfo info = parserVersion(getConfigPath(context)) ;
		if(info == null){
			return null ;
		}
		
		String url 		= AppConfig.STB_HOME + info.getStbFileName();
		String stbName 	= getUniqueName(url);
		
		return root + "/" + stbName ;
	}
	
	/** 服务器配置文件路径 */
	public static String getTempConfig(Context context){
		String root = AppConfig.createLocalDevicePath(context);
		return root + "/" + AppConfig.STB_UPDATE_TEMP;
	}
	
	/** 解析版本字符串: V3.5.0 ---> {3,5,0} */
	public static String[] parserVersionString(String version){
		
		if(version == null){
			return new String[]{};
		}
		
		return version.substring(version.indexOf("V") + 1).replace(".", "&").split("&");
	}
	
	/** 清除本地版本文件 */
	public static void clearAll(Context context){
		
		clearVesion(context);
		
		String temp = getTempConfig(context);
		if(temp != null){
			File file = new File(temp);
			file.delete() ;
		}
	}
	
	/** 清除本地版本文件 */
	public static void clearVesion(Context context){
		
		String config = getConfigPath(context);
		if(config != null){
			File file = new File(config);
			file.delete() ;
		}
		
		String path = getStbBinPath(context);
		if(path != null){
			File file = new File(path);
			file.delete() ;
		}
	}
	
	/** 下载机顶盒软件 */
	public static void downSTBSoft(final Context context,final Handler handler) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String config	= getConfigPath(context);
				String path		= getStbBinPath(context);
				
				// 1. 本地不存在版本
				VersionInfo local = parserVersion(config) ;
				
				if(local == null || path == null){
					
					// 清除
					clearAll(context);
					
					Log.d(TAG, "zhouwei : 本地不存在版本");
					
					// 下载配置文件
					String con  = downloadFile(AppConfig.STB_CONFIG_URL,context,null,AppConfig.STB_UPDATE_CONFIG);
					String bin	= null ;
					
					if(con != null){
						local = parserVersion(config) ;
						Log.d(TAG, "zhouwei : 下载版本");
						bin = downloadFile(AppConfig.STB_BIN_URL,context,null,local.getStbFileName());
					}
					
					if(con == null || bin == null){
						Log.e(TAG, "zhouwei : 服务器版本下载错误");
						clearAll(context) ;
						handler.obtainMessage(MSG_DOWNLOAD_ERROR).sendToTarget();
					}
					
					Log.d(TAG, "zhouwei : 版本下载完成");
				} 
				// 2. 存在版本
				else {
					
					Log.d(TAG, "zhouwei : 本地存在版本,下载服务器配置文件");
					
					// 3. 下载服务器配置文件
					String temp	= getTempConfig(context);
					File file = new File(temp) ;
					file.delete() ;
					
					// 下载
					temp = downloadFile(AppConfig.STB_CONFIG_URL,context,null,AppConfig.STB_UPDATE_TEMP);
					
					if(temp == null){
						Log.e(TAG, "zhouwei : 服务器配置文件下载错误");
						handler.obtainMessage(MSG_DOWNLOAD_ERROR).sendToTarget();
						return ;
					}
					
					Log.d(TAG, "zhouwei : 比较版本");
					
					// 比较版本
					VersionInfo info = parserVersion(temp) ;
					if(info != null){
						
						File f = new File(path) ;
						
						if(f == null || !f.exists()){
							handler.obtainMessage(MSG_HAS_NEW_VERSION).sendToTarget();
							return ;
						}
						
						String l = local.getStbSoftwareVer() ;
						String s = info.getStbSoftwareVer() ;
						
						String[] ll = parserVersionString(l);
						String[] ss = parserVersionString(s);
						
						if(ll.length == 3 && ss.length == 3){
							
							int vl1 = Integer.valueOf(ll[0]) ;
							int vl2 = Integer.valueOf(ll[1]) ;
							int vl3 = Integer.valueOf(ll[2]) ;
							
							int vs1 = Integer.valueOf(ss[0]) ;
							int vs2 = Integer.valueOf(ss[1]) ;
							int vs3 = Integer.valueOf(ss[2]) ;
							
							if((vl1 < vs1) 
									|| (vl1 == vs1 && vl2 < vs2)
									|| (vl1 == vs1 && vl2 == vs2 && vl3 < vs3)){
								
								Log.d(TAG, "zhouwei : 需要更新");
								
								// 清除
								clearAll(context);
								
								handler.obtainMessage(MSG_HAS_NEW_VERSION).sendToTarget();
								
							} else {
								
								Log.d(TAG, "zhouwei : 不需要更新");
								
								handler.obtainMessage(MSG_NOT_NEW_VERSION).sendToTarget();
							}
						} else {
							
							// 清除
							clearAll(context);
							
							Log.e(TAG, "zhouwei : 版本格式错误");
						}
					}
				}
			}
		}).start() ;
	}
	
	public static void downloadVersion(final Context context,final Handler handler){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				// 清除版本
				clearVesion(context);
				
				// 下载config
				String path = downloadFile(AppConfig.STB_CONFIG_URL,context,handler,AppConfig.STB_UPDATE_CONFIG);
				
				if(path != null){
					
					VersionInfo info = parserVersion(path);
					
					String url 		= AppConfig.STB_HOME + info.getStbFileName();
					String stbName 	= getUniqueName(url);
					
					// 下载bin
					downloadFile(url,context,handler,stbName);
				} else {
					if(handler != null){
						handler.obtainMessage(MSG_DOWNLOAD_ERROR).sendToTarget();
					}
				}
			}
		}).start() ;
	}
	
	private static String downloadFile(String url,Context context,Handler handler,String filename) {

		try {
			
			String root = AppConfig.createLocalDevicePath(context);

			if (root == null) {
				return null ;
			}
			
			String path = root + "/" + filename;

			URL u = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) u.openConnection();
			connection.setConnectTimeout(AppConfig.TIME_OUT);
			connection.setReadTimeout(AppConfig.TIME_OUT);
			connection.setRequestMethod("GET");
			connection.connect();
			int size = connection.getContentLength();
			InputStream input = connection.getInputStream();
			
			FileOutputStream output = new FileOutputStream(path);
			
			// read byte[]
			int read = -1;
			int down = 0, parent = 0, pre_parent = 0;
			byte[] buffer = new byte[8 * 1024];// 8KB

			// start read
			while ((read = input.read(buffer)) != -1) {

				// download counter
				down += read;
				
				// download parent
				if (size > 0)
					parent = (int) (((float) down / size) * 100);
				
				// save source to local sdcard
				if (output != null) {
					output.write(buffer, 0, read);
					output.flush();
				}
				
				// call back
				if (parent != pre_parent) {
					if(handler != null){
						handler.obtainMessage(MSG_DOWNLOAD_PROGRESS, size,down).sendToTarget();
					}
					pre_parent = parent;
				}
			}
			
			if(handler != null){
				handler.obtainMessage(MSG_DOWNLOAD_FINISH).sendToTarget();
			}
			
			// 下载完成
			if(output != null){
				output.close() ;
				output	= null ;
			}
			
			if(input != null){
				input.close() ;
				input = null ;
			}
			
			Log.d(TAG, "zhouwei : 下载完成: " + path);
			
			return path ;
		} catch (Exception e) {
			e.printStackTrace();
			if(handler != null){
				handler.obtainMessage(MSG_DOWNLOAD_ERROR).sendToTarget();
			}
			return null ;
		}
	}
	
	public static VersionInfo parserVersion(String path){
		
		if(path == null){
			return null ;
		}
		
		File file = new File(path);
		
		if(!file.exists()){
			return null ;
		}
		
		VersionInfo info = null ;
		
		try{
			
			InputStream input = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			info	= new VersionInfo() ;
			
			while(true){
				
				String line	= reader.readLine() ;
				
				if(line == null){
					break ;
				}
				
				if(line.indexOf("=") > 0){
					
					String[] str = line.split("=") ;
					
					if(str[0].equals("PROJECT_NAME")){
						info.setProjectName(str[1]);
					} else if(str[0].equals("Platform")){
						info.setPlatform(str[1]);
					} else if(str[0].equals("STB_HARDWARE_VER")){ // stb hardware version
						info.setStbHardwareVer(str[1]);
					} else if(str[0].equals("STB_APPLICATION_VER")){// stb version
						info.setStbSoftwareVer(str[1]);
					} else if(str[0].equals("IOS_MOBILE_APP_VER")){
						info.setIosMobileAppVer(str[1]);
					} else if(str[0].equals("ANDROID_MOBILE_APP_VER")){
						info.setAndroidMobileAppVer(str[1]);
					} else if(str[0].equals("STB_FILE_NAME")){
						info.setStbFileName(str[1]);
					} else if(str[0].equals("IOS_FILE_NAME")){
						info.setIosFileName(str[1]);
					} else if(str[0].equals("ANDROID_FILE_NAME")){
						info.setAndroidFileName(str[1]);
					} else if(str[0].equals("STB_FILE_NAME_DEV")){
						info.setStbDevFileName(str[1]);
					}
				}
			}
			
			reader.close() ;
			reader	= null ;
			
			input.close() ;
			input	= null ;
			
			return info ;
		} catch(Exception e){
			e.printStackTrace() ;
			return null ;
		}
	}
	
	private static final String getUniqueName(String fileName) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			return byteArray2HexStr(digest.digest(fileName.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static final String byteArray2HexStr(final byte[] bytes) {
		final StringBuffer sb = new StringBuffer();
		final int size = bytes.length;
		for (int i = 0; i < size; i++) {
			String hex = Integer.toHexString(bytes[i] + 128);
			// if the value is smaller than 16, the hex string has only one
			// character, add a "0".
			if (hex.length() == 1) {
				hex = "0" + hex;
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
