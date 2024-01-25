package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.MhtWarning;

import java.util.List;
import java.util.Map;

/**
 * @author YWSnow
 * @date 2022/3/10 17:39
 * @Description:
 */
@Mapper
public interface MhtWarningMapper extends BaseMapper<MhtWarning> {

   List<Map<String, Object>> getTaskSchoolInfo(Integer taskId, List schoolIds,String county);

    List<Map<String, Object>> getStudentGradeWarningInfo(List periodList,List greadeIn,List classIn,Integer userId, Integer taskId, List schoolIds);


    List<Map<String, Object>> getStudentGradeInfo(List periodList,List greadeIn,List classIn, Integer taskId, List schoolIds,String county);

    List<Map<String, Object>> getTaskByCounty(String county);

}
