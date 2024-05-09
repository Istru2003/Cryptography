package com.example.lab_tsi;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigInteger;

public class RSA extends AppCompatActivity {
    private EditText inputEditText, outputEditText, publickey, privatekey;
    private Button encryptButton, decryptButton, readFileButton;

    private int selectedKeyLength = 512;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rsa);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        inputEditText = findViewById(R.id.inputEditText_rsa);
        outputEditText = findViewById(R.id.outputEditText_rsa);
        publickey = findViewById(R.id.publickeyEditText);
        privatekey = findViewById(R.id.privatekeyEditText);
        encryptButton = findViewById(R.id.encryptButton_rsa);
        decryptButton = findViewById(R.id.decryptButton_rsa);
        readFileButton = findViewById(R.id.readFileButton_rsa);
        RadioGroup keyLengthRadioGroup = findViewById(R.id.keyLengthRadioGroup);
        keyLengthRadioGroup.check(R.id.radio512);

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

        keyLengthRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio512:
                        selectedKeyLength = 512;
                        break;
                    case R.id.radio1024:
                        selectedKeyLength = 1024;
                        break;
                    case R.id.radio2048:
                        selectedKeyLength = 2048;
                        break;
                }
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
        String inputText = inputEditText.getText().toString();

        if (encrypt) {
            Random r = new Random();
            BigInteger p = BigInteger.probablePrime(selectedKeyLength, r);
            BigInteger q = BigInteger.probablePrime(selectedKeyLength, r);
            BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
            BigInteger e;
            do {
                e = new BigInteger(phi.bitLength(), r);
            } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.ONE));
            BigInteger n = p.multiply(q);
            BigInteger d = calculatePrivateKey(phi, e) ;
            String asciiString = toAsciiString(inputText);
            String ciphertext = encrypt(asciiString, e, n);
            outputEditText.setText(ciphertext);
            publickey.setText("("+e+", "+n+")");
            privatekey.setText("("+d+", "+n+")");
        } else {
            String privateKeyText = privatekey.getText().toString();
            if(privateKeyText.isEmpty()){
                outputEditText.setText("Please enter private key");
                return;
            }else {
                privateKeyText = privateKeyText.replaceAll("[\\(\\)]", "");
                String[] privateKeyParts = privateKeyText.split(",");
                if (privateKeyParts.length != 2) {
                    outputEditText.setText("Invalid private key format");
                    return;
                }

                BigInteger d = new BigInteger(privateKeyParts[0].trim());
                BigInteger n = new BigInteger(privateKeyParts[1].trim());
                String decryptedAsciiString = decrypt(inputText, d, n);
                String decryptedWord = decodeAsciiString(decryptedAsciiString);
                outputEditText.setText(decryptedWord);
            }
        }
    }


    private BigInteger calculatePrivateKey(BigInteger phi, BigInteger e) {
        return extendedEuclidean(e, phi)[1].mod(phi);
    }

    private BigInteger[] extendedEuclidean(BigInteger a, BigInteger b) {
        if (b.equals(BigInteger.ZERO)) {
            return new BigInteger[]{a, BigInteger.ONE, BigInteger.ZERO};
        }
        BigInteger[] values = extendedEuclidean(b, a.mod(b));
        BigInteger x = values[2];
        BigInteger y = values[1].subtract(a.divide(b).multiply(values[2]));
        return new BigInteger[]{values[0], x, y};
    }

    private static String toAsciiString(String message) {
        StringBuilder sb = new StringBuilder();
        for (char c : message.toCharArray()) {
            int asciiCode = (int) c;
            sb.append(asciiCode).append(" ");
        }
        return sb.toString().trim();
    }

    public static String decodeAsciiString(String asciiString) {
        StringBuilder sb = new StringBuilder();
        String[] asciiCodes = asciiString.split(" ");
        for (String asciiCode : asciiCodes) {
            int code = Integer.parseInt(asciiCode);
            sb.append((char) code);
        }
        return sb.toString();
    }

    public String encrypt(String message, BigInteger e, BigInteger n) {
        StringBuilder ciphertextBuilder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            BigInteger m = BigInteger.valueOf((int) c);
            BigInteger encrypted = modPow(m, e, n);
            ciphertextBuilder.append(encrypted).append(" ");
        }
        return ciphertextBuilder.toString();
    }

    public String decrypt(String ciphertext, BigInteger d, BigInteger n) {
        StringBuilder plaintextBuilder = new StringBuilder();
        String[] encryptedChars = ciphertext.split(" ");
        for (String encryptedChar : encryptedChars) {
            BigInteger encrypted = new BigInteger(encryptedChar);
            BigInteger decrypted = modPow(encrypted, d, n);
            char plaintextChar = (char) decrypted.intValue();
            plaintextBuilder.append(plaintextChar);
        }
        return plaintextBuilder.toString();
    }

    private BigInteger modPow(BigInteger base, BigInteger exponent, BigInteger modulus) {
        BigInteger result = BigInteger.ONE;
        base = base.mod(modulus);
        while (exponent.compareTo(BigInteger.ZERO) > 0) {
            if (exponent.testBit(0)) {
                result = result.multiply(base).mod(modulus);
            }
            base = base.multiply(base).mod(modulus);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    private void readFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        startActivityForResult(intent, 1);
    }
}
