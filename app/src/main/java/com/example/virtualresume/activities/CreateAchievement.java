package com.example.virtualresume.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateAchievement extends AppCompatActivity {

    private static final String TAG = "Create Achievement";
    private ImageView imageAchievement;
    private Achievement achievement;
    private Button btnEditAchievement;
    private EditText title;
    private EditText field;
    private EditText description;
    private EditText organization;
    private EditText timeOf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_achievement);

        ActivityCreateAchievementBinding binding = ActivityCreateAchievementBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        achievement = (Achievement) Parcels.unwrap(getIntent().getParcelableExtra(Achievement.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s:'", achievement.getTitle()));

        //Bind attributes
        imageAchievement = view.findViewById(R.id.imageAchievement);
        btnEditAchievement = view.findViewById(R.id.btnEditAchievement);
        title = view.findViewById(R.id.title);
        field = view.findViewById(R.id.field);
        description = view.findViewById(R.id.description);
        organization = view.findViewById(R.id.organization);
        timeOf = view.findViewById(R.id.timeOf);

        title.setText(achievement.getTitle());
        field.setText(achievement.getField());
        description.setText(achievement.getDescription());
        organization.setText(achievement.getOrganization());
        timeOf.setText(achievement.getTime().toString());

        ParseFile picture = achievement.getImage();
        if (picture != null) {
            Glide.with(this).load(achievement.getImage().getUrl()).into(imageAchievement);
        }else{
            imageAchievement.setVisibility(View.GONE);
        }

        //Set quick listener on button
        btnEditAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleContent = title.getText().toString();
                if(titleContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the title is empty!", Toast.LENGTH_LONG).show();
                    return;
                }achievement.setTitle(titleContent);

                String fieldContent = field.getText().toString();
                if(fieldContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the field is empty!", Toast.LENGTH_LONG).show();
                    return;
                }achievement.setField(fieldContent);

                String descriptionContent = description.getText().toString();
                if(descriptionContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the description is empty!", Toast.LENGTH_LONG).show();
                    return;
                }achievement.setDescription(descriptionContent);

                String organizationContent = organization.getText().toString();
                if(organizationContent.isEmpty()){
                    Toast.makeText(CreateAchievement.this, "Sorry, the organization is empty!", Toast.LENGTH_LONG).show();
                    return;
                }achievement.setOrganization(organizationContent);

                Toast.makeText(CreateAchievement.this, achievement.getTitle(), Toast.LENGTH_SHORT).show();
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