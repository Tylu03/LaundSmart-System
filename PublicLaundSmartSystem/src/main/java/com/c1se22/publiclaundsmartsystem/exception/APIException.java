package com.c1se22.publiclaundsmartsystem.exception;

import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class APIException extends BusinessException{
    public APIException(HttpStatus httpStatus, ErrorCode errorCode, Object... args) {
        super(httpStatus, errorCode, args);
    }
    public APIException(HttpStatus httpStatus, ErrorCode errorCode) {
        super(httpStatus, errorCode);
    }
}
