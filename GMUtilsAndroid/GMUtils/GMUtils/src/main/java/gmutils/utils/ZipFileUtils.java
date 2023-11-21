package gmutils.utils;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import gmutils.BackgroundTask;
import gmutils.listeners.ResultCallback;

public class ZipFileUtils {
    public static class Error {
        public final String error;

        public Error(String error) {
            this.error = error;
        }
    }

    public void compress(
            File outZipFile,
            File rootDir,
            ResultCallback<Error> onComplete
    ) {
        BackgroundTask.run(() -> compressSync(
                outZipFile,
                rootDir
        ), (e) -> {
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error compressSync(
            File outZipFile,
            File rootDir
    ) {
        if (!outZipFile.exists()) {
            try {
                outZipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return new Error(e.getMessage());
            }
        }

        if (!rootDir.isDirectory()) {
            return new Error("rootDir must be directory");
        }

        List<File> files = new ArrayList<>();

        List<File> dirs = new ArrayList<>();
        dirs.add(rootDir);

        int d = 0;
        for (; d < dirs.size(); d++) {
            File dir = dirs.get(d);
            File[] subFiles = dir.listFiles();
            if (subFiles != null) {
                for (File sf : subFiles) {
                    if (sf.isFile()) files.add(sf);
                    else if (sf.isDirectory()) dirs.add(sf);
                }
            }
        }

        return compressSync(outZipFile, rootDir, files);
    }

    //-------------------------------------------------

    public void compress(
            File outZipFile,
            File rootDir,
            List<File> toCompressFiles,
            ResultCallback<Error> onComplete
    ) {
        BackgroundTask.run(() -> compressSync(
                outZipFile,
                rootDir,
                toCompressFiles
        ), (e) -> {
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error compressSync(
            File outZipFile,
            File rootDir,
            List<File> toCompressFiles
    ) {
        if (!outZipFile.exists()) {
            try {
                outZipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return new Error(e.getMessage());
            }
        }

        if (!rootDir.isDirectory()) {
            return new Error("rootDir must be directory");
        }

        OutputStream zipFileOutputStream = null;
        ZipOutputStream zipStream = null;
        try {
            zipFileOutputStream = new FileOutputStream(outZipFile);
            zipStream = new ZipOutputStream(zipFileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Error(e.getMessage());
        } finally {
            if (zipFileOutputStream != null) {
                try {
                    zipFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (zipStream != null) {
                try {
                    zipStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        final String rootDirPath = rootDir.getAbsolutePath() + "/";

        for (File file : toCompressFiles) {
            String entryName = file.getAbsolutePath().replace(rootDirPath, "");

            ZipEntry zipEntry = new ZipEntry(entryName);
            FileInputStream fileInputStream = null;
            try {
                zipStream.putNextEntry(zipEntry);

                fileInputStream = new FileInputStream(file);
                var length = 0;
                byte[] buffer = new byte[1024];
                while ((length = fileInputStream.read(buffer)) > 0) {
                    zipStream.write(buffer, 0, length);
                }

                fileInputStream.close();
                zipStream.closeEntry();
            } catch (Exception e) {
                e.printStackTrace();
                return new Error(e.getMessage());
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    zipFileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    zipStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            zipStream.finish();
            zipStream.close();
            zipFileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            return new Error(e.getMessage());
        }

        return null;
    }

    //----------------------------------------------------------------------------------------------

    public void extract(
            InputStream fileInputStream,
            File outputDir,
            ResultCallback<Error> onComplete
    ) {
        BackgroundTask.run(() -> extractSync(
                fileInputStream,
                outputDir
        ), (e) -> {
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error extractSync(
            InputStream fileInputStream,
            File outputDir
    ) {
        if (!outputDir.isDirectory()) {
            return new Error("outputDir must be directory");
        }

        FileOutputStream fileOutputStream = null;

        try (fileInputStream; ZipInputStream zipInputStream = new ZipInputStream(fileInputStream)) {
            ZipEntry zipEntry = null;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                int lastSlash = zipEntry.getName().lastIndexOf("/");
                if (lastSlash >= 0) {
                    String dirPath = zipEntry.getName().substring(0, lastSlash);
                    new File(outputDir, dirPath).mkdirs();
                }

                File outFile = new File(outputDir, zipEntry.getName());
                if (!outFile.exists()) try {
                    outFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Error(e.getMessage());
                }

                fileOutputStream = new FileOutputStream(outFile);

                //-------------------------------------------------------------

                byte[] buffer = new byte[1024];
                int length = 0;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }

                zipInputStream.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Error(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
