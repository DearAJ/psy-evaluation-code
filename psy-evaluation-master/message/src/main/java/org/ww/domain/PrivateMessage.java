package org.ww.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 站内信
 * </p>
 *
 * @author zq
 * @since 2023-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("private_message")
public class PrivateMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 1,用户单对单发信
2,预警催办
3,任务催办

     */
    @TableField("type")
    private Integer type;

    /**
     * 发送的对象，比如说哪个学校
     */
    @TableField("target")
    private String target;

    @TableField("target_title")
    private String targetTitle;

    /**
     * 预警或任务ID
     */
    @TableField("ref_id")
    private String refId;

    @TableField("ref_id2")
    private String refId2;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("sender_username")
    private String senderUsername;

    @TableField("sender_title")
    private String senderTitle;

    /**
     * 发送时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableLogic
    private Integer deleted;


}
