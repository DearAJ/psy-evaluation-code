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
 * @since 2023-05-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tel_cdr_info")
public class TelCdrInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tel_group_id")
    private Long telGroupId;

    @TableField("sdk_app_id")
    private String sdkAppId;

    /**
     * 数据最初来源1,网页；2，JOB
     */
    @TableField("source")
    private Integer source;

    /**
     * 处理状态
     */
    @TableField("process_status")
    private Integer processStatus;

    @TableField("blocked")
    private Integer blocked;

    @TableField("record_local_path")
    private String recordLocalPath;

    @TableField("session_id")
    private String sessionId;

    @TableField("caller")
    private String caller;

    @TableField("callee")
    private String callee;

    @TableField("time")
    private Date time;

    @TableField("direction")
    private Integer direction;

    @TableField("duration")
    private Integer duration;

    @TableField("record_url")
    private String recordUrl;

    @TableField("seat_user_id")
    private String seatUserId;

    @TableField("end_status")
    private Integer endStatus;

    @TableField("caller_location")
    private String callerLocation;

    @TableField("ivr_duration")
    private Integer ivrDuration;

    @TableField("ring_timestamp")
    private Date ringTimestamp;

    @TableField("accept_timestamp")
    private Date acceptTimestamp;

    @TableField("ended_timestamp")
    private Date endedTimestamp;

    @TableField("ivr_key_pressed")
    private String ivrKeyPressed;

    @TableField("hung_up_side")
    private String hungUpSide;

    @TableField("skill_group_id")
    private Integer skillGroupId;

    @TableField("end_status_string")
    private String endStatusString;

    @TableField("start_timestamp")
    private Date startTimestamp;

    @TableField("queued_timestamp")
    private Date queuedTimestamp;

    @TableField("post_ivr_key_pressed")
    private String postIvrKeyPressed;

    @TableField("protected_caller")
    private String protectedCaller;

    @TableField("protected_callee")
    private String protectedCallee;

    @TableField("asr_url")
    private String asrUrl;

    @TableField("asr_local")
    private String asrLocal;

    @TableField("call_in_count")
    private Long callInCount;


}
