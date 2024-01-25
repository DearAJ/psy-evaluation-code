package org.ww.EntityVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.ww.base.BaseEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseLevelVO extends BaseEntity {
    String msgLevel;
    private String province;
    private String city;
    private String county;
    private String schoolName;
    Long schoolId;
}
