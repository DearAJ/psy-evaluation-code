package org.ww.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.ww.constants.Constants;
import org.ww.domain.*;
import org.ww.dto.FindSchoolRoleDto;
import org.ww.dto.SchoolRoleDto;
import org.ww.enums.RoleEnum;
import org.ww.exception.BusinessException;
import org.ww.mapper.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.SchoolRoleService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author 13096
 * @Date 2022/6/5 16:20
 * @Version 1.0
 */
@Slf4j
@Service
public class SchoolRoleServiceImpl extends ServiceImpl<SchoolRoleMapper, SchoolRole> implements SchoolRoleService {

    @Resource
    private SchoolRoleMapper schoolRoleMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Resource
    private ManagerMapper managerMapper;

    @Override
    public Wrapper<String> addSchoolRole(SchoolRoleDto schoolRoleDto) {
        try {
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.eq("username", schoolRoleDto.getUsername());
            int count = userMapper.selectCount(userQueryWrapper);
            if (count != 0) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "登录名已存在");
            }
            QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
            schoolQueryWrapper.select("name", "province", "city", "county").eq("id", schoolRoleDto.getSchoolId());
            School school = schoolMapper.selectOne(schoolQueryWrapper);
            schoolRoleDto.setUnit(school.getName());
//            List<String> periods_grades_classes = Arrays.asList(schoolRoleDto.getClasses().split(";"));
//            List<String> grades = periods_grades_classes.stream().map(period_grade_class -> period_grade_class.split(",")[0]).collect(Collectors.toList());
//            List<String> periods = periods_grades_classes.stream().map(period_grade_class -> period_grade_class.split(",")[1]).collect(Collectors.toList());
//            List<String> classes = periods_grades_classes.stream().map(period_grade_class -> period_grade_class.split(",")[2]).collect(Collectors.toList());
            // 添加角色
            SchoolRole schoolRole = new SchoolRole();
            BeanUtils.copyProperties(schoolRoleDto, schoolRole);
            if (schoolRoleDto.getRole().equals("校长")) {
                schoolRole.setClasses("");
            }
            schoolRoleMapper.insert(schoolRole);
            log.info("add SchoolRole:{}", JSON.toJSON(schoolRoleDto));
            // 创建角色账户
            User user = new User();
            user.setUsername(schoolRoleDto.getUsername());
            if (schoolRoleDto.getRole().equals("校长")) {
                user.setEnabled(1);
            } else {
                user.setEnabled(0);
            }
            String rawPassword = schoolRoleDto.getUsername().substring(schoolRoleDto.getUsername().length() - 6);
            user.setPassword(new BCryptPasswordEncoder().encode(rawPassword));
            if (schoolRoleDto.getSex().equals("男")) {
                user.setAvatar(Constants.avatarPsychologistMale);
            } else if (schoolRoleDto.getSex().equals("女")){
                user.setAvatar(Constants.avatarPsychologistFemale);
            } else {
                user.setAvatar(Constants.avatarManager);
            }
            userMapper.insert(user);
            log.info("add User:{}", JSON.toJSON(user));
            // 添加角色权限
            Role role = new Role();
            role.setUsername(schoolRoleDto.getUsername());
            role.setRole(schoolRoleDto.getRole());
            role.setLevel(RoleEnum.getKey(schoolRoleDto.getRole()));
            roleMapper.insert(role);
            log.info("add Role:{}", JSON.toJSON(role));
            if (!schoolRoleDto.getRole().equals("校长")) {
                // 添加管理员表
                Manager manager = new Manager();
                manager.setUsername(schoolRoleDto.getUsername());
                manager.setName(schoolRoleDto.getName());
                manager.setPhone(schoolRoleDto.getPhone());
                manager.setSex(schoolRoleDto.getSex());
                manager.setProvince(school.getProvince());
                manager.setCity(school.getCity());
                manager.setCounty(school.getCounty());
                manager.setSchool(school.getName());
                manager.setEmail("");
                managerMapper.insert(manager);
                log.info("add Manager:{}", JSON.toJSON(manager));
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
    public Wrapper<Object> schoolRoleList(FindSchoolRoleDto findSchoolRoleDto) {
        try {
            QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
            schoolRoleQueryWrapper.eq("school_id", findSchoolRoleDto.getSchoolId());
            if (findSchoolRoleDto.getName() != null) {
                schoolRoleQueryWrapper.like("name", findSchoolRoleDto.getName());
            }
            if (findSchoolRoleDto.getRole() != null) {
                schoolRoleQueryWrapper.eq("role", findSchoolRoleDto.getRole());
            }
            if (findSchoolRoleDto.getPhone() != null) {
                schoolRoleQueryWrapper.like("phone", findSchoolRoleDto.getPhone());
            }
            schoolRoleQueryWrapper.orderByDesc("create_time");
            Integer pageNum = findSchoolRoleDto.getPageNum();
            Integer pageSize = findSchoolRoleDto.getPageSize();
            IPage<SchoolRole> schoolRoleIPage = schoolRoleMapper.selectPage(new Page<>(pageNum, pageSize), schoolRoleQueryWrapper);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, schoolRoleIPage);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<String> updateSchoolRole(SchoolRole schoolRole) {
        try {
            // 查询当前信息
            SchoolRole schoolRole1 = schoolRoleMapper.selectById(schoolRole.getId());
            // 如果改了名字、电话、邮箱、性别，需要同时修改关联的表manager和users
            if (!schoolRole.getName().equals(schoolRole1.getName()) || !schoolRole.getPhone().equals(schoolRole1.getPhone()) || !schoolRole.getSex().equals(schoolRole1.getSex())) {
                String name = schoolRole.getName() == null || schoolRole.getName().equals("") ? "" : schoolRole.getName();
                String phone = schoolRole.getPhone() == null || schoolRole.getPhone().equals("") ? "" : schoolRole.getPhone();
                String sex = schoolRole.getSex() == null || schoolRole.getSex().equals("") ? "" : schoolRole.getSex();
                managerMapper.updateManager(schoolRole.getUsername(), name, phone, "", sex);
                log.info("update Manager:{}", JSON.toJSONString(schoolRole));
            }
            schoolRole.setUnit(schoolRole1.getUnit());
            schoolRole.setRole(schoolRole1.getRole());
            schoolRole.setUsername(schoolRole1.getUsername());
            schoolRoleMapper.updateById(schoolRole);
            log.info("update SchoolRole:{}", JSON.toJSONString(schoolRole));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Integer> deleteSchoolRole(JSONObject jsonObject) {
        try {
            // 是否全选  0否 1是
            Integer isAll = jsonObject.getInteger("isAll");
            Integer schoolId = jsonObject.getInteger("schoolId");
            Integer hasFilter = jsonObject.getInteger("hasFilter");
            List<String> userList;
            List<Long> ids = new ArrayList<>();
            if (isAll != null && isAll == 1) {
                QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                schoolRoleQueryWrapper.select("id", "username").eq("school_id", schoolId);
                if (hasFilter != null && hasFilter == 1) {
                    if (jsonObject.containsKey("name")) {
                        schoolRoleQueryWrapper.like("name", jsonObject.getString("name"));
                    }
                    if (jsonObject.containsKey("role")) {
                        schoolRoleQueryWrapper.eq("role", jsonObject.getString("role"));
                    }
                    if (jsonObject.containsKey("phone")) {
                        schoolRoleQueryWrapper.like("phone", jsonObject.getString("phone"));
                    }
                }
                List<SchoolRole> schoolRoleList = schoolRoleMapper.selectList(schoolRoleQueryWrapper);
                userList = schoolRoleList.stream().map(SchoolRole::getUsername).collect(Collectors.toList());
                ids = schoolRoleList.stream().map(SchoolRole::getId).collect(Collectors.toList());
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
                QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                schoolRoleQueryWrapper.select("username").in("id", ids);
                List<SchoolRole> schoolRoleList = schoolRoleMapper.selectList(schoolRoleQueryWrapper);
                userList = schoolRoleList.stream().map(SchoolRole::getUsername).collect(Collectors.toList());
            }
            // 查询关联用户id
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.select("id").in("username", userList);
            List<Long> userIds = userMapper.selectList(userQueryWrapper).stream().map(User::getId).collect(Collectors.toList());
            // 删除关联用户
            userMapper.deleteBatchIds(userIds);
            log.info("delete Users:{}", JSON.toJSONString(userList));
            // 删除关联权限
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.in("username", userList);
            roleMapper.delete(roleQueryWrapper);
            log.info("delete Roles:{}", JSON.toJSONString(userList));
            // 删除关联manager中记录
            QueryWrapper<Manager> managerQueryWrapper = new QueryWrapper<>();
            managerQueryWrapper.in("username", userList);
            managerMapper.delete(managerQueryWrapper);
            log.info("delete Managers:{}", JSON.toJSONString(userList));
            // 删除角色
            schoolRoleMapper.deleteBatchIds(ids);
            log.info("delete SchoolRoles:{}", JSON.toJSONString(ids));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }
}
