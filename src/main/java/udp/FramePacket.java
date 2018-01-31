package udp;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class FramePacket {

    public static final int DATA_LEN = 1024;
    private static final int DATA_KEY_LEN = 3; // protobuf key len for bytes > 127
    public static final int HEADER_LEN = 34 + DATA_KEY_LEN; //todo 5+5+5+5+4+10
    private static final int VERSION = 1;
    public static final int NONCE_LEN = 8;
    public static final int PACKET_LEN = DATA_LEN + HEADER_LEN; //todo check

    private FramePacketProto.Frame frame;

    /**
     * Creates new FP
     */
    public FramePacket(byte[] dataChunk, int index, int totalAmount, int lengthTotal, byte[] nonce, String command) {
        FramePacketProto.Frame.Builder builder = FramePacketProto.Frame.newBuilder();
        ByteString cmd = ByteString.copyFrom(command.getBytes());

        builder.setVersion(VERSION); // 7
        builder.setIndex(index); // 7
        builder.setAmount(totalAmount); // 7
        builder.setLengthTotal(lengthTotal); // 7
        builder.setCommand(cmd); // 2
        builder.setNonce(ByteString.copyFrom(nonce)); // 8
        builder.setData(ByteString.copyFrom(dataChunk)); // 1024

        frame = builder.build();
    }

    /**
     * Parses incoming FP
     */
    public FramePacket(byte[] mpBytes) throws InvalidProtocolBufferException {
        frame = FramePacketProto.Frame.parseFrom(mpBytes);
    }


    public byte[] getBytes() {
        return frame.toByteArray();
    }

    public byte[] getData() {
        return frame.getData().toByteArray();
    }

    public int getLengthTotal() {
        return frame.getLengthTotal();
    }

    public int getNonceHashCode() {
        return Arrays.hashCode(frame.getNonce().toByteArray());
    }

    public int getIndex() {
        return frame.getIndex();
    }

    public int getAmount() {
        return frame.getAmount();
    }

    public String getCommand() {
        return frame.getCommand().toStringUtf8();
    }

    public FramePacketProto.Frame getFrame() {
        return frame;
    }

    public static FramePacket[] splitMessage(GexMessage gm, byte[] nonce, String command) {
        byte[] gmBytes = gm.getBytes();

        int amountPackages = ((gmBytes.length) / DATA_LEN) + 1;
        int lengthTotal = gm.getBytes().length;

        FramePacket[] packets = new FramePacket[amountPackages];

        for (int i = 0; i < amountPackages; i++) {
            int from = i * DATA_LEN;
            int to = i * DATA_LEN + DATA_LEN;
            byte[] chunk = Arrays.copyOfRange(gmBytes, from, to);
            packets[i] = new FramePacket(chunk, i, amountPackages, lengthTotal, nonce, command);
        }


        return packets;
    }

    public static GexMessage assembleMessage(FramePacket[] packets) throws InvalidProtocolBufferException {
        Arrays.sort(packets);

        int length = packets[0].getLengthTotal();
        byte[] total = new byte[1024];
        ByteBuffer buffer = ByteBuffer.wrap(total);

        for (int i = 0; i < packets.length; i++) {
            //todo
//            if (i != packets[i].getIndex()) {
//                throw new Exception(String.format("Packet Index Mismatch, %s != %s", i, packets[i].getIndex()));
//            }
            System.out.println(packets[i].getData().length);
            buffer.put(packets[i].getData());
        }
        byte[] x = Arrays.copyOfRange(total, 0, length);
        return new GexMessage(x);
    }

    //1061
    public static void main(String[] args) throws NoSuchAlgorithmException {
        byte[] NONCE = RandomGenerator.generateByteArray(FramePacket.NONCE_LEN);

        GexMessage gm = new GexMessage("test");
        FramePacket[] fPackets = FramePacket.splitMessage(gm, NONCE, "sg");

        for (FramePacket fp : fPackets) {
            System.out.println(fp.getBytes().length);
            System.out.println();
            System.out.println(fp.getFrame());

            System.out.println();

            System.out.println("version length 4=todo");
            System.out.println("index length 4=todo");
            System.out.println("amount length 4=todo");
            System.out.println("lengthTotal length 4=todo");
            System.out.println("command length 2=" + fp.getFrame().getCommand().toByteArray().length); //todo
            System.out.println("nonce length 8=" + fp.getFrame().getNonce().toByteArray().length);
            System.out.println("data length 1024=" + fp.getFrame().getData().toByteArray().length);

            System.out.println();

        }

    }


}
