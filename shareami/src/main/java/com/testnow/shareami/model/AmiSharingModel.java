package com.testnow.shareami.model;

import java.util.List;

public class AmiSharingModel {
	private SourceProvider sourceProvider;
	private List<String> amiIds;
	private List<TargetProvider> targetProviders;

	public SourceProvider getSourceProvider() {
		return sourceProvider;
	}

	public void setSourceProvider(SourceProvider sourceProvider) {
		this.sourceProvider = sourceProvider;
	}

	public List<String> getAmiIds() {
		return amiIds;
	}

	public void setAmiIds(List<String> amiIds) {
		this.amiIds = amiIds;
	}

	public List<TargetProvider> getTargetProviders() {
		return targetProviders;
	}

	public void setTargetProviders(List<TargetProvider> targetProviders) {
		this.targetProviders = targetProviders;
	}

	@Override
	public String toString() {
		return "AmiSharingModel [sourceProvider=" + sourceProvider + ", amiIds=" + amiIds + ", targetProviders="
				+ targetProviders + "]";
	}

}
