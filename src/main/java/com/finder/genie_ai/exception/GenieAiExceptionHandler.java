package com.finder.genie_ai.exception;

import com.finder.genie_ai.model.commons.ErrorModel;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GenieAiExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ErrorModel handleException(BaseException e) {
        return new ErrorModel(e.getStatusCode(), e.getMessage());
    }

}
