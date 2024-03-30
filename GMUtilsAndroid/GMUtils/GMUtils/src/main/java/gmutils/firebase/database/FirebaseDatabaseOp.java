package gmutils.firebase.database;

import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import org.jetbrains.annotations.Nullable;

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

class FirebaseDatabaseOp extends IFirebaseDatabaseOp {
    private DatabaseReference databaseReference;

    public FirebaseDatabaseOp(String rootNodeName) {
        try {
            Class.forName("com.google.firebase.database.FirebaseDatabase");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "//https://firebase.google.com/docs/database/android/start\n" +
                    "implementation 'com.google.firebase:firebase-database:19.6.0'");
        }

        databaseReference = FirebaseDatabase.getInstance().getReference(rootNodeName);
    }

    //----------------------------------------------------------------------------

    private String _refineKeyName(String name) {
        return FirebaseUtils.refineKeyName(name);
    }

    private DatabaseReference _getReferenceOfNode(String subNodePath) {
        if (subNodePath == null) return databaseReference;

        try {
            subNodePath = FirebaseUtils.refinePathFragmentNames(subNodePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var ref = databaseReference;
        return ref.child(subNodePath);
    }

    //----------------------------------------------------------------------------

    @Override
    public <T> void saveData(T data, @Nullable String subNodePath, ResultCallback<Response<Boolean>> callback) {
        DatabaseReference ref = _getReferenceOfNode(subNodePath);
        saveData(ref, data, callback);
    }

    public <T> void saveData(DatabaseReference ref, T data, ResultCallback<Response<Boolean>> callback) {
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
    public <T> void saveMultipleData(Map<String, T> nodesAndData, ResultCallback<Response<Boolean>> callback) {
        DatabaseReference ref = _getReferenceOfNode(null);
        saveMultipleData(ref, nodesAndData, callback);
    }

    public <T> void saveMultipleData(DatabaseReference ref, Map<String, T> nodesAndData, ResultCallback<Response<Boolean>> callback) {
        ref.setValue(nodesAndData)
                .addOnSuccessListener(task -> {
                    if (callback != null) callback.invoke(Response.success(true));
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (callback != null)
                            callback.invoke(Response.failed(new StringSet(e.getMessage())));
                    }
                });
    }

    //----------------------------------------------------------------------------

    protected BackgroundTaskAbs backgroundTask() {
        return BackgroundTaskAbs.getInstance();
    }

    @Override
    public <T> void retrieveAll(FBFilterOption filterOption, Class<T> dataType, ActionCallback<Object, List<T>> collectionSource, ResultCallback<Response<List<T>>> callback) {
        /*isConnectionAvailable(r -> {
            if (r) {
                retrieveAll2(filterOption, collectionSource, callback);
            } else {
                callback.invoke(
                        Response.failed(
                                new StringSet("No Connection", "لا يوجد اتصال"),
                                true
                        )
                );
            }
        });*/

        var ref = databaseReference;

        if (filterOption != null) {
            ref.orderByChild(filterOption.key);

            if (filterOption.type == FBFilterTypes.equal) {
                if (filterOption.args instanceof String) {
                    ref.equalTo((String) filterOption.args);
                } else if (filterOption.args instanceof Double) {
                    ref.equalTo((Double) filterOption.args);
                } else if (filterOption.args instanceof Boolean) {
                    ref.equalTo((Boolean) filterOption.args);
                }
            } else if (filterOption.type == FBFilterTypes.greaterThan) {
                if (filterOption.args instanceof String) {
                    ref.startAfter((String) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Double) {
                    ref.startAfter((Double) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Boolean) {
                    ref.startAfter((Boolean) filterOption.args, filterOption.key);
                }
            } else if (filterOption.type == FBFilterTypes.greaterThanOrEqual) {
                if (filterOption.args instanceof String) {
                    ref.startAt((String) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Double) {
                    ref.startAt((Double) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Boolean) {
                    ref.startAt((Boolean) filterOption.args, filterOption.key);
                }
            } else if (filterOption.type == FBFilterTypes.lessThan) {
                if (filterOption.args instanceof String) {
                    ref.endBefore((String) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Double) {
                    ref.endBefore((Double) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Boolean) {
                    ref.endBefore((Boolean) filterOption.args, filterOption.key);
                }
            } else if (filterOption.type == FBFilterTypes.lessThanOrEqual) {
                if (filterOption.args instanceof String) {
                    ref.endAt((String) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Double) {
                    ref.endAt((Double) filterOption.args, filterOption.key);
                } else if (filterOption.args instanceof Boolean) {
                    ref.endAt((Boolean) filterOption.args, filterOption.key);
                }
            }

            if (filterOption.limit != null) {
                ref.limitToFirst(filterOption.limit);
            }
        }

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();

                if (snapshot.exists()) {
                    backgroundTask().execute(() -> {
                        List<T> list = new ArrayList<>();
                        Object value = snapshot.getValue();
                        if (value != null) {
                            if (collectionSource == null) {
                                try {
                                    if (value instanceof List) {
                                        GenericTypeIndicator<List<T>> type = new GenericTypeIndicator<List<T>>() {
                                        };
                                        List<T> values = snapshot.getValue(type);
                                        list.addAll(values);
                                    }
                                    //
                                    else if (value instanceof Map) {
                                        try {
                                            GenericTypeIndicator<Map<String, T>> type = new GenericTypeIndicator<>() {
                                            };
                                            Map<String, T> map = snapshot.getValue(type);
                                            list.addAll(map.values());
                                        } catch (Exception e) {
                                            GenericTypeIndicator<Map<String, List<T>>> type = new GenericTypeIndicator<>() {
                                            };
                                            Map<String, List<T>> map = snapshot.getValue(type);
                                            for (var entry : map.values()) {
                                                list.addAll(entry);
                                            }
                                        }
                                    }
                                    //
                                    else {
                                        T value2 = snapshot.getValue(dataType);
                                        list.add(value2);
                                    }
                                } catch (Exception e) {
                                    Log.e("*****", e.getMessage() + "\n--------\n" + value);
                                }
                            }
                            //
                            else {
                                var values = collectionSource.invoke(value);
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
            } else {
                callback.invoke(Response.failed(
                        new StringSet(task.getException() != null ? task.getException().getMessage() : "Retrieving failed")
                ));
            }
        });
    }

    //----------------------------------------------------------------------------

    @Override
    public <T> void retrieveSingle(String subNodePath, Class<T> dataType, ResultCallback<Response<T>> callback) {
        var ref = _getReferenceOfNode(subNodePath);
        retrieveSingle(ref, dataType, callback);
    }

    public <T> void retrieveSingle(
            DatabaseReference ref,
            Class<T> dataType,
            ResultCallback<Response<T>> callback
    ) {
        /*if (isConnectionAvailable() == false) {
            return Response.failed(new StringSet('No Connection', 'لا يوجد اتصال'), true);
        }*/

        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                handleReceivedDataSnapshot(snapshot, dataType, callback);

            } else {
                callback.invoke(Response.failed(
                        new StringSet(task.getException() != null ? task.getException().getMessage() : "Retrieving failed")
                ));
            }
        });
    }

    private <T2> void handleReceivedDataSnapshot(DataSnapshot snapshot, Class<T2> dataType, ResultCallback<Response<T2>> callback) {
        if (snapshot.exists()) {
            backgroundTask().execute(() -> {
                Pair<T2, StringSet> data = null;

                if (snapshot.getValue() != null) {
                    T2 obj = null;
                    try {
                        obj = snapshot.getValue(dataType);
                    } catch (Exception ignored) {
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
            ResultCallback<Updates<T>> onChange,
            ResultCallback<String> onError
    ) {
        listenToChanges(
                _getReferenceOfNode(subNodePath),
                dataType,
                onChange,
                onError
        );
    }

    public <T> void listenToChanges(
            DatabaseReference ref,
            Class<T> dataType,
            ResultCallback<Updates<T>> onChange,
            ResultCallback<String> onError
    ) {
        _listenTo(
                ref,
                true,
                dataType,
                onChange,
                onError
        );
    }

    @Override
    public <N> void listenToChangesSpecific(String subNodePath, Class<N> dataType, ResultCallback<Updates<N>> onChange, ResultCallback<String> onError) {
        _listenTo(
                _getReferenceOfNode(subNodePath),
                true,
                dataType,
                onChange,
                onError
        );
    }

    @Override
    public <T> void listenToAdding(
            String subNodePath,
            Class<T> dataType,
            ResultCallback<Updates<T>> onAdd,
            ResultCallback<String> onError
    ) {
        listenToAdding(
                _getReferenceOfNode(subNodePath),
                dataType,
                onAdd,
                onError
        );
    }

    public <T> void listenToAdding(
            DatabaseReference ref,
            Class<T> dataType,
            ResultCallback<Updates<T>> onAdd,
            ResultCallback<String> onError
    ) {
        _listenTo(
                ref,
                false,
                dataType,
                onAdd,
                onError
        );
    }

    @Override
    public <N> void listenToAddingSpecific(String subNodePath, Class<N> dataType, ResultCallback<Updates<N>> onAdd, ResultCallback<String> onError) {
        _listenTo(
                _getReferenceOfNode(subNodePath),
                false,
                dataType,
                onAdd,
                onError
        );
    }

    private <T2> void _listenTo(
            DatabaseReference ref,
            boolean listenToAnyChange,
            Class<T2> dataType,
            ResultCallback<Updates<T2>> onUpdate,
            ResultCallback<String> onError
    ) {
        if (_listners == null) {
            _listners = new HashMap<>();
        }

        if (listenToAnyChange) {
            var listener = new ChildEventListener() {
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
            _listners.put(getRefPath(ref), listener);
            ref.addChildEventListener(listener);
        } else {
            var listener = new ChildEventListener() {
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
            _listners.put(getRefPath(ref), listener);
            ref.addChildEventListener(listener);
        }
    }

    private <T2> void onChangeReceived(
            Class<T2> dataType,
            DataSnapshot snapshot,
            Boolean wasAdded,
            ResultCallback<Updates<T2>> onUpdate,
            ResultCallback<String> onError
    ) {
        handleReceivedDataSnapshot(snapshot, dataType, result -> {
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
    public void removeListeners(String subNodePath) {
        var ref = _getReferenceOfNode(subNodePath);
        removeListeners(ref);
    }

    public void removeListeners(DatabaseReference ref) {
        if (_listners == null) return;

        var listener = _listners.get(getRefPath(ref));
        if (listener == null) return;

        ref.removeEventListener(listener);
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
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@androidx.annotation.Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (callback != null) {
                    if (error == null) {
                        callback.invoke(Response.success(true));
                    } else {
                        callback.invoke(Response.failed(new StringSet(error.getMessage())));
                    }
                }
            }
        });
    }

}
