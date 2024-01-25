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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.ww.constants.Constants;
import org.ww.domain.*;
import org.ww.dto.FindTeacherDto;
import org.ww.exception.BusinessException;
import org.ww.mapper.RoleMapper;
import org.ww.mapper.TeachersMapper;
import org.ww.mapper.UserMapper;
import org.ww.provider.TaskProvider;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.TeacherService;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author 13096
 * @Date 2022/6/1 20:45
 * @Version 1.0
 */
@Slf4j
@Service
public class TeacherServiceImpl extends ServiceImpl<TeachersMapper, Teachers> implements TeacherService{

    @Resource
    private TeachersMapper teachersMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @DubboReference
    private TaskProvider taskProvider;

    @Override
    public Wrapper<String> addTeacher(Teachers teacher) {
        try {
            QueryWrapper<Teachers> teachersQueryWrapper = new QueryWrapper<>();
            teachersQueryWrapper.eq("username", teacher.getUsername())
                    .or(teachersQueryWrapper1 -> teachersQueryWrapper1.eq("id_number", teacher.getIdNumber()));
            int count = teachersMapper.selectCount(teachersQueryWrapper);
            if (count != 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "身份证号已存在");
            }

            // 判断user表里是否有相同的username
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("username", teacher.getUsername());
            int userCount = userMapper.selectCount(userQueryWrapper);
            if (userCount != 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "已有相同的身份证号作为用户名");
            }

