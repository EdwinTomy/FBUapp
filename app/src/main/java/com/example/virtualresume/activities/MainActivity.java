package com.example.virtualresume.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.virtualresume.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                        //fragment = new LogoutFragment();
                        Toast.makeText(MainActivity.this, "1!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_contacts:
                        //fragment = new PostsFragment();
                        Toast.makeText(MainActivity.this, "2!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_edit:
                        //fragment = new ComposeFragment();
                        Toast.makeText(MainActivity.this, "3!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_map:
                        //fragment = new ComposeFragment();
                        Toast.makeText(MainActivity.this, "4!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_view:
                    default:
                        //fragment = new ProfileFragment();
                        Toast.makeText(MainActivity.this, "6!", Toast.LENGTH_SHORT).show();
                        break;
                }
                //fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        //Default view before selection
        bottomNavigationView.setSelectedItemId(R.id.action_view);
    }
}