package com.example.virtualresume.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.virtualresume.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

//Login screen with username and password input
public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //If user has logged in already
        if(ParseUser.getCurrentUser() != null){
            goMainActivity();
        }

        //Linking the elements between java and xml
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        //Clicking on login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "OnClick login button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        //Clicking on sign up button
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "OnClick sign up button");
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                SignUpUser(username, password);
            }
        });

    }

    //Attempting to login after onClick
    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempting to login user:" + username);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Failed login!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Navigate to MainActivity
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Success in login!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Attempting to sign up after onClick
    private void SignUpUser(String username, String password) {
        Log.i(TAG, "Attempting to sign up user:" + username);
        //Create the ParseUser
        ParseUser user = new ParseUser();
        //Set core properties
        user.setUsername(username);
        user.setPassword(password);
        //Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    //Navigate to MainActivity
                    Toast.makeText(LoginActivity.this, "Success in sign up!", Toast.LENGTH_SHORT).show();
                    //Navigate to MainActivity
                    goMainActivity();
                    return;
                }
                Toast.makeText(LoginActivity.this, "Failed in sign up!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Issue with sign up", e);
                return;
            }
        });
    }

    //Navigate to MainActivity
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}