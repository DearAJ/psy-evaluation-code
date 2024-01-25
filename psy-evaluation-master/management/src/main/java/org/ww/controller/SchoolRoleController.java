package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.ww.domain.SchoolRole;
import org.ww.dto.FindSchoolRoleDto;
import org.ww.dto.SchoolRoleDto;
import org.ww.result.Wrapper;
import org.ww.service.SchoolRoleService;

/**
 * @Author 13096
 * @Date 2022/6/5 16:14
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/manage")
public class SchoolRoleController {
    private final SchoolRoleService schoolRoleService;

    public SchoolRoleController(SchoolRoleService schoolRoleService) {
        this.schoolRoleService = schoolRoleService;
    }

    @PostMapping("addSchoolRole")
    public Wrapper<String> addSchoolRole(SchoolRoleDto schoolRoleDto) {
        return schoolRoleService.addSchoolRole(schoolRoleDto);
    }

    @PostMapping("schoolRoleList")
    public Wrapper<Object> schoolRoleList(FindSchoolRoleDto findSchoolRoleDto) {
        return schoolRoleService.schoolRoleList(findSchoolRoleDto);
    }

    @PostMapping("updateSchoolRole")
    public Wrapper<String> updateSchoolRole(SchoolRole schoolRole) {
        return schoolRoleService.updateSchoolRole(schoolRole);
    }

    @ResponseBody
    @PostMapping("deleteSchoolRole")
    public Wrapper<Integer> deleteSchoolRole(@RequestBody JSONObject jsonObject) {
        return schoolRoleService.deleteSchoolRole(jsonObject);
    }
}
