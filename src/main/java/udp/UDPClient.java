package udp;

import java.io.IOException;
import java.net.*;

public class UDPClient {

    private DatagramSocket socket;

    public UDPClient(String addr, int port) throws UnknownHostException, SocketException {
        InetAddress address = InetAddress.getByName(addr);
        socket = new DatagramSocket(port, address);
    }

//    public void sendMessage(String msg, String addr, int port) throws IOException {
//        InetAddress address = InetAddress.getByName(addr);
//        byte[] msgBytes = msg.getBytes();
//
//        DatagramPacket packet = new DatagramPacket(msgBytes, msgBytes.length, address, port);
//        socket.send(packet);
//    }

    //TODO throws Exception -> change to customException
    public void sendMessage(String msg, String command, String addr, int port) throws Exception {
        String NONCE = RandomGenerator.generateString(MessagePacket.NONCE_LENGTH);
        System.out.println("NONCE: " + NONCE);

        MessagePacket[] mPackets = MessagePacket.splitMessage(msg, NONCE, command);
        for (MessagePacket mp : mPackets) {
            sendData(mp.getBytes(), addr, port);
        }
    }

    private void sendData(byte[] data, String addr, int port) throws IOException {
        InetAddress address = InetAddress.getByName(addr);
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }



//    public String receiveMessage() throws IOException {
//        byte[] buffer = new byte[MessagePacket.MESSAGE_LENGTH];
//        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
//        socket.receive(packet);
//
//        return parsePacket(packet);
//    }

    /**
     * blocks thread until message is received
     *
     * @return messagePacket
     */
    // TODO throws Exception -> change to customException
    public MessagePacket receiveMessage() throws Exception {
        byte[] buffer = new byte[MessagePacket.MESSAGE_LENGTH];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

//        System.out.println(parsePacket(packet));

        return new MessagePacket(packet.getData());
    }


    private String parsePacket(DatagramPacket p) {
        return new String(p.getData(), 0, p.getLength());
    }


}
