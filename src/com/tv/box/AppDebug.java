package com.tv.box;

import android.util.Log;

public class AppDebug {

	public static void d(String tag, String message) {
		if (AppConfig.DEBUG) {
			Log.d(tag, "zhouwei : " + message);
		}
	}

	public static void e(String tag, String message) {
		if (AppConfig.DEBUG) {
			Log.e(tag, "zhouwei : " + message);
		}
	}

	public static void w(String tag, String message) {
		if (AppConfig.DEBUG) {
			Log.w(tag, "zhouwei : " + message);
		}
	}
}
