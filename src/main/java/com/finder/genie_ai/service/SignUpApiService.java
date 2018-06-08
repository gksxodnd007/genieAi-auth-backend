package com.finder.genie_ai.service;

import com.finder.genie_ai.dto.UserDto;
import com.finder.genie_ai.model.commons.ApiServiceRtn;
import com.finder.genie_ai.model.user.command.UserSignUpCommand;

public interface SignUpApiService {

    ApiServiceRtn<UserDto> signup(UserSignUpCommand signupPrm,
                                  ApiServiceRtn<UserDto> serviceRtn);

}
