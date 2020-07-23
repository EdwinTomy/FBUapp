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
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.CameraApplication;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateAchievement extends CameraApplication {

    private static final String TAG = "Create Achievement";

    private ImageView imageAchievement;
    private Achievement achievement;
    private Button btnEditAchievement;
    private Button btnPicture;
    private EditText title;
    private EditText field;
    private EditText description;
    private EditText organization;
    private File photoFile;

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

        //For existing achievements, setting up the data
        if(achievement != null) {
            title.setText(achievement.getTitle());
            field.setText(achievement.getField());
            description.setText(achievement.getDescription());
            organization.setText(achievement.getOrganization());
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
                photoFile = launchCamera(imageAchievement, photoFile);
            }
        });

        //Set quick listener on button
        btnEditAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtaining input data
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

                //Creating new achievement
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
                    achievement.saveInBackground();
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