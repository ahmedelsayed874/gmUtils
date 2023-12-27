package gmutils.logger;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

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
public class Logger extends LoggerAbs {
    private static Map<String, LoggerAbs> _instances;

    /**
     * d: for default
     */
    public static LoggerAbs d() {
        return instance(null);
    }

    public static LoggerAbs instance(String logId) {
        if (logId != null && logId.isEmpty()) logId = null;
        if (_instances == null) _instances = new HashMap<>();
        if (!_instances.containsKey(logId)) _instances.put(logId, new Logger(logId));
        return _instances.get(logId);
    }

    //----------------------------------------------------------------------------------------------

    private Logger(String logId) {
        super(logId);
    }

    @Override
    protected void writeToLog(String tag, String msg) {
        Log.e(tag, msg);
    }

}
