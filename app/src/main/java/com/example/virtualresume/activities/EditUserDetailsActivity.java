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
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.parceler.Parcels;

import java.io.File;

import static com.parse.ParseUser.getCurrentUser;

public class EditUserDetailsActivity extends AppCompatActivity {

    public static final String TAG = "EditUserDetailsActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 40;

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText bioInput;
    private ImageView picture;
    private Button btnPicture;
    private Button btnEditProfile;
    private User user;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        bioInput = findViewById(R.id.bioInput);
        picture = findViewById(R.id.picture);
        btnPicture = findViewById(R.id.btnPicture);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        //user = Parcels.unwrap(getIntent().getParcelableExtra(User.class.getSimpleName()));
        firstNameInput.setText(User.getCurrentUser().getString("firstName"));
        lastNameInput.setText(User.getCurrentUser().getString("lastName"));
        bioInput.setText(User.getCurrentUser().getString("bio"));

        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                launchCamera();
            }
        });

        //Clicking to create new user
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

    //Update after onClick
    private void UpdateDetails(String lastName, String firstName, String bio, File photoFile) {
        ParseUser user = ParseUser.getCurrentUser();
        Log.i(TAG, "Updating details of" + firstName);

        //Update properties
        user.put("fullName", firstName + " " + lastName);
        user.put("bio", bio);
        if(photoFile != null)
            user.put("profileImage", new ParseFile(photoFile));
        //Invoke signUpInBackground
        user.saveInBackground();
    }

    //Navigate to phone's inbuilt camera
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(EditUserDetailsActivity.this, "com.virtualresume.fileprovider", photoFile);
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