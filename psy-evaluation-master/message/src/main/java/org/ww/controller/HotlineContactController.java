package org.ww.controller;

import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.ww.domain.TelContact;
import org.ww.domain.TelGroup;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.HotlineCallService;
import org.ww.service.HotlineContactService;
import org.ww.utils.PhoneUtils;
import org.ww.xlsx.ContactUploadDataListener;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/hotline/contact")
public class HotlineContactController extends BaseController {
    private HotlineContactService hotlineContactService;

    private final HotlineCallService hotlineCallService;

    public HotlineContactController(HotlineContactService hotlineContactService,
                                    HotlineCallService hotlineCallService)
    {
        this.hotlineContactService = hotlineContactService;
        this.hotlineCallService = hotlineCallService;
    }

    @RequestMapping("/list")
    public Wrapper<Object> list(TelContact user, Integer pageNum, Integer pageSize)
    {
        Long telGroupId = currentTelGroupId();

        user.setTelGroupId(telGroupId);

        pageNum = (pageNum == null ? 1 : pageNum);
        pageSize = (pageSize == null ? Integer.MAX_VALUE : pageSize);

        return WrapMapper.ok(hotlineContactService.list(user, pageNum, pageSize));

    }

    @RequestMapping("/details")
    public Wrapper<Object> details(String phone)
    {
        if(StringUtils.isBlank(phone))
        {
            return WrapMapper.error("请输入【phone】参数");
        }
        Long telGroupId = currentTelGroupId();

        return WrapMapper.ok(hotlineContactService.details(telGroupId, PhoneUtils.toFullPhoneNumber(phone)));

    }

    @PostMapping("/add")
    public Wrapper<Object> add(@RequestBody TelContact telContact)
    {
        Long telGroupId = currentTelGroupId();

        telContact.setTelGroupId(telGroupId);

        return WrapMapper.ok(hotlineContactService.saveByPhone(telContact));

    }

    @ResponseBody
    @GetMapping("/template")
    public Wrapper<Object> downloadTemplate(HttpServletResponse response) throws IOException
    {
        TelGroup group = hotlineCallService.findTelGroupById( currentTelGroupId());

        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("用户信息模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        TelContact contact = new TelContact();
        contact.setName("张三");
        contact.setRole("学生");
        contact.setPhone("13188888888");
        contact.setIdCard("222222222222222222");
        contact.setAge(10);
        contact.setProvince(group.getProvince());
        contact.setCity(group.getCity());
        contact.setCounty(group.getCounty());
        contact.setGender("男");
        contact.setSchoolName("三墩小学");
        contact.setStudyPeriod("小学");
        contact.setEducation("在读");
        contact.setSchoolGrade("四年级");
        contact.setSchoolClass("一班");
        contact.setAddress("xxx路xx号xxx室");
        contact.setSingleChild("是");
        contact.setBirthOrder("老二");
        contact.setParentalMarriageState("离异");
        contact.setLastQuestionLevel("无危机");
        EasyExcel.write(response.getOutputStream(), TelContact.class).sheet("用户信息")
                .doWrite(Arrays.asList(contact));

        return WrapMapper.ok();

    }

    @ResponseBody
    @PostMapping("/import")
    public Wrapper<Object> importContact(@RequestParam("file") MultipartFile multipartFile) throws Exception
    {
        if(multipartFile.isEmpty())
        {
            return WrapMapper.error("请选择文件");
        }

        EasyExcel.read(multipartFile.getInputStream(), TelContact.class,
                new ContactUploadDataListener(hotlineContactService, currentTelGroupId())).sheet().doRead();

        return WrapMapper.ok();

    }





}
