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
import android.widget.Toast;

import com.example.virtualresume.R;
import com.example.virtualresume.adapters.AchievementsAdapter;
import com.example.virtualresume.adapters.UsersAdapter;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.EndlessRecyclerViewScrollListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsfeedFragment extends Fragment {

    final private static String TAG = "NewsfeedFragment";

    private RecyclerView rvContactsAchievements;
    protected SwipeRefreshLayout swipeContainer;
    protected AchievementsAdapter contactsAchievementsAdapter;
    protected List<Achievement> allContactsAchievements;
    protected UsersAdapter userContactsAdapter;
    protected List<ParseObject> allUserContacts;
    final protected int POST_LIMIT = 4;
    protected int postsLimit = 4;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
    private EndlessRecyclerViewScrollListener scrollListener;

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

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Filling the RecyclerView with the query of user Achievements
        settingRecyclerView(view);

        //Endless scrolling
        rvContactsAchievements.setLayoutManager(linearLayoutManager);
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "Loading in");
                postsLimit += 10;
                queryUserContacts();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvContactsAchievements.addOnScrollListener(scrollListener);

        queryUserContacts();
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
                Log.i(TAG, "Refreshing in");
                postsLimit = POST_LIMIT;
                queryUserContacts();
            }
        });
    }

    //Setting the RecyclerView
    protected void settingRecyclerView(@NonNull View view){
        rvContactsAchievements = view.findViewById(R.id.rvPosts);
        allContactsAchievements = new ArrayList<>();
        contactsAchievementsAdapter = new AchievementsAdapter(getContext(), allContactsAchievements);
        rvContactsAchievements.setAdapter(contactsAchievementsAdapter);
        rvContactsAchievements.setLayoutManager(new LinearLayoutManager(getContext()));
        allUserContacts = new ArrayList<>();
        userContactsAdapter = new UsersAdapter(getContext(), allUserContacts);
    }

    //Retrieving achievements of contacts
    protected void queryContactsAchievements() {
        //Creating and constraining query
        ParseQuery<Achievement> query = ParseQuery.getQuery(Achievement.class);
        query.include(Achievement.ACHIEVEMENT_KEY_USER);
        query.whereContainedIn(Achievement.ACHIEVEMENT_KEY_USER, allUserContacts);
        query.setLimit(postsLimit);
        query.addDescendingOrder(Achievement.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with getting achievements", e);
                    return;
                }
                for(Achievement achievement : achievements){
                    Log.i(TAG, "Achievement: " + achievement.getAchievementTitle() + ", user: " + achievement.getAchievementUser().getUsername());
                }
                //Set the adapter with new list of achievements
                contactsAchievementsAdapter.clear();
                allContactsAchievements.addAll(achievements);
                contactsAchievementsAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }

    //Retrieving contacts of current user
    protected void queryUserContacts() {
        //Creating and constraining query
        ParseQuery<ParseObject> query = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
        query.addAscendingOrder(User.USER_KEY_FULLNAME);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for(ParseObject user: users){
                    Log.i(TAG, "Contact: " + user.getString(User.USER_KEY_FULLNAME));
                }
                //Set the adapter with new list of contacts
                allUserContacts.clear();
                allUserContacts.addAll(users);
                userContactsAdapter.notifyDataSetChanged();
                queryContactsAchievements();
            }
        });
    }
}