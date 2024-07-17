package com.hackathonteam1.refreshrator.exception;

import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;

public class ConflictException extends CustomException{
    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ConflictException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
