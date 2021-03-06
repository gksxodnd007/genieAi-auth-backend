package com.finder.genie_ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends BaseException {

    public static final String MESSAGE = "Invalid or expired session-token";

    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED.value(), MESSAGE);
    }

}
