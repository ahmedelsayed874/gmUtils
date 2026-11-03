package gmutils.firebase.configs;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gmutils.listeners.ResultCallback;
import gmutils.logger.LoggerAbs;

public class FirebaseConfigs {
    private static volatile FirebaseConfigs _instance;

    public static FirebaseConfigs createInstance(@NotNull List<FBConfigSet> configSets) {
        return createInstance(configSets, null);
    }

    public static FirebaseConfigs createInstance(List<FBConfigSet> configSets, LoggerAbs logger) {
        synchronized (FirebaseConfigs.class) {
            if (_instance == null) _instance = new FirebaseConfigs(configSets, logger);
            return _instance;
        }
    }

    public static FirebaseConfigs getInstance() {
        synchronized (FirebaseConfigs.class) {
            if (_instance == null) throw new RuntimeException(
                    "must use FirebaseConfigs.createInstance() first"
            );

            return _instance;
        }
    }

    //----------------------------------------------------------------------------------------------

    private final List<FBConfigSet> configSets;
    private final LoggerAbs logger;
    private FirebaseRemoteConfig firebaseRemoteConfig;

    public FirebaseConfigs(@NotNull List<FBConfigSet> configSets, LoggerAbs logger) {
        this.configSets = configSets;
        this.logger = logger;

        log(() -> " INIT...");

        FirebaseRemoteConfigSettings configSettings = null;

        try {
            firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(20)
                    .build();
        } catch (Exception e) {
            firebaseRemoteConfig = null;
            log(() -> " EXCEPTION:: " + e);
        }

        if (configSettings != null) {
            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        }

        Map<String, Object> defaults = new HashMap<>();

        for (FBConfigSet configSet : configSets) {
            Map<String, Object> def = configSet.getDefaults();
            if (def == null || def.isEmpty()) continue;
            defaults.putAll(def);
        }

        if (firebaseRemoteConfig != null) {
            log(() -> "SETTING DEFAULTs...");

            firebaseRemoteConfig
                    .setDefaultsAsync(defaults)
                    .addOnCompleteListener((it) -> {
                        log(() -> "firebaseRemoteConfig.setDefaultsAsync:: " + it.isSuccessful());
                        isFetchReady = true;
                        if (!isFetchCalled) fetch(null);
                    });
        }
        //
        else {
            log(() -> "RETURNING DEFAULT (init failed)...");

            for (FBConfigSet configSet : configSets) {
                configSet.onFetchComplete(cachedConfigs(), false);
            }
        }
    }

    private boolean isFetchReady = false;
    private boolean isFetchCalled = false;

    public void fetch(ResultCallback<Exception> callback) {
        isFetchCalled = true;
        if (!isFetchReady) return;

        log(() -> " FETCHING...");

        int x = 0;
        long tn = System.currentTimeMillis();
        for (FBConfigSet configSet : configSets) {
            if (configSet.fetchTime() != null) {
                long d = tn - configSet.fetchTime();
                if (d < configSet.getCacheInterval()) x++;
            }
        }
        if (configSets.size() == x) {
            log(() -> " DATA ALREADY FETCHED");
            for (FBConfigSet configSet : configSets) {
                configSet.onFetchComplete(cachedConfigs(), false);
            }
            return;
        }

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean updated = task.getResult();
                log(() -> " Config params updated: " + updated);

                if (callback != null) {
                    callback.invoke(null);
                }

                for (FBConfigSet configSet : configSets) {
                    configSet.onFetchComplete(cachedConfigs(), true);
                }
            }
            //
            else {
                log(() -> "Config fetch failed (" +
                        (task.getException() == null ? "--" : task.getException().getMessage()) +
                        ") .... RETURNING DEFAULT"
                );

                if (callback != null) {
                    callback.invoke(task.getException());
                }

                for (FBConfigSet configSet : configSets) {
                    configSet.onFetchComplete(cachedConfigs(), false);
                }
            }
        });
    }

    public Map<String, FirebaseRemoteConfigValue> cachedConfigs() {
        try {
            return firebaseRemoteConfig.getAll();
        } catch (Exception ignore) {
            return null;
        }
    }

    public int getConfigSetsCount() {
        return configSets.size();
    }

    public FBConfigSet getConfigSet(int index) {
        return configSets.get(index);
    }

    private void log(LoggerAbs.ContentGetter content) {
        if (logger != null) {
            logger.print(
                    () -> this.getClass().getSimpleName(),
                    content
            );
        }
    }
}
