package org.ww.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ww.mapper.RoleMapper;
import org.ww.domain.Role;
import org.ww.service.RoleService;
import org.ww.vo.RoleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Override
    public List<RoleVo> getRoleByUserId(String username) {
        return roleMapper.getRoleByUserId(username);
    }
}
