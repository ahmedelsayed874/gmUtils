package gmutils.net.retrofit.example.callers._interfaces;

import android.net.Uri;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;

import gmutils.listeners.ResultCallback3;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public interface FileAPIs {

    void downloadFile(
            String fileURL, //http:.......
            File destFile, //destination location on your memory (you will specify first then path here) (4e: externalCacheDir())
            ResultCallback3<File, Boolean, String> onResponseReady//: (localUri: Uri, success: Boolean, msg: String) -> Unit
    ) throws FileNotFoundException;

    void downloadFile(
            String fileURL, //http:.......
            Uri localUri, //destination location on your memory (you will specify first then path here)
            OutputStream outputStream, //you get it from (for example) ContentResolver.openOutputString(Uri)
            ResultCallback3<Uri, Boolean, String> onResponseReady//: (localUri: Uri, success: Boolean, msg: String) -> Unit
    );

}
