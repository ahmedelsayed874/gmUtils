package gmutils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gmutils.app.BaseApplication;
import gmutils.security.Security;

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
public class Logger {
    private final static int DEF_ENC_KEY = 112439;
    public int MAX_LOG_FILES_COUNT = 50;

    public static class LogConfigs {
        private DateOp logDeadline;
        private DateOp writeToFileDeadline;
        private DateOp writeLogsToFileDeadline;
        private DateOp fileContentEncryptionDeadline;

        private Integer fileContentEncryptionKey;

        //region set printing logs deadline
        public LogConfigs setLogDeadline(int d, int M, int y) {
            return setLogDeadline(d, M, y, 23, 59);
        }

        public LogConfigs setLogDeadline(int d, int M, int y, int h, int m) {
            return setLogDeadline(DateOp.getInstance().setDate(d, M, y).setTime(h, m, 59));
        }

        public LogConfigs setLogDeadline(DateOp dateOp) {
            this.logDeadline = dateOp;
            return this;
        }

        //endregion set printing logs deadline

        //region set writing to file deadline
        public LogConfigs setWriteToFileDeadline(int d, int M, int y) {
            return setWriteToFileDeadline(d, M, y, 23, 59);
        }

        public LogConfigs setWriteToFileDeadline(int d, int M, int y, int h, int m) {
            return setWriteToFileDeadline(DateOp.getInstance().setDate(d, M, y).setTime(h, m, 59));
        }

        public LogConfigs setWriteToFileDeadline(DateOp dateOp) {
            this.writeToFileDeadline = dateOp;
            return this;
        }
        //endregion set writing to file deadline

        //region set writing <<<logs>>> to file deadline
        public LogConfigs setWriteLogsToFileDeadline(int d, int M, int y) {
            return setWriteLogsToFileDeadline(d, M, y, 23, 59);
        }

        public LogConfigs setWriteLogsToFileDeadline(int d, int M, int y, int h, int m) {
            return setWriteLogsToFileDeadline(DateOp.getInstance().setDate(d, M, y).setTime(h, m, 59));
        }

        public LogConfigs setWriteLogsToFileDeadline(DateOp dateOp) {
            this.writeLogsToFileDeadline = dateOp;
            return this;
        }
        //endregion set writing <<<logs>>> to file deadline

        //region set encrypting file content deadline
        public LogConfigs setFileContentEncryptionDeadline(int encryptKey, int d, int M, int y) {
            return setFileContentEncryptionDeadline(encryptKey, d, M, y, 23, 59);
        }

        public LogConfigs setFileContentEncryptionDeadline(int encryptKey, int d, int M, int y, int h, int m) {
            return setFileContentEncryptionDeadline(encryptKey, DateOp.getInstance().setDate(d, M, y).setTime(h, m, 59));
        }

        public LogConfigs setFileContentEncryptionDeadline(int encryptKey, DateOp dateOp) {
            this.fileContentEncryptionDeadline = dateOp;
            this.fileContentEncryptionKey = encryptKey;
            return this;
        }
        //endregion set encrypting file content deadline

        //region check enable status
        public boolean isLogEnabled() {
            return logDeadline != null && logDeadline.getTimeInMillis() >= System.currentTimeMillis();
        }

        public boolean isWriteToFileEnabled() {
            return writeToFileDeadline != null && writeToFileDeadline.getTimeInMillis() >= System.currentTimeMillis();
        }

        public boolean isWriteLogsToFileEnabled() {
            return writeLogsToFileDeadline != null && writeLogsToFileDeadline.getTimeInMillis() >= System.currentTimeMillis();
        }

        public boolean isFileContentEncryptEnabled() {
            return fileContentEncryptionDeadline != null && fileContentEncryptionDeadline.getTimeInMillis() >= System.currentTimeMillis();
        }
        //endregion check enable status

