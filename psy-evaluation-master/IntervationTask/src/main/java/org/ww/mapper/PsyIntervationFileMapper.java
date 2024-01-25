package org.ww.mapper;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.EntityDO.IdBaseEntity;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.base.BaseEntity;

import java.util.List;

/**
 * @author lqk
 * @date 2022/3/10 17:39
 * @Description: 获取档案
 */
@Mapper
@TableName("psyintervationfiles")
public interface PsyIntervationFileMapper extends BaseMapper<PsyIntervationFileDO> {
    List<BaseEntity> selectByStuId(@Param("stuId") Long stuId);
    List<BaseEntity> selectLatestByStuId(@Param("stuId")Long stuId);

    List<Long> selectUnChangedStu();
}
