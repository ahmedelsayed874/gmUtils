package gmutils.logger;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import gmutils.DateOp;

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
        return instance(logId, (LogConfigs) null);
    }

    public static LoggerAbs instance(String logId, DateOp deadline) {
        LogConfigs logsOptions = null;
        if (deadline != null) {
            logsOptions = new LogConfigs()
                    .setLogDeadline(deadline)
                    .setWriteLogsToPublicFileDeadline(deadline)
                    .setWriteLogsToPrivateFileDeadline(deadline);
        }
        return instance(logId, logsOptions);
    }

    public static LoggerAbs instance(String logId, LogConfigs logsOptions) {
        if (logId != null && logId.isEmpty()) logId = null;
        if (_instances == null) _instances = new HashMap<>();
        if (!_instances.containsKey(logId)) {
            Logger logger = new Logger(logId);
            logger.setLogConfigs(logsOptions);
            _instances.put(logId, logger);
        }
        return _instances.get(logId);
    }

    public static Set<String> loggersNames() {
        if (_instances == null) _instances = new HashMap<>();
        return _instances.keySet();
    }

    public static Collection<LoggerAbs> loggers() {
        if (_instances == null) _instances = new HashMap<>();
        return _instances.values();
    }

    public static void printToAll(ContentGetter content) {
        for(LoggerAbs logger : loggers()) {
            logger.print(content);
        }
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
