package com.blogspot.gm4s.gmutileexample.app

import android.app.Activity
import android.app.Application
import android.util.Log
import gmutils.Logger
import gmutils.app.BaseApplication

class AndroidApplicationAndBaseApplication : Application() {

    lateinit var app : BaseApplication

    override fun onCreate() {
        super.onCreate()

        app = BaseApplication.register(this)

        Logger.SET_WRITE_TO_FILE_DEADLINE(22, 11, 2021)
        Log.d("*****", "onCreate()")

        Log.d("*****", "reportedBugs: " + app.reportedBugs)

        app.globalVariables().add("zx", 123)
        val s = app.globalVariables().size();
        Log.d("****", "global var count: " + s.toString())
        Log.d("****", "var val (zx)" +  app.globalVariables().retrieve("zx").toString())

        app.messagingCenter().subscribeAlways(javaClass, "zxc") {name, data ->
            Log.d("****", name + " : " + data.toString())
        }

        app.messagingCenter().post("zxc", "asd")

//        val x = "mmc_wms".hashCode()
//        print("****************** $x")
    }
}