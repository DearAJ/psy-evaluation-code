package org.ww.EntityVO;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.base.BaseEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder

public class PsyIntervationContentVO extends BaseEntity implements Serializable {
    private List<PsyIntervationPlanDO> psyIntervationPlanDO;
    private List<PsyIntervationRecordDO> psyIntervationRecordDO;
    private List<PsyIntervationRecordDO> psyIntervationCrisisDO;

    public PsyIntervationContentVO(){
        psyIntervationPlanDO = new ArrayList<>();
        psyIntervationRecordDO = new ArrayList<>();
        psyIntervationCrisisDO = new ArrayList<>();
    }


}
