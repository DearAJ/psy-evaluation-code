package org.ww.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class UserDto implements Serializable {

    private Long id;

    private String username;

    private String password;

    private Integer enabled;

    private String clientId;

    private List<Integer> roles;

}