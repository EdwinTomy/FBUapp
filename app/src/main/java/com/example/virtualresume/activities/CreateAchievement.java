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
import com.example.virtualresume.databinding.ActivityCreateAchievementBinding;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.CameraApplication;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;

public class CreateAchievement extends CameraApplication {

    private static final String TAG = "Create Achievement";

    private ImageView achievementImage;
    private Achievement achievement;
    private Button btnEditAchievement;
    private Button btnDeleteAchievement;
    private Button btnPicture;
    private EditText achievementTitle;
    private EditText achievementField;
    private EditText achievementDescription;
    private EditText achievementOrganization;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_achievement);

        ActivityCreateAchievementBinding binding = ActivityCreateAchievementBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Unwrapping class
        achievement = (Achievement) Parcels.unwrap(getIntent().getParcelableExtra(Achievement.class.getSimpleName()));

        //Binding attributes
        achievementImage = view.findViewById(R.id.ivAchievement);
        btnEditAchievement = view.findViewById(R.id.btnEditAchievement);
        btnDeleteAchievement = view.findViewById(R.id.btnDeleteAchievement);
        btnPicture = view.findViewById(R.id.btnPicture);
        achievementTitle = view.findViewById(R.id.etTitle);
        achievementField = view.findViewById(R.id.etField);
        achievementDescription = view.findViewById(R.id.etDescription);
        achievementOrganization = view.findViewById(R.id.etOrganization);

        //Setting up template for existing achievement
        if(achievement != null)
            existingAchievementTemplate();
        else {
            btnDeleteAchievement.setEnabled(false);
            btnDeleteAchievement.setVisibility(View.GONE);
        }

        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                photoFile = launchCamera(achievementImage, photoFile);
            }
        });

        //Set quick listener on button
        btnEditAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtaining input data
                String titleContent = achievementTitle.getText().toString();
                if(titleContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the title is empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                String fieldContent = achievementField.getText().toString();
                if(fieldContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the field is empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                String descriptionContent = achievementDescription.getText().toString();
                if(descriptionContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the description is empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                String organizationContent = achievementOrganization.getText().toString();
                if(organizationContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the organization is empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                //Creating or editing achievement
                if(achievement == null){
                    newAchievement(titleContent, fieldContent, descriptionContent, organizationContent);
                    return;
                }
                editAchievement(titleContent, fieldContent, descriptionContent, organizationContent);
            }
        });

        //Delete achievement
        btnDeleteAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                achievement.deleteInBackground(new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        finish();
                    }
                });
                finish();
            }
        });
    }

    //Setting up template for existing achievement
    public void existingAchievementTemplate(){
        achievementTitle.setText(achievement.getAchievementTitle());
        achievementField.setText(achievement.getAchievementField());
        achievementDescription.setText(achievement.getAchievementDescription());
        achievementOrganization.setText(achievement.getAchievementOrganization());
        Log.d(TAG, String.format("Showing details for '%s:'", achievement.getAchievementTitle()));
        ParseFile picture = achievement.getAchievementImage();
        if (picture != null) {
            Glide.with(this).load(achievement.getAchievementImage().getUrl()).into(achievementImage);
        }
    }

    //Creating new achievement
    public void newAchievement(String titleContent, String fieldContent, String descriptionContent, String organizationContent){
        Achievement achievement = new Achievement();
        achievement.setAchievementUser(User.getCurrentUser());
        Log.i(TAG, "new achievement");
        achievement.setAchievementTitle(titleContent);
        achievement.setAchievementField(fieldContent);
        achievement.setAchievementDescription(descriptionContent);
        achievement.setAchievementOrganization(organizationContent);
        if(photoFile != null)
            achievement.setAchievementImage(new ParseFile(photoFile));
        achievement.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
        finish();
    }

    //Editing existing achievement
    public void editAchievement(String titleContent, String fieldContent, String descriptionContent, String organizationContent){
        achievement.setAchievementTitle(titleContent);
        achievement.setAchievementField(fieldContent);
        achievement.setAchievementDescription(descriptionContent);
        achievement.setAchievementOrganization(organizationContent);
        if(photoFile != null)
            achievement.setAchievementImage(new ParseFile(photoFile));
        achievement.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                finish();
            }
        });
        finish();
    }
}