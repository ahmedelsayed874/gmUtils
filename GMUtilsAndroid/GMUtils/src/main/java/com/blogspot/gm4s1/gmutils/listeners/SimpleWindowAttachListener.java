package com.blogspot.gm4s1.gmutils.listeners;

import android.os.Build;
import android.view.ViewTreeObserver;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class SimpleWindowAttachListener implements ViewTreeObserver.OnWindowAttachListener {
    @Override
    public void onWindowAttached() {
    }

    @Override
    public void onWindowDetached() {
    }
}
