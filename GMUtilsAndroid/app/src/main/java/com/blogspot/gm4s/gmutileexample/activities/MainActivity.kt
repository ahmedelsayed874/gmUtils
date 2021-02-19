package com.blogspot.gm4s.gmutileexample.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.blogspot.gm4s.gmutileexample.ContactEditorActivity
import com.blogspot.gm4s.gmutileexample.DB
import com.blogspot.gm4s.gmutileexample.R
import com.blogspot.gm4s.gmutileexample.ReadLogFileActivity
import com.blogspot.gm4s1.gmutils.ui.MyToast
import com.blogspot.gm4s1.gmutils.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.text = "Show contact editor"
        btn1.setOnClickListener {
            val intent = Intent(this, ContactEditorActivity::class.java)
            startActivity(intent)
        }

        btn2.text = "read log file"
        btn2.setOnClickListener {
            val intent = Intent(this, ReadLogFileActivity::class.java)
            startActivity(intent)
        }

        btn3.text = "show my toast"
        btn3.setOnClickListener {
            MyToast.show(this, "test my toast")
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
                ActivityCompat.requestPermissions(this, arrayOf("android.permission.WRITE_SETTINGS"), 10)
                return@setOnClickListener
            }

            var bn = Utils.createInstance().getDeviceBrightness(this)

            bn += 10
            if (100 - bn in 1..9) bn = 100
            else if (bn > 100) bn = 0

            Utils.createInstance().setDeviceBrightness(this, bn)

            btn7.text = "change device brightness (C: $bn)"
        }

    }

}
