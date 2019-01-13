package com.testnow.shareami.model;

public class SourceProvider {
	private String access_key;
	private String secret_key;
	private String region;
	public String getAccess_key() {
		return access_key;
	}
	public void setAccess_key(String access_key) {
		this.access_key = access_key;
	}
	public String getSecret_key() {
		return secret_key;
	}
	public void setSecret_key(String secret_key) {
		this.secret_key = secret_key;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	@Override
	public String toString() {
		return "AwsCredential [access_key=" + access_key + ", secret_key=" + secret_key + ", region=" + region + "]";
	}

}
