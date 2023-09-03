package gmutils.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

    //----------------------------------------------------------------------------------------------

    public void remove(String key) {
        mPreference.edit().remove(key).apply();
    }

    public void removeAll() {
        mPreference.edit().clear().apply();
    }

    //==============================================================================================

    public void saveToList(String listName, String... value) {
        saveToList(listName, false, value);
    }

    public void saveToList(String listName, List<String> value) {
        saveToList(listName, false, value);
    }


    public void saveToList(String listName, boolean onTop, String... value) {
        List<String> valueList = Arrays.asList(value);
        saveToList(listName, onTop, valueList);
    }

    public void saveToList(String listName, boolean onTop, List<String> value) {
        List<String> list = retrieveList(listName);

        int valueCount = value.size();
        for (int i = valueCount - 1; i >= 0; i--) {
            String v = value.get(i);
            if (onTop)
                list.add(0, v);
            else
                list.add(v);
        }

        saveCollectionHelper(listName, list);
    }


    private void saveCollectionHelper(String listName, Collection<String> data) {
        try {
            JSONArray jsonArray = new JSONArray();

            for (String it : data) {
                jsonArray.put(it);
            }

            save(listName, jsonArray.toString());

        } catch (Throwable e) {
        }
    }

    //---------------------------------------------------------------------

    public void saveToSet(String setName, String... value) {
        List<String> valueList = Arrays.asList(value);
        saveToSet(setName, valueList);
    }

    public void saveToSet(String setName, List<String> value) {
        Set<String> set = new HashSet<>(retrieveList(setName));

        int valueLength = value.size();
        for (int i = valueLength - 1; i >= 0; i--) {
            String v = value.get(i);
            set.add(v);
        }

        saveCollectionHelper(setName, set);
    }

    //---------------------------------------------------------------------

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

    //---------------------------------------------------------------------

    public void removeFromList(String listName, String value) {
        List<String> list = retrieveList(listName);

        while (list.remove(value));
        saveCollectionHelper(listName, list);

    }

    public void removeFromSet(String setName, String value) {
        Set<String> list = retrieveSet(setName);

        list.remove(value);
        saveCollectionHelper(setName, list);

    }

    //---------------------------------------------------------------------

    public void clearList(String listName) {
        remove(listName);
    }

    public void clearSet(String listName) {
        remove(listName);
    }

}
