package org.ww.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 站内信发送状态
 * </p>
 *
 * @author zq
 * @since 2023-04-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("private_message_deliver")
public class PrivateMessageDeliver implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("private_message_id")
    private Long privateMessageId;

    /**
     * 接送人账号
     */
    @TableField("target_username")
    private String targetUsername;

    /**
     * 0, 未读
1, 已读
     */
    @TableField("status")
    private Integer status;

    @TableLogic
    private Integer deleted;


}
