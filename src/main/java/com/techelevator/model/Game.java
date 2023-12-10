package com.techelevator.model;

import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDate;

public class Game {

    public static final int WORD_LENGTH = 5;
    public static final int MAX_GUESSES = 6;

    public enum Type {
        DAILY,
        RANDOM;

        @JsonValue
        public String toValue() {
            return name();
        }
    }

    private int gameId;
    private String word;
    private LocalDate date;
    private Type type = Type.DAILY;

    public Game() {
    }

    public Game(int gameId, String word, LocalDate date, Type type) {
        this.gameId = gameId;
        this.word = word;
        this.date = date;
        this.type = type;
    }

    public Game(String word, LocalDate date, Type type) {
        this(0, word, date, type);
    }

    public int getGameId() {
        return gameId;
    }

    public String getWord() {
        return word;
    }

    public LocalDate getDate() {
        return date;
    }

    public Type getType() {
        return type;
    }
}
