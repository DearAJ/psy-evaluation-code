package org.ww.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ww.domain.Role;
import org.ww.vo.RoleVo;

import java.util.List;

public interface RoleService extends IService<Role> {

    List<RoleVo> getRoleByUserId(String username);
}
