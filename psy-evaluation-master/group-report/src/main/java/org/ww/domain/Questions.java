package org.ww.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;

/**
 * @author YWSnow
 * @date 2022/3/4 16:31
 * @Description:
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "questions")
public class Questions extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer scaleId;

    private String main;

    private Integer type;

    private Integer moduleId;

    private Integer required;

    private String min;

    private String max;

    private Integer gear;

    @TableLogic
    private Integer deleted;
}
