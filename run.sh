#!/bin/sh
# $Id: run.sh,v 1.9 2004/11/29 07:31:54 valeks Exp $
JAVA_HOME=${JAVA_HOME:-/usr/local/jdk1.4.1}; export JAVA_HOME
UUIDPATH=$PWD/lib/jug.jar
CLASSPATH=$CLASSPATH:$PWD/build/:$PWD/lib/xlang-parser.jar:$UUIDPATH; export CLASSPATH
CLASSES_CONF=$PWD/classes.cfg
# for debugging: 
# ./run.sh -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n
# jdb -attach 127.0.0.1:1039 (or other port)
JAVAFLAGS=$*

$JAVA_HOME/bin/java $JAVAFLAGS org.valabs.odisp.standart.Dispatcher $CLASSES_CONF
