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
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import gmutils.BackgroundTask;
import gmutils.listeners.ActionCallback;
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
            ActionCallback<File, Boolean> isDirExcluded,
            ActionCallback<File, Boolean> isFileExcluded,
            ResultCallback<Error> onComplete
    ) {
        BackgroundTask.run(() -> compressSync(
                outZipFile,
                rootDir,
                isDirExcluded,
                isFileExcluded
        ), (e) -> {
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error compressSync(
            File outZipFile,
            File rootDir,
            ActionCallback<File, Boolean> isDirExcluded,
            ActionCallback<File, Boolean> isFileExcluded
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
                    if (sf.isFile()) {
                        if (isFileExcluded == null)
                            files.add(sf);
                        else {
                            Boolean exclude = isFileExcluded.invoke(sf);
                            if (exclude == null) exclude = false;
                            if (!exclude) {
                                files.add(sf);
                            }
                        }
                    }
                    else if (sf.isDirectory()) {
                        if (isDirExcluded == null) {
                            dirs.add(sf);
                        } else {
                            Boolean exclude = isDirExcluded.invoke(sf);
                            if (exclude == null) exclude = false;
                            if (!exclude) {
                                dirs.add(sf);
                            }
                        }
                    }
                }
            }
        }

        return compressSync(outZipFile, files, rootDir);
    }

    //-------------------------------------------------

    public void compress(
            File outZipFile,
            List<File> toCompressFiles,
            File rootDirOfFiles,
            ResultCallback<Error> onComplete
    ) {
        BackgroundTask.run(() -> compressSync(
                outZipFile,
                toCompressFiles,
                rootDirOfFiles
        ), (e) -> {
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error compressSync(
            File outZipFile,
            List<File> toCompressFiles,
            File rootDirOfFiles
    ) {
        if (!outZipFile.exists()) {
            try {
                outZipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return new Error(e.getMessage());
            }
        }

        if (!rootDirOfFiles.isDirectory()) {
            return new Error("rootDir must be directory");
        }

        OutputStream zipFileOutputStream = null;
        try {
            zipFileOutputStream = new FileOutputStream(outZipFile);

            ZipOutputStream zipStream = new ZipOutputStream(zipFileOutputStream);

            final String rootDirPath = rootDirOfFiles.getAbsolutePath() + "/";

            Error error = null;

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
                    error = new Error(e.getMessage());
                    break;
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                zipStream.finish();
                zipStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                return new Error(e.getMessage());
            }

            return error;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (zipFileOutputStream != null) {
                try {
                    zipFileOutputStream.flush();
                    zipFileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public Error compressSync(
            File outZipFile,
            File[] toCompressFiles,
            File rootDirOfFiles
    ) {
        return compressSync(
                outZipFile,
                Arrays.asList(toCompressFiles),
                rootDirOfFiles
        );
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
