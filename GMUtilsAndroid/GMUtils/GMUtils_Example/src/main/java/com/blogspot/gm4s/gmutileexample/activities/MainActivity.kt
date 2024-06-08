package com.blogspot.gm4s.gmutileexample.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.blogspot.gm4s.gmutileexample.DB
import com.blogspot.gm4s.gmutileexample.R
import com.blogspot.gm4s.gmutileexample.databinding.ActivityMainBinding
import gmutils.Activities
import gmutils.DateOp
import gmutils.Intents
import gmutils.backgroundWorkers.LooperThread
import gmutils.app.BaseApplication
import gmutils.firebase.fcm.FCM
import gmutils.firebase.fcm.FcmMessageHandler
import gmutils.firebase.fcm.FcmNotificationProperties
import gmutils.logger.Logger
import gmutils.logger.LoggerAbs
import gmutils.net.SimpleHTTPRequest
import gmutils.net.retrofit.RetrofitService
import gmutils.net.retrofit.example.data.TimeOfArea
import gmutils.net.volley.example.URLs.TimeURLs
import gmutils.ui.activities.BaseActivity
import gmutils.ui.dialogs.InputDialog
import gmutils.ui.dialogs.ListDialog
import gmutils.ui.toast.MyToast
import gmutils.ui.utils.ViewSource
import gmutils.utils.FileUtils
import gmutils.utils.Utils
import okhttp3.OkHttpClient
import java.io.InputStream
import javax.net.ssl.X509TrustManager
import kotlin.concurrent.thread
import kotlin.random.Random

class MainActivity : BaseActivity() {

    override fun getViewSource(inflater: LayoutInflater) =
        ViewSource.ViewBinding(ActivityMainBinding.inflate(inflater))

