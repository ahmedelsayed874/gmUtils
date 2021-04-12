package com.blogspot.gm4s.gmutileexample.activities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.blogspot.gm4s1.gmutils.ui.activities.BaseActivity
import com.blogspot.gm4s1.gmutils.ui.viewModels.BaseViewModel
import java.util.HashMap

class ActivityWithViewModelExampleActivity : BaseActivity() {

    companion object {
        const val VM1_ID = 0
        const val VM2_ID = 1
    }

    override fun getActivityLayout(): Int {
        TODO("Not yet implemented")
    }

    override fun getViewModelClasses(): HashMap<Int, Class<out BaseViewModel>> {
        return hashMapOf(
            VM1_ID to ActivityWithViewModelExampleViewModel1::class.java,
            VM2_ID to ActivityWithViewModelExampleViewModel2::class.java,
        )
    }

    override fun getViewModelFactory(id: Int): ViewModelProvider.Factory {
        return if (VM1_ID == id) {
            super.getViewModelFactory(id)

        } else {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val m = ActivityWithViewModelExampleViewModel2(application, 100.0)
                    return m as T
                }
            }
        }
    }

    override fun getViewModel(id: Int): BaseViewModel {
        return if (VM1_ID == id)
            super.getViewModel(id) as ActivityWithViewModelExampleViewModel1
        else
            super.getViewModel(id) as ActivityWithViewModelExampleViewModel2
    }


}