package org.ww.EntityDO;

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
@TableName("psyintervationplan")
public class PsyIntervationPlanDO extends IdBaseEntity implements Serializable {
    private Long id;
    private Long userId;
    private Long fileId;
    private String symptom;
    private String intervationPlanContent;
    private String conclusion;
    private Long studentId;
    private Boolean isSubmit;
    private Date createTime;

}
