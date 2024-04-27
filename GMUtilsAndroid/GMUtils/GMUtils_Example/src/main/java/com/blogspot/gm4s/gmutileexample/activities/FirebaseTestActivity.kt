package com.blogspot.gm4s.gmutileexample.activities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.blogspot.gm4s.gmutileexample.R
import com.blogspot.gm4s.gmutileexample.databinding.ActivityMainBinding
import com.google.firebase.messaging.RemoteMessage
import gmutils.DateOp
import gmutils.collections.ListWrapper
import gmutils.collections.MapWrapper
import gmutils.firebase.database.FBFilterOption
import gmutils.firebase.database.FBFilterTypes
import gmutils.firebase.database.FirebaseDatabaseOp
import gmutils.firebase.fcm.FCM
import gmutils.firebase.fcm.FcmMessageHandler
import gmutils.firebase.fcm.FcmNotificationProperties
import gmutils.logger.LoggerAbs
import gmutils.ui.activities.BaseActivity
import gmutils.ui.utils.ViewSource

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
                    log(this.view.btn3.text.toString(), "completed ... RESPONSE:: $it")
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
                            node,
                            5
                        ),
                        AnyData::class.java,
                        null
                    ) {
                        log(this.view.btn4.text.toString(), "completed ... RESPONSE:: $it")
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
                    log(this.view.btn5.text.toString(), "completed ... RESPONSE:: $it")
                }

        }

        this.view.btn6.text = "Retrieve Single (data)"
        this.view.btn6.setOnClickListener {
            DateOp.getInstance().showDateThenTimePickerDialog(this) {
                val node = it.formatDate("yyMMdd-HHmmss", true)

                log(this.view.btn6.text.toString(), "($node):: starting....")

                FirebaseDatabaseOp("data")
                    .retrieveSingle(
                        node,
                        AnyData::class.java,
                        null
                    ) {
                        log(this.view.btn6.text.toString(), "completed ... RESPONSE:: $it")
                    }

            }
        }

        this.view.btn7.text = "Retrieve Single (data) (with custom converter)"
        this.view.btn7.setOnClickListener {
            DateOp.getInstance().showDateThenTimePickerDialog(this) {
                val node = it.formatDate("yyMMdd-HHmmss", true)

                log(this.view.btn7.text.toString(), "($node):: starting....")

                FirebaseDatabaseOp("data")
                    .retrieveSingle(
                        node,
                        AnyData::class.java,
                        {
                            log(this.view.btn7.text.toString(), "Custom Converter Received:: $it")
                            null
                        }
                    ) {
                        log(this.view.btn7.text.toString(), "completed ... RESPONSE:: $it")
                    }

            }
        }

        /////////////////////////////////////////////////////////////////////////

        this.view.btn0809sep.visibility = View.VISIBLE //......................

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
        this.view.btn14.setOnClickListener {
            log(this.view.btn14.text.toString(), "starting....")

            FCM.instance()
                .setLogger(LoggerImpl())
                .init(
                    FcmMessageHandlerImpl::class.java,
                    FcmMessageHandlerImpl().also {
                        it.onMessageReceived = {
                            log(this.view.btn14.text.toString(), "FCM-MESSAGE-RECEIVED:: $it")
                        }
                    },
                    {
                        log(this.view.btn14.text.toString(), "FCM-TOKEN:: $it")
                    },
                    "AAAAmUbdOeE:APA91bFkCMheNuX_CKBbHMbonnCjLip5uxilmb-GMRh9khyEA5LmK1osEd-eLhLtll1cBF8H9T6-1DLDptLO26gme63W3hfil5hLjlItrlaQph-tEclZIOFynzTaQ5eA_zpCJlJ0B7Qa"
                )
                .subscribeToTopics(mutableListOf("test")) {
                    log(this.view.btn14.text.toString(), "TOPIC-SUBSCRIPTION:: $it")

                    FCM.instance().sendMessageToTopic(
                        "test",
                        "Title: test",
                        "Message: test",
                        false,
                        "data: test",
                        null, //"default",
                        null,
                    ) {
                        log(this.view.btn14.text.toString(), "NOTIFICATION-SENT:: $it")
                    }
                }
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
                "#0" to "0",
                "#1" to "1",
                "#2" to "2",
            )
        )
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