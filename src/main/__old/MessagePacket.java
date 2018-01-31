package udp.__old;

import java.nio.ByteBuffer;
import java.util.Arrays;

import utils.ByteUtils;

public class MessagePacket implements Comparable<MessagePacket> {


    //        public static final int PACKET_LEN = 1024;
    public static final int PACKET_LEN = 32;

    private static final int VERSION_LEN = 1;
    private static final int INDEX_LEN = 4;
    private static final int AMOUNT_LEN = 4;
    public static final int NONCE_LEN = 8;
    private static final int COMMAND_LEN = 2;

    private static final int HEADER_LEN = VERSION_LEN + INDEX_LEN + AMOUNT_LEN + NONCE_LEN + COMMAND_LEN; // 19

    private static final int DATA_LEN = PACKET_LEN - HEADER_LEN;

    private byte[] version;
    private byte[] index;
    private byte[] amount;
    private byte[] nonce;
    private byte[] command;
    private byte[] data;


//    private HashMap<String, byte[]> header; //iterate over header fields ?


    /**
     * @param msg - [index,amount,nonce,command,data] byte message
     *            <p>
     *            Parses incoming message
     */
    public MessagePacket(byte[] msg) throws Exception {
        checkLength(msg);
        parseData(msg);
        parseHeader(msg);
    }


    /**
     * Creates message packet from raw byte chunk of
     * message and header info
     */
//    public MessagePacket(byte[] msg, int index, int totalAmount, String nonce, String command) throws Exception {
//        checkLength(msg); // TODO it should check DATA_LEN
//        this.data = msg;
//        createHeader(index, totalAmount, nonce, command);
//    }
    public MessagePacket(byte[] data, int index, int totalAmount, byte[] nonce, String command) throws Exception {
        checkLength(data); // TODO it should check DATA_LEN
        this.data = data;
        createHeader(index, totalAmount, nonce, command);
    }

    //    private void createHeader(int index, int totalAmount, String nonce, String command) {
//        this.index = fillLack(String.valueOf(index), 2).getBytes();
//        this.amount = fillLack(String.valueOf(totalAmount), 2).getBytes();
//        this.nonce = String.valueOf(nonce).getBytes();
//        this.command = command.getBytes();
//    }
    private void createHeader(int index, int totalAmount, byte[] nonce, String command) {
//        this.index = fillLack(String.valueOf(index), 2).getBytes();
//        this.amount = fillLack(String.valueOf(totalAmount), 2).getBytes();
        this.version = "1".getBytes(); // TODO
        this.index = ByteUtils.intToByteArr(index);
        this.amount = ByteUtils.intToByteArr(totalAmount);
        this.nonce = nonce;
        this.command = command.getBytes();
    }


    private void parseHeader(byte[] bMsg) {

        // TODO use hashMap<String, byte[]> header ?
        version = Arrays.copyOfRange(bMsg, 0, VERSION_LEN);
        index = Arrays.copyOfRange(bMsg, VERSION_LEN, VERSION_LEN + INDEX_LEN);
        amount = Arrays.copyOfRange(bMsg, VERSION_LEN + INDEX_LEN, VERSION_LEN + INDEX_LEN + AMOUNT_LEN);
        nonce = Arrays.copyOfRange(bMsg, VERSION_LEN + INDEX_LEN + AMOUNT_LEN, VERSION_LEN + INDEX_LEN + AMOUNT_LEN + NONCE_LEN);
        command = Arrays.copyOfRange(bMsg, VERSION_LEN + INDEX_LEN + AMOUNT_LEN + NONCE_LEN,
                VERSION_LEN + INDEX_LEN + AMOUNT_LEN + NONCE_LEN + COMMAND_LEN);

    }

    private void parseData(byte[] bMsg) {
        data = Arrays.copyOfRange(bMsg, HEADER_LEN, PACKET_LEN);
    }

    private void checkLength(byte[] msg) throws Exception {
        if (msg.length > PACKET_LEN) {
            throw new Exception("Message is too big.");
        }
    }

    public String toString() {
        return ByteUtils.byteArrToString(version) + ByteUtils.byteArrToInt(index) + " " + ByteUtils.byteArrToInt(amount) + " " + ByteUtils.byteArrToString(nonce)
                + " " + ByteUtils.byteArrToString(command) + " " + ByteUtils.byteArrToString(data);
    }


//    public static String byteArrToString(byte[] arr) {
//        return new String(arr, 0, arr.length);
//    }

