package com.blogspot.gm4s.gmutileexample

import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.blogspot.gm4s1.gmutils.MyToast
import com.blogspot.gm4s1.gmutils.database.BaseDatabase
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

        btn5.text = "db"
        btn5.setOnClickListener {
            db(this, data1::class.java)
        }

    }

}

open class data0(
    val intField0: Int,
) {
    var longField0: Long? = null
}

data class data1(
    val intField1: Int,
    val longField: Long,

    val floatField: Float,
    val doubleField: Double,

    val stringField: String,

    val booleanField: Boolean,

    val data2Field: data2
) : data0(intField1) {

    var longField1: Long? = null
}

data class data2(
    val intField2: Int
)

class db(context: Context?, dataClass: Class<data1>) : BaseDatabase<data1>(context, dataClass) {

}