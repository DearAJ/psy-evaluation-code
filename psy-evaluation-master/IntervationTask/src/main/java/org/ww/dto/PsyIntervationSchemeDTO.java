package org.ww.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.ww.EntityDO.*;

import java.io.Serializable;

@Data
@SuperBuilder
public class PsyIntervationSchemeDTO implements Serializable {

   private PsyIntervationFileDO fileDO;
   private PsyIntervationPlanDO planDO;
   private PsyIntervationRecordDO recordDO;
   private PsyIntervationStuDO studentDO;
   private PsyIntervationManagerDO teacherDO;

}
