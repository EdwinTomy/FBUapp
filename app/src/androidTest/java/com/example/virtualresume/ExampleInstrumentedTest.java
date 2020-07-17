package com.example.virtualresume;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.virtualresume.activities.CreateUserActivity;
import com.example.virtualresume.activities.LoginActivity;
import com.example.virtualresume.models.User;
import com.parse.ParseUser;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.parse.ParseUser.getCurrentUser;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.virtualresume", appContext.getPackageName());
    }

    @Test
    public void userObject(){
        LoginActivity loginActivity = new LoginActivity();
        loginActivity.loginUser("edwintomy", "edwintomy");
        //String username = getCurrentUser().getUsername();

        assertEquals("edwintomy", "holea");
    }
}