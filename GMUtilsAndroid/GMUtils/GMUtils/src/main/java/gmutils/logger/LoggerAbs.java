package gmutils.logger;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmutils.BackgroundTask;
import gmutils.DateOp;
import gmutils.LooperThread;
import gmutils.app.BaseApplication;
import gmutils.listeners.ResultCallback;

import java.lang.Runnable;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import gmutils.security.Security;
import gmutils.utils.ZipFileUtils;

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
public abstract class LoggerAbs {
    public static class LogConfigs {
        private DateOp logDeadline;
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

        public boolean isWriteLogsToFileEnabled() {
            return writeLogsToFileDeadline != null && writeLogsToFileDeadline.getTimeInMillis() >= System.currentTimeMillis();
        }

        public boolean isFileContentEncryptEnabled() {
            return fileContentEncryptionDeadline != null && fileContentEncryptionDeadline.getTimeInMillis() >= System.currentTimeMillis();
        }
        //endregion check enable status


        public Integer getFileContentEncryptionKey() {
            return fileContentEncryptionKey;
        }

        @Override
        public String toString() {
            return "LogConfigs{" +
                    "logDeadline=" + logDeadline +
                    ", writeLogsToFileDeadline=" + writeLogsToFileDeadline +
                    ", fileContentEncryptionDeadline=" + fileContentEncryptionDeadline +
                    ", fileContentEncryptionKey=" + fileContentEncryptionKey +
                    '}';
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();

            logDeadline = null;
            writeLogsToFileDeadline = null;
            fileContentEncryptionDeadline = null;
        }
    }

    public static class LogFileWriter {
        public final File file;
        public final boolean enableEncryption;
        public final Integer encryptionKey;

