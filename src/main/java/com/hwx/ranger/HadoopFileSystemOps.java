package com.hwx.ranger;


import java.io.IOException;
import java.net.URI;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;

import org.apache.adldap.KerberosClient;
import org.apache.adldap.LdapApi;
import org.apache.adldap.LdapClient;
import org.apache.adldap.LdapClientSASL;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HadoopFileSystemOps {
	private static final Logger LOG = LoggerFactory.getLogger(HadoopFileSystemOps.class);

	static InitialDirContext ctx = null;
	public static String gcbaseDn = "";
	static FileSystem fs = null;
	static String type = null;
	static boolean userFolder = false;
	static KerberosClient krbClient;
	private final Object lock = new Object();
	private final Object userProclock = new Object();
	static String userPrincipalName;
	static Response objInputAssist=null;
	UserGroupInformation ugi = null;
	static Configuration hdpConfig = new Configuration();
	
	public HadoopFileSystemOps(Response objInput) {

		try {
			this.objInputAssist=objInput;
			userPrincipalName = UserGroupInformation.getCurrentUser().getUserName();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void manageFolders() {

		try {
			if (objInputAssist.getEnvDetails().isUseHdfsKeytab()) {
	            ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(objInputAssist.getEnvDetails().getHdfsKeytabUpn(),objInputAssist.getEnvDetails().getHdfsKeytab());
	            UserGroupInformation.setLoginUser(ugi);
	            ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
			} else {
				ugi = UserGroupInformation.getCurrentUser();
				ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.SIMPLE);
			}
			LOG.debug("HdfsUPN: "+ugi.getUserName());

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			LOG.error(e1.getMessage());
		}
		//ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
		try {
			ugi.doAs(new PrivilegedExceptionAction<Void>() {

				public Void run() throws Exception {

					try {
					    hdpConfig.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
					    hdpConfig.addResource(new Path("/etc/hive/conf/hive-site.xml"));
					    hdpConfig.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
					    //hdpConfig.set("hadoop.security.authentication", "kerberos");
						LOG.info("Config: "+hdpConfig.get("hadoop.security.authentication"));
						LOG.info("Config: "+hdpConfig.get("dfs.namenode.kerberos.principal"));
						LOG.info("Config: "+hdpConfig.get("fs.defaultFS"));
						LOG.info("Before initialized file system");
						//fs = FileSystem.get(new URI("hdfs://xena.hdp.com:8020"), hdpConfig);
						fs = FileSystem.get(hdpConfig);
						LOG.info("initialized file system");
				    	  if (fs.exists(new Path("/"))) {
				    		  org.apache.hadoop.fs.FileStatus[] fileStatus = fs.listStatus(new Path("/"));
				    		    for( org.apache.hadoop.fs.FileStatus status : fileStatus){
				    		        LOG.info(status.getPath().toString());
				    		    }
                                    LOG.info("FolderExists");
		
				    	  }
				    	  else
				    	  {
				    		  LOG.error("Failed to connect to HDFS");
				    	  }
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						LOG.info(e2.getMessage());
						e2.getStackTrace();
					}

					return null;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage());
		}

	}
	
}
