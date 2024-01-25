package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ww.domain.TelCommonCode;
import org.ww.mapper.TelCommonCodeMapper;
import org.ww.service.HotlineCommonCodeService;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class HotlineCommonCodeServiceImpl implements HotlineCommonCodeService {

    @Resource
    private TelCommonCodeMapper telCommonCodeMapper;
    @Override
    public List<TelCommonCode> listByPage(String pageName) {
        return telCommonCodeMapper.selectList(new QueryWrapper<TelCommonCode>()
                .eq("page_name", pageName)
                .eq("deleted", 0)
                .orderByDesc("priority", "id"));

    }
}
