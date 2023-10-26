package gmutils.net.retrofit.responseHolders;


import org.jetbrains.annotations.Nullable;

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

    public static <T> BaseObjectResponse<T> createInstance(Class<T> c) {

        BaseObjectResponse<T> response = new BaseObjectResponse<T>() {
            T d;

            @Override
            public void setData(T data) {
                d = data;
            }

            @Override
            public T getData() {
                return d;
            }

            @Override
            public Statuses getResponseStatus() {
                return d != null ? Statuses.Succeeded : Statuses.Error;
            }
        };

        return response;
    }

    //----------------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "BaseObjectResponse{" +
                "data='" + getData() + '\'' +
                ", _callbackStatus='" + getCallbackStatus() + '\'' +
                ", _code=" + _code +
                ", _error='" + _error + '\'' +
                ", _extras=" + _extras +
                ", _requestTime=" + _requestTime +
                ", _responseTime=" + _responseTime +
                '}';
    }
}
