package com.testnow.shareami.model;

import java.util.List;

public class TargetProvider {
	private String access_key;
	private String secret_key;
	private List<String> regions;

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

	public List<String> getRegions() {
		return regions;
	}

	public void setRegions(List<String> regions) {
		this.regions = regions;
	}

	@Override
	public String toString() {
		return "TargetProvider [access_key=" + access_key + ", secret_key=" + secret_key + ", regions=" + regions + "]";
	}

}