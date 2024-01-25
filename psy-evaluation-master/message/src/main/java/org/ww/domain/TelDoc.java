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
 * @since 2023-05-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tel_doc")
public class TelDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tel_group_id")
    private Long telGroupId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("create_time")
    private Date createTime;

    @TableField("create_by")
    private String createBy;

    @TableField("update_time")
    private Date updateTime;

    @TableField("update_by")
    private String updateBy;

    @TableField("priority")
    private Integer priority;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;


}
