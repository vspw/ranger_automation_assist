package com.hwx.ranger;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileStatusResponse {

@SerializedName("FileStatus")
@Expose
private FileStatus fileStatus;

public FileStatus getFileStatus() {
return fileStatus;
}

public void setFileStatus(FileStatus fileStatus) {
this.fileStatus = fileStatus;
}

}