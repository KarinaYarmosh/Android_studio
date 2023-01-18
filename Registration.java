package ru.startandroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;

import ru.startandroid.security.DBManager;

public class Registration extends AppCompatActivity {

    private Button button3;
    private Button button_login;
    private EditText name;
    private EditText password_reg;
    private EditText password2_reg;
    private EditText login_reg;
    private Editable pass;
    private Editable log;
    private Editable pass2;
    private Editable name_of;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        sharedPreferences = getSharedPreferences("ru.startandroid", MODE_PRIVATE);

        addListenerOnButton();
    }

    public void addListenerOnButton() {

        button_login = findViewById(R.id.button_login);
        button3 = findViewById(R.id.button3);
        login_reg = findViewById(R.id.login_reg);
        password_reg = findViewById(R.id.password_reg);
        button_login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }

        });

        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String hash = " ";
                log = login_reg.getText();
                System.out.println(log);
                pass = password_reg.getText();
                System.out.println(pass);
                if (log.length() != 0 && pass.length() != 0) {
                    if (pass.length() >= 12) {
                        try {
                            String salt = "qwertyuiopasdfghjklzxcvbnmqwerty";
                            hash = Main.Main2(pass.toString(), salt);
                            sharedPreferences.edit().putString("salt", salt).apply();
                            sharedPreferences.edit().putString("login", log.toString()).apply();
                            sharedPreferences.edit().putString("hash", hash).apply();
                            startActivity(new Intent(v.getContext(), MainActivity.class));
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
