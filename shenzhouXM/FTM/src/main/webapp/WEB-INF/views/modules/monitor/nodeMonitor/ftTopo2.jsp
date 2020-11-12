<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<!DOCTYPE>
<html>
<head>
    <meta charset="UTF-8">
    <%--<script src="http://d3js.org/d3.v3.min.js" charset="UTF-8"></script>--%>
    <script src="${ctxStatic}/d3/d3/d3.min.js" charset="UTF-8"></script>
    <script src="${ctxStatic}/d3/d3/d3.v3.min.js" charset="UTF-8"></script>
    <script src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
    <script src="${ctxStatic}/highstock/Highstock-4.2.5/js/highstock.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/highcharts.js"></script>
    <script src="${ctxStatic}/highcharts/Highcharts-4.2.5/js/modules/exporting.js"></script>

    <style type="text/css">
        .link { stroke: blue;
            stroke-linejoin:bevel;
            stroke-width:2px
        }

        .link_error{
            stroke:grey;
            stroke-linejoin:bevel;
            stroke-width:2px
        }

        /*.nodetext {*/

            /*font: 12px;*/
            /*-webkit-user-select:none;*/
            /*-moze-user-select:none;*/
            /*stroke-linejoin:bevel;*/

        /*}*/

        #container{
            width:95%;
            height:200%;
            position:relative;
            margin:20px;
        }

    </style>
