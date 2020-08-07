package com.example.virtualresume.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.virtualresume.R;
import com.example.virtualresume.adapters.AchievementsAdapter;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ContactProfileActivity extends AppCompatActivity {

    final private static String TAG = "ContactProfileActivity";

    private ImageView userProfileImage;
    private TextView userHome;
    private TextView userUsername;
    private TextView userBio;
    private TextView userFullName;

    private ParseUser user;
    private RecyclerView rvContactAchievements;
    protected SwipeRefreshLayout swipeContainer;
    protected AchievementsAdapter contactAchievementsAdapter;
    protected List<Achievement> allContactAchievements;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view);

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Filling the RecyclerView with the query of contact achievements
        settingRecyclerView();

        //Binding contact details
        bindContactDetails();
        queryContactAchievements();
    }

    //Posting contact details
    private void bindContactDetails() {
        //Unwrapping User
        user = Parcels.unwrap(getIntent().getParcelableExtra(ParseObject.class.getSimpleName()));

        userProfileImage = findViewById(R.id.ivProfileImage);
        userFullName = findViewById(R.id.etFullName);
        userUsername = findViewById(R.id.etUsername);
        userBio = findViewById(R.id.bio);
        userHome = findViewById(R.id.etHome);

        //Profile picture of contact
        ParseFile profile = user.getParseFile(User.USER_KEY_PROFILEIMAGE);
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).into(userProfileImage);
            Log.i(TAG, "Profile Image loaded");
        }

        //Name, username and bio of user
        userFullName.setText(user.getString(User.USER_KEY_FULLNAME));
        userBio.setText(user.getString(User.USER_KEY_BIO));
        userUsername.setText("@" + user.getString(User.USER_KEY_USERNAME));
        userHome.setText(user.getString(User.USER_KEY_ADDRESS));
    }

    //Configuring the container
    public void setupPullToRefresh(SwipeRefreshLayout swipeContainer){
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Loading in");
                queryContactAchievements();

            }
        });
    }

    //Setting the RecyclerView
    protected void settingRecyclerView(){
        rvContactAchievements = findViewById(R.id.rvPosts);
        allContactAchievements = new ArrayList<>();
        contactAchievementsAdapter = new AchievementsAdapter(this, allContactAchievements);
        rvContactAchievements.setAdapter(contactAchievementsAdapter);
        rvContactAchievements.setLayoutManager(new LinearLayoutManager(this));
    }

    //Retrieving achievements of the contact
    protected void queryContactAchievements() {
        //Creating and constraining query
        ParseQuery<Achievement> query = ParseQuery.getQuery(Achievement.class);
        query.include(Achievement.ACHIEVEMENT_KEY_USER);
        query.whereEqualTo(Achievement.ACHIEVEMENT_KEY_USER, user);
        query.addDescendingOrder(Achievement.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for(Achievement achievement : achievements){
                    Log.i(TAG, "Achievement: " + achievement.getAchievementTitle() + ", from user: " + achievement.getAchievementUser().getUsername());
                }
                //Set the adapter with new list of achievements
                allContactAchievements.clear();
                allContactAchievements.addAll(achievements);
                contactAchievementsAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}