    public byte[] getBytes() {
        // todo check!
        byte[] total = new byte[PACKET_LEN];
        ByteBuffer buffer = ByteBuffer.wrap(total);

        buffer.put(version);
        buffer.put(index);
        buffer.put(amount);
        buffer.put(nonce);
        buffer.put(command);
        buffer.put(data);

//        byte[] indexAmount = ArrayUtils.addAll(index, amount);
//        byte[] indexAmountNonce = ArrayUtils.addAll(indexAmount, nonce);
//        byte[] indexAmountNonceCommand = ArrayUtils.addAll(indexAmountNonce, command);
//        byte[] total = ArrayUtils.addAll(indexAmountNonceCommand, data);

        return total;
    }

    private String fillLack(String str, int size) {

        int diff = size - str.length();
        if (diff == 0) {
            return str;
        }

        String lack = new String(new char[diff]).replace("\0", "0");
        str = lack + str;

        return str;
    }


    public byte[] getNonce() {
        return nonce;
    }

    public int getNonceHashCode() {
        return Arrays.hashCode(nonce);
    }

    public int getAmount() {
        return ByteUtils.byteArrToInt(amount);
    }

    public int getIndex() {
        return ByteUtils.byteArrToInt(index);
    }

    public String getCommand() {
        return ByteUtils.byteArrToString(command);
    }

    public String getData() {
        // todo don't trim -> add new field message_length?
        return ByteUtils.byteArrToString(trim(data));
    }

//    public static MessagePacket[] splitMessage(String msg, String nonce, String command) throws Exception {
//        byte[] bMsg = msg.getBytes();
//        int amountPackages = ((bMsg.length) / DATA_LEN) + 1;
//        MessagePacket[] packets = new MessagePacket[amountPackages];
//
//        for (int i = 0; i < amountPackages; i++) {
//            int from = i * DATA_LEN;
//            int to = i * DATA_LEN + DATA_LEN;
//            byte[] chunk = Arrays.copyOfRange(bMsg, from, to);
//            packets[i] = new MessagePacket(chunk, i, amountPackages, nonce, command);
//        }
//
//
//        return packets;
//    }

    public static MessagePacket[] splitMessage(String msg, byte[] nonce, String command) throws Exception {
        byte[] bMsg = msg.getBytes();
        int amountPackages = ((bMsg.length) / DATA_LEN) + 1;
        MessagePacket[] packets = new MessagePacket[amountPackages];

        for (int i = 0; i < amountPackages; i++) {
            int from = i * DATA_LEN;
            int to = i * DATA_LEN + DATA_LEN;
            byte[] chunk = Arrays.copyOfRange(bMsg, from, to);
            packets[i] = new MessagePacket(chunk, i, amountPackages, nonce, command);
        }


        return packets;
    }

    static byte[] trim(byte[] bytes) {
        int i = bytes.length - 1;
        while (i >= 0 && bytes[i] == 0) {
            --i;
        }

        return Arrays.copyOf(bytes, i + 1);
    }

    public static String assembleMessage(MessagePacket[] packets) throws Exception {
        Arrays.sort(packets);
        StringBuilder msg = new StringBuilder();
        for (int i = 0; i < packets.length; i++) {
            if (i != packets[i].getIndex()) {
                throw new Exception(String.format("Packet Index Mismatch, %s != %s", i, packets[i].getIndex()));
            }
            msg.append(packets[i].getData());
        }

        return String.valueOf(msg);
    }


//    public static void main(String[] args) throws Exception {
//
////        String msg = "Hello world!";
//        String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eget odio eu elit rhoncus consequat ut ut quam.";
//        String nonce = "qt8l";
//
//        String cmd = "sg";
//        MessagePacket[] packets = splitMessage(msg, nonce, cmd);
//
//        MessagePacket[] parsed = new MessagePacket[packets.length];
//
//        int i = 0;
//        for (MessagePacket mp : packets) {
//            parsed[i] = new MessagePacket(mp.getBytes());
//            i++;
//        }
//
//        String parsedMsg = assembleMessage(parsed);
//        System.out.println(parsedMsg);
//
//    }


    @Override
    public int compareTo(MessagePacket messagePacket) {
        //ascending order
        return this.getIndex() - messagePacket.getIndex();
    }
}
