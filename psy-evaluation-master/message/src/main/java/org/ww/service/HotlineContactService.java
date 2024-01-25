package org.ww.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.domain.TelContact;
import org.ww.dto.StatisticDto;

import java.util.List;

public interface HotlineContactService {
    TelContact saveByPhone(TelContact telContact);

    TelContact findByPhone(Long telGroupId, String phone);

    TelContact details(Long telGroupId, String phone);

    IPage<TelContact> list(TelContact query, Integer pageNum, Integer pageSize);

    void batchSave(List<TelContact> cachedDataList);

    List<StatisticDto> contactStatisticsByRegion(Long telGroupId);
}
