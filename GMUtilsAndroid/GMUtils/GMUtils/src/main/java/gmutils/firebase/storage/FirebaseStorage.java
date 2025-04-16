package gmutils.firebase.storage;

import android.net.Uri;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import gmutils.DateOp;
import gmutils.StringSet;
import gmutils.firebase.Response;
import gmutils.listeners.ResultCallback;

public class FirebaseStorage implements IFirebaseStorage {
    public final com.google.firebase.storage.FirebaseStorage storage;

    public FirebaseStorage() {
        try {
            Class.forName("com.google.firebase.storage.FirebaseStorage");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("add this line to gradle script file:\n" +
                    "//https://firebase.google.com/docs/database/android/start\n" +
                    "implementation 'com.google.firebase:firebase-storage:19.6.0'");
        }
        storage = com.google.firebase.storage.FirebaseStorage.getInstance();
    }

    /*private String _refinePath(String path) {
        try {
            path = FirebaseUtils.refinePathFragmentNames(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return path;
    }*/

    //----------------------------------------------------------------------------

    @Override
    public void upload(Uri fileUri, String toPath, ResultCallback<Response<String>> callback) {
        //toPath = _refine Path(toPath);
        var ref = storage.getReference(toPath);
        upload(fileUri, ref, callback);
    }

    public void upload(
            Uri fileUri,
            StorageReference reference,
            ResultCallback<Response<String>> callback
    ) {
        UploadTask uploadTask = reference.putFile(fileUri);
        uploadTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                getDownloadURL(reference, callback);

            } else {
                if (callback != null) {
                    String erEn;
                    String erAr;
                    Exception exception = task.getException();
                    if (exception != null) {
                        erEn = exception.getMessage();
                        erAr = erEn;
                    } else {
                        erEn = "Upload failed";
                        erAr = "تعذر ارسال الملف";
                    }
                    callback.invoke(Response.failed(new StringSet(erEn, erAr)));
                }
            }
        });
    }

    //----------------------------------------------------------------------------

    @Override
    public void getDownloadURL(String firebasePath, ResultCallback<Response<String>> callback) {
        //firebasePath = _refine Path(firebasePath);
        var reference = storage.getReference(firebasePath);
        getDownloadURL(reference, callback);
    }

    public void getDownloadURL(StorageReference reference, ResultCallback<Response<String>> callback) {
        reference.getDownloadUrl().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                if (callback != null) {
                    callback.invoke(Response.success(task1.getResult().toString()));
                }
            } else {
                if (callback != null) {
                    String erEn;
                    String erAr;
                    Exception exception = task1.getException();
                    if (exception != null) {
                        erEn = exception.getMessage();
                        erAr = erEn;
                    } else {
                        erEn = "Creating download link failed";
                        erAr = "تعذر انشاء رابط للتحميل";
                    }
                    callback.invoke(Response.failed(new StringSet(erEn, erAr)));
                }
            }
        });
    }

    //----------------------------------------------------------------------------

    @Override
    public void download(String firebasePath, File toDir, @Nullable File suggestedOutFile, ResultCallback<Response<File>> callback) {
        File outFile;

        if (suggestedOutFile != null) {
            outFile = suggestedOutFile;
        } else {
            var i = firebasePath.lastIndexOf('/');
            String fileName;
            if (i >= 0) {
                fileName = firebasePath.substring(i + 1);
            } else {
                fileName = DateOp.getInstance().formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss, true).replace(
                        "-",
                        ""
                );
            }

            outFile = new File(toDir, fileName);
        }

        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //fromPath = _refine Path(fromPath);
        storage.getReference(firebasePath).getFile(outFile).addOnCompleteListener(task -> {
            if (callback != null) {
                if (task.isSuccessful()) {
                    callback.invoke(Response.success(outFile));
                } else {
                    String erEn;
                    String erAr;
                    Exception exception = task.getException();
                    if (exception != null) {
                        erEn = exception.getMessage();
                        erAr = erEn;
                    } else {
                        erEn = "Creating download link failed";
                        erAr = "تعذر انشاء رابط للتحميل";
                    }
                    callback.invoke(Response.failed(new StringSet(erEn, erAr)));
                }
            }
        });
    }

    //----------------------------------------------------------------------------

    @Override
    public void delete(String atPath, ResultCallback<Response<Boolean>> callback) {
        //atPath = _refine Path(atPath);
        var reference = storage.getReference(atPath);
        reference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) {
                    callback.invoke(Response.success(true));
                }
            } else {
                if (callback != null) {
                    String erEn;
                    String erAr;
                    Exception exception = task.getException();
                    if (exception != null) {
                        erEn = exception.getMessage();
                        erAr = erEn;
                    } else {
                        erEn = "Creating download link failed";
                        erAr = "تعذر انشاء رابط للتحميل";
                    }
                    callback.invoke(Response.failed(new StringSet(erEn, erAr)));
                }
            }
        });
    }


}
