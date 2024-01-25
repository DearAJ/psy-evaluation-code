package org.ww.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.TaskSchool;

/**
 * @Author 13096
 * @Date 2022/2/19 11:26
 * @Version 1.0
 */
@Mapper
public interface TaskSchoolMapper extends BaseMapper<TaskSchool> {
    Map<String, String> selectByTaskIdAndSchoolId(Integer taskId, Integer schoolId);
    /**
     * 更新学校完成人数
     * @param taskId taskId
     * @param schoolId schoolId
     * @return 1/0
     */
    Integer updateComNumbers(Integer taskId, Integer schoolId);

    /**
     * 更新学校预警人数
     * @param taskId taskId
     * @param schoolId schoolId
     * @return 1/0
     */
    Integer updateWarningNumbers(Integer taskId, Integer schoolId);

    /**
     * 更新学校有效人数
     * @param taskId taskId
     * @param schoolId schoolId
     * @return 1/0
     */
    Integer updateValidNumbers(Integer taskId, Integer schoolId);
    /**
     * 获取市级最新任务的各区县记录
     * @param province province
     * @param city city
     * @return 返回各区县任务信息
     */
    List<Map<String, Object>> getLatestCityTask(String province, String city);

    /**
     * 更新任务的某个学校的参与人数
     * @param id id
     * @return int
     */
    Integer updateNumbersById(Long id);

    /**
     * 更新任务的某个学校的参与人数
     * @param id id
     * @return int
     */
    Integer reduceNumbersById(Long id);

    /**
     * 更新任务的某个学校的完成人数
     * @param taskId taskId
     * @param schoolId schoolId
     * @param count count
     * @return int
     */
    Integer reduceComNumbersById(Integer taskId, Integer schoolId, Integer count);

    /**
     * 更新任务的某个学校的预警数
     * @param taskId taskId
     * @param schoolId schoolId
     * @param count count
     * @return int
     */
    Integer reduceWarnNumbersById(Integer taskId, Integer schoolId, Integer count);

    /**
     * 获取任务进度相关信息
     * @param province 省
     * @param city 市（可选）
     * @param county 区（可选）
     * @param school 学校（可选）
     * @param taskId 任务id
     * @param schoolId 学校id（可选）
     * @return 返回列表
     */
    List<Map<String, Object>> getTaskProcessInfo(String province, String city, String county, String school, Integer taskId, Integer schoolId);
}
