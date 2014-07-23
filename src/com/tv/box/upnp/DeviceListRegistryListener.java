package com.tv.box.upnp;

import org.teleal.cling.model.meta.DeviceDetails;
import org.teleal.cling.model.meta.LocalDevice;
import org.teleal.cling.model.meta.RemoteDevice;
import org.teleal.cling.registry.DefaultRegistryListener;
import org.teleal.cling.registry.Registry;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

import com.tv.box.AppConfig;
import com.tv.box.AppPreferences;

public final class DeviceListRegistryListener extends DefaultRegistryListener {
	
	static final String TAG = "DeviceListRegistryListener" ;
	
	private Context 		mContext ;
	private Handler 		mHandler ;
	private String 			mHost ;
	
	public DeviceListRegistryListener(Context context){
		mContext	= context ;
		mHandler	= new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// Toast.makeText(mContext, "UPNP Service search " + msg.obj, Toast.LENGTH_SHORT).show();
			}
			
		} ;
		
		mHost	= getIPAddress(context);
	}
	
	@Override
	public void remoteDeviceDiscoveryStarted(Registry registry,
			RemoteDevice device) {
	}

	@Override
	public void remoteDeviceDiscoveryFailed(Registry registry,
			final RemoteDevice device, final Exception ex) {
	}

	@Override
	public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
		
		Log.d(TAG, "remoteDeviceAdded");
		
		if(device != null){
			
			DeviceDetails item = device.getDetails() ;
			String name = item.getFriendlyName() ;
			
			Log.d(TAG, "zhouwei : remoteDeviceAdded : " + name);
			
			if(name != null && name.equals(AppConfig.UPNP_NAME)){
				
				String number = item.getModelDetails().getModelNumber() ;
				String[] str = mHost.replace(".", "&").split("&");
				
				if(str != null && str.length > 2){
					
					String ip = str[0] + "." + str[1] + "." + number ;
					AppPreferences pf = AppPreferences.getPreferences() ;
					pf.putString(AppPreferences.KEY_ANDROID_BOX_IP, ip);
					
					mContext.sendBroadcast(new Intent(AppConfig.ACTION_SEARCH_BOX_IP));
				}
				
				mHandler.obtainMessage(0, name).sendToTarget() ;
			}
		}
	}

	@Override
	public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
		
	}

	@Override
	public void localDeviceAdded(Registry registry, LocalDevice device) {
		
	}

	@Override
	public void localDeviceRemoved(Registry registry, LocalDevice device) {
		
	}

	public void deviceAdded(final DeviceItem di) {
		
	}

	public void deviceRemoved(final DeviceItem di) {
		
	}
	
	private String getIPAddress(Context ctx){  
		
		WifiManager wifi_service = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();  
        WifiInfo wifiinfo = wifi_service.getConnectionInfo();  
        System.out.println("Wifi info----->"+wifiinfo.getIpAddress());  
        System.out.println("DHCP info gateway----->"+Formatter.formatIpAddress(dhcpInfo.gateway));  
        System.out.println("DHCP info netmask----->"+Formatter.formatIpAddress(dhcpInfo.netmask));  
        return Formatter.formatIpAddress(dhcpInfo.ipAddress);  
    }
}