        @Override
        public String toString() {
            return "LogConfigs{" +
                    "logDeadline=" + logDeadline +
                    ", writeToFileDeadline=" + writeToFileDeadline +
                    ", writeLogsToFileDeadline=" + writeLogsToFileDeadline +
                    ", fileContentEncryptionDeadline=" + fileContentEncryptionDeadline +
                    ", fileContentEncryptionKey=" + fileContentEncryptionKey +
                    '}';
        }
    }

    //----------------------------------------------------------------------------------------------

    private static Map<String, Logger> _instances;

    /**
     * d: for default
     */
    public static Logger d() {
        return instance(null);
    }

    public static Logger instance(String logId) {
        if (_instances == null) _instances = new HashMap<>();
        if (!_instances.containsKey(logId)) _instances.put(logId, new Logger(logId));
        return _instances.get(logId);
    }

    //----------------------------------------------------------------------------------------------

    private final String logId;
    private LogConfigs logConfigs;

    public Logger(@Nullable String logId) {
        this.logId = logId;
        this.logConfigs = new LogConfigs();
    }

    @NotNull
    private String logId() {
        if (logId != null && !(logId.trim()).isEmpty()) return logId.trim();
        return "";
    }

    //----------------------------------------------------------------------------------------------

    public void setLogConfigs(@NotNull LogConfigs logConfigs) {
        if (logConfigs == null) throw new IllegalArgumentException();
        this.logConfigs = logConfigs;
    }

    @NotNull
    public LogConfigs getLogConfigs() {
        return logConfigs;
    }

    //----------------------------------------------------------------------------------------------

    public interface TitleGetter {
        String getTitle();
    }

    public interface ContentGetter {
        Object getContent();
    }

    //region LOGs
    public void print(Throwable throwable) {
        print(null, throwable);
    }

    public void print(TitleGetter title, Throwable throwable) {
        print(
                () -> "EXCEPTION **** " + (title == null ? "" : title.getTitle()),
                () -> throwable == null ? "null" : throwable.toString()
        );
    }

    //----------------

    public void print(@NotNull ContentGetter callback) {
        print(null, callback);
    }

    public void print(TitleGetter title, @NotNull ContentGetter callback) {
        print(title, callback, logConfigs.isWriteLogsToFileEnabled());
    }

    public void print(TitleGetter title, @NotNull ContentGetter callback, boolean writeToFileAlso) {
        String content = "";

        if (logConfigs.isLogEnabled() || writeToFileAlso) {
            content = ("" + callback.getContent());
        }

        if (logConfigs.isLogEnabled()) {
            String title2 = "**** ";
            if (!logId().isEmpty()) title2 += "|" + logId() + "| ";
            if (title != null) title2 += title.getTitle();

            String[] t = refineTitle(title2);
            String[] m = divideLogMsg(content, t.length > 1 ? t[1].length() : 0);

            if (m.length == 1) {
                if (t.length == 1)
                    Log.e(t[0], m[0]);
                else
                    Log.e(t[0], t[1] + ": " + m[0]);
            } else {
                for (int i = 0; i < m.length; i++) {
                    if (t.length == 1) {
                        Log.e(t[0], "LOG[" + i + "]-> " + m[i]);
                    } else {
                        Log.e(t[0], t[1] + ": " + "LOG[" + i + "]-> " + m[i]);
                    }
                }
            }
        }

        if (writeToFileAlso) {
            if (BaseApplication.current() != null) {
                try {
                    writeToFile(BaseApplication.current(), content);//, "APP_LOGS");
                } catch (Exception e) {
                }
            }
        }
    }

    //----------------

    public void printMethod() {
        printMethod(null, logConfigs.isWriteLogsToFileEnabled(), 0);
    }

    public void printMethod(int exceptionIndexTuner) {
        printMethod(null, logConfigs.isWriteLogsToFileEnabled(), exceptionIndexTuner);
    }

    public void printMethod(ContentGetter moreInfoCallback) {
        printMethod(moreInfoCallback, logConfigs.isWriteLogsToFileEnabled(), 0);
    }

    public void printMethod(ContentGetter moreInfoCallback, int exceptionIndexTuner) {
        printMethod(moreInfoCallback, logConfigs.isWriteLogsToFileEnabled(), exceptionIndexTuner);
    }

