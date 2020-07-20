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
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.virtualresume.R;
import com.example.virtualresume.databinding.ActivityCreateAchievementBinding;
import com.example.virtualresume.databinding.ActivityDetailedViewBinding;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateAchievement extends AppCompatActivity {

    private static final String TAG = "Create Achievement";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 40;

    private ImageView imageAchievement;
    private Achievement achievement;
    private Button btnEditAchievement;
    private Button btnPicture;
    private EditText title;
    private EditText field;
    private EditText description;
    private EditText organization;
    private EditText timeOf;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_achievement);

        ActivityCreateAchievementBinding binding = ActivityCreateAchievementBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        achievement = (Achievement) Parcels.unwrap(getIntent().getParcelableExtra(Achievement.class.getSimpleName()));

        //Bind attributes
        imageAchievement = view.findViewById(R.id.imageAchievement);
        btnEditAchievement = view.findViewById(R.id.btnEditAchievement);
        btnPicture = view.findViewById(R.id.btnPicture);
        title = view.findViewById(R.id.title);
        field = view.findViewById(R.id.field);
        description = view.findViewById(R.id.description);
        organization = view.findViewById(R.id.organization);
        timeOf = view.findViewById(R.id.timeOf);

        if(achievement != null) {
            title.setText(achievement.getTitle());
            field.setText(achievement.getField());
            description.setText(achievement.getDescription());
            organization.setText(achievement.getOrganization());
            timeOf.setText(achievement.getTime().toString());
            Log.d(TAG, String.format("Showing details for '%s:'", achievement.getTitle()));
            ParseFile picture = achievement.getImage();
            if (picture != null) {
                Glide.with(this).load(achievement.getImage().getUrl()).into(imageAchievement);
            }else{
                imageAchievement.setVisibility(View.GONE);
            }
        }

        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                launchCamera();
            }
        });

        //Set quick listener on button
        btnEditAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String titleContent = title.getText().toString();
                if(titleContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the title is empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String fieldContent = field.getText().toString();
                if(fieldContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the field is empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String descriptionContent = description.getText().toString();
                if(descriptionContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the description is empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String organizationContent = organization.getText().toString();
                if(organizationContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the organization is empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                if(achievement == null){
                    Achievement achievement = new Achievement();
                    achievement.setUser(User.getCurrentUser());
                    Log.i(TAG, "new achievement");
                    achievement.setTitle(titleContent);
                    achievement.setField(fieldContent);
                    achievement.setDescription(descriptionContent);
                    achievement.setOrganization(organizationContent);
                    if(photoFile != null)
                        achievement.setImage(new ParseFile(photoFile));
                    achievement.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null){
                                Log.e(TAG, "Error while saving", e);
                                Toast.makeText(CreateAchievement.this, "Error while saving", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    });
                    return;
                }

                achievement.setTitle(titleContent);
                achievement.setField(fieldContent);
                achievement.setDescription(descriptionContent);
                achievement.setOrganization(organizationContent);
                if(photoFile != null)
                    achievement.setImage(new ParseFile(photoFile));
                achievement.saveInBackground();
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
        Uri fileProvider = FileProvider.getUriForFile(CreateAchievement.this, "com.virtualresume.fileprovider", photoFile);
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
                imageAchievement.setImageBitmap(takenImage);
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

    //Formatting time passed
    public String getRelativeTimeAgo(Date date) {

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String dateString = dateFormat.format(date);

        String relativeDate = "";
        try {
            long dateMillis = dateFormat.parse(dateString).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return relativeDate;
    }
}