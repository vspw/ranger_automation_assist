##Objective
The objective of the application is to help with the management of Ranger HDFS Policies by Create New policies and Maintain existing Ranger Policies.  Features include:-
- Dynamic Detection of HDFS directories at a given depth and create corresponding Ranger Policy.
- Maintenance of existing Ranger Policies by reverting any unwanted changes. Thus, the ranger policies are consistent with the input file provided to the appliction. Any changes to the governed ranger policies should be done from the input file ONLY.
- Auto Identification feature allows for naming Ranger Policies based on the HDFS Directory Naming convention.

##Contents:-
[resources](/src/main/resources/)
- hdp_ranger_policy_input.json
This is the the input file which is supplied to the application. This file dictates which Ranger policies should be managed by this application. HDFS Ranger policies which are not within this input file will NOT be maintained by the program. Any new Ranger Policy should be added via this input file.
- log4j.properties
The log4j properties which helps with logging.
- ranger_assist_exec.sh
This is a wrapper around this java application that helps with starting/stopping/restarting the application. It includes the classpath directions etc.
- keystore.jceks (Although this file is not in Git, the application expects this file to get credentials for Ranger connection. See usage section)

[java](src/main/java/com/hwx/ranger/)
###Main Class:-
- RangerAssistScheduler.java
This application has to be initiated using this class. It handles the input options, creates the Hadoop UGI based on the keytab provided and starts the Ranger Automation threads based on the input option- frequency (see Input/Usage section).
Helper/Utility Classes:-
- HadoopUGI.java
This class initiates the Hadoop UGI using the keytab provided to this application. It reads the appropriate hadoop config files.
- JsonUtils.java
This class helps with parsing the Json input/output files into appropriate Java objects.
- HadoopFileSystemOps.java
This helper class has functions to wrap around the Hadoop File system operations like isDirectory, listStatus etc. Its used for navigating through HDFS directories.
- RangerAssist.java
This class does the bulk of the intended work with regards to maintenance of HDFS Ranger policies. Please read the subsequent sections for more infomation.
- SecretKeyUtil.java
This class is used to operate on the Java JCEKS keystore. The keystore is used to host the password for the user whose credentials are used to connect to Ranger and update/create policies.
- RangerConnection.java
This is an Interface implemented by BasicAuthRangerConnection. The idea was to support other connection types (kerberos connection) etc going forward.
- BasicAuthRangerConnection.java
This class helps with establishing connection to the Ranger service and has the wrappers to interact with Ranger REST API to post/get information. Functions include: updatePolicyByName, createPolicy. 


###Classes used to parse the input json
- Response.java
The input json file (hdp_ranger_policy_input.json) is read using Gson into this object (Response). The Response object further comprises of EnvDetails and HDFSCheckList objects.
- EnvDetails.java
Any generic properties/variables which are common to the environment can be read/stored via this object. For instance- envName, rangerURI, opUsername etc.
- HDFSCheckList.java
Each HDFSCheckList object represents an HDFS Ranger Policy. Understanding the elements in this object is vital to use the features provided by this application. Please see Usage and Control-Flow sections for more information. 
- PolicyItem.java
Each HDFSCheckList object has a list of PolicyItem objects. Each PolicyItem object has Access information (rwx info) and associated users and groups. 
- Access.java
Access objects have a type and an associated boolean flag which represents whether a user or group has privileges for the said type.
Eg: type can be read, and isAllowed flag can be set to true of false.

###Classes used to parse the json responses from Ranger REST calls
- RangerPolicyResponse.java
The json responses from Ranger REST API are parsed into this object in java. This object further comprises of objects such as Resources, PolicyItem etc. to accurately represent a Ranger Policy.
- Resources.java
Wrapper around a Path object in Ranger.
- Path.java
Represents a Ranger Path resource. Has a string representing an hdfs path and boolean attributes like isRecursive, isExcludes.
- PolicyItem.java
Same as the one mentioned above.
- Access.java
Same as the one mentioned above.

