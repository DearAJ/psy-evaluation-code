package org.ww.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.ww.domain.Notice;
import org.ww.result.Wrapper;

/**
 * @Author 13096
 * @Date 2022/6/17 19:28
 * @Version 1.0
 */
public interface NoticeService extends IService<Notice> {
    /**
     * 上传图片或附件
     * @param fileName 文件名
     * @param fileType 文件类型
     * @param filePath 文件路径
     * @param fileSize 文件大小
     * @param type 图片or附件
     * @return 返回文件所在服务器路径
     */
    Wrapper<String> noticeAppendixUpload(String fileName, String fileType, String filePath, String fileSize, String type);

    /**
     * 删除图片或附件
     * @param url 文件url
     * @return 返回是否删除成功
     */
    Wrapper<String> noticeAppendixDelete(String url);

    /**
     * 添加通知
     * @param notice notice对象
     * @return 返回是否成功
     */
    Wrapper<String> addNotice(Notice notice);

    /**
     * 通知列表
     * @param token token
     * @param pageNum 当前页数
     * @param pageSize 每页大小
     * @param title 筛选标题
     * @param date 筛选日期
     * @return 返回notice列表
     */
    Wrapper<Object> noticeList(String token, Integer pageNum, Integer pageSize, String title, String date);

    /**
     * 通知列表
     * @param token token
     * @param id id
     * @return 返回notice详情
     */
    Wrapper<Object> getNotice(String token, Long id);
}
