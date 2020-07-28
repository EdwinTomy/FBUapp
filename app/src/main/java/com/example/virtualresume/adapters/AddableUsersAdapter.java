package com.example.virtualresume.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.virtualresume.R;
import com.example.virtualresume.activities.ContactProfileActivity;
import com.example.virtualresume.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;

import org.parceler.Parcels;

import java.util.List;

public class AddableUsersAdapter extends UsersAdapter {

    public static final String TAG = "UsersAdapter";
    private Context context;
    private List<ParseObject> users;

    public AddableUsersAdapter(Context context, List<ParseObject> users) {
        super(context, users);
        this.context = context;
        this.users = users;
    }

    //
    @Override
    public void onClickAction(ParseObject user) {
        Log.i(TAG, user.getString(User.USER_KEY_USERNAME));
        ParseRelation<ParseObject> relation = User.getCurrentUser().getRelation(User.USER_KEY_CONTACTS);
        relation.add(user);
        try {
            User.getCurrentUser().save();
            Toast.makeText(context, "Added", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
