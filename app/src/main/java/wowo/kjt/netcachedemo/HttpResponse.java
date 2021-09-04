package wowo.kjt.netcachedemo;

import com.google.gson.annotations.SerializedName;

public class HttpResponse<T> {

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("data")
    public T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return code == 200 ? true : false;
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
