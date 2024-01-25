package org.ww.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.TelContact;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ww.dto.StatisticDto;

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
public interface TelContactMapper extends BaseMapper<TelContact> {
    List<StatisticDto> selectRegionStatistics(Long telGroupId);
}
