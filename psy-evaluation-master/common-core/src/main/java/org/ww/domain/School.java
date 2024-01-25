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
 * @Date 2022/2/10 22:06
 * @Version 1.0
 */

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "schools")
public class School extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String province;

    private String city;

    private String county;

    private String address;

    private String period;

    private Integer studentNumbers;

    private Integer studentAccountNumbers;

    private String chargePerson;

    private String schoolLogin;

    private String phone;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;
}
