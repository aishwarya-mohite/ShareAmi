package com.testnow.shareami.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityResult;

public class AmiSharingUtility {
	public static void shareAmi(AmazonEC2Client amazonEC2Client, String amisToShare, String destAccId) {
		boolean flag = false;
		DescribeImagesRequest request = new DescribeImagesRequest().withImageIds(amisToShare);
		DescribeImagesResult describeImagesResult = amazonEC2Client.describeImages(request);

		List<Image> images = describeImagesResult.getImages();
		for (Image img : images) {
			if (img.getImageId().equals(amisToShare)) {
				flag = true;
			}
		}
		if (flag == true) {
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
		for (TagDescription td : tagDiscription) {
			Tag t = new Tag();
			t.setKey(td.getKey());
			t.setValue(td.getValue());
			tag.add(t);
		}
		return tag;

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
}
