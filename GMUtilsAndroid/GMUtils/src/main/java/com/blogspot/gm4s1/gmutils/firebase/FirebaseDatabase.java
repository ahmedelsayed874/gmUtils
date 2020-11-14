package com.blogspot.gm4s1.gmutils.firebase;

import android.view.animation.ScaleAnimation;

import com.blogspot.gm4s1.gmutils.listeners.ResultCallback2;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * https://firebase.google.com/docs/database/android/start
 * <p>
 * implementation 'com.google.firebase:firebase-database:19.5.1'
 */
public class FirebaseDatabase<T> {
    /*
        AVAILABLE DATA-TYPES::
            String
            Long
            Double
            Boolean
            Map<String, Object>
            List<Object>
     */

    public static <T> FirebaseDatabase<T> getInstance(String path, Class<T> valueType) {
        return new FirebaseDatabase<T>(path, valueType);
    }

    private final Class<T> mValueType;
    private final DatabaseReference myRef;
    private Map<ResultCallback2<T, String>, ValueEventListener> readObservers;

    private FirebaseDatabase(String path, Class<T> valueType) {
        com.google.firebase.database.FirebaseDatabase database;
        database = com.google.firebase.database.FirebaseDatabase.getInstance();

        mValueType = valueType;
        myRef = database.getReference(path);
    }

    //----------------------------------------------------------------------------------------------

    public void insert(T object, ResultCallback2<T, Boolean> insertCallback) {
        myRef.setValue(object).addOnCompleteListener(task -> {
            if (insertCallback != null)
                insertCallback.invoke(object, task.isSuccessful());
        });
    }

    //----------------------------------------------------------------------------------------------

    public void read(ResultCallback2<T, String> callback) {
        read(myRef, callback);
    }

    public void read(String path, ResultCallback2<T, String> callback) {
        DatabaseReference child = myRef.child(path);
        read(child, callback);
    }

    private void read(DatabaseReference ref, ResultCallback2<T, String> callback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                T value = dataSnapshot.getValue(mValueType);
                callback.invoke(value, "");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.invoke(null, error.toException().getMessage());
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    public void addObserve(ResultCallback2<T, String> observer) {
        addObserve(myRef, observer);
    }

    public void addObserve(String path, ResultCallback2<T, String> observer) {
        DatabaseReference child = myRef.child(path);
        addObserve(child, observer);
    }

    private void addObserve(DatabaseReference ref, ResultCallback2<T, String> observer) {
        if (readObservers == null) readObservers = new HashMap<>();

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                T value = dataSnapshot.getValue(mValueType);
                observer.invoke(value, "");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                observer.invoke(null, error.toException().getMessage());
            }
        };

        ref.addValueEventListener(listener);

        readObservers.put(observer, listener);
    }

    public void removeObserve(ResultCallback2<T, String> observer) {
        removeObserve(myRef, observer);
    }

    public void removeObserve(String path, ResultCallback2<T, String> observer) {
        DatabaseReference child = myRef.child(path);
        removeObserve(child, observer);
    }

    private void removeObserve(DatabaseReference ref, ResultCallback2<T, String> observer) {
        if (readObservers != null) {
            ref.removeEventListener(readObservers.get(observer));
            readObservers.remove(observer);
        }
    }

    public void clearObserves() {
        if (readObservers != null) {
            Set<Map.Entry<ResultCallback2<T, String>, ValueEventListener>> entries;
            entries = readObservers.entrySet();
            for (Map.Entry<ResultCallback2<T, String>, ValueEventListener> entry : entries) {
                myRef.removeEventListener(entry.getValue());
            }

            readObservers.clear();
        }
    }

    //----------------------------------------------------------------------------------------------

    public void delete(String key, ResultCallback2<String, String> deleteCallback) {
        myRef.child(key).removeValue((error, ref) -> {
            if (deleteCallback != null) {
                if (error == null) {
                    deleteCallback.invoke(key, "");
                } else {
                    deleteCallback.invoke(key, error.getDetails());
                }
            }
        });
    }

    public void deleteMultiple(String[] keys, ResultCallback2<String, String> deleteCallback) {
        Map<String, Object> kMap = new HashMap<>();
        String keyString = "";

        for (String k : keys) {
            kMap.put(k, null);
            keyString += "[" + k + "]";
        }

        String finalKeyString = keyString;
        myRef.updateChildren(kMap, (error, ref) -> {
            if (deleteCallback != null) {
                if (error == null) {
                    deleteCallback.invoke(finalKeyString, "");
                } else {
                    deleteCallback.invoke(finalKeyString, error.getDetails());
                }
            }
        });
    }

}
