package org.ww.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author zq
 * @since 2023-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tel_note")
public class TelNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tel_group_id")
    private Long telGroupId;

    @TableField("tel_cdr_id")
    private Long telCdrId;

    @TableField("phone")
    private String phone;

    @TableField("teacher_id")
    private Long teacherId;

    @TableField("teacher_name")
    private String teacherName;

    @TableField("question_type")
    private String questionType;

    @TableField("question_level")
    private String questionLevel;

    @TableField("question")
    private String question;

    @TableField("answer")
    private String answer;

    @TableField("remark")
    private String remark;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;


}
