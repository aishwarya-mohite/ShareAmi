package com.testnow.shareami.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;

public class AmiConfig {
	
	public AmazonEC2Client getAwsClient(AWSCredentials credentials) {
		AmazonEC2Client amazonEC2Client = (AmazonEC2Client) AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		return amazonEC2Client;
	}
	public AmazonEC2Client getAwsClientWithRegion(AWSCredentials credentials,String region) {
		AmazonEC2Client amazonEC2Client = (AmazonEC2Client) AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(region).build();
		return amazonEC2Client;
	}

}
