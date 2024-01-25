package org.ww.service;

import org.ww.EntityDO.*;
import org.ww.EntityVO.IntervationStatisticVO;
import org.ww.base.BaseEntity;
import org.ww.dto.PsyIntervationSchemeDTO;

import java.util.List;

public interface IntervationSchemeService {
    public List<BaseEntity> getAllIntervationSchemePageVO(List<PsyIntervationSchemeDTO> files);
    public List<PsyIntervationSchemeDTO>  getAllIntervationSchemeDTO(List<PsyIntervationFileDO> files,
                                                          List<PsyIntervationRecordDO> records,
                                                          List<PsyIntervationStuDO> students,
                                                          List<PsyIntervationUserDO> users,
                                                          List<PsyIntervationPlanDO> plans,
                                                          List<PsyIntervationManagerDO> teachers);

    /**
     * vo 转 do
     * @param statisticDOS
     * @return
     */
    public List<IntervationStatisticVO> getIntervationStatisticVOs(List<IntervationStatisticDO> statisticDOS);
//    public Boolean createIntervationRecordDO(Long fileId);
//    public Boolean createIntervationPlanDO(Long fileId);
//    public Boolean createIntervationSchemeDO(Long fileId);
    default String getKey(String ...args){
        String key = "";
        for(String s : args){
            key = key + ":"+ s;
        }
        return key;
    }
}
