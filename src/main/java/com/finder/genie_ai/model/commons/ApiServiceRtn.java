package com.finder.genie_ai.model.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiServiceRtn<T> {

    private int code;
    private String message;
    private T result;

    private ApiServiceRtn() {

    }

    public static <T> ApiServiceRtn<T> create() {
        return new ApiServiceRtn<>();
    }

}
