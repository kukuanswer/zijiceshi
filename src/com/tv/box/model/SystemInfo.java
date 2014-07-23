package com.tv.box.model;

public class SystemInfo {

	private String application;
	private String loader;
	private String HWVersion;
	private int releaseDate;
	private String default_db;
	private String lib;

	public String getApplication() {
		return application;
	}

	public String getHWVersion() {
		return HWVersion;
	}

	public int getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(int releaseDate) {
		this.releaseDate = releaseDate;
	}

	public void setHWVersion(String hWVersion) {
		HWVersion = hWVersion;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getLoader() {
		return loader;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	public String getDefault_db() {
		return default_db;
	}

	public void setDefault_db(String default_db) {
		this.default_db = default_db;
	}

	public String getLib() {
		return lib;
	}

	public void setLib(String lib) {
		this.lib = lib;
	}

}
