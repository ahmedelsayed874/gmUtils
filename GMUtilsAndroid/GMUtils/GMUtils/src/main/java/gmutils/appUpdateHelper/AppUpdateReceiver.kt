package gmutils.appUpdateHelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import gmutils.listeners.ActionCallback2
import gmutils.listeners.ResultCallback
import gmutils.listeners.ResultCallback2
import gmutils.logger.Logger

class AppUpdateReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_INSTALL_COMPLETE = "ACTION_INSTALL_COMPLETE"

        var onAppPendingUserAction: ActionCallback2<Intent, Intent, Boolean>? = null
        var onAppUpdated: ResultCallback<Intent>? = null
        var onAppUpdateError: ResultCallback2<Intent, String>? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, -1)
        when (status) {
            PackageInstaller.STATUS_PENDING_USER_ACTION -> {
                // If Android determines user confirmation is needed, launch the confirmation prompt
                val confirmationIntent = intent.getParcelableExtra<Intent>(Intent.EXTRA_INTENT)

                if (onAppPendingUserAction == null) {
                    Logger.d().printMethod {
                        "🔴onAppPendingUserAction is NULL"
                    }
                }

                val handled = onAppPendingUserAction?.invoke(intent, confirmationIntent)
                if (handled != true) {
                    confirmationIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(confirmationIntent)
                }
            }

            PackageInstaller.STATUS_SUCCESS -> {
                // Update succeeded; app will be replaced by new version
                if (onAppUpdated == null) {
                    Logger.d().printMethod {
                        "🔴onAppUpdated is NULL"
                    }
                }

                onAppUpdated?.invoke(intent)
            }

            else -> {
                val errorMsg = intent.getStringExtra(PackageInstaller.EXTRA_STATUS_MESSAGE)
                // Handle installation error log

                if (onAppUpdateError == null) {
                    Logger.d().printMethod {
                        "🔴onAppUpdateError is NULL"
                    }
                }
                onAppUpdateError?.invoke(intent, errorMsg)
            }
        }
        dispose()
    }

    private fun dispose() {
        onAppPendingUserAction = null
        onAppUpdated = null
        onAppUpdateError = null
    }
}