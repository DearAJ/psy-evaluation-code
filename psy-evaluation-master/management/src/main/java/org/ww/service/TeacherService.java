package org.ww.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import org.ww.domain.Teachers;
import org.ww.dto.FindTeacherDto;
import org.ww.result.Wrapper;

import java.util.List;

/**
 * @Author 13096
 * @Date 2022/6/1 20:44
 * @Version 1.0
 */
public interface TeacherService extends IService<Teachers> {
    /**
     * 新增教师
     * @param teacher 教师对象
     * @return 返回是否成功
     */
    Wrapper<String> addTeacher(Teachers teacher);

    /**
     * 根据姓名、身份证号、性别、手机号筛选教师
     * @param findTeacherDto findTeacherDto对象
     * @return 返回教师列表
     */
    Wrapper<Object> teacherList(FindTeacherDto findTeacherDto);

    /**
     * 修改教师
     * @param teacher 教师对象
     * @return 返回是否成功
     */
    Wrapper<String> updateTeacher(Teachers teacher);

    /**
     * 删除教师
     * @param jsonObject id或id数组
     * @return 返回删除记录条数
     */
    Wrapper<Object> deleteTeacher(JSONObject jsonObject);

    /**
     * 验证教师表格
     * @param teacherExcel 上传教师文件
     * @param schoolId 学校id
     * @return 返回校验结果
     */
    Wrapper<Object> verifyTeacherExcel(MultipartFile teacherExcel, Integer schoolId);

    /**
     * 批量添加教师
     * @param teachersList 教师列表
     * @return 返回是否添加成功
     */
    Wrapper<Object> addTeachers(List<Teachers> teachersList);
}
