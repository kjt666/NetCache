package wowo.kjt.lib_netcache.util;

import android.text.TextUtils;

import java.security.MessageDigest;

public class MD5Util {

    public static final String encodeBy32BitMD5(String source) {
        return encrypt(source, false);
    }

    private static final String encrypt(String source, boolean is16bit) {
        if (TextUtils.isEmpty(source)) return "";

        String encryptedStr = null;
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            encryptedStr = convertToHexString(digester.digest(source.getBytes("utf-8")));
            if (is16bit) {
                encryptedStr = encryptedStr.substring(8, 24);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedStr;
    }

    private static final String convertToHexString(byte data[]) {
        int i;
        StringBuffer buf = new StringBuffer();
        for (int offset = 0; offset < data.length; offset++) {
            i = data[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }
        return buf.toString();
    }

}
