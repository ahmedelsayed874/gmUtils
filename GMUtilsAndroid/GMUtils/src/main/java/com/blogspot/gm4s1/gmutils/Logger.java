package com.blogspot.gm4s1.gmutils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

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

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class Logger {
    public static DateOp LOG_DEADLINE = null;
    public static DateOp WRITE_TO_FILE_DEADLINE = null;
    public static int MAX_LOG_FILES_COUNT = 50;
    public static boolean WRITE_LOGS_TO_FILE = false;

    public static void setLOGDeadline(int d, int M, int y) {
        setLOGDeadline(d, M, y, 23, 59);
    }

    public static void setLOGDeadline(int d, int M, int y, int h, int m) {
        LOG_DEADLINE = DateOp.getInstance()
                .setDate(y, M, d)
                .setTime(h, m, 59);
    }

    public static void setWriteToFileDeadline(int d, int M, int y) {
        setWriteToFileDeadline(d, M, y, 23, 59);
    }

    public static void setWriteToFileDeadline(int d, int M, int y, int h, int m) {
        WRITE_TO_FILE_DEADLINE = DateOp.getInstance()
                .setDate(y, M, d)
                .setTime(h, m, 59);
    }

    public static boolean IS_LOG_ENABLED() {
        return LOG_DEADLINE == null
                || System.currentTimeMillis() <= LOG_DEADLINE.getTimeInMillis();
    }

    public static boolean IS_WRITE_TO_FILE_ENABLED() {
        return WRITE_TO_FILE_DEADLINE == null
                || System.currentTimeMillis() <= WRITE_TO_FILE_DEADLINE.getTimeInMillis();

    }

    //----------------------------------------------------------------------------------------------

    public static void print(Throwable e) {
        print("", e);
    }

    public static void print(String title, Throwable e) {
        if (IS_LOG_ENABLED()) {
            print("EXCEPTION **** " + title, e == null ? "null" : e.toString());
        }
    }

    public static void print(Object... o) {
        if (IS_LOG_ENABLED()) {
            print("", o);
        }
    }

    public static void print(String title, Object... o) {
        if (IS_LOG_ENABLED()) {
            String msg = "";

            if (o == null || o.length == 0) {
                msg = "null";

            } else {
                for (int i = 0; i < o.length; i++) {
                    msg += "OBJ[" + i + "]: " + (o[i] == null ? "null" : o[i].toString());
                    if (i < o.length - 1) {
                        msg += "\n+++\n";
                    }
                }
            }

            print("" + title, msg);
        }
    }

    public static void print(String msg) {
        if (IS_LOG_ENABLED()) {
            print("", msg);
        }
    }

    public static void print(String title, String msg) {
        if (IS_LOG_ENABLED()) {
            String[] t = refineTitle("**** " + title);
            String[] m = divideMsg(msg, t.length > 1 ? t[1].length() : 0);

            if (m.length == 1) {
                if (t.length == 1)
                    Log.e(t[0], m[0]);
                else
                    Log.e(t[0], t[1] + ": " + m[0]);
            } else {
                for (int i = 0; i < m.length; i++) {
                    if (t.length == 1)
                        Log.e(t[0], "LOG[" + i + "]-> " + m[i]);
                    else
                        Log.e(t[0], t[1] + ": " + "LOG[" + i + "]-> " + m[i]);
                }
            }
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

    public static final int MAX_LOG_LENGTH = 4000;

    public static String[] divideMsg(String msg, int offset) {
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

                    sw.write(text);

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
    }

    public static LogFileWriter createLogFileWriter(File file) {
        return new LogFileWriter(file);
    }

    public static LogFileWriter createLogFileWriter(Context context, @Nullable String fileName) {
        File file = createOrGetLogFile(context, fileName);
        return new LogFileWriter(file);
    }

    //--------------

    public static void writeToFile(Context context, String text) {
        writeToFile(context, text, null);
    }

    public static void writeToFile(Context context, String text, String fileName) {
        if (!IS_WRITE_TO_FILE_ENABLED()) return;
        createLogFileWriter(context, fileName).append(text);
    }

    public static String readFromCurrentSessionFile(Context context) {
        return readFile(context, null);
    }

    //--------------

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

    private static String readFileContent(File file) {
        try {
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            String text = new String(b);
            return text;

        } catch (Exception e) {
            return "";
        }
    }

    //--------------

    public static List<File> getSavedFiles(Context context) {
        List<File> savedFiles = new ArrayList<>();

        try {
            File logFiles = getLogDirector(context);
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

    public static void deleteSavedFiles(Context context) {
        List<File> files = getSavedFiles(context);
        if (files != null) {
            for (File file : files) {
                try {
                    file.delete();
                } catch (Exception e) {
                }
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


    private static String sessionId;

    private synchronized static File createOrGetLogFile(Context context, String fileName) {
        try {
            File logFiles = getLogDirector(context);
            if (TextUtils.isEmpty(fileName)) {
                if (sessionId == null) {
                    sessionId = new SimpleDateFormat("yyyyMMdd-HHmm", Locale.ENGLISH).format(new Date());
                }
                fileName = sessionId;
            }

            String filePath = logFiles.getPath() + "/LOG_FILE_" + fileName + ".txt";
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

    private synchronized static File getLogDirector(Context context) {
        try {
            File filesDir = context.getExternalFilesDir(null);
            File logFiles = new File(filesDir.getPath() + "/LOGS");
            if (!logFiles.exists()) {
                logFiles.mkdir();
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

}
