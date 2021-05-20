package gmutils.ui.utils

import android.view.View
import androidx.annotation.LayoutRes

sealed class ViewSource {
    class LayoutResource(@LayoutRes val resourceId: Int): ViewSource()
    class ViewBinding(val viewBinding: androidx.viewbinding.ViewBinding) : ViewSource()
    class View(val view: android.view.View) : ViewSource()
}