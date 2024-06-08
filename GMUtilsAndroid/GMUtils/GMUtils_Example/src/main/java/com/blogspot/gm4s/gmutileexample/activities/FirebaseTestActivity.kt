package com.blogspot.gm4s.gmutileexample.activities

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.blogspot.gm4s.gmutileexample.R
import com.blogspot.gm4s.gmutileexample.databinding.ActivityMainBinding
import com.google.firebase.messaging.RemoteMessage
//import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import gmutils.DateOp
import gmutils.collections.ListWrapper
import gmutils.collections.MapWrapper
import gmutils.firebase.auth.FirebaseAuthManager
import gmutils.firebase.configs.FBConfigSet
import gmutils.firebase.configs.FirebaseConfigs
import gmutils.firebase.database.FBFilterOption
import gmutils.firebase.database.FBFilterTypes
import gmutils.firebase.database.FirebaseDatabaseOp
import gmutils.firebase.fcm.FCM
import gmutils.firebase.fcm.FcmMessageHandler
import gmutils.firebase.fcm.FcmNotificationProperties
import gmutils.firebase.fcm.SendFcmMessageParameters
import gmutils.firebase.storage.FirebaseStorage
import gmutils.json.JsonBuilder
import gmutils.logger.LoggerAbs
import gmutils.ui.activities.BaseActivity
import gmutils.ui.dialogs.InputDialog
import gmutils.ui.toast.MyToast
import gmutils.ui.utils.ViewSource
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import kotlin.concurrent.thread

class FirebaseTestActivity : BaseActivity() {
    override fun getViewSource(inflater: LayoutInflater) =
        ViewSource.ViewBinding(ActivityMainBinding.inflate(inflater))

    private val view: ActivityMainBinding get() = viewBinding as ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.view.btn1.text = "Add New Sub Data"
        this.view.btn1.setOnClickListener {
            val id = DateOp.getInstance().formatDate("yyMMdd-HHmmss", true)

            log(this.view.btn1.text.toString(), "starting....id=$id")

            FirebaseDatabaseOp("data")
                .saveData(AnyData.newInstance(id), id) {
                    log(this.view.btn1.text.toString(), "completed ... RESPONSE:: $it")
                }

        }

        this.view.btn2.text = "Add New Multiple Sub Data"
        this.view.btn2.setOnClickListener {
            log(this.view.btn2.text.toString(), "starting....")

            val map = MapWrapper.create(mutableMapOf<String, AnyData>())
                .add(
                    ListWrapper.create(mutableListOf<String>())
                        .add(5) {
                            val t = DateOp.getInstance().formatDate("yyMMdd-HHmmss", true)
                            "$t-$it"
                        }
                        .list
                ) { AnyData.newInstance(it) }
                .map

            FirebaseDatabaseOp("data")
                .saveMultipleData(map) {
                    log(this.view.btn2.text.toString(), "completed ... RESPONSE:: $it")
                }

        }

        this.view.btn3.text = "Retrieve All (without conditions)"
        this.view.btn3.setOnClickListener {
            log(this.view.btn3.text.toString(), "starting....")

            FirebaseDatabaseOp("data")
                .retrieveAll(
                    null,
                    AnyData::class.java,
                    null
                ) {
                    log(
                        this.view.btn3.text.toString(), "completed ... " +
                                "\nRESPONSE:: " +
                                "\n\terror: ${it.error}" +
                                "\n\tdata: ${it.data.size}-items --- \n${it.data.joinToString { "$it\n" }}"
                    )
                }

        }

        this.view.btn4.text = "Retrieve All (with filter)"
        this.view.btn4.setOnClickListener {
            DateOp.getInstance().showDateThenTimePickerDialog(this) {
                val node = it.formatDate("yyMMdd-HHmmss", true)

                log(this.view.btn4.text.toString(), "($node):: starting....")

                FirebaseDatabaseOp("data")
                    .retrieveAll(
                        FBFilterOption(
                            FBFilterTypes.GreaterThanOrEqual,
                            "id",
                            node
                        ),
                        AnyData::class.java,
                        null
                    ) {
                        log(
                            this.view.btn4.text.toString(), "completed ... " +
                                    "RESPONSE:: ${it.data.size}-items --- \n$it"
                        )
                    }

            }
        }

