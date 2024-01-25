package org.ww.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import org.ww.domain.Students;
import org.ww.dto.FindStudentDto;
import org.ww.result.Wrapper;

import java.util.List;

/**
 * @Author 13096
 * @Date 2022/2/26 21:00
 * @Version 1.0
 */
public interface StudentService extends IService<Students> {
    /**
     * 新增学生
     * @param student 学生对象
     * @return 返回是否成功
     */
    Wrapper<String> addStudent(Students student);

    /**
     * 根据学校、入学年份、学段、年级、班级、性别查询学生
     * @param findStudentDto studentDto对象
     * @return 返回学生列表
     */
    Wrapper<Object> studentList(FindStudentDto findStudentDto);

    /**
     * 修改学生
     * @param student 学生对象
     * @return 返回是否成功
     */
    Wrapper<String> updateStudent(Students student);

    /**
     * 删除学生
     * @param jsonObject id或id数组
     * @return 返回删除记录条数
     */
    Wrapper<Object> deleteStudent(JSONObject jsonObject);

    /**
     * 验证学生表格
     * @param studentExcel 上传学生文件
     * @param schoolId 学校id
     * @return 返回校验结果
     */
    Wrapper<Object> verifyStudentExcel(MultipartFile studentExcel, Integer schoolId);

    /**
     * 批量添加学生
     * @param studentsList 学生列表
     * @return 返回是否添加成功
     */
    Wrapper<Object> addStudents(List<Students> studentsList);
}
