package org.ww.EntityVO;

import org.ww.EntityDO.IdBaseEntity;

import java.io.Serializable;

public class IntervationRecordVO extends IdBaseEntity implements Serializable {
    private Long recordId;
    @Override
    public Long getId() {
        return recordId;
    }
    private String grade;
    private String stuName;
    private String period;

}
