package com.springboot_c.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 鲁先生
 * @create 2020-06-06 17:40
 **/
@RestController
public class HelloController {

  @RequestMapping("/hello")
  public String   hello(){
    return "hello";
  }

  @RequestMapping("/login")
  public String login(){

    return null;
  }



}
