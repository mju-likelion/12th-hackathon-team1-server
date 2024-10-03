package com.hackathonteam1.refreshrator.annotation;


import com.hackathonteam1.refreshrator.annotation.resolver.MatchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.hackathonteam1.refreshrator.constant.ValidatorConstant.MATCH_MAX;
import static com.hackathonteam1.refreshrator.constant.ValidatorConstant.MATCH_MIN;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = MatchValidator.class)
public @interface Match {

    String message() default "레시피 추천 시 재료 일치 수는 {min}이상, {max}이하여야 합니다.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int min() default MATCH_MIN;
    int max() default MATCH_MAX;
}
