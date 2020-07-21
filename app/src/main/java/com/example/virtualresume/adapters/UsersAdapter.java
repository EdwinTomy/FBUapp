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
import com.example.virtualresume.activities.DetailedViewActivity;
import com.example.virtualresume.fragments.ViewFragment;
import com.example.virtualresume.models.Achievement;
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
    private List<ParseUser> users;

    public UsersAdapter(Context context, List<ParseUser> users) {
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
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    //Return total posts count
    @Override
    public int getItemCount() {
        return users.size();
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

        public void bind(ParseUser user) {
            //Bind achievement data into view elements
            ParseFile profile = user.getParseFile("profileImage");
            if (profile != null) {
                Glide.with(context).load(profile.getUrl()).into(profileImage);
                Log.i(TAG, "Profile Image loaded");
            }

            String firstName = user.getString("firstName");
            String lastName = user.getString("lastName");
            fullName.setText(firstName + " " + lastName);
            username.setText(user.getUsername());
        }

        //When post clicked, details appear
        @Override
        public void onClick(View view) {
            //item position
            int position = getAdapterPosition();
            //Validity of position
            if(position != RecyclerView.NO_POSITION){
                ParseUser user = users.get(position);
                goToActivity(user);
            }
        }
    }

    public void goToActivity(ParseUser user) {
        //Create intent for new activity
        Intent intent = new Intent(context, ContactProfileActivity.class);
        //Serialize the movie with parser
        intent.putExtra(ParseUser.class.getSimpleName(), Parcels.wrap(user));//show activity
        context.startActivity(intent);
    }

    // Clean all elements of the recycler
    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<User> list) {
        users.addAll(list);
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
