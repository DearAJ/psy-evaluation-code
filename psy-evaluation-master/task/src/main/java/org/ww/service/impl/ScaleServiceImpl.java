package org.ww.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.ww.domain.*;
import org.ww.exception.BusinessException;
import org.ww.mapper.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.ScaleService;
import org.ww.service.TaskService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YWSnow
 * @date 2022/3/2 17:15
 * @Description:
 */

@Slf4j
@Service
public class ScaleServiceImpl extends ServiceImpl<ScaleMapper, Scale> implements ScaleService {
    @Resource
    private ScaleMapper scaleMapper;

    @Resource
    private QuestionsMapper questionsMapper;

    @Resource
    private OptionsMapper optionsMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private StudentsMapper studentsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private TaskSchoolMapper taskSchoolMapper;

    private final TaskService taskService;

    public ScaleServiceImpl(TaskService taskService) {
        this.taskService = taskService;
    }

    @Override
    public Wrapper<String> addScale(JSONObject jsonObject) {
        try {
            Scale scale = new Scale();
            scale.setType(2);
            scale.setName(jsonObject.getString("name"));
            scale.setIntroduction(jsonObject.getString("introduction"));
            scale.setConclusion(jsonObject.getString("conclusion"));
            scale.setWarningOptions(1);
            scale.setApplication("其他");

            Integer userId = jsonObject.getInteger("userId");
            scale.setCreateUser(userId);
            //设置量表权限
            Integer level = (int)(userMapper.getUserInfo(userId).get("level"));
            scale.setLevel(level);
            scaleMapper.insert(scale);
            //题目传入
            List scaleList = jsonObject.getJSONArray("scaleList");
            ListIterator<Map> scaleListIterator = scaleList.listIterator();
            while (scaleListIterator.hasNext()){
                Map scaleCur = scaleListIterator.next();
                Questions questions = new Questions();
                questions.setMain(scaleCur.get("question").toString());
                questions.setRequired((int)scaleCur.get("required"));
                questions.setScaleId(scale.getId().intValue());
                Integer type = (int)scaleCur.get("type");
                questions.setType(type);
                if(type == 2){
                    questions.setMin(scaleCur.get("min").toString());
                    questions.setMax(scaleCur.get("max").toString());
                }
                else if(type == 3){
                    questions.setMin(scaleCur.get("min").toString());
                    questions.setMax(scaleCur.get("max").toString());
                    questions.setGear((int)scaleCur.get("gear"));
                }
                questionsMapper.insert(questions);
                if(type == 1 || type == 2){
                    List optionsList = (ArrayList)scaleCur.get("options");
                    ListIterator<String> optionListIterator = optionsList.listIterator();
                    while (optionListIterator.hasNext()){
                        String option = optionListIterator.next();
                        Options options = new Options();
                        options.setQuestionId(questions.getId().intValue());
                        options.setMain(option);
                        optionsMapper.insert(options);
                    }
                }
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<IPage<Scale>> scaleList(JSONObject jsonObject) {
        try {
            Integer userId = jsonObject.getInteger("userId");
            String name = jsonObject.getString("name");
            String application = jsonObject.getString("application");
            Integer warningOptions = jsonObject.getInteger("warningOptions");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            if(pageSize == null) pageSize = 15;
            QueryWrapper<Scale> queryWrapper = new QueryWrapper<>();
            if(name != null && !name.equals("")){
                queryWrapper.like("name", name);
            }
            if(application != null && !application.equals("")){
                queryWrapper.eq("application", application);
            }
            if(warningOptions != null){
                queryWrapper.eq("warning_options", warningOptions);
            }

            //判断userId权限
            Integer role = (int) (userMapper.getUserInfo(userId).get("level"));
            queryWrapper.le("level", role);

            queryWrapper.and(wrapper->wrapper.eq("create_user", 0).or().eq("create_user", userId));
            queryWrapper.orderByDesc("create_time");
            IPage<Scale> page = scaleMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
            log.info("find scale list:{}", JSON.toJSON(page));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, page);
        } catch (BusinessException e){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e){
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> updateScale(JSONObject jsonObject) {
        try {
            Integer scaleId = jsonObject.getInteger("scaleId");
            //判断是否关联任务
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.eq("scale_id", scaleId);
            if(taskMapper.selectCount(taskQueryWrapper) > 0){
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, "存在关联数据，无法修改");
            }
            Scale scale = new Scale();
            scale.setId(scaleId.longValue());
            scale.setType(2);
            scale.setName(jsonObject.getString("name"));
            scale.setIntroduction(jsonObject.getString("introduction"));
            scale.setConclusion(jsonObject.getString("conclusion"));
            scale.setCreateUser(jsonObject.getInteger("userId"));
            scale.setWarningOptions(1);
            scale.setApplication("其他");
            scaleMapper.updateById(scale);

            //删除原始问题和选项
            QueryWrapper<Questions> queryWrapperQuestion = new QueryWrapper<>();
            queryWrapperQuestion.select("id").eq("scale_id", scaleId);
            List<Questions> questionsList = questionsMapper.selectList(queryWrapperQuestion);
            for(Questions questions : questionsList){
                QueryWrapper<Options> queryWrapperOptions = new QueryWrapper<>();
                queryWrapperOptions.eq("question_id", questions.getId());
                optionsMapper.delete(queryWrapperOptions);

            }
            questionsMapper.delete(queryWrapperQuestion);

            //添加新问题和选项
            List scaleList = jsonObject.getJSONArray("scaleList");
            ListIterator<Map> scaleListIterator = scaleList.listIterator();
            while (scaleListIterator.hasNext()){
                Map scaleCur = scaleListIterator.next();
                Questions questions = new Questions();
                questions.setMain(scaleCur.get("question").toString());
                questions.setRequired((int)scaleCur.get("required"));
                questions.setScaleId(scaleId);
                Integer type = (int)scaleCur.get("type");
                questions.setType(type);
                if(type == 2){
                    questions.setMin(scaleCur.get("min").toString());
                    questions.setMax(scaleCur.get("max").toString());
                }
                else if(type == 3){
                    questions.setMin(scaleCur.get("min").toString());
                    questions.setMax(scaleCur.get("max").toString());
                    questions.setGear((int)scaleCur.get("gear"));
                }
                questionsMapper.insert(questions);
                if(type == 1 || type == 2){
                    List optionsList = (ArrayList)scaleCur.get("options");
                    ListIterator<String> optionListIterator = optionsList.listIterator();
                    while (optionListIterator.hasNext()){
                        String option = optionListIterator.next();
                        Options options = new Options();
                        options.setQuestionId(questions.getId().intValue());
                        options.setMain(option);
                        optionsMapper.insert(options);
                    }
                }
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> deleteScale(JSONObject jsonObject) {
        try {
            Integer scaleId = jsonObject.getInteger("scaleId");
            //判断是否关联任务
            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
            taskQueryWrapper.eq("scale_id", scaleId);
            if(taskMapper.selectCount(taskQueryWrapper) == 0){
                QueryWrapper<Questions> queryWrapperQuestion = new QueryWrapper<>();
                queryWrapperQuestion.select("id").eq("scale_id", scaleId);
                List<Questions> questionsList = questionsMapper.selectList(queryWrapperQuestion);
                for(Questions questions : questionsList){
                    QueryWrapper<Options> queryWrapperOptions = new QueryWrapper<>();
                    queryWrapperOptions.eq("question_id", questions.getId());
                    optionsMapper.delete(queryWrapperOptions);

                }
                questionsMapper.delete(queryWrapperQuestion);
                scaleMapper.delete(new QueryWrapper<Scale>().eq("id", scaleId));
            }
            else{
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, "存在关联数据，无法删除");
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<List<Map<String, Object>>> getScale(JSONObject jsonObject) {
        try {

            List<Integer> ids = new ArrayList<>();
            if (jsonObject.size() == 1) {
                if (jsonObject.get("id") instanceof Integer) {
                    ids.add(jsonObject.getInteger("id"));
                } else {
                    ids.addAll(jsonObject.getJSONArray("id").toJavaList(Integer.class));
                }
            } else {
                ids.addAll(jsonObject.getJSONArray("id").toJavaList(Integer.class));
            }

            List<Map<String, Object>> result = new ArrayList<>();
            for(Integer scaleId : ids){
                Map<String, Object> scaleContent = new HashMap<>();
                Scale scale = scaleMapper.selectById(scaleId);
                scaleContent.put("scaleName", scale.getName());
                scaleContent.put("introduction", scale.getIntroduction());
                scaleContent.put("conclusion", scale.getConclusion());
                List<Map<String, Object>> questionList = scaleMapper.getScale(scaleId);
                scaleContent.put("content", questionList);
                for (Map question : questionList){
                    if((int)question.get("type") == 1 || (int)question.get("type") == 2){
                        Integer questionId = Integer.parseInt(question.get("id").toString());
                        question.put("options", scaleMapper.getOptions(questionId));
                    }
                }
                result.add(scaleContent);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e){
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> verifyScaleData(MultipartFile scaleDataExcel, Integer task_id, Integer scale_id) {
        try {
            if (scaleDataExcel == null || scaleDataExcel.getSize() == 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空");
            }
            String fileName = scaleDataExcel.getOriginalFilename();
            if (fileName == null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件名为空");
            }
            if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件格式不正确");
            }
            boolean isExcel2007 = !fileName.matches("^.+\\.(?i)(xls)$");
            InputStream inputStream = scaleDataExcel.getInputStream();
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
            int rows = sheet.getLastRowNum();
            Row row = sheet.getRow(1);
            String county, school;
            try {
                county = row.getCell(1).getStringCellValue();
                school = row.getCell(4).getStringCellValue();
            } catch (BusinessException e) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
            } catch (Exception e) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "区县和学校名称填写存在问题");
            }
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id", "period").eq("county", county).eq("name", school);
            School school1 = schoolMapper.selectOne(queryWrapper);
            if (school1 == null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "数据库中不存在该学校");
            }
            Long schoolId = school1.getId();
            Row row1 = sheet.getRow(3);
            Map<String, Integer> fieldColumn = new HashMap<>();
            Map<Integer, String> columnField = new HashMap<>();
            int columnNum = row1.getPhysicalNumberOfCells();
            // 添加列名和列号对应
            for (int i = 0; i < 5; i ++) {
                fieldColumn.put(row1.getCell(i).getStringCellValue(), i);
                columnField.put(i, row1.getCell(i).getStringCellValue());
            }
            if (!fieldColumn.containsKey("学生姓名") || !fieldColumn.containsKey("年级") || !fieldColumn.containsKey("班级")
                    || !fieldColumn.containsKey("学籍号") || !fieldColumn.containsKey("学段")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "模板列不匹配，请使用模板上传数据");
            }
            if (rows <= 3) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "表格内无量表数据");
            }
            QueryWrapper<Questions> questionsQueryWrapper = new QueryWrapper<>();
            questionsQueryWrapper.select("id").eq("scale_id", scale_id);
            List<Long> questionsIdList = questionsMapper.selectList(questionsQueryWrapper).stream().map(Questions::getId).collect(Collectors.toList());
            if (questionsIdList.size() != columnNum - 5) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "量表题目数量不匹配，应为" + questionsIdList.size() + "题");
            }
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.select("is_submit").eq("school_id", schoolId).eq("task_id", task_id);
            TaskSchool taskSchool = taskSchoolMapper.selectOne(taskSchoolQueryWrapper);
            if (taskSchool.getIsSubmit() == 1) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "此任务已提交，不可导入");
            }
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> rightScaleData = new ArrayList<>();
            List<Map<String, Object>> allRightScaleData = new ArrayList<>();
            List<Map<String, Object>> errorScaleData = new ArrayList<>();
            List<Map<String, Object>> existScaleData = new ArrayList<>();
            List<Map<String, Object>> notExistScaleData = new ArrayList<>();
            List<String> studentCodes = new ArrayList<>();
            List<String> periodEnum = Arrays.asList(school1.getPeriod().split(","));
            int errorRows = 0;
            for (int i = 4; i <= rows; i ++) {
                boolean errorRow = false;   // 数据格式错误
                boolean existRow = false;   // 重复行提示（学籍号）
                boolean notExistRow = false;  // 学生不存在提示
                StringBuilder info = new StringBuilder();
                Row row2 = sheet.getRow(i);
                if (row2 != null) {
                    if (row2.getCell(0) == null || StringUtils.isEmpty(row2.getCell(0))) {
                        continue;
                    }
                    Map<String, Object> scaleData = new HashMap<>();
                    Map<String, Object> answer = new HashMap<>();
                    scaleData.put("row", i + 1);
                    // 读取学生信息
                    for (int j = 0; j < 5; j ++) {
                        Cell cell = row2.getCell(j);
                        String temp;
                        if (cell == null) {
                            temp = "";
                        } else {
                            cell.setCellType(CellType.STRING);
                            temp = cell.getStringCellValue();
                        }
                        String filedName = columnField.get(j);
                        switch (filedName) {
                            case "学生姓名":
                                scaleData.put("name", temp);
                                if (temp.equals("")) {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";学生姓名为空");
                                    } else {
                                        info = new StringBuilder("学生姓名为空");
                                    }
                                }
                                break;
                            case "学段":
                                scaleData.put("period", temp);
                                if (temp.equals("")) {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";学段为空");
                                    } else {
                                        info = new StringBuilder("学段为空");
                                    }
                                } else if (!periodEnum.contains(temp)) {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";该学校不存在").append(temp).append("学段");
                                    } else {
                                        info = new StringBuilder("该学校不存在" + temp + "学段");
                                    }
                                }
                                break;
                            case "年级":
                                scaleData.put("grade", temp);
                                if (temp.equals("")) {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";年级为空");
                                    } else {
                                        info = new StringBuilder("年级为空");
                                    }
                                } else if (!temp.startsWith("年级", 1)) {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";年级格式错误");
                                    }else {
                                        info = new StringBuilder("年级格式错误");
                                    }
                                } else if (temp.charAt(0) != '一' && temp.charAt(0) != '二' && temp.charAt(0) != '三'
                                        && temp.charAt(0) != '四' && temp.charAt(0) != '五' && temp.charAt(0) != '六'
                                        && temp.charAt(0) != '七' && temp.charAt(0) != '八' && temp.charAt(0) != '九') {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";年级格式错误");
                                    }else {
                                        info = new StringBuilder("年级格式错误");
                                    }
                                }
                                break;
                            case "班级":
                                scaleData.put("classes", temp);
                                if (temp.equals("")) {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";班级为空");
                                    } else {
                                        info = new StringBuilder("班级为空");
                                    }
                                }
                                break;
                            case "学籍号":
                                scaleData.put("studentCode", temp);
                                if (temp.equals("")) {
                                    errorRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";班级为空");
                                    } else {
                                        info = new StringBuilder("班级为空");
                                    }
                                } else if (studentCodes.contains(scaleData.get("studentCode").toString())) {  // 检验该条数据是否重复，检验学籍号
                                    existRow = true;
                                    errorRows += 1;
                                    if (!info.toString().equals("")) {
                                        info.append(";表格中已存在学籍号为").append(temp).append("的学生");
                                    } else {
                                        info = new StringBuilder("表格中已存在学籍号为" + temp + "的学生");
                                    }
                                } else {
                                    studentCodes.add(scaleData.get("studentCode").toString());
                                }
                                // 检查学生是否存在
                                if (!errorRow && !existRow) {  // 为减少查询次数，当该行没有出现error时，才进行查询
                                    QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                                    studentsQueryWrapper.select("name", "period", "grade", "classes", "school_id").eq("student_code", scaleData.get("studentCode").toString());
                                    Students students = studentsMapper.selectOne(studentsQueryWrapper);
                                    if (students == null || students.getSchoolId() == null) {
                                        notExistRow = true;
                                        errorRows += 1;
                                        if (!info.toString().equals("")) {
                                            info.append(";系统中不存在学籍号为").append(temp).append("的学生");
                                        } else {
                                            info = new StringBuilder("系统中不存在学籍号为" + temp + "的学生");
                                        }
                                    } else {
                                        // 检查学生和学校、姓名等是否对应
                                        if (!Objects.equals(students.getSchoolId(), schoolId.intValue())) {
                                            errorRow = true;
                                            errorRows += 1;
                                            if (!info.toString().equals("")) {
                                                info.append(";该学生系统中学校和所填学校不匹配");
                                            } else {
                                                info = new StringBuilder("该学生系统中学校和所填学校不匹配");
                                            }
                                        }
                                        if (!students.getName().equals(scaleData.get("name"))) {
                                            errorRow = true;
                                            errorRows += 1;
                                            if (!info.toString().equals("")) {
                                                info.append(";该学生系统中姓名和所填姓名不匹配");
                                            } else {
                                                info = new StringBuilder("该学生系统中姓名和所填姓名不匹配");
                                            }
                                        }
                                        if (!students.getPeriod().equals(scaleData.get("period"))) {
                                            errorRow = true;
                                            errorRows += 1;
                                            if (!info.toString().equals("")) {
                                                info.append(";该学生系统中学段和所填学段不匹配");
                                            } else {
                                                info = new StringBuilder("该学生系统中学段和所填学段不匹配");
                                            }
                                        }
                                        if (!students.getGrade().equals(scaleData.get("grade"))) {
                                            errorRow = true;
                                            errorRows += 1;
                                            if (!info.toString().equals("")) {
                                                info.append(";该学生系统中年级和所填年级不匹配");
                                            } else {
                                                info = new StringBuilder("该学生系统中年级和所填年级不匹配");
                                            }
                                        }
                                        if (!students.getClasses().equals(scaleData.get("classes"))) {
                                            errorRow = true;
                                            errorRows += 1;
                                            if (!info.toString().equals("")) {
                                                info.append(";该学生系统中班级和所填班级不匹配");
                                            } else {
                                                info = new StringBuilder("该学生系统中班级和所填班级不匹配");
                                            }
                                        }
                                    }
                                }
                                break;
                        }
                    }
                    // 读取题目答案
                    for (int k = 5; k < columnNum; k ++) {
                        Cell cell1 = row2.getCell(k);
                        if (cell1 != null) {
                            cell1.setCellType(CellType.NUMERIC);
                            if (cell1.getNumericCellValue() != 0 && cell1.getNumericCellValue() != 1) {
                                errorRow = true;
                                errorRows += 1;
                                if (!info.toString().equals("")) {
                                    info.append(";第").append(k - 4).append("题答案格式错误");
                                } else {
                                    info = new StringBuilder("第" + (k - 4) + "题答案格式错误");
                                }
                            }
                        } else {
                            errorRow = true;
                            errorRows += 1;
                            if (!info.toString().equals("")) {
                                info.append(";第").append(k - 4).append("题答案为空");
                            } else {
                                info = new StringBuilder("第" + (k - 4) + "题答案为空");
                            }
                        }
                        if (!errorRow && !existRow && !notExistRow) {
                            answer.put(String.valueOf(questionsIdList.get(k - 5)), cell1.getNumericCellValue());
                        }
                    }
                    scaleData.put("warn", info.toString());
                    if (errorRow) {
                        errorScaleData.add(scaleData);
                    } else if (existRow) {
                        existScaleData.add(scaleData);
                    } else if (notExistRow) {
                        notExistScaleData.add(scaleData);
                    } else {
                        Map<String, Object> rightData = new HashMap<>(scaleData);
                        rightData.put("answer", answer);
                        allRightScaleData.add(rightData);
                        rightScaleData.add(scaleData);
                    }
                }
            }
            result.put("taskId", task_id);
            result.put("scaleId", scale_id);
            result.put("schoolId", schoolId);
            result.put("errorData", errorScaleData);
            result.put("existData", existScaleData);
            result.put("notExistData", notExistScaleData);
            if (errorRows == 0) {  // 表格数据全部正确时，返回包括答案的正确数据，否则只返回学生信息
                result.put("rightData", allRightScaleData);
            } else {
                result.put("rightData", rightScaleData);
            }
