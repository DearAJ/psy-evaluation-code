package org.ww.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

/**
 * @Author 13096
 * @Date 2022/2/10 22:06
 * @Version 1.0
 */

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "task")
public class Task extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String content;
    private String semester;

    private String scaleId;

    private Date startDate;

    private Date endDate;

    private Long issuedId;

    private String issuedUnit;

    private Integer state;

    private Integer numbers;

    private Integer completeNumbers;

    private Integer warningNumbers;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic
    private Integer deleted;

    private String schoolYear;
    private Integer archived;
}
