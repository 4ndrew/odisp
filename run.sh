#!/bin/sh
# $Id: run.sh,v 1.6 2004/03/26 21:53:37 valeks Exp $
JAVA_HOME=${JAVA_HOME:-/usr/local/jdk1.4.1}; export JAVA_HOME
CLASSPATH=$CLASSPATH:$PWD/../build/:$PWD/../lib/xlang-parser.jar; export CLASSPATH
CLASSES_CONF=$PWD/classes.cfg
# for debugging: 
# ./run.sh -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n
# jdb -attach 127.0.0.1:1039 (or other port)
JAVAFLAGS=$*

$JAVA_HOME/bin/java $JAVAFLAGS com.novel.odisp.StandartDispatcher $CLASSES_CONF
