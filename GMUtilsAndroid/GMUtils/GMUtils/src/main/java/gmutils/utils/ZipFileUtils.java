package gmutils.utils;

import java.io.File;
import java.io.FileInputStream;
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

import gmutils.backgroundWorkers.BackgroundTask;
import gmutils.listeners.ActionCallback;
import gmutils.listeners.ResultCallback;
import gmutils.logger.Logger;
import gmutils.logger.LoggerAbs;

public class ZipFileUtils {
    public static class Error {
        public final String error;

        public Error(String error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "error='" + error + '\'' +
                    '}';
        }
    }

    private final LoggerAbs logger;

    public ZipFileUtils() {
        this(Logger.d());
    }

    public ZipFileUtils(LoggerAbs logger) {
        this.logger = logger;
    }

    public void compress(
            File outZipFile,
            File rootDir,
            ActionCallback<File, Boolean> isDirExcluded,
            ActionCallback<File, Boolean> isFileExcluded,
            ResultCallback<Error> onComplete
    ) {
        if (logger != null) logger.printMethod();

        BackgroundTask.run(() -> compressSync(
                outZipFile,
                rootDir,
                isDirExcluded,
                isFileExcluded
        ), (e) -> {
            if (logger != null) logger.print(() -> new StringBuilder()
                    .append("compress() ---> COMPLETED")
                    .append(" ... ERROR: ").append(e)
            );
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error compressSync(
            File outZipFile,
            File rootDir,
            ActionCallback<File, Boolean> isDirExcluded,
            ActionCallback<File, Boolean> isFileExcluded
    ) {
        if (logger != null) logger.printMethod(() -> new StringBuilder()
                .append("outZipFile: ")
                .append(outZipFile)
                .append(", rootDir: ")
                .append(rootDir)
        );

        if (!outZipFile.exists()) {
            try {
                boolean b = outZipFile.createNewFile();
                if (!b) throw new IOException("creating Out Zip File Failed");
            } catch (Exception e) {
                if (logger != null) logger.printMethod(() -> new StringBuilder()
                        .append("compressSync() ---> EXCEPTION: ")
                        .append(e.toString())
                );
                return new Error(e.getMessage());
            }
        }

        if (!rootDir.isDirectory()) {
            if (logger != null) logger.print(() -> new StringBuilder()
                    .append("ERROR:: rootDir must be directory")
            );
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
                    } else if (sf.isDirectory()) {
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
        if (logger != null) logger.printMethod();

        BackgroundTask.run(() -> compressSync(
                outZipFile,
                toCompressFiles,
                rootDirOfFiles
        ), (e) -> {
            if (logger != null) logger.print(() -> new StringBuilder()
                    .append("compress() ---> COMPLETED .. ERROR: ").append(e)
            );
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error compressSync(
            File outZipFile,
            List<File> toCompressFiles,
            File rootDirOfFiles
    ) {
        if (logger != null) logger.printMethod(() -> new StringBuilder()
                .append("outZipFile: ").append(outZipFile)
                .append(", rootDirOfFiles: ").append(rootDirOfFiles)
                .append(", toCompressFiles: ")
                .append(toCompressFiles == null ? 0 : toCompressFiles.size())
                .append("-files, ")
        );

        if (!outZipFile.exists()) {
            try {
                boolean b = outZipFile.createNewFile();
                if (!b) throw new IOException("creating Out Zip File Failed");
            } catch (Exception e) {
                if (logger != null) logger.printMethod(() -> new StringBuilder()
                        .append("compressSync() ---> EXCEPTION: ")
                        .append(e.toString())
                );
                return new Error(e.getMessage());
            }
        }

        if (!rootDirOfFiles.isDirectory()) {
            if (logger != null) logger.print(() -> new StringBuilder()
                    .append("ERROR:: rootDir must be directory")
            );
            return new Error("rootDir must be directory");
        }

        OutputStream zipFileOutputStream = null;
        Error error = null;
        try {
            zipFileOutputStream = new FileOutputStream(outZipFile);
            ZipOutputStream zipStream = new ZipOutputStream(zipFileOutputStream);

            //region compress
            final String rootDirPath = rootDirOfFiles.getAbsolutePath() + "/";
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
                    if (logger != null) logger.printMethod(() -> new StringBuilder()
                            .append("compressSync() ---> EXCEPTION: ")
                            .append(e.toString())
                    );
                    error = new Error(e.getMessage());
                    break;
                } finally {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e) {
                            if (logger != null) logger.printMethod(() -> new StringBuilder()
                                    .append("compressSync() ---> EXCEPTION: ")
                                    .append(e.toString())
                            );
                        }
                    }
                }
            }
            //endregion

            //region save/close
            if (error == null) {
                try {
                    zipStream.finish();
                    zipStream.close();
                } catch (Exception e) {
                    if (logger != null) logger.printMethod(() -> new StringBuilder()
                            .append("compressSync() ---> EXCEPTION: ")
                            .append(e.toString())
                    );
                    error = new Error(e.getMessage());
                }
            }

            if (error == null) {
                try {
                    zipFileOutputStream.flush();
                    zipFileOutputStream.close();
                } catch (Exception e) {
                    if (logger != null) logger.printMethod(() -> new StringBuilder()
                            .append("compressSync() ---> EXCEPTION: ")
                            .append(e.toString())
                    );
                    error = new Error(e.getMessage());
                }
            }
            //endregion

            if (logger != null) {
                Error finalError = error;
                logger.print(() -> new StringBuilder()
                        .append("compressSync() --> COMPLETED .... ERROR: ")
                        .append(finalError == null ? "-" : finalError.error)
                );
            }

            return error;
        } catch (Exception e) {
            if (logger != null) logger.printMethod(() -> new StringBuilder()
                    .append("EXCEPTION:: ")
                    .append(e.toString())
            );
            throw new RuntimeException(e);
        } finally {
            if (zipFileOutputStream != null) {
                try {
                    zipFileOutputStream.flush();
                    zipFileOutputStream.close();
                } catch (Exception e) {
                    if (logger != null) logger.printMethod(() -> {
                        return new StringBuilder()
                                .append("compressSync() ---> EXCEPTION: ")
                                .append(e.toString());

                    });
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
        if (logger != null) logger.printMethod();

        BackgroundTask.run(() -> extractSync(
                fileInputStream,
                outputDir
        ), (e) -> {
            if (logger != null) logger.print(() -> "extract() ---> COMPLETED ... ERROR: " + e);
            if (onComplete != null) onComplete.invoke(e);
        });
    }

    public Error extractSync(
            InputStream fileInputStream,
            File outputDir
    ) {
        if (logger != null) logger.printMethod(() -> "outputDir: " + outputDir);

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
                    if (logger != null)
                        logger.printMethod(() -> "extractSync() --> EXCEPTION: " + e.getMessage());
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
            if (logger != null)
                logger.printMethod(() -> "extractSync() --> EXCEPTION: " + e.getMessage());
            return new Error(e.getMessage());
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    if (logger != null) logger.printMethod(() -> {
                        return new StringBuilder()
                                .append("extractSync() ---> EXCEPTION: ")
                                .append(e.toString());

                    });
                }
            }
        }

        return null;
    }
}
