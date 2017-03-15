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

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((allowExceptions == null) ? 0 : allowExceptions.hashCode());
	result = prime * result + ((dataMaskPolicyItems == null) ? 0 : dataMaskPolicyItems.hashCode());
	result = prime * result + ((denyExceptions == null) ? 0 : denyExceptions.hashCode());
	result = prime * result + ((denyPolicyItems == null) ? 0 : denyPolicyItems.hashCode());
	result = prime * result + (isAuditEnabled ? 1231 : 1237);
	result = prime * result + (isEnabled ? 1231 : 1237);
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + ((policyItems == null) ? 0 : policyItems.hashCode());
	result = prime * result + policyType;
	result = prime * result + ((resources == null) ? 0 : resources.hashCode());
	return result;
}

@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	RangerPolicyResponse other = (RangerPolicyResponse) obj;
	if (allowExceptions == null) {
		if (other.allowExceptions != null)
			return false;
	} else if (!allowExceptions.equals(other.allowExceptions))
		return false;
	if (dataMaskPolicyItems == null) {
		if (other.dataMaskPolicyItems != null)
			return false;
	} else if (!dataMaskPolicyItems.equals(other.dataMaskPolicyItems))
		return false;
	if (denyExceptions == null) {
		if (other.denyExceptions != null)
			return false;
	} else if (!denyExceptions.equals(other.denyExceptions))
		return false;
	if (denyPolicyItems == null) {
		if (other.denyPolicyItems != null)
			return false;
	} else if (!denyPolicyItems.equals(other.denyPolicyItems))
		return false;
	if (isAuditEnabled != other.isAuditEnabled)
		return false;
	if (isEnabled != other.isEnabled)
		return false;
	if (name == null) {
		if (other.name != null)
			return false;
	} else if (!name.equals(other.name))
		return false;
	if (policyItems == null) {
		if (other.policyItems != null)
			return false;
	} else if (!policyItems.equals(other.policyItems))
		return false;
	if (policyType != other.policyType)
		return false;
	if (resources == null) {
		if (other.resources != null)
			return false;
	} else if (!resources.equals(other.resources))
		return false;
	return true;
}

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