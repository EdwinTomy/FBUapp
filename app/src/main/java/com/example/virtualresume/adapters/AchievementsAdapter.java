package com.example.virtualresume.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.virtualresume.R;
import com.example.virtualresume.activities.DetailedViewActivity;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {

    public static final String TAG = "AchievementsAdapter";
    private Context context;
    private List<Achievement> achievements;

    public AchievementsAdapter(Context context, List<Achievement> achievements) {
        this.context = context;
        this.achievements = achievements;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_achievement, parent, false);
        return new ViewHolder(view);
    }

    //Binds view into a specific position
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Achievement achievement = achievements.get(position);
        holder.bind(achievement);
    }

    //Return total posts count
    @Override
    public int getItemCount() {
        return achievements.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userProfileImage;
        private ImageView achievementImage;
        private TextView userFullName;
        private TextView userUsername;
        private TextView achievementTitle;
        private TextView achievementTimePassed;

        public ViewHolder(@NonNull View itemView) {
            super((itemView));
            userProfileImage = itemView.findViewById(R.id.ivProfileImage);
            userFullName = itemView.findViewById(R.id.etFullName);
            achievementImage = itemView.findViewById(R.id.image);
            achievementTitle = itemView.findViewById(R.id.etAchivementSum);
            userUsername = itemView.findViewById(R.id.etTagUser);
            achievementTimePassed = itemView.findViewById(R.id.etTimePassed);
            itemView.setOnClickListener(this);
        }

        //Bind achievement data into view elements
        public void bind(Achievement achievement) {
            ParseFile picture = achievement.getAchievementImage();
            if (picture != null) {
                Glide.with(context).load(achievement.getAchievementImage().getUrl()).into(achievementImage);
            }else{
                achievementImage.setVisibility(View.GONE);
            }

            ParseFile profile = achievement.getAchievementUser().getParseFile(User.USER_KEY_PROFILEIMAGE);
            if (profile != null) {
                Glide.with(context).load(profile.getUrl()).into(userProfileImage);
                Log.i(TAG, "Profile Image loaded");
            }

            userFullName.setText(achievement.getAchievementUser().getString(User.USER_KEY_FULLNAME));
            userUsername.setText("@" + achievement.getAchievementUser().getString(User.USER_KEY_USERNAME));
            achievementTimePassed.setText(getRelativeTimeAgo(achievement.getCreatedAt()));
            achievementTitle.setText(achievement.getAchievementTitle());
        }

        //When post clicked, details appear
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                Achievement achievement = achievements.get(position);
                goToActivity(achievement);
            }
        }
    }

    public void goToActivity(Achievement achievement) {
        Intent intent = new Intent(context, DetailedViewActivity.class);
        //Serialize the movie with parser
        intent.putExtra(Achievement.class.getSimpleName(), Parcels.wrap(achievement));//show activity
        context.startActivity(intent);
    }

    // Clean all elements of the recycler
    public void clear() {
        achievements.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Achievement> list) {
        achievements.addAll(list);
        notifyDataSetChanged();
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