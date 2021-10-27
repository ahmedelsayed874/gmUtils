package gmutilssupport.ui.utils

import android.support.annotation.LayoutRes

sealed class ViewSource {
    class LayoutResource(@LayoutRes val resourceId: Int): ViewSource()
    class ViewBinding(val viewBinding: android.viewbinding.ViewBinding) : ViewSource()
    class View(val view: android.view.View) : ViewSource()
}