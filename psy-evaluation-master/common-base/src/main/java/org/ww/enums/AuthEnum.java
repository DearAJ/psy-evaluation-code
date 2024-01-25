package org.ww.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum AuthEnum {
    AUTH_PASS_ERROR(200, "用户名或密码错误"),

    AUTH_NO_TOKEN(401, "Token已过期或有误"),

    AUTH_NO_ACCESS(403, "无访问权限"),

    AUTH_NONEXISTENT(404, "请求路径不存在"),

    ;

    private Integer key;

    private String value;

    public static String getValue(Integer key) {
        for (AuthEnum value : AuthEnum.values()) {
            if (Objects.equals(key, value.getKey())) {
                return value.getValue();
            }
        }
        return null;
    }


}
