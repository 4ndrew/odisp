#!/bin/sh
# $Id: run.sh,v 1.4 2003/10/14 19:14:27 dron Exp $
JAVA_HOME=${JAVA_HOME:-/usr/local/jdk1.4.1}; export JAVA_HOME
CLASSPATH=$CLASSPATH:$PWD/../build/; export CLASSPATH
CLASSES_CONF=$PWD/classes.cfg
# for debugging: 
JAVAFLAGS=$*

$JAVA_HOME/bin/java $JAVAFLAGS com.novel.odisp.StandartDispatcher $CLASSES_CONF
