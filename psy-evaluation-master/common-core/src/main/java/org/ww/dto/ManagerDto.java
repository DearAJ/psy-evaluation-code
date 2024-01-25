package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;

/**
 * @Author 13096
 * @Date 2022/3/4 18:03
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ManagerDto extends BaseEntity implements Serializable {
    private String username;

    private String name;

    private String role;

    private String phone;

    private String email;

    private String sex;

    private String avatar;

    private Integer enabled;

    // 专兼职 0 专  1兼
    private Integer fullPart;

    // 学历
    private String education;

    // 专业
    private String major;

    // 毕业院校
    private String graduatedSchool;

    // 证书
    private String certificate;
}
