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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.virtualresume.R;
import com.example.virtualresume.adapters.UsersAdapter;
import com.example.virtualresume.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    final private static String TAG = "ContactsFragment";

    private RecyclerView rvPosts;
    protected SwipeRefreshLayout swipeContainer;
    protected UsersAdapter adapterContacts;
    protected UsersAdapter adapterAddableContacts;
    protected List<ParseObject> allContacts;
    protected List<ParseObject> allAddableContacts;
    private EditText searchText;
    final protected int POST_LIMIT = 20;
    protected int postsLimit = 20;
    protected Button btnAddContact;
    protected Button btnSearchContact;
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
        allContacts = new ArrayList<>();
        adapterContacts = new UsersAdapter(getContext(), allContacts);
        rvPosts.setAdapter(adapterContacts);
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
                queryContacts(POST_LIMIT, editable.toString());
            }
        });

        //Adding Contact
        btnAddContact = view.findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryAddableUsers(POST_LIMIT, null);
            }
        });

        //Searching Contact
        btnSearchContact = view.findViewById(R.id.btnAddContact);
        btnSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryContacts(POST_LIMIT, null);
            }
        });

        queryContacts(POST_LIMIT, null);
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
                queryContacts(POST_LIMIT, null);
                postsLimit = POST_LIMIT;
            }
        });
    }

    //Retrieving Contacts
    protected void queryContacts(int postsLimit, String searchText) {
        //Object to be queried (Post)
        Log.i(TAG, "Inside query");
        ParseQuery<ParseObject> query = User.getCurrentUser().getRelation("friends").getQuery();
        query.addAscendingOrder(User.KEY_FULLNAME);

        //When searching
        if(searchText != null) {
            query.whereContains("username", searchText);
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for(ParseObject user: users){
                    Log.i(TAG, "User: " + user.getString("username"));
                }
                allContacts.clear();
                allContacts.addAll(users);
                adapterContacts.notifyDataSetChanged();
            }
        });
        swipeContainer.setRefreshing(false);
    }


    //Retrieving ParseUsers
    protected void queryAddableUsers(int postsLimit, String searchText) {
        //Object to be queried (Post)
        Log.i(TAG, "Inside query");

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", searchText); // find adults
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    // The query was successful.
                } else {
                    // Something went wrong.
                }
            }
        });

        swipeContainer.setRefreshing(false);
    }
}

