package gmutils.firebase.database;

import android.util.Pair;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

import gmutils.StringSet;
import gmutils.firebase.Response;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ResultCallback;
import gmutils.listeners.ResultCallback2;

public abstract class IFirebaseDatabaseOp {

    /*public void isConnectionAvailable(ResultCallback<Boolean> callback) {
        FirebaseUtils.isConnectionAvailable(callback);
    }*/

    //----------------------------------------------------------------------------

    public abstract <T> void saveData(T data, @Nullable String subNodePath, ResultCallback<Response<Boolean>> callback);

    public abstract <T> void saveMultipleData(Map<String, T> nodesAndData, ResultCallback<Response<Boolean>> callback);

    //----------------------------------------------------------------------------

    public abstract <T> void retrieveAll(
            FBFilterOption filterOption,
            Class<T> dataType,
            ActionCallback<Object, List<T>> customConverter,
            ResultCallback<Response<List<T>>> callback
    );

    //----------------------------------------------------------------------------

    public abstract <T> void retrieveSingle(
            String subNodePath,
            Class<T> dataType,
            ActionCallback<Object, Pair<T, StringSet>> customConverter,
            ResultCallback<Response<T>> callback
    );

    //----------------------------------------------------------------------------

    public static class Updates<T> {
        public final T item;
        public final Boolean isNew;

        public Updates(T item, Boolean isNew) {
            this.item = item;
            this.isNew = isNew;
        }

        @Override
        public String toString() {
            return "Updates{" +
                    "item=" + item +
                    ", isNew=" + isNew +
                    '}';
        }
    }
    
    public abstract <T> void listenToChanges(
            String subNodePath,
            Class<T> dataType,
            ResultCallback<Updates<T>> onChange,
            //Runnable onDone,
            ResultCallback<String> onError
    );

    public abstract <N> void listenToChangesSpecific(
            String subNodePath,
            Class<N> dataType,
            ResultCallback<Updates<N>> onChange,
            //Runnable onDone,
            ResultCallback<String> onError
    );

    public abstract <T> void listenToAdding(
            String subNodePath,
            Class<T> dataType,
            ResultCallback<Updates<T>> onAdd,
            //Runnable onDone,
            ResultCallback<String> onError
    );

    public abstract <N> void listenToAddingSpecific(
            String subNodePath,
            Class<N> dataType,
            ResultCallback<Updates<N>> onAdd,
            //Runnable onDone,
            ResultCallback<String> onError
    );

    public abstract void removeListeners(String subNodePath);

    //----------------------------------------------------------------------------

    public abstract void clear(ResultCallback<Response<Boolean>> callback);

    public abstract void removeNode(String subNodePath, ResultCallback<Response<Boolean>> callback);
}
