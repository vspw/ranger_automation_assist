package com.hwx.ranger;

import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.hadoop.security.authentication.client.AuthenticationException;

/**
 * 
===== HTTP GET <br/>
<li>GETPOLICYBYNAME 
<li>GETPOLICYBYID 
<li>GETPOLICIESBYSEARCH 
<br/>
===== HTTP PUT <br/>
<li>UPDATEPOLICY
<br/>
===== HTTP POST <br/>
CREATEPOLICYBYID 
<br/>


 */
public interface RangerConnection {



	/*
	 * ========================================================================
	 * GET	
	 * ========================================================================
	 */

	public String getPolicyByName(String policyName) throws MalformedURLException, IOException, AuthenticationException ;

	public  String getPolicybyId(String id) throws MalformedURLException, IOException, AuthenticationException ;

	public  String getAllRepositoryPolicies() throws MalformedURLException, IOException, AuthenticationException ;


	/*
	 * ========================================================================
	 * POST
	 * ========================================================================	
	 */

	public String createPolicy(String jsonContent) throws MalformedURLException, IOException, AuthenticationException;

	/*
	 * ========================================================================
	 * PUT
	 * ========================================================================	
	 */

	public String updatePolicyByName(String policyName, String jsonContent) throws MalformedURLException, IOException, AuthenticationException ;




}
