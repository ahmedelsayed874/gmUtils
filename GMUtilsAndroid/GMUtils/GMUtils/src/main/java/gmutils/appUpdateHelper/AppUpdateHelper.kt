package gmutils.appUpdateHelper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import gmutils.listeners.ActionCallback2
import gmutils.listeners.ResultCallback
import gmutils.listeners.ResultCallback2
import gmutils.logger.Logger
import java.io.File

class AppUpdateHelper {
    fun checkInstallPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
                ).apply {
                    data = Uri.parse("package:${context.packageName}")
                }

                context.startActivity(intent)

                return true
            }
        }

        return false
    }

    //==============================================================================================

    /**
     * Android 12+: Use PackageInstaller for unattended background update
     * Android 12+ (API 31+): Supports unattended/silent self-updates without forcing the user to
     * tap an "Install" dialog, provided the app updates itself and the user has granted
     * the "Install unknown apps" permission once.
     */
    fun installUnattended(
        context: Context,
        apkFile: File,
        onAppPendingUserAction: ActionCallback2<Intent, Intent, Boolean>? = null,
        onAppUpdated: ResultCallback<Intent>? = null,
        onAppUpdateError: ResultCallback2<Intent, String>? = null
    ) {
        val packageInstaller = context.packageManager.packageInstaller
        val params = PackageInstaller.SessionParams(
            PackageInstaller.SessionParams.MODE_FULL_INSTALL
        ).apply {
            setAppPackageName(context.packageName)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                setRequireUserAction(PackageInstaller.SessionParams.USER_ACTION_NOT_REQUIRED)
            }
        }

        val sessionId = packageInstaller.createSession(params)
        val session = packageInstaller.openSession(sessionId)

        apkFile.inputStream().use { input ->
            session.openWrite(
                "apk_update_stream",
                0,
                apkFile.length()
            ).use { output ->
                input.copyTo(output)
                session.fsync(output)
            }
        }

        AppUpdateReceiver.onAppPendingUserAction = onAppPendingUserAction
        AppUpdateReceiver.onAppUpdated = onAppUpdated
        AppUpdateReceiver.onAppUpdateError = onAppUpdateError

        val intent = Intent(
            context,
            AppUpdateReceiver::class.java
        ).apply {
            action = AppUpdateReceiver.ACTION_INSTALL_COMPLETE
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            sessionId,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        session.commit(pendingIntent.intentSender)
        session.close()
    }

    /**
     * Android 11 and lower: Launch system installer Intent
     * Android 11 & Below: Security restrictions prevent non-system apps from updating silently without root.
     * The app can download the APK automatically, but it must launch the system installer screen
     * where the user taps Update once.
     */
    fun installLegacy(context: Context, apkFile: File) {
        Logger.d().printMethod {
            "Make sure to add the next permission to AndroidManifest.xml:\n" +
                    "\t<uses-permission android:name=\"android.permission.REQUEST_INSTALL_PACKAGES\" />" +
                    "\n\n" +
                    "Also add:\n" +
                    "<provider\n" +
                    "            android:name=\"androidx.core.content.FileProvider\"\n" +
                    "            android:authorities=\"\${applicationId}.fileprovider\"\n" +
                    "            android:exported=\"false\"\n" +
                    "            android:grantUriPermissions=\"true\">\n" +
                    "            <meta-data\n" +
                    "                android:name=\"android.support.FILE_PROVIDER_PATHS\"\n" +
                    "                android:resource=\"@xml/file_paths\" />\n" +
                    "</provider>" +
                    "\n\n" +
                    "Also add the next line to file_paths.xml:\n" +
                    "<external-files-path name=\"downloaded_apks\" path=\"Download\" />"
        }

        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }

        context.startActivity(intent)
    }
}
