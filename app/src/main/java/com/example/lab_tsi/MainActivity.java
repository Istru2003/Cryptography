package com.example.lab_tsi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private EditText inputEditText, outputEditText, keyEditText1, keyEditText2;
    private Button encryptButton, decryptButton, readFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        inputEditText = findViewById(R.id.inputEditText);
        outputEditText = findViewById(R.id.outputEditText);
        keyEditText1 = findViewById(R.id.keyEditText1);
        keyEditText2 = findViewById(R.id.keyEditText2);
        encryptButton = findViewById(R.id.encryptButton);
        decryptButton = findViewById(R.id.decryptButton);
        readFileButton = findViewById(R.id.readFileButton);

        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performOperation(true);
            }
        });

        decryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performOperation(false);
            }
        });

        readFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readFile();
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    inputEditText.setText(sb.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void readFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, 1);
    }

    private void performOperation(boolean encrypt) {
        String inputText = inputEditText.getText().toString().toUpperCase();
        String key1Text = keyEditText1.getText().toString();
        String key2Text = keyEditText2.getText().toString().toUpperCase();

        // Check if key1Text is empty
        if (key1Text.isEmpty()) {
            outputEditText.setText("Please enter a valid key.");
            return;
        }

        int key1;
        try {
            // Parse key1Text to integer
            key1 = Integer.parseInt(key1Text);
        } catch (NumberFormatException e) {
            // Handle NumberFormatException
            outputEditText.setText("Please enter a valid integer key.");
            return;
        }

        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String result;

        if (!key2Text.isEmpty()) {
            if (inputText.isEmpty()) {
                outputEditText.setText("Please enter text to encrypt/decrypt.");
                return;
            }
            if (key2Text.length() < 7) {
                outputEditText.setText("Key 2 must be at least 7 characters long.");
                return;
            }

            String modifiedAlphabet = createNewAlphabet(key2Text, alphabet);
            result = encrypt ? encryptWithKey(inputText, key1, modifiedAlphabet) : decryptWithKey(inputText, key1, modifiedAlphabet);
        } else {
            if (inputText.isEmpty()) {
                outputEditText.setText("Please enter text to encrypt/decrypt.");
                return;
            }
            if (key1 == 26) {
                outputEditText.setText("Error: Key 1 cannot be 26.");
                return;
            }
            result = encrypt ? encryptWithKey(inputText, key1, alphabet) : decryptWithKey(inputText, key1, alphabet);
        }
        outputEditText.setText(result);
    }



    public String encryptWithKey(String input, int key, String alphabet) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            int charPosition = alphabet.indexOf(input.charAt(i));
            if (charPosition == -1) {
                result.append(currentChar);
                continue;
            }
            int keyVal = (key + charPosition) % 26;
            char replaceVal = alphabet.charAt(keyVal);
            result.append(replaceVal);
        }
        return result.toString();
    }

    public String decryptWithKey(String input, int key, String alphabet) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            int charPosition = alphabet.indexOf(input.charAt(i));
            if (charPosition == -1) {
                result.append(currentChar);
                continue;
            }
            int keyVal = (charPosition - key) % 26;
            if (keyVal < 0) {
                keyVal = alphabet.length() + keyVal;
            }
            char replaceVal = alphabet.charAt(keyVal);
            result.append(replaceVal);
        }
        return result.toString();
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
}


