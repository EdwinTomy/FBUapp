package com.example.virtualresume.activities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PointFEvaluator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
    private ImageView person1;
    private ImageView person2;
    private ImageView person3;
    private ImageView idea1;
    private ImageView idea2;
    private ImageView idea3;

    @RequiresApi(api = Build.VERSION_CODES.O)
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
                goCreateUserActivity();
            }
        });

        //Animation
        person1 = findViewById(R.id.ivLeftPerson);
        person2 = findViewById(R.id.ivRightPerson);
        person3 = findViewById(R.id.ivLeftPerson2);
        idea1 = findViewById(R.id.ivIdea1);
        idea2 = findViewById(R.id.ivIdea);
        idea3 = findViewById(R.id.ivIdea2);

        AnimatorSet motionXY = new AnimatorSet();
        ObjectAnimator animation1 = ObjectAnimator.ofFloat(person1, "translationX", 450);
        ObjectAnimator animation2 = ObjectAnimator.ofFloat(person2, "translationX", -450);
        ObjectAnimator animation3 = ObjectAnimator.ofFloat(person3, "translationY", -450);

        ObjectAnimator animationIdea1 = ObjectAnimator.ofFloat(idea1, "translationX", 500);
        ObjectAnimator animationIdea2 = ObjectAnimator.ofFloat(idea2, "translationX", -500);
        ObjectAnimator animationIdea3 = ObjectAnimator.ofFloat(idea3, "translationY", -500);

        motionXY.playTogether(animation1, animation2, animation3, animationIdea1, animationIdea2, animationIdea3);
        motionXY.setDuration(2000);

        motionXY.start();

    }

    //Attempting to login after onClick
    public void loginUser(String username, String password) {
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

    //Navigate to MainActivity
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    //Navigate to CreateUserActivity
    private void goCreateUserActivity() {
        Intent i = new Intent(this, CreateUserActivity.class);
        startActivity(i);
        finish();
    }
}