package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.EntityDO.IntervationStatisticDO;
import org.ww.EntityDO.PsyIntervationFileDO;

import java.util.List;

@Mapper
public interface IntervationStatisticMapper  extends BaseMapper<IntervationStatisticDO> {

    public List<IntervationStatisticDO> selectStatisticRes(@Param("period")String period,
                                                           @Param("grade")String grade,
                                                           @Param("classes")String classes);
}
