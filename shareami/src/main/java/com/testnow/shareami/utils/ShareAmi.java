package com.testnow.shareami.utils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Tag;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testnow.shareami.model.AmiSharingModel;
import com.testnow.shareami.model.TargetProvider;

public class ShareAmi {

	public static void shareAmiMain(String path) {

		ObjectMapper mapper = new ObjectMapper();
		File file = new File(path);
		AmiSharingModel amiSharingModel;
		try {
			amiSharingModel = mapper.readValue(file, AmiSharingModel.class);

			AWSCredentials sourceCredentials = new BasicAWSCredentials(
					amiSharingModel.getSourceProvider().getAccess_key(),
					amiSharingModel.getSourceProvider().getSecret_key());

			String sourceAccId = AmiSharingUtility.getAccountIDUsingAccessKey(sourceCredentials);

			String sourceAccRegion = amiSharingModel.getSourceProvider().getRegion();

			AmiConfig amiConfig = new AmiConfig();
			AmazonEC2Client amazonEC2ClientSource = amiConfig.getAwsClient(sourceCredentials);

			if (amiSharingModel.getAmiIds().size() > 0) {
				System.out.println(
						"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				for (String ami : amiSharingModel.getAmiIds()) {
					if (amiSharingModel.getTargetProviders().size() > 0) {
						for (TargetProvider tProvider : amiSharingModel.getTargetProviders()) {
							System.out.println(
									">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
							AWSCredentials targetCredentials = new BasicAWSCredentials(tProvider.getAccess_key(),
									tProvider.getSecret_key());
							String targetAccId = AmiSharingUtility.getAccountIDUsingAccessKey(targetCredentials);
							if (!sourceAccId.equals(targetAccId)) {
								for (String targetRegion : tProvider.getRegions()) {
									AmazonEC2Client amazonEC2ClientTargetWithRegion = amiConfig
											.getAwsClientWithRegion(targetCredentials, targetRegion);
									if (sourceAccRegion.equals(targetRegion)) {
										System.out.println(
												"===================================================================================================");
										System.out.println(
												"Sharing ami with other  account with both source and destination region as "
														+ targetRegion);
										AmiSharingUtility.shareAmi(amazonEC2ClientSource, ami, targetAccId);
										System.out.println("AMI " + ami + " shared in  region " + targetRegion
												+ " from source accId " + sourceAccId + " with dest accId "
												+ targetAccId);
										Collection<Tag> tags = AmiSharingUtility.copyTags(ami, amazonEC2ClientSource);
										if (tags.size() > 0) {
											AmiSharingUtility.createTagsToDestinationAcc(tags,
													amazonEC2ClientTargetWithRegion, ami);
											System.out.println("Tags " + tags + " are copied for ami " + ami
													+ " in region " + targetRegion + " from source accId " + sourceAccId
													+ " with dest accId " + targetAccId);
										}
									} else {
										System.out.println(
												"====================================================================================================");
										System.out.println(
												"Copying ami within same account so as to share it in destination accounts region "
														+ targetRegion);
										AmazonEC2Client amazonEC2ClientSourceWithRegion = amiConfig
												.getAwsClientWithRegion(sourceCredentials, targetRegion);
										String amiToShare = AmiSharingUtility.copyAmi(amazonEC2ClientSourceWithRegion,
												ami, sourceAccRegion);
										try {
											System.out.println("Copying ami...");
											TimeUnit.MINUTES.sleep(6);
										} catch (InterruptedException e) {
										}
										System.out.println("AMI " + ami + " copied with new name " + amiToShare
												+ " in region " + targetRegion + " from source accId " + sourceAccId
												+ " and source accRegion " + sourceAccRegion);

										AmiSharingUtility.shareAmi(amazonEC2ClientSourceWithRegion, amiToShare,
												targetAccId);
										System.out.println("AMI " + amiToShare + " shared  in region " + targetRegion
												+ " from source accId " + sourceAccId + " with dest accId "
												+ targetAccId);
										Collection<Tag> tags = AmiSharingUtility.copyTags(ami, amazonEC2ClientSource);
										if (tags.size() > 0) {
											AmiSharingUtility.createTagsToDestinationAcc(tags,
													amazonEC2ClientTargetWithRegion, amiToShare);
											System.out.println("Tags " + tags + " are copied  in region " + targetRegion
													+ " from source accId " + sourceAccId + " with dest accId "
													+ targetAccId);
										}
									}
								}
							} else {
								for (String targetRegion : tProvider.getRegions()) {
									System.out.println(
											"=========================================================================================================");
									System.out.println("Copying ami within same account");
									if (!sourceAccRegion.equals(targetRegion)) {
										AmazonEC2Client amazonEC2ClientTargetWithRegion = amiConfig
												.getAwsClientWithRegion(targetCredentials, targetRegion);

										String amiToShare = AmiSharingUtility.copyAmi(amazonEC2ClientTargetWithRegion,
												ami, sourceAccRegion);
										Collection<Tag> tags = AmiSharingUtility.copyTags(ami, amazonEC2ClientSource);
										try {
											System.out.println("Copying ami...");
											TimeUnit.MINUTES.sleep(6);
										} catch (InterruptedException e) {
										}
										System.out.println("AMI " + ami + " copied with new name " + amiToShare
												+ " in region " + targetRegion + " from source accId " + sourceAccId
												+ " and source accRegion " + sourceAccRegion);
										if (tags.size() > 0) {
											AmiSharingUtility.createTagsToDestinationAcc(tags,
													amazonEC2ClientTargetWithRegion, amiToShare);
											System.out.println("Tags " + tags + " are copied  in region " + targetRegion
													+ " from source accId " + sourceAccId + " and target accId "
													+ targetAccId);
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
