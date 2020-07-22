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
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class ContactProfileActivity extends AppCompatActivity {

    final private static String TAG = "ContactProfileActivity";

    private ImageView profileImage;
    private TextView home;
    private TextView username;
    private TextView bio;
    private TextView fullName;

    private ParseUser user;
    private RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected AchievementsAdapter adapter;
    protected List<Achievement> allAchievements;
    final protected int POST_LIMIT = 20;
    protected int postsLimit = 20;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_view);

        rvPosts = findViewById(R.id.rvPosts);

        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Unwrapping User
        user = Parcels.unwrap(getIntent().getParcelableExtra(ParseUser.class.getSimpleName()));

        profileImage = findViewById(R.id.profileImage);
        fullName = findViewById(R.id.fullName);
        username = findViewById(R.id.username);
        bio = findViewById(R.id.bio);
        home = findViewById(R.id.home);

        //Profile picture of user
        ParseFile profile = User.getCurrentUser().getParseFile("profileImage");
        if (profile != null) {
            Glide.with(this).load(profile.getUrl()).into(profileImage);
            Log.i(TAG, "Profile Image loaded");
        }

        //Name, username and bio of user
        fullName.setText(user.getString("fullName"));
        bio.setText(user.getString("bio"));
        username.setText(user.getString("username"));
        if (user.getParseFile("profileImage") != null)
            Glide.with(this).load(user.getParseFile("profileImage").getUrl()).into(profileImage);

        //Create adapter and data source
        allAchievements = new ArrayList<>();
        adapter = new AchievementsAdapter(this, allAchievements);
        //Create layout for one row in the list
        //Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        //Set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        queryPosts(POST_LIMIT);
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
                queryPosts(POST_LIMIT);
                postsLimit = POST_LIMIT;
            }
        });
    }

    //Retrieving ParseObjects (achievements) of current user
    protected void queryPosts(int postLimit) {

        //Object to be queried (Post)
        ParseQuery<Achievement> query = ParseQuery.getQuery(Achievement.class);
        query.include(Achievement.KEY_USER);
        query.whereEqualTo(Achievement.KEY_USER, user);
        query.setLimit(postsLimit);
        query.addDescendingOrder(Achievement.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                // Access data using the getter methods for the object
                for(Achievement achievement : achievements){
                    Log.i(TAG, "Post: " + achievement.getDescription() + ", user: " + achievement.getUser().getUsername());
                }
                allAchievements.clear();
                allAchievements.addAll(achievements);
                adapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}