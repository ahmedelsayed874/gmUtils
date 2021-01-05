package com.blogspot.gm4s1.gmutils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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

    public static boolean checkVersionMode(Context context) {
        context = context.getApplicationContext();

        ApplicationInfo appInfo = context.getApplicationInfo();
        boolean isDebuggable = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        //DEBUG_MODE = isDebuggable;
        if (isDebuggable)
            LOG_DEADLINE = DateOp.getInstance().increaseDays(7);
        else
            LOG_DEADLINE = DateOp.getInstance().decreaseDays(7);

        return isDebuggable;
    }

    //----------------------------------------------------------------------------------------------

    public static void print(Throwable e) {
        if (IS_LOG_ENABLED()) {
            try {
                Log.e("**** EXCEPTION ****", e.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void print(String msg) {
        if (IS_LOG_ENABLED()) Log.e("****", msg == null ? "null" : msg);
    }

    public static void print(String title, String msg) {
        if (IS_LOG_ENABLED()) Log.e("**** " + title, msg == null ? "null" : msg);
    }

    //----------------------------------------------------------------------------------------------

    public static class FileWriter {
        private final File file;
        private FileWriter(File file) {
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

    public static FileWriter createFileWriter(File file) {
        return new FileWriter(file);
    }

    public static FileWriter createFileWriter(Context context, @Nullable String fileName) {
        File file = createOrGetLogFile(context, fileName);
        return new FileWriter(file);
    }

    //----------------------------------------------------------------------------------------------

    public static void writeToFile(Context context, String text) {
        writeToFile(context, text, null);
    }

    public static void writeToFile(Context context, String text, String fileName) {
        if (!IS_WRITE_TO_FILE_ENABLED()) return;
        createFileWriter(context, fileName).append(text);
    }

    public static String readFromCurrentSessionFile(Context context) {
        return readFile(context, null);
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

    public static String readAllFilesContents(Context context) {
        String content = "";

        try {
            File logFiles = getLogDirector(context, false);
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

    public static List<File> getSavedFiles(Context context) {
        List<File> savedFiles = new ArrayList<>();

        try {
            File logFiles = getLogDirector(context, false);
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

    private static String sessionId;

    private synchronized static File createOrGetLogFile(Context context, String fileName) {
        try {
            File logFiles = getLogDirector(context, true);
            if (TextUtils.isEmpty(fileName)) {
                if (sessionId == null)
                    sessionId = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH).format(new Date());
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

    private synchronized static File getLogDirector(Context context, boolean allowDeletingOldFiles) {
        try {
            File filesDir = context.getExternalFilesDir(null);
            File logFiles = new File(filesDir.getPath() + "/LOGS");
            if (!logFiles.exists()) {
                logFiles.mkdir();
            }

            if (allowDeletingOldFiles) {
                String[] list = logFiles.list();
                final int max_file_count = 50;

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
            }

            return logFiles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
