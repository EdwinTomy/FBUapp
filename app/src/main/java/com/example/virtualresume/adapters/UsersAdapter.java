package com.example.virtualresume.adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.virtualresume.models.User;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import org.parceler.Parcels;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    public static final String TAG = "UsersAdapter";
    private Context context;
    private List<ParseObject> users;
    final private double EARTH_RADIUS = 6371.8;

    public UsersAdapter(Context context, List<ParseObject> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new UsersAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParseObject user = users.get(position);
        holder.bind(user);
    }

    //Return total posts count
    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userProfileImage;
        private TextView userFullName;
        private TextView userUsername;
        private TextView userDistanceFrom;

        public ViewHolder(@NonNull View itemView) {
            super((itemView));
            userProfileImage = itemView.findViewById(R.id.ivProfileImage);
            userFullName = itemView.findViewById(R.id.etFullName);
            userUsername = itemView.findViewById(R.id.etUsername);
            userDistanceFrom = itemView.findViewById(R.id.etDistanceFrom);
            itemView.setOnClickListener(this);
        }

        //Bind achievement data into view elements
        public void bind(ParseObject user) {
            ParseFile profile = user.getParseFile(User.USER_KEY_PROFILEIMAGE);
            if (profile != null) {
                Glide.with(context).load(profile.getUrl()).into(userProfileImage);
                Log.i(TAG, "Profile Image loaded");
            }
            userFullName.setText(user.getString(User.USER_KEY_FULLNAME));
            userUsername.setText(user.getString(User.USER_KEY_USERNAME));
            ParseGeoPoint userHome = User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME);
            ParseGeoPoint contactHome = user.getParseGeoPoint(User.USER_KEY_HOME);
            double distance = calculateDistanceKilometer(userHome, contactHome);
            userDistanceFrom.setText(distanceToString(distance));
        }

        //ProfileView when contact clicked
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION){
                ParseObject user = users.get(position);
                onClickAction(user);
            }
        }
    }

    public void onClickAction(ParseObject user) {
        Intent intent = new Intent(context, ContactProfileActivity.class);
        //Serialize the movie with parser
        intent.putExtra(ParseObject.class.getSimpleName(), Parcels.wrap(user));//show activity
        context.startActivity(intent);
    }

    //Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    //Add a list of items -- change to type used
    public void addAll(List<User> list) {
        users.addAll(list);
        notifyDataSetChanged();
    }

    //Below is part of the complex algorithm

    //Calculate distances between two ParseGeoPoints
    protected double calculateDistanceKilometer(ParseGeoPoint userHome, ParseGeoPoint contactHome){

        double userHomeLatitudeRad = Math.toRadians(userHome.getLatitude());
        Log.i("Distance", String.valueOf(userHomeLatitudeRad));
        double userHomeLongitudeRad = Math.toRadians(userHome.getLongitude());
        Log.i("Distance", String.valueOf(userHomeLongitudeRad));
        double contactHomeLatitudeRad = Math.toRadians(contactHome.getLatitude());
        Log.i("Distance", String.valueOf(contactHomeLatitudeRad));
        double contactHomeLongitudeRad = Math.toRadians(contactHome.getLongitude());
        Log.i("Distance", String.valueOf(contactHomeLongitudeRad));

        double latitudeDistance = contactHomeLatitudeRad - userHomeLatitudeRad;
        double longitudeDistance = contactHomeLongitudeRad - userHomeLongitudeRad;

        //Haversine formula
        double distance = Math.pow(Math.sin(latitudeDistance / 2), 2)
                + Math.cos(userHomeLatitudeRad)
                * Math.cos(contactHomeLatitudeRad)
                * Math.pow(Math.sin(longitudeDistance / 2), 2);
        distance = 2 * Math.asin(Math.sqrt(distance)) * EARTH_RADIUS;

        Log.i("Distance", String.valueOf(distance));

        return distance;
    }

    //Convert double distance into whole kilometers
    protected String distanceToString(Double distance){

        int distanceToInt = (int) Math.round(distance);
        if(distanceToInt == 0)
            return "Current location";
        return distanceToInt + " km.";
    }
}
