package com.hackathonteam1.refreshrator.annotation.resolver;

import com.hackathonteam1.refreshrator.annotation.Match;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MatchValidator implements ConstraintValidator<Match, Integer> {

    int min;
    int max;

    @Override
    public void initialize(Match constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if(integer == null){
            log.info("is null");
           return false;
        }
        return integer>=min && integer<=max;
    }
}
