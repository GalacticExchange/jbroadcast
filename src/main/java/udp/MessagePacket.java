package udp;

import javax.swing.*;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

public class MessagePacket {


    //    public static final int MESSAGE_LENGTH = 1024;
    public static final int MESSAGE_LENGTH = 30;

    //    public static final int HEADER_LENGTH = 8;
    public static final int COMMAND_LENGTH = 2;

    private byte[] index;
    private byte[] amount;
    private byte[] nonce;
    private byte[] command;
    private byte[] data;


    /**
     * @param msg - [index,amount,nonce,command,data] byte message
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
    public MessagePacket(byte[] msg, int index, int totalAmount, String cmd) throws Exception {
        checkLength(msg);
        data = msg;
        createHeader(index, totalAmount, cmd);
    }

    private void createHeader(int i, int totalAmount, String cmd) {
        index = fillLack(String.valueOf(i), 2).getBytes();
        amount = fillLack(String.valueOf(totalAmount), 2).getBytes();

        nonce = String.valueOf("zcq4").getBytes(); //TODO pass/generate nonce?
        command = cmd.getBytes();
    }


    private void parseHeader(byte[] bMsg) {
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


//        return ArrayUtils.addAll(
//                ArrayUtils.addAll(
//                        ArrayUtils.addAll(
//                                ArrayUtils.addAll(index, amount), nonce),
//                        command), data);
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


    public int getAmount() {
        return Integer.parseInt(byteArrToString(amount));
    }

    public int getIndex() {
        return Integer.parseInt(byteArrToString(index));
    }

    public String getData() {
        return byteArrToString(data);
    }


    public static void main(String[] args) throws Exception {

        MessagePacket mp = new MessagePacket("Hello world!".getBytes(), 1, 1, "sg");
        System.out.println("Created Message Packet: {" + mp + "}");

        MessagePacket parsed = new MessagePacket(mp.getBytes());
        System.out.println("Parsed Message Packet: {" + parsed + "}");

        System.out.println("Amount: {" + parsed.getAmount() + "}");
        System.out.println("Index: {" + parsed.getIndex() + "}");
        System.out.println("Data: {" + parsed.getData() + "}");

    }


}
