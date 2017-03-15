package com.hwx.ranger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HadoopFileSystemOps {

	static FileSystem fs = null;
	static Configuration hdpConfig = new Configuration();
	private static final Logger logger = LoggerFactory.getLogger(HadoopUGI.class);

	public HadoopFileSystemOps() {
		hdpConfig.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
		hdpConfig.addResource(new Path("/etc/hive/conf/hive-site.xml"));
		hdpConfig.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
		try {
			fs = FileSystem.get(hdpConfig);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Initialized file system object");
	}

	public boolean isHdfsPathValid( String strPath, boolean checkForDir) throws IOException
	{
		boolean retVal=false;
		Path path=new Path(strPath);

		if (fs.exists(path)) {
			//org.apache.hadoop.fs.FileStatus[] fileStatus = fs.listStatus(path);
			//for( org.apache.hadoop.fs.FileStatus status : fileStatus){
			//    LOG.info(status.getPath().toString());
			//}
			logger.info("HDFSOps:Path Exists: "+path.getName());
			retVal=true;
			if(checkForDir)
			{
				logger.info("HDFSOps:Directory Exists: "+path.getName());
				retVal=fs.isDirectory(path);
			}
		}

		return retVal;

	}

	public List<String> getHdfsListStringPaths( String strPath) throws IOException
	{
		Path path=new Path(strPath);
		List<String> listStringPaths = new ArrayList<String>();
		//RemoteIterator<LocatedFileStatus> iteratorLocatedFileStatus=null;
		FileStatus[] arrayFileStatus=null;
        arrayFileStatus = fs.listStatus(path);
		for(FileStatus hdfsFileStatus : arrayFileStatus)
		{
			//LocatedFileStatus objLocFileStatus=iteratorLocatedFileStatus.next();
			if (hdfsFileStatus.isDirectory() && !(hdfsFileStatus.getPath().getName().charAt(0)=='.'))
			{
				logger.debug("HDFSOps: adding to directory Path list: "+hdfsFileStatus.getPath().getName());
				listStringPaths.add(hdfsFileStatus.getPath().getName());
			}
		}
		return listStringPaths;

	}
}
