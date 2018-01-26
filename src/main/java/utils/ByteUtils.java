package utils;

public class ByteUtils {

    public static String byteArrToString(byte[] arr) {
        return new String(arr, 0, arr.length);
    }

}
