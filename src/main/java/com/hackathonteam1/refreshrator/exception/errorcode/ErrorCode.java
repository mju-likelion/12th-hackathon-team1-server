package com.hackathonteam1.refreshrator.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //BadRequestException
    SIZE("4000", "길이가 유효하지 않습니다."),
    PATTERN("4001","형식에 맞지 않습니다."),
    NOT_BLANK("4002", "필수값이 공백입니다."),
    LENGTH("4003", "길이가 유효하지 않습니다."),
    EMAIL("4004", "이메일 형식이 유효하지 않습니다."),

    //AuthorizedException
    COOKIE_NOT_FOUND("4010", "쿠키를 찾을 수 없습니다."),
    INVALID_TOKEN("4011", "유효하지 않은 토큰입니다."),
    INVALID_PASSWORD("4012","검증되지 않은 비밀번호입니다."),

    //ForbiddenException

    //NotFoundException
    USERID_NOT_FOUND("4040","존재하지 않는 사용자 입니다"),
    INGREDIENT_NOT_FOUND("4041", "재료를 찾을 수 없습니다."),
    RECIPE_NOT_FOUND("4042", "레시피를 찾을 수 없습니다."),
    INGREDIENT_RECIPE_NOT_FOUND("4043", "레시피의 재료를 찾을 수 없습니다."),
    FRIDGE_NOT_FOUND("4044","냉장고를 찾을 수 없습니다"),

    //ConflictException
    DUPLICATED_EMAIL("4090", "이미 사용 중인 이메일입니다.");


    private final String code;
    private final String message;

    public static ErrorCode resolveValidationErrorCode(String code){
        return switch (code){
            case "Size" -> SIZE;
            case "Pattern" -> PATTERN;
            case "NotBlank" -> NOT_BLANK;
            case "Length" -> LENGTH;
            case "Email" -> EMAIL;
            default -> throw new IllegalArgumentException("Unexpected value: "+ code);
        };
    }
}
