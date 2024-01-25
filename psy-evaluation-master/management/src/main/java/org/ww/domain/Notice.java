package org.ww.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 13096
 * @Date 2022/6/17 19:22
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "notice")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notice implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String author;

    private String issuedUnit;

    private String province;

    private String city;

    private String county;

    private String school;

    private String title;

    private String content;

    private String appendix;

    @TableField(fill = FieldFill.INSERT)
    private Date issuedTime;
}
