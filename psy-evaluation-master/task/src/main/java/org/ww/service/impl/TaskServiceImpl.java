package org.ww.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.assertj.core.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.domain.Manager;
import org.ww.domain.MhtWarning;
import org.ww.domain.Module;
import org.ww.domain.Options;
import org.ww.domain.Questions;
import org.ww.domain.Role;
import org.ww.domain.Scale;
import org.ww.domain.School;
import org.ww.domain.SchoolClasses;
import org.ww.domain.SchoolRole;
import org.ww.domain.Students;
import org.ww.domain.Task;
import org.ww.domain.TaskSchool;
import org.ww.domain.TaskUser;
import org.ww.domain.Teachers;
import org.ww.domain.UnitNumber;
import org.ww.domain.User;
import org.ww.domain.Warning;
import org.ww.dto.TaskDto;
import org.ww.dto.TaskResetDto;
import org.ww.exception.BusinessException;
import org.ww.mapper.ManagerMapper;
import org.ww.mapper.MhtWarningMapper;
import org.ww.mapper.ModuleMapper;
import org.ww.mapper.OptionsMapper;
import org.ww.mapper.QuestionsMapper;
import org.ww.mapper.RoleMapper;
import org.ww.mapper.ScaleMapper;
import org.ww.mapper.SchoolClassesMapper;
import org.ww.mapper.SchoolMapper;
import org.ww.mapper.SchoolRoleMapper;
import org.ww.mapper.StudentsMapper;
import org.ww.mapper.TaskMapper;
import org.ww.mapper.TaskSchoolMapper;
import org.ww.mapper.TaskUserMapper;
import org.ww.mapper.TeachersMapper;
import org.ww.mapper.UnitNumberMapper;
import org.ww.mapper.UserMapper;
import org.ww.mapper.WarningMapper;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.DubboService.Provider.PsyFileService;
import org.ww.service.TaskService;
import org.ww.utils.SchoolYearUtils;

/**
 * @Author 13096
 * @Date 2022/2/11 9:46
 * @Version 1.0
 */
