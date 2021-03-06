/*******************************************************************************
 * Copyright 2018 T Mobile, Inc. or its affiliates. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.tmobile.cso.pacman.inventory.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.apigateway.AmazonApiGateway;
import com.amazonaws.services.apigateway.AmazonApiGatewayClientBuilder;
import com.amazonaws.services.apigateway.model.GetRestApisRequest;
import com.amazonaws.services.apigateway.model.GetRestApisResult;
import com.amazonaws.services.apigateway.model.RestApi;
import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.cloudformation.AmazonCloudFormation;
import com.amazonaws.services.cloudformation.AmazonCloudFormationClientBuilder;
import com.amazonaws.services.cloudformation.model.DescribeStacksRequest;
import com.amazonaws.services.cloudformation.model.DescribeStacksResult;
import com.amazonaws.services.cloudformation.model.Stack;
import com.amazonaws.services.cloudfront.AmazonCloudFront;
import com.amazonaws.services.cloudfront.AmazonCloudFrontClientBuilder;
import com.amazonaws.services.cloudfront.model.DistributionSummary;
import com.amazonaws.services.cloudfront.model.ListDistributionsRequest;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ListTagsOfResourceRequest;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DescribeNatGatewaysRequest;
import com.amazonaws.services.ec2.model.DescribeNatGatewaysResult;
import com.amazonaws.services.ec2.model.DescribeNetworkInterfacesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeSnapshotsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVolumesResult;
import com.amazonaws.services.ec2.model.DescribeVpcEndpointsRequest;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.NatGateway;
import com.amazonaws.services.ec2.model.NetworkInterface;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.ec2.model.Snapshot;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Volume;
import com.amazonaws.services.ec2.model.Vpc;
import com.amazonaws.services.ec2.model.VpcEndpoint;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClientBuilder;
import com.amazonaws.services.elasticbeanstalk.model.ApplicationDescription;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentResourcesRequest;
import com.amazonaws.services.elasticbeanstalk.model.DescribeEnvironmentsRequest;
import com.amazonaws.services.elasticbeanstalk.model.EnvironmentDescription;
import com.amazonaws.services.elasticfilesystem.AmazonElasticFileSystem;
import com.amazonaws.services.elasticfilesystem.AmazonElasticFileSystemClientBuilder;
import com.amazonaws.services.elasticfilesystem.model.DescribeFileSystemsRequest;
import com.amazonaws.services.elasticfilesystem.model.DescribeFileSystemsResult;
import com.amazonaws.services.elasticfilesystem.model.DescribeTagsRequest;
import com.amazonaws.services.elasticfilesystem.model.FileSystemDescription;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClientBuilder;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.TagDescription;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetGroupsResult;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetHealthRequest;
import com.amazonaws.services.elasticloadbalancingv2.model.DescribeTargetHealthResult;
import com.amazonaws.services.elasticloadbalancingv2.model.LoadBalancer;
import com.amazonaws.services.elasticloadbalancingv2.model.TargetGroup;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.Cluster;
import com.amazonaws.services.elasticmapreduce.model.ClusterSummary;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterRequest;
import com.amazonaws.services.elasticmapreduce.model.DescribeClusterResult;
import com.amazonaws.services.elasticmapreduce.model.ListClustersRequest;
import com.amazonaws.services.elasticmapreduce.model.ListClustersResult;
import com.amazonaws.services.health.AWSHealth;
import com.amazonaws.services.health.AWSHealthClientBuilder;
import com.amazonaws.services.health.model.AffectedEntity;
import com.amazonaws.services.health.model.DescribeAffectedEntitiesRequest;
import com.amazonaws.services.health.model.DescribeAffectedEntitiesResult;
import com.amazonaws.services.health.model.DescribeEventDetailsRequest;
import com.amazonaws.services.health.model.DescribeEventsRequest;
import com.amazonaws.services.health.model.DescribeEventsResult;
import com.amazonaws.services.health.model.EntityFilter;
import com.amazonaws.services.health.model.Event;
import com.amazonaws.services.health.model.EventDetails;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.AccessKeyMetadata;
import com.amazonaws.services.identitymanagement.model.GetAccessKeyLastUsedRequest;
import com.amazonaws.services.identitymanagement.model.GetAccessKeyLastUsedResult;
import com.amazonaws.services.identitymanagement.model.GetLoginProfileRequest;
import com.amazonaws.services.identitymanagement.model.Group;
import com.amazonaws.services.identitymanagement.model.ListAccessKeysRequest;
import com.amazonaws.services.identitymanagement.model.ListGroupsForUserRequest;
import com.amazonaws.services.identitymanagement.model.ListMFADevicesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolesRequest;
import com.amazonaws.services.identitymanagement.model.ListRolesResult;
import com.amazonaws.services.identitymanagement.model.ListUsersRequest;
import com.amazonaws.services.identitymanagement.model.ListUsersResult;
import com.amazonaws.services.identitymanagement.model.LoginProfile;
import com.amazonaws.services.identitymanagement.model.Role;
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.kms.model.AliasListEntry;
import com.amazonaws.services.kms.model.DescribeKeyRequest;
import com.amazonaws.services.kms.model.DescribeKeyResult;
import com.amazonaws.services.kms.model.GetKeyRotationStatusRequest;
import com.amazonaws.services.kms.model.KeyListEntry;
import com.amazonaws.services.kms.model.ListResourceTagsRequest;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.FunctionConfiguration;
import com.amazonaws.services.lambda.model.ListFunctionsRequest;
import com.amazonaws.services.lambda.model.ListFunctionsResult;
import com.amazonaws.services.lambda.model.ListTagsRequest;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBCluster;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBSnapshot;
import com.amazonaws.services.rds.model.DescribeDBClustersRequest;
import com.amazonaws.services.rds.model.DescribeDBClustersResult;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsRequest;
import com.amazonaws.services.rds.model.DescribeDBSnapshotsResult;
import com.amazonaws.services.rds.model.ListTagsForResourceRequest;
import com.amazonaws.services.redshift.AmazonRedshift;
import com.amazonaws.services.redshift.AmazonRedshiftClientBuilder;
import com.amazonaws.services.redshift.model.DescribeClustersRequest;
import com.amazonaws.services.redshift.model.DescribeClustersResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.Tag;
import com.amazonaws.services.s3.model.TagSet;
import com.amazonaws.services.support.AWSSupport;
import com.amazonaws.services.support.AWSSupportClientBuilder;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorCheckResultRequest;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorCheckResultResult;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorChecksRequest;
import com.amazonaws.services.support.model.DescribeTrustedAdvisorChecksResult;
import com.amazonaws.services.support.model.RefreshTrustedAdvisorCheckRequest;
import com.amazonaws.services.support.model.TrustedAdvisorCheckDescription;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmobile.cso.pacman.inventory.InventoryConstants;
import com.tmobile.cso.pacman.inventory.file.ErrorManageUtil;
import com.tmobile.cso.pacman.inventory.file.FileGenerator;
import com.tmobile.cso.pacman.inventory.vo.AccessKeyMetadataVH;
import com.tmobile.cso.pacman.inventory.vo.BucketVH;
import com.tmobile.cso.pacman.inventory.vo.CheckVH;
import com.tmobile.cso.pacman.inventory.vo.ClassicELBVH;
import com.tmobile.cso.pacman.inventory.vo.CloudFrontVH;
import com.tmobile.cso.pacman.inventory.vo.DBClusterVH;
import com.tmobile.cso.pacman.inventory.vo.DBInstanceVH;
import com.tmobile.cso.pacman.inventory.vo.DynamoVH;
import com.tmobile.cso.pacman.inventory.vo.EbsVH;
import com.tmobile.cso.pacman.inventory.vo.EfsVH;
import com.tmobile.cso.pacman.inventory.vo.KMSKeyVH;
import com.tmobile.cso.pacman.inventory.vo.LambdaVH;
import com.tmobile.cso.pacman.inventory.vo.LoadBalancerVH;
import com.tmobile.cso.pacman.inventory.vo.PhdVH;
import com.tmobile.cso.pacman.inventory.vo.Resource;
import com.tmobile.cso.pacman.inventory.vo.TargetGroupVH;
import com.tmobile.cso.pacman.inventory.vo.UserVH;
import com.tmobile.cso.pacman.inventory.vo.VpcEndPointVH;
import com.tmobile.cso.pacman.inventory.vo.VpcVH;

/**
 * The Class InventoryUtil.
 */
