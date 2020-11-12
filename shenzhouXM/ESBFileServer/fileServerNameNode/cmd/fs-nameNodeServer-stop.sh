#!/bin/bash
ps -ef|grep com.dcfs.esc.ftp.namenode.server.StartFileServer|grep -v grep|awk '{print $2}'|xargs kill -9 >/dev/null 2>&1