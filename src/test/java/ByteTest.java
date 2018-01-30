import utils.ByteUtils;

import java.io.*;
import java.nio.ByteBuffer;

public class ByteTest {

    public static void main(String[] args) throws IOException {
//        String s = "sign";
//        String y = "0";
//        int i = 1;
//        int j = 100000;
//        System.out.println(s.getBytes().length);
//        System.out.println("str 10 bytes:" + y.getBytes().length);
//        System.out.println("1 bytes: " + intToBytes(i).length);
//        System.out.println("10 bytes: " + intToBytes(j).length);
//        int x = bytesToInt(intToBytes(j));
//        System.out.println(x);
        int i = 1;
        byte[] b = ByteUtils.intToByteArr(i);
        System.out.println(b.length);
        System.out.println("Bytes: " + b);
        System.out.println(ByteUtils.byteArrToInt(b));
    }


}
