package com.techelevator.utils;

import java.io.*;
import java.util.*;

/**
 * Utility class to read in a set of words.
 */
public class WordReader {

    /**
     * Read the words from the given files and return a set of all the valid words
     * @param wordsFiles one or more files containing words to add
     * @return the set of valid words
     */
    public static Set<String> getWords(File... wordsFiles) {

        Set<String> guesses = new HashSet<>();

        for (File wordsFile : wordsFiles) {
            try (Scanner scanner = new Scanner(new FileInputStream(wordsFile))) {

                while (scanner.hasNext()) {

                    String word = scanner.next();
                    if (isValidWord(word)) {
                        guesses.add(word);
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return guesses;
    }

    private static boolean isValidWord(String word) {
        if (word.length() == 5) {
            for ( int i = 0; i < 5; ++i ) {
                if (!Character.isLetter(word.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
