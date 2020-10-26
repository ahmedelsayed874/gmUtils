package com.blogspot.gm4s1.gmutils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import android.util.Log;

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
public class AppLog {
    public static DateOp DEADLINE = DateOp.getInstance(
            "20-10-2020 00:00:00",
            DateOp.PATTERN_dd_MM_yyyy_HH_mm_ss);

    public static boolean DEBUG_MODE() {
        return System.currentTimeMillis() <= DEADLINE.getTimeInMillis();
    }

    public static Boolean WRITE_TO_FILE_ENABLED = null;

    public static boolean WRITE_TO_FILE_ENABLED() {
        if (WRITE_TO_FILE_ENABLED == null) return DEBUG_MODE();
        return WRITE_TO_FILE_ENABLED;
    }

    ;


    public static boolean checkVersionMode(Context context) {
        context = context.getApplicationContext();

        ApplicationInfo appInfo = context.getApplicationInfo();
        boolean isDebuggable = (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;

        //DEBUG_MODE = isDebuggable;
        if (isDebuggable)
            DEADLINE = DateOp.getInstance().increaseDays(7);
        else
            DEADLINE = DateOp.getInstance().decreaseDays(7);

        return isDebuggable;
    }

    public static void print(Throwable e) {
        if (DEBUG_MODE()) {
            try {
                Log.e("**** EXCEPTION ****", e.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void print(String msg) {
        if (DEBUG_MODE()) Log.e("****", msg == null ? "null" : msg);
    }

    public static void print(String title, String msg) {
        if (DEBUG_MODE()) Log.e("**** " + title, msg == null ? "null" : msg);
    }

    //----------------------------------------------------------------------------------------------

    public static void writeToFile(Context context, String text) {
        writeToFile(context, text, null);
    }

    public static void writeToFile(Context context, String text, String fileName) {
        if (!WRITE_TO_FILE_ENABLED()) return;
        try {
            File file = createOrGetLogFile(context, fileName);

            OutputStream os = new FileOutputStream(file, true);
            OutputStreamWriter sw = new OutputStreamWriter(os);
            try {
                String date = new Date().toString();
                sw.write(date);
                sw.write(":\n");
                sw.write(text);
                sw.write("\n\n*-----*-----*-----*-----*\n\n");
            } finally {
                sw.flush();
                sw.close();
                os.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
