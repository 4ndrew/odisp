#!/bin/sh
# $Id: run.sh,v 1.2 2003/10/13 19:20:05 dron Exp $
JAVA_HOME=${JAVA_HOME:-/usr/local/jdk1.4.1}; export JAVA_HOME
CLASSPATH=$PWD/../build/; export CLASSPATH
CLASSES_CONF=$PWD/classes.cfg

$JAVA_HOME/bin/java com.novel.odisp.StandartDispatcher $CLASSES_CONF