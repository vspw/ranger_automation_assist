package com.hwx.ranger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.file.Paths;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;
import java.util.TimerTask;


import org.apache.hadoop.security.authentication.client.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class RangerAssist extends TimerTask {

	RangerConnection rconn=null;
	String inputFilePath=null;
	Response objInput=null;
	ListIterator<HDFSCheckList> iteratorHDFSCheckList=null;
	HadoopFileSystemOps objHdfsOps=new HadoopFileSystemOps();
	protected static final Logger logger = LoggerFactory.getLogger(RangerAssist.class);

	public RangerAssist(String inputFilePath) {
		this.inputFilePath=inputFilePath;
	}

	@Override
	public void run() {
		try {

			//Begin prep to read input file
			JsonUtils objJutils = new JsonUtils();
			logger.info("###############LETS BEGIN###############");
			logger.info("Make sure pass the input json file path with all necessary details");
			Scanner objScanner=new Scanner(new File(inputFilePath));
			String strJsonInput = objScanner.useDelimiter("\\Z").next();
			objScanner.close();
			logger.info("Parsing the json input file");
			objInput = objJutils.parseJSON(strJsonInput);
			logger.debug("fromInputJson->Environment: "+ objInput.getEnvDetails());
			
			logger.info("Reading the keyStoreFile using the input Path");
	        URI uri1 =URI.create(objInput.getEnvDetails().getOpKeyStoreFile());
	        java.nio.file.Path path1=Paths.get(uri1);
	        SecretKeyUtil keyUtils= new SecretKeyUtil(path1, objInput.getEnvDetails().getOpKeyStorePassword().toCharArray(),true);
	    	logger.info("Retrieve Entry Password from the keystore using alias");
	        String varSafePW=new String(keyUtils.retrieveEntryPassword(objInput.getEnvDetails().getOpKeyAlias()));
	        objInput.getEnvDetails().setOpPassword(varSafePW);
	        
			//End or preparation
			logger.info("Establishing Connection to Ranger");
			this.rconn=this.connectr(objInput);

			logger.info("Iterating the input HDFS policy checklist");
			//changed the iterator to ListIterator as we should be able to add new checkList items 
			// if autoIdentifyAttribute is set to true to any of the inputs. A vanilla iterator would
			// not allow us to add new items to list.
			iteratorHDFSCheckList=objInput.getHdfschecklist().listIterator();
			logger.info("ForEach Input HDFS Path");

			while(iteratorHDFSCheckList.hasNext())
			{

				logger.info("#############################################" );
				HDFSCheckList objInputHDFSItem= iteratorHDFSCheckList.next();
				List<String> listInputPaths=objInputHDFSItem.getPaths();
				ArrayList<String> listHdfsDepthPaths= new ArrayList<String>();

				Iterator<String> iteratorInputPaths = listInputPaths.iterator();				
				logger.info("---Iterating through the list of Input Paths" );
				while(iteratorInputPaths.hasNext())
				{
					String strInputPath=iteratorInputPaths.next();
					logger.info("####INPUT PATH:"+strInputPath+"####" );
					logger.info("----Get list of paths in HDFS based on the depth from input");

					//This does not matter if depth is 0, since its the provided directory only
					logger.info("----HDFS listStatus for the given Depth, retrieving list of hdfs-depth-paths");
					this.listStatusForDepth(strInputPath,objInputHDFSItem.getDepth(),listHdfsDepthPaths);
				}

				//Check if there any depth paths that are needed to be considered for the input path(s)
				if (listHdfsDepthPaths.isEmpty())
				{
					logger.warn("--No Depth paths found, Going to the next input checklist item. Consider deleting the Ranger Policy if it exists. ");
					continue;
				}

				//if depth paths are calculated AND autoIdentifyAttributes is true
				//start parsing depth paths and add Dynamic entries the iteratorHDFSCheckList item.
				if(objInputHDFSItem.isAutoIdentifyAttributes())
				{
					autoIdentifyAndReplaceAttributes(objInputHDFSItem,listHdfsDepthPaths,iteratorHDFSCheckList);
					//the autoIdentifyAttributes input checklist item itself does not represent
					//a policy item. Hence just move to the next CheckList item.
					//By this time, new policy(s),if any, should be added to the iterator
					continue;

				}

				logger.debug("HDFS Check List New: "+new Gson().toJson(objInput));

				//Get policy in Ranger using the resource_name from input
				//0. Get all policies and check for the input policy name.
				//1. Get Policy by service-name and policy-name
				//2. Update Policy by service-name and policy-name
				logger.info("---Get All Existing Ranger Polices");
				String strAllPolicies=this.getAllRepositoryPolicies();
				logger.debug(strAllPolicies);
				List<RangerPolicyResponse> objRangerPolicies=new JsonUtils().parseRangerPolicies(strAllPolicies);
				logger.debug(objRangerPolicies.iterator().next().getName());

				logger.info("---Check if we need a NEW ranger policy");
				Iterator<RangerPolicyResponse> iteratorObjRangerPolicies = objRangerPolicies.iterator();
				boolean policyFoundInRanger=false;
				// check if its a new Ranger Policy
				while(iteratorObjRangerPolicies.hasNext())
				{
					RangerPolicyResponse objRangerPolicy=iteratorObjRangerPolicies.next();
					if(objRangerPolicy.getName().equals(objInputHDFSItem.getResourceName()))
					{
						logger.info("-----Ranger Policy with InputResourceName: "+objInputHDFSItem.getResourceName()+" found");
						policyFoundInRanger=true;
						break; 
					}
				}// end of check on a new Ranger Policy

				//Initiate a new Ranger Policy Object which is used to either Create or Update the existing policy.
				RangerPolicyResponse objNewRangerPolicy = this.initRangerPolicy(objInputHDFSItem, listHdfsDepthPaths);
				//If policy is not found create a new policy and break
				if (policyFoundInRanger==false)
				{
					logger.info("---Current HDFS Checklist Policy: "+objInputHDFSItem.getResourceName()+" is NOT found in Ranger. CREATING a new Ranger Policy");
					this.createPolicy(objNewRangerPolicy); 
					//created a policy for this inputCheckList item. Now go to the next Input checklist item.
					continue;
				}
				else //Policy is already present in Ranger, compare and update the existing policy
				{
					logger.info("---Policy is Found in Ranger. Continue and edit this.");
					logger.info("---Get Ranger Policy by service-name and policy-name");
					String strRangerPolicyName=objInputHDFSItem.getResourceName();
					String strRangerPolicy =this.getRangerPolicyByName(strRangerPolicyName);
					RangerPolicyResponse objRangerPol=new JsonUtils().parseRangerPolicy(strRangerPolicy);
					this.compareAndUpdateRangerPolicy(objNewRangerPolicy, objRangerPol);
				}


			}//FOR EACH INPUT HDFS PATH


		} catch(FileNotFoundException e)
		{
			logger.error(e.getMessage());
			logger.error("Verify your URL for HDFS, path is not present");
			e.printStackTrace();
		}
		catch (Exception e)
		{	
			e.printStackTrace();
		}


	}

	/**
	 * This procedure takes an InputHDFS
	 * @param iteratorHDFSCheckList2 
	 */
	private void autoIdentifyAndReplaceAttributes(HDFSCheckList objInputHDFSItem, List<String> listHdfsDepthPaths, ListIterator<HDFSCheckList> iteratorHDFSCheckList2)
	{
		List<String> objAutoIdentifyAttributeKeys = objInputHDFSItem.getAutoIdentifyAttributesKeys();
		Iterator<String> iteratorDepthPaths=listHdfsDepthPaths.iterator();
		HDFSCheckList objNewInputHDFSItem;
		while(iteratorDepthPaths.hasNext())
		{
			String strCurrPath=iteratorDepthPaths.next();
			//strCurrPath = strCurrPath.startsWith("/") ? strCurrPath.substring(1) : strCurrPath;
			String[] arrayPathTokens=strCurrPath.split("/");
			logger.debug("autoIdentifyAndReplaceAttributes: Checking if count of keys greater than count of directory in depth path: "+strCurrPath);
			if(objAutoIdentifyAttributeKeys.size() > arrayPathTokens.length)
			{
				logger.warn("autoIdentifyAndReplaceAttributes: Count of AutoIdentifyAttributeKeys greater than depth path size!!");
				return;
			}
			logger.debug("autoIdentifyAndReplaceAttributes: For each input key start replacing it with depth path token item");
			HashMap<String, String> objMapKeyToToken = new HashMap<String, String>();
			for(int i=0;i<objAutoIdentifyAttributeKeys.size();i++)
			{
				//index = length of depth path tokens - size of autoIdentifyKeys + i
				int indexArrayPathTokens = (arrayPathTokens.length - objAutoIdentifyAttributeKeys.size())+i;
				logger.debug("autoIdentifyAndReplaceAttributes: "
						+ "Putting token: "+ arrayPathTokens[indexArrayPathTokens]
								+ " in AttributeKey: "+objAutoIdentifyAttributeKeys.get(i));
				objMapKeyToToken.put(objAutoIdentifyAttributeKeys.get(i), arrayPathTokens[indexArrayPathTokens]);
			}

			logger.debug("Json HDFS Item to be replaced :"+new Gson().toJson(objInputHDFSItem));
			String strInputHDFSItem = new Gson().toJson(objInputHDFSItem);
			Iterator<String> iteratorAutoIdentifyKeys = objMapKeyToToken.keySet().iterator();
			String key;
			while(iteratorAutoIdentifyKeys.hasNext())
			{
				key=iteratorAutoIdentifyKeys.next(); 
				logger.debug("Key:Value to be replaced :"+key+":"+objMapKeyToToken.get(key));
				strInputHDFSItem=strInputHDFSItem.replaceAll(key, objMapKeyToToken.get(key));	
			}

			objNewInputHDFSItem=new JsonUtils().parseHDFSCheckList(strInputHDFSItem);
			logger.debug("RESULTANT json CHECKLIST ITEM :"+new Gson().toJson(objNewInputHDFSItem));
			objNewInputHDFSItem.setAutoIdentifyAttributes(false);
			objNewInputHDFSItem.setDescription("Dynamically created from Dynamic:LOB-FA-Writes");
			objNewInputHDFSItem.setDepth(0);
			List<String> objNewPaths=new ArrayList<String>();
			objNewPaths.add(strCurrPath);
			objNewInputHDFSItem.setPaths(objNewPaths);
			iteratorHDFSCheckList2.add(objNewInputHDFSItem);
			iteratorHDFSCheckList2.previous();
			//end creating new object

		}


	}
	//recursiveList("/tenant",1)
	private void listStatusForDepth(String strInputPath,int intDepth, ArrayList<String> listPaths) throws Exception {


		//depth 0 is for root level of depth verification only. E.g: For input paths /source or /base/test etc.
		//the list should have only /source or /base/test respectively
		//If Depth=0 we don't need listStatus.
		if (intDepth==0)
		{

			if (objHdfsOps.isHdfsPathValid(strInputPath, true))
			{
				logger.debug("listStatusForDepth: Depth"+intDepth+", Adding Directory path: "+strInputPath);
				listPaths.add(strInputPath);
				return;
			}
			else
			{
				logger.warn("listStatusForDepth: Depth"+intDepth+", HDFS Directory path NOT FOUND:"+strInputPath);
				return;
			}

		}
		else //if depth > 0, we need listStatus to dig deeper
		{
			//String strHdfsLsContent=null;
			//HDFSListStatusResponse objHdfsLs=new JsonUtils().parseHDFSList(strHdfsLsContent);
			//Iterator<FileStatus> iteratorFileStatus=objHdfsLs.getFileStatuses().getFileStatus().iterator();
			logger.debug("listStatusForDepth: Depth"+intDepth+", Checking Directory path: "+strInputPath);
			List<String> listStringPaths=objHdfsOps.getHdfsListStringPaths(strInputPath);
			Iterator<String> iteratorListPaths= listStringPaths.iterator();
			//iterate for all the Files returned inside the list status (Might not be needed for depth 0)
			//For every FileStatus Returned- based on depth, iterate for paths
			while(iteratorListPaths.hasNext())
			{
				//FileStatus objFileStatus=iteratorFileStatus.next();
				String strPath= iteratorListPaths.next();

				//depth 1 is for first level of verification only. E.g: For input paths /source or /base/test etc.
				//the list should have only /source/A, /source/B or /base/test/case1, //base/test/case2, /base/test/case3 respectively
				//The reason depth 0 and depth 1 are two different conditions is because of the way we get the directory contents
				if (intDepth==1)
				{
					//logger.debug("listStatusForDepth: Depth:"+intDepth+"::"+conn.listStatus(strPath));
					//if(objFileStatus.getType().equals("DIRECTORY"))
					logger.debug("listStatusForDepth: Depth"+intDepth+", Adding path: "+strInputPath+"/"+strPath);
					listPaths.add(strInputPath+"/"+strPath);
				}
				//This is for Nth level of verification only. We recursively call this function until we reach the required level
				else
				{
					//FileStatus item= iteratorFileStatus.next();
					//int adepth=intDepth-1;

					logger.debug("listStatusForDepth: Depth:"+(intDepth-1)+" recursive call for path:"+strInputPath+"/"+strPath);
					listStatusForDepth(strInputPath+"/"+strPath, intDepth-1,listPaths);

				}
			}
		}
	}

	private RangerConnection connectr(Response objInput) throws Exception {

		rconn = new BasicAuthRangerConnection(objInput.getEnvDetails().getRangerURI(), objInput.getEnvDetails().getOpUsername().split("@")[0], objInput.getEnvDetails().getOpPassword(), objInput.getHdfschecklist().iterator().next().getRepositoryName());
		return rconn;

	}

	private String getRangerPolicyByName(String strRangerPolicyName) throws MalformedURLException, IOException, AuthenticationException
	{
		String strRangerPolicyContent=new JsonUtils().prettyPrint(rconn.getPolicyByName(strRangerPolicyName));
		return strRangerPolicyContent;

	}
	private String getAllRepositoryPolicies() throws MalformedURLException, IOException, AuthenticationException
	{
		String strRangerPoliciesContent=new JsonUtils().prettyPrint(rconn.getAllRepositoryPolicies());
		return strRangerPoliciesContent;

	}
	private void createPolicy(RangerPolicyResponse objRangerPol) throws MalformedURLException, IOException, AuthenticationException
	{

		Gson gson = new Gson();
		logger.debug("createPolicy: "+gson.toJson(objRangerPol));
		rconn.createPolicy(gson.toJson(objRangerPol));
	}


	private void compareAndUpdateRangerPolicy(RangerPolicyResponse objNewRangerPolicy, RangerPolicyResponse objRangerPol) throws MalformedURLException, IOException, AuthenticationException
	{
		//print the current state of policy
		Gson gson = new Gson();
		logger.info("UpdatePolicy: Compare and Correct any parameters in this policy");
		//Comparing both these policies and edit the original one.
		if(!objNewRangerPolicy.equals(objRangerPol))
		{
			String strNewRangerPolicy=gson.toJson(objNewRangerPolicy);
			logger.debug("compareAndUpdateRangerPolicy: "+strNewRangerPolicy);
			rconn.updatePolicyByName(objRangerPol.getName(), strNewRangerPolicy);
		}

	}

	private RangerPolicyResponse initRangerPolicy(HDFSCheckList objInputHDFSItem,ArrayList<String> listHdfsDepthPaths)
	{
		RangerPolicyResponse objNewRangerPolicy= new RangerPolicyResponse();
		boolean boolNewIsAuditEnabled=true;
		objNewRangerPolicy.setName(objInputHDFSItem.getResourceName());
		objNewRangerPolicy.setService(objInputHDFSItem.getRepositoryName());
		objNewRangerPolicy.setIsAuditEnabled(boolNewIsAuditEnabled);
		objNewRangerPolicy.setIsEnabled(objInputHDFSItem.isEnabled());

		//PolicyItems are copied from the HDFS input list
		objNewRangerPolicy.setPolicyItems(objInputHDFSItem.getPolicyItemList());

		Resources objNewResources=new Resources();
		objNewRangerPolicy.setResources(objNewResources);

		Path objNewPath= new Path();
		objNewPath.setIsExcludes(false);
		objNewPath.setIsRecursive(objInputHDFSItem.isRecursive());
		objNewPath.setValues(listHdfsDepthPaths);
		objNewRangerPolicy.getResources().setPath(objNewPath);

		return objNewRangerPolicy;
	}


}
