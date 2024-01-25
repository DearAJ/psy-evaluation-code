package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.ww.domain.Students;
import org.ww.dto.FindStudentDto;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.StudentService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author 13096
 * @Date 2022/2/26 20:59
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/manage")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("addStudent")
    public Wrapper<String> addStudent(Students students) {
        return studentService.addStudent(students);
    }

    @PostMapping("studentList")
    public Wrapper<Object> studentList(FindStudentDto findStudentDto) {
        return studentService.studentList(findStudentDto);
    }

    @PostMapping("updateStudent")
    public Wrapper<String> updateStudent(Students student) {
        return studentService.updateStudent(student);
    }

    @ResponseBody
    @PostMapping("deleteStudent")
    public Wrapper<Object> deleteStudent(@RequestBody JSONObject jsonObject) {
        return studentService.deleteStudent(jsonObject);
    }

    @ResponseBody
    @PostMapping("verifyStudentExcel")
    public Wrapper<Object> verifyStudentExcel(HttpServletRequest request) {
        MultipartFile studentExcel = ((MultipartHttpServletRequest) request).getFile("file");
        String school_id = request.getParameter("schoolId");
        try {
            if (studentExcel != null && studentExcel.getSize() > 0) {
                if (school_id == null || school_id.equals("")) {
                    return WrapMapper.wrap(Wrapper.ERROR_CODE, "请传入学校参数");
                }
                Integer schoolId = Integer.parseInt(school_id);
                return studentService.verifyStudentExcel(studentExcel, schoolId);
            }else {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空或文件异常，请选择文件");
            }
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @PostMapping("/addStudents")
    public Wrapper<Object> addStudents(@RequestBody List<Students> studentsList) {
        return studentService.addStudents(studentsList);
    }
}
