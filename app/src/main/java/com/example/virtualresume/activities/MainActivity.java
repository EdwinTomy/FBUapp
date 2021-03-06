package com.example.virtualresume.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.virtualresume.R;
import com.example.virtualresume.fragments.ContactsFragment;
import com.example.virtualresume.fragments.EditFragment;
import com.example.virtualresume.fragments.MapsFragment;
import com.example.virtualresume.fragments.NewsfeedFragment;
import com.example.virtualresume.fragments.ViewFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity{

    public static final String TAG = "MainActivity";
    private BottomNavigationView bottomNavigationView;
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Linking elements between java and xml
        bottomNavigationView = findViewById((R.id.bottomNavigation));

        //Menu item
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_newsfeed:
                        fragment = new NewsfeedFragment();
                        break;
                    case R.id.action_contacts:
                        fragment = new ContactsFragment();
                        break;
                    case R.id.action_edit:
                        fragment = new EditFragment();
                        break;
                    case R.id.action_map:
                        fragment = new MapsFragment();
                        break;
                    case R.id.action_view:
                    default:
                        fragment = new ViewFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        //Default view before selection
        bottomNavigationView.setSelectedItemId(R.id.action_view);
    }

    //Adding menu option for logout
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu and items present in it
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    //Logout
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //Menu item clicked
        if(item.getItemId() == R.id.logout){
            ParseUser.logOut();
            ParseUser currentUser = ParseUser.getCurrentUser();
            //Navigate to Login Screen
            goLoginActivity();
        }
        return true;
    }

    //Navigate to LoginActivity
    private void goLoginActivity() {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }

}