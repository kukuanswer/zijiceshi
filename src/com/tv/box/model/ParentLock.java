package com.tv.box.model;

public class ParentLock {

	private int channel_lock;
	private int menu_lock;
	private String password;
	private String passwd_channel;
	private String univeral_passwd;

	public int getChannel_lock() {
		return channel_lock;
	}

	public void setChannel_lock(int channel_lock) {
		this.channel_lock = channel_lock;
	}

	public int getMenu_lock() {
		return menu_lock;
	}

	public void setMenu_lock(int menu_lock) {
		this.menu_lock = menu_lock;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswd_channel() {
		return passwd_channel;
	}

	public void setPasswd_channel(String passwd_channel) {
		this.passwd_channel = passwd_channel;
	}

	public String getUniveral_passwd() {
		return univeral_passwd;
	}

	public void setUniveral_passwd(String univeral_passwd) {
		this.univeral_passwd = univeral_passwd;
	}

}
