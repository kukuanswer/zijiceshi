package com.tv.box.model;

public class SignalInfo {

	private int bitErrorRate;

	private int strength;

	private int noiseRatio;

	private String lockStatus;

	public int getBitErrorRate() {
		return bitErrorRate;
	}

	public void setBitErrorRate(int bitErrorRate) {
		this.bitErrorRate = bitErrorRate;
	}

	public int getStrength() {
		return strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getNoiseRatio() {
		return noiseRatio;
	}

	public void setNoiseRatio(int noiseRatio) {
		this.noiseRatio = noiseRatio;
	}

	public String getLockStatus() {
		return lockStatus;
	}

	public void setLockStatus(String lockStatus) {
		this.lockStatus = lockStatus;
	}

}
