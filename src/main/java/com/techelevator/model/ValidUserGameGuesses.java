package com.techelevator.model;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {UserGameGuessesValidator.class})
@Documented
public @interface ValidUserGameGuesses {
    String message() default "Invalid user game guesses";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