        this.view.btn5.text = "Retrieve All (with custom converter)"
        this.view.btn5.setOnClickListener {
            log(this.view.btn5.text.toString(), "starting....")

            FirebaseDatabaseOp("data")
                .retrieveAll(
                    null,
                    AnyData::class.java,
                    {
                        log(this.view.btn5.text.toString(), "Custom Converter Received:: $it")
                        listOf<AnyData>()
                    }
                ) {
                    log(
                        this.view.btn5.text.toString(), "completed ... " +
                                "RESPONSE:: ${it.data.size}-items --- \n$it"
                    )
                }

        }

        this.view.btn6.text = "Retrieve Single (data)"
        this.view.btn6.setOnClickListener {
            InputDialog.create(this)
                .setMessage("Enter the value")
                .setInputHint("value")
                .setPositiveButtonCallback {
                    val node = it[0]

                    log(this.view.btn6.text.toString(), "($node):: starting....")

                    FirebaseDatabaseOp("data")
                        .retrieveSingle(
                            node,
                            AnyData::class.java,
                            null
                        ) {
                            log(this.view.btn6.text.toString(), "completed ... RESPONSE:: $it")
                        }

                    null
                }
                .show()
        }

        this.view.btn7.text = "Retrieve Single (data) (with custom converter)"
        this.view.btn7.setOnClickListener {
            InputDialog.create(this)
                .setMessage("Enter the value")
                .setInputHint("value")
                .setPositiveButtonCallback {
                    val node = it[0]

                    log(this.view.btn7.text.toString(), "($node):: starting....")

                    FirebaseDatabaseOp("data")
                        .retrieveSingle(
                            node,
                            AnyData::class.java,
                            {
                                log(
                                    this.view.btn7.text.toString(),
                                    "Custom Converter Received:: $it"
                                )
                                null
                            }
                        ) {
                            log(this.view.btn7.text.toString(), "completed ... RESPONSE:: $it")
                        }

                    null
                }
                .show()
        }

        /////////////////////////////////////////////////////////////////////////

        this.view.btn0708sep.visibility = View.VISIBLE //......................

        this.view.btn8.text = "Add New Data Directly"
        this.view.btn8.setOnClickListener {
            val id = DateOp.getInstance().formatDate("yyMMdd-HHmmss", true)

            log(this.view.btn8.text.toString(), "starting....")

            FirebaseDatabaseOp("data2")
                .saveData(AnyData.newInstance(id), null) {
                    log(this.view.btn8.text.toString(), "completed ... RESPONSE:: $it")
                }

        }

        this.view.btn9.text = "Retrieve Direct Data (All)"
        this.view.btn9.setOnClickListener {
            log(this.view.btn9.text.toString(), "starting....")

            FirebaseDatabaseOp("data2")
                .retrieveAll(null, AnyData::class.java, null) {
                    log(this.view.btn9.text.toString(), "completed ... RESPONSE:: $it")
                }

        }

        this.view.btn10.text = "Retrieve Direct Data (Single)"
        this.view.btn10.setOnClickListener {
            log(this.view.btn10.text.toString(), "starting....")

            FirebaseDatabaseOp("data2")
                .retrieveSingle(null as String?, AnyData::class.java, null) {
                    log(this.view.btn10.text.toString(), "completed ... RESPONSE:: $it")
                }

        }

        /////////////////////////////////////////////////////////////////////////

        this.view.btn1011sep.visibility = View.VISIBLE //......................

