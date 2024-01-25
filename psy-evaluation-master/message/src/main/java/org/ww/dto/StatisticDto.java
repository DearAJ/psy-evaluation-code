package org.ww.dto;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ExcelIgnoreUnannotated
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatisticDto {

    @ExcelProperty(value = "日期", index = 0)
    private String label;

    private String label2;

    @ExcelProperty(value = "接通数量", index = 2)
    private Integer successCount;

    private Integer failedCount;

    @ExcelProperty(value = "来电数量", index = 1)
    private Integer totalCount;

    @ExcelProperty(value = "总通话时长（秒）")
    private Long successDuration;
    private Long failedDuration;
    private Long totalDuration;


    @ExcelProperty(value = "平均通话时长（秒）")
    private Long avgSuccessDuration;
    public Long getAvgSuccessDuration()
    {
        if(successCount == null || successCount == 0 || successDuration == null || successDuration == 0)
        {
            return 0L;
        }

        return successDuration / successCount;
    }

    private List<StatisticDto> roles;

}
