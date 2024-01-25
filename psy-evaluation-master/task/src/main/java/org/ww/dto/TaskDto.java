package org.ww.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author 13096
 * @Date 2022/2/26 13:59
 * @Version 1.0
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto implements Serializable {
    private Long id;

    private String name;

    private String content;
    private String semester;

    private String scaleId;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate;

    private Long issuedId;

    private String schoolId;

    private String period;

    private String grade;

    private String classes;
}
