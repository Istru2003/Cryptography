package com.example.lab_tsi;

public class vigenereCipher {
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String decryptMessage(String key, String message) {
        return translateMessage(key, message, "decrypt");
    }

    private static String translateMessage(String key, String message, String mode) {
        StringBuilder translated = new StringBuilder();
        int keyIndex = 0;
        key = key.toUpperCase();

        for (char symbol : message.toCharArray()) {
            int num = LETTERS.indexOf(Character.toUpperCase(symbol));
            if (num != -1) {
                if (keyIndex < key.length()) {
                    if (mode.equals("encrypt")) {
                        num += LETTERS.indexOf(key.charAt(keyIndex));
                    } else if (mode.equals("decrypt")) {
                        num -= LETTERS.indexOf(key.charAt(keyIndex));
                        num = (num + LETTERS.length()) % LETTERS.length();
                    }
                }
                num %= LETTERS.length();

                if (Character.isUpperCase(symbol)) {
                    translated.append(LETTERS.charAt(num));
                } else if (Character.isLowerCase(symbol)) {
                    translated.append(Character.toLowerCase(LETTERS.charAt(num)));
                }

                keyIndex++;
                if (keyIndex == key.length()) {
                    keyIndex = 0;
                }
            } else {
                translated.append(symbol);
            }
        }

        return translated.toString();
    }
}
