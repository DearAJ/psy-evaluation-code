package org.ww.EntityVO;

import lombok.Data;
import org.ww.EntityDO.HotWord;
import org.ww.EntityDO.PsyIntervationRecordDO;

import java.io.Serializable;
import java.util.List;

@Data
public class RecordAndHotwordsVO implements Serializable {
    PsyIntervationRecordDO  recordDO;
    List<String> hotWords;
}
