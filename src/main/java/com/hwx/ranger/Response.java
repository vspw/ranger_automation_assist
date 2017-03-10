package com.hwx.ranger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Response {


private EnvDetails envdetails;
private List<HDFSCheckList> hdfschecklist;

private Map<String, Object> additionalProperties = new HashMap<String, Object>();


public EnvDetails getEnvDetails() {
return envdetails;
}


public void setEnvDetails(EnvDetails envdetails) {
this.envdetails = envdetails;
}


public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}


public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}


public List<HDFSCheckList> getHdfschecklist() {
	return hdfschecklist;
}


public void setHdfschecklist(List<HDFSCheckList> hdfschecklist) {
	this.hdfschecklist = hdfschecklist;
}


}