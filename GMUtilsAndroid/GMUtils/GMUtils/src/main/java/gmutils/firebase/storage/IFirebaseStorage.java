package gmutils.firebase.storage;

import android.net.Uri;
import org.jetbrains.annotations.Nullable;
import java.io.File;

import gmutils.firebase.Response;
import gmutils.listeners.ResultCallback;

public interface IFirebaseStorage {
  void upload(Uri fileUri, String toPath, ResultCallback<Response<String>> callback);

  void getDownloadURL(String firebasePath, ResultCallback<Response<String>> callback);

  void download(String fromPath, File toDir, @Nullable File suggestedOutFile, ResultCallback<Response<File>> callback);

  void delete(String atPath, ResultCallback<Response<Boolean>> callback);
}

