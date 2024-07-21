package com.hackathonteam1.refreshrator.exception;

import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;

public class BadRequestException extends CustomException{
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }

    public BadRequestException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
