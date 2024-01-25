package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.ww.domain.GroupReport;
import org.ww.result.Wrapper;
import org.ww.service.GroupReportService;

import java.util.Map;

/**
 * @Author 13096
 * @Date 2022/11/21 22:40
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/groupReport")
public class GroupReportController {
    private final GroupReportService groupReportService;

    public GroupReportController(GroupReportService groupReportService) {
        this.groupReportService = groupReportService;
    }


    @PostMapping("addGroupReport")
    public Wrapper<String> addGroupReport(@RequestBody JSONObject jsonObject) {
        return groupReportService.addGroupReport(jsonObject);
    }


    @ResponseBody
    @PostMapping("groupReportList")
    public Wrapper<Object> groupReportList(@RequestBody JSONObject jsonObject){
        return groupReportService.groupReportList(jsonObject);
    }


    @ResponseBody
    @PostMapping("updateGroupReport")
    public Wrapper<String> updateGroupReport (GroupReport groupReport) {
        return groupReportService.updateGroupReport(groupReport);
    }

    @ResponseBody
    @PostMapping("deleteGroupReport")
    public Wrapper<Object> deleteGroupReport (@RequestBody JSONObject jsonObject) {
        return groupReportService.deleteGroupReport(jsonObject);
    }

    @ResponseBody
    @PostMapping("viewGroupReport")
    public Wrapper<Map<String, Object>> viewGroupReport(@RequestBody JSONObject jsonObject){
        return groupReportService.viewGroupReport(jsonObject);
    }

    @ResponseBody
    @PostMapping("getSchoolsByCounty")
    public Wrapper<Object> getSchoolsByCounty(@RequestBody JSONObject jsonObject){
        return groupReportService.getSchoolsByCounty(jsonObject);
    }

    @ResponseBody
    @PostMapping("getCountyTasks")
    public Wrapper<Object> getCountyTasks(@RequestBody JSONObject jsonObject){
        return groupReportService.getCountyTasks(jsonObject);
    }


}
