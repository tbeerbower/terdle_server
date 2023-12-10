package com.techelevator.validator;

import com.techelevator.dao.UserGameDao;
import com.techelevator.model.Game;
import com.techelevator.model.UserGame;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserGameValidator implements Validator {

    private final UserGameDao userGameDao;

    public UserGameValidator(UserGameDao userGameDao) {
        this.userGameDao = userGameDao;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UserGame.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserGame modifiedUserGame = (UserGame) target;
        UserGame userGame = userGameDao.getUserGameByUserIdAndGameId(modifiedUserGame.getUserId(), modifiedUserGame.getGameId());

        if (userGame.isSuccess() || userGame.getGuesses().size() >= Game.MAX_GUESSES) {
            errors.rejectValue("guesses", "guesses.exhausted",
                    "Can not add more guesses to a game that is already solved or exhausted.");
        }

        for ( int i = 0; i < userGame.getGuesses().size(); i++ ) {
            String existingGuess = userGame.getGuesses().get(i);
            String existingGuessOfModifiedUserGame = modifiedUserGame.getGuesses().get(i);
            if ( !existingGuess.equals(existingGuessOfModifiedUserGame) ) {
        		errors.rejectValue("guesses", "existing.guesses",
                        String.format("Can not change existing guess %d from '%s' to '%s'.",
                                i, existingGuess, existingGuessOfModifiedUserGame));
        	}
        }
    }
}
