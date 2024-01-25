package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.ww.domain.TelCdrInfo;
import org.ww.domain.TelContact;
import org.ww.dto.StatisticDto;
import org.ww.mapper.TelCdrInfoMapper;
import org.ww.mapper.TelContactMapper;
import org.ww.service.HotlineContactService;
import org.ww.utils.PhoneUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HotlineContactServiceImpl implements HotlineContactService {

    @Resource
    private TelContactMapper telContactMapper;

    @Resource
    private TelCdrInfoMapper telCdrInfoMapper;

    @Override
    public TelContact saveByPhone(TelContact telContact) {
        //
        telContact.setPhone(PhoneUtils.toFullPhoneNumber(telContact.getPhone()));

        TelContact existed = findByPhone(telContact.getTelGroupId(), telContact.getPhone());
        if(existed == null)
        {
            // 新增
            telContact.setCreateTime(new Date());
            telContact.setUpdateTime(telContact.getCreateTime());
            telContact.setDeleted(0);
            telContactMapper.insert(telContact);
        }else {
            // 修改
            telContact.setId(existed.getId());
            telContact.setUpdateTime(new Date());
            telContactMapper.updateById(telContact);
            telContact.setCreateTime(existed.getCreateTime());
        }

        return telContact;
    }

    @Override
    public TelContact findByPhone(Long telGroupId, String phone) {
        return telContactMapper.selectOne(new QueryWrapper<TelContact>()
                .eq("phone", phone)
                .eq("tel_group_id", telGroupId)
                .eq("deleted", 0));
    }

    @Override
    public TelContact details(Long telGroupId, String phone) {
        TelContact contact = findByPhone(telGroupId, phone);
        TelCdrInfo cdr = telCdrInfoMapper.selectOne(new QueryWrapper<TelCdrInfo>()
                .eq("caller", phone)
                .eq("direction", 0)
                .eq("tel_group_id", telGroupId)
                .isNotNull("end_status")
                .isNotNull("call_in_count")
                .orderByDesc("time")
                .last("limit 1"));
        if(cdr != null && cdr.getCallInCount() != null)
        {
            contact.setCallInCount(cdr.getCallInCount());
        }else {
            contact.setCallInCount(0L);
        }

        return contact;
    }

    @Override
    public IPage<TelContact> list(TelContact query, Integer pageNum, Integer pageSize) {

        IPage<TelContact> page = new Page<>(pageNum, pageSize);

        return telContactMapper.selectPage(page, new QueryWrapper<TelContact>()
                .eq("tel_group_id", query.getTelGroupId())
                .like(StringUtils.isNotBlank(query.getPhone()), "phone", "%" + query.getPhone()+ "%")
                .eq(StringUtils.isNotBlank(query.getProvince()), "province", query.getProvince())
                .eq(StringUtils.isNotBlank(query.getCity()), "city", query.getCity())
                .eq(StringUtils.isNotBlank(query.getCounty()), "county", query.getCounty())
                .eq(StringUtils.isNotBlank(query.getLastQuestionLevel()), "last_question_level", query.getLastQuestionLevel()));
    }

    @Override
    public void batchSave(List<TelContact> cachedDataList) {
        if(cachedDataList == null)
        {
            return;
        }
        log.info("saving..." + cachedDataList);
        for(TelContact contact: cachedDataList)
        {
            saveByPhone(contact);
        }

    }

    @Override
    public List<StatisticDto> contactStatisticsByRegion(Long telGroupId) {
        List<StatisticDto> statisticDtos = telContactMapper.selectRegionStatistics(telGroupId);
        return statisticDtos;
    }
}
