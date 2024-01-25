package org.ww.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.domain.TelCdrInfo;
import org.ww.domain.TelGroup;
import org.ww.domain.TelNote;
import org.ww.dto.CallInfoDto;
import org.ww.dto.StatisticDto;
import org.ww.dto.TelCdrDto;

import java.util.Date;
import java.util.List;

public interface HotlineCallService {
    String generateAccount(TelGroup telGroup);

    TelGroup findTelGroupByUsername(String username);

    TelCdrInfo checkToAddCdr(TelCdrInfo telCdrInfo);

    CallInfoDto getLastCallInfo(TelCdrInfo telCdrInfo);

    TelGroup findOldestCdrProceed();

    void updateTelGroupCdrTimestamp(Long id, Date now);

    void saveTelNoteBySessionId(TelNote telNote, String sessionId);

    /**
     * 查询呼入的历史记录
     * @param isSucceed 已接：1；未接：0
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<TelCdrDto> listCallIn(Long telGroupId, String caller, Integer isSucceed, Integer pageNum, Integer pageSize);

    TelGroup findTelGroupByUsernameOrAdmin(String username);

    TelGroup findTelGroupByAdmin(String username);

    TelCdrInfo getTelRecordToDownload();

    void updateTelCdrLocalPath(Long id, String s);

    void updateTelCdrCallerCount(Long telGroupId);

    TelGroup findTelGroupById(Long id);

    List<StatisticDto> cdrStatisticsByContinuousDate(Long telGroupId, Date startDate, Integer size);

    List<StatisticDto> noteStatisticsByType(Long telGroupId);

    List<StatisticDto> noteStatisticsByLevel(Long telGroupId);

    IPage<StatisticDto> cdrStatisticsByDate(Long telGroupId, Date selectedDate, Integer pageNum, Integer pageSize);
}
