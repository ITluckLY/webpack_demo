package com.springboot_c.config;

import io.swagger.models.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @author 鲁先生
 * @create 2020-06-06 17:52
 **/
@Configuration
@EnableSwagger2 //开启swaager
public class SwaggerConfig {
  //http://localhost:8080/swagger-ui.html

  //配置类swagger 的docket的bean 实例
  @Bean
  public Docket docket(Environment environment) {

    //获取项目中的生产环境
    Profiles profiles = Profiles.of("dev","test");
    System.out.println(profiles);
    //获取项目的环境
    //通过environment.acceptsProfiles 判断是否处在自己设定的环境中。
    // 如果enable 为false，则swagger不能浏览器中访问。
   boolean hjbl=  environment.acceptsProfiles(profiles);
    System.out.println(hjbl);
    return new Docket( DocumentationType.SWAGGER_2)
        .apiInfo(apiInfo())
        .enable(hjbl)
        .select()
          // requestHandlerSwlwctors 配置要扫描接口的方式。
          //basePackage 指定要扫描的包
          // any() 扫描全部
          // none() 不扫描
          //withClassAnnotaction 扫描类上的注解。
        .apis(RequestHandlerSelectors.basePackage("com.springboot_c.controller"))
        .build();
  }


  @Bean
  public Docket docket1(){
    return  new Docket(DocumentationType.SWAGGER_2).groupName("a");
  }

  @Bean
  public Docket docket2(){
    return  new Docket(DocumentationType.SWAGGER_2).groupName("b");
  }
  @Bean
  public Docket docket3(){
    return  new Docket(DocumentationType.SWAGGER_2).groupName("c");
  }
  //配置swagger 信息=apiinfo
  public ApiInfo apiInfo() {
    //作者信息
    Contact contact = new Contact("鲁先生", "https://www.bilibili.com", "21312");
    return new ApiInfo(
        "鲁先生的swagger Api 文档",
        "des",
        "v1.0",
        "https://www.bilibili.com/video/BV1PE411i7CV?p=48",
        contact,
        "apache 2.0",
        "https://www.bilibili.com",
        new ArrayList()
    );
  }
}
