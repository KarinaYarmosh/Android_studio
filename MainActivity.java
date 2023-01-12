package ru.startandroid;


import static android.os.SystemClock.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText password_field;
    private EditText login_field;
    private Button btnSubmit;
    private Button registration;
    private Editable password;
    private Editable login;
    private Button finger;
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    ConstraintLayout mMainLayout;
    //public boolean fingerprint = false;
    Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("ru.startandroid", MODE_PRIVATE);
        mMainLayout = findViewById(R.id.main_layout);


        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(getApplicationContext(), "App can authenticate using biometrics", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(), "No biometric features available on this device", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(), "Biometric features are currently unavailable", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(getApplicationContext(), "Need register at least one fingerprint", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Unknown cause", Toast.LENGTH_SHORT).show();
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                addListenerOnButton();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //mMainLayout.setVisibility(View.VISIBLE);
                boolean fingerprint = sharedPreferences.edit().putBoolean("fingerprint", true).commit();
                startActivity(new Intent(getApplicationContext(), MainAfterLogin.class));
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                addListenerOnButton();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle("Use your fingerprint to login")
                .setDescription("").setDeviceCredentialAllowed(true).build();

        biometricPrompt.authenticate(promptInfo);

        addListenerOnButton();
    }

    public void addListenerOnButton() {

        password_field = findViewById(R.id.editTextNumberPassword);
        login_field = findViewById(R.id.editTextTextEmailAddress2);
        btnSubmit = findViewById(R.id.button);
        registration = findViewById(R.id.button2);

        registration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Registration.class));
            }
        });

        btnSubmit.setOnClickListener(new OnClickListener() {

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                String hash = " ";
                login = login_field.getText();
                System.out.println(login);
                password = password_field.getText();
                System.out.println(password);
                if (login.length() != 0 && password.length() != 0) {
                    if (password.length() >= 12) {
//                        String login_pr = sharedPreferences.getString("login", "");
//                        String hash_pr = sharedPreferences.getString("hash", "");
                        try {
//                            String salt = "qwertyuiopasdfghjklzxcvbnmqwerty";
//                            hash = Main.Main2(password.toString(), salt);
//                            sharedPreferences.edit().putString("salt", salt).apply();
//                            sharedPreferences.edit().putString("login", login.toString()).apply();
//                            sharedPreferences.edit().putString("hash", hash).apply();

                            String salt = sharedPreferences.getString("salt", "");
                            hash = Main.Main2(password.toString(), salt);
                            String login_pr = sharedPreferences.getString("login", "");
                            String hash_pr = sharedPreferences.getString("hash", "");

                            if (login_pr.equals(login.toString()) && hash_pr.equals(hash)) {
                                //generacja nowych dannych
                                String generated_hash = " ";

                                byte[] generated_salt = Main.generateSalt();
                                generated_hash = Main.Main2(password.toString(), Arrays.toString(generated_salt));

                                sharedPreferences.edit().putString("salt", Arrays.toString(generated_salt)).apply();
                                sharedPreferences.edit().putString("login", login.toString()).apply();
                                sharedPreferences.edit().putString("hash", generated_hash).apply();

                                boolean fingerprint = sharedPreferences.edit().putBoolean("fingerprint", true).commit();
                                startActivity(new Intent(getApplicationContext(), MainAfterLogin.class));

                            } else {
                                Snackbar.make(v, "Password or login is not correct", Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Snackbar.make(v, "Password min 12", Snackbar.LENGTH_LONG)
                                .show();
                    }
                } else {
                    Snackbar.make(v, "Empty password or login", Snackbar.LENGTH_LONG)
                            .show();
                }

            }

        });

    }
}
