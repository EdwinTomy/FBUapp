package com.example.virtualresume.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.virtualresume.R;
import com.example.virtualresume.models.User;
import com.parse.ParseException;
import com.parse.SignUpCallback;

import java.io.File;

public class CreateUserActivity extends AppCompatActivity {

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
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        bioInput = findViewById(R.id.bioInput);
        picture = findViewById(R.id.picture);
        btnPicture = findViewById(R.id.btnPicture);
        btnCreate = findViewById(R.id.btnEditProfile);


        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                launchCamera();
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
                ImageView picture = picture.getImage

                if(firstName.isEmpty()){
                    Toast.makeText(CreateUserActivity.this, "Enter first name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(lastName.isEmpty()){
                    Toast.makeText(CreateUserActivity.this, "Enter last name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                SignUpUser(username, password, lastName, firstName, bio);
            }
        });

    }

    //Attempting to sign up after onClick
    private void SignUpUser(String username, String password, String lastName, String firstName, String bio) {
        Log.i(TAG, "Attempting to sign up user:" + username);
        //Create the ParseUser
        User user = new User();
        //Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setBio(bio);
        //Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    //Navigate to MainActivity
                    Toast.makeText(CreateUserActivity.this, "Success in sign up!", Toast.LENGTH_SHORT).show();
                    goMainActivity();
                    return;
                }
                Toast.makeText(CreateUserActivity.this, "Enter a password and unique username!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Issue with sign up", e);
                return;
            }
        });
    }

    //Navigate to phone's inbuilt camera
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(CreateUserActivity.this, "com.virtualresume.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                picture.setImageBitmap(takenImage);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    //Navigate to MainActivity
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}