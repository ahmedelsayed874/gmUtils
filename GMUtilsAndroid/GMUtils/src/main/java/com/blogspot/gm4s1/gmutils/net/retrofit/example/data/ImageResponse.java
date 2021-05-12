package com.blogspot.gm4s1.gmutils.net.retrofit.example.data;


import com.blogspot.gm4s1.gmutils.net.retrofit.responseHolders.BaseDataWrapperResponse;

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
/*
    it's not suitable to declare this class in this package which is dedicated to retrofit requests only
    but I added it here for demonstration purpose
 */
public class ImageResponse extends BaseDataWrapperResponse<Object> {
    @Override
    public void setData(Object data) {
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public Statuses getResponseStatus() {
        return null;
    }
}