package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.TaskUser;

import java.util.List;
import java.util.Map;

/**
 * @Author 13096
 * @Date 2022/2/26 13:26
 * @Version 1.0
 */
@Mapper
public interface TaskUserMapper extends BaseMapper<TaskUser> {
    /**
     * 根据user_id获取学生信息
     * @param id user_id
     * @return Map 学生信息
     */
    Map<String, Object> getStudentInfoById(Integer id, String schoolYear);

    /**
     * 获取某个任务的预警人数
     * @param task_id task_id
     * @return 返回
     */
    Integer getTaskWarnCount(Integer task_id);
    /**
     * 获取班级中所有学生
     * @param period 学段
     * @param grade 年级
     * @param classes 班级
     * @return 学生列表
     */
    List<Map<String, Object>> getStudentInfoForTask(String period, String grade, String classes, Integer schoolId);

    /**
     * 获取当前用户最新任务提交数据
     * @param id user_id
     * @return 用户任务提交数据
     */
    TaskUser getLatestSubmit(Integer id, Integer task_id);

    /**
     * 获取有任务记录的user_id
     * @param taskId 任务id
     * @param schoolId 学校id
     * @return 用户id列表
     */
    List<Integer> getUserIdList(Integer taskId, Integer schoolId);

    /**
     * 修改to_reset为1
     * @param taskUserId 任务id
     * @return int
     */
    Integer resetTask(Long taskUserId);
}
