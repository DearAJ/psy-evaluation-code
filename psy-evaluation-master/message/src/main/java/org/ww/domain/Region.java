package org.ww.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author zq
 * @since 2023-05-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = {"pid", "sname", "level",
        "citycode", "yzcode", "mername", "lng", "lat", "pinyin"})
@TableName("region")
public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 区域主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 区域名称
     */
    @JsonProperty("label")
    @TableField("name")
    private String name;

    /**
     * 区域上级标识
     */
    @TableField("pid")
    private Integer pid;

    /**
     * 地名简称
     */
    @TableField("sname")
    private String sname;

    /**
     * 区域等级
     */
    @TableField("level")
    private Integer level;

    /**
     * 区域编码
     */
    @TableField("citycode")
    private String citycode;

    /**
     * 邮政编码
     */
    @TableField("yzcode")
    private String yzcode;

    /**
     * 组合名称
     */
    @TableField("mername")
    private String mername;

    @TableField("Lng")
    private Float lng;

    @TableField("Lat")
    private Float lat;

    @TableField("pinyin")
    private String pinyin;

    @TableField(exist = false)
    private List<Region> children;


}
