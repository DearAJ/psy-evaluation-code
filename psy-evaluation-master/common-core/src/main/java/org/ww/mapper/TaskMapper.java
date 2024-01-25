package org.ww.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.ww.domain.Task;

/**
 * @Author 13096
 * @Date 2022/2/11 9:48
 * @Version 1.0
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    /**
     * 获取任务简要信息
     * @param task_ids 任务id列表
     * @return 返回list
     */
    List<Map<String, Object>> getTaskInfoByIds(List<Integer> task_ids);
    Task getTaskStateInfoById(@Param("taskId") Integer taskId);

    /**
     * 获取任务简要信息和下发人信息
     * @param task_id task_id
     * @return 返回list
     */
    Map<String, Object> getTaskAndIssueInfoById(Integer task_id);

    /**
     * 查询某个学校完成任务学生所在班级
     * @param taskId taskId
     * @param schoolId schoolId
     * @return 返回列表
     */
    List<Map<String, String>> getTaskStudentClassInfo(Integer taskId, Integer schoolId);

    /**
     * 更新task的参与人数
     * @param id id
     * @return int
     */
    Integer updateNumbersById(Integer id);

    /**
     * 更新task的参与人数
     * @param id id
     * @param count count
     * @return int
     */
    Integer reduceNumbersById(Integer id, Integer count);

    /**
     * 更新task的完成人数
     * @param id id
     * @param count count
     * @return int
     */
    Integer reduceComNumbersById(Integer id, Integer count);

    /**
     * 更新task的预警人数
     * @param id id
     * @param count count
     * @return int
     */
    Integer reduceWarnNumbersById(Integer id, Integer count);

    // 更新任务状态
    Integer updateTaskState(Long id, Integer state);
}
