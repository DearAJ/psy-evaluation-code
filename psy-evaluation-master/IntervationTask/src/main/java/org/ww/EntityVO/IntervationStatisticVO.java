package org.ww.EntityVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class IntervationStatisticVO extends BaseLevelVO implements Serializable {




    private String grade;
    private String classes;
    private String period;
    /**
     *以上为前端传入参数必填项
     */
    private Long schoolId;
    private Long stuId;
    private Integer intervationNums =0;
    private Integer aLevel= 0;
    private Integer bLevel = 0;
    private Integer cLevel = 0;

}
