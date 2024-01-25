package org.ww.EntityVO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ww.base.BaseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class RecordAndPlanVOExt extends BaseEntity {

    private String moudle;
    private Date date;
    List<RecordAndPlanVO> recordAndPlanVOList = new ArrayList<>();
    public RecordAndPlanVOExt(String type){
        this.moudle = type;
        this.date = new Date(System.currentTimeMillis());
    }
    public RecordAndPlanVOExt(String type,Date date){
        this.moudle = type;
        this.date = date;
    }

}
