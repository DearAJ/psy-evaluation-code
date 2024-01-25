package org.ww.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.ww.domain.GroupReport;
import org.ww.result.Wrapper;

import java.util.Map;

/**
 * @Author 13096
 * @Date 2022/11/15 10:44
 * @Version 1.0
 */
public interface GroupReportService extends IService<GroupReport> {
    /**
     * 新增团体报告
     * @param jsonObject 团体报告对象
     * @return 返回是否成功
     */
    Wrapper<String> addGroupReport(JSONObject jsonObject);


    public Wrapper<Object> groupReportList(JSONObject jsonObject);

    /**
     * 修改团体报告
     * @param groupReport 团体报告对象
     * @return 返回是否成功
     */
    Wrapper<String> updateGroupReport(GroupReport groupReport);

    /**
     * 删除团体报告
     * @param jsonObject id或id数组
     * @return 返回删除记录条数
     */
    Wrapper<Object> deleteGroupReport(JSONObject jsonObject);

    /**
     * 查看团体报告
     * @param jsonObject
     * @return 返回团体报告
     */
    public Wrapper<Map<String, Object>> viewGroupReport(JSONObject jsonObject);

    public Wrapper<Object> getSchoolsByCounty(JSONObject jsonObject);

    public Wrapper<Object> getCountyTasks(JSONObject jsonObject);
}
