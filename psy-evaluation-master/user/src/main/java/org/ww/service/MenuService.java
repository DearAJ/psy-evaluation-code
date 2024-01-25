package org.ww.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ww.domain.Menu;
import org.ww.vo.MenuVo;

import java.util.List;

public interface MenuService extends IService<Menu> {

    List<MenuVo> getMenuByRoleId(String roleId);

}
