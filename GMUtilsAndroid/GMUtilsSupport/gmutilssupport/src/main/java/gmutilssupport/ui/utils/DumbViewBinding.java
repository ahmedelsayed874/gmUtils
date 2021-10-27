package gmutilssupport.ui.utils;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.view.View;
import android.viewbinding.ViewBinding;

public class DumbViewBinding implements ViewBinding {
    private View view;

    public DumbViewBinding(View view) {
        this.view = view;
    }

    @NonNull
    @Override
    public View getRoot() {
        return view;
    }

    public View findView(@IdRes int id) {
        return view.findViewById(id);
    }

    public void dispose() {
        view = null;
    }
}
