package com.blogspot.gm4s.gmutileexample.app

import android.app.Application
import android.util.Log
import com.blogspot.gm4s1.gmutils.Logger
import com.blogspot.gm4s1.gmutils.ui.app.BaseApplication

class AndroidApplicationAndBaseApplication : Application() {

    val app = object : BaseApplication() {
        override fun thisInstance(): Application {
            return this@AndroidApplicationAndBaseApplication
        }

        override fun onPreCreate() {
            Log.d("*****", "onPreCreate()")
        }

        override fun onPostCreate() {
            Log.d("*****", "onPostCreate()")
        }

        override fun onApplicationStartedFirstActivity() {
            Log.d("*****", "onApplicationStartedFirstActivity()")
        }

        override fun onApplicationFinishedLastActivity() {
            Log.d("*****", "onApplicationFinishedLastActivity()")
        }

    }

    override fun onCreate() {
        super.onCreate()
        app.onCreate()

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

        app.addCallback("asd", object: BaseApplication.Callbacks {
            override fun onApplicationStartedFirstActivity(key: String?) {
                Log.d("*****", "BaseApplication.Callbacks.onApplicationStartedFirstActivity($key)")
            }

            override fun onApplicationFinishedLastActivity(key: String?) {
                Log.d("*****", "BaseApplication.Callbacks.onApplicationFinishedLastActivity($key)")
            }

        })

    }
}