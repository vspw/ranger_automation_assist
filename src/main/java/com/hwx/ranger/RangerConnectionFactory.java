package com.hwx.ranger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
/**
 * 
 * @author wesley
 *
 */
public class RangerConnectionFactory {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	/** The default host to connect to */
	public static final String DEFAULT_HOST = "localhost";

	/** The default port */
	public static final int DEFAULT_PORT = 6080;

	/** The default username */
	public static final String DEFAULT_USERNAME = "admin";

	/** The default username */
	public static final String DEFAULT_PASSWORD = "admin";
	
	public static final String DEFAULT_PROTOCOL = "http://";
	
	public static final String DEFAULT_REPOSITORY = "hadoop";

	public static enum AuthenticationType {
		KERBEROS, PSEUDO
	}

	private String 	host 	= 	DEFAULT_HOST;
	private int 	port	=	DEFAULT_PORT;
	private String 	username=	DEFAULT_USERNAME;
	private String 	password=	DEFAULT_PASSWORD;
	private String 	repository=	DEFAULT_REPOSITORY;
	private String 	authenticationType	= AuthenticationType.KERBEROS.name();
	private RangerConnection rangerConnection;
	
	public RangerConnectionFactory() {
	}
	
	

	public RangerConnectionFactory(String host, int port , String username, String password, String repository, String authType) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.repository = repository;
		this.authenticationType = authType;
	}

	
	public RangerConnection getConnection() {
		//TODO: use pool ...
		
		Assert.notNull(host, "Property <host> must not be null");
		Assert.notNull(port, "Property <port> must not be null");
		Assert.notNull(username, "Property <username> must not be null");
		Assert.notNull(authenticationType, "Property <authenticationType> must not be null");
		Assert.notNull(repository, "Property <repository> must not be null");
		
		String httpfsUrl = DEFAULT_PROTOCOL + host + ":" + port;
		if (rangerConnection == null) {
			if (authenticationType.equalsIgnoreCase(AuthenticationType.KERBEROS.name())) {
				rangerConnection = new BasicAuthRangerConnection(httpfsUrl, username, password, repository);
			} else
			// if(authenticationType.equalsIgnoreCase(AuthenticationType.PSEUDO.name()))
			{
				rangerConnection = new BasicAuthRangerConnection(httpfsUrl, username, password, repository);
			}
		}
		return rangerConnection;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(String authenticationType) {
		this.authenticationType = authenticationType;
	}
	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}
}
