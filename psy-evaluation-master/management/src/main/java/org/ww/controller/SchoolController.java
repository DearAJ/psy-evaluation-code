package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.ww.domain.School;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.SchoolService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author 13096
 * @Date 2022/2/10 22:40
 * @Version 1.0
 */

@Slf4j
@RestController
@RequestMapping("/manage")
public class SchoolController {

    private final SchoolService schoolService;

    public SchoolController(SchoolService schoolService) {
        this.schoolService = schoolService;
    }

    @PostMapping("addSchool")
    public Wrapper<String> addSchool(School school) {
        return schoolService.addSchool(school);
    }

    @ResponseBody
    @PostMapping("schoolList")
    public Wrapper<IPage<School>> schoolList(@RequestBody JSONObject jsonObject) {
        return schoolService.schoolList(jsonObject);
    }

    @ResponseBody
    @PostMapping("schoolListAll")
    public Wrapper<Object> schoolListAll(@RequestBody JSONObject jsonObject) {
        return schoolService.schoolListAll(jsonObject);
    }

    @PostMapping("updateSchool")
    public Wrapper<String> updateSchool(School school) {
        return schoolService.updateSchool(school);
    }

    @ResponseBody
    @PostMapping("deleteSchool")
    public Wrapper<Integer> deleteSchool(@RequestBody JSONObject jsonObject) {
        return schoolService.deleteSchool(jsonObject);
    }

    @ResponseBody
    @PostMapping("verifySchoolExcel")
    public Wrapper<Object> verifySchoolExcel(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile schoolExcel = multipartHttpServletRequest.getFile("file");
        String province = request.getParameter("province");
        String city = request.getParameter("city");
        String county = request.getParameter("county");
        try {
            if (schoolExcel != null && schoolExcel.getSize() > 0) {
                if (province == null || province.equals("")) {
                    return WrapMapper.wrap(Wrapper.ERROR_CODE, "请输入省份参数");
                }
                if (city == null || city.equals("")) {
                    return WrapMapper.wrap(Wrapper.ERROR_CODE, "请输入市参数");
                }
                if (county == null || county.equals("")) {
                    return WrapMapper.wrap(Wrapper.ERROR_CODE, "请输入区县参数");
                }
                return schoolService.verifySchoolExcel(schoolExcel, province, city, county);
            }else {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空或文件异常，请选择文件");
            }
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @PostMapping("addSchools")
    public Wrapper<Object> addSchools(@RequestBody List<School> schoolList) {
        return schoolService.addSchools(schoolList);
    }

    @PostMapping("getClasses")
    public Wrapper<Object> getClasses(@RequestBody JSONObject jsonObject) {
        return schoolService.getClasses(jsonObject);
    }

    @PostMapping("getAllClasses")
    public Wrapper<Object> getAllClasses(@RequestBody JSONObject jsonObject) {
        return schoolService.getAllClasses(jsonObject);
    }

    @PostMapping("countyList")
    public Wrapper<Object> countyList(@RequestBody JSONObject jsonObject){
        return schoolService.countyList(jsonObject);
    }

    @PostMapping("schoolNameList")
    public Wrapper<Object> schoolNameList(@RequestBody JSONObject jsonObject){
        return schoolService.schoolNameList(jsonObject);
    }

    @PostMapping("getAllCounty")
    public Wrapper<Object> getAllCounty(@RequestBody JSONObject jsonObject){
        return schoolService.getAllCounty(jsonObject);
    }

    @PostMapping("getAllCity")
    public Wrapper<Object> getAllCity(@RequestBody JSONObject jsonObject){
        return schoolService.getAllCity(jsonObject);
    }

    @PostMapping("getDataStatistics")
    public Wrapper<Object> getDataStatistics(@RequestBody JSONObject jsonObject){
        return schoolService.getDataStatistics(jsonObject);
    }
}
