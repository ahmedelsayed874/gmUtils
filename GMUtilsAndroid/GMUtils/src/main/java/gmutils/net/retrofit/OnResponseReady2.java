package gmutils.net.retrofit;

import gmutils.net.retrofit.responseHolders.BaseObjectResponse;

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
public interface OnResponseReady2<T, R extends BaseObjectResponse<T>> {
    void invoke(R response);
}
