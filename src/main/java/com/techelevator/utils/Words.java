package com.techelevator.utils;

import com.techelevator.model.Game;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Utility methods related to words used in the game.
 */
public class Words {
    /** The directory that contains the words files */
    private static final String RESOURCE_DIR = Optional.of(System.getenv("RESOURCE_DIR")).orElse("src/main/resources");

    /** The set of words that can be used for a game */
    private static final Set<String> words = WordReader.getWords(new File(RESOURCE_DIR + "/words.txt"));

    /** The set of words that can be guessed for a game */
    private static final Set<String> guesses = WordReader.getWords(
            new File(RESOURCE_DIR + "/words.txt"), new File(RESOURCE_DIR + "/guesses.txt"));

    /** Random number generator */
    public static final Random RANDOM = new Random();

    /**
     * @return a word based on the given game type and date
     */
    public static String getWord(Game.Type type, LocalDate date) {
        return type == Game.Type.DAILY ? getDailyWord(date) : getWord(RANDOM.nextInt(words.size()));
    }

    /**
     * @return true if the given word is a valid game word
     */
    public static boolean validWord(String word) {
        return validWord(word, words);
    }

    /**
     * @return true if the given word is a valid game guess
     */
    public static boolean validGuess(String guess) {
        return validWord(guess, guesses);
    }

    private static boolean validWord(String word, Set<String> set) {
        return word != null && word.length() == 5 && set.contains(word);
    }

    private static String getDailyWord(LocalDate date) {
        long epoch = date == null ? LocalDate.now(ZoneId.of("GMT")).toEpochDay() : date.toEpochDay();
        return getWord(Long.valueOf(epoch % words.size()).intValue());
    }
    private static String getWord(int index) {
        Iterator<String> iter = words.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }
}
