package com.example.virtualresume.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.virtualresume.R;
import com.example.virtualresume.adapters.UsersAdapter;
import com.example.virtualresume.models.Following;
import com.example.virtualresume.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

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
    protected List<Following> allUsers;
    private EditText searchText;
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

        //Recycler View
        rvPosts = view.findViewById(R.id.rvPosts);
        allUsers = new ArrayList<Following>();
        adapter = new UsersAdapter(getContext(), allUsers);
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Search EditText
        searchText = view.findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                queryPosts(POST_LIMIT, editable.toString());
            }
        });

        queryPosts(POST_LIMIT, null);
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
                queryPosts(POST_LIMIT, null);
                postsLimit = POST_LIMIT;
            }
        });
    }

    //Retrieving ParseObjects (posts)
    protected void queryPosts(int postsLimit, String searchText) {
        //Object to be queried (Post)
        Log.i(TAG, "Inside query");
        ParseQuery<Following> query = ParseQuery.getQuery(Following.class);
        query.whereEqualTo(Following.FOLLOWING_KEY_USER, User.getCurrentUser());
        query.addAscendingOrder(User.KEY_FULLNAME);

        //When searching
        if(searchText != null) {
            query.whereContains("username", searchText);
        }

        query.findInBackground(new FindCallback<Following>() {
            @Override
            public void done(List<Following> followings, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for(Following following: followings){
                    Log.i(TAG, "User: " + following.getFollowingUser().getString("fullName"));
                }
                adapter.clear();
                allUsers.addAll(followings);
                adapter.notifyDataSetChanged();
            }
        });
        swipeContainer.setRefreshing(false);
    }
}