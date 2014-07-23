package com.tv.box.version;

public final class STBUpdate {
	
	/** 上传文件执行升级 */
	public static native int updateStbFile(String ip,int port,String path) ;
}
