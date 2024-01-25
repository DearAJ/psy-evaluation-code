package org.ww.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.ww.constants.Constants;
import org.ww.domain.*;
import org.ww.exception.BusinessException;
import org.ww.mapper.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.SchoolService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author 13096
 * @Date 2022/2/11 9:46
 * @Version 1.0
 */
@Slf4j
@Service
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private UnitNumberMapper unitNumberMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private ManagerMapper managerMapper;

    @Resource
    private SchoolClassesMapper schoolClassesMapper;

    @Resource
    private StudentsMapper studentsMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private TaskSchoolMapper taskSchoolMapper;

    @Resource
    private SchoolRoleMapper schoolRoleMapper;

    @Resource
    private TeachersMapper teachersMapper;

    @Override
    public Wrapper<String> addSchool(School school) {
        try {
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("name", school.getName())
                    .and(schoolQueryWrapper -> schoolQueryWrapper.eq("province", school.getProvince()))
                    .and(schoolQueryWrapper -> schoolQueryWrapper.eq("city", school.getCity()))
                    .and(schoolQueryWrapper -> schoolQueryWrapper.eq("county", school.getCounty()));
            int count = schoolMapper.selectCount(queryWrapper);
            if (count != 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "学校名称已存在");
            }
            // 生成学校编号   3位学校编号递增，由于是创建账号，肯定是第一个账号，则最后一位为1
            String newCode = createSchoolLogin(school.getProvince(), school.getCity(), school.getCounty());
            school.setSchoolLogin(newCode);
            // 学生账户数写死，待修改
            school.setStudentAccountNumbers(school.getStudentNumbers());
            schoolMapper.insert(school);
            log.info("add School:{}", JSON.toJSON(school));
            // 创建学校管理员账户
            User user = new User();
            user.setUsername(newCode);
            user.setPassword(new BCryptPasswordEncoder().encode("123456"));
            user.setEnabled(0);
            user.setAvatar(Constants.avatarManager);
            userMapper.insert(user);
            log.info("add User:{}", JSON.toJSON(user));
            // 创建学校管理员用户角色
            Role role = new Role();
            role.setUsername(newCode);
            role.setRole("学校管理员");
            role.setLevel(3);
            roleMapper.insert(role);
            log.info("add Role:{}", JSON.toJSON(role));
            // 添加管理员用户和所属对应
            Manager manager = new Manager();
            manager.setUsername(newCode);
            manager.setName(school.getChargePerson());
            manager.setPhone(school.getPhone());
            manager.setEmail("");
            manager.setSex("");
            manager.setProvince(school.getProvince());
            manager.setCity(school.getCity());
            manager.setCounty(school.getCounty());
            manager.setSchool(school.getName());
            managerMapper.insert(manager);
            log.info("add Manager:{}", JSON.toJSON(manager));
            // 添加school_role
            SchoolRole schoolRole = new SchoolRole();
            schoolRole.setSchoolId(school.getId());
            schoolRole.setUsername(school.getSchoolLogin());
            schoolRole.setRole("学校管理员");
            schoolRole.setUnit(school.getName());
            schoolRole.setName(school.getChargePerson());
            schoolRole.setPhone(school.getPhone());
            schoolRoleMapper.insert(schoolRole);
            log.info("add SchoolRole:{}", JSON.toJSON(schoolRole));
            // 添加任务
//            String provinceCity = school.getProvince() + school.getCity();
//            QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
//            taskQueryWrapper.select("id").eq("issued_unit", provinceCity);
//            Task task = taskMapper.selectOne(taskQueryWrapper);
//            if (task != null) {
//                TaskSchool taskSchool = new TaskSchool();
//                taskSchool.setTaskId(Math.toIntExact(task.getId()));
//                taskSchool.setProvince(school.getProvince());
//                taskSchool.setCity(school.getCity());
//                taskSchool.setCounty(school.getCounty());
//                taskSchool.setSchoolId(Math.toIntExact(school.getId()));
//                taskSchool.setGrade("");
//                taskSchool.setClasses("");
//                taskSchool.setNumbers(0);
//                taskSchool.setCompleteNumbers(0);
//                taskSchool.setIsSubmit(0);
//                taskSchool.setWarningNumbers(0);
//                taskSchoolMapper.insert(taskSchool);
//                log.info("insert task_school:{}", JSON.toJSONString(school));
//            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public String createSchoolLogin(String province, String city, String county) {
        String unit = city + county;
        QueryWrapper<UnitNumber> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.select("number").eq("unit", unit);
        UnitNumber unitNumber = unitNumberMapper.selectOne(queryWrapper1);
        String newCode = unitNumber.getNumber();
        School lastSchool = schoolMapper.getLatestSchool(province, city, county);
        if (lastSchool != null) {
            int tempNewCode = Integer.parseInt(lastSchool.getSchoolLogin().substring(4, 7)) + 1;
            String tempNewCode1 = String.valueOf(tempNewCode);
            if (tempNewCode1.length() == 1) {
                newCode += "00" + tempNewCode1 + "1";
            }else if (tempNewCode1.length() == 2) {
                newCode += "0" + tempNewCode1 + "1";
            }else {
                newCode += tempNewCode1 + "1";
            }
        } else {
            newCode += "0011";
        }
        return newCode;
    }

    @Override
    public Wrapper<IPage<School>> schoolList(JSONObject jsonObject) {
        try {
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("province", jsonObject.getString("province"));
            if (jsonObject.containsKey("city")) {
                queryWrapper.eq("city", jsonObject.getString("city"));
            }
            if (jsonObject.containsKey("county")) {
                queryWrapper.eq("county", jsonObject.getString("county"));
            }
            if (jsonObject.containsKey("school")) {
                queryWrapper.like("name", jsonObject.getString("school"));
            }
            queryWrapper.orderByDesc("create_time");

//            List<School> schoolList = schoolMapper.selectList(queryWrapper);
            Integer pageNum = jsonObject.getInteger("pageNum");
            Integer pageSize = jsonObject.getInteger("pageSize");
            IPage<School> schoolPage = schoolMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, schoolPage);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> schoolListAll(JSONObject jsonObject) {
        try {
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id", "name", "period").eq("province", jsonObject.getString("province"));
            if (jsonObject.containsKey("city")) {
                queryWrapper.eq("city", jsonObject.getString("city"));
            }
            if (jsonObject.containsKey("county")) {
                queryWrapper.eq("county", jsonObject.getString("county"));
            }
            if (jsonObject.containsKey("school")) {
                queryWrapper.like("name", jsonObject.getString("school"));
            }
            queryWrapper.orderByDesc("create_time");

            List<School> schoolList = schoolMapper.selectList(queryWrapper);

            List<Map<String, Object>> result = new ArrayList<>();
            if (jsonObject.containsKey("period")) {
                List<String> periods = jsonObject.getJSONArray("period").toJavaList(String.class);
                for (School school : schoolList) {
                    boolean flag = false;
                    List<String> curPer = Arrays.asList(school.getPeriod().split(","));
                    for (String per : periods) {
                        if (curPer.contains(per)) {
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        Map<String, Object> temp = new HashMap<>();
                        temp.put("id", school.getId());
                        temp.put("name", school.getName());
                        result.add(temp);
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
    public Wrapper<String> updateSchool(School school) {
        try {
            School schoolOld = schoolMapper.selectById(school.getId());
            if (!schoolOld.getName().equals(school.getName()) || !schoolOld.getChargePerson().equals(school.getChargePerson()) || !schoolOld.getPhone().equals(school.getPhone())) {
                if (!schoolOld.getName().equals(school.getName())) {
                    schoolRoleMapper.updateSchoolName(school.getId(), school.getName());
                    managerMapper.updateSchoolName(school.getSchoolLogin(), school.getName());
                }
                if (!schoolOld.getChargePerson().equals(school.getChargePerson()) || !schoolOld.getPhone().equals(school.getPhone())) {
                    schoolRoleMapper.updateSchoolManagerInfo(school.getSchoolLogin(), school.getChargePerson(), school.getPhone());
                    managerMapper.updateSchoolManagerInfo(school.getSchoolLogin(), school.getChargePerson(), school.getPhone());
                }
            }
            schoolMapper.updateById(school);
            log.info("update school:{}", JSON.toJSON(school));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Integer> deleteSchool(JSONObject jsonObject) {
        try {
//            List<Integer> ids = new ArrayList<>();
//            if (jsonObject.size() == 1) {
//                if (jsonObject.get("id") instanceof Integer) {
//                    ids.add(jsonObject.getInteger("id"));
//                } else {
//                    ids.addAll(jsonObject.getJSONArray("id").toJavaList(Integer.class));
//                }
//            } else {
//                ids.addAll(jsonObject.getJSONArray("id").toJavaList(Integer.class));
//            }
            Long id = jsonObject.getLongValue("id");
            QueryWrapper<TaskSchool> taskSchoolQueryWrapper = new QueryWrapper<>();
            taskSchoolQueryWrapper.eq("school_id", id);
            int count = taskSchoolMapper.selectCount(taskSchoolQueryWrapper);
            if (count != 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "该学校存在关联任务，不可删除");
            }
            // 获取学校省市县等信息
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("province", "city", "county", "name").eq("id", id);
            List<School> schoolList = schoolMapper.selectList(queryWrapper);
            // 删除学校关联账号
            List<String> accounts = new ArrayList<>();
            for (School school : schoolList) {
                QueryWrapper<Manager> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.select("username").eq("province", school.getProvince())
                        .and(schoolQueryWrapper -> schoolQueryWrapper.eq("city", school.getCity()))
                        .and(schoolQueryWrapper -> schoolQueryWrapper.eq("county", school.getCounty()))
                        .and(schoolQueryWrapper -> schoolQueryWrapper.eq("school", school.getName()));
                List<Manager> managerList = managerMapper.selectList(queryWrapper1);
                accounts.addAll(managerList.stream().map(Manager::getUsername).collect(Collectors.toList()));
            }
            // 删除manager表信息
            QueryWrapper<Manager> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.in("username", accounts);
            managerMapper.delete(queryWrapper2);
            log.info("delete Manager list:{}", JSON.toJSON(accounts));
            // 删除学校
            int rowsSchool = schoolMapper.deleteById(id);
            log.info("delete school list:{}", id);
            // 查询学校关联学生
            QueryWrapper<Students> queryWrapper5 = new QueryWrapper<>();
            queryWrapper5.select("id", "username").eq("school_id", id);
            List<Students> studentsList = studentsMapper.selectList(queryWrapper5);
            List<Long> studentIdList = studentsList.stream().map(Students::getId).collect(Collectors.toList());
            List<String> studentUsernameList = studentsList.stream().map(Students::getUsername).collect(Collectors.toList());
            accounts.addAll(studentUsernameList);
            // 删除学生
            if (studentIdList.size() != 0) {
                studentsMapper.deleteBatchIds(studentIdList);
                log.info("delete students list:{}", JSON.toJSON(studentIdList));
            }
            // 删除用户表 学生和管理员
            userMapper.deleteUsersByUsernames(accounts);
            log.info("delete users list:{}", JSON.toJSON(accounts));
            // 删除学生和管理员角色
            roleMapper.deleteRoleByUsernames(accounts);
            log.info("delete role list:{}", JSON.toJSON(accounts));
            // 删除学校班级表
            schoolClassesMapper.deleteClassesById(id);
            log.info("delete classes by school list:{}", JSON.toJSON(accounts));
            // 删除学校角色
            schoolRoleMapper.deleteByUsernames(accounts);
            log.info("delete school roles by username list:{}", JSON.toJSON(accounts));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, rowsSchool);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> verifySchoolExcel(MultipartFile schoolExcel, String province, String city, String county) {
        try {
            if (schoolExcel == null || schoolExcel.getSize() == 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空");
            }
            String fileName = schoolExcel.getOriginalFilename();
            if (fileName == null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件名为空");
            }
            if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件格式不正确");
            }
            boolean isExcel2007 = !fileName.matches("^.+\\.(?i)(xls)$");
            InputStream inputStream = schoolExcel.getInputStream();
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
            Row row = sheet.getRow(2);
            Map<String, Integer> fieldColumn = new HashMap<>();
            Map<Integer, String> columnField = new HashMap<>();
            int columnNum = row.getPhysicalNumberOfCells();
            // 添加列名和列号对应
            for (int i = 0; i < columnNum; i ++) {
                fieldColumn.put(row.getCell(i).getStringCellValue(), i);
                columnField.put(i, row.getCell(i).getStringCellValue());
            }
            if (!fieldColumn.containsKey("学校名称") || !fieldColumn.containsKey("学校地址") || !fieldColumn.containsKey("所含学段")
                    || !fieldColumn.containsKey("负责人") || !fieldColumn.containsKey("联系电话")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "模板列不匹配，请使用模板上传数据");
            }
            if (rows <= 2) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "表格内无学校数据");
            }
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> rightSchoolList = new ArrayList<>();
            List<Map<String, Object>> errorSchoolList = new ArrayList<>();
            List<Map<String, Object>> existSchoolList = new ArrayList<>();
            List<Map<String, Object>> baseExistSchoolList = new ArrayList<>();
            String[] periodEnum = {"小学", "初中", "高中", "职高", "其他"};
            List<String> schools = new ArrayList<>();
            for (int i = 3; i <= rows; i ++) {
                boolean errorRow = false;   // 数据格式错误
                boolean existRow = false;   // 重复行提示
                boolean baseExistRow = false;  // 学校已存在提示
                StringBuilder info = new StringBuilder();
                Row row1 = sheet.getRow(i);
                if (row1 != null) {
                    if (row1.getCell(0) == null || StringUtils.isEmpty(row1.getCell(0))) {
                        continue;
                    }
                    boolean isError = false;
                    Map<String, Object> school = new HashMap<>();
                    school.put("row", i + 1);
                    school.put("province", province);
                    school.put("city", city);
                    school.put("county", county);
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
                            case "学校名称":
                                school.put("name", temp);
                                break;
                            case "学校地址":
                                school.put("address", temp);
                                break;
                            case "所含学段":
                                school.put("period", temp);
                                break;
                            case "负责人":
                                school.put("chargePerson", temp);
                                break;
                            case "联系电话":
                                school.put("phone", temp);
                                break;
                        }
                    }
                    // 检验某个cell是否为空值
                    for (Map.Entry<String, Object> entry : school.entrySet()) {
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
                    school.put("studentNumbers", 0);
                    school.put("studentAccountNumbers", 0);
                    // 检验该条数据是否重复，只检验学校名称
                    if (schools.contains(school.get("name").toString())) {
                        existRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";表格中已存在重复的").append(school.get("name").toString());
                        } else {
                            info = new StringBuilder("表格中已存在重复的").append(school.get("name").toString());
                        }
                    } else {
                        schools.add(school.get("name").toString());
                    }

                    // 检验手机号
                    String regExp = "^(1)\\d{10}$";
                    Pattern pattern = Pattern.compile(regExp);
                    Matcher matcher = pattern.matcher(school.get("phone").toString());
                    if (!matcher.matches()) {
                        errorRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";手机号格式错误");
                        } else {
                            info = new StringBuilder("手机号格式错误");
                        }
                    }
                    // 检验学段格式
                    if (school.get("period").toString().contains("，")) {
                        String[] temp_period = school.get("period").toString().split("，");
                        for (String period1 : temp_period) {
                            if (!Arrays.asList(periodEnum).contains(period1)) {         // 如果学段不是几个学段内的，加入错误提示列表
                                errorRow = true;
                                if (!info.toString().equals("")) {
                                    info.append(";学段格式错误");
                                } else {
                                    info = new StringBuilder("学段格式错误");
                                }
                                isError = true;
                                break;
                            }
                        }
                        if (!isError) {  // 将中文逗号替换为英文逗号
                            school.put("period", school.get("period").toString().replace("，", ","));
                        }
                    } else {
                        if (!Arrays.asList(periodEnum).contains(school.get("period").toString())) {
                            errorRow = true;
                            if (!info.toString().equals("")) {
                                info.append(";学段格式错误");
                            } else {
                                info = new StringBuilder("学段格式错误");
                            }
                        }
                    }

                    // 检验数据库中是否存在该学校名称
                    if (!errorRow && !existRow) {
                        QueryWrapper<School> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("name", school.get("name").toString()).eq("province", province)
                                .eq("city", city).eq("county", county);
                        int count = schoolMapper.selectCount(queryWrapper);
                        if (count != 0) {
                            baseExistRow = true;
                            if (!info.toString().equals("")) {
                                info.append(";同区县中已存在同名学校");
                            } else {
                                info = new StringBuilder("同区县中已存在同名学校");
                            }
                        }
                    }
                    if (errorRow) {
                        school.put("warn", info);
                        errorSchoolList.add(school);
                    }  else if (existRow) {
                        school.put("warn", info);
                        existSchoolList.add(school);
                    } else if (baseExistRow) {
                        school.put("warn", info);
                        baseExistSchoolList.add(school);
                    } else {
                        rightSchoolList.add(school);
                    }
                }
            }
            result.put("errorSchoolList", errorSchoolList);
            result.put("rightSchoolList", rightSchoolList);
            result.put("existSchoolList", existSchoolList);
            result.put("baseExistSchoolList", baseExistSchoolList);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, "表格读取完成", JSON.toJSON(result));
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> addSchools(List<School> schoolList) {
        try {
            int listCur = 0;
            String province = schoolList.get(0).getProvince();
            String city = schoolList.get(0).getCity();
            String county = schoolList.get(0).getCounty();
            String unit = city + county;
            QueryWrapper<UnitNumber> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("number").eq("unit", unit);
            UnitNumber unitNumber = unitNumberMapper.selectOne(queryWrapper1);
            String newCode = unitNumber.getNumber();
            QueryWrapper<School> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.select("school_login").eq("province", province)
                    .and(schoolQueryWrapper -> schoolQueryWrapper.eq("city", city))
                    .and(schoolQueryWrapper -> schoolQueryWrapper.eq("county", county))
                    .orderByDesc("create_time").last("limit 1");
            School lastSchool = schoolMapper.selectOne(queryWrapper2);
            if (lastSchool != null) {
                int tempNewCode = Integer.parseInt(lastSchool.getSchoolLogin().substring(4, 7)) + 1;
                String tempNewCode1 = String.valueOf(tempNewCode);
                if (tempNewCode1.length() == 1) {
                    newCode += "00" + tempNewCode1 + "1";
                }else if (tempNewCode1.length() == 2) {
                    newCode += "0" + tempNewCode1 + "1";
                }else {
                    newCode += tempNewCode1 + "1";
                }
            } else {
                newCode += "0011";
            }
            for (School school : schoolList) {
                // 生成学校编号   3位学校编号递增，由于是创建账号，肯定是第一个账号，则最后一位为1
                if (listCur != 0) {
                    int temp = Integer.parseInt(newCode.substring(4, 7)) + 1;
                    String temp1 = String.valueOf(temp);
                    if (temp1.length() == 1) {
                        newCode = newCode.substring(0, 4) + "00" + temp1 + "1";
                    }else if (temp1.length() == 2) {
                        newCode = newCode.substring(0, 4) + "0" + temp1 + "1";
                    }else {
                        newCode = newCode.substring(0, 4) + temp1 + "1";
                    }
                }
                listCur += 1;
                school.setSchoolLogin(newCode);
                // 学生账户数写死，待修改
                school.setStudentAccountNumbers(school.getStudentNumbers());
                schoolMapper.insert(school);
                log.info("add School:{}", JSON.toJSON(school));
                // 创建学校管理员账户
                User user = new User();
                user.setUsername(newCode);
                user.setPassword(new BCryptPasswordEncoder().encode("123456"));
                user.setAvatar(Constants.avatarManager);
                user.setEnabled(0);
                userMapper.insert(user);
                log.info("add User:{}", JSON.toJSON(user));
                // 创建学校管理员用户角色
                Role role = new Role();
                role.setUsername(newCode);
                role.setRole("学校管理员");
                role.setLevel(3);
                roleMapper.insert(role);
                log.info("add Role:{}", JSON.toJSON(role));
                // 添加管理员用户和所属对应
                Manager manager = new Manager();
                manager.setUsername(newCode);
                manager.setName(school.getChargePerson());
                manager.setPhone(school.getPhone());
                manager.setEmail("");
                manager.setSex("");
                manager.setProvince(school.getProvince());
                manager.setCity(school.getCity());
                manager.setCounty(school.getCounty());
                manager.setSchool(school.getName());
                managerMapper.insert(manager);
                log.info("add Manager:{}", JSON.toJSON(manager));
                // 添加school_role
                SchoolRole schoolRole = new SchoolRole();
                schoolRole.setSchoolId(school.getId());
                schoolRole.setUsername(school.getSchoolLogin());
                schoolRole.setRole("学校管理员");
                schoolRole.setUnit(school.getName());
                schoolRole.setName(school.getChargePerson());
                schoolRole.setPhone(school.getPhone());
                schoolRoleMapper.insert(schoolRole);
                log.info("add SchoolRole:{}", JSON.toJSON(schoolRole));
                // 添加任务
//                String provinceCity = school.getProvince() + school.getCity();
//                QueryWrapper<Task> taskQueryWrapper = new QueryWrapper<>();
//                taskQueryWrapper.select("id").eq("issued_unit", provinceCity);
//                Task task = taskMapper.selectOne(taskQueryWrapper);
//                if (task != null) {
//                    TaskSchool taskSchool = new TaskSchool();
//                    taskSchool.setTaskId(Math.toIntExact(task.getId()));
//                    taskSchool.setProvince(school.getProvince());
//                    taskSchool.setCity(school.getCity());
//                    taskSchool.setCounty(school.getCounty());
//                    taskSchool.setSchoolId(Math.toIntExact(school.getId()));
//                    taskSchool.setGrade("");
//                    taskSchool.setClasses("");
//                    taskSchool.setNumbers(0);
//                    taskSchool.setCompleteNumbers(0);
//                    taskSchool.setIsSubmit(0);
//                    taskSchool.setWarningNumbers(0);
//                    taskSchoolMapper.insert(taskSchool);
//                    log.info("insert task_school:{}", JSON.toJSONString(school));
//                }
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
    public Wrapper<Object> getClasses(JSONObject jsonObject) {
        try {
            Integer school_id = jsonObject.getInteger("schoolId");
            String period = jsonObject.getString("period");
            String grade = jsonObject.getString("grade");
            QueryWrapper<SchoolClasses> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("classes").eq("school_id", school_id).eq("period", period).eq("grade", grade).orderByAsc("classes");
            List<SchoolClasses> schoolClassesList = schoolClassesMapper.selectList(queryWrapper);
            List<String> classesList = schoolClassesList.stream().map(SchoolClasses::getClasses).collect(Collectors.toList());
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, classesList);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> countyList(JSONObject jsonObject){
        try {
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("county").eq("province", jsonObject.getString("province"))
                    .and(cityQueryWrapper -> cityQueryWrapper.eq("city", jsonObject.getString("city"))).groupBy("county");
            List countyList = schoolMapper.selectList(queryWrapper);
            List result = new ArrayList();
            Iterator<School> iteratorCounty = countyList.iterator();
            while (iteratorCounty.hasNext()) {
                School county = iteratorCounty.next();
                result.add(county.getCounty());
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    public Wrapper<Object> schoolNameList(JSONObject jsonObject){
        try {
            QueryWrapper<School> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("name").eq("province", jsonObject.getString("province"))
                    .and(cityQueryWrapper -> cityQueryWrapper.eq("city", jsonObject.getString("city")))
                    .and(countyQueryWrapper -> countyQueryWrapper.eq("county", jsonObject.getString("county")));
            List schoolList = schoolMapper.selectList(queryWrapper);
            List result = new ArrayList();
            Iterator<School> iteratorSchool = schoolList.iterator();
            while (iteratorSchool.hasNext()) {
                School school = iteratorSchool.next();
                result.add(school.getName());
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
    public Wrapper<Object> getAllClasses(JSONObject jsonObject) {
        try {
            Integer school_id = jsonObject.getInteger("schoolId");
            QueryWrapper<SchoolClasses> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("period", "grade", "classes").eq("school_id", school_id).ne("class_numbers", 0).orderByAsc("classes");
            List<SchoolClasses> schoolClassesList = schoolClassesMapper.selectList(queryWrapper);
            Map<String, List<String>> result = new HashMap<>();
            for (SchoolClasses schoolClasses : schoolClassesList) {
                List<String> cur;
                if (result.containsKey(schoolClasses.getPeriod() + schoolClasses.getGrade())) {
                    cur = result.get(schoolClasses.getPeriod() + schoolClasses.getGrade());
                } else {
                    cur = new ArrayList<>();
                }
                cur.add(schoolClasses.getClasses());
                result.put(schoolClasses.getPeriod() + schoolClasses.getGrade(), cur);
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
    public Wrapper<Object> getAllCounty(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            List<String> result = schoolMapper.getAllCounty(province, city);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> getAllCity(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            List<String> result = schoolMapper.getAllCity(province);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> getDataStatistics(JSONObject jsonObject) {
        try {
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String county = jsonObject.getString("county");
            Integer schoolId = jsonObject.getInteger("schoolId");

            int level = 4;
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.select("id", "county", "city").eq("province", province);
            if (city != null && !city.equals("")) {
                schoolQueryWrapper.eq("city", city);
                level = 3;
            }
            if (county != null && !county.equals("")) {
                schoolQueryWrapper.eq("county", county);
                level = 2;
            }
            if( schoolId != null){
                schoolQueryWrapper.eq("id", schoolId);
                level = 1;
            }

            List<School> schoolList = schoolMapper.selectList(schoolQueryWrapper);

            Map<String, Object> result = new HashMap<>();
            if (level > 1) {
                result.put("schoolNum", schoolList.size());
            }

            int countyNum = 0, cityNum = 0;
            Set<String> counties = new HashSet<>();
            Set<String> cities = new HashSet<>();
            for (School school : schoolList) {
                if (level > 2) {
                    if (!counties.contains(school.getCounty())) {
                        countyNum ++;
                        counties.add(school.getCounty());
                    }
                    if (!cities.contains(school.getCity())) {
                        cityNum ++;
                        cities.add(school.getCity());
                    }
                }
            }

            if (level > 2) {
                result.put("countyNum", countyNum);
            }

            if (level == 4) {
                result.put("cityNum", cityNum);
            }

            List<Long> schoolIds = schoolList.stream().map(School::getId).collect(Collectors.toList());
            if (schoolIds.size() != 0) {
                QueryWrapper<Students> studentsQueryWrapper = new QueryWrapper<>();
                studentsQueryWrapper.in("school_id", schoolIds);
                studentsQueryWrapper.eq("archived", 0);
                int studentsNum = studentsMapper.selectCount(studentsQueryWrapper);
                result.put("studentsNum", studentsNum);

                QueryWrapper<Teachers> teachersQueryWrapper = new QueryWrapper<>();
                teachersQueryWrapper.in("school_id", schoolIds);
                int teachersNum = teachersMapper.selectCount(teachersQueryWrapper);
                result.put("teachersNum", teachersNum);

                QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                schoolRoleQueryWrapper.select("role", "full_part").in("school_id", schoolIds);
                List<SchoolRole> schoolRoleList = schoolRoleMapper.selectList(schoolRoleQueryWrapper);

                int fullTea = 0, partTea = 0, psyTea = 0;
                for (SchoolRole schoolRole : schoolRoleList) {
                    if (schoolRole.getRole().equals("心理老师") || schoolRole.getRole().equals("学校管理员")) {
                        if (schoolRole.getFullPart() != null && schoolRole.getFullPart() == 0) {
                            fullTea ++;
                            psyTea ++;
                        }
                        if (schoolRole.getFullPart() != null && schoolRole.getFullPart() == 1) {
                            partTea ++;
                            psyTea ++;
                        }
                    }
                }

                if (level == 1) {
                    result.put("roleNum", schoolRoleList.size());
                    result.put("psyTeacherNum", psyTea);
                    result.put("fullNum", fullTea);
                    result.put("partNum", partTea);
                } else {
                    result.put("fullNum", fullTea);
                    result.put("partNum", partTea);
                }
            } else {
                result.put("studentsNum", 0);
                result.put("teachersNum", 0);
                if (level == 1) {
                    result.put("psyTeacherNum", 0);
                } else {
                    result.put("fullNum", 0);
                    result.put("partNum", 0);
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
}
