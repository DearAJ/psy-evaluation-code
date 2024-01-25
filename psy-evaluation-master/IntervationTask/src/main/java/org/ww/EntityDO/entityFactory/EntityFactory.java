package org.ww.EntityDO.entityFactory;

import org.springframework.stereotype.Service;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.EntityDO.PsyIntervationPlanDO;
import org.ww.EntityDO.PsyIntervationRecordDO;
import org.ww.EntityDO.valueObject.CrisisLevel;
import org.ww.EntityDO.valueObject.IntervationWay;
import org.ww.EntityDO.valueObject.StuState;

import java.sql.Date;
import java.sql.Timestamp;

@Service
public class EntityFactory {
    public static Long timeGap = 2592000000L;//30天
    public static PsyIntervationFileDO getBaseFileDO(Long taskId,Long stuId,Long userId){
        return PsyIntervationFileDO.builder()
                .userId(userId)
                .taskId(taskId)
                .studentId(stuId)
                .createTime(new Timestamp(System.currentTimeMillis()))
                .build();
    }

    public static PsyIntervationRecordDO getTestRecordDO(){
        return PsyIntervationRecordDO.builder()
                .crisisLevel(CrisisLevel.ELEAVEL.getVal().toString())
                .intervationWay(IntervationWay.FACE.getVal())
                .intervationRecord("这是一个测试的记录")
                .postProcessing("就医")
                .stuState(StuState.INHOME.getVal()).build();
    }
    public static PsyIntervationPlanDO getTestPlanDO(){
        return PsyIntervationPlanDO.builder()
                .symptom("测试症状")
                .intervationPlanContent("这是个测试方案")
                .conclusion("没毛病")
                .build();
    }
}