        public LogFileWriter(Context context, @Nullable String dirName, @NotNull String fileName, boolean enableEncryption, Integer encryptionKey) {
            File root = context.getExternalFilesDir(null);
            if (dirName != null && !dirName.isEmpty()) {
                root = new File(root, dirName);
                if (!root.exists()) root.mkdirs();
            }

            File file = new File(root, fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            this.file = file;
            this.enableEncryption = enableEncryption;
            this.encryptionKey = encryptionKey;
        }

        public LogFileWriter(File file, boolean enableEncryption, Integer encryptionKey) {
            this.file = file;
            this.enableEncryption = enableEncryption;
            this.encryptionKey = encryptionKey;
        }

        public void write(String text) {
            write(text, true, true);
        }

        public void write(String text, boolean addDate, boolean addSeparation) {
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
                            int encryptionKey2 = encryptionKey != null ? encryptionKey : LoggerAbs.DEF_ENC_KEY;
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

        public void writeCurrentStep() {
            writeCurrentStep("");
        }

        public void writeCurrentStep(String moreInfo) {
            try {
                StackTraceElement[] stackTraceList = new Throwable().getStackTrace();
                StackTraceElement stackTrace = null;
                stackTrace = stackTraceList[2];

                write(
                        stackTrace.getFileName() +
                                "\n" +
                                stackTrace.getMethodName() +
                                " -> line: " + stackTrace.getLineNumber() +
                                (TextUtils.isEmpty(moreInfo) ? "" : ("\n-> " + moreInfo))
                );
            } catch (Exception e) {
                write(moreInfo);
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

        public double fileSizeInMb() {
            long bytes = file.length();
            double mb = bytes / 1024f / 1024f; //12.34567  | 12.0
            int mbi = (int) mb;                  //12        | 12
            mb -= mbi;                           //0.34567   |  0.0
            mb *= 100;                           //34.567    |  0.0
            int mbfi = (int) mb;                 //34        |  0
            return mbi + (mbfi / 100f);
        }
    }

    public interface TitleGetter {
        String getTitle();
    }

    public interface ContentGetter {
        Object getContent();
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private final static int DEF_ENC_KEY = 112439;
    public int MAX_LOG_FILES_COUNT = 50;

    //----------------------------------------------------------------------------------------------

    private final String logId;
    private LogConfigs logConfigs;
    private LogFileWriter logFileWriter;

    public LoggerAbs(@Nullable String logId) {
        this.logId = logId;
        this.logConfigs = new LogConfigs();

        numberOnInstances++;
    }

    @NotNull
    public String logId() {
        if (logId != null && !(logId.trim()).isEmpty()) return logId.trim();
        return "";
    }

    //----------------------------------------------------------------------------------------------

    private static int numberOnInstances = 0;
    private static LooperThread _looperThread;
//    private List<Runnable> _tasks = new ArrayList<>();

    protected void runOnLoggerThread(Runnable task) {
//        _tasks.add(task);

        if (_looperThread == null) {
            _looperThread = new LooperThread(
                    "logger-thread",
                    args -> {
//                        if (_tasks.size() > 0) {
//                            Runnable r = _tasks.remove(0);
//                            if (r != null) r.run();
//                        }
                        Object o = args.getMsg().obj;
                        if (o instanceof Runnable) {
                            ((Runnable) o).run();
                        }
                    }
            );
        }

        Message message = Message.obtain();
        message.obj = task;
        _looperThread.sendMessage(message);
    }

    protected void runOnUiThread(Runnable task) {
        new Handler(Looper.getMainLooper()).post(task);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        logConfigs = null;
        logFileWriter = null;

        if (--numberOnInstances <= 0) {
            numberOnInstances = 0;
            if (_looperThread != null) _looperThread.quit();
            _looperThread = null;
//            _tasks.clear();
        }
    }

    //----------------------------------------------------------------------------------------------

    @NotNull
    public LogConfigs getLogConfigs() {
        return logConfigs;
    }

    public void setLogConfigs(@NotNull LogConfigs logConfigs) {
        if (logConfigs == null) throw new IllegalArgumentException();
        this.logConfigs = logConfigs;
    }

    //----------------------------------------------------------------------------------------------

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
        if (logConfigs.isLogEnabled() || logConfigs.isWriteLogsToFileEnabled()) {
            runOnLoggerThread(() -> {
                String content = ("" + callback.getContent());

                if (logConfigs.isLogEnabled()) {
                    String title2 = "**** ";
                    if (!logId().isEmpty()) title2 += "|" + logId() + "| ";
                    if (title != null) title2 += title.getTitle();

                    String[] t = refineTitle(title2);
                    String[] m = divideLogMsg(content, t.length > 1 ? t[1].length() : 0);

                    if (m.length == 1) {
                        if (t.length == 1)
                            writeToLog(t[0], m[0]);
                        else
                            writeToLog(t[0], t[1] + ": " + m[0]);
                    } else {
                        for (int i = 0; i < m.length; i++) {
                            if (t.length == 1) {
                                writeToLog(t[0], "LOG[" + i + "]-> " + m[i]);
                            } else {
                                writeToLog(t[0], t[1] + ": " + "LOG[" + i + "]-> " + m[i]);
                            }
                        }
                    }
                }

                if (logConfigs.isWriteLogsToFileEnabled()) {
                    if (BaseApplication.current() != null) {
                        try {
                            String[] title2 = new String[]{""};
                            if (title != null) title2[0] = "<<|(" + title.getTitle() + ")|>>\n";

                            writeToFile(BaseApplication.current(), () -> title2[0] + content);
                        } catch (Exception e) {
                        }
                    }
                }
            });
        }
    }

    protected abstract void writeToLog(String tag, String msg);

    public String getLogFilesPath(Context context) {
        return getLogDirector(context).getAbsolutePath();
    }

    //----------------

    //region printMethod
    public void printMethod() {
        printMethod(null, null, 0);
    }

    public void printMethod(int tuner) { //Class<?> stopClass) {
        printMethod(null, null, tuner);
    }

    public void printMethod(ContentGetter moreInfoCallback) {
        printMethod(null, moreInfoCallback, 0);
    }

    public void printMethod(ContentGetter moreInfoCallback, int tuner) { //Class<?> stopClass) {
        printMethod(null, moreInfoCallback, tuner);
    }

    public void printMethod(TitleGetter title, ContentGetter moreInfoCallback) {
        printMethod(title, moreInfoCallback, 0);
    }

    public void printMethod(TitleGetter title, ContentGetter moreInfoCallback, int tuner) { //Class<?> stopClass) {
        if (logConfigs.isLogEnabled() || logConfigs.isWriteLogsToFileEnabled()) {
            StackTraceElement[] stackTraceList = new Throwable().getStackTrace();
            runOnLoggerThread(() -> {
                try {
                    StackTraceElement stackTrace = null;
                    int idx = 1;
                    for (int s = 1; s < stackTraceList.length; s++) {
                        if (!LoggerAbs.class.getName().equals(stackTraceList[s].getClassName())) {
                            idx = s;
                            break;
                        }
                    }

                    stackTrace = stackTraceList[idx + tuner];

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

                    print(title, () -> msg);
                } catch (Exception e) {
                    print(
                            title,
                            () -> "printMethod failed with exception: " + e.getMessage() +
                                    (moreInfoCallback == null ? "" : "\nMORE-INFO: " + moreInfoCallback.getContent())
                    );
                }
            });
        }
    }

    //endregion

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

    //region FILE
    //region write to files
    public void writeToFile(Context context, ContentGetter text) {
        if (logConfigs.isWriteLogsToFileEnabled()) {
            runOnLoggerThread(() -> {
                getLogFileWriter(
                        context
                ).write(
                        text.getContent().toString()
                );
            });
        } else {
            if (!logConfigs.isWriteLogsToFileEnabled()) {
                deleteSavedFiles(context, null);
            }
        }
    }
    //endregion write to files

    //region read files
    public void readFromCurrentSessionFile(Context context, ResultCallback<String> callback) {
        runOnLoggerThread(() -> {
            try {
                //File file = createOrGetLogFile(context);
                String text = getLogFileWriter(context).readFileContent();
                runOnUiThread(() -> callback.invoke(text));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> callback.invoke(""));
            }
        });
    }

    public void readAllFilesContents(Context context, ResultCallback<String> callback) {
        runOnLoggerThread(() -> {
            String content = "";

            try {
                File logFiles = getLogDirector(context);
                String[] list = logFiles.list();

                if (list != null) {
                    for (String fileName : list) {
                        try {
                            File f = new File(logFiles.getPath() + "/" + fileName);
                            LogFileWriter logFileWriter = new LogFileWriter(
                                    f,
                                    logConfigs.isFileContentEncryptEnabled(),
                                    logConfigs.fileContentEncryptionKey
                            );

                            content += fileName;
                            content += ":\n\n";
                            content += logFileWriter.readFileContent();
                            content += "\n\n+++++++++++ END_OF_FILE<" + fileName + "> +++++++++++++\n\n";

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            String finalContent = content;
            runOnUiThread(() -> callback.invoke(finalContent));
        });
    }
    //endregion read files

    //region list saved files
    public void getSavedFiles(Context context, ResultCallback<List<File>> callback) {
        File logFiles = getLogDirector(context);
        getSavedFiles(logFiles, callback);
    }

    public void getSavedFiles(Context context, String dirName, ResultCallback<List<File>> callback) {
        File logFiles = getLogDirector(context, dirName);
        getSavedFiles(logFiles, callback);
    }

    public void getSavedFiles(File logFiles, ResultCallback<List<File>> callback) {
        runOnLoggerThread(() -> {
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

            runOnUiThread(() -> callback.invoke(savedFiles));
        });
    }
    //endregion list saved files

    //region delete files
    public void deleteSavedFiles(Context context, Runnable callback) {
        getSavedFiles(context, (files) -> {
            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                }
            }
            if (callback != null) callback.run();
        });
    }
    //endregion delete files

    //endregion

    //----------------------------------------------------------------------------------------------

    //region create log file

    private String _sessionId;

    private String sessionId() {
        if (_sessionId == null) {
            _sessionId = new SimpleDateFormat("yyyyMMdd-HHmmssSSS", Locale.ENGLISH).format(new Date());
            if (!logId().isEmpty()) _sessionId = logId() + "_" + _sessionId;
        }
        return _sessionId;
    }

    private synchronized File createOrGetLogFile(Context context) {
        File logFilesDir = getLogDirector(context);
        int filesCount = 0;
        try {
            filesCount = Objects.requireNonNull(logFilesDir.list()).length;
        } catch (Exception ignored) {}
        String fileName = "/LOG_FILE_" + sessionId() + "_" + (filesCount + 1);

        try {
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

    private LogFileWriter getLogFileWriter(Context context) {
        if (logFileWriter == null) {
            File file = createOrGetLogFile(context);

            logFileWriter = new LogFileWriter(
                    file,
                    logConfigs.isFileContentEncryptEnabled(),
                    logConfigs.fileContentEncryptionKey
            );
        }

        if (logFileWriter.fileSizeInMb() > 2) {
            _sessionId = null;
            logFileWriter = null;
            logFileWriter = getLogFileWriter(context);
        }

        return logFileWriter;
    }

    //endregion create file

    //----------------------------------------------------------------------------------------------

    public final String extensionOfBackupOfPrivate = "bac";
    public final String extensionOfBackupOfPublic = "bacpub";

    public void exportAppBackup(Context context, boolean includePublicFiles, ResultCallback<String> onComplete) {
        BackgroundTask.run(() -> {
            //region create Back up Directory
            String backupDirName = "Backup-" + DateOp.getInstance().formatDate("yyyyMMddHHmm", true);
            File backupDir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    context.getPackageName() + "/" + backupDirName
            );
            if (!backupDir.exists() && !backupDir.mkdirs()) {
                String path0 = backupDir.getAbsolutePath();
                backupDir = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                        backupDirName
                );
                if (!backupDir.exists() && !backupDir.mkdirs()) {
                    return "Couldn't create a folder in any of those paths:\n" +
                            "Path 1:" + path0 + "\n" +
                            "Path 2:" + backupDir.getAbsolutePath();
                }
            }
            //endregion

            //region get desired dirs
            File[] dirsToZip;
            if (!includePublicFiles) {
                dirsToZip = new File[1];
            } else {
                //int size = 2;
                int size = 1 + context.getExternalFilesDirs(null).length;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    size += context.getExternalMediaDirs().length;
                }
                dirsToZip = new File[size];
            }

            dirsToZip[0] = context.getFilesDir().getParentFile();
            if (includePublicFiles) {
                //dirsToZip[1] = context.getExternalFilesDir(null);
                File[] filesDirs = context.getExternalFilesDirs(null);
                int i = 1;
                for (File filesDir : filesDirs) {
                    dirsToZip[i++] = filesDir;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    File[] mediaDirs = context.getExternalMediaDirs();
                    i = filesDirs.length + 1;
                    for (File mediaDir : mediaDirs) {
                        dirsToZip[i++] = mediaDir;
                    }
                }
            }
            //endregion

            //region create out zip files
            File[] outZipFiles = new File[dirsToZip.length];
            final String mainBackupFileName = "Backup-" + DateOp.getInstance().formatDate("yyyyMMddHHmmss", true) + "." + extensionOfBackupOfPrivate;
            try {
                for (int i = 0; i < outZipFiles.length; i++) {
                    String backupFileName;
                    if (i == 0) {
                        backupFileName = mainBackupFileName;
                    } else {
                        backupFileName = "Backup-" + DateOp.getInstance().formatDate("yyyyMMddHHmmss", true) + "-public" + i + "." + extensionOfBackupOfPublic;
                    }

                    //outZipFiles[i] = new File(backupDir, backupFileName);
                    outZipFiles[i] = includePublicFiles ?
                            new File(context.getCacheDir(), backupFileName) :
                            new File(backupDir, backupFileName);
                    outZipFiles[i].createNewFile();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Couldn't create a backup file in: '" + backupDir.getAbsolutePath() + "'\n" +
                        "Details: " + e.getMessage();
            }
            //endregion

            //region compress dirs
            ZipFileUtils zipFileUtils = new ZipFileUtils();
            AtomicInteger idx = new AtomicInteger();
            for (int i = 0; i < outZipFiles.length; i++) {
                File outZipFile = outZipFiles[i];
                File dirToZip = dirsToZip[i];

                idx.set(i);
                File finalBackupDir = backupDir;
                ZipFileUtils.Error error = zipFileUtils.compressSync(
                        outZipFile,
                        dirToZip,
                        (dir) -> {
                            if (idx.get() == 0) {//private
                                if (dir.getName().equalsIgnoreCase("databases")) {
                                    return false;
                                }
                                //
                                else if (dir.getName().equalsIgnoreCase("files")) {
                                    return false;
                                }
                                //
                                else if (dir.getName().equalsIgnoreCase("shared_prefs")) {
                                    return false;
                                }

                                return true;
                            }
                            //
                            else {
                                if (dir.getPath().equalsIgnoreCase(finalBackupDir.getPath())) {
                                    return true;
                                }

                                return false;
                            }
                        },
                        (file) -> {
                            return false;
                        }
                );
                if (error != null) {
                    writeToLog("exportAppBackup", error.error);
                    return "Failed to backup [Reason: " + error.error + "]";
                }
            }
            //endregion

            if (includePublicFiles) {
                try {
                    File outZipFile = new File(backupDir, mainBackupFileName);
                    outZipFile.createNewFile();

                    zipFileUtils.compressSync(
                            outZipFile,
                            outZipFiles,
                            context.getCacheDir()
                    );

                } catch (Exception e) {
                    e.printStackTrace();
                    return "Couldn't create a backup file in: '" + backupDir.getAbsolutePath() + "'\n" +
                            "Details: " + e.getMessage();
                }

                try {
                    for (File zipFile : outZipFiles) {
                        zipFile.delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return "Data backed-up successfully to: '" + backupDir.getAbsolutePath() + "'";
        }, onComplete);
    }

    public void importAppBackup(Context context, Uri backupFile, ResultCallback<String> onComplete) {
        BackgroundTask.run(() -> {
            File cache = new File(context.getCacheDir(), "Cache-" + System.currentTimeMillis());
            if (!cache.exists()) cache.mkdirs();

            InputStream backupFileStream = null;
            try {
                backupFileStream = context.getContentResolver().openInputStream(backupFile);
            } catch (Exception e) {
                return "Couldn't open the file";
            }

            ZipFileUtils zipFileUtils = new ZipFileUtils();
            ZipFileUtils.Error error = zipFileUtils.extractSync(backupFileStream, cache);
            if (error != null) {
                return "Couldn't open the file: " + error.error;
            }

            try {
                backupFileStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            File[] subFiles = cache.listFiles();
            if (subFiles == null) subFiles = new File[0];

            boolean privateSucceeded = true;
            boolean publicSucceeded = true;

            if (subFiles.length > 0) {
                boolean containsOtherBacFiles = false;

                int i = subFiles[0].getName().lastIndexOf(".");
                if (i > 0) {
                    String extension = subFiles[0].getName().substring(i + 1);
                    if (extensionOfBackupOfPrivate.equalsIgnoreCase(extension) ||
                            extensionOfBackupOfPublic.equalsIgnoreCase(extension)) {
                        containsOtherBacFiles = true;
                    }
                }

                if (containsOtherBacFiles) {
                    for (File subBackupFile : subFiles) {
                        boolean isPrivateFiles = false;
                        i = subBackupFile.getName().lastIndexOf(".");
                        String extension = subBackupFile.getName().substring(i + 1);
                        if (extensionOfBackupOfPrivate.equalsIgnoreCase(extension)) {
                            isPrivateFiles = true;
                        }

                        File cache2 = new File(cache, subBackupFile.getName().substring(0, i));
                        if (!cache2.exists()) cache2.mkdirs();

                        ZipFileUtils.Error error2 = null;

                        try(InputStream is = new FileInputStream(subBackupFile)) {
                            error2 = zipFileUtils.extractSync(is, cache2);
                            if (error2 != null) {
                                return "Couldn't open the file: " + error.error;
                            }

                            subFiles = cache2.listFiles();
                            if (subFiles == null) subFiles = new File[0];

                            if (isPrivateFiles) {
                                File appPrivateDir = context.getFilesDir().getParentFile();
                                boolean b = copyToTargetDir(subFiles, appPrivateDir);
                                if (!b) privateSucceeded = false;
                            } else {
                                File appPublicDir = context.getExternalFilesDir(null);
                                boolean b = copyToTargetDir(subFiles, appPublicDir);
                                if (!b) publicSucceeded = false;
                            }
                        } catch (Exception e) {
                            return "Couldn't open the file" + (error2 == null ? ".." : error2.error);
                        }
                    }

                } else {
                    //copy to private dir
                    File appPrivateDir = context.getFilesDir().getParentFile();
                    privateSucceeded = copyToTargetDir(subFiles, appPrivateDir);
                }

                boolean deleted = cache.delete();
                boolean del = deleted;
            }

            if (privateSucceeded && publicSucceeded) {
                return "All files restored successfully";
            } else if (privateSucceeded) {
                return "All private app data files restored successfully, but not all public.";
            } else {
                return "All public app data files restored successfully, but not all private.";
            }
        }, onComplete);
    }

    private boolean copyToTargetDir(File[] extracted, File targetDir) {
        boolean returningBool = true;

        try {
            for (File subFile : extracted) {
                File dest = new File(targetDir, subFile.getName());

                if (dest.exists()) {
                    File[] subSubFiles = subFile.listFiles();
                    if (subSubFiles == null) subSubFiles = new File[0];
                    for (File subSubFile : subSubFiles) {
                        File dest2 = new File(dest, subSubFile.getName());
                        //if (!dest2.exists()) dest2.mkdirs();
                        boolean b = subSubFile.renameTo(dest2);
                        if (!b) returningBool = false;
                    }
                } else {
                    //if (!dest.exists()) dest.mkdirs();
                    boolean b = subFile.renameTo(dest);
                    if (!b) returningBool = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returningBool;
    }
}


