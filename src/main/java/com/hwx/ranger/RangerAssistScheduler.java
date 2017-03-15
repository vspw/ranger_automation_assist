package com.hwx.ranger;
/*
 * Usage:
 * java -Dproc_com.hwx.ranger.RangerAssistScheduler 
 * -Dlog4j.configuration=file:///home/t93ki8h/ranger-automation-assist/log4j.properties  
 * -cp /usr/hdp/current/hadoop-client/client/*:./ranger-automation-assist-0.0.1-SNAPSHOT.jar 
 * com.hwx.ranger.RangerAssistScheduler -i nyl_ranger_policy_input.json 
 * -u hdfs-tech@TECH.HDP.NEWYORKLIFE.COM 
 * -t /home/user1223/ranger-automation-assist/hdfs.headless.keytab 
 * -q 30
 * 
 */
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.file.Paths;
import java.security.PrivilegedExceptionAction;
import java.util.Scanner;
import java.util.Timer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RangerAssistScheduler {
	protected static final Logger logger = LoggerFactory.getLogger(RangerAssistScheduler.class);
	static HadoopUGI objHDFSOps=null;
	public static void main(String[] args) throws IOException {
		
        Options options = new Options();

        Option input = new Option("i", "input", true, "input file path");
        input.setRequired(true);
        options.addOption(input);

        Option upn = new Option("u", "upn", true, "User principal for HDFS operations");
        upn.setRequired(true);
        options.addOption(upn);

        Option keytab = new Option("t", "keytab", true, "Keytab file path for HDFS authentication");
        keytab.setRequired(true);
        options.addOption(keytab);
        
        Option freq = new Option("q", "freq", true, "The frequency in seconds for repeat period of application");
        freq.setRequired(true);
        options.addOption(freq);
        
        CommandLineParser parser = new GnuParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            logger.info(e.getMessage());
            
            Writer buffer = new StringWriter();
            PrintWriter pw = new PrintWriter(buffer);
            formatter.printUsage(pw, 512, "java -Dlog4j.configuration=file:<log4jfile> -cp <hadoop_client_libs>" + RangerAssistScheduler.class.getName(), options);
            logger.info(buffer.toString());
            pw.flush();
            buffer.close();
            pw.close();
            System.exit(1);
            return;
        }

        final String inputFilePath = cmd.getOptionValue("input");
        final String upnAuth = cmd.getOptionValue("upn");
        final String keytabAuth = cmd.getOptionValue("keytab");
        final int freqSeconds = Integer.parseInt(cmd.getOptionValue("freq"));

        logger.info("InputFilePath: "+inputFilePath);
        logger.info("upnAuth: "+upnAuth);
        logger.info("keytabAuth: "+keytabAuth);
        logger.info("freqSeconds: "+freqSeconds);
        
		try
		{

		
		logger.info("Establishing Connection to HDFS. This initializes the UGI for future processing.");
		objHDFSOps= new HadoopUGI(upnAuth,keytabAuth);
		objHDFSOps.ugi.doAs(new PrivilegedExceptionAction<Void>() {

			public Void run() throws Exception {
				logger.info("Start the Timer threads with the input repeatPeriod (seconds)");
				Timer timer = new Timer();
				timer.schedule(new RangerAssist(inputFilePath), 0, freqSeconds*1000);

				return null;
			}
		});
        
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}
}
