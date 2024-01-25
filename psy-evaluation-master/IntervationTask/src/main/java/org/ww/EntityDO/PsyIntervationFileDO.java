package org.ww.EntityDO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;
import org.ww.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;


@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Component
@TableName("psyintervationfiles")
public class PsyIntervationFileDO extends IdBaseEntity implements Serializable {
    private Long id;
    private Long taskId;
    private Long studentId;
    private Long userId;
    private Long psyIntervationCrisisId;
    private Long psyIntervationLastPlanId;
    private Long psyIntervationLastRecordId;
   // private Long psyIntervationRecordId;
   // private Long psyIntervationPlanId;
    private Date createTime;

}
