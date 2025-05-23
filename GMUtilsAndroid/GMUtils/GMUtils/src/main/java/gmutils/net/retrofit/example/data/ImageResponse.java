package gmutils.net.retrofit.example.data;


import gmutils.net.retrofit.responseHolders.BaseObjectResponse;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - Java swing
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */
/*
    it's not suitable to declare this class in this package which is dedicated to retrofit requests only
    but I added it here for demonstration purpose
 */
public class ImageResponse extends BaseObjectResponse<Object> {
    @Override
    public void setData(Object data) {
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public Status getResponseStatus() {
        return null;
    }
}