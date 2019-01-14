package com.testnow.shareami.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Tag;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.testnow.shareami.model.AmiSharingModel;
import com.testnow.shareami.model.AmiSharingResult;
import com.testnow.shareami.model.TargetProvider;

public class ShareAmi {

	public static void shareAmiMain(String path) {

		ObjectMapper mapper = new ObjectMapper();
		File file = new File(path);
		List<AmiSharingResult> finalList = new ArrayList<AmiSharingResult>();
		AmiSharingModel amiSharingModel;
		try {
			amiSharingModel = mapper.readValue(file, AmiSharingModel.class);

			AWSCredentials sourceCredentials = new BasicAWSCredentials(
					amiSharingModel.getSourceProvider().getAccess_key(),
					amiSharingModel.getSourceProvider().getSecret_key());

			String sourceAccId = AmiSharingUtility.getAccountIDUsingAccessKey(sourceCredentials);

			String sourceAccRegion = amiSharingModel.getSourceProvider().getRegion();

			String sourceAccName = AmiSharingUtility.getAccountAlies(sourceCredentials);
			AmiConfig amiConfig = new AmiConfig();
			AmazonEC2Client amazonEC2ClientSource = amiConfig.getAwsClientWithRegion(sourceCredentials,
					sourceAccRegion);

			if (amiSharingModel.getAmiIds().size() > 0) {
				for (String ami : amiSharingModel.getAmiIds()) {
					System.out.println(
							"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
					if (amiSharingModel.getTargetProviders().size() > 0) {
						for (TargetProvider tProvider : amiSharingModel.getTargetProviders()) {
							System.out.println(
									">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
							AWSCredentials targetCredentials = new BasicAWSCredentials(tProvider.getAccess_key(),
									tProvider.getSecret_key());
							String targetAccId = AmiSharingUtility.getAccountIDUsingAccessKey(targetCredentials);
							String targetAccName = AmiSharingUtility.getAccountAlies(targetCredentials);
							AmiSharingResult amiSharingResult = new AmiSharingResult();
							if (!sourceAccId.equals(targetAccId)) {
								for (String targetRegion : tProvider.getRegions()) {
									AmazonEC2Client amazonEC2ClientTargetWithRegion = amiConfig
											.getAwsClientWithRegion(targetCredentials, targetRegion);
									if (sourceAccRegion.equals(targetRegion)) {
										System.out.println(
												"===================================================================================================");
										System.out.println("Sharing ami");
										AmiSharingUtility.shareAmi(amazonEC2ClientSource, ami, targetAccId);
										System.out.println("Source(accId= " + sourceAccId + " , region= "
												+ sourceAccRegion + " , amiID=" + ami + ")");
										System.out.println(
												"Target(accId= " + targetAccId + " , region= " + targetRegion + ")");

										Collection<Tag> tags = AmiSharingUtility.copyTags(ami, amazonEC2ClientSource);
										if (tags.size() > 0) {
											AmiSharingUtility.createTagsToDestinationAcc(tags,
													amazonEC2ClientTargetWithRegion, ami);
											System.out.println("Tags are copied to Target(accId= " + targetAccId
													+ " , region= " + targetRegion + ")");
										}
										amiSharingResult.setAccountName(targetAccName);
										amiSharingResult.setAmiId(ami);
										amiSharingResult.setRegion(targetRegion);
										finalList.add(amiSharingResult);
									} else {
										AmiSharingResult amiSharingResult1 = new AmiSharingResult();
										System.out.println(
												"====================================================================================================");
										AmazonEC2Client amazonEC2ClientSourceWithRegion = amiConfig
												.getAwsClientWithRegion(sourceCredentials, targetRegion);
										String amiToShare = AmiSharingUtility.copyAmi(amazonEC2ClientSourceWithRegion,
												ami, sourceAccRegion);
										System.out.println("Source(accId= " + sourceAccId + " , region= "
												+ sourceAccRegion + " , amiID= " + ami + ")");
										System.out.println(
												"Target(accId= " + sourceAccId + " , region= " + targetRegion + ")");
										System.out.println("Copying ami...");
										Collection<Tag> tags = AmiSharingUtility.copyTags(ami, amazonEC2ClientSource);
										if (tags.size() > 0) {
											AmiSharingUtility.createTagsToDestinationAcc(tags,
													amazonEC2ClientTargetWithRegion, amiToShare);
											System.out.println("Tags are copied to Target(accId= " + sourceAccId
													+ " , region= " + targetRegion + " , amiId= " + amiToShare + ")");

										}
										AmiSharingUtility.shareAmi(amazonEC2ClientSourceWithRegion, amiToShare,
												targetAccId);
										System.out.println("AMI shared");
										System.out.println("Source(accId= " + sourceAccId + " ,region= "
												+ sourceAccRegion + " , amiID= " + amiToShare + ")");
										System.out.println(
												"Target(accId= " + targetAccId + " ,region= " + targetRegion + ")");

										if (tags.size() > 0) {
											AmiSharingUtility.createTagsToDestinationAcc(tags,
													amazonEC2ClientTargetWithRegion, amiToShare);
											System.out.println("Tags are copied to Target(accId= " + targetAccId
													+ " , region= " + targetRegion + " , amiId= " + amiToShare + ")");

										}
										amiSharingResult1.setAccountName(targetAccName);
										amiSharingResult1.setAmiId(amiToShare);
										amiSharingResult1.setRegion(targetRegion);
										finalList.add(amiSharingResult1);
									}
								}
							} else {
								for (String targetRegion : tProvider.getRegions()) {
									AmiSharingResult amiSharingResult2 = new AmiSharingResult();
									System.out.println(
											"=========================================================================================================");
									System.out.println("Copying ami within same account");
									if (!sourceAccRegion.equals(targetRegion)) {
										AmazonEC2Client amazonEC2ClientTargetWithRegion = amiConfig
												.getAwsClientWithRegion(targetCredentials, targetRegion);

										String amiToShare = AmiSharingUtility.copyAmi(amazonEC2ClientTargetWithRegion,
												ami, sourceAccRegion);
										Collection<Tag> tags = AmiSharingUtility.copyTags(ami, amazonEC2ClientSource);
										System.out.println("Source(accId= " + sourceAccId + " , region= "
												+ sourceAccRegion + " , amiID= " + ami + ")");
										System.out.println(
												"Target(accId= " + sourceAccId + " , region= " + targetRegion + ")");
										if (tags.size() > 0) {
											AmiSharingUtility.createTagsToDestinationAcc(tags,
													amazonEC2ClientTargetWithRegion, amiToShare);
											System.out.println("Tags are copied to Target(accId= " + targetAccId
													+ " ,region= " + targetRegion + " , amiId= " + amiToShare + ")");

										}
										amiSharingResult2.setAccountName(targetAccName);
										amiSharingResult2.setAmiId(amiToShare);
										amiSharingResult2.setRegion(targetRegion);
										finalList.add(amiSharingResult2);
									}
								}
							}
						}
					}
				}
			}
			System.out.println(
					"=========================================================================================================");
			for (AmiSharingResult a : finalList) {
				System.out.println(a.toString());
			}
		} catch (JsonParseException e1) {
			System.out.println("Please enter proper json format");
		} catch (JsonMappingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
