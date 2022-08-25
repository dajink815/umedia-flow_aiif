#/*************************************************************************************
#
#   Copyright(c)2020 by UANGEL, Inc.
#    All rights reserved.
#
# All source codes & script in this file must not be reproduced, stored in a retrieval
# system, transmitted in any form without the permission of UANGEL, Inc.
#
#*************************************************************************************/
#
#/*************************************************************************************
#                                Startup Script
#                              ------------------
#      begin              :  Sep 24 2020
#      email              :  Jong Chul Lim, TonyLim (tonylim@uangel.com)
#*************************************************************************************/
#
#/*************************************************************************************
#    change history
#    2020. 9. 24 : Initial Code (Tonylim)
#*************************************************************************************/

#!/bin/sh
HOME=/home/aicall
UASYS_HOME=$HOME/HOME
LD_LIBRARY_PATH=$UASYS_HOME/oam/lib:$HOME/aiif/lib
export HOME UASYS_HOME LD_LIBRARY_PATH
PATH_TO_JAR=aiif-jar-with-dependencies.jar
SERVICE_NAME=AIIF
JAVA_CONF=$HOME/aiif/config/
JAVA_OPT="-Dlogback.configurationFile=$HOME/aiif/config/logback.xml"
JAVA_OPT="$JAVA_OPT -Dio.netty.leakDetectionLevel=simple -Djdk.nio.maxCachedBufferSize=262144 -Dio.netty.allocator.type=unpooled"
JAVA_OPT="$JAVA_OPT -Dio.netty.noPreferDirect=true"
JAVA_OPT="$JAVA_OPT -XX:+UnlockDiagnosticVMOptions -XX:+LogVMOutput -XX:LogFile=$HOME/aiif/logs/thd_dmp.log"
JAVA_OPT="$JAVA_OPT -XX:+UseG1GC -XX:G1RSetUpdatingPauseTimePercent=5 -XX:MaxGCPauseMillis=500 -XX:+UseLargePagesInMetaspace -XX:+UseLargePages -verbosegc  -Xms4G -Xmx4G"
JAVA_OPT="$JAVA_OPT -Xlog:gc*:file=$HOME/aiif/logs/gc.log:time:filecount=10,filesize=10M:gc+heap=trace:age*=debug:safepoint:gc+promotion=debug"

#ulimit -n 65535
#ulimit -s 65535
#ulimit -Hn 65535
#ulimit -Hs 65535

if [ "$USER" != "aicall" ] ; then
	echo "Need to be application account(aicall)"
	exit 1
fi

checkfile()
{
	if [ ! -e $1 ]; then
		echo "$1" file does not exist.
		exit 2
	fi
}
checkdir()
{
	if [ ! -d $1 ]; then
		echo "$1" directory does not exist.
		exit 3
	fi
}

case $1 in
    start)

	if [ -f "$HOME/aiif/lib/$PATH_TO_JAR" ]; then

	  java $JAVA_OPT $DEBUG -classpath $HOME/aiif/lib/$PATH_TO_JAR com.uangel.aiif.AiifMain $JAVA_CONF > /dev/null 2>&1 &
	  echo "$SERVICE_NAME started ..."
	  /usr/bin/logger -p info -t "$0" "AIIF started"
	else
	  echo "(ERROR) start fail : $?"
	  exit 4
	fi
    ;;
    stop)
	PID=`ps -ef | grep java | grep AiifMain | awk '{print $2}'`
	if [ -z $PID ]
	then
		echo "aiif is not running"
	else
		echo "stopping aiif"
		kill $PID
		sleep 1
		PID=`ps -ef | grep java | grep AiifMain | awk '{print $2}'`
		if [ ! -z $PID ]
		then
			echo "kill -9"
			kill -9 $PID
		fi
		echo "aiif stopped"
		/usr/bin/logger -p info -t "$0" "AIIF stopped"
	fi
    ;;
esac