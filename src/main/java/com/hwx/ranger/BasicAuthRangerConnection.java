package com.hwx.ranger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.security.authentication.client.AuthenticationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

public class BasicAuthRangerConnection implements RangerConnection {

	protected static final Logger logger = LoggerFactory.getLogger(BasicAuthRangerConnection.class);

	private String rangerUrl = RangerConnectionFactory.DEFAULT_PROTOCOL 
			+ RangerConnectionFactory.DEFAULT_HOST + ":" + RangerConnectionFactory.DEFAULT_PORT;
	private String principal = RangerConnectionFactory.DEFAULT_USERNAME;
	private String password = RangerConnectionFactory.DEFAULT_PASSWORD;
	private String repository = RangerConnectionFactory.DEFAULT_REPOSITORY;

	public BasicAuthRangerConnection() {
	}

	public BasicAuthRangerConnection(String rangerUrl, String principal, String password, String repository) {
		this.rangerUrl = rangerUrl;
		this.principal = principal;
		this.password = password;
		this.repository = repository;
	}



	protected static long copy(InputStream input, OutputStream result) throws IOException {
		byte[] buffer = new byte[12288]; // 8K=8192 12K=12288 64K=
		long count = 0L;
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			result.write(buffer, 0, n);
			count += n;
			result.flush();
		}
		result.flush();
		return count;
	}

	/**
	 * Report the result in JSON way
	 * 
	 * @param conn
	 * @param input
	 * @return
	 * @throws IOException
	 */
	private static String result(HttpURLConnection conn, boolean input) throws IOException {
		StringBuffer sb = new StringBuffer();
		if (input) {
			InputStream is = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			reader.close();
			is.close();
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", conn.getResponseCode());
		result.put("mesg", conn.getResponseMessage());
		result.put("type", conn.getContentType());
		result.put("data", sb);
		//
		// Convert a Map into JSON string.
		//
		Gson gson = new Gson();
		String json = gson.toJson(result);
		logger.info("json = " + json);

		//
		// Convert JSON string back to Map.
		//
		// Type type = new TypeToken<Map<String, Object>>(){}.getType();
		// Map<String, Object> map = gson.fromJson(json, type);
		// for (String key : map.keySet()) {
		// System.out.println("map.get = " + map.get(key));
		// }

		return json;
	}

	public String getPolicyByName(String policyName) throws MalformedURLException, IOException,
			AuthenticationException {
		
		URL url = new URL(new URL(rangerUrl), MessageFormat.format("/service/public/v2/api/service/{0}/policy/{1}", URLUtil.encodePath(repository),URLUtil.encodePath(policyName)));
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		Base64.Encoder encoder = Base64.getEncoder();
		String encodedString = encoder.encodeToString((principal+":"+password).getBytes(StandardCharsets.UTF_8) );
		conn.setRequestMethod("GET");
		conn.setRequestProperty  ("Authorization", "Basic " + encodedString);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		String resp = result(conn, true);
		conn.disconnect();

		return resp;
	}

	public String getPolicybyId(String id)
			throws MalformedURLException, IOException, AuthenticationException {
		
		URL url = new URL(new URL(rangerUrl), MessageFormat.format("/service/public/v2/api/policy/{0}", URLUtil.encodePath(id)));
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		Base64.Encoder encoder = Base64.getEncoder();
		String encodedString = encoder.encodeToString((principal+":"+password).getBytes(StandardCharsets.UTF_8) );
		conn.setRequestMethod("GET");
		conn.setRequestProperty  ("Authorization", "Basic " + encodedString);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		String resp = result(conn, true);
		conn.disconnect();

		return resp;
	}

	public String getAllRepositoryPolicies()
			throws MalformedURLException, IOException, AuthenticationException {
		// TODO Auto-generated method stub
		URL url = new URL(new URL(rangerUrl), MessageFormat.format("/service/public/v2/api/service/{0}/policy", URLUtil.encodePath(this.repository)));
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		Base64.Encoder encoder = Base64.getEncoder();
		String encodedString = encoder.encodeToString((principal+":"+password).getBytes(StandardCharsets.UTF_8) );
		conn.setRequestMethod("GET");
		conn.setRequestProperty  ("Authorization", "Basic " + encodedString);
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.connect();
		String resp = result(conn, true);
		conn.disconnect();

		return resp;
	}

	public String createPolicy(String jsonContent)
			throws MalformedURLException, IOException, AuthenticationException {
		// TODO Auto-generated method stub
		URL url = new URL(new URL(rangerUrl), URLUtil.encodePath("service/public/v2/api/policy"));
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		Base64.Encoder encoder = Base64.getEncoder();
		String encodedString = encoder.encodeToString((principal+":"+password).getBytes(StandardCharsets.UTF_8) );
		conn.setRequestMethod("POST");
		conn.setRequestProperty  ("Authorization", "Basic " + encodedString);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		//conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		//
		OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
        osw.write(jsonContent);
        osw.flush();
        osw.close();
		//
		conn.connect();
		String resp = result(conn, false);
		conn.disconnect();
		return resp;
	}

	public String updatePolicyByName(String policyName, String jsonContent) throws MalformedURLException,
			IOException, AuthenticationException {
		// TODO Auto-generated method stub
		logger.info(jsonContent);
		URL url = new URL(new URL(rangerUrl), MessageFormat.format("/service/public/v2/api/service/{0}/policy/{1}", URLUtil.encodePath(repository),URLUtil.encodePath(policyName)));
		logger.info(url.toString());
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		Base64.Encoder encoder = Base64.getEncoder();
		String encodedString = encoder.encodeToString((principal+":"+password).getBytes(StandardCharsets.UTF_8) );
		conn.setRequestMethod("PUT");
		conn.setRequestProperty  ("Authorization", "Basic " + encodedString);
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		//
		OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
        osw.write(jsonContent);
        osw.flush();
        osw.close();
		//
		conn.connect();
		String resp = result(conn, true);
		conn.disconnect();

		return resp;
	}

}
