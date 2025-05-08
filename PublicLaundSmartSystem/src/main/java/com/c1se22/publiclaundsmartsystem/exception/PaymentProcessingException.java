package com.c1se22.publiclaundsmartsystem.exception;

import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class PaymentProcessingException extends BusinessException{
    public PaymentProcessingException(String message) {
        super(HttpStatus.BAD_REQUEST, ErrorCode.PAYMENT_PROCESSING_ERROR, message);
    }
}
