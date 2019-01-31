package com.testnow.shareami.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.CopyImageRequest;
import com.amazonaws.services.ec2.model.CopyImageResult;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeTagsRequest;
import com.amazonaws.services.ec2.model.DescribeTagsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.LaunchPermission;
import com.amazonaws.services.ec2.model.LaunchPermissionModifications;
import com.amazonaws.services.ec2.model.ModifyImageAttributeRequest;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TagDescription;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.ListAccountAliasesResult;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;

public class AmiSharingUtility {
	public static void shareAmi(AmazonEC2Client amazonEC2Client, String amisToShare, String destAccId) {
		DescribeImagesRequest request = new DescribeImagesRequest().withImageIds(amisToShare);
		DescribeImagesResult describeImagesResult = amazonEC2Client.describeImages(request);

		if("available".equalsIgnoreCase(describeImagesResult.getImages().get(0).getState())) {
			System.out.println("AMI already Available in dest acc = "+destAccId);
		}
		else {
		String amiState = "pending";
		int maxRetry = 10, retryCount = 0;
		while (amiState.equalsIgnoreCase(describeImagesResult.getImages().get(0).getState())) {
			describeImagesResult = amazonEC2Client.describeImages(request);
			try {
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
			}
			System.out.println("Waiting for AMI to be availalbe...");
			retryCount++;
			if (retryCount > maxRetry) {
				System.out.println("Failed to copy and share ani " + amisToShare + " to dest acc " + destAccId);
				break;
			}
		}
		LaunchPermissionModifications withAdd = new LaunchPermissionModifications()
				.withAdd(new LaunchPermission().withUserId(destAccId));
		ModifyImageAttributeRequest requestWithLaunchPermission = new ModifyImageAttributeRequest()
				.withLaunchPermission(withAdd).withImageId(amisToShare);
		amazonEC2Client.modifyImageAttribute(requestWithLaunchPermission);
		}
	}

	public static String copyAmi(AmazonEC2Client amazonEC2Client, String amisToCopy, String sourceAccRegion) {
		CopyImageRequest request = new CopyImageRequest().withSourceImageId(amisToCopy)
				.withSourceRegion(sourceAccRegion);
		CopyImageResult response = amazonEC2Client.copyImage(request);
		return response.getImageId();
	}

	public static Collection<Tag> copyTags(String amisToCopy, AmazonEC2Client amazonEC2Client) {
		DescribeTagsRequest request = new DescribeTagsRequest()
				.withFilters(new Filter().withName("resource-id").withValues(amisToCopy));
		DescribeTagsResult response = amazonEC2Client.describeTags(request);
		List<TagDescription> tagDiscription = response.getTags();
		Collection<Tag> tag = new ArrayList<Tag>();
		if(!tagDiscription.isEmpty()) {
		for (TagDescription td : tagDiscription) {
			Tag t = new Tag();
			t.setKey(td.getKey());
			t.setValue(td.getValue());
			tag.add(t);
		}
		return tag;
		}
		else return null;

	}

	public static void createTagsToDestinationAcc(Collection<Tag> tags, AmazonEC2Client amazonEC2Client, String ami) {
		CreateTagsRequest request = new CreateTagsRequest().withResources(ami).withTags(tags);
		CreateTagsResult response = amazonEC2Client.createTags(request);
	}

	public static String getAccountIDUsingAccessKey(AWSCredentials credential) {
		AWSSecurityTokenService stsService = AWSSecurityTokenServiceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credential)).build();
		GetCallerIdentityResult callerIdentity = stsService.getCallerIdentity(new GetCallerIdentityRequest());
		return callerIdentity.getAccount();
	}

	public static String getAccountAlies(AWSCredentials credential) {
		final AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credential)).build();
		ListAccountAliasesResult response = iam.listAccountAliases();
		String alias = response.getAccountAliases().get(0);
		return alias;
	}
}
