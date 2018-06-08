package com.finder.genie_ai.service;

import com.finder.genie_ai.dao.mysql.UserRepository;
import com.finder.genie_ai.dto.UserDto;
import com.finder.genie_ai.model.commons.ApiServiceRtn;
import com.finder.genie_ai.model.commons.BaseCode;
import com.finder.genie_ai.model.user.UserModel;
import com.finder.genie_ai.model.user.command.UserSignUpCommand;
import com.finder.genie_ai.util.TokenGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class SignUpApiServiceImpl implements SignUpApiService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public SignUpApiServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder,
                                UserRepository userRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public ApiServiceRtn<UserDto> signup(UserSignUpCommand signupPrm,
                                         ApiServiceRtn<UserDto> serviceRtn) {
        UserModel user = new UserModel();
        String salt = TokenGenerator.generateSaltValue();

        user.setUserId(signupPrm.getUserId());
        user.setPasswd(bCryptPasswordEncoder.encode(signupPrm.getPasswd() + salt));
        user.setSalt(salt);
        user.setUserName(signupPrm.getUserName());
        user.setEmail(signupPrm.getEmail());
        user.setBirth(LocalDate.parse(signupPrm.getBirth()));
        user.setIntroduce(signupPrm.getIntroduce());

        user = userRepository.save(user);
        UserDto userDto = modelMapper.map(user, UserDto.class);

        serviceRtn.setCode(BaseCode.SUCCESS.getCode());
        serviceRtn.setMessage(BaseCode.SUCCESS.getMeesage());
        serviceRtn.setResult(userDto);

        return serviceRtn;
    }

}
