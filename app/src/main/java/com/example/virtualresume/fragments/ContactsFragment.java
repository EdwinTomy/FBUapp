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
import com.example.virtualresume.adapters.UsersAdapter;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    final private static String TAG = "ConstactsFragment";

    private RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected UsersAdapter adapter;
    protected List<ParseUser> allUsers;
    final protected int POST_LIMIT = 20;
    protected int postsLimit = 20;
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

    public ContactsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Create adapter and data source
        allUsers = new ArrayList<>();
        adapter = new UsersAdapter(getContext(), allUsers);
        //Create layout for one row in the list
        //Set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        //Set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
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

    //Retrieving ParseObjects (posts)
    protected void queryPosts(int postsLimit) {
        //Object to be queried (Post)
        Log.i(TAG, "Inside query");
        ParseQuery<ParseUser> query = User.getQuery();
        query.addAscendingOrder(User.KEY_FIRSTNAME);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for(ParseUser user: users){
                    Log.i(TAG, "User: " + user.getUsername());
                }
                adapter.clear();
                allUsers.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });
        swipeContainer.setRefreshing(false);
    }
}