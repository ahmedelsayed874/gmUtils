package com.blogspot.gm4s.gmutileexample.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.blogspot.gm4s.gmutileexample.DB
import com.blogspot.gm4s.gmutileexample.R
import com.blogspot.gm4s.gmutileexample.ReadLogFileActivity
import com.blogspot.gm4s1.gmutils.DateOp
import com.blogspot.gm4s1.gmutils.LooperThread
import com.blogspot.gm4s1.gmutils.net.SimpleHTTPRequest
import com.blogspot.gm4s1.gmutils.net.volley.example.URLs.TimeURLs
import com.blogspot.gm4s1.gmutils.ui.MyToast
import com.blogspot.gm4s1.gmutils.ui.MyToast2
import com.blogspot.gm4s1.gmutils.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.text = "calc size"
        btn1.setOnClickListener {
            val x = Utils.createInstance().calculatePixelInCm(this, 1.0)
            log("", "x = $x")
        }

        btn2.text = "read log file"
        btn2.setOnClickListener {
            val intent = Intent(this, ReadLogFileActivity::class.java)
            startActivity(intent)
        }

        btn3.text = "show my toast2"
        btn3.setOnClickListener {
            MyToast2.show(this, "test my toast", R.drawable.shape_solid_round_accent)
        }

        btn4.text = "show original toast"
        btn4.setOnClickListener {
            Toast.makeText(this, "test original toast", Toast.LENGTH_LONG).show()
        }

        btn5.text = "test BaseDatabase class"
        btn5.setOnClickListener {
            DB(this).test()
        }

        btn6.text = "change screen brightness ${Utils.createInstance().getScreenBrightness(this)}"
        btn6.setOnClickListener {
            var bn = Utils.createInstance().getScreenBrightness(this)

            bn += 0.1f
            if (1f - bn < 0.1f && 1f - bn > 0) bn = 1f
            else if (bn > 1) bn = -0.1f

            Utils.createInstance().setScreenBrightness(this, bn)

            btn6.text = "change screen brightness (C: $bn)"
        }

        btn7.text = "change device brightness ${Utils.createInstance().getDeviceBrightness(this)}"
        btn7.setOnClickListener {
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

            btn7.text = "change device brightness (C: $bn)"
        }

        btn8.text = "DateOp"
        btn8.setOnClickListener {
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

        btn9.text = "time api"
        btn9.setOnClickListener {
            val url = TimeURLs.CurrentTimeURL("Etc/UTC").finalURL
            log("api", "getting time from: $url")
            SimpleHTTPRequest.get(url) { request, response ->
                log("api", response.code.toString())
                log("api", response.text)
                log("api", "Exception: ${response.exception}")
            }
        }
        
        btn10.text = "LooperThread"
        var x = 1
        btn10.setOnClickListener {
            LooperThread("MyLooperThread") {args ->
                btn10.postDelayed({
                    btn10.text = "LooperThread ${args.msg.arg1}"
                }, 1000)
                Log.e("***", Thread.currentThread().name)
            }.sendMessage(Message().also { it.arg1 = x++ })
        }

        //------------------------------------------------------------------------------------------

        logTv.viewTreeObserver.addOnGlobalLayoutListener {
            logSection.scrollTo(0, logTv.height - 1)
        }
    }

    fun log(tag: String, text: String?) {
        logTv.append("$tag: $text\n")
    }
    
    fun onShowOrHideLogClick(view: View) {
        if (logSection.visibility == View.GONE) {
            logSection.visibility = View.VISIBLE
            (view as TextView).text = "Hide Log"
            
        } else {
            logSection.visibility = View.GONE
            (view as TextView).text = "Show Log"
        }
    }

}
