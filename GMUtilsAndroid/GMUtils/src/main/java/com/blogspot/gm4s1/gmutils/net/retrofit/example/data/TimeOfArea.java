package com.blogspot.gm4s1.gmutils.net.retrofit.example.data;

import android.text.TextUtils;

import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer with (Java/Kotlin, Swift)
 * Have experience with:
 *      - (C/C++, C#) languages
 *      - .NET environment
 *      - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
/*
    it's not suitable to declare this class in this package which is dedicated to retrofit requests only
    but I added it here for demonstration purpose
 */
public class TimeOfArea extends BaseResponse {

    @SerializedName("datetime")
    @Expose
    private String datetime;

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDatetime() {
        return datetime;
    }

    @Override
    public Statuses getResponseStatus() {
        return TextUtils.isEmpty(datetime) ? Statuses.Error : Statuses.Succeeded;
    }
}
