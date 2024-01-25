package org.ww.EntityDO;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName("hotword")
public class HotWord extends BaseEntity implements Serializable {
    private Long id;
    private String name;
    private Date updateTime;
    private Long gapTime;
    private Long clickNums;
    private Long newClickNums;
}
