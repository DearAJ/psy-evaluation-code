package org.ww.provider.impl;

import org.ww.exception.BusinessException;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.provider.UserProvider;
import org.ww.service.MenuService;
import org.ww.service.RoleService;
import org.ww.service.UserService;
import org.ww.vo.MenuVo;
import org.ww.vo.RoleVo;
import org.ww.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
@DubboService
public class UserProviderImpl implements UserProvider {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Override
    public Wrapper<UserVo> findByUsername(String username) {
        try {
            UserVo userVo = userService.findByUsername(username);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, userVo);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<List<RoleVo>> getRoleByUserId(String username) {
        try {
            List<RoleVo> roleVoList = roleService.getRoleByUserId(username);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, roleVoList);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<List<MenuVo>> getRolePermission(String roleId) {
        try {
            List<MenuVo> menuVoList = menuService.getMenuByRoleId(roleId);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, menuVoList);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }
}
