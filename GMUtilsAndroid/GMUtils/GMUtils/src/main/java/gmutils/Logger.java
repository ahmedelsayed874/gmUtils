package gmutils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import gmutils.app.BaseApplication;

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
    private static DateOp LOG_DEADLINE = null;
    private static DateOp WRITE_TO_FILE_DEADLINE = null;
    private static DateOp WRITE_LOGS_TO_FILE_DEADLINE = null;
    private static DateOp FILE_CONTENT_ENCRYPT_DEADLINE = null;

    private static Integer FILE_CONTENT_ENCRYPT_KEY = null;
    private static final int DEF_ENC_KEY = 112439;

    public static int MAX_LOG_FILES_COUNT = 50;

    //----------------------------------------------------------------------------------------------

    //region set printing logs deadline
    public static void SET_LOG_DEADLINE(int d, int M, int y) {
        SET_LOG_DEADLINE(d, M, y, 0, 0);
    }

    public static void SET_LOG_DEADLINE(int d, int M, int y, int h, int m) {
        LOG_DEADLINE = DateOp.getInstance()
                .setDate(d, M, y)
                .setTime(h, m, 0);
    }

    public static void SET_LOG_DEADLINE(DateOp dateOp) {
        LOG_DEADLINE = dateOp;
    }
    //endregion set printing logs deadline

    //region set writing to file deadline
    public static void SET_WRITE_TO_FILE_DEADLINE(int d, int M, int y) {
        SET_WRITE_TO_FILE_DEADLINE(d, M, y, 0, 0);
    }

    public static void SET_WRITE_TO_FILE_DEADLINE(int d, int M, int y, int h, int m) {
        WRITE_TO_FILE_DEADLINE = DateOp.getInstance()
                .setDate(d, M, y)
                .setTime(h, m, 0);
    }

    public static void SET_WRITE_TO_FILE_DEADLINE(DateOp dateOp) {
        WRITE_TO_FILE_DEADLINE = dateOp;
    }
    //endregion set writing to file deadline

    //region set writing <<<logs>>> to file deadline
    public static void SET_WRITE_LOGS_TO_FILE_DEADLINE(int d, int M, int y) {
        SET_WRITE_LOGS_TO_FILE_DEADLINE(d, M, y, 0, 0);
    }

    public static void SET_WRITE_LOGS_TO_FILE_DEADLINE(int d, int M, int y, int h, int m) {
        WRITE_LOGS_TO_FILE_DEADLINE = DateOp.getInstance()
                .setDate(d, M, y)
                .setTime(h, m, 0);
    }

    public static void SET_WRITE_LOGS_TO_FILE_DEADLINE(DateOp dateOp) {
        WRITE_LOGS_TO_FILE_DEADLINE = dateOp;
    }
    //endregion set writing <<<logs>>> to file deadline

    //region set encrypting file content deadline
    public static void SET_FILE_CONTENT_ENCRYPT_DEADLINE(int encryptKey, int d, int M, int y) {
        SET_FILE_CONTENT_ENCRYPT_DEADLINE(encryptKey, d, M, y, 0, 0);
    }

    public static void SET_FILE_CONTENT_ENCRYPT_DEADLINE(int encryptKey, int d, int M, int y, int h, int m) {
        FILE_CONTENT_ENCRYPT_DEADLINE = DateOp.getInstance()
                .setDate(d, M, y)
                .setTime(h, m, 0);
        FILE_CONTENT_ENCRYPT_KEY = encryptKey;
    }

    public static void SET_FILE_CONTENT_ENCRYPT_DEADLINE(int encryptKey, DateOp dateOp) {
        FILE_CONTENT_ENCRYPT_DEADLINE = dateOp;
        FILE_CONTENT_ENCRYPT_KEY = encryptKey;
    }
    //endregion set encrypting file content deadline

    //----------------------------------------------------------------------------------------------

    //region get printing logs deadline
    public static DateOp GET_LOG_DEADLINE() {
        return LOG_DEADLINE;
    }
    //endregion get printing logs deadline

    //region get writing to file deadline
    public static DateOp GET_WRITE_TO_FILE_DEADLINE() {
        return WRITE_TO_FILE_DEADLINE;
    }
    //endregion get writing to file deadline

    //region get writing <<<logs>>> to file deadline
    public static DateOp GET_WRITE_LOGS_TO_FILE_DEADLINE() {
        return WRITE_LOGS_TO_FILE_DEADLINE;
    }
    //endregion get writing <<<logs>>> to file deadline

    //region get encrypting file content deadline
    public static DateOp GET_FILE_CONTENT_ENCRYPT_DEADLINE() {
        return FILE_CONTENT_ENCRYPT_DEADLINE;
    }

    public static Integer GET_FILE_CONTENT_ENCRYPT_KEY() {
        return FILE_CONTENT_ENCRYPT_KEY;
    }
    //endregion get encrypting file content deadline

    //----------------------------------------------------------------------------------------------

    //region check enable status
    public static boolean IS_LOG_ENABLED() {
        return LOG_DEADLINE != null && LOG_DEADLINE.getTimeInMillis() >= System.currentTimeMillis();
    }

    public static boolean IS_WRITE_TO_FILE_ENABLED() {
        return WRITE_TO_FILE_DEADLINE != null && WRITE_TO_FILE_DEADLINE.getTimeInMillis() >= System.currentTimeMillis();
    }

    public static boolean IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED() {
        return WRITE_LOGS_TO_FILE_DEADLINE != null && WRITE_LOGS_TO_FILE_DEADLINE.getTimeInMillis() >= System.currentTimeMillis();
    }

    public static boolean IS_FILE_CONTENT_ENCRYPT_ENABLED() {
        return FILE_CONTENT_ENCRYPT_DEADLINE != null && FILE_CONTENT_ENCRYPT_DEADLINE.getTimeInMillis() >= System.currentTimeMillis();
    }
    //endregion check enable status

    //----------------------------------------------------------------------------------------------

    public interface Callbacks {
        interface PrintSingle {
            Object getContent();
        }
        interface PrintMultiple {
            Object[] getContent();
        }
    }

    //region LOGs
    public static void print(Throwable throwable) {
        print("", throwable);
    }

    public static void print(String title, Throwable throwable) {
        print("EXCEPTION **** " + title, () -> throwable == null ? "null" : throwable.toString());
    }

    public static void printm(@NotNull Callbacks.PrintMultiple callback) {
        printm("", callback);
    }

    public static void printm(String title, @NotNull Callbacks.PrintMultiple callback) {
        if (IS_LOG_ENABLED() || IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED()) {
            String[] msg = new String[] {""};

            Object[] content = callback.getContent();
            if (content == null || content.length == 0) {
                msg[0] = "null";

            } else {
                for (int i = 0; i < content.length; i++) {
                    msg[0] += "OBJ[" + i + "]: " + (content[i] == null ? "null" : content[i].toString());
                    if (i < content.length - 1) {
                        msg[0] += "\n+++\n";
                    }
                }
            }

            print("" + title, () -> msg[0]);
        }

    }

    public static void print(@NotNull Callbacks.PrintSingle callback) {
        print("", callback);
    }

    public static void print(String title, @NotNull Callbacks.PrintSingle callback) {
        print(title, callback, IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED());
    }

    public static void print(String title, @NotNull Callbacks.PrintSingle callback, boolean writeToFileAlso) {
        String content = "";

        if (IS_LOG_ENABLED() || writeToFileAlso) {
            content = callback.getContent().toString();
        }

        if (IS_LOG_ENABLED()) {
            String[] t = refineTitle("**** " + title);
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
                    writeToFile(BaseApplication.current(), content, "APP_LOGS");
                } catch (Exception e) {
                }
            }
        }
    }

    public static void printMethod() {
        printMethod(null);
    }

    public static void printMethod(Callbacks.PrintSingle moreInfoCallback) {
        printMethod(moreInfoCallback, IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED());
    }

    public static void printMethod(Callbacks.PrintSingle moreInfoCallback, boolean writeToFileAlso) {
        try {
            StackTraceElement[] stackTraceList = new Throwable().getStackTrace();
            StackTraceElement stackTrace = null;
            for (int s = 1; s <= 3; s++) {
                if (!Logger.class.getName().equals(stackTraceList[s].getClassName())) {
                    stackTrace = stackTraceList[s];
                    break;
                }
            }

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
                    " -> line: " + stackTrace.getLineNumber() +
                    (TextUtils.isEmpty(moreInfo) ? "" : ("\n-> " + moreInfo));

            print("", () -> msg, writeToFileAlso);
        } catch (Exception e) {
            print("", e::toString, writeToFileAlso);
        }
    }

    //----------------------------------------------------------------------------------------------

    private static String[] refineTitle(String title) {
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

    private static final int MAX_LOG_LENGTH = 4000;

    private static String[] divideLogMsg(String msg, int offset) {
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

    public static class LogFileWriter {
        private final File file;

        private LogFileWriter(File file) {
            this.file = file;
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
                        if (IS_FILE_CONTENT_ENCRYPT_ENABLED()) {
                            if (FILE_CONTENT_ENCRYPT_KEY == null)
                                FILE_CONTENT_ENCRYPT_KEY = DEF_ENC_KEY;
                            String encText = Security.getSimpleInstance(FILE_CONTENT_ENCRYPT_KEY).encrypt(text);
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
    }

    public static LogFileWriter createLogFileWriter(String destinationFilePath) {
        return createLogFileWriter(new File(destinationFilePath));
    }

    public static LogFileWriter createLogFileWriter(File destinationFile) {
        return new LogFileWriter(destinationFile);
    }

    public static LogFileWriter createLogFileWriter(Context context, String fileName) {
        File file = TextUtils.isEmpty(fileName) ?
                createOrGetLogFile(context) :
                createOrGetLogFile(context, fileName);
        return new LogFileWriter(file);
    }

    public static LogFileWriter createLogFileWriter(Context context, String dirName, String fileName) {
        File file = createOrGetLogFile(context, dirName, fileName);
        return new LogFileWriter(file);
    }

    //region write to files
    public static void writeToFile(Context context, String text) {
        writeToFile(context, text, null);
    }

    public static void writeToFile(Context context, String text, String fileName) {
        if (IS_WRITE_TO_FILE_ENABLED() || IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED()) {
            createLogFileWriter(context, fileName).append(text);
        } else {
            if (!IS_WRITE_TO_FILE_ENABLED() || !IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED()) {
                deleteSavedFiles(context);
            }
        }
    }

    public static void writeToFile(Context context, String text, String dirName, String fileName) {
        if (IS_WRITE_TO_FILE_ENABLED() || IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED()) {
            createLogFileWriter(context, fileName).append(text);
        } else {
            if (!IS_WRITE_TO_FILE_ENABLED() || !IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED()) {
                deleteSavedFiles(context, dirName);
            }
        }
    }

    public static void writeToFile(String filePath, String text) {
        if (IS_WRITE_TO_FILE_ENABLED() || IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED()) {
            createLogFileWriter(filePath).append(text);
        } else {
            if (!IS_WRITE_TO_FILE_ENABLED() || !IS_WRITE_LOGS_TO_FILE_DEADLINE_ENABLED()) {
                try {
                    File f = new File(filePath);
                    f.deleteOnExit();
                } catch (Exception ignored) {
                }
            }
        }
    }
    //endregion write to files

    //region read files
    public static String readFromCurrentSessionFile(Context context) {
        try {
            File file = createOrGetLogFile(context);
            String text = readFileContent(file);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readFile(Context context, String fileName) {
        try {
            File file = createOrGetLogFile(context, fileName);
            String text = readFileContent(file);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readFile(Context context, String dirName, String fileName) {
        try {
            File file = createOrGetLogFile(context, dirName, fileName);
            String text = readFileContent(file);
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String readAllFilesContents(Context context) {
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

    public static String readFileContent(File file) {
        try {
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            String text = new String(b);

            if (IS_FILE_CONTENT_ENCRYPT_ENABLED()) {
                if (FILE_CONTENT_ENCRYPT_KEY == null)
                    FILE_CONTENT_ENCRYPT_KEY = DEF_ENC_KEY;

                try {
                    text = Security.getSimpleInstance(FILE_CONTENT_ENCRYPT_KEY).decrypt(text);
                } catch (Exception e) {
                }
            }

            return text;

        } catch (Exception e) {
            return "";
        }
    }
    //endregion read files

    //region list saved files
    public static List<File> getSavedFiles(Context context) {
        File logFiles = getLogDirector(context);
        return getSavedFiles(logFiles);
    }

    public static List<File> getSavedFiles(Context context, String dirName) {
        File logFiles = getLogDirector(context, dirName);
        return getSavedFiles(logFiles);
    }

    public static List<File> getSavedFiles(File logFiles) {
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
    public static void deleteSavedFiles(Context context) {
        List<File> files = getSavedFiles(context);
        for (File file : files) {
            try {
                file.delete();
            } catch (Exception e) {
            }
        }
    }

    public static void deleteSavedFile(Context context, String fileName) {
        try {
            File file = createOrGetLogFile(context, fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteSavedFile(Context context, String dirName, String fileName) {
        try {
            File file = createOrGetLogFile(context, dirName, fileName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteSavedFiles(Context context, String dirName) {
        try {
            File file = createOrGetLogFile(context, dirName);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //endregion delete files

    //region create file
    public static String getLogFilesPath(Context context) {
        return getLogDirector(context).getAbsolutePath();
    }

    private static String sessionId;

    private synchronized static File createOrGetLogFile(Context context) {
        if (sessionId == null) {
            sessionId = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.ENGLISH).format(new Date());
        }

        return createOrGetLogFile(context, "/LOG_FILE_" + sessionId + ".txt");
    }

    private synchronized static File createOrGetLogFile(Context context, String fileName) {
        File logFiles = getLogDirector(context);
        return createOrGetLogFile(logFiles, fileName);
    }

    private synchronized static File createOrGetLogFile(Context context, String dirName, String fileName) {
        File logFiles = getLogDirector(context, dirName);
        return createOrGetLogFile(logFiles, fileName);
    }

    private synchronized static File createOrGetLogFile(File logFilesDir, @NotNull String fileName) {
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
    private synchronized static File getLogDirector(Context context) {
        return getLogDirector(context, "LOGS");
    }

    private synchronized static File getLogDirector(Context context, String dirName) {
        File filesDir = context.getExternalFilesDir(null);
        return getLogDirector(filesDir.getPath() + "/" + dirName);
    }

    private synchronized static File getLogDirector(String dirPath) {
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

}
