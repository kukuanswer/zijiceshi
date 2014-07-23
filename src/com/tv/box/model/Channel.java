package com.tv.box.model;

public class Channel {

	private int lock;
	private int audioMode;
	private int videoPid;
	private int definition;
	private int satId;
	private int audioPid;
	private int audioEcmPid;
	private int channelId;
	private int tpId;
	private String name;
	private int audioVolume;
	private int groupNum;
	private int serviceId;
	private int subtitlePid;
	private int teletextPid;
	private int scrambled;
	private int pmtPid;
	private int pcrPid;
	private int originalId;
	private int videoType;
	private int audioType;
	private int serviceType;
	private int tsId;
	private int videoEcmPid;
	private int subtitleType;
	private int skip;
	private int bouquetId;
	private int casId;
	private int lcn;
	
	// wei.zhou
	private boolean selected ;
	private boolean locked ;

	public Channel(){
		setChannelId(-1);
	}
	
	public int getLock() {
		return lock;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setLock(int lock) {
		this.lock = lock;
	}

	public int getAudioMode() {
		return audioMode;
	}

	public void setAudioMode(int audioMode) {
		this.audioMode = audioMode;
	}

	public int getVideoPid() {
		return videoPid;
	}

	public void setVideoPid(int videoPid) {
		this.videoPid = videoPid;
	}

	public int getDefinition() {
		return definition;
	}

	public void setDefinition(int definition) {
		this.definition = definition;
	}

	public int getSatId() {
		return satId;
	}

	public void setSatId(int satId) {
		this.satId = satId;
	}

	public int getAudioPid() {
		return audioPid;
	}

	public void setAudioPid(int audioPid) {
		this.audioPid = audioPid;
	}

	public int getAudioEcmPid() {
		return audioEcmPid;
	}

	public void setAudioEcmPid(int audioEcmPid) {
		this.audioEcmPid = audioEcmPid;
	}

	public int getChannelId() {
		return channelId;
	}

	public void setChannelId(int channelId) {
		this.channelId = channelId;
	}

	public int getTpId() {
		return tpId;
	}

	public void setTpId(int tpId) {
		this.tpId = tpId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAudioVolume() {
		return audioVolume;
	}

	public void setAudioVolume(int audioVolume) {
		this.audioVolume = audioVolume;
	}

	public int getGroupNum() {
		return groupNum;
	}

	public void setGroupNum(int groupNum) {
		this.groupNum = groupNum;
	}

	public int getServiceId() {
		return serviceId;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public int getSubtitlePid() {
		return subtitlePid;
	}

	public void setSubtitlePid(int subtitlePid) {
		this.subtitlePid = subtitlePid;
	}

	public int getTeletextPid() {
		return teletextPid;
	}

	public void setTeletextPid(int teletextPid) {
		this.teletextPid = teletextPid;
	}

	public int getScrambled() {
		return scrambled;
	}

	public void setScrambled(int scrambled) {
		this.scrambled = scrambled;
	}

	public int getPmtPid() {
		return pmtPid;
	}

	public void setPmtPid(int pmtPid) {
		this.pmtPid = pmtPid;
	}

	public int getPcrPid() {
		return pcrPid;
	}

	public void setPcrPid(int pcrPid) {
		this.pcrPid = pcrPid;
	}

	public int getOriginalId() {
		return originalId;
	}

	public void setOriginalId(int originalId) {
		this.originalId = originalId;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getAudioType() {
		return audioType;
	}

	public void setAudioType(int audioType) {
		this.audioType = audioType;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public int getTsId() {
		return tsId;
	}

	public void setTsId(int tsId) {
		this.tsId = tsId;
	}

	public int getVideoEcmPid() {
		return videoEcmPid;
	}

	public void setVideoEcmPid(int videoEcmPid) {
		this.videoEcmPid = videoEcmPid;
	}

	public int getSubtitleType() {
		return subtitleType;
	}

	public void setSubtitleType(int subtitleType) {
		this.subtitleType = subtitleType;
	}

	public int getSkip() {
		return skip;
	}

	public void setSkip(int skip) {
		this.skip = skip;
	}

	public int getBouquetId() {
		return bouquetId;
	}

	public void setBouquetId(int bouquetId) {
		this.bouquetId = bouquetId;
	}

	public int getCasId() {
		return casId;
	}

	public void setCasId(int casId) {
		this.casId = casId;
	}

	public int getLcn() {
		return lcn;
	}

	public void setLcn(int lcn) {
		this.lcn = lcn;
	}

	@Override
	public boolean equals(Object o) {
		return getChannelId() == ((Channel)o).getChannelId();
	}
}
