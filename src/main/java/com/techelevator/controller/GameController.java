package com.techelevator.controller;

import com.techelevator.dao.GameDao;
import com.techelevator.exception.DaoException;
import com.techelevator.model.Game;
import com.techelevator.model.User;
import com.techelevator.utils.Word;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * The GameController is a class for handling HTTP Requests related to getting Game information.
 */
@RestController
@CrossOrigin
@PreAuthorize("isAuthenticated()")
@RequestMapping( path = "/games")
public class GameController {

    private GameDao gameDao;

    public GameController(GameDao gameDao) {
        this.gameDao = gameDao;
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Game> getAllGames() {
        try {
            return gameDao.getGames();
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(path = "/{gameId}", method = RequestMethod.GET)
    public Game getGameById(@PathVariable int gameId, Principal principal) {
        Game game = null;

        try {
            game = gameDao.getGameById(gameId);
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return game;
    }

    @RequestMapping(path = "/today", method = RequestMethod.GET)
    public Game getTodaysGame(Principal principal) {
        try {
            LocalDate now = LocalDate.now(ZoneId.of("GMT"));
            List<Game> games = gameDao.getGamesByDateAndType(now, Game.Type.DAILY);
            return games.isEmpty() ? null : games.get(0);
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(path = "/{gameId}", method = RequestMethod.PUT)
    public Game updateGame(@PathVariable int gameId, @RequestBody Game modifiedGame, Principal principal) {
        try {
            return gameDao.updateGame(gameId, modifiedGame);
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public Game createGame(@RequestBody Game newGame) {
        try {
            Game.Type type = newGame.getType();
            String word = newGame.getWord() == null ? Word.getWord(type) : newGame.getWord();
            LocalDate date = newGame.getDate() == null ? LocalDate.now(ZoneId.of("GMT")) : newGame.getDate();
            return gameDao.createGame(new Game(0, word, date, type));
        }
        catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
