package com.blogspot.gm4s.gmutileexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.blogspot.gm4s1.gmutils.listeners.SearchTextChangeListener
import kotlinx.android.synthetic.main.activity_contact_editor.*

class ContactEditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_editor)

        backBtn.setOnClickListener {
            finish()
        }
    }
}