public class InventoryUtil {
	
	/** The log. */
	private static Logger log = LogManager.getLogger(InventoryUtil.class);
	
	/** The delimiter. */
	private static String delimiter = FileGenerator.DELIMITER;
	
	/** The asg max record. */
	private static int asgMaxRecord = 100;
	
	/**
	 * Instantiates a new inventory util.
	 */
	private InventoryUtil(){
	}
	
	
	/**
	 * Fetch instances.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<Instance>> fetchInstances(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		
		Map<String,List<Instance>> instanceMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"EC2\" , \"region\":\"" ;
	
		for(Region region : RegionUtils.getRegions()) { 
			try{
			if(!skipRegions.contains(region.getName())){ 
				ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
				List<Instance> instanceList = new ArrayList<>();
				DescribeInstancesResult  descInstResult ;
				String nextToken = null;
				do{
					descInstResult =  ec2Client.describeInstances(new DescribeInstancesRequest().withNextToken(nextToken));
					descInstResult.getReservations().forEach(
							reservation -> instanceList.addAll(reservation.getInstances()));
					nextToken = descInstResult.getNextToken();
				}while(nextToken!=null);
				
				if(!instanceList.isEmpty() ) {
					log.debug(InventoryConstants.ACCOUNT + account + " Type : EC2 "+ region.getName()+" >> " + instanceList.size());
					instanceMap.put(account+delimiter+region.getName(), instanceList);
				}
		   	}
			}catch(Exception e){
		   		log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"ec2",e.getMessage());
		   	}
		}
		return instanceMap;
	}
	
	/**
	 * Fetch network intefaces.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<NetworkInterface>> fetchNetworkIntefaces(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		
		Map<String,List<NetworkInterface>> niMap = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Network Interface\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeNetworkInterfacesResult  descNIRslt =  ec2Client.describeNetworkInterfaces();
					List<NetworkInterface> niList = descNIRslt.getNetworkInterfaces();
					if(!niList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account + " Type : Network Interface " +region.getName()+" >> " + niList.size());
						niMap.put(account+delimiter+region.getName(),niList);
					}
					
				}
			}catch(Exception e){
				log.error("Exception fetching Network Interfaces for "+region.getName() + e);
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"eni",e.getMessage());
			}
		}
		return niMap;
	}
	
	/**
	 * Fetch security groups.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<SecurityGroup>> fetchSecurityGroups(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		
		Map<String,List<SecurityGroup>> secGrpList = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Security Group\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeSecurityGroupsResult rslt =  ec2Client.describeSecurityGroups();
					List<SecurityGroup> secGrpListTemp = rslt.getSecurityGroups();
					if( !secGrpListTemp.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Security Group "+region.getName()+" >> " + secGrpListTemp.size());
						secGrpList.put(account+delimiter+region.getName(),secGrpListTemp);
					}
	
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"sg",e.getMessage());
			}
		}
		return secGrpList;
	}

	
	/**
	 * Fetch asg.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<AutoScalingGroup>> fetchAsg(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		
		AmazonAutoScaling asgClient;
		Map<String,List<AutoScalingGroup>> asgList = new LinkedHashMap<>();
		
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"ASG\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){ 
			try{
				if(!skipRegions.contains(region.getName())){
					List<AutoScalingGroup> asgListTemp = new ArrayList<>();
					asgClient = AmazonAutoScalingClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextToken = null;
					DescribeAutoScalingGroupsResult  describeResult ;
					do{
						describeResult =  asgClient.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest().withNextToken(nextToken).withMaxRecords(asgMaxRecord));
						asgListTemp.addAll(describeResult.getAutoScalingGroups());
						nextToken = describeResult.getNextToken();
					}while(nextToken!=null);
					
					if(!asgListTemp.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account + " Type : ASG "+region.getName()+" >> " + asgListTemp.size());
						asgList.put(account+delimiter+region.getName(), asgListTemp);
					}
			   	}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"asg",e.getMessage());
			}
		}
		return asgList;
	}
	
	/**
	 * Fetch cloud formation stack.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<Stack>> fetchCloudFormationStack(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		AmazonCloudFormation cloudFormClient ;
		Map<String,List<Stack>> stacks = new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Stack\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){ 
			try{
				if(!skipRegions.contains(region.getName())){ 
					List<Stack> stacksTemp = new ArrayList<>();
					String nextToken = null;
					cloudFormClient = AmazonCloudFormationClientBuilder.standard().
							 withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeStacksResult describeResult ;
					do{
						describeResult =  cloudFormClient.describeStacks(new DescribeStacksRequest().withNextToken(nextToken));
						stacksTemp.addAll(describeResult.getStacks());
						nextToken = describeResult.getNextToken();
					}while(nextToken!=null);
					
					if(! stacksTemp.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Cloud Formation Stack "+region.getName() + " >> " + stacksTemp.size());
						stacks.put(account+delimiter+region.getName(), stacksTemp);
					}
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"stack",e.getMessage());
			}
		}
		return stacks;
	}
	
	/**
	 * Fetch dynamo DB tables.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<DynamoVH>> fetchDynamoDBTables(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		Map<String,List<DynamoVH>> dynamodbtables = new LinkedHashMap<>();
		
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"DynamoDB\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){ 
			try{
				if(!skipRegions.contains(region.getName())){ 
					AmazonDynamoDB awsClient= AmazonDynamoDBClientBuilder.standard().
						 withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					ListTablesResult listTableResult = awsClient.listTables();
					List<String> tables = listTableResult.getTableNames();
					List<DynamoVH> dynamodbtablesTemp = new ArrayList<>();
					tables.parallelStream().forEach(tblName -> {
						TableDescription table = awsClient.describeTable(tblName).getTable();
						List<com.amazonaws.services.dynamodbv2.model.Tag> tags = awsClient.listTagsOfResource(new ListTagsOfResourceRequest().withResourceArn( table.getTableArn())).getTags();
						synchronized (dynamodbtablesTemp) {
							dynamodbtablesTemp.add(new DynamoVH(table,tags));
						}
					
					});
					if(!dynamodbtablesTemp.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account +" Type : DynamoDB "+region.getName() + " >> "+dynamodbtablesTemp.size());
						dynamodbtables.put(account+delimiter+region.getName(), dynamodbtablesTemp);
					}
					
				}
			}catch(Exception e){
				if(region.isServiceSupported(AmazonDynamoDB.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"dynamodb",e.getMessage());
				}
			}
		}
		return dynamodbtables;
	}
	
	/**
	 * Fetch EFS info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<EfsVH>> fetchEFSInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		AmazonElasticFileSystem efsClient ;
		Map<String,List<EfsVH>> efsMap =  new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"EFS\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					efsClient = AmazonElasticFileSystemClientBuilder.standard().
							 withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<FileSystemDescription> efsListTemp = new ArrayList<>();
					String nextToken = null;
					DescribeFileSystemsResult descRslt ;
					do{
						descRslt = efsClient.describeFileSystems(new DescribeFileSystemsRequest().withMarker(nextToken));
						 efsListTemp.addAll(descRslt.getFileSystems());
						 nextToken = descRslt.getNextMarker();
					}while(nextToken!=null);
					
					List<EfsVH> efsList = new ArrayList<>();
					for(FileSystemDescription efs :efsListTemp ){
						efsList.add( new EfsVH(efs,
													efsClient.describeTags(new DescribeTagsRequest().withFileSystemId(efs.getFileSystemId())).getTags()));
					}
					if(! efsList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account + " Type : EFS "+region.getName() + " >> "+efsList.size());
						efsMap.put(account+delimiter+region.getName(), efsList);
					}
				}
			}catch(Exception e){
				if(region.isServiceSupported(AmazonElasticFileSystem.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"efs",e.getMessage());
				}
			}
		}
		return efsMap;
	}
	
	
	/**
	 * Fetch EMR info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<Cluster>> fetchEMRInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		
		Map<String,List<Cluster>> clusterList = new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"EMR\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					AmazonElasticMapReduce emrClient = AmazonElasticMapReduceClientBuilder.standard().
					 	withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<ClusterSummary> clusters = new ArrayList<>();
					String marker = null;
					ListClustersResult clusterResult ;
					do{
						clusterResult = emrClient.listClusters(new ListClustersRequest().withMarker(marker));
						clusters.addAll(clusterResult.getClusters());
						marker = clusterResult.getMarker();
					}while(marker!=null);
					
					List<Cluster> clustersList = new ArrayList<>();
					clusters.forEach(cluster -> 
						{
							DescribeClusterResult descClstrRslt = emrClient.describeCluster(new DescribeClusterRequest().withClusterId(cluster.getId()));
							clustersList.add(descClstrRslt.getCluster());
						});
					
					if( !clustersList.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account +" Type : EMR "+region.getName() + " >> "+clustersList.size());
						clusterList.put(account+delimiter+region.getName(),clustersList);
					}
				}	
			}catch(Exception e){
				if(region.isServiceSupported(AmazonElasticMapReduce.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"emr",e.getMessage());
				}
			}
		}
		return clusterList;
	}
	
	/**
	 * Fetch lambda info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static  Map<String,List<LambdaVH>> fetchLambdaInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		
		Map<String,List<LambdaVH>> functions = new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Lambda\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){
					AWSLambda lamdaClient = AWSLambdaClientBuilder.standard().
						 	withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					ListFunctionsResult listFnRslt ;
					List<FunctionConfiguration> functionsTemp ;
					List<LambdaVH> lambdaList = new ArrayList<>();
					String nextMarker = null;
					do{
						listFnRslt = lamdaClient.listFunctions(new ListFunctionsRequest().withMarker(nextMarker));
						functionsTemp = listFnRslt.getFunctions();
						if( !functionsTemp.isEmpty() ) {
							functionsTemp.forEach( function -> {
								Map<String,String> tags = lamdaClient.listTags(new ListTagsRequest().withResource(function.getFunctionArn())).getTags();
								LambdaVH  lambda = new LambdaVH(function, tags);
								lambdaList.add(lambda);
							});
						}
						nextMarker = listFnRslt.getNextMarker();
					}while(nextMarker!=null);
					
					if( !lambdaList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Lambda " +region.getName() + " >> "+lambdaList.size());
						functions.put(account+delimiter+region.getName(),lambdaList);
					}
				}
			}catch(Exception e){
				if(region.isServiceSupported(AWSLambda.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"lambda",e.getMessage());
				}
			}
		}
		return functions ;
	}
	
	/**
	 * Fetch classic elb info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<ClassicELBVH>> fetchClassicElbInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		
		Map<String,List<ClassicELBVH>> elbList = new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Classic ELB\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					AmazonElasticLoadBalancing elbClient = AmazonElasticLoadBalancingClientBuilder.standard().
							 	withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextMarker = null;
					List<LoadBalancerDescription> elbListTemp = new ArrayList<>();
					com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult elbDescResult ;
					do{
						elbDescResult = elbClient.describeLoadBalancers(new DescribeLoadBalancersRequest().withMarker(nextMarker));
						elbListTemp.addAll(elbDescResult.getLoadBalancerDescriptions());
						nextMarker = elbDescResult.getNextMarker();	
					}while(nextMarker!=null);
				
					List<ClassicELBVH> classicElbList = new ArrayList<>();
					if( !elbListTemp.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account + " Type : Classic ELB "+region.getName() + " >> "+elbListTemp.size());
						List<String> elbNames = elbListTemp.stream().map(elb -> { return elb.getLoadBalancerName();}).collect(Collectors.toList());
						List<TagDescription> tagDescriptions = new ArrayList<>();
						List<String> elbNamesTemp = new ArrayList<>();
						int i=0;
						for(String elbName : elbNames){
							i++;
							elbNamesTemp.add(elbName);
							if(i%20==0){
								tagDescriptions.addAll(elbClient.describeTags( new com.amazonaws.services.elasticloadbalancing.model.DescribeTagsRequest().withLoadBalancerNames(elbNamesTemp)).getTagDescriptions());
								elbNamesTemp = new ArrayList<>();
							}
							
						}
						if(!elbNamesTemp.isEmpty())
							tagDescriptions.addAll(elbClient.describeTags( new com.amazonaws.services.elasticloadbalancing.model.DescribeTagsRequest().withLoadBalancerNames(elbNamesTemp)).getTagDescriptions());
							
						elbListTemp.parallelStream().forEach(elb->	{		
								List<List<com.amazonaws.services.elasticloadbalancing.model.Tag>> tagsInfo =  tagDescriptions.stream().filter(tag -> tag.getLoadBalancerName().equals( elb.getLoadBalancerName())).map(x-> x.getTags()).collect(Collectors.toList());
								List<com.amazonaws.services.elasticloadbalancing.model.Tag> tags = new ArrayList<>();
								if(!tagsInfo.isEmpty())
									tags = tagsInfo.get(0);
								classicElbList.add(new ClassicELBVH(elb,tags));
							});			
						elbList.put(account+delimiter+region.getName(),classicElbList);
					}
					
					
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"classicelb",e.getMessage());
			}
		}
		return elbList;
	}
	
	/**
	 * Fetch elb info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<LoadBalancerVH>> fetchElbInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing elbClient ;
		Map<String,List<LoadBalancerVH>> elbMap = new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Application ELB\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					elbClient = com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder.standard().
						 	withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextMarker = null;
					DescribeLoadBalancersResult descElbRslt ;
					List<LoadBalancer> elbList = new ArrayList<>();
					do{
						descElbRslt = elbClient.describeLoadBalancers(new com.amazonaws.services.elasticloadbalancingv2.model.DescribeLoadBalancersRequest().withMarker(nextMarker));
						elbList.addAll(descElbRslt.getLoadBalancers());
						nextMarker = descElbRslt.getNextMarker();
					}while(nextMarker!=null);
					
					if(! elbList.isEmpty() ) {
						List<LoadBalancerVH> elbListTemp = new ArrayList<>();
						List<String> elbArns = elbList.stream().map(LoadBalancer::getLoadBalancerArn).collect(Collectors.toList());
						List<com.amazonaws.services.elasticloadbalancingv2.model.TagDescription> tagDescriptions = new ArrayList<>();
						int i = 0;
						List<String> elbArnsTemp  = new ArrayList<>();
						for(String elbArn : elbArns){
							i++;
							elbArnsTemp.add(elbArn);
							if(i%20 == 0){
								tagDescriptions.addAll(elbClient.describeTags(new com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsRequest().withResourceArns(elbArnsTemp)).getTagDescriptions());
								elbArnsTemp  = new ArrayList<>();
							}
							
						}
						if(!elbArnsTemp.isEmpty())
							tagDescriptions.addAll(elbClient.describeTags(new com.amazonaws.services.elasticloadbalancingv2.model.DescribeTagsRequest().withResourceArns(elbArnsTemp)).getTagDescriptions());
						
						elbList.parallelStream().forEach(elb->	{		
							List<List<com.amazonaws.services.elasticloadbalancingv2.model.Tag>> tagsInfo =  tagDescriptions.stream().filter(tag -> tag.getResourceArn().equals( elb.getLoadBalancerArn())).map(x-> x.getTags()).collect(Collectors.toList());
							List<com.amazonaws.services.elasticloadbalancingv2.model.Tag> tags = new ArrayList<>();
							if(!tagsInfo.isEmpty())
								tags = tagsInfo.get(0);
							LoadBalancerVH elbTemp = new LoadBalancerVH(elb,tags);
							elbListTemp.add(elbTemp);
						});	
						
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Application ELB " +region.getName() + " >> "+elbListTemp.size());
						elbMap.put(account+delimiter+region.getName(),elbListTemp);
					}
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"appelb",e.getMessage());
			}
		}
		return elbMap;	
	}
	
	/**
	 * Fetch target groups.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<TargetGroupVH>> fetchTargetGroups(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancing elbClient ;
		Map<String,List<TargetGroupVH>> targetGrpMap = new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Target Group\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					elbClient = com.amazonaws.services.elasticloadbalancingv2.AmazonElasticLoadBalancingClientBuilder.standard().
						 	withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextMarker = null;
					List<TargetGroupVH> targetGrpList = new ArrayList<>();
					do{
						DescribeTargetGroupsResult  trgtGrpRslt =  elbClient.describeTargetGroups(new DescribeTargetGroupsRequest().withMarker(nextMarker));
						List<TargetGroup> targetGrpListTemp = trgtGrpRslt.getTargetGroups();
						for(TargetGroup tg : targetGrpListTemp) {
							DescribeTargetHealthResult rslt =  elbClient.describeTargetHealth(new DescribeTargetHealthRequest().withTargetGroupArn(tg.getTargetGroupArn()));
							targetGrpList.add(new TargetGroupVH(tg, rslt.getTargetHealthDescriptions()));
						}
						nextMarker = trgtGrpRslt.getNextMarker();
					}while(nextMarker!=null);
					
					if( !targetGrpList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Target Group " +region.getName() + "-"+targetGrpList.size());
						targetGrpMap.put(account+delimiter+region.getName(), targetGrpList);
					}
					
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"targetgroup",e.getMessage());
			}
		}
		return targetGrpMap;
	}
	
	/**
	 * Fetch NAT gateway info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<NatGateway>> fetchNATGatewayInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		Map<String,List<NatGateway>> natGatwayMap =  new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Nat Gateway\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeNatGatewaysResult rslt = ec2Client.describeNatGateways(new DescribeNatGatewaysRequest());
					List<NatGateway> natGatwayList =rslt.getNatGateways();
					if(! natGatwayList.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account + " Type : Nat Gateway "+region.getName() + " >> "+natGatwayList.size());
						natGatwayMap.put(account+delimiter+region.getName(), natGatwayList);
					}
					
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"nat",e.getMessage());
			}
		}
		return natGatwayMap;
	}
	
	/**
	 * Fetch RDS cluster info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<DBClusterVH>> fetchRDSClusterInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		Map<String,List<DBClusterVH>> rdsMap =  new LinkedHashMap<>();
		AmazonRDS rdsClient ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"RDS Cluster\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					rdsClient = AmazonRDSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeDBClustersResult rslt ;
					String nextMarker = null;
					List<DBClusterVH> rdsList = new ArrayList<>();
					do{
						rslt = rdsClient.describeDBClusters( new DescribeDBClustersRequest().withMarker(nextMarker));
						List<DBCluster> rdsListTemp = rslt.getDBClusters();
						for(DBCluster cluster : rdsListTemp){
							DBClusterVH vh = new DBClusterVH(cluster,rdsClient.listTagsForResource(new ListTagsForResourceRequest().
									withResourceName(cluster.getDBClusterArn())).
									getTagList());
							rdsList.add(vh);
						}
						nextMarker = rslt.getMarker();
					}while(nextMarker!=null);
					
					if( !rdsList.isEmpty() ){ 
						log.debug(InventoryConstants.ACCOUNT + account +" Type : RDS Cluster "+region.getName() + " >> "+rdsList.size());
						rdsMap.put(account+delimiter+region.getName(), rdsList);
					}
				}
			}catch(Exception e){
				if(region.isServiceSupported(AmazonRDS.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"rdscluster",e.getMessage());
				}
			}
		}
		return rdsMap;
	}
	
	/**
	 * Fetch RDS instance info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<DBInstanceVH>> fetchRDSInstanceInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		Map<String,List<DBInstanceVH>> dbInstMap =  new LinkedHashMap<>();
		AmazonRDS rdsClient ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"RDS Instance\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					rdsClient = AmazonRDSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextMarker = null;
					DescribeDBInstancesResult rslt;
					List<DBInstanceVH> dbInstList = new ArrayList<>();
					do{
						rslt = rdsClient.describeDBInstances(new DescribeDBInstancesRequest().withMarker(nextMarker));
						List<DBInstance> dbInstListTemp = rslt.getDBInstances();
						for(DBInstance db : dbInstListTemp){
							DBInstanceVH vh = new DBInstanceVH(db, rdsClient.listTagsForResource(new ListTagsForResourceRequest().
														withResourceName(db.getDBInstanceArn())).
															getTagList());
							dbInstList.add(vh);
						}
						nextMarker = rslt.getMarker();
					}while(nextMarker!=null);
					
					if(! dbInstList.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account +" Type : RDS Instance" +region.getName() + " >> "+dbInstList.size());
						dbInstMap.put(account+delimiter+region.getName(),  dbInstList);
					}
				}
			}catch(Exception e){
				if(region.isServiceSupported(AmazonRDS.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"rdsdb",e.getMessage());
				}
			}
		}
		return dbInstMap;
	}
	
	/**
	 * Fetch S 3 info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the list
	 */
	public static List<BucketVH>  fetchS3Info(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource\" ,\"type\": \"S3\" , \"Bucket\":\"" ;
		AmazonS3 amazonS3Client ;
		List<BucketVH> buckets = new ArrayList<>();
		/* A region is needed for the client and setting to us-east-1 is causing issues */
		amazonS3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(InventoryConstants.REGION_US_WEST_2).build();
		List<Bucket> s3buckets = amazonS3Client.listBuckets();
		log.debug(InventoryConstants.ACCOUNT + account +" Type : S3 "+  " >> "+s3buckets.size());
		Map<String,AmazonS3> regionS3map = new HashMap<>();
		for(Region region : RegionUtils.getRegions()){
			if(!skipRegions.contains(region.getName())){ 
				regionS3map.put(region.getName(), AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build());
			}	
		}
		s3buckets.parallelStream().forEach(bucket -> {
			String bucketRegion ="";
			BucketVersioningConfiguration versionconfig = null;
			List<Tag> tags = new ArrayList<>();
			try{
				String bucketLocation = amazonS3Client.getBucketLocation(bucket.getName());
				bucketRegion = com.amazonaws.services.s3.model.Region.fromValue(bucketLocation).toAWSRegion().getName();
				AmazonS3 s3Client = regionS3map.get(bucketRegion);
				versionconfig =  s3Client.getBucketVersioningConfiguration(bucket.getName());
				BucketTaggingConfiguration tagConfig = s3Client.getBucketTaggingConfiguration(bucket.getName());
				if(tagConfig!=null){
					List<TagSet> tagSets = tagConfig.getAllTagSets();
					for(TagSet ts : tagSets){
						Map<String,String> tagsTemp = ts.getAllTags();
						Iterator<Entry<String,String>> it = tagsTemp.entrySet().iterator();
						while(it.hasNext()){
							Entry<String,String> tag = it.next();
							tags.add(new Tag(tag.getKey(),tag.getValue()));
						}
					}
				}
				buckets.add(new BucketVH(bucket,bucketRegion,versionconfig, tags));
			}
			catch(AmazonS3Exception e){
				if("AccessDenied".equals(e.getErrorCode())){
					log.info("Access Denied for bucket " + bucket.getName());
					buckets.add(new BucketVH(bucket,"",versionconfig, tags));
				}else{
					log.info(e);
				}
			}
			catch(Exception e){
				log.warn(expPrefix+ bucket.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,"","s3",e.getMessage());
			}
		});
		return buckets;
	}

