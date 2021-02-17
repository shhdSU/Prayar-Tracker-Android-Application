package com.example.prayartracker;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.prayartracker.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prayer_login);

        final EditText EmailEditText = findViewById(R.id.Email);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);



        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        EmailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        loginButton.setOnClickListener(new View.OnClickListener() {
        String email = EmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                if(db.validateLogin(email,password)) {
                    Intent intent = new Intent(LoginActivity.this,HomeScreenActivity.class);
                    startActivity(intent);
                }
//                String email = EmailEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//                if (email.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                if (password.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_SHORT).show();
//                    return;
//
//                }
//                if (validateInputs(email,password)) {


//                }
            }
        });
    }

    private boolean validateInputs(String email, String pass){
        String emailRegex = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.find()){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(!pass.matches(".*\\d.*")){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;

        }
        else if(!pass.matches("[a-zA-Z0-9]*")){
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return false;

        }
        else
            return true;
    }
}
