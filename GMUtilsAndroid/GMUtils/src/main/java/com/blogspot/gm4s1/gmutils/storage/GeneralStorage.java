package com.blogspot.gm4s1.gmutils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

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
public class GeneralStorage {

    public static GeneralStorage getInstance() {
        return new GeneralStorage(getPrefName());
    }

    public static GeneralStorage getInstance(String name) {
        return new GeneralStorage(
                TextUtils.isEmpty(name) ?
                        getPrefName() :
                        name
        );
    }

    private static String getPrefName() {
        return GeneralStorage.class.getName() + "GENERAL";
    }

    //----------------------------------------------------------------------------------------------

    private SharedPreferences mPreference;

    private GeneralStorage(String name) {
        Context appContext = StorageManager.getAppContext();

        mPreference = appContext.getSharedPreferences(name, Context.MODE_PRIVATE);

    }

    public void save(String key, String value) {
        mPreference.edit().putString(key, value).apply();
    }

    public String retrieve(String key, String defaultValue) {
        return mPreference.getString(key, defaultValue);
    }

    public void remove(String key) {
        mPreference.edit().remove(key).apply();
    }
}
