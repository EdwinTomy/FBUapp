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

import com.example.virtualresume.R;
import com.example.virtualresume.models.Achievement;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsfeedFragment extends Fragment {

    final private static String TAG = "NewsfeedFragment";

    private RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    //protected PostsAdapter adapter;
    protected List<Achievement> allAchievements;
    final protected int POST_LIMIT = 20;
    protected int postsLimit = 20;
    //protected EndlessRecyclerViewScrollListener scrollListener;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    public NewsfeedFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);

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
        adapter = new PostsAdapter(getContext(), allAchievements);
        //Create layout for one row in the list
        //Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        //Set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts(POST_LIMIT);

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

    //Retrieving ParseObjects (posts)
    protected void queryPosts(int postsLimit) {

        //Object to be queried (Post)
        ParseQuery<Achievement> query = ParseQuery.getQuery(Achievement.class);
        query.include(Achievement.KEY_USER);
        query.setLimit(postsLimit);
        query.addDescendingOrder(Achievement.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with getting achievements", e);
                    return;
                }
                // Access data using the getter methods for the object
                for(Achievement achievement : achievements){
                    Log.i(TAG, "Post: " + achievement.getDescription() + ", user: " + achievement.getUser().getUsername());
                }
                adapter.clear();
                allAchievements.addAll(achievements);
                adapter.notifyDataSetChanged();
            }
        });
        swipeContainer.setRefreshing(false);
    }
}