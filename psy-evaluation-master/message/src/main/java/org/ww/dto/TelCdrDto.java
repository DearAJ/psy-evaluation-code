package org.ww.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class TelCdrDto {
    private Long id;

    private String sessionId;

    private String caller;

    private String callee;

    private String callerLocation;

    public Integer direction;

    private String time;

    private String name;

    private String role;

    private String province;
    private String city;
    private String county;

    private String questionType;

    private String questionLevel;

    private String endStatus;

    private String endStatusString;

    private Long totalCount;

    private String record;

    private String teacherName;

    private String idCard;

    private String question;
    private String answer;

    private Date startTimestamp;
    private Date endedTimestamp;

    public Long getTotalDuration()
    {
        if(startTimestamp == null || endedTimestamp == null)
        {
            return 0L;
        }
        return (endedTimestamp.getTime() - startTimestamp.getTime()) / 1000;
    }

}
