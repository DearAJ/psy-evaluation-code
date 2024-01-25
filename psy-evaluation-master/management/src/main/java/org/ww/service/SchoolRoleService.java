package org.ww.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ww.domain.SchoolRole;
import org.ww.dto.FindSchoolRoleDto;
import org.ww.dto.SchoolRoleDto;
import org.ww.result.Wrapper;

/**
 * @Author 13096
 * @Date 2022/6/5 16:20
 * @Version 1.0
 */
public interface SchoolRoleService extends IService<SchoolRole> {
    /**
     * 新增角色
     * @param schoolRoleDto 角色对象
     * @return 返回是否成功
     */
    Wrapper<String> addSchoolRole(SchoolRoleDto schoolRoleDto);

    /**
     * 根据姓名、角色、手机号筛选教师
     * @param findSchoolRoleDto findSchoolRoleDto
     * @return 返回角色列表
     */
    Wrapper<Object> schoolRoleList(FindSchoolRoleDto findSchoolRoleDto);

    /**
     * 修改角色
     * @param schoolRole 角色对象
     * @return 返回是否成功
     */
    Wrapper<String> updateSchoolRole(SchoolRole schoolRole);

    /**
     * 删除角色
     * @param jsonObject id或id数组
     * @return 返回删除记录条数
     */
    Wrapper<Integer> deleteSchoolRole(JSONObject jsonObject);
}
