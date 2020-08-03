package com.example.virtualresume.fragments;

import android.graphics.Color;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.virtualresume.R;
import com.example.virtualresume.adapters.AchievementsAdapter;
import com.example.virtualresume.adapters.AddableUsersAdapter;
import com.example.virtualresume.adapters.UsersAdapter;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.ItemSwiper;
import com.example.virtualresume.utils.MyButtonClickListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    final private static String TAG = "ContactsFragment";

    protected UsersAdapter userContactsAdapter;
    protected AddableUsersAdapter addableContactsAdapter;
    protected List<Achievement> allUserAchievements;
    protected List<ParseObject> allUserContacts;
    protected List<ParseObject> allAddableContacts;
    private RecyclerView rvUserContacts;
    protected SwipeRefreshLayout swipeContainer;
    protected ItemSwiper itemSwiper;

    //Sorting and search filtering elements
    private EditText searchByName;
    private EditText searchByProximity;
    private EditText searchByField;
    protected ToggleButton btnSearchContact;
    protected ToggleButton btnSortAlphabetically;
    protected boolean isSearching = true;
    protected boolean isSortedAlphabetically = true;
    protected String nameConstraint;
    protected String proximityConstraint;
    protected String fieldConstraint;

    final private double EARTH_RADIUS = 6371.8;
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
        queryUserContacts();

        //Sliding mechanism
        itemSwiper = new ItemSwiper(getContext(), rvUserContacts, 200) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<ItemSwiper.MyButton> buffer) {
                buffer.add(new MyButton(getContext(), "Delete", 30, 0, Color.parseColor("#FF4C30"), new MyButtonClickListener() {
                    @Override
                    public void onClick(int position) {
                        Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                        deleteAddContact(position);
                    }
                }));

            }
        };

        //Searching constraints
        inputName(view);
        inputProximity(view);
        inputField(view);

        //Searching contact or user
        btnSearchContact = view.findViewById(R.id.btnSearchContact);
        btnSearchContact.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isSearching = false;
                    queryAddableUsers();
                }else{
                    isSearching = true;
                    queryUserContacts();
                }
            }
        });


        //Sorting button
        btnSortAlphabetically = view.findViewById(R.id.btnSortAlphabetically);
        btnSortAlphabetically.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    isSortedAlphabetically = false;
                    if(isSearching)
                        queryUserContacts();
                    else
                        queryAddableUsers();
                }else{
                    isSortedAlphabetically = true;
                    if (isSearching)
                        queryUserContacts();
                    else
                        queryAddableUsers();
                }
            }
        });
    }

    //Delete or add contact
    private void deleteAddContact(int position){
        if(position != RecyclerView.NO_POSITION){
            swipeContainer.setRefreshing(true);
            ParseObject user = allUserContacts.get(position);
            Log.i(TAG, user.getString(User.USER_KEY_USERNAME));
            ParseRelation<ParseObject> relation = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS);
            if(isSearching) {
                relation.remove(user);
                allUserContacts.remove(position);
                userContactsAdapter.notifyItemRemoved(position);
                swipeContainer.setRefreshing(false);
            }else{
                relation.add(user);
                User.getCurrentUser().saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        swipeContainer.setRefreshing(true);
                    }
                });
            }
        }
    }

    //Inputs for filtering search
    //Search by full name or username
    private void inputName(View view) {
        searchByName = view.findViewById(R.id.searchName);
        searchByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                nameConstraint = editable.toString();
                if(isSearching)
                    queryUserContacts();
                else
                    queryAddableUsers();
            }
        });
    }

    //Search by distance from current user
    private void inputProximity(View view) {
        searchByProximity = view.findViewById(R.id.searchLocation);
        searchByProximity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                proximityConstraint = editable.toString();
                if(isSearching)
                    queryUserContacts();
                else
                    queryAddableUsers();
            }
        });
    }

    //Search by distance from current user
    private void inputField(View view) {
        searchByField = view.findViewById(R.id.searchField);
        searchByField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                fieldConstraint = editable.toString();
                if(isSearching)
                    queryUserContacts();
                else
                    queryAddableUsers();
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
                if(isSearching)
                    queryUserContacts();
                else
                    queryAddableUsers();
            }
        });
    }

    //Setting the RecyclerView
    protected void settingRecyclerView(@NonNull View view){
        rvUserContacts = view.findViewById(R.id.rvPosts);
        allUserContacts = new ArrayList<>();
        allAddableContacts = new ArrayList<>();
        allUserAchievements = new ArrayList<>();
        userContactsAdapter = new UsersAdapter(getContext(), allUserContacts);
        rvUserContacts.setAdapter(userContactsAdapter);
        rvUserContacts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Retrieving contacts of current user
    protected void queryUserContacts() {
        //Creating and constraining query
        ParseQuery<ParseObject> query;

        //Compound querying for username and/or full name constraint matching from same input
        if(nameConstraint != null && !nameConstraint.isEmpty()) {
            //Constraining to username matching nameConstraint
            ParseQuery<ParseObject> queryUsername =
                    User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
            queryUsername.whereContains(User.USER_KEY_USERNAME, nameConstraint);

            //Constraining to full name matching nameConstraint
            ParseQuery<ParseObject> queryFullName =
                    User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
            queryFullName.whereContains(User.USER_KEY_FULLNAME, nameConstraint);

            //Compounding queries
            List<ParseQuery<ParseObject>> queries = new ArrayList<>();
            queries.add(queryUsername);
            queries.add(queryFullName);
            query = ParseQuery.or(queries);
        } else {
            query = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
        }

        //Constraining users within distance entered
        if(proximityConstraint != null && !proximityConstraint.isEmpty()) {
            query.whereWithinKilometers(User.USER_KEY_HOME,
                    User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME), Double.parseDouble(proximityConstraint));
        }

        //Sorting alphabetically or proximity
        if(isSortedAlphabetically)
            query.addAscendingOrder(User.USER_KEY_FULLNAME);
        else
            query.whereNear(User.USER_KEY_HOME, User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME));

        //Finalize query
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for(ParseObject user: users){
                    Log.i(TAG, "Query contacts: " + user.getString(User.USER_KEY_FULLNAME));

                }
                //Set the adapter with new list of contacts
                allUserContacts.clear();
                allUserContacts.addAll(users);
                if(fieldConstraint != null && !fieldConstraint.isEmpty()) {

                    //Removing all users who have no achievements containing the constraint text in their fields
                    for (ParseObject user : users) {
                        Log.i(TAG, "     After typing, contacts: " + user.getString(User.USER_KEY_FULLNAME));
                        userAchievementsMatchField(user);

                    }
                } else {
                    userContactsAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }
            }
        });
    }

    //Retrieving achievements of where the fields contain the search constrain
    protected void userAchievementsMatchField(final ParseObject user) {
        swipeContainer.setRefreshing(true);
        //Creating and constraining query
        ParseQuery<Achievement> query = ParseQuery.getQuery(Achievement.class);
        query.include(Achievement.ACHIEVEMENT_KEY_USER);
        query.include(Achievement.ACHIEVEMENT_KEY_FIELD);
        query.whereEqualTo(Achievement.ACHIEVEMENT_KEY_USER, user);
        query.whereContains(Achievement.ACHIEVEMENT_KEY_FIELD, fieldConstraint);

        query.findInBackground(new FindCallback<Achievement>() {
            @Override
            public void done(List<Achievement> achievements, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                //Removing the users that have no matching achievements
                if(achievements == null || achievements.isEmpty()) {
                    allUserContacts.remove(user);
                }

                //Set the adapter with new list of achievements
                userContactsAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
        });
    }


    //Retrieving ParseUsers
    protected void queryAddableUsers() {
        //Creating and constraining query
        Log.i(TAG, "inside addable");
        ParseQuery<ParseUser> query;

        //Compound querying for username and/or full name constraint matching from same input
        if(nameConstraint != null && !nameConstraint.isEmpty()) {
            //Constraining to username matching nameConstraint
            ParseQuery<ParseUser> queryUsername = User.getQuery();
            queryUsername.whereContains(User.USER_KEY_USERNAME, nameConstraint);

            //Constraining to full name matching nameConstraint
            ParseQuery<ParseUser> queryFullName = User.getQuery();
            queryFullName.whereContains(User.USER_KEY_FULLNAME, nameConstraint);

            //Compounding queries
            List<ParseQuery<ParseUser>> queries = new ArrayList<>();
            queries.add(queryUsername);
            queries.add(queryFullName);
            query = ParseQuery.or(queries);
        } else {
            query = User.getQuery();
        }

        //Constraining users within distance entered
        if(proximityConstraint != null && !proximityConstraint.isEmpty()) {
            query.whereWithinKilometers(User.USER_KEY_HOME,
                    User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME), Double.parseDouble(proximityConstraint));
        }

        //Sorting alphabetically or proximity
        if(isSortedAlphabetically)
            query.addAscendingOrder(User.USER_KEY_FULLNAME);
        else
            query.whereNear(User.USER_KEY_HOME, User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME));

        //Finalize query
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for(ParseObject user: users){
                    Log.i(TAG, "Query contacts: " + user.getString(User.USER_KEY_FULLNAME));

                }
                //Set the adapter with new list of contacts
                allUserContacts.clear();
                allUserContacts.addAll(users);
                if(fieldConstraint != null && !fieldConstraint.isEmpty()) {

                    //Removing all users who have no achievements containing the constraint text in their fields
                    for (ParseObject user : users) {
                        Log.i(TAG, "     After typing, contacts: " + user.getString(User.USER_KEY_FULLNAME));
                        userAchievementsMatchField(user);

                    }

                } else {
                    userContactsAdapter.notifyDataSetChanged();
                    swipeContainer.setRefreshing(false);
                }
            }
        });
    }
}

