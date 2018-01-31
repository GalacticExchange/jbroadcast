package udp;

import com.google.protobuf.InvalidProtocolBufferException;
import java.util.ArrayList;

public class GexMessage {
    private FramePacketProto.GexMessage gexMessage;

    /**
     * Creates new gexMessage
     */
    public GexMessage(String message) {
        FramePacketProto.GexMessage.Builder builder = FramePacketProto.GexMessage.newBuilder();
        builder.setMessage(message);
        gexMessage = builder.build();
    }

    public GexMessage(String message, ArrayList<String> signs) {
        FramePacketProto.GexMessage.Builder builder = FramePacketProto.GexMessage.newBuilder();
        builder.setMessage(message);
        builder.addAllSigns(signs);
        gexMessage = builder.build();
    }


    /**
     * Parses GexMessage
     */
    public GexMessage(byte[] msg) throws InvalidProtocolBufferException {
        gexMessage = FramePacketProto.GexMessage.parseFrom(msg);
    }

    public String toString(){
        return gexMessage.toString();
    }

    public byte[] getBytes() {
        return gexMessage.toByteArray();
    }



}
