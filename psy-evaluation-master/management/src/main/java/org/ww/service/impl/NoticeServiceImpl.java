package org.ww.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ww.constants.Constants;
import org.ww.domain.*;
import org.ww.exception.BusinessException;
import org.ww.mapper.*;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.NoticeService;
import org.ww.utils.JWTUtils;
import org.ww.vo.UserVo;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author 13096
 * @Date 2022/6/17 19:28
 * @Version 1.0
 */
@Slf4j
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
    @Resource
    private NoticeMapper noticeMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private ManagerMapper managerMapper;

    @Resource
    private SchoolRoleMapper schoolRoleMapper;

    @Resource
    private SchoolMapper schoolMapper;

    @Autowired
    private TaskServiceImpl taskService;

    @Override
    public Wrapper<String> noticeAppendixUpload(String fileName, String fileType, String filePath, String fileSize, String type) {
        Process process = null;
        try {
            String path = type.equals("image") ? Constants.noticeImagePath : Constants.noticeAppendixPath;

            // 按月份分类
            Calendar calendar = Calendar.getInstance();
            String dir = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月";
            path = path + dir;
            File file = new File(Constants.baseDir + path);
            if (!file.exists()) {
                file.setWritable(true, false);
                file.mkdirs();
                log.info("mkdir path:{}", Constants.baseDir + path);
            }
            log.info("dir path:{}", Constants.baseDir + path);
            // 生成文件名
            String newFileName = type.equals("image") ? "psa" + UUID.randomUUID().toString().replaceAll("-", "") + fileName : fileName;
            // 目标文件路径
            String newFilePath = path + "/" + newFileName;
            log.info("file path:{}", Constants.baseDir + newFilePath);
            process = Runtime.getRuntime().exec("mv " + filePath + " " + Constants.baseDir + newFilePath);
            process.waitFor(5, TimeUnit.SECONDS);
            // 记录url
            String url = "http://" + Constants.host + newFilePath;
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, url);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    public Wrapper<String> noticeAppendixDelete(String url) {
        Process process = null;
        try {
            String path = url.split(Constants.host)[1];
            path = Constants.baseDir + path;

            process = Runtime.getRuntime().exec("rm " + path);
            process.waitFor(5, TimeUnit.SECONDS);

            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, "success");
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    public Wrapper<String> addNotice(Notice notice) {
        try {
            Date issuedTime = new Date();
            notice.setIssuedTime(issuedTime);
            noticeMapper.insert(notice);
            log.info("add Notice: {}", JSON.toJSON(notice));
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> noticeList(String token, Integer pageNum, Integer pageSize, String title, String date) {
        try {
            SignedJWT signedJWT = JWTUtils.getSignedJWT(token);
            Object payload = signedJWT.getJWTClaimsSet().getClaim("payload");
            UserVo userVo = JSON.parseObject(payload.toString(), UserVo.class);
            String username = userVo.getUsername();
            // 查询用户权限
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.select("level").eq("username", username);
            roleQueryWrapper.orderByDesc("id").last("limit 1");
            int level = roleMapper.selectOne(roleQueryWrapper).getLevel();

            String province;

            if (level > 1) {
                QueryWrapper<Manager> managerQueryWrapper = new QueryWrapper<>();
                managerQueryWrapper.select("province").eq("username", username);
                managerQueryWrapper.orderByDesc("id").last("limit 1");
                province = managerMapper.selectOne(managerQueryWrapper).getProvince();
            } else {
                QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                schoolRoleQueryWrapper.select("school_id").eq("username", username);
                Long school_id = schoolRoleMapper.selectOne(schoolRoleQueryWrapper).getSchoolId();
                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.select("province").eq("id", school_id);
                province = schoolMapper.selectOne(schoolQueryWrapper).getProvince();
            }



            // 查询对应的通知列表
            QueryWrapper<Notice> noticeQueryWrapper = new QueryWrapper<>();
            noticeQueryWrapper.select("id", "author", "issued_unit", "title", "issued_time").eq("province", province).eq("city", "").eq("county", "").eq("school", "");
            if (!title.equals("")) {
                noticeQueryWrapper.like("title", title);
            }

            if (!date.equals("")) {
                noticeQueryWrapper.last("and DATE_FORMAT(issued_time, '%Y-%m-%d') = '" + date + "'");
            }

            List<Notice> noticeList = noticeMapper.selectList(noticeQueryWrapper);

            if (level <= 5 && level > 1) {
                QueryWrapper<Manager> managerQueryWrapper1 = new QueryWrapper<>();
                managerQueryWrapper1.select("province", "city").eq("username", username);
                managerQueryWrapper1.orderByDesc("id").last("limit 1");
                Manager manager = managerMapper.selectOne(managerQueryWrapper1);
                // 查询对应的通知列表
                QueryWrapper<Notice> noticeQueryWrapper1 = new QueryWrapper<>();
                noticeQueryWrapper1.select("id", "author", "issued_unit", "title", "issued_time").eq("province", manager.getProvince()).eq("city", manager.getCity()).eq("county", "").eq("school", "");
                if (!title.equals("")) {
                    noticeQueryWrapper1.like("title", title);
                }

                if (!date.equals("")) {
                    noticeQueryWrapper1.last("and DATE_FORMAT(issued_time, '%Y-%m-%d') = '" + date + "'");
                }
                noticeList.addAll(noticeMapper.selectList(noticeQueryWrapper1));
            }
            if (level <= 4 && level > 1) {
                QueryWrapper<Manager> managerQueryWrapper2 = new QueryWrapper<>();
                managerQueryWrapper2.select("province", "city", "county").eq("username", username);
                managerQueryWrapper2.orderByDesc("id").last("limit 1");
                Manager manager = managerMapper.selectOne(managerQueryWrapper2);
                // 查询对应的通知列表
                QueryWrapper<Notice> noticeQueryWrapper2 = new QueryWrapper<>();
                noticeQueryWrapper2.select("id", "author", "issued_unit", "title", "issued_time").eq("province", manager.getProvince()).eq("city", manager.getCity()).eq("county", manager.getCounty()).eq("school", "");
                if (!title.equals("")) {
                    noticeQueryWrapper2.like("title", title);
                }

                if (!date.equals("")) {
                    noticeQueryWrapper2.last("and DATE_FORMAT(issued_time, '%Y-%m-%d') = '" + date + "'");
                }
                noticeList.addAll(noticeMapper.selectList(noticeQueryWrapper2));
            }
            if (level <= 3 && level > 1) {
                QueryWrapper<Manager> managerQueryWrapper3 = new QueryWrapper<>();
                managerQueryWrapper3.select("province", "city", "county", "school").eq("username", username);
                managerQueryWrapper3.orderByDesc("id").last("limit 1");
                Manager manager = managerMapper.selectOne(managerQueryWrapper3);

                // 查询对应的通知列表
                QueryWrapper<Notice> noticeQueryWrapper3 = new QueryWrapper<>();
                noticeQueryWrapper3.select("id", "author", "issued_unit", "title", "issued_time").eq("province", manager.getProvince()).eq("city", manager.getCity()).eq("county", manager.getCounty()).eq("school", manager.getSchool());
                if (!title.equals("")) {
                    noticeQueryWrapper3.like("title", title);
                }

                if (!date.equals("")) {
                    noticeQueryWrapper3.last("and DATE_FORMAT(issued_time, '%Y-%m-%d') = '" + date + "'");
                }
                noticeList.addAll(noticeMapper.selectList(noticeQueryWrapper3));
            } else if (level == 1) {
                QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                schoolRoleQueryWrapper.select("school_id").eq("username", username);
                Long schoolId = schoolRoleMapper.selectOne(schoolRoleQueryWrapper).getSchoolId();

                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.select("province", "city", "county", "name").eq("id", schoolId);
                School school = schoolMapper.selectOne(schoolQueryWrapper);

                // 查询对应的通知列表
                QueryWrapper<Notice> noticeQueryWrapper4 = new QueryWrapper<>();
                noticeQueryWrapper4.select("id", "author", "issued_unit", "title", "issued_time").eq("province", school.getProvince()).eq("city", school.getCity()).eq("county", school.getCounty()).eq("school", school.getName());
                if (!title.equals("")) {
                    noticeQueryWrapper4.like("title", title);
                }

                if (!date.equals("")) {
                    noticeQueryWrapper4.last("and DATE_FORMAT(issued_time, '%Y-%m-%d') = '" + date + "'");
                }
                noticeList.addAll(noticeMapper.selectList(noticeQueryWrapper4));
            }
            if (noticeList == null || noticeList.size() == 0) {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, new ArrayList<>());
            }
            Collections.sort(noticeList, new CalendarComparator());
            IPage<List<Notice>> page = taskService.getPages(pageNum, pageSize, noticeList);
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, page);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @Override
    public Wrapper<Object> getNotice(String token, Long id) {
        try {
            SignedJWT signedJWT = JWTUtils.getSignedJWT(token);
            Object payload = signedJWT.getJWTClaimsSet().getClaim("payload");
            UserVo userVo = JSON.parseObject(payload.toString(), UserVo.class);
            String username = userVo.getUsername();
            // 查询用户权限
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.select("level").eq("username", username);
            roleQueryWrapper.orderByDesc("id").last("limit 1");
            int level = roleMapper.selectOne(roleQueryWrapper).getLevel();

            Notice notice = noticeMapper.selectById(id);

            if (level >= 3) {
                QueryWrapper<Manager> managerQueryWrapper = new QueryWrapper<>();
                managerQueryWrapper.select("province", "city", "county", "school").eq("username", username);
                managerQueryWrapper.orderByDesc("id").last("limit 1");
                Manager manager = managerMapper.selectOne(managerQueryWrapper);

                if (!manager.getProvince().equals(notice.getProvince())) {
                    return WrapMapper.wrap(Wrapper.ERROR_CODE, "无权访问该通知");
                }

                if (manager.getCity() != null && !manager.getCity().equals("")) {
                    if (notice.getCity() != null && !notice.getCity().equals("") && !manager.getCity().equals(notice.getCity())) {
                        return WrapMapper.wrap(Wrapper.ERROR_CODE, "无权访问该通知");
                    }
                }

                if (manager.getCounty() != null && !manager.getCounty().equals("")) {
                    if (notice.getCounty() != null && !notice.getCounty().equals("") && !manager.getCounty().equals(notice.getCounty())) {
                        return WrapMapper.wrap(Wrapper.ERROR_CODE, "无权访问该通知");
                    }
                }
            } else {
                QueryWrapper<SchoolRole> schoolRoleQueryWrapper = new QueryWrapper<>();
                schoolRoleQueryWrapper.select("school_id").eq("username", username);
                Long schoolId = schoolRoleMapper.selectOne(schoolRoleQueryWrapper).getSchoolId();

                QueryWrapper<School> schoolQueryWrapper = new QueryWrapper<>();
                schoolQueryWrapper.select("province", "city", "county", "name").eq("id", schoolId);
                School school = schoolMapper.selectOne(schoolQueryWrapper);

                if (!school.getProvince().equals(notice.getProvince())) {
                    return WrapMapper.wrap(Wrapper.ERROR_CODE, "无权访问该通知");
                }

                if (school.getCity() != null && !school.getCity().equals("")) {
                    if (notice.getCity() != null && !notice.getCity().equals("") && !school.getCity().equals(notice.getCity())) {
                        return WrapMapper.wrap(Wrapper.ERROR_CODE, "无权访问该通知");
                    }
                }

                if (school.getCounty() != null && !school.getCounty().equals("")) {
                    if (notice.getCounty() != null && !notice.getCounty().equals("") && !school.getCounty().equals(notice.getCounty())) {
                        return WrapMapper.wrap(Wrapper.ERROR_CODE, "无权访问该通知");
                    }
                }

                if (school.getName() != null && !school.getName().equals("")) {
                    if (notice.getSchool() != null && !notice.getSchool().equals("") && !school.getName().equals(notice.getSchool())) {
                        return WrapMapper.wrap(Wrapper.ERROR_CODE, "无权访问该通知");
                    }
                }
            }
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, notice);
        } catch (BusinessException e) {
            return WrapMapper.wrap(Wrapper.ERROR_CODE, e.getMessage());
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    // 时间倒序排序
    private static class CalendarComparator implements Comparator {
        public int compare(Object object1, Object object2) {
            //实现接口中的方法
            Notice p1 = (Notice) object1; // 强制转换
            Notice p2 = (Notice) object2;
            return p2.getIssuedTime().compareTo(p1.getIssuedTime());
        }
    }
}
