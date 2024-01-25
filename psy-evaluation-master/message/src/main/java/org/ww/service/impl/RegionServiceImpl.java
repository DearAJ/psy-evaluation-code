package org.ww.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.ww.domain.Region;
import org.ww.mapper.RegionMapper;
import org.ww.service.RegionService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl implements RegionService {

    @Resource
    private RegionMapper regionMapper;
    @Override
    public List<Region> allRegionTree() {
        List<Region> all = regionMapper.selectList(new QueryWrapper<Region>()
                .gt("pid", 0)
                .orderByAsc("id"));
        Map<Integer, List<Region>> map = all.stream().collect(Collectors.groupingBy(Region::getPid));
        List<Region> result = new ArrayList<>();
        result.addAll(map.get(100000));
        for(Region province : result)
        {
            List<Region> cities = map.get(province.getId());
            if(cities != null)
            {
                for(Region city : cities)
                {
                    city.setChildren(map.get(city.getId()));
                }
            }
            province.setChildren(cities);
        }

        return result;
    }
}
