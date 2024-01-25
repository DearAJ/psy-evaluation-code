package org.ww.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class PrivateMessageDto {
    public Long id;
    public Integer type;
    private String content;
    private String refId;
    private Date createTime;
    private Integer status;

    private String senderUser;
    private String senderTitle;
    private String senderName;
    private String targetUser;
    private String targetTitle;


}
