package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationStuDO;
import org.ww.base.BaseEntity;

import java.util.List;
import java.util.Map;

/**
 * @author lqk
 * @date 2022/3/10 17:39
 * @Description: 获取档案
 */
@Mapper

public interface PsyIntervationStuMapper extends BaseMapper<PsyIntervationStuDO> {
    BaseEntity selectByStuId(@Param("stuId") Long stuId);


}

