package com.dcfs.esb.ftp.servcomm;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test2XML {
  public static List getArraysXML(String xmlfile) {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    List<String[]> list = new ArrayList();
    try {
      String filestr = "fileServerDataNode/target/classes/cfg/" + xmlfile;
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document document = db.parse(filestr);
      NodeList cmdList = document.getElementsByTagName("flow");
      //System.err.println(cmdList.getLength());
      String[] DefStandardFlow;
      String[] FullCheckFlow = null;
      String[] str = null;
      String str1 = "";
      for (int i = 0; i < cmdList.getLength(); i++) {
        Node name = cmdList.item(i);
        //System.out.println("name:"+name);
        // System.out.println(name.getAttributes().getNamedItem("name"));
        String result = name.getAttributes().getNamedItem("components").toString();

        result = result.substring(6);

        result = result.substring(6, result.length() - 1);
        if (i == 0) {
          DefStandardFlow = result.split(",");
          list.add(DefStandardFlow);
        } else {
          FullCheckFlow = result.split(",");
          list.add(FullCheckFlow);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return list;
  }

  public static String[] getXML(String preprocessName) {
    List<String[]> getxml = getArraysXML("flow.xml");

    String[] ssss = null;
    if (preprocessName.equals("FilesProcesFlow")) {
      for (String[] getFlow : getxml) {
        ssss = getFlow;
      }
    }

    return ssss;
  }

  /*public static void main(String[] args) {
    String[] str1 = getXML("FilesProcesFlow");
    System.out.println(Arrays.toString(str1));

  }*/

}
