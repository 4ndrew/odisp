#!/bin/sh
# $Id: run.sh,v 1.3 2003/10/14 13:52:18 valeks Exp $
JAVA_HOME=${JAVA_HOME:-/usr/local/jdk1.4.1}; export JAVA_HOME
CLASSPATH=$CLASSPATH:$PWD/../build/; export CLASSPATH
CLASSES_CONF=$PWD/classes.cfg

$JAVA_HOME/bin/java com.novel.odisp.StandartDispatcher $CLASSES_CONF
