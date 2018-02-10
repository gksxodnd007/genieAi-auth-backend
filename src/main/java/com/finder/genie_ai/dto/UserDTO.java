package com.finder.genie_ai.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDTO {

    @ApiModelProperty(notes = "User's ID", required = true)
    @SerializedName("userId")
    private String userId;

    @ApiModelProperty(notes = "User's name", required = true)
    @SerializedName("userName")
    private String userName;

    @ApiModelProperty(notes = "User's playing nickname")
    @SerializedName("nickname")
    private String nickname;

    @ApiModelProperty(notes = "User's email", required = true)
    @SerializedName("email")
    private String email;

    @ApiModelProperty(notes = "User's birth", required = true)
    @SerializedName("birth")
    private LocalDate birth;

    @ApiModelProperty(notes = "User's introduce", required = true)
    @SerializedName("introduce")
    private String introduce;

}
