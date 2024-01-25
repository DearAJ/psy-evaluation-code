package org.ww.mapper.ProvicesMapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.EntityDO.IntervationStatisticDO;
import org.ww.EntityDO.ProvinceEntityDO.PRIntervationStatisticDO;

import java.util.List;

@Mapper
public interface PRIntervationStatisticMapper extends BaseMapper<PRIntervationStatisticDO> {

    public IPage<PRIntervationStatisticDO> selectStatisticRes(IPage<PRIntervationStatisticDO> page, @Param("province")String province,
                                                             @Param("city")String city,
                                                             @Param("county")String county,
                                                             @Param("schoolName")String schoolName,
                                                             @Param("schoolId")Long schoolId,
                                                             @Param("period")String period,
                                                             @Param("grade")String grade,
                                                             @Param("classes")String classes);
    public Long selectStatisticCounts(@Param("province")String province,
                                                             @Param("city")String city,
                                                             @Param("county")String county,
                                                             @Param("schoolName")String schoolName,
                                                             @Param("period")String period,
                                                             @Param("grade")String grade,
                                                             @Param("classes")String classes);
}
