package org.ww.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 心理热线值班老师表
 * </p>
 *
 * @author zq
 * @since 2023-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tel_teacher")
public class TelTeacher implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tel_group_id")
    private Long telGroupId;

    @TableField("real_name")
    private String realName;

    @TableField("gender")
    private String gender;

    @TableField("phone")
    private String phone;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private Date createTime;

    @TableField("create_by")
    private String createBy;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;

//    @TableField(exist = false)
//    private List<TelCommonCode> certificates;


}
