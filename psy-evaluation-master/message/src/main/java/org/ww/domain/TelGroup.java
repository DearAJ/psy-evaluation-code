package org.ww.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

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
@TableName("tel_group")
public class TelGroup implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 类型，对应管理员level
     */
    @TableField("type")
    private Integer type;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("county")
    private String county;

    @TableField("school")
    private String school;

    /**
     * 管理员对应账号
     */
    @TableField("admin")
    private String admin;

    /**
     * 接线员对应账号
     */
    @TableField("username")
    private String username;

    @TableField("secret_id")
    private String secretId;

    @TableField("secret_key")
    private String secretKey;

    @TableField("sdk_app_id")
    private String sdkAppId;

    /**
     * 第三方系统坐席账号
     */
    @TableField("seat_account")
    private String seatAccount;

    @TableField("cdr_last_fetch_timestamp")
    private Date cdrLastFetchTimestamp;

    @TableField("create_time")
    private Date createTime;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
