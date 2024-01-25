package org.ww.base;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class BaseEntity implements Serializable {
    /**
     * 页数
     */
    @TableField(exist = false)
    private Integer pageNum = 1;

    /**
     * 页大小
     */
    @TableField(exist = false)
    private Integer pageSize = 10;

    /**
     * 关键字
     */
    @TableField(exist = false)
    private String keyword;
}
