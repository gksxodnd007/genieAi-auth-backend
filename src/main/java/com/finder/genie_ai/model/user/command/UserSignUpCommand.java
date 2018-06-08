package com.finder.genie_ai.model.user.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserSignUpCommand {

    @ApiModelProperty(notes = "User's ID", required = true)
    @NotNull
    private String userId;
    @ApiModelProperty(notes = "User's password", required = true)
    @NotNull
    private String passwd;
    @ApiModelProperty(notes = "User's name", required = true)
    @NotNull
    private String userName;
    @ApiModelProperty(notes = "User's email", required = true)
    @NotNull
    private String email;
    @ApiModelProperty(notes = "User's birth", required = true)
    @NotNull
    private String birth;
    @ApiModelProperty(notes = "User's introduce", required = true)
    @NotNull
    private String introduce;

}
