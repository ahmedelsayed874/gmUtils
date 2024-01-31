package gmutils.net.retrofit.responseHolders;


import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import gmutils.listeners.ActionCallback;

/**
 * Created by Ahmed El-Sayed (Glory Maker)
 * Computer Engineer / 2012
 * Android/iOS Developer (Java/Kotlin, Swift) also Flutter (Dart)
 * Have precedent experience with:
 * - (C/C++, C#) languages
 * - .NET environment
 * - AVR Microcontrollers
 * a.elsayedabdo@gmail.com
 * +201022663988
 */

/*
    it's suitable for response of this format
    (This's a standard response: status, message and data are fixed keys)::
    {
        "status" : "success",
        "message" : "",
        "data" : {
            "key1" : "value1",
            "key2" : "value2",
            "key3" : "value3",
        }
    }

    "data" can be any type: (string, json object or json array
    I suppose to inherit it this class (see the example)::

    class MyResponse<T> extends Response<T> {
        String status;
        String message;
        T data;

        @Override
        public T getData() {
            return data;
        }

        @Override
        public void setData(T data) {
            this.data = data;
        }

        @Override
        public Statuses getInternalStatus() {
            //return getData() != null ? Statuses.Succeeded : Statuses.Error;
            // or
            //return "succeeded".equalsIgnoreCase(status) ? Statuses.Succeeded : Statuses.Error;
            // or
            //return "true".equalsIgnoreCase(status) ? Statuses.Succeeded : Statuses.Error;
            // or
            return "200".equalsIgnoreCase(status) ? Statuses.Succeeded : Statuses.Error;
            // or whatever you agree with your team
        }
    }
    class XData {
        String key1;
        String key2;
        String key3;
    }
    //to use later
    class Example {
        void main() {
            MyResponse<XData> response = new MyResponse<>();
            print(response.getData().key1);
            print(response.getData().key2);
        }
    }
*/

public abstract class BaseObjectResponse<T> extends BaseResponse {

    public abstract void setData(@Nullable T data);

    @Nullable
    public abstract T getData();

    //----------------------------------------------------------------------------------------------

    @Override
    public void copyFrom(@NonNull BaseResponse otherResponse) {
        super.copyFrom(otherResponse);
            if (otherResponse instanceof BaseObjectResponse) {
                try {
                    Object data = ((BaseObjectResponse) otherResponse).getData();
                    this.setData((T) data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
    }

    public <To> void copyFrom(@NonNull BaseObjectResponse<To> otherResponse, ActionCallback<To, T> dataConverter) {
        super.copyFrom(otherResponse);

        if (dataConverter != null) {
            T data = dataConverter.invoke(otherResponse.getData());
            this.setData(data);
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return "BaseObjectResponse{" + "\n" +
                "data='" + getData() + '\'' + ",\n" +
                "callbackStatus='" + getCallbackStatus() + '\'' + ",\n" +
                "code=" + _code + ",\n" +
                "error='" + _error + '\'' + ",\n" +
                "extras=" + _extras + ",\n" +
                "requestTime=" + _requestTime + ",\n" +
                "responseTime=" + _responseTime + "\n" +
                '}';
    }
}