</head>
<body>
<%@include file="/WEB-INF/views/modules/monitor/nodeMonitor/ftNodeInfo.jsp"%>
<%@include file="/WEB-INF/views/modules/monitor/nodeMonitor/ftNodeTransfor.jsp"%>
<div id='container'></div>
<%@include file="/WEB-INF/views/modules/monitor/nodeMonitor/ftNodeDetail.jsp"%>
<script type="text/javascript">
    d3.json("${ctx}/monitor/FtNodeMonitor/allnodeInfo",function(error,data)
    {
        var valid = document.getElementsByName('valid');
        var invalid = document.getElementsByName('invalid');
        var total = document.getElementsByName('total');

        var nodes=[];
        var validnum=0;
        var invalidnum=0;
        var totalnum=0;
        nodes[0]= {id:'',type:'router',state:1,expand:true};
                var links=[];
                data.forEach(function(d) {
                    nodes.push({id:d.node,type:'switch',state:parseInt(d.state),system:d.system,expand:false});
                    links.push({source:'',target:d.node});
                    totalnum++;
                    if(d.state==1){
                        validnum++;
                    }else{
                        invalidnum++;
                    }
                })
        valid[0].innerHTML=validnum;
        invalid[0].innerHTML=invalidnum;
        total[0].innerHTML=totalnum;
        function Topology(ele){
            typeof(ele)=='string' && (ele=document.getElementById(ele));
            var w=ele.clientWidth,
                    h=ele.clientHeight,
                    self=this;
            this.force = d3.layout.force().gravity(.1).distance(300).charge(-2000).linkStrength(0.5).size([w, h]);
            this.nodes=this.force.nodes();
            this.links=this.force.links();
            this.clickFn=function(){};
            this.vis = d3.select(ele).append("svg:svg")
                    .attr("width", w).attr("height", h).attr("pointer-events", "all");

            this.force.on("tick", function(x) {
                self.vis.selectAll("g.node")
                        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
                self.vis.selectAll("g.centernode")
                        .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
                self.vis.selectAll("line.link")
                        .attr("x1", function(d) { return d.source.x; })
                        .attr("y1", function(d) { return d.source.y; })
                        .attr("x2", function(d) { return d.target.x; })
                        .attr("y2", function(d) { return d.target.y; });
            });
        }


        Topology.prototype.doZoom=function(){
            d3.select(this).select('g').attr("transform","translate(" + d3.event.translate + ")"+ " scale(" + d3.event.scale + ")");

        }


        //增加节点
        Topology.prototype.addNode=function(node){
            this.nodes.push(node);
        }

        Topology.prototype.addNodes=function(nodes){
            if (Object.prototype.toString.call(nodes)=='[object Array]' ){
                var self=this;
                nodes.forEach(function(node){
                    self.addNode(node);
                });

            }
        }

        //增加连线
        Topology.prototype.addLink=function(source,target){
            this.links.push({source:this.findNode(source),target:this.findNode(target)});
        }

        //增加多个连线
        Topology.prototype.addLinks=function(links){
            if (Object.prototype.toString.call(links)=='[object Array]' ){
                var self=this;
                links.forEach(function(link){
                    self.addLink(link['source'],link['target']);
                });
            }
        }


        //删除节点
        Topology.prototype.removeNode=function(id){
            var i=0,
                    n=this.findNode(id),
                    links=this.links;
            while ( i < links.length){
                links[i]['source']==n || links[i]['target'] ==n ? links.splice(i,1) : ++i;
            }
            this.nodes.splice(this.findNodeIndex(id),1);
        }

        //删除节点下的子节点，同时清除link信息
        Topology.prototype.removeChildNodes=function(id){
            var node=this.findNode(id),
                    nodes=this.nodes;
            links=this.links,
                    self=this;

            var linksToDelete=[],
                    childNodes=[];

            links.forEach(function(link,index){
                link['source']==node
                && linksToDelete.push(index)
                && childNodes.push(link['target']);
            });

            linksToDelete.reverse().forEach(function(index){
                links.splice(index,1);
            });

            var remove=function(node){
                var length=links.length;
                for(var i=length-1;i>=0;i--){
                    if (links[i]['source'] == node ){
                        var target=links[i]['target'];
                        links.splice(i,1);
                        nodes.splice(self.findNodeIndex(node.id),1);
                        remove(target);

                    }
                }
            }

            childNodes.forEach(function(node){
                remove(node);
            });

            //清除没有连线的节点
            for(var i=nodes.length-1;i>=0;i--){
                var haveFoundNode=false;
                for(var j=0,l=links.length;j<l;j++){
                    ( links[j]['source']==nodes[i] || links[j]['target']==nodes[i] ) && (haveFoundNode=true)
                }
                !haveFoundNode && nodes.splice(i,1);
            }
        }



        //查找节点
        Topology.prototype.findNode=function(id){
            var nodes=this.nodes;
            for (var i in nodes){
                if (nodes[i]['id']==id ) return nodes[i];
            }
            return null;
        }


        //查找节点所在索引号
        Topology.prototype.findNodeIndex=function(id){
            var nodes=this.nodes;
            for (var i in nodes){
                if (nodes[i]['id']==id ) return i;
            }
            return -1;
        }

        //节点点击事件
        Topology.prototype.setNodeClickFn=function(callback){
            this.clickFn=callback;
        }

        //更新拓扑图状态信息
        Topology.prototype.update=function(){
            var link = this.vis.selectAll("line.link")
                    .data(this.links, function(d) { return d.source.id + "-" + d.target.id; })
                    .attr("class", function(d){
                        return d['source']['state'] && d['target']['state'] ? 'link' :'link link_error';
                    });

            link.enter().insert("svg:line", "g.node")
                    .attr("class", function(d){
                        return d['source']['state'] && d['target']['state'] ? 'link' :'link link_error';
                    });

            link.exit().remove();

            var node = this.vis.selectAll("g.node")
                    .data(this.nodes, function(d) { return  d.id;});

            var nodeEnter = node.enter().append("svg:g")
                    .attr("class",function(d){
                        return d.expand ? "centernode":"node"
                    })
//            var nodeEnter = node.enter().append("svg:g")
//                    .attr("class","node");
            var router=this.vis.selectAll(".centernode")
                    .call(this.force.drag);;
            var outnode=this.vis.selectAll(".node")
                    .call(this.force.drag);

            //增加图片，可以根据需要来修改
            var self=this;

//            router.append("circle")
//                    .attr("class", "circle")
//                    .attr("r","60px")
//                    .style("fill", "green")
//                    .style("stroke", "blue")
//                    .style("stroke-width","5px");
            router.append("svg:image")
                    .attr("class", "image")
                    .attr("xlink:href","${ctxStatic}/images/router.png")
                    .attr("x", "-75px")
                    .attr("y", "-50px")
                    .attr("width", "150px")
                    .attr("height", "100px")

            outnode.append("svg:image")
                    .attr("class", "image")
                    .attr("xlink:href", function(d){
                        //根据类型来使用图片
                        return d.state ? "${ctxStatic}/images/server4.jpg" : "${ctxStatic}/images/server_off.jpg";
                    })
                    .attr("x", "-24px")
                    .attr("y", "-24px")
                    .attr("width", "48px")
                    .attr("height", "48px")
                    .on('click',function(d){
                        nodetrasfor(d.id,d.system);nodeinfo(d.id); d.expand && self.clickFn(d);
                    })

            outnode.append("svg:text")
                    .attr("class", "nodetext")
                    .attr("dx", -10)
                    .attr("dy", 35)
                    .text(function(d) { return d.id });
            outnode.append("svg:text")
                    .attr("class", "systemtext")
                    .attr("dx", -8)
                    .attr("dy", -30)
                    .text(function(d) { return d.system });

            node.exit().remove();

            this.force.start();
        }




        var topology=new Topology('container');



        topology.addNodes(nodes);
        topology.addLinks(links);
        //可展开节点的点击事件
        topology.setNodeClickFn(function(node){
            if(!node['_expanded']){
                expandNode(node.id);
                node['_expanded']=true;
            }else{
                collapseNode(node.id);
                node['_expanded']=false;
            }
        });
        topology.update();


        function expandNode(id){
            topology.addNodes(childNodes);
            topology.addLinks(childLinks);
            topology.update();
        }

        function collapseNode(id){
            topology.removeChildNodes(id);
            topology.update();
        }

    })

</script>

</body>
</html>