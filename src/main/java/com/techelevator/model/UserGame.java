package com.techelevator.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserGame extends Game {

    private int userId;
    @ValidUserGameGuesses
    private final List<String> guesses = new ArrayList<>();
    private boolean success;

    public UserGame() {
    }

    public UserGame(int userId, int gameId, String word, LocalDate date, Type type, boolean success) {
        super(gameId, word, date, type);
        this.userId = userId;
        this.success = success;
    }

    public UserGame(int userId, Game game) {
       this(userId, game.getGameId(), game.getWord(), game.getDate(), game.getType(), false);
    }

    public int getUserId() {
        return userId;
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
