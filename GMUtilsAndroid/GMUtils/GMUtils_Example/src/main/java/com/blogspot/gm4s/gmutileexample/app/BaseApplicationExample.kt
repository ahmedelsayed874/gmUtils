package com.blogspot.gm4s.gmutileexample.app

import android.app.Activity
import gmutils.app.BaseApplication

class BaseApplicationExample : BaseApplication() {

    override fun onApplicationStartedFirstActivity(activity: Activity) {

    }

    override fun onApplicationFinishedLastActivity(activity: Activity) {
    }

}