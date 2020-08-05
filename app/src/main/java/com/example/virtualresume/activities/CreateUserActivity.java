package com.example.virtualresume.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;
import com.example.virtualresume.R;
import com.example.virtualresume.models.User;
import com.example.virtualresume.utils.CameraApplication;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SignUpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.Headers;

public class CreateUserActivity extends CameraApplication {

    public static final String TAG = "CreateUserActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 40;

    private EditText firstNameInput;
    private EditText lastNameInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText bioInput;
    private EditText addressInput;
    private ImageView picture;
    private Button btnPicture;
    private Button btnCreate;
    private File photoFile;

    Double JSONlatitude;
    Double JSONlongitude;
    final private String GEOCODER_URL = "http://api.positionstack.com/v1/forward?access_key=db50b1be9f183ecfa3dbfb53faaa22c5";

    String firstName;
    String username;
    String lastName;
    String password;
    String address;
    String bio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        firstNameInput = findViewById(R.id.etFullName);
        lastNameInput = findViewById(R.id.etLastName);
        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        bioInput = findViewById(R.id.etBio);
        addressInput = findViewById(R.id.etLat);
        picture = findViewById(R.id.ivProfileImage);
        btnPicture = findViewById(R.id.btnPicture);
        btnCreate = findViewById(R.id.btnEditProfile);


        //Taking a picture
        btnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Clicked");
                photoFile = launchCamera(picture, photoFile);
            }
        });

        //Clicking to create new user
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstName = firstNameInput.getText().toString();
                username = usernameInput.getText().toString();
                lastName = lastNameInput.getText().toString();
                password = passwordInput.getText().toString();
                address = addressInput.getText().toString();
                bio = bioInput.getText().toString();

                if(firstName.isEmpty()){
                    Toast.makeText(CreateUserActivity.this, "Enter first name!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(lastName.isEmpty()){
                    Toast.makeText(CreateUserActivity.this, "Enter last name!",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                if(address.isEmpty()){
                    Toast.makeText(CreateUserActivity.this, "Enter address name!",
                            Toast.LENGTH_SHORT).show();
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

                        Toast.makeText(getApplicationContext(), "succccccesssss", Toast.LENGTH_LONG).show();

                        JSONObject jsonObject = json.jsonObject;
                        try {
                            JSONArray data = jsonObject.getJSONArray("data");
                            //JSONArray results = data.getJSONArray("results");
                            JSONObject location = data.getJSONObject(0);
                            JSONlatitude = location.getDouble("latitude");
                            JSONlongitude = location.getDouble("longitude");

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e(TAG, "Hit JSON exception", e);
                        }

                        //Sign up user
                        SignUpUser();
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


    //Attempting to sign up after onClick
    private void SignUpUser() {

        Log.i(TAG, "Attempting to sign up user:" + username);
        final User user = new User();
        //Set core properties
        user.setUsername(username);
        user.setPassword(password);
        user.setUserFullName(firstName + " " + lastName);
        user.setUserBio(bio);
        user.setUserHome(new ParseGeoPoint(JSONlatitude, JSONlongitude));
        user.setUserAddress(address);
        user.saveInBackground();
        //Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    //Navigate to MainActivity
                    Toast.makeText(CreateUserActivity.this, "Success in sign up!",
                            Toast.LENGTH_SHORT).show();
                    savePhoto(user);
                    goMainActivity();
                    return;
                }
                Toast.makeText(CreateUserActivity.this,
                        "Enter a password and unique username!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Issue with sign up", e);
                return;
            }
        });

    }

    //Navigate to MainActivity
    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    //Save Photo
    private void savePhoto(User user) {
        if(photoFile != null) {
            ParseFile file = new ParseFile(photoFile);
            user.setUserProfileImage(file);
            user.saveInBackground();
            Log.i(TAG, "File not null");
            Toast.makeText(CreateUserActivity.this, "Profile loaded", Toast.LENGTH_SHORT).show();
        }
    }
}