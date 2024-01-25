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
 * @Date 2022/2/26 20:53
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "students")
public class Students extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String username;

    private String name;

    private Integer schoolId;

    private String entranceYear;

    private String period;

    private String grade;

    private String classes;

    private String sex;

    // 学号
    private String studentId;

    // 学籍号
    private String studentCode;

    // 证件类型
    private String idType;

    // 证件号
    private String idNumber;

    // 是否毕业
    private Integer graduated;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;

    private String schoolYear;
    private Integer archived;
}
