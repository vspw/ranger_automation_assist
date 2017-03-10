package com.hwx.ranger;

import java.util.ArrayList;


public class Path {

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
