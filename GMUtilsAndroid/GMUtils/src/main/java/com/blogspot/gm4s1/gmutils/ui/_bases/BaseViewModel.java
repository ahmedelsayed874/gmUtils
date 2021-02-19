package com.blogspot.gm4s1.gmutils.ui._bases;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;


/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
public class BaseViewModel extends AndroidViewModel {

    public BaseViewModel(@NotNull Application application) {
        super(application);

    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

}