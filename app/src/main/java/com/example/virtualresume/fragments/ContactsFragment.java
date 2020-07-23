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
import com.example.virtualresume.adapters.AchievementsAdapter;
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

    protected UsersAdapter userContactsAdapter;
    protected UsersAdapter adapterAddableContacts;
    protected List<ParseObject> allUserContacts;
    protected List<ParseObject> allAddableContacts;

    private RecyclerView rvUserContacts;
    protected SwipeRefreshLayout swipeContainer;
    private EditText searchText;
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
        //Lookup the swipe container view
        swipeContainer = view.findViewById(R.id.swipeContainer);
        setupPullToRefresh(swipeContainer);

        //Filling the RecyclerView with the query of user Achievements
        settingRecyclerView(view);
        queryUserContacts(null);

        //Searching for a contact
        searchContact(view);

        //Adding Contact
        btnAddContact = view.findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryAddableUsers(null);
            }
        });

        //Searching Contact
        btnSearchContact = view.findViewById(R.id.btnAddContact);
        btnSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                queryUserContacts( null);
            }
        });

        queryUserContacts(null);
    }

    //Searching for a contact
    private void searchContact(View view) {
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
                queryUserContacts(editable.toString());
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
                queryUserContacts(null);
            }
        });
    }

    //Setting the RecyclerView
    protected void settingRecyclerView(@NonNull View view){
        rvUserContacts = view.findViewById(R.id.rvPosts);
        allUserContacts = new ArrayList<>();
        userContactsAdapter = new UsersAdapter(getContext(), allUserContacts);
        rvUserContacts.setAdapter(userContactsAdapter);
        rvUserContacts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Retrieving contacts of current user
    protected void queryUserContacts(String searchText) {
        //Creating and constraining query
        ParseQuery<ParseObject> query = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
        query.addAscendingOrder(User.USER_KEY_FULLNAME);

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
                    Log.i(TAG, "Contact: " + user.getString(User.USER_KEY_FULLNAME));
                }
                //Set the adapter with new list of contacts
                allUserContacts.clear();
                allUserContacts.addAll(users);
                userContactsAdapter.notifyDataSetChanged();
            }
        });
        swipeContainer.setRefreshing(false);
    }


    //Retrieving ParseUsers
    protected void queryAddableUsers(String searchText) {
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

