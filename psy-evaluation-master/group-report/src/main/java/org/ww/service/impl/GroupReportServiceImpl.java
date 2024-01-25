package org.ww.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.ww.domain.*;
import org.ww.exception.BusinessException;
import org.ww.mapper.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.GroupReportService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.CollationKey;
import java.text.Collator;
import java.util.*;

/**
 * @Author 13096
 * @Date 2022/11/15 20:45
 * @Version 1.0
 */
@Slf4j
@Service
public class GroupReportServiceImpl extends ServiceImpl<GroupReportMapper, GroupReport> implements GroupReportService {

    @Resource
    private GroupReportMapper groupReportMapper;

    @Resource
    private ScaleMapper scaleMapper;

    @Resource
    private StudentsMapper studentsMapper;

    @Resource
    private TaskSchoolMapper taskSchoolMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    TaskUserMapper taskUserMapper;

    @Resource
    MhtWarningMapper mhtWarningMapper;

    @Resource
    UserMapper userMapper;

    @Resource
    TeachersMapper teachersMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    ManagerMapper managerMapper;

    @Resource
    SchoolMapper schoolMapper;

    @Override
    public Wrapper<String> addGroupReport(JSONObject jsonObject){
        try {
            synchronized(this){
                QueryWrapper<GroupReport> groupReportQueryWrapper = new QueryWrapper<>();

                String reportName = jsonObject.getString("reportName");
                String textTask = jsonObject.getString("textTask");
                String textScale = jsonObject.getString("textScale");
                String reportScope = jsonObject.getString("reportScope");
                String classes = jsonObject.getString("classes");
                String userId = jsonObject.getString("userId");
                Integer schoolId = jsonObject.getInteger("schoolId");
                Integer taskId = jsonObject.getInteger("taskId");
                Integer scaleId = jsonObject.getInteger("scaleId");
                String submitPerson = jsonObject.getString("createPerson");
                String schoolName = jsonObject.getString("textSchool");
                String county = jsonObject.getString("county");
                Integer level = jsonObject.getInteger("level");

                GroupReport groupReport = new GroupReport();
                groupReport.setName(reportName);
                groupReport.setTaskName(textTask);
                groupReport.setScaleName(textScale);
                groupReport.setPeriod(classes);
                groupReport.setUserId(Integer.valueOf(userId));
                groupReport.setTaskId(taskId);
                groupReport.setScaleId(scaleId);
                groupReport.setSchoolId(schoolId);
                groupReport.setSchool(schoolName);
                groupReport.setCreatePerson(submitPerson);
                groupReport.setCreateTime(new Date());
                if(level!=null && level==4){
                    String period = jsonObject.getString("period");
                    groupReport.setPeriod(period);
                    groupReport.setSchoolId(0);

                    QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
                    taskSchoolQueryWrapper.select("school_id").eq("task_id",taskId).eq("county",county);
                    List<TaskSchool>  taskSchoolList = taskSchoolMapper.selectList(taskSchoolQueryWrapper);
                    JSONArray j = new JSONArray();
                    for (TaskSchool taskSchool:taskSchoolList) {
                        j.add(taskSchool.getSchoolId());
                    }
                    QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                    schoolQueryWrapper.select("name").eq("county",county).in("id",j);
                    List<School> schoolList = schoolMapper.selectList(schoolQueryWrapper);

                    String schoolsName = "";
                    for (int i = 0;i<schoolList.size();i++) {
                        School school = schoolList.get(i);
                        schoolsName += school.getName();
                        if(i!=schoolList.size()-1) {
                            schoolsName = schoolsName+"、";
                        }
                    }
                    groupReport.setSchool(schoolsName);
                }
                String textGrade = jsonObject.getString("textGrade");
                groupReport.setLevel(level);
                groupReport.setGrade(textGrade);

                // 添加团体报告
                groupReportMapper.insert(groupReport);
                log.info("add GroupReport:{}", JSON.toJSON(groupReport));

                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
            }
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> groupReportList(JSONObject jsonObject) {
        try {

            String reportName = jsonObject.getString("reportName");
            String textTask = jsonObject.getString("textTask");
            String textScale = jsonObject.getString("textScale");
            String createTime = jsonObject.getString("createTime");
            String classes = jsonObject.getString("classes");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer taskId = jsonObject.getInteger("taskId");
            Integer scaleId = jsonObject.getInteger("scaleId");
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            Integer level = jsonObject.getInteger("level");
            String county = jsonObject.getString("county");
            Integer userId = jsonObject.getInteger("userId");

            QueryWrapper<GroupReport> teachersQueryWrapper = new QueryWrapper<>();

            /*if(level==4){
                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.eq("county", county);
                List<School> schoolList =  schoolMapper.selectList(schoolQueryWrapper);
                List<Integer> schoolIdList = new ArrayList<>();
                for(School school : schoolList){
                    schoolIdList.add(school.getId().intValue());
                }
                teachersQueryWrapper.in("school_id", schoolIdList);
            }else{
                teachersQueryWrapper.eq("school_id", schoolId);
            }*/
            teachersQueryWrapper.eq("user_id", userId);
            if (reportName != null&&!"".equals(reportName)) {
                teachersQueryWrapper.like("name", reportName);
            }
            if (textTask != null&&!"".equals(textTask)) {
                teachersQueryWrapper.eq("task_name", textTask);
            }
            if (textScale != null) {
                teachersQueryWrapper.like("scale_name", textScale);
            }
            if (createTime != null&&!"".equals(createTime)) {
                teachersQueryWrapper.like("create_time", createTime);
            }
            teachersQueryWrapper.orderByDesc("create_time");
            IPage<GroupReport> groupReportIPage = groupReportMapper.selectPage(new Page<>(pageNum, pageSize), teachersQueryWrapper);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, groupReportIPage);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> updateGroupReport(GroupReport groupReport) {
        try {
            groupReportMapper.updateById(groupReport);
            log.info("update teacher:{}", JSON.toJSON(groupReport));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> deleteGroupReport(JSONObject jsonObject) {
        try {
            Integer id = jsonObject.getInteger("id");
            Integer name = jsonObject.getInteger("name");
            groupReportMapper.deleteById(id);
            log.info("delete GroupReport:{}", name);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Map<String, Object>> viewGroupReport(JSONObject jsonObject) {
        try {

            //GroupReport团体报告相关信息
            Map<String, Object> report = new HashMap<>();
            Integer id = jsonObject.getInteger("id");
            Integer userId = jsonObject.getInteger("userId");
            Integer taskId = jsonObject.getInteger("taskId");
            Integer scaleId = jsonObject.getInteger("scaleId");
            Integer schoolId = jsonObject.getInteger("schoolId");
            String reportName = jsonObject.getString("reportName");
            String scaleName = jsonObject.getString("scale");
            String period1 = jsonObject.getString("period");
            String submitTime = jsonObject.getString("createTime");
            String submitPerson = jsonObject.getString("createPerson");
            Integer level = jsonObject.getInteger("level");
            String county = jsonObject.getString("county");

            JSONArray schoolIds = new JSONArray();
            GroupReport groupReport = groupReportMapper.selectById(id);
            if(schoolId!=null) {
                schoolIds.add(schoolId);
            } else if (groupReport.getSchoolId()!=null) {
                schoolIds.add(groupReport.getSchoolId());
            }

            if(level==4){
                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.select("id").eq("county",county);
                List<School> schoolList = schoolMapper.selectList(schoolQueryWrapper);
                for (School school:schoolList) {
                    schoolIds.add( school.getId().intValue());
                }
            }

            JSONArray period = null;
            JSONArray greadeIn = null;
            JSONArray classIn = null;

            period1 = groupReport.getPeriod();
            String [] period2 =  period1.split(",");
            for(String p:period2){
                if(p.endsWith("级")){
                    if(greadeIn==null){
                        greadeIn = new JSONArray();
                    }
                    greadeIn.add(p);
                }else if(p.endsWith("班")){
                    if(classIn==null){
                        classIn = new JSONArray();
                    }
                    classIn.add(p);
                }else{
                    if(period==null){
                        period = new JSONArray();
                    }
                    period.add(p);
                }
            }
            //List list = Arrays.asList(period1.split(","));


            String school = groupReport.getSchool();
            String[] schools = school.split("、");
            report.put("school",school);
            report.put("schoolLength",schools.length);
            report.put("period",groupReport.getPeriod());

            report.put("reportName",reportName);
            report.put("scaleName",scaleName);
            report.put("submitTime",submitTime);

            /* QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.select("name", "province", "city", "county").eq("id", groupReportDto.getSchoolId());
            School school = schoolMapper.selectOne(schoolQueryWrapper);
            Integer studentNumbers = school.getStudentNumbers();  //测评学生数量*/

            Scale scale = scaleMapper.selectById(scaleId);
            Task task = taskMapper.selectById(taskId);
            Date start = task.getStartDate();  //开始时间
            Date end = task.getEndDate();  //结束时间
            String taskName = task.getName(); //测评任务
            report.put("测评工具",scale.getIntroduction());
            report.put("start",start);
            report.put("end",end);
            report.put("taskName",taskName);
            report.put("scaleName",scale.getName());
            report.put("issuedUnit",task.getIssuedUnit());

            Map<String, List<Integer>> periodMap = new HashMap<>();
            Map<String,BigDecimal> map = studentsMapper.selectStudentsSexCount(schoolIds, task.getSchoolYear());
            Integer boysSumNum = map.get("boys").intValue();
            Integer grilsSumNum = map.get("girls").intValue();//测评数量
            Integer totals = boysSumNum + grilsSumNum;

            report.put("boysNum", boysSumNum);
            report.put("grilsNum", grilsSumNum);
            report.put("totals", totals);

            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.eq("school_id",schoolId);
            taskSchoolQueryWrapper.eq("task_id",taskId);
            List<Map<String, Object>>  taskSchoolList = mhtWarningMapper.getTaskSchoolInfo( taskId,schoolIds,null);
            report.put("taskSchoolList",taskSchoolList);

            //
            List<Map<String, Object>> studentGradeInfo = mhtWarningMapper.getStudentGradeInfo(period,greadeIn,classIn,taskId,schoolIds,null);
            Integer warningBoys = 0,warningGrils = 0;  //预警数量
            Integer boysNum = 0,grilsNum = 0;//总分数量
            Integer boysScore = 0,grilsScore = 0;//总分数量
            Map<String, Integer> dimensionWarningMap = new HashMap<>();
            for (Map<String, Object> student : studentGradeInfo) {

                Integer scores = 0;
                //维度检出情况
                String result = student.get("result").toString();
                String [] results = result.split(",");
                if (scaleName.equals("中小学生心理健康普查(MHT)")) {

                    for (int i =0;i<results.length;i++) {
                        if(1 != (Integer) student.get("valid"))
                        {
                            continue;
                        }

                        String [] ress = results[i].split(":");
                        if(Integer.valueOf(ress[1])>=8){
                            if("1".equals(ress[0])){
                                Integer nums =  dimensionWarningMap.get("学习焦虑");
                                if(nums==null){
                                    dimensionWarningMap.put("学习焦虑",1);
                                }else {
                                    dimensionWarningMap.put("学习焦虑", nums+1);
                                }
                            } else if ("2".equals(ress[0])) {
                                Integer nums =  dimensionWarningMap.get("对人焦虑");
                                if(nums==null){
                                    dimensionWarningMap.put("对人焦虑",1);
                                }else {
                                    dimensionWarningMap.put("对人焦虑", nums+1);
                                }
                            } else if ("3".equals(ress[0])) {
                                Integer nums =  dimensionWarningMap.get("孤独倾向");
                                if(nums==null){
                                    dimensionWarningMap.put("孤独倾向",1);
                                }else {
                                    dimensionWarningMap.put("孤独倾向", nums+1);
                                }
                            } else if ("4".equals(ress[0])) {
                                Integer nums =  dimensionWarningMap.get("自责倾向");
                                if(nums==null){
                                    dimensionWarningMap.put("自责倾向",1);
                                }else{
                                    dimensionWarningMap.put("自责倾向",nums+1);
                                }
                            } else if ("5".equals(ress[0])) {
                                Integer nums =  dimensionWarningMap.get("过敏倾向");
                                if(nums==null){
                                    dimensionWarningMap.put("过敏倾向",1);
                                }else {
                                    dimensionWarningMap.put("过敏倾向",nums+1);
                                }
                            } else if ("6".equals(ress[0])) {
                                Integer nums =  dimensionWarningMap.get("身体症状");
                                if(nums==null){
                                    dimensionWarningMap.put("身体症状",1);
                                }else {
                                    dimensionWarningMap.put("身体症状", nums+1);
                                }
                            } else if ("7".equals(ress[0])) {
                                Integer nums =  dimensionWarningMap.get("恐怖倾向");
                                if(nums==null){
                                    dimensionWarningMap.put("恐怖倾向",1);
                                }else {
                                    dimensionWarningMap.put("恐怖倾向",nums+1);
                                }
                            } else if ("8".equals(ress[0])) {
                                Integer nums =  dimensionWarningMap.get("冲动倾向");
                                if(nums==null){
                                    dimensionWarningMap.put("冲动倾向",1);
                                }else {
                                    dimensionWarningMap.put("冲动倾向", nums+1);
                                }
                            }
                        }
                    }
                    report.put("dimensionWarningMap",dimensionWarningMap);

                    String [] ress = results[9].split(":");
                    scores = Integer.valueOf(ress[1]);

                }
                else if (scaleName.equals("抑郁自评量表(SDS)")) { //SDS
                    String [] ress = results[0].split(":");
                    scores = Integer.valueOf(ress[1]);
                }
                else if (scaleName.equals("焦虑自评量表(SAS)")) { //SAS
                    String [] ress = results[0].split(":");
                    scores = Integer.valueOf(ress[1]);
                }
                else if (scaleName.equals("Sarason考试焦虑量表(TAS)")) { //TAS
                    String [] ress = results[0].split(":");
                    scores = Integer.valueOf(ress[1]);
                }
                else if (scaleName.equals("90项症状自查量表(SCL-90)")) { //SCL90
                    for (int i = 0; i < results.length; i++) {
                        String[] ress = results[i].split(":");
                        boolean warning = false;
                        Integer moduleScore = Integer.valueOf(ress[1]);
                        String moduleName = null;
                        if (i == 0 && moduleScore > 12) {
                            moduleName = "躯体化";
                            warning = true;
                        }
                        else if (i == 1 && moduleScore > 10) {
                            moduleName = "强迫症状";
                            warning = true;
                        }
                        else if (i == 2 && moduleScore > 9) {
                            moduleName = "人际关系敏感";
                            warning = true;
                        }
                        else if (i == 3 && moduleScore > 13) {
                            moduleName = "抑郁";
                            warning = true;
                        }
                        else if (i == 4 && moduleScore > 10) {
                            moduleName = "焦虑";
                            warning = true;
                        }
                        else if (i == 5 && moduleScore > 6) {
                            moduleName = "敌对";
                            warning = true;
                        }
                        else if (i == 6 && moduleScore > 7) {
                            moduleName = "恐怖";
                            warning = true;
                        }
                        else if (i == 7 && moduleScore > 6) {
                            moduleName = "偏执";
                            warning = true;
                        }
                        else if (i == 8 && moduleScore > 10) {
                            moduleName = "精神病性";
                            warning = true;
                        }
                        else if (i == 9 && moduleScore > 7) {
                            moduleName = "其它";
                            warning = true;
                        }
                        else if (i == 10 && moduleScore > 43) {
                            moduleName = "阳性项目";
                            warning = true;
                        }

                        if (warning) {
                            Integer nums =  dimensionWarningMap.get(moduleName);
                            if(nums==null){
                                dimensionWarningMap.put(moduleName,1);
                            }else {
                                dimensionWarningMap.put(moduleName, nums+1);
                            }
                        }
                    }
                    report.put("dimensionWarningMap",dimensionWarningMap);
                    String [] ress = results[11].split(":");
                    scores = Integer.valueOf(ress[1]);
                }
                else if (scaleName.equals("中文网络成瘾量表（CIAS-R）")) { //CIAS
                    String [] ress = results[0].split(":");
                    scores = Integer.valueOf(ress[1]);
                }
                else if (scaleName.equals("广泛性焦虑量表(GAD-7)")) { //GAD-7
                    String [] ress = results[0].split(":");
                    scores = Integer.valueOf(ress[1]);
                }
                else if (scaleName.equals("中学生心理健康量表-MSSMHS")) { //MSSMHS
                    for (int i = 1; i < results.length; i++) {  //0处为总分
                        String[] ress = results[i].split(":");
                        if(Integer.valueOf(ress[1]) >= 3) {
                            String moduleName = null;
                            if (i == 1) moduleName = "强迫症状";
                            else if (i == 2) moduleName = "偏执";
                            else if (i == 3) moduleName = "敌对";
                            else if (i == 4) moduleName = "人际关系紧张与敏感";
                            else if (i == 5) moduleName = "抑郁";
                            else if (i == 6) moduleName = "焦虑";
                            else if (i == 7) moduleName = "学习压力";
                            else if (i == 8) moduleName = "适应不良";
                            else if (i == 9) moduleName = "情绪不平衡";
                            else if (i == 10) moduleName = "心理不平衡";
                            Integer nums = dimensionWarningMap.get(moduleName);
                            if (nums == null) {
                                dimensionWarningMap.put(moduleName, 1);
                            } else {
                                dimensionWarningMap.put(moduleName, nums + 1);
                            }
                        }
                    }
                    report.put("dimensionWarningMap",dimensionWarningMap);
                    String [] ress = results[0].split(":");
                    scores = Integer.valueOf(ress[1]);
                }

                String sex = student.get("sex").toString();
                Object per = student.get("period");
                Object grade = student.get("grade");

                if(periodMap.get(per+grade.toString())==null){
                    periodMap.put(per+grade.toString(),Arrays.asList(0,0,0));
                }

                if("男".equals(sex)){
                    if(1==(Integer) student.get("warning") && 1 == (Integer) student.get("valid")){
                        warningBoys++;
                    }
                    boysNum++;
                    boysScore+=scores;

                    periodMap.get(per+grade.toString()).set(1, periodMap.get(per+grade.toString()).get(1)+scores);

                } else if ("女".equals(sex)) {
                    if(1==(Integer) student.get("warning") && 1 == (Integer) student.get("valid")){
                        warningGrils++;
                    }
                    grilsNum++;
                    grilsScore+=scores;
                    periodMap.get(per+grade.toString()).set(2, periodMap.get(per+grade.toString()).get(2)+scores);

                }
                periodMap.get(per+grade.toString()).set(0,boysNum+grilsNum);
            }
            Integer scoretotals = boysScore+grilsScore; // 总分总共数量
            Integer warnNumbers = warningBoys+warningGrils; //预警总共数量
            Integer totalValidNums = boysNum+grilsNum;  //测评学生数量

            report.put("periodMap",periodMap);

            java.text.DecimalFormat df=new java.text.DecimalFormat("##%");//传入格式模板
            QueryWrapper<TaskSchool> taskSchoolWrapper = new QueryWrapper<>();
            taskSchoolWrapper.in("school_id",schoolIds).eq("task_id",taskId);
            String completePercent = "0%",validPercent = "0%",warningPercent = "0%";
            Integer completeNumbers = 0,validNumbers =0,warningNumbers=0;

            List<TaskSchool> taskSchoolList1 = taskSchoolMapper.selectList(taskSchoolWrapper);
            for (TaskSchool taskSchool:taskSchoolList1) {
                completeNumbers += taskSchool.getCompleteNumbers(); //完成测评人数
                validNumbers += taskSchool.getValidNumbers(); //有效测评数量
                warningNumbers += taskSchool.getWarningNumbers();//预警人数
            }
            if(completeNumbers!=0&&totals!=0)  {
                completePercent = percentPrettyToString((float)completeNumbers/(float)totals);
            }
            if(validNumbers!=0&&totals!=0)  {
                validPercent = percentPrettyToString((float)validNumbers/(float)totals);;
            }
            if(warningNumbers!=0&&validNumbers!=0)  {
                warningPercent = percentPrettyToString((float)warningNumbers/(float)validNumbers );//预警比例
            }

            report.put("completeNumbers",completeNumbers);
            report.put("completePercent",completePercent);
            report.put("validNumbers",validNumbers);
            report.put("validPercent",validPercent);
            report.put("warningNumbers",warningNumbers);
            report.put("warningPercent",warningPercent);
            String warningBoysPercent = "0%",warningGrilsPercent = "0%";
            if(warningBoys!=0&&warningNumbers!=0)  {
                warningBoysPercent = percentPrettyToString((float)warningBoys/(float)warningNumbers);  //男预警占比情况
            }
            if(warningGrils!=0&&warningNumbers!=0)  {
                warningGrilsPercent = percentPrettyToString((float)warningGrils/(float)warningNumbers);//女预警占比情况
            }
            report.put("warningBoys",warningBoys);
            report.put("warningGrils",warningGrils);
            report.put("warnNumbers",warnNumbers);
            report.put("warningBoysPercent",warningBoysPercent);
            report.put("warningGrilsPercent",warningGrilsPercent);


            //年级检出情况
            List<Map<String, Object>> gradeWarningMap = mhtWarningMapper.getStudentGradeWarningInfo(period,greadeIn,classIn,userId,taskId,schoolIds);
            gradeWarningMap.sort(new Comparator<Map<String, Object>>() {

                Collator collator = Collator.getInstance();
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    String g1 = "" + o1.get("grade");
                    String g2 = "" + o2.get("grade");

                    if(StringUtils.isBlank(g1) || StringUtils.isBlank(g2))
                    {
                        return 0;
                    }

                    g1 = g1.replaceAll("小学", "11")
                            .replaceAll("初中", "22")
                            .replaceAll("高中", "33")
                            .replaceAll("职高", "44")
                            .replaceAll("一", "1")
                            .replaceAll("二", "2")
                            .replaceAll("三", "3")
                            .replaceAll("四", "4")
                            .replaceAll("五", "5")
                            .replaceAll("六", "6");

                    g2 = g2.replaceAll("小学", "11")
                            .replaceAll("初中", "22")
                            .replaceAll("高中", "33")
                            .replaceAll("职高", "44")
                            .replaceAll("一", "1")
                            .replaceAll("二", "2")
                            .replaceAll("三", "3")
                            .replaceAll("四", "4")
                            .replaceAll("五", "5")
                            .replaceAll("六", "6");

                    CollationKey key1 = collator.getCollationKey(g1);
                    CollationKey key2 = collator.getCollationKey(g2);
                    return key1.compareTo(key2);
                }
            });
            report.put("gradeWarningMap",gradeWarningMap);

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE,report);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return WrapMapper.error();
        }
    }

    public String percentPrettyToString(float percent) {

        java.text.DecimalFormat df=new java.text.DecimalFormat("##%");//传
        return df.format(percentPretty(percent));
    }
    public float percentPretty(float percent) {
        if(percent > 1){
            log.warn("percentage unexpected : {}" + percent);
            return 1;
        }else if (percent < 0){
            log.warn("percentage unexpected : {}" + percent);
            return 0;
        }else {
            return percent;
        }
    }
    @Override
    public Wrapper<Object> getSchoolsByCounty(JSONObject jsonObject) {
        try {
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            String county = jsonObject.getString("county");

            schoolQueryWrapper.eq("county", county);
            List<School>  schools = schoolMapper.selectList(schoolQueryWrapper);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, schools);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> getCountyTasks(JSONObject jsonObject) {
        try {
            String county = jsonObject.getString("county");

            List<Map<String, Object>>  result = mhtWarningMapper.getTaskByCounty(county);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
            return WrapMapper.error();
        }
    }


}
