package org.ww.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ww.EntityDO.*;
import org.ww.EntityDO.valueObject.CrisisLevel;
import org.ww.EntityVO.*;
import org.ww.base.BaseEntity;
import org.ww.dto.PsyIntervationSchemeDTO;
import org.ww.mapper.ManagerMapper;
import org.ww.service.IntervationSchemeService;
import zipkin2.Call;

import java.util.*;

@Slf4j
@Service
public class IntervationSchemeServiceImpl implements IntervationSchemeService {



    @Override
    public List<BaseEntity> getAllIntervationSchemePageVO(List<PsyIntervationSchemeDTO> files) {
        if(files == null ) return null;

        List<BaseEntity> intervationSchemePageVOS = new ArrayList<>();
        for( PsyIntervationSchemeDTO file : files){
            try {
                intervationSchemePageVOS.add(
                        IntervationSchemePageVO.builder()
                                .isTransfer(file.getRecordDO().getIsTransfer())
                                .stuName(file.getStudentDO().getName())
                                .stuClass(file.getStudentDO().getClasses())
                                .stuGrade(file.getStudentDO().getGrade())
                                .stuPeriod(file.getStudentDO().getPeriod())
                                .stuState(file.getRecordDO().getStuState())
                                .psyTeacherName(file.getTeacherDO().getName())
                                .postProcessing(file.getRecordDO().getPostProcessing())
                                .intervationContent(file.getPlanDO().getIntervationPlanContent())
                                .crisisLevel(file.getRecordDO().getCrisisLevel())
                                .createTime(file.getFileDO().getCreateTime())
                                .fileId(file.getFileDO().getId())
                                .expireTime(file.getRecordDO().getIntervationTime())
                                .build());
            }catch (Exception e){
                log.error("ERROR ====> {}", e);
            }
        }
        return intervationSchemePageVOS;
    }
    @Override
    public List<PsyIntervationSchemeDTO> getAllIntervationSchemeDTO(List<PsyIntervationFileDO> files,
                                                          List<PsyIntervationRecordDO> records,
                                                          List<PsyIntervationStuDO> students,
                                                          List<PsyIntervationUserDO> users,
                                                          List<PsyIntervationPlanDO> plans,
                                                          List<PsyIntervationManagerDO> teachers) {

        if(files == null|| students==null || teachers==null ) return null;


        List<PsyIntervationSchemeDTO> schemeDTOS = new ArrayList<>();
        HashMap<Long,PsyIntervationStuDO> stuMap = new HashMap<>();
        HashMap<String, Long> userMap = new HashMap<>();
        HashMap<Long,PsyIntervationRecordDO> recordMap = new HashMap<>();
        HashMap<Long,PsyIntervationPlanDO> planMap = new HashMap<>();
        HashMap<Long, PsyIntervationManagerDO> teacherMap = new HashMap<>();

        for(PsyIntervationStuDO s : students){ stuMap.put(s.getId(),s); }
        for(PsyIntervationUserDO u : users){ userMap.put(u.getUsername(),u.getId());}
        for(PsyIntervationManagerDO t : teachers){
            if(userMap.containsKey(t.getUsername())) {
                teacherMap.put(userMap.get(t.getUsername()),t);
            }
        }

        for(PsyIntervationRecordDO r : records){ recordMap.put(r.getId(),r); }
        for(PsyIntervationPlanDO p : plans){ planMap.put(p.getId(),p); }

        for(PsyIntervationFileDO f : files){
//            schemeDTOS.add(
//                    PsyIntervationSchemeDTO
//                            .builder()
//                            .fileDO(f)
//                            .recordDO(recordMap.size() != 0 ? recordMap.get(f.getPsyIntervationRecordId()) : null)
//                            .planDO(planMap.size() != 0 ? planMap.get(f.getPsyIntervationPlanId()) : null)
//                            .studentDO(stuMap.get(f.getStudentId()))
//                            .teacherDO(teacherMap.get(f.getUserId()))
//                            .build()
//            );
        }

        return schemeDTOS;

    }



