package com.hwx.ranger;

import java.util.List;

public class RangerPolicyResponse {

private int id;
private boolean isEnabled;
private int version;
private String service;
private String name;
private int policyType;
private boolean isAuditEnabled;
private Resources resources;
private List<PolicyItem> policyItems = null;
private List<Object> denyPolicyItems = null;
private List<Object> allowExceptions = null;
private List<Object> denyExceptions = null;
private List<Object> dataMaskPolicyItems = null;
private List<Object> rowFilterPolicyItems = null;

public int getId() {
return id;
}

public void setId(int id) {
this.id = id;
}

public boolean isIsEnabled() {
return isEnabled;
}

public void setIsEnabled(boolean isEnabled) {
this.isEnabled = isEnabled;
}

public int getVersion() {
return version;
}

public void setVersion(int version) {
this.version = version;
}

public String getService() {
return service;
}

public void setService(String service) {
this.service = service;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public int getPolicyType() {
return policyType;
}

public void setPolicyType(int policyType) {
this.policyType = policyType;
}

public boolean isIsAuditEnabled() {
return isAuditEnabled;
}

public void setIsAuditEnabled(boolean isAuditEnabled) {
this.isAuditEnabled = isAuditEnabled;
}

public Resources getResources() {
return resources;
}

public void setResources(Resources resources) {
this.resources = resources;
}

public List<PolicyItem> getPolicyItems() {
return policyItems;
}

public void setPolicyItems(List<PolicyItem> policyItems) {
this.policyItems = policyItems;
}

public List<Object> getDenyPolicyItems() {
return denyPolicyItems;
}

public void setDenyPolicyItems(List<Object> denyPolicyItems) {
this.denyPolicyItems = denyPolicyItems;
}

public List<Object> getAllowExceptions() {
return allowExceptions;
}

public void setAllowExceptions(List<Object> allowExceptions) {
this.allowExceptions = allowExceptions;
}

public List<Object> getDenyExceptions() {
return denyExceptions;
}

public void setDenyExceptions(List<Object> denyExceptions) {
this.denyExceptions = denyExceptions;
}

public List<Object> getDataMaskPolicyItems() {
return dataMaskPolicyItems;
}

public void setDataMaskPolicyItems(List<Object> dataMaskPolicyItems) {
this.dataMaskPolicyItems = dataMaskPolicyItems;
}

public List<Object> getRowFilterPolicyItems() {
return rowFilterPolicyItems;
}

public void setRowFilterPolicyItems(List<Object> rowFilterPolicyItems) {
this.rowFilterPolicyItems = rowFilterPolicyItems;
}

}