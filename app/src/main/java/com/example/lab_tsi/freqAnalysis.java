package com.example.lab_tsi;
import java.util.HashMap;
import java.util.Map;
public class freqAnalysis {
    private static final String ETAOIN = "ETAOINSHRDLCUMWFGYPBVKJXQZ";
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static Map<Character, Integer> getLetterCount(String message) {
        Map<Character, Integer> letterCount = new HashMap<>();
        for (char letter : message.toUpperCase().toCharArray()) {
            if (LETTERS.indexOf(letter) != -1) {
                letterCount.put(letter, letterCount.getOrDefault(letter, 0) + 1);
            }
        }
        return letterCount;
    }

    public static char getItemAtIndexZero(Map.Entry<Character, Integer> entry) {
        return entry.getKey();
    }

    public static String getFrequencyOrder(String message) {
        Map<Character, Integer> letterToFreq = getLetterCount(message);

        Map<Integer, StringBuilder> freqToLetter = new HashMap<>();
        for (char letter : LETTERS.toCharArray()) {
            int freq = letterToFreq.getOrDefault(letter, 0);
            freqToLetter.putIfAbsent(freq, new StringBuilder());
            freqToLetter.get(freq).append(letter);
        }

        StringBuilder freqOrder = new StringBuilder();
        for (int freq = message.length(); freq > 0; freq--) {
            if (freqToLetter.containsKey(freq)) {
                String letters = freqToLetter.get(freq).toString();
                letters.chars().sorted().forEach(c -> freqOrder.append((char) c));
            }
        }

        return freqOrder.toString();
    }

    public static int englishFreqMatchScore(String message) {
        String freqOrder = getFrequencyOrder(message);
        int matchScore = 0;
        int len = freqOrder.length();
        for (char commonLetter : ETAOIN.substring(0, 6).toCharArray()) {
            if (len >= 6 && freqOrder.substring(0, 6).indexOf(commonLetter) != -1) {
                matchScore++;
            }
        }
        for (char uncommonLetter : ETAOIN.substring(20).toCharArray()) {
            if (len > 20 && len - 6 >= 0 && freqOrder.substring(len - 6, len).indexOf(uncommonLetter) != -1) {
                matchScore++;
            }
        }
        return matchScore;
    }
}
