#!/usr/bin/env bash
HADOOP_HOME_PATH=/usr/hdp/current/hadoop-client
HADOOP_CONFIG_SCRIPT=$HADOOP_HOME_PATH/libexec/hadoop-config.sh
HADOOP_CLIENT_LIBS=$HADOOP_HOME_PATH/client
## including hs2 libs because of the dependency on guava 0.12 or greater. hdfs client libs have guava 0.11 only.
HIVESERVER2_LIBS=/usr/hdp/current/hive-server2/lib

if [ -e $HADOOP_CONFIG_SCRIPT ] ; then
        .  $HADOOP_CONFIG_SCRIPT
else
        echo "Hadoop Client not Installed on Node"
        exit 1
fi

WORKING_DIR="/home/t93ki8h/ranger-automation-assist"
LOG4J_PROP_FILE=$WORKING_DIR/log4j.properties
export WORK_PATH=$WORKING_DIR/*
PIDFILE=$WORKING_DIR/pid.tmp
OPTS="-Dlog4j.configuration=file:$LOG4J_PROP_FILE"

##Class and options passed
CLASS=com.hwx.ranger.RangerAssistScheduler
export FILE_FQDN=$WORKING_DIR/nyl_ranger_policy_input.json
UPN="hdfs-tech@TECH.HDP.NEWYORKLIFE.COM"
KEYTAB="/home/t93ki8h/ranger-automation-assist/hdfs.headless.keytab"
FREQ=30

CURR_PROGRAM=ranger-assist
case "${1:-}" in
 start)
        echo "Checking for previously running Ranger Assist..."
        if [ -f $PIDFILE ]; then
          PID=`cat $PIDFILE`
          if ! (ps -p $PID >/dev/null 2>/dev/null); then
            echo "$PIDFILE found with no process. Removing $PID..."
            rm -f $PIDFILE
          else
            tput bold
            echo "ERROR:$CURR_PROGRAM already running"
            tput sgr0
            echo "Check $PIDFILE for PID."
            exit -1
          fi
        fi

        echo "Starting $CURR_PROGRAM"
        nohup java -Dproc_$CLASS $OPTS -cp "$WORK_PATH:$HIVESERVER2_LIBS/*:$HADOOP_CLIENT_LIBS/*" $CLASS -i $FILE_FQDN -u $UPN -t $KEYTAB -q $FREQ  /tmp 2>> ./ra_error.log >> /dev/null &
        PID=$!
        echo $PID > $PIDFILE
        sleep 2
        echo "Verifying $CURR_PROGRAM process status..."
        if ! (ps -p $PID >/dev/null 2>/dev/null); then
          echo "ERROR: $CURR_PROGRAM start failed."
          exit -1
        fi
        tput bold
        echo "$CURR_PROGRAM successfully started"
        tput sgr0
        echo "Agent PID at: $PIDFILE"
        ;;

 stop)
        if [ -f $PIDFILE ]; then
          PID=`cat $PIDFILE`
          echo "Found $CURR_PROGRAM PID: $PID"
          if ! (ps -p $PID >/dev/null 2>/dev/null); then
            tput bold
            echo "ERROR: $CURR_PROGRAM not running. Stale PID File at: $PIDFILE"
            tput sgr0
          else
            echo "Stopping $CURR_PROGRAM"
            kill -9 $PID
          fi
          echo "Removing PID file at $PIDFILE"
          rm -f $PIDFILE
          tput bold
          echo "$CURR_PROGRAM successfully stopped"
          tput sgr0
        else
          tput bold
          echo "$CURR_PROGRAM is not running. No PID found at $PIDFILE"
          tput sgr0
        fi
        ;;

 restart)
        echo -e "Restarting $CURR_PROGRAM"
        scriptpath=$0
        $scriptpath stop
        shift
        $scriptpath start "$@"
        retcode=$?
        ;;
  *)
        tput bold
        echo "Usage: $0 {start|stop|restart}"
        tput sgr0
        exit 1
        ;;
esac
