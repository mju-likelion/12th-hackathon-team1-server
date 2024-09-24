package com.hackathonteam1.refreshrator.annotation.resolver;


import com.hackathonteam1.refreshrator.annotation.PageNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PageNumberValidator implements ConstraintValidator<PageNumber, Integer> {

    int min;

    @Override
    public void initialize(PageNumber constraintAnnotation) {
        this.min = constraintAnnotation.min();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if(integer == null){
            return false;
        }
        return integer >= min;
    }
}
