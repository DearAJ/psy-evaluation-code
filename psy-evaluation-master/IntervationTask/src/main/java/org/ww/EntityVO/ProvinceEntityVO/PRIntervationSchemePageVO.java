package org.ww.EntityVO.ProvinceEntityVO;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.EntityVO.IntervationSchemePageVO;

/**
 * 对应危机预警的预警库展示页面
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PRIntervationSchemePageVO extends IntervationSchemePageVO implements Serializable {

//    Boolean isTransfer;
//
//    String stuPeriod;
//    String stuGrade;
//    String stuClass;
//    String stuName;
//
//    String stuState;
//
//    String  psyTeacherName;
//
//    String postProcessing;
//    String intervationContent;
//
//    String crisisLevel;
//
//    Date createTime;
//    Date expireTime;
    String mode;
    private String address;
    private Long schoolId;

    // feature:超时提醒
    private Integer expired;

}

