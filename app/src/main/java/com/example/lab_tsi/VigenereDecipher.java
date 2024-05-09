package com.example.lab_tsi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.util.HashSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VigenereDecipher extends AppCompatActivity {

    private EditText inputEditText, outputEditText;
    private TextView keypanel_decipher;
    private Button decryptButton, readFileButton, continueButton;
    private final boolean SILENT_MODE = false;
    private final int NUM_MOST_FREQ_LETTERS = 4;
    private static final int MAX_KEY_LENGTH = 16;
    private final char[] LETTERS = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    private final Pattern NONLETTERS_PATTERN = Pattern.compile("[^A-Z]");
    private detectEnglish englishDetector;
    private String ciphertext, ciphertextUp;
    private int mostLikelyKeyLength;
    private List<List<String[]>> allFreqScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viginere_decipher);

        keypanel_decipher = findViewById(R.id.keyTextView);
        inputEditText = findViewById(R.id.inputEditText_decipher);
        outputEditText = findViewById(R.id.outputEditText_decipher);
        decryptButton = findViewById(R.id.decryptButton_decipher);
        readFileButton = findViewById(R.id.readFileButton_decipher);
        continueButton = findViewById(R.id.continue_decipher);

        Toolbar toolbar = findViewById(R.id.toolbar_decypher);
        setSupportActionBar(toolbar);

        englishDetector = new detectEnglish(this);
        detectEnglish.initDictionary();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hackVigenere();
            }
        });
        readFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hack();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.nav_menu, menu);
        return true;
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


    public void hackVigenere() {
        String ciphertext = inputEditText.getText().toString().toUpperCase();
        List<Integer> allLikelyKeyLengths = kasiskiExamination(ciphertext);
        if (!SILENT_MODE) {
            StringBuilder keyLengthStr = new StringBuilder();
            for (int keyLength : allLikelyKeyLengths) {
                keyLengthStr.append(keyLength).append(" ");
                //System.out.println("Kasiski examination results say the most likely key lengths are: " + keyLengthStr + "\n");
            }
        }
        String hackedMessage = null;
        for (int keyLength : allLikelyKeyLengths) {
            if (!SILENT_MODE) {
                //System.out.println("Attempting hack with key length " + keyLength + " (" + Math.pow(NUM_MOST_FREQ_LETTERS, keyLength) + " possible keys)...");
            }
            hackedMessage = attemptHackWithKeyLength(ciphertext, keyLength);
            if (hackedMessage != null) {
                outputEditText.setText(hackedMessage);
                break;
            }
        }

        if (hackedMessage == null) {
            if (!SILENT_MODE) {
                //System.out.println("Unable to hack message with likely key length(s). Brute-forcing key length...");
            }
            for (int keyLength = 1; keyLength <= MAX_KEY_LENGTH; keyLength++) {
                if (!allLikelyKeyLengths.contains(keyLength)) {
                    if (!SILENT_MODE) {
                        //System.out.println("Attempting hack with key length " + keyLength + " (" + Math.pow(NUM_MOST_FREQ_LETTERS, keyLength) + " possible keys)...");
                    }
                    hackedMessage = attemptHackWithKeyLength(ciphertext, keyLength);
                    if (hackedMessage != null) {
                        break;
                    }
                }
            }
        }
    }

    public String hack(){
        String decryptedTextAttempt = null; // Initialize outside the loop

        for (int[] indexes : product(NUM_MOST_FREQ_LETTERS, mostLikelyKeyLength)) {
            StringBuilder possibleKey = new StringBuilder();
            for (int i = 0; i < mostLikelyKeyLength; i++) {
                List<String[]> freqScoreList = allFreqScores.get(i);
                String[] freqScoreArray = freqScoreList.get(indexes[i]);
                possibleKey.append(freqScoreArray[0].charAt(0));
            }

            if (!SILENT_MODE) {
                //System.out.println("Attempting with key: " + possibleKey);
            }

            String decryptedTextAttemptCandidate = vigenereCipher.decryptMessage(possibleKey.toString(), ciphertextUp);

            if (detectEnglish.isEnglish(decryptedTextAttemptCandidate)) {
                StringBuilder origCase = new StringBuilder();
                for (int i = 0; i < ciphertext.length(); i++) {
                    if (Character.isUpperCase(ciphertext.charAt(i))) {
                        origCase.append(Character.toUpperCase(decryptedTextAttemptCandidate.charAt(i)));
                    } else {
                        origCase.append(Character.toLowerCase(decryptedTextAttemptCandidate.charAt(i)));
                    }
                }
                decryptedTextAttempt = origCase.toString();
                if(!decryptedTextAttempt.isEmpty()){
                    keypanel_decipher.setText("Decrypted with key: " + possibleKey);
                    return decryptedTextAttempt;
                }
            }
        }
        return decryptedTextAttempt; // Return outside the loop
    }


    public List<Integer> kasiskiExamination(String ciphertext) {
        Map<String, List<Integer>> repeatedSeqSpacings = findRepeatSequencesSpacings(ciphertext);

        Map<String, List<Integer>> seqFactors = new HashMap<>();
        for (String seq : repeatedSeqSpacings.keySet()) {
            seqFactors.put(seq, new ArrayList<>());
            for (int spacing : repeatedSeqSpacings.get(seq)) {
                seqFactors.get(seq).addAll(getUsefulFactors(spacing));
            }
        }

        List<Map.Entry<Integer, Integer>> factorsByCount = getMostCommonFactors(seqFactors);

        List<Integer> allLikelyKeyLengths = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : factorsByCount) {
            allLikelyKeyLengths.add(entry.getKey());
        }

        return allLikelyKeyLengths;
    }

    public String getNthSubkeysLetters(int nth, int keyLength, String message) {
        Pattern pattern = NONLETTERS_PATTERN;
        Matcher matcher = pattern.matcher(message.toUpperCase());
        String cleanedMessage = matcher.replaceAll("");

        int i = nth - 1;
        StringBuilder letters = new StringBuilder();
        while (i < cleanedMessage.length()) {
            letters.append(cleanedMessage.charAt(i));
            i += keyLength;
        }

        return letters.toString();
    }

    public String attemptHackWithKeyLength(String ciphertext, int mostLikelyKeyLength) {
        String ciphertextUp = ciphertext.toUpperCase();
        List<List<String[]>> allFreqScores = new ArrayList<>();
        for (int nth = 1; nth <= mostLikelyKeyLength; nth++) {
            String nthLetters = getNthSubkeysLetters(nth, mostLikelyKeyLength, ciphertextUp);

            List<String[]> freqScores = new ArrayList<>();
            for (char possibleKey : LETTERS) {
                String decryptedTextAttempt = vigenereCipher.decryptMessage(Character.toString(possibleKey), nthLetters);
                String[] keyAndFreqMatchTuple = {String.valueOf(possibleKey), String.valueOf(freqAnalysis.englishFreqMatchScore(decryptedTextAttempt))};
                freqScores.add(keyAndFreqMatchTuple);
            }
            freqScores.sort((a, b) -> Double.compare(Double.parseDouble(b[1]), Double.parseDouble(a[1])));

            List<String[]> mostFreqLetters = new ArrayList<>();
            for (int i = 0; i < NUM_MOST_FREQ_LETTERS; i++) {
                mostFreqLetters.add(freqScores.get(i));
            }
            allFreqScores.add(freqScores);
        }

        if (!SILENT_MODE) {
            for (int i = 0; i < allFreqScores.size(); i++) {
                //System.out.print("Possible letters for letter " + (i + 1) + " of the key: ");
                for (String[] freqScoreArray : allFreqScores.get(i)) {
                    //System.out.print(freqScoreArray[0] + " ");
                }
                //System.out.println();
            }

        }

        this.ciphertext = ciphertext;
        this.mostLikelyKeyLength = mostLikelyKeyLength;
        this.allFreqScores = allFreqScores;
        this.ciphertextUp = ciphertextUp;

        String decryptedtext = hack();

        return decryptedtext;
    }

    public List<int[]> product(int numMostFreqLetters, int mostLikelyKeyLength) {
        List<int[]> combinations = new ArrayList<>();
        int[] indexes = new int[mostLikelyKeyLength];
        generateCombination(combinations, indexes, numMostFreqLetters, 0);
        return combinations;
    }

    private void generateCombination(List<int[]> combinations, int[] indexes, int numMostFreqLetters, int position) {
        if (position == indexes.length) {
            combinations.add(indexes.clone());
            return;
        }
        for (int i = 0; i < numMostFreqLetters; i++) {
            indexes[position] = i;
            generateCombination(combinations, indexes, numMostFreqLetters, position + 1);
        }
    }

    public String input(String prompt) {
        Scanner scanner = new Scanner(System.in);
        //System.out.print(prompt);
        return scanner.nextLine();
    }


    public Map<String, List<Integer>> findRepeatSequencesSpacings(String message) {
        String cleanedMessage = message.replaceAll("[^A-Z]", "").toUpperCase();

        Map<String, List<Integer>> seqSpacings = new HashMap<>();

        for (int seqLen = 3; seqLen <= 5; seqLen++) {
            for (int seqStart = 0; seqStart < cleanedMessage.length() - seqLen; seqStart++) {
                String seq = cleanedMessage.substring(seqStart, seqStart + seqLen);

                for (int i = seqStart + seqLen; i < cleanedMessage.length() - seqLen; i++) {
                    if (cleanedMessage.substring(i, i + seqLen).equals(seq)) {
                        if (!seqSpacings.containsKey(seq)) {
                            seqSpacings.put(seq, new ArrayList<>());
                        }
                        seqSpacings.get(seq).add(i - seqStart);
                    }
                }
            }
        }

        return seqSpacings;
    }


    public List<Map.Entry<Integer, Integer>> getMostCommonFactors(Map<String, List<Integer>> seqFactors) {
        Map<Integer, Integer> factorCounts = new HashMap<>();

        for (List<Integer> factorList : seqFactors.values()) {
            for (int factor : factorList) {
                if (!factorCounts.containsKey(factor)) {
                    factorCounts.put(factor, 0);
                }
                factorCounts.put(factor, factorCounts.get(factor) + 1);
            }
        }

        List<Map.Entry<Integer, Integer>> factorsByCount = new ArrayList<>(factorCounts.entrySet());
        factorsByCount.removeIf(entry -> entry.getKey() > MAX_KEY_LENGTH);

        factorsByCount.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        return factorsByCount;
    }

    public static List<Integer> getUsefulFactors(int num) {
        List<Integer> factors = new ArrayList<>();

        if (num < 2) {
            return factors;
        }
        for (int i = 2; i <= MAX_KEY_LENGTH; i++) {
            if (num % i == 0) {
                factors.add(i);
                int otherFactor = num / i;
                if (otherFactor < MAX_KEY_LENGTH + 1 && otherFactor != 1) {
                    factors.add(otherFactor);
                }
            }
        }
        return new ArrayList<>(new HashSet<>(factors));
    }
    private void readFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, 1);
    }
}