package com.finder.genie_ai.model.commons;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorModel {

    private int code;
    private String message;

    public ErrorModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
