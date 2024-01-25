package org.ww.service;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;

import org.ww.domain.Task;
import org.ww.dto.TaskDto;
import org.ww.dto.TaskResetDto;
import org.ww.result.Wrapper;

/**
 * @Author 13096
 * @Date 2022/2/11 9:44
 * @Version 1.0
 */
public interface TaskService extends IService<Task> {
    /**
     * 添加任务
     * @param taskDto taskDto
     * @return 返回是否添加成功
     */
    Wrapper<String> addTask(TaskDto taskDto);

    /**
     * 学生端任务列表
     * @param jsonObject jsonObject
     * @return 返回任务列表
     */
    Wrapper<Object> studentTaskList(JSONObject jsonObject);

    /**
     * 量表提交
     * @param jsonObject jsonObject
     * @return 返回是否提交成功
     */
    Wrapper<String> taskResult(JSONObject jsonObject);

    /**
     * 个人报告
     * @param jsonObject jsonObject
     * @return 返回个人报告内容
     */
    Wrapper<Map<String, Object>> personalReport(JSONObject jsonObject);

    /**
     * 自定义量表列表
     * @param jsonObject jsonObject
     * @return 返回自定义量表列表
     */
    Wrapper<Object> customTaskList(JSONObject jsonObject);

    /**
     * 报告列表
     * @param jsonObject
     * @return 个人报告列表
     */
    Wrapper<Object> personalReportList(JSONObject jsonObject);

    /**
     * 用户个人报告列表
     * @param jsonObject
     * @return 个人报告列表
     */
    Wrapper<Object> personalReportListByUserId(JSONObject jsonObject);

    /**
     * 学校端任务列表
     * @param jsonObject jsonObject
     * @return 返回任务列表
     */
    Wrapper<Object> schoolTaskList(JSONObject jsonObject);

    /**
     * 学校端任务执行情况
     * @param jsonObject jsonObject
     * @return 执行情况列表
     */
    Wrapper<Object> taskExecution(JSONObject jsonObject);
    /**
     * 学校端任务执行度
     * @param jsonObject jsonObject
     * @return 执行度列表
     */
    Wrapper<Object> schoolTaskCompletion(JSONObject jsonObject);
    /**
     * 区县端任务列表
     * @param jsonObject jsonObject
     * @return 返回任务列表
     */
    Wrapper<Object> taskList(JSONObject jsonObject);

    /**
     * 获取任务进度
     * @param jsonObject jsonObject
     * @return 返回任务进度列表
     */
    Wrapper<Object> taskProgress(JSONObject jsonObject);

    /**
     * 区县端最新任务
     * @param jsonObject jsonObject
     * @return 返回最新任务信息
     */
    Wrapper<Object> countyLatestTask(JSONObject jsonObject);

    /**
     * 某个任务的统计数据
     * @param jsonObject jsonObject
     * @return 返回该任务的统计信息
     */
    Wrapper<Object> taskStatistics(JSONObject jsonObject);

    /**
     * 学校端首页 最新任务及班级进度
     * @param jsonObject jsonObject
     * @return 最新任务及班级进度
     */
    Wrapper<Object> schoolTaskProgress(JSONObject jsonObject);

    /**
     * 市级首页 最新任务及各区县进度
     * @param jsonObject jsonObject
     * @return 返回任务信息
     */
    Wrapper<Object> cityTaskStatistics(JSONObject jsonObject);

    /**
     * 市级首页 最新任务及各区县预警统计
     * @param jsonObject jsonObject
     * @return 返回预警统计
     */
    Wrapper<Object> cityTaskWarningStatistics(JSONObject jsonObject);

    /**
     * 学校端提交任务
     * @param jsonObject jsonObject
     * @return 返回是否提交成功
     */
    Wrapper<String> submitTask(JSONObject jsonObject);

    /**
     * 获取自定义量表的原始数据
     * @param jsonObject jsonObject
     * @return 返回量表数据
     */
    Wrapper<Object> getTaskRawData(JSONObject jsonObject);

    /**
     * 修改任务结束时间
     * @param jsonObject jsonObject
     * @return 返回是否成功
     */
    Wrapper<Object> updateTaskEndDate(JSONObject jsonObject);

    /**
     * 发回重测
     * @param taskResetDto taskResetDto
     * @return 返回是否成功
     */
    Wrapper<Object> resetTask(TaskResetDto taskResetDto);

    /**
     * 省市区学校最新任务
     * @param jsonObject jsonObject
     * @return 返回任务信息
     */
    Wrapper<Object> latestTask(JSONObject jsonObject);

    /**
     * 省市下属单位最新任务进度
     * @param jsonObject jsonObject
     * @return 返回任务进度
     */
    Wrapper<Object> allUnitTaskProcess(JSONObject jsonObject);

    /**
     * 学生量表原始任务数据
     * @param jsonObject jsonObject
     * @return 返回学生量表原始任务数据
     */
    Wrapper<Object> getAllOriginalData(JSONObject jsonObject);


}
