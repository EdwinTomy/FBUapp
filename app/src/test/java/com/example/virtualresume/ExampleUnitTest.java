package com.example.virtualresume;

import com.example.virtualresume.activities.LoginActivity;
import com.example.virtualresume.models.Achievement;
import com.example.virtualresume.models.User;

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
    public void userObject1(){
        User user = new User();
        user.setLastName("Lopez de Santa Anna");

        assertEquals("Lopez de Santa Anna", user.getLastName());
    }

    @Test
    public void userObject2(){
        User user = new User();
        user.setFirstName("Antonio");

        assertEquals("Antonio", user.getLastName());
    }

    @Test
    public void achievementObject1(){
        Achievement achievement = new Achievement();
        String description = "I served in the Spanish army and rose to the rank of captain. Fought on both sides of nearly every issue of the day. Backed Vicente Guerrero for president, only to help depose him later.";
        achievement.setDescription(description);

        assertEquals(description, achievement.getDescription());
    }

    @Test
    public void achievementObject2(){
        Achievement achievement = new Achievement();
        String field = "war";
        achievement.setField(field);

        assertEquals(field, achievement.getField());
    }
}