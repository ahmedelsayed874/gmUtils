package gmutils.storage;

import static org.junit.Assert.*;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.util.List;

public class GeneralStorageTest {

    @Test
    public void saveToList() {
        StorageManager.registerCallback(() -> {
            Application a = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
            return a;
        });

        GeneralStorage s = GeneralStorage.getInstance("xxxxxxxxxxxxxxxxxxx");
        s.saveToList("xx", "1", "2", "3");
        List<String> xx1 = s.retrieveList("xx");

        s.removeAll();
        List<String> xx2 = s.retrieveList("xx");

        assertEquals(3, xx1.size());
        assertEquals(0, xx2.size());

    }

    @Test
    public void testSaveToList() {
    }

    @Test
    public void saveToSet() {
    }
}