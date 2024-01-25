package org.ww.service.DubboService.Provider;

import org.springframework.web.bind.annotation.PathVariable;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.result.Wrapper;

public interface PsyFileService {
    Wrapper<Boolean> createIntervationSchemeItem(@PathVariable("fileDO") PsyIntervationFileDO fileDO);
}
