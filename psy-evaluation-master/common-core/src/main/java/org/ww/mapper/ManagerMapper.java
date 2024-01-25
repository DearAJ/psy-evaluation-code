package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.Manager;

/**
 * @Author 13096
 * @Date 2022/2/23 22:47
 * @Version 1.0
 */
@Mapper
public interface ManagerMapper extends BaseMapper<Manager> {
    // 修改manager，manager中可修改的只有四个值，即name，phone，email，sex
    Integer updateManager(String username, String name, String phone, String email, String sex);

    // 根据学校id更新学校名称
    Integer updateSchoolName(String school_login, String school_name);

    // 根据学校id更新学校负责人信息
    Integer updateSchoolManagerInfo(String school_login, String name, String phone);
}
