package gmutils;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class IntentsTest {

    @Test
    public void getImageIntents() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Intents.ImageIntents imageIntents = Intents.getInstance().getImageIntents();
        assertNotNull(imageIntents);
    }
}