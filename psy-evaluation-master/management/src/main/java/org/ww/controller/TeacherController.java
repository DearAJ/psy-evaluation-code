package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.ww.domain.Teachers;
import org.ww.dto.FindTeacherDto;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.TeacherService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author 13096
 * @Date 2022/6/1 20:44
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/manage")
public class TeacherController {
    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    //localhost:8087/manage/addTeacher
    /*
        form:

     */
    @PostMapping("addTeacher")
    public Wrapper<String> addTeacher(Teachers teacher) {
        return teacherService.addTeacher(teacher);
    }

    @PostMapping("teacherList")
    public Wrapper<Object> teacherList(FindTeacherDto findTeacherDto) {
        return teacherService.teacherList(findTeacherDto);
    }

    @PostMapping("updateTeacher")
    public Wrapper<String> updateTeacher(Teachers teacher) {
        return teacherService.updateTeacher(teacher);
    }

    @ResponseBody
    @PostMapping("deleteTeacher")
    public Wrapper<Object> deleteTeacher(@RequestBody JSONObject jsonObject) {
        return teacherService.deleteTeacher(jsonObject);
    }

    @ResponseBody
    @PostMapping("verifyTeacherExcel")
    public Wrapper<Object> verifyTeacherExcel(HttpServletRequest request) {
        MultipartFile teacherExcel = ((MultipartHttpServletRequest) request).getFile("file");
        String school_id = request.getParameter("schoolId");
        try {
            if (teacherExcel != null && teacherExcel.getSize() > 0) {
                if (school_id == null || school_id.equals("")) {
                    return WrapMapper.wrap(Wrapper.ERROR_CODE, "请传入学校参数");
                }
                Integer schoolId = Integer.parseInt(school_id);
                return teacherService.verifyTeacherExcel(teacherExcel, schoolId);
            }else {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空或文件异常，请选择文件");
            }
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @PostMapping("/addTeachers")
    public Wrapper<Object> addTeachers(@RequestBody List<Teachers> teachersList) {
        return teacherService.addTeachers(teachersList);
    }
}
