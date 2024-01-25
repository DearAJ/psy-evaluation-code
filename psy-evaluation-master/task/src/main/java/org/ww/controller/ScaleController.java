package org.ww.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.ww.domain.Scale;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.ScaleService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author YWSnow
 * @date 2022/3/2 16:51
 * @Description: 
 */

@Slf4j
@RestController
@RequestMapping("/scale")


public class ScaleController {

    private final ScaleService scaleService;

    public ScaleController(ScaleService scaleService) {
        this.scaleService = scaleService;
    }


    @PostMapping("addScale")
    public Wrapper<String> addScale(@RequestBody JSONObject jsonObject) { return scaleService.addScale(jsonObject); }

    @ResponseBody
    @PostMapping("scaleList")
    public Wrapper<IPage<Scale>> scaleList(@RequestBody JSONObject jsonObject) { return scaleService.scaleList(jsonObject); }

    @ResponseBody
    @PostMapping("updateScale")
    public Wrapper<Object> updateScale(@RequestBody JSONObject jsonObject) { return scaleService.updateScale(jsonObject); }

    @ResponseBody
    @PostMapping("deleteScale")
    public Wrapper<Object> deleteScale(@RequestBody JSONObject jsonObject) { return scaleService.deleteScale(jsonObject); }

    @ResponseBody
    @PostMapping("getScale")
    public Wrapper<List<Map<String, Object>>> getScale(@RequestBody JSONObject jsonObject) { return scaleService.getScale(jsonObject); }

    @PostMapping("verifyScaleData")
    public Wrapper<Object> verifyScaleData(HttpServletRequest request) {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile scaleDataExcel = multipartHttpServletRequest.getFile("file");
        Integer task_id = Integer.valueOf(request.getParameter("taskId"));
        Integer scale_id = Integer.valueOf(request.getParameter("scaleId"));
        try {
            if (scaleDataExcel != null && scaleDataExcel.getSize() > 0) {
                return scaleService.verifyScaleData(scaleDataExcel, task_id, scale_id);
            }else {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, "文件为空或文件异常，请选择文件");
            }
        } catch (Exception e) {
            log.error("ERROR ====> {}", e);
            return WrapMapper.error();
        }
    }

    @ResponseBody
    @PostMapping("batchAddScaleData")
    public Wrapper<String> batchAddScaleData(@RequestBody JSONObject jsonObject) {
        return scaleService.batchAddScaleData(jsonObject);
    }
}
