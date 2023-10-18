package com.techelevator.utils;

import java.io.*;
import java.util.*;

public class WordReader {

    public static Map<Integer, String> getWords(File wordsFile) {

        Map<Integer, String> words = new HashMap<>();
        int count = 0;

        try (Scanner scanner = new Scanner(new FileInputStream(wordsFile))) {

            while (scanner.hasNext()) {

                String word = scanner.next();
                if (isValidWord(word)) {
                    words.put(count++, word);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return words;
    }

    public static Set<String> getGuesses(File... wordsFiles) {

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
    public static void processFiles(File wordsFolder, File wordsFile) {

        Set<String> words = new HashSet<>();


        for (File file : wordsFolder.listFiles()) {


            if (!file.isDirectory()) {
                try (Scanner scanner = new Scanner(new FileInputStream(file))) {


                    while (scanner.hasNext()) {

                        String word = scanner.next();
                        if (isValidWord(word)) {
                            words.add(word);
                        }
                    }
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        List<String> wordList = new ArrayList<>(words);
        Collections.sort(wordList);
        try (PrintWriter writer = new PrintWriter(wordsFile)) {
            for (String word : wordList) {
                writer.println(word);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
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
