package com.blogspot.gm4s1.gmutils;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.os.Environment.DIRECTORY_PICTURES;

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
public class ImageUtils {

    public static ImageUtils createInstance() {
        return new ImageUtils();
    }

    //----------------------------------------------------------------------------------------------

    public  Bitmap getBitmap(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    public  Bitmap resizeImage(Bitmap bitmap) {
        return resizeImage(bitmap, 1500);
    }

    public  Bitmap resizeImage(Bitmap bitmap, int maxOneDimensionLength) {
        if (bitmap == null) return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        final int length = maxOneDimensionLength;

        if (w * h > length * length) {
            int nW, nH;

            if (w > h) {
                nW = length;
                nH = (int) (h / (float) w * length);
            } else {
                nW = (int) (w / (float) h * length);
                nH = length;
            }

            bitmap = Bitmap.createScaledBitmap(bitmap, nW, nH, false);

        }

        return bitmap;
    }

    /**
     * to avoid OutOfMemoryException
     */
    public void scaleImageIntoView(ImageView imageView, String imagePath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * to avoid OutOfMemoryException
     */
    public void scaleImageIntoView(ImageView imageView, InputStream imageFileStream) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageFileStream, null, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeStream(imageFileStream, null, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public  byte[] encodeImage(Bitmap bitmap) {
        if (bitmap == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public  String convertToBase64(Bitmap image) {
        if (image == null) return "";
        byte[] data1 = encodeImage(image);
        return Base64.encodeToString(data1, Base64.DEFAULT);
    }

    //------------------------------------------------------------------------------------------

    public  Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context
                        .getContentResolver()
                        .openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public  Bitmap getBitmapFromUri2(Context context, Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            AppLog.print(e);
        }

        return bitmap;
    }

    //------------------------------------------------------------------------------------------

    public  void addImageToGallery(Context context, String filePath, String mimeType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);//"image/jpeg");
        contentValues.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);
    }

    public  void addImageToGallery(Context context, Uri image) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(image);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * this method won't run on Android 10
     */
    @Deprecated
    public  void addImageToGallery(Context context, File imageFile) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(imageFile);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * this method won't run on Android 10
     */
    @Deprecated
    public  void addImageToGalleryByScanner(Context ctx, File filepath) {

        MediaScannerConnection.scanFile(
                ctx,
                new String[]{filepath.getAbsolutePath()},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.w("mydebug", "file " + path + " was scanned successfully: " + uri);
                    }
                });
    }

    //------------------------------------------------------------------------------------------

    /**
     * this method won't run on Android 10
     */
    @Deprecated
    public  String saveImageToExternal(Context context, Bitmap bm, String imgName) throws IOException {
        File imgDir = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);

        File imgFile = new File(imgDir, imgName + ".png");
        imgFile.createNewFile();

        FileOutputStream out = new FileOutputStream(imgFile);
        try {
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            MediaScannerConnection.scanFile(context, new String[]{imgFile.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });

            return imgFile.getAbsolutePath();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    //------------------------------------------------------------------------------------------

    public static class SaveBitmapToDevice extends AsyncTask<Bitmap, Void, String> {
        ContentResolver contentResolver;
        String title, description;
        boolean savedOnSD;

        public SaveBitmapToDevice(ContentResolver contentResolver, String title, String description) {
            this.contentResolver = contentResolver;
            this.title = title;
            this.description = description;
        }

        @Override
        protected String doInBackground(Bitmap... cards) {
            return insertImageIntoGallery(contentResolver, cards[0], title, description);
        }

        public String insertImageIntoGallery(ContentResolver cr, Bitmap source, String title, String description) {

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, title);
            values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
            values.put(MediaStore.Images.Media.DESCRIPTION, description);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            // Add the date meta data to ensure the image is added at the front of the gallery
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            Uri url = null;
            String stringUrl = null;

            try {
                url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                if (source != null) {
                    OutputStream imageOut = cr.openOutputStream(url);
                    try {
                        source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                    } finally {
                        imageOut.close();
                    }

                    long id = ContentUris.parseId(url);
                    // Wait until MINI_KIND thumbnail is generated.
                    Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                    // This is for backward compatibility.
                    storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
                } else {
                    cr.delete(url, null, null);
                    return storeToAlternateSd(source, title);
                    // url = null;
                }
            } catch (Exception e) {
                if (url != null) {
                    cr.delete(url, null, null);
                    return storeToAlternateSd(source, title);
                    // url = null;
                }
            }

            savedOnSD = false;

            if (url != null) {
                stringUrl = url.toString();
            }

            return stringUrl;
        }

        /**
         * A copy of the Android internals StoreThumbnail method, it used with the insertImage to
         * populate the android.provider.MediaStore.Images.Media#insertImage with all the correct
         * meta data. The StoreThumbnail method is private so it must be duplicated here.
         *
         * @see MediaStore.Images.Media (StoreThumbnail private method).
         */
        private Bitmap storeThumbnail(
                ContentResolver cr,
                Bitmap source,
                long id,
                float width,
                float height,
                int kind) {

            // create the matrix to scale it
            Matrix matrix = new Matrix();

            float scaleX = width / source.getWidth();
            float scaleY = height / source.getHeight();

            matrix.setScale(scaleX, scaleY);

            Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                    source.getWidth(),
                    source.getHeight(), matrix,
                    true
            );

            ContentValues values = new ContentValues(4);
            values.put(MediaStore.Images.Thumbnails.KIND, kind);
            values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
            values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
            values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());

            Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

