package com.example.virtualresume.fragments;

import android.app.Activity;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewFragment extends Fragment {

    public static final String TAG = "ViewFragment";

    private ImageView profileImage;
    private TextView home;
    private TextView username;
    private TextView bio;
    private TextView fullName;

    private RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected AchievementsAdapter adapter;
    protected List<Achievement> allAchievements;
    final protected int POST_LIMIT = 20;
    protected int postsLimit = 20;
    //protected EndlessRecyclerViewScrollListener scrollListener;
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
        rvPosts = view.findViewById(R.id.rvPosts);

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Create adapter and data source
        allAchievements = new ArrayList<>();
        adapter = new AchievementsAdapter(getContext(), allAchievements);
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



        ParseFile profile = ParseUser.getCurrentUser().getParseFile("profileImage");
        if (profile != null) {
            Glide.with(getContext()).load(profile.getUrl()).into(profileImage);
            Log.i(TAG, "Profile Image loaded");
        }

        String firstName = ParseUser.getCurrentUser().get("firstName").toString();
        String lastName = ParseUser.getCurrentUser().get("lastName").toString();
        fullName.setText(firstName + " " + lastName);
        bio.setText(ParseUser.getCurrentUser().getString("bio"));
        username.setText(ParseUser.getCurrentUser().getString("username"));

        /*

        //Endless scrolling
        rvPosts.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                postsLimit += 10;
                queryPosts(postsLimit);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPosts.addOnScrollListener(scrollListener);


         */
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
        query.whereEqualTo(Achievement.KEY_USER, ParseUser.getCurrentUser());
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