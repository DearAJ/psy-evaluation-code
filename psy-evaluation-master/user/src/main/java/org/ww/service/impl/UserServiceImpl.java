package org.ww.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import org.ww.domain.School;
import org.ww.domain.SchoolRole;
import org.ww.dto.ManagerDto;
import org.ww.exception.BusinessException;
import org.ww.mapper.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.constants.Constants;
import org.ww.domain.Manager;
import org.ww.domain.User;
import org.ww.service.UserService;
import org.ww.utils.JWTUtils;
import org.ww.utils.RSAUtils;
import org.ww.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private final ManagerMapper managerMapper;
    private final StudentsMapper studentsMapper;
    private final SchoolMapper schoolMapper;
    private final SchoolRoleMapper schoolRoleMapper;
    private final TeachersMapper teachersMapper;

    public UserServiceImpl(UserMapper userMapper, ManagerMapper managerMapper, StudentsMapper studentsMapper, SchoolMapper schoolMapper, SchoolRoleMapper schoolRoleMapper, TeachersMapper teachersMapper) {
        this.userMapper = userMapper;
        this.managerMapper = managerMapper;
        this.studentsMapper = studentsMapper;
        this.schoolMapper = schoolMapper;
        this.schoolRoleMapper = schoolRoleMapper;
        this.teachersMapper = teachersMapper;
    }

    @Override
    public UserVo findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public String importUsers(String fileName, MultipartFile file) {
        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            System.out.println("文件格式错误");
        }
        return null;
    }

    @Override
    public Wrapper<Object> getUserInfo(String token) {
        try {
            SignedJWT signedJWT = JWTUtils.getSignedJWT(token);
            Object payload = signedJWT.getJWTClaimsSet().getClaim("payload");
            UserVo userVo = JSON.parseObject(payload.toString(), UserVo.class);
            int id = userVo.getId();
            Map<String, Object> result = userMapper.getUserInfo(id);
            if (result == null) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "用户不存在");
            }
            result.put("userId", id);
            Map<String, Object> extra = new HashMap<>();
            if(result.get("level").toString().equals("0")){
                if (result.get("role").equals("学生")) {
                    Map<String, Object> student = studentsMapper.getStudentInfo(result.get("username").toString());
                    result.put("name",student.get("name"));
                    result.put("sex", student.get("sex"));
                    extra.put("schoolId", student.get("school_id"));
                    extra.put("school",student.get("school"));
                    extra.put("period", student.get("period"));
                    extra.put("grade",student.get("grade"));
                    extra.put("classes",student.get("classes"));
                    extra.put("studentId",student.get("student_id"));
                    extra.put("studentCode",student.get("student_code"));
                } else {
                    Map<String, Object> teacher = teachersMapper.getTeacherInfo(result.get("username").toString());
                    result.put("name",teacher.get("name"));
                    result.put("sex", teacher.get("sex"));
                    extra.put("schoolId", teacher.get("school_id"));
                    extra.put("school",teacher.get("school"));
                }
            } else{
                if (!result.get("level").toString().equals("1")) {
                    QueryWrapper<Manager> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("username", result.get("username").toString());
                    queryWrapper.orderByDesc("id").last("limit 1");
                    Manager manager = managerMapper.selectOne(queryWrapper);
                    result.put("province", manager.getProvince());
                    result.put("unit", manager.getProvince() + "教育厅");
                    result.put("name", manager.getName());
                    result.put("sex", manager.getSex());
                    extra.put("phone", manager.getPhone());
                    extra.put("email", manager.getEmail());
                    if (manager.getCity() != null) {
                        result.put("city", manager.getCity());
                        result.put("unit", manager.getCity() + "教育局");
                    }
                    if (manager.getCounty() != null) {
                        result.put("county", manager.getCounty());
                        result.put("unit", manager.getCounty() + "教育局");
                    }
                    if (manager.getSchool() != null) {
                        extra.put("school", manager.getSchool());
                        QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                        schoolRoleQueryWrapper.select("unit", "full_part", "education", "major", "graduated_school", "certificate", "school_id", "classes").eq("username", result.get("username").toString());
                        schoolRoleQueryWrapper.orderByDesc("id").last("limit 1");
                        SchoolRole schoolRole = schoolRoleMapper.selectOne(schoolRoleQueryWrapper);
                        extra.put("classes", schoolRole.getClasses());
                        extra.put("schoolId", schoolRole.getSchoolId());
                        result.put("unit", schoolRole.getUnit());
                        extra.put("fullPart", schoolRole.getFullPart());
                        extra.put("education", schoolRole.getEducation());
                        extra.put("major", schoolRole.getMajor());
                        extra.put("graduatedSchool", schoolRole.getGraduatedSchool());
                        extra.put("certificate", schoolRole.getCertificate());
                        QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                        schoolQueryWrapper.select("period").eq("id", schoolRole.getSchoolId());
                        School school = schoolMapper.selectOne(schoolQueryWrapper);
                        extra.put("period", Arrays.asList(school.getPeriod().split(",")));
                    }
                } else {
                    QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                    schoolRoleQueryWrapper.select("unit", "school_id", "phone", "sex", "name").eq("username", result.get("username").toString());
                    schoolRoleQueryWrapper.orderByDesc("id").last("limit 1");
                    SchoolRole schoolRole = schoolRoleMapper.selectOne(schoolRoleQueryWrapper);
                    extra.put("phone", schoolRole.getPhone());
                    extra.put("schoolId", schoolRole.getSchoolId());
                    result.put("unit", schoolRole.getUnit());
                    result.put("sex", schoolRole.getSex());
                    result.put("name", schoolRole.getName());
                    QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                    schoolQueryWrapper.select("province", "city", "county", "name").eq("id", schoolRole.getSchoolId());
                    School school = schoolMapper.selectOne(schoolQueryWrapper);
                    result.put("province", school.getProvince());
                    result.put("city", school.getCity());
                    result.put("county", school.getCounty());
                    extra.put("school", school.getName());
                }
            }
            result.put("extra", extra);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, result);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> uploadAvatar(String token, String fileName, String fileType, String filePath, String fileSize) {
        Process process = null;
        try {
            SignedJWT signedJWT = JWTUtils.getSignedJWT(token);
            Object payload = signedJWT.getJWTClaimsSet().getClaim("payload");
            UserVo userVo = JSON.parseObject(payload.toString(), UserVo.class);
            String username = userVo.getUsername();
            String newFileName = username + fileName;
            process = Runtime.getRuntime().exec("mv " + filePath + " " + Constants.baseDir + Constants.avatarPath + newFileName);
            process.waitFor(5, TimeUnit.SECONDS);
            String url = "http://" + Constants.host + Constants.avatarPath + newFileName;
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, url);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    public Wrapper<String> updateManager(ManagerDto managerDto) {
        try {
            String username = managerDto.getUsername();
            String name = managerDto.getName();
            String phone = managerDto.getPhone();
            String email = managerDto.getEmail();
            String sex = managerDto.getSex();

            Integer enabled = managerDto.getEnabled();
            String avatar = managerDto.getAvatar();

            // 如果enabled == 0，则未激活，将其置为1，激活
            if (enabled == 0) {
                enabled = 1;
            }

            // 更新manager信息
            managerMapper.updateManager(username, name, phone, email, sex);

            // 更新school_role
            if (managerDto.getRole().equals("学校管理员") || managerDto.getRole().equals("心理老师")) {
                schoolRoleMapper.updateSchoolRole(username, name, phone, email, sex, managerDto.getFullPart(), managerDto.getEducation(), managerDto.getMajor(), managerDto.getGraduatedSchool(), managerDto.getCertificate());
            }

            // 关联更新schools中的charge_person
            if (managerDto.getRole().equals("学校管理员")) {
                schoolMapper.updateSchoolPerson(username, name);
            }

            if (avatar.equals(Constants.avatarManager)) {
                avatar = sex.equals("男") ? Constants.avatarManagerMale : Constants.avatarManagerFemale;
            }

            // 更新users信息
            userMapper.updateUserEnabled(username, avatar, enabled);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> modifyPass(JSONObject jsonObject) {
        try {
            String username = jsonObject.getString("username");
            String oldPassWord = jsonObject.getString("oldPassWord");
            String newPassWord = jsonObject.getString("newPassWord");
            String rawOldPass = RSAUtils.decrypt(oldPassWord);
            String rawNewPass = RSAUtils.decrypt(newPassWord);
            UserVo userVo = userMapper.findByUsername(username);
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            if (bCryptPasswordEncoder.matches(rawOldPass, userVo.getPassword())) {
                String newPass = bCryptPasswordEncoder.encode(rawNewPass);
                userMapper.modifyPass(username, newPass);
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
            }
            return WrapMapper.wrap(Wrapper.ERROR_CODE, "旧密码验证失败");
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }
}
