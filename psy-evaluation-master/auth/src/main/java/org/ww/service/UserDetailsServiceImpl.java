package org.ww.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.ww.constants.Constants;
import org.ww.constants.JWTConstants;
import org.ww.domain.AuthUser;
import org.ww.provider.UserProvider;
import org.ww.result.Wrapper;
import org.ww.vo.RoleVo;
import org.ww.vo.UserVo;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @DubboReference
    private UserProvider userProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Wrapper<UserVo> userInfo = userProvider.findByUsername(username);
        if (userInfo.getResult() == null) {
            throw new UsernameNotFoundException("用户:" + username + ",不存在!");
        }

        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(userInfo.getResult(), userVo);

        Wrapper<List<RoleVo>> roleInfo = userProvider.getRoleByUserId(username);
        if (roleInfo.getCode() == Constants.SUCCESS) {
            List<RoleVo> roleVoList = roleInfo.getResult();
            for (RoleVo role : roleVoList) {
                // 角色必须是ROLE_开头，可以在数据库中设置
                SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority(JWTConstants.ROLE_PREFIX + role.getLevel());
                grantedAuthorities.add(grantedAuthority);

                // 获取权限
//                Wrapper<List<MenuVo>> menuInfo = userProvider.getRolePermission(String.valueOf(role.getId()));
//                if (menuInfo.getCode() == Constants.SUCCESS) {
//                    List<MenuVo> permissionList = menuInfo.getResult();
//                    for (MenuVo menu : permissionList) {
//                        if (!StringUtils.isEmpty(menu.getUrl())) {
//                            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(menu.getUrl());
//                            grantedAuthorities.add(authority);
//                        }
//                    }
//                }
            }
        }

        AuthUser user = new AuthUser(userVo.getUsername(), userVo.getPassword(), grantedAuthorities);
        user.setId(userVo.getId());
        log.info("{} {}", user.getPassword(), user.getAuthorities());
        return user;
    }

}
