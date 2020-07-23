package com.example.virtualresume.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.virtualresume.R;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    final private static String TAG = "MapFragment";
    protected List<ParseUser> allUsers;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.i(TAG, "inside callback");
            queryPosts();
            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            for(ParseUser user: allUsers) {
                Log.i(TAG, "User: " + user.getUsername());
                double latitude = user.getParseGeoPoint("home").getLatitude();
                double longitude = user.getParseGeoPoint("home").getLongitude();
                LatLng home = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(home).title(user.getString("fullName")));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            Log.i(TAG, "before callback");
            mapFragment.getMapAsync(callback);
            Log.i(TAG, "after callback");
        }
        Log.i(TAG, "list creation");
        allUsers = new ArrayList<>();
    }

    //Retrieving ParseUsers (posts)
    protected void queryPosts() {
        //Object to be queried (User)
        Log.i(TAG, "Inside query");
        ParseQuery<ParseUser> query = User.getQuery();
        query.addAscendingOrder(User.USER_KEY_FULLNAME);
        query.include("user");

        try {
            allUsers = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
