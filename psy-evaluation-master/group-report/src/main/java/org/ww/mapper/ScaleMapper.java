package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.Scale;

import java.util.List;
import java.util.Map;

/**
 * @author YWSnow
 * @date 2022/3/2 16:58
 * @Description:
 */

@Mapper
public interface ScaleMapper extends BaseMapper<Scale> {
    List<Map<String, Object>> getScale(Integer id);
    List<Map<String, Object>> getOptions(Integer id);
}