##Input/Usage
The Main Class (RangerAssistScheduler) expects the following options to be passed when the application is invoked:-
1. input.json (set of ranger policies which are monitored and maintained) -i
2. user-principal-name (upn using which we connect to hdfs) -u
3. keytab (keytab path for the upn) -t
4. frequency (repeat-period for the application) -q

###Invoking main class
```
java -Dproc_com.hwx.ranger.RangerAssistScheduler -Dlog4j.configuration=file:///home/user123/ranger-automation-assist/log4j.properties  -cp /usr/hdp/current/hadoop-client/lib/*:./ranger-automation-assist-0.0.1-SNAPSHOT.jar com.hwx.ranger.RangerAssistScheduler -i hdp_ranger_policy_input.json -u hdfs@TECH.HDP.hdphost.COM -t /home/user123/ranger-automation-assist/hdfs.headless.keytab -q 30 
```

- ranger_assist_exec.sh is a helper script which is wrapper around the above jar. It can work with three options:-
```
./ranger_assist_exec.sh start
./ranger_assist_exec.sh stop
./ranger_assist_exec.sh restart
```
It also creates a pid file in the working dir. It has all the variables to satisfy the classpath.


###Input JSON (hdp_ranger_policy_input)
This file has two sections 
- envdetails
- hdfschecklist. 

envdetails has the following items:-

```
{
        "envName": "Tech",
        "hdfsURI": "",
        "rangerURI": "https://ranger.tech.hdp.hdphost.com",
        "opUsername": "user123@HQ.NT.hdphost.COM",
        "opPassword": "",
        "opKeyAlias": "hadoop",
        "opKeyStoreFile": "file:///home/user123/ranger-automation-assist/rastore.jceks",
        "opKeyStorePassword": "hadoop1234",
        "useHdfsKeytab":true,
        "hdfsKeytabUpn":"",
        "hdfsKeytab":"",
        "repeatPeriod": 
} 
```
envName - environment name. Lab, DR, Prod etc.
hdfsURI - is meant to be used for WebHDFS calls. Can be left empty as the application uses HDFS native FileSystem API.
rangerURI- URI(host and port) used to communicate with ranger service.
opUsername - username used to authenticate to Ranger
opPassword - can be left empty as we are deriving it from the keystore
opKeyAlias - key alias for the password stored in keystore JCEKS file
opKeyStoreFile - path for the keystore JCEKS file.
useHdfsKeytab - flag which can be set to indicate that authentication for HDFS connection is done via a keytab
hdfsKeytabUpn - can be left empty as we are supplying this as an input option
hdfsKeytab - can be left empty as we are supplying this as an input option
repeatPeriod - can be left empty as we are supplying this as an input option

hdfschecklist is an array of policy inputs (aka. input item(s)). Each input consists of the following items:-

```
{
        "repositoryName": "tech_hadoop",
        "depth": 0,
        "description": "Public Data that is Readable by Cluster Users",
        "resourceName": "End-User:Get-All-Contents2",
        "paths": ["/user/oozie","/source/public","/data/base"],
        "isEnabled": false,
        "isRecursive": true,
        "allowRangerPathDelete": false,
        "autoIdentifyAttributes": false,
        "policyItems": [{
            "accesses": [{
                "type": "read",
                "isAllowed": true
            }, {
                "type": "execute",
                "isAllowed": true
            }],
            "users": [],
            "groups": ["hdp-user"],
            "conditions": [],
            "delegateAdmin": false
        }]
}
```

