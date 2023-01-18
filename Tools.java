package ru.startandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import java.util.Arrays;

public class Tools extends Activity {

    private Button apply;
    private Button quit_tools;
    private EditText current_password;
    private EditText new_password;
    private Editable c_password;
    private Editable n_password;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tools);
        sharedPreferences = getSharedPreferences("ru.startandroid", MODE_PRIVATE);

        addListenerOnButton();
    }

    public void addListenerOnButton() {
        apply = findViewById(R.id.button4);
        quit_tools = findViewById(R.id.button_quit);
        current_password = findViewById(R.id.editTextTextPassword);
        new_password = findViewById(R.id.editTextTextPassword2);


        quit_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), MainActivity.class));
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("CommitPrefEdits")
            @Override
            public void onClick(View v) {
                String hash = "";
                c_password = current_password.getText();
                System.out.println(c_password);
                n_password = new_password.getText();
                System.out.println(n_password);

                if (c_password.length() != 0 && n_password.length() != 0) {
                    if (c_password.length() >= 12&& n_password.length() >= 12) {
                        try {
                            String salt = sharedPreferences.getString("salt","");
                            hash = Main.Main2(c_password.toString(), salt);
                            String login_pr = sharedPreferences.getString("login", "");
                            String hash_pr = sharedPreferences.getString("hash", "");

                            if(hash_pr.equals(hash)){
                                //generacja nowych dannych
                                String generated_hash = " ";

                                byte[] generated_salt = Main.generateSalt();
                                generated_hash = Main.Main2(n_password.toString(), Arrays.toString(generated_salt));

                                sharedPreferences.edit().putString("salt", Arrays.toString(generated_salt)).apply();
                                sharedPreferences.edit().putString("login", login_pr.toString()).apply();
                                sharedPreferences.edit().putString("hash", generated_hash).apply();

                            } else{
                                Snackbar.make(v, "Password is not correct" , Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else{
                        Snackbar.make(v, "Password min 12", Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
                else{
                    Snackbar.make(v, "Empty one of the field", Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
}
