package com.blogspot.gm4s.gmutileexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.gm4s1.gmutils.dialogs.MessageDialog
import com.blogspot.gm4s1.gmutils.listeners.SearchTextChangeListener
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openBtn.setOnClickListener {
            val intent = Intent(this, ContactEditorActivity::class.java)
            startActivity(intent)
        }

        showDialogMsgBtn.setOnClickListener {
            MessageDialog.create(this)
                .setMessage("Message")
                .setButton1("OK", null)
                .show()
        }

        doExceptionBtn.setOnClickListener {
            throw RuntimeException("zxc")
        }

    }
}