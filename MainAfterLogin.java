package ru.startandroid;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import android.security.KeyPairGeneratorSpec;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;

public class MainAfterLogin extends AppCompatActivity {

    private Button btnTools;
    private Button btnQuit;
    private EditText note;
    private Button save_btn;
    private String filename = "My_notes.txt";
    private SharedPreferences sharedPreferences;
    private String alias = "alias";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_after_login);
        sharedPreferences = getSharedPreferences("ru.startandroid", MODE_PRIVATE);
        note = findViewById(R.id.note);


        String text = sharedPreferences.getString("Text_note", "");
        /*
         * Generate a new EC key pair entry in the Android Keystore by
         * using the KeyPairGenerator API. The private key can only be
         * used for signing or verification and only with SHA-256 or
         * SHA-512 as the message digest.
         */

        //String alias = "alias";
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        try {
            assert keyStore != null;
            keyStore.load(null);
        } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        KeyPair keyPair = null;
        boolean fingerprint = sharedPreferences.getBoolean("fingerprint", false);
        // Create new key if needed
        try {
            if (!keyStore.containsAlias(alias)) {
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 1);
                KeyPairGeneratorSpec spec;
                spec = new KeyPairGeneratorSpec.Builder(getApplicationContext())
                        .setAlias(alias)
                        .setEncryptionRequired() //
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();
                KeyPairGenerator generator = null;
                try {
                    generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore");
                } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                    e.printStackTrace();
                }
                try {
                    assert generator != null;
                    generator.initialize(spec);
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                }

                keyPair = generator.generateKeyPair();
            }
        }catch (KeyStoreException e) {
            e.printStackTrace();
        }

        //boolean fingerprint = sharedPreferences.getBoolean("fingerprint", false);
        System.out.println(fingerprint);
        PublicKey publicKey = null;
        Key privateKey = null;

        if (fingerprint) {
            try {
                publicKey = keyStore.getCertificate(alias).getPublicKey();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            try {
                privateKey = (PrivateKey) keyStore.getKey(alias, null);
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                e.printStackTrace();
            }
        }
//        try {
//            publicKey = keyStore.getCertificate(alias).getPublicKey();
//        } catch (KeyStoreException e) {
//            e.printStackTrace();
//        }
//        try {
//            privateKey = (PrivateKey) keyStore.getKey(alias, null);
//        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
//            e.printStackTrace();
//        }

        System.out.println("PUBLIC " + publicKey);
        System.out.println("PRIVATE " + privateKey);

        Cipher cipher;
        Cipher cipher1;

        System.out.println("Text: " + text);
        byte[] notatka = text.getBytes(StandardCharsets.UTF_8);
        System.out.println("Notatka: " + Arrays.toString(notatka));

        byte[] decrypt_note = new byte[0];
        try {
            //RSA/ECB/OAEPWithSHA-1AndMGF1Padding
            //AES/CBC/PKCS5Padding
            //DES/CBC/PKCS5Padding
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
            cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");

            byte[] IV = cipher.getIV();

//            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//            String encrypt_note = Base64.encodeToString(cipher.doFinal(notatka),Base64.DEFAULT);
//            System.out.println("EN: " + encrypt_note);

            cipher1.init(Cipher.DECRYPT_MODE, privateKey);
            decrypt_note = cipher1.doFinal(Base64.decode(text, Base64.DEFAULT));
            System.out.println("DE: " + new String(decrypt_note));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        note.setText(new String(decrypt_note));

        addListenerOnButton();
    }

        public void addListenerOnButton() {

        btnTools = findViewById(R.id.tools);
        btnQuit = findViewById(R.id.main_menu);
        note = findViewById(R.id.note);
        save_btn = findViewById(R.id.tools2);

        btnTools.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Tools.class));
            }

        });

        btnQuit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
                boolean fingerprint = sharedPreferences.edit().putBoolean("fingerprint", false).commit();
            }

        });

        save_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String text = String.valueOf(note.getText());

                KeyStore keyStore = null;
                try {
                    keyStore = KeyStore.getInstance("AndroidKeyStore");
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
                try {
                    assert keyStore != null;
                    keyStore.load(null);
                } catch (CertificateException | IOException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }

                PublicKey publicKey = null;
                Key privateKey = null;
                try {
                    publicKey = keyStore.getCertificate(alias).getPublicKey();
                } catch (KeyStoreException e) {
                    e.printStackTrace();
                }
                try {
                    privateKey = (PrivateKey) keyStore.getKey(alias, null);
                } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                    e.printStackTrace();
                }

                Cipher cipher;
                Cipher cipher1;

                System.out.println("Text: " + text);
                byte[] notatka = text.getBytes(StandardCharsets.UTF_8);
                System.out.println("Notatka: " + Arrays.toString(notatka));

                String encrypt_note = null;
                try {
                    //RSA/ECB/OAEPWithSHA-1AndMGF1Padding
                    //AES/CBC/PKCS5Padding
                    //DES/CBC/PKCS5Padding
                    cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
                    cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");

                    byte[] IV = cipher.getIV();

                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    encrypt_note = Base64.encodeToString(cipher.doFinal(notatka), Base64.DEFAULT);
                    System.out.println("EN: " + encrypt_note);

                    cipher1.init(Cipher.DECRYPT_MODE, privateKey);
                    byte[] decrypt_note = cipher1.doFinal(Base64.decode(encrypt_note, Base64.DEFAULT));
                    System.out.println("DE: " + new String(decrypt_note));

                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }

                sharedPreferences.edit().putString("Text_note", encrypt_note).apply();
            }

        });
    }
}
