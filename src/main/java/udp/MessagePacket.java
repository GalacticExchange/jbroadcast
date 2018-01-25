package udp;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

public class MessagePacket implements Comparable<MessagePacket> {


//        public static final int MESSAGE_LENGTH = 1024;
    public static final int MESSAGE_LENGTH = 16;
    public static final int HEADER_LENGTH = 10;
    public static final int DATA_LENGTH = MESSAGE_LENGTH - HEADER_LENGTH;
    public static final int NONCE_LENGTH = 4;


    //    public static final int HEADER_LENGTH = 8;
    public static final int COMMAND_LENGTH = 2;

    private byte[] index;
    private byte[] amount;
    private byte[] nonce;
    private byte[] command;
    private byte[] data;


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
    public MessagePacket(byte[] msg, int index, int totalAmount, String nonce, String command) throws Exception {
        checkLength(msg); // TODO it should check DATA_LENGTH
        this.data = msg;
        createHeader(index, totalAmount, nonce, command);
    }

    private void createHeader(int index, int totalAmount, String nonce, String command) {
        this.index = fillLack(String.valueOf(index), 2).getBytes();
        this.amount = fillLack(String.valueOf(totalAmount), 2).getBytes();
        this.nonce = String.valueOf(nonce).getBytes();
        this.command = command.getBytes();
    }


    private void parseHeader(byte[] bMsg) {
        // TODO use ${FIELD}_LENGTH logic instead of hardcoded positions
        index = Arrays.copyOfRange(bMsg, 0, 2);
        amount = Arrays.copyOfRange(bMsg, 2, 4);
        nonce = Arrays.copyOfRange(bMsg, 4, 8);
        command = Arrays.copyOfRange(bMsg, 8, 10);

    }

    private void parseData(byte[] bMsg) {
        data = Arrays.copyOfRange(bMsg, 10, MESSAGE_LENGTH);
    }

    private void checkLength(byte[] msg) throws Exception {
        if (msg.length > MESSAGE_LENGTH) {
            throw new Exception("Message is too big.");
        }
    }

    public String toString() {
        return byteArrToString(index) + " " + byteArrToString(amount) + " " + byteArrToString(nonce)
                + " " + byteArrToString(command) + " " + byteArrToString(data);
    }


    public static String byteArrToString(byte[] arr) {
        return new String(arr, 0, arr.length);
    }

    public byte[] getBytes() {
        byte[] indexAmount = ArrayUtils.addAll(index, amount);
        byte[] indexAmountNonce = ArrayUtils.addAll(indexAmount, nonce);
        byte[] indexAmountNonceCommand = ArrayUtils.addAll(indexAmountNonce, command);
        byte[] total = ArrayUtils.addAll(indexAmountNonceCommand, data);

//        String s = byteArrToString(index) + byteArrToString(amount) + byteArrToString(nonce) +
//                byteArrToString(command) + byteArrToString(data);


//        return ArrayUtils.addAll(
//                ArrayUtils.addAll(
//                        ArrayUtils.addAll(
//                                ArrayUtils.addAll(index, amount), nonce),
//                        command), data);
        return total;
//        return s.getBytes();
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


    public String getNonce() {
        return byteArrToString(trim(nonce));
    }

    public int getAmount() {
        return Integer.parseInt(byteArrToString(amount));
    }

    public int getIndex() {
        return Integer.parseInt(byteArrToString(index));
    }

    public String getData() {
        return byteArrToString(trim(data));
    }

    public static MessagePacket[] splitMessage(String msg, String nonce, String command) throws Exception {
        byte[] bMsg = msg.getBytes();
        int amountPackages = ((bMsg.length) / DATA_LENGTH) + 1;
        MessagePacket[] packets = new MessagePacket[amountPackages];

        for (int i = 0; i < amountPackages; i++) {
            int from = i * DATA_LENGTH;
            int to = i * DATA_LENGTH + DATA_LENGTH;
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


    public static void main(String[] args) throws Exception {

//        String msg = "Hello world!";
        String msg = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eget odio eu elit rhoncus consequat ut ut quam.";
        String nonce = "qt8l";

        String cmd = "sg";
        MessagePacket[] packets = splitMessage(msg, nonce, cmd);

        MessagePacket[] parsed = new MessagePacket[packets.length];

        int i = 0;
        for (MessagePacket mp : packets) {
            parsed[i] = new MessagePacket(mp.getBytes());
            i++;
        }

        String parsedMsg = assembleMessage(parsed);
        System.out.println(parsedMsg);

    }


    @Override
    public int compareTo(MessagePacket messagePacket) {
        //ascending order
        return this.getIndex() - messagePacket.getIndex();
    }
}
