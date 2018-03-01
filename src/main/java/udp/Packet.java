package udp;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class Packet implements Comparable<Packet> {

    private static final int VERSION = 1;
    public static final int NONCE_LEN = 8;
    private static final int DATA_LEN = 1024;
    private static final int DATA_KEY_LEN = 3; // ProtoBuf key length for bytes > 127
//    private static final int DATA_KEY_LEN = 2; // ProtoBuf key length for bytes < 127

    public static final int HEADER_LEN = 30 + DATA_KEY_LEN; // 5+5+5+5+10 = 30
    public static final int PACKET_LEN = DATA_LEN + HEADER_LEN;

    private FragmentProto.Fragment packetProto;

    private String address;
    private int port;

    /**
     * Creates new Packet
     */
    public Packet(byte[] dataChunk, int index, int totalAmount, int lengthTotal, String nonce) {
        FragmentProto.Fragment.Builder builder = FragmentProto.Fragment.newBuilder();

        builder.setVersion(VERSION); // 5
        builder.setIndex(index); // 5
        builder.setAmount(totalAmount); // 5
        builder.setLengthTotal(lengthTotal); // 5
        builder.setNonce(nonce); // 10 (8+2)
        builder.setData(ByteString.copyFrom(dataChunk)); // 1024 + 3

        packetProto = builder.build();
    }

    /**
     * Parses incoming Packet
     */
    public Packet(byte[] mpBytes, String sourceAddress, int sourcePort) throws InvalidProtocolBufferException {
        packetProto = FragmentProto.Fragment.parseFrom(mpBytes);
        this.address = sourceAddress;
        this.port = sourcePort;
    }


    public byte[] getBytes() {
        return packetProto.toByteArray();
    }

    public byte[] getRawData() {
        return packetProto.getData().toByteArray();
    }

    public byte[] getData() {
        return Arrays.copyOfRange(getRawData(), 0, getLengthTotal());
    }

    public int getLengthTotal() {
        return packetProto.getLengthTotal();
    }

    public String getNonce() {
        return packetProto.getNonce();
    }

    public int getIndex() {
        return packetProto.getIndex();
    }

    public int getAmount() {
        return packetProto.getAmount();
    }

    public static Packet[] splitMessage(GexMessage gm, String nonce) {
        byte[] gmBytes = gm.getBytes();

        int amountPackages = ((gmBytes.length) / DATA_LEN) + 1;
        int lengthTotal = gm.getBytes().length;

        Packet[] packets = new Packet[amountPackages];

        for (int i = 0; i < amountPackages; i++) {
            int from = i * DATA_LEN;
            int to = i * DATA_LEN + DATA_LEN;
            byte[] chunk = Arrays.copyOfRange(gmBytes, from, to);
            packets[i] = new Packet(chunk, i, amountPackages, lengthTotal, nonce);
        }


        return packets;
    }

    public static Packet batchToPacket(BatchMessages batch, String nonce) {
        byte[] batchBytes = batch.toByteArray();
        int amountPackages = 0; // todo
        int lengthTotal = batchBytes.length;
        byte[] chunk = Arrays.copyOfRange(batchBytes, 0, DATA_LEN);

        return new Packet(chunk, 0, amountPackages, lengthTotal, nonce);

    }


    public static GexMessage assembleMessage(Packet[] packets) throws InvalidProtocolBufferException {
//        Arrays.sort(packets); // todo Comparable!

        int lengthTotal = packets[0].getLengthTotal();
        byte[] total = new byte[DATA_LEN * packets[0].getAmount()];
        ByteBuffer buffer = ByteBuffer.wrap(total);
        if (packets.length != 1) {
            System.out.println(packets.length);
            String a = packets[0].getNonce();
            String b = packets[1].getNonce();
//            GexMessage g1 = new GexMessage(Arrays.copyOfRange(packets[0].getMessageData(), 0, lengthTotal));
//            GexMessage g2 = new GexMessage(Arrays.copyOfRange(packets[1].getMessageData(), 0, lengthTotal));
            System.out.println(a + " " + b);
            System.out.println(packets[0]);
            System.out.println(packets[1]);
        }
        for (int i = 0; i < packets.length; i++) {
            //todo
            if (i != packets[i].getIndex()) {
//                throw new InvalidProtocolBufferException(String.format("Packet Index Mismatch, %s != %s", i, packets[i].getIndex()));
                System.out.println(String.format("**************************** Packet Index Mismatch, %s != %s", i, packets[i].getIndex()));
            }
            buffer.put(packets[i].getRawData());
        }
        byte[] assembled = Arrays.copyOfRange(total, 0, lengthTotal);
        return new GexMessage(assembled);
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return packetProto.toString();
    }

    @Override
    public int compareTo(Packet fp) {
        return this.getIndex() - fp.getIndex();
    }
}
