package com.hackathonteam1.refreshrator.annotation;


import com.hackathonteam1.refreshrator.annotation.resolver.PageNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.hackathonteam1.refreshrator.constant.ValidatorConstant.PAGE_NUMBER_MIN;

@Constraint(validatedBy = PageNumberValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface PageNumber {
    String message() default "페이지 번호는 0이상 이어야 합니다.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default PAGE_NUMBER_MIN;
}
