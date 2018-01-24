import java.io.*;

public class ByteTest {

    public static void main(String[] args) throws IOException {
        String s = "sign";
        String y = "0";
        int i = 1;
        int j = 100000;
        System.out.println(s.getBytes().length);
        System.out.println("str 10 bytes:" + y.getBytes().length);
        System.out.println("1 bytes: " + intToBytes(i).length);
        System.out.println("10 bytes: " + intToBytes(j).length);
//        String x = new String (intToBytes(j), 0, intToBytes(j).length);
        int x = bytesToInt(intToBytes(j));
        System.out.println(x);
    }

    public static byte[] intToBytes(int my_int) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeInt(my_int);
        out.close();
        byte[] int_bytes = bos.toByteArray();
        bos.close();
        return int_bytes;
    }

    public static int bytesToInt(byte[] int_bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(int_bytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        int my_int = ois.readInt();
        ois.close();
        return my_int;
    }

}
