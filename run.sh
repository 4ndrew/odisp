#!/bin/sh
# $Id: run.sh,v 1.8 2004/08/23 07:42:37 valeks Exp $
JAVA_HOME=${JAVA_HOME:-/usr/local/jdk1.4.1}; export JAVA_HOME
CLASSPATH=$CLASSPATH:$PWD/../build/:$PWD/../lib/xlang-parser.jar; export CLASSPATH
CLASSES_CONF=$PWD/classes.cfg
# for debugging: 
# ./run.sh -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n
# jdb -attach 127.0.0.1:1039 (or other port)
JAVAFLAGS=$*

$JAVA_HOME/bin/java $JAVAFLAGS org.valabs.odisp.standart.Dispatcher $CLASSES_CONF
