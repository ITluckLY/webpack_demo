#echo packaging started
#mvn clean package -DskipTests=true -Pjarwar,sita
#echo packaging completed
cd jarwar/fts-datanode

echo begin to update properties file ins fts-datanode

if [ $1 == "A" ];then
	echo "检测到百信SIT_A环境"
	cp  -f $PROFILE/fts-datanode/A/efs.properties  classes/efs.properties
	cp  -f $PROFILE/fts-datanode/A/log4j.properties  classes/log4j.properties
	cp  -f $PROFILE/fts-datanode/A/log4j2.xml  classes/log4j2.xml 
	cp  -f $PROFILE/fts-datanode/A/cfg.xml  classes/cfg/cfg.xml
elif  [ $1 == "D" ];then
	echo "检测到百信UAT_D环境"
	cp  -f $PROFILE/fts-datanode/D/efs.properties  classes/efs.properties
	cp  -f $PROFILE/fts-datanode/D/log4j.properties  classes/log4j.properties
	cp  -f $PROFILE/fts-datanode/D/log4j2.xml  classes/log4j2.xml 
	cp  -f $PROFILE/fts-datanode/D/cfg.xml  classes/cfg/cfg.xml
elif  [ $1 == "F" ];then
	echo "检测到百信UAT_F环境"
	cp  -f $PROFILE/fts-datanode/F/efs.properties  classes/efs.properties
	cp  -f $PROFILE/fts-datanode/F/log4j.properties  classes/log4j.properties
	cp  -f $PROFILE/fts-datanode/F/log4j2.xml  classes/log4j2.xml 
	cp  -f $PROFILE/fts-datanode/F/cfg.xml  classes/cfg/cfg.xml
elif  [ $1 == "G" ];then
	echo "检测到百信UAT_G环境"
	cp  -f $PROFILE/fts-datanode/G/efs.properties  classes/efs.properties
	cp  -f $PROFILE/fts-datanode/G/log4j.properties  classes/log4j.properties
	cp  -f $PROFILE/fts-datanode/G/log4j2.xml  classes/log4j2.xml
	cp  -f $PROFILE/fts-datanode/G/cfg.xml  classes/cfg/cfg.xml
elif  [ $1 == "H" ];then
	echo "检测到百信UAT_H环境"
	cp  -f $PROFILE/fts-datanode/H/efs.properties  classes/efs.properties
	cp  -f $PROFILE/fts-datanode/H/log4j.properties  classes/log4j.properties
	cp  -f $PROFILE/fts-datanode/H/log4j2.xml  classes/log4j2.xml
	cp  -f $PROFILE/fts-datanode/H/cfg.xml  classes/cfg/cfg.xml
elif  [ $1 == "p" ];then
	echo "检测到百信生产环境"
	cp  -f $PROFILE/fts-datanode/P/efs.properties  classes/efs.properties
	cp  -f $PROFILE/fts-datanode/P/log4j.properties  classes/log4j.properties
	cp  -f $PROFILE/fts-datanode/P/log4j2.xml  classes/log4j2.xml 
	cp  -f $PROFILE/fts-datanode/P/cfg.xml  classes/cfg/cfg.xml
fi

echo completed  update properties file ins fts-datanode

tar -cf fts-datanode.tar *
mkdir -p fts-datanode/fts-datanode
cp fts-datanode.tar fts-datanode-init.sh order.txt fts-datanode/fts-datanode/
tar -cf fts-datanode.tar fts-datanode
mv fts-datanode.tar ./../../../
rm -rf fts-datanode

echo packaging ended