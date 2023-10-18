package com.techelevator.model;

import com.techelevator.utils.Word;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class UserGameGuessesValidator implements ConstraintValidator<ValidUserGameGuesses, List<String>> {
    @Override
    public boolean isValid(List<String> guesses, ConstraintValidatorContext constraintValidatorContext) {
        for (String word : guesses) {
            if (!Word.validGuess(word)) {
                return false;
            }
        }
        return true;
    }
}
