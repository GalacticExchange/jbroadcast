package udp;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import utils.ByteUtils;

import java.time.Instant;
import java.util.Arrays;

public class SkaleMessage {

    public static final int NONCE_LEN = 8;
    private static final int COMMAND_LEN = 2;

    private static final int TIME_VAL_LEN = 4; // fixed32 always 4 bytes value
    private static final int MESSAGE_LEN = 200;
    private static final int MESSAGE_SIZE_LEN = 4; // fixed32 always 4 bytes value

    /**
     * proto string key = 2 bytes
     * proto fixed32 key = 1 byte
     * proto bytes key = 3 bytes if bytes lengths > 127
     * 2 + 2 + 1 + 1 + 3 = 9
     */
    private static final int KEYS_LEN = 9;

    public static final int PACKET_LEN = NONCE_LEN + COMMAND_LEN + TIME_VAL_LEN + MESSAGE_LEN +
            MESSAGE_SIZE_LEN + KEYS_LEN;

    private FragmentProto.SkaleMessage skaleMsg;
    private String message;

    public SkaleMessage(String message, String command, String nonce) {
        FragmentProto.SkaleMessage.Builder builder = FragmentProto.SkaleMessage.newBuilder();

        // message field should be always 200 bytes.
        // filling in zero bytes here
        builder.setMessage(ByteString.copyFrom(getBufferedMessage(message.getBytes())));

        builder.setMessageLength(message.getBytes().length);

        builder.setCommand(command);
        builder.setNonce(nonce);
        int sendTime = (int) Instant.now().getEpochSecond();
        builder.setSendTime(sendTime);
        skaleMsg = builder.build();
        setMessage(message);
    }


    public SkaleMessage(FragmentProto.SkaleMessage skaleMsg) {
        this.skaleMsg = skaleMsg;
        setMessage(skaleMessageString(skaleMsg));
    }

    private byte[] getBufferedMessage(byte[] msg) {
        if (msg.length > MESSAGE_LEN) {
            throw new ArrayIndexOutOfBoundsException("Message is to big");
        }
        return Arrays.copyOfRange(msg, 0, MESSAGE_LEN);
//        return msg;
    }

    private void setSkaleMsg(FragmentProto.SkaleMessage skaleMsg) {
        this.skaleMsg = skaleMsg;
    }

    public byte[] getBytes() {
        return skaleMsg.toByteArray();
    }

    public byte[] getMessageData() {
        return skaleMsg.getMessage().toByteArray();
    }

    public String getCommand() {
        return skaleMsg.getCommand();
    }

    public String getNonce() {
        return skaleMsg.getNonce();
    }

    //todo check
    public String getMessage() {
//        byte[] bMsg = skaleMsg.getMessage().toByteArray();
//        bMsg = Arrays.copyOfRange(bMsg, 0, skaleMsg.getMessageLength());
//        return ByteUtils.byteArrToString(bMsg);
        return message;

    }

    private void setMessage(String message) {
        this.message = message;
    }

    private static String skaleMessageString(FragmentProto.SkaleMessage skaleMsg) {
        byte[] bMsg = skaleMsg.getMessage().toByteArray();
        bMsg = Arrays.copyOfRange(bMsg, 0, skaleMsg.getMessageLength());
        return ByteUtils.byteArrToString(bMsg);
    }


    public int getMessageLength() {
        return skaleMsg.getMessageLength();
    }

    @Override
    public String toString() {
        return skaleMsg.toString();
    }

    public static SkaleMessage parse(byte[] msg) throws InvalidProtocolBufferException {
        FragmentProto.SkaleMessage skaleMsg = FragmentProto.SkaleMessage.parseFrom(msg);
        return new SkaleMessage(skaleMsg);
    }

    public FragmentProto.SkaleMessage getProtoObject() {
        return skaleMsg;
    }

    public String getSendTime() {
        return Instant.ofEpochSecond(skaleMsg.getSendTime()).toString();
    }

}
