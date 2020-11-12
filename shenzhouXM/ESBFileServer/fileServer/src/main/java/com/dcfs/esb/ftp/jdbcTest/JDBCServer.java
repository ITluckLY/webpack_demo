package com.dcfs.esb.ftp.jdbcTest;

import java.sql.*;

public class JDBCServer {
  public static void main(String[] args) throws Exception {
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    try {
      Class.forName("com.mysql.jdbc.Driver");
      //connection =DriverManager.getConnection("jdbc:mysql://localhost:3306/ftm?characterEncoding=utf-8","root", "root");

      connection =DriverManager.getConnection("jdbc:mysql://www.saan.vip:3306/ftm?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull", "root", "root");
      String sql ="select * from biz_cfg_file a where a.filename = ?";
      preparedStatement =connection.prepareStatement(sql);
      preparedStatement.setString(1,"flow.xml");
      resultSet = preparedStatement.executeQuery();
      int id=0;
      String name ="";
      while(resultSet.next()){
         name= resultSet.getString("filename");
      }
      System.out.println(id+":"+name);
    }catch (Exception e){
        e.printStackTrace();
    }finally {
      if(resultSet != null){
        try {
          resultSet.close();
        } catch (SQLException e) {
          e.printStackTrace();
        } finally{
          resultSet = null;
        }
      }
      if(preparedStatement != null){
        try {
          preparedStatement.close();
        } catch (SQLException e) {
          e.printStackTrace();
        } finally{
          preparedStatement = null;
        }
      }
      if(connection != null){
        try {
          connection.close();
        } catch (SQLException e) {
          e.printStackTrace();
        } finally{
          connection = null;
        }
      }
    }
  }
}