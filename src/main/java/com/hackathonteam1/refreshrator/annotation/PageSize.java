package com.hackathonteam1.refreshrator.annotation;

import com.hackathonteam1.refreshrator.annotation.resolver.PageSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.hackathonteam1.refreshrator.constant.ValidatorConstant.PAGE_SIZE_MAX;
import static com.hackathonteam1.refreshrator.constant.ValidatorConstant.PAGE_SIZE_MIN;

@Constraint(validatedBy = PageSizeValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PageSize {
    String message() default "페이지 크기는 {min}이상, {max}이어야 합니다.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default PAGE_SIZE_MIN;
    int max() default PAGE_SIZE_MAX;
}
