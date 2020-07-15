package com.example.virtualresume.adapters;

import android.content.Context;
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
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.virtualresume.R;
import com.example.virtualresume.models.Achievement;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {

    public static final String TAG = "PostsAdapter";
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

        private ImageView profileImage;
        private TextView fullName;
        private ImageView image;
        private TextView title;
        private TextView timePassed;

        public ViewHolder(@NonNull View itemView) {
            super((itemView));
            profileImage = itemView.findViewById(R.id.profileImage);
            fullName = itemView.findViewById(R.id.fullName);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            //itemView.setOnClickListener(this);
            //visibleChange();
        }

        public void bind(Achievement achievement) {
            //Bind post data into view elements
            ParseFile image = achievement.getImage();
            if (image != null) {
                Glide.with(context).load(achievement.getImage().getUrl()).into(image);
            }
            String firstName = achievement.getUser().getU
            fullName.setText(achievement.getUser().getUsername());
            tvDescription.setText(achievement.getDescription());
            tvTime.setText(getRelativeTimeAgo(achievement.getCreatedAt()));
        }

        //When post clicked, details appear
        @Override
        public void onClick(View view) {
            //item position
            int position = getAdapterPosition();
            //Validity of position
            if(position != RecyclerView.NO_POSITION){
                //Get movie at position
                Achievement post = achievements.get(position);
                visibleChange();
            }
        }

        //Changes visibility of description and time stamp
        public void visibleChange(){
            if(tvDescription.getVisibility() == View.VISIBLE){
                tvDescription.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                Log.i(TAG, "invisible");
            }else{
                tvDescription.setVisibility(View.VISIBLE);
                tvTime.setVisibility(View.VISIBLE);
                Log.i(TAG, "again visible");
            }
        }
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

        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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