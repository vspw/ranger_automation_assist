package com.hwx.ranger;


public class Access {

private String type;
private boolean isAllowed;

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

public boolean isIsAllowed() {
return isAllowed;
}

public void setIsAllowed(boolean isAllowed) {
this.isAllowed = isAllowed;
}

/* (non-Javadoc)
 * @see java.lang.Object#hashCode()
 */
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (isAllowed ? 1231 : 1237);
	result = prime * result + ((type == null) ? 0 : type.hashCode());
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
	Access other = (Access) obj;
	if (isAllowed != other.isAllowed)
		return false;
	if (type == null) {
		if (other.type != null)
			return false;
	} else if (!type.equals(other.type))
		return false;
	return true;
}

}