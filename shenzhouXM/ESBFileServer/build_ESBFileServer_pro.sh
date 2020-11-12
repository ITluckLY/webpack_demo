echo packaging started
mvn clean install -DskipTests=true -Pjarwar,pro
echo packaging completed
cd jarwar/fts-datanode
tar -cvf fts-datanode.tar *
mkdir -p fts-datanode/fts-datanode
cp fts-datanode.tar fts-datanode-init.sh order.txt fts-datanode/fts-datanode/
tar -cvf fts-datanode.tar fts-datanode
cp fts-datanode.tar ./../../../

echo packaging ended