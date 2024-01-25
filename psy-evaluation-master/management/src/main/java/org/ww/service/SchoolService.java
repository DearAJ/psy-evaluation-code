package org.ww.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import org.ww.domain.School;
import org.ww.result.Wrapper;

import java.util.List;

/**
 * @Author 13096
 * @Date 2022/2/11 9:44
 * @Version 1.0
 */
public interface SchoolService extends IService<School> {
    /**
     * 新增学校
     * @param  school 学校对象
     * @return 返回是否成功
     */
    Wrapper<String> addSchool(School school);

    /**
     * 生成学校登陆账号
     * @param province 省
     * @param city 市
     * @param county 区
     * @return 返回账号
     */
    String createSchoolLogin(String province, String city, String county);

    /**
     * 根据省份名/市/区县 获取学校列表
     * @param jsonObject object
     * @return 分页返回学校列表
     */
    Wrapper<IPage<School>> schoolList(JSONObject jsonObject);

    /**
     * 根据省份名/市/区县 获取学校列表
     * @param jsonObject object
     * @return 返回学校列表
     */
    Wrapper<Object> schoolListAll(JSONObject jsonObject);

    /**
     * 更新学校信息
     * @param school 学校对象
     * @return 返回是否成功
     */
    Wrapper<String> updateSchool(School school);

    /**
     * 删除学校 支持批量
     * @param jsonObject object
     * @return 返回删除学校个数
     */
    Wrapper<Integer> deleteSchool(JSONObject jsonObject);

    /**
     * 验证学校上传表格
     * @param schoolExcel 表格
     * @param province 省
     * @param city 市
     * @param county 区县
     * @return 返回表格正确和错误记录
     */
    Wrapper<Object> verifySchoolExcel(MultipartFile schoolExcel, String province, String city, String county);

    /**
     * 批量添加学校
     * @param schoolList 学校列表
     * @return 返回是否成功或失败信息
     */
    Wrapper<Object> addSchools(List<School> schoolList);

    /**
     * 根据学校、学段、年级获取班级列表
     * @param jsonObject jsonObject
     * @return 返回班级列表
     */
    Wrapper<Object> getClasses(JSONObject jsonObject);

    /**
     * 根据省市返回 区县列表
     * @param jsonObject jsonObject
     * @return 返回区县列表
     */
    Wrapper<Object> countyList(JSONObject jsonObject);

    /**
     * 根据省市区返回学校列表
     * @param jsonObject jsonObject
     * @return 返回学校列表
     */
    Wrapper<Object> schoolNameList(JSONObject jsonObject);

    /**
     * 根据学校获取班级列表
     * @param jsonObject jsonObject
     * @return 返回班级列表
     */
    Wrapper<Object> getAllClasses(JSONObject jsonObject);

    /**
     * 根据省市获取区县
     * @param jsonObject jsonObject
     * @return 返回区县列表
     */
    Wrapper<Object> getAllCounty(JSONObject jsonObject);

    /**
     * 根据省获取市
     * @param jsonObject jsonObject
     * @return 返回市列表
     */
    Wrapper<Object> getAllCity(JSONObject jsonObject);

    /**
     * 数量统计
     * @param jsonObject jsonObject
     * @return 返回统计结果
     */
    Wrapper<Object> getDataStatistics(JSONObject jsonObject);
}
