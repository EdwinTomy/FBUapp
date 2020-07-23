package com.example.virtualresume.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.virtualresume.R;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.CameraApplication;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SignUpCallback;

import java.io.File;

public class CreateUserActivity extends CameraApplication {

    public static final String TAG = "CreateUserActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 40;

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText bioInput;
    private ImageView picture;
    private Button btnPicture;
    private Button btnCreate;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        firstNameInput = findViewById(R.id.etFullName);
        lastNameInput = findViewById(R.id.etLastName);
        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        bioInput = findViewById(R.id.etBio);
        picture = findViewById(R.id.ivProfileImage);
        btnPicture = findViewById(R.id.btnPicture);
        btnCreate = findViewById(R.id.btnEditProfile);


        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                photoFile = launchCamera(picture, photoFile);
            }
        });

        //Clicking to create new user
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameInput.getText().toString();
                String username = usernameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String password = passwordInput.getText().toString();
                String bio = bioInput.getText().toString();

                if(firstName.isEmpty()){
                    Toast.makeText(CreateUserActivity.this, "Enter first name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(lastName.isEmpty()){
                    Toast.makeText(CreateUserActivity.this, "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SignUpUser(username, password, lastName, firstName, bio, photoFile);
            }
        });

    }

    //Attempting to sign up after onClick
    private void SignUpUser(String username, String password, String lastName, String firstName, String bio, File photoFile) {
        Log.i(TAG, "Attempting to sign up user:" + username);
        final User user = new User();
        //Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setUserFullName(firstName + " " + lastName);
        user.setUserBio(bio);
        user.saveInBackground();
        //Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    //Navigate to MainActivity
                    Toast.makeText(CreateUserActivity.this, "Success in sign up!", Toast.LENGTH_SHORT).show();
                    savePhoto(user);
                    return;
                }
                Toast.makeText(CreateUserActivity.this, "Enter a password and unique username!", Toast.LENGTH_SHORT).show();
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

    //Save Photo
    private void savePhoto(User user) {
        if(photoFile != null) {
            ParseFile file = new ParseFile(photoFile);
            user.setUserProfileImage(file);
            user.saveInBackground();
            Log.i(TAG, "File not null");
            Toast.makeText(CreateUserActivity.this, "Profile loaded", Toast.LENGTH_SHORT).show();
            goMainActivity();
        }
    }
}