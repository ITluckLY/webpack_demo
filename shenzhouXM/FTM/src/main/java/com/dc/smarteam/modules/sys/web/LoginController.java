/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.dc.smarteam.modules.sys.web;

import com.dc.smarteam.common.config.Global;
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
import com.google.common.collect.Maps;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 登录Controller
 */
@Controller
public class LoginController extends BaseController {

    @Autowired
    private SessionDAO sessionDAO;
    @Autowired
    private DictService dictService;

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
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();

//		// 默认页签模式
//		String tabmode = CookieUtils.getCookie(request, "tabmode");//NOSONAR
//		if (tabmode == null) CookieUtils.setCookie(response, "tabmode", "1");//NOSONAR

        if (logger.isDebugEnabled()) {
            logger.debug("login, active session size: {}", sessionDAO.getActiveSessions(false).size());
        }

        // 如果已登录，再次访问主页，则退出原账号。
        if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
            CookieUtils.setCookie(response, "LOGINED", "false");
        }

        // 如果已经登录，则跳转到管理首页
        if (principal != null && !principal.isMobileLogin()) {
            return "redirect:" + adminPath;
        }
//		String view;//NOSONAR
//		view = "/WEB-INF/views/modules/sys/sysLogin.jsp";//NOSONAR
//		view = "classpath:";//NOSONAR
//		view += "jar:file:/D:/GitHub/jeesite/src/main/webapp/WEB-INF/lib/jeesite.jar!";//NOSONAR
//		view += "/"+getClass().getName().replaceAll("\\.", "/").replace(getClass().getSimpleName(), "")+"view/sysLogin";//NOSONAR
//		view += ".jsp";//NOSONAR
        CurrNameNodeHelper.setCurrSysname(request, null);
        return "modules/sys/sysLogin";
    }

    /**
     * 登录失败，真正登录的POST请求由Filter完成
     */
    @RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
    public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();

        // 如果已经登录，则跳转到管理首页
        if (principal != null) {
            return "redirect:" + adminPath;
        }

        String username = WebUtils.getCleanParam(request, FormAuthenticationFilter.DEFAULT_USERNAME_PARAM);
        boolean rememberMe = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM);
        boolean mobile = WebUtils.isTrue(request, FormAuthenticationFilter.DEFAULT_MOBILE_PARAM);
        String exception = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
        String message = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM);

        if (StringUtils.isBlank(message) || StringUtils.equals(message, "null")) {
            message = "用户或密码错误, 请重试.";
        }

        model.addAttribute(FormAuthenticationFilter.DEFAULT_USERNAME_PARAM, username);
        model.addAttribute(FormAuthenticationFilter.DEFAULT_REMEMBER_ME_PARAM, rememberMe);
        model.addAttribute(FormAuthenticationFilter.DEFAULT_MOBILE_PARAM, mobile);
        model.addAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME, exception);
        model.addAttribute(FormAuthenticationFilter.DEFAULT_MESSAGE_PARAM, message);

        if (logger.isDebugEnabled()) {
            logger.debug("login fail, active session size: {}, message: {}, exception: {}",
                    sessionDAO.getActiveSessions(false).size(), message, exception);
        }

        // 非授权异常，登录失败，验证码加1。
        if (!StringUtils.equalsIgnoreCase(exception, UnauthorizedException.class.getName())) {
            model.addAttribute("isValidateCodeLogin", isValidateCodeLogin(username, true, false));
        }

        // 验证失败清空验证码
        request.getSession().setAttribute(ValidateCodeServlet.VALIDATE_CODE, IdGen.uuid());

        // 如果是手机登录，则返回JSON字符串
        if (mobile) {
            return renderString(response, model);
        }

        return "modules/sys/sysLogin";
    }

    /**
     * 登录成功，进入管理首页
     */
    @RequiresPermissions("user")
    @RequestMapping(value = "${adminPath}")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        SystemAuthorizingRealm.Principal principal = UserUtils.getPrincipal();
        if (principal == null) return "redirect:" + adminPath + "/login";

        String remoteAddr = StringUtils.getRemoteAddr(request);
        logger.info("login success, sessionId: {}, username: {}, ipAddress: {}",
                principal.getSessionid(), principal.getLoginName(), remoteAddr);
        request.getSession().setAttribute("loginName", principal.getLoginName());

        // 登录成功后，验证码计算器清零
        isValidateCodeLogin(principal.getLoginName(), false, true);

        request.getSession().setAttribute("loginUsername", principal.getLoginName());

        if (logger.isDebugEnabled()) {
            logger.debug("show index, active session size: {}", sessionDAO.getActiveSessions(false).size());
        }
        // 如果已登录，再次访问主页，则退出原账号。
        if (Global.TRUE.equals(Global.getConfig("notAllowRefreshIndex"))) {
            String logined = CookieUtils.getCookie(request, "LOGINED");
            if (StringUtils.isBlank(logined) || "false".equals(logined)) {
                CookieUtils.setCookie(response, "LOGINED", "true");
            } else if (StringUtils.equals(logined, "true")) {
                UserUtils.getSubject().logout();
                return "redirect:" + adminPath + "/login";
            }
        }

        // 如果是手机登录，则返回JSON字符串
        if (principal.isMobileLogin()) {
            if (request.getParameter("login") != null) {
                return renderString(response, principal);
            }
            if (request.getParameter("index") != null) {
                return "modules/sys/sysIndex";
            }
            return "redirect:" + adminPath + "/login";
        }

        return "modules/sys/sysIndex";
    }

    /**
     * 获取主题方案
     */
    @RequestMapping(value = "/theme/{theme}")
    public String getThemeInCookie(@PathVariable String theme, HttpServletRequest request, HttpServletResponse response) {
        List<Dict> themeList = DictUtils.getDictList("theme");
        if (StringUtils.isNotBlank(theme) && strinList(theme, themeList)) {
            CookieUtils.setCookie(response, "theme", theme);
        } else {
            CookieUtils.getCookie(request, "theme");
        }
        return "redirect:" + request.getParameter("url");
    }

    public boolean strinList(String str, List<Dict> themeList) {
        for (Dict dict : themeList) {
            if (dict.getValue().equals(str)) {
                return true;
            }
        }
        return false;
    }
}
