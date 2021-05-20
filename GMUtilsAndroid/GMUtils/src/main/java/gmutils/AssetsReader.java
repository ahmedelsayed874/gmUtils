package gmutils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class AssetsReader {

    public static InputStream openFile(Context context, String assetPath) throws IOException {
        if (TextUtils.isEmpty(assetPath)) return null;

        AssetManager assets = context.getAssets();
        InputStream inputStream = assets.open(assetPath);

        return inputStream;
    }

    public static Bitmap getBitmap(Context context, String assetPath) {
        if (TextUtils.isEmpty(assetPath)) return null;
        Bitmap bitmap = null;
        InputStream inputStream = null;

        try {
            context = context.getApplicationContext();
            inputStream = openFile(context, assetPath);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bitmap;
    }

    public static String getText(Context context, String assetPath) {
        if (TextUtils.isEmpty(assetPath)) return null;
        InputStream inputStream = null;

        try {
            inputStream = openFile(context, assetPath);
            DataInputStream bis = new DataInputStream(inputStream);

            StringBuilder buffer = new StringBuilder();
            try {
                byte[] bytes = new byte[bis.available()];
                bis.read(bytes);
                String t = new String(bytes);
                buffer.append(t);

            } catch (Exception e1) {}

            return buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