            // 添加教师
            teachersMapper.insert(teacher);
            log.info("add Teacher:{}", JSON.toJSON(teacher));
            // 创建教师账户
            User user = new User();
            user.setUsername(teacher.getIdNumber());
            String rawPassword = teacher.getIdNumber().substring(teacher.getIdNumber().length() - 6);
            user.setPassword(new BCryptPasswordEncoder().encode(rawPassword));
            if (teacher.getSex().equals("男")) {
                user.setAvatar(Constants.avatarTeacherMale);
            } else {
                user.setAvatar(Constants.avatarTeacherFemale);
            }
            userMapper.insert(user);
            log.info("add User:{}", JSON.toJSON(user));
            // 添加教师角色
            Role role = new Role();
            role.setUsername(teacher.getUsername());
            role.setRole("教师");
            role.setLevel(0);
            roleMapper.insert(role);
            log.info("add Role:{}", JSON.toJSON(role));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> teacherList(FindTeacherDto findTeacherDto) {
        try {
            QueryWrapper<Teachers> teachersQueryWrapper = new QueryWrapper<>();
            teachersQueryWrapper.eq("school_id", findTeacherDto.getSchoolId());
            if (findTeacherDto.getName() != null) {
                teachersQueryWrapper.like("name", findTeacherDto.getName());
            }
            if (findTeacherDto.getSex() != null) {
                teachersQueryWrapper.eq("sex", findTeacherDto.getSex());
            }
            if (findTeacherDto.getIdNumber() != null) {
                teachersQueryWrapper.like("id_number", findTeacherDto.getIdNumber());
            }
            if (findTeacherDto.getPhone() != null) {
                teachersQueryWrapper.like("phone", findTeacherDto.getPhone());
            }
            teachersQueryWrapper.orderByDesc("create_time");
            Integer pageNum = findTeacherDto.getPageNum();
            Integer pageSize = findTeacherDto.getPageSize();
            IPage<Teachers> teachersIPage = teachersMapper.selectPage(new Page<>(pageNum, pageSize), teachersQueryWrapper);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, teachersIPage);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> updateTeacher(Teachers teacher) {
        try {
            teachersMapper.updateById(teacher);
            log.info("update teacher:{}", JSON.toJSON(teacher));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> deleteTeacher(JSONObject jsonObject) {
        try {
            // 是否全选  0否 1是
            Integer isAll = jsonObject.getInteger("isAll");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer hasFilter = jsonObject.getInteger("hasFilter");
            List<String> userList;
            List<Long> ids = new ArrayList<>();
            List<Long> newIds = new ArrayList<>();
            List<Long> newTeacherIds = new ArrayList<>();
            List<String> newUsers = new ArrayList<>();
            List<String> cantDeleteUsers = new ArrayList<>();
            if (isAll != null && isAll == 1) {
                QueryWrapper<Teachers> teachersQueryWrapper = new QueryWrapper<>();
                teachersQueryWrapper.select("id").eq("school_id", schoolId);
                if (hasFilter != null && hasFilter == 1) {
                    if (jsonObject.containsKey("sex")) {
                        teachersQueryWrapper.eq("sex", jsonObject.getString("sex"));
                    }
                    if (jsonObject.containsKey("name")) {
                        teachersQueryWrapper.like("name", jsonObject.getString("name"));
                    }
                    if (jsonObject.containsKey("idNumber")) {
                        teachersQueryWrapper.like("id_number", jsonObject.getString("idNumber"));
                    }
                    if (jsonObject.containsKey("phone")) {
                        teachersQueryWrapper.like("phone", jsonObject.getString("phone"));
                    }
                }
                List<Teachers> teachersList = teachersMapper.selectList(teachersQueryWrapper);
                ids = teachersList.stream().map(Teachers::getId).collect(Collectors.toList());
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

            QueryWrapper<Teachers> teachersQueryWrapper = new QueryWrapper<>();
            teachersQueryWrapper.select("id", "username", "school_id").in("id", ids);
            List<Teachers> teachersList = teachersMapper.selectList(teachersQueryWrapper);
            userList = teachersList.stream().map(Teachers::getUsername).collect(Collectors.toList());

            // 查询教师关联用户id
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

            for (Teachers teachers : teachersList) {
                if (newUsers.contains(teachers.getUsername())) {
                    newTeacherIds.add(teachers.getId());
                }
            }

            if (newIds.size() == 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "所选教师均不可删除");
            }

            // 删除教师关联用户
            userMapper.deleteBatchIds(newIds);
            log.info("delete Users:{}", JSON.toJSONString(newUsers));
            // 删除教师关联权限
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.in("username", newUsers);
            roleMapper.delete(roleQueryWrapper);
            log.info("delete Roles:{}", JSON.toJSONString(newUsers));
            // 删除教师
            teachersMapper.deleteBatchIds(newTeacherIds);
            log.info("delete Teachers:{}", JSON.toJSONString(newTeacherIds));
            // 获取不可删除教师信息
            if (cantDeleteUsers.size() != 0) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, JSON.toJSON(cantDeleteUsers));
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
    public Wrapper<Object> verifyTeacherExcel(MultipartFile teacherExcel, Integer schoolId) {
        try {
            if (teacherExcel == null || teacherExcel.getSize() == 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空");
            }
            String fileName = teacherExcel.getOriginalFilename();
            if (fileName == null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件名为空");
            }
            if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件格式不正确");
            }
            boolean isExcel2007 = !fileName.matches("^.+\\.(?i)(xls)$");
            InputStream inputStream = teacherExcel.getInputStream();
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
            if (!fieldColumn.containsKey("教师姓名") || !fieldColumn.containsKey("性别") || !fieldColumn.containsKey("证件类型")
                    || !fieldColumn.containsKey("教师身份证号") || !fieldColumn.containsKey("手机号")) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "模板列不匹配，请使用模板上传数据");
            }
            if (rows <= 2) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "表格内无教师数据");
            }
            Map<String, Object> result = new HashMap<>();
            List<Map<String, Object>> rightTeacherList = new ArrayList<>();
            List<Map<String, Object>> errorTeacherList = new ArrayList<>();
            List<Map<String, Object>> existTeacherList = new ArrayList<>();
            List<Map<String, Object>> baseExistTeacherList = new ArrayList<>();
            List<String> idNumbers = new ArrayList<>();
            for (int i = 3; i <= rows; i ++) {
                boolean errorRow = false;   // 数据格式错误
                boolean existRow = false;   // 重复行提示
                boolean baseExistRow = false;  // 教师已存在提示
                StringBuilder info = new StringBuilder();
                Row row1 = sheet.getRow(i);
                if (row1 != null) {
                    if (row1.getCell(0) == null || StringUtils.isEmpty(row1.getCell(0))) {
                        continue;
                    }
                    Map<String, Object> teacher = new HashMap<>();
                    teacher.put("row", i + 1);
                    teacher.put("schoolId", schoolId);
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
                            case "教师姓名":
                                teacher.put("name", temp);
                                break;
                            case "性别":
                                teacher.put("sex", temp);
                                break;
                            case "证件类型":
                                teacher.put("idType", temp);
                                break;
                            case "教师身份证号":
                                teacher.put("idNumber", temp);
                                teacher.put("username", temp);
                                break;
                            case "手机号":
                                teacher.put("phone", temp);
                                break;
                        }
                    }

                    // 检验某个cell是否为空值
                    for (Map.Entry<String, Object> entry : teacher.entrySet()) {
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

                    // 检验该条数据是否重复，身份证号
                    if (idNumbers.contains(teacher.get("idNumber").toString())) {
                        StringBuilder existFiled = new StringBuilder();
                        if (idNumbers.contains(teacher.get("idNumber").toString())) {
                            existFiled.append("证件号");
                        }
                        existRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";表格中已存在重复的").append(existFiled);
                        } else {
                            info = new StringBuilder("表格中已存在重复的").append(existFiled);
                        }
                    } else {
                        idNumbers.add(teacher.get("idNumber").toString());
                    }

