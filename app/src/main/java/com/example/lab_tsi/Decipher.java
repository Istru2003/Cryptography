package com.example.lab_tsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import android.widget.TableLayout;


public class Decipher extends AppCompatActivity {
    private Button decryptButton_decipher, readFileButton_decipher;
    private EditText inputEditText_decipher, outputEditText_decipher, keyEditText2;
    private TextView keypanel_decipher;

    private static final Map<Character, Double> ENGLISH_LETTER_FREQUENCY = new HashMap<>();

    static {
        ENGLISH_LETTER_FREQUENCY.put('A', 0.0817);
        ENGLISH_LETTER_FREQUENCY.put('B', 0.0149);
        ENGLISH_LETTER_FREQUENCY.put('C', 0.0278);
        ENGLISH_LETTER_FREQUENCY.put('D', 0.0425);
        ENGLISH_LETTER_FREQUENCY.put('E', 0.127);
        ENGLISH_LETTER_FREQUENCY.put('F', 0.0223);
        ENGLISH_LETTER_FREQUENCY.put('G', 0.0202);
        ENGLISH_LETTER_FREQUENCY.put('H', 0.0609);
        ENGLISH_LETTER_FREQUENCY.put('I', 0.0697);
        ENGLISH_LETTER_FREQUENCY.put('J', 0.0015);
        ENGLISH_LETTER_FREQUENCY.put('K', 0.0077);
        ENGLISH_LETTER_FREQUENCY.put('L', 0.0403);
        ENGLISH_LETTER_FREQUENCY.put('M', 0.0241);
        ENGLISH_LETTER_FREQUENCY.put('N', 0.0675);
        ENGLISH_LETTER_FREQUENCY.put('O', 0.0751);
        ENGLISH_LETTER_FREQUENCY.put('P', 0.0193);
        ENGLISH_LETTER_FREQUENCY.put('Q', 0.001);
        ENGLISH_LETTER_FREQUENCY.put('R', 0.0599);
        ENGLISH_LETTER_FREQUENCY.put('S', 0.0633);
        ENGLISH_LETTER_FREQUENCY.put('T', 0.0906);
        ENGLISH_LETTER_FREQUENCY.put('U', 0.0276);
        ENGLISH_LETTER_FREQUENCY.put('V', 0.0098);
        ENGLISH_LETTER_FREQUENCY.put('W', 0.0236);
        ENGLISH_LETTER_FREQUENCY.put('X', 0.0015);
        ENGLISH_LETTER_FREQUENCY.put('Y', 0.0197);
        ENGLISH_LETTER_FREQUENCY.put('Z', 0.0007);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.decipher_activity);