        this.view.btn11.text = "Listen2Changes of (data)"
        this.view.btn11.setOnClickListener {
            log(this.view.btn11.text.toString(), "starting....")

            FirebaseDatabaseOp("data")
                .listenToChanges(
                    null as String?,
                    AnyData::class.java,
                    null,
                    {
                        log(this.view.btn11.text.toString(), "RESPONSE:: $it")
                    },
                    {
                        log(this.view.btn11.text.toString(), "ERROR:: $it")
                    }
                )
        }

        this.view.btn12.text = "Listen2Changes of SUB OF (data)"
        this.view.btn12.setOnClickListener {
            DateOp.getInstance().showDateThenTimePickerDialog(this) {
                val node = it.formatDate("yyMMdd-HHmmss", true)

                log(this.view.btn12.text.toString(), "($node):: starting....")

                FirebaseDatabaseOp("data")
                    .listenToChanges(
                        node,
                        AnyData::class.java,
                        FBFilterOption(FBFilterTypes.GreaterThanOrEqual, "id", node),
                        {
                            log(this.view.btn12.text.toString(), "RESPONSE:: $it")
                        },
                        {
                            log(this.view.btn12.text.toString(), "ERROR:: $it")
                        }
                    )

            }
        }

        /////////////////////////////////////////////////////////////////////////

        this.view.btn1213sep.visibility = View.VISIBLE //......................

        this.view.btn13.text = "Listen2Changes of (data2)"
        this.view.btn13.setOnClickListener {
            log(this.view.btn13.text.toString(), "starting....")

            FirebaseDatabaseOp("data2")
                .listenToChanges(
                    null as String?,
                    AnyData::class.java,
                    null,
                    {
                        log(this.view.btn13.text.toString(), "RESPONSE:: $it")
                    },
                    {
                        log(this.view.btn13.text.toString(), "ERROR:: $it")
                    }
                )
        }

        /////////////////////////////////////////////////////////////////////////

        this.view.btn1314sep.visibility = View.VISIBLE //......................

        this.view.btn14.text = "Send FCM message"
        /*this.view.btn14.setOnClickListener {
            log(this.view.btn14.text.toString(), "starting....")

            FirebaseConfigs.createInstance(listOf(
                object : FBConfigSet() {
                    val keyName = "fcm_message_key"

                    override fun getDefaults() = mutableMapOf(keyName to "")

                    override fun onFetchCompleteAbs(
                        firebaseRemoteConfig: FirebaseRemoteConfig?,
                        success: Boolean
                    ) {
                        val key = firebaseRemoteConfig?.getString(keyName)

                        log(
                            this@FirebaseTestActivity.view.btn14.text.toString(),
                            "remote configurations fetched ($success)...." +
                                    "MessageKey: $key"
                        )

                        if (success) {
                            FCM.instance()
                                .setLogger(LoggerImpl())
                                .init(
                                    FcmMessageHandlerImpl::class.java,
                                    FcmMessageHandlerImpl().also {
                                        it.onMessageReceived = {
                                            log(
                                                this@FirebaseTestActivity.view.btn14.text.toString(),
                                                "FCM-MESSAGE-RECEIVED:: $it"
                                            )
                                        }
                                    },
                                    {
                                        log(
                                            this@FirebaseTestActivity.view.btn14.text.toString(),
                                            "FCM-TOKEN:: $it"
                                        )
                                    },
                                    key
                                )
                                .subscribeToTopics(mutableListOf("test")) {
                                    log(
                                        this@FirebaseTestActivity.view.btn14.text.toString(),
                                        "TOPIC-SUBSCRIPTION:: $it"
                                    )

                                    FCM.instance().sendMessageToTopic(
                                        "test",
                                        "Title: test",
                                        "Message: test",
                                        false,
                                        JsonBuilder.ofJsonObject().addString("data", "test").json as JSONObject,
                                        null, //"default",
                                        null,
                                    ) {suc, err ->
                                        log(
                                            this@FirebaseTestActivity.view.btn14.text.toString(),
                                            "NOTIFICATION-SENT:: $it [ERR: $err]"
                                        )
                                    }
                                }
                        }
                    }

                }
            )).fetch();
        }*/
        this.view.btn14.setOnClickListener {
            log(this.view.btn14.text.toString(), "starting....")
            FcmOp(
                loggerAbs = LoggerImpl(),
                filesDir = filesDir
            ) {
                log(this.view.btn14.text.toString(), it)
            }.sendMsg(
                this
            )
        }

