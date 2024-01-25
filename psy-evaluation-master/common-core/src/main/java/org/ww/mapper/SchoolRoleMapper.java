package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.SchoolRole;

import java.util.List;

/**
 * @Author 13096
 * @Date 2022/6/5 16:03
 * @Version 1.0
 */
@Mapper
public interface SchoolRoleMapper extends BaseMapper<SchoolRole> {
    // 修改school_role中的name，phone，email，sex, full_part, education, major, graduated_school, certificate
    Integer updateSchoolRole(String username, String name, String phone, String email, String sex, Integer full_part, String education, String major, String graduated_school, String certificate);

    // 根据学校id更新学校名称
    Integer updateSchoolName(Long school_id, String school_name);

    // 根据学校id更新学校负责人信息
    Integer updateSchoolManagerInfo(String username, String name, String phone);

    // 根据用户名批量删除（删除其实是更新deleted为1）
    Integer deleteByUsernames(List<String> usernames);
}
