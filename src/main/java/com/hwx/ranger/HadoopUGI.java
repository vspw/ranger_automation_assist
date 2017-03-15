package com.hwx.ranger;


import java.io.IOException;
import javax.naming.directory.InitialDirContext;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HadoopUGI {
	private static final Logger LOG = LoggerFactory.getLogger(HadoopUGI.class);

	static InitialDirContext ctx = null;
	public static String gcbaseDn = "";
	static FileSystem fs = null;
	static String type = null;
	static boolean userFolder = false;
	String userPrincipalName;
	String keytabPath;
	UserGroupInformation ugi = null;
	static Configuration hdpConfig = new Configuration();

	public HadoopUGI(String userPrincipalName, String keytabPath) {

		this.userPrincipalName=userPrincipalName;
		LOG.debug("Current User Before UGI init: "+userPrincipalName);
		this.keytabPath=keytabPath;
		this.initUGIAndFileSystem();

	}

	public void initUGIAndFileSystem() {

		try {
			hdpConfig.addResource(new Path("/etc/hadoop/conf/core-site.xml"));
			hdpConfig.addResource(new Path("/etc/hive/conf/hive-site.xml"));
			hdpConfig.addResource(new Path("/etc/hadoop/conf/hdfs-site.xml"));
			ugi=null;
			if (! this.keytabPath.equals(null) ) {
				LOG.info("UseKeyTab is true");
				UserGroupInformation.setConfiguration(hdpConfig);
				ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(this.userPrincipalName,this.keytabPath);
				LOG.info("UGI name:"+ ugi.getUserName());
				UserGroupInformation.setLoginUser(ugi);
				ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);
			} else {
				LOG.info("UserKeyTab is False");
				ugi = UserGroupInformation.getCurrentUser();
				ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.SIMPLE);
			}
			LOG.debug("UserName UPN: "+ugi.getUserName());
			//hdpConfig.set("hadoop.security.authentication", "kerberos");
			LOG.info("Config hadoop.security.authentication: "+hdpConfig.get("hadoop.security.authentication"));
			LOG.info("Config dfs.namenode.kerberos.principal: "+hdpConfig.get("dfs.namenode.kerberos.principal"));
			LOG.info("Config fs.defaultFS: "+hdpConfig.get("fs.defaultFS"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			LOG.error(e1.getMessage());
		}

		//ugi.setAuthenticationMethod(UserGroupInformation.AuthenticationMethod.KERBEROS);

	}



}
