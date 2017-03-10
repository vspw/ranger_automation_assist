package com.hwx.ranger;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RangerAssistScheduler {
	protected static final Logger logger = LoggerFactory.getLogger(RangerAssistScheduler.class);
	public static void main(String[] args) {
		
		try
		{

		JsonUtils objJutils = new JsonUtils();
		logger.info("###############LETS BEGIN###############");
		logger.info("Make sure pass the input json file path with all necessary details");
		Scanner objScanner=new Scanner(new File(args[0]));
		String strJsonInput = objScanner.useDelimiter("\\Z").next();
		objScanner.close();
		logger.info("Parsing the json input file");
		Response objInput = objJutils.parseJSON(strJsonInput);
		logger.debug("fromInputJson->Environment: "+ objInput.getEnvDetails());

        //refer the keypass from the keystore
		logger.info("Reading the keyStoreFile using the input Path");
        URI uri1 =URI.create(objInput.getEnvDetails().getOpKeyStoreFile());
        java.nio.file.Path path1=Paths.get(uri1);
        SecretKeyUtil keyUtils= new SecretKeyUtil(path1, objInput.getEnvDetails().getOpKeyStorePassword().toCharArray(),true);
    	logger.info("Retrieve Entry Password from the keystore using alias");
        String varSafePW=new String(keyUtils.retrieveEntryPassword(objInput.getEnvDetails().getOpKeyAlias()));
        objInput.getEnvDetails().setOpPassword(varSafePW);
       	logger.info("Start the Timer threads with the input repeatPeriod (seconds)");
		Timer timer = new Timer();
		timer.schedule(new RangerAssist(objInput), 0, objInput.getEnvDetails().getRepeatPeriod()*1000);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
}