    @Override
    public List<IntervationStatisticVO> getIntervationStatisticVOs(List<IntervationStatisticDO> statisticDOS){
        if(statisticDOS == null ) return null;
        Map<String,IntervationStatisticVO> statisticVOMap= new HashMap<>();
        List<IntervationStatisticVO> res= new LinkedList<>();
        for (IntervationStatisticDO statisticDO : statisticDOS){
            String key = getKey(statisticDO.getPeriod(),statisticDO.getGrade(),statisticDO.getClasses());
            if(statisticVOMap.containsKey(key) == false){
                statisticVOMap.put(key,new IntervationStatisticVO());
            }
            IntervationStatisticVO vo = statisticVOMap.get(key);
            vo.setGrade(statisticDO.getGrade());
            vo.setPeriod(statisticDO.getPeriod());
            vo.setClasses(statisticDO.getClasses());

            if(statisticDO.getCrisisLevel().equals(CrisisLevel.ALEAVEL.getVal())){
                vo.setALevel(vo.getALevel()+statisticDO.getIntervationNums());
            }else if(statisticDO.getCrisisLevel().equals(CrisisLevel.BLEAVEL.getVal())){
                vo.setBLevel(vo.getBLevel()+statisticDO.getIntervationNums());
            }else if(statisticDO.getCrisisLevel().equals(CrisisLevel.CLEAVEL.getVal())){
                vo.setALevel(vo.getCLevel()+statisticDO.getIntervationNums());
            }


        }

        for(String key : statisticVOMap.keySet()){
            IntervationStatisticVO v = statisticVOMap.get(key);
            v.setIntervationNums(v.getALevel()+v.getBLevel()+v.getCLevel());
            res.add(v);
        }
        return res;
    }


    /**
     * 组装vo,plan record 、crisis 存放着fileId
     * @param crisisDOS
     * @param recordDOS
     * @param planDOS
     * @param fileDOS
     * @return
     */
    public List<PsyIntervationContentVO> getIntervationContentVOs(List<PsyIntervationRecordDO> crisisDOS,
                                                                  List<PsyIntervationRecordDO> recordDOS,
                                                                  List<PsyIntervationPlanDO> planDOS,
                                                                  List<PsyIntervationFileDO> fileDOS){
        if(crisisDOS == null || recordDOS == null || planDOS == null || fileDOS == null ) return new ArrayList<>();

        Map<Long,List<PsyIntervationRecordDO>> recordDOHashMap= new HashMap<>();
        Map<Long,List<PsyIntervationPlanDO>> planDOHashMap= new HashMap<>();
        Map<Long,List<PsyIntervationRecordDO>> crisisDOHashMap= new HashMap<>();

        //List<BaseEntity> contentVOS = new ArrayList<>();

        for(PsyIntervationRecordDO crisisDO:crisisDOS){
            if(crisisDOHashMap.containsKey(crisisDO.getFileId()) == false){
                crisisDOHashMap.put(crisisDO.getFileId(),new ArrayList<>());
            }
            crisisDOHashMap.get(crisisDO.getFileId()).add(crisisDO);
        }

        for(PsyIntervationRecordDO recordDO:recordDOS){
            if(recordDOHashMap.containsKey(recordDO.getFileId()) == false){
                recordDOHashMap.put(recordDO.getFileId(),new ArrayList<>());
            }
            recordDOHashMap.get(recordDO.getFileId()).add(recordDO);
        }

        for(PsyIntervationPlanDO planDO:planDOS){
            if(planDOHashMap.containsKey(planDO.getFileId()) == false){
                planDOHashMap.put(planDO.getFileId(),new ArrayList<>());
            }
            planDOHashMap.get(planDO.getFileId()).add(planDO);
        }


        List<PsyIntervationContentVO> contentVOS = new ArrayList<>();

        for(PsyIntervationFileDO file : fileDOS){

            PsyIntervationContentVO contentVO = new PsyIntervationContentVO();
            List crisis = crisisDOHashMap.get(file.getId());
            List record = recordDOHashMap.get(file.getId());
            List plan = planDOHashMap.get(file.getId());

            if(crisis == null || crisis.size() == 0) continue;

            contentVO.setPsyIntervationCrisisDO(crisis);
            contentVO.setPsyIntervationRecordDO(record == null ? new ArrayList<>():record);
            contentVO.setPsyIntervationPlanDO(plan == null ? new ArrayList<>():plan);

            contentVOS.add( contentVO );
        }


        return contentVOS;
    }


}
