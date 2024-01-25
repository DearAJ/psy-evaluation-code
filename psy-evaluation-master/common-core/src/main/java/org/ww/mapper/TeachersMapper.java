package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.Teachers;

import java.util.Map;

/**
 * @Author 13096
 * @Date 2022/6/1 20:40
 * @Version 1.0
 */
@Mapper
public interface TeachersMapper extends BaseMapper<Teachers> {
    Map<String, Object> getTeacherInfo(String username);
}