repositoryName - Name of the HDFS Repository in Ranger.
depth - the depth relative to the "paths" input below, which needs to be monitored in case of dynamic policies. For instance a depth of 2 on '/base' with consider /base/sub1/mod1, /base/sub1/mod2 and /base/sub2/mod3. Check the Usage patterns section for more info on dynamic policies. 
description - the description common to all policies created/maintained as part of this input checklist item.
resourceName - the Ranger Policy name
paths - the list of paths to be considered. Should be absolute HDFS path. Should NOT have any "hdfs://" prefix.
isEnabled - boolean indicating if the new or existing policy should be enabled or disabled.
isRecursive - boolean indicating if the new or existing policy should be recursive in nature
allowRangerPathDelete - boolean indicating if the paths in Ranger policy need to be deleted if they do not exist in HDFS. This flag is not being handled currently in the application as by default Ranger policy paths are deleted if corresponding HDFS directory does not exist.
autoIdentifyAttributes - this boolean flag is set to true to indicate if this checklist input item is meant for dynamically creating and maintaining ranger policies based on the hdfs directory structure. Refer to usage patterns for more information.
autoIdentifyAttributesKeys - the keys which should be used in conjunction with autoIdentifyAttributes. See usage pattern section for more info.
policyItems - This specifies the List of ACLs to be applied in the ranger policy. Multiple ACL+user/group combinations can be provided as required.

##Usage Patterns:-
This section describes the different ways to maintain the Ranger policies based on the inputs applied via the HDFSCheckList item. Each HDFSCheckList can broadly follow one of the three patterns.

###1. Standard - non dynamic input

"depth" = 0 ensures that "paths" are not dynamically evaluated. The above input item with "depth"=0, would consider the paths mentioned in "paths" array element, and straight up apply the ACLs listed in the "policyItems" element.

Eg:-
```
{
        "repositoryName": "tech_hadoop",
        "depth": 0,
        "description": "Public Data that is Readable by Cluster Users",
        "resourceName": "End-User:-Get-Direct-Contents2",
        "paths": ["/","/apps","/apps/hive","/data/base"],
        "isEnabled": false,
        "isRecursive": true,
        "allowRangerPathDelete": false,
        "autoIdentifyAttributes": false,
        "policyItems": [{
            "accesses": [{
                "type": "read",
                "isAllowed": true
            }, {
                "type": "execute",
                "isAllowed": true
            }],
            "users": [],
            "groups": ["hdp-user"],
            "conditions": [],
            "delegateAdmin": false
        }]
}
```


###2. Dynamic with autoIdentifyAttributes disabled 

"depth" > 0, ensures that the HDFS directories in "paths" are evaluated recursively for the "depth" mentioned.
For instance, consider the following HDFS directory structure.
```
/data
  /data/base
    /data/base/hr
      /data/base/hr/oregon
         /data/base/hr/oregon/portland
      /data/base/hr/alaska
         /data/base/hr/alaska/anchorage
         /data/base/hr/alaska/nightmute
      /data/base/hr/virginia
         /data/base/hr/virginia/herndon
         /data/base/hr/virginia/vienna
    /data/base/eng
      /data/base/eng/washington
         /data/base/eng/washington/seattle
         /data/base/eng/washington/olympia
      /data/base/eng/california
         /data/base/eng/california/santaclara
         /data/base/eng/california/paloalto
```
An input item with paths: "/data/base" and depth 2 - should create and maintain a ranger policy with name "resourceName" and paths "/data/base/hr/oregon", "/data/base/hr/alaska", "/data/base/hr/virginia" , "/data/base/eng/washington" and "/data/base/eng/california". 

Let us suppose tomorrow, you have a new department (testing) and/or a new state (texas) added to the HDFS directory structure. This 1 policy should dynamically add that HDFS path to the appropriate Ranger policy.


Eg:-
```
{
        "repositoryName": "tech_hadoop",
        "depth": "2",
        "description": "Allow Read",
        "resourceName": "EndUser:Dev-Admin",
        "paths": ["/data/base"],
        "isEnabled": false,
        "isRecursive": true,
        "allowRangerPathDelete": "false",
        "policyItems": [{
            "accesses": [{
                "type": "read",
                "isAllowed": true
            },{
                "type": "write",
                "isAllowed": true
            }, {
                "type": "execute",
                "isAllowed": true
            }],
            "users": [],
            "groups": ["hdp-admin"],
            "conditions": [],
            "delegateAdmin": false
        }]
}
```
###3. Dynamic with autoIdentifyAttributes enabled

