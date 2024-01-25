package org.ww.domain;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadStyle;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.ww.dto.TelCdrDto;

/**
 * <p>
 * 
 * </p>
 *
 * @author zq
 * @since 2023-05-07
 */
@Data
@ExcelIgnoreUnannotated
@EqualsAndHashCode(callSuper = false)
@TableName("tel_contact")
public class TelContact implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @JsonIgnore
    @TableField("tel_group_id")
    private Long telGroupId;

    /**
     * 电话号码
     */
    @ExcelProperty(value = "手机号", index = 2)
    @TableField("phone")
    private String phone;

    /**
     * 身份
     */
    @ExcelProperty(value = "身份", index = 0)
    @TableField("role")
    private String role;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 1)
    @TableField("name")
    private String name;

    /**
     * 身份证
     */
    @ExcelProperty(value = "身份证号", index = 3)
    @ColumnWidth(30)
    @TableField("id_card")
    private String idCard;

    /**
     * 年龄
     */
    @ExcelProperty("年龄")
    @TableField("age")
    private Integer age;

    /**
     * 性别
     */
    @ExcelProperty("性别")
    @TableField("gender")
    private String gender;

    /**
     * 学校名
     */
    @ExcelProperty("学校")
    @TableField("school_name")
    private String schoolName;

    /**
     * 学段
     */
    @ExcelProperty("学段")
    @TableField("study_period")
    private String studyPeriod;

    /**
     * 年级
     */
    @ExcelProperty("年级")
    @TableField("school_grade")
    private String schoolGrade;

    /**
     * 班级
     */
    @ExcelProperty("班级")
    @TableField("school_class")
    private String schoolClass;

    /**
     * 学历
     */
    @ExcelProperty("学历")
    @TableField("education")
    private String education;


    @ExcelProperty("省份")
    @TableField("province")
    private String province;

    @ExcelProperty("城市")
    @TableField("city")
    private String city;

    @ExcelProperty("区县")
    @TableField("county")
    private String county;

    @ExcelProperty("详细地址")
    @TableField("address")
    @ColumnWidth(30)
    private String address;

    /**
     * 是否独生子女
     */
    @ExcelProperty("是否独生子女")
    @TableField("single_child")
    private String singleChild;


//    @TableField(exist = false)
//    private String singleChildString;

    /**
     * 家中排行
     */
    @ExcelProperty("排行")
    @TableField("birth_order")
    private String birthOrder;

    /**
     * 父母婚姻状况
     */
    @ExcelProperty("父母婚姻状况")
    @TableField("parental_marriage_state")
    private String parentalMarriageState;

    @TableField("last_question_type")
    private String lastQuestionType;

    @ExcelProperty("生命危机等级")
    @TableField("last_question_level")
    private String lastQuestionLevel;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @JsonIgnore
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    @TableField(exist = false)
    private Long callInCount;


}
