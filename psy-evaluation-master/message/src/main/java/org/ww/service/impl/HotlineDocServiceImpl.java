package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.ww.domain.TelDoc;
import org.ww.mapper.TelDocMapper;
import org.ww.result.WrapMapper;
import org.ww.service.HotlineDocService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class HotlineDocServiceImpl implements HotlineDocService {

    @Resource
    private TelDocMapper telDocMapper;

    @Override
    public IPage<TelDoc> query(Long telGroupId, String keywords, Integer pageNum, Integer pageSize) {
        if(StringUtils.isBlank(keywords))
        {
            return list(telGroupId, pageNum, pageSize);
        }

        Integer total = telDocMapper.queryCount(telGroupId, keywords);

        IPage<TelDoc> page = new Page<>(pageNum, pageSize, total);
        List<TelDoc> records = telDocMapper.query(telGroupId, keywords.replaceAll("　", " "), (pageNum - 1) * pageSize, pageSize);
        page.setRecords(records);

        return page;
    }

    @Override
    public int remove(Long telGroupId, Long id) {
        TelDoc telDoc = telDocMapper.selectById(id);
        if(telDoc != null && Objects.equals(telDoc.getTelGroupId(), telGroupId))
        {
            return telDocMapper.deleteById(id);
        }

        return 0;
    }

    @Override
    public TelDoc add(TelDoc doc) {
        telDocMapper.insert(doc);
        return doc;
    }

    @Override
    public IPage<TelDoc> list(Long telGroupId, Integer pageNum, Integer pageSize) {
        IPage<TelDoc> page = new Page<>(pageNum, pageSize);
        return telDocMapper.selectPage(page, new QueryWrapper<TelDoc>()
                .eq("tel_group_id", telGroupId)
                .eq("deleted", 0)
                .orderByDesc("id"));
    }

    @Override
    public TelDoc get(Long telGroupId, Long id) {
        TelDoc telDoc = telDocMapper.selectById(id);
        if(telDoc == null || telDoc.getId().equals(telGroupId))
        {
            return null;
        }

        return telDoc;

    }

    @Override
    public int update(Long telGroupId, TelDoc doc) {
        TelDoc existed = get(telGroupId, doc.getId());
        if(existed == null)
        {
            return 0;
        }

        doc.setTelGroupId(existed.getTelGroupId());
        doc.setUpdateTime(new Date());

        return telDocMapper.updateById(doc);
    }
}
