package com.blogspot.gm4s.gmutileexample.activities

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
import com.blogspot.gm4s.gmutileexample.databinding.ActivityReadLogFileBinding
import gmutils.Activities
import gmutils.DateOp
import gmutils.LooperThread
import gmutils.net.SimpleHTTPRequest
import gmutils.net.volley.example.URLs.TimeURLs
import gmutils.ui.activities.BaseActivity
import gmutils.ui.toast.MyToast
import gmutils.ui.utils.ViewSource
import gmutils.utils.Utils
import java.util.*

class MainActivity : BaseActivity() {

    override fun getViewSource(inflater: LayoutInflater) =
        ViewSource.ViewBinding(ActivityMainBinding.inflate(inflater))

    private val view: ActivityMainBinding get() = activityViewBinding as ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            MyToast.show(this, "test my toast", R.drawable.shape_solid_round_accent)
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
            SimpleHTTPRequest.get(url) { request, response ->
                log("api", response.code.toString())
                log("api", response.text)
                log("api", "Exception: ${response.exception}")
            }
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
        }

        Activities.start(ColorPickerActivity::class.java, thisActivity())
        //Activities.start(ColorPicker2Activity::class.java, thisActivity())

        //------------------------------------------------------------------------------------------

        this.view.logTv.viewTreeObserver.addOnGlobalLayoutListener {
            this.view.logSection.scrollTo(0, this.view.logTv.height - 1)
        }
    }

    fun log(tag: String, text: String?) {
        this.view.logTv.append("$tag: $text\n")
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

}
