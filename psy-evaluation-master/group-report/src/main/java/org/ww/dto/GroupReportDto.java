
package org.ww.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.ww.base.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author 13096
 * @Date 2022/6/14 15:32
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class GroupReportDto extends BaseEntity implements Serializable  {

    private Long id;

    private String name;

    private Long schoolId;

    private Integer taskId;

    private String scaleId;

    private String createPerson;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date createDate;

    private String scope;
}
