package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotWithSpaceValidator.class)

public @interface NotWithSpace {
    String message() default "Строка не может быть пустой и не может содержать пробелы";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}

