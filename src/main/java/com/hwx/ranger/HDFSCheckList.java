package com.hwx.ranger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class HDFSCheckList {

	
private int depth;
private String description;
private List<String> paths;
private String resourceName;
private String repositoryName;
private boolean isEnabled;
private boolean isRecursive;
private List<PolicyItem> policyItems;
private boolean allowRangerPathDelete;
private boolean autoIdentifyAttributes;
private List<String> autoIdentifyAttributesKeys;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

/**
 * Get any additional properties that can be set.
 *
 * @return A Map of key-String, value-Object.
 */
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}


public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

/**
 * Get a description for the Checklist Item.
 *
 * @return A String representation of the Description.
 */
public String getDescription() {
	return description;
}


public void setDescription(String description) {
	this.description = description;
}

/**
 * Get a List of comma separated paths from the Checklist item .
 *
 * @return A List of Strings.
 */
public List<String> getPaths() {
	return paths;
}


public void setPaths(List<String> paths) {
	this.paths = paths;
}

/**
 * Get the ResourceName, which is the Policy Name which needs to be set/updated.
 *
 * @return A String representing the policy name.
 */
public String getResourceName() {
	return resourceName;
}


public void setResourceName(String resourceName) {
	this.resourceName = resourceName;
}

/**
 * Get the list of Policy Items.
 * Each policy Item in turn consists of
 * 1. Access : Read-true, Write-true, Execute-false
 * 2. Users : hdp-user1,hdp-user2
 * 3. Group : hdp-hr-grp, hdp-eng-grp
 * 4. Conditions
 * 5. DelegateAdmins : true/false
 * @return A List of policy items.
 */
public List<PolicyItem> getPolicyItemList() {
	return policyItems;
}


public void setPolicyItemList(List<PolicyItem> policyItemList) {
	this.policyItems = policyItemList;
}

/**
 * Get the Repository Name, which is the HDFS Repository name initialized in Ranger.
 * 
 * @return A String representing the repository name.
 */
public String getRepositoryName() {
	return repositoryName;
}


public void setRepositoryName(String repositoryName) {
	this.repositoryName = repositoryName;
}

/**
 * Get the Depth which represents the relative directory-level that should 
 * be considered for identifying updates to the Ranger policy
 *
 * depth 0, means that the exact path(s) listed in input should be processed.
 * depth 2, means that the all the directories which are 2 levels below the paths
 *   listed in the input should be processed.
 * @return An integer representing the depth level
 */
public int getDepth() {
	return depth;
}


public void setDepth(int depth) {
	this.depth = depth;
}


/**
 * Parses and returns all the attributes used in the Checklist item object.
 *
 * @return A String representing all the checklist items.
 */
@Override
public String toString() {
	return getClass().getName() + " {\n\tdepth: " + depth + "\n\tdescription: "
			+ description + "\n\tpath: " + paths + "\n\tresourceName: "
			+ resourceName + "\n\trepositoryName: " + repositoryName
			+ "\n\tpolicyItemList: " + policyItems + "\n\tallowRangerPathDelete: " + allowRangerPathDelete
			+ "\n\tadditionalProperties: " + additionalProperties + "\n}";
}



/**
 * Returns true if the checklist item which gets Created in Ranger needs to be enabled.
 *
 * @return A boolean representing the state of the created policy.
 */
public boolean isEnabled() {
	return isEnabled;
}


public void setEnabled(boolean isEnabled) {
	this.isEnabled = isEnabled;
}

/**
 * Returns true if the Ranger Policy needs to be recursive in nature.
 *
 * @return A boolean representing the recursive nature of the created policy.
 */
public boolean isRecursive() {
	return isRecursive;
}


public void setRecursive(boolean isRecursive) {
	this.isRecursive = isRecursive;
}

/**
 * Returns true if autoIdentifyAttributes attribute is true.
 * 
 * autoIdentifyAttributes attribute, if set to true, means that 
 * the application dynamically construct the user/group names based on the path(s)
 * that have been listed in the input of the Checklist item or from the relative depth path,
 * in cases where depth > 0
 * 
 * @return A boolean indicating if autoIdentifyAttributes is enabled.
 */
public boolean isAutoIdentifyAttributes() {
	return autoIdentifyAttributes;
}


public void setAutoIdentifyAttributes(boolean autoIdentifyUserGroup) {
	this.autoIdentifyAttributes = autoIdentifyUserGroup;
}

/**
 * Returns the list of place-holders that should be replaced with 
 * actual attributes.
 * 
 * autoIdentifyAttributesKeys are a set of place-holders that should
 * be replaced dynamically before Ranger policies are compared/processed/updated.
 * 
 * @return A boolean indicating if autoIdentifyAttributes is enabled.
 */
public List<String> getAutoIdentifyAttributesKeys() {
	return autoIdentifyAttributesKeys;
}


public void setAutoIdentifyAttributesKeys(List<String> autoIdentifyAttributesKeys) {
	this.autoIdentifyAttributesKeys = autoIdentifyAttributesKeys;
}


/**
 * Returns true if allowRangerPathDelete attribute is true.
 * 
 * allowRangerPathDelete attribute, if set to true, ensures that 
 * Ranger clears the directories from its policy list if the relative 
 * depth does not have the said folders
 * This CANNOT DELETE the entire policy.
 * 
 * @return A boolean indicating if allowRangerPathDelete is enabled.
 */
public boolean isAllowRangerPathDelete() {
	return allowRangerPathDelete;
}


public void setAllowRangerPathDelete(boolean allowRangerPathDelete) {
	this.allowRangerPathDelete = allowRangerPathDelete;
}


}