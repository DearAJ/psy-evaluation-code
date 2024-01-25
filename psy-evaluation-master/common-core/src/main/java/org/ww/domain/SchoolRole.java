package org.ww.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 13096
 * @Date 2022/6/5 15:51
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "school_role")
public class SchoolRole extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long schoolId;

    private String username;

    private String role;

    private String unit;

    private String classes;

    private String name;

    private String sex;

    private String phone;

    private String email;

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

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}
