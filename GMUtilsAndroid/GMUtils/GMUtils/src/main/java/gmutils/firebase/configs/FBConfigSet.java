package gmutils.firebase.configs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public abstract class FBConfigSet {
    public abstract Map<String, Object> getDefaults();

    private Boolean isFetchCompleted = null;

    void onFetchComplete(@Nullable FirebaseRemoteConfig firebaseRemoteConfig, boolean success) {
        onFetchCompleteAbs(firebaseRemoteConfig, success);

        if (firebaseRemoteConfig != null) {
            isFetchCompleted = success;
        }

        ((MutableLiveData) configs).postValue(this);
    }

    protected abstract void onFetchCompleteAbs(@Nullable FirebaseRemoteConfig firebaseRemoteConfig, boolean success);

    public Boolean isFetchCompleted() {
        return isFetchCompleted;
    }

    //----------------------------------

    public final LiveData<FBConfigSet> configs = new MutableLiveData<>();

}
