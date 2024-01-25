package org.ww.EntityVO;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ww.EntityDO.PsyIntervationRecordDO;

@Data
@NoArgsConstructor
public class RecordParamsVO {
    private FileIdAndStuIdVO fileIdAndStuIdVO;
    private PsyIntervationRecordDO recordDO;
}
