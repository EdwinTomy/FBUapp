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
import com.example.virtualresume.adapters.UsersAdapter;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment {

    final private static String TAG = "MapFragment";
    protected List<ParseObject> allUserContacts;

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
            queryUserContacts();

            //Marking current user and contacts locations
            markContactsLocation(googleMap);
            markUserLocation(googleMap);
        }
    };

    //Marking user location
    private void markUserLocation(GoogleMap googleMap) {
        ParseUser user = User.getCurrentUser();
        if(User.getCurrentUser().getParseGeoPoint(User.USER_KEY_HOME) != null) {
            double latitude = user.getParseGeoPoint(User.USER_KEY_HOME).getLatitude();
            double longitude = user.getParseGeoPoint(User.USER_KEY_HOME).getLongitude();
            LatLng home = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(home).title("YOU").
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(home));
        }
    }

    //Marking contacts location
    public void markContactsLocation(GoogleMap googleMap){
        for(ParseObject contact: allUserContacts) {
            if(!contact.getString(User.USER_KEY_FULLNAME).equals(User.getCurrentUser().getUsername())){
                Log.i(TAG, "Contact: " + contact.getString(User.USER_KEY_FULLNAME));
                double latitude = contact.getParseGeoPoint(User.USER_KEY_HOME).getLatitude();
                double longitude = contact.getParseGeoPoint(User.USER_KEY_HOME).getLongitude();
                LatLng home = new LatLng(latitude, longitude);
                googleMap.addMarker(new MarkerOptions().position(home).title(contact.getString(User.USER_KEY_FULLNAME)));
            }
        }
    }

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
            mapFragment.getMapAsync(callback);
        }
        allUserContacts = new ArrayList<>();
    }

    //Retrieving contacts of current user
    protected void queryUserContacts() {
        //Creating and constraining query
        ParseQuery<ParseObject> query = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS).getQuery();
        query.addAscendingOrder(User.USER_KEY_FULLNAME);

        try {
            allUserContacts = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
