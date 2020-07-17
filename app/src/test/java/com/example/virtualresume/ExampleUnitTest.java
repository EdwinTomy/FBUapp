package com.example.virtualresume;

import com.example.virtualresume.activities.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static com.parse.ParseUser.getCurrentUser;
import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void userObject(){
        LoginActivity loginActivity = new LoginActivity();
        loginActivity.loginUser("edwintomy", "edwintomy");
        String username = getCurrentUser().getUsername();

        assertEquals("edwintomy", username);
    }
}