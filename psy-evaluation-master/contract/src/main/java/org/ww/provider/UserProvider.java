package org.ww.provider;

import org.ww.result.Wrapper;
import org.ww.vo.MenuVo;
import org.ww.vo.RoleVo;
import org.ww.vo.UserVo;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface UserProvider {

    Wrapper<UserVo> findByUsername(@PathVariable("username") String username);

    Wrapper<List<RoleVo>> getRoleByUserId(@PathVariable("username") String username);

    Wrapper<List<MenuVo>> getRolePermission(@PathVariable("roleId") String roleId);
}
