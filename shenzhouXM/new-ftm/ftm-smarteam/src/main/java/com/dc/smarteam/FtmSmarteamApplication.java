package com.dc.smarteam;

import com.dc.smarteam.common.utils.CacheUtils;
import com.dc.smarteam.modules.sys.entity.Office;
import com.dc.smarteam.modules.sys.entity.Role;
import com.dc.smarteam.modules.sys.entity.User;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@MapperScan(basePackages="com.dc.smarteam")
@CrossOrigin
@EnableCaching
public class FtmSmarteamApplication{

	public static void main(String[] args) {
		SpringApplication.run(FtmSmarteamApplication.class, args);
		System.err.println("启动成功了 ！！！！！！！！！！！！");
	}

}