	/**
	 * Fetch subnets.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<Subnet>> fetchSubnets(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		Map<String,List<Subnet>> subnets = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Subnet\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeSubnetsResult rslt = ec2Client.describeSubnets();
					List<Subnet> subnetsTemp =rslt.getSubnets();
					if(! subnetsTemp.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Subnet "+region.getName() + " >> "+subnetsTemp.size());
						subnets.put(account+delimiter+region.getName(),subnetsTemp);
					}
					
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"subnet",e.getMessage());
			}
		}
		
		return subnets;
	}

	/**
	 * Fetch trusterd advisors checks.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param account the account
	 * @return the list
	 */
	public static List<CheckVH> fetchTrusterdAdvisorsChecks(BasicSessionCredentials temporaryCredentials,String account ) {
		List<CheckVH> checkList = new ArrayList<>();
		AWSSupport awsSupportClient = AWSSupportClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion("us-east-1").build();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource\" ,\"type\": \"Trusted Advisor Check\"" ;
		List<String> checkids = new ArrayList<>();
		try{ 
			DescribeTrustedAdvisorChecksResult rslt = awsSupportClient.describeTrustedAdvisorChecks(new DescribeTrustedAdvisorChecksRequest().withLanguage("en"));
			List<TrustedAdvisorCheckDescription> trstdAdvsrList = rslt.getChecks();
			for(TrustedAdvisorCheckDescription check : trstdAdvsrList){
				try{
					checkids.add(check.getId());
					DescribeTrustedAdvisorCheckResultResult result =
								awsSupportClient.describeTrustedAdvisorCheckResult(new DescribeTrustedAdvisorCheckResultRequest().withCheckId(check.getId()));
					List<String> metadata = check.getMetadata();
					
					if(!"OK".equalsIgnoreCase(result.getResult().getStatus())){
						
						CheckVH checkVH = new CheckVH(check,result.getResult().getStatus());
						List<Resource> resources = new ArrayList<>();
						checkVH.setResources(resources);
						// TODO : Raise a ticket with AWS to fix this API issue
						if( ("ePs02jT06w".equalsIgnoreCase(check.getId()) || "rSs93HQwa1".equalsIgnoreCase(check.getId())) && !result.getResult().getFlaggedResources().isEmpty() ){
							int dataSize = result.getResult().getFlaggedResources().get(0).getMetadata().size() ;
							if(dataSize == metadata.size()+1 && !metadata.contains("Status")){
								metadata.add(0, "Status");
							}
						}
						
						result.getResult().getFlaggedResources().forEach(
							rsrc -> {
								List<String> data = rsrc.getMetadata();
								StringBuilder resounceInfo =  new StringBuilder("{");
								if(data.size() == metadata.size() ){
									
									for(int i=0;i<metadata.size();i++){
										resounceInfo.append("\""+metadata.get(i)+"\":\""+data.get(i)+"\",");
									}
									resounceInfo.deleteCharAt(resounceInfo.length()-1);
								}
								resounceInfo.append("}");
								resources.add(new Resource(check.getId(),rsrc.getResourceId(),rsrc.getStatus(),resounceInfo.toString()));
								
							}
					    );
						checkList.add(checkVH);
					}
				}catch(Exception e){
					log.debug(e.getMessage());
					log.error(e);
				}	
			}
		}catch(Exception e){
			log.error(expPrefix +", \"cause\":\"" +e.getMessage()+"\"}");
			ErrorManageUtil.uploadError(account,"","checks",e.getMessage());
		}	
		log.debug(InventoryConstants.ACCOUNT + account + " Type : Trusted Advisor Check " +checkList.size());
		
		for(String checkId : checkids){
			try{
				awsSupportClient.refreshTrustedAdvisorCheck(new RefreshTrustedAdvisorCheckRequest().withCheckId(checkId));
			}catch(Exception e){
				log.info(e.getMessage());
			}
		}
		
		return checkList;
	}

