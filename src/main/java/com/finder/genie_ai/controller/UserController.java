package com.finder.genie_ai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finder.genie_ai.dao.mysql.PlayerRepository;
import com.finder.genie_ai.dao.mysql.UserRepository;
import com.finder.genie_ai.dto.UserDto;
import com.finder.genie_ai.exception.*;
import com.finder.genie_ai.model.game.player.PlayerModel;
import com.finder.genie_ai.model.session.SessionModel;
import com.finder.genie_ai.model.user.UserModel;
import com.finder.genie_ai.model.user.command.UserChangeInfoCommand;
import com.finder.genie_ai.model.user.command.UserDeleteCommand;
import com.finder.genie_ai.model.user.command.UserSignInCommand;
import com.finder.genie_ai.model.user.command.UserSignUpCommand;
import com.finder.genie_ai.dao.redis.SessionTokenRedisRepository;
import com.finder.genie_ai.util.TokenGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.swagger.annotations.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping(value = "/genie")
@Api(value = "genieAi user", description = "Operations pertaining to user rest api")
public class UserController {

    private UserRepository userRepository;
    private PlayerRepository playerRepository;
    private ObjectMapper mapper;
    private SessionTokenRedisRepository sessionTokenRedisRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public UserController(UserRepository userRepository,
                          PlayerRepository playerRepository,
                          SessionTokenRedisRepository sessionTokenRedisRepository,
                          ObjectMapper mapper,
                          BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.sessionTokenRedisRepository = sessionTokenRedisRepository;
        this.mapper = mapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @ApiOperation(value = "Signin user", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully signin",
                    responseHeaders = @ResponseHeader(name = "session-token", response = String.class)),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 404, message = "Please check your ID or Password"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/signin", method = RequestMethod.POST)
    public void signinUser(@RequestBody @Valid UserSignInCommand command,
                           BindingResult bindingResult,
                           HttpServletRequest request,
                           HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid parameter form");
        }

        UserModel user = userRepository
                            .findByUserId(command.getUserId())
                            .orElseThrow(() -> new NotFoundException("Please check your ID or Password"));
        if (bCryptPasswordEncoder.matches(command.getPasswd() + user.getSalt(), user.getPasswd())) {
            response.setStatus(204);

            String newToken = TokenGenerator.generateSessionToken(user.getUserId());
            SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.now(), LocalDateTime.now());
            String data = mapper.writeValueAsString(sessionModel);
            String oldToken = sessionTokenRedisRepository.findSessionUserId(command.getUserId());
            if (oldToken != null) {
                sessionTokenRedisRepository.updateSessionTokenOnDupLogin(newToken, oldToken, command.getUserId(), data);
            }
            else {
                sessionTokenRedisRepository.saveSessionToken(newToken, command.getUserId(), data);
            }

            response.setHeader("session-token", newToken);
        }
        else {
            throw new NotFoundException("Please check your ID or Password");
        }
    }

    @ApiOperation(value = "Signout user", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully signout",
                    responseHeaders = @ResponseHeader(name = "expired-token", response = Boolean.class)),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/signout", method = RequestMethod.DELETE)
    public void signoutUser(@RequestHeader(name = "session-token") String token,
                            @RequestHeader(name = "userId") String userId,
                            HttpServletResponse response) {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }

        if (sessionTokenRedisRepository.expireSession(token, userId)) {
            response.setHeader("expired-token", Boolean.TRUE.toString());
        }
        else {
            response.setHeader("expired-token", Boolean.FALSE.toString());
        }
        response.setStatus(204);
    }

    @ApiOperation(value = "Check Duplicated user ID when signin", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully "),
            @ApiResponse(code = 409, message = "Duplicated user ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/user/checkDup/{userId}", method = RequestMethod.GET)
    public void checkDup(@PathVariable("userId") String userId, HttpServletResponse response) {

        if (userRepository.findByUserId(userId).isPresent()) {
            throw new DuplicateException("Duplicated user ID");
        }

        response.setStatus(204);
    }

    @ApiOperation(value = "Get user information", response = UserDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully get user info"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 404, message = "Please check user ID"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    UserDto getUserInfo(@PathVariable("userId") String userId,
                        @RequestHeader(name = "session-token") String token,
                        HttpServletRequest request) throws JsonProcessingException {
        if (!sessionTokenRedisRepository.isSessionValid(token)) {
            throw new UnauthorizedException();
        }

        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        UserModel user =  userRepository
                .findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Please check user ID"));

        UserDto userDto = modelMapper.map(user, UserDto.class);

        Optional<PlayerModel> playerModel = playerRepository.findByUserId(user);
        if (playerModel.isPresent()) {
            userDto.setNickname(playerModel.get().getNickname());
        }
        else {
            userDto.setNickname("0");
        }

        return userDto;
    }

    @ApiOperation(value = "Modify user information", response = UserDto.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully modify user information"),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 404, message = "Please check your ID or Password"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.PUT, produces = "application/json")
    public @ResponseBody
    UserDto updateUserInfo(@PathVariable("userId") String userId,
                           @RequestBody @Valid UserChangeInfoCommand command,
                           BindingResult bindingResult,
                           @RequestHeader(name = "session-token") String token,
                           HttpServletRequest request) throws JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid parameter form");
        }

        UserModel user = userRepository
                .findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Please check your ID or Password"));

        if (!sessionTokenRedisRepository.isSessionValid(token)
                || !bCryptPasswordEncoder.matches(command.getPasswd() + user.getSalt(), user.getPasswd())) {
            throw new UnauthorizedException();
        }

        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        int resCount = userRepository.updateUserInfo(
                command.getUserName(),
                command.getEmail(),
                LocalDate.parse(command.getBirth()),
                command.getIntroduce(),
                userId);

        if (resCount == 0) {
            throw new ServerException("doesn't execute query");
        }

        UserDto userDto = new UserDto();
        userDto.setUserName(command.getUserName());
        userDto.setEmail(command.getEmail());
        userDto.setBirth(LocalDate.parse(command.getBirth()));
        userDto.setIntroduce(command.getIntroduce());

        return userDto;
    }

    @ApiOperation(value = "Delete user information", response = Void.class)
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully delete user information"),
            @ApiResponse(code = 400, message = "Invalid parameter form"),
            @ApiResponse(code = 401, message = "Invalid or expired session-token"),
            @ApiResponse(code = 404, message = "Please check your ID or Password"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @RequestMapping(value = "/user/{userId}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable("userId") String userId,
                           @RequestHeader(name = "session-token") String token,
                           @RequestBody @Valid UserDeleteCommand command,
                           BindingResult bindingResult,
                           HttpServletRequest request) throws JsonProcessingException {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException("Invalid parameter form");
        }
        UserModel user = userRepository
                .findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Please check your ID or Password"));

        if (!sessionTokenRedisRepository.isSessionValid(token)
                || !bCryptPasswordEncoder.matches(command.getPasswd() + user.getSalt(), user.getPasswd())) {
            throw new UnauthorizedException();
        }
        JsonElement element = new JsonParser().parse(sessionTokenRedisRepository.findSessionToken(token));
        SessionModel sessionModel = new SessionModel(request.getRemoteAddr(), LocalDateTime.parse(element.getAsJsonObject().get("signin_at").getAsString()), LocalDateTime.now());
        sessionTokenRedisRepository.updateSessionToken(token, mapper.writeValueAsString(sessionModel));

        userRepository.deleteByUserId(userId);
        sessionTokenRedisRepository.expireSession(token, userId);
    }

}

