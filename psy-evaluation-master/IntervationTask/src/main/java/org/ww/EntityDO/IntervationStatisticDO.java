package org.ww.EntityDO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntervationStatisticDO extends IdBaseEntity implements Serializable {
    private Long id;
    @Override
    public Long getId() {
        return id;
    }

    private String grade;
    private String name;
    private String classes;
    private String period;
    private Integer intervationNums;
    private String crisisLevel;

}
