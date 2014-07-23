package com.tv.box.model;

public class Transponder {

	private int tpId;
	private int type;
	private int satId;
	private int frequency;
	private int symbolRate;
	private int polarization;
	private int modulation;
	private int bandwidth;

	public int getType() {
		return type;
	}

	public int getTpId() {
		return tpId;
	}

	public void setTpId(int tpId) {
		this.tpId = tpId;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSatId() {
		return satId;
	}

	public void setSatId(int satId) {
		this.satId = satId;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getSymbolRate() {
		return symbolRate;
	}

	public void setSymbolRate(int symbolRate) {
		this.symbolRate = symbolRate;
	}

	public int getPolarization() {
		return polarization;
	}

	public void setPolarization(int polarization) {
		this.polarization = polarization;
	}

	public int getModulation() {
		return modulation;
	}

	public void setModulation(int modulation) {
		this.modulation = modulation;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

}
