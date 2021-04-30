package wowo.kjt.lib_netcache.listener;

/**
 * <pre>
 * author : kjt
 * e-mail : kjt333@126.com
 * time : 2020/12/16
 * desc :
 * </pre>
 */
public interface INetCacheIoListener {

     void onProcessing(String fileName, float progress);

     void onInterrupted(String fileName);

     void onFail(String fileName, Exception e);

    void onComplete(String fileName, String result);
}
