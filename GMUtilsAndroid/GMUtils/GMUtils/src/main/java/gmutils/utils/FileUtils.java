package gmutils.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gmutils.logger.Logger;
import gmutils.R;

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
public class FileUtils {

    public static FileUtils createInstance() {
        return new FileUtils();
    }


    //----------------------------------------------------------------------------------------------

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public File createFolderInPublicStorage(Context context, String dirName) {
        File root = Environment.getExternalStorageDirectory();
        return createFolderOnStorage(context, dirName, root);
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public File createFolderOnStorage(Context context, String dirName, File root) {
        if (dirName == null) dirName = context.getPackageName();

        File dir = new File(root, dirName);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                return null;
            }
        }

        return dir;
    }

    //----------------------------------------------------------------------------------------------

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public File createFileInCacheStorage(Context context, String dirName, String fileName) {
        File root = context.getCacheDir();
        return createFileOnStorage(context, dirName, fileName, root);
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public File createFileInPublicStorage(Context context, String dirName, String fileName) {
        File root = Environment.getExternalStorageDirectory();
        return createFileOnStorage(context, dirName, fileName, root);
    }

    @RequiresPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public File createFileOnStorage(Context context, String dirName, String fileName, File root) {
        if (dirName == null) dirName = context.getPackageName();

        File dir = new File(root, dirName);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                //root = context.getExternalFilesDir(null);
                //dir = new File(root, dirName);
                //if (!dir.exists()) {
                //if (!dir.mkdirs()) {
                return null;
                //}
                //}
            }
        }

        if (TextUtils.isEmpty(fileName)) {
            Date now = new Date();
            fileName = android.text.format.DateFormat.format("yyyyMMddhhmmss", now).toString();
        }

        File file = new File(dir, fileName);
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                return null;
            }
        }

        return file;
    }

    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean createFileOnStorageUsingFileExplorer(Fragment fragment, String fileName, String mimeType, @Nullable Uri pickerInitialUri, int requestId) {
        Intent intent = createFileOnStorageUsingFileExplorerIntent(fileName, mimeType, pickerInitialUri);

        if (intent.resolveActivity(fragment.getActivity().getPackageManager()) != null) {
            fragment.startActivityForResult(intent, requestId);
            return true;
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean createFileOnStorageUsingFileExplorer(Activity activity, String fileName, String mimeType, @Nullable Uri pickerInitialUri, int requestId) {
        Intent intent = createFileOnStorageUsingFileExplorerIntent(fileName, mimeType, pickerInitialUri);

        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(intent, requestId);
            return true;
        }

        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Intent createFileOnStorageUsingFileExplorerIntent(String fileName, String mimeType, @Nullable Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);//"application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);//"invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        if (pickerInitialUri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
            }
        }

        return intent;
    }

    //----------------------------------------------------------------------------------------------

    private void validateInputsOfCreatingFileUsingFileProvider(File root, String[] fileExtension) throws IOException {
        if (!root.exists()) {
            if (!root.mkdirs()) {
                throw new IOException("Couldn't create file on: " + root);
            }
        }

        if (!fileExtension[0].startsWith(".")) fileExtension[0] = "." + fileExtension[0];
    }

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public File createFileUsingFileProvider(File root, String fileExtension) throws IOException {
        String imageFileName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(new Date());

        String[] ext = new String[]{fileExtension};
        validateInputsOfCreatingFileUsingFileProvider(root, ext);

        File file = File.createTempFile(
                imageFileName,   /* prefix */
                ext[0],          /* suffix */
                root            /* directory */
        );

        Logger.d().print(() -> "createFileUsingFileProvider-> " + file);

        return file;
    }

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public File createFileUsingFileProvider(File root, String fileName, String extension) throws IOException {
        String[] ext = new String[]{extension};
        validateInputsOfCreatingFileUsingFileProvider(root, ext);

        File file = new File(root, fileName + ext[0]);
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new IOException("Couldn't create file on: " + root);
            }
        }
        return file;
    }

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public Uri createFileUsingFileProvider(Context context, File root, String fileExtension) throws IOException {
        File file = createFileUsingFileProvider(root, fileExtension);
        return createUriForFileUsingFileProvider(context, file);
    }

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public Uri createFileUsingFileProvider(Context context, File root, String fileName, String fileExtension) throws IOException {
        File file = createFileUsingFileProvider(root, fileName, fileExtension);
        return createUriForFileUsingFileProvider(context, file);
    }

    /**
     * must add in manifest
     * <provider
     * android:name="androidx.core.content.FileProvider"
     * android:authorities="APP_PACKAGE_NAME.fileprovider"
     * android:exported="false"
     * android:grantUriPermissions="true">
     * <meta-data
     * android:name="android.support.FILE_PROVIDER_PATHS"
     * android:resource="@xml/file_paths" />
     * </provider>
     * <p>
     * ------------------------------------------------------------------
     * add this this text to xml/file_paths
     * <p>
     * <?xml version="1.0" encoding="utf-8"?>
     * <paths xmlns:android="http://schemas.android.com/apk/res/android">
     * <files-path name="my_images" path="Pictures/" />
     * <external-files-path name="my_images" path="Pictures/" />
     * </paths>
     *
     * @param context
     * @param file
     * @return
     */
    public Uri createUriForFileUsingFileProvider(Context context, File file) {
        String authority;

        try {
            ComponentName cm = new ComponentName(context, "androidx.core.content.FileProvider");
            ProviderInfo providerInfo = context.getPackageManager().getProviderInfo(cm, 0);
            authority = providerInfo.authority;
        } catch (Exception e) {
            String er = e.getMessage() + "\n---------------------------------\n" + getFileProviderExceptionMessage();
            throw new IllegalArgumentException(er);
        }

        try {
            return FileProvider.getUriForFile(context, authority, file);
        } catch (Exception e) {
            throw new IllegalArgumentException("review defined <path> in xml file of FileProvider", e);
        }
    }

    private String getFileProviderExceptionMessage() {

        String sb = "please create a file in 'res/xml' path with a name of 'file_paths' or whatever you want" +
                "\n" +
                "this file will contain the following: (for example) .. (I already created one for you)" +
                "\n" +
                "check this for more info: https://developer.android.com/reference/androidx/core/content/FileProvider" +
                "\n" +
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<paths xmlns:android=\"http://schemas.android.com/apk/res/android\">" +
                "\n\n\t<!-- ============== INTERNAL-FILES ============== -->" +
                "\n\t<!-- Context.getFilesDir() -->" +
                "\n\t<files-path name=\"files\" path=\"_Files/\" />" +
                "\n\n\t<!-- getCacheDir() -->" +
                "\n\t<cache-path name=\"files\" path=\"_Files/\" />" +
                "\n\n\t<!-- ============== EXTERNAL-FILES ============== -->" +
                "\n\t<!-- Environment.getExternalStorageDirectory() -->" +
                "\n\t<external-path name=\"files\" path=\"_Files/\" />" +
                "\n\t<!-- Context#getExternalFilesDir(String) || Context.getExternalFilesDir(null) -->" +
                "\n\t<external-files-path name=\"pics\" path=\"Pictures/\" />" +
                "\n\t<external-files-path name=\"files\" path=\"_Files/\" />" +
                "\n\t<!-- Context.getExternalCacheDir() -->" +
                "\n\t<external-cache-path name=\"files\" path=\"_Files/\" />" +
                "\n\t<!-- Context.getExternalMediaDirs() -->" +
                "\n\t<external-media-path name=\"files\" path=\"_Files/\" />" +
                "\n" +
                "</paths>\n" +
                "-----------------------------------------------------" +
                "\n\n" +
                "then add The following to your manifest file:" +
                "\n" +
                "<provider" +
                "\n\tandroid:name=\"androidx.core.content.FileProvider\"" +
                "\n\tandroid:authorities=\"APP_PACKAGE_NAME.fileprovider\"" +
                "\n\tandroid:exported=\"false\"" +
                "\n\tandroid:grantUriPermissions=\"true\">" +
                "\n\t<meta-data" +
                "\n\t\tandroid:name=\"android.support.FILE_PROVIDER_PATHS\"" +
                "\n\t\tandroid:resource=\"@xml/file_paths\" />\n" +
                "</provider>" +
                "\n" +
                "\n----------------------------------------------------" +
                "\n" +
                "\nmake sure to replace APP_PACKAGE_NAME with your own package name; ex: (com.example)";

        return sb;
    }

    //----------------------------------------------------------------------------------------------

    public boolean showFileExplorer(Fragment fragment, String mimeType, @Nullable Uri pickerInitialUri, int requestId) {
        Intent intent = canShowFileExplorer(fragment.getActivity(), mimeType, pickerInitialUri, requestId);

        if (intent != null) {
            fragment.startActivityForResult(intent, requestId);
            return true;
        }

        return false;
    }

    public boolean showFileExplorer(Activity activity, String mimeType, @Nullable Uri pickerInitialUri, int requestId) {
        Intent intent = canShowFileExplorer(activity, mimeType, pickerInitialUri, requestId);

        if (intent != null) {
            activity.startActivityForResult(intent, requestId);
            return true;
        }

        return false;
    }

    private Intent canShowFileExplorer(Activity activity, String mimeType, @Nullable Uri pickerInitialUri, int requestId) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Intent intent = createShowingFileExplorerIntent19(mimeType, pickerInitialUri);

            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                return intent;
            } else {
                intent = createShowingFileExplorerIntent(mimeType);
                if (intent.resolveActivity(activity.getPackageManager()) != null)
                    return intent;
            }

        } else {
            Intent intent = createShowingFileExplorerIntent(mimeType);
            if (intent.resolveActivity(activity.getPackageManager()) != null)
                return intent;
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Intent createShowingFileExplorerIntent19(String mimeType, @Nullable Uri pickerInitialUri) {
        return createShowingFileExplorerIntent(Intent.ACTION_OPEN_DOCUMENT, mimeType, pickerInitialUri);
    }

    private Intent createShowingFileExplorerIntent(String mimeType) {
        return createShowingFileExplorerIntent(Intent.ACTION_GET_CONTENT, mimeType, null);
    }

    private Intent createShowingFileExplorerIntent(String action, String mimeType, @Nullable Uri pickerInitialUri) {
        if (TextUtils.isEmpty(mimeType)) mimeType = "*/*";

        Intent intent = new Intent(action);

        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(mimeType);

        if (pickerInitialUri != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
            }
        }

        return intent;

    }

    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showFolderExplorer(Activity activity, int requestCode) {
        showFolderExplorer(activity, null, null, null, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showFolderExplorer(Activity activity, Uri uriToLoad, int requestCode) {
        showFolderExplorer(activity, null, null, uriToLoad, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showFolderExplorer(Fragment fragment, int requestCode) {
        showFolderExplorer(null, fragment, null, null, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showFolderExplorer(Fragment fragment, Uri uriToLoad, int requestCode) {
        showFolderExplorer(null, fragment, null, uriToLoad, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void showFolderExplorer(android.app.Fragment fragment, int requestCode) {
        showFolderExplorer(null, null, fragment, null, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showFolderExplorer(android.app.Fragment fragment, Uri uriToLoad, int requestCode) {
        showFolderExplorer(null, null, fragment, uriToLoad, requestCode);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showFolderExplorer(Activity activity, Fragment fragment1, android.app.Fragment fragment2, Uri uriToLoad, int requestCode) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);
        }

        if (activity != null) activity.startActivityForResult(intent, requestCode);
        else if (fragment1 != null) fragment1.startActivityForResult(intent, requestCode);
        else if (fragment2 != null) fragment2.startActivityForResult(intent, requestCode);
    }

    //----------------------------------------------------------------------------------------------

    public void viewFileContent(Context context, Uri uri, String mimeType) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mimeType);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            Intent chooser = Intent.createChooser(intent, context.getString(R.string.app_name));
            context.startActivity(chooser);
        }
    }

    //----------------------------------------------------------------------------------------------

    @RequiresPermission(value = Manifest.permission.READ_EXTERNAL_STORAGE)
    public List<File> collectFiles(String[] extensions) {
        List<File> fileList = new ArrayList<>();
        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        return findFiles(extensions, dir, fileList);
    }

    private List<File> findFiles(String[] extensions, File dir, List<File> fileList) {
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (File file : listFile) {
                if (file.isDirectory()) {
                    findFiles(extensions, file, fileList);
                } else {
                    for (String extension : extensions) {
                        if (file.getName().endsWith(extension)) {
                            fileList.add(file);
                        }
                    }
                }
            }
        }
        return fileList;
    }

    @RequiresPermission(value = Manifest.permission.READ_EXTERNAL_STORAGE)
    public List<Uri> collectFiles(Context context, String[] mimeTypes) {
        String[] projection = new String[]{
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATE_MODIFIED,
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.TITLE,
                MediaStore.Files.FileColumns.SIZE
        };

        StringBuilder mimeTypesStr = new StringBuilder();
        if (mimeTypes != null) {
            for (String mimeType : mimeTypes) {
                mimeTypesStr
                        .append("'")
                        .append(mimeType)
                        .append("', ");
            }

            mimeTypesStr
                    .deleteCharAt(mimeTypesStr.length() - 1)
                    .deleteCharAt(mimeTypesStr.length() - 1);
        }

        String whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN (" + mimeTypesStr + ")";
        String orderBy = MediaStore.Files.FileColumns.TITLE + " ASC";

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                projection,
                whereClause,
                null,
                orderBy
        );

        List<Uri> fileURIs = new ArrayList<>();

        if (cursor != null) {
            int idCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID);
            int sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE);

            if (cursor.moveToFirst()) {
                do {
                    Uri fileUri = Uri.withAppendedPath(
                            MediaStore.Files.getContentUri("external"),
                            cursor.getString(idCol)
                    );

                    fileURIs.add(fileUri);
                } while (cursor.moveToNext());
            }
        }

        return fileURIs;
    }

    //----------------------------------------------------------------------------------------------

    public String findMimeType(String extension) {
        Map<String, String> mimeTypes = new HashMap<>();

        mimeTypes.put("bin", "application/octet-stream");
        mimeTypes.put("asd", "application/astound");
        mimeTypes.put("asn", "application/astound");
        mimeTypes.put("lcc", "application/fastman");
        mimeTypes.put("jar", "application/java-archive");
        mimeTypes.put("ser", "application/java-serialized-object");
        mimeTypes.put("class", "application/java-vm");
        mimeTypes.put("hqx", "application/mac-binhex40");
        mimeTypes.put("sit", "application/x-stuffit");
        mimeTypes.put("mbd", "application/mbedlet");
        mimeTypes.put("doc", "application/msword");
        mimeTypes.put("rtf", "application/msword");
        mimeTypes.put("wiz", "application/msword");
        mimeTypes.put("dot", "application/msword");
        mimeTypes.put("oda", "application/oda");
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("ps", "application/postscript");
        mimeTypes.put("eps", "application/postscript");
        mimeTypes.put("ai", "application/postscript");
        mimeTypes.put("smp", "application/studiom");
        mimeTypes.put("tbt", "application/timbuktu");
        mimeTypes.put("xlt", "application/vnd.ms-excel");
        mimeTypes.put("xlm", "application/vnd.ms-excel");
        mimeTypes.put("xlc", "application/vnd.ms-excel");
        mimeTypes.put("xla", "application/vnd.ms-excel");
        mimeTypes.put("xlw", "application/vnd.ms-excel");
        mimeTypes.put("xls", "application/vnd.ms-excel");
        mimeTypes.put("pot", "application/vnd.ms-powerpoint");
        mimeTypes.put("pps", "application/vnd.ms-powerpoint");
        mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
        mimeTypes.put("mpp", "application/vnd.ms-project");
        mimeTypes.put("hlp", "application/winhlp");
        mimeTypes.put("js", "application/x-javascript");
        mimeTypes.put("jsu", "application/x-javascript;charset=UTF-8");
        mimeTypes.put("jnlp", "application/x-java-jnlp-file");
        mimeTypes.put("aim", "application/x-aim");
        mimeTypes.put("asp", "application/x-asap");
        mimeTypes.put("csh", "application/x-csh");
        mimeTypes.put("dvi", "application/x-dvi");
        mimeTypes.put("etc", "application/x-earthtime");
        mimeTypes.put("evy", "application/x-envoy");
        mimeTypes.put("gtar", "application/x-gtar");
        mimeTypes.put("cpio", "application/x-cpio");
        mimeTypes.put("hdf", "application/x-hdf");
        mimeTypes.put("latex", "application/x-latex");
        mimeTypes.put("jsc", "application/x-javascript-config");
        mimeTypes.put("fm", "application/x-maker");
        mimeTypes.put("mif", "application/x-mif");
        mimeTypes.put("mi", "application/x-mif");
        mimeTypes.put("mocha", "application/x-mocha");
        mimeTypes.put("moc", "application/x-mocha");
        mimeTypes.put("mdb", "application/x-msaccess");
        mimeTypes.put("crd", "application/x-mscardfile");
        mimeTypes.put("clp", "application/x-msclip");
        mimeTypes.put("m13", "application/x-msmediaview");
        mimeTypes.put("m14", "application/x-msmediaview");
        mimeTypes.put("wmf", "application/x-msmetafile");
        mimeTypes.put("mny", "application/x-msmoney");
        mimeTypes.put("pub", "application/x-mspublisher");
        mimeTypes.put("scd", "application/x-msschedule");
        mimeTypes.put("trm", "application/x-msterminal");
        mimeTypes.put("wri", "application/x-mswrite");
        mimeTypes.put("ins", "application/x-NET-Install");
        mimeTypes.put("nc", "application/x-netcdf");
        mimeTypes.put("cdf", "application/x-netcdf");
        mimeTypes.put("proxy", "application/x-ns-proxy-autoconfig");
        mimeTypes.put("slc", "application/x-salsa");
        mimeTypes.put("sh", "application/x-sh");
        mimeTypes.put("shar", "application/x-shar");
        mimeTypes.put("spr", "application/x-sprite");
        mimeTypes.put("sprite", "application/x-sprite");
        mimeTypes.put("tar", "application/x-tar");
        mimeTypes.put("tcl", "application/x-tcl");
        mimeTypes.put("pl", "application/x-perl");
        mimeTypes.put("tex", "application/x-tex");
        mimeTypes.put("texinfo", "application/x-texinfo");
        mimeTypes.put("texi", "application/x-texinfo");
        mimeTypes.put("tbp", "application/x-timbuktu");
        mimeTypes.put("tki", "application/x-tkined");
        mimeTypes.put("tkined", "application/x-tkined");
        mimeTypes.put("man", "application/x-troff-man");
        mimeTypes.put("me", "application/x-troff-me");
        mimeTypes.put("ms", "application/x-troff-ms");
        mimeTypes.put("t", "application/x-troff");
        mimeTypes.put("tr", "application/x-troff");
        mimeTypes.put("roff", "application/x-troff");
        mimeTypes.put("src", "application/x-wais-source");
        mimeTypes.put("zip", "application/zip");
        mimeTypes.put("enc", "application/pre-encrypted");
        mimeTypes.put("crl", "application/x-pkcs7-crl");
        mimeTypes.put("ckl", "application/x-fortezza-ckl");
        mimeTypes.put("dtd", "application/xml-dtd");

        mimeTypes.put("au", "audio/basic");
        mimeTypes.put("snd", "audio/basic");
        mimeTypes.put("es", "audio/echospeech");
        mimeTypes.put("esl", "audio/echospeech");
        mimeTypes.put("midi", "audio/midi");
        mimeTypes.put("mid", "audio/midi");
        mimeTypes.put("aif", "audio/x-aiff");
        mimeTypes.put("aiff", "audio/x-aiff");
        mimeTypes.put("aifc", "audio/x-aiff");
        mimeTypes.put("wav", "audio/x-wav");
        mimeTypes.put("ra", "audio/x-pn-realaudio");
        mimeTypes.put("ram", "audio/x-pn-realaudio");
        mimeTypes.put("pac", "audio/x-pac");
        mimeTypes.put("pae", "audio/x-epac");
        mimeTypes.put("lam", "audio/x-liveaudio");

        mimeTypes.put("dwf", "drawing/x-dwf");

        mimeTypes.put("fif", "image/fif");
        mimeTypes.put("ico", "image/x-icon");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("ief", "image/ief");
        mimeTypes.put("ifs", "image/ifs");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpe", "image/jpeg");
        mimeTypes.put("jfif", "image/jpeg");
        mimeTypes.put("pjpeg", "image/jpeg");
        mimeTypes.put("pjp", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("tiff", "image/tiff");
        mimeTypes.put("tif", "image/tiff");
        mimeTypes.put("dwg", "image/vnd");
        mimeTypes.put("svf", "image/vnd");
        mimeTypes.put("wi", "image/wavelet");
        mimeTypes.put("bmp", "image/bmp");
        mimeTypes.put("pcd", "image/x-photo-cd");
        mimeTypes.put("ras", "image/x-cmu-raster");
        mimeTypes.put("pnm", "image/x-portable-anymap");
        mimeTypes.put("pbm", "image/x-portable-bitmap");
        mimeTypes.put("pgm", "image/x-portable-graymap");
        mimeTypes.put("ppm", "image/x-portable-pixmap");
        mimeTypes.put("rgb", "image/x-rgb");
        mimeTypes.put("xbm", "image/x-xbitmap");
        mimeTypes.put("xpm", "image/x-xpixmap");
        mimeTypes.put("xwd", "image/x-xwindowdump");

        mimeTypes.put("css", "text/css");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("rtx", "text/richtext");
        mimeTypes.put("tsv", "text/tab-separated-values");
        mimeTypes.put("etx", "text/x-setext");
        mimeTypes.put("talk", "text/x-speech");
        mimeTypes.put("xml", "text/xml");
        mimeTypes.put("xul", "text/xul");

        mimeTypes.put("fvi", "video/isivideo");
        mimeTypes.put("mpeg", "video/mpeg");
        mimeTypes.put("mpg", "video/mpeg");
        mimeTypes.put("mpe", "video/mpeg");
        mimeTypes.put("mpv", "video/mpeg");
        mimeTypes.put("vbs", "video/mpeg");
        mimeTypes.put("mpegv", "video/mpeg");
        mimeTypes.put("mpv2", "video/x-mpeg2");
        mimeTypes.put("mp2v", "video/x-mpeg2");
        mimeTypes.put("avi", "video/msvideo");
        mimeTypes.put("qt", "video/quicktime");
        mimeTypes.put("mov", "video/quicktime");
        mimeTypes.put("moov", "video/quicktime");
        mimeTypes.put("viv", "video/vivo");
        mimeTypes.put("vivo", "video/vivo");
        mimeTypes.put("wv", "video/wavelet");
        mimeTypes.put("movie", "video/x-sgi-movie");

        return mimeTypes.get(extension);
    }

    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPathFromUri(Context context, Uri uri) {

        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) { // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1];
                }

            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.parseLong(id)
                );
                return getDataColumn(context, contentUri, null, null);

            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equalsIgnoreCase(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                } else if ("video".equalsIgnoreCase(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                } else if ("audio".equalsIgnoreCase(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            else
                return getDataColumn(
                        context,
                        uri,
                        null,
                        null
                );
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = new String[]{column};

        try {
            cursor = context.getContentResolver().query(
                    uri, projection, selection, selectionArgs,
                    null
            );
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) cursor.close();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    //----------------------------------------------------------------------------------------------

    public String readStream(InputStream inputStream) throws IOException {
        String text = null;

        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder stringBuffer = new StringBuilder();

            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }

            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            text = stringBuffer.toString();
        }

        return text;
    }

}