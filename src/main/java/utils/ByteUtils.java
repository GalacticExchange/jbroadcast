package utils;

import java.nio.ByteBuffer;

public class ByteUtils {

    public static String byteArrToString(byte[] arr) {
        return new String(arr, 0, arr.length);
    }


    public static byte[] intToByteArr(int i) {
        return ByteBuffer.allocate(4).putInt(i).array();
    }

    public static int byteArrToInt(byte[] arr) {
        return ByteBuffer.wrap(arr).getInt();
    }


}
