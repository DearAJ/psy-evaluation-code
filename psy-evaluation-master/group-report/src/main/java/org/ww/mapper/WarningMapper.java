package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.MhtWarning;
import org.ww.domain.Warning;

/**
 * @author YWSnow
 * @date 2022/5/31 22:57
 * @Description: 
 */

@Mapper
public interface WarningMapper extends BaseMapper<Warning> {
}
