package udp;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import java.nio.ByteBuffer;
import java.util.Arrays;


public class FragmentPacket implements Comparable<FragmentPacket>{

    private static final int VERSION = 1;
    public static final int NONCE_LEN = 8;
    public static final int DATA_LEN = 200;
    private static final int DATA_KEY_LEN = 3; // ProtoBuf key length for bytes > 127

    public static final int HEADER_LEN = 30 + DATA_KEY_LEN; // 5+5+5+5+10 = 30
    public static final int PACKET_LEN = DATA_LEN + HEADER_LEN;

    private FragmentProto.Fragment fragment;

    private String address;
    private int port;

    /**
     * Creates new FP
     */
    public FragmentPacket(byte[] dataChunk, int index, int totalAmount, int lengthTotal, String nonce) {
        FragmentProto.Fragment.Builder builder = FragmentProto.Fragment.newBuilder();

        builder.setVersion(VERSION); // 5
        builder.setIndex(index); // 5
        builder.setAmount(totalAmount); // 5
        builder.setLengthTotal(lengthTotal); // 5
        builder.setNonce(nonce); // 10
        builder.setData(ByteString.copyFrom(dataChunk)); // 1024 + 3

        fragment = builder.build();
    }

    /**
     * Parses incoming FP
     */
    public FragmentPacket(byte[] mpBytes, String sourceAddress, int sourcePort) throws InvalidProtocolBufferException {
        fragment = FragmentProto.Fragment.parseFrom(mpBytes);
        this.address = sourceAddress;
        this.port = sourcePort;
    }


    public byte[] getBytes() {
        return fragment.toByteArray();
    }

    public byte[] getData() {
        return fragment.getData().toByteArray();
    }

    public int getLengthTotal() {
        return fragment.getLengthTotal();
    }

    public String getNonce() {
        return fragment.getNonce();
    }

    public int getIndex() {
        return fragment.getIndex();
    }

    public int getAmount() {
        return fragment.getAmount();
    }

    public static FragmentPacket[] splitMessage(GexMessage gm, String nonce) {
        byte[] gmBytes = gm.getBytes();

        int amountPackages = ((gmBytes.length) / DATA_LEN) + 1;
        int lengthTotal = gm.getBytes().length;

        FragmentPacket[] packets = new FragmentPacket[amountPackages];

        for (int i = 0; i < amountPackages; i++) {
            int from = i * DATA_LEN;
            int to = i * DATA_LEN + DATA_LEN;
            byte[] chunk = Arrays.copyOfRange(gmBytes, from, to);
            packets[i] = new FragmentPacket(chunk, i, amountPackages, lengthTotal, nonce);
        }


        return packets;
    }

    public static GexMessage assembleMessage(FragmentPacket[] packets) throws InvalidProtocolBufferException {
//        Arrays.sort(packets); // todo Comparable!

        int lengthTotal = packets[0].getLengthTotal();
        byte[] total = new byte[DATA_LEN * packets[0].getAmount()];
        ByteBuffer buffer = ByteBuffer.wrap(total);
        if(packets.length != 1){
            System.out.println(packets.length);
            String a = packets[0].getNonce();
            String b = packets[1].getNonce();
            GexMessage g1 = new GexMessage(Arrays.copyOfRange(packets[0].getData(), 0, lengthTotal));
            GexMessage g2 = new GexMessage(Arrays.copyOfRange(packets[1].getData(), 0, lengthTotal));
            System.out.println(a + " " + b );
            System.out.println(packets[0]);
            System.out.println(packets[1]);
        }
        for (int i = 0; i < packets.length; i++) {
            //todo
            if (i != packets[i].getIndex()) {
//                throw new InvalidProtocolBufferException(String.format("Packet Index Mismatch, %s != %s", i, packets[i].getIndex()));
                System.out.println(String.format("**************************** Packet Index Mismatch, %s != %s", i, packets[i].getIndex()));
            }
            buffer.put(packets[i].getData());
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
        return fragment.toString();
    }

    @Override
    public int compareTo(FragmentPacket fp) {
        return this.getIndex() - fp.getIndex();
    }
}
