package gmutils.firebase.database;

import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmutils.StringSet;
import gmutils.backgroundWorkers.BackgroundTaskAbs;
import gmutils.firebase.FirebaseUtils;
import gmutils.firebase.Response;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ResultCallback;

public class FirebaseDatabaseOp extends IFirebaseDatabaseOp {
    public final DatabaseReference databaseReference;

    public FirebaseDatabaseOp() {
        this("");
    }

    public FirebaseDatabaseOp(String rootNodeName) {
        try {
            Class.forName("com.google.firebase.database.FirebaseDatabase");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "//https://firebase.google.com/docs/database/android/start\n" +
                    "implementation 'com.google.firebase:firebase-database:19.6.0'");
        }

        if (TextUtils.isEmpty(rootNodeName)) {
            databaseReference = FirebaseDatabase.getInstance().getReference();
        } else {
            databaseReference = FirebaseDatabase.getInstance().getReference(rootNodeName);
        }
    }

    //----------------------------------------------------------------------------

    private DatabaseReference _getReferenceOfNode(String subNodePath) {
        if (TextUtils.isEmpty(subNodePath)) return databaseReference;

        try {
            subNodePath = FirebaseUtils.refinePathFragmentNames(subNodePath);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception at _getReferenceOfNode: ",
                    e
            );
        }

        var ref = databaseReference;
        return ref.child(subNodePath);
    }

    //----------------------------------------------------------------------------

    @Override
    public <T> void saveData(
            T data,
            @Nullable String subNodePath,
            ResultCallback<Response<Boolean>> callback
    ) {
        DatabaseReference ref = _getReferenceOfNode(subNodePath);
        saveData(ref, data, callback);
    }

    public <T> void saveData(
            DatabaseReference ref,
            T data,
            ResultCallback<Response<Boolean>> callback
    ) {
        ref.setValue(data)
                .addOnSuccessListener(unused -> {
                    if (callback != null) callback.invoke(Response.success(true));
                })
                .addOnFailureListener(e -> {
                    if (callback != null)
                        callback.invoke(Response.failed(new StringSet(e.getMessage())));
                });
    }

    @Override
    public <T> void saveMultipleData(
            Map<String, T> nodesAndData,
            ResultCallback<Response<Boolean>> callback
    ) {
        String[] keys = nodesAndData.keySet().toArray(new String[]{});
        if (keys.length > 0) {
            int i = 0;
            saveData(nodesAndData, keys, i, callback);
        }

//        DatabaseReference ref = _getReferenceOfNode(null);
//        saveMultipleData(ref, nodesAndData, callback);
    }

    private <T> void saveData(
            Map<String, T> nodesAndData,
            String[] keys,
            int kidx,
            ResultCallback<Response<Boolean>> callback
    ) {
        saveData(nodesAndData.get(keys[kidx]), keys[kidx], result -> {
            if (result.data != Boolean.TRUE) {
                callback.invoke(result);
            } else {
                int idx = kidx + 1;
                if (idx < keys.length) {
                    saveData(nodesAndData, keys, idx, callback);
                } else {
                    callback.invoke(result);
                }
            }
        });
    }

    //----------------------------------------------------------------------------

    @Override
    public <T> void retrieveAll(
            FBFilterOption filterOption,
            Class<T> dataType,
            ActionCallback<Object, List<T>> customConverter,
            ResultCallback<Response<List<T>>> callback
    ) {
        var ref = databaseReference;

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    backgroundTask().execute(() -> {
                        List<T> list = new ArrayList<>();
                        Object value = snapshot.getValue();

                        if (value != null) {
                            if (customConverter == null) {
                                try {
                                    if (value instanceof List) {
                                        GenericTypeIndicator<List<T>> type = new GenericTypeIndicator<List<T>>() {
                                        };
                                        List<T> values = snapshot.getValue(type);
                                        list.addAll(values);
                                    }
                                    //
                                    else if (value instanceof Map) {
                                        Map valueAsMap = (Map) value;
                                        try {
                                            for (Object key : valueAsMap.keySet()) {
                                                Object o = valueAsMap.get(key);
                                                String json = new Gson().toJson(o);
                                                T t = new Gson().fromJson(json, dataType);
                                                list.add(t);
                                            }
                                        } catch (Exception e) {
                                            String json = new Gson().toJson(valueAsMap);
                                            T t = new Gson().fromJson(json, dataType);
                                            list.add(t);
                                        }
                                    }
                                    //
                                    else {
                                        T value2 = snapshot.getValue(dataType);
                                        list.add(value2);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(
                                            "Exception at retrieveAll: " + e.getMessage() + "\n--------\n" + value,
                                            e
                                    );
                                }
                            }
                            //
                            else {
                                var values = customConverter.invoke(value);
                                list.addAll(values);
                            }
                        }

                        return list;
                    }, list -> {
                        callback.invoke(Response.success(list));
                    });
                }
                //
                else {
                    callback.invoke(
                            Response.failed(
                                    new StringSet("No data", "لا توجد بيانات")
                            )
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.invoke(Response.failed(new StringSet(error.getMessage())));
            }
        };

        if (filterOption == null) {
            ref.addListenerForSingleValueEvent(eventListener);
        } else {
            Query query = buildQuery(ref, filterOption);
            query.addListenerForSingleValueEvent(eventListener);
        }
    }

    protected BackgroundTaskAbs backgroundTask() {
        return BackgroundTaskAbs.getInstance();
    }

    //----------------------------------------------------------------------------

    @Override
    public <T> void retrieveSingle(
            String subNodePath,
            Class<T> dataType,
            ActionCallback<Object, Pair<T, StringSet>> customConverter,
            ResultCallback<Response<T>> callback
    ) {
        var ref = _getReferenceOfNode(subNodePath);
        retrieveSingle(ref, dataType, customConverter, callback);
    }

    public <T> void retrieveSingle(
            DatabaseReference ref,
            Class<T> dataType,
            ActionCallback<Object, Pair<T, StringSet>> customConverter,
            ResultCallback<Response<T>> callback
    ) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                handleReceivedDataSnapshot(snapshot, dataType, customConverter, callback);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.invoke(Response.failed(new StringSet(error.getMessage())));
            }
        });
    }

    private <T2> void handleReceivedDataSnapshot(
            DataSnapshot snapshot,
            Class<T2> dataType,
            ActionCallback<Object, Pair<T2, StringSet>> customConverter,
            ResultCallback<Response<T2>> callback
    ) {
        if (snapshot.exists()) {
            backgroundTask().execute(() -> {
                Pair<T2, StringSet> data = null;

                if (customConverter == null) {
                    if (snapshot.getValue() != null) {
                        T2 obj = null;

                        try {
                            obj = snapshot.getValue(dataType);
                        } catch (Exception e) {
                            throw new RuntimeException(
                                    "Exception at handleReceivedDataSnapshot: " + e.getMessage() + "\nValue: " + snapshot.getValue(),
                                    e
                            );
                        }

                        if (obj != null) {
                            data = new Pair<>(obj, null);
                        } else {
                            data = new Pair<>(
                                    null,
                                    new StringSet(
                                            "Invalid data type (" + dataType + ") ... exist object => " + snapshot.getValue()
                                    )
                            );
                        }
                    } else {
                        data = new Pair<>(null, new StringSet("No data..", "لا توجد بيانات.."));
                    }
                } else {
                    data = customConverter.invoke(snapshot.getValue());
                }

                return data;
            }, data -> {
                if (data.first != null) {
                    callback.invoke(Response.success(data.first));
                } else {
                    callback.invoke(Response.failed(data.second));
                }
            });
        }

        //
        else {
            callback.invoke(
                    Response.failed(
                            new StringSet("No data", "لا توجد بيانات")
                    )
            );
        }
    }

    //----------------------------------------------------------------------------

    @Override
    public <T> void listenToChanges(
            String subNodePath,
            Class<T> dataType,
            FBFilterOption filterOption,
            ResultCallback<Updates<T>> onChange,
            ResultCallback<String> onError
    ) {
        listenToChanges(
                _getReferenceOfNode(subNodePath),
                dataType,
                filterOption,
                onChange,
                onError
        );
    }

    public <T> void listenToChanges(
            @NotNull DatabaseReference ref,
            Class<T> dataType,
            FBFilterOption filterOption,
            ResultCallback<Updates<T>> onChange,
            ResultCallback<String> onError
    ) {
        _listenTo(
                ref,
                true,
                dataType,
                filterOption,
                onChange,
                onError
        );
    }

    @Override
    public <N> void listenToChangesSpecific(
            String subNodePath,
            Class<N> dataType,
            FBFilterOption filterOption,
            ResultCallback<Updates<N>> onChange,
            ResultCallback<String> onError
    ) {
        _listenTo(
                _getReferenceOfNode(subNodePath),
                true,
                dataType,
                filterOption,
                onChange,
                onError
        );
    }

    @Override
    public <T> void listenToAdding(
            String subNodePath,
            Class<T> dataType,
            FBFilterOption filterOption,
            ResultCallback<Updates<T>> onAdd,
            ResultCallback<String> onError
    ) {
        listenToAdding(
                _getReferenceOfNode(subNodePath),
                dataType,
                filterOption,
                onAdd,
                onError
        );
    }

    public <T> void listenToAdding(
            DatabaseReference ref,
            Class<T> dataType,
            FBFilterOption filterOption,
            ResultCallback<Updates<T>> onAdd,
            ResultCallback<String> onError
    ) {
        _listenTo(
                ref,
                false,
                dataType,
                filterOption,
                onAdd,
                onError
        );
    }

    @Override
    public <N> void listenToAddingSpecific(
            String subNodePath,
            Class<N> dataType,
            FBFilterOption filterOption,
            ResultCallback<Updates<N>> onAdd,
            ResultCallback<String> onError
    ) {
        _listenTo(
                _getReferenceOfNode(subNodePath),
                false,
                dataType,
                filterOption,
                onAdd,
                onError
        );
    }

    private <T2> void _listenTo(
            DatabaseReference ref,
            boolean listenToAnyChange,
            Class<T2> dataType,
            FBFilterOption filterOption,
            ResultCallback<Updates<T2>> onUpdate,
            ResultCallback<String> onError
    ) {
        if (_listners == null) {
            _listners = new HashMap<>();
        }

        ChildEventListener listener;

        if (listenToAnyChange) {
            listener = new FirebaseDatabaseOp.ChildEventListener() {
                @Override
                void onAnyChange(DataSnapshot snapshot, Boolean wasAdded) {
                    onChangeReceived(dataType, snapshot, wasAdded, onUpdate, onError);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (onError != null) {
                        onError.invoke(error.getMessage());
                    }
                }
            };
        }
        //
        else {
            listener = new FirebaseDatabaseOp.ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
                    onChangeReceived(dataType, snapshot, true, onUpdate, onError);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    if (onError != null) {
                        onError.invoke(error.getMessage());
                    }
                }
            };
        }

        if (filterOption == null) {
            _listners.put(getRefPath(ref), listener);
            ref.addChildEventListener(listener);
        }
        //
        else {
            Query query = buildQuery(ref, filterOption);
            _listners.put(getRefPath(ref) + "/" + filterOption.toString().hashCode(), listener);
            query.addChildEventListener(listener);
        }
    }

    private <T2> void onChangeReceived(
            Class<T2> dataType,
            DataSnapshot snapshot,
            Boolean wasAdded,
            ResultCallback<Updates<T2>> onUpdate,
            ResultCallback<String> onError
    ) {
        onChangeReceived(
                dataType,
                snapshot,
                wasAdded,
                null,
                onUpdate,
                onError
        );
    }

    private <T2> void onChangeReceived(
            Class<T2> dataType,
            DataSnapshot snapshot,
            Boolean wasAdded,
            ActionCallback<Object, Pair<T2, StringSet>> customConverter,
            ResultCallback<Updates<T2>> onUpdate,
            ResultCallback<String> onError
    ) {
        handleReceivedDataSnapshot(snapshot, dataType, customConverter, result -> {
            if (result.data != null) {
                onUpdate.invoke(new Updates<>(result.data, wasAdded));
            } else {
                if (onError != null) {
                    onError.invoke(
                            "Error in handling value: " + snapshot.getValue() +
                                    "\nKey: " + snapshot.getKey() +
                                    "\nDetails: " + snapshot.toString()
                    );
                }
            }
        });
    }

    //--------------------------------------------------------

    @Override
    public void removeListeners(String subNodePath, FBFilterOption filterOption) {
        var ref = _getReferenceOfNode(subNodePath);
        removeListeners(ref, filterOption);
    }

    public void removeListeners(DatabaseReference ref, FBFilterOption filterOption) {
        if (_listners == null) return;

        if (filterOption == null) {
            var listener = _listners.get(getRefPath(ref));
            if (listener == null) return;

            ref.removeEventListener(listener);
        } else {
            var listener = _listners.get(getRefPath(ref) + "/" + filterOption.toString().hashCode());
            if (listener == null) return;

            ref.removeEventListener(listener);
        }
    }

    //--------------------------------------------------------

    private Map<String, ChildEventListener> _listners;

    private static class ChildEventListener implements com.google.firebase.database.ChildEventListener {
        void onAnyChange(DataSnapshot snapshot, Boolean wasAdded) {
        }

        @Override
        public void onChildAdded(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            onAnyChange(snapshot, true);
        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            onAnyChange(snapshot, false);
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            onAnyChange(snapshot, null);
        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot snapshot, @androidx.annotation.Nullable String previousChildName) {
            onAnyChange(snapshot, null);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
        }
    }

    private String getRefPath(DatabaseReference ref) {
        StringBuilder sb = new StringBuilder();
        sb.append(ref.getKey());

        var parent = ref.getParent();
        while (parent != null) {
            sb.insert(0, "/");
            sb.insert(0, parent.getKey());
            parent = parent.getParent();
        }

        return sb.toString();
    }

    //----------------------------------------------------------------------------

    @Override
    public void clear(ResultCallback<Response<Boolean>> callback) {
        var ref = databaseReference;
        removeNode(ref, callback);
    }

    @Override
    public void removeNode(String subNodePath, ResultCallback<Response<Boolean>> callback) {
        var ref = _getReferenceOfNode(subNodePath);
        removeNode(ref, callback);
    }

    public void removeNode(DatabaseReference ref, ResultCallback<Response<Boolean>> callback) {
        ref.removeValue((error, ref1) -> {
            if (callback != null) {
                if (error == null) {
                    callback.invoke(Response.success(true));
                } else {
                    callback.invoke(Response.failed(new StringSet(error.getMessage())));
                }
            }
        });
    }

    //------------------------------------------------------------------------------

    private Query buildQuery(DatabaseReference ref, @NotNull FBFilterOption filterOption) {
        Query query = ref.orderByChild(filterOption.key);

        if (filterOption.type == FBFilterTypes.Equal) {
            if (filterOption.args instanceof String) {
                query = ref.equalTo((String) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Double) {
                query = ref.equalTo((Double) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Boolean) {
                query = ref.equalTo((Boolean) filterOption.args);
            }
        }
        //
        else if (filterOption.type == FBFilterTypes.GreaterThan) {
            if (filterOption.args instanceof String) {
                query = query.startAfter((String) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Double) {
                query = query.startAfter((Double) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Boolean) {
                query = query.startAfter((Boolean) filterOption.args);
            }
        }
        //
        else if (filterOption.type == FBFilterTypes.GreaterThanOrEqual) {
            if (filterOption.args instanceof String) {
                query = query.startAt((String) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Double) {
                query = query.startAt((Double) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Boolean) {
                query = query.startAt((Boolean) filterOption.args);
            }
        }
        //
        else if (filterOption.type == FBFilterTypes.LessThan) {
            if (filterOption.args instanceof String) {
                query = query.endBefore((String) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Double) {
                query = query.endBefore((Double) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Boolean) {
                query = query.endBefore((Boolean) filterOption.args);
            }
        }
        //
        else if (filterOption.type == FBFilterTypes.LessThanOrEqual) {
            if (filterOption.args instanceof String) {
                query = query.endAt((String) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Double) {
                query = query.endAt((Double) filterOption.args);
            }
            //
            else if (filterOption.args instanceof Boolean) {
                query = query.endAt((Boolean) filterOption.args);
            }
        }

        if (filterOption.limit != null) {
            if (filterOption.limit.fromStart) {
                query = query.limitToFirst(filterOption.limit.count);
            } else {
                query = query.limitToLast(filterOption.limit.count);
            }
        }

        return query;
    }
}
