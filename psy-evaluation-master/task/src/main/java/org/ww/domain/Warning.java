package org.ww.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;

/**
 * @author YWSnow
 * @date 2022/5/31 22:54
 * @Description:
 */

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "warning")
public class Warning extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer taskId;

    private Integer userId;

    private Integer schoolId;

    private String warning;
}
