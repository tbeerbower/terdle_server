package com.techelevator.utils;

import com.techelevator.model.Game;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Word {

    private static final String RESOURCE_DIR = Optional.of(System.getenv("RESOURCE_DIR")).orElse("src/main/resources");
    private static final Map<Integer, String> words = WordReader.getWords(new File(RESOURCE_DIR + "/words.txt"));
    private static final Set<String> guesses = WordReader.getGuesses(
            new File(RESOURCE_DIR + "/words.txt"), new File(RESOURCE_DIR + "/guesses.txt"));
    public static final Random RANDOM = new Random();

    public static String getDailyWord() {
        LocalDate now = LocalDate.now(ZoneId.of("GMT"));
        return words.get(Long.valueOf(now.toEpochDay() % words.size()).intValue());
    }

    public static String getWord(Game.Type gameType) {
        return words.get(RANDOM.nextInt(words.size()));
    }

    public static boolean validGuess(String guess) {
        return guess.length() == 5 && guesses.contains(guess);
    }
}
