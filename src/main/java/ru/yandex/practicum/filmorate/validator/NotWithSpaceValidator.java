package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NotWithSpaceValidator implements ConstraintValidator<NotWithSpace, String> {

    @Override
    public void initialize(NotWithSpace constraintAnnotation) {
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext context) {
        return login != null && !(login.isEmpty() || login.contains(" "));
    }
}


