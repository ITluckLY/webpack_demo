package com.springboot_c.demo.config;

import io.swagger.models.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
    //获取项目的环境
    environment.acceptsProfiles();



    return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo());
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
