#!/bin/bash
# Which java to use
if [ -z "$JAVA_HOME" ]; then
  JAVA="java"
else
  JAVA="$JAVA_HOME/bin/java"
fi

base_dir=$(dirname $0)

jars=${base_dir}/classes:${base_dir}/lib/*
echo ${jars}

exec ${JAVA} -classpath "${jars}" com.dcfs.esb.ftp.server.namenode.StartFileServer
