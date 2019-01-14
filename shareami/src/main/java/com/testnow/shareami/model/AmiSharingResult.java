package com.testnow.shareami.model;

public class AmiSharingResult {
	private String accountName;
	private String amiId;
	private String region;
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getAmiId() {
		return amiId;
	}
	public void setAmiId(String amiId) {
		this.amiId = amiId;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	@Override
	public String toString() {
		return "AmiSharingResult [accountName=" + accountName + ", amiId=" + amiId + ", region=" + region + "]";
	}
	
	
}
