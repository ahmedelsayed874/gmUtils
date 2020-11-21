package com.blogspot.gm4s1.gmutils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.blogspot.gm4s1.gmutils.ContextWrapper;

import java.util.*;

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
public class SettingsStorage {

    private static String PREF_NAME = SettingsStorage.class.getName() + "SETTINGS";

    public static SettingsStorage getInstance() {
        Context appContext = StorageManager.getAppContext();

        return new SettingsStorage(appContext);
    }

    private SharedPreferences mPreference;
    private Language language = null;

    private SettingsStorage(Context context) {
        mPreference = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Language languagePref() {
        if (language == null) language = new Language(mPreference);
        return language;
    }

    //---------------------------------------------------------------------------------------------

    public static abstract class BasePref {
        SharedPreferences mPreference;
        String PREF_KEY;

        BasePref(SharedPreferences mPreference, String PREF_KEY) {
            this.mPreference = mPreference;
            this.PREF_KEY = PREF_KEY;
        }

    }

    public static class Language extends BasePref {

        Language(SharedPreferences preferences) {
            super(preferences, "lang");

        }

        public boolean isSavedLanguageEN() {
            return mPreference.getBoolean(PREF_KEY, true);
        }

        public String savedLanguage() {
            if (isSavedLanguageEN()) return "en";
            else return "ar";
        }

        public void saveLanguage(boolean en) {
            mPreference.edit().putBoolean(PREF_KEY, en).apply();
            usingEnglish = en;
        }

        public void applySavedLanguage(Context context) {
            Locale locale = new Locale(savedLanguage());
            Locale.setDefault(locale);

            Resources resources = context.getResources();

            Configuration configuration = resources.getConfiguration();
            configuration.setLayoutDirection(locale);

            configuration.locale = locale;
            configuration.setLocale(locale);

            resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        }

        public Context createNewContext(Context context) {
            return ContextWrapper.wrap(context, savedLanguage());
        }

        //------------------------------------------------------------------------------------------


        private static Boolean usingEnglish = null;

        public static boolean usingEnglish() {
            boolean ar;

            if (usingEnglish != null)
                ar = !usingEnglish;
            else
                ar = locale().getLanguage().equalsIgnoreCase("ar");

            return !ar;
        }

        public static Locale locale() {
            return Locale.getDefault();
        }

    }

}
