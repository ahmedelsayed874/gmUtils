package com.blogspot.gm4s.gmutileexample.activities

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import com.blogspot.gm4s.gmutileexample.databinding.ActivityReadLogFileBinding
import gmutils.ui.toast.MyToast
import gmutils.security.Security
import gmutils.ui.activities.BaseActivity
import gmutils.ui.dialogs.WaitDialog
import gmutils.ui.utils.ViewSource
import gmutils.utils.FileUtils
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.util.*

class ReadLogFileActivity : BaseActivity() {

    private var text: String = ""

    override fun getViewSource(inflater: LayoutInflater) = ViewSource.ViewBinding(
        ActivityReadLogFileBinding.inflate(inflater)
    )

    private val view: ActivityReadLogFileBinding get() = viewBinding as ActivityReadLogFileBinding

    //----------------------------------------------------------------------------------------------

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
            "*/*",
            false,
            null,
            1
        )
    }

    fun onOpenTextEditorClick(v: View) {
        //todo
    }

    fun readFile(uri: Uri) {
        val waitDialog = WaitDialog.show(this)

        Thread {
            val inputStream = contentResolver.openInputStream(uri)!!
            val s = inputStream.available()
            val bytes = ByteArray(s)
            inputStream.read(bytes)
            inputStream.close()

            text = String(bytes)

            runOnUiThread {
                this.view.textView.text = text
                waitDialog.dismiss()
            }
        }.start()
    }

    //----------------------------------------------------------------------------------------------

    fun onEncryptClick(view: View) {
        val key = this.view.etKey.text.toString().toIntOrNull()

        if (key == null) {
            MyToast.show(this, "key?")
            return
        }

        try {
            val text = Security.getSimpleInstance(key).decrypt(text)
            this.view.textView.text = text
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
        val waitDialog = WaitDialog.show(this)

        Thread {
            val os = contentResolver.openOutputStream(uri)
            val sw = OutputStreamWriter(os, Charset.forName("utf-8"))

            sw.write(this.view.textView.text.toString())

            sw.flush()
            sw.close()
            os?.close()

            runOnUiThread {
                waitDialog.dismiss()
            }
        }.start()
    }

}

