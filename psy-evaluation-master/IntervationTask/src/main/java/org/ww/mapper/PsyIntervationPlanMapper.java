package org.ww.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.EntityDO.IdBaseEntity;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityVO.RecordAndPlanVO;
import org.ww.base.BaseEntity;

import java.util.List;

/**
 * @author lqk
 * @date 2022/3/10 17:39
 * @Description: 获取详细干预方案
 */
@Mapper
public interface PsyIntervationPlanMapper extends BaseMapper<PsyIntervationPlanDO> {
    public List<BaseEntity> selectAllPlan();

    int selectCount(QueryWrapper<IdBaseEntity> id);

    List<RecordAndPlanVO> selectAdvices(Long fileId);
}
