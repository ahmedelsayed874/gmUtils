package com.blogspot.gm4s.gmutileexample

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.gm4s1.gmutils._ui.MyToast
import com.blogspot.gm4s1.gmutils.Security
import com.blogspot.gm4s1.gmutils.utils.FileUtils
import kotlinx.android.synthetic.main.activity_read_log_file.*
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.util.*

class ReadLogFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_log_file)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                readFile(data!!.data!!)

            } else if (requestCode == 2) {
                saveFile(data!!.data!!)
            }
        }
    }

    //----------------------------------------------------------------------------------------------

    fun onOpenFileClick(view: View) {
        FileUtils.createInstance().showFileExplorer(
            this,
            "text/*",
            null,
            1
        )
    }

    fun readFile(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)!!
        val s = inputStream.available()
        val bytes = ByteArray(s)
        inputStream.read(bytes)
        textView.text = String(bytes)
        inputStream.close()
    }

    //----------------------------------------------------------------------------------------------

    fun onEncryptClick(view: View) {
        val key = etKey.text.toString().toIntOrNull()

        if (key == null) {
            MyToast.show(this, "key?")
            return
        }

        try {
            val text = Security.getSimpleInstance(key).decrypt(textView.text.toString())
            textView.text = text
        } catch (e: Exception) {
            MyToast.show(this, "error: ${e.message}")
        }
    }

    //----------------------------------------------------------------------------------------------

    fun onSaveClick(view: View) {
        FileUtils.createInstance().createFileOnStorageUsingFileExplorer(
            this,
            "log file ${Date().toString().replace(":", "-")}",
            "text/*",
            null,
            2
        )
    }

    fun saveFile(uri: Uri) {
        val os = contentResolver.openOutputStream(uri)
        val sw = OutputStreamWriter(os, Charset.forName("utf-8"))

        sw.write(textView.text.toString())

        sw.flush()
        sw.close()
        os?.close()
    }

}

