package com.hwx.ranger;


public class FileStatus {


private Long accessTime;

private Long blockSize;

private Long childrenNum;

private Long fileId;

private String group;

private Long length;

private Long modificationTime;

private String owner;

private String pathSuffix;

private String permission;

private Long replication;

private Long storagePolicy;

private String type;

public Long getAccessTime() {
return accessTime;
}

public void setAccessTime(Long accessTime) {
this.accessTime = accessTime;
}

public Long getBlockSize() {
return blockSize;
}

public void setBlockSize(Long blockSize) {
this.blockSize = blockSize;
}

public Long getChildrenNum() {
return childrenNum;
}

public void setChildrenNum(Long childrenNum) {
this.childrenNum = childrenNum;
}

public Long getFileId() {
return fileId;
}

public void setFileId(Long fileId) {
this.fileId = fileId;
}

public String getGroup() {
return group;
}

public void setGroup(String group) {
this.group = group;
}

public Long getLength() {
return length;
}

public void setLength(Long length) {
this.length = length;
}

public Long getModificationTime() {
return modificationTime;
}

public void setModificationTime(Long modificationTime) {
this.modificationTime = modificationTime;
}

public String getOwner() {
return owner;
}

public void setOwner(String owner) {
this.owner = owner;
}

public String getPathSuffix() {
return pathSuffix;
}

public void setPathSuffix(String pathSuffix) {
this.pathSuffix = pathSuffix;
}

public String getPermission() {
return permission;
}

public void setPermission(String permission) {
this.permission = permission;
}

public Long getReplication() {
return replication;
}

public void setReplication(Long replication) {
this.replication = replication;
}

public Long getStoragePolicy() {
return storagePolicy;
}

public void setStoragePolicy(Long storagePolicy) {
this.storagePolicy = storagePolicy;
}

public String getType() {
return type;
}

public void setType(String type) {
this.type = type;
}

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Override
public String toString() {
	return getClass().getName() + " {\n\taccessTime: " + accessTime
			+ "\n\tblockSize: " + blockSize + "\n\tchildrenNum: " + childrenNum
			+ "\n\tfileId: " + fileId + "\n\tgroup: " + group + "\n\tlength: "
			+ length + "\n\tmodificationTime: " + modificationTime
			+ "\n\towner: " + owner + "\n\tpathSuffix: " + pathSuffix
			+ "\n\tpermission: " + permission + "\n\treplication: "
			+ replication + "\n\tstoragePolicy: " + storagePolicy
			+ "\n\ttype: " + type + "\n}";
}

}