	/**
	 * Fetch redshift info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<com.amazonaws.services.redshift.model.Cluster>> fetchRedshiftInfo(BasicSessionCredentials temporaryCredentials,String skipRegions,String account) {
		Map<String,List<com.amazonaws.services.redshift.model.Cluster>> redshiftMap = new LinkedHashMap<>();
		AmazonRedshift redshiftClient ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Redshift\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					redshiftClient = AmazonRedshiftClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					String nextMarker = null;
					DescribeClustersResult result;
					List<com.amazonaws.services.redshift.model.Cluster> redshiftList = new ArrayList<>();
					do{
						 result= redshiftClient.describeClusters(new DescribeClustersRequest().withMarker(nextMarker));
						 redshiftList.addAll(result.getClusters());
						 nextMarker = result.getMarker();
					}while(nextMarker!=null);
					
					if(!redshiftList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Redshift " +region.getName() + " >> "+redshiftList.size());
						redshiftMap.put(account+delimiter+region.getName(),redshiftList);
					}
					
				}

			}catch(Exception e){
				if(region.isServiceSupported(AmazonRedshift.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"redshift",e.getMessage());
				}
			}
		}
		return redshiftMap;
	}

	/**
	 * Fetch volumet info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<Volume>> fetchVolumetInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		Map<String,List<Volume>> volumeList = new LinkedHashMap<>();
		AmazonEC2 ec2Client ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Volume\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeVolumesResult  rslt = ec2Client.describeVolumes(); // No need to paginate as all volumes will be returned.
					List<Volume> volumeListTemp = rslt.getVolumes();
					
					if( !volumeListTemp.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Volume "+region.getName() + " >> "+volumeListTemp.size());
						volumeList.put(account+delimiter+region.getName(),volumeListTemp);
					}
				}
					
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"volume",e.getMessage());
			}
		}
		return volumeList;
	}

	/**
	 * Fetch snapshots.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<Snapshot>> fetchSnapshots(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		Map<String,List<Snapshot>> snapShots = new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Snapshot\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<Snapshot> snapShotsList = ec2Client.describeSnapshots(new DescribeSnapshotsRequest().withOwnerIds(account)).getSnapshots();// No need to paginate as all results will be returned
					if( !snapShotsList.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account +" Type : Snapshot " +region.getName() + " >> "+snapShotsList.size());
						snapShots.put(account+delimiter+region.getName(),snapShotsList);
					}      
				}
					
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"snapshot",e.getMessage());
			}
		}
		return snapShots;
	}

	/**
	 * Fetch vpc info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String,List<VpcVH>> fetchVpcInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		Map<String,List<VpcVH>> vpcMap = new LinkedHashMap<>();
		
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"Vpc\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					List<VpcVH> vpcList = new ArrayList<>();
					AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<Vpc> tmpVpcList = ec2Client.describeVpcs().getVpcs();
					tmpVpcList.stream().forEach(vpc -> {
						VpcVH vpcVH = new VpcVH();
						vpcVH.setVpc(vpc);
						List<VpcEndpoint> vpcEndPoints = ec2Client.describeVpcEndpoints(new DescribeVpcEndpointsRequest().withFilters(new Filter("vpc-id",Arrays.asList(vpc.getVpcId())))).getVpcEndpoints();
						List<VpcEndPointVH> vpcEndPointsList = new ArrayList<>();
						vpcEndPoints.stream().forEach(vpcEndPoint -> {
							VpcEndPointVH  vpcEndPointVH = new VpcEndPointVH(vpcEndPoint);
							vpcEndPointVH.setPublicAccess(false);
							ObjectMapper mapper = new ObjectMapper();
							Map<String, Object> policyDoc = new HashMap<>();
							try {
								policyDoc = mapper.readValue(vpcEndPoint.getPolicyDocument(), new TypeReference<Map<String, Object>>(){});
								Map statement = (Map)((ArrayList)policyDoc.get("Statement")).get(0);
								if(statement.get("Effect").equals("Allow") && statement.get("Resource").equals("*")) {
									vpcEndPointVH.setPublicAccess(true);
								}
							} catch (Exception e) {
								log.error(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
							}
							vpcEndPointsList.add(vpcEndPointVH);
						});
						vpcVH.setVpcEndPoints(vpcEndPointsList);
						vpcList.add(vpcVH);
					});
					if(!vpcList.isEmpty()) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : VPC "+region.getName() + " >> "+vpcList.size());
						vpcMap.put(account+delimiter+region.getName(),vpcList);
					}
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"VPC",e.getMessage());
			}
		}
		return vpcMap;
	}
	
	/**
	 * Fetch api gateways.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<RestApi>> fetchApiGateways(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		Map<String,List<RestApi>> apiGateWays = new LinkedHashMap<>();
		
		AmazonApiGateway apiGatWayClient ;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"API\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					apiGatWayClient = AmazonApiGatewayClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<RestApi> apiGateWaysList = new ArrayList<>();
					String position = null;
					GetRestApisResult rslt ;
					do{
						rslt = apiGatWayClient.getRestApis(new GetRestApisRequest().withPosition(position));
						apiGateWaysList.addAll(rslt.getItems());
						position = rslt.getPosition();
					}while(position!=null);
					
					if( !apiGateWaysList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : ApiGateway "+region.getName() + " >> "+apiGateWaysList.size());
						apiGateWays.put(account+delimiter+region.getName(),apiGateWaysList);
					}
					
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"api",e.getMessage());
			}
		}
		return apiGateWays;
	}
	
	/**
	 * Fetch IAM users.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<UserVH>> fetchIAMUsers(BasicSessionCredentials temporaryCredentials,String account) {
	
	    String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"IAM\"" ;
	    
		AmazonIdentityManagement iamClient = AmazonIdentityManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(InventoryConstants.REGION_US_WEST_2).build();
		String marker = null;
		List<User> users = new ArrayList<>();
		ListUsersResult rslt;
		do{
			rslt = iamClient.listUsers(new ListUsersRequest().withMarker(marker));
			users.addAll(rslt.getUsers());
			marker = rslt.getMarker();
		}while(marker!=null);
		
		List<UserVH> userList = new ArrayList<>();
		Map<String,List<UserVH>> iamUsers = new HashMap<>();
		iamUsers.put(account, userList);
		users.parallelStream().forEach(user -> {
			UserVH userTemp = new UserVH(user);
			String userName = user.getUserName();
			List<AccessKeyMetadata> accessKeys = iamClient.listAccessKeys(new ListAccessKeysRequest().withUserName(userName)).getAccessKeyMetadata();
			List<AccessKeyMetadataVH> accessKeysTemp = new ArrayList<>();
			userTemp.setAccessKeys(accessKeysTemp);
			try {
				if(!CollectionUtils.isEmpty(accessKeys)){
					accessKeys.stream().forEach(accesskeyInfo -> {
						GetAccessKeyLastUsedResult accessKeyLastUsedResult = iamClient.getAccessKeyLastUsed(new GetAccessKeyLastUsedRequest().withAccessKeyId(accesskeyInfo.getAccessKeyId()));
						AccessKeyMetadataVH accessKeyVH = new AccessKeyMetadataVH(accesskeyInfo);
						accessKeysTemp.add(accessKeyVH);
						if(accessKeyLastUsedResult != null) {
							accessKeyVH.setLastUsedDate(accessKeyLastUsedResult.getAccessKeyLastUsed().getLastUsedDate());
							
						}
					});
				}
			}
			catch (Exception e){
			    log.warn(expPrefix+ InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
			    ErrorManageUtil.uploadError(account,"","IAM",e.getMessage());
			}
			
			try{
				LoginProfile logProf =  iamClient.getLoginProfile(new GetLoginProfileRequest().withUserName(userName)).getLoginProfile();
				userTemp.setPasswordCreationDate(logProf.getCreateDate());
				userTemp.setPasswordResetRequired(logProf.isPasswordResetRequired());
			}catch (Exception e) {
				// Ignore as there may not be login profile for all users
			}
			List<Group> groups = iamClient.listGroupsForUser(new ListGroupsForUserRequest().withUserName(userName)).getGroups();
			List<String> groupsList = new ArrayList<>();
			for(Group grp : groups){
				groupsList.add(grp.getGroupName());
			}
			userTemp.setGroups(groupsList);
			userTemp.setMfa(!iamClient.listMFADevices(new ListMFADevicesRequest().withUserName(userName)).getMFADevices().isEmpty());
			synchronized (userList) {
				userList.add(userTemp);
			}
		});
		log.debug(InventoryConstants.ACCOUNT + account +" Type : IAM User >> "+userList.size());
		return iamUsers;
	}
	
	/**
	 * Fetch IAM roles.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param account the account
	 * @return the map
	 */
	public static  Map<String,List<Role>>  fetchIAMRoles(BasicSessionCredentials temporaryCredentials,String account) {
		
		AmazonIdentityManagement iamClient = AmazonIdentityManagementClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(InventoryConstants.REGION_US_WEST_2).build();
		List<Role> roles = new ArrayList<>();
		ListRolesResult rslt;
		String marker = null;
		do{
			rslt =  iamClient.listRoles(new ListRolesRequest().withMarker(marker));
			roles.addAll(rslt.getRoles());
			marker = rslt.getMarker();
		}while(marker!=null);
		
		log.debug(InventoryConstants.ACCOUNT + account +" Type : IAM Roles >> "+roles.size());
		Map<String,List<Role>> iamRoles = new HashMap<>();
		iamRoles.put(account, roles);
		return iamRoles;
	}
	
