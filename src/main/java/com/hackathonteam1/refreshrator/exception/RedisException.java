package com.hackathonteam1.refreshrator.exception;

import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;

public class RedisException extends CustomException{
    public RedisException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}