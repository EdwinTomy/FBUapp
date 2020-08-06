package com.example.virtualresume.activities;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.virtualresume.R;
import com.example.virtualresume.adapters.EditAchievementsAdapter;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.CameraApplication;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Headers;

public class EditUserDetailsActivity extends CameraApplication {

    public static final String TAG = "EditUserDetailsActivity";

    private EditText fullNameInput;
    private EditText bioInput;
    private EditText addressInput;
    private ImageView pictureInput;
    private Button btnPicture;
    private Button btnEditProfile;
    private File photoFile;

    Double JSONlatitude;
    Double JSONlongitude;
    String address;
    String bio;
    String fullName;
    final private String GEOCODER_URL = "http://api.positionstack.com/v1/forward?access_key=db50b1be9f183ecfa3dbfb53faaa22c5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        bindUserDetails();

        //Clicking to update user details
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = fullNameInput.getText().toString();
                bio = bioInput.getText().toString();
                address = addressInput.getText().toString();

                if(fullName.isEmpty()){
                    Toast.makeText(EditUserDetailsActivity.this, "Enter full name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(bio.isEmpty()){
                    Toast.makeText(EditUserDetailsActivity.this, "Enter bio!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(address.isEmpty()){
                    Toast.makeText(EditUserDetailsActivity.this, "Enter address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                getCoordinates();
            }
        });

    }

    //Turning string address into coordinates
    private void getCoordinates(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("limit", "1");
        params.put("query", address);
        params.put("fields", "latitude, longitude");
        client.get(GEOCODER_URL, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {

                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            //JSONArray results = data.getJSONArray("results");
                            JSONObject location = data.getJSONObject(0);
                            JSONlatitude = location.getDouble("latitude");
                            JSONlongitude = location.getDouble("longitude");

                            //Update user details
                            UpdateDetails(fullName, bio, JSONlatitude, JSONlongitude, photoFile, address);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Hit JSON exception", e);
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String errorResponse, Throwable t) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Toast.makeText(getApplicationContext(), errorResponse, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Locatiooon:" + errorResponse);
                    }
                }
        );

        Log.i(TAG, "Location:  " + JSONlatitude + "" + JSONlongitude);
    }

    //Posting user details
    private void bindUserDetails() {
        fullNameInput = findViewById(R.id.etFullName);
        bioInput = findViewById(R.id.etBio);
        pictureInput = findViewById(R.id.ivProfileImage);
        btnPicture = findViewById(R.id.btnPicture);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        addressInput = findViewById(R.id.etLat);

        fullNameInput.setText(User.getCurrentUser().getString(User.USER_KEY_FULLNAME));
        bioInput.setText(User.getCurrentUser().getString(User.USER_KEY_BIO));
        addressInput.setText(User.getCurrentUser().getString(User.USER_KEY_ADDRESS));

        ParseFile picture = User.getCurrentUser().getParseFile(User.USER_KEY_PROFILEIMAGE);
        if (picture != null)
            Glide.with(this).load(picture.getUrl()).into(pictureInput);

        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                photoFile = launchCamera(pictureInput, photoFile);
            }
        });
    }

    //Update after onClick
    private void UpdateDetails(String fullName, String bio, Double latitude, Double longitude, File photoFile, String address) {
        ParseUser user = ParseUser.getCurrentUser();
        Log.i(TAG, "Updating details of" + fullName);

        //Update properties
        user.put(User.USER_KEY_FULLNAME, fullName);
        user.put(User.USER_KEY_BIO, bio);
        user.put(User.USER_KEY_ADDRESS, address);
        user.put(User.USER_KEY_HOME, new ParseGeoPoint(latitude, longitude));

        if(photoFile != null)
            user.put(User.USER_KEY_PROFILEIMAGE, new ParseFile(photoFile));
        //Invoke signUpInBackground
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
            }
        });
        finish();
    }
}