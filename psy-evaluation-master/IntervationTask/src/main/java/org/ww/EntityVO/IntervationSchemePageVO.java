package org.ww.EntityVO;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 对应危机预警的预警库展示页面
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class IntervationSchemePageVO extends BaseLevelVO implements Serializable {

    Boolean isTransfer;
    Long fileId;
    Long stuId;
    Long userId;

    String stuPeriod;
    String stuGrade;
    String stuClass;
    String stuName;

    String stuState;

    String  psyTeacherName;

    String postProcessing;
    String intervationContent;
    String crisisLevel;

    String scaleName;
    String taskName;
    String taskId;

    String city;
    String county;
    String semester;
    //String school;

    Date createTime;
    Date expireTime;
}

