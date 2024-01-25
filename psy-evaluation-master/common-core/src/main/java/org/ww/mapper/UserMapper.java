package org.ww.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.ww.domain.User;
import org.ww.vo.UserVo;

import java.util.List;
import java.util.Map;


@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 根据username获取用户信息
    UserVo findByUsername(String username);
    // 获取用户信息
    Map<String, Object> getUserInfo(Integer id);
    // 学校端用户第一次登录时需填写个人信息激活账号，此时也可能会更新头像
    Integer updateUserEnabled(String username, String avatar, Integer enabled);
    // 根据用户名批量删除用户表（删除其实是更新deleted为1）
    Integer deleteUsersByUsernames(List<String> usernames);
    // 根据用户名修改密码
    Integer modifyPass(String username, String password);
    // 根据user_id查询用户名和学生姓名班级
    Map<String, String> getNameByUserId(Integer user_id, String schoolYear);
}
