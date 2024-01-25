package org.ww.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author zq
 * @since 2023-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tel_common_code")
public class TelCommonCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("page_name")
    private String pageName;

    @TableField("group_name")
    private String groupName;

    @TableField("title")
    private String title;

    @TableField("code")
    private String code;

    @TableField("sub_title")
    private String subTitle;

    @TableField("description")
    private String description;

    @TableField("create_time")
    private Date createTime;

    @TableField("priority")
    private Integer priority;

    @TableField("deleted")
    private Integer deleted;


}
