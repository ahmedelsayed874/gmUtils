package gmutils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class GeneralStorage {

    public static GeneralStorage getInstance() {
        return new GeneralStorage(null);
    }

    public static GeneralStorage getInstance(String name) {
        return new GeneralStorage(name);
    }

    /*private static String getPrefName() {
        return GeneralStorage.class.getName() + "GENERAL";
    }*/

    //----------------------------------------------------------------------------------------------

    private final SharedPreferences mPreference;

    private GeneralStorage(String name) {
        Context appContext = StorageManager.getAppContext();

        if (TextUtils.isEmpty(name)) {
            name = appContext.getPackageName() + "_" + getClass().getSimpleName().toUpperCase();
        } else {
            name = appContext.getPackageName() + "_" + name;
        }

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

    public void removeAll() {
        mPreference.edit().clear().apply();
    }

    //----------------------------------------------------------------------------------------------

    public void saveToList(String listName, String... value) {
        saveToList(listName, false, value);
    }

    public void saveToList(String listName, boolean onTop, String... value) {

        List<String> list = retrieveList(listName);
        for (int i = value.length - 1; i >= 0; i--) {
            String v = value[i];
            if (onTop)
                list.add(0, v);
            else
                list.add(v);
        }

        try {
            JSONArray jsonArray = new JSONArray();

            for (String it : list) {
                jsonArray.put(it);
            }

            save(listName, jsonArray.toString());

        } catch (Throwable e) {
        }
    }

    public List<String> retrieveList(String listName) {
        List<String> list = new ArrayList<>();

        try {
            String addressesJson = retrieve(listName, "[]");
            JSONArray jsonArray = new JSONArray(addressesJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.getString(i));
            }
        } catch (Throwable ignored) {
        }

        return list;
    }

    public void saveToSet(String setName, String... value) {

        Set<String> set = new HashSet<>(retrieveList(setName));
        for (int i = value.length - 1; i >= 0; i--) {
            String v = value[i];
            set.add(v);
        }

        try {
            JSONArray jsonArray = new JSONArray();

            for (String it : set) {
                jsonArray.put(it);
            }

            save(setName, jsonArray.toString());

        } catch (Throwable e) {
        }
    }

    public Set<String> retrieveSet(String setName) {
        Set<String> set = new HashSet<>();

        try {
            String addressesJson = retrieve(setName, "[]");
            JSONArray jsonArray = new JSONArray(addressesJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                set.add(jsonArray.getString(i));
            }
        } catch (Throwable ignored) {
        }

        return set;
    }

}