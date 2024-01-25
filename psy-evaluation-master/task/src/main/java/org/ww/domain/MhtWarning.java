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
 * @date 2022/3/10 17:32
 * @Description:
 */

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "mht_warning")
public class MhtWarning extends BaseEntity implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer taskId;

    private Integer userId;

    private Integer schoolId;

    private Integer total;

    private Integer study;

    private Integer person;

    private Integer loneliness;

    private Integer selfAccusation;

    private Integer allergy;

    private Integer health;

    private Integer terror;

    private Integer impulse;
}
