package udp;

import com.google.protobuf.InvalidProtocolBufferException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class GexMessage {
    private FragmentProto.GexMessage gexMessage;
    public static final int NONCE_LEN = 8;

    /**
     * Creates new GexMessage
     */
    public GexMessage(String message, String command, String nonce) {
        FragmentProto.GexMessage.Builder builder = FragmentProto.GexMessage.newBuilder();
        builder.setMessage(message);
        builder.setCommand(command);
        builder.setNonce(nonce);
        builder.setSendTime(Instant.now().toString());
        gexMessage = builder.build();
    }

    /**
     * Creates new GexMessage with signs
     */
    public GexMessage(String message, String command, String nonce, String sendTime, HashMap<String, String> signs) {
        FragmentProto.GexMessage.Builder builder = FragmentProto.GexMessage.newBuilder();
        builder.setMessage(message);
        builder.setCommand(command);
        builder.setNonce(nonce);
        builder.setSendTime(sendTime);
        builder.putAllSigns(signs);
        gexMessage = builder.build();
    }


    /**
     * Parses GexMessage
     */
    public GexMessage(byte[] msg) throws InvalidProtocolBufferException {
        gexMessage = FragmentProto.GexMessage.parseFrom(msg);
    }

    public String toString() {
        return gexMessage.toString();
    }

    public String getCommand() {
        return gexMessage.getCommand();
    }

    public String getMessage() {
        return gexMessage.getMessage();
    }

    public byte[] getBytes() {
        return gexMessage.toByteArray();
    }

    public String getNonce() {
        return gexMessage.getNonce();
    }

    public Map<String, String> getSigns() {
        return gexMessage.getSignsMap();
    }

    public String getSign(String key) {
        return gexMessage.getSignsOrThrow(key);
    }

    public String getSendTime() {
        return gexMessage.getSendTime();
    }

//    public static void main(String[] args) {
////        String strDate = new Date().toString();
////        System.out.println(strDate);
//        String strDate = Instant.now().toString();
//        System.out.println(strDate);
//        System.out.println(Instant.parse(strDate).getEpochSecond());
//
//
//        System.out.println(Instant.now().getEpochSecond());
//        Instant.parse(Instant.now().toString());
//    }


}
