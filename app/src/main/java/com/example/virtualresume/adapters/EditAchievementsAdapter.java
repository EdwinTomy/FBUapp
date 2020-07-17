package com.example.virtualresume.adapters;

import android.content.Context;
import android.content.Intent;

import com.example.virtualresume.activities.CreateAchievement;
import com.example.virtualresume.activities.DetailedViewActivity;
import com.example.virtualresume.models.Achievement;

import org.parceler.Parcels;

import java.util.List;

public class EditAchievementsAdapter extends AchievementsAdapter {

    private Context context;
    private List<Achievement> achievements;

    public EditAchievementsAdapter(Context context, List<Achievement> achievements) {
        super(context, achievements);
        this.context = context;
        this.achievements = achievements;
    }

    @Override
    public void goToActivity(Achievement achievement) {
        //Create intent for new activity
        Intent intent = new Intent(context, CreateAchievement.class);
        //Serialize the movie with parser
        intent.putExtra(Achievement.class.getSimpleName(), Parcels.wrap(achievement));
        //show activity
        context.startActivity(intent);
    }
}