    private val view: ActivityMainBinding get() = viewBinding as ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)

        this.view.btn1.text = "calc size"
        this.view.btn1.setOnClickListener {
            val x = Utils.createInstance().calculatePixelInCm(this, 1.0)
            log("", "x = $x")
        }

        this.view.btn2.text = "read log file"
        this.view.btn2.setOnClickListener {
            val intent = Intent(this, ReadLogFileActivity::class.java)
            startActivity(intent)
        }

        this.view.btn3.text = "show my toast2"
        this.view.btn3.setOnClickListener {
            MyToast.show(this, "test my toast", false, R.drawable.shape_solid_round_accent)
        }

        this.view.btn4.text = "show original toast"
        this.view.btn4.setOnClickListener {
            Toast.makeText(this, "test original toast", Toast.LENGTH_LONG).show()
        }

        this.view.btn5.text = "test BaseDatabase class"
        this.view.btn5.setOnClickListener {
            DB(this).test()
        }

        this.view.btn6.text =
            "change screen brightness ${Utils.createInstance().getScreenBrightness(this)}"
        this.view.btn6.setOnClickListener {
            var bn = Utils.createInstance().getScreenBrightness(this)

            bn += 0.1f
            if (1f - bn < 0.1f && 1f - bn > 0) bn = 1f
            else if (bn > 1) bn = -0.1f

            Utils.createInstance().setScreenBrightness(this, bn)

            this.view.btn6.text = "change screen brightness (C: $bn)"
        }

        this.view.btn7.text =
            "change device brightness ${Utils.createInstance().getDeviceBrightness(this)}"
        this.view.btn7.setOnClickListener {
            val checkSelfPermission =
                ActivityCompat.checkSelfPermission(this, "android.permission.WRITE_SETTINGS")
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("android.permission.WRITE_SETTINGS"),
                    10
                )
                return@setOnClickListener
            }

            var bn = Utils.createInstance().getDeviceBrightness(this)

            bn += 10
            if (100 - bn in 1..9) bn = 100
            else if (bn > 100) bn = 0

            Utils.createInstance().setDeviceBrightness(this, bn)

            this.view.btn7.text = "change device brightness (C: $bn)"
        }

        this.view.btn8.text = "DateOp"
        this.view.btn8.setOnClickListener {
            val d1 = DateOp.getInstance()
            log("*****", "------------------------------")
            log("*****", d1.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssXXX, false))
            log("*****", d1.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssZ, false))
            log("*****", d1.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_z, false))
            log("*****", d1.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSXXX, false))
            log("*****", d1.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSZ, false))
            log("*****", d1.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSS_z, false))

            val d11 = DateOp.getInstance("2021-03-01 11:12:13", false)
            log("*****", "------------------------------")
            log("*****", d11.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssXXX, false))
            log("*****", d11.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssZ, false))
            log("*****", d11.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_z, false))
            log("*****", d11.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSXXX, false))
            log("*****", d11.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSZ, false))
            log("*****", d11.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSS_z, false))

            val d2 = DateOp.getInstance("2021-03-01 11:12", false)
            log("*****", "------------------------------")
            log("*****", d2.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssXXX, false))
            log("*****", d2.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssZ, false))
            log("*****", d2.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_z, false))
            log("*****", d2.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSXXX, false))
            log("*****", d2.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSZ, false))
            log("*****", d2.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSS_z, false))

            val d = DateOp.getInstance("2021-03-01", false)
            log("*****", "------------------------------")
            log("*****", d.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssXXX, false))
            log("*****", d.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ssZ, false))
            log("*****", d.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_z, false))
            log("*****", d.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSXXX, false))
            log("*****", d.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSSZ, false))
            log("*****", d.formatDate(DateOp.PATTERN_yyyy_MM_dd_HH_mm_ss_SSS_z, false))

        }

        this.view.btn9.text = "time api"
        this.view.btn9.setOnClickListener {
            val url = TimeURLs.CurrentTimeURL("Etc/UTC").finalURL
            log("api", "getting time from: $url")

            //execute using static methods
            SimpleHTTPRequest.get(url) { request, response ->
                log("api", response.code.toString())
                log("api", response.text)
                log("api", "Exception: ${response.exception}")
            }

            //execute using initiating new instance
            SimpleHTTPRequest.TextRequestExecutor(
                SimpleHTTPRequest.Request(url, SimpleHTTPRequest.Method.GET, null)
            ).executeAsynchronously { request, response ->
                log("api", response.code.toString())
                log("api", response.text)
                log("api", "Exception: ${response.exception}")
            }

            //execute synchronously
            Thread {
                val response = SimpleHTTPRequest.createSynchronously(
                    SimpleHTTPRequest.Request(
                        url,
                        SimpleHTTPRequest.Method.GET,
                        null
                    )
                )
                runOnUiThread {
                    log("api", response.second.code.toString())
                    log("api", response.second.text)
                    log("api", "Exception: ${response.second.exception}")
                }
            }.start()
        }

        this.view.btn10.text = "LooperThread"
        var x = 1
        this.view.btn10.setOnClickListener {
            LooperThread("MyLooperThread") { args ->
                this.view.btn10.postDelayed({
                    this.view.btn10.text = "LooperThread ${args.msg.arg1}"
                }, 1000)
                Log.e("***", Thread.currentThread().name)
            }.sendMessage(Message().also { it.arg1 = x++ })
        }

        this.view.btn11.text = "ColorPickerActivity"
        this.view.btn11.setOnClickListener {
            Activities.start(ColorPickerActivity::class.java, thisActivity())
            Activities.start(ColorPicker2Activity::class.java, thisActivity())
        }

        this.view.btn12.text = "take photo"
        this.view.btn12.setOnClickListener {
            val url = Intents.getInstance().imageIntents.takePicture(this, 123)
            log("image from camera uri", url?.toString())
        }

        this.view.btn13.text = "pick photo from gallery"
        this.view.btn13.setOnClickListener {
            Intents.getInstance().imageIntents.pickImage(this, 456)
            log("pick image began", "")
        }

        this.view.btn14.text = "Test Logger Functions"
        this.view.btn14.setOnClickListener {
            log("test logger", "")
            testLogger()
        }

        this.view.btn15.text = "Get App Backup"
        this.view.btn15.setOnClickListener {
            /*val r = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            val f = File(r, System.currentTimeMillis().toString() + ".txt");
            val b = f.createNewFile();*/

            log("get-app-backup", "getting app backup started")
            Logger.d().logConfigs
                .setLogDeadline(DateOp.getInstance().increaseDays(1))
                .setWriteLogsToFileDeadline(DateOp.getInstance().increaseDays(1))

            Logger.d().exportAppBackup(thisActivity(), true) {
                log("get-app-backup", "getting app backup finished: ${it.message}")
            }
        }

        this.view.btn16.text = "Restore App Backup"
        this.view.btn16.setOnClickListener {
            log("restore-app-backup", "restoring app backup started")
            val b = FileUtils.createInstance().showFileExplorer(
                this,
                "application/*",
                null,
                12321
            )
            log("restore-app-backup", "restoring app backup started: $b")
        }

        this.view.btn17.text = "Test Untrusted Connection"
        this.view.btn17.setOnClickListener {
            testUntrustedConnection()
        }

        this.view.btn18.text = "Input Dialog with 3 inputs"
        this.view.btn18.setOnClickListener {
            InputDialog.create(this)
                .setTitle("title")
                .setMessage("message")
                .addInputField {
                    it.setTitle("Title 1")
                    it.setHint("input 1")
                }
                .addInputField {
                    it.setHint("input 2")
                }
                .addInputField {
                    it.setHint("input 3")
                }
                .setPositiveButtonCallback { INP ->
                    INP.forEach {
                        log("input dialog", it)
                    }
                    Array<String>(INP.size) { INP[it] }
                }
                .show()
        }

        this.view.btn19.text = "List Dialog"
        this.view.btn19.setOnClickListener {
            ListDialog<String>(this) { inp, idx ->
                log("List Dialog", inp)
            }.setTitle("Title")
                .setHint("Hint")
                .setList(listOf("Item 1", "Item 2", "Item 3"))
                .show()
        }

        this.view.btn20.text = "Firebase"
        this.view.btn20.setOnClickListener {
            startActivity(Intent(this, FirebaseTestActivity::class.java))
        }


        //------------------------------------------------------------------------------------------

        this.view.logTv.viewTreeObserver.addOnGlobalLayoutListener {
            this.view.logSection.scrollTo(0, this.view.logTv.height - 1)
        }

        //val bug = 1 / 0
        (application as BaseApplication).checkBugsExist(this) {
            Log.d(this::class.java.name, "onCreate: ")
        }


    }

    fun log(tag: String, text: String?) {
        this.view.logTv.append("$tag: $text\n---------------------\n\n")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123) {
            log("action result from camera", data?.data?.toString())
            log("action result from camera", data?.extras?.toString())
        }
        //
        else if (requestCode == 456) {
            log("action result pick image", data?.data?.toString())
            log("action result pick image", data?.extras?.toString())
        }
        //
        else if (requestCode == 12313) {
            testUntrustedConnection_openCertificate(contentResolver.openInputStream(data!!.data!!)!!)
        }
        //
        else if (requestCode == 12321) {
            Logger.d().importAppBackup(thisActivity(), data!!.data) { b, it ->
                log("restore-app-backup", "restoring app backup finished: $it")
            }
        }
    }

    fun testLogger() {
        val logger = Logger.instance("testLogger")
        logger.logConfigs.apply {
            val dl = DateOp.getInstance().increaseDays(1)
            setLogDeadline(dl)
            setWriteLogsToFileDeadline(dl)
        }

        thread {
            for (i in 0..200) {
                //_testLogger(logger)
                logger.print { "A$i," }
//                Thread.sleep(Random.nextLong(1, 5))
            }
        }

//        thread {
//            for (i in 0..100) {
//                //_testLogger(logger)
//                logger.print { "B$i," }
//                Thread.sleep(Random.nextLong(1, 5))
//            }
//        }
    }

    fun _testLogger(logger: LoggerAbs) {
        try {
            val x = 1 / 0
        } catch (e: Exception) {
            logger.print(e)
            logger.print({ logger.logId() }, e)
        }

        logger.print { "loggerTest()" }
        logger.print({ logger.logId() }) { "loggerTest()" }
        Log.d("testLogger", logger.getLogFilesPath(this))

        logger.printMethod()
        logger.printMethod({ logger.logId() })
        logger.printMethod({ logger.logId() }) { "this is more info" }
        val r = Runnable {
            logger.printMethod()
            logger.printMethod { logger.logId() }
            logger.printMethod({ logger.logId() }, { "this is more info" })
        }
        r.run()
        logger.writeToFile(this) { "writing to file" }
    }


    private fun testUntrustedConnection() {
        Logger.d().logConfigs.setLogDeadline(DateOp.getInstance().increaseDays(1))
        log("Test Untrusted Connection", "Test Untrusted Connection (Retrofit)")

        FileUtils.createInstance().showFileExplorer(
            this,
            "*/*",
            null,
            12313
        )


    }

    private fun testUntrustedConnection_openCertificate(inputStream: InputStream) {
        val s = RetrofitService.create(
            "https://famsproductdemo.mnc-control.com/",
            gmutils.net.retrofit.example.apiServices.TimeAPIsRequests::class.java,
            object : RetrofitService.ClientBuildCallback {
                override fun getX509TrustManager(): X509TrustManager? {
                    //it solved the issue of connecting https
                    /*val ks = File(filesDir, "cert-keystore")
                    if (!ks.exists()) ks.createNewFile()

                    return RetrofitService
                        .TrustManagerHelper()
                        .getOrCreateTrustManagerFromCertificate(
                            ks.path,
                            "crt",
                            "any-password",
                        ) { inputStream }*/

                    //it solved the issue of connecting https
                    return RetrofitService
                        .TrustManagerHelper()
                        .unsafeTrustManager

                    //it blocks not trusted connection by https
                    /*return RetrofitService
                        .TrustManagerHelper()
                        .defaultTrustManager*/

                    //it blocks not trusted connection by https
                    //return null
                }

                override fun config(httpClient: OkHttpClient.Builder, error: String?) {
                    log("Test Untrusted Connection", "ClientBuildCallback.config >> Error: $error")
                }
            }
        )
        val c = s.getCurrentTime("cairo")
        c.enqueue(gmutils.net.retrofit.callback.Callback(
            c.request(),
            TimeOfArea::class.java
        ) {
            Logger.d().print { it }
            log("Test Untrusted Connection", it.toString())
        })
    }

}
