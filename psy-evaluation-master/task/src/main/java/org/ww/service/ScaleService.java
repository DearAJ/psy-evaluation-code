package org.ww.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.multipart.MultipartFile;
import org.ww.domain.Scale;
import org.ww.result.Wrapper;

import java.util.List;
import java.util.Map;

/**
 * @author YWSnow
 * @date 2022/3/2 17:15
 * @Description: 
 */

public interface ScaleService {
    /**
     * 添加自定义量表
     * @param jsonObject jsonObject
     * @return 返回是否成功
     */
    Wrapper<String> addScale(JSONObject jsonObject);

    /**
     * 量表列表
     * @param jsonObject jsonObject
     * @return 分页返回量表列表
     */
    Wrapper<IPage<Scale>> scaleList(JSONObject jsonObject);

    /**
     * 更新量表
     * @param jsonObject 最新自定义量表数据
     * @return 返回是否成功
     */
    Wrapper<Object> updateScale(JSONObject jsonObject);

    /**
     * 删除量表 支持批量
     * @param jsonObject jsonObject
     * @return 返回删除结果
     */
    Wrapper<Object> deleteScale(JSONObject jsonObject);

    /**
     * 获取量表
     * @param jsonObject jsonObject
     * @return 分页返回量表题目
     */
    Wrapper<List<Map<String, Object>>> getScale(JSONObject jsonObject);


    /**
     * 验证量表数据表格
     * @param scaleDataExcel 量表数据表格
     * @param task_id 任务id
     * @param scale_id 量表id
     * @return 返回验证结果
     */
    Wrapper<Object> verifyScaleData(MultipartFile scaleDataExcel, Integer task_id, Integer scale_id);

    /**
     * 批量添加量表数据
     * @param jsonObject jsonObject
     * @return 返回是否添加成功
     */
    Wrapper<String> batchAddScaleData(JSONObject jsonObject);
}
