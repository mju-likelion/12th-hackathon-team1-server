package com.hackathonteam1.refreshrator.exception;

import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;

public class DtoValidationException extends CustomException{
    public DtoValidationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DtoValidationException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
