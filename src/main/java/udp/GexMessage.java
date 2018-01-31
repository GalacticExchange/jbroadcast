package udp;

import com.google.protobuf.InvalidProtocolBufferException;
import java.util.ArrayList;

public class GexMessage {
    private FragmentProto.GexMessage gexMessage;

    /**
     * Creates new gexMessage
     */
    public GexMessage(String message) {
        FragmentProto.GexMessage.Builder builder = FragmentProto.GexMessage.newBuilder();
        builder.setMessage(message);
        gexMessage = builder.build();
    }

    public GexMessage(String message, ArrayList<String> signs) {
        FragmentProto.GexMessage.Builder builder = FragmentProto.GexMessage.newBuilder();
        builder.setMessage(message);
        builder.addAllSigns(signs);
        gexMessage = builder.build();
    }


    /**
     * Parses GexMessage
     */
    public GexMessage(byte[] msg) throws InvalidProtocolBufferException {
        gexMessage = FragmentProto.GexMessage.parseFrom(msg);
    }

    public String toString(){
        return gexMessage.toString();
    }

    public byte[] getBytes() {
        return gexMessage.toByteArray();
    }



}
