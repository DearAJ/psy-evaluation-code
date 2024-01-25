package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ww.domain.TelBlacklist;
import org.ww.dto.BlacklistDto;
import org.ww.mapper.TelBlacklistMapper;
import org.ww.service.HotlineBlacklistService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HotlineBlacklistServiceImpl implements HotlineBlacklistService {

    @Resource
    private TelBlacklistMapper telBlacklistMapper;

    @Override
    public int isBlocked(Long telGroupId, String caller) {
        if(findByPhone(telGroupId, caller) != null)
        {
            return 1;
        }
        return 0;
    }

    @Override
    public IPage<BlacklistDto> list(Long telGroupId, Integer pageNum, Integer pageSize) {

        Integer total = telBlacklistMapper.selectCount(new QueryWrapper<TelBlacklist>()
                .eq("tel_group_id", telGroupId)
                .eq("deleted", 0));
        IPage<BlacklistDto> page = new Page<>(pageNum, pageSize, total);
        List<BlacklistDto> records = telBlacklistMapper.selectBlackAndUserInfo(telGroupId, null, (pageNum - 1) * pageSize, pageSize);

        page.setRecords(records);

        return page;
    }

    @Override
    public TelBlacklist checkToAdd(TelBlacklist black) {
        TelBlacklist existed = findByPhone(black.getTelGroupId(), black.getPhone());
        if(existed == null)
        {
            black.setCreateTime(new Date());
            telBlacklistMapper.insert(black);
        }else {
            black.setId(existed.getId());
            telBlacklistMapper.updateById(black);
        }


        return black;
    }

    @Override
    public int removeByPhone(String phone, Long telGroupId) {

        return telBlacklistMapper.delete(new QueryWrapper<TelBlacklist>()
                .eq("tel_group_id", telGroupId)
                .eq("phone", phone));
    }

    @Override
    public TelBlacklist findByPhone(Long telGroupId, String phone) {
        return telBlacklistMapper.selectOne(new QueryWrapper<TelBlacklist>()
                .eq("tel_group_id", telGroupId)
                .eq("phone", phone)
                .eq("deleted", 0)
                .last("limit 1"));
    }

    @Override
    public BlacklistDto findDetailsByPhone(Long telGroupId, String phone) {
        List<BlacklistDto> records = telBlacklistMapper.selectBlackAndUserInfo(telGroupId, phone, 0, 1);
        if(records.size() > 0)
        {
            return records.get(0);
        }
        return null;
    }
}
