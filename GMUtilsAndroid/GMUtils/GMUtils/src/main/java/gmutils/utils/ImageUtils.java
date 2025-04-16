package gmutils.utils;

import static android.os.Environment.DIRECTORY_PICTURES;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import gmutils.R;
import gmutils.listeners.ActionCallback;
//import okhttp3.RequestBody.Companion.toRequestBody;

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
public class ImageUtils {

    public static ImageUtils createInstance() {
        return new ImageUtils();
    }

    //----------------------------------------------------------------------------------------------

    public Bitmap getBitmap(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        return null;
    }

    public Bitmap resizeImage(Bitmap bitmap) {
        return resizeImage(bitmap, 1500);
    }

    public Bitmap resizeImage(Bitmap bitmap, int maxOneDimensionLength) {
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

    //----------------------------------------------------------------------------------------------

    /**
     * to avoid OutOfMemoryException
     */
    public void scaleImageSafelyIntoView(ImageView imageView, File imageFile) {
        try {
            FileInputStream stream = new FileInputStream(imageFile);
            scaleImageSafelyIntoView(imageView, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * to avoid OutOfMemoryException
     */
    public void scaleImageSafelyIntoView(ImageView imageView, Uri imageUri) {
        try {
            InputStream stream = imageView.getContext().getContentResolver().openInputStream(imageUri);
            scaleImageSafelyIntoView(imageView, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to avoid OutOfMemoryException
     */
    public void scaleImageSafelyIntoView(ImageView imageView, InputStream imageFileStream) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        Bitmap bitmap = null;
        try {
            byte[] imgBytes = new byte[imageFileStream.available()];
            imageFileStream.read(imgBytes);

            bitmap = scaleImageSafely(targetW, targetH, imgBytes);
        } catch (Exception e) {
        }

        imageView.setImageBitmap(bitmap);
    }


    /**
     * to avoid OutOfMemoryException
     */
    public Bitmap scaleImageSafely(int targetWidth, int targetHeight, File imageFile) {
        try {
            FileInputStream stream = new FileInputStream(imageFile);
            return scaleImageSafely(targetWidth, targetHeight, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * to avoid OutOfMemoryException
     */
    public Bitmap scaleImageSafely(int targetWidth, int targetHeight, Uri imageUri, Context context) {
        try {
            InputStream stream = context.getContentResolver().openInputStream(imageUri);
            return scaleImageSafely(targetWidth, targetHeight, stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * to avoid OutOfMemoryException
     */
    public Bitmap scaleImageSafely(int targetWidth, int targetHeight, InputStream imageFileStream) {
        Bitmap bitmap = null;

        try {
            byte[] imgBytes = new byte[imageFileStream.available()];
            imageFileStream.read(imgBytes);

            bitmap = scaleImageSafely(targetWidth, targetHeight, imgBytes);
        } catch (Exception e) {
        }

        return bitmap;

//        return scaleImageSafely(targetWidth, targetHeight, input -> {
//            Bitmap bitmap = BitmapFactory.decodeStream(imageFileStream, null, input);
//            try {
//                imageFileStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        });
    }


    /**
     * to avoid OutOfMemoryException
     */
    public Bitmap scaleImageSafely(int targetWidth, int targetHeight, byte[] imageBytes) {
        return scaleImageSafely(targetWidth, targetHeight, input ->
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, input)
        );
    }

    /**
     * to avoid OutOfMemoryException
     */
    public Bitmap scaleImageSafely(int targetWidth, int targetHeight, ActionCallback<BitmapFactory.Options, Bitmap> decoder) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        decoder.invoke(bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetWidth, photoH / targetHeight);
        if (scaleFactor <= 0) scaleFactor = 1;

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = decoder.invoke(bmOptions);

        return bitmap;
    }

    //------------------------------------------------------------------------------------------

    public byte[] encodeImage(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        if (bitmap == null) return null;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(compressFormat, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        try {
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public String convertToBase64(Bitmap image, Bitmap.CompressFormat compressFormat) {
        if (image == null) return "";
        byte[] data1 = encodeImage(image, compressFormat);
        return Base64.encodeToString(data1, Base64.DEFAULT);
    }

    //------------------------------------------------------------------------------------------

    public Bitmap openBitmapFromUri1(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }

    public Bitmap openBitmapFromUri2(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context
                        .getContentResolver()
                        .openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public Bitmap openBitmapFile(File image) throws IOException {
        FileInputStream fis = new FileInputStream(image);
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        fis.close();
        return bitmap;
    }

    public Bitmap openBitmapFromAssets(Context context, String assetName) throws IOException {
        InputStream inputStream = context.getAssets().open(assetName);
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();
        return bitmap;
    }

    //------------------------------------------------------------------------------------------

    public void addImageToGallery(Context context, String filePath, String mimeType) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType);//"image/jpeg");
        contentValues.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues);
    }

    public void addImageToGallery(Context context, Uri image) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(image);
        context.sendBroadcast(mediaScanIntent);
    }

    /**
     * this method won't run on Android 10
     */
    @Deprecated
    public void addImageToGallery(Context context, File imageFile) {
        Uri contentUri = Uri.fromFile(imageFile);
        addImageToGallery(context, contentUri);
    }

    /**
     * this method won't run on Android 10
     */
    @Deprecated
    public void addImageToGalleryByScanner(Context ctx, File filepath) {

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
    public File saveImageToPublicStorage(Bitmap bm, Bitmap.CompressFormat compressFormat, String imgName) throws IOException {
        File root = Environment.getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        return saveImageToStorage(root, bm, compressFormat, imgName);
    }

    /**
     * this method won't run on Android 10
     */
    public File saveImageToStorage(File root, Bitmap bm, Bitmap.CompressFormat compressFormat, String imgName) throws IOException {
        File imgFile = new File(root, imgName + "." + compressFormat.name());
        if (!imgFile.createNewFile()) {
            throw new IOException("couldn't create the file");
        }

        FileOutputStream out = new FileOutputStream(imgFile);
        saveImageToStorage(bm, compressFormat, out);

        return imgFile;
    }

    public boolean saveImageToStorage(Bitmap image, Bitmap.CompressFormat compressFormat, OutputStream outputStream) throws IOException {
        try {
            boolean b = image.compress(compressFormat, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            return b;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    //------------------------------------------------------------------------------------------

    public Uri saveImageUsingFileProvide(Context context, Bitmap image, Bitmap.CompressFormat compressFormat) {
        return saveImageUsingFileProvide(context, image, compressFormat, (String) null);
    }

    public Uri saveImageUsingFileProvide(Context context, Bitmap image, Bitmap.CompressFormat compressFormat, @Nullable String fileName) {
        try {
            File imgFile;
            if (TextUtils.isEmpty(fileName))
                imgFile = createImageFileUsingFileProvider(context, compressFormat.name());
            else
                imgFile = createImageFileUsingFileProvider(context, fileName, compressFormat.name());

            FileOutputStream fos = new FileOutputStream(imgFile);
            boolean b = saveImageToStorage(image, compressFormat, fos);
            if (!b) {
                imgFile.delete();
            }

            return FileUtils.createInstance().createUriForFileUsingFileProvider(context, imgFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Uri saveImageUsingFileProvide(Context context, Bitmap image, Bitmap.CompressFormat compressFormat, File root) {
        return saveImageUsingFileProvide(context, image, compressFormat, root, null);
    }

    public Uri saveImageUsingFileProvide(Context context, Bitmap image, Bitmap.CompressFormat compressFormat, File root, @Nullable String fileName) {
        try {
            File imgFile;
            if (TextUtils.isEmpty(fileName))
                imgFile = createImageFileUsingFileProvider(root, compressFormat.name());
            else
                imgFile = createImageFileUsingFileProvider(root, fileName, compressFormat.name());

            FileOutputStream fos = new FileOutputStream(imgFile);
            boolean b = saveImageToStorage(image, compressFormat, fos);
            if (!b) {
                imgFile.delete();
            }

            return FileUtils.createInstance().createUriForFileUsingFileProvider(context, imgFile);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //========

    /**
     * save into ExternalFilesDir -> Pictures
     * check {@link R.xml#file_paths}
     */
    public File createImageFileUsingFileProvider(Context context, String imageFileExtension) throws IOException {
        File root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return createImageFileUsingFileProvider(root, imageFileExtension);
    }

    /**
     * save into ExternalFilesDir -> Pictures
     * check {@link R.xml#file_paths}
     */
    public File createImageFileUsingFileProvider(Context context, String fileName, String imageFileExtension) throws IOException {
        File root = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return createImageFileUsingFileProvider(root, fileName, imageFileExtension);
    }

    //========

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public File createImageFileUsingFileProvider(File root, String imageFileExtension) throws IOException {
        File file = FileUtils.createInstance().createFileUsingFileProvider(root, imageFileExtension);
        return file;
    }

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public File createImageFileUsingFileProvider(File root, String fileName, String imageFileExtension) throws IOException {
        File file = FileUtils.createInstance().createFileUsingFileProvider(root, fileName, imageFileExtension);
        return file;
    }

    //========

    /**
     * save into ExternalFilesDir -> Pictures
     * check {@link R.xml#file_paths}
     */
    public Uri createImageFileUsingFileProvider2(Context context, String imageFileExtension) throws IOException {
        File file = createImageFileUsingFileProvider(context, imageFileExtension);
        return FileUtils.createInstance().createUriForFileUsingFileProvider(context, file);
    }

    /**
     * save into ExternalFilesDir -> Pictures
     * check {@link R.xml#file_paths}
     */
    public Uri createImageFileUsingFileProvider2(Context context, String fileName, String imageFileExtension) throws IOException {
        File file = createImageFileUsingFileProvider(context, fileName, imageFileExtension);
        return FileUtils.createInstance().createUriForFileUsingFileProvider(context, file);
    }

    //========

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public Uri createImageFileUsingFileProvider2(Context context, File root, String imageFileExtension) throws IOException {
        File file = createImageFileUsingFileProvider(root, imageFileExtension);
        return FileUtils.createInstance().createUriForFileUsingFileProvider(context, file);
    }

    /**
     * @param root check {@link R.xml#file_paths}
     */
    public Uri createImageFileUsingFileProvider2(Context context, File root, String fileName, String imageFileExtension) throws IOException {
        File file = createImageFileUsingFileProvider(root, fileName, imageFileExtension);
        return FileUtils.createInstance().createUriForFileUsingFileProvider(context, file);
    }

    //------------------------------------------------------------------------------------------

    public static class SaveBitmapToDevice extends AsyncTask<Bitmap, Void, String> {
        ContentResolver contentResolver;
        String title, description;
        Bitmap.CompressFormat imageCompressFormat;
        boolean savedOnSD;

        public SaveBitmapToDevice(ContentResolver contentResolver, String title, String description, Bitmap.CompressFormat imageCompressFormat) {
            this.contentResolver = contentResolver;
            this.title = title;
            this.description = description;
            this.imageCompressFormat = imageCompressFormat;
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
                        source.compress(imageCompressFormat, 50, imageOut);
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
                thumb.compress(imageCompressFormat, 100, thumbOut);
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
                src.compress(imageCompressFormat, 100, imageOut);
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

    public ScreenshotHelper screenshotHelper() {
        return new ScreenshotHelper();
    }

    public static class ScreenshotHelper {

        public Bitmap takeScreenshotOfView(View view, OutputStream outputStream) {
            try {
                Bitmap bitmap = Bitmap.createBitmap(
                        view.getWidth(),
                        view.getHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(bitmap);
                view.draw(canvas);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                }

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public Bitmap takeScreenshotOfEntireScreen(Window window, OutputStream outputStream) {
            try {
                View v1 = window.getDecorView().getRootView();

                v1.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                v1.setDrawingCacheEnabled(false);

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                }

                return bitmap;
            } catch (Throwable e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    //------------------------------------------------------------------------------------------

    public Bitmap drawImageIntoCircle(Bitmap image, int dstWidth, int dstHeight) {
        Bitmap b = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.ARGB_8888);

        Path path = new Path();
        path.addCircle(dstWidth / 2f, dstHeight / 2f, dstWidth / 2f, Path.Direction.CW);

        Canvas canvas = new Canvas(b);
        canvas.clipPath(path);
        canvas.drawBitmap(
                image,
                null,
                new RectF(0f, 0f, dstWidth, dstHeight),
                null
        );

        return b;
    }
}
