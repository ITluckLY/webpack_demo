package com.dcfs.esb.ftp.key;

public class Key {
  private String user;
  private String type;
  private String content;


  public Key(){

  }
  public Key(String user, String type, String content) {
    this.user = user;
    this.type = type;
    this.content = content;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}

