package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ww.domain.Role;
import org.ww.vo.RoleVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    List<RoleVo> getRoleByUserId(String username);

    // 根据用户名批量删除角色
    Integer deleteRoleByUsernames(List<String> usernames);
}
