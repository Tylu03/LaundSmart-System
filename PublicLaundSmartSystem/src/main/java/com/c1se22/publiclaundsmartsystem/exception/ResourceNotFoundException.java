package com.c1se22.publiclaundsmartsystem.exception;

import com.c1se22.publiclaundsmartsystem.enums.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
public class ResourceNotFoundException extends BusinessException{
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue){
        super(HttpStatus.NOT_FOUND, ErrorCode.RESOURCE_NOT_FOUND, resourceName, fieldName, fieldValue);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
