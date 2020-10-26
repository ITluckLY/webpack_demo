package com.dc.smarteam.modules.sys.service.impl;

import com.dc.smarteam.common.config.Global;
import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.security.Digests;
import com.dc.smarteam.common.security.shiro.session.SessionDAO;
import com.dc.smarteam.common.service.BaseService;
import com.dc.smarteam.common.service.ServiceException;
import com.dc.smarteam.common.utils.CacheUtils;
import com.dc.smarteam.common.utils.Encodes;
import com.dc.smarteam.common.utils.StringUtils;
import com.dc.smarteam.common.web.Servlets;
import com.dc.smarteam.modules.sys.dao.MenuMapper;
import com.dc.smarteam.modules.sys.dao.RoleMapper;
import com.dc.smarteam.modules.sys.dao.UserMapper;
import com.dc.smarteam.modules.sys.entity.Menu;
import com.dc.smarteam.modules.sys.entity.Office;
import com.dc.smarteam.modules.sys.entity.Role;
import com.dc.smarteam.modules.sys.entity.User;
import com.dc.smarteam.modules.sys.security.SystemAuthorizingRealm;
import com.dc.smarteam.modules.sys.service.SystemService;
import com.dc.smarteam.modules.sys.utils.LogUtils;
import com.dc.smarteam.modules.sys.utils.UserUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;


/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 *
 */
@Service("SystemServiceImpl")
@Transactional
public class SystemServiceImpl extends BaseService implements SystemService, InitializingBean {
    /**
     * 是需要同步Activiti数据，如果从未同步过，则同步数据。
     */
    //private static boolean isSynActivitiIndetity = true
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private SessionDAO sessionDao;
    @Autowired
    private SystemAuthorizingRealm systemRealm;

    //-- User Service --//

