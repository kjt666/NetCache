package wowo.kjt.netcachedemo;

import com.google.gson.annotations.SerializedName;

public class HttpResponse<T>{

    @SerializedName("code")
    public int code;
    @SerializedName("message")
    public String message;
    @SerializedName("msg")
    public String msg;
    @SerializedName(value = "data",alternate = {"user_data"})
    public T data;
    //data 是通用解析，user_data 用于userinfo/save接口的解析

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



    public boolean isSuccess(){
        return code==0?true:false;
    }

    @Override
    public String toString() {
        return " {"+code+"=code , message=" + message +",data="+data+"}";
    }
}
