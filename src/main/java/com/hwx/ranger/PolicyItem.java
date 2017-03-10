package com.hwx.ranger;

import java.util.List;

public class PolicyItem {

/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PolicyItem [accesses=" + accesses + ", users=" + users
				+ ", groups=" + groups + ", conditions=" + conditions
				+ ", delegateAdmin=" + delegateAdmin + "]";
	}

private List<Access> accesses = null;
private List<String> users = null;
private List<String> groups = null;
private List<Object> conditions = null;
private boolean delegateAdmin;

public List<Access> getAccesses() {
return accesses;
}

public void setAccesses(List<Access> accesses) {
this.accesses = accesses;
}

public List<String> getUsers() {
return users;
}

public void setUsers(List<String> users) {
this.users = users;
}

public List<String> getGroups() {
return groups;
}

public void setGroups(List<String> groups) {
this.groups = groups;
}

public List<Object> getConditions() {
return conditions;
}

public void setConditions(List<Object> conditions) {
this.conditions = conditions;
}

public boolean isDelegateAdmin() {
return delegateAdmin;
}

public void setDelegateAdmin(boolean delegateAdmin) {
this.delegateAdmin = delegateAdmin;
}

/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((accesses == null) ? 0 : accesses.hashCode());
	result = prime * result + (delegateAdmin ? 1231 : 1237);
	result = prime * result + ((groups == null) ? 0 : groups.hashCode());
	result = prime * result + ((users == null) ? 0 : users.hashCode());
	return result;
}

/* (non-Javadoc)
 * @see java.lang.Object#equals(java.lang.Object)
 */
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	PolicyItem other = (PolicyItem) obj;
	if (accesses == null) {
		if (other.accesses != null)
			return false;
	} else if (!accesses.equals(other.accesses))
		return false;
	if (delegateAdmin != other.delegateAdmin)
		return false;
	if (groups == null) {
		if (other.groups != null)
			return false;
	} else if (!groups.containsAll(other.groups))
		return false;
	if (users == null) {
		if (other.users != null)
			return false;
	} else if (!users.containsAll(other.users))
		return false;
	return true;
}



}