//            log.info("read scale excel result:{}", JSON.toJSONString(result));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, "表格读取完成", result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> batchAddScaleData(JSONObject jsonObject) {
        try {
            Integer taskId = jsonObject.getInteger("taskId");
            Integer scaleId = jsonObject.getInteger("scaleId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            List<Map<String, Object>> scaleData = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("rightData");
            // 读取题号
            QueryWrapper<Questions> questionsQueryWrapper = new QueryWrapper<>();
            questionsQueryWrapper.select("id").eq("scale_id", scaleId).orderByAsc("id");
            List<Long> questionsId = questionsMapper.selectList(questionsQueryWrapper).stream().map(Questions::getId).collect(Collectors.toList());
            // 读取选项列表
            QueryWrapper<Options> optionsQueryWrapper = new QueryWrapper<>();
            optionsQueryWrapper.select("id", "question_id").in("question_id", questionsId).orderByAsc("question_id").orderByAsc("id");
            List<Options> optionsList = optionsMapper.selectList(optionsQueryWrapper);
            // 建立题号和选项号的映射，题号：选项数组；上传表格中选项0123顺序对应选项数组中的0123顺序
            HashMap<String, List<Integer>> questionOptions = new HashMap<>();
            for (Options options : optionsList) {
                if (!questionOptions.containsKey(options.getQuestionId().toString())) {
                    List<Integer> temp = new ArrayList<>();
                    temp.add(Math.toIntExact(options.getId()));
                    questionOptions.put(options.getQuestionId().toString(), temp);
                } else {
                    questionOptions.get(options.getQuestionId().toString()).add(Math.toIntExact(options.getId()));
                }
            }
            List<JSONObject> result = new ArrayList<>();
            // 将每一个学生的测评数据组织成需要的格式
            for (Object o : jsonArray) {
                Map<String, Object> temp = (Map<String, Object>) o;
                // 查询学生登录账号
                QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                studentsQueryWrapper.select("username").eq("student_code", temp.get("studentCode").toString());
                String username = studentsMapper.selectOne(studentsQueryWrapper).getUsername();
                // 查询用户id
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.select("id").eq("username", username);
                User user = userMapper.selectOne(userQueryWrapper);
                long userId = user.getId();
                JSONObject jsonObject1 = new JSONObject();
                List<Map<String, Integer>> submitAnswer = new ArrayList<>();
                Map<String, Integer> answer = (Map<String, Integer>)temp.get("answer");
                for (Map.Entry<String, Integer> entry: answer.entrySet()) {
                    Map<String, Integer> eveAnswer = new HashMap<>();
                    eveAnswer.put("id", Integer.valueOf(entry.getKey()));
                    eveAnswer.put("answer", questionOptions.get(entry.getKey()).get(entry.getValue()));
                    submitAnswer.add(eveAnswer);
                }
                jsonObject1.put("taskId", taskId);
                jsonObject1.put("userId", userId);
                jsonObject1.put("schoolId", schoolId);
                jsonObject1.put("source", 0);
                jsonObject1.put("duration", 300);
                jsonObject1.put("answerList", submitAnswer);
                result.add(jsonObject1);
            }
            for (JSONObject jsonObject2 : result) {
                taskService.taskResult(jsonObject2);
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }
}
