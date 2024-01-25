package org.ww.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.ww.domain.Manager;
import org.ww.domain.Role;
import org.ww.domain.User;
import org.ww.dto.ManagerDto;
import org.ww.mapper.ManagerMapper;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.RoleService;
import org.ww.service.UserService;
import org.ww.utils.RSAUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final RoleService roleService;
    @Resource
    private ManagerMapper managerMapper;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostMapping("userList")
    public Wrapper<IPage<User>> userList() {
        User user = new User();
        log.info("find start user:{}", JSON.toJSON(user));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(user.getKeyword()), "username", user.getKeyword());
        IPage<User> page = userService.page(new Page<>(user.getPageNum(), user.getPageSize()), queryWrapper);
        log.info("find end result:{}", JSON.toJSON(page));
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, page);
    }

    @PostMapping("importUsers")
    public Wrapper<String> importUsers() {
        String password = new BCryptPasswordEncoder().encode("123456");
        log.info("加密后的password{}", password);
        String username = "10600001";
        User user = new User(0L, username, password, 0, new Date(), new Date(), "", 0);
        userService.save(user);
        Role role = new Role(0L, username, "市管理员", 4, new Date(), new Date(), 0);
        roleService.save(role);
        Manager manager = new Manager(0L, username, "", "", "", "", "浙江省", "嘉兴市", null, null, new Date(), new Date(), 0);
        managerMapper.insert(manager);
        String[] county = {"06010001", "06020001", "06030001", "06040001", "06050001", "06060001", "06070001", "06080001"};
        for (String cou : county) {
            User user1 = new User(0L, cou, password, 0, new Date(), new Date(), "", 0);
            userService.save(user1);
            Role role1 = new Role(0L, cou, "区县管理员", 3, new Date(), new Date(), 0);
            roleService.save(role1);
            Manager manager1 = new Manager(0L, cou, "", "", "", "", "浙江省", "嘉兴市", null, null, new Date(), new Date(), 0);
            managerMapper.insert(manager1);
        }
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
    }

    @GetMapping("encryptPassword")
    public Wrapper<String> encryptPassword() {
        String password = "124563993";
        String encryptPassword = RSAUtils.encrypt(password);
        return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, encryptPassword);
    }

    @GetMapping("getUserInfo")
    public Wrapper<Object> getUserInfo(HttpServletRequest request) throws ParseException {
        String token = request.getHeader("Authorization");
        return userService.getUserInfo(token);
    }

    @PostMapping("avatar.upload")
    public Wrapper<String> avatarUpload(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String fileName = request.getParameter("file_name");
        String fileType = request.getParameter("file_content_type");
        String tempPath = request.getParameter("file_path");
        String fileSize = request.getParameter("file_size");
        log.info("Authorization:{}", token);
        log.info("file_name:{}", fileName);
        return userService.uploadAvatar(token, fileName, fileType, tempPath, fileSize);
    }

    @PostMapping("updateManager")
    public Wrapper<String> updateManager(ManagerDto managerDto) {
        return userService.updateManager(managerDto);
    }

    @PostMapping("modifyPass")
    public Wrapper<String> modifyPass(@RequestBody JSONObject jsonObject) {
        return userService.modifyPass(jsonObject);
    }
}
