package com.hackathonteam1.refreshrator.annotation.resolver;

import com.hackathonteam1.refreshrator.annotation.PageSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;

public class PageSizeValidator implements ConstraintValidator<PageSize, Integer> {

    int min;
    int max;

    @Override
    public void initialize(PageSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if(value==null){
            return false;
        }
        return value >= min && value <= max;
    }

}