The primary difference between this Pattern and Pattern-2 is that Pattern-2 works on ONE Ranger Policy. Any new paths created in HDFS would get added into that ONE static Ranger Policy Item. However, they may be cases where we would need separate Ranger Policies with separate ACLs based on the privileges for an HDFS Path. 

Consider the same HDFS directory structure:-
```
/data
  /data/base
    /data/base/hr
      /data/base/hr/oregon
         /data/base/hr/oregon/portland
      /data/base/hr/alaska
         /data/base/hr/alaska/anchorage
         /data/base/hr/alaska/nightmute
      /data/base/hr/virginia
         /data/base/hr/virginia/herndon
         /data/base/hr/virginia/vienna
    /data/base/eng
      /data/base/eng/washington
         /data/base/eng/washington/seattle
         /data/base/eng/washington/olympia
      /data/base/eng/california
         /data/base/eng/california/santaclara
         /data/base/eng/california/paloalto
```
- "/data/base/eng/washington" must be read,written only by AD group "hdpdev-eng-washington-rw"
- "/data/base/hr/oregon" must be read,written only by AD group "hdpdev-hr-oregon-rw"

In such cases where each path in HDFS might have a separate ACL, this pattern type can be used.

```
{
        "repositoryName": "tech_hadoop",
        "depth": 2,
        "description": "Dynamic-",
        "resourceName": "Dynamic:Dept-State-ReadWrite",
        "paths": ["/data/base/"],
        "isEnabled": false,
        "isRecursive": false,
        "allowRangerPathDelete": false,
        "autoIdentifyAttributes": true,
        "autoIdentifyAttributesKeys": ["Dept","State"],
        "policyItems": [{
            "accesses": [{
                "type": "read",
                "isAllowed": true
            }, {
                "type": "execute",
                "isAllowed": true
            }, {
                "type": "write",
                "isAllowed": true
            }],
            "users": [],
            "groups": ["hdp-Dept-State-rw"],
            "conditions": [],
            "delegateAdmin": false
}
```
**How autoIdentifyAttributes works?**

If autoIdentifyAttributes is true:-

First, find a list of directories under a depth 2 for path "/data/base". Lets call these "depth-paths"
```
  /data/base/hr/oregon
  /data/base/hr/alaska
  /data/base/hr/virginia
  /data/base/eng/washington
  /data/base/eng/california
```
Second, autoIdentifyAttributesKeys are read by the application. The application then tries to find appropriate values for these keys.
- Parsing "depth-paths" from REVERSE and assign each key to corrponding sub-directory (values).
```
Dept=hr
State=oregon
```
Create a Ranger Policy for these set of key-values (Policy Name: Dynamic:hr-oregon-ReadWrite and ACL applied to group: hdp-hr-oregon-rw)

```
Dept=hr
State=alaska
```
Create a Ranger Policy for these set of key-values (Policy Name: Dynamic:hr-alaska-ReadWrite and ACL applied to group: hdp-hr-alaska-rw)

```
Dept=hr
State=virginia
```
Create a Ranger Policy for these set of key-values (Policy Name: Dynamic:hr-virginia-ReadWrite and ACL applied to group: hdp-hr-virginia-rw)

```
Dept=eng
State=washington
```
Create a Ranger Policy for these set of key-values (Policy Name: Dynamic:eng-washington-ReadWrite and ACL applied to group: hdp-eng-washington-rw)

```
Dept=eng
State=california
```
Create a Ranger Policy for these set of key-values (Policy Name: Dynamic:eng-california-ReadWrite and ACL applied to group: hdp-eng-california-rw)

The above input item will result in the following separate policies in Ranger:-
 * Dynamic:hr-oregon-ReadWrite
 * Dynamic:hr-alaska-ReadWrite
 * Dynamic:hr-virginia-ReadWrite
 * Dynamic:eng-washington-ReadWrite
 * Dynamic:eng-california-ReadWrite

- Make sure that the appropriate groups like "hdpdev-hr-oregon-rw" already exist in Ranger.

