package org.ww.EntityDO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;


import java.io.Serializable;
import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Component
@TableName("psyintervationrecord")
public class PsyIntervationRecordDO extends IdBaseEntity implements Serializable {

    private Long id;
    private Long userId;
    private Long fileId;
    private Boolean isTransfer;


    private String intervationRecord;
    private String postProcessing;
    private String recordType;//干预记录、危机确认
    private String intervationWay;
    private String crisisLevel;
    private String stuState;
    private Boolean isSubmit;
    private Date intervationTime;
    private Date intervationExpireTime;
    private Date createTime;

}