    /**
     * 生成安全的密码，生成随机的16位salt并经过1024次 sha-1 hash
     */
    public String entryptPassword(String plainPassword) {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
        return Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword);
    }

    /**
     * 验证密码
     *
     * @param plainPassword 明文密码
     * @param password      密文密码
     * @return 验证成功返回true
     */
    public boolean validatePassword(String plainPassword, String password) {
        byte[] salt = Encodes.decodeHex(password.substring(0, 16));
        byte[] hashPassword = Digests.sha1(plainPassword.getBytes(), salt, HASH_INTERATIONS);
        return password.equals(Encodes.encodeHex(salt) + Encodes.encodeHex(hashPassword));
    }

    /**
     * 获取Key加载信息
     */
    public boolean printKeyLoadMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("\r\n======================================================================\r\n");
        sb.append("\r\n    欢迎使用 " + Global.getConfig("productName") + "  - Powered By http://jeesite.com\r\n");
        sb.append("\r\n======================================================================\r\n");
        System.out.println(sb.toString());
        return true;
    }

    public SessionDAO getSessionDao() {
        return sessionDao;
    }

    /**
     * 获取用户
     *
     * @param id
     * @return
     */
    public User getUser(String id) {
        return UserUtils.get(id);
    }

    /**
     * 根据登录名获取用户
     *
     * @param loginName
     * @return
     */
    public User getUserByLoginName(String loginName) {
        return UserUtils.getByLoginName(loginName);
    }

    public Page<User> findUser(Page<User> page, User user) {
        // 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
        user.getSqlMap().put("dsf", dataScopeFilter(user.getCurrentUser(), "o", "a"));
        System.out.println("------>>>>");
        System.out.println(dataScopeFilter(user.getCurrentUser(), "o", "a"));
        // 设置分页参数
        user.setPage(page);
        // 执行分页查询
        page.setList(userMapper.findList(user));
        return page;
    }

    /**
     * 获取所有的用户
     */
    public List<User> findList(User user) {
        return userMapper.findList(user);
    }

    /**
     * 无分页查询人员列表
     *
     * @param user
     * @return
     */
    public List<User> findUser(User user) {
        // 生成数据权限过滤条件（dsf为dataScopeFilter的简写，在xml中使用 ${sqlMap.dsf}调用权限SQL）
        user.getSqlMap().put("dsf", dataScopeFilter(user.getCurrentUser(), "o", "a"));
        List<User> list = userMapper.findList(user);
        return list;
    }

    /**
     * 通过部门ID获取用户列表，仅返回用户id和name（树查询用户时用）
     *
     * @param
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<User> findUserByOfficeId(String officeId) {
        List<User> list = (List<User>) CacheUtils.get(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID + officeId);
        if (list == null) {
            User user = new User();
            user.setOffice(new Office(officeId));
            list = userMapper.findUserByOfficeId(user);
            CacheUtils.put(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID + officeId, list);
        }
        return list;
    }

    @Transactional(readOnly = false)
    public void saveUser(User user) {
        if (StringUtils.isBlank(user.getId())) {
            user.preInsert();
            userMapper.insert(user);
        } else {
            // 清除原用户机构用户缓存
            User oldUser = userMapper.get(user.getId());
            if (oldUser != null && oldUser.getOffice() != null && oldUser.getOffice().getId() != null) {
                CacheUtils.remove(UserUtils.USER_CACHE, UserUtils.USER_CACHE_LIST_BY_OFFICE_ID + oldUser.getOffice().getId());
            }
            // 更新用户数据
            user.preUpdate();
            userMapper.update(user);
        }
        if (StringUtils.isNotBlank(user.getId())) {
            // 更新用户与角色关联
            userMapper.deleteUserRole(user);
            if (user.getRoleList() != null && user.getRoleList().size() > 0) {
                userMapper.insertUserRole(user);
            } else {
                throw new ServiceException(user.getLoginName() + "没有设置角色！");
            }
            // 将当前用户同步到Activiti
            //saveActivitiUser(user)
            // 清除用户缓存
            UserUtils.clearCache(user);
//			// 清除权限缓存
//			systemRealm.clearAllCachedAuthorizationInfo()
        }
    }

    @Transactional(readOnly = false)
    public void updateUserInfo(User user) {
        user.preUpdate();
        userMapper.updateUserInfo(user);
        // 清除用户缓存
        UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo()
    }

    @Transactional(readOnly = false)
    public void deleteUser(User user) {
        userMapper.delete(user);
        // 同步到Activiti
        //deleteActivitiUser(user)
        // 清除用户缓存
        UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo()
    }

    @Transactional(readOnly = false)
    public void updatePasswordById(String id, String loginName, String newPassword) {
        User user = new User(id);
        user.setPassword(entryptPassword(newPassword));
        userMapper.updatePasswordById(user);
        // 清除用户缓存
        user.setLoginName(loginName);
        UserUtils.clearCache(user);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo()
    }

    //-- Role Service --//

    @Transactional(readOnly = false)
    public void updateUserLoginInfo(User user) {
        // 保存上次登录信息
        user.setOldLoginIp(user.getLoginIp());
        user.setOldLoginDate(user.getLoginDate());
        // 更新本次登录信息
        user.setLoginIp(StringUtils.getRemoteAddr(Servlets.getRequest()));
        user.setLoginDate(new Date());
        userMapper.updateLoginInfo(user);
    }

    /**
     * 获得活动会话
     *
     * @return
     */
    public Collection<Session> getActiveSessions() {
        return sessionDao.getActiveSessions(false);
    }

    public Role getRole(String id) {
        return roleMapper.get(id);
    }

    public Role getRoleByName(String name) {
        Role r = new Role();
        r.setName(name);
        return roleMapper.getByName(r);
    }

    public Role getRoleByEnname(String enname) {
        Role r = new Role();
        r.setEnname(enname);
        return roleMapper.getByEnname(r);
    }

    public List<Role> findRole(Role role) {
        return roleMapper.findList(role);
    }

    public List<Role> findAllRole() {
        return UserUtils.getRoleList();
    }

    @Transactional(readOnly = false)
    public void saveRole(Role role) {
        if (StringUtils.isBlank(role.getId())) {
            role.preInsert();
            roleMapper.insert(role);
            // 同步到Activiti
            // saveActivitiGroup(role)
        } else {
            role.preUpdate();
            roleMapper.update(role);
        }
        // 更新角色与菜单关联
        roleMapper.deleteRoleMenu(role);
        if (role.getMenuList().size() > 0) {
            roleMapper.insertRoleMenu(role);
        }
        // 更新角色与部门关联
        roleMapper.deleteRoleOffice(role);
        if (role.getOfficeList().size() > 0) {
            roleMapper.insertRoleOffice(role);
        }
        // 同步到Activiti
        //saveActivitiGroup(role)
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo()
    }

    @Transactional(readOnly = false)
    public void deleteRole(Role role) {
        roleMapper.delete(role);
        // 同步到Activiti
        //deleteActivitiGroup(role)
        // 清除用户角色缓存
        UserUtils.removeCache(UserUtils.CACHE_ROLE_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo()
    }

    //-- Menu Service --//

    @Transactional(readOnly = false)
    public Boolean outUserInRole(Role role, User user) {
        List<Role> roles = user.getRoleList();
        for (Role e : roles) {
            if (e.getId().equals(role.getId())) {
                roles.remove(e);
                saveUser(user);
                return true;
            }
        }
        return false;
    }

    @Transactional(readOnly = false)
    public User assignUserToRole(Role role, User user) {
        if (user == null) {
            return null;
        }
        List<String> roleIds = user.getRoleIdList();
        if (roleIds.contains(role.getId())) {
            return null;
        }
        user.getRoleList().add(role);
        saveUser(user);
        return user;
    }

    public Menu getMenu(String id) {
        return menuMapper.get(id);
    }

    public List<Menu> findAllMenu() {
        return UserUtils.getMenuList();
    }

    @Transactional(readOnly = false)
    public void saveMenu(Menu menu) {

        // 获取父节点实体
        menu.setParent(this.getMenu(menu.getParent().getId()));

        // 获取修改前的parentIds，用于更新子节点的parentIds
        String oldParentIds = menu.getParentIds();

        // 设置新的父节点串
        menu.setParentIds(menu.getParent().getParentIds() + menu.getParent().getId() + ",");

        // 保存或更新实体
        if (StringUtils.isBlank(menu.getId())) {
            menu.preInsert();
            menuMapper.insert(menu);
        } else {
            menu.preUpdate();
            menuMapper.update(menu);
        }

        // 更新子节点 parentIds
        Menu m = new Menu();
        m.setParentIds("%," + menu.getId() + ",%");
        List<Menu> list = menuMapper.findByParentIdsLike(m);
        for (Menu e : list) {
            e.setParentIds(e.getParentIds().replace(oldParentIds, menu.getParentIds()));
            menuMapper.updateParentIds(e);
        }
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo()
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    @Transactional(readOnly = false)
    public void updateMenuSort(Menu menu) {
        menuMapper.updateSort(menu);
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo();
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    ///////////////// Synchronized to the Activiti //////////////////

    // 已废弃，同步见：ActGroupEntityServiceFactory.java、ActUserEntityServiceFactory.java

    @Transactional(readOnly = false)
    public void deleteMenu(Menu menu) {
        menuMapper.delete(menu);
        // 清除用户菜单缓存
        UserUtils.removeCache(UserUtils.CACHE_MENU_LIST);
//		// 清除权限缓存
//		systemRealm.clearAllCachedAuthorizationInfo()
        // 清除日志相关缓存
        CacheUtils.remove(LogUtils.CACHE_MENU_NAME_PATH_MAP);
    }

    public void afterPropertiesSet() throws Exception {
        if (!Global.isSynActivitiIndetity()) {
            return;
        }
    }
}
