package com.finder.genie_ai.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@EqualsAndHashCode
public class UserDto {

    @ApiModelProperty(notes = "User's ID", required = true)
    private String userId;

    @ApiModelProperty(notes = "User's name", required = true)
    private String userName;

    @ApiModelProperty(notes = "User's playing nickname")
    private String nickname;

    @ApiModelProperty(notes = "User's email", required = true)
    private String email;

    @ApiModelProperty(notes = "User's birth", required = true)
    private LocalDate birth;

    @ApiModelProperty(notes = "User's introduce", required = true)
    private String introduce;

}