- Understand that autoIdentifyAttributes works by parsing paths from reverse and assigning it to the "autoIdentifyAttributesKeys" in the input. Hence "depth-paths" calculated in Step 1 form the basis of which Keys gets replaced with which values. Order is important. For instance, "hr" will be assisned to key1 (Dept) and "oregon" will be assigned to key2 (State).
The autoIdentifyAttributes feature would not work if the depths are off. For instance, for the previous input item, if depth is changed to 3 then, the depth-paths calcuated in Step1 are: "/data/base/hr/oregon/portland", "/data/base/hr/alaska/anchorage" and so on. In this case, while parsing from REVERSE, the keys are assigned as:-
Dept=oregon
State=portland
and
Dept=alaska
State=anchorage
and so on...
which is clearly incorrect. Corrosponding AD groups would not exist in Ranger and policy creating would fail.

- Make sure that the number of autoIdentifyAttributesKeys corrospond to the number of tokens in the placeholders you would want to be replaced in "groups",  "resourceName" or even "description". For instance, if <Dept> and <State> are your keys then the placeholders for "groups",  "resourceName" and "description" should have "Dept" and "State" keys mentioned.

- Note that a "depth" : 3 and "paths" : ["/data"] would also work similarly. However, the policy will apply not just on "/data/base" but ALSO on "/data/test", for instance. If you want to restrict this policy to "/data/base" make sure you use the appropriate path and depth combinations (depth : 2 and paths : ["/data/base"])

- This pattern will , like pattern-2, will also consider new HDFS Paths which might get created in the future and create appropriate Ranger Policies dynamically.
 
Editing existing input item vs. creating new input item:-
Consider the combination of elements - "depth", "paths" and "policyItems" when you are trying to decide on whether to add to an existing list of input items or modify an already present input item (hdfschecklist item). For instance, if an hdfschecklist item exists with "depth" 0 and the ACLs in "policyItems" are what you need for your HDFS path, just edit the already existing hdfschecklist item to include your HDFS path in the "paths" element.
Also, consider using Role based Ranger policies (End-User:Get-Direct-Contents) to help with the editing based on roles required for the new path.

##Control Flow:-
A brief description the control flow of the program:-
1. Read the UPN, keytab options from the input. Get the User Security Credentials and execute the rest of the flow as said user. 
2. Schedule a new Thread every "n" seconds based on the input frequency option specified.
3. Parse the json input file. (Changes made to the input file should be dynamically grabbed without restarting the application)
4. Read keyStoreFile from the path in input json item "opKeyStoreFile". Get the credentials. Connect to Ranger.
5. For each input item in json (HDFSCheckList):-
  1. From every path specified in "paths" element get a list of depth-paths. If depth paths are empty go to next HDFSCheckList item.
  2. Check if AutoIdentifyAttributes is true. (in which case create appropriate policies in Ranger and go to next HDFSCheckList item). Else go to 5.3
  3. Check if an existing Ranger policy already exists for the "resourceName". If policy is NOT found in Ranger then create a new policy and go to next HDFSCheckList item.
   * IF policy if already found in Ranger then, then compare the current HDFSCheckList item with the ranger policy and edit the Ranger Policy if it deviates from the HDFSCheckList item.

Logging:-
The classes in the application have appropriate log, log-levels specified. The log4j properties file can be used to alter the log-level and log-file paths and names.

##Deploy Instructions:-
1. Copy the git project and build the maven jar.
2. Copy the files in git-project resources to the appropriate deployment directory in an HDFS client node. (the application need HDFS libs and com.google.guava lib greater than version 0.11)
3. Edit the hdp_ranger_policy_input.json, to have the necessary input items, env details.
 Edit the log4j.properties to have appropriate log4j details
 Edit file ranger_assist_exec.sh, to have appropriate classpaths, working dir etc.
4. Make sure that the keytab used to work with HDFS is acceessible.
5. Make sure that the keystore JCEKS file is present, accessible. Edit the alias and password in hdp_ranger_policy_input.json env-details.
- IMPORTANT: Password for Ranger LDAP Authentication, if updated, needs to be updated in the keystore.
6. start the application using- ./ranger_assist_exec.sh start
<ToDo: Check accuracy for the build instructions>