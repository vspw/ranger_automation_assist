package com.hwx.ranger;

import java.util.ArrayList;


public class Path {

@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isExcludes ? 1231 : 1237);
		result = prime * result + (isRecursive ? 1231 : 1237);
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		Path other = (Path) obj;
		if (isExcludes != other.isExcludes)
			return false;
		if (isRecursive != other.isRecursive)
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

private ArrayList<String> values = null;
private boolean isExcludes;
private boolean isRecursive;

public ArrayList<String> getValues() {
return values;
}

public void setValues(ArrayList<String> values) {
this.values = values;
}

public boolean isIsExcludes() {
return isExcludes;
}

public void setIsExcludes(boolean isExcludes) {
this.isExcludes = isExcludes;
}

public boolean isIsRecursive() {
return isRecursive;
}

public void setIsRecursive(boolean isRecursive) {
this.isRecursive = isRecursive;
}

}
