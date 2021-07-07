package gmutils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import gmutils.images.ImageUtils;
import gmutils.storage.SettingsStorage;
import gmutils.utils.Utils;

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

    private final ImageIntents mImageIntents = new ImageIntents();

    public ImageIntents getImageIntents() {
        return mImageIntents;
    }

    public static class ImageIntents {

        private Uri createUriForFile(Context context) {
            ImageUtils imageUtils = ImageUtils.createInstance();
            try {
                return imageUtils.createImageFileUsingFileProvider2(context);
            } catch (IOException e) {
                return null;
            }
        }

        //------------------------------------------------------------------------------------------

        public boolean checkPermissionForCamera(Activity activity, int requestCode) {
            String[] neededPermissions = {
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            List<String> nonPermittedPermissions = new ArrayList<>();

            for (String neededPermission : neededPermissions) {
                boolean isPermitted = ActivityCompat.checkSelfPermission(
                        activity,
                        neededPermission
                ) == PackageManager.PERMISSION_GRANTED;

                if (!isPermitted) {
                    nonPermittedPermissions.add(neededPermission);
                }
            }

            if (nonPermittedPermissions.size() > 0) {
                ActivityCompat.requestPermissions(
                        activity,
                        nonPermittedPermissions.toArray(new String[0]),
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
                try {
                    Uri photoURI = createUriForFile(activity);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    activity.startActivityForResult(takePictureIntent, requestCode);
                    return photoURI;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        //@RequiresPermission(allOf = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})
        public Uri takePicture(Fragment fragment, int requestCode) {
            if (!checkPermissionForCamera(fragment.getActivity(), requestCode)) return null;

            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
                try {
                    Uri photoURI = createUriForFile(fragment.getContext());
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    fragment.startActivityForResult(takePictureIntent, requestCode);

                    return photoURI;
                } catch (Exception ex) {
                    ex.printStackTrace();
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
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                showImage(imageView.getContext(), ((BitmapDrawable) drawable).getBitmap());
            }
        }

        public boolean showImage(Context context, Bitmap image) {
            ImageUtils imageUtils = ImageUtils.createInstance();
            Uri imgUri = imageUtils.saveImageUsingFileProvide(context, image);

            if (imgUri != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(imgUri, "image/*");
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
            Bitmap bitmap = ImageUtils.createInstance().getBitmap(imageView);
            if (bitmap != null) {
                shareImage(imageView.getContext(), bitmap, text);
            }
        }

        public void shareImage(Context context, Bitmap image, String text) {
            ImageUtils imageUtils = ImageUtils.createInstance();
            Uri imgUri = imageUtils.saveImageUsingFileProvide(context, image);

            if (imgUri != null) {
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
                boolean en = SettingsStorage.Language.usingEnglish();
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
