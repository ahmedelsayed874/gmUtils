package com.blogspot.gm4s.gmutileexample.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.blogspot.gm4s.gmutileexample.R
import com.blogspot.gm4s.gmutileexample.databinding.ActivityMainBinding
import gmutils.Activities
import gmutils.DateOp
import gmutils.Intents
import gmutils.app.BaseApplication
import gmutils.json.JsonBuilder
import gmutils.listeners.TextChangedListener
import gmutils.logger.Logger
import gmutils.logger.LoggerAbs
import gmutils.net.SimpleHTTPRequest
import gmutils.net.retrofit.RetrofitService
import gmutils.net.retrofit.example.data.TimeOfArea
import gmutils.net.volley.example.URLs.TimeURLs
import gmutils.ui.activities.ActivityFunctions
import gmutils.ui.activities.BaseActivity
import gmutils.ui.dialogs.CustomDialog
import gmutils.ui.dialogs.InputDialog
import gmutils.ui.dialogs.ListDialog
import gmutils.ui.dialogs.MessageDialog
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

    override fun onConfigureStatusBar(): ActivityFunctions.StatusBarOverlappingController? {
        //return null
        return object : ActivityFunctions.StatusBarOverlappingController() {
            override fun useLightStatusBarAppearance(): Boolean? {
                //return super.useLightStatusBarAppearance()
                return true
            }
        }
    }

    private val view: ActivityMainBinding get() = viewBinding as ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        var methodCache = ""
        var urlCache = ""
        var headersCache = ""
        var bodyCache = ""
        this.view.btn1.text = "Call API"
        this.view.btn1.setOnClickListener {
            InputDialog(this)
                .setTitle("Call API")
                //
                .setInputTitle("Method")
                .setInputText(methodCache)
                .addInputField {
                    it.setTitle("URL")
                    it.setText(urlCache)
                }
                .addInputField {
                    it.setTitle("Headers")
                    it.setHint("KEY1:VALUE1,KEY2:VALUE2")
                    it.setText(headersCache)
                    it.inputEditText.maxLines = 5
                }
                .addInputField {
                    it.setTitle("Body")
                    it.setText(bodyCache)
                    it.inputEditText.maxLines = 5
                }
                //.setMinimumWidth(windowManager.currentWindowMetrics.windowInsets.frame.width)
                .setMinimumWidth(resources.getDimensionPixelSize(R.dimen.size_400))
                .setPositiveButtonCallback { inputs ->
                    val errors = arrayOf("", "", "", "")

                    val method = when (inputs[0].trim().uppercase()) {
                        SimpleHTTPRequest.Method.GET.name -> SimpleHTTPRequest.Method.GET
                        SimpleHTTPRequest.Method.POST.name -> SimpleHTTPRequest.Method.POST
                        SimpleHTTPRequest.Method.PUT.name -> SimpleHTTPRequest.Method.PUT
                        SimpleHTTPRequest.Method.PATCH.name -> SimpleHTTPRequest.Method.PATCH
                        SimpleHTTPRequest.Method.DELETE.name -> SimpleHTTPRequest.Method.DELETE
                        else -> null
                    }
                    methodCache = method?.toString() ?: ""
                    if (method == null) {
                        errors[0] = ("Method name is wrong")
                    }

                    val url = inputs[1].trim().lowercase()
                    urlCache = url
                    if (url.isEmpty()) {
                        errors[1] = ("Url is empty")
                    }
                    //
                    else if (
                        !url.startsWith("http://", true) &&
                        !url.startsWith("https://", true)
                    ) {
                        errors[1] = ("Enter a valid URL (start by http:// or https://)")
                    }

                    val headers = mutableMapOf<String, String>()
                    headersCache = inputs[2].trim()
                    if (headersCache.isNotEmpty()) {
                        val headerPairs = headersCache.split(",")
                        if (headerPairs.isNotEmpty()) {
                            for (pair in headerPairs) {
                                val kv = pair.trim().split(":")
                                if (kv.size == 2) {
                                    val k = kv[0].trim()
                                    val v = kv[1].trim()

                                    if (k.isNotEmpty()) {
                                        headers[k] = v
                                    } else {
                                        errors[2] += ("$kv is  not correct\n")
                                    }
                                } else {
                                    errors[2] += ("$kv is  not correct\n")
                                }
                            }
                        }
                    }

                    val body = inputs[3]
                    bodyCache = body

                    if (errors.none { it.isNotEmpty() }) {
                        callApi(
                            method = method,
                            url = url,
                            headers = headers,
                            body = body
                        )
                    }

                    errors
                }
                .show()
        }

        this.view.btn2.text = "time api"
        this.view.btn2.setOnClickListener {
            val url = TimeURLs.CurrentTimeURL("Etc/UTC").finalURL
            log("api", "getting time from: $url")

            //execute using static methods
            SimpleHTTPRequest.call(
                true,
                SimpleHTTPRequest.Request(url, SimpleHTTPRequest.Method.GET),
                null
            ) { request, response ->
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
                val response = SimpleHTTPRequest.call(
                    false,
                    SimpleHTTPRequest.Request(
                        url,
                        SimpleHTTPRequest.Method.GET,
                        null
                    ),
                    null,
                    null
                )
                runOnUiThread {
                    log("api", response.second.code.toString())
                    log("api", response.second.text)
                    log("api", "Exception: ${response.second.exception}")
                }
            }.start()
        }

        this.view.btn3.text =
            "change screen brightness ${Utils.createInstance().getScreenBrightness(this)}"
        this.view.btn3.setOnClickListener {
            var bn = Utils.createInstance().getScreenBrightness(this)

            bn += 0.1f
            if (1f - bn < 0.1f && 1f - bn > 0) bn = 1f
            else if (bn > 1) bn = -0.1f

            Utils.createInstance().setScreenBrightness(this, bn)

            this.view.btn6.text = "change screen brightness (C: $bn)"
        }

        this.view.btn4.text =
            "change device brightness ${Utils.createInstance().getDeviceBrightness(this)}"
        this.view.btn4.setOnClickListener {
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

        this.view.btn5.text = "ColorPickerActivity"
        this.view.btn5.setOnClickListener {
            Activities.start(ColorPickerActivity::class.java, thisActivity())
            Activities.start(ColorPicker2Activity::class.java, thisActivity())
        }

        this.view.btn6.text = "take photo"
        this.view.btn6.setOnClickListener {
            val url = Intents.getInstance().imageIntents.takePicture(this, 123)
            log("image from camera uri", url?.toString())
        }

        this.view.btn7.text = "pick photo from gallery"
        this.view.btn7.setOnClickListener {
            Intents.getInstance().imageIntents.pickImage(this, 456)
            log("pick image began", "")
        }

        this.view.btn8.text = "Export App Backup"
        this.view.btn8.setOnClickListener {
            /*val r = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            val f = File(r, System.currentTimeMillis().toString() + ".txt");
            val b = f.createNewFile();*/

            log("get-app-backup", "getting app backup started")
            Logger.d().logConfigs
                .setLogcatDeadline(DateOp.getInstance().increaseDays(1))
                .setWriteLogsToPublicFileDeadline(DateOp.getInstance().increaseDays(1))
                .setWriteLogsToPrivateFileDeadline(DateOp.getInstance().increaseDays(1))

            Logger.d().exportAppBackup(thisActivity(), true) {
                log("get-app-backup", "getting app backup finished: ${it.message}")
            }
        }

        this.view.btn9.text = "Restore App Backup"
        this.view.btn9.setOnClickListener {
            log("restore-app-backup", "restoring app backup started")
            val b = FileUtils.createInstance().showFileExplorer(
                this,
                "application/*",
                false,
                null,
                12321
            )
            log("restore-app-backup", "restoring app backup started: $b")
        }

        this.view.btn10.text = "Test Untrusted Connection"
        this.view.btn10.setOnClickListener {
            testUntrustedConnection()
        }

        this.view.btn11.text = "Input Dialog with 3 inputs"
        this.view.btn11.setOnClickListener {
            InputDialog(this)
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

        this.view.btn12.text = "List Dialog"
        this.view.btn12.setOnClickListener {
            ListDialog<String>(this) { inp, idx ->
                log("List Dialog", inp)
            }.setTitle("Title")
                .setHint("Hint")
                .setList(listOf("Item 1", "Item 2", "Item 3"))
                .show()
        }

        this.view.btn13.text = "Firebase"
        this.view.btn13.setOnClickListener {
            startActivity(Intent(this, FirebaseTestActivity::class.java))
        }

        this.view.btn14.text = "Open File"
        this.view.btn14.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    9
                )
                return@setOnClickListener
            }

            InputDialog(this)
                .setTitle("Open File Explore")
                .setMessage("Type mimetype of the file")
                .setInputHint("Mimetype (separate by ,)")
                .setInputText("application/*")
                .setPositiveButtonCallback {
                    val split = it[0].split(',').toTypedArray()

                    val b = FileUtils.createInstance().showFileExplorer(
                        this,
                        split,
                        false,
                        null,
                        12321
                    )
                    log("opening-file", "opening-file of mimetype = ${it[0]} ---> got $b")

                    null
                }
                .show()


        }

        this.view.btn15.text = "Compare App Version"
        this.view.btn15.setOnClickListener {
            var randomVer = Random.nextInt(0, 3).toString()
            randomVer += "."
            randomVer += Random.nextInt(0, 3).toString()
            randomVer += "."
            randomVer += Random.nextInt(0, 3).toString()

            val b = Utils.createInstance().compareAppVersion(this, randomVer)
            val vn = Utils.createInstance().currentAppVersion(this)

            log(
                "compareAppVersion",
                "compareAppVersion(focalVersion: $randomVer, currentAppVersion: $vn) ------> $b"
            )
        }

        this.view.btn16.text = "Show Custom Dialog 1"
        this.view.btn16.setOnClickListener {
            val cd = CustomDialog(this)
            cd.setTitle("Custom Dialog")
            //cd.setWidth(resources.getDimensionPixelSize(R.dimen.size_400))
            //cd.setWidthBy(R.dimen.size_400)
            cd.setWidthToMax(50)
            cd.addChildren(
                //
                CustomDialog.Text("Input 1:"),
                CustomDialog.TextField("Input 1")
                    .setTextChangedListener(object : TextChangedListener() {
                        override fun onTextChanged(s: String?) {
                            print(s)
                        }
                    })
                /*.subscribeOnUpdate { args ->
                    print(args)
                    //to do
                }*/,

                //
                CustomDialog.Text("Input 2:"),
                CustomDialog.Checkbox("Check 1")
                    .setOnClickListener { builder, view ->
                        print(builder)
                    }
                /*.subscribeOnUpdate { args ->
                    print(args)
                    //to do
                }*/,
                CustomDialog.Checkbox("Check 2")
                    .setOnClickListener { builder, view ->
                        print(builder)
                    }
                /*.subscribeOnUpdate { args ->
                    print(args)
                    //to do
                }*/,
                CustomDialog.Checkbox("Check 3")
                    .setOnClickListener { builder, view ->
                        print(builder)
                    }
                /*.subscribeOnUpdate { args ->
                    print(args)
                    //to do
                }*/,

                //
                CustomDialog.Text("Input 3:"),
                CustomDialog.Options()
                    .addOption(CustomDialog.Options.OptionElement("Option 1"))
                    .addOption(CustomDialog.Options.OptionElement("Option 2"))
                    .addOption(CustomDialog.Options.OptionElement("Option 3"))
                    .setOnOptionSelected { option, idx ->
                        print(option)
                        print(idx)
                    }
                /*.subscribeOnUpdate { args ->
                    print(args)
                    //to do
                }*/,

                //
                CustomDialog.Text("Input 4:"),
                CustomDialog.Row()
                    .addChild(
                        CustomDialog
                            .Text("Row Text 1")
                            .setOnClickListener { builder, view ->
                                MyToast.show(this, "Row Text 1 clicked")
                            }
                    )
                    .addChild(
                        CustomDialog
                            .BasicView(
                                CustomDialog.ViewSize.absolute(1),
                                CustomDialog.ViewSize.absolute(50),
                            )
                            .setBackgroundColor(Color.RED)
                            .setMarginSymmetric(10, 0)
                    )
                    .addChild(
                        CustomDialog
                            .Text("Row Text 2")
                            .setOnClickListener { builder, view ->
                                MyToast.show(this, "Row Text 2 clicked")
                            }
                    )
                    .addChild(
                        CustomDialog
                            .Text("Dispatch Data Lister")
                            .setOnClickListener { builder, view ->
                                cd.dispatchStateUpdated(builder, "Dispatch")
                            }
                    ),

                //
                CustomDialog.BasicView(
                    CustomDialog.ViewSize.matchParent(),
                    CustomDialog.ViewSize.absolute(3)
                ).setBackgroundColor(
                    Color.RED
                ).setMarginSymmetric(
                    0,
                    10
                ),
                CustomDialog
                    .Button("DONE")
                    .setOnClickListener { builder, view ->
                        print(builder)

                        cd.dismiss("DONE")
                    }
            ).subscribeOnUpdate { args ->
                args.children.forEachIndexed { index, view ->
                    Log.d(
                        "CustomDialog",
                        "$index | ${view.javaClass.simpleName} | ${view.nativeView}"
                    )
                }

                val input1 = (args.children[1].nativeView as EditText).text
                val input1_ = args.children[1].value

                val input2Chk1 = (args.children[3].nativeView as CheckBox).isChecked
                val input2Chk1_ = args.children[3].value
                val input2Chk2 = (args.children[4].nativeView as CheckBox).isChecked
                val input2Chk2_ = args.children[4].value
                val input2Chk3 = (args.children[5].nativeView as CheckBox).isChecked
                val input2Chk3_ = args.children[5].value

                val input3 = (args.children[7].nativeView as RadioGroup).checkedRadioButtonId
                val input3_ = args.children[7].value
                val input3Opt1 =
                    (args.children[7].subViews[0].nativeView as RadioButton).let { "${it.id} / ${it.isChecked}" }
                val input3Opt1_ = args.children[7].subViews[0].value
                val input3Opt2 =
                    (args.children[7].subViews[1].nativeView as RadioButton).let { "${it.id} / ${it.isChecked}" }
                val input3Opt2_ = args.children[7].subViews[1].value
                val input3Opt3 =
                    (args.children[7].subViews[2].nativeView as RadioButton).let { "${it.id} / ${it.isChecked}" }
                val input3Opt3_ = args.children[7].subViews[2].value

                val btn = (args.children[11].nativeView as Button).text
                val btn_ = args.children[11].value

                Log.d(
                    "CustomDialog",
                    JsonBuilder.ofJsonObject()
                        .add("input1", input1)
                        .add("input1_", input1_)

                        .add("input2Chk1", input2Chk1)
                        .add("input2Chk1_", input2Chk1_)
                        .add("input2Chk2", input2Chk2)
                        .add("input2Chk2_", input2Chk2_)
                        .add("input2Chk3", input2Chk3)
                        .add("input2Chk3_", input2Chk3_)

                        .add("input3", input3)
                        .add("input3_", input3_)
                        .add("input3Opt1", input3Opt1)
                        .add("input3Opt1_", input3Opt1_)
                        .add("input3Opt2", input3Opt2)
                        .add("input3Opt2_", input3Opt2_)
                        .add("input3Opt3", input3Opt3)
                        .add("input3Opt3_", input3Opt3_)

                        .add("btn", btn)
                        .add("btn_", btn_)

                        .toString()
                )
            }
            cd.show()
        }

        this.view.btn17.text = "Show Custom Dialog 2"
        this.view.btn17.setOnClickListener {
            val cd = CustomDialog(this)
            cd.setTitle("Custom Dialog")
            cd.setWidthToMax(50)
            cd.addChildren(
                //
                CustomDialog.Text("Input 1:"),
                CustomDialog.TextField("Input 1").watchTextChanges(), //1

                //
                CustomDialog.Text("Input 2:"),
                CustomDialog.Checkbox("Check 1").watchCheckChanges(), //3
                CustomDialog.Checkbox("Check 2").watchCheckChanges(), //4
                CustomDialog.Checkbox("Check 3").watchCheckChanges(), //5

                //
                CustomDialog.Text("Input 3:"),
                CustomDialog.Options()                  //7
                    .addOption(CustomDialog.Options.OptionElement("Option 1"))
                    .addOption(CustomDialog.Options.OptionElement("Option 2"))//.setSelected(true))
                    .addOption(CustomDialog.Options.OptionElement("Option 3"))
                    .watchOptionSelections(),

                //
                CustomDialog.Text("User actions:"),
                CustomDialog.Text(""),                //9

                //line
                CustomDialog.BasicView(
                    CustomDialog.ViewSize.matchParent(),
                    CustomDialog.ViewSize.absolute(3)
                ).setBackgroundColor(
                    Color.RED
                ).setMarginSymmetric(
                    0,
                    10
                ),

                //btn
                CustomDialog
                    .Button("DONE")
                    .setLayoutGravity(Gravity.CENTER)
                    .setOnClickListener { builder, view ->
                        cd.dismiss("DONE")
                    }
            ).subscribeOnUpdate { args ->
                args.children.forEachIndexed { index, view ->
                    Log.d(
                        "CustomDialog",
                        "$index | ${view.javaClass.simpleName} | ${view.nativeView}"
                    )
                }

                val input1Text = args.children[1].value

                val input2Chk1Text = (args.children[3].nativeView as TextView).text.toString()
                val input2Chk1 = args.children[3].value

                val input2Chk2Text = (args.children[4].nativeView as TextView).text.toString()
                val input2Chk2 = args.children[4].value

                val input2Chk3Text = (args.children[5].nativeView as TextView).text.toString()
                val input2Chk3 = args.children[5].value

                val input3Text = (args.children[6].nativeView as TextView).text.toString()
                val i3v = args.children[7].value
                val i3vt = i3v.javaClass
                val input3 =
                    (args.children[7].value as? android.util.Pair<*, *>)?.second ?: "null"

                val text = JsonBuilder.ofJsonObject()
                    .add("text", input1Text)

                    .add(input2Chk1Text, input2Chk1)
                    .add(input2Chk2Text, input2Chk2)
                    .add(input2Chk3Text, input2Chk3)

                    .add(input3Text, input3)

                    .toString(8)

                Log.d("CustomDialog", text)

                (args.children[9].nativeView as TextView).text = text
            }
            cd.show()
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

    private fun callApi(
        method: gmutils.net.SimpleHTTPRequest.Method?,
        url: String,
        headers: kotlin.collections.MutableMap<String, String>,
        body: String
    ) {
        MyToast.show(this, "wait...")
        SimpleHTTPRequest.call(
            true,
            SimpleHTTPRequest.Request(
                url,
                method,
                headers,
                body
            ),
            null
        ) { req, res ->
            MessageDialog.create(this)
                .setMessage(res.toString())
                .show()
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
            if (data?.data == null) return

            Logger.d().importAppBackup(thisActivity(), data.data) { b, it ->
                log("restore-app-backup", "restoring app backup finished: $it")
            }
        }
    }

    fun testLogger() {
        val logger = Logger.instance("testLogger")
        logger.logConfigs.apply {
            val dl = DateOp.getInstance().increaseDays(1)
            setLogcatDeadline(dl)
            setWriteLogsToPublicFileDeadline(dl)
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
        Log.d("testLogger", logger.getLogFilesPath(this, null))

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
        Logger.d().logConfigs.setLogcatDeadline(DateOp.getInstance().increaseDays(1))
        log("Test Untrusted Connection", "Test Untrusted Connection (Retrofit)")

        FileUtils.createInstance().showFileExplorer(
            this,
            "*/*",
            false,
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
        c.enqueue(
            gmutils.net.retrofit.callback.Callback(
                c.request(),
                TimeOfArea::class.java
            ) {
                Logger.d().print { it }
                log("Test Untrusted Connection", it.toString())
            })
    }
}
