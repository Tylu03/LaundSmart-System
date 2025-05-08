package com.c1se22.publiclaundsmartsystem.exception;

import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BusinessException extends RuntimeException{
    private final HttpStatus httpStatus;
    private final ErrorCode errorCode;
    private final Object[] args;

    public BusinessException(HttpStatus httpStatus, ErrorCode errorCode) {
        this(httpStatus, errorCode, (Object[]) null);
    }

    public BusinessException(HttpStatus httpStatus, ErrorCode errorCode, Object... args) {
        super(args != null ? errorCode.getMessage(args) : errorCode.getMessage());
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.args = args;
    }
}
