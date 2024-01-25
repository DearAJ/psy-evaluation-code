package org.ww.controller;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.ww.dto.TaskDto;
import org.ww.dto.TaskResetDto;
import org.ww.result.Wrapper;
import org.ww.service.TaskService;

/**
 * @Author 13096
 * @Date 2022/2/10 22:40
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping("addTask")
    public Wrapper<String> addTask(TaskDto taskDto) {
        return taskService.addTask(taskDto);
    }

    @PostMapping("studentTaskList")
    public Wrapper<Object> studentTaskList(@RequestBody JSONObject jsonObject) {
        return taskService.studentTaskList(jsonObject);
    }

    @PostMapping("taskResult")
    public Wrapper<String> taskResult(@RequestBody JSONObject jsonObject){
        return taskService.taskResult(jsonObject);
    }

    @ResponseBody
    @PostMapping("personalReport")
    public Wrapper<Map<String, Object>> personalReport(@RequestBody JSONObject jsonObject){
        return taskService.personalReport(jsonObject);
    }

    @ResponseBody
    @PostMapping("customTaskList")
    public Wrapper<Object> customTaskList(@RequestBody JSONObject jsonObject){
        return taskService.customTaskList(jsonObject);
    }

    @ResponseBody
    @PostMapping("personalReportList")
    public Wrapper<Object> personalReportList(@RequestBody JSONObject jsonObject){
        return taskService.personalReportList(jsonObject);
    }

    @ResponseBody
    @PostMapping("personalReportListByUserId")
    public Wrapper<Object> personalReportListByUserId(@RequestBody JSONObject jsonObject){
        return taskService.personalReportListByUserId(jsonObject);
    }

    @ResponseBody
    @PostMapping("schoolTaskList")
    public Wrapper<Object> schoolTaskList(@RequestBody JSONObject jsonObject) {
        return taskService.schoolTaskList(jsonObject);
    }

    @ResponseBody
    @PostMapping("taskExecution")
    public Wrapper<Object> taskExecution(@RequestBody JSONObject jsonObject){
        return taskService.taskExecution(jsonObject);
    }

    @ResponseBody
    @PostMapping("schoolTaskCompletion")
    public Wrapper<Object> schoolTaskCompletion(@RequestBody JSONObject jsonObject){
        return taskService.schoolTaskCompletion(jsonObject);
    }

    @ResponseBody
    @PostMapping("taskList")
    public Wrapper<Object> taskList(@RequestBody JSONObject jsonObject) {
        return taskService.taskList(jsonObject);
    }

    @ResponseBody
    @PostMapping("taskProgress")
    public Wrapper<Object> taskProgress(@RequestBody JSONObject jsonObject) {
        return taskService.taskProgress(jsonObject);
    }

    @ResponseBody
    @PostMapping("countyLatestTask")
    public Wrapper<Object> countyLatestTask(@RequestBody JSONObject jsonObject) {
        return taskService.countyLatestTask(jsonObject);
    }

    @ResponseBody
    @PostMapping("taskStatistics")
    public Wrapper<Object> taskStatistics(@RequestBody JSONObject jsonObject) {
        return taskService.taskStatistics(jsonObject);
    }

    @ResponseBody
    @PostMapping("schoolTaskProgress")
    public Wrapper<Object> schoolTaskProgress(@RequestBody JSONObject jsonObject) {
        return taskService.schoolTaskProgress(jsonObject);
    }

    @ResponseBody
    @PostMapping("cityTaskStatistics")
    public Wrapper<Object> cityTaskStatistics(@RequestBody JSONObject jsonObject) {
        return taskService.cityTaskStatistics(jsonObject);
    }

    @ResponseBody
    @PostMapping("cityTaskWarningStatistics")
    public Wrapper<Object> cityTaskWarningStatistics(@RequestBody JSONObject jsonObject){
        return taskService.cityTaskWarningStatistics(jsonObject);
    }

    @ResponseBody
    @PostMapping("submitTask")
    public Wrapper<String> submitTask (@RequestBody JSONObject jsonObject) {
        return taskService.submitTask(jsonObject);
    }

    @ResponseBody
    @PostMapping("getTaskRawData")
    public Wrapper<Object> getTaskRawData (@RequestBody JSONObject jsonObject) {
        return taskService.getTaskRawData(jsonObject);
    }

    @ResponseBody
    @PostMapping("updateTaskEndDate")
    public Wrapper<Object> updateTaskEndDate (@RequestBody JSONObject jsonObject) {
        return taskService.updateTaskEndDate(jsonObject);
    }

    @ResponseBody
    @PostMapping("resetTask")
    public Wrapper<Object> resetTask (@RequestBody TaskResetDto taskResetDto) {
        return taskService.resetTask(taskResetDto);
    }

    @ResponseBody
    @PostMapping("latestTask")
    public Wrapper<Object> latestTask (@RequestBody JSONObject jsonObject) {
        return taskService.latestTask(jsonObject);
    }

    @ResponseBody
    @PostMapping("allUnitTaskProcess")
    public Wrapper<Object> allUnitTaskProcess (@RequestBody JSONObject jsonObject) {
        return taskService.allUnitTaskProcess(jsonObject);
    }

    @ResponseBody
    @PostMapping("getAllOriginalData")
    public Wrapper<Object> getAllOriginalData (@RequestBody JSONObject jsonObject) {
        return taskService.getAllOriginalData(jsonObject);
    }
}