        //

        //------------------------------------------------------------------------------------------

        this.view.logTv.viewTreeObserver.addOnGlobalLayoutListener {
            this.view.logSection.scrollTo(0, this.view.logTv.height - 1)
        }

    }

    fun log(tag: String, text: String?) {
        this.view.logTv.append(
            "" +
                    "<${DateOp.getInstance().formatDate("yyMMdd-HHmmss", true)}>\n" +
                    "$tag: $text" +
                    "\n------------------------------\n"
        )
    }

    fun onShowOrHideLogClick(view: View) {
        if (this.view.logSection.visibility == View.GONE) {
            this.view.logSection.visibility = View.VISIBLE
            (view as TextView).text = "Hide Log"

        } else {
            this.view.logSection.visibility = View.GONE
            (view as TextView).text = "Show Log"
        }
    }

    fun clearLog(v: View) {
        this.view.logTv.text = ""
    }

    //================================================

    class FcmOp(val loggerAbs: LoggerAbs, val filesDir: File, val log: (String) -> Unit) {

        fun sendMsg(context: Context) {
            step1Auth(context) {
                step2DownloadFile {
                    step3InitFcm(it) {topic, s ->
                        step4SendMsg(topic)
                    }
                }
            }
        }

        private fun step1Auth(context: Context, callback: () -> Unit) {
            log("authenticating ....")

            val user = FirebaseAuthManager().currentUser()
            if (user != null) {
                callback()
            } else {
                InputDialog.create(context)
                    .setMessage("enter user name & pw")
                    .addInputField {
                        it.setTitle("User name:")
                    }
                    .addInputField {
                        it.setTitle("pw:")
                    }
                    .setPositiveButtonCallback {
                        FirebaseAuthManager().loginByEmail(
                            it[0],
                            it[1]
                        ) {
                            log("authenticating completed: $it")

                            if (it.data != null) {
                                callback()
                            } else {
                                log("auth failed")
                            }
                        }
                        null
                    }
                    .show()
            }
        }

        private fun step2DownloadFile(callback: (File) -> Unit) {
            log("serviceAccountFileName downloading.....")

            val serviceAccountFileName = "gmutils-4a29b-firebase-adminsdk-m28u7-24ba6aaffd.json"

            /*FirebaseStorage().getDownloadURL(
                serviceAccountFileName
            ) {
                if (!TextUtils.isEmpty(it.data)) {
                    log(
                        "serviceAccountFileName download url: ${it.data}"
                    )

                    val file = File(filesDir, serviceAccountFileName)

                    if (file.exists()) {
                        callback(file)

                    } else {
                        file.createNewFile()

                        gmutils.net.SimpleHTTPRequest.downloadFile(
                            it.data,
                            file
                        ) { request, response ->
                            log("serviceAccountFileName download response: $response")

                            if (!response.hasError()) {
                                callback(response.file)
                            } else {
                                var error = response.error

                                if (TextUtils.isEmpty(error)) error += ""
                                else error += "\n"

                                if (response.exception != null) {
                                    error += response.exception.message
                                }

                            }
                        }
                    }
                } else {
                    log("Error: ${it.error.default}")
                }
            }*/

            val file = File(filesDir, serviceAccountFileName)

            if (file.exists()) {
                callback(file)

            } else {
                FirebaseStorage().download(
                    serviceAccountFileName,
                    filesDir,
                    file
                ) {response ->
                    log("serviceAccountFileName download response: $response")

                    if (response.data != null) {
                        callback(response.data)
                    }
                }
            }
        }

        private fun step3InitFcm(file: File, callback: (String, Boolean) -> Unit) {
            log("setup fcm started.....")

            FCM.instance()
                .setLogger(loggerAbs)
                .init(
                    FcmMessageHandlerImpl::class.java,
                    FcmMessageHandlerImpl().also {
                        it.onMessageReceived = {
                            log("FCM-MESSAGE-RECEIVED:: $it")
                        }
                    },
                    {
                        log("FCM-TOKEN:: $it")
                    },
                    SendFcmMessageParameters(
                        "gmutils-4a29b",
                        SendFcmMessageParameters.FileSource.Storage,
                        file.path
                    )
                )
                .subscribeToTopics(mutableListOf("test")) {
                    log("TOPIC-SUBSCRIPTION:: $it \nsending message to topic \"test\"")
                    callback("test", it["test"] == true)
                }
        }

        private fun step4SendMsg(topic: String) {
            log("sending msg.....")

            FCM.instance().sendMessageToTopic(
                topic,
                "Title: $topic",
                "Message: $topic",
                false,
                JsonBuilder
                    .ofJsonObject()
                    .addString("data", "test").json as JSONObject,
                null, //"default",
                null,
            ) {
                log("NOTIFICATION-SENT:: response: $it")
            }
        }

    }

    inner class LoggerImpl : LoggerAbs(FirebaseTestActivity::class.java.simpleName) {
        init {
            val dl = DateOp.getInstance().increaseDays(10)
            logConfigs.setLogDeadline(dl)
            logConfigs.setWriteLogsToFileDeadline(dl)
        }

        override fun writeToLog(tag: String?, msg: String?) {
            runOnUiThread {
                log(tag ?: "", msg)
            }
        }
    }

}

