package com.blogspot.gm4s1.gmutils.ui.utils;

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.viewbinding.ViewBinding;

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
