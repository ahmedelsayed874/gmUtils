package gmutils.firebase.configs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

import gmutils.DateOp;

public abstract class FBConfigSet {
    public abstract Map<String, Object> getDefaults();

    private long cacheInterval = DateOp.ONE_MINUTE_MILLIS * 5;
    private Long fetchTime = null;

    void onFetchComplete(@Nullable FirebaseRemoteConfig firebaseRemoteConfig, boolean success) {
        onFetchCompleteAbs(firebaseRemoteConfig, success);

        if (firebaseRemoteConfig != null) {
            fetchTime = success ? System.currentTimeMillis() : null;
        }

        ((MutableLiveData) configs).postValue(this);
    }

    protected abstract void onFetchCompleteAbs(@Nullable FirebaseRemoteConfig firebaseRemoteConfig, boolean success);

    public void setCacheInterval(long cacheInterval) {
        this.cacheInterval = cacheInterval;
    }

    public long getCacheInterval() {
        return cacheInterval;
    }

    public Long fetchTime() {
        return fetchTime;
    }

    //----------------------------------

    public final LiveData<FBConfigSet> configs = new MutableLiveData<>();

}
