package udp;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.ArrayList;
import java.util.List;

public class GexMessage {
    private FragmentProto.GexMessage gexMessage;
    public static final int NONCE_LEN = 8;

    /**
     * Creates new gexMessage
     */
    public GexMessage(String message, String command, String nonce) {
        FragmentProto.GexMessage.Builder builder = FragmentProto.GexMessage.newBuilder();
        builder.setMessage(message);
        builder.setCommand(command);
        builder.setNonce(nonce);
        gexMessage = builder.build();
    }

    public GexMessage(String message, String command, String nonce, ArrayList<String> signs) {
        FragmentProto.GexMessage.Builder builder = FragmentProto.GexMessage.newBuilder();
        builder.setMessage(message);
        builder.setCommand(command);
        builder.setNonce(nonce);
        builder.addAllSigns(signs);
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

    public List<String> getSigns() {
        return gexMessage.getSignsList();
    }

    public String getSign(int index){
        return gexMessage.getSigns(index);
    }


}
