package com.blogspot.gm4s1.gmutils;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.blogspot.gm4s1.gmutils.preferences.SettingsPreferences;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
public class Intents {

    public static Intents getInstance() {
        return new Intents();
    }

    //----------------------------------------------------------------------------------------------

    public void launchHomeScreen(Context context) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startMain);
    }

    public void openWebPage(Context context, String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public void openPlayStorePage(Context context, String packageName) {
        openWebPage(context, "https://play.google.com/store/apps/details?id=" + packageName);
    }

    public void launchOtherApp(Context context, String packageName) {
        launchOtherApp(context, packageName, false);
    }

    public void launchOtherApp(Context context, String packageName, boolean finishCurrent) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        if (intent != null) {
            if (finishCurrent) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else {
            openPlayStorePage(context, packageName);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void showMap(Context context, String lat, String lng, String label) {
        if (TextUtils.isEmpty(lat) || TextUtils.isEmpty(lng)) return;
        try {
            label = URLEncoder.encode(label, "utf8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        String uriString = String.format("geo:0,0?q=%s,%s(%s)", lat, lng, label);
        Uri geoLocation = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public void addEventToCalendar(Context context, String title, String description, String location, long begin, long end) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.DESCRIPTION, description)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin)
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end)
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.Events.HAS_ALARM, true);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    //----------------------------------------------------------------------------------------------

    private ImageIntents mImageIntents = new ImageIntents();

    public ImageIntents getImageIntents() {
        return mImageIntents;
    }

    public static class ImageIntents {
        private File createImageFile(Context context, boolean inCache) throws IOException {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = inCache ? context.getCacheDir() : context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".png",   /* suffix */
                    storageDir      /* directory */
            );

            return image;
        }

        /**
         * must add in manifest
         * <provider
         *          android:name="androidx.core.content.FileProvider"
         *          android:authorities="APP_PACKAGE_NAME.fileprovider"
         *          android:exported="false"
         *          android:grantUriPermissions="true">
         *          <meta-data
         *              android:name="android.support.FILE_PROVIDER_PATHS"
         *              android:resource="@xml/file_paths" />
         * </provider>
         *
         * ------------------------------------------------------------------
         * add this this text to xml/file_paths
         *
         * <?xml version="1.0" encoding="utf-8"?>
         * <paths xmlns:android="http://schemas.android.com/apk/res/android">
         *     <files-path name="my_images" path="Pictures/" />
         *     <external-files-path name="my_images" path="Pictures/" />
         * </paths>
         *
         * @param context
         * @param file
         * @return
         */
        private Uri createFileUri(Context context, File file, boolean useAppPackage) {
            if (!useAppPackage) {
                try {
                    ComponentName cm = new ComponentName(context, "androidx.core.content.FileProvider");
                    ProviderInfo providerInfo = context.getPackageManager().getProviderInfo(cm, 0);
                    if (providerInfo.authority.contains(context.getPackageName()))
                        throw new Exception();
                } catch (Exception e) {//(PackageManager.NameNotFoundException e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("please create a file in 'res/xml' path with a name of 'file_paths' or whatever you want");
                    sb.append("\n");
                    sb.append("this file will contain the following:");
                    sb.append("\n");
                    sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
                    sb.append("<paths xmlns:android=\"http://schemas.android.com/apk/res/android\">");
                    sb.append("\n\t<files-path name=\"my_images\" path=\"Pictures/\" />");
                    sb.append("\n\t<external-files-path name=\"my_images\" path=\"Pictures/\" />");
                    sb.append("\n");
                    sb.append("</paths>\n");
                    sb.append("-----------------------------------------------------");
                    sb.append("\n\n");
                    sb.append("then add The following to your manifest file:");
                    sb.append("\n");
                    sb.append("<provider");
                    sb.append("\n\tandroid:name=\"androidx.core.content.FileProvider\"");
                    sb.append("\n\tandroid:authorities=\"APP_PACKAGE_NAME.fileprovider\"");
                    sb.append("\n\tandroid:exported=\"false\"");
                    sb.append("\n\tandroid:grantUriPermissions=\"true\">");
                    sb.append("\n\t<meta-data");
                    sb.append("\n\t\tandroid:name=\"android.support.FILE_PROVIDER_PATHS\"");
                    sb.append("\n\t\tandroid:resource=\"@xml/file_paths\" />\n");
                    sb.append("</provider>");
                    sb.append("\n");
                    sb.append("\n----------------------------------------------------");
                    sb.append("\n");
                    sb.append("\nmake sure to replace APP_PACKAGE_NAME with your own package name; ex: (com.example)");

                    throw new IllegalArgumentException(sb.toString());
                }
            }

            String authority = context == null ?
                    "com.blogspot.gm4s1.gmutils.fileprovider" :
                    context.getPackageName() + ".fileprovider";

            return FileProvider.getUriForFile(context, authority, file);
        }

        //------------------------------------------------------------------------------------------

        public boolean checkPermissionForCamera(Activity activity, int requestCode) {
            boolean isCameraPermitted = ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED;

            boolean isWriteExternalPermitted = ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED;

            if (!isCameraPermitted || !isWriteExternalPermitted) {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        requestCode);

                return false;
            }

            return true;
        }

        public boolean checkPermissionForGallery(Activity activity, int requestCode) {
            boolean isReadFilesPermitted = ActivityCompat.checkSelfPermission(
                    activity, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED;

            if (!isReadFilesPermitted) {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);

                return false;
            }

            return true;
        }

        //------------------------------------------------------------------------------------------

        //@RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        public Uri takePicture(Activity activity, int requestCode) {
            return takePicture(activity, requestCode, false);
        }

        public Uri takePicture(Activity activity, int requestCode, boolean useAppPackage) {
            if (!checkPermissionForCamera(activity, requestCode)) return null;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile(activity, true);
                } catch (IOException ex) {
                }

                if (photoFile != null) {
                    Uri photoURI = createFileUri(activity, photoFile, useAppPackage);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    activity.startActivityForResult(takePictureIntent, requestCode);

                    return photoURI;
                }
            }

            return null;
        }

        //@RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        public Uri takePicture(Fragment fragment, int requestCode) {
            return takePicture(fragment, requestCode, false);
        }

        public Uri takePicture(Fragment fragment, int requestCode, boolean useAppPackage) {
            if (!checkPermissionForCamera(fragment.getActivity(), requestCode)) return null;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile(fragment.getContext(), true);
                } catch (IOException ex) {
                }

                if (photoFile != null) {
                    Uri photoURI = createFileUri(fragment.getContext(), photoFile, useAppPackage);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    fragment.startActivityForResult(takePictureIntent, requestCode);

                    return photoURI;
                }
            }

            return null;
        }

        //@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        public void pickImage(Activity activity, int requestCode) {
            if (!checkPermissionForGallery(activity, requestCode)) return;
            activity.startActivityForResult(getPickImageIntent(), requestCode);
        }

        //@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
        public void pickImage(Fragment fragment, int requestCode) {
            if (!checkPermissionForGallery(fragment.getActivity(), requestCode)) return;
            fragment.startActivityForResult(getPickImageIntent(), requestCode);
        }

        private Intent getPickImageIntent() {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            return intent;
        }

        //------------------------------------------------------------------------------------------

        public void addPhotoToGallery(Context context, String filePath) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(filePath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        }

        public void showImage(ImageView imageView) {
            showImage(imageView, false);
        }

        public void showImage(ImageView imageView, boolean useAppPackage) {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                showImage(imageView.getContext(), ((BitmapDrawable) drawable).getBitmap(), useAppPackage);
            }
        }

        public boolean showImage(Context context, Bitmap image) {
            return showImage(context, image, false);
        }

        public boolean showImage(Context context, Bitmap image, boolean useAppPackage) {
            File imgFile = null;
            try {
                imgFile = createImageFile(context, true);
            } catch (IOException ex) {
            }

            if (imgFile != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(imgFile);
                    boolean compress = image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    if (!compress) {
                        imgFile.delete();
                        imgFile = null;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    imgFile = null;
                }
            }

            if (imgFile != null) {
                Uri imgUri = createFileUri(context, imgFile, useAppPackage);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(imgUri, "image/*");
                //intent.putExtra("prefix", "jpg");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                    return true;
                }
            }

            return false;
        }

        public void shareImage(ImageView imageView, String text) {
            shareImage(imageView, text, false);
        }

        public void shareImage(ImageView imageView, String text, boolean useAppPackage) {
            Bitmap bitmap = ImageUtils.createInstance().getBitmap(imageView);
            if (bitmap != null) {
                shareImage(imageView.getContext(), bitmap, text, useAppPackage);
            }
        }

        public void shareImage(Context context, Bitmap image, String text) {
            shareImage(context, image, text, false);
        }

        public void shareImage(Context context, Bitmap image, String text, boolean useAppPackage) {
            File imgFile = null;
            try {
                imgFile = createImageFile(context, true);
            } catch (IOException ex) {
            }

            if (imgFile != null) {
                try {
                    FileOutputStream fos = new FileOutputStream(imgFile);
                    boolean compress = image.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    if (!compress) {
                        imgFile.delete();
                        imgFile = null;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    imgFile = null;
                }
            }

            if (imgFile != null) {
                Uri imgUri = createFileUri(context, imgFile, useAppPackage);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);

                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imgUri);
                intent.putExtra(Intent.EXTRA_TEXT, text);

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (intent.resolveActivity(context.getPackageManager()) != null) {

                    context.startActivity(Intent.createChooser(intent, "Share via"));
                }
            }


        }

    }

    //--------------------------------------------------------------------------------------------//

    public void composeEmail(Context context, String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public boolean composeEmail(Context context, String[] addresses, String subject, String body) {
        return composeEmail(context, addresses, subject, body, (Uri) null);
    }

    public boolean composeEmail(Context context, String[] addresses, String subject, String body, Uri attachment) {
        ArrayList<Uri> attachments = null;

        if (attachment != null) {
            attachments = new ArrayList<>();
            attachments.add(attachment);
        }

        return composeEmail(context, addresses, subject, body, attachments);
    }

    public boolean composeEmail(Context context, String[] addresses, String subject, String body, ArrayList<Uri> attachments) {
        Intent intent = null;

        if (attachments == null || attachments.size() == 1) {
            intent = new Intent(Intent.ACTION_SENDTO);
        } else {
            intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        }

        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        if (attachments != null) {
            if (attachments.size() == 1) {
                intent.putExtra(Intent.EXTRA_STREAM, attachments.get(0));
            } else {
                intent.putExtra(Intent.EXTRA_STREAM, attachments);
            }
        }

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            return true;
        } else {
            Toast.makeText(context, "There is no E-mail App on your mobile", Toast.LENGTH_LONG).show();
        }

        return false;
    }

    //----------------------------------------------------------------------------------------------

    public boolean callPhoneNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    @RequiresPermission(Manifest.permission.CALL_PHONE)
    public void callPhoneNumberDirectly(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber.replace("+", "00").replace(" ", "")));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                boolean en = SettingsPreferences.Language.usingEnglish();
                String msg = en ?
                        "You did not give us the permission for call" :
                        "عفوا، لم تعطنا السماحية بإجراء المكالمات الهاتفية";

                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                return;
            }
            context.startActivity(intent);
        }
    }

    //----------------------------------------------------------------------------------------------

    public void openUrl(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public boolean openMapApp(Context context, double lat, double lon, String title) {
        try {
            title = URLEncoder.encode(title, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Uri geoLocation = Uri.parse(String.format("geo:0,0?q=%f,%f(%s)", lat, lon, title));

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
            return true;
        }

        return false;
    }

    public void sendViaTwitter(Context context, String tweetText) {
        // Create intent using ACTION_VIEW and a normal Twitter url:
        String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s",
                Utils.createInstance().urlEncode(tweetText)
        );
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));

        // Narrow down to official Twitter app, if available:
        List<ResolveInfo> matches = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }

        context.startActivity(intent);
    }

    //----------------------------------------------------------------------------------------------

    public void shareAppLink(Context context, String appName, String message) {
        shareAppLink(context, appName, message, "Share with:");
    }

    public void shareAppLink(Context context, String appName, String message, String chooserTitle) {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, appName);

            String sAux = "\n" + message + "\n\n";
            sAux += "https://play.google.com/store/apps/details?id=" + context.getPackageName() + "\n\n";

            i.putExtra(Intent.EXTRA_TEXT, sAux);
            context.startActivity(Intent.createChooser(i, chooserTitle));
        } catch (Exception e) {
            //e.toString();
        }

    }

    public void shareUrl(Context context, String title, String url) {
        shareUrl(context, title, url, "Share link!");
    }

    public void shareUrl(Context context, String title, String url, String chooserTitle) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, title);
        share.putExtra(Intent.EXTRA_TEXT, url);

        context.startActivity(Intent.createChooser(share, chooserTitle));
    }

    public void shareText(Context context, String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(sendIntent, "Share"));
    }

}
