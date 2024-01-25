package org.ww.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.domain.Scale;

/**
 * @author YWSnow
 * @date 2022/3/2 16:58
 * @Description:
 */

@Mapper
public interface ScaleMapper extends BaseMapper<Scale> {
    List<Map<String, Object>> getScale(Integer id);
    List<Map<String, Object>> getOptions(Integer id);
    List<HashMap<String, String>> getAllTaskResultsByTaskIds(@Param("taskIds") List<String> taskIds, @Param("schoolIds") List<String> schoolIds,
                                                             @Param("periods") List<String> periods,
                                                             @Param("grades") List<String> grades,
                                                             @Param("classes") List<String> classes);
    List<Map<String, Object>> getAllTaskProgressBySchoolAndTaskIds(@Param("taskIds") List<String> taskIds, @Param("schoolIds") List<String> schoolIds, @Param("schoolYear") String schoolYear);

    IPage personalReportList(@Param("page") Page page, @Param("schoolIds") List schoolIds, @Param("role") String role, @Param("name") String name, @Param("period") String period, @Param("grade") String grade,
                         @Param("classes") String classes, @Param("sex") String sex, @Param("idNumber") String idNumber, @Param("phoneNumber") String phoneNumber,@Param("studentId") String studentId,
                             @Param("archived") Integer archived);

    IPage getExecutionTaskUserInfo(@Param("page") Page page, @Param("schoolIds") List schoolIds, @Param("taskIds") List taskIds, @Param("period") List<String> period, @Param("grade") List<String> grade,
                                   @Param("classes") List<String> classes, @Param("name") String name, @Param("studentId") String studentId, @Param("scaleName") String scaleName, @Param("warning")  Integer warning, @Param("valid") Integer valid, @Param("semester") String semester);

    IPage getSchoolTaskCompletion(@Param("page") Page page, @Param("schoolIds") List schoolIds, @Param("taskIds") List taskIds, @Param("period") List<String> period, @Param("grade") List<String> grade,
                                  @Param("classes") List<String> classes,
                                  @Param("schoolYear") String schoolYear,
                                  @Param("name") String name, @Param("studentId") String studentId, @Param("completion") String completion, @Param("semester") String semester);


}
