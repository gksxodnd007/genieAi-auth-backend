package com.finder.genie_ai.model.user.command;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class UserDeleteCommand {

    @ApiModelProperty(notes = "User's password", required = true)
    @NotNull
    private String passwd;

}
