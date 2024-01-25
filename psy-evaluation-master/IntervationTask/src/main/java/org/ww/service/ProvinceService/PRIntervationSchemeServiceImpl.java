package org.ww.service.ProvinceService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ww.EntityDO.*;
import org.ww.EntityDO.ProvinceEntityDO.PRIntervationStatisticDO;
import org.ww.EntityDO.valueObject.CrisisLevel;
import org.ww.EntityVO.IntervationStatisticVO;
import org.ww.EntityVO.ProvinceEntityVO.PRIntervationSchemePageVO;
import org.ww.base.BaseEntity;
import org.ww.domain.School;
import org.ww.dto.PRIntervationDTO.PsyPRIntervationSchemeDTO;
import org.ww.service.impl.IntervationSchemeServiceImpl;

import java.util.*;

@Slf4j
@Service
public class PRIntervationSchemeServiceImpl{



    @Autowired
    IntervationSchemeServiceImpl schemeService;

//    public List<BaseEntity> getAllPRIntervationSchemePageVO(List<PsyPRIntervationSchemeDTO> fileDTOS) {
//        if (fileDTOS == null) return null;
//
//        List<BaseEntity> intervationSchemePageVOS = new ArrayList<>();
//        for (PsyPRIntervationSchemeDTO file : fileDTOS) {
//            try {
//                intervationSchemePageVOS.add(
//                        PRIntervationSchemePageVO.builder()
//                                .fileId(file.getFileDO().getId())
//                                .isTransfer(file.getRecordDO().getIsTransfer())
//                                .stuName(file.getStudentDO().getName())
//                                .stuClass(file.getStudentDO().getClasses())
//                                .stuGrade(file.getStudentDO().getGrade())
//                                .stuPeriod(file.getStudentDO().getPeriod())
//                                .stuState(file.getRecordDO().getStuState())
//                                .psyTeacherName(file.getTeacherDO().getName())
//                                .postProcessing(file.getRecordDO().getPostProcessing())
//                                .intervationContent(file.getPlanDO().getIntervationPlanContent())
//                                .crisisLevel(file.getRecordDO().getCrisisLevel())
//                                .city(file.getSchoolDO().getCity())
//                                .county(file.getSchoolDO().getCounty())
//                                .schoolName(file.getSchoolDO().getName())
//                                .createTime(file.getFileDO().getCreateTime())
//                                .expireTime(file.getRecordDO().getIntervationTime())
//                                .build());
//            } catch (Exception e) {
//                log.error("ERROR ====> {}", e);
//            }
//        }
//        return intervationSchemePageVOS;
//    }



    public List<IntervationStatisticVO> getPRIntervationStatisticVOs(List<PRIntervationStatisticDO> statisticDOS){
        //查询条件组成的key和统计实体的关系
//        Map<String, IntervationStatisticVO> statisticVOMap= new HashMap<>();
        List<IntervationStatisticVO> res= new LinkedList<>();

        for (PRIntervationStatisticDO statisticDO : statisticDOS){
            //根据查询条件拼接key值
//            String key = schemeService.getKey(
//                    statisticDO.getProvince(),
//                    statisticDO.getCity(),
//                    statisticDO.getCounty(),
//                    statisticDO.getSchoolId() == null ? "/" :statisticDO.getSchoolId().toString(),
//                    statisticDO.getSchoolName(),
//                    statisticDO.getPeriod(),
//                    statisticDO.getGrade(),
//                    statisticDO.getClasses());
//
//            if(statisticVOMap.containsKey(key) == false){
//                statisticVOMap.put(key,new IntervationStatisticVO());
//            }

            IntervationStatisticVO vo = new IntervationStatisticVO();

            vo.setPeriod(statisticDO.getPeriod());
            vo.setGrade(statisticDO.getGrade());
            vo.setClasses(statisticDO.getClasses());

            vo.setCity(statisticDO.getCity());
            vo.setCounty(statisticDO.getCounty());
            vo.setSchoolName(statisticDO.getSchoolName());

            vo.setALevel(statisticDO.getALevel());
            vo.setBLevel(statisticDO.getBLevel());
            vo.setCLevel(statisticDO.getCLevel());

            vo.setIntervationNums(statisticDO.getALevel() + statisticDO.getBLevel() + statisticDO.getCLevel());

            res.add(vo);

//            log.error("----------------------"+statisticDO.getCrisisLevel()+CrisisLevel.ALEAVEL.getVal());
//            String criisLevelStr = statisticDO.getCrisisLevel();
//            if(CrisisLevel.ALEAVEL.getVal().equals(criisLevelStr)){
//                vo.setALevel(vo.getALevel()+statisticDO.getIntervationNums());
//            }
//            else if(CrisisLevel.BLEAVEL.getVal().equals(criisLevelStr)){
//            vo.setBLevel(vo.getBLevel()+statisticDO.getIntervationNums());
//            }
//            else if(CrisisLevel.CLEAVEL.getVal().equals(criisLevelStr)){
//            vo.setCLevel(vo.getCLevel()+statisticDO.getIntervationNums());
//            }


        }

//        for(String key : statisticVOMap.keySet()){
//            IntervationStatisticVO v = statisticVOMap.get(key);
//            v.setIntervationNums(v.getALevel()+v.getBLevel()+v.getCLevel());
//            res.add(v);
//        }
        return res;
    }

}
