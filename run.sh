#!/bin/sh
# $Id: run.sh,v 1.11 2005/01/27 14:18:14 valeks Exp $
JAVA_HOME=${JAVA_HOME:-/usr/local/jdk1.4.1}; export JAVA_HOME
CLASSPATH=$CLASSPATH:release/launcher.jar ; export CLASSPATH
CLASSES_CONF=$PWD/classes.cfg
# for debugging: 
# ./run.sh -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n
# jdb -attach 127.0.0.1:1039 (or other port)
JAVAFLAGS=$*

$JAVA_HOME/bin/java $JAVAFLAGS org.valabs.odisp.Launcher --libdir=release/ --libdir=lib/ -- $CLASSES_CONF

while [ -e 'restart.snap' ]; do
  $JAVA_HOME/bin/java $JAVAFLAGS org.valabs.odisp.Launcher --libdir=release/ --libdir=lib/ -- $CLASSES_CONF
done
