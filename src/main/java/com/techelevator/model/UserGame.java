package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonValue;
import com.techelevator.validator.ValidUserGameGuesses;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserGame extends Game {

    public enum Match {
        EXACT_MATCH,
        WRONG_LOCATION,
        NO_MATCH;

        @JsonValue
        public String toValue() {
            return name();
        }
    }

    public static class MatchPair {
        private final Match match;
        private final char c;

        public MatchPair(Match match, char c) {
            this.match = match;
            this.c = c;
        }

        public Match getMatch() {
            return match;
        }

        public char getChar() {
            return c;
        }
    }

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

    public UserGame(int userId, int gameId, List<String> guesses) {
        super(gameId, null, null, null);
        this.userId = userId;
        this.guesses.addAll(guesses);
    }

    public UserGame( UserGame userGame, List<String> guesses ){
    	this(userGame.getUserId(), userGame.getGameId(), userGame.getWord(), userGame.getDate(), userGame.getType(), userGame.isSuccess());
    	this.guesses.addAll(guesses);
        this.success = guesses.contains(getWord());
    }

    public int getUserId() {
        return userId;
    }

    public List<String> getGuesses() {
        return guesses;
    }

    public List<UserGame.MatchPair[]> getMatches() {
        List<UserGame.MatchPair[]> matchesList = new ArrayList<>();
        for (String guess : getGuesses()) {
            String word = getWord();
            UserGame.MatchPair[] matches = new UserGame.MatchPair[Game.WORD_LENGTH];
            ArrayList<Character> misses = new ArrayList<>();
            for (int i = 0; i < Game.WORD_LENGTH; i++) {
                Character ch = word.charAt(i);
                if (ch == guess.charAt(i)) {
                    matches[i] = new UserGame.MatchPair(UserGame.Match.EXACT_MATCH, ch);
                } else {
                    misses.add(ch);
                }
            }
            for (int i = 0; i < Game.WORD_LENGTH && !misses.isEmpty(); i++) {
                Character ch = guess.charAt(i);
                if (word.charAt(i) != ch) {
                    matches[i] = new UserGame.MatchPair(misses.remove(ch) ? UserGame.Match.WRONG_LOCATION : UserGame.Match.NO_MATCH, ch);
                }
            }
            matchesList.add(matches);
        }
        return matchesList;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
