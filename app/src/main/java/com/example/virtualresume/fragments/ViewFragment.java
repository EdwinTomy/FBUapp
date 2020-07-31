package com.example.virtualresume.fragments;

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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    public static final String TAG = "ViewFragment";

    private ImageView userProfileImage;
    private TextView userHome;
    private TextView userUsername;
    private TextView userBio;
    private TextView userFullName;

    private RecyclerView rvUserAchievements;
    protected SwipeRefreshLayout swipeContainer;
    protected AchievementsAdapter userAchievementsAdapter;
    protected List<Achievement> allUserAchievements;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    public ViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Filling the RecyclerView with the query of user Achievements
        settingRecyclerView(view);
        queryUserAchievements();

        //Binding user details
        bindUserDetails(view);
    }

    //Posting user details
    private void bindUserDetails(View view) {
        userProfileImage = view.findViewById(R.id.ivProfileImage);
        userFullName = view.findViewById(R.id.etFullName);
        userUsername = view.findViewById(R.id.etUsername);
        userBio = view.findViewById(R.id.bio);
        userHome = view.findViewById(R.id.etHome);


        //Profile image of user
        ParseFile profile = User.getCurrentUser().getParseFile(User.USER_KEY_PROFILEIMAGE);
        if (profile != null) {
            Glide.with(getContext()).load(profile.getUrl()).into(userProfileImage);
            Log.i(TAG, "Profile Image loaded");
        }
        //Full name, username and bio of user
        userFullName.setText(User.getCurrentUser().getString(User.USER_KEY_FULLNAME));
        userBio.setText(User.getCurrentUser().getString(User.USER_KEY_BIO));
        userUsername.setText("@" + User.getCurrentUser().getUsername());
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
                Log.i(TAG, "Loading in user achievements");
                queryUserAchievements();
            }
        });
    }

    //Setting the RecyclerView
    protected void settingRecyclerView(@NonNull View view){
        rvUserAchievements = view.findViewById(R.id.rvPosts);
        allUserAchievements = new ArrayList<>();
        userAchievementsAdapter = new AchievementsAdapter(getContext(), allUserAchievements);
        rvUserAchievements.setAdapter(userAchievementsAdapter);
        rvUserAchievements.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Retrieving achievements of the current user
    protected void queryUserAchievements() {
        //Creating and constraining query
        ParseQuery<Achievement> query = ParseQuery.getQuery(Achievement.class);
        query.include(Achievement.ACHIEVEMENT_KEY_USER);
        query.whereEqualTo(Achievement.ACHIEVEMENT_KEY_USER, User.getCurrentUser());
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
                allUserAchievements.clear();
                allUserAchievements.addAll(achievements);
                userAchievementsAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }
}