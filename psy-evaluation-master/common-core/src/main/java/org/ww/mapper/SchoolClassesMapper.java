package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.SchoolClasses;

import java.util.List;
import java.util.Map;

/**
 * @Author 13096
 * @Date 2022/2/26 14:35
 * @Version 1.0
 */
@Mapper
public interface SchoolClassesMapper extends BaseMapper<SchoolClasses> {
    // 获取班级人数
    Integer getClassNumbers(Integer school_id, String entrance_year, String period, String grade, String classes, String schoolYear);
    // 修改班级人数 + 1
    Integer plusSchoolClassNumbers(Integer school_id, String entrance_year, String period, String grade, String classes, String schoolYear);
    // 修改班级人数 - 1
    Integer reduceSchoolClassNumbers(Integer school_id, String entrance_year, String period, String grade, String classes, String schoolYear);
    // 批量删除classes
    Integer deleteClassesById(Long school_id);
    // 删除班级人数为0的班
    Integer deleteClasses();
    // 根据学段年级获取班级人数列表
    List<SchoolClasses> getClassesByPG(Integer school_id, List<Map<String, String>> list, String schoolYear);

    // 根据学段年级班级获取班级人数列表
    List<SchoolClasses> getClassesByPGC(Integer school_id, List<Map<String, String>> list, String schoolYear);
}
