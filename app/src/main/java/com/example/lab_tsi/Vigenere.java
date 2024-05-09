package com.example.lab_tsi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Vigenere extends AppCompatActivity {

    private EditText inputEditText, outputEditText, keyEditText;
    private Button encryptButton, decryptButton, readFileButton, changelangButton;

    final CharSequence[] languages = {"Română", "English"};
    private String selectedLanguage = "English";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viginere_activity);

        inputEditText = findViewById(R.id.inputEditText_viginere);
        outputEditText = findViewById(R.id.outputEditText_viginere);
        keyEditText = findViewById(R.id.keyEditText);
        encryptButton = findViewById(R.id.encryptButton_viginere);
        decryptButton = findViewById(R.id.decryptButton_viginere);
        readFileButton = findViewById(R.id.readFileButton_viginere);
        changelangButton = findViewById(R.id.cangeButton_viginere);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

        changelangButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLanguageDialog();
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

    private void performOperation(boolean encrypt) {
        String inputText = inputEditText.getText().toString().toUpperCase();
        String keyText = keyEditText.getText().toString().toUpperCase();
        String alfabetRO = "AĂÂBCDEFGHIÎJKLMNOPQRSȘTȚUVWXYZ";
        String alfabetEN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        if (inputText.isEmpty() || keyText.isEmpty()) {
            outputEditText.setText("Introduceți text și cheia.");
            return;
        }

        String selectedAlphabet = selectedLanguage.equals("Română") ? alfabetRO : alfabetEN;

        if (encrypt) {
            outputEditText.setText(cripteaza(inputText, keyText, selectedAlphabet));
        } else {
            outputEditText.setText(decripteaza(inputText, keyText, selectedAlphabet));
        }
    }

    private void readFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, 1);
    }

    // Funcție pentru criptare
    public static String cripteaza(String textClar, String cheie, String alfabet) {
        StringBuilder rezultat = new StringBuilder();
        int lungimeCheie = cheie.length();
        for (int i = 0, j = 0; i < textClar.length(); i++) {
            char caracter = textClar.charAt(i);
            if (alfabet.indexOf(caracter) != -1) {
                int indexCaracter = alfabet.indexOf(caracter);
                int indexCheie = alfabet.indexOf(cheie.charAt(j));
                int indexCriptat = (indexCaracter + indexCheie) % alfabet.length();
                rezultat.append(alfabet.charAt(indexCriptat));
                j = (j + 1) % lungimeCheie;
            } else {
                rezultat.append(caracter);
            }
        }
        return rezultat.toString();
    }

    // Funcție pentru decriptare
    public static String decripteaza(String textCriptat, String cheie, String alfabet) {
        StringBuilder rezultat = new StringBuilder();
        int lungimeCheie = cheie.length();
        for (int i = 0, j = 0; i < textCriptat.length(); i++) {
            char caracter = textCriptat.charAt(i);
            if (alfabet.indexOf(caracter) != -1) {
                int indexCaracter = alfabet.indexOf(caracter);
                int indexCheie = alfabet.indexOf(cheie.charAt(j));
                int indexDecriptat = (indexCaracter - indexCheie + alfabet.length()) % alfabet.length();
                rezultat.append(alfabet.charAt(indexDecriptat));
                j = (j + 1) % lungimeCheie;
            } else {
                rezultat.append(caracter);
            }
        }
        return rezultat.toString();
    }

    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Language");
        builder.setItems(languages, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedLanguage = languages[which].toString();
                changelangButton.setText("Change Language (for " + selectedLanguage + " encryption)");
            }
        });
        builder.show();
    }


}