        keyEditText2 = findViewById(R.id.keyEditText2);
        keypanel_decipher = findViewById(R.id.keyTextView);
        decryptButton_decipher = findViewById(R.id.decryptButton_decipher);
        readFileButton_decipher = findViewById(R.id.readFileButton_decipher);
        inputEditText_decipher = findViewById(R.id.inputEditText_decipher);
        outputEditText_decipher = findViewById(R.id.outputEditText_decipher);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        decryptButton_decipher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDecryption();
            }
        });

        readFileButton_decipher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_cesar) {
            Intent intent= new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_decipher) {
            Intent intent= new Intent(this, Decipher.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_viginere) {
            Intent intent= new Intent(this, Vigenere.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.nav_viginere_decipher) {
            Intent intent= new Intent(this, VigenereDecipher.class);
            startActivity(intent);
            return true;
        }else if (id == R.id.nav_rsa) {
            Intent intent= new Intent(this, RSA.class);
            startActivity(intent);
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
    }

    public String decryptCesar(String input, int key, String ALPHABET) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            int charPosition = ALPHABET.indexOf(input.charAt(i));
            if (charPosition == -1) {
                result.append(currentChar);
                continue;
            }
            int keyVal = (charPosition - key) % 26;
            if (keyVal < 0) {
                keyVal = ALPHABET.length() + keyVal;
            }
            char replaceVal = ALPHABET.charAt(keyVal);
            result.append(replaceVal);
        }
        return result.toString();
    }

    private void performDecryption() {
        String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String key2Text = keyEditText2.getText().toString().toUpperCase();
        String text = inputEditText_decipher.getText().toString().toUpperCase();

        if(!key2Text.isEmpty()){
            if (text.isEmpty()) {
                outputEditText_decipher.setText("Please enter text to encrypt/decrypt.");
                return;
            }
            if (key2Text.length() < 7) {
                outputEditText_decipher.setText("Key 2 must be at least 7 characters long.");
                return;
            }
            String modifiedAlphabet = createNewAlphabet(key2Text, ALPHABET);
            decrypt(modifiedAlphabet, text);
        } else {
            if (text.isEmpty()) {
                outputEditText_decipher.setText("Please enter text to encrypt/decrypt.");
                return;
            }
            decrypt(ALPHABET, text);
        }
    }

    private void decrypt(String ALPHABET, String text){
        Map<Character, Integer> letterCount = new HashMap<>();
        Map<Character, Double> percentages = calculateLetterPercentage(text);
        TableLayout letterPercentageTable = new TableLayout(this);
        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(10, 5, 10, 5);
        letterPercentageTable.setLayoutParams(rowParams);

        for (int i = 0; i < ALPHABET.length(); i += 9) {
            TableRow letterRow = new TableRow(this);
            TableRow percentageRow = new TableRow(this);

            for (int j = i; j < Math.min(i + 9, ALPHABET.length()); j++) {
                TextView letterCell = new TextView(this);
                letterCell.setText(Character.toString(ALPHABET.charAt(j)));
                letterCell.setPadding(10, 5, 10, 5);
                letterRow.addView(letterCell);

                TextView percentageCell = new TextView(this);
                double percentage = percentages.getOrDefault(ALPHABET.charAt(j), 0.0);
                percentageCell.setText(String.format("%.2f%%", percentage));
                percentageCell.setPadding(10, 5, 10, 5);
                percentageRow.addView(percentageCell);
            }

            letterPercentageTable.addView(letterRow);
            letterPercentageTable.addView(percentageRow);
        }

        TableLayout inputPanel = findViewById(R.id.percentageTable);
        inputPanel.removeAllViews();
        inputPanel.addView(letterPercentageTable);

        int totalLetters = 0;
        for (char c : text.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
                totalLetters++;
            }
        }

        Map<Character, Double> letterFrequency = new HashMap<>();
        for (Map.Entry<Character, Integer> entry : letterCount.entrySet()) {
            letterFrequency.put(entry.getKey(), (double) entry.getValue() / totalLetters);
        }

        Map<Integer, Double> scores = new HashMap<>();
        for (int key = 1; key <= 26; key++) {
            double score = 0;
            String decryptedText = decryptCesar(text, key, ALPHABET);
            for (Map.Entry<Character, Double> entry : letterFrequency.entrySet()) {
                char letter = entry.getKey();
                double frequency = entry.getValue();
                if (ENGLISH_LETTER_FREQUENCY.containsKey(letter)) {
                    score += Math.abs(ENGLISH_LETTER_FREQUENCY.get(letter) - ((double) decryptedText.chars().filter(c -> c == letter).count() / totalLetters));
                }
            }
            scores.put(key, score);
        }

        int bestKey = scores.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
        String decryptedText = decryptCesar(text, bestKey, ALPHABET);

        outputEditText_decipher.setText(decryptedText);
        keypanel_decipher.setText("Decrypted with key: " + bestKey);
    }

    private Map<Character, Double> calculateLetterPercentage(String text) {
        text = text.toUpperCase();
        Map<Character, Integer> letterCount = new HashMap<>();
        int totalLetters = 0;

        for (char c : text.toCharArray()) {
            if (Character.isAlphabetic(c)) {
                letterCount.put(c, letterCount.getOrDefault(c, 0) + 1);
                totalLetters++;
            }
        }

        Map<Character, Double> percentages = new HashMap<>();
        for (Map.Entry<Character, Integer> entry : letterCount.entrySet()) {
            char letter = entry.getKey();
            int count = entry.getValue();
            double percentage = (double) count / totalLetters * 100;
            percentages.put(letter, percentage);
        }

        return percentages;
    }

    public String createNewAlphabet(String key2, String alphabet) {
        LinkedHashSet<Character> set = new LinkedHashSet<>();
        for (char c : key2.toUpperCase().toCharArray()) {
            if (Character.isLetter(c)) {
                set.add(c);
            }
        }

        for (int i = 0; i < alphabet.length(); i++) {
            char c = alphabet.charAt(i);
            if (!set.contains(c)) {
                set.add(c);
            }
        }

        StringBuilder result = new StringBuilder();
        for (char c : set) {
            result.append(c);
        }

        return result.toString();
    }


    private void readFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, 1);
    }
}
