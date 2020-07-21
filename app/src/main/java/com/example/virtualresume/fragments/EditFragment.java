package com.example.virtualresume.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.virtualresume.R;
import com.example.virtualresume.activities.CreateAchievement;
import com.example.virtualresume.activities.EditUserDetailsActivity;
import com.example.virtualresume.adapters.AchievementsAdapter;
import com.example.virtualresume.adapters.EditAchievementsAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment extends Fragment {

    public static final String TAG = "EditFragment";

    private ImageView profileImage;
    private TextView home;
    private TextView username;
    private TextView bio;
    private TextView fullName;
    private Button btnEditProfile;
    private Button btnCreateAchievement;

    private RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected EditAchievementsAdapter adapter;
    protected List<Achievement> allAchievements;
    final protected int POST_LIMIT = 20;
    protected int postsLimit = 20;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    public EditFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);
        //Create adapter and data source
        allAchievements = new ArrayList<>();
        adapter = new EditAchievementsAdapter(getContext(), allAchievements);
        //Create layout for one row in the list
        //Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        //Set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts(POST_LIMIT);

        profileImage = view.findViewById(R.id.profileImage);
        fullName = view.findViewById(R.id.fullName);
        username = view.findViewById(R.id.username);
        bio = view.findViewById(R.id.bio);
        home = view.findViewById(R.id.home);

        btnCreateAchievement = view.findViewById(R.id.btnCreateAchievement);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        //Profile picture of user
        ParseFile profile = User.getCurrentUser().getParseFile("profileImage");
        if (profile != null) {
            Glide.with(getContext()).load(profile.getUrl()).into(profileImage);
            Log.i(TAG, "Profile Image loaded");
        }

        //Name, username and bio of user
        String firstName = User.getCurrentUser().getString("firstName");
        String lastName = User.getCurrentUser().getString("lastName");
        fullName.setText(firstName + " " + lastName);
        bio.setText(User.getCurrentUser().getString("bio"));
        username.setText(User.getCurrentUser().getString("username"));

        btnCreateAchievement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Achievement achievement = new Achievement();
                goToCreateAchievementActivity();
            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Achievement achievement = new Achievement();
                goToEditUserDetailsActivity(User.getCurrentUser());
            }
        });
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
        query.whereEqualTo(Achievement.KEY_USER, User.getCurrentUser());
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

    //Go to Create Achievement Screen
    public void goToCreateAchievementActivity() {
        //Create intent for new activity
        Intent intent = new Intent(getContext(), CreateAchievement.class);
        //show activity
        getContext().startActivity(intent);
    }

    //Go to Edit User Details Screen
    public void goToEditUserDetailsActivity(ParseUser user) {
        //Create intent for new activity
        Intent intent = new Intent(getContext(), EditUserDetailsActivity.class);
        //Serialize the achievement with parser
        intent.putExtra(User.class.getSimpleName(), Parcels.wrap(user));
        //show activity
        getContext().startActivity(intent);
    }
}