@Slf4j
@Service
@Controller
@DubboService
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Resource
    private SchoolClassesMapper schoolClassesMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private TaskSchoolMapper taskSchoolMapper;

    @Resource
    private TaskUserMapper taskUserMapper;

    @Resource
    private ScaleMapper scaleMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private QuestionsMapper questionsMapper;

    @Resource
    private OptionsMapper optionsMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private StudentsMapper studentsMapper;

    @Resource
    private UnitNumberMapper unitNumberMapper;

    @Resource
    private MhtWarningMapper mhtWarningMapper;

    @Resource
    private WarningMapper warningMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TeachersMapper teachersMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private SchoolRoleMapper schoolRoleMapper;

    @DubboReference
    private PsyFileService psyFileService;

    private final SimpleDateFormat simpleDateFormat_YMDHMS = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    private final SimpleDateFormat simpleDateFormat_YMD = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 分页函数
     *
     * @param currentPage 当前页数
     * @param pageSize    每一页的数据条数
     * @param list        要进行分页的数据列表
     * @return 当前页要展示的数据
     */
    public Page getPages(Integer currentPage, Integer pageSize, List list) {
        Page page = new Page();
        int size = list.size();

        if (size == 0) {
            return null;
        }

        if (pageSize > size) {
            pageSize = size;
        }

        // 求出最大页数，防止currentPage越界
        int maxPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;

        if (currentPage > maxPage) {
            currentPage = maxPage;
        }

        // 当前页第一条数据的下标
        int curIdx = currentPage > 1 ? (currentPage - 1) * pageSize : 0;

        List pageList = new ArrayList();

        // 将当前页的数据放进pageList
        for (int i = 0; i < pageSize && curIdx + i < size; i++) {
            pageList.add(list.get(curIdx + i));
        }

        page.setCurrent(currentPage).setSize(pageSize).setTotal(list.size()).setRecords(pageList);
        return page;
    }


    @Resource
    private ManagerMapper managerMapper;


    @Override
    public Wrapper<String> addTask(TaskDto taskDto) {

        String currentYear = "" + SchoolYearUtils.currentSchoolYear();

        try {
            int allNumbers = 0;
            // 暂时写入task，以获取task_id
            Task task = new Task();
            BeanUtils.copyProperties(taskDto, task);
            task.setNumbers(allNumbers);  //暂设为0，后面再update
            Date nowDate = new Date();
            Date startDate = simpleDateFormat_YMDHMS.parse(simpleDateFormat_YMD.format(task.getStartDate()) + " 00:00:00");
            Date endDate = simpleDateFormat_YMDHMS.parse(simpleDateFormat_YMD.format(task.getEndDate()) + " 23:59:59");
            if (nowDate.before(startDate)) {
                task.setState(0);
            }
            if (nowDate.after(startDate) && nowDate.before(endDate)) {
                task.setState(1);
            }
            if (nowDate.after(endDate)) {
                task.setState(2);
            }
            task.setCompleteNumbers(0);//完成人数为0
            task.setCreateTime(new Date());
            task.setUpdateTime(new Date());
            // 根据下发人填充下发单位  并判定是否能下发到班级
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("username").eq("id", taskDto.getIssuedId());
            String issuedUsername = userMapper.selectOne(userQueryWrapper).getUsername();
            // 查询并组织下发单位
            QueryWrapper<Manager> managerQueryWrapper = new QueryWrapper<>();
            managerQueryWrapper.select("province", "city", "county", "school").eq("username", issuedUsername);
            managerQueryWrapper.orderByDesc("id").last("limit 1");
            Manager manager = managerMapper.selectOne(managerQueryWrapper);
            List<String> periods_grades_classes = new ArrayList<>();
            List<String> periods_grades;
//            List<String> classes = new ArrayList<>();
            if (manager.getSchool() != null && !manager.getSchool().equals("")) {
                periods_grades_classes = Arrays.asList(taskDto.getClasses().split(","));
                periods_grades = periods_grades_classes.stream().map(periods_grade_class -> periods_grade_class.substring(0, 5)).collect(Collectors.toList());
//                classes = periods_grades_classes.stream().map(periods_grade_class -> periods_grade_class.substring(5, periods_grade_class.length())).collect(Collectors.toList());
            } else {
                periods_grades = Arrays.asList(taskDto.getGrade().split(","));
            }
            task.setIssuedUnit(manager.getProvince() + (manager.getCity() == null ? "" : manager.getCity()) + (manager.getCounty() == null ? "" : manager.getCounty()) + (manager.getSchool() == null ? "" : manager.getSchool()));

            task.setArchived(0);
            task.setSchoolYear(currentYear);
            taskMapper.insert(task);    // 插入任务记录
            log.info("add Task: {}", JSON.toJSON(task));

            List<String> grades = periods_grades.stream().map(periods_grade -> periods_grade.substring(2, 5)).collect(Collectors.toList());
            List<String> periods = periods_grades.stream().map(periods_grade -> periods_grade.substring(0, 2)).collect(Collectors.toList());
            int[] schoolIds = Arrays.stream(taskDto.getSchoolId().split(",")).mapToInt(Integer::parseInt).toArray();

            if (manager.getSchool() != null && !manager.getSchool().equals("")) {
                int schoolId = schoolIds[0];

                List<Map<String, String>> list = new ArrayList<>();
                for (int i = 0; i < periods_grades.size(); i++) {
                    Map<String, String> tempMap = new HashMap<>();
                    tempMap.put("period", periods_grades_classes.get(i).substring(0, 2));
                    tempMap.put("grade", periods_grades_classes.get(i).substring(2, 5));
                    tempMap.put("class", periods_grades_classes.get(i).substring(5));
                    list.add(tempMap);
                }

                List<SchoolClasses> schoolClasses = schoolClassesMapper.getClassesByPGC(schoolId, list, currentYear);

//                QueryWrapper<SchoolClasses> schoolClassesQueryWrapper = new QueryWrapper<>();
//                schoolClassesQueryWrapper.select("classes", "class_numbers").eq("school_id", schoolId)
//                        .in("period", periods).in("grade", grades).in("classes", classes);
//                List<SchoolClasses> schoolClasses = schoolClassesMapper.selectList(schoolClassesQueryWrapper);
                int numbers = 0;  // 当前学校选中的人数
                for (SchoolClasses schoolClasses1 : schoolClasses) {
                    numbers += schoolClasses1.getClassNumbers();
                }
                allNumbers += numbers;
                // 查询学校所在区县等信息
                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.select("province", "city", "county").eq("id", schoolId);
                School school = schoolMapper.selectOne(schoolQueryWrapper);

                // 设置任务学校相关信息
                TaskSchool taskSchool = new TaskSchool();
                taskSchool.setSchoolId(schoolId);
                taskSchool.setProvince(school.getProvince());
                taskSchool.setCity(school.getCity());
                taskSchool.setCounty(school.getCounty());
                taskSchool.setGrade(taskDto.getGrade());
                taskSchool.setTaskId(task.getId().intValue());
                taskSchool.setClasses(taskDto.getClasses());
                taskSchool.setNumbers(numbers);
                taskSchool.setCompleteNumbers(0);
                taskSchool.setWarningNumbers(0);
                taskSchool.setValidNumbers(0);
                taskSchool.setIsSubmit(0);

                taskSchool.setArchived(0);
                taskSchool.setSchoolYear(currentYear);

                taskSchoolMapper.insert(taskSchool);  // 插入任务学校记录
                log.info("add TaskSchool: {}", JSON.toJSON(taskSchool));
            } else {
                for (int schoolId : schoolIds) {
                    List<Map<String, String>> list = new ArrayList<>();
                    for (int i = 0; i < periods_grades.size(); i++) {
                        Map<String, String> tempMap = new HashMap<>();
                        tempMap.put("period", periods_grades.get(i).substring(0, 2));
                        tempMap.put("grade", periods_grades.get(i).substring(2, 5));
                        list.add(tempMap);
                    }
//                    QueryWrapper<SchoolClasses> queryWrapper = new QueryWrapper<>();
//                    queryWrapper.select("classes", "class_numbers").eq("school_id", schoolId)
//                            .in("period", periods).in("grade", grades);
//                    List<SchoolClasses> schoolClasses = schoolClassesMapper.selectList(queryWrapper);
                    List<SchoolClasses> schoolClasses = schoolClassesMapper.getClassesByPG(schoolId, list, currentYear);
                    int numbers = 0;  // 当前学校选中的人数
                    for (SchoolClasses schoolClasses1 : schoolClasses) {
                        numbers += schoolClasses1.getClassNumbers();
                    }
                    allNumbers += numbers;
                    // 查询学校所在区县等信息
                    QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                    schoolQueryWrapper.select("province", "city", "county").eq("id", schoolId);
                    School school = schoolMapper.selectOne(schoolQueryWrapper);

                    // 设置任务学校相关信息
                    TaskSchool taskSchool = new TaskSchool();
                    taskSchool.setSchoolId(schoolId);
                    taskSchool.setProvince(school.getProvince());
                    taskSchool.setCity(school.getCity());
                    taskSchool.setCounty(school.getCounty());
                    taskSchool.setGrade(taskDto.getGrade());
                    taskSchool.setTaskId(task.getId().intValue());
                    taskSchool.setClasses(taskDto.getClasses());
                    taskSchool.setNumbers(numbers);
                    taskSchool.setCompleteNumbers(0);

                    taskSchool.setArchived(0);
                    taskSchool.setSchoolYear(currentYear);

                    taskSchoolMapper.insert(taskSchool);  // 插入任务学校记录
                    log.info("add TaskSchool: {}", JSON.toJSON(taskSchool));
                }
            }
            task.setNumbers(allNumbers);  // 更新numbers
            taskMapper.updateById(task);  // 更新任务记录
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> studentTaskList(JSONObject jsonObject) {
        try {
            List<Map<String, Object>> taskList = new ArrayList<>();
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer userId = jsonObject.getInteger("userId");
            String period = jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            String classes = jsonObject.getString("classes");
            if (!jsonObject.containsKey("period") || !jsonObject.containsKey("grade") || !jsonObject.containsKey("classes")) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, taskList);
            }
            QueryWrapper<TaskSchool> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("task_id", "grade", "classes").eq("school_id", schoolId).orderByDesc("id");
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(queryWrapper);
            log.info("schoolId :{} taskSchoolList:{}", schoolId, JSON.toJSONString(taskSchoolList));
            List<Integer> taskIds = new ArrayList<>();
            List<String> curGradesList;
            List<String> curClassesList;
            grade = period + grade;         // 年级由学段和年级组成
            classes = grade + classes;    // 班级由学段年级班级组成
            for (TaskSchool taskSchool : taskSchoolList) {
                JSONObject jsonObject1 = (JSONObject) JSONObject.toJSON(taskSchool);
                if (!taskSchool.getGrade().equals("")) {   // 如果年级列表不为空，则判断后继续检查班级列表
                    curGradesList = Arrays.asList(taskSchool.getGrade().split(","));
                    if (curGradesList.contains(grade)) {   // 如果年级列表包含当前年级，则继续判断班级
                        if (jsonObject1.containsKey("classes") && jsonObject1.get("classes") != null && !jsonObject1.getString("classes").equals("")) {    // 如果班级列表不为空，则判断是否包含当前班级
                            curClassesList = Arrays.asList(jsonObject1.getString("classes").split(","));
                            if (curClassesList.contains(classes)) {     // 如果班级列表包含当前班级，则将任务id加入任务列表
                                taskIds.add(taskSchool.getTaskId());
                            }
                        } else {   // 如果班级列表为空，则为全部包含，则将任务id加入任务列表
                            taskIds.add(taskSchool.getTaskId());
                        }
                    }
                } else { // 如果年级列表为空，则为全部包含，则将任务id加入任务列表
                    taskIds.add(taskSchool.getTaskId());
                }
            }
            log.info("task list:{}", JSON.toJSONString(taskIds));
            for (Integer taskId : taskIds) {
                Map<String, Object> taskInfo = new HashMap<>();
                // 获取任务信息
                Task task = taskMapper.getTaskStateInfoById(taskId);
                taskInfo.put("id", task.getId());
                taskInfo.put("name", task.getName());
                taskInfo.put("scaleId", task.getScaleId());
                taskInfo.put("startDate", task.getStartDate());
                taskInfo.put("endDate", task.getEndDate());
                // 获取已完成任务
                QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
                taskUserQueryWrapper.select("to_retest").eq("user_id", userId).eq("task_id", taskId).orderByDesc("submit_time").last("limit 1");
                TaskUser taskUser = taskUserMapper.selectOne(taskUserQueryWrapper);
                if (taskUser == null) {
                    taskInfo.put("state", 0);
                    taskInfo.put("retest", 0);
                } else {
                    if (taskUser.getToRetest() == 1) {
                        taskInfo.put("retest", 1);
                        taskInfo.put("state", 0);
                    } else {
                        taskInfo.put("retest", 0);
                        taskInfo.put("state", 1);
                    }
                }
                // 判断任务是否在时间内
//                int inTime = 1;     // 0 未开始；1 进行中；2 已结束
//                Date now = new Date();
//                if (now.after(task.getEndDate())) {
//                    inTime = 2;
//                } else if (now.before(task.getStartDate())) {
//                    inTime = 0;
//                }
                taskInfo.put("inTime", task.getState());
                taskList.add(taskInfo);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, taskList);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> taskResult(JSONObject jsonObject) {
        try {
            TaskUser taskUser = new TaskUser();

            taskUser.setArchived(0);
            taskUser.setSchoolYear("" + SchoolYearUtils.currentSchoolYear());

            Integer taskId = jsonObject.getInteger("taskId");
            Integer userId = jsonObject.getInteger("userId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            List answerList = jsonObject.getJSONArray("answerList");
            taskUser.setTaskId(taskId);
            taskUser.setUserId(userId);
            taskUser.setSchoolId(schoolId);
            taskUser.setSubmitTime(new Date());
            //获取量表
            Task task = taskMapper.selectById(taskId);
            Scale scale = scaleMapper.selectById(Integer.valueOf(task.getScaleId()));
            Integer type = scale.getType().intValue();
            taskUser.setType(type);

            StringBuffer record = new StringBuffer();
            if (type == 1) {
                Warning warning = new Warning();
                warning.setTaskId(taskId);
                warning.setUserId(userId);
                warning.setSchoolId(schoolId);

                // 将to_retest置为0
                taskUser.setToRetest(0);
                //时长判断
                Integer duration = jsonObject.getInteger("duration");
                taskUser.setDuration(duration);
                Integer valid = scale.getDuration().intValue() > duration ? 0 : 1;
                //获取量表模块
                QueryWrapper<Module> queryWrapperModule = new QueryWrapper<>();
                queryWrapperModule.eq("scale_id", scale.getId());
                List<Module> moduleList = moduleMapper.selectList(queryWrapperModule);
                //模块复杂度分析
                Integer totalId = null;
                Integer validId = null;
                Integer positiveId = null;

                StringBuffer result = new StringBuffer();
                StringBuffer warningList = new StringBuffer();

                //初始化各模块分数
                Map<Integer, Integer> score = new HashMap<>();
                ListIterator<Module> moduleListIterator = moduleList.listIterator();
                while (moduleListIterator.hasNext()) {
                    Module module = moduleListIterator.next();
                    if (module.getName().equals("总分")) {
                        totalId = module.getId().intValue();
                    }
                    if (module.getName().equals("测试可信性")) {
                        validId = module.getId().intValue();
                    }
                    if (module.getName().equals("阳性项目")) {
                        positiveId = module.getId().intValue();
                    }
                    score.put(module.getId().intValue(), 0);
                }
                //逐个计算量表结果
                ListIterator<Map> mapListIterator = answerList.listIterator();
                Integer positiveItem = 0;
                while (mapListIterator.hasNext()) {
                    Map answers = (Map) mapListIterator.next();
                    Integer questionId = (Integer) answers.get("id");
                    Integer optionId = (Integer) answers.get("answer");
                    record.append(questionId + ":" + optionId + ";");
                    //分数计算
                    scoreCompute(questionId, optionId, score, totalId, validId, positiveId);
                }
                //result构造及预警分析
                Integer scaleType = scale.getId().intValue();
                String scaleName = scale.getName();
                Integer warn = 0;
                Map<String, Object> tmp = new HashMap<>();
                for (Module module : moduleList) {
                    Integer moduleId = module.getId().intValue();
                    Integer curScoreThreshold = module.getScoreThreshold();
                    //预警分析
                    if (scaleName.equals("中小学生心理健康普查(MHT)")) {
                        Integer curScore = score.get(moduleId);
                        result.append(moduleId + ":" + curScore + ",");
                        if (!moduleId.equals(validId)) {
                            warningList.append(moduleId + ":");
                            if (curScore >= curScoreThreshold) {
                                warningList.append(1);
                                warn = 1;
                            } else {
                                warningList.append(0);
                            }
                            warningList.append(",");
                        } else {
                            if (curScore >= curScoreThreshold) {
                                valid = 0;
                            } else {
                                valid = Math.min(1, valid);
                            }
                        }
                    } else if (scaleName.equals("抑郁自评量表(SDS)") ||
                            scaleName.equals("焦虑自评量表(SAS)")) {
                        Integer curScore = (int) (score.get(moduleId) * 1.25);
                        result.append(moduleId + ":" + curScore + ",");
                        warningList.append(moduleId + ":");
                        if (curScore >= curScoreThreshold) {
                            warningList.append(1);
                            warn = 1;
                        } else {
                            warningList.append(0);
                        }
                        warningList.append(",");
                    } else if (scaleName.equals("Sarason考试焦虑量表(TAS)")) {
                        Integer curScore = (int) score.get(moduleId);
                        result.append(moduleId + ":" + curScore + ",");
                        warningList.append(moduleId + ":");
                        if (curScore >= curScoreThreshold) {
                            warningList.append(1);
                            warn = 1;
                        } else {
                            warningList.append(0);
                        }
                        warningList.append(",");
                    } else if (scaleName.equals("90项症状自查量表(SCL-90)")) {
                        Integer curScore = (int) score.get(moduleId);
                        result.append(moduleId + ":" + curScore + ",");
                        warningList.append(moduleId + ":");
                        if (curScore >= curScoreThreshold) {
                            warningList.append(1);
                            warn = 1;
                        } else {
                            warningList.append(0);
                        }
                        warningList.append(",");
                    }

                    else if (scaleName.equals("一般自我效能感量表")) {  //一般自我效能感量表    无预警 无标准分
                        Integer curScore = (int) score.get(moduleId);
                        result.append(moduleId + ":" + curScore + ",");
                        warningList.append(moduleId + ":0,");   //非预警型量表不会触发预警
                    }
                    else if (scaleName.equals("中文网络成瘾量表（CIAS-R）")) {  //中文网络成瘾量表（CIAS-R） 有预警 无标准分
                        Integer curScore = (int) score.get(moduleId);
                        result.append(moduleId + ":" + curScore + ",");
                        warningList.append(moduleId + ":");
                        if (curScore >= curScoreThreshold) {
                            warningList.append(1);
                            warn = 1;
                        } else {
                            warningList.append(0);
                        }
                        warningList.append(",");
                    }
                    else if (scaleName.equals("卡特尔16PF性格测验")) {//卡特尔16PF性格测验    无预警 有标准分
                        Integer curScore = (int) score.get(moduleId);
                        Integer standardScore = getStandardScore_16pf(module.getName(), curScore);
                        result.append(moduleId + ":" + standardScore + ",");
                        warningList.append(moduleId + ":0,");   //非预警型量表不会触发预警
                    }
                    else if (scaleName.equals("广泛性焦虑量表(GAD-7)")) {  //广泛性焦虑量表(GAD-7)   有预警 无标准分
                        Integer curScore = (int) score.get(moduleId);
                        result.append(moduleId + ":" + curScore + ",");
                        warningList.append(moduleId + ":");
                        if (curScore >= curScoreThreshold) {
                            warningList.append(1);
                            warn = 1;
                        } else {
                            warningList.append(0);
                        }
                        warningList.append(",");
                    }
                    else if (scaleName.equals("教师职业倦怠量表MBI-ES")) {//教师职业倦怠量表MBI-ES     无预警 有标准分
                        Integer curScore = (int) score.get(moduleId);
                        double standardScore = getStandardScore_MBIES(module.getName(), curScore);
                        result.append(moduleId + ":" + standardScore + ",");
                        warningList.append(moduleId + ":0,");   //非预警型量表不会触发预警
                    }
                    else if (scaleName.equals("艾森克人格问卷(EPQ)")) {//艾森克人格问卷(EPQ)成人版    无预警 有标准分
                        QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                        User user = userMapper.selectById(userId);
                        studentsQueryWrapper.eq("username", user.getUsername());
                        studentsQueryWrapper.eq("school_year", taskUser.getSchoolYear());
                        studentsQueryWrapper.orderByDesc("id").last("limit 1");

                        Students students = studentsMapper.selectOne(studentsQueryWrapper);
                        if (students != null) {
                            Integer curScore = (int) score.get(moduleId);
                            String moduleName = module.getName();
                            //标准分计算
                            double standardScore = getStandardScore_EPQ_au(curScore, getAge(students.getIdNumber()), students.getSex(), moduleName);
                            result.append(moduleId + ":" + standardScore + ",");
                            warningList.append(moduleId + ":0,");   //非预警型量表不会触发预警
                        }
                    }
                    else if (scaleName.equals("艾森克人格问卷【儿童版】")) {//艾森克人格问卷(EPQ)儿童版    无预警 有标准分
                        QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                        User user = userMapper.selectById(userId);
                        studentsQueryWrapper.eq("username", user.getUsername());
                        studentsQueryWrapper.eq("school_year", taskUser.getSchoolYear());
                        studentsQueryWrapper.orderByDesc("id").last("limit 1");

                        Students students = studentsMapper.selectOne(studentsQueryWrapper);
                        if (students != null) {
                            Integer curScore = (int) score.get(moduleId);
                            String moduleName = module.getName();
                            //标准分计算
                            double standardScore = getStandardScore_EPQ_ch(curScore, getAge(students.getIdNumber()), students.getSex(), moduleName);
                            result.append(moduleId + ":" + standardScore + ",");
                            warningList.append(moduleId + ":0,");   //非预警型量表不会触发预警
                        }
                    }
                    else if (scaleName.equals("霍兰德职业兴趣测试量表")) {//霍兰德职业兴趣测试量表    无预警 无标准分
                        Integer curScore = (int) score.get(moduleId);
                        result.append(moduleId + ":" + curScore + ",");
                        warningList.append(moduleId + ":0,");   //非预警型量表不会触发预警
                    }
                    else if (scaleName.equals("中学生心理健康量表-MSSMHS")) {  //中学生心理健康量表（MSSMSH）   有预警 有标准分
                        Integer curScore = (int) score.get(moduleId);
                        double standardScore = getStandardScore_MSSMSH(module.getName(), curScore);
                        result.append(moduleId + ":" + standardScore + ",");
                        warningList.append(moduleId + ":");
                        if (standardScore >= curScoreThreshold) {
                            warningList.append(1);
                            warn = 1;
                        } else {
                            warningList.append(0);
                        }
                        warningList.append(",");
                    }
                }

                //valid、result、record、warn
                taskUser.setRecord(record.toString());
                taskUser.setResult(result.toString());
                taskUser.setValid(valid);
                taskUser.setWarning(warn);
                warning.setWarning(warningList.toString());
                // 查询task_user是否有记录，没有的话在下边更新进度人数
                QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
                taskUserQueryWrapper.eq("task_id", taskId).eq("user_id", userId);
                int taskUserCount = taskUserMapper.selectCount(taskUserQueryWrapper);
                if (taskUserCount == 0) {
                    Task taskTmp = new Task();
                    taskTmp.setCompleteNumbers(task.getCompleteNumbers() + 1);
                    taskTmp.setId(taskId.longValue());
                    taskMapper.updateById(taskTmp);
                    taskSchoolMapper.updateComNumbers(taskId, schoolId);
                }
                //若有效，更新预警人数、warning和预警表
                if (valid == 1) {
                    //查询task_user是否已存在有效记录，没有的话更新有效人数
                    taskUserQueryWrapper.eq("valid", 1);
                    if (taskUserMapper.selectCount(taskUserQueryWrapper) == 0) {
                        taskSchoolMapper.updateValidNumbers(taskId, schoolId);
                    }
                    //查询warning是否有记录，没有的话添加记录并更新预警人数，否则更新记录
                    QueryWrapper<Warning> warningQueryWrapper = new QueryWrapper<>();
                    warningQueryWrapper.eq("task_id", taskId).eq("user_id", userId);
                    int warningCount = warningMapper.selectCount(warningQueryWrapper);
                    if (warningCount == 0) {
                        if (warn == 1) {
                            warningMapper.insert(warning);
                            Task taskTmp = new Task();
                            taskTmp.setWarningNumbers(task.getWarningNumbers() + 1);
                            taskTmp.setId(taskId.longValue());
                            taskMapper.updateById(taskTmp);
                            taskSchoolMapper.updateWarningNumbers(taskId, schoolId);
                            //若为学生添加预警库记录
                            QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                            User user = userMapper.selectById(userId);
                            studentsQueryWrapper.eq("username", user.getUsername());
                            studentsQueryWrapper.eq("school_year", taskUser.getSchoolYear());
                            studentsQueryWrapper.orderByDesc("id").last("limit 1");

                            Students students = studentsMapper.selectOne(studentsQueryWrapper);
                            if (students != null) {
                                psyFileService.createIntervationSchemeItem(PsyIntervationFileDO.builder().taskId(taskId.longValue()).studentId(students.getId()).userId(userId.longValue()).build());
                            }
                        }
                    } else {
                        warning.setId(warningMapper.selectOne(warningQueryWrapper).getId());
                        warningMapper.updateById(warning);
                    }
                }
                // task_user中插入记录
                taskUserMapper.insert(taskUser);
            } else if (type == 2) {
                ListIterator<Map> mapListIterator = answerList.listIterator();
                while (mapListIterator.hasNext()) {
                    Map answers = (Map) mapListIterator.next();
                    Integer questionId = (Integer) answers.get("id");
                    String answer = answers.get("answer").toString();
                    record.append(questionId + ":" + answer + ";");
                }
                taskUser.setRecord(record.toString());
                taskUserMapper.insert(taskUser);
                taskSchoolMapper.updateValidNumbers(taskId, schoolId);
                taskSchoolMapper.updateComNumbers(taskId, schoolId);
                Task taskTmp = new Task();
                taskTmp.setCompleteNumbers(task.getCompleteNumbers() + 1);
                taskTmp.setId(taskId.longValue());
                taskMapper.updateById(taskTmp);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    /**
     * 各模块分数计算
     *
     * @param question 题目id
     * @param answer   答案id
     * @param score    各模块分数记录Map
     * @param totalId
     */
    public void scoreCompute(Integer question, Integer answer, Map score, Integer totalId, Integer validId, Integer positiveId) {
        Integer module = questionsMapper.selectById(question).getModuleId();
        Integer optionScore = optionsMapper.selectById(answer).getScore();
        if (module != null && module != totalId) {
            Integer newScore = (Integer) score.get(module) + optionScore;
            score.replace(module, newScore);
        }
        if (totalId != null && (validId == null || module != validId.intValue())) {
            score.replace(totalId, (Integer) score.get(totalId) + optionScore);
            if (positiveId != null) {
                if (optionScore >= 1) {
                    score.replace(positiveId, (Integer) score.get(positiveId) + 1);
                }
            }
        }
    }


    //EPQ标准分计算
    //对于16岁以下人群按照16——19岁标准执行
    public double getStandardScore_EPQ_au(Integer curScore, Integer age, String sex, String moduleName) {
        double score = 0;

        if ("男".equals(sex)) {
            if (moduleName.startsWith("P")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 6.65) / 4.36;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 5.96) / 2.84;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 5.85) / 3.32;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 5.67) / 2.45;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 6.05) / 3.31;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 4.4) / 2.33;
                }
            } else if (moduleName.startsWith("E")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 11.55) / 3.99;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 10.63) / 4.44;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 9.92) / 3.9;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 9.65) / 4.77;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 8.63) / 3.69;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 9.8) / 4.46;
                }
            } else if (moduleName.startsWith("N")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 12.31) / 4;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 11.26) / 4.26;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 12.02) / 4.56;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 10.12) / 5.04;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 11.07) / 6.31;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 8.92) / 4.59;
                }
            } else if (moduleName.startsWith("L")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 11.76) / 4.18;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 12.17) / 3.57;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 12.39) / 3.93;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 13.55) / 3.56;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 13.93) / 3.8;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 15.35) / 2.73;
                }
            }

        } else if ("女".equals(sex)) {
            if (moduleName.startsWith("P")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 5.06) / 2.69;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 4.92) / 2.95;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 4.8) / 3.33;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 4.03) / 2.4;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 4.05) / 2.9;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 3.82) / 2.41;
                }
            } else if (moduleName.startsWith("E")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 10.23) / 4.09;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 8.65) / 4.49;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 8.97) / 4.45;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 8.37) / 4.35;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 9.22) / 4.21;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 9.34) / 4.31;
                }
            } else if (moduleName.startsWith("N")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 12.28) / 4.92;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 13.06) / 4.42;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 12.02) / 5.05;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 12.15) / 5.73;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 11.09) / 5.21;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 11.36) / 5.05;
                }
            } else if (moduleName.startsWith("L")) {
                if (age <= 19) {
                    score = 50 + 10 * (curScore - 12.85) / 4.08;
                } else if (age >= 20 && age <= 29) {
                    score = 50 + 10 * (curScore - 12.39) / 3.63;
                } else if (age >= 30 && age <= 39) {
                    score = 50 + 10 * (curScore - 14.17) / 3.65;
                } else if (age >= 40 && age <= 49) {
                    score = 50 + 10 * (curScore - 15.41) / 3.22;
                } else if (age >= 50 && age <= 59) {
                    score = 50 + 10 * (curScore - 14.09) / 4.03;
                } else if (age >= 60) {
                    score = 50 + 10 * (curScore - 15.95) / 3.65;
                }
            }
        }
        DecimalFormat df = new DecimalFormat("#.0");
        return Double.valueOf(df.format(score));
    }

    public double getStandardScore_EPQ_ch(Integer curScore, Integer age, String sex, String moduleName) {
        return 50;
    }

    //周岁计算（使用身份证号码和当前时间）
    public Integer getAge(String idNumber) {
        Integer age = null;
        if (idNumber.length() != 18) return null;

        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayNow = cal.get(Calendar.DATE);

        int year = Integer.valueOf(idNumber.substring(6, 10));
        int month = Integer.valueOf(idNumber.substring(10, 12));
        int day = Integer.valueOf(idNumber.substring(12, 14));

        if ((month < monthNow) || (month == monthNow && day <= dayNow)) {
            age = yearNow - year;
        } else {
            age = yearNow - year - 1;
        }
        return age;
    }

    //16pf标准分计算
    public Integer getStandardScore_16pf(String moduleName, Integer score) {
        if ("A".equals(moduleName)) {
            if (score >= 0 && score <= 1)           return 1;
            else if (score >= 2 && score <= 3)      return 2;
            else if (score >= 4 && score <= 5)      return 3;
            else if (score == 6)                    return 4;
            else if (score >= 7 && score <= 8)      return 5;
            else if (score >= 9 && score <= 11)     return 6;
            else if (score >= 12 && score <= 13)    return 7;
            else if (score == 14)                   return 8;
            else if (score >= 15 && score <= 16)    return 9;
            else if (score >= 17 && score <= 20)    return 10;
        }
        else if ("B".equals(moduleName)) {
            if (score >= 0 && score <= 3)           return 1;
            else if (score == 4)                    return 2;
            else if (score == 5)                    return 3;
            else if (score == 6)                    return 4;
            else if (score == 7)                    return 5;
            else if (score == 8)                    return 6;
            else if (score == 9)                    return 7;
            else if (score == 10)                   return 8;
            else if (score == 11)                   return 9;
            else if (score >= 12 && score <= 13)    return 10;
        }
        else if ("C".equals(moduleName)) {
            if (score >= 0 && score <= 5)           return 1;
            else if (score >= 6 && score <= 7)      return 2;
            else if (score >= 8 && score <= 9)      return 3;
            else if (score >= 10 && score <= 11)    return 4;
            else if (score >= 12 && score <= 13)    return 5;
            else if (score >= 14 && score <= 16)    return 6;
            else if (score >= 17 && score <= 18)    return 7;
            else if (score >= 19 && score <= 20)    return 8;
            else if (score >= 21 && score <= 22)    return 9;
            else if (score >= 23 && score <= 26)    return 10;
        }
        else if ("E".equals(moduleName)) {
            if (score >= 0 && score <= 2)           return 1;
            else if (score >= 3 && score <= 4)      return 2;
            else if (score == 5)                    return 3;
            else if (score >= 6 && score <= 7)      return 4;
            else if (score >= 8 && score <= 9)      return 5;
            else if (score >= 10 && score <= 12)    return 6;
            else if (score >= 13 && score <= 14)    return 7;
            else if (score >= 15 && score <= 16)    return 8;
            else if (score >= 17 && score <= 18)    return 9;
            else if (score >= 19 && score <= 26)    return 10;
        }
        else if ("F".equals(moduleName)) {
            if (score >= 0 && score <= 3)           return 1;
            else if (score == 4)                    return 2;
            else if (score >= 5 && score <= 6)      return 3;
            else if (score == 7)                    return 4;
            else if (score >= 8 && score <= 9)      return 5;
            else if (score >= 10 && score <= 12)    return 6;
            else if (score >= 13 && score <= 14)    return 7;
            else if (score >= 15 && score <= 16)    return 8;
            else if (score >= 17 && score <= 18)    return 9;
            else if (score >= 19 && score <= 26)    return 10;
        }
        else if ("G".equals(moduleName)) {
            if (score >= 0 && score <= 5)           return 1;
            else if (score >= 6 && score <= 7)      return 2;
            else if (score >= 8 && score <= 9)      return 3;
            else if (score == 10)                   return 4;
            else if (score >= 11 && score <= 12)    return 5;
            else if (score >= 13 && score <= 14)    return 6;
            else if (score >= 15 && score <= 16)    return 7;
            else if (score == 17)                   return 8;
            else if (score == 18)                   return 9;
            else if (score >= 19 && score <= 20)    return 10;
        }
        else if ("H".equals(moduleName)) {
            if (score >= 0 && score <= 1)           return 1;
            else if (score == 2)                    return 2;
            else if (score == 3)                    return 3;
            else if (score >= 4 && score <= 6)      return 4;
            else if (score >= 7 && score <= 8)      return 5;
            else if (score >= 9 && score <= 11)     return 6;
            else if (score >= 12 && score <= 14)    return 7;
            else if (score >= 15 && score <= 16)    return 8;
            else if (score >= 17 && score <= 19)    return 9;
            else if (score >= 20 && score <= 26)    return 10;
        }
        else if ("I".equals(moduleName)) {
            if (score >= 0 && score <= 5)           return 1;
            else if (score == 6)                    return 2;
            else if (score >= 7 && score <= 8)      return 3;
            else if (score == 9)                    return 4;
            else if (score >= 10 && score <= 11)    return 5;
            else if (score >= 12 && score <= 13)    return 6;
            else if (score == 14)                   return 7;
            else if (score >= 15 && score <= 16)    return 8;
            else if (score == 17)                   return 9;
            else if (score >= 18 && score <= 19)    return 10;
        }
        else if ("L".equals(moduleName)) {
            if (score >= 0 && score <= 3)           return 1;
            else if (score >= 4 && score <= 5)      return 2;
            else if (score == 6)                    return 3;
            else if (score >= 7 && score <= 8)      return 4;
            else if (score >= 9 && score <= 10)     return 5;
            else if (score >= 11 && score <= 12)    return 6;
            else if (score == 13)                   return 7;
            else if (score >= 14 && score <= 15)    return 8;
            else if (score == 16)                   return 9;
            else if (score >= 17 && score <= 20)    return 10;
        }
        else if ("M".equals(moduleName)) {
            if (score >= 0 && score <= 5)           return 1;
            else if (score >= 6 && score <= 7)      return 2;
            else if (score >= 8 && score <= 9)      return 3;
            else if (score >= 10 && score <= 11)    return 4;
            else if (score >= 12 && score <= 13)    return 5;
            else if (score >= 14 && score <= 15)    return 6;
            else if (score >= 16 && score <= 17)    return 7;
            else if (score >= 18 && score <= 19)    return 8;
            else if (score == 20)                   return 9;
            else if (score >= 21 && score <= 26)    return 10;
        }
        else if ("N".equals(moduleName)) {
            if (score >= 0 && score <= 2)           return 1;
            else if (score == 3)                    return 2;
            else if (score == 4)                    return 3;
            else if (score >= 5 && score <= 6)      return 4;
            else if (score >= 7 && score <= 8)      return 5;
            else if (score >= 9 && score <= 10)     return 6;
            else if (score == 11)                   return 7;
            else if (score >= 12 && score <= 13)    return 8;
            else if (score == 14)                   return 9;
            else if (score >= 15 && score <= 20)    return 10;
        }
        else if ("O".equals(moduleName)) {
            if (score >= 0 && score <= 2)           return 1;
            else if (score >= 3 && score <= 4)      return 2;
            else if (score >= 5 && score <= 6)      return 3;
            else if (score >= 7 && score <= 8)      return 4;
            else if (score >= 9 && score <= 10)     return 5;
            else if (score >= 11 && score <= 12)    return 6;
            else if (score >= 13 && score <= 14)    return 7;
            else if (score >= 15 && score <= 16)    return 8;
            else if (score >= 17 && score <= 18)    return 9;
            else if (score >= 19 && score <= 26)    return 10;
        }
        else if ("Q1".equals(moduleName)) {
            if (score >= 0 && score <= 4)           return 1;
            else if (score == 5)                    return 2;
            else if (score >= 6 && score <= 7)      return 3;
            else if (score == 8)                    return 4;
            else if (score >= 9 && score <= 10)     return 5;
            else if (score >= 11 && score <= 12)    return 6;
            else if (score == 13)                   return 7;
            else if (score == 14)                   return 8;
            else if (score == 15)                   return 9;
            else if (score >= 16 && score <= 20)    return 10;
        }
        else if ("Q2".equals(moduleName)) {
            if (score >= 0 && score <= 5)           return 1;
            else if (score >= 6 && score <= 7)      return 2;
            else if (score == 8)                    return 3;
            else if (score >= 9 && score <= 10)     return 4;
            else if (score >= 11 && score <= 12)    return 5;
            else if (score >= 13 && score <= 14)    return 6;
            else if (score == 15)                   return 7;
            else if (score >= 16 && score <= 17)    return 8;
            else if (score == 18)                   return 9;
            else if (score >= 19 && score <= 20)    return 10;
        }
        else if ("Q3".equals(moduleName)) {
            if (score >= 0 && score <= 4)           return 1;
            else if (score >= 5 && score <= 6)      return 2;
            else if (score >= 7 && score <= 8)      return 3;
            else if (score >= 9 && score <= 10)     return 4;
            else if (score >= 11 && score <= 12)    return 5;
            else if (score >= 13 && score <= 14)    return 6;
            else if (score == 15)                   return 7;
            else if (score >= 16 && score <= 17)    return 8;
            else if (score == 18)                   return 9;
            else if (score >= 19 && score <= 20)    return 10;
        }
        else if ("Q4".equals(moduleName)) {
            if (score >= 0 && score <= 2)           return 1;
            else if (score >= 3 && score <= 4)      return 2;
            else if (score >= 5 && score <= 6)      return 3;
            else if (score >= 7 && score <= 8)      return 4;
            else if (score >= 9 && score <= 11)     return 5;
            else if (score >= 12 && score <= 14)    return 6;
            else if (score >= 15 && score <= 16)    return 7;
            else if (score >= 17 && score <= 19)    return 8;
            else if (score >= 20 && score <= 21)    return 9;
            else if (score >= 22 && score <= 26)    return 10;
        }
        return 0;
    }

    public double getStandardScore_MBIES(String moduleName, Integer score) {
        double standardScore = 0;

        if ("情绪衰竭".equals(moduleName)) {
            standardScore = score / 9;
        }
        else if ("去个性化".equals(moduleName)) {
            standardScore = score / 5;
        }
        else if ("低成就感".equals(moduleName)) {
            standardScore = score / 8;
        }

        DecimalFormat df = new DecimalFormat("#.0");
        return Double.valueOf(df.format(standardScore));
    }

    public double getStandardScore_MSSMSH(String moduleName, Integer score) {
        double standardScore = 0;

        if ("总分".equals(moduleName)) {
            standardScore = score / 60;
        }
        else  {
            standardScore = score / 6;
        }

        DecimalFormat df = new DecimalFormat("#.0");
        return Double.valueOf(df.format(standardScore));
    }

    @Override
    public Wrapper<Map<String, Object>> personalReport(JSONObject jsonObject) {
        try {
            Integer userId = jsonObject.getInteger("userId");
            Integer taskId = jsonObject.getInteger("taskId");

            //report个人报告相关信息
            Map<String, Object> report = new HashMap<>();

            //获取量表
            Task task = taskMapper.selectById(taskId);
            Scale scale = scaleMapper.selectById(Integer.valueOf(task.getScaleId()));
            report.put("量表介绍", scale.getIntroduction());

            //获取量表模块
            QueryWrapper<Module> queryWrapperModule = new QueryWrapper<>();
            queryWrapperModule.eq("scale_id", scale.getId());
            List<Module> moduleList = moduleMapper.selectList(queryWrapperModule);
            Integer positiveItem = null;

            //获取量表结果
            QueryWrapper<TaskUser> queryWrapperTaskUser = new QueryWrapper<>();
            queryWrapperTaskUser.eq("task_id", taskId)
                    .and(userQueryWrapper -> userQueryWrapper.eq("user_id", userId))
                    .orderByDesc("submit_time")
                    .last("limit 1");
            TaskUser taskUser = taskUserMapper.selectOne(queryWrapperTaskUser);

            // add //
            if (scale.getName().contains("EPQ") || scale.getName().contains("儿童版") ||
                    scale.getName().contains("MSSMHS") || scale.getName().contains("MBI-ES")) {
                //if(score.matches("[+-]?[0-9]+(\\\\.[0-9]+)?")){
                //量表结果处理
                Map<Integer, Double> result = new HashMap<>();
                String[] resultArray = taskUser.getResult().split(",");
                for (String moduleResult : resultArray) {
                    String[] moduleScore = moduleResult.split(":");
                    result.put(Integer.valueOf(moduleScore[0]), Double.valueOf(moduleScore[1]));
                }

                ListIterator<Module> mapListIterator = moduleList.listIterator();
                while (mapListIterator.hasNext()) {
                    Module module = mapListIterator.next();
                    String moduleName = module.getName();
                    Integer moduleId = module.getId().intValue();
                    Double curScore = result.get(moduleId);
                    //mean处理
                    String[] section = module.getMean().split(";");
                    String[] scoreSection = new String[3];
                    for (int i = section.length - 1; i >= 0; i--) {
                        if (section[i].contains(":")) {
                            if (curScore >= Double.valueOf(section[i].split(":")[0])) {
                                scoreSection = section[i].split("/");
                                break;
                            }
                        } else {
                            if (curScore >= Double.valueOf(section[i].split("/")[0])) {
                                scoreSection = section[i].split("/");
                                break;
                            }
                        }
                    }
                    //分数，解释及建议
                    Map<String, Object> specific = new HashMap<>();
                    specific.put("score", curScore);
                    specific.put("explain", scoreSection[1]);
                    specific.put("moduleId", moduleId);
                    report.put(moduleName, specific);
                }
                if (taskUser.getValid() == 1) {
                    report.put("测试可信性", "本次测评结果有效");
                } else {
                    report.put("测试可信性", "本次测评结果无效，需要在适当时候重新进行测验。");
                }
            } else {
                //量表结果处理
                Map<Integer, Integer> result = new HashMap<>();
                String[] resultArray = taskUser.getResult().split(",");
                for (String moduleResult : resultArray) {
                    String[] moduleScore = moduleResult.split(":");
                    result.put(Integer.valueOf(moduleScore[0]), Integer.valueOf(moduleScore[1]));
                }

                Iterator<Module> mapListIterator = moduleList.iterator();
                Integer size = moduleList.size();
                while (mapListIterator.hasNext()) {
                    Map<String, Object> specific = new HashMap<>();
                    Module module = mapListIterator.next();
                    String moduleName = module.getName();

                    report.put(moduleName, specific);

                    if (moduleName.equals("测试可信性")) {
                        continue;
                    }
                    Integer moduleId = module.getId().intValue();
                    Integer curScore = result.get(moduleId);
                    if (moduleName.equals("阳性项目")) {
                        positiveItem = result.get(module.getId().intValue());
                    }
                    //mean处理
                    String[] section = module.getMean().split(";");
                    String[] scoreSection = new String[3];
                    int index = 0;
                    for (int i = section.length - 1; i >= 0 && !section[i].equals(""); i--) {
                        if (curScore >= Integer.parseInt(section[i].split(":")[0])) {
                            scoreSection = section[i].split("/");
                            index = i;
                            break;
                        }
                    }
                    if (moduleName.equals("总分") && positiveItem != null) {
                        if (positiveItem <= 43 && curScore < 70) {
                            if (index > 0) {
                                scoreSection = section[index - 1].split("/");
                            }
                        }
                    }

                    //分数，解释及建议
                    specific.put("score", curScore);
                    specific.put("explain", scoreSection[1]);
                    if (scoreSection.length == 3) {
                        specific.put("advice", scoreSection[2]);
                    }
                    specific.put("moduleId", moduleId);
                }
                if (taskUser.getValid() == 1) {
                    report.put("测试可信性", "本次测评结果有效");
                } else {
                    report.put("测试可信性", "本次测评结果无效，需要在适当时候重新进行测验。");
                }
            }

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, report);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> customTaskList(JSONObject jsonObject) {
        try {
            Integer userId = jsonObject.getInteger("userId");
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            Integer schoolId = jsonObject.getInteger("school_id");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            String scaleName = jsonObject.getString("scaleName");
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select().eq("province", province);
            if (city != null && !city.equals("")) {
                taskSchoolQueryWrapper.eq("city", city);
            }
            if (county != null && !county.equals("")) {
                taskSchoolQueryWrapper.eq("county", county);
            }
            if (schoolId != null) {
                taskSchoolQueryWrapper.eq("school_id", schoolId);
            }
            taskSchoolQueryWrapper.groupBy("task_id");
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);
            List<Object> result = new ArrayList<>();
            for (TaskSchool taskSchool : taskSchoolList) {
                //判断任务是否被删除以及是否为系统量表
                Integer taskId = taskSchool.getTaskId();
                Task task = taskMapper.selectById(taskId);
                if (task != null) {
                    QueryWrapper<Scale> scaleQueryWrapper = new QueryWrapper<>();
                    scaleQueryWrapper.select().eq("id", task.getScaleId());
                    if (scaleName != null) {
                        scaleQueryWrapper.like("name", scaleName);
                    }
                    Scale scale = scaleMapper.selectOne(scaleQueryWrapper);
                    if (scale != null && scale.getType() == 2 && Objects.equals(scale.getCreateUser(), userId)) {
                        Map<String, Object> taskCur = new HashMap<>();
                        taskCur.put("taskId", taskId);
                        taskCur.put("taskName", task.getName());
                        taskCur.put("scaleName", scale.getName());
                        taskCur.put("startDate", task.getStartDate());
                        taskCur.put("endDate", task.getEndDate());
                        result.add(taskCur);
                    }
                }
            }
            IPage<List> page = getPages(pageNum, pageSize, result);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, page);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }


    @Override
    public Wrapper<Object> personalReportList(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            // 心理老师权限
            String teaRole = jsonObject.getString("teaRole");
            Integer level = jsonObject.getInteger("level");
            String chargeClasses = jsonObject.getString("chargeClasses");

            //模糊查询条件
            String role = jsonObject.getString("role");
            String name = jsonObject.getString("name");
            String period = jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            String classes = jsonObject.getString("classes");
            String sex = jsonObject.getString("sex");
            String idNumber = jsonObject.getString("idNumber");
            String studentId = jsonObject.getString("studentId");
            String phoneNumber = jsonObject.getString("phoneNumber");
            //学校名称
            String schoolName = jsonObject.getString("schoolName");

            List schoolIds = schoolMapper.selectObjs(new QueryWrapper<School>()
                    .eq(province == null ? false : true, "province", province)
                    .eq(city == null ? false : true, "city", city)
                    .eq(county == null ? false : true, "county", county)
                    .eq(schoolName == null ? false : true, "name", schoolName)
                    .eq(schoolId == null ? false : true, "id", schoolId));

            if (schoolIds == null || schoolIds.size() < 1) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, null);
            }

            if (role != null && role.equals("教师")) {
                if ((period != null && !period.equals("")) || (grade != null && !grade.equals("")) || (classes != null && !classes.equals("")) || (studentId != null && !studentId.equals(""))) {
                    return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, null);
                }
            }

            Page<Map> page = new Page<>(pageNum, pageSize);

            Integer archived = 0;
            if(StringUtils.isNotBlank(idNumber) || StringUtils.isNotBlank(name))
            {
                archived = null;
            }

            IPage<Map> pageRes = scaleMapper.personalReportList(page, schoolIds, role, name, period, grade,
                    classes, sex, idNumber, phoneNumber, studentId, archived);

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, pageRes);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    public Wrapper<Object> oldPersonalReportList(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            // 心理老师权限
            String teaRole = jsonObject.getString("teaRole");
            Integer level = jsonObject.getInteger("level");
            String chargeClasses = jsonObject.getString("chargeClasses");

            //模糊查询条件
            String role = jsonObject.getString("role");
            String name = jsonObject.getString("name");
            String period = jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            String classes = jsonObject.getString("classes");
            String sex = jsonObject.getString("sex");
            String idNumber = jsonObject.getString("idNumber");
            String studentId = jsonObject.getString("studentId");
            //学校名称
            String schoolName = jsonObject.getString("schoolName");
            List<Integer> schoolIdList = new ArrayList<>();
            if (schoolName != null && !schoolName.equals("")) {
                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.like("name", schoolName);
                List<School> schoolList = schoolMapper.selectList(schoolQueryWrapper);
                for (School school : schoolList) {
                    schoolIdList.add(school.getId().intValue());
                }
            }

            if (role != null && role.equals("教师")) {
                if ((period != null && !period.equals("")) || (grade != null && !grade.equals("")) || (classes != null && !classes.equals("")) || (studentId != null && !studentId.equals(""))) {
                    return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, null);
                }
            }

            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select().eq("province", province);
            if (city != null && !city.equals("")) {
                taskSchoolQueryWrapper.eq("city", city);
            }
            if (county != null && !county.equals("")) {
                taskSchoolQueryWrapper.eq("county", county);
            }
            if (schoolId != null) {
                taskSchoolQueryWrapper.eq("school_id", schoolId);
            } else if (schoolIdList != null && schoolIdList.size() > 0) {
                taskSchoolQueryWrapper.in("school_id", schoolIdList);
            }
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);
            List<Map<String, Object>> result = new ArrayList<>();
            for (TaskSchool taskSchool : taskSchoolList) {
                //判断任务是否被删除以及是否为系统量表
                Integer taskId = taskSchool.getTaskId();
                Task task = taskMapper.selectById(taskId);
                if (task != null) {
                    Scale scale = scaleMapper.selectById(task.getScaleId());
                    if (scale.getType() == 1) {
                        // 判断学校是否被删除
                        Integer schoolIdCur = taskSchool.getSchoolId();
                        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                        schoolQueryWrapper.eq("id", schoolIdCur);
                        School schoolCur = schoolMapper.selectOne(schoolQueryWrapper);
                        if (schoolCur != null) {
                            QueryWrapper<TaskUser> taskUserListQueryWrapper = new QueryWrapper<>();
                            taskUserListQueryWrapper.eq("school_id", schoolIdCur).eq("task_id", taskId).groupBy("user_id", "task_id");
                            List<TaskUser> taskUserList = taskUserMapper.selectList(taskUserListQueryWrapper);
                            for (TaskUser taskUser : taskUserList) {
                                Integer userId = taskUser.getUserId();
                                Map<String, Object> userinfo = userMapper.getUserInfo(userId);
                                // 判断用户是否被删除
                                if (userinfo != null) {
                                    if (userinfo.get("role").equals("教师") && (role == null || role.equals("教师"))) {
                                        QueryWrapper<Teachers> teachersQueryWrapper = new QueryWrapper<>();
                                        teachersQueryWrapper.select().eq("username", userinfo.get("username"));

                                        //模糊查询条件
                                        if (name != null && !name.equals("")) {
                                            teachersQueryWrapper.like("name", name);
                                        }
                                        if (sex != null && !sex.equals("")) {
                                            teachersQueryWrapper.like("sex", sex);
                                        }
                                        if (idNumber != null && !idNumber.equals("")) {
                                            teachersQueryWrapper.like("id_number", idNumber);
                                        }
                                        Teachers teachers = teachersMapper.selectOne(teachersQueryWrapper);
                                        if (teachers == null) {
                                            continue;
                                        }
                                        Map<String, Object> info = new HashMap<>();
                                        info.put("name", teachers.getName());
                                        info.put("sex", teachers.getSex());
                                        info.put("phone", teachers.getPhone());
                                        info.put("id_number", teachers.getIdNumber());
                                        info.put("role", "教师");
                                        info.put("task_name", task.getName());
                                        info.put("task_id", task.getId());
                                        info.put("scale_name", scale.getName());
                                        info.put("school", schoolCur.getName());
                                        info.put("province", schoolCur.getProvince());
                                        info.put("city", schoolCur.getCity());
                                        info.put("county", schoolCur.getCounty());
                                        info.put("user_id", userId);
                                        TaskUser taskUserLatest = taskUserMapper.getLatestSubmit(userId, taskId);
                                        info.put("submit_time", taskUserLatest.getSubmitTime());
                                        result.add(info);
                                    } else if (userinfo.get("role").equals("学生") && (role == null || role.equals("学生"))) {
                                        QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                                        studentsQueryWrapper.select().eq("username", userinfo.get("username"));

                                        // 心理老师权限筛选
                                        if (teaRole != null && teaRole.equals("心理老师") && level != null && level == 2 && (chargeClasses != null || !chargeClasses.equals(""))) {
                                            String[] classesTemp = chargeClasses.split(",");
                                            studentsQueryWrapper.and(studentsQueryWrapper2 -> {
                                                for (String classTemp : classesTemp) {
                                                    studentsQueryWrapper2.or(studentsQueryWrapper1 -> studentsQueryWrapper1.eq("period", classTemp.substring(0, 2)).eq("grade", classTemp.substring(2, 5)).eq("classes", classTemp.substring(5)));
                                                }
                                            });
                                        }

                                        //模糊查询条件
                                        if (name != null && !name.equals("")) {
                                            studentsQueryWrapper.like("name", name);
                                        }
                                        if (period != null && !period.equals("")) {
                                            studentsQueryWrapper.like("period", period);
                                        }
                                        if (grade != null && !grade.equals("")) {
                                            studentsQueryWrapper.like("grade", grade);
                                        }
                                        if (classes != null && !classes.equals("")) {
                                            studentsQueryWrapper.like("classes", classes);
                                        }
                                        if (sex != null && !sex.equals("")) {
                                            studentsQueryWrapper.like("sex", sex);
                                        }
                                        if (idNumber != null && !idNumber.equals("")) {
                                            studentsQueryWrapper.like("id_number", idNumber);
                                        }
                                        if (studentId != null && !studentId.equals("")) {
                                            studentsQueryWrapper.like("student_id", studentId);
                                        }
                                        Students students = studentsMapper.selectOne(studentsQueryWrapper);
                                        if (students == null) {
                                            continue;
                                        }
                                        Map<String, Object> info = new HashMap<>();
                                        info.put("name", students.getName());
                                        info.put("period", students.getPeriod());
                                        info.put("grade", students.getGrade());
                                        info.put("classes", students.getClasses());
                                        info.put("sex", students.getSex());
                                        info.put("student_id", students.getStudentId());
                                        info.put("student_code", students.getStudentCode());
                                        info.put("id_number", students.getIdNumber());
                                        info.put("role", "学生");
                                        info.put("task_name", task.getName());
                                        info.put("task_id", task.getId());
                                        info.put("scale_name", scale.getName());
                                        info.put("school", schoolCur.getName());
                                        info.put("province", schoolCur.getProvince());
                                        info.put("city", schoolCur.getCity());
                                        info.put("county", schoolCur.getCounty());
                                        info.put("user_id", userId);
                                        TaskUser taskUserLatest = taskUserMapper.getLatestSubmit(userId, taskId);
                                        info.put("submit_time", taskUserLatest.getSubmitTime());
                                        result.add(info);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Collections.sort(result, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    return ((Date) o2.get("submit_time")).compareTo((Date) o1.get("submit_time"));
                }
            });
            IPage<List> page = getPages(pageNum, pageSize, result);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, page);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> personalReportListByUserId(JSONObject jsonObject) {
        try {
            Integer userId = jsonObject.getInteger("userId");
            String taskName = jsonObject.getString("taskName");
            String scaleName = jsonObject.getString("scaleName");
            QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
            taskUserQueryWrapper.eq("user_id", userId).orderByDesc("submit_time").groupBy("task_id");
            List<TaskUser> taskUserList = taskUserMapper.selectList(taskUserQueryWrapper);
            List<Object> result = new ArrayList<>();
            for (TaskUser taskUser : taskUserList) {
                TaskUser taskUserLatest = taskUserMapper.getLatestSubmit(userId, taskUser.getTaskId());
                if (taskUserLatest.getType() == 1) {
                    QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
                    taskQueryWrapper.eq("id", taskUserLatest.getTaskId());
                    if (taskName != null && !taskName.equals("")) {
                        taskQueryWrapper.like("name", taskName);
                    }
                    Task task = taskMapper.selectOne(taskQueryWrapper);
                    if (task != null) {
                        QueryWrapper<Scale> scaleQueryWrapper = new QueryWrapper<>();
                        scaleQueryWrapper.eq("id", task.getScaleId());
                        if (scaleName != null && !scaleName.equals("")) {
                            scaleQueryWrapper.like("name", scaleName);
                        }
                        Scale scale = scaleMapper.selectOne(scaleQueryWrapper);
                        if (scale != null) {
                            Map<String, Object> scaleRecord = new HashMap<>();
                            scaleRecord.put("taskId", taskUserLatest.getTaskId());
                            scaleRecord.put("submitTime", taskUserLatest.getSubmitTime());
                            scaleRecord.put("taskName", task.getName());
                            scaleRecord.put("scaleName", scale.getName());
                            result.add(scaleRecord);
                        }
                    }
                }
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> schoolTaskList(JSONObject jsonObject) {
        try {
            Integer schoolId = jsonObject.getInteger("schoolId");
            // task_school表查询学校关联task_id
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select("task_id", "numbers", "complete_numbers", "is_submit").eq("school_id", schoolId).orderByDesc("id");
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);
            List<Map<String, Object>> result = new ArrayList<>();
            for (TaskSchool taskSchool : taskSchoolList) {
                Map<String, Object> taskInfo = taskMapper.getTaskAndIssueInfoById(taskSchool.getTaskId());
                taskInfo.put("task_id", taskSchool.getTaskId());
                taskInfo.put("numbers", taskSchool.getNumbers());
                taskInfo.put("complete_numbers", taskSchool.getCompleteNumbers());
                taskInfo.put("is_submit", taskSchool.getIsSubmit());
                double percent;
                if (taskSchool.getNumbers() == 0 || taskSchool.getCompleteNumbers() == 0) {
                    percent = 0;
                } else {
                    percent = (double) taskSchool.getCompleteNumbers() / taskSchool.getNumbers();
                }

                if (percent >= 0 && percent < 0.3) {
                    taskInfo.put("percentRange", "0-30%");
                } else if (percent >= 0.3 && percent < 0.6) {
                    taskInfo.put("percentRange", "30%-60%");
                } else if (percent < 1){
                    taskInfo.put("percentRange", "60%-100%");
                }else{
                    percent = 1;
                    taskInfo.put("percentRange", "100%");
                }
                taskInfo.put("percent", percent);
                String level = "";
                if (taskInfo.containsKey("province")) {
                    level = "省级";
                    if (taskInfo.containsKey("city")) {
                        level = "市级";
                        if (taskInfo.containsKey("county")) {
                            level = "区县级";
                            if (taskInfo.containsKey("school")) {
                                level = "校级";
                            }
                        }
                    }
                }
                taskInfo.put("level", level);
                result.add(taskInfo);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("schoolTaskList error {}", e);
            return WrapMapper.error();
        }
    }


    @Override
    public Wrapper<Object> taskExecution(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            String period = jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            String classes = jsonObject.getString("classes");
            String name = jsonObject.getString("name");
            String scaleName = jsonObject.getString("scaleName");
            String studentId = jsonObject.getString("student_id");
            String warningStr = jsonObject.getString("warning");
            String validStr = jsonObject.getString("valid");
            String semester = jsonObject.getString("semester");

            Integer warning = null;
            Integer valid = null;
            if ("是".equals(warningStr)) {
                warning = 1;
            } else if ("否".equals(warningStr)) {
                warning = 0;
            } else {
            }

            if ("是".equals(validStr)) {
                valid = 1;
            } else if ("否".equals(validStr)) {
                valid = 0;
            } else {
            }

            Map<String, String> taskInfo = taskSchoolMapper.selectByTaskIdAndSchoolId(taskId, schoolId);
            String gradeOriStr = taskInfo.get("grade");
            String classesOriStr = taskInfo.get("classes");

            List<String> periodList = new ArrayList<>();
            List<String> gradeList = new ArrayList<>();
            List<String> classesList = new ArrayList<>();
            if (!Strings.isNullOrEmpty(gradeOriStr)) {
                Arrays.asList(gradeOriStr.split(",")).forEach(x -> {
                    periodList.add(x.substring(0, 2));
                    gradeList.add(x.substring(2, 5));
                });
            } else if (!Strings.isNullOrEmpty(classesOriStr)) {
                Arrays.asList(classesOriStr.split(",")).forEach(x -> {
                    periodList.add(x.substring(0, 2));
                    gradeList.add(x.substring(2, 5));
                    classesList.add(x.substring(5));
                });
            }

            List<String> searchPeriodList = new ArrayList<>();
            List<String> searchGradeList = new ArrayList<>();
//            classes NULL 表示查全部
            List<String> searchClassesList = new ArrayList<>();

            if (Strings.isNullOrEmpty(period)) {
                searchPeriodList = periodList;
            } else {
                if (periodList.contains(period)) {
                    searchPeriodList.add(period);
                } else {
                    periodList.add("");
                }
            }

            if (Strings.isNullOrEmpty(grade)) {
                searchGradeList = gradeList;
            } else {
                if (gradeList.contains(grade)) {
                    searchGradeList.add(grade);
                } else {
                    searchGradeList.add("");
                }
            }

            if (Strings.isNullOrEmpty(classes)) {
                searchClassesList = classesList;
            } else {
                if (!classesList.isEmpty() && !classesList.contains(classes)) {
                    searchClassesList = new ArrayList<>();
                    searchClassesList.add("");
                } else {
                    searchClassesList = new ArrayList<>();
                    searchClassesList.add(classes);
                }
            }
            List<Integer> schoolIds = new ArrayList();
            List<Integer> taskIds = new ArrayList();
            schoolIds.add(schoolId);
            taskIds.add(taskId);
            Page<Map> page = new Page<>(pageNum, pageSize);
            IPage<Map> pageRes = scaleMapper.getExecutionTaskUserInfo(page, schoolIds, taskIds, searchPeriodList, searchGradeList, searchClassesList, name, studentId, scaleName, warning, valid, semester);

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, pageRes);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    public Wrapper<Object> oldTaskExecution(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            String period = jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            String classes = jsonObject.getString("classes");
            String name = jsonObject.getString("name");
            String studentId = jsonObject.getString("student_id");
            int warning = -1;
            if (jsonObject.getString("warning") != null && !jsonObject.getString("warning").equals("")) {
                warning = jsonObject.getString("warning").equals("是") ? 1 : 0;
            }
            int valid = -1;
            if (jsonObject.getString("valid") != null && !jsonObject.getString("valid").equals("")) {
                valid = jsonObject.getString("valid").equals("是") ? 1 : 0;
            }

            //任务对应量表名称
            Scale scale = scaleMapper.selectById(Integer.valueOf(taskMapper.selectById(taskId).getScaleId()));
            String scaleName = scale.getName();
            Integer scaleType = scale.getType();
            List<Integer> userIdList = taskUserMapper.getUserIdList(taskId, schoolId);
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Integer userId : userIdList) {
                QueryWrapper<TaskUser> queryWrapperTaskUser = new QueryWrapper<>();
                queryWrapperTaskUser.eq("task_id", taskId)
                        .and(schoolQueryWrapper -> schoolQueryWrapper.eq("school_id", schoolId))
                        .and(userQueryWrapper -> userQueryWrapper.eq("user_id", userId))
                        .orderByDesc("submit_time")
                        .last("limit 1");
                TaskUser taskUser = taskUserMapper.selectOne(queryWrapperTaskUser);
                if (warning != -1 && !Objects.equals(taskUser.getWarning(), warning)) {
                    continue;
                }
                if (valid != -1 && !Objects.equals(taskUser.getValid(), valid)) {
                    continue;
                }
                Map<String, Object> result = new HashMap<>();
                result.put("scaleName", scaleName);
                result.put("scaleType", scaleType);
                result.put("valid", taskUser.getValid());
                result.put("warning", taskUser.getWarning());
                result.put("to_retest", taskUser.getToRetest());
                result.put("userId", userId);
                result.put("id", taskUser.getId());
                Map<String, Object> student_info = taskUserMapper.getStudentInfoById(taskUser.getUserId(), taskUser.getSchoolYear());
                if (student_info != null) {
                    if (period != null && !period.equals("") && !student_info.get("period").equals(period)) {
                        continue;
                    }
                    if (grade != null && !grade.equals("") && !student_info.get("grade").equals(grade)) {
                        continue;
                    }
                    if (classes != null && !classes.equals("") && !student_info.get("classes").equals(classes)) {
                        continue;
                    }
                    if (name != null && !name.equals("") && !student_info.get("name").equals(name)) {
                        continue;
                    }
                    if (studentId != null && !studentId.equals("") && !student_info.get("student_id").equals(studentId)) {
                        continue;
                    }
                    result.putAll(student_info);
                    resultList.add(result);
                }
            }
            IPage<List> page = getPages(pageNum, pageSize, resultList);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, page);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> schoolTaskCompletion(JSONObject jsonObject) {

        try {
            Integer taskId = jsonObject.getInteger("taskId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            String period = jsonObject.getString("period") == null ? "" : jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            String classes = jsonObject.getString("classes");
            String name = jsonObject.getString("name");
            String studentId = jsonObject.getString("student_id");
            String completion = jsonObject.getString("completion");
            String semester = jsonObject.getString("semester");

            Map<String, String> taskInfo = taskSchoolMapper.selectByTaskIdAndSchoolId(taskId, schoolId);
            String gradeOriStr = taskInfo.get("grade");
            String classesOriStr = taskInfo.get("classes");
            String schoolYear = taskInfo.get("school_year");

            List<String> periodList = new ArrayList<>();
            List<String> gradeList = new ArrayList<>();
            List<String> classesList = new ArrayList<>();
            if (!Strings.isNullOrEmpty(gradeOriStr)) {
                Arrays.asList(gradeOriStr.split(",")).forEach(x -> {
                    periodList.add(x.substring(0, 2));
                    gradeList.add(x.substring(2, 5));
                });
            } else if (!Strings.isNullOrEmpty(classesOriStr)) {
                Arrays.asList(classesOriStr.split(",")).forEach(x -> {
                    periodList.add(x.substring(0, 2));
                    gradeList.add(x.substring(2, 5));
                    classesList.add(x.substring(5));
                });
            }

            List<String> searchPeriodList = new ArrayList<>();
            List<String> searchGradeList = new ArrayList<>();
//            classes NULL 表示查全部
            List<String> searchClassesList = new ArrayList<>();

            if (Strings.isNullOrEmpty(period)) {
                searchPeriodList = periodList;
            } else {
                if (periodList.contains(period)) {
                    searchPeriodList.add(period);
                } else {
                    periodList.add("");
                }
            }

            if (Strings.isNullOrEmpty(grade)) {
                searchGradeList = gradeList;
            } else {
                if (gradeList.contains(grade)) {
                    searchGradeList.add(grade);
                } else {
                    searchGradeList.add("");
                }
            }

            if (Strings.isNullOrEmpty(classes)) {
                searchClassesList = classesList;
            } else {
                if (!classesList.isEmpty() && !classesList.contains(classes)) {
                    searchClassesList = new ArrayList<>();
                    searchClassesList.add("");
                } else {
                    searchClassesList = new ArrayList<>();
                    searchClassesList.add(classes);
                }
            }
            List<Integer> schoolIds = new ArrayList();
            List<Integer> taskIds = new ArrayList();
            schoolIds.add(schoolId);
            taskIds.add(taskId);
            Page<Map> page = new Page<>(pageNum, pageSize);
            IPage<Map> pageRes = scaleMapper.getSchoolTaskCompletion(page, schoolIds, taskIds, searchPeriodList, searchGradeList, searchClassesList, schoolYear, name, studentId, completion, semester);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, pageRes);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    public Wrapper<Object> oldSchoolTaskCompletion(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            String period = jsonObject.getString("period") == null ? "" : jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            String classes = jsonObject.getString("classes");
            String name = jsonObject.getString("name");
            String studentId = jsonObject.getString("student_id");
            String completion = jsonObject.getString("completion");

            //获取年级、班级
            QueryWrapper<TaskSchool> queryWrapperTaskSchool = new QueryWrapper<>();
            queryWrapperTaskSchool.select("grade", "classes").eq("task_id", taskId).eq("school_id", schoolId);
            TaskSchool taskSchool = taskSchoolMapper.selectOne(queryWrapperTaskSchool);
            List<Map<String, Object>> resultList = new ArrayList<>();
            List<String> curAttributionList = new ArrayList<>();
            //班级列表(学段 + 年级 + 班级)
            JSONObject jsonObject1 = (JSONObject) JSONObject.toJSON(taskSchool);
            if (Strings.isNullOrEmpty(taskSchool.getGrade()) || Strings.isNullOrEmpty(jsonObject1.getString("classes"))) {  // 年级为空时，班级必为空；班级为空时。这两种情况都是全选班级
                QueryWrapper<SchoolClasses> queryWrapperSchoolClasses = new QueryWrapper<>();
                queryWrapperSchoolClasses.select("period", "grade", "classes").eq("school_id", schoolId);
                // 当年级不为空但班级为空时，只选中了当前年级
//                String curPeriod = taskSchool.getGrade().substring(0, 2);
//                String curGrade = taskSchool.getGrade().substring(2, 5);
                if (!Objects.equals(taskSchool.getGrade(), "")) {
                    List<String> schoolGrades = Arrays.asList(taskSchool.getGrade().split(","));
                    List<String> curPeriods = new ArrayList<>();
                    List<String> curGrades = new ArrayList<>();
                    for (String schoolGrade : schoolGrades) {
                        curPeriods.add(schoolGrade.substring(0, 2));
                        curGrades.add(schoolGrade.substring(2, 5));
                    }
                    queryWrapperSchoolClasses.in("period", curPeriods).in("grade", curGrades);
                }
                List<SchoolClasses> schoolClassesList = schoolClassesMapper.selectList(queryWrapperSchoolClasses);
                for (SchoolClasses schoolClasses : schoolClassesList) {
                    String curSchoolClasses = schoolClasses.getPeriod() + schoolClasses.getGrade() + schoolClasses.getClasses();
                    if (!curAttributionList.contains(curSchoolClasses)) {
                        curAttributionList.add(curSchoolClasses);
                    }
                }
            } else {   // 否则统计已选择的班级
                String[] AttributionStringList = jsonObject1.getString("classes").split(",");
                Collections.addAll(curAttributionList, AttributionStringList);
            }
            for (String attribution : curAttributionList) {
                if (Objects.equals(attribution, "")) {
                    continue;
                }
                String curPeriod = attribution.substring(0, 2);
                String curGrade = attribution.substring(2, 5);
                String curClasses = attribution.substring(5);
                if (period != null && !period.equals("") && !curPeriod.equals(period)) {
                    continue;
                }
                if (grade != null && !grade.equals("") && !curGrade.equals(grade)) {
                    continue;
                }
                if (classes != null && !classes.equals("") && !curClasses.equals(classes)) {
                    continue;
                }
                List<Map<String, Object>> studentInfoForTaskList = taskUserMapper.getStudentInfoForTask(curPeriod, curGrade, curClasses, schoolId);
                for (Map<String, Object> studentInfoForTask : studentInfoForTaskList) {
                    if (name != null && !name.equals("") && !studentInfoForTask.get("name").equals(name)) {
                        continue;
                    }
                    if (studentId != null && !studentId.equals("") && !studentInfoForTask.get("student_id").equals(studentId)) {
                        continue;
                    }
                    Integer userId = Integer.valueOf(studentInfoForTask.get("user_id").toString());
                    TaskUser taskUser = taskUserMapper.getLatestSubmit(userId, taskId);
                    if (taskUser == null || taskUser.getToRetest() == 1) {
                        if (completion != null && !completion.equals("") && !completion.equals("未完成")) {
                            continue;
                        }
                        studentInfoForTask.put("completion", "未完成");
                    } else {
                        if (completion != null && !completion.equals("") && !completion.equals("已完成")) {
                            continue;
                        }
                        studentInfoForTask.put("completion", "已完成");
                    }
                    resultList.add(studentInfoForTask);
                }
            }
            IPage<List> page = getPages(pageNum, pageSize, resultList);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, page);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> taskList(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            // 查询任务id及人数
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select("task_id", "numbers", "complete_numbers").eq("province", province);
            if (city != null && !city.equals("")) {
                taskSchoolQueryWrapper.eq("city", city);
            }
            if (county != null && !county.equals("")) {
                taskSchoolQueryWrapper.eq("county", county);
            }
            taskSchoolQueryWrapper.orderByDesc("id");
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);
            Map<Integer, Map<String, Object>> result = new HashMap<>();
            Map<Integer, Map<String, Object>> taskExists = new HashMap<>();
            for (TaskSchool taskSchool : taskSchoolList) {
                log.info("task_id -> " + taskSchool.getTaskId());
                Map<String, Object> taskInfo = taskMapper.getTaskAndIssueInfoById(taskSchool.getTaskId());
                if (taskInfo == null) {
                    continue;
                }
                if (taskExists.containsKey(taskSchool.getTaskId())) {
                    taskInfo.put("numbers", Integer.parseInt(taskExists.get(taskSchool.getTaskId()).get("numbers").toString()) + taskSchool.getNumbers());
                    taskInfo.put("complete_numbers", Integer.parseInt(taskExists.get(taskSchool.getTaskId()).get("complete_numbers").toString()) + taskSchool.getCompleteNumbers());

                } else {
                    taskInfo.put("numbers", taskSchool.getNumbers());
                    taskInfo.put("complete_numbers", taskSchool.getCompleteNumbers());
                }
                String level = "";
                if (taskInfo.containsKey("province")) {
                    level = "省级";
                    if (taskInfo.containsKey("city")) {
                        level = "市级";
                        if (city == null || city.equals("") || !city.equals(taskInfo.get("city"))) {
                            continue;
                        }
                        if (taskInfo.containsKey("county")) {
                            level = "区县级";
                            if (county == null || county.equals("") || !county.equals(taskInfo.get("county"))) {
                                continue;
                            }
                            if (taskInfo.containsKey("school")) {
//                                level = "校级";
                                continue;
                            }
                        }
                    }
                }
                taskInfo.put("level", level);
                taskInfo.put("task_id", taskSchool.getTaskId());
                // 查询量表名称
                QueryWrapper<Scale> scaleQueryWrapper = new QueryWrapper<>();
                scaleQueryWrapper.select("name").eq("id", taskInfo.get("scale_id"));
                String scaleName = scaleMapper.selectOne(scaleQueryWrapper).getName();
                taskInfo.put("scaleName", scaleName);
                taskExists.put(taskSchool.getTaskId(), taskInfo);
                double percent;
                if (Integer.parseInt(taskInfo.get("numbers").toString()) == 0 || Integer.parseInt(taskInfo.get("complete_numbers").toString()) == 0) {
                    percent = 0;
                } else {
                    percent = (double) Integer.parseInt(taskInfo.get("complete_numbers").toString()) / Integer.parseInt(taskInfo.get("numbers").toString());
                }
                taskInfo.put("percent", percent);
                result.put(taskSchool.getTaskId(), taskInfo);
            }
            result = new TreeMap<>(result).descendingMap();
            List<Map<String, Object>> result1 = new ArrayList<>();
            for (Map.Entry<Integer, Map<String, Object>> entry : result.entrySet()) {
                Map<String, Object> temp = entry.getValue();
//                if (temp.get("level").equals("省级")) {
//                    JSONObject jsonObject1 = new JSONObject();
//                    jsonObject1.put("taskId", temp.get("task_id"));
//                    jsonObject1.put("province", province);
//                    Wrapper<Object> tempRes = taskStatistics(jsonObject1);
//                    Map<String, Object> tempMap = (Map<String, Object>) tempRes.getResult();
//                    if (tempMap != null) {
//                        temp.put("percent", tempMap.get("completePercent"));
//                    }
//                } else if (temp.get("level").equals("市级")) {
//                    JSONObject jsonObject2 = new JSONObject();
//                    jsonObject2.put("taskId", temp.get("task_id"));
//                    jsonObject2.put("province", province);
//                    jsonObject2.put("city", city);
//                    Wrapper<Object> tempRes = taskStatistics(jsonObject2);
//                    Map<String, Object> tempMap = (Map<String, Object>) tempRes.getResult();
//                    if (tempMap != null) {
//                        temp.put("percent", tempMap.get("completePercent"));
//                    }
//                } else if (temp.get("level").equals("区县级")) {
//                    JSONObject jsonObject3 = new JSONObject();
//                    jsonObject3.put("taskId", temp.get("task_id"));
//                    jsonObject3.put("province", province);
//                    jsonObject3.put("city", city);
//                    jsonObject3.put("county", county);
//                    Wrapper<Object> tempRes = taskStatistics(jsonObject3);
//                    Map<String, Object> tempMap = (Map<String, Object>) tempRes.getResult();
//                    if (tempMap != null) {
//                        temp.put("percent", tempMap.get("completePercent"));
//                    }
//                }
//                double percentNew = Double.parseDouble(temp.get("percent").toString());
//                if (percentNew >= 0 && percentNew < 0.3) {
//                    temp.put("percentRange", "0-30%");
//                } else if (percentNew >= 0.3 && percentNew < 0.6) {
//                    temp.put("percentRange", "30%-60%");
//                } else {
//                    temp.put("percentRange", "60%-100%");
//                }
                result1.add(temp);
            }

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result1);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> taskProgress(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            String schoolName = jsonObject.getString("school");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
//            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
//            taskSchoolQueryWrapper.select("school_id", "numbers", "complete_numbers", "warning_numbers").eq("task_id", taskId);
//            if(province != null && !province.equals("")){
//                taskSchoolQueryWrapper.eq("province", province);
//            }
//            if(city != null && !city.equals("")){
//                taskSchoolQueryWrapper.eq("city", city);
//            }
//            if(county != null && !county.equals("")){
//                taskSchoolQueryWrapper.eq("county", county);
//            }
//            if (schoolId != null) {
//                taskSchoolQueryWrapper.eq("school_id", schoolId);
//            }
//            // 分页获取
//            IPage<TaskSchool> taskSchoolIPage = taskSchoolMapper.selectPage(new Page<>(pageNum, pageSize), taskSchoolQueryWrapper);
//            // 新建分页存新数据
//            IPage<Map<String, Object>> pageRes = new Page<>();
//            pageRes.setTotal(taskSchoolIPage.getTotal());
//            pageRes.setCurrent(taskSchoolIPage.getCurrent());
//            pageRes.setSize(taskSchoolIPage.getSize());
//            pageRes.setPages(taskSchoolIPage.getPages());
            List<Map<String, Object>> taskSchoolList = taskSchoolMapper.getTaskProcessInfo(province, city, county, schoolName, taskId, schoolId);
            List<Map<String, Object>> result = new ArrayList<>();
            for (Map<String, Object> taskSchool : taskSchoolList) {
                Map<String, Object> temp = new HashMap<>();
                temp.put("completeNumbers", taskSchool.get("complete_numbers"));
                temp.put("numbers", taskSchool.get("numbers"));
                // 计算每个学校的完成比例
                double completePercent;
                int numbers = Integer.parseInt(taskSchool.get("numbers").toString());
                int com_numbers = Integer.parseInt(taskSchool.get("complete_numbers").toString());
                if (numbers == 0 || com_numbers == 0) {
                    completePercent = 0;
                } else {
                    completePercent = (double) com_numbers / numbers;
                }
                temp.put("completePercent", completePercent);
                // 查询task_user中预警人数
//                QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
//                taskUserQueryWrapper.eq("warning", 1).eq("school_id", taskSchool.getSchoolId());
//                Integer warnCount = taskUserMapper.selectCount(taskUserQueryWrapper);
                // 计算每个学校的预警比例
                int warn_numbers = Integer.parseInt(taskSchool.get("warning_numbers").toString());
                temp.put("warnCount", warn_numbers);
                if (numbers == 0 || warn_numbers == 0) {
                    temp.put("warnPercent", 0);
                } else {
                    double warnPercent = (double) warn_numbers / com_numbers;
                    temp.put("warnPercent", warnPercent);
                }
                // 查询学校信息
                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.select("name", "charge_person", "phone", "county", "city").eq("id", taskSchool.get("school_id"));
                School school = schoolMapper.selectOne(schoolQueryWrapper);
                if (school == null) {
                    continue;
                }
                if (schoolName != null && !schoolName.equals("") && !schoolName.equals(school.getName())) {
                    continue;
                }
                temp.put("schoolId", taskSchool.get("school_id"));
                temp.put("school", school.getName());
                temp.put("chargePerson", school.getChargePerson());
                temp.put("phone", school.getPhone());
                temp.put("county", school.getCounty());
                temp.put("city", school.getCity());
                result.add(temp);
            }
            if (pageNum == null && pageSize == null) {
                pageNum = 1;
                pageSize = result.size();
            }
            IPage<List> pageRes = getPages(pageNum, pageSize, result);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, pageRes);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> countyLatestTask(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            Map<String, Object> result = new HashMap<>();
            result.put("completeSchool", 0);
            result.put("unCompleteSchool", 0);
            result.put("allNumbers", 0);
            result.put("allPercent", 0);
            result.put("warnPercent", 0);
            result.put("warnNumbers", 0);
            // 赶进度，不会写sql，麻烦的办法……
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select("task_id").eq("province", province).eq("city", city).eq("county", county).orderByDesc("id").last("limit 1");
            TaskSchool taskSchool1 = taskSchoolMapper.selectOne(taskSchoolQueryWrapper);
            if (taskSchool1 == null) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
            }
            Integer taskId = taskSchool1.getTaskId();
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper1 = new QueryWrapper<>();
            taskSchoolQueryWrapper1.select("task_id", "school_id", "numbers", "complete_numbers", "warning_numbers").eq("task_id", taskId).eq("province", province).eq("city", city).eq("county", county);
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper1);
            int completeSchool = 0, unCompleteSchool = 0;
            int allNumbers = 0, warnNumbers = 0, comNums = 0;
            double allPercent, warnPercent;
            for (TaskSchool taskSchool : taskSchoolList) {
                int tempNumbers = taskSchool.getNumbers();
                int tempComNumbers = taskSchool.getCompleteNumbers();
                if (tempComNumbers == tempNumbers) {
                    completeSchool += 1;
                } else {
                    unCompleteSchool += 1;
                }
                comNums += taskSchool.getCompleteNumbers();
                warnNumbers += taskSchool.getWarningNumbers();
                allNumbers += tempNumbers;
            }
//            QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
//            taskUserQueryWrapper.eq("task_id", taskId);
//            Integer comNums = taskUserMapper.selectCount(taskUserQueryWrapper);
//            taskUserQueryWrapper.eq("warning", 1);
//            warnNumbers = taskUserMapper.selectCount(taskUserQueryWrapper);
            if (allNumbers == 0) {
                allPercent = 0;
            } else {
                allPercent = (double) comNums / allNumbers;
            }
            if (comNums == 0) {
                warnPercent = 0;
            } else {
                warnPercent = (double) warnNumbers / comNums;
            }
            // 查询任务名
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.select("name").eq("id", taskId);
            String taskName = taskMapper.selectOne(taskQueryWrapper).getName();
            result.put("completeSchool", completeSchool);
            result.put("unCompleteSchool", unCompleteSchool);
            result.put("allNumbers", allNumbers);
            result.put("allPercent", allPercent);
            result.put("warnPercent", warnPercent);
            result.put("warnNumbers", warnNumbers);
            result.put("taskId", taskId);
            result.put("taskName", taskName);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> taskStatistics(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            Integer schoolId = jsonObject.getInteger("schoolId");

            //判断前端级别
            Integer level = 5;
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.eq("task_id", taskId).eq("province", province);
            if (city != null && !city.equals("")) {
                taskSchoolQueryWrapper.eq("city", city);
                level = 4;
            }
            if (county != null && !county.equals("")) {
                taskSchoolQueryWrapper.eq("county", county);
                level = 3;
            }
            if (schoolId != null) {
                taskSchoolQueryWrapper.eq("school_id", schoolId);
                level = 1;
            }
            List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);

            int complete = 0, unComplete = 0;
            int allNumbers = 0, completeNumbers = 0, warnNumbers = 0, validNumbers = 0;
            int primarySchoolWarning = 0, juniorMiddleSchoolWarning = 0, seniorMiddleSchoolWarning = 0;
            int primarySchoolValid = 0, juniorMiddleSchoolValid = 0, seniorMiddleSchoolValid = 0;
            int girlWarning = 0;
            double completePercent = 0, warnPercent = 0, validPercent = 0;
            double primarySchoolWarningPercent = 0, juniorMiddleSchoolWarningPercent = 0, seniorMiddleSchoolWarningPercent = 0;
            double girlWarningPercent = 0;

            Map<String, Object> result = new HashMap<>();

            //学校任务统计
            if (taskSchoolList != null && taskSchoolList.size() != 0 && level == 1) {
                for (TaskSchool taskSchool : taskSchoolList) {
                    Integer schoolIdCur = taskSchool.getSchoolId();
                    School school = schoolMapper.selectById(schoolIdCur);
                    if (school != null) {
                        Integer tempAllNumbers = taskSchool.getNumbers();
                        Integer tempCompleteNumbers = taskSchool.getCompleteNumbers();
                        warnNumbers += taskSchool.getWarningNumbers();
                        validNumbers += taskSchool.getValidNumbers();
                        allNumbers += tempAllNumbers;
                        completeNumbers += tempCompleteNumbers;
                    }
                }
                if (allNumbers != 0) {
                    completePercent = (double) completeNumbers / allNumbers;
                }
                if (completeNumbers != 0) {
                    validPercent = (double) validNumbers / completeNumbers;
                }
                if (validNumbers != 0) {
                    warnPercent = (double) warnNumbers / validNumbers;
                }
                result.put("completeNumbers", completeNumbers);
                result.put("validNumbers", validNumbers);
                result.put("warnNumbers", warnNumbers);
                result.put("completePercent", completePercent);
                result.put("validPercent", validPercent);
                result.put("warnPercent", warnPercent);
            }

            //区县任务统计
            if (taskSchoolList != null && taskSchoolList.size() != 0 && level == 3) {
                for (TaskSchool taskSchool : taskSchoolList) {
                    Integer schoolIdCur = taskSchool.getSchoolId();
                    School school = schoolMapper.selectById(schoolIdCur);
                    if (school != null) {
                        int tempAllNumbers = taskSchool.getNumbers();
                        int tempCompleteNumbers = taskSchool.getCompleteNumbers();
                        warnNumbers += taskSchool.getWarningNumbers();
                        validNumbers += taskSchool.getValidNumbers();
                        if (tempCompleteNumbers >= tempAllNumbers && tempCompleteNumbers != 0) {
                            complete += 1;
                        } else {
                            unComplete += 1;
                        }
                        allNumbers += tempAllNumbers;
                        completeNumbers += tempCompleteNumbers;
                        QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
                        taskUserQueryWrapper.eq("task_id", taskId).eq("school_id", school.getId()).groupBy("user_id");
                        List<TaskUser> taskUserList = taskUserMapper.selectList(taskUserQueryWrapper);
                        for (TaskUser taskUser : taskUserList) {
                            taskUser = taskUserMapper.getLatestSubmit(taskUser.getUserId(), taskUser.getTaskId());
                            if (taskUser.getValid() == 1) {
                                Map userInfo = taskUserMapper.getStudentInfoById(taskUser.getUserId(), taskUser.getSchoolYear());
                                if (userInfo != null) {
                                    if (userInfo.get("period").equals("小学")) {
                                        primarySchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            primarySchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    } else if (userInfo.get("period").equals("初中")) {
                                        juniorMiddleSchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            juniorMiddleSchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    } else {
                                        seniorMiddleSchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            seniorMiddleSchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
//                if(allNumbers != 0) { completePercent = (double) completeNumbers / allNumbers; }
                if (complete + unComplete != 0) {
                    completePercent = (double) complete / (complete + unComplete);
                }
                if (completeNumbers != 0) {
                    validPercent = (double) validNumbers / completeNumbers;
                }
                if (validNumbers != 0) {
                    warnPercent = (double) warnNumbers / validNumbers;
                }
                if (primarySchoolValid != 0) {
                    primarySchoolWarningPercent = (double) primarySchoolWarning / primarySchoolValid;
                }
                if (juniorMiddleSchoolValid != 0) {
                    juniorMiddleSchoolWarningPercent = (double) juniorMiddleSchoolWarning / juniorMiddleSchoolValid;
                }
                if (seniorMiddleSchoolValid != 0) {
                    seniorMiddleSchoolWarningPercent = (double) seniorMiddleSchoolWarning / seniorMiddleSchoolValid;
                }
                if (warnNumbers != 0) {
                    girlWarningPercent = (double) girlWarning / warnNumbers;
                }
                result.put("completeSchool", complete);
                result.put("unCompleteSchool", unComplete);
                result.put("completeNumbers", completeNumbers);
                result.put("validNumbers", validNumbers);
                result.put("warnNumbers", warnNumbers);
                result.put("completePercent", completePercent);
                result.put("validPercent", validPercent);
                result.put("warnPercent", warnPercent);
                result.put("primarySchoolWarningPercent", primarySchoolWarningPercent);
                result.put("juniorMiddleSchoolWarningPercent", juniorMiddleSchoolWarningPercent);
                result.put("seniorMiddleSchoolWarningPercent", seniorMiddleSchoolWarningPercent);
                result.put("girlWarningPercent", girlWarningPercent);
                result.put("boyWarningPercent", 1 - girlWarningPercent);

            }
            //市任务统计
            if (taskSchoolList != null && taskSchoolList.size() != 0 && level == 4) {
                Map<String, Object> taskCounty = new HashMap<>();
                for (TaskSchool taskSchool : taskSchoolList) {
                    Integer schoolIdCur = taskSchool.getSchoolId();
                    School school = schoolMapper.selectById(schoolIdCur);
                    if (school != null) {
                        int tempAllNumbers = taskSchool.getNumbers();
                        int tempCompleteNumbers = taskSchool.getCompleteNumbers();
                        if (taskCounty.containsKey(school.getCounty())) {
                            Map tempCounty = (Map) taskCounty.get(school.getCounty());
                            tempCounty.put("allNumbers", (Integer) tempCounty.get("allNumbers") + taskSchool.getNumbers());
                            tempCounty.put("completeNumbers", (Integer) tempCounty.get("completeNumbers") + taskSchool.getCompleteNumbers());
                        } else {
                            Map<String, Object> tempCounty = new HashMap<>();
                            tempCounty.put("allNumbers", taskSchool.getNumbers());
                            tempCounty.put("completeNumbers", taskSchool.getCompleteNumbers());
                            taskCounty.put(school.getCounty(), tempCounty);
                        }
                        warnNumbers += taskSchool.getWarningNumbers();
                        validNumbers += taskSchool.getValidNumbers();
                        allNumbers += tempAllNumbers;
                        completeNumbers += tempCompleteNumbers;
                        QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
                        taskUserQueryWrapper.eq("task_id", taskId).eq("school_id", school.getId()).groupBy("user_id");
                        List<TaskUser> taskUserList = taskUserMapper.selectList(taskUserQueryWrapper);
                        for (TaskUser taskUser : taskUserList) {
                            taskUser = taskUserMapper.getLatestSubmit(taskUser.getUserId(), taskUser.getTaskId());
                            if (taskUser.getValid() == 1) {
                                Map userInfo = taskUserMapper.getStudentInfoById(taskUser.getUserId(), taskUser.getSchoolYear());
                                if (userInfo != null) {
                                    if (userInfo.get("period").equals("小学")) {
                                        primarySchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            primarySchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    } else if (userInfo.get("period").equals("初中")) {
                                        juniorMiddleSchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            juniorMiddleSchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    } else {
                                        seniorMiddleSchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            seniorMiddleSchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Iterator<String> iterator = taskCounty.keySet().iterator();
                while (iterator.hasNext()) {
                    String tempCounty = iterator.next();
                    Integer tempAllNumbers = (Integer) ((Map) taskCounty.get(tempCounty)).get("allNumbers");
                    Integer tempCompleteNumbers = (Integer) ((Map) taskCounty.get(tempCounty)).get("completeNumbers");
                    if (tempAllNumbers == tempCompleteNumbers && tempCompleteNumbers != 0) {
                        complete += 1;
                    } else {
                        unComplete += 1;
                    }
                }
//                if(allNumbers != 0) { completePercent = (double) completeNumbers / allNumbers; }
                if (complete + unComplete != 0) {
                    completePercent = (double) complete / (complete + unComplete);
                }
                if (completeNumbers != 0) {
                    validPercent = (double) validNumbers / completeNumbers;
                }
                if (validNumbers != 0) {
                    warnPercent = (double) warnNumbers / validNumbers;
                }
                if (primarySchoolValid != 0) {
                    primarySchoolWarningPercent = (double) primarySchoolWarning / primarySchoolValid;
                }
                if (juniorMiddleSchoolValid != 0) {
                    juniorMiddleSchoolWarningPercent = (double) juniorMiddleSchoolWarning / juniorMiddleSchoolValid;
                }
                if (seniorMiddleSchoolValid != 0) {
                    seniorMiddleSchoolWarningPercent = (double) seniorMiddleSchoolWarning / seniorMiddleSchoolValid;
                }
                if (warnNumbers != 0) {
                    girlWarningPercent = (double) girlWarning / warnNumbers;
                }
                result.put("completeCounty", complete);
                result.put("unCompleteCounty", unComplete);
                result.put("completeNumbers", completeNumbers);
                result.put("validNumbers", validNumbers);
                result.put("warnNumbers", warnNumbers);
                result.put("completePercent", completePercent);
                result.put("validPercent", validPercent);
                result.put("warnPercent", warnPercent);
                result.put("primarySchoolWarningPercent", primarySchoolWarningPercent);
                result.put("juniorMiddleSchoolWarningPercent", juniorMiddleSchoolWarningPercent);
                result.put("seniorMiddleSchoolWarningPercent", seniorMiddleSchoolWarningPercent);
                result.put("girlWarningPercent", girlWarningPercent);
                result.put("boyWarningPercent", 1 - girlWarningPercent);
            }
            //省任务统计
            if (taskSchoolList != null && taskSchoolList.size() != 0 && level == 5) {
                Map<String, Object> taskCity = new HashMap<>();
                for (TaskSchool taskSchool : taskSchoolList) {
                    Integer schoolIdCur = taskSchool.getSchoolId();
                    School school = schoolMapper.selectById(schoolIdCur);
                    if (school != null) {
                        Integer tempAllNumbers = taskSchool.getNumbers();
                        Integer tempCompleteNumbers = taskSchool.getCompleteNumbers();
                        if (taskCity.containsKey(school.getCity())) {
                            Map tempCity = (Map) taskCity.get(school.getCity());
                            tempCity.put("allNumbers", (Integer) tempCity.get("allNumbers") + taskSchool.getNumbers());
                            tempCity.put("completeNumbers", (Integer) tempCity.get("completeNumbers") + taskSchool.getCompleteNumbers());
                        } else {
                            Map<String, Object> tempCity = new HashMap<>();
                            tempCity.put("allNumbers", taskSchool.getNumbers());
                            tempCity.put("completeNumbers", taskSchool.getCompleteNumbers());
                            taskCity.put(school.getCity(), tempCity);
                        }
                        warnNumbers += taskSchool.getWarningNumbers();
                        validNumbers += taskSchool.getValidNumbers();
                        allNumbers += tempAllNumbers;
                        completeNumbers += tempCompleteNumbers;
                        QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
                        taskUserQueryWrapper.eq("task_id", taskId).eq("school_id", school.getId()).groupBy("user_id");
                        List<TaskUser> taskUserList = taskUserMapper.selectList(taskUserQueryWrapper);
                        for (TaskUser taskUser : taskUserList) {
                            taskUser = taskUserMapper.getLatestSubmit(taskUser.getUserId(), taskUser.getTaskId());
                            if (taskUser.getValid() == 1) {
                                Map userInfo = taskUserMapper.getStudentInfoById(taskUser.getUserId(), taskUser.getSchoolYear());
                                if (userInfo != null) {
                                    if (userInfo.get("period").equals("小学")) {
                                        primarySchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            primarySchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    } else if (userInfo.get("period").equals("初中")) {
                                        juniorMiddleSchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            juniorMiddleSchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    } else {
                                        seniorMiddleSchoolValid += 1;
                                        if (taskUser.getWarning() == 1) {
                                            seniorMiddleSchoolWarning += 1;
                                            if (userInfo.get("sex").equals("女")) {
                                                girlWarning += 1;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Iterator<String> iterator = taskCity.keySet().iterator();
                while (iterator.hasNext()) {
                    String tempCity = iterator.next();
                    Integer tempAllNumbers = (Integer) ((Map) taskCity.get(tempCity)).get("allNumbers");
                    Integer tempCompleteNumbers = (Integer) ((Map) taskCity.get(tempCity)).get("completeNumbers");
                    if (tempAllNumbers == tempCompleteNumbers && tempCompleteNumbers != 0) {
                        complete += 1;
                    } else {
                        unComplete += 1;
                    }
                }
//                if(allNumbers != 0) { completePercent = (double) completeNumbers / allNumbers; }
                if (complete + unComplete != 0) {
                    completePercent = (double) complete / (complete + unComplete);
                }
                if (completeNumbers != 0) {
                    validPercent = (double) validNumbers / completeNumbers;
                }
                if (validNumbers != 0) {
                    warnPercent = (double) warnNumbers / validNumbers;
                }
                if (primarySchoolValid != 0) {
                    primarySchoolWarningPercent = (double) primarySchoolWarning / primarySchoolValid;
                }
                if (juniorMiddleSchoolValid != 0) {
                    juniorMiddleSchoolWarningPercent = (double) juniorMiddleSchoolWarning / juniorMiddleSchoolValid;
                }
                if (seniorMiddleSchoolValid != 0) {
                    seniorMiddleSchoolWarningPercent = (double) seniorMiddleSchoolWarning / seniorMiddleSchoolValid;
                }
                if (warnNumbers != 0) {
                    girlWarningPercent = (double) girlWarning / warnNumbers;
                }
                result.put("completeCity", complete);
                result.put("unCompleteCity", unComplete);
                result.put("completeNumbers", completeNumbers);
                result.put("validNumbers", validNumbers);
                result.put("warnNumbers", warnNumbers);
                result.put("completePercent", completePercent);
                result.put("validPercent", validPercent);
                result.put("warnPercent", warnPercent);
                result.put("primarySchoolWarningPercent", primarySchoolWarningPercent);
                result.put("juniorMiddleSchoolWarningPercent", juniorMiddleSchoolWarningPercent);
                result.put("seniorMiddleSchoolWarningPercent", seniorMiddleSchoolWarningPercent);
                result.put("girlWarningPercent", girlWarningPercent);
                result.put("boyWarningPercent", 1 - girlWarningPercent);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> schoolTaskProgress(JSONObject jsonObject) {
        try {

            Map<String, Object> result = new HashMap<>();
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer taskId = jsonObject.getInteger("taskId");
            if (taskId == null) {
                // 学校的最新任务
                QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
                taskSchoolQueryWrapper.select("task_id", "grade", "classes", "numbers", "complete_numbers").eq("school_id", schoolId).orderByDesc("id").last("limit 1");
                TaskSchool taskSchool = taskSchoolMapper.selectOne(taskSchoolQueryWrapper);
                if (taskSchool == null) {
                    return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
                }
                taskId = taskSchool.getTaskId();
            }

            List<String> schoolIds = new ArrayList<>();
            List<String> taskIds = new ArrayList<>();

            schoolIds.add(schoolId.toString());
            taskIds.add(taskId.toString());



            List<Map<String, Object>> schoolList = scaleMapper.getAllTaskProgressBySchoolAndTaskIds(taskIds, schoolIds, taskMapper.selectById(taskId).getSchoolYear());

            result.put("schoolList", schoolList);
//            result.put("isSubmit", isSubmit);
            // 查询任务信息
//            Map<String, Object> taskInfo = taskMapper.getTaskAndIssueInfoById(taskId);
//            result.put("name", taskInfo.get("name"));
            result.put("taskId", taskId);
//            result.put("startDate", taskInfo.get("start_date"));
//            result.put("endDate", taskInfo.get("end_date"));
//            result.put("issuedName", taskInfo.get("issued_name"));
//            result.put("issuedUnit", taskInfo.get("issued_unit"));
//            result.put("scaleId", taskInfo.get("scale_id"));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    public Wrapper<Object> oldSchoolTaskProgress(JSONObject jsonObject) {
        try {
            Map<String, Object> result = new HashMap<>();
            Integer schoolId = jsonObject.getInteger("schoolId");
            // 学校的最新任务
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select("task_id", "grade", "classes", "numbers", "complete_numbers").eq("school_id", schoolId).orderByDesc("id").last("limit 1");
            TaskSchool taskSchool = taskSchoolMapper.selectOne(taskSchoolQueryWrapper);
            if (taskSchool == null) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
            }
            Integer taskId = taskSchool.getTaskId();
            // 记录学校选中的全部班级及其人数
            Map<String, Integer> schoolClassesMap = new HashMap<>();
            // 根据task_school记录的年级和班级查到对应班级信息
            List<String> periodGrades = Arrays.asList(taskSchool.getGrade().split(","));   // 记录全部学段年级
            JSONObject jsonObject1 = (JSONObject) JSONObject.toJSON(taskSchool);
            List<String> periodGradeClasses = new ArrayList<>();
            if (jsonObject1.containsKey("classes") && jsonObject1.get("classes") != null && !jsonObject1.getString("classes").equals("")) {    // 如果班级列表不为空，则判断是否包含当前班级
                periodGradeClasses = Arrays.asList(jsonObject1.getString("classes").split(","));
            }
            if (periodGrades.size() != 0 && !periodGrades.get(0).equals("")) {   // 学段年级不为空，代表不是全选年级
                if (periodGradeClasses.size() != 0 && !periodGradeClasses.get(0).equals("")) {   // 学段年级班级不为空，需要遍历选中班级
                    for (String periodGradeClass : periodGradeClasses) {
                        QueryWrapper<SchoolClasses> schoolClassesQueryWrapper = new QueryWrapper<>();
                        schoolClassesQueryWrapper.select("entrance_year", "period", "grade", "classes", "class_numbers").eq("school_id", schoolId).eq("period", periodGradeClass.substring(0, 2))
                                .eq("grade", periodGradeClass.substring(2, 5)).eq("classes", periodGradeClass.substring(5, 7));
                        SchoolClasses schoolClasses = schoolClassesMapper.selectOne(schoolClassesQueryWrapper);
                        if (schoolClasses != null) {
                            schoolClassesMap.put(schoolClasses.getEntranceYear() + schoolClasses.getPeriod() + schoolClasses.getGrade() + schoolClasses.getClasses(), schoolClasses.getClassNumbers());
                        }
                    }
                } else { // 学段年级班级为空，班级全选
                    for (String periodGrade : periodGrades) {
                        QueryWrapper<SchoolClasses> schoolClassesQueryWrapper1 = new QueryWrapper<>();
                        schoolClassesQueryWrapper1.select("entrance_year", "period", "grade", "classes", "class_numbers").eq("school_id", schoolId).eq("period", periodGrade.substring(0, 2))
                                .eq("grade", periodGrade.substring(2, 5));
                        List<SchoolClasses> schoolClassesList1 = schoolClassesMapper.selectList(schoolClassesQueryWrapper1);
                        for (SchoolClasses schoolClass : schoolClassesList1) {
                            schoolClassesMap.put(schoolClass.getEntranceYear() + schoolClass.getPeriod() + schoolClass.getGrade() + schoolClass.getClasses(), schoolClass.getClassNumbers());
                        }
                    }
                }
            } else {  // 学段年级为空，代表全选年级
                QueryWrapper<SchoolClasses> schoolClassesQueryWrapper2 = new QueryWrapper<>();
                schoolClassesQueryWrapper2.select("entrance_year", "period", "grade", "classes", "class_numbers").eq("school_id", schoolId).ne("grade", "毕业");
                List<SchoolClasses> schoolClassesList2 = schoolClassesMapper.selectList(schoolClassesQueryWrapper2);
                for (SchoolClasses schoolClass : schoolClassesList2) {
                    schoolClassesMap.put(schoolClass.getEntranceYear() + schoolClass.getPeriod() + schoolClass.getGrade() + schoolClass.getClasses(), schoolClass.getClassNumbers());
                }
            }
            List<Map<String, String>> comStudentClass = taskMapper.getTaskStudentClassInfo(taskId, schoolId);
            Map<String, Integer> comClassNumbers = new HashMap<>();
            Map<String, Set<String>> comClassUsers = new HashMap<>();
            Map<String, Integer> warnClassNumbers = new HashMap<>();
            Map<String, Integer> validClassNumbers = new HashMap<>();
            for (Map<String, String> comStudent : comStudentClass) {
                String tempClass = comStudent.get("entrance_year") + comStudent.get("period") + comStudent.get("grade") + comStudent.get("classes");
                if (comClassUsers.containsKey(tempClass)) {
                    Set<String> temp = comClassUsers.get(tempClass);
                    temp.add(comStudent.get("user_id"));
                    comClassUsers.put(tempClass, temp);
                    comClassNumbers.put(tempClass, temp.size());
                    if (Objects.equals(comStudent.get("valid"), 1)) {
                        validClassNumbers.put(tempClass, validClassNumbers.get(tempClass) + 1);
                    }
                    if (Objects.equals(comStudent.get("warning"), 1)) {
                        warnClassNumbers.put(tempClass, warnClassNumbers.get(tempClass) + 1);
                    }
                } else {
                    Set<String> temp = new HashSet<>();
                    temp.add(comStudent.get("user_id"));
                    comClassUsers.put(tempClass, temp);
                    comClassNumbers.put(tempClass, 1);
                    if (Objects.equals(comStudent.get("valid"), 1)) {
                        validClassNumbers.put(tempClass, 1);
                    } else {
                        validClassNumbers.put(tempClass, 0);
                    }
                    if (Objects.equals(comStudent.get("warning"), 1)) {
                        warnClassNumbers.put(tempClass, 1);
                    } else {
                        warnClassNumbers.put(tempClass, 0);
                    }
                }
            }
            // 获取学校负责人信息（暂时没有班主任）
//            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
//            schoolQueryWrapper.select("charge_person", "phone").eq("id", schoolId);
//            School school = schoolMapper.selectOne(schoolQueryWrapper);
//            String chargePerson = school.getChargePerson();
//            String phone = school.getPhone();
            List<Map<String, Object>> schoolList = new ArrayList<>();
            Map<String, Map<String, Object>> tempClassResult = new HashMap<>();  // 记录当前班级对应的结果，便于合并不同入学年份相同班级的
            // 记录总人数和完成人数
            Integer allNumbers = 0, comNumbers = 0;
            // 计算班级进度并将班级信息存入结果
            for (Map.Entry<String, Integer> entry : schoolClassesMap.entrySet()) {
                // 合并不同入学年份  同样学段年级班级的
                String period = entry.getKey().substring(4, 6);
                String grade = entry.getKey().substring(6, 9);
                String classes = entry.getKey().substring(9, 11);

                Map<String, Object> tempMap = new HashMap<>();

                allNumbers += entry.getValue();

                if (tempClassResult.containsKey(period + grade + classes)) {
                    tempMap = tempClassResult.get(period + grade + classes);

                    tempMap.put("classNum", Integer.parseInt(tempMap.get("classNum").toString()) + entry.getValue());

                    // 修改有效人数、预警人数、完成人数  及 进度、预警比例、有效比例
                    if (comClassNumbers.containsKey(entry.getKey())) {
                        comNumbers += comClassNumbers.get(entry.getKey());
                        int newComNum = Integer.parseInt(tempMap.get("comNum").toString()) + comClassNumbers.get(entry.getKey());
                        tempMap.put("comNum", newComNum);
                        int newWarnNum = Integer.parseInt(tempMap.get("warnNum").toString()) + warnClassNumbers.get(entry.getKey());
                        tempMap.put("warnNum", newWarnNum);
                        int newValidNum = Integer.parseInt(tempMap.get("validNum").toString()) + validClassNumbers.get(entry.getKey());
                        tempMap.put("validNum", newValidNum);
                        if (newComNum == 0 || entry.getValue() == 0) {
                            tempMap.put("process", 0);
                        } else {
                            tempMap.put("process", (double) newComNum / entry.getValue());
                        }
                        if (newWarnNum == 0 || newComNum == 0) {
                            tempMap.put("warnPercent", 0);
                        } else {
                            tempMap.put("warnPercent", (double) newWarnNum / newComNum);
                        }
                        if (newValidNum == 0 || newComNum == 0) {
                            tempMap.put("validPercent", 0);
                        } else {
                            tempMap.put("validPercent", (double) newValidNum / newComNum);
                        }
                    } else {
                        tempMap.put("process", 0);
                        tempMap.put("warnPercent", 0);
                        tempMap.put("validPercent", 0);
                        tempMap.put("comNum", 0);
                        tempMap.put("warnNum", 0);
                        tempMap.put("validNum", 0);
                    }

                } else {
                    tempMap.put("period", period);
                    tempMap.put("grade", grade);
                    tempMap.put("classes", classes);

                    tempMap.put("classNum", entry.getValue());
//                if (taskSchool.getIsSubmit() == 1) {
//                    tempMap.put("process", 1);
//                } else {
                    if (comClassNumbers.containsKey(entry.getKey())) {
                        comNumbers += comClassNumbers.get(entry.getKey());
                        tempMap.put("comNum", comClassNumbers.get(entry.getKey()));
                        tempMap.put("warnNum", warnClassNumbers.get(entry.getKey()));
                        tempMap.put("validNum", validClassNumbers.get(entry.getKey()));
                        if (comClassNumbers.get(entry.getKey()) == 0 || entry.getValue() == 0) {
                            tempMap.put("process", 0);
                        } else {
                            tempMap.put("process", (double) comClassNumbers.get(entry.getKey()) / entry.getValue());
                        }
                        if (warnClassNumbers.get(entry.getKey()) == 0 || comClassNumbers.get(entry.getKey()) == 0) {
                            tempMap.put("warnPercent", 0);
                        } else {
                            tempMap.put("warnPercent", (double) warnClassNumbers.get(entry.getKey()) / comClassNumbers.get(entry.getKey()));
                        }
                        if (validClassNumbers.get(entry.getKey()) == 0 || comClassNumbers.get(entry.getKey()) == 0) {
                            tempMap.put("validPercent", 0);
                        } else {
                            tempMap.put("validPercent", (double) validClassNumbers.get(entry.getKey()) / comClassNumbers.get(entry.getKey()));
                        }
                    } else {
                        tempMap.put("process", 0);
                        tempMap.put("warnPercent", 0);
                        tempMap.put("validPercent", 0);
                        tempMap.put("comNum", 0);
                        tempMap.put("warnNum", 0);
                        tempMap.put("validNum", 0);
                    }
                    tempClassResult.put(period + grade + classes, tempMap);
                }

//                }
//                tempMap.put("chargePerson", chargePerson);
//                tempMap.put("phone", phone);
//                if (entry.getValue() != 0) {
//                    schoolList.add(tempMap);
//                }
            }

            for (Map.Entry<String, Map<String, Object>> entry : tempClassResult.entrySet()) {
                schoolList.add(entry.getValue());
            }

            // schoolList排序
            for (Map<String, Object> map : schoolList) {
                if (map.get("period").toString().equals("小学")) {
                    map.put("period", 0);
                } else if (map.get("period").toString().equals("初中")) {
                    map.put("period", 1);
                } else if (map.get("period").toString().equals("高中")) {
                    map.put("period", 2);
                } else if (map.get("period").toString().equals("职高")) {
                    map.put("period", 3);
                }
                if (map.get("grade").toString().equals("一年级")) {
                    map.put("grade", 0);
                } else if (map.get("grade").toString().equals("二年级")) {
                    map.put("grade", 1);
                } else if (map.get("grade").toString().equals("三年级")) {
                    map.put("grade", 2);
                } else if (map.get("grade").toString().equals("四年级")) {
                    map.put("grade", 3);
                } else if (map.get("grade").toString().equals("五年级")) {
                    map.put("grade", 4);
                } else if (map.get("grade").toString().equals("六年级")) {
                    map.put("grade", 5);
                }
            }

            schoolList.sort((o1, o2) -> {
                if (Integer.parseInt(o1.get("period").toString()) < Integer.parseInt(o2.get("period").toString())) {
                    return -1;
                } else if (Integer.parseInt(o1.get("period").toString()) > Integer.parseInt(o2.get("period").toString())) {
                    return 1;
                } else {
                    if (Integer.parseInt(o1.get("grade").toString()) < Integer.parseInt(o2.get("grade").toString())) {
                        return -1;
                    } else if (Integer.parseInt(o1.get("grade").toString()) > Integer.parseInt(o2.get("grade").toString())) {
                        return 1;
                    } else {
                        if (Integer.parseInt(o1.get("classes").toString().substring(0, o1.get("classes").toString().length() - 1)) < Integer.parseInt(o2.get("classes").toString().substring(0, o1.get("classes").toString().length() - 1))) {
                            return -1;
                        } else if (Integer.parseInt(o1.get("classes").toString().substring(0, o1.get("classes").toString().length() - 1)) > Integer.parseInt(o2.get("classes").toString().substring(0, o1.get("classes").toString().length() - 1))) {
                            return 1;
                        }
                    }
                }
                return 0;
            });

            for (Map<String, Object> map : schoolList) {
                if (map.get("period").toString().equals("0")) {
                    map.put("period", "小学");
                } else if (map.get("period").toString().equals("1")) {
                    map.put("period", "初中");
                } else if (map.get("period").toString().equals("2")) {
                    map.put("period", "高中");
                } else if (map.get("period").toString().equals("3")) {
                    map.put("period", "职高");
                }
                if (map.get("grade").toString().equals("0")) {
                    map.put("grade", "一年级");
                } else if (map.get("grade").toString().equals("1")) {
                    map.put("grade", "二年级");
                } else if (map.get("grade").toString().equals("2")) {
                    map.put("grade", "三年级");
                } else if (map.get("grade").toString().equals("3")) {
                    map.put("grade", "四年级");
                } else if (map.get("grade").toString().equals("4")) {
                    map.put("grade", "五年级");
                } else if (map.get("grade").toString().equals("5")) {
                    map.put("grade", "六年级");
                }
            }

            // 计算总进度
//            if (allNumbers == 0) {
//                result.put("allProcess", 0);
//            } else {
//                double allProcess = (double)comNumbers/allNumbers;
//                result.put("allProcess", allProcess);
//            }
            result.put("schoolList", schoolList);
//            result.put("isSubmit", isSubmit);
            // 查询任务信息
//            Map<String, Object> taskInfo = taskMapper.getTaskAndIssueInfoById(taskId);
//            result.put("name", taskInfo.get("name"));
            result.put("taskId", taskId);
//            result.put("startDate", taskInfo.get("start_date"));
//            result.put("endDate", taskInfo.get("end_date"));
//            result.put("issuedName", taskInfo.get("issued_name"));
//            result.put("issuedUnit", taskInfo.get("issued_unit"));
//            result.put("scaleId", taskInfo.get("scale_id"));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> cityTaskStatistics(JSONObject jsonObject) {
        try {
            Map<String, Object> result = new HashMap<>();
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            // 获取区县列表
            QueryWrapper<UnitNumber> unitNumberQueryWrapper = new QueryWrapper<>();
            unitNumberQueryWrapper.select("unit").like("unit", city).ne("unit", city);
            List<String> unitList = unitNumberMapper.selectList(unitNumberQueryWrapper).stream().map(UnitNumber::getUnit).collect(Collectors.toList());
            // 查询市级各区县的最新任务信息
            List<Map<String, Object>> countyTaskList = taskSchoolMapper.getLatestCityTask(province, city);
            // 统计各个区县完成进度
            Map<String, Map<String, Integer>> countyNumbersList = new HashMap<>();
            // 统计区县提交情况
            Map<String, Object> countySubmitTime = new HashMap<>();
            int submitCount, notSubmitCount, allCountyCount;
            if (countyTaskList == null || countyTaskList.size() == 0) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
            }
            int taskId = Integer.parseInt(countyTaskList.get(0).get("task_id").toString());
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.select("name").eq("id", taskId);
            String taskName = taskMapper.selectOne(taskQueryWrapper).getName();
            for (Map<String, Object> countyTask : countyTaskList) {
                // 更新区县总的任务人数和完成人数
                if (countyNumbersList.containsKey(countyTask.get("county").toString())) {
                    Map<String, Integer> tempCity = countyNumbersList.get(countyTask.get("county").toString());
                    tempCity.put("numbers", tempCity.get("numbers") + Integer.parseInt(countyTask.get("numbers").toString()));
                    tempCity.put("complete_numbers", tempCity.get("complete_numbers") + Integer.parseInt(countyTask.get("complete_numbers").toString()));
                    countyNumbersList.put(countyTask.get("county").toString(), tempCity);
                } else {
                    Map<String, Integer> tempCity = new HashMap<>();
                    tempCity.put("numbers", Integer.parseInt(countyTask.get("numbers").toString()));
                    tempCity.put("complete_numbers", Integer.parseInt(countyTask.get("complete_numbers").toString()));
                    countyNumbersList.put(countyTask.get("county").toString(), tempCity);
                }
                // 区县提交情况记录
                if (countySubmitTime.containsKey(countyTask.get("county").toString())) {  // 如果提交时间map中已存在该区县，则再判断当前学校是否提交，若未提交，则该区县没提交完，将其删除
                    if (Objects.equals(countyTask.get("is_submit").toString(), "0")) {
                        countySubmitTime.remove(countyTask.get("county").toString());
                    }
                } else {
                    if (Objects.equals(countyTask.get("is_submit").toString(), "1")) {  // 如果提交时间中不存在该区县，则先将其加入提交时间map中
                        countySubmitTime.put(countyTask.get("county").toString(), countyTask.get("submit_time").toString());
                    }
                }
            }
            submitCount = countySubmitTime.size();
            allCountyCount = unitList.size();
            notSubmitCount = allCountyCount - submitCount;
            // 整理区县进度数据
            List<Map<String, Object>> countyProcessList = new ArrayList<>();
            for (String unit : unitList) {
                String curCounty = unit.replace(city, "");
                if (countyNumbersList.containsKey(curCounty)) {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("county", curCounty);
                    Integer numbers = countyNumbersList.get(curCounty).get("numbers");
                    Integer comNumbers = countyNumbersList.get(curCounty).get("complete_numbers");
                    if (numbers == 0 || comNumbers == 0) {
                        temp.put("process", 0);
                    } else {
                        temp.put("process", (double) comNumbers / numbers);
                    }
                    countyProcessList.add(temp);
                } else {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("county", curCounty);
                    temp.put("process", 0);
                    countyProcessList.add(temp);
                }
            }
            // 整理区县完成时间
            List<Map<String, Object>> countySubmit = new ArrayList<>();
            for (String unit : unitList) {
                String curCounty = unit.replace(city, "");
                if (countySubmitTime.containsKey(curCounty)) {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("county", curCounty);
                    temp.put("submitTime", countySubmitTime.get(curCounty));
                    countySubmit.add(temp);
                } else {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("county", curCounty);
                    temp.put("submitTime", "未提交");
                    countySubmit.add(temp);
                }
            }
            // 获取该市的学校数
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.select("id").eq("province", province).eq("city", city);
            List<Long> schools = schoolMapper.selectList(schoolQueryWrapper).stream().map(School::getId).collect(Collectors.toList());
            // 获取该市的学生数
            QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
            studentsQueryWrapper.in("school_id", schools);
            Integer studentCount = studentsMapper.selectCount(studentsQueryWrapper);
            result.put("taskName", taskName);
            result.put("schoolCount", schools.size());
            result.put("studentCount", studentCount);
            result.put("submitCount", submitCount);
            result.put("notSubmitCount", notSubmitCount);
            result.put("allCountyCount", allCountyCount);
            result.put("countySubmitTime", countySubmit);
            result.put("countyProcessList", countyProcessList);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> cityTaskWarningStatistics(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            Map<String, Object> result = new HashMap<>();
            result.put("studentCount", 0);
            result.put("warningCount", 0);
            result.put("countyWarningNumbersList", null);
            // 获取区县列表
            QueryWrapper<UnitNumber> unitNumberQueryWrapper = new QueryWrapper<>();
            unitNumberQueryWrapper.select("unit").like("unit", city).ne("unit", city);
            List<String> unitList = unitNumberMapper.selectList(unitNumberQueryWrapper).stream().map(UnitNumber::getUnit).collect(Collectors.toList());
            // 查询市级各区县的最新任务信息
            List<Map<String, Object>> countyTaskList = taskSchoolMapper.getLatestCityTask(province, city);
            Map<String, Integer> countyWarningNumbers = new HashMap<>();
            Map<String, Integer> factorWarningNumbers = new HashMap<>();
            factorWarningNumbers.put("total", 0);
            factorWarningNumbers.put("study", 0);
            factorWarningNumbers.put("person", 0);
            factorWarningNumbers.put("loneliness", 0);
            factorWarningNumbers.put("selfAccusation", 0);
            factorWarningNumbers.put("allergy", 0);
            factorWarningNumbers.put("health", 0);
            factorWarningNumbers.put("terror", 0);
            factorWarningNumbers.put("impulse", 0);
            result.putAll(factorWarningNumbers);
            if (countyTaskList == null || countyTaskList.size() == 0) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
            }
            for (Map<String, Object> countyTask : countyTaskList) {
                // 更新区县的预警人数
                String county = countyTask.get("county").toString();
                if (countyWarningNumbers.containsKey(county)) {
                    countyWarningNumbers.put(county, countyWarningNumbers.get(county) + Integer.parseInt(countyTask.get("warning_numbers").toString()));
                } else {
                    countyWarningNumbers.put(county, Integer.valueOf(countyTask.get("warning_numbers").toString()));
                }
                //记录各维度预警人数
                QueryWrapper<MhtWarning> mhtWarningQueryWrapper = new QueryWrapper<>();
                mhtWarningQueryWrapper.eq("task_id", countyTask.get("task_id")).eq("school_id", countyTask.get("school_id"));
                List<MhtWarning> mhtWarningList = mhtWarningMapper.selectList(mhtWarningQueryWrapper);
                for (MhtWarning mhtWarning : mhtWarningList) {
                    factorWarningNumbers.put("total", factorWarningNumbers.get("total") + mhtWarning.getTotal());
                    factorWarningNumbers.put("study", factorWarningNumbers.get("study") + mhtWarning.getStudy());
                    factorWarningNumbers.put("person", factorWarningNumbers.get("person") + mhtWarning.getPerson());
                    factorWarningNumbers.put("loneliness", factorWarningNumbers.get("loneliness") + mhtWarning.getLoneliness());
                    factorWarningNumbers.put("selfAccusation", factorWarningNumbers.get("selfAccusation") + mhtWarning.getSelfAccusation());
                    factorWarningNumbers.put("allergy", factorWarningNumbers.get("allergy") + mhtWarning.getAllergy());
                    factorWarningNumbers.put("health", factorWarningNumbers.get("health") + mhtWarning.getHealth());
                    factorWarningNumbers.put("terror", factorWarningNumbers.get("terror") + mhtWarning.getTerror());
                    factorWarningNumbers.put("impulse", factorWarningNumbers.get("impulse") + mhtWarning.getImpulse());
                }
            }
            // 整理区县预警数据
            List<Map<String, Object>> countyWarningNumbersList = new ArrayList<>();
            int warningCount = 0;
            for (String unit : unitList) {
                String curCounty = unit.replace(city, "");
                if (countyWarningNumbers.containsKey(curCounty)) {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("county", curCounty);
                    temp.put("warning_numbers", countyWarningNumbers.get(curCounty));
                    warningCount = warningCount + countyWarningNumbers.get(curCounty);
                    countyWarningNumbersList.add(temp);
                } else {
                    Map<String, Object> temp = new HashMap<>();
                    temp.put("county", curCounty);
                    temp.put("warning_numbers", 0);
                    countyWarningNumbersList.add(temp);
                }
            }
            // 获取该市的学校数
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.select("id").eq("province", province).eq("city", city);
            List<Long> schools = schoolMapper.selectList(schoolQueryWrapper).stream().map(School::getId).collect(Collectors.toList());
            // 获取该市的学生数
            QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
            studentsQueryWrapper.in("school_id", schools);
            Integer studentCount = studentsMapper.selectCount(studentsQueryWrapper);
            result.put("studentCount", studentCount);
            result.put("warningCount", warningCount);
            result.put("countyWarningNumbersList", countyWarningNumbersList);
            result.putAll(factorWarningNumbers);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> submitTask(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select("id", "numbers", "complete_numbers").eq("task_id", taskId).eq("school_id", schoolId);
            TaskSchool taskSchool = taskSchoolMapper.selectOne(taskSchoolQueryWrapper);
            if (!Objects.equals(taskSchool.getNumbers(), taskSchool.getCompleteNumbers())) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "未全部完成任务，不可提交");
            } else if (taskSchool.getNumbers() == 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "不存在学生，不可提交");
            } else {
                TaskSchool taskSchool1 = new TaskSchool();
                taskSchool1.setId(taskSchool.getId());
                taskSchool1.setIsSubmit(1);
                taskSchool1.setSubmitTime(new Date());
                taskSchoolMapper.updateById(taskSchool1);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> getTaskRawData(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");

            Map<String, Object> result = new HashMap<>();

            // 获取任务名称、量表id
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.select("name", "scale_id").eq("id", taskId);
            Task task = taskMapper.selectOne(taskQueryWrapper);
            Long scaleId = Long.parseLong(task.getScaleId());

            result.put("taskName", task.getName());

            // 获取量表信息
            QueryWrapper<Scale> scaleQueryWrapper = new QueryWrapper<>();
            scaleQueryWrapper.select("name").eq("id", scaleId);
            Scale scale = scaleMapper.selectOne(scaleQueryWrapper);

            result.put("scaleName", scale.getName());

            // 查询已测评数据
            QueryWrapper<TaskUser> taskUserQueryWrapper = new QueryWrapper<>();
            taskUserQueryWrapper.select("user_id", "record").eq("task_id", taskId);
            List<TaskUser> taskUserList = taskUserMapper.selectList(taskUserQueryWrapper);
            List<Map<String, String>> records = new ArrayList<>();
            Map<Long, String> questionsList = new HashMap<>();     // 题目列表
            Set<Long> optionsList = new HashSet<>();      // 所有选项列表
            Map<Long, Integer> questionType = new HashMap<>();   // 题目对应类型
            if (taskUserList.size() == 0) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, new ArrayList<>());
            }
            for (TaskUser taskUser : taskUserList) {
                Map<String, String> userInfo = userMapper.getNameByUserId(taskUser.getUserId(), taskUser.getSchoolYear());
                Map<String, String> record = new HashMap<>(userInfo);
                String[] rawRecords = taskUser.getRecord().split(";");
                for (String rawRecord : rawRecords) {
                    Long question_id = Long.parseLong(rawRecord.split(":")[0]);
                    int type;
                    if (questionType.containsKey(question_id)) {
                        type = questionType.get(question_id);
                    } else {
                        QueryWrapper<Questions> questionsQueryWrapper = new QueryWrapper<>();
                        questionsQueryWrapper.select("main", "type").eq("id", question_id);
                        Questions questions = questionsMapper.selectOne(questionsQueryWrapper);
                        questionsList.put(question_id, questions.getMain());
                        type = questions.getType();
                        questionType.put(question_id, type);
                    }

                    if (type == 1) {  // 单选题
                        Long optionSingle = Long.parseLong(rawRecord.split(":")[1]);
                        record.put(String.valueOf(question_id), rawRecord.split(":")[1]);    // 记录该学生对应题目的选项
                        optionsList.add(optionSingle);   // 记录所有选项，便于以后查询
                    } else if (type == 2) {   // 多选题
                        String optionsString = rawRecord.split(":")[1];
                        String optionsMulti = optionsString.substring(0, optionsString.length());
                        for (String optionMulti : optionsMulti.split(",")) {
                            optionsList.add(Long.valueOf(optionMulti));
                        }
                        record.put(String.valueOf(question_id), optionsMulti);
                    } else {   // 填空或评分题
                        record.put(String.valueOf(question_id), rawRecord.split(":")[1]);
                    }
                }
                records.add(record);
            }

            // 查询数据库，处理数据
            // 查询所有的选项
            QueryWrapper<Options> optionsQueryWrapper = new QueryWrapper<>();
            optionsQueryWrapper.select("id", "main").in("id", optionsList);
            List<Options> optionsList1 = optionsMapper.selectList(optionsQueryWrapper);
            // 处理成选项id：main的map
            Map<String, String> optionsMain = new HashMap<>();
            for (Options options : optionsList1) {
                optionsMain.put(String.valueOf(options.getId()), options.getMain());
            }

            // 处理数据返回前端
            List<Map<String, Object>> resultRecords = new ArrayList<>();
            int num = 1;
            for (Map<String, String> record : records) {
                Map<String, Object> curRecord = new HashMap<>();
                curRecord.put("序号", num);
                for (String key : record.keySet()) {
                    switch (key) {
                        case "name":
                            curRecord.put("姓名", record.get(key));
                            break;
                        case "period":
                            curRecord.put("学段", record.get(key));
                            break;
                        case "grade":
                            curRecord.put("年级", record.get(key));
                            break;
                        case "classes":
                            curRecord.put("班级", record.get(key));
                            break;
                        default:
                            Long questionId = Long.parseLong(key);
                            int quesType = questionType.get(questionId);
                            if (quesType == 1) {  // 单选
                                String optionSin = record.get(key);
                                curRecord.put(questionsList.get(questionId), optionsMain.get(optionSin));
                            } else if (quesType == 2) {  // 多选
                                String[] optionMul = record.get(key).split(",");
                                StringBuilder optionMulti = new StringBuilder();
                                for (int i = 0; i < optionMul.length; i++) {
                                    if (i == optionMul.length - 1) {
                                        optionMulti.append(optionsMain.get(optionMul[i]));
                                    } else {
                                        optionMulti.append(optionsMain.get(optionMul[i])).append(" | ");
                                    }
                                }
                                curRecord.put(questionsList.get(questionId), optionMulti.toString());
                            } else {   // 填空或评分题
                                curRecord.put(questionsList.get(questionId), record.get(key));
                            }
                            break;
                    }
                }
                resultRecords.add(curRecord);
                num++;
            }
            Map<Long, String> questionsTemp = new TreeMap<>(questionsList);
            Map<Long, String> questionsSort = new TreeMap<>();
            int sortNum = 1;
            for (Long key : questionsTemp.keySet()) {
                questionsSort.put((long) sortNum, questionsTemp.get(key));
                sortNum++;
            }
            result.put("questionList", questionsSort);
            result.put("records", resultRecords);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> updateTaskEndDate(JSONObject jsonObject) {
        try {
            Long userId = jsonObject.getLongValue("userId");
            Long taskId = jsonObject.getLongValue("taskId");
            String endDate = jsonObject.getString("endDate");
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.select("start_date", "end_date", "issued_id").eq("id", taskId);
            Task task = taskMapper.selectOne(taskQueryWrapper);
            if (!userId.equals(task.getIssuedId())) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "不可修改其他人发布任务的截止时间");
            }
            UpdateWrapper<Task> taskUpdateWrapper = new UpdateWrapper<>();
            taskUpdateWrapper.eq("id", taskId);
            task.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(endDate));
            int update = taskMapper.update(task, taskUpdateWrapper);
            if (update == 1) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
            }
            return WrapMapper.wrap(Wrapper.ERROR_CODE, "不可修改其他人发布任务的截止时间");
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> resetTask(TaskResetDto taskResetDto) {
        try {
            // 是否全选  0否 1是
            Integer isAll = taskResetDto.getIsAll();
            Integer schoolId = taskResetDto.getSchoolId();
            Integer taskId = taskResetDto.getTaskId();
            Integer pageNum = taskResetDto.getPageNum();
            Integer pageSize = taskResetDto.getTotal();
            Integer hasFilter = taskResetDto.getHasFilter();

            List<Map<String, Object>> selectRecords;

            List<String> cantReset = new ArrayList<>();

            if (isAll != null && isAll == 1) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("schoolId", schoolId);
                jsonObject.put("taskId", taskId);
                jsonObject.put("pageNum", pageNum);
                jsonObject.put("pageSize", pageSize);
                if (hasFilter != null && hasFilter == 1) {
                    if (taskResetDto.getPeriod() != null) {
                        jsonObject.put("period", taskResetDto.getPeriod());
                    }
                    if (taskResetDto.getGrade() != null) {
                        jsonObject.put("grade", taskResetDto.getGrade());
                    }
                    if (taskResetDto.getClasses() != null) {
                        jsonObject.put("classes", taskResetDto.getClasses());
                    }
                    if (taskResetDto.getName() != null) {
                        jsonObject.put("name", taskResetDto.getName());
                    }
                    if (taskResetDto.getStudentId() != null) {
                        jsonObject.put("student_id", taskResetDto.getStudentId());
                    }
                    if (taskResetDto.getWarning() != null) {
                        jsonObject.put("warning", taskResetDto.getWarning());
                    }
                    if (taskResetDto.getValid() != null) {
                        jsonObject.put("valid", taskResetDto.getValid());
                    }
                }
                IPage wrapRecords = (IPage) taskExecution(jsonObject).getResult();
                selectRecords = (List<Map<String, Object>>) wrapRecords.getRecords();
            } else {
                selectRecords = taskResetDto.getRecords();
            }
            for (Map<String, Object> selectRecord : selectRecords) {
                if (selectRecord.get("valid").toString().equals("1")) {
                    continue;
                }
                Long taskUserId = Long.parseLong(selectRecord.get("id").toString());
                String name = selectRecord.get("name").toString();

                int to_retest = Integer.parseInt(selectRecord.get("to_retest").toString());

                if (to_retest == 1) {
                    cantReset.add(name);
                } else {
                    taskUserMapper.resetTask(taskUserId);
                }

            }

            if (cantReset.size() == 0) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
            }

            return WrapMapper.wrap(Wrapper.ERROR_CODE, "已为重测状态，无需修改", cantReset);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> latestTask(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Long reqTaskId = jsonObject.getLong("taskId");

            int level = 4;
            if (city != null && !city.equals("")) {
                level = 3;
            }
            if (county != null && !county.equals("")) {
                level = 2;
            }
            if (schoolId != null) {
                level = 1;
            }

            Long taskId = 0L;
            TaskSchool taskSchool = null;
            if (level > 1) {
                QueryWrapper<Task> taskQueryWrapper1 = new QueryWrapper<>();
                taskQueryWrapper1.select("id");

                List<String> queryList = new ArrayList<>();
                queryList.add(province);

                if (level <= 3) {
                    queryList.add(province + city);
                }
                if (level <= 2) {
                    queryList.add(province + city + county);
                }

                taskQueryWrapper1.in("issued_unit", queryList).orderByDesc("id");
                List<Long> taskIds = taskMapper.selectList(taskQueryWrapper1).stream().map(Task::getId).collect(Collectors.toList());
                if (taskIds == null || taskIds.size() == 0) {
                    return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, null);
                }
                for (Long taskId1 : taskIds) {
                    QueryWrapper<TaskSchool> taskSchoolQueryWrapper1 = new QueryWrapper<>();
                    taskSchoolQueryWrapper1.eq("task_id", taskId1);
                    taskSchoolQueryWrapper1.eq("province", province);
                    if (level <= 3) {
                        taskSchoolQueryWrapper1.eq("city", city);
                    }
                    if (level == 2) {
                        taskSchoolQueryWrapper1.eq("county", county);
                    }
                    int count = taskSchoolMapper.selectCount(taskSchoolQueryWrapper1);
                    if (count > 0) {
                        taskId = taskId1;
                        taskSchoolQueryWrapper1.select("grade", "classes");
                        taskSchoolQueryWrapper1.last("limit 1");
                        taskSchool = taskSchoolMapper.selectOne(taskSchoolQueryWrapper1);
                        break;
                    }
                }

            } else {
                QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
                taskSchoolQueryWrapper.select("task_id", "grade", "classes").eq("province", province);

                if (reqTaskId != null) {
                    taskSchoolQueryWrapper.eq("task_id", reqTaskId);
                }
                taskSchoolQueryWrapper.eq("city", city);
                taskSchoolQueryWrapper.eq("county", county);
                taskSchoolQueryWrapper.eq("school_id", schoolId);
                taskSchoolQueryWrapper.orderByDesc("id").last("limit 1");
                taskSchool = taskSchoolMapper.selectOne(taskSchoolQueryWrapper);
                if (taskSchool == null) {
                    return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, null);
                }
                taskId = taskSchool.getTaskId().longValue();
            }

            if (taskSchool == null) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, null);
            }
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.select("name", "content", "scale_id", "issued_id", "issued_unit", "start_date", "end_date").eq("id", taskId);
            Task task = taskMapper.selectOne(taskQueryWrapper);

            if (task == null) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, null);
            }
            Map<String, Object> result = new HashMap<>();
            result.put("taskId", taskId);
            result.put("name", task.getName());
            result.put("content", task.getContent());
            result.put("startDate", new SimpleDateFormat("yyyy-MM-dd").format(task.getStartDate()));
            result.put("endDate", new SimpleDateFormat("yyyy-MM-dd").format(task.getEndDate()));
            result.put("issuedUnit", task.getIssuedUnit());

            QueryWrapper<Scale> scaleQueryWrapper = new QueryWrapper<>();
            scaleQueryWrapper.select("name").eq("id", task.getScaleId());
            String scaleName = scaleMapper.selectOne(scaleQueryWrapper).getName();
            result.put("scaleName", scaleName);

            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("username").eq("id", task.getIssuedId());
            String username = userMapper.selectOne(userQueryWrapper).getUsername();

            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.select("level").eq("username", username);
            roleQueryWrapper.orderByDesc("id").last("limit 1");
            Integer roleLevel = roleMapper.selectOne(roleQueryWrapper).getLevel();
            if (roleLevel == 2) {
                QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                schoolRoleQueryWrapper.select("name").eq("username", username);
                String issuedName = schoolRoleMapper.selectOne(schoolRoleQueryWrapper).getName();
                result.put("issuedName", issuedName);
            } else {
                QueryWrapper<Manager> managerQueryWrapper = new QueryWrapper<>();
                managerQueryWrapper.select("name").eq("username", username);
                managerQueryWrapper.orderByDesc("id").last("limit 1");
                String issuedName = managerMapper.selectOne(managerQueryWrapper).getName();
                result.put("issuedName", issuedName);
            }

            if (level > 1) {
                if (Strings.isNullOrEmpty(taskSchool.getGrade())) {
                    result.put("crowd", "全部");
                } else {
                    result.put("crowd", taskSchool.getGrade());
                }
            }

            if (level == 1) {
                if (Strings.isNullOrEmpty(taskSchool.getGrade())) {
                    result.put("crowd", "全部");
                } else {
                    JSONObject jsonObject1 = (JSONObject) JSONObject.toJSON(taskSchool);
                    if (jsonObject1.containsKey("classes") && jsonObject1.get("classes") != null && !jsonObject1.getString("classes").equals("")) {
                        result.put("crowd", taskSchool.getGrade());
                    } else {
                        result.put("crowd", jsonObject1.getString("classes"));
                    }
                }
            }

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> allUnitTaskProcess(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            Integer taskId = jsonObject.getInteger("taskId");
            //判断前端级别
            Integer level = 5;
            if (city != null && !city.equals("")) {
                level = 4;
            }

            int complete = 0, unComplete = 0;
            int allNumbers = 0, completeNumbers = 0;
            double completePercent = 0;

            List<Map<String, Object>> result = new ArrayList<>();
            //市任务统计
            if (level == 4) {
                QueryWrapper<UnitNumber> unitNumberQueryWrapper = new QueryWrapper<>();
                unitNumberQueryWrapper.select("unit").like("unit", city).ne("unit", city);
                List<String> units = unitNumberMapper.selectList(unitNumberQueryWrapper).stream().map(UnitNumber::getUnit).collect(Collectors.toList());
                List<String> counties = new ArrayList<>();
                for (String unit : units) {
                    counties.add(unit.substring(city.length()));
                }
                for (String county : counties) {
                    QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
                    taskSchoolQueryWrapper.eq("task_id", taskId).eq("province", province).eq("city", city).eq("county", county);
                    List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);

                    Map<String, Object> curCountyRes = new HashMap<>();

                    if (taskSchoolList == null || taskSchoolList.size() == 0) {
                        curCountyRes.put("name", county);
                        curCountyRes.put("percent", 0);
                        result.add(curCountyRes);
                        continue;
                    }
                    for (TaskSchool taskSchool : taskSchoolList) {
                        Integer schoolIdCur = taskSchool.getSchoolId();
                        School school = schoolMapper.selectById(schoolIdCur);
                        if (school != null) {
                            Integer tempAllNumbers = taskSchool.getNumbers();
                            Integer tempCompleteNumbers = taskSchool.getCompleteNumbers();
                            if (tempCompleteNumbers == tempAllNumbers && tempCompleteNumbers != 0) {
                                complete += 1;
                            } else {
                                unComplete += 1;
                            }
                            allNumbers += tempAllNumbers;
                            completeNumbers += tempCompleteNumbers;
                        }
                    }
                    if (complete + unComplete != 0) {
                        completePercent = (double) complete / (complete + unComplete);
                    }
                    curCountyRes.put("name", county);
                    curCountyRes.put("percent", completePercent);
                    result.add(curCountyRes);
                }
            }
            //省任务统计
            if (level == 5) {
                String[] cities = {"杭州市", "宁波市", "温州市", "绍兴市", "嘉兴市", "金华市", "衢州市", "台州市", "丽水市", "舟山市", "湖州市"};
                for (String curCity : cities) {
                    QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
                    taskSchoolQueryWrapper.eq("task_id", taskId).eq("province", province).eq("city", curCity);
                    List<TaskSchool> taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);

                    Map<String, Object> curCityRes = new HashMap<>();

                    Map<String, Object> taskCounty = new HashMap<>();
                    if (taskSchoolList == null || taskSchoolList.size() == 0) {
                        curCityRes.put("name", curCity);
                        curCityRes.put("percent", 0);
                        result.add(curCityRes);
                        continue;
                    }
                    for (TaskSchool taskSchool : taskSchoolList) {
                        Integer schoolIdCur = taskSchool.getSchoolId();
                        School school = schoolMapper.selectById(schoolIdCur);
                        if (school != null) {
                            Integer tempAllNumbers = taskSchool.getNumbers();
                            Integer tempCompleteNumbers = taskSchool.getCompleteNumbers();
                            if (taskCounty.containsKey(school.getCounty())) {
                                Map tempCounty = (Map) taskCounty.get(school.getCounty());
                                tempCounty.put("allNumbers", (Integer) tempCounty.get("allNumbers") + taskSchool.getNumbers());
                                tempCounty.put("completeNumbers", (Integer) tempCounty.get("completeNumbers") + taskSchool.getCompleteNumbers());
                            } else {
                                Map<String, Object> tempCounty = new HashMap<>();
                                tempCounty.put("allNumbers", taskSchool.getNumbers());
                                tempCounty.put("completeNumbers", taskSchool.getCompleteNumbers());
                                taskCounty.put(school.getCounty(), tempCounty);
                            }
                            allNumbers += tempAllNumbers;
                            completeNumbers += tempCompleteNumbers;
                        }
                    }
                    Iterator<String> iterator = taskCounty.keySet().iterator();
                    while (iterator.hasNext()) {
                        String tempCounty = iterator.next();
                        Integer tempAllNumbers = (Integer) ((Map) taskCounty.get(tempCounty)).get("allNumbers");
                        Integer tempCompleteNumbers = (Integer) ((Map) taskCounty.get(tempCounty)).get("completeNumbers");
                        if (tempAllNumbers == tempCompleteNumbers && tempCompleteNumbers != 0) {
                            complete += 1;
                        } else {
                            unComplete += 1;
                        }
                    }
                    if (complete + unComplete != 0) {
                        completePercent = (double) complete / (complete + unComplete);
                    }

                    curCityRes.put("name", curCity);
                    curCityRes.put("percent", completePercent);
                    result.add(curCityRes);
                }
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> getAllOriginalData(JSONObject jsonObject) {
        try {
            List<String> taskIds = jsonObject.getJSONArray("taskIds").toJavaList(String.class);
            List<String> schoolIds = jsonObject.getJSONArray("schoolIds").toJavaList(String.class);
            List<String> periods = null;
            List<String> grades = null;
            List<String> classes = null;
            if (jsonObject.containsKey("periods")) {
                periods = jsonObject.getJSONArray("periods").toJavaList(String.class);
            }
            if (jsonObject.containsKey("grades")) {
                grades = jsonObject.getJSONArray("grades").toJavaList(String.class);
            }
            if (jsonObject.containsKey("classes")) {
                classes = jsonObject.getJSONArray("classes").toJavaList(String.class);
            }


            List<Questions> questionsList = questionsMapper.selectQuestionsByTaskIds(taskIds);
            HashMap<String, String> questions = new HashMap<>();
            for (Questions question : questionsList) {
                questions.put(Long.toString(question.getId()), question.getMain());
            }
            List<String> questionIds = new ArrayList<>(questions.keySet());
            List<Options> optionsList = optionsMapper.selectOptionsByQuestionIds(questionIds);

            HashMap<String, String> options = new HashMap<>();
            for (Options option : optionsList) {
                options.put(Long.toString(option.getId()), option.getMain());
            }

            List<HashMap<String, String>> results_pre = scaleMapper.getAllTaskResultsByTaskIds(taskIds, schoolIds,
                    periods, grades, classes);


            Iterator<HashMap<String, String>> iterator = results_pre.iterator();
            while (iterator.hasNext()) {
                HashMap<String, String> hashMap = iterator.next();
                List<String> qaidsList = Arrays.asList(hashMap.get("record").split(";"));
                for (String qaid : qaidsList) {
                    String[] qaids = qaid.split(":");
                    if (qaids.length == 2) {
                        hashMap.put(questions.get(qaids[0]), options.get(qaids[1]));
                    }
                }

                List<String> scoreResults = Arrays.asList(hashMap.get("result").split(","));
                String score = null;
                try {
                    for (String scoreResult : scoreResults) {
                        String[] scoreIds = scoreResult.split(":");
                        if (scoreIds.length == 2) {
                            score = scoreIds[1];
                        }
                    }
                } catch (Exception e) {

                }

                hashMap.remove("record");
                hashMap.remove("result");
                hashMap.put("score", score);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, results_pre);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }
}
