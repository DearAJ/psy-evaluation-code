package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @Author 13096
 * @Date 2022/6/14 15:32
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SchoolRoleDto implements Serializable {
    private Long id;

    private Long schoolId;

    private String username;

    private String role;

    private String unit;

    private String classes;

    private String name;

    private String sex;

    private String phone;
}
