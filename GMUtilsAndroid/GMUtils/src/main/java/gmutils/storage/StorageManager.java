package gmutils.storage;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class StorageManager {

    public interface Callback {
        Application getApplication();
    }

    private static Callback registeredCallback = null;

    public static void registerCallback(Callback callback) {
        registeredCallback = callback;
    }

    static Context getAppContext() {
        if (registeredCallback != null) {
            return registeredCallback.getApplication();

        } else {
            throw new RuntimeException("you haven't PreferencesManager.Callback in your Application class");
        }
    }

}