                    // 如果性别不为男女的，身份证号不为18位的，加入错误提示列表
                    if (!teacher.get("sex").toString().equals("男") && !teacher.get("sex").toString().equals("女")) {
                        errorRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";性别错误");
                        } else {
                            info = new StringBuilder("性别错误");
                        }
                    }

//                    String reg = "^[1-9]\\\\d{5}(18|19|20)\\\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\\\d{3}[0-9Xx]$";
                    String reg = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
//                    log.info("----------{}----------", teacher.get("idNumber").toString());
                    if (teacher.get("idType").toString().equals("居民身份证") && !teacher.get("idNumber").toString().matches(reg)) {
                        errorRow = true;
                        if (!info.toString().equals("")) {
                            info.append(";身份证号不合法");
                        } else {
                            info = new StringBuilder("身份证号不合法");
                        }
                    }

                    // 检验数据库中是否存在相同身份证号
                    if (!errorRow && !existRow) {
                        QueryWrapper<Teachers> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("id_number", teacher.get("idNumber"));
                        int count = teachersMapper.selectCount(queryWrapper);
                        if (count != 0) {
                            baseExistRow = true;
                            if (!info.toString().equals("")) {
                                info.append(";系统中已存在相同的证件号");
                            } else {
                                info = new StringBuilder("系统中已存在相同的证件号");
                            }
                        }
                    }
                    if (errorRow) {
                        teacher.put("warn", info);
                        errorTeacherList.add(teacher);
                    }  else if (existRow) {
                        teacher.put("warn", info);
                        existTeacherList.add(teacher);
                    } else if (baseExistRow) {
                        teacher.put("warn", info);
                        baseExistTeacherList.add(teacher);
                    } else {
                        rightTeacherList.add(teacher);
                    }
                }
            }
            result.put("errorTeacherList", errorTeacherList);
            result.put("rightTeacherList", rightTeacherList);
            result.put("existTeacherList", existTeacherList);
            result.put("baseExistTeacherList", baseExistTeacherList);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, "表格读取完成", JSON.toJSON(result));
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> addTeachers(List<Teachers> teachersList) {
        try {
            int count = teachersList.size();
            int cur, insertNum = 0;
            List<Teachers> errTeachers = new ArrayList<>();
            for (Teachers teacher : teachersList) {
                cur = teachersMapper.insert(teacher);
                if (cur == 0) {
                    errTeachers.add(teacher);
                    log.info("add teacher fail:{}", teacher);
                }else {
                    // 创建教师账户
                    User user = new User();

                    user.setUsername(teacher.getUsername());
                    String rawPassword = teacher.getIdNumber().substring(teacher.getIdNumber().length() - 6);
                    user.setPassword(new BCryptPasswordEncoder().encode(rawPassword));
                    if (teacher.getSex().equals("男")) {
                        user.setAvatar(Constants.avatarBoy);
                    } else {
                        user.setAvatar(Constants.avatarGirl);
                    }
                    userMapper.insert(user);
                    log.info("add User:{}", JSON.toJSON(user));
                    // 添加教师角色
                    Role role = new Role();
                    role.setUsername(teacher.getUsername());
                    role.setRole("教师");
                    role.setLevel(0);
                    roleMapper.insert(role);
                    log.info("add Role:{}", JSON.toJSON(role));
                    insertNum += cur;
                    log.info("add teacher:{}", teacher);
                }
            }
            if (count != insertNum) {
                log.info("{} teachers add fail:", count - insertNum);
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE, errTeachers);
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
