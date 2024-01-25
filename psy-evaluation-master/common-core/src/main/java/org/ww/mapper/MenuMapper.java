package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.ww.domain.Menu;
import org.ww.vo.MenuVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<MenuVo> getMenuByRoleId(String roleId);

}
