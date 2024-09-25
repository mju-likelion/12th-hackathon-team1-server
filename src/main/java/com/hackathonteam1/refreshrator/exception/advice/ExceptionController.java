package com.hackathonteam1.refreshrator.exception.advice;

import com.hackathonteam1.refreshrator.dto.ErrorResponseDto;
import com.hackathonteam1.refreshrator.exception.BadRequestException;
import com.hackathonteam1.refreshrator.exception.CustomException;
import com.hackathonteam1.refreshrator.exception.DtoValidationException;
import com.hackathonteam1.refreshrator.exception.errorcode.ErrorCode;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    //지정한 커스텀 예외 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException customException){
        writeLog(customException);
        HttpStatus httpStatus = this.resolveHttpStatus(customException);
        return new ResponseEntity<>(ErrorResponseDto.res(customException), httpStatus);
    }

    // 스프링의 Validation 예외 핸들러
    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponseDto> handleCustomException(MethodArgumentNotValidException methodArgumentNotValidException){
        FieldError fieldError = methodArgumentNotValidException.getBindingResult().getFieldError();
        if(fieldError == null){
            return new ResponseEntity<>(ErrorResponseDto.res(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    methodArgumentNotValidException), HttpStatus.BAD_REQUEST);
        }
        ErrorCode validationErrorCode = ErrorCode.resolveValidationErrorCode(fieldError.getCode());
        String detail = fieldError.getDefaultMessage();
        DtoValidationException dtoValidationException = new DtoValidationException(validationErrorCode, detail);
        this.writeLog(dtoValidationException);
        return new ResponseEntity<>(ErrorResponseDto.res(dtoValidationException),HttpStatus.BAD_REQUEST);
    }

    //DB에서 데이터를 찾을 수 없는 경우 예외 핸들러
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEntityNotFoundException(EntityNotFoundException entityNotFoundException){
        writeLog(entityNotFoundException);
        return new ResponseEntity<>(ErrorResponseDto.res(String.valueOf(HttpStatus.NOT_FOUND.value()),entityNotFoundException), HttpStatus.NOT_FOUND);
    }

    //Validation 커스텀 어노테이션 예외 핸들러
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodValidationException(
            HandlerMethodValidationException exception){
        writeLog(exception);
        String errorMessage = exception.getDetailMessageArguments()[0].toString();
        CustomException customException = new BadRequestException(ErrorCode.INVALID_REQUEST_PARAMETER, errorMessage);

        return new ResponseEntity<>(ErrorResponseDto.res(customException), HttpStatus.BAD_REQUEST);
    }

    //예상 불가한 예외 처리 핸들러
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleExceptioon(Exception exception){
        this.writeLog(exception);
        return new ResponseEntity<>(
                ErrorResponseDto.res(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), exception),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private void writeLog(CustomException customException){
        String exceptionName = customException.getClass().getSimpleName();
        ErrorCode errorCode = customException.getErrorCode();
        String detail = customException.getDetail();
        log.error("[{}]{}:{}", exceptionName,errorCode.getMessage(), detail);
    }

    private void writeLog(Exception exception){
        String exceptionName = exception.getClass().getSimpleName();
        String message = exception.getMessage();
        log.error("[{}]:{}", exceptionName, message);
    }

    private HttpStatus resolveHttpStatus(CustomException customException){
        return HttpStatus.resolve(Integer.parseInt(customException.getErrorCode().getCode().substring(0,3)));
    }
}
