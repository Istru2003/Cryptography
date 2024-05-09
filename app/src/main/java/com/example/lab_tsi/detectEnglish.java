package com.example.lab_tsi;
import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
public class detectEnglish {
    private static final String UPPERLETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LETTERS_AND_SPACE = UPPERLETTERS + UPPERLETTERS.toLowerCase() + " \t\n";
    private static Context context;
    private static Map<String, Object> ENGLISH_WORDS;

    public detectEnglish(Context ctx) {
        context = ctx;
        loadDictionary();
    }
    private static void loadDictionary() {
        Map<String, Object> englishWords = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.dictionary)))) {
            String word;
            while ((word = reader.readLine()) != null) {
                englishWords.put(word, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ENGLISH_WORDS = englishWords;
    }

    private static String removeNonLetters(String message) {
        StringBuilder lettersOnly = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char symbol = message.charAt(i);
            if (LETTERS_AND_SPACE.indexOf(symbol) != -1) {
                lettersOnly.append(symbol);
            }
        }
        return lettersOnly.toString();
    }

    private static double getEnglishCount(String message) {
        if (ENGLISH_WORDS == null) {
            loadDictionary();
        }

        message = message.toUpperCase();
        message = removeNonLetters(message);
        String[] possibleWords = message.split("\\s+");

        if (possibleWords.length == 0) {
            return 0.0;
        }

        int matches = 0;
        for (String word : possibleWords) {
            if (ENGLISH_WORDS.containsKey(word)) {
                matches++;
            }
        }
        return (double) matches / possibleWords.length;
    }

    public static void initDictionary() {
        loadDictionary();
    }

    public static boolean isEnglish(String message) {
        int wordPercentage = 20;
        int letterPercentage = 85;
        boolean wordsMatch = getEnglishCount(message) * 100 >= wordPercentage;
        int numLetters = removeNonLetters(message).length();
        float messageLettersPercentage = (float) numLetters / message.length() * 100;
        boolean lettersMatch = messageLettersPercentage >= letterPercentage;
        return wordsMatch && lettersMatch;
    }
}
