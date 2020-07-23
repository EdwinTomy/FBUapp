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
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.File;

public class EditUserDetailsActivity extends CameraApplication {

    public static final String TAG = "EditUserDetailsActivity";

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText bioInput;
    private ImageView pictureInput;
    private Button btnPicture;
    private Button btnEditProfile;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        bindUserDetails();

        //Clicking to update user details
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String firstName = firstNameInput.getText().toString();
                String lastName = lastNameInput.getText().toString();
                String bio = bioInput.getText().toString();

                if(firstName.isEmpty()){
                    Toast.makeText(EditUserDetailsActivity.this, "Enter first name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(lastName.isEmpty()){
                    Toast.makeText(EditUserDetailsActivity.this, "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                UpdateDetails(lastName, firstName, bio, photoFile);
            }
        });

    }

    //Posting user details
    private void bindUserDetails() {
        firstNameInput = findViewById(R.id.etFirstName);
        lastNameInput = findViewById(R.id.etLastName);
        bioInput = findViewById(R.id.etBio);
        pictureInput = findViewById(R.id.ivProfileImage);
        btnPicture = findViewById(R.id.btnPicture);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        firstNameInput.setText(User.getCurrentUser().getString("firstName"));
        lastNameInput.setText(User.getCurrentUser().getString("lastName"));
        bioInput.setText(User.getCurrentUser().getString("bio"));

        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                photoFile = launchCamera(pictureInput, photoFile);
            }
        });
    }

    //Update after onClick
    private void UpdateDetails(String lastName, String firstName, String bio, File photoFile) {
        ParseUser user = ParseUser.getCurrentUser();
        Log.i(TAG, "Updating details of" + firstName);

        //Update properties
        user.put(User.USER_KEY_FULLNAME, firstName + " " + lastName);
        user.put(User.USER_KEY_BIO, bio);
        if(photoFile != null)
            user.put(User.USER_KEY_PROFILEIMAGE, new ParseFile(photoFile));
        //Invoke signUpInBackground
        user.saveInBackground();
    }

    //Navigate to MainActivity
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}