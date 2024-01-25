package org.ww.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.ww.mapper.MenuMapper;
import org.ww.domain.Menu;
import org.ww.service.MenuService;
import org.ww.vo.MenuVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Resource
    private MenuMapper menuMapper;

    @Override
    public List<MenuVo> getMenuByRoleId(String roleId) {
        return menuMapper.getMenuByRoleId(roleId);
    }

}
