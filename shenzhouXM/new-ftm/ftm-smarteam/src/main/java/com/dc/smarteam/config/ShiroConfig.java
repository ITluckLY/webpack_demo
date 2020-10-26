package com.dc.smarteam.config;

import com.dc.smarteam.common.security.shiro.session.CacheSessionDAO;
import com.dc.smarteam.common.security.shiro.session.SessionManager;
import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.modules.sys.security.FormAuthenticationFilter;
import com.dc.smarteam.modules.sys.security.SystemAuthorizingRealm;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.cas.CasFilter;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {

    @Value("${adminPath}")
    private String adminPath;

    @Value("${ehcache.configFile}")
    private String ehcacheConfFile;

    @Value("${cookie.session.id}")
    private String cookSessId;

    @Value("${session.sessionTimeout}")
    private Long sessionTimeout;

    @Value("${session.sessionTimeoutClean}")
    private Long sessionTimeoutClean;


    public ShiroConfig(){
        System.out.println("ShiroConfig init ......");
    }


    /**
     *开启@RequirePermission注解的配置
     *
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor auththorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor auththorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        auththorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return auththorizationAttributeSourceAdvisor;
    }


    /**
     * shiro过滤器配置
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilter(){
        System.out.println("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager());
        shiroFilterFactoryBean.setLoginUrl(adminPath+"/login");
        shiroFilterFactoryBean.setSuccessUrl(adminPath+"?login");

        Map<String,Filter> filterMap = new LinkedHashMap<>();
//        filterMap.put("cas",casFilter());
        filterMap.put("authc",new FormAuthenticationFilter());
        shiroFilterFactoryBean.setFilters(filterMap);

        //拦截器
        Map<String,String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        //权限配置  (暂时注释掉)
        filterChainDefinitionMap.put("/act/rest/service/editor/**","perms[act:model:edit]");
        filterChainDefinitionMap.put("/act/rest/service/model/**","perms[act:model:edit]");
        //配置不会被拦截的链接 顺序判断 相关静态资源
        filterChainDefinitionMap.put("/**","anon");
        filterChainDefinitionMap.put("/static/**","anon");
        filterChainDefinitionMap.put("/userfiles/**","anon");
        filterChainDefinitionMap.put(adminPath+"/logout","anon");
        //所有的url都必须认证通过才可以访问
//        filterChainDefinitionMap.put(adminPath+"/cas","cas");
        filterChainDefinitionMap.put(adminPath+"/login","authc");
        filterChainDefinitionMap.put(adminPath+"/**","authc,user");
        filterChainDefinitionMap.put("/act/rest/service/**","authc,user");
        filterChainDefinitionMap.put("/ReportServer/**","authc,user");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }
    /**
     * 加密方式配置
     *
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(2);
        return  hashedCredentialsMatcher;
    }

    /**
     * 认证器配置
     *
     */
    @Bean
    public SystemAuthorizingRealm systemAuthorizingRealm(){
        SystemAuthorizingRealm systemAuthorizingRealm = new SystemAuthorizingRealm();
        return systemAuthorizingRealm;
    }
    /**
     * 安全管理配置
     *
     */
    @Bean
    public DefaultWebSecurityManager securityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(systemAuthorizingRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(shiroCacheManager());
        return securityManager;
    }

    /**
     * 自定义会话管理配置
     *
     */
    @Bean
    public SessionManager  sessionManager(){
        SessionManager sessionManager = new SessionManager();
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setGlobalSessionTimeout(sessionTimeout);  //session.sessionTimeout
        sessionManager.setSessionValidationInterval(sessionTimeoutClean); //session.sessionTimeoutClean
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionIdCookie(sessionIdCookie());
        sessionManager.setSessionIdCookieEnabled(true);
        return  sessionManager;
    }


    /**
     * 指定本系统SESSIONID, 默认为: JSESSIONID 问题: 与SERVLET容器名冲突, 如JETTY, TOMCAT 等默认JSESSIONID,
     * 当跳出SHIRO SERVLET时如ERROR-PAGE容器会为JSESSIONID重新分配值导致登录会话丢失!
     */
    @Bean
    public SimpleCookie sessionIdCookie(){
        SimpleCookie sessionIdCookie = new SimpleCookie();
        sessionIdCookie.setName(cookSessId); //${cookie.session.id}
        return sessionIdCookie;
    }


    /**
     * sessionDAO
     *
     */
    @Bean
    public CacheSessionDAO sessionDAO(){
        CacheSessionDAO sessionDAO = new CacheSessionDAO();
        sessionDAO.setSessionIdGenerator(new IdGen());
        sessionDAO.setActiveSessionsCacheName("activeSessionsCache");
        sessionDAO.setCacheManager(shiroCacheManager());
        return sessionDAO;
    }

    /**
     * 定义授权缓存管理器
     *
     */
    @Bean
    public EhCacheManager shiroCacheManager(){
        EhCacheManager shiroCacheManager = new EhCacheManager();
        shiroCacheManager.setCacheManagerConfigFile("classpath:"+ehcacheConfFile);
        return shiroCacheManager;
    }

    /**
     * 缓存配置
     *
     */
    @Bean
    public EhCacheManagerFactoryBean sfCacheManager(){
        EhCacheManagerFactoryBean cacheManager = new EhCacheManagerFactoryBean();
        cacheManager.setConfigLocation(new ClassPathResource(ehcacheConfFile));
        cacheManager.setShared(true);
        return cacheManager;
    }


    @Bean
    public CasFilter casFilter(){
        CasFilter casFilter = new CasFilter();
        casFilter.setFailureUrl(adminPath+"/login");
        return casFilter;
    }

    /**
     *保证实现了Shiro内部lifecycle函数的bean执行
     *
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor(){
        LifecycleBeanPostProcessor LifecycleBeanPostProcessor = new LifecycleBeanPostProcessor();
        return LifecycleBeanPostProcessor;
    }

    /**
     *保证实现了Shiro内部lifecycle函数的bean执行
     *
     */
    @DependsOn("lifecycleBeanPostProcessor")
    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     *
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(){
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager());
        return authorizationAttributeSourceAdvisor;
    }

    @Bean
    public FilterRegistrationBean delegatingFilterProxy(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        DelegatingFilterProxy proxy = new DelegatingFilterProxy();
        proxy.setTargetFilterLifecycle(true);
        proxy.setTargetBeanName("shiroFilter");
        filterRegistrationBean.setFilter(proxy);
        return filterRegistrationBean;
    }


}

