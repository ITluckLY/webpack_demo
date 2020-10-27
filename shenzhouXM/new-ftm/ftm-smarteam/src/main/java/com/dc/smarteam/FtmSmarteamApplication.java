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

	/*public static final String USER_CACHE = "userCache";
	public static final String USER_CACHE_ID = "id_";
	public static final String USER_CACHE_LOGIN_NAME = "ln";
	public static final String USER_CACHE_LIST_BY_OFFICE_ID = "oid_";
	public static final String CACHE_OFFICE_LIST = "officeList";
	public static final String CACHE_OFFICE_ALL_LIST = "officeAllList";*/

	public static void main(String[] args) {
		SpringApplication.run(FtmSmarteamApplication.class, args);
		System.err.println("启动成功了 ！！！！！！！！！！！！");

//		initMemory();
//		System.out.println("initMemory end .......");
	}

	/*public static void initMemory(){

        User user = new User();
        user.setLoginName("admin");
        user.setPassword("986b9d8a0d9137b478d508bf0e54cfe8cbf37cba52f564b3240af41a");
        user.setNo("0001");
        user.setName("系统管理员");
        user.setEmail("thinkgem@163.com");
        user.setPhone("01011111111");
        user.setMobile("13511111111");
        user.setLoginIp("0:0:0:0:0:0:0:1");
        user.setLoginFlag("1");
        user.setPhoto("/smarteam/userfiles/1/images/photo/2017/11/Chrysanthemum.jpg");
        user.setRemarks("最高管理员");

		Role role = new Role();
		role.setName("超级管理员");
		role.setEnname("admin");
		role.setRoleType("assignment");
		role.setDataScope("1");
		role.setSysData("1");
		role.setUseable("1");
		role.setDelFlag("0");
		role.setId("1");

		Office office = new Office();
//		office.setArea("广东省");
        office.setType("2");
        office.setParentIds("0,1,");
        office.setName("公司领导");
        office.setDelFlag("0");
        office.setId("2");
        office.setIsNewRecord(false);

		Office company = new Office();
		company.setType("2");
		company.setParentIds("0,");
		company.setName("XX银行");
		company.setType("2");
		company.setDelFlag("0");
		company.setId("1");
		company.setIsNewRecord(false);

		user.setRole(role);
		user.setCurrentUser(user);
		user.setOffice(office);
		user.setCompany(company);

		CacheUtils.put(USER_CACHE, USER_CACHE_ID + user.getId(), user);
		CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME + user.getLoginName(), user);
	}*/

}
