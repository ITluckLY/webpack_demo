package com.dc.smarteam.modules.sys.service;

import com.dc.smarteam.common.persistence.Page;
import com.dc.smarteam.common.security.shiro.session.SessionDAO;
import com.dc.smarteam.modules.sys.entity.Menu;
import com.dc.smarteam.modules.sys.entity.Role;
import com.dc.smarteam.modules.sys.entity.User;
import org.apache.shiro.session.Session;
import java.util.Collection;
import java.util.List;

public interface SystemService {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    String entryptPassword(String plainPassword);

    boolean validatePassword(String plainPassword, String password);

    boolean printKeyLoadMessage();
    
     SessionDAO getSessionDao();
    
     User getUser(String id);
    
     User getUserByLoginName(String loginName);

     Page<User> findUser(Page<User> page, User user);

     List<User> findList(User user);
    
     List<User> findUser(User user);
    
     List<User> findUserByOfficeId(String officeId);

     void saveUser(User user);
    
     void updateUserInfo(User user);
    
     void deleteUser(User user);
    
     void updatePasswordById(String id, String loginName, String newPassword);

     void updateUserLoginInfo(User user);

     Collection<Session> getActiveSessions();
    
     Role getRole(String id);

     Role getRoleByName(String name);

     Role getRoleByEnname(String enname);

     List<Role> findRole(Role role);

     List<Role> findAllRole();
    
     void saveRole(Role role);
    
     void deleteRole(Role role);

     Boolean outUserInRole(Role role, User user);

     User assignUserToRole(Role role, User user);

     Menu getMenu(String id);

     List<Menu> findAllMenu();
    
     void saveMenu(Menu menu);
    
     void updateMenuSort(Menu menu);

     void deleteMenu(Menu menu);
}
