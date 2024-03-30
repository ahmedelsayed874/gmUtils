package gmutils.firebase.database.w2;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gmutils.listeners.ResultCallback2;


/**
 * https://firebase.google.com/docs/database/android/start
 * <p>
 * implementation 'com.google.firebase:firebase-database:19.5.1'
 */
public class FirebaseDbCollection<T> {
    /*
        AVAILABLE DATA-TYPES::
            String
            Long
            Double
            Boolean
            Map<String, Object>
            List<Object>
     */

    public static <T> FirebaseDbCollection<T> getInstance(String path, Class<T> valueType) {
        return new FirebaseDbCollection<T>(path, valueType);
    }

    private final Class<T> mValueType;
    private final DatabaseReference myRef;
    private Map<ResultCallback2<T, String>, ValueEventListener> readObservers;

    private FirebaseDbCollection(String path, Class<T> valueType) {
        try {
            Class.forName("com.google.firebase.database.FirebaseDatabase");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "//https://firebase.google.com/docs/database/android/start\n" +
                    "implementation 'com.google.firebase:firebase-database:19.6.0'");
        }

        com.google.firebase.database.FirebaseDatabase database;
        database = com.google.firebase.database.FirebaseDatabase.getInstance();

        mValueType = valueType;
        myRef = database.getReference(path);
    }

    private String refinePath(String path) {
        if (path == null) return "";
        return path
                .replace(".", "-")
                .replace("#", "-")
                .replace("$", "-")
                .replace("[", "-")
                .replace("]", "-");
    }

    //----------------------------------------------------------------------------------------------

    public void save(T object, ResultCallback2<T, Boolean> insertCallback) {
        myRef.setValue(object).addOnCompleteListener(task -> {
            if (insertCallback != null)
                insertCallback.invoke(object, task.isSuccessful());
        });
    }

    public void save(T object, String path, ResultCallback2<T, Boolean> insertCallback) {
        DatabaseReference ref = myRef.child(refinePath(path));

        ref.setValue(object).addOnCompleteListener(task -> {
            if (insertCallback != null)
                insertCallback.invoke(object, task.isSuccessful());
        });
    }

    //----------------------------------------------------------------------------------------------

    public void retrieve(ResultCallback2<List<T>, String> callback) {
        retrieve(myRef, callback);
    }

    public void retrieve(String path, ResultCallback2<List<T>, String> callback) {
        DatabaseReference child = myRef.child(refinePath(path));
        retrieve(child, callback);
    }

    private void retrieve(DatabaseReference ref, ResultCallback2<List<T>, String> callback) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Object value = snapshot.getValue();
                    try {
                        if (value instanceof List) {
                            GenericTypeIndicator<List<T>> type = new GenericTypeIndicator<List<T>>() {
                            };
                            List<T> values = snapshot.getValue(type);
                            callback.invoke(values, null);
                        }
                        //
                        else if (value instanceof Map) {
                            try {
                                GenericTypeIndicator<Map<String, T>> type = new GenericTypeIndicator<>() {
                                };
                                Map<String, T> map = snapshot.getValue(type);
                                callback.invoke(new ArrayList<>(map.values()), null);
                            } catch (Exception e) {
                                GenericTypeIndicator<Map<String, List<T>>> type = new GenericTypeIndicator<>() {
                                };
                                Map<String, List<T>> map = snapshot.getValue(type);
                                List<T> values = new ArrayList<>();
                                for (var entry : map.values()) {
                                    values.addAll(entry);
                                }
                                callback.invoke(values, null);
                            }
                        }
                        //
                        else {
                            T value2 = snapshot.getValue(mValueType);
                            callback.invoke(Arrays.asList(value2), null);
                        }
                    } catch (Exception e) {
                        Log.e("*****", e.getMessage() + "\n--------\n" + value);
                    }
                } else {
                    callback.invoke(null, "No data");
                }
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
        DatabaseReference child = myRef.child(refinePath(path));
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
        DatabaseReference child = myRef.child(refinePath(path));
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

    public void delete(String path, ResultCallback2<String, String> deleteCallback) {
        myRef.child(refinePath(path)).removeValue((error, ref) -> {
            if (deleteCallback != null) {
                if (error == null) {
                    deleteCallback.invoke(path, "");
                } else {
                    deleteCallback.invoke(path, error.getDetails());
                }
            }
        });
    }

    public void deleteMultiple(String[] paths, ResultCallback2<String, String> deleteCallback) {
        Map<String, Object> kMap = new HashMap<>();
        String keyString = "";

        for (String k : paths) {
            kMap.put(refinePath(k), null);
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