    public void printMethod(ContentGetter moreInfoCallback, boolean writeToFileAlso) {
        printMethod(moreInfoCallback, writeToFileAlso, 0);
    }

    public void printMethod(ContentGetter moreInfoCallback, boolean writeToFileAlso, int exceptionIndexTuner) {
        try {
            StackTraceElement[] stackTraceList = new Throwable().getStackTrace();
            StackTraceElement stackTrace = null;
            int idx = -1;
            for (int s = 1; s < stackTraceList.length; s++) {
                if (!Logger.class.getName().equals(stackTraceList[s].getClassName())) {
                    idx = s;
                    break;
                }
            }

            stackTrace = stackTraceList[idx + exceptionIndexTuner];

            String moreInfo = "";
            if (moreInfoCallback != null) {
                Object content = moreInfoCallback.getContent();
                if (content != null) {
                    moreInfo = content.toString();
                }
            }

            String msg = stackTrace.getFileName() +
                    "\n" +
                    stackTrace.getMethodName() +
                    "() -> line: " + stackTrace.getLineNumber() +
                    (TextUtils.isEmpty(moreInfo) ? "" : ("\n-> " + moreInfo));

            print(null, () -> msg, writeToFileAlso);
        } catch (Exception e) {
            print(null, e::toString, writeToFileAlso);
        }
    }

    //----------------------------------------------------------------------------------------------

    private String[] refineTitle(String title) {
        String reqTitle, remTitle;
        if (title.length() > 23) {
            reqTitle = (title).substring(0, 23);
            remTitle = (title).substring(23);
        } else {
            reqTitle = title;
            remTitle = "";
        }

        return new String[]{reqTitle, remTitle};
    }

    private final int MAX_LOG_LENGTH = 4000;

    private String[] divideLogMsg(String msg, int offset) {
        if (msg == null) msg = "null";
        final int partLength = MAX_LOG_LENGTH - (offset == 0 ? 0 : (offset + 2));

        int parts = (int) Math.ceil(msg.length() / ((float) partLength));

        if (parts <= 1) {
            return new String[]{msg};

        } else {
            String[] strings = new String[parts];
            int i = 0;

            while (i < parts) {
                try {
                    int fi = i * (partLength);
                    int li = fi + (partLength);
                    if (li < msg.length()) {
                        strings[i] = msg.substring(fi, li); //(0,4000), (4000, 8000), (8000, 12000)
                    } else {
                        strings[i] = msg.substring(fi);
                    }
                } catch (Exception e) {
                }

                i++;
            }

            return strings;
        }
    }
    //endregion LOGs

    //----------------------------------------------------------------------------------------------

    //region write to files
    public void writeToFile(Context context, String text) {
        writeToFile(context, text, null);
    }

    public void writeToFile(Context context, String text, String fileName) {
        if (logConfigs.isWriteToFileEnabled() || logConfigs.isWriteLogsToFileEnabled()) {
            createLogFileWriter(context, fileName).append(text);
        } else {
            if (!logConfigs.isWriteToFileEnabled() || !logConfigs.isWriteLogsToFileEnabled()) {
                deleteSavedFiles(context);
            }
        }
    }

    public void writeToFile(Context context, String text, String dirName, String fileName) {
        if (logConfigs.isWriteToFileEnabled() || logConfigs.isWriteLogsToFileEnabled()) {
            createLogFileWriter(context, fileName).append(text);
        } else {
            if (!logConfigs.isWriteToFileEnabled() || !logConfigs.isWriteLogsToFileEnabled()) {
                deleteSavedFiles(context, dirName);
            }
        }
    }

    public void writeToFile(File file, String text) {
        if (logConfigs.isWriteToFileEnabled() || logConfigs.isWriteLogsToFileEnabled()) {
            createLogFileWriter(file).append(text);
        } else {
            if (!logConfigs.isWriteToFileEnabled() || !logConfigs.isWriteLogsToFileEnabled()) {
                try {
                    file.deleteOnExit();
                } catch (Exception ignored) {
                }
            }
        }
    }
    //endregion write to files

