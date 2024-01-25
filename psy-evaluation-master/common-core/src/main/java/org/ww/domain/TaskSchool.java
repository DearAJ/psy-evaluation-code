package org.ww.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 13096
 * @Date 2022/2/19 11:16
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "task_school")
public class TaskSchool extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer taskId;

    private String province;

    private String city;

    private String county;

    private Integer schoolId;

    private String grade;

    private String classes;

    private Integer numbers;

    private Integer completeNumbers;

    private Integer validNumbers;

    private Integer isSubmit;

    private Date submitTime;

    private Integer warningNumbers;

    private String schoolYear;
    private Integer archived;
}
