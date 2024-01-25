package org.ww.dto.PRIntervationDTO;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.ww.domain.School;
import org.ww.dto.PsyIntervationSchemeDTO;

import java.io.Serializable;

@Data
@SuperBuilder
public class PsyPRIntervationSchemeDTO extends PsyIntervationSchemeDTO implements Serializable {

   private School schoolDO;

}
