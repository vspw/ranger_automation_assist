package com.hwx.ranger;

import java.util.HashMap;
import java.util.Map;



public class EnvDetails {


private String envName;
private String hdfsURI;
private String rangerURI;
private String opUsername;
private String opPassword;
private int repeatPeriod;
private String opKeyStoreFile;
private String opKeyAlias;
private String opKeyStorePassword;
private boolean useHdfsKeytab;
private String hdfsKeytabUpn;
private String hdfsKeytab;
private Map<String, Object> additionalProperties = new HashMap<String, Object>();





public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}


public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}


public String getRangerURI() {
	return rangerURI;
}


public void setRangerURI(String rangerURI) {
	this.rangerURI = rangerURI;
}


public String getOpUsername() {
	return opUsername;
}


public void setOpUsername(String opUsername) {
	this.opUsername = opUsername;
}


public String getOpPassword() {
	return opPassword;
}


public void setOpPassword(String opPassword) {
	this.opPassword = opPassword;
}


public String getEnvName() {
	return envName;
}


public void setEnvName(String envName) {
	this.envName = envName;
}


public String getHdfsURI() {
	return hdfsURI;
}


public void setHdfsURI(String hdfsURI) {
	this.hdfsURI = hdfsURI;
}


/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Override
public String toString() {
	return getClass().getName() + " {\n\tenvName: " + envName + "\n\thdfsURI: "
			+ hdfsURI + "\n\trangerURI: " + rangerURI + "\n\topUsername: "
			+ opUsername + "\n\topPassword(hashvalue): " + opPassword.hashCode()
			+ "\n\tadditionalProperties: " + additionalProperties + "\n}";
}


public int getRepeatPeriod() {
	return repeatPeriod;
}


public void setRepeatPeriod(int repeatPeriod) {
	this.repeatPeriod = repeatPeriod;
}


public String getOpKeyStoreFile() {
	return opKeyStoreFile;
}


public void setOpKeyStoreFile(String opKeyStoreFile) {
	this.opKeyStoreFile = opKeyStoreFile;
}


public String getOpKeyStorePassword() {
	return opKeyStorePassword;
}


public void setOpKeyStorePassword(String opKeyStorePassword) {
	this.opKeyStorePassword = opKeyStorePassword;
}


public String getOpKeyAlias() {
	return opKeyAlias;
}


public void setOpKeyAlias(String opKeyAlias) {
	this.opKeyAlias = opKeyAlias;
}


public boolean isUseHdfsKeytab() {
	return useHdfsKeytab;
}


public void setUseHdfsKeytab(boolean useHdfsKeytab) {
	this.useHdfsKeytab = useHdfsKeytab;
}


public String getHdfsKeytabUpn() {
	return hdfsKeytabUpn;
}


public void setHdfsKeytabUpn(String hdfsKeytabUpn) {
	this.hdfsKeytabUpn = hdfsKeytabUpn;
}


public String getHdfsKeytab() {
	return hdfsKeytab;
}


public void setHdfsKeytab(String hdfsKeytab) {
	this.hdfsKeytab = hdfsKeytab;
}

}