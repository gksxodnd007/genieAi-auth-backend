package com.finder.genie_ai.exception;

import com.finder.genie_ai.model.commons.BaseCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class BadRequestException extends BaseException {

    public BadRequestException(String message) {
        super(BaseCode.BAD_REQUEST.getCode(), message);
    }

}
