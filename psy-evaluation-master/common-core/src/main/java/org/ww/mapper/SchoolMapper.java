package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.School;

import java.util.List;

/**
 * @Author 13096
 * @Date 2022/2/11 9:48
 * @Version 1.0
 */
@Mapper
public interface SchoolMapper extends BaseMapper<School> {
    // 根据登录名修改负责老师名字，由于manager名字修改关联的
    Integer updateSchoolPerson(String username, String charge_person);

    // 修改学校学生人数和学生账户数 + 1
    Integer plusStudentNumbers(Integer school_id);

    // 修改学校学生人数和学生账户数 + num
    Integer plusBatchStudentNumbers(Integer school_id, Integer num);

    // 修改学校学生人数和学生账户数 - num
    Integer reduceBatchStudentNumbers(Integer school_id, Integer num);

    // 获取区县列表
    List<String> getAllCounty(String province, String city);

    // 获取市列表
    List<String> getAllCity(String province);

    // 获取最新的学校账号
    School getLatestSchool(String province, String city, String county);
}