data class AnyData(
    val id: String,
    val text: String,
    val intNum: Int,
    val longNum: Long,
    val doubleNum: Double,
    val floatNum: Float,
    val boolValue: Boolean,
    val listOfString: List<String>,
    val listOfNum: List<Int>,
    val mapOfString: Map<String, String>
) {
    companion object {
        fun newInstance(id: String) = AnyData(
            id = id,
            text = DateOp.getInstance().toString(),
            intNum = Int.MAX_VALUE,
            longNum = Long.MAX_VALUE,
            doubleNum = Double.MAX_VALUE,
            floatNum = Float.MAX_VALUE,
            boolValue = id.hashCode() % 2 == 0,
            listOfString = listOf(id, "#$id#", "**$id**"),
            listOfNum = listOf(1, 2, 4, 5, 6, 7, 8),
            mapOfString = mapOf(
                "+0" to "0",
                "+1" to "1",
                "+2" to "2",
            )
        )
    }

    constructor() : this(
        "",
        "",
        0,
        0,
        0.0,
        0f,
        false,
        emptyList(),
        emptyList(),
        mapOf()
    )

    override fun toString(): String {
        return "AnyData(" +
                "\n" +
                "\tid='$id', " +
                "\n" +
                "\ttext='$text', " +
                "\n" +
                "\tintNum=$intNum, " +
                "\n" +
                "\tlongNum=$longNum, " +
                "\n" +
                "\tdoubleNum=$doubleNum, " +
                "\n" +
                "\tfloatNum=$floatNum, " +
                "\n" +
                "\tboolValue=$boolValue, " +
                "\n" +
                "\tlistOfString=$listOfString, " +
                "\n" +
                "\tlistOfNum=$listOfNum, \n" +
                "\tmapOfString=$mapOfString\n" +
                ")"
    }


}

class FcmMessageHandlerImpl : FcmMessageHandler {
    var onMessageReceived: ((RemoteMessage) -> Unit)? = null

    override fun onMessageReceived(
        context: Context,
        message: RemoteMessage
    ): FcmNotificationProperties {
        onMessageReceived?.invoke(message)
        return FcmNotificationProperties(
            R.mipmap.ic_launcher,
            R.color.gmAccent
        )
    }

}