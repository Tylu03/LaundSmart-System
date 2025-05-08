package com.c1se22.publiclaundsmartsystem.exception;

import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.math.BigDecimal;

@Getter
public class InsufficientBalanceException extends BusinessException{
    public InsufficientBalanceException(BigDecimal required, BigDecimal available) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.INSUFFICIENT_BALANCE, required, available);
    }
}
