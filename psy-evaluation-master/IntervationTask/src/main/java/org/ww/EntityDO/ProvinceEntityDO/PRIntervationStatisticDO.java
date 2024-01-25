package org.ww.EntityDO.ProvinceEntityDO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ww.EntityDO.IdBaseEntity;
import org.ww.EntityDO.IntervationStatisticDO;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PRIntervationStatisticDO extends IntervationStatisticDO implements Serializable {
//    private Long id;
//    @Override
//    public Long getId() {
//        return id;
//    }
//
//    private String grade;
//    private String name;
//    private String classes;
//    private String period;
//    private Long intervationNums;
//    private String crisisLevel;
    private String province;
    private String city;
    private String county;
    private String schoolName;
    private Long schoolId;


    private Integer aLevel= 0;
    private Integer bLevel = 0;
    private Integer cLevel = 0;

}