            try {
                OutputStream thumbOut = cr.openOutputStream(url);
                thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
                thumbOut.close();
                return thumb;
            } catch (FileNotFoundException ex) {
                Log.e("IMAGE_COMPRESSION_ERROR", "File not found");
                ex.printStackTrace();
                return null;
            } catch (IOException ex) {
                Log.e("IMAGE_COMPRESSION_ERROR", "IO Exception");
                ex.printStackTrace();
                return null;
            }
        }

        /**
         * If we have issues saving into our MediaStore, save it directly to our SD card. We can then interact with this file
         * directly, opposed to pulling from the MediaStore. Again, this is a backup method if things don't work out as we
         * would expect (seeing as most devices will have a MediaStore).
         *
         * @param src
         * @param title
         * @return - the file's path
         */
        private String storeToAlternateSd(Bitmap src, String title) {
            if (src == null)
                return null;

            File sdCardDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "My Cards");
            if (!sdCardDirectory.exists())
                sdCardDirectory.mkdir();

            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy - (hh.mm.a)", Locale.US);
            File image = new File(sdCardDirectory, title + " -- [" + sdf.format(new Date()) + "].jpg");
            try {
                FileOutputStream imageOut = new FileOutputStream(image);
                src.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                imageOut.close();
                savedOnSD = true;
                return image.getAbsolutePath();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                return null;
            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        public void onPostExecute(String url) {
//            if (url != null) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                if (savedOnSD) {
//                    File file = new File(url);
//                    if (file.exists())
//                        intent.setDataAndType(Uri.fromFile(file), "image/jpeg");
//                    else
//                        return;
//                } else
//                    intent.setDataAndType(Uri.parse(url), "image/jpeg");
//
//                startActivity(intent);
//            } else
//                Toast.makeText(ActivityA.this, getString(R.string.error_compressing), Toast.LENGTH_SHORT).show();
        }

    }

    //------------------------------------------------------------------------------------------

    public  MultipartBody.Part createRetrofitMultipartBodyForImage(
            ImageView imageView,
            String paramName
    ) {
        Bitmap bitmap = getBitmap(imageView);

        if (bitmap != null) {
            try {
                bitmap = resizeImage(bitmap);
            } catch (Exception e) {
            }

            return createRetrofitMultipartBodyForImage(bitmap, paramName);
        }

        return null;
    }

    public  MultipartBody.Part createRetrofitMultipartBodyForImage(
            Bitmap image,
            String paramName
    ) {
        try {
            RequestBody requestFile = createRetrofitRequestBodyForImage(image);

            MultipartBody.Part photo =
                    MultipartBody.Part.createFormData(
                            paramName,
                            new Date().toString() + ".png",
                            requestFile
                    );

            return photo;
        } catch (Exception e) {
            return null;
        }
    }

    public  RequestBody createRetrofitRequestBodyForImage(
            Bitmap image
    ) {

        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, bs);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bs.toByteArray());

            return requestFile;
        } catch (Exception e) {
            return null;
        }
    }

    //------------------------------------------------------------------------------------------

    public ScreenshotHelper screenshotHelper() { return new ScreenshotHelper(); }

    public static class ScreenshotHelper {

        public  Bitmap takeScreenshotOfView(View view, OutputStream outputStream) {
            try {
                Bitmap bitmap = Bitmap.createBitmap(
                        view.getWidth(),
                        view.getHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public  Bitmap takeScreenshotOfEntireScreen(Window window, OutputStream outputStream) {
            try {
                View v1 = window.getDecorView().getRootView();

                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();

                return bitmap;
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
