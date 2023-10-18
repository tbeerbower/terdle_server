package com.techelevator.service;

import com.techelevator.dao.GameDao;
import com.techelevator.dao.UserGameDao;
import com.techelevator.model.Game;
import com.techelevator.model.UserGame;
import com.techelevator.utils.Word;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static com.techelevator.model.Game.Type.DAILY;

@Component
public class GameService {
    private final GameDao gameDao;
    private final UserGameDao userGameDao;

    public GameService(GameDao gameDao, UserGameDao userGameDao) {
        this.gameDao = gameDao;
        this.userGameDao = userGameDao;
    }

    public UserGame createUserGame(int userId, UserGame userGame) {
           return userGameDao.createUserGame(userId, userGame.getGameId(), userGame);
    }

    public UserGame getUserGame(int userId, int gameId) {
        return userGameDao.getUserGameByUserIdAndGameId(userId, gameId);
    }

    public UserGame getDailyUserGame(int userId) {
        LocalDate now = LocalDate.now(ZoneId.of("GMT"));

        List<Game> games = gameDao.getGamesByDateAndType(now, DAILY);
        Game game = games.isEmpty() ? null : games.get(0);

        return game == null ? null : userGameDao.getUserGameByUserIdAndGameId(userId, game.getGameId());
    }

    public void updateUserGame(int userId, int gameId, UserGame userGame) {
        userGameDao.updateUserGame(userId, gameId, userGame, userGame.getGuesses().contains(userGame.getWord()));
    }
}