    //region read files
    public String readFromCurrentSessionFile(Context context) {
        try {
            File file = createOrGetLogFile(context);
            String text = readFileContent(file);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String readFile(Context context, String fileName) {
        try {
            File file = createOrGetLogFile(context, fileName);
            String text = readFileContent(file);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String readFile(Context context, String dirName, String fileName) {
        try {
            File file = createOrGetLogFile(context, dirName, fileName);
            String text = readFileContent(file);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String readAllFilesContents(Context context) {
        String content = "";

        try {
            File logFiles = getLogDirector(context);
            String[] list = logFiles.list();

            if (list != null) {
                for (String fileName : list) {
                    try {
                        File f = new File(logFiles.getPath() + "/" + fileName);
                        content += fileName;
                        content += ":\n\n";
                        content += readFileContent(f);
                        content += "\n\n+++++++++++ END_OF_FILE<" + fileName + "> +++++++++++++\n\n";

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

    public String readFileContent(File file) {
        return createLogFileWriter(file).readFileContent();
    }
    //endregion read files

    //region list saved files
    public List<File> getSavedFiles(Context context) {
        File logFiles = getLogDirector(context);
        return getSavedFiles(logFiles);
    }

    public List<File> getSavedFiles(Context context, String dirName) {
        File logFiles = getLogDirector(context, dirName);
        return getSavedFiles(logFiles);
    }

    public List<File> getSavedFiles(File logFiles) {
        List<File> savedFiles = new ArrayList<>();

        try {
            String[] list = logFiles.list();

            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    try {
                        File f = new File(logFiles.getPath() + "/" + list[i]);
                        savedFiles.add(f);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return savedFiles;
    }
    //endregion list saved files

    //region delete files
    public void deleteSavedFiles(Context context) {
        List<File> files = getSavedFiles(context);
        for (File file : files) {
            try {
                file.delete();
            } catch (Exception e) {
            }
        }
    }

    public void deleteSavedFiles(Context context, String dirName) {
        List<File> files = getSavedFiles(context, dirName);
        for (File file : files) {
            try {
                file.delete();
            } catch (Exception e) {
            }
        }
    }

    //---------------------------

    public void deleteSavedFile(Context context, String fileName) {
        try {
            File file = createOrGetLogFile(context, fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteSavedFile(Context context, String dirName, String fileName) {
        try {
            File file = createOrGetLogFile(context, dirName, fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion delete files

    //region create file
    public String getLogFilesPath(Context context) {
        return getLogDirector(context).getAbsolutePath();
    }

    //--------------------------------------------

    private String _sessionId;

    private String sessionId() {
        if (_sessionId == null) {
            _sessionId = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.ENGLISH).format(new Date());
        }
        return _sessionId;
    }

    private synchronized File createOrGetLogFile(Context context) {
        return createOrGetLogFile(context, "/LOG_FILE_" + sessionId());
    }

    private synchronized File createOrGetLogFile(Context context, String fileName) {
        File logFiles = getLogDirector(context);
        return createOrGetLogFile(logFiles, fileName);
    }

    private synchronized File createOrGetLogFile(Context context, String dirName, String fileName) {
        File logFiles = getLogDirector(context, dirName);
        return createOrGetLogFile(logFiles, fileName);
    }

    private synchronized File createOrGetLogFile(File logFilesDir, @NotNull String fileName) {
        try {
            if (TextUtils.isEmpty(fileName)) {
                fileName = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.ENGLISH).format(new Date());
            }

            String filePath = logFilesDir.getPath() + "/" + fileName + ".txt";
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // get/create directory
    private synchronized File getLogDirector(Context context) {
        String dirName = "LOGS";
        if (!logId().isEmpty()) dirName += "/" + logId();

        return getLogDirector(context, dirName);
    }

    private synchronized File getLogDirector(Context context, String dirName) {
        File filesDir = context.getExternalFilesDir(null);
        return getLogDirector(filesDir.getPath() + "/" + dirName);
    }

    private synchronized File getLogDirector(String dirPath) {
        try {
            File logFiles = new File(dirPath);
            if (!logFiles.exists()) {
                logFiles.mkdirs();
            }

            String[] list = logFiles.list();
            final int max_file_count = MAX_LOG_FILES_COUNT;

            if (list != null && list.length > max_file_count) {
                int fc = list.length - max_file_count;
                for (int i = 0; i < fc; i++) {
                    try {
                        File f2d = new File(logFiles.getPath() + "/" + list[i]);
                        f2d.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return logFiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    //endregion create file

    //----------------------------------------------------------------------------------------------

    public static class LogFileWriter {
        private final File file;
        private final boolean enableEncryption;
        private final Integer encryptionKey;

        public LogFileWriter(File file, boolean enableEncryption, Integer encryptionKey) {
            this.file = file;
            this.enableEncryption = enableEncryption;
            this.encryptionKey = encryptionKey;
        }

        public void append(String text) {
            append(text, true, true);
        }

        public void append(String text, boolean addDate, boolean addSeparation) {
            try {
                OutputStream os = new FileOutputStream(file, true);
                OutputStreamWriter sw = new OutputStreamWriter(os);
                try {
                    if (addDate) {
                        String date = new Date().toString();
                        sw.write(date);
                        sw.write(":\n");
                    }

                    try {
                        if (enableEncryption) {
                            int encryptionKey2 = encryptionKey != null ? encryptionKey : Logger.DEF_ENC_KEY;
                            String encText = Security.getSimpleInstance(encryptionKey2).encrypt(text);
                            sw.write(encText);
                        } else {
                            sw.write(text);
                        }
                    } catch (Exception e) {
                        sw.write(text);
                    }

                    if (addSeparation) {
                        sw.write("\n\n*-----*-----*-----*-----*\n\n");
                    } else {
                        sw.write("\n");
                    }
                } finally {
                    sw.flush();
                    sw.close();
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void appendHere() {
            appendHere("");
        }

        public void appendHere(String moreInfo) {
            try {
                StackTraceElement[] stackTraceList = new Throwable().getStackTrace();
                StackTraceElement stackTrace = null;
                stackTrace = stackTraceList[2];

                append(
                        stackTrace.getFileName() +
                                "\n" +
                                stackTrace.getMethodName() +
                                " -> line: " + stackTrace.getLineNumber() +
                                (TextUtils.isEmpty(moreInfo) ? "" : ("\n-> " + moreInfo))
                );
            } catch (Exception e) {
                append(moreInfo);
            }
        }

        public String readFileContent() {
            try {
                FileInputStream is = new FileInputStream(file);
                byte[] b = new byte[is.available()];
                is.read(b);
                String text = new String(b);

                if (enableEncryption) {
                    Integer encryptionKey2 = encryptionKey;
                    if (encryptionKey2 == null)
                        encryptionKey2 = DEF_ENC_KEY;

                    try {
                        text = Security.getSimpleInstance(encryptionKey2).decrypt(text);
                    } catch (Exception e) {
                    }
                }

                return text;

            } catch (Exception e) {
                return "";
            }
        }
    }

    public LogFileWriter createLogFileWriter(File destinationFile) {
        return new LogFileWriter(
                destinationFile,
                logConfigs.isFileContentEncryptEnabled(),
                logConfigs.fileContentEncryptionKey
        );
    }

    public LogFileWriter createLogFileWriter(Context context, String fileName) {
        File file = TextUtils.isEmpty(fileName) ?
                createOrGetLogFile(context) :
                createOrGetLogFile(context, fileName);

        return new LogFileWriter(
                file,
                logConfigs.isFileContentEncryptEnabled(),
                logConfigs.fileContentEncryptionKey
        );
    }

    public LogFileWriter createLogFileWriter(Context context, String dirName, String fileName) {
        File file = createOrGetLogFile(context, dirName, fileName);

        return new LogFileWriter(
                file,
                logConfigs.isFileContentEncryptEnabled(),
                logConfigs.fileContentEncryptionKey
        );
    }

}
