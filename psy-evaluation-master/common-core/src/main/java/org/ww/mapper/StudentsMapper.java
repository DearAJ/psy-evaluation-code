package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.Students;

import java.util.Map;

/**
 * @Author 13096
 * @Date 2022/2/26 20:57
 * @Version 1.0
 */
@Mapper
public interface StudentsMapper extends BaseMapper<Students> {
    Map<String, Object> getStudentInfo(String username);

    Integer updateStudentGraduated(Long id);

    Integer updateStudentGrade(Long id, String grade);
}
