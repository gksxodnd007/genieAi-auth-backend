package com.finder.genie_ai.controller;

import com.finder.genie_ai.dao.mysql.UserRepository;
import com.finder.genie_ai.dto.UserDto;
import com.finder.genie_ai.exception.DuplicateException;
import com.finder.genie_ai.model.commons.ApiServiceRtn;
import com.finder.genie_ai.model.commons.BaseCode;
import com.finder.genie_ai.model.user.UserModel;
import com.finder.genie_ai.model.user.command.UserSignUpCommand;
import com.finder.genie_ai.service.SignUpApiService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/genie")
public class UserSignUp {

    private final UserRepository userRepository;
    private final SignUpApiService signUpApiService;

    @Autowired
    public  UserSignUp(UserRepository userRepository,
                       @Qualifier("signUpApiServiceImpl") SignUpApiService signUpApiService) {
        this.userRepository = userRepository;
        this.signUpApiService = signUpApiService;
    }

    @ApiOperation(value = "Signup user", response = ApiServiceRtn.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully sign up"),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 409, message = "Duplicated user ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @PostMapping(value = "/signup", consumes = "application/json")
    public ApiServiceRtn<UserDto> signup(@RequestBody @Valid UserSignUpCommand signupPrm,
                                         BindingResult bindingResult) {
        ApiServiceRtn<UserDto> apiServiceRtn = ApiServiceRtn.create();

        if (bindingResult.hasErrors()) {
            System.out.println(signupPrm.toString());
            apiServiceRtn.setCode(BaseCode.BAD_REQUEST.getCode());
            apiServiceRtn.setMessage(BaseCode.BAD_REQUEST.getMeesage());
            return apiServiceRtn;
        }

        List<UserModel> existUsers = userRepository
                .findByUserIdOrEmail(signupPrm.getUserId(), signupPrm.getEmail());

        if (!existUsers.isEmpty()) {
            for (UserModel user : existUsers) {
                if (user.getUserId().equals(signupPrm.getUserId())) {
                    throw new DuplicateException(BaseCode.DUPLICATED.getMeesage() + " -> 아이디 중복");
                }

                if (user.getEmail().equals(signupPrm.getEmail())) {
                    throw new DuplicateException(BaseCode.DUPLICATED.getMeesage() + " -> 이메일 중복");
                }
            }
        }

        return signUpApiService.signup(signupPrm, apiServiceRtn);
    }

}
