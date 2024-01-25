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
@TableName("tel_blacklist")
public class TelBlacklist implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tel_group_id")
    private Long telGroupId;

    @TableField("phone")
    private String phone;

    @TableField("create_time")
    private Date createTime;

    @TableField("remark")
    private String remark;

    @TableField("create_username")
    private String createUsername;

    @TableLogic
    @TableField("deleted")
    private Integer deleted;


}
