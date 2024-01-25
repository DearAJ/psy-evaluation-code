package org.ww.EntityVO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ww.base.BaseEntity;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileIdAndStuIdVO extends BaseEntity implements Serializable {
    Long fileId;
    Long stuId;
    Long userId;
    Long planId;
    Long recordId;
}
