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
import com.example.virtualresume.activities.ContactProfileActivity;
import com.example.virtualresume.models.Following;
import com.example.virtualresume.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG = "UsersAdapter";
    private Context context;
    private List<Following> followings;

    public UsersAdapter(Context context, List<Following> followings) {
        this.context = context;
        this.followings = followings;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Following following = followings.get(position);
        holder.bind(following);
    }

    //Return total posts count
    @Override
    public int getItemCount() {
        return followings.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView profileImage;
        private TextView fullName;
        private TextView username;

        public ViewHolder(@NonNull View itemView) {
            super((itemView));
            profileImage = itemView.findViewById(R.id.profileImage);
            fullName = itemView.findViewById(R.id.fullName);
            username = itemView.findViewById(R.id.username);
            itemView.setOnClickListener(this);
        }

        public void bind(Following following) {
            //Bind achievement data into view elements
            ParseFile profile = following.getFollowingUser().getParseFile("profileImage");
            if (profile != null) {
                Glide.with(context).load(profile.getUrl()).into(profileImage);
                Log.i(TAG, "Profile Image loaded");
            }

            fullName.setText(following.getFollowingUser().getString("fullName"));
            username.setText(following.getFollowingUser().getUsername());
        }

        //When post clicked, details appear
        @Override
        public void onClick(View view) {
            //item position
            int position = getAdapterPosition();
            //Validity of position
            if(position != RecyclerView.NO_POSITION){
                Following following = followings.get(position);
                goToActivity(following);
            }
        }
    }

    public void goToActivity(Following following) {
        //Create intent for new activity
        Intent intent = new Intent(context, ContactProfileActivity.class);
        //Serialize the movie with parser
        intent.putExtra(Following.class.getSimpleName(), Parcels.wrap(following));//show activity
        context.startActivity(intent);
    }

    // Clean all elements of the recycler
    public void clear() {
        followings.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Following> list) {
        followings.addAll(list);
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
