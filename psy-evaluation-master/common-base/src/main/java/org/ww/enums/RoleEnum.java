package org.ww.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @Author 13096
 * @Date 2022/6/5 19:48
 * @Version 1.0
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {
    ROLE_STUDENT(0, "学生"),

    ROLE_SCHOOL_MASTER(1, "校长"),

    ROLE_PSYCHOLOGIST(2, "心理老师"),

    ROLE_SCHOOL_MANAGER(3, "学校管理员"),

    ROLE_COUNTY_MANAGER(4, "区县管理员"),

    ROLE_CITY_MANAGER(5, "市级管理员"),

    ROLE_PROVINCE_MANAGER(6, "省级管理员")

    ;

    private final Integer key;

    private final String value;

    public static String getValue(Integer key) {
        for (RoleEnum role : RoleEnum.values()) {
            if (Objects.equals(key, role.getKey())) {
                return role.getValue();
            }
        }
        return null;
    }

    public static Integer getKey(String value) {
        for (RoleEnum role : RoleEnum.values()) {
            if (Objects.equals(value, role.getValue())) {
                return role.getKey();
            }
        }
        return null;
    }
}
