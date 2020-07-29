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
import android.widget.EditText;
import android.widget.Toast;

import com.example.virtualresume.R;
import com.example.virtualresume.adapters.AddableUsersAdapter;
import com.example.virtualresume.adapters.UsersAdapter;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    final private static String TAG = "ContactsFragment";

    protected UsersAdapter userContactsAdapter;
    protected AddableUsersAdapter addableContactsAdapter;
    protected List<ParseObject> allUserContacts;
    protected List<ParseObject> allAddableContacts;
    private RecyclerView rvUserContacts;
    protected SwipeRefreshLayout swipeContainer;
    protected ItemSwiper itemSwiper;

    //Sorting and search filtering elements
    private EditText searchByName;
    private EditText searchByProximity;
    private EditText searchByField;
    protected Button btnAddContact;
    protected Button btnSearchContact;
    protected Button btnSortAlphabetically;
    protected Button btnSortProximity;
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

        //Searching for a contact
        searchContact(view);

        //Adding contact
        btnAddContact = view.findViewById(R.id.btnAddContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearching = false;
                queryAddableUsers();
            }
        });

        //Searching Contact
        btnSearchContact = view.findViewById(R.id.btnSearchContact);
        btnSearchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSearching = true;
                queryUserContacts();
            }
        });

        //Sorting Alphabetically
        btnSortAlphabetically = view.findViewById(R.id.btnSortAlphabetically);
        btnSortAlphabetically.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSortedAlphabetically = true;
                if(isSearching)
                    queryUserContacts();
                else
                    queryAddableUsers();
            }
        });

        //Sorting by proximity
        btnSortProximity = view.findViewById(R.id.btnSortProximity);
        btnSortProximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSortedAlphabetically = false;
                if(isSearching)
                    queryUserContacts();
                else
                    queryAddableUsers();
            }
        });
    }

    //Delete or add contact
    private void deleteAddContact(int position){
        if(position != RecyclerView.NO_POSITION){
            ParseObject user = allUserContacts.get(position);
            Log.i(TAG, user.getString(User.USER_KEY_USERNAME));
            ParseRelation<ParseObject> relation = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS);
            if(isSearching)
                relation.remove(user);
            else
                relation.add(user);
            try {
                User.getCurrentUser().save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    //Searching for a contact
    private void searchContact(View view) {
        searchByName = view.findViewById(R.id.searchName);
        searchByName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

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
        userContactsAdapter = new UsersAdapter(getContext(), allUserContacts);
        rvUserContacts.setAdapter(userContactsAdapter);
        rvUserContacts.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Retrieving contacts of current user
    protected void queryUserContacts() {
        //Creating and constraining query
        ParseQuery<ParseObject> query = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
        filterQueryObject();

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
    protected void queryAddableUsers() {
        //Creating and constraining query
        Log.i(TAG, "inside addable");
        ParseQuery<ParseUser> query =  User.getQuery();
        filterQueryUser(query);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if(e != null){
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
                for(ParseObject user: users){
                    Log.i(TAG, "Contact: " + user.getString(User.USER_KEY_FULLNAME));
                }
                //Set the adapter with new list of contacts
                allAddableContacts.clear();
                allAddableContacts.addAll(users);
                allAddableContacts.removeAll(allUserContacts);
                allUserContacts.clear();
                allUserContacts.addAll(allAddableContacts);
                userContactsAdapter.notifyDataSetChanged();
            }
        });
        swipeContainer.setRefreshing(false);
    }

    //Assign the distances to the users from current user.
    protected void updateDistances(List<ParseObject> users){
        for(ParseObject user: users){
            ParseGeoPoint userHome = User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME);
            ParseGeoPoint contactHome = user.getParseGeoPoint(User.USER_KEY_HOME);
            double distance = calculateDistanceKilometer(userHome, contactHome);
            user.put(User.USER_KEY_DISTANCE_FROM, distance);
            Log.i(TAG, user.getString(User.USER_KEY_FULLNAME) + ": " + distance);
            user.saveInBackground();
        }
    }

    //Calculate distances between two ParseGeoPoints
    protected double calculateDistanceKilometer(ParseGeoPoint userHome, ParseGeoPoint contactHome){

        double userHomeLatitudeRad = Math.toRadians(userHome.getLatitude());
        Log.i("Distance", String.valueOf(userHomeLatitudeRad));
        double userHomeLongitudeRad = Math.toRadians(userHome.getLongitude());
        Log.i("Distance", String.valueOf(userHomeLongitudeRad));
        double contactHomeLatitudeRad = Math.toRadians(contactHome.getLatitude());
        Log.i("Distance", String.valueOf(contactHomeLatitudeRad));
        double contactHomeLongitudeRad = Math.toRadians(contactHome.getLongitude());
        Log.i("Distance", String.valueOf(contactHomeLongitudeRad));

        double latitudeDistance = contactHomeLatitudeRad - userHomeLatitudeRad;
        double longitudeDistance = contactHomeLongitudeRad - userHomeLongitudeRad;

        //Haversine formula
        double distance = Math.pow(Math.sin(latitudeDistance / 2), 2)
                + Math.cos(userHomeLatitudeRad)
                * Math.cos(contactHomeLatitudeRad)
                * Math.pow(Math.sin(longitudeDistance / 2), 2);
        distance = 2 * Math.asin(Math.sqrt(distance)) * EARTH_RADIUS;

        Log.i("Distance", String.valueOf(distance));

        return distance;
    }

    //Sorting and filtering based on user input for ParseObject
    protected void filterQueryObject(){
        ParseQuery<ParseObject> query;

        //Compound querying for username and/or full name constraint matching
        if(nameConstraint != null) {
            ParseQuery<ParseObject> queryUsername = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
            queryUsername.whereContains(User.USER_KEY_USERNAME, nameConstraint);

            ParseQuery<ParseObject> queryFullName = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
            queryFullName.whereContains(User.USER_KEY_FULLNAME, nameConstraint);

            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            queries.add(queryUsername);
            queries.add(queryFullName);

            query = ParseQuery.or(queries);
        } else {
            query = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
        }

        //Constraining users within distance entered
        if(proximityConstraint != null) {

        } else {
            query = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
        }

        //Sorting
        if(isSortedAlphabetically)
            query.addAscendingOrder(User.USER_KEY_FULLNAME);
        else
            query.whereNear(User.USER_KEY_HOME, User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME));


    }

    //Sorting and filtering based on user input for ParseUser
    protected void filterQueryUser(ParseQuery<ParseUser> query){
        //Sorting
        if(isSortedAlphabetically)
            query.addAscendingOrder(User.USER_KEY_FULLNAME);
        else
            query.whereNear(User.USER_KEY_HOME, User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME));

        if(nameConstraint != null) {
            query.whereContains("username", nameConstraint);
        }
    }
}

