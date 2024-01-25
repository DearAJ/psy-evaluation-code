package org.ww.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;
import org.ww.dto.ManagerDto;
import org.ww.result.Wrapper;
import org.ww.domain.User;
import org.ww.vo.UserVo;

import java.text.ParseException;
import java.util.Map;

public interface UserService extends IService<User> {

    UserVo findByUsername(String username);

    String importUsers(String fileName, MultipartFile file);

    /**
     * getUserInfo  根据token获取用户信息
     * @param token String
     * @return map
     */
    Wrapper<Object> getUserInfo(String token) throws ParseException;

    /**
     * 上传头像
     * @param token token
     * @param fileName 文件名
     * @param fileType 文件类型
     * @param filePath 文件路径（由nginx保存的路径）
     * @param fileSize 文件大小
     * @return 返回图片请求地址
     */
    Wrapper<String> uploadAvatar(String token, String fileName, String fileType, String filePath, String fileSize);

    /**
     * 更新用户信息
     * @param managerDto managerDto
     * @return 返回是否成功
     */
    Wrapper<String> updateManager(ManagerDto managerDto);

    /**
     * 修改密码
     * @param jsonObject jsonObject
     * @return 返回是否修改成功
     */
    Wrapper<String> modifyPass(JSONObject jsonObject);
}
