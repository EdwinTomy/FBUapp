package com.example.virtualresume.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.virtualresume.R;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.CameraApplication;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class EditUserDetailsActivity extends CameraApplication {

    public static final String TAG = "EditUserDetailsActivity";

    private EditText fullNameInput;
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
                String fullName = fullNameInput.getText().toString();
                String bio = bioInput.getText().toString();

                if(fullName.isEmpty()){
                    Toast.makeText(EditUserDetailsActivity.this, "Enter full name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(bio.isEmpty()){
                    Toast.makeText(EditUserDetailsActivity.this, "Enter bio!", Toast.LENGTH_SHORT).show();
                    return;
                }

                UpdateDetails(fullName, bio, photoFile);
            }
        });

    }

    //Posting user details
    private void bindUserDetails() {
        fullNameInput = findViewById(R.id.etFullName);
        bioInput = findViewById(R.id.etBio);
        pictureInput = findViewById(R.id.ivProfileImage);
        btnPicture = findViewById(R.id.btnPicture);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        fullNameInput.setText(User.getCurrentUser().getString(User.USER_KEY_FULLNAME));
        bioInput.setText(User.getCurrentUser().getString(User.USER_KEY_BIO));
        ParseFile picture = User.getCurrentUser().getParseFile(User.USER_KEY_PROFILEIMAGE);
        if (picture != null)
            Glide.with(this).load(picture.getUrl()).into(pictureInput);

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
    private void UpdateDetails(String fullName, String bio, File photoFile) {
        ParseUser user = ParseUser.getCurrentUser();
        Log.i(TAG, "Updating details of" + fullName);

        //Update properties
        user.put(User.USER_KEY_FULLNAME, fullName);
        user.put(User.USER_KEY_BIO, bio);
        if(photoFile != null)
            user.put(User.USER_KEY_PROFILEIMAGE, new ParseFile(photoFile));
        //Invoke signUpInBackground
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
    }
}