	/**
	 * Fetch RDSDB snapshots.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<DBSnapshot>> fetchRDSDBSnapshots(BasicSessionCredentials temporaryCredentials, String skipRegions,String account){
		Map<String,List<DBSnapshot>> snapshots =  new LinkedHashMap<>();
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"RDS Snapshot\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()){
			try{
				if(!skipRegions.contains(region.getName())){ 
					AmazonRDS rdsClient = AmazonRDSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					DescribeDBSnapshotsResult rslt ;
					List<DBSnapshot> snapshotsTemp = new ArrayList<>();
					String marker = null;
					do{
						rslt = rdsClient.describeDBSnapshots(new DescribeDBSnapshotsRequest().withIncludePublic(false).withIncludeShared(false).withMarker(marker));
						snapshotsTemp.addAll(rslt.getDBSnapshots());
						marker = rslt.getMarker();
					}while(marker!=null);
		
					if(! snapshotsTemp.isEmpty() ){
						log.debug(InventoryConstants.ACCOUNT + account +" Type : RDS Snapshot" +region.getName() + " >> "+snapshotsTemp.size());
						snapshots.put(account+delimiter+region.getName(),  snapshotsTemp);
					}
				}
				
			}catch(Exception e){
				if(region.isServiceSupported(AmazonRDS.ENDPOINT_PREFIX)){
					log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
					ErrorManageUtil.uploadError(account,region.getName(),"rdssnapshot",e.getMessage());
				}
			}
		}
		return snapshots;
	}
	
	/**
	 * Fetch KMS keys.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<KMSKeyVH>> fetchKMSKeys(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		
		Map<String,List<KMSKeyVH>> kmsKeys = new LinkedHashMap<>();
		AWSKMS awskms;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"KMS\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					awskms = AWSKMSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<KeyListEntry> regionKeys = awskms.listKeys().getKeys();
					List<AliasListEntry> regionKeyAliases = awskms.listAliases().getAliases();
					if(!regionKeys.isEmpty()) {
						List<KMSKeyVH> kmsKeysList = new ArrayList<>();
						for(KeyListEntry key : regionKeys) {
							KMSKeyVH kmsKey = new KMSKeyVH();
							try {
								DescribeKeyResult result = awskms.describeKey(new DescribeKeyRequest().withKeyId(key.getKeyId()));
								kmsKey.setKey(result.getKeyMetadata());
								kmsKey.setTags(awskms.listResourceTags(new ListResourceTagsRequest().withKeyId(key.getKeyId())).getTags());
							} catch (Exception e) {
								log.debug(e.getMessage());
							}
							try {
								kmsKey.setRotationStatus(awskms.getKeyRotationStatus(new GetKeyRotationStatusRequest().withKeyId(key.getKeyId())).getKeyRotationEnabled());
							} catch (Exception e) {
								log.debug(e.getMessage());
							}
							if(!regionKeyAliases.isEmpty() ) {
								for(AliasListEntry alias: regionKeyAliases) {
									if(key.getKeyId().equals(alias.getTargetKeyId())) {
										kmsKey.setAlias(alias);
										break;
									}
								}
							}
							kmsKeysList.add(kmsKey);
						}
						log.debug(InventoryConstants.ACCOUNT + account +" Type : KMSKey "+region.getName() + " >> "+kmsKeysList.size());
						kmsKeys.put(account+delimiter+region.getName(),kmsKeysList);
						
					}
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"kms",e.getMessage());
			}
		}
		return kmsKeys;
	}
	
	/**
	 * Fetch cloud front info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<CloudFrontVH>> fetchCloudFrontInfo(BasicSessionCredentials temporaryCredentials,String account) {
		
		Map<String,List<CloudFrontVH>> cloudFront = new LinkedHashMap<>();
		List<DistributionSummary> distributionSummary = new ArrayList<>();
		AmazonCloudFront amazonCloudFront;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource \" ,\"type\": \"CloudFront\"" ;
		try{
			amazonCloudFront = AmazonCloudFrontClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(InventoryConstants.REGION_US_WEST_2).build();
			distributionSummary = amazonCloudFront.listDistributions(new ListDistributionsRequest()).getDistributionList().getItems();
			List<CloudFrontVH> cloudFrontList = new ArrayList<>();
			for(DistributionSummary ds : distributionSummary) {
				CloudFrontVH cf = new CloudFrontVH();
				cf.setDistSummary(ds);
				cf.setTags(amazonCloudFront.listTagsForResource(new com.amazonaws.services.cloudfront.model.ListTagsForResourceRequest().withResource(ds.getARN())).getTags().getItems());
				cloudFrontList.add(cf);
			}
			log.debug(InventoryConstants.ACCOUNT + account +" Type : CloudFront "+ " >> "+cloudFrontList.size());
			cloudFront.put(account,cloudFrontList);
		}catch(Exception e){
			log.error(expPrefix+ InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
			ErrorManageUtil.uploadError(account,"","cloudfront",e.getMessage());
		}
		return cloudFront;
	}
	
	/**
	 * Fetch EBS info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param skipRegions the skip regions
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<EbsVH>> fetchEBSInfo(BasicSessionCredentials temporaryCredentials, String skipRegions,String account) {
		
		Map<String,List<EbsVH>> ebs = new LinkedHashMap<>();
		
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource in specific region\" ,\"type\": \"EBS\" , \"region\":\"" ;
		for(Region region : RegionUtils.getRegions()) { 
			try{
				if(!skipRegions.contains(region.getName())){ 
					AWSElasticBeanstalk awsElasticBeanstalk  = AWSElasticBeanstalkClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion(region.getName()).build();
					List<ApplicationDescription> appDesList = awsElasticBeanstalk.describeApplications().getApplications();
					List<EbsVH> ebsList = new ArrayList<>();
					for(ApplicationDescription appDes : appDesList) {
						List<EnvironmentDescription> envDesList = awsElasticBeanstalk.describeEnvironments(new DescribeEnvironmentsRequest().withApplicationName(appDes.getApplicationName())).getEnvironments();
						if(envDesList.isEmpty()) {
							EbsVH ebsObj = new EbsVH();
							ebsObj.setApp(appDes);
							ebsList.add(ebsObj);
						} 
						else {
							for(EnvironmentDescription envDes : envDesList) {
								EbsVH ebsObj = new EbsVH();
								ebsObj.setApp(appDes);
								ebsObj.setEnv(envDes);
								ebsObj.setEnvResource(awsElasticBeanstalk.describeEnvironmentResources(new DescribeEnvironmentResourcesRequest().withEnvironmentId(envDes.getEnvironmentId())).getEnvironmentResources());
								ebsList.add(ebsObj);
							}
						}
					}
					if( !ebsList.isEmpty() ) {
						log.debug(InventoryConstants.ACCOUNT + account +" Type : EBS "+region.getName() + " >> "+ebsList.size());
						ebs.put(account+delimiter+region.getName(),ebsList);
					}
				}
			}catch(Exception e){
				log.warn(expPrefix+ region.getName()+InventoryConstants.ERROR_CAUSE +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,region.getName(),"beanstalk",e.getMessage());
			}
		}
		return ebs;
	}
	
	/**
	 * Fetch PHD info.
	 *
	 * @param temporaryCredentials the temporary credentials
	 * @param account the account
	 * @return the map
	 */
	public static Map<String,List<PhdVH>> fetchPHDInfo(BasicSessionCredentials temporaryCredentials,String account) {
		
		Map<String,List<PhdVH>> phd = new LinkedHashMap<>();
		AWSHealth awsHealth;
		String expPrefix = InventoryConstants.ERROR_PREFIX_CODE+account + "\",\"Message\": \"Exception in fetching info for resource\" ,\"type\": \"PHD\"" ;
		try{
			awsHealth = AWSHealthClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(temporaryCredentials)).withRegion("us-east-1").build();
			List<PhdVH> phdList = new ArrayList<>();
			List<Event> resultEvents = new ArrayList<>();
			String nextToken = "";
			do {
				DescribeEventsRequest describeEventsRequest = new DescribeEventsRequest().withMaxResults(100);
				if (!StringUtils.isEmpty(nextToken)) {
					describeEventsRequest.withNextToken(nextToken);
				}
				DescribeEventsResult eventsResult = awsHealth.describeEvents(describeEventsRequest);
				nextToken = eventsResult.getNextToken();
				resultEvents.addAll(eventsResult.getEvents());
			} while (!StringUtils.isEmpty(nextToken));
			List<String> eventArns = resultEvents.stream().map(Event::getArn).collect(Collectors.toList());
			int eventSize = eventArns.size();
			List<String> eventArnsTemp = new ArrayList<>();
			for(int i =0 ; i<eventSize;i++){
				eventArnsTemp.add(eventArns.get(i));
				if((i+1)%10==0 || i == eventSize-1){ // 10 is because API could only accept 10 event arns max
					List<EventDetails> successfulEventDetails = awsHealth.describeEventDetails(new DescribeEventDetailsRequest().withEventArns(eventArnsTemp)). getSuccessfulSet();
					List<AffectedEntity> affectedEntities = new ArrayList<>();
					do {
						DescribeAffectedEntitiesRequest affectedEntitiesRequest = new DescribeAffectedEntitiesRequest().withMaxResults(100);
						if (!StringUtils.isEmpty(nextToken)) {
							affectedEntitiesRequest.withNextToken(nextToken);
						}
						DescribeAffectedEntitiesResult affectedEntitiesResult = awsHealth.describeAffectedEntities(affectedEntitiesRequest.withFilter(new EntityFilter().withEventArns(eventArnsTemp)));
						nextToken = affectedEntitiesResult.getNextToken();
						affectedEntities.addAll(affectedEntitiesResult.getEntities());
					} while (!StringUtils.isEmpty(nextToken));
					for(EventDetails eventDetail : successfulEventDetails) {
						PhdVH phdObj = new PhdVH();
						phdObj.setEventDetails(eventDetail);
						phdObj.setAffectedEntities(affectedEntities.parallelStream().filter(affEntity -> affEntity.getEventArn().equals(eventDetail.getEvent().getArn())).collect(Collectors.toList()));
						phdList.add(phdObj);
					}
					eventArnsTemp =  new ArrayList<>();
				}
			}
			if( !phdList.isEmpty() ) {
				log.debug(InventoryConstants.ACCOUNT + account +" Type : PHD "+ " >> "+phdList.size());
				phd.put(account,phdList);
			}
		}catch(Exception e){
				log.error(expPrefix +", \"cause\":\"" +e.getMessage()+"\"}");
				ErrorManageUtil.uploadError(account,"","phd",e.getMessage());
		}
		return phd;
	}
}
