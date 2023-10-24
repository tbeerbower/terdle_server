package com.techelevator.service;

import com.techelevator.dao.GameDao;
import com.techelevator.dao.UserGameDao;
import com.techelevator.model.Game;
import com.techelevator.model.UserGame;
import com.techelevator.utils.Words;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.techelevator.model.Game.Type.DAILY;
import static com.techelevator.model.Game.Type.RANDOM;

@Component
public class GameService {
    public static final int MAX_ATTEMPTS_FOR_UNUSED_WORD = 1000;

    private final GameDao gameDao;
    private final UserGameDao userGameDao;

    public GameService(GameDao gameDao, UserGameDao userGameDao) {
        this.gameDao = gameDao;
        this.userGameDao = userGameDao;
    }

    public synchronized Game createGame(Game game) {
        return gameDao.createGame(game);
    }

    public UserGame createUserGame(int userId, UserGame userGame) {
        int gameId = userGame.getGameId();

        if (gameId == 0) {
            Game game = userGame.getType() == DAILY ?
                    getDailyGame(userGame.getDate(), userGame.getWord()) : getRandomGame(userId, userGame.getWord());
            gameId = game.getGameId();
        }
        return userGameDao.createUserGame(userId, gameId, userGame);
    }

    public UserGame getUserGame(int userId, int gameId) {
        return userGameDao.getUserGameByUserIdAndGameId(userId, gameId);
    }

    public void updateUserGame(int userId, int gameId, UserGame userGame) {
        userGameDao.updateUserGame(userId, gameId, userGame, userGame.getGuesses().contains(userGame.getWord()));
    }

    private synchronized Game getDailyGame(LocalDate date, String word) {
        if (date == null) {
            date = LocalDate.now(ZoneId.of("GMT"));
        }
        List<Game> games = gameDao.getGamesByDateAndType(date, DAILY);
        if (games.isEmpty()) {
            if (word == null) {
                List<Game> userGames = gameDao.getGamesByType(DAILY);
                Set<String> usedWords = userGames.stream().map(Game::getWord).collect(Collectors.toSet());
                word = Words.getUnusedWord(usedWords, MAX_ATTEMPTS_FOR_UNUSED_WORD);
            }
            return gameDao.createGame(new Game(word, date, DAILY));
        }
        return games.get(0);
    }

    private synchronized Game getRandomGame(int userId, String word) {
        if (word == null) {
            List<UserGame> userGames = userGameDao.getUserGamesByUserId(userId, null, RANDOM);
            Set<String> usedWords = userGames.stream().map(Game::getWord).collect(Collectors.toSet());
            word = Words.getUnusedWord(usedWords, MAX_ATTEMPTS_FOR_UNUSED_WORD);
        }
        List<Game> games = gameDao.getGamesByWordAndType(word, RANDOM);
        return games.isEmpty() ? gameDao.createGame(new Game(word, LocalDate.now(ZoneId.of("GMT")), RANDOM)) : games.get(0);
    }
}
