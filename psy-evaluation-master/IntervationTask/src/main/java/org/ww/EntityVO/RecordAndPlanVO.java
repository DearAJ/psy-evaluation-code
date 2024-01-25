package org.ww.EntityVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityDO.valueObject.IntervationRecordType;
import org.ww.base.BaseEntity;

import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor

public class RecordAndPlanVO extends BaseEntity {
    private String moudle;
    private Date date;
    private Long id;
    private String psyTeacher;
    private String methods;
    private String content;
    private String crisisLevel;
    private Boolean isSubmit;
    private Long stuId;

    // 类型:
    // 1: 指导建议
    private Integer type;

}
