/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.web;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.json.ResultDtoTool;
import com.dc.smarteam.common.security.shiro.session.SessionDAO;
import com.dc.smarteam.common.servlet.ValidateCodeServlet;
import com.dc.smarteam.common.utils.CacheUtils;
import com.dc.smarteam.common.utils.CookieUtils;
import com.dc.smarteam.common.utils.IdGen;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.BaseController;
import com.dc.smarteam.helper.CurrNameNodeHelper;
import com.dc.smarteam.modules.sys.entity.Dict;
import com.dc.smarteam.modules.sys.security.FormAuthenticationFilter;
import com.dc.smarteam.modules.sys.security.SystemAuthorizingRealm;
import com.dc.smarteam.modules.sys.service.DictService;
import com.dc.smarteam.modules.sys.utils.DictUtils;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import com.dc.smarteam.util.PublicRepResultTool;
import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录Controller
 */
@Log4j2
@RestController
public class LoginController{

    @Value("${notAllowRefreshIndex}")
    private Boolean notAllowRefreshIndex;

    @Autowired
    private SessionDAO sessionDAO;
//    @Resource(name = "DictServiceImpl")
//    private DictService dictService;

    /**
     * 是否是验证码登录
     *
     * @param useruame 用户名
     * @param isFail   计数加1
     * @param clean    计数清零
     * @return
     */
    @SuppressWarnings("unchecked")
    public static boolean isValidateCodeLogin(String useruame, boolean isFail, boolean clean) {
        Map<String, Integer> loginFailMap = (Map<String, Integer>) CacheUtils.get("loginFailMap");
        if (loginFailMap == null) {
            loginFailMap = Maps.newHashMap();
            CacheUtils.put("loginFailMap", loginFailMap);
        }
        Integer loginFailNum = loginFailMap.get(useruame);
        if (loginFailNum == null) {
            loginFailNum = 0;
        }
        if (isFail) {
            loginFailNum++;
            loginFailMap.put(useruame, loginFailNum);
        }
        if (clean) {
            loginFailMap.remove(useruame);
        }
        return loginFailNum >= 3;
    }


    /**
     * 管理登录
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET)
    public Object login(HttpServletRequest request, HttpServletResponse response) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();
        log.debug("principal:{}",principal);
        log.debug("principal is no ? {}",principal==null? true:false);
        if (log.isDebugEnabled()) {
            log.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
        }

        // 如果已登录，再次访问主页，则退出原账号。
        if (Global.TRUE.equals(notAllowRefreshIndex)) {
            log.debug("已登录，再次访问主页，退出原账号");
            CookieUtils.setCookie(response, "LOGINED", "false");
        }

        // 如果已经登录，则跳转到管理首页
        if (principal != null && !principal.isMobileLogin()) {
            log.debug("已经登录，则跳转到管理首页");
            return index(request,response);
        }

        CurrNameNodeHelper.setCurrSysname(request, null);
        log.debug("跳转到登录页面");
        return ResultDtoTool.buildSucceed("跳转到登录页面",null);
    }

    /**
     * 登录失败，真正登录的POST请求由Filter完成
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
    public Object loginFail(HttpServletRequest request, HttpServletResponse response) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();
        log.debug("login_post  principal:{}",principal);
        // 如果已经登录，则跳转到管理首页
        if (principal != null) {
//            return "redirect:" + adminPath;
            return index(request,response);
        }

        String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
        log.debug("username:{}",username);
        boolean rememberMe = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
        log.debug("rememberMe:{}",rememberMe);
        boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
        log.debug("mobile:{}",mobile);
        String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        log.debug("exception:{}",exception);
        String message = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);
        log.debug("message:{}",message);

        if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
            message = "用户或密码错误, 请重试.";
            log.debug(message);
        }

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
        resultMap.put(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
        resultMap.put(FormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
        resultMap.put(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
        resultMap.put(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);

        if (log.isDebugEnabled()) {
            log.debug("login fail, active session size: {}, message: {}, exception: {}",
                    sessionDAO.getActiveSessions(false).size(), message, exception);
        }

        // 非授权异常，登录失败，验证码加1。
        if (!StringUtils.equalsIgnoreCase(exception, UnauthorizedException.class.getName())) {
            resultMap.put("isValidateCodeLogin", isValidateCodeLogin(username, true, false));
        }

        // 验证失败清空验证码
        request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

        // 如果是手机登录，则返回JSON字符串
        if (mobile) {
            return ResultDtoTool.buildSucceed("登录成功",resultMap);
        }

        return ResultDtoTool.buildSucceed("登录成功",null);
    }

    /**
     * 登录成功，进入管理首页
     */
//    @RequiresPermissions("user")
    @RequestMapping(value = "${adminPath}")
    public Object index(HttpServletRequest request, HttpServletResponse response) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();
        if (principal == null) return login(request,response);

        String remoteAddr = StringUtils.getRemoteAddr(request);
        log.info("login success, sessionId: {}, username: {}, ipAddress: {}",
                principal.getSessionid(), principal.getLoginName(), remoteAddr);
        request.getSession().setAttribute("loginName", principal.getLoginName());

        // 登录成功后，验证码计算器清零
        isValidateCodeLogin(principal.getLoginName(), false, true);

        request.getSession().setAttribute("loginUsername", principal.getLoginName());

        if (log.isDebugEnabled()) {
            log.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
        }
        // 如果已登录，再次访问主页，则退出原账号。
        if (Global.TRUE.equals(notAllowRefreshIndex)) {
            String logined = CookieUtils.getCookie(request, "LOGINED");
            if (StringUtils.isBlank(logined) || "false".equals(logined)) {
                CookieUtils.setCookie(response, "LOGINED", "true");
            } else if (StringUtils.equals(logined, "true")) {
                UserUtils.getSubject().logout();
                return login(request,response);
            }
        }

        // 如果是手机登录，则返回JSON字符串
        if (principal.isMobileLogin()) {
            log.debug("登录方式为: 手机登录");
            if (request.getParameter("login") != null) {
                log.debug("登录成功");
                return ResultDtoTool.buildSucceed("登录成功",principal);
            }
            if (request.getParameter("index") != null) {
                log.debug("手机登录成功");
                return ResultDtoTool.buildSucceed("手机登录成功",null);
            }
            return login(request,response);
        }
        log.debug("index 登录成功");
        return  ResultDtoTool.buildSucceed("登录成功",null);
    }

    /**
     * 获取主题方案
     */
    @RequestMapping(value = "/theme/{theme}")
    public Object getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response) {
        List<Dict> themeList = DictUtils.getDictList("theme");
        if (StringUtils.isNotBlank(theme) && strinList(theme, themeList)) {
            CookieUtils.setCookie(response, "theme", theme);
        } else {
            CookieUtils.getCookie(request, "theme");
        }
        return ResultDtoTool.buildSucceed("成功",null);
    }

    public boolean strinList(String str, List<Dict> themeList) {
        for (Dict dict : themeList) {
            if (dict.getValue().equals(str)) {
                return true;            }
        }
        return false;
    }
}
