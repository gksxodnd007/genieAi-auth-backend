package com.finder.genie_ai.model.user.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class UserSignInCommand {

    @ApiModelProperty(notes = "User's ID", required = true)
    @NotNull
    private String userId;
    @ApiModelProperty(notes = "User's password", required = true)
    @NotNull
    private String passwd;

}
