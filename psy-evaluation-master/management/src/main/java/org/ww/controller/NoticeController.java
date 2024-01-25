package org.ww.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ww.domain.Notice;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.NoticeService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

/**
 * @Author 13096
 * @Date 2022/6/17 19:32
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("noticeAppendix.upload")
    public Wrapper<String> noticeAppendixUpload(HttpServletRequest request) {
        String fileName = request.getParameter("file_name");
        String fileType = request.getParameter("file_content_type");
        String tempPath = request.getParameter("file_path");
        String fileSize = request.getParameter("file_size");
        String type = request.getParameter("type");
        log.info("file_name:{}", fileName);
        return noticeService.noticeAppendixUpload(fileName, fileType, tempPath, fileSize, type);
    }

    @PostMapping("noticeAppendixDelete")
    public Wrapper<String> noticeAppendixDelete(HttpServletRequest request) {
        String url = request.getParameter("url");
        return noticeService.noticeAppendixDelete(url);
    }

    @PostMapping("addNotice")
    public Wrapper<String> addNotice(Notice notice) {
        return noticeService.addNotice(notice);
    }

    @PostMapping("noticeList")
    public Wrapper<Object> noticeList(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.equals("")) {
            return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, new ArrayList<>());
        }
        Integer pageNum = Integer.valueOf(request.getParameter("pageNum"));
        Integer pageSize = Integer.valueOf(request.getParameter("pageSize"));
        String title = "";
        if (request.getParameterMap().containsKey("title")) {
            title = request.getParameter("title");
        }
        String date = "";
        if (request.getParameterMap().containsKey("issuedTime")) {
            date = request.getParameter("issuedTime");
        }
        return noticeService.noticeList(token, pageNum, pageSize, title, date);
    }

    @PostMapping("getNotice")
    public Wrapper<Object> getNotice(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        Long id = Long.valueOf(request.getParameter("id"));
        return noticeService.getNotice(token, id);
    }
}
