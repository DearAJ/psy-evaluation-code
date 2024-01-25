package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.GroupReport;

/**
 * @Author 13096
 * @Date 2022/11/15 9:48
 * @Version 1.0
 */
@Mapper
public interface GroupReportMapper extends BaseMapper<GroupReport> {

    int insertGroupReport(GroupReport groupReport);
}
