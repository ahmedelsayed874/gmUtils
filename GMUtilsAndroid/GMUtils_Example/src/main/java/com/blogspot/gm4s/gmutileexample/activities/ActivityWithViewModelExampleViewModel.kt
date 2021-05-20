package com.blogspot.gm4s.gmutileexample.activities

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.annotation.FloatRange
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import gmutils.ui.viewModels.BaseViewModel
import gmutils.utils.Utils

class ActivityWithViewModelExampleViewModel1(
    application: Application
) : BaseViewModel(application) {

    val waitDialogShownStatusLiveData: LiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val resultLiveData: LiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    private var value : Double = 0.0

    fun init() {
        val ld = waitDialogShownStatusLiveData as MutableLiveData
        ld.value = true
        Handler(Looper.getMainLooper()).postDelayed({
            ld.value = false
        }, 3000)
    }

    fun add(value: Double) {
        this.value += value
        (resultLiveData as MutableLiveData).value = this.value
    }

    fun round(@FloatRange(from = 0.1, to = 0.9) threshold: Float) {
        this.value = Utils.createInstance().roundNumber(this.value, threshold).toDouble()
        (resultLiveData as MutableLiveData).value = this.value
    }

}

class ActivityWithViewModelExampleViewModel2(
    application: Application,
    initValue: Double
) : BaseViewModel(application) {

    val waitDialogShownStatusLiveData: LiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val resultLiveData: LiveData<Double> by lazy {
        MutableLiveData<Double>()
    }

    private var value : Double = initValue

    fun init() {
        val ld = waitDialogShownStatusLiveData as MutableLiveData
        ld.value = true
        Handler(Looper.getMainLooper()).postDelayed({
            ld.value = false
        }, 3000)
    }

    fun add(value: Double) {
        this.value += value
        (resultLiveData as MutableLiveData).value = this.value
    }

    fun round(@FloatRange(from = 0.1, to = 0.9) threshold: Float) {
        this.value = Utils.createInstance().roundNumber(this.value, threshold).toDouble()
        (resultLiveData as MutableLiveData).value = this.value
    }

}
