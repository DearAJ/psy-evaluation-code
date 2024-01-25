package org.ww.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.TelCdrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ww.dto.StatisticDto;
import org.ww.dto.TelCdrDto;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zq
 * @since 2023-05-07
 */
@Mapper
public interface TelCdrInfoMapper extends BaseMapper<TelCdrInfo> {
    List<TelCdrDto> selectCallIn(Long telGroupId, String caller, Integer isSucceed, int start, int size);
    Long selectCallInCount(Long telGroupId, String caller,  Integer isSucceed);

    List<StatisticDto> selectContinuousDateStatistics(Long telGroupId, Date startTime, int size);

    List<StatisticDto> selectDateStatistics(Long telGroupId, Date selectedDate, int  start, int size);
    Long selectDateStatisticsCount(Long telGroupId, Date selectedDate);

}
