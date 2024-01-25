package org.ww.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.ww.domain.TelBlacklist;
import org.ww.dto.BlacklistDto;

public interface HotlineBlacklistService {
    int isBlocked(Long telGroupId, String caller);

    IPage<BlacklistDto> list(Long telGroupId, Integer pageNum, Integer pageSize);

    TelBlacklist checkToAdd(TelBlacklist black);

    int removeByPhone(String phone, Long telGroupId);

    TelBlacklist findByPhone(Long telGroupId, String phone);

    BlacklistDto findDetailsByPhone(Long telGroupId, String phone);
}
