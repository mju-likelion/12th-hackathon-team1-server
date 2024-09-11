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
    NOT_NULL("4005", "필수값이 공백입니다."),
    DUPLICATED_RECIPE_INGREDIENT("4006","중복되는 레시피 재료 관련 요청은 불가합니다."),
    FILE_TYPE_ERROR("4007", "유효하지 않은 파일 형식입니다."),
    NOT_IMAGE_OF_RECIPE("4008", "해당 레시피의 이미지 요청이 아닙니다."),
    SORT_TYPE_ERROR("4009", "정렬 타입이 유효하지 않습니다."),

    //AuthorizedException
    COOKIE_NOT_FOUND("4010", "쿠키를 찾을 수 없습니다."),
    INVALID_TOKEN("4011", "유효하지 않은 토큰입니다."),
    INVALID_PASSWORD("4012","검증되지 않은 비밀번호입니다."),

    //ForbiddenException
    RECIPE_FORBIDDEN("4030","해당 레시피에 대한 권한이 없습니다."),
    FRIDGE_ITEM_FORBIDDEN("4031","해당 재료정보에 대한 권한이 없습니다."),

    //NotFoundException
    USERID_NOT_FOUND("4040","존재하지 않는 사용자 입니다"),
    INGREDIENT_NOT_FOUND("4041", "재료를 찾을 수 없습니다."),
    RECIPE_NOT_FOUND("4042", "레시피를 찾을 수 없습니다."),
    INGREDIENT_RECIPE_NOT_FOUND("4043", "레시피의 재료를 찾을 수 없습니다."),
    FRIDGE_NOT_FOUND("4044","냉장고를 찾을 수 없습니다"),
    FRIDGE_ITEM_NOT_FOUND("4045","냉장고에 등록된 재료 정보를 찾을 수 없습니다."),
    PAGE_NOT_FOUND("4046", "페이지를 찾을 수 없습니다"),
    IMAGE_NOT_FOUND("4047","이미지를 찾을 수 없습니다"),
    RECIPE_LIKE_NOT_FOUND("4048", "좋아요를 누른 레시피가 아닙니다."),
    REFRESH_TOKEN_NOT_FOUND("4049", "RefreshToken을 찾을 수 없습니다"),

    //ConflictException
    DUPLICATED_EMAIL("4090", "이미 사용 중인 이메일입니다."),
    RECIPE_INGREDIENT_CONFLICT("4091", "이미 해당 레시피에 존재하는 재료입니다."),
    USER_ALREADY_ADD_LIKE("4092", "해당 레시피는 이미 좋아요를 누른 레시피입니다."),
    RECIPE_IMAGE_CONFLICT("4093", "레시피에 이미지가 이미 존재합니다."),

    //InternetException
    FILE_STORAGE_ERROR("5000", "파일을 업로드할 수 없습니다."),
    REDIS_ERROR("5001", "Redis에서 오류가 발생했습니다.");

    private final String code;
    private final String message;

    public static ErrorCode resolveValidationErrorCode(String code){
        return switch (code){
            case "Size" -> SIZE;
            case "Pattern" -> PATTERN;
            case "NotBlank" -> NOT_BLANK;
            case "Length" -> LENGTH;
            case "Email" -> EMAIL;
            case "NotNull" -> NOT_NULL;
            default -> throw new IllegalArgumentException("Unexpected value: "+ code);
        };
    }
}
