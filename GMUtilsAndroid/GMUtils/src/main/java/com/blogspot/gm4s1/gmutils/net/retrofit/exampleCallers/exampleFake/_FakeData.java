package com.blogspot.gm4s1.gmutils.net.retrofit.exampleCallers.exampleFake;

import android.os.Handler;
import android.os.Looper;

import com.blogspot.gm4s1.gmutils.listeners.ResultCallback;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeOfArea;
import com.blogspot.gm4s1.gmutils.net.retrofit.exampleAPIRequestes.TimeZones;

import java.util.Random;

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

public class _FakeData {

    public void run(ResultCallback<Boolean> res) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            int r = new Random().nextInt(10);
            if (r == 0) res.invoke(null);
            else if (r == 1) res.invoke(false);
            else res.invoke(true);

        }, 1500);

    }

    public static _FakeData instance() {
        return new _FakeData();
    }

    public static final long delay = 1200;

    public static int randomNumber() {
        Random r = new Random();
        return r.nextInt(10);
    }

    private _FakeData() {
    }


    public TimeZones timeZones() {
        TimeZones t = new TimeZones();
        t.add("Africa/Cairo");
        return t;
    }

    public TimeOfArea timeOfArea() {
        TimeOfArea t = new TimeOfArea();
        t.setDatetime("2020-10-20T01:00:00.123456+02:00");
        return t;
    }
}
