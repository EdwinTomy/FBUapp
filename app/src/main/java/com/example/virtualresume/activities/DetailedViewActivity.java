package com.example.virtualresume.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.virtualresume.R;
import com.example.virtualresume.databinding.ActivityDetailedViewBinding;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailedViewActivity extends AppCompatActivity {

    Achievement achievement;
    private static final String TAG = "DetailedView";
    private ImageView imageUser;
    private ImageView imageAchievement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);

        ActivityDetailedViewBinding binding = ActivityDetailedViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        imageUser = view.findViewById(R.id.imageUser);
        imageAchievement = view.findViewById(R.id.imageAchievement);

        achievement = (Achievement) Parcels.unwrap(getIntent().getParcelableExtra(Achievement.class.getSimpleName()));
        Log.d(TAG, String.format("Showing details for '%s:'", achievement.getAchievementTitle()));

        //Bind attributes
        binding.title.setText(achievement.getAchievementTitle());
        binding.field.setText(achievement.getAchievementField());
        binding.description.setText(achievement.getAchievementDescription());
        binding.fullName.setText(achievement.getAchievementUser().getString(User.USER_KEY_FULLNAME));
        binding.username.setText(achievement.getAchievementUser().getUsername());
        binding.timePassed.setText(getRelativeTimeAgo(achievement.getUpdatedAt()));
        binding.organization.setText(achievement.getAchievementOrganization());

        ParseFile picture = achievement.getAchievementImage();
        if (picture != null) {
            Glide.with(this).load(achievement.getAchievementImage().getUrl()).into(imageAchievement);
        }else{
            imageAchievement.setVisibility(View.GONE);
        }

        ParseFile profile = achievement.getAchievementUser().getParseFile(User.USER_KEY_PROFILEIMAGE);
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).into(imageUser);
            Log.i(TAG, "Profile Image loaded");
        }
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