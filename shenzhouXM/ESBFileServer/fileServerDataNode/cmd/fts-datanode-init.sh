#!/bin/bash

ROOT_DIR="/opt/fts/app"
echo "fts-datanode部署根目录为 $ROOT_DIR"
LOG_DIR="/var/log/fts"
echo "fts-datanode日志根目录为 $LOG_DIR"

mkdir -p $ROOT_DIR
mkdir -p $LOG_DIR

echo "==================获取本机IP地址=================="

OS=`uname`
declare HOST_IP
case $OS in
   Linux) HOST_IP=`/sbin/ifconfig | grep 'inet addr:'| grep -v '127.0.0.1' | cut -d: -f2 | awk '{ print $1}'`;;
   FreeBSD|OpenBSD) HOST_IP=`ifconfig  | grep -E 'inet.[0-9]' | grep -v '127.0.0.1' | awk '{ print $2}'` ;;
   SunOS) HOST_IP=`ifconfig -a | grep inet | grep -v '127.0.0.1' | awk '{ print $2} '` ;;
   *) HOST_IP="Unknown";;
esac

if [ -z $HOST_IP ]; then
    HOST_IP=`ifconfig | grep 'inet '| grep -v '127.0.0.1' | awk '{ print $2}'`
fi

if [ -z $HOST_IP ]; then
    HOST_IP="127.0.0.1"
    echo "获取本机IP地址失败"
    exit 1
fi

echo "==================开始修改cfg.xml文件=================="

PROP_HOST_IP=`cat $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml |grep HOST_IP |cut -d'>' -f2| cut -d'<' -f1`
sed -i "s?\"本机IP地址\">$PROP_HOST_IP?\"本机IP地址\">$HOST_IP?g" $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml

PROP_NODE_NAME=`cat $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml |grep NODE_NAME |cut -d'>' -f2| cut -d'<' -f1`
NODE_NAME=`echo $HOST_IP |cut -d'.' -f4`

if [ ${#NODE_NAME} -eq 1 ]; then
    NODE_NAME='FSD00'$NODE_NAME
fi
if [ ${#NODE_NAME} -eq 2 ]; then
    NODE_NAME='FSD0'$NODE_NAME
fi
if [ ${#NODE_NAME} -eq 3 ]; then
    NODE_NAME='FSD'$NODE_NAME
fi

sed -i "s?\"节点名称(唯一)\">$PROP_NODE_NAME?\"节点名称(唯一)\">$NODE_NAME?g" $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml

FILE_ROOT_PATH=$ROOT_DIR/ftsfile
FILE_BACKUP_ROOT_PATH=$ROOT_DIR/ftsfileBackup

PROP_FILE_ROOT_PATH=`cat $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml |grep FILE_ROOT_PATH |cut -d'>' -f2| cut -d'<' -f1`
PROP_FILE_BACKUP_ROOT_PATH=`cat $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml |grep FILE_BACKUP_ROOT_PATH |cut -d'>' -f2| cut -d'<' -f1`

PROP_FILE_ROOT_PATH_FORMAT=`echo "$PROP_FILE_ROOT_PATH" |sed "s?\\\\\\\\?\\\\\\\\\\\\\\\\?g"`
PROP_FILE_BACKUP_ROOT_PATH_FORMAT=`echo "$PROP_FILE_BACKUP_ROOT_PATH" |sed "s?\\\\\\\\?\\\\\\\\\\\\\\\\?g"`

sed -i "s?$PROP_FILE_ROOT_PATH_FORMAT</entry>?$FILE_ROOT_PATH</entry>?g" $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml
sed -i "s?$PROP_FILE_BACKUP_ROOT_PATH_FORMAT</entry>?$FILE_BACKUP_ROOT_PATH</entry>?g" $ROOT_DIR/fts-datanode/classes/cfg/cfg.xml

mkdir -p $FILE_ROOT_PATH
mkdir -p $FILE_BACKUP_ROOT_PATH

