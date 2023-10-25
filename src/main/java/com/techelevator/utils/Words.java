package com.techelevator.utils;

import java.io.File;
import java.util.Iterator;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

/**
 * Utility methods related to words used in the game.
 */
public class Words {
    /** The directory that contains the words files */
    private static final String RESOURCE_DIR = Optional.ofNullable(System.getenv("RESOURCE_DIR")).orElse("src/main/resources");

    /** The set of words that can be used for a game */
    private static final Set<String> words = WordReader.getWords(new File(RESOURCE_DIR + "/words.txt"));

    /** The set of words that can be guessed for a game */
    private static final Set<String> guesses = WordReader.getWords(
            new File(RESOURCE_DIR + "/words.txt"), new File(RESOURCE_DIR + "/guesses.txt"));

    /** Random number generator */
    public static final Random RANDOM = new Random();



    /**
     * @param attempts the number of times to try to find an unused word
     * @return attempt to get a random word that doesn't occur in the given set of used words
     */
    public static String getUnusedWord(Set<String> usedWords, int attempts) {
        String word = getWord();
        while (usedWords.contains(word) && attempts-- > 0) {
            word = getWord();
        }
        return word;
    }

    /**
     * @return a word based on the given game type and date
     */
    public static String getWord() {
        return getWord(RANDOM.nextInt(words.size()));
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

    private static String getWord(int index) {
        Iterator<String> iter = words.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }
}
