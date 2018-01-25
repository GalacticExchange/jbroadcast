package udp;


import java.util.Arrays;


public class GexMessage {

    public static final int MESSAGE_LENGTH = 1024;
    public static final int HEADER_LENGTH = 8;

    public static String parsePacket(byte[] msg) {

        return "";
    }


    public static String parseHeader(byte[] msg) {
        byte[] index = Arrays.copyOfRange(msg, 0, 2);
        byte[] length = Arrays.copyOfRange(msg, 2, 4);
        byte[] nonce = Arrays.copyOfRange(msg, 4, 8);

        String strIndex = byteArrToString(index);
        String strLength = byteArrToString(length);
        String strNonce = byteArrToString(nonce);
//        for (int i = 0; i < HEADER_LENGTH; i++){
//
//        }

        return "Index:" + strIndex + "; Length:" + strLength + "; Nonce:" + strNonce;
    }

    public static String byteArrToString(byte[] arr) {
        return new String(arr, 0, arr.length);
    }

    // TODO split message to packets (to be able iteratively send them)
    public static byte[] messageToPacket(String msg) {
        byte[] bMsg = msg.getBytes();

        String index = "01"; // TODO

        String length = String.valueOf(bMsg.length / MESSAGE_LENGTH);
        if (length.length() < 2) {
            length = "0" + length;
        }
        String nonce = "zcq4";

        String sPacket = index + length + nonce + msg;

        return sPacket.getBytes();
    }

    public static String messageLength(){
        return "";
    }

    public static void main(String[] args) {
        String sMsg = "hello world!";
//        byte[] bMsg = sMsg.getBytes();
        byte[] bMsg = messageToPacket(sMsg);

        System.out.println(parseHeader(bMsg));
    }
}
