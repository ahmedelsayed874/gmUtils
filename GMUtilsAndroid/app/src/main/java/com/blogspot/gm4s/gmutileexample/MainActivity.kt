package com.blogspot.gm4s.gmutileexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.blogspot.gm4s1.gmutils.MyToast
import com.blogspot.gm4s1.gmutils.dialogs.MessageDialog
import com.blogspot.gm4s1.gmutils.listeners.SearchTextChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException

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

    }

}
