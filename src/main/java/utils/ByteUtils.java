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

    public static void main(String[] args) {

        String s1 = "1234 ";
        String s2 = "56789";

        byte[] arr = new byte[10];
        ByteBuffer target = ByteBuffer.wrap(arr);

        target.put(s1.getBytes());
        target.put(s2.getBytes());

        System.out.println(byteArrToString(arr));
        System.out.println(arr.length);
        System.out.println(arr);
    }

}
