package com.hackathonteam1.refreshrator.annotation;

import com.hackathonteam1.refreshrator.annotation.resolver.TypeStrategyValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashSet;
import java.util.Set;

@Constraint(validatedBy = TypeStrategyValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface TypeStrategy {
    String message() default "정렬 전략이 유효하지 않습니다.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String[] strategies() default {"newest", "popularity"};
}
