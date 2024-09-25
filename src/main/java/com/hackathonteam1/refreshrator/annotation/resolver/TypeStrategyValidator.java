package com.hackathonteam1.refreshrator.annotation.resolver;

import com.hackathonteam1.refreshrator.annotation.TypeStrategy;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class TypeStrategyValidator implements ConstraintValidator<TypeStrategy, String> {

    Set<String> strategies;

    @Override
    public void initialize(TypeStrategy constraintAnnotation) {
        this.strategies = Arrays.stream(constraintAnnotation.strategies()).collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {

        if(string==null || string.isEmpty()) return false;

        return strategies.stream().anyMatch(strategy -> strategy.equals(string));
    }
}
