package org.ww.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.assertj.core.util.Strings;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.ww.constants.Constants;
import org.ww.domain.*;
import org.ww.dto.FindStudentDto;
import org.ww.exception.BusinessException;
import org.ww.mapper.*;
import org.ww.provider.TaskProvider;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.StudentService;
import org.ww.utils.SchoolYearUtils;
import org.ww.vo.UserVo;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author 13096
 * @Date 2022/2/26 21:00
 * @Version 1.0
 */
@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentsMapper, Students> implements StudentService {
    @Resource
    private StudentsMapper studentsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private SchoolClassesMapper schoolClassesMapper;

    @Resource
    private TaskSchoolMapper taskSchoolMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private TaskUserMapper taskUserMapper;

    @DubboReference
    private TaskProvider taskProvider;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Wrapper<String> addStudent(Students student) {
        String currentYear = "" + SchoolYearUtils.currentSchoolYear();

        try {
            Students existed = checkExisted(student);
            if (existed != null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "学籍号或身份证号已存在");
            }

            // 判断user表里是否有相同的username
//            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
//            userQueryWrapper.eq("username", student.getUsername());
//            int userCount = userMapper.selectCount(userQueryWrapper);
//            if (userCount != 0) {
//                return WrapMapper.wrap(Wrapper.ERROR_CODE, "已有相同的身份证号作为用户名");
//            }

            QueryWrapper<SchoolClasses> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("class_numbers").eq("school_id", student.getSchoolId()).eq("entrance_year", student.getEntranceYear())
                    .eq("period", student.getPeriod()).eq("grade", student.getGrade()).eq("classes", student.getClasses())
                    .eq("school_year", currentYear);
            // 不考虑入学年份，只考虑学段年级班级
//            queryWrapper1.select("class_numbers").eq("school_id", student.getSchoolId()).eq("period", student.getPeriod()).eq("grade", student.getGrade()).eq("classes", student.getClasses());
            SchoolClasses schoolClasses = schoolClassesMapper.selectOne(queryWrapper1);
            // 如果要加入的学生所在班级不存在，则在school_class新增记录；否则人数加1
            if (schoolClasses == null) {
                schoolClassesMapper.insert(new SchoolClasses(1L, student.getSchoolId(), student.getEntranceYear(), student.getPeriod(), student.getGrade(), student.getClasses(), 1, new Date(), new Date(), 0, currentYear, 0));
            } else {
                schoolClassesMapper.plusSchoolClassNumbers(student.getSchoolId(), student.getEntranceYear(), student.getPeriod(), student.getGrade(), student.getClasses(), currentYear);
            }
            // 学校总人数加1
            schoolMapper.plusStudentNumbers(student.getSchoolId());
            // 添加学生
            student.setSchoolYear(currentYear);
            student.setArchived(0);
            studentsMapper.insert(student);

//            studentsMapper.insert(student);
            log.info("add Student:{}", JSON.toJSON(student));

            // 创建学生账户
            boolean foundUser = false;
            User user = null;
            UserVo userVo = userMapper.findByUsername(student.getIdNumber());
            if(userVo != null)
            {
                foundUser = true;
                user = new User();
                user.setId(Long.valueOf(userVo.getId()));
            }

            if(user == null){
                user = new User();
            }

            user.setUsername(student.getIdNumber());
            String rawPassword = student.getIdNumber().substring(student.getIdNumber().length() - 6);
            user.setPassword(new BCryptPasswordEncoder().encode(rawPassword));
            if (student.getSex().equals("男")) {
                user.setAvatar(Constants.avatarBoy);
            } else {
                user.setAvatar(Constants.avatarGirl);
            }

            if(foundUser)
            {
                userMapper.updateById(user);
            }else {
                userMapper.insert(user);
            }

            log.info("add User:{}", JSON.toJSON(user));

            // 添加学生角色
            Role role = roleMapper.selectOne(new QueryWrapper<Role>()
                    .eq("username", student.getUsername())
                    .orderByDesc("id")
                    .last("limit 1"));
            if(role == null)
            {
                role = new Role();
                role.setUsername(student.getUsername());
                role.setRole("学生");
                role.setLevel(0);
                roleMapper.insert(role);
            }else {
                role.setUsername(student.getUsername());
                role.setRole("学生");
                role.setLevel(0);
                roleMapper.updateById(role);
            }

            log.info("add Role:{}", JSON.toJSON(role));
            // 查询学生所在班级是否存在正在进行的任务
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select("id", "task_id", "grade", "classes")
                    .eq("school_id", student.getSchoolId()).eq("is_submit", 0)
                    .eq("school_year", currentYear);
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);
            Set<Integer> taskIds = new HashSet<>();
            List<Long> ids = new ArrayList<>();   // 为避免task_school中task_id重复，同时记录task_school表的id，用来实现该记录包括人数的增加
            List<String> curGradesList;
            List<String> curClassesList;
            String grade = student.getPeriod() + student.getGrade();         // 年级由学段和年级组成
            String classes = grade + student.getClasses();    // 班级由学段年级班级组成
            for (TaskSchool taskSchool : taskSchoolList) {
                if (!org.apache.commons.lang3.StringUtils.isBlank(taskSchool.getGrade())) {   // 如果年级列表不为空，则判断后继续检查班级列表
                    curGradesList = Arrays.asList(taskSchool.getGrade().split(","));
                    if (curGradesList.contains(grade)) {   // 如果年级列表包含当前年级，则继续判断班级
                        if (!org.apache.commons.lang3.StringUtils.isBlank(taskSchool.getClasses())) {    // 如果班级列表不为空，则判断是否包含当前班级
                            curClassesList = Arrays.asList(taskSchool.getClasses().split(","));
                            if (curClassesList.contains(classes)) {     // 如果班级列表包含当前班级，则将任务id加入任务列表
                                taskIds.add(taskSchool.getTaskId());
                                ids.add(taskSchool.getId());
                            }
                        }else {   // 如果班级列表为空，则为全部包含，则将任务id加入任务列表
                            taskIds.add(taskSchool.getTaskId());
                            ids.add(taskSchool.getId());
                        }
                    }
                }else { // 如果年级列表为空，则为全部包含，则将任务id加入任务列表
                    taskIds.add(taskSchool.getTaskId());
                    ids.add(taskSchool.getId());
                }
            }
            // 如果taskIds不为空，则增加task任务的参与人数
            for (Integer taskId : taskIds) {
                taskMapper.updateNumbersById(taskId);
            }
            // 如果ids不为空，则增加task_school的参与人数
            for (Long id : ids) {
                taskSchoolMapper.updateNumbersById(id);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    private Students checkExisted(Students student)
    {
        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("archived", 0)
//                    .or(studentsQueryWrapper -> studentsQueryWrapper.eq("student_id", student.getStudentId()))
                .and(studentsQueryWrapper -> studentsQueryWrapper.eq("id_number", student.getIdNumber())
                        .or().eq("username", student.getUsername())
                        .or().eq("student_code", student.getStudentCode()))
                .orderByDesc("id")
                .last("limit 1");
        Students s = studentsMapper.selectOne(queryWrapper);
        return s;
    }

    @Override
    public Wrapper<Object> studentList(FindStudentDto findStudentDto) {
        try {
            QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("school_id", findStudentDto.getSchoolId());

            if(findStudentDto.getArchived() == null)
            {
                findStudentDto.setArchived(0);
            }

            queryWrapper.eq("archived", findStudentDto.getArchived());


            if ("心理老师".equals(findStudentDto.getRole()) && findStudentDto.getLevel() == 2 && (findStudentDto.getRangeClass() != null || !Strings.isNullOrEmpty(findStudentDto.getRangeClass()))) {
                String rangeClass = findStudentDto.getRangeClass();
                String[] classes = rangeClass.split(",");
                queryWrapper.and(studentsQueryWrapper -> {
                    for (String classTemp : classes) {
                        studentsQueryWrapper.or(studentsQueryWrapper1 -> studentsQueryWrapper1.eq("period", classTemp.substring(0, 2)).eq("grade", classTemp.substring(2, 5)).eq("classes", classTemp.substring(5)));
                    }
                });
            }
            if (!Strings.isNullOrEmpty(findStudentDto.getEntranceYear())) {
                queryWrapper.eq("entrance_year", findStudentDto.getEntranceYear());
            }
            if (!Strings.isNullOrEmpty(findStudentDto.getPeriod())) {
                queryWrapper.eq("period", findStudentDto.getPeriod());
                if (!Strings.isNullOrEmpty(findStudentDto.getGrade() )) {
                    queryWrapper.eq("grade", findStudentDto.getGrade());
                    if (!Strings.isNullOrEmpty(findStudentDto.getClasses())) {
                        queryWrapper.eq("classes", findStudentDto.getClasses());
                    }
                }
            }
            if (!Strings.isNullOrEmpty(findStudentDto.getSex())) {
                queryWrapper.eq("sex", findStudentDto.getSex());
            }
            if (findStudentDto.getGraduated() != null) {
                queryWrapper.eq("graduated", findStudentDto.getGraduated());
            }
            if (!Strings.isNullOrEmpty(findStudentDto.getStudentId() )) {
                queryWrapper.like("student_id", findStudentDto.getStudentId());
            }
            if (!Strings.isNullOrEmpty(findStudentDto.getName())) {
                queryWrapper.like("name", findStudentDto.getName());
            }

            if (!Strings.isNullOrEmpty(findStudentDto.getIdNumber())) {
                queryWrapper.like("id_number", findStudentDto.getIdNumber());
            }

            queryWrapper.orderByDesc("id");
            Integer pageNum = findStudentDto.getPageNum();
            Integer pageSize = findStudentDto.getPageSize();
            IPage<Students> studentsIPage = studentsMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
            for(Students s : studentsIPage.getRecords())
            {
                if(s.getGraduated() == 1)
                {
                    s.setGrade(s.getGrade() + "(毕业)");
                }
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, studentsIPage);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> updateStudent(Students student) {

        try {

            Students curStudent = studentsMapper.selectById(student.getId());
            if(curStudent == null)
            {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "用户不存在");
            }

            String sYear = curStudent.getSchoolYear();
            Integer sArchived = curStudent.getArchived();

            QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(studentsQueryWrapper -> studentsQueryWrapper.eq("student_id", student.getStudentId()).or().eq("student_code", student.getStudentCode()));
            queryWrapper.ne("id", student.getId());
            queryWrapper.eq("school_id", student.getSchoolId());
            queryWrapper.eq("school_year", sYear);
            queryWrapper.eq("archived", sArchived);

            int count = studentsMapper.selectCount(queryWrapper);
            if (count != 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "学号、学籍号已存在");
            }

            if (!curStudent.getPeriod().equals(student.getPeriod()) || !curStudent.getGrade().equals(student.getGrade()) || !curStudent.getClasses().equals(student.getClasses())) {
                // 减少旧班级的人数
                schoolClassesMapper.reduceSchoolClassNumbers(curStudent.getSchoolId(), curStudent.getEntranceYear(), curStudent.getPeriod(), curStudent.getGrade(), curStudent.getClasses(), sYear);
                // 增加新班级人数
                QueryWrapper<SchoolClasses> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.select("class_numbers").eq("school_id", student.getSchoolId()).eq("entrance_year", student.getEntranceYear()).eq("period", student.getPeriod()).eq("grade", student.getGrade()).eq("classes", student.getClasses())
                        .eq("school_year", sYear);
                SchoolClasses schoolClasses = schoolClassesMapper.selectOne(queryWrapper1);
                // 如果要加入的学生所在班级不存在，则在school_class新增记录；否则人数加1
                if (schoolClasses == null) {
                    schoolClassesMapper.insert(new SchoolClasses(1L, student.getSchoolId(), student.getEntranceYear(), student.getPeriod(), student.getGrade(), student.getClasses(), 1, new Date(), new Date(), 0, sYear, sArchived));
                } else {
                    schoolClassesMapper.plusSchoolClassNumbers(student.getSchoolId(), student.getEntranceYear(), student.getPeriod(), student.getGrade(), student.getClasses(), sYear);
                }
            }
            // 删除class_numbers等于0的班级
            schoolClassesMapper.deleteClasses();

            studentsMapper.updateById(student);
            log.info("update student:{}", JSON.toJSON(student));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> deleteStudent(JSONObject jsonObject) {
        try {
            // 是否全选  0否 1是
            Integer isAll = jsonObject.getInteger("isAll");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer hasFilter = jsonObject.getInteger("hasFilter");
            List<String> userList;
            List<Students> studentsList;
            List<Long> ids = new ArrayList<>();
            List<Long> newIds = new ArrayList<>();
            List<Long> newStudentIds = new ArrayList<>();
            List<String> newUsers = new ArrayList<>();
            List<String> cantDeleteUsers = new ArrayList<>();
            if (isAll != null && isAll == 1) {
                QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                studentsQueryWrapper.select("id").eq("school_id", schoolId);
                if (hasFilter != null && hasFilter == 1) {
                    if (jsonObject.containsKey("entranceYear")) {
                        studentsQueryWrapper.eq("entrance_year", jsonObject.getString("entranceYear"));
                    }
                    if (jsonObject.containsKey("period")) {
                        studentsQueryWrapper.eq("period", jsonObject.getString("period"));
                    }
                    if (jsonObject.containsKey("grade")) {
                        studentsQueryWrapper.eq("grade", jsonObject.getString("grade"));
                    }
                    if (jsonObject.containsKey("classes")) {
                        studentsQueryWrapper.eq("classes", jsonObject.getString("classes"));
                    }
                    if (jsonObject.containsKey("sex")) {
                        studentsQueryWrapper.eq("sex", jsonObject.getString("sex"));
                    }
                    if (jsonObject.containsKey("graduated")) {
                        studentsQueryWrapper.eq("graduated", jsonObject.getInteger("graduated"));
                    }
                    if (jsonObject.containsKey("studentId")) {
                        studentsQueryWrapper.like("student_id", jsonObject.getString("studentId"));
                    }
                    if (jsonObject.containsKey("name")) {
                        studentsQueryWrapper.like("name", jsonObject.getString("name"));
                    }
                    if (jsonObject.containsKey("id_number")) {
                        studentsQueryWrapper.like("idNumber", jsonObject.getString("idNumber"));
                    }
                }
                List<Students> idList = studentsMapper.selectList(studentsQueryWrapper);
                ids = idList.stream().map(Students::getId).collect(Collectors.toList());
            } else {
                if (jsonObject.size() == 1) {
                    if (jsonObject.get("id") instanceof Long) {
                        ids.add(jsonObject.getLongValue("id"));
                    } else {
                        ids.addAll(jsonObject.getJSONArray("id").toJavaList(Long.class));
                    }
                } else {
                    ids.addAll(jsonObject.getJSONArray("id").toJavaList(Long.class));
                }
            }

            QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id", "username", "school_id", "entrance_year", "period", "grade", "classes").in("id", ids);
            studentsList = studentsMapper.selectList(queryWrapper);
            userList = studentsList.stream().map(Students::getUsername).collect(Collectors.toList());

            // 查询关联用户id
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("id", "username").in("username", userList);
            List<User> userList1 = userMapper.selectList(userQueryWrapper);
            for (User user : userList1) {
                if (taskProvider.hasTask(user.getId()) == 0) {
                    newIds.add(user.getId());
                    newUsers.add(user.getUsername());
                } else {
                    cantDeleteUsers.add(user.getUsername());
                }
            }

            List<Students> newStudentsList = new ArrayList<>();
            for (Students students : studentsList) {
                if (newUsers.contains(students.getUsername())) {
                    newStudentsList.add(students);
                    newStudentIds.add(students.getId());
                }
            }

            if (newIds.size() == 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "所选学生均不可删除");
            }

            // 删除关联用户
            userMapper.deleteBatchIds(newIds);
            log.info("delete users:{}", JSON.toJSONString(newUsers));
            // 删除关联权限
            QueryWrapper<Role> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.in("username", newUsers);
            roleMapper.delete(queryWrapper2);
            log.info("delete roles:{}", JSON.toJSONString(newUsers));
            // 更新班级人数
            for (Students students : newStudentsList) {
                schoolClassesMapper.reduceSchoolClassNumbers(students.getSchoolId(), students.getEntranceYear(), students.getPeriod(), students.getGrade(), students.getClasses(), students.getSchoolYear());
                log.info("reduce classNumbers in schoolClasses:{}", JSON.toJSONString(newIds));
                // 查询学生所在班级是否存在正在进行的任务
                QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
                taskSchoolQueryWrapper.select("id", "task_id", "grade", "classes").eq("school_id", students.getSchoolId()).eq("is_submit", 0);
                List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);
                Set<Integer> taskIds = new HashSet<>();
                List<Long> ts_ids = new ArrayList<>();   // 为避免task_school中task_id重复，同时记录task_school表的id，用来实现该记录包括人数的增加
                List<String> curGradesList;
                List<String> curClassesList;
                String grade = students.getPeriod() + students.getGrade();         // 年级由学段和年级组成
                String classes = grade + students.getClasses();    // 班级由学段年级班级组成
                for (TaskSchool taskSchool : taskSchoolList) {
                    if (!Strings.isNullOrEmpty(taskSchool.getGrade())) {   // 如果年级列表不为空，则判断后继续检查班级列表
                        curGradesList = Arrays.asList(taskSchool.getGrade().split(","));
                        if (curGradesList.contains(grade)) {   // 如果年级列表包含当前年级，则继续判断班级
                            if (!Strings.isNullOrEmpty(taskSchool.getClasses())) {    // 如果班级列表不为空，则判断是否包含当前班级
                                curClassesList = Arrays.asList(taskSchool.getClasses().split(","));
                                if (curClassesList.contains(classes)) {     // 如果班级列表包含当前班级，则将任务id加入任务列表
                                    taskIds.add(taskSchool.getTaskId());
                                    ts_ids.add(taskSchool.getId());
                                }
                            }else {   // 如果班级列表为空，则为全部包含，则将任务id加入任务列表
                                taskIds.add(taskSchool.getTaskId());
                                ts_ids.add(taskSchool.getId());
                            }
                        }
                    }else { // 如果年级列表为空，则为全部包含，则将任务id加入任务列表
                        taskIds.add(taskSchool.getTaskId());
                        ts_ids.add(taskSchool.getId());
                    }
                }
                // 如果taskIds不为空，则减少task任务的参与人数
                for (Integer taskId : taskIds) {
                    taskMapper.reduceNumbersById(taskId, 1);
                }
                // 如果ids不为空，则减少task_school的参与人数
                for (Long id : ts_ids) {
                    taskSchoolMapper.reduceNumbersById(id);
                }
            }
            // 根据user_id查询task_user中的task_id
            QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
            taskUserQueryWrapper.select("task_id", "valid", "warning", "user_id").in("user_id", newIds).orderByAsc("id");
            List<TaskUser> comTasks = taskUserMapper.selectList(taskUserQueryWrapper);
            Set<Integer> hasDeleteTask = new HashSet<>();  // 防止重复减少人数
            Map<Integer, Set<Integer>> warnTaskUsers = new HashMap<>();
            for (TaskUser comTask : comTasks) {
                if (comTask.getValid() == 1 && comTask.getWarning() == 1) {   // 如果某user的某task预警，则加入到任务对应user列表的map里
                    if (warnTaskUsers.containsKey(comTask.getTaskId())) {
                        warnTaskUsers.get(comTask.getTaskId()).add(comTask.getUserId());
                    } else {
                        Set<Integer> temp = new HashSet<>();
                        temp.add(comTask.getUserId());
                        warnTaskUsers.put(comTask.getTaskId(), temp);
                    }
                } else if (comTask.getValid() == 1 && comTask.getWarning() == 0) {  // 如果不是预警，可能是user重做了，则需要把之前加入的改掉
                    if (warnTaskUsers.containsKey(comTask.getTaskId())) {
                        warnTaskUsers.get(comTask.getTaskId()).remove(comTask.getUserId());
                    }
                }
                if (!hasDeleteTask.contains(comTask.getTaskId())) {
                    taskSchoolMapper.reduceComNumbersById(comTask.getTaskId(), schoolId, 1);
                    hasDeleteTask.add(comTask.getTaskId());
                }
            }
            // 更新task和task_school的预警人数
            for (Map.Entry<Integer, Set<Integer>> entry : warnTaskUsers.entrySet()) {
                int count = entry.getValue().size();
                taskMapper.reduceWarnNumbersById(entry.getKey(), count);
                taskSchoolMapper.reduceWarnNumbersById(entry.getKey(), schoolId, count);
            }
            // 更新学校人数
            schoolMapper.reduceBatchStudentNumbers(schoolId, newIds.size());
            log.info("reduce studentNumbers in schools:{}", newIds.size());
            // 删除学生
            studentsMapper.deleteBatchIds(newStudentIds);
            log.info("delete students:{}", JSON.toJSONString(newStudentIds));
            // 删除class_numbers等于0的班级
            schoolClassesMapper.deleteClasses();
            // 获取不可删除学生信息
            if (cantDeleteUsers.size() != 0) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, JSON.toJSON(cantDeleteUsers));
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> verifyStudentExcel(MultipartFile studentExcel, Integer schoolId) {
        try {
            if (studentExcel == null || studentExcel.getSize() == 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空");
            }
            String fileName = studentExcel.getOriginalFilename();
            if (fileName == null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件名为空");
            }
            if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件格式不正确");
            }
            boolean isExcel2007 = !fileName.matches("^.+\\.(?i)(xls)$");
            InputStream inputStream = studentExcel.getInputStream();
            Workbook workbook;
            if (isExcel2007) {
                workbook = new XSSFWorkbook(inputStream);
            }else {
                workbook = new HSSFWorkbook(inputStream);
            }
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "表格sheet为空");
            }
            // 查询该学校包括哪些学段
            QueryWrapper<School> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("period").eq("id", schoolId);
            School school = schoolMapper.selectOne(queryWrapper1);
            List<String> legalPeriod = Arrays.asList(school.getPeriod().split(","));

            int rows = sheet.getLastRowNum();
            Row row = sheet.getRow(2);
            Map<String, Integer> fieldColumn = new HashMap<>();
            Map<Integer, String> columnField = new HashMap<>();
            int columnNum = row.getPhysicalNumberOfCells();
            // 添加列名和列号对应
            for (int i = 0; i < columnNum; i ++) {
                fieldColumn.put(row.getCell(i).getStringCellValue(), i);
                columnField.put(i, row.getCell(i).getStringCellValue());
            }
            if (!fieldColumn.containsKey("入学年份") || !fieldColumn.containsKey("学段") || !fieldColumn.containsKey("年级")
                    || !fieldColumn.containsKey("班级") || !fieldColumn.containsKey("学生姓名") || !fieldColumn.containsKey("性别")
                    || !fieldColumn.containsKey("学号") || !fieldColumn.containsKey("学生身份证号") || !fieldColumn.containsKey("学籍号")
                    || !fieldColumn.containsKey("证件类型")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "模板列不匹配，请使用模板上传数据");
            }
            if (rows <= 2) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "表格内无学生数据");
            }
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> rightStudentList = new ArrayList<>();
            List<Map<String, Object>> errorStudentList = new ArrayList<>();
            List<Map<String, Object>> existStudentList = new ArrayList<>();
            List<Map<String, Object>> baseExistStudentList = new ArrayList<>();
            List<String> studentIds = new ArrayList<>();
            List<String> studentCodes = new ArrayList<>();
            List<String> idNumbers = new ArrayList<>();
            for (int i = 3; i <= rows; i ++) {
                boolean errorRow = false;   // 数据格式错误
                boolean existRow = false;   // 重复行提示
                boolean baseExistRow = false;  // 学生已存在提示
                StringBuilder info = new StringBuilder();
                Row row1 = sheet.getRow(i);
                if (row1 != null) {
                    if (row1.getCell(0) == null || StringUtils.isEmpty(row1.getCell(0))) {
                        continue;
                    }
                    Map<String, Object> student = new HashMap<>();
                    student.put("row", i + 1);
                    student.put("schoolId", schoolId);
                    for (int j = 0; j < columnNum; j ++) {
                        Cell cell = row1.getCell(j);
                        String temp;
                        if (cell == null) {
                            temp = "";
                        } else {
                            cell.setCellType(CellType.STRING);
                            temp = cell.getStringCellValue();
                        }
                        String filedName = columnField.get(j);
                        switch (filedName) {
                            case "入学年份":
                                student.put("entranceYear", temp);
                                break;
                            case "学段":
                                student.put("period", temp);
                                break;
                            case "年级":
                                student.put("grade", temp);
                                break;
                            case "班级":
                                student.put("classes", temp);
                                break;
                            case "学生姓名":
                                student.put("name", temp);
                                break;
                            case "性别":
                                student.put("sex", temp);
                                break;
                            case "学号":
                                student.put("studentId", temp);
                                break;
                            case "证件类型":
                                student.put("idType", temp);
                                break;
                            case "学生身份证号":
                                student.put("idNumber", temp);
                                student.put("username", temp);
                                break;
                            case "学籍号":
                                student.put("studentCode", temp);
                                break;
                        }
                    }

                    // 检验某个cell是否为空值
                    for (Map.Entry<String, Object> entry : student.entrySet()) {
                        if (entry.getValue().equals("")) {
                            errorRow = true;
                            if (!info.toString().equals("")) {
                                info.append(";该行存在某一列为空值");
                            } else {
                                info = new StringBuilder("该行存在某一列为空值");
                            }
                            break;
                        }
                    }

                    // 检验该条数据是否重复，检验学号、学籍号、身份证号
                    if (studentIds.contains(student.get("studentId").toString()) || studentCodes.contains(student.get("studentCode").toString()) || idNumbers.contains(student.get("idNumber").toString())) {
                        StringBuilder existFiled = new StringBuilder();
                        if (studentIds.contains(student.get("studentId").toString())) {
                            existFiled.append("学号");
                        }
                        if (studentCodes.contains(student.get("studentCode").toString())) {
                            existFiled.append("学籍号");
                        }
                        if (idNumbers.contains(student.get("idNumber").toString())) {
                            existFiled.append("证件号");
                        }
                        existRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";表格中已存在重复的").append(existFiled);
                        } else {
                            info = new StringBuilder("表格中已存在重复的").append(existFiled);
                        }
                    } else {
                        studentIds.add(student.get("studentId").toString());
                        studentCodes.add(student.get("studentCode").toString());
                        idNumbers.add(student.get("idNumber").toString());
                    }

                    // 如果学段不是学校对应学段内的，性别不为男女的，身份证号不为18位的，加入错误提示列表
                    if (!legalPeriod.contains(student.get("period").toString())) {
                        errorRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";学段格式错误或该学校不存在该学段");
                        } else {
                            info = new StringBuilder("学段格式错误或该学校不存在该学段");
                        }
                    }

                    if (!student.get("sex").toString().equals("男") && !student.get("sex").toString().equals("女")) {
                        errorRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";性别错误");
                        } else {
                            info = new StringBuilder("性别错误");
                        }
                    }

                    String reg = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";

                    if (student.get("idType").toString().equals("居民身份证") && !student.get("idNumber").toString().matches(reg)) {
                        errorRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";身份证号不合法");
                        } else {
                            info = new StringBuilder("身份证号不合法");
                        }
                    }

                    // 检验数据库中是否存在该学生学籍号、身份证号
                    if (!errorRow && !existRow) {
                        QueryWrapper<Students> queryWrapper = new QueryWrapper<>();
                        queryWrapper
                                .eq("archived",0)
//                    .or(studentsQueryWrapper -> studentsQueryWrapper.eq("student_id", student.getStudentId()))
                                .and(studentsQueryWrapper -> studentsQueryWrapper.eq("id_number", student.get("idNumber"))
                                        .or().eq("username", student.get("idNumber"))
                                        .or().eq("student_code", student.get("studentCode").toString()));
//                        queryWrapper.eq("student_code", student.get("studentCode").toString())
//                                .or(studentQueryWrapper -> studentQueryWrapper.eq("id_number", student.get("idNumber")));
                        int count = studentsMapper.selectCount(queryWrapper);
                        if (count != 0) {
                            baseExistRow = true;
                            if (!info.toString().equals("")) {
                                info.append(";系统中已存在相同的学籍号或证件号");
                            } else {
                                info = new StringBuilder("系统中已存在相同的学籍号或证件号");
                            }
                        } else {
                            QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                            studentsQueryWrapper.eq("school_id", schoolId).eq("student_id", student.get("studentId").toString())
                                    .eq("archived", 0);
                            int count1 = studentsMapper.selectCount(studentsQueryWrapper);
                            if (count1 != 0) {
                                baseExistRow = true;
                                if (!info.toString().equals("")) {
                                    info.append(";系统中该学校已存在相同的学号");
                                } else {
                                    info = new StringBuilder("系统中该学校已存在相同的学号");
                                }
                            }
                        }
                    }
                    if (errorRow) {
                        student.put("warn", info);
                        errorStudentList.add(student);
                    }  else if (existRow) {
                        student.put("warn", info);
                        existStudentList.add(student);
                    } else if (baseExistRow) {
                        student.put("warn", info);
                        baseExistStudentList.add(student);
                    } else {
                        rightStudentList.add(student);
                    }
                }
            }
            result.put("errorStudentList", errorStudentList);
            result.put("rightStudentList", rightStudentList);
            result.put("existStudentList", existStudentList);
            result.put("baseExistStudentList", baseExistStudentList);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, "表格读取完成", JSON.toJSON(result));
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> addStudents(List<Students> studentsList) {
        try {

            String currentYear = "" + SchoolYearUtils.currentSchoolYear();

            int count = studentsList.size();
            int cur, insertNum = 0;
            int schoolId = studentsList.get(0).getSchoolId();
            List<Students> errStudents = new ArrayList<>();
            for (Students student : studentsList) {

                Students existed = checkExisted(student);
                if (existed != null) {
                    errStudents.add(student);
                    log.info("student existed:{}", student);
                    continue;
                }

                cur = studentsMapper.insert(student);

                if (cur == 0) {
                    errStudents.add(student);
                    log.info("add student fail:{}", student);
                }else {
                    // 创建学生账户
                    boolean foundUser = false;
                    User user = null;
                    UserVo userVo = userMapper.findByUsername(student.getIdNumber());
                    if(userVo != null)
                    {
                        foundUser = true;
                        user = new User();
                        user.setId(user.getId());
                    }

                    if(user == null){
                        user = new User();
                    }

                    user.setUsername(student.getUsername());
                    String rawPassword = student.getIdNumber().substring(student.getIdNumber().length() - 6);
                    user.setPassword(new BCryptPasswordEncoder().encode(rawPassword));
                    if (student.getSex().equals("男")) {
                        user.setAvatar(Constants.avatarBoy);
                    } else {
                        user.setAvatar(Constants.avatarGirl);
                    }

                    if(foundUser)
                    {
                        userMapper.updateById(user);
                    }else {
                        userMapper.insert(user);
                    }

                    log.info("add User:{}", JSON.toJSON(user));
                    // 添加学生角色
                    Role role = roleMapper.selectOne(new QueryWrapper<Role>()
                            .eq("username", student.getUsername())
                            .orderByDesc("id")
                            .last("limit 1"));
                    if(role == null)
                    {
                        role = new Role();
                        role.setUsername(student.getUsername());
                        role.setRole("学生");
                        role.setLevel(0);
                        roleMapper.insert(role);
                    }else {
                        role.setUsername(student.getUsername());
                        role.setRole("学生");
                        role.setLevel(0);
                        roleMapper.updateById(role);
                    }

                    log.info("add Role:{}", JSON.toJSON(role));
                    insertNum += cur;
                    log.info("add student:{}", student);
                    //增加学校人数
                    schoolMapper.plusStudentNumbers(student.getSchoolId());
                    // 增加班级人数
                    QueryWrapper<SchoolClasses> queryWrapper1 = new QueryWrapper<>();
                    queryWrapper1.select("class_numbers").eq("school_id", student.getSchoolId()).eq("entrance_year", student.getEntranceYear())
                            .eq("period", student.getPeriod()).eq("grade", student.getGrade()).eq("classes", student.getClasses())
                            .eq("school_year", currentYear);
                    // 不考虑入学年份，只考虑学段年级
//                    queryWrapper1.select("class_numbers").eq("school_id", student.getSchoolId()).eq("period", student.getPeriod()).eq("grade", student.getGrade()).eq("classes", student.getClasses());
                    SchoolClasses schoolClasses = schoolClassesMapper.selectOne(queryWrapper1);
                    // 如果要加入的学生所在班级不存在，则在school_class新增记录；否则人数加1
                    if (schoolClasses == null) {
                        schoolClassesMapper.insert(new SchoolClasses(1L, student.getSchoolId(), student.getEntranceYear(), student.getPeriod(), student.getGrade(), student.getClasses(), 1, new Date(), new Date(), 0, currentYear, 0));
                    } else {
                        schoolClassesMapper.plusSchoolClassNumbers(student.getSchoolId(), student.getEntranceYear(), student.getPeriod(), student.getGrade(), student.getClasses(), currentYear);
                    }
                    // 查询学生所在班级是否存在正在进行的任务
                    QueryWrapper<TaskSchool> queryWrapper = new QueryWrapper<>();
                    queryWrapper.select("id", "task_id", "grade", "classes").eq("school_id", schoolId).eq("is_submit", 0)
                            .eq("school_year", currentYear);
                    List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(queryWrapper);
                    Set<Integer> taskIds = new HashSet<>();
                    List<Long> ids = new ArrayList<>();   // 为避免task_school中task_id重复，同时记录task_school表的id，用来实现该记录包括人数的增加
                    List<String> curGradesList;
                    List<String> curClassesList;
                    String grade = student.getPeriod() + student.getGrade();         // 年级由学段和年级组成
                    String classes = grade + student.getClasses();    // 班级由学段年级班级组成
                    for (TaskSchool taskSchool : taskSchoolList) {
                        if (!org.apache.commons.lang3.StringUtils.isBlank(taskSchool.getGrade())) {   // 如果年级列表不为空，则判断后继续检查班级列表
                            curGradesList = Arrays.asList(taskSchool.getGrade().split(","));
                            if (curGradesList.contains(grade)) {   // 如果年级列表包含当前年级，则继续判断班级
                                JSONObject jsonObject1 = (JSONObject) JSONObject.toJSON(taskSchool);
                                if (jsonObject1.containsKey("classes") && jsonObject1.get("classes") != null && !jsonObject1.getString("classes").equals("")) {  // 如果班级列表不为空，则判断是否包含当前班级
                                    curClassesList = Arrays.asList(jsonObject1.getString("classes").split(","));
                                    if (curClassesList.contains(classes)) {     // 如果班级列表包含当前班级，则将任务id加入任务列表
                                        taskIds.add(taskSchool.getTaskId());
                                        ids.add(taskSchool.getId());
                                    }
                                }else {   // 如果班级列表为空，则为全部包含，则将任务id加入任务列表
                                    taskIds.add(taskSchool.getTaskId());
                                    ids.add(taskSchool.getId());
                                }
                            }
                        }else { // 如果年级列表为空，则为全部包含，则将任务id加入任务列表
                            taskIds.add(taskSchool.getTaskId());
                            ids.add(taskSchool.getId());
                        }
                    }
                    // 如果taskIds不为空，则增加task任务的参与人数
                    for (Integer taskId : taskIds) {
                        taskMapper.updateNumbersById(taskId);
                    }
                    // 如果ids不为空，则增加task_school的参与人数
                    for (Long id : ids) {
                        taskSchoolMapper.updateNumbersById(id);
                    }
                }
            }
            // 学校增加人数
//            schoolMapper.plusBatchStudentNumbers(schoolId, insertNum);
            if (count != insertNum) {
                log.info("{} students add fail:", count - insertNum);
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE, errStudents);
            }else {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
            }